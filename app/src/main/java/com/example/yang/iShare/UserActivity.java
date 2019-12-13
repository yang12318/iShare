package com.example.yang.iShare;

import android.annotation.SuppressLint;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.yang.iShare.Utils.HelloHttp;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Response;

public class UserActivity extends AppCompatActivity {
    private int userId;
    private TabLayout tabLayout;
    private TextView tv_dynamic, tv_concern, tv_follow, tv_user, tv_nickname, tv_bio;
    private Button btn_follow;
    private ImageButton ib_back;
    private LinearLayout ll_follow,ll_concern;
    private CircleImageView civ;
    private String username, nickname, src, birthday, address, introduction = null;
    private int gender = 3, follow_num = 0, concern_num = 0, posts = 0;
    private boolean flag = false;
    private AlbumFragment mAlbumFragment;
    private DynamicFragment mDynamicFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        tv_dynamic = (TextView) findViewById(R.id.user_dynamic);
        tv_concern = (TextView) findViewById(R.id.user_concern);
        tv_follow = (TextView) findViewById(R.id.user_follow);
        tv_nickname = (TextView) findViewById(R.id.user_nickname);
        tv_user = (TextView) findViewById(R.id.tv_user);
        tv_bio = (TextView) findViewById(R.id.user_bio);
        btn_follow = (Button) findViewById(R.id.me_revise);
        civ = (CircleImageView) findViewById(R.id.user_image);
        ib_back = (ImageButton)  findViewById(R.id.ib_user_back);
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ll_concern = (LinearLayout)  findViewById(R.id.ll_user_concern);
        ll_follow = (LinearLayout) findViewById(R.id.ll_user_follow);
        tabLayout= (TabLayout)findViewById(R.id.tab_user);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.selector_album));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.selector_dynamic));
        tabLayout.setSelectedTabIndicatorColor(Color.BLACK);
        tabLayout.setSelectedTabIndicatorHeight(0);
        Intent intent = getIntent();
        userId = intent.getIntExtra("userId", 0);
        Log.d("UserActivity", Integer.toString(userId));
        ll_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserActivity.this, FollowActivity.class);
                intent.putExtra("user_id", userId);
                startActivity(intent);
            }
        });
        ll_concern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserActivity.this, ConcernActivity.class);
                intent.putExtra("user_id", userId);
                startActivity(intent);
            }
        });
        tabLayout.setTabMode(TabLayout.MODE_FIXED);//设置tab模式，当前为系统默认模式
        setDefaultFragment();//设置默认加载项
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.e("TAG","tab position:"+tab.getPosition());
                FragmentManager fm = UserActivity.this.getSupportFragmentManager();
                //开启事务
                FragmentTransaction transaction = fm.beginTransaction();
                switch (tab.getPosition()){
                    case 0:{
                        if (mAlbumFragment == null) {
                            mAlbumFragment = new AlbumFragment();
                            Bundle bundle = new Bundle();
                            bundle.putInt("id", userId);
                            mAlbumFragment.setArguments(bundle);
                        }
                        transaction = fm.beginTransaction();
                        transaction.replace(R.id.ll_tbuser, mAlbumFragment);
                        transaction.commit();
                        break;
                    }
                    case 1:{
                        if (mDynamicFragment == null) {
                            mDynamicFragment = new DynamicFragment();
                            Bundle bundle = new Bundle();
                            bundle.putInt("id", userId);
                            mDynamicFragment.setArguments(bundle);
                        }
                        transaction = fm.beginTransaction();
                        transaction.replace(R.id.ll_tbuser, mDynamicFragment);
                        transaction.commit();
                        break;
                    }
                    default:
                        break;
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        Map<String, Object> map = new HashMap<>();
        HelloHttp.sendGetRequest("api/user/detail/"+Integer.toString(userId), map, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("UserActivity", "FAILURE");
                Looper.prepare();
                Toast.makeText(UserActivity.this, "服务器未响应", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                Log.d("UserActivity", responseData);
                try {
                    JSONObject jsonObject1 = new JSONObject(responseData);
                    JSONObject jsonObject = jsonObject1.getJSONObject("result");
                    posts = jsonObject1.getInt("post_num");
                    username = jsonObject.getString("username");
                    nickname = jsonObject.getString("nickname");
                    gender = jsonObject.getInt("gender");
                    birthday = jsonObject.getString("birthday");
                    follow_num = jsonObject.getInt("following_num");
                    concern_num = jsonObject.getInt("followed_num");
                    src = jsonObject.getString("profile_picture");
                    src = "http://ins.itstudio.club" + src;
                    address = jsonObject.getString("address");
                    introduction = jsonObject.getString("introduction");
                    mHandler.sendEmptyMessageDelayed(1, 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                    String result = null;
                    try {
                        result = new JSONObject(responseData).getString("status");
                        Looper.prepare();
                        Toast.makeText(UserActivity.this,result, Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        Map<String, Object> map2 = new HashMap<>();
        HelloHttp.sendGetRequest("api/user/checkfollow/"+Integer.toString(userId), map2, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("UserActivity", "FAILURE");
                Looper.prepare();
                Toast.makeText(UserActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                Log.d("UserActivity", responseData);
                String result = null;
                try {
                    result = new JSONObject(responseData).getString("status");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(result != null && result.equals("Yes")) {
                    //这个人你关注了
                    flag = true;
                    setButtonStyle(true);
                }
                else if(result != null && result.equals("No")) {
                    //这个人你没关注
                    flag = false;
                    setButtonStyle(false);
                }
                else if(result != null && result.equals("UnknownError")) {
                    Looper.prepare();
                    Toast.makeText(UserActivity.this, result, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                else {
                    Looper.prepare();
                    Toast.makeText(UserActivity.this, result, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        });
        btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
            if(!flag) {
                //现在是没关注状态
                setButtonStyle(true);
                Map<String, Object> map = new HashMap<>();
                map.put("pk", userId);
                HelloHttp.sendPostRequest("api/user/followyou", map, new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("UserActivity", "FAILURE");
                        setButtonStyle(false);
                        Looper.prepare();
                        Toast.makeText(UserActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }

                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseData = response.body().string();
                        Log.d("UserActivity", responseData);
                        String result = null;
                        try {
                            result = new JSONObject(responseData).getString("status");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            setButtonStyle(false);
                        }
                        if(result != null && result.equals("Success")) {
                            Looper.prepare();
                            Snackbar.make(v,"关注成功",Snackbar.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                        else {
                            setButtonStyle(false);
                            if(result != null && result.equals("UnknownError")) {
                                Looper.prepare();
                                Snackbar.make(v,"未知错误",Snackbar.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                            else if(result != null && result.equals("Failure")) {
                                Looper.prepare();
                                Snackbar.make(v,"错误：重复的关注请求，已取消关注",Snackbar.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                            else {
                                Looper.prepare();
                                Toast.makeText(UserActivity.this, result, Toast.LENGTH_SHORT ).show();
                                Looper.loop();
                            }
                        }
                    }
                });
            }
            else {
                //现在是关注状态
                Map<String, Object> map = new HashMap<>();
                map.put("pk", userId);
                setButtonStyle(false);
                HelloHttp.sendDeleteRequest("api/user/followyou", map, new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("UserActivity", "FAILURE");
                        setButtonStyle(true);
                        Looper.prepare();
                        Snackbar.make(v,"服务器错误",Snackbar.LENGTH_SHORT).show();
                        Looper.loop();
                    }

                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseData = response.body().string();
                        Log.d("UserActivity", responseData);
                        String result = null;
                        try {
                            result = new JSONObject(responseData).getString("status");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            setButtonStyle(true);
                        }
                        if(result != null && result.equals("Success")) {
                            Looper.prepare();
                            Snackbar.make(v,"已取消关注",Snackbar.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                        else {
                            setButtonStyle(true);
                            if(result != null && result.equals("UnknownError")) {
                                Looper.prepare();
                                Snackbar.make(v,"未知错误",Snackbar.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                            else {
                                Looper.prepare();
                                Toast.makeText(UserActivity.this, result, Toast.LENGTH_SHORT ).show();
                                Looper.loop();
                            }
                        }
                    }
                });
            }
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        tabLayout.getTabAt(0).select();
    }
    private void setDefaultFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        mAlbumFragment = new AlbumFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("id",userId);
        mAlbumFragment.setArguments(bundle);
        transaction.replace(R.id.ll_tbuser, mAlbumFragment);
        transaction.commit();
    }
        @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg)
        {
            if(msg.what == 1)
            {
                tv_concern.setText(Integer.toString(concern_num));
                tv_follow.setText(Integer.toString(follow_num));
                tv_nickname.setText(nickname);
                tv_user.setText(username);
                if(introduction == null || introduction.equals("-")) {
                    introduction = "这个人很懒，还没有填写个人简介";
                }
                tv_bio.setText(introduction);
                tv_dynamic.setText(Integer.toString(posts));
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.empty_like);
                requestOptions.error(R.drawable.empty_like);
                if (UserActivity.this == null) {
                    return;
                }
                else {
                    Glide.with(UserActivity.this)
                            .setDefaultRequestOptions(requestOptions)
                            .load(src).into(civ);
                }
            }
        }
    };

    private void setButtonStyle(final boolean flag2) {
        runOnUiThread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void run() {
                if(flag2) {
                    //这个人你关注了
                    flag = true;
                    btn_follow.setText("关注中");
                    btn_follow.setTextColor(Color.BLACK);
                }
                else {
                    //这个人你没关注
                    flag = false;
                    btn_follow.setText("关注");
                    btn_follow.setTextColor(Color.BLACK);
                }
            }
        });
    }
}
