package com.penn.ppj.models;

import android.util.Log;

import com.penn.ppj.utils.PPData;
import com.penn.ppj.utils.PPNet;
import com.qiniu.android.common.Zone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by penn on 23/02/2017.
 */

public class MomentCreated {
    private static UploadManager uploadManager = new UploadManager(new Configuration.Builder()
            .chunkSize(256 * 1024)  //分片上传时，每片的大小。 默认256K
            .putThreshhold(512 * 1024)  // 启用分片上传阀值。默认512K
            .connectTimeout(10) // 链接超时。默认10秒
            .responseTimeout(60) // 服务器响应超时。默认60秒
            .zone(Zone.zone0) // 设置区域，指定不同区域的上传域名、备用域名、备用IP。
            .build());

    String authorUsername;
    String placeName;
    Loc loc;
    String content;
    String tag;
    long createdTime;
    ArrayList<PPImage> ppImages;
    String status; //local, uploading, net

    public MomentCreated(String authorUsername, String placeName, Loc loc, String content, String tag, ArrayList<String> imagePaths) {
        this.authorUsername = authorUsername;
        this.placeName = placeName;
        this.loc = loc;
        this.content = content;
        this.tag = tag;
        this.createdTime = System.currentTimeMillis();
        this.ppImages = getPPImage(imagePaths);
        this.status = "local";
    }

    int uploadedPPImageCount = 0;
    int uploadedPPImageSuccessCount = 0;
    int uploadedPPImageFailedCount = 0;

    public ArrayList<PPImage> getPPImage(ArrayList<String> imagePaths) {
        ArrayList<PPImage> result = new ArrayList<PPImage>();
        for (int i = 0; i < imagePaths.size(); i++) {
            result.add(new PPImage(imagePaths.get(i), i));
        }

        return result;
    }

    public ArrayList<String> getImagePathArrayList() {
        ArrayList<String> result = new ArrayList<String>();
        for (int i = 0; i < ppImages.size(); i++) {
            result.add(ppImages.get(i).localPath);
        }

        return result;
    }

    public String getStatus() {
        return status;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void upload() {
        status = "uploading";
        PPData.getInstance().setDefaultsLocalMoments();
        for (PPImage item: ppImages) {
            if (item.netName == null) {
                item.upload();
            }
        }
    }

    public void uploadedPPImage(boolean success) {
        //防止不同图片上传同时成功
        synchronized (this) {
            uploadedPPImageCount++;
            if (success) {
                uploadedPPImageSuccessCount++;
            } else {
                uploadedPPImageFailedCount++;
            }

            PPData.getInstance().setDefaultsLocalMoments();

            if (uploadedPPImageCount == ppImages.size()) {
                if (uploadedPPImageSuccessCount == ppImages.size()) {
                    //图片全部上传成功, 开始上传moment记录
                    uploadMomentRecord();
                } else {
                    //图片没有完全上传成功
                    uploadFailed();
                }
                PPData.getInstance().setDefaultsLocalMoments();
            }
        }
    }

    private String convertPPImages() {
        String result = "";
        for (PPImage item : ppImages) {
            result += "," + item.netName;
        }

        return result.substring(1);
    }

    private void uploadMomentRecord() {
        Call<MomentOverview> call = PPNet.getInstance().createMoment(convertPPImages(), placeName, content, loc.lon, loc.lat, tag, createdTime);

        call.enqueue(new Callback<MomentOverview>() {
            @Override
            public void onResponse(Call<MomentOverview> call, Response<MomentOverview> response) {
                if (response.errorBody() != null) {
                    String errStr;
                    try {
                        errStr = response.errorBody().string();
                        Log.e("P", "创建Moment失败1:" + errStr);
                        uploadFailed();

                    } catch (IOException e) {
                        Log.e("P", "创建Moment失败2:" + e.toString());
                        uploadFailed();
                    }
                } else {
                    //pptodo 典型案例tommy, izz如果这个时候用户强行退出程序的话, 会引起下次程序启动同一个moment重复上传, 如果服务器端对moment的createdTime做了唯一索引的话就会创建失败
                    status = "net";
                    MomentOverview mo = response.body();
                    //把对应的moment从localMoments移到netMoments, 然后刷新对应的moment记录在列表中的显示
                    PPData.getInstance().localMomentUploadedOK(mo);
                }
            }

            @Override
            public void onFailure(Call<MomentOverview> call, Throwable t) {
                Log.e("P", "创建Moment失败3:" + t.toString());
                uploadFailed();
            }
        });
    }

    private void uploadFailed() {
        status = "failed";
    }

    class PPImage {
        String localPath;
        String netName;
        int index;

        public PPImage(String localPath, int index) {
            this.localPath = localPath;
            this.index = index;
        }

        public void upload() {
            final String key = authorUsername + "_" + createdTime + "_" + index;
            Call<String> call = PPNet.getInstance().getQiniuToken(key);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.errorBody() != null) {
                        String errStr;
                        try {
                            errStr = response.errorBody().string();
                            Log.e("P", "获取qiniu token失败:" + errStr);
                            uploadedPPImage(false);
                        } catch (IOException e) {
                            Log.e("P", "获取qiniu token失败:" + e.toString());
                            uploadedPPImage(false);
                        }
                    } else {
                        String token = response.body();
                        File file = new File(localPath.substring(6));
                        uploadManager.put(file, key, token,
                                new UpCompletionHandler() {
                                    @Override
                                    public void complete(String key, ResponseInfo info, JSONObject res) {
                                        //res包含hash、key等信息，具体字段取决于上传策略的设置
                                        if (info.isOK()) {
                                            Log.d("qiniu", "Upload Success");
                                            netName = key;
                                            uploadedPPImage(true);
                                        } else {
                                            Log.d("qiniu", "Upload Fail");
                                            //如果失败，这里可以把info信息上报自己的服务器，便于后面分析上传错误原因
                                            uploadedPPImage(false);
                                        }
                                    }
                                }, null);

                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e("P", "获取qiniu token失败:" + t.toString());
                    uploadedPPImage(false);
                }
            });
}

