package com.penn.ppj.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.google.android.flexbox.FlexboxLayout;
import com.google.gson.Gson;
import com.penn.ppj.R;
import com.penn.ppj.models.Loc;
import com.penn.ppj.models.MomentCreated;
import com.penn.ppj.utils.PPData;
import com.squareup.picasso.Picasso;
import com.tapadoo.alerter.Alerter;

import java.util.ArrayList;

public class NewMomentActivity extends AppCompatActivity implements GeocodeSearch.OnGeocodeSearchListener {
    private MomentCreated mc;
    private ArrayList<String> picPaths;
    private EditText etMomentContent;
    private String strTag;
    private TextView tvMomentLocation;
    private Loc loc;
    private Button btMomentPublish;

    private Button btTag1;
    private Button btTag2;
    private Button btTag3;
    private Button btTag4;
    private Button btTag5;
    private Button btTag6;

    private GeocodeSearch geocoderSearch;
    private RegeocodeQuery query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_moment);

        etMomentContent = (EditText) findViewById(R.id.et_moment_content);
        etMomentContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                    return true;
                }
                return false;
            }
        });

        etMomentContent.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() != 0)
                    validatePublish();
            }
        });

        tvMomentLocation = (TextView) findViewById(R.id.tv_moment_location);

        btMomentPublish = (Button) findViewById(R.id.bt_moment_publish);
        btMomentPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishMoment();
            }
        });

        btTag1 = (Button) findViewById(R.id.bt_tag_1);
        btTag1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strTag = "BURGERS";
                validatePublish();
            }
        });

        btTag2 = (Button) findViewById(R.id.bt_tag_2);
        btTag2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strTag = "SOUP";
                validatePublish();
            }
        });

        btTag3 = (Button) findViewById(R.id.bt_tag_3);
        btTag3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strTag = "FASHION";
                validatePublish();
            }
        });

        btTag4 = (Button) findViewById(R.id.bt_tag_4);
        btTag4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strTag = "SPORT";
                validatePublish();
            }
        });

        btTag5 = (Button) findViewById(R.id.bt_tag_5);
        btTag5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strTag = "POLITICS";
                validatePublish();
            }
        });

        btTag6 = (Button) findViewById(R.id.bt_tag_6);
        btTag6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strTag = "ART";
                validatePublish();
            }
        });

        FlexboxLayout container = (FlexboxLayout) findViewById(R.id.lo_preview_container);

        Intent it = getIntent();
        picPaths = it.getStringArrayListExtra("picPaths");

        for (String item : picPaths) {
            ImageView iv = new ImageView(getApplicationContext());
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(90, 160);

            container.addView(iv, params);

            Picasso.with(getApplicationContext())
                    .load(item)
                    .resize(90, 160)
                    .placeholder(R.drawable.logo)
                    .error(R.drawable.img_error)
                    .into(iv);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Gson gson = new Gson();
        String json = prefs.getString("USER_LOCATION", "");
        Loc userLocation = gson.fromJson(json, Loc.class);

        loc = userLocation;

        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);

        query = new RegeocodeQuery(new LatLonPoint(userLocation.getLat(), userLocation.getLon()), 200, GeocodeSearch.AMAP);

        geocoderSearch.getFromLocationAsyn(query);

        validatePublish();
    }

    private void validatePublish() {
        boolean validate = false;
        if (!etMomentContent.getText().toString().isEmpty()
                && !tvMomentLocation.getText().toString().isEmpty()
                && strTag != null
                && !strTag.isEmpty()) {
            validate = true;
        }

        btMomentPublish.setEnabled(validate);
    }

    private void publishMoment() {
        mc = new MomentCreated(PPData.getInstance().user.getUsername(), tvMomentLocation.getText().toString(), loc, etMomentContent.getText().toString(), strTag, picPaths);

        PPData.getInstance().addLocalMoment(mc);

        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        if (regeocodeResult == null) {
            Alerter.create(NewMomentActivity.this)
                    .setText("获取地理位置名称失败!")
                    .setBackgroundColor(R.color.colorAccent)
                    .show();
        } else {
            tvMomentLocation.setText(regeocodeResult.getRegeocodeAddress().getFormatAddress() + "," + loc.getLat() + "," + loc.getLon());
            validatePublish();
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }
}
