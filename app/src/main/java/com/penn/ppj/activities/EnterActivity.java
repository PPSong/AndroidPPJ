package com.penn.ppj.activities;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.penn.ppj.R;
import com.penn.ppj.fragments.LoginFragment;
import com.penn.ppj.fragments.SignUpFragment;

public class EnterActivity extends AppCompatActivity {

    private SectionsPagerAdapter spAdapter;

    private Button btnLogin;
    private Button btnSignUp;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);

        spAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.vp);
        mViewPager.setAdapter(spAdapter);


        btnLogin = (Button) findViewById(R.id.bt_login);
        btnSignUp = (Button) findViewById(R.id.bt_sign_up);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(0, true);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(1, true);
            }
        });
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return LoginFragment.newInstance();
                case 1:
                    return SignUpFragment.newInstance();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
