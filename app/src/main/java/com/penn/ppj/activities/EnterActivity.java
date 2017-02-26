package com.penn.ppj.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.penn.ppj.R;
import com.penn.ppj.fragments.LoginFragment;
import com.penn.ppj.fragments.SignUpFragment;
import com.penn.ppj.utils.CustomViewPager;

public class EnterActivity extends AppCompatActivity {

    private SectionsPagerAdapter spAdapter;
    private CustomViewPager cViewPager;

    private Button btnLogin;
    private Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);

        spAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        cViewPager = (CustomViewPager) findViewById(R.id.vp);
        cViewPager.setAdapter(spAdapter);
        cViewPager.setSwipeable(false);


        btnLogin = (Button) findViewById(R.id.bt_go_login);
        btnSignUp = (Button) findViewById(R.id.bt_go_sign_up);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cViewPager.setCurrentItem(0, true);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cViewPager.setCurrentItem(1, true);
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
