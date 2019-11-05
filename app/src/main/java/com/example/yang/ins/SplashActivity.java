package com.example.yang.ins;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.yang.ins.Utils.DateUtil;

import java.util.Map;

public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGHT = 2000; // 两秒后进入系统
    private TextView tv_start;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        int id = 0;
        String old_date = null;
        SharedPreferences mShared;
        final Intent mainIntent = new Intent();
        mShared = MainApplication.getContext().getSharedPreferences("share", MODE_PRIVATE);
        tv_start = (TextView) findViewById(R.id.tv_splash);
        Map<String, Object> mapParam = (Map<String, Object>) mShared.getAll();
        for (Map.Entry<String, Object> item_map : mapParam.entrySet()) {
            String key = item_map.getKey();
            Object value = item_map.getValue();
            if(key.equals("Date")) {
                old_date = value.toString();
            }
            else if(key.equals("id")) {
                id = Integer.parseInt(value.toString());
            }
        }
        if(old_date == null || old_date.length() <= 0) {
            mainIntent.setClass(SplashActivity.this, LoginActivity.class);
        }
        else if(DateUtil.getDeltaDate(old_date) <= 7) {
            MainApplication application = MainApplication.getInstance();
            application.mInfoMap.put("id", id);
            mainIntent.setClass(SplashActivity.this, MainActivity.class);
        }
        else if(DateUtil.getDeltaDate(old_date) > 7) {
            mainIntent.setClass(SplashActivity.this, LoginActivity.class);
        }
        new android.os.Handler().postDelayed(new Runnable() {
            public void run() {
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGHT);
    }
}
