package com.example.lukaskris.houseofdesign.Account;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.example.lukaskris.houseofdesign.R;

public class ContainerLoginRegisterActivity extends AppCompatActivity {

    private FrameLayout mFragmentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFragmentLayout = (FrameLayout)findViewById(R.id.login_fragment_layout);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.login_fragment_layout,new LoginFragment());
        ft.commit();
    }
}
