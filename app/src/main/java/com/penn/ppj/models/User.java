package com.penn.ppj.models;

/**
 * Created by penn on 20/02/2017.
 */

public class User {
    String _id;
    String username;
    String nickname;
    String avatar;
    String sex;

    public User(LoginUser loginUser) {
        _id = loginUser._id;
        username = loginUser.username;
        nickname = loginUser.nickname;
        avatar = loginUser.avatar;
        sex = loginUser.sex;
    }

    public String getUsername() {
        return username;
    }
}