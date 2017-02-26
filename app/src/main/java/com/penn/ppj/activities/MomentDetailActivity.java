package com.penn.ppj.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.penn.ppj.R;
import com.penn.ppj.models.LoginUser;
import com.penn.ppj.models.MomentDetails;
import com.penn.ppj.models.MomentOverview;
import com.penn.ppj.models.ReadMoment;
import com.penn.ppj.utils.CommentsListAdapter;
import com.penn.ppj.utils.MomentsListAdapter;
import com.penn.ppj.utils.PPNet;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MomentDetailActivity extends AppCompatActivity {
    private String momentId;
    private MomentDetails md;

    private CommentsListAdapter cAdapter;
    private RecyclerView rvCommentsList;

    private ImageView ivImage;
    private TextView tvMomentContent;
    private TextView tvCommentsCount;
    private TextView tvReadCount;
    private TextView tvPlaceName;
    private TextView tvMomentCreatedTime;
    private TextView tvTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moment_detail);

        ivImage = (ImageView) findViewById(R.id.iv_image);

        tvMomentContent = (TextView) findViewById(R.id.tv_moment_content);

        tvCommentsCount = (TextView) findViewById(R.id.tv_comments_count);

        tvReadCount = (TextView) findViewById(R.id.tv_read_count);

        tvPlaceName = (TextView) findViewById(R.id.tv_place_name);

        tvMomentCreatedTime = (TextView) findViewById(R.id.tv_moment_created_time);

        tvTag = (TextView) findViewById(R.id.tv_tag);

        rvCommentsList = (RecyclerView) findViewById(R.id.rv_comments);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rvCommentsList.setLayoutManager(layoutManager);

        Intent it = getIntent();
        momentId = it.getStringExtra("momentId");
        readMoment();
    }

    private void readMoment() {
        Call<ReadMoment> call = PPNet.getInstance().readMoment(momentId);

        call.enqueue(new Callback<ReadMoment>() {
            @Override
            public void onResponse(Call<ReadMoment> call,
                                   Response<ReadMoment> response) {
                if (response.errorBody() != null) {
                    String errStr;
                    try {
                        errStr = response.errorBody().string();
                        Log.e("P", "readMoment失败1:" + errStr);
                    } catch (IOException e) {
                        Log.e("P", "readMoment失败2:" + e.toString());
                    }
                } else {
                    Log.v("P", response.body() + "");
                    md = response.body().getMomentDetails();
                    bind();
                }
            }

            @Override
            public void onFailure(Call<ReadMoment> call, Throwable t) {
                Log.e("P", "readMoment失败3:" + t.toString());
            }
        });
    }

    private void bind() {
        ivImage.setImageResource(R.drawable.signupimage);
        Picasso.with(getApplicationContext())
                .load(md.getOverviewImage())
                .placeholder(R.drawable.logo)
                .error(R.drawable.error)
                .into(ivImage);
        tvMomentContent.setText(md.getContent());
        tvCommentsCount.setText(md.getCommentsCountStr());
        tvReadCount.setText(md.getReadCountStr());
        tvPlaceName.setText(md.getPlaceName());
        tvMomentCreatedTime.setText(md.getCreatedTimeDes());
        tvTag.setText(md.getTag());

        cAdapter = new CommentsListAdapter(md.getComments());
        rvCommentsList.setAdapter(cAdapter);
    }
}
