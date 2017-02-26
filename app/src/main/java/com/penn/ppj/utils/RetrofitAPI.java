package com.penn.ppj.utils;

/**
 * Created by penn on 20/02/2017.
 */

import com.penn.ppj.models.LoginUser;
import com.penn.ppj.models.MomentDetails;
import com.penn.ppj.models.MomentOverview;
import com.penn.ppj.models.ReadMoment;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RetrofitAPI {
    @GET("/getQiniuToken/{key}")
    Call<String> getQiniuToken(@Path("key") String key);

    @GET("/readMoment/{momentId}")
    Call<ReadMoment> readMoment(@Path("momentId") String momentId);

    @FormUrlEncoded
    @POST("/users/login")
    Call<LoginUser> login(@Field("username") String username, @Field("password") String password);

    @FormUrlEncoded
    @POST("/createMoment")
    Call<MomentOverview> createMoment(@Field("images") String images,
                                      @Field("placeName") String placeName,
                                      @Field("content") String content,
                                      @Field("longitude") double longitude,
                                      @Field("latitude") double latitude,
                                      @Field("tag") String tag,
                                      @Field("createdTime") long createdTime);

    @GET("/getMoments")
    Call<ArrayList<MomentOverview>> getMoments();
}

