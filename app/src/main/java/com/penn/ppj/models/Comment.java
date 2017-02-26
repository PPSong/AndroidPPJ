package com.penn.ppj.models;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by penn on 20/02/2017.
 */

public class Comment {
    User author;
    long createdTime;
    String content;

    public String getAuthorNickname() {
        return author.nickname;
    }

    public String getCreatedTimeDes() {
        SimpleDateFormat sf = new SimpleDateFormat("EEE, MMM d, ''yy h:mm a");
        return sf.format(new Date(createdTime));
    }

    public String getContent() {
        return content;
    }

}
