package com.penn.ppj.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.penn.ppj.models.Loc;
import com.penn.ppj.models.LoginUser;
import com.penn.ppj.models.MomentCreated;
import com.penn.ppj.models.MomentOverview;
import com.penn.ppj.models.NotifyObject;
import com.penn.ppj.models.User;

import junit.framework.Test;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Observable;

import static com.amap.api.col.x.i;

/**
 * Created by penn on 24/02/2017.
 */
public class PPData extends Observable {
    public static String imageBaseURL = "http://oemogmm69.bkt.clouddn.com/";

    private static PPData ourInstance;

    private Context context;

    public User user;

    private ArrayList<MomentOverview> netMoments;
    private ArrayList<MomentCreated> localMoments;
    private ArrayList<MomentOverview> totalMoments;

    public static PPData initInstance(Context context,
                                      LoginUser loginUser) {

        ourInstance = new PPData(context,
                loginUser);

        return ourInstance;
    }

    public static PPData getInstance() {
        return ourInstance;
    }

    public void resumeLocalMomentsUpload() {
        for (MomentCreated item: localMoments) {
            if (item.getStatus() != "failed") {
                //包含local, uploading
                item.upload();
            }
        }
    }

    public int getTotalMomentsCount() {
        return totalMoments.size();
    }

    public MomentOverview getTotalMoments(int index) {
        return totalMoments.get(index);
    }

    public int getMomentOverviewPosition(MomentOverview mo) {
        for (int i = 0; i < totalMoments.size(); i++) {
            MomentOverview item = totalMoments.get(i);
            if (item.getCreatedTime() == mo.getCreatedTime() && item.getAuthorUsername() == mo.getAuthorUsername()) {
                return i;
            }
        }

        //pptodo 应该抛异常
        return -1;
    }

    public void addLocalMoment(MomentCreated mc) {
        localMoments.add(mc);
        setDefaultsLocalMoments();

        setTotalMoments();

        NotifyObject no = new NotifyObject("momentCreated", mc);
        setChanged();
        notifyObservers(no);
    }

    public void setNetMoments(ArrayList<MomentOverview> netMoments) {
        this.netMoments = netMoments;
        setTotalMoments();

        setChanged();
        NotifyObject no = new NotifyObject("momentsRefresh", null);
        notifyObservers(no);
    }

    //把对应的moment从localMoments移到netMoments, 然后刷新对应的moment记录在列表中的显示
    public void localMomentUploadedOK(MomentOverview mo) {
        MomentCreated mc = null;
        for (MomentCreated item : localMoments) {
            if (item.getCreatedTime() == mo.getCreatedTime()) {
                mc = item;
                break;
            }
        }

        localMoments.remove(mc);

        netMoments.add(mo);

        setTotalMoments();

        setChanged();
        NotifyObject no = new NotifyObject("momentUpdated", mo);
        notifyObservers(no);
    }

    private PPData(Context context,
                   LoginUser loginUser) {
        this.context = context;

        this.user = new User(loginUser);
        //加载localMoments
        this.localMoments = getDefaultsLocalMoments();
    }

    private ArrayList<MomentCreated> getDefaultsLocalMoments() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = prefs.getString(user.getUsername() + "_LOCAL_MOMENTS", "");
        Type listType = new TypeToken<ArrayList<MomentCreated>>(){}.getType();
        ArrayList<MomentCreated> localMoments = gson.fromJson(json, listType);
        if (localMoments == null) {
            localMoments = new ArrayList<MomentCreated>();
        }
        return localMoments;
    }

    public void setDefaultsLocalMoments() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<MomentCreated>>(){}.getType();
        String json = gson.toJson(localMoments, listType);
        Log.d("P", json);
        editor.putString(user.getUsername() + "_LOCAL_MOMENTS", json);
        editor.commit();
    }

    private void setTotalMoments() {
        synchronized (this) {
            ArrayList<MomentOverview> localMomentsOverview = new ArrayList<MomentOverview>();
            if (localMoments != null) {
                for (MomentCreated item : localMoments) {
                    localMomentsOverview.add(new MomentOverview(item, user));
                }
            }
            ArrayList<MomentOverview> tmpTotalMoments = new ArrayList<MomentOverview>();
            tmpTotalMoments.addAll(localMomentsOverview);
            for (MomentOverview mo : netMoments) {
                mo.setStatusNet();
                tmpTotalMoments.add(mo);
            }
            //sort
            Collections.sort(tmpTotalMoments, new MomentComparator());
            totalMoments = tmpTotalMoments;
        }
    }
}

class MomentComparator implements Comparator<MomentOverview> {
    public int compare(MomentOverview left, MomentOverview right) {
        return left.getCreatedTime() > right.getCreatedTime() ? -1 : 1;
    }
}