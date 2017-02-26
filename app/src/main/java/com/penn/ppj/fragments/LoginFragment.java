package com.penn.ppj.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.penn.ppj.R;
import com.penn.ppj.activities.MainActivity;
import com.penn.ppj.models.Loc;
import com.penn.ppj.models.LoginUser;
import com.penn.ppj.models.MomentOverview;
import com.penn.ppj.utils.PPData;
import com.penn.ppj.utils.PPNet;
import com.tapadoo.alerter.Alerter;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {
    private PPData ppData;

    private Button btLogin;
    private EditText etUsername;
    private EditText etPassword;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        btLogin = (Button) view.findViewById(R.id.bt_login);

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        etUsername = (EditText) view.findViewById(R.id.et_username);
        etPassword = (EditText) view.findViewById(R.id.et_password);

        return view;


    }

    private void login() {
        Call<LoginUser> call = PPNet.getInstance().login(etUsername.getText().toString(), etPassword.getText().toString());

        call.enqueue(new Callback<LoginUser>() {
            @Override
            public void onResponse(Call<LoginUser> call,
                                   Response<LoginUser> response) {
                if (response.errorBody() != null) {
                    String errStr;
                    try {
                        errStr = response.errorBody().string();
                        Log.e("P", "Login失败:" + errStr);
                    } catch (IOException e) {
                        errStr =  e.toString();
                        Log.e("P", "Login失败:" + e.toString());
                    }
                    Alerter.create(getActivity())
                            .setText(errStr)
                            .setBackgroundColor(R.color.colorAccent)
                            .show();
                } else {
                    Log.v("P", response.body() + "");
                    LoginUser result = response.body();

                    PPNet.setToken(result.getToken());

                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("TOKEN", result.getToken());

                    getMoments(result);
                }
            }

            @Override
            public void onFailure(Call<LoginUser> call, Throwable t) {
                Log.e("P", "Login失败:" + t.toString());
                Alerter.create(getActivity())
                        .setText(t.toString())
                        .setBackgroundColor(R.color.colorAccent)
                        .show();
            }
        });
    }

    private void getMoments(final LoginUser loginUser) {
        Call<ArrayList<MomentOverview>> call = PPNet.getInstance().getMoments();

        call.enqueue(new Callback<ArrayList<MomentOverview>>() {
            @Override
            public void onResponse(Call<ArrayList<MomentOverview>> call,
                                   Response<ArrayList<MomentOverview>> response) {
                if (response.errorBody() != null) {
                    String errStr;
                    try {
                        errStr = response.errorBody().string();
                        Log.e("P", "getMoments失败:" + errStr);  // 6
                    } catch (IOException e) {
                        Log.e("P", "getMoments失败:" + e.toString());
                    }
                } else {
                    Log.v("P", response.body() + "");
                    ArrayList<MomentOverview> netMoments = response.body();
                    loginOK(loginUser, netMoments);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<MomentOverview>> call, Throwable t) {
                Log.e("P", "Login失败:" + t.toString());
            }
        });
    }

    private void loginOK(LoginUser loginUser, ArrayList<MomentOverview> netMoments) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        Gson gson = new Gson();
        String json = prefs.getString("USER_LOCATION", "");
        Loc userLocation = gson.fromJson(json, Loc.class);
        if (userLocation == null) {
            userLocation = new Loc(31.0, 121.1);
            json = gson.toJson(userLocation);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("USER_LOCATION", json);
            editor.commit();

        }

        ppData = PPData.initInstance(getContext(), loginUser);

        ppData.setNetMoments(netMoments);

        Class destinationActivity = MainActivity.class;
        Intent it = new Intent(getContext(), destinationActivity);

        startActivity(it);
    }
}
