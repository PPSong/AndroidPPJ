package com.penn.ppj.utils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by penn on 23/02/2017.
 */

public class PPNet {
    private static final String BASE_URL = "http://192.168.1.2:3000";
    private static String token;
    private static RetrofitAPI retrofitAPI;

    class HeaderInterceptor
            implements Interceptor {
        @Override
        public Response intercept(Chain chain)
                throws IOException {
            Request request = chain.request();
            request = request.newBuilder()
                    .addHeader("Authorization", "JWT " + token)
                    .build();
            Response response = chain.proceed(request);
            return response;
        }
    }

    private PPNet()
    {
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .writeTimeout(3, TimeUnit.SECONDS)
                .addInterceptor(new HeaderInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL) // 1
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create()) // 2
                .build();

        retrofitAPI = retrofit.create(RetrofitAPI.class); // 3
    }

    public static RetrofitAPI getInstance()
    {
        if (retrofitAPI == null) {
            new PPNet();
        }
        return retrofitAPI;
    }

    public static void setToken(String tokenString) {
        token = tokenString;
    }
}
