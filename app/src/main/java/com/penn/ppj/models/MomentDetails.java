package com.penn.ppj.models;

import com.penn.ppj.utils.PPData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.penn.ppj.utils.PPData.imageBaseURL;

/**
 * Created by penn on 20/02/2017.
 */

public class MomentDetails {
    String _id;
    User author;
    ArrayList<String> images;
    String placeName;
    Loc loc;
    String content;
    String tag;
    int read;
    ArrayList<Comment> comments;
    long createdTime;
    String status;

//    public MomentDetails(MomentCreated mc, User user) {
//        author = user;
//        ArrayList<String> images = new ArrayList<String>();
//        for (PPImage item : mc.ppImages) {
//            images.add(item.localPath);
//        }
//        placeName = mc.placeName;
//        loc = mc.loc;
//        content = mc.content;
//        tag = mc.tag;
//        read = 0;
//        comments = new ArrayList<Comment>();
//        createdTime = mc.createdTime;
//        status = mc.status;
//    }

    public String getAuthorNickname() {
        return author.nickname;
    }

    public String getOverviewImage() {
        return PPData.imageBaseURL + images.get(0) + "-normal";
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
        if (comments == null) {
            return "0";
        }

        return String.valueOf(comments.size());
    }

    public String getCreatedTimeDes() {
        SimpleDateFormat sf = new SimpleDateFormat("EEE, MMM d, ''yy h:mm a");
        return sf.format(new Date(createdTime));
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public long getCreatedTime() {
        return createdTime;
    }
}
