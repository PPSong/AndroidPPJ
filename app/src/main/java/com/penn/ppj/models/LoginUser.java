package com.penn.ppj.models;

/**
 * Created by penn on 24/02/2017.
 */

public class LoginUser {
    String _id;
    String username;
    String nickname;
    String token;
    String avatar;
    String sex;

    String error;

    public String getToken() {
        return token;
    }
}