//class PPImage {
//    private static UploadManager uploadManager = new UploadManager(new Configuration.Builder()
//            .chunkSize(256 * 1024)  //分片上传时，每片的大小。 默认256K
//            .putThreshhold(512 * 1024)  // 启用分片上传阀值。默认512K
//            .connectTimeout(10) // 链接超时。默认10秒
//            .responseTimeout(60) // 服务器响应超时。默认60秒
//            .zone(Zone.zone0) // 设置区域，指定不同区域的上传域名、备用域名、备用IP。
//            .build());
//
//    MomentCreated mc;
//
//    String localPath;
//    String netName;
//    int index;
//
//    public PPImage(MomentCreated mc, String localPath, int index) {
//        this.mc = mc;
//        this.localPath = localPath;
//        this.index = index;
//    }
//
//    public static ArrayList<PPImage> getPPImage(MomentCreated mc, ArrayList<String> imagePaths) {
//        ArrayList<PPImage> result = new ArrayList<PPImage>();
//        for (int i = 0; i < imagePaths.size(); i++) {
//            result.add(new PPImage(mc, imagePaths.get(i), i));
//        }
//
//        return result;
//    }
//
//    public void upload() {
//        final String key = mc.authorUsername + "_" + mc.createdTime + "_" + index;
//        Call<String> call = PPNet.getInstance().getQiniuToken(key);
//
//        call.enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(Call<String> call, Response<String> response) {
//                if (response.errorBody() != null) {
//                    String errStr;
//                    try {
//                        errStr = response.errorBody().string();
//                        Log.e("P", "获取qiniu token失败:" + errStr);
//                        mc.uploadedPPImage(false);
//                    } catch (IOException e) {
//                        Log.e("P", "获取qiniu token失败:" + e.toString());
//                        mc.uploadedPPImage(false);
//                    }
//                } else {
//                    String token = response.body();
//                    File file = new File(localPath.substring(6));
//                    uploadManager.put(file, key, token,
//                            new UpCompletionHandler() {
//                                @Override
//                                public void complete(String key, ResponseInfo info, JSONObject res) {
//                                    //res包含hash、key等信息，具体字段取决于上传策略的设置
//                                    if (info.isOK()) {
//                                        Log.d("qiniu", "Upload Success");
//                                        netName = key;
//                                        mc.uploadedPPImage(true);
//                                    } else {
//                                        Log.d("qiniu", "Upload Fail");
//                                        //如果失败，这里可以把info信息上报自己的服务器，便于后面分析上传错误原因
//                                        mc.uploadedPPImage(false);
//                                    }
//                                }
//                            }, null);
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<String> call, Throwable t) {
//                Log.e("P", "获取qiniu token失败:" + t.toString());
//                mc.uploadedPPImage(false);
//            }
//        });
    }
}