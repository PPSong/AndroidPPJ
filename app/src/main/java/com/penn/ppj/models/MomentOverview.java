package com.penn.ppj.models;

import android.util.Log;

import com.penn.ppj.utils.PPData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by penn on 20/02/2017.
 */

public class MomentOverview {
    String _id;
    User author;
    ArrayList<String> images;
    String placeName;
    Loc loc;
    String content;
    String tag;
    int read;
    ArrayList<String> comments;
    long createdTime;
    String status;

    public MomentOverview(MomentCreated mc, User user) {
        author = user;
        images = mc.getImagePathArrayList();

        placeName = mc.placeName;
        loc = mc.loc;
        content = mc.content;
        tag = mc.tag;
        read = 0;
        comments = new ArrayList<String>();
        createdTime = mc.createdTime;
        status = "local";
    }

    public String getMomentId() {
        return _id;
    }

    public String getAuthorNickname() {
        return author.nickname;
    }

    public String getAuthorUsername() {
        return author.username;
    }

    public String getOverviewImage() {
        String result;
        if (status == "net") {
            result = PPData.imageBaseURL + images.get(0) + "-normal";
        } else {
            result = images.get(0);
        }

        Log.d("P", "result:" + result);

        return result;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getContent() {
        return content;
    }

    public String getTag() {
        return tag;
    }

    public String getReadCountStr() {
        return String.valueOf(read);
    }

    public String getCommentsCountStr() {
        return String.valueOf(comments.size());
    }

    public String getCreatedTimeDes() {
        SimpleDateFormat sf = new SimpleDateFormat("EEE, MMM d, ''yy h:mm a");
        return sf.format(new Date(createdTime));
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setStatusNet() {
        status = "net";
    }
}
