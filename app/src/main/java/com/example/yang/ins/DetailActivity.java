package com.example.yang.ins;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.yang.ins.Utils.HelloHttp;
import com.example.yang.ins.bean.Dynamic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.photopicker.activity.BGAPhotoPreviewActivity;
import cn.bingoogolapple.photopicker.widget.BGANinePhotoLayout;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Response;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class DetailActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, BGANinePhotoLayout.Delegate{
    private static final int PRC_PHOTO_PREVIEW = 1;
    private int postId = 0, userId = -10;
    private ImageButton ib_back, ib_menu, ib_like, ib_collect, ib_comment;
    private CircleImageView ci_head;
    private TextView tv_username, tv_introduction, tv_review, tv_time;
    private TextSwitcher tv_like;
    private FunGameRefreshView refreshView;
    private int myId = -9;
    final Dynamic dynamic = new Dynamic();
    private boolean flag = false;
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);
    BGANinePhotoLayout ninePhotoLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        postId = intent.getIntExtra("id", 0);
        userId = intent.getIntExtra("user_id", -10);
        if(userId == -10) {
            Toast.makeText(DetailActivity.this, "Intent错误", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("DetailActivity", "postid="+Integer.toString(postId));
        Log.d("DetailActivity", "userid="+Integer.toString(userId));
        refreshView = (FunGameRefreshView) findViewById(R.id.refreshview);
        ci_head = (CircleImageView) findViewById(R.id.ci_head);
        ib_back = (ImageButton) findViewById(R.id.ib_detail_back);
        ib_menu = (ImageButton) findViewById(R.id.ib_menu);
        ib_like = (ImageButton) findViewById(R.id.ib_like);
        ib_comment = (ImageButton) findViewById(R.id.ib_comment);
        ib_collect = (ImageButton) findViewById(R.id.ib_collect);
        tv_username = (TextView) findViewById(R.id.tv_username);
        tv_introduction = (TextView) findViewById(R.id.tv_detail) ;
        tv_review = (TextView) findViewById(R.id.tv_comment);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_like = (TextSwitcher) findViewById(R.id.tv_like);
        ninePhotoLayout = (BGANinePhotoLayout) findViewById(R.id.npl_item_moment_photos);
        SharedPreferences mShared;
        mShared = MainApplication.getContext().getSharedPreferences("share", MODE_PRIVATE);
        Map<String, Object> mapParam = (Map<String, Object>) mShared.getAll();
        for (Map.Entry<String, Object> item_map : mapParam.entrySet()) {
            String key = item_map.getKey();
            Object value = item_map.getValue();
            if(key.equals("id")) {
                myId = Integer.valueOf(value.toString());
            }
        }
        if(myId == -9) {
            Toast.makeText(DetailActivity.this, "全局内存中变量为空", Toast.LENGTH_SHORT).show();
            return;
        }
        refreshView.setOnRefreshListener(new FunGameRefreshView.FunGameRefreshListener() {
            @Override
            public void onPullRefreshing() {
                initData();
            }

            @Override
            public void onRefreshComplete() {

            }
        });
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ib_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this); //定义一个AlertDialog
                String[] strarr = {"删除动态","取消"};
                builder.setItems(strarr, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface arg0, int arg1)
                    {
                        if (arg1 == 0) {
                            Map<String, Object> map = new HashMap<>();
                            Map<String, Object> urlmap = new HashMap<>();
                            urlmap.put("pk", postId);
                            HelloHttp.sendSpecialDeleteRequest("api/dynamic", urlmap, map, new okhttp3.Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Log.e("DetailActivity", "FAILURE");
                                    Looper.prepare();
                                    Toast.makeText(DetailActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    String responseData = response.body().string();
                                    Log.d("DetailActivity", responseData);
                                    try{
                                        JSONObject jsonObject = new JSONObject(responseData);
                                        String result = jsonObject.getString("status");
                                        if(result.equals("Success")) {
                                            Looper.prepare();
                                            Snackbar.make(view,"删除成功",Snackbar.LENGTH_SHORT).show();
                                            finish();
                                            Looper.loop();
                                        }
                                        else if(result.equals("Failure")) {
                                            Looper.prepare();
                                            Snackbar.make(view,"删除失败",Snackbar.LENGTH_SHORT).show();
                                            Looper.loop();
                                        }
                                        else {
                                            Looper.prepare();
                                            Toast.makeText(DetailActivity.this, result, Toast.LENGTH_SHORT).show();
                                            Looper.loop();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }else if(arg1 == 1){
                            return;
                        }
                    }
                });
                builder.show();
            }
        });
        initData();             //向服务器请求数据
        ninePhotoLayout.setDelegate(DetailActivity.this);
        ib_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this, CommentActivity.class);
                intent.putExtra("post_id", postId);
                startActivity(intent);
            }
        });
        tv_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this, CommentActivity.class);
                intent.putExtra("post_id", postId);
                startActivity(intent);
            }
        });
        ci_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userId == -10) {
                    Toast.makeText(DetailActivity.this, "Intent错误", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(myId == userId) {
                    //这个人是我自己
                    Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                    intent.putExtra("me_id",userId );
                    startActivity(intent);
                }
                else {
                    //这个人不是我
                    Intent intent = new Intent(DetailActivity.this, UserActivity.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                }
            }
        });
        tv_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(myId == userId) {
                //这个人是我自己
                Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                intent.putExtra("me_id",userId );
                startActivity(intent);
            }
            else {
                //这个人不是我
                Intent intent = new Intent(DetailActivity.this, UserActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
            }
        });
        ib_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                int pk = dynamic.getId();
                final boolean flag = dynamic.isIs_like();
                Map<String, Object> map = new HashMap<>();
                map.put("pk", pk);
                if(!flag){
                    //未点赞
                    setLikeStyle(true);
                    HelloHttp.sendPostRequest("api/post/dianzan", map, new okhttp3.Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e("Detail", "FAILURE");
                            Looper.prepare();
                            setLikeStyle(false);
                            Toast.makeText(DetailActivity.this, "服务器未响应", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String responseData = response.body().string();
                            Log.d("Detail", responseData);
                            String result = null;
                            try {
                                result = new JSONObject(responseData).getString("status");
                            } catch (JSONException e) {
                                setLikeStyle(false);
                                e.printStackTrace();
                            }
                            if(result.equals("Success")) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Snackbar.make(view,"点赞成功",Snackbar.LENGTH_SHORT).show();
                                        TextSwitcher ts = (TextSwitcher) findViewById(R.id.tv_like);
                                        dynamic.setLikes_num(dynamic.getLikes_num() + 1);
                                        ts.setText(Integer.toString(dynamic.getLikes_num())+"次赞");
                                    }
                                });
                            }
                            else if(result.equals("Failure")) {
                                Looper.prepare();
                                setLikeStyle(false);
                                Toast.makeText(DetailActivity.this,"记录已存在", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                            else if(result.equals("UnknownError")){
                                Looper.prepare();
                                setLikeStyle(false);
                                Toast.makeText(DetailActivity.this,"未知错误", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                            else {
                                Looper.prepare();
                                setLikeStyle(false);
                                Toast.makeText(DetailActivity.this, result, Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                        }
                    });
                }
                else {
                    //已点赞
                    setLikeStyle(false);
                    HelloHttp.sendDeleteRequest("api/post/dianzan", map, new okhttp3.Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e("Detail", "FAILURE");
                            Looper.prepare();
                            setLikeStyle(true);
                            Toast.makeText(DetailActivity.this, "服务器未响应", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String responseData = response.body().string();
                            Log.d("Detail", responseData);
                            String result = null;
                            try {
                                result = new JSONObject(responseData).getString("status");
                            } catch (JSONException e) {
                                setLikeStyle(true);
                                e.printStackTrace();
                            }
                            if(result.equals("Success")) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Snackbar.make(view,"取消点赞成功",Snackbar.LENGTH_SHORT).show();
                                        TextSwitcher ts = (TextSwitcher) findViewById(R.id.tv_like);
                                        dynamic.setLikes_num(dynamic.getLikes_num() - 1);
                                        ts.setText(Integer.toString(dynamic.getLikes_num())+"次赞");
                                    }
                                });
                            }
                            else if(result.equals("Failure")) {
                                Looper.prepare();
                                setLikeStyle(true);
                                Toast.makeText(DetailActivity.this,"记录不存在", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                            else if(result.equals("UnknownError")){
                                Looper.prepare();
                                setLikeStyle(true);
                                Toast.makeText(DetailActivity.this,"未知错误", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                            else {
                                Looper.prepare();
                                setLikeStyle(true);
                                Toast.makeText(DetailActivity.this, result, Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                        }
                    });
                }
            }
        });
        ib_collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                boolean flag = dynamic.isIs_collect();
                if(!flag){
                    //未收藏
                    int pk = dynamic.getId();
                    Map<String, Object> map = new HashMap<>();
                    map.put("post_id", pk);
                    setCollectStyle(true);
                    HelloHttp.sendPostRequest("api/post/like", map, new okhttp3.Callback() {

                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e("Detail", "FAILURE");
                            Looper.prepare();
                            setCollectStyle(false);
                            Toast.makeText(DetailActivity.this, "服务器未响应", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String responseData = response.body().string();
                            Log.d("Detail", responseData);
                            String result = null;
                            try {
                                result = new JSONObject(responseData).getString("status");
                            } catch (JSONException e) {
                                setCollectStyle(false);
                                e.printStackTrace();
                            }
                            if (result != null) {
                                if(result.equals("Success")) {
                                    Looper.prepare();
                                    Snackbar.make(view,"收藏成功",Snackbar.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                                else if(result.equals("Failure")) {
                                    Looper.prepare();
                                    setCollectStyle(false);
                                    Toast.makeText(DetailActivity.this,"记录已存在", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                                else if(result.equals("UnknownError")){
                                    Looper.prepare();
                                    setCollectStyle(false);
                                    Toast.makeText(DetailActivity.this,"未知错误", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                                else {
                                    Looper.prepare();
                                    setCollectStyle(false);
                                    Toast.makeText(DetailActivity.this, result, Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                            }
                        }
                    });
                }
                else {
                    //已收藏
                    int pk = dynamic.getId();
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", pk);
                    setCollectStyle(false);
                    HelloHttp.sendDeleteRequest("api/post/like", map, new okhttp3.Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e("Detail", "FAILURE");
                            Looper.prepare();
                            setCollectStyle(true);
                            Toast.makeText(DetailActivity.this, "服务器未响应", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String responseData = response.body().string();
                            Log.d("Detail", responseData);
                            String result = null;
                            try {
                                result = new JSONObject(responseData).getString("status");
                            } catch (JSONException e) {
                                setCollectStyle(true);
                                e.printStackTrace();
                            }
                            if (result != null) {
                                if(result.equals("Success")) {
                                    Looper.prepare();
                                    Snackbar.make(view,"取消收藏成功",Snackbar.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                                else if(result.equals("Failure")) {
                                    Looper.prepare();
                                    setCollectStyle(true);
                                    Toast.makeText(DetailActivity.this,"记录不存在", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                                else if(result.equals("UnknownError")){
                                    Looper.prepare();
                                    setCollectStyle(true);
                                    Toast.makeText(DetailActivity.this,"未知错误", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                                else {
                                    Looper.prepare();
                                    setCollectStyle(true);
                                    Toast.makeText(DetailActivity.this, result, Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private void initData() {
        Map<String, Object> map = new HashMap<>();
        HelloHttp.sendGetRequest("api/post/brief/" + Integer.toString(postId), map, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("DetailActivity", "FAILURE");
                Looper.prepare();
                Toast.makeText(DetailActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                Log.d("DetailActivity", responseData);
                try {
                    final JSONObject jsonObject = new JSONObject(responseData);
                    dynamic.setUsername(jsonObject.getString("username"));
                    dynamic.setIntroduction(jsonObject.getString("introduction"));
                    dynamic.setPub_time(jsonObject.getString("Pub_time"));
                    dynamic.setSrc(jsonObject.getString("profile_picture"));
                    dynamic.setLikes_num(jsonObject.getInt("likes_num"));
                    dynamic.setCom_num(jsonObject.getInt("com_num"));
                    dynamic.setIs_collect(jsonObject.getBoolean("is_shoucang"));
                    dynamic.setIs_like(jsonObject.getBoolean("is_dianzan"));
                    dynamic.setId(jsonObject.getInt("post_id"));
                    dynamic.setUserId(jsonObject.getInt("user_id"));
                    Map<String, Object> map2 = new HashMap<>();
                    map2.put("postid", postId);
                    HelloHttp.sendGetRequest("api/photoList", map2, new okhttp3.Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e("DetailActivity", "FAILURE");
                            Looper.prepare();
                            Toast.makeText(DetailActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String responseData = response.body().string();
                            Log.d("DetailActivity", responseData);
                            try {
                                JSONObject jsonObject1 = new JSONObject(responseData);
                                JSONArray jsonArray = jsonObject1.getJSONArray("result");
                                ArrayList<String> arrayList = new ArrayList<>();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    arrayList.add("http://ktchen.cn"+jsonObject.getString("photo"));
                                }
                                dynamic.setPhotos(arrayList);
                                mHandler.sendEmptyMessageDelayed(1, 0);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                String result = null;
                                try {
                                    result = new JSONObject(responseData).getString("status");
                                    Looper.prepare();
                                    Toast.makeText(DetailActivity.this, result, Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    String result = null;
                    try {
                        result = new JSONObject(responseData).getString("status");
                        Looper.prepare();
                        Toast.makeText(DetailActivity.this, result, Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg)
        {
            if(msg.what == 1)
            {
                if(TextUtils.isEmpty(dynamic.getIntroduction())) {
                    tv_introduction.setVisibility(View.GONE);
                }
                else {
                    tv_introduction.setVisibility(View.VISIBLE);
                    String s1 = dynamic.getUsername();
                    String s2 = s1 + ":\t" + dynamic.getIntroduction();
                    SpannableString ss = new SpannableString(s2);
                    ss.setSpan(new ForegroundColorSpan(Color.parseColor("#2b5a83")), 0,s1.length()+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_introduction.setText(ss);
                }
                Log.d("DetailActivity", Integer.toString(myId));
                Log.d("DetailActivity", Integer.toString(userId));
                if(myId == userId) {
                    ib_menu.setVisibility(View.VISIBLE);
                }
                else {
                    ib_menu.setVisibility(View.GONE);
                }
                tv_like.setText(dynamic.getLikes_num()+"次赞");
                tv_time.setText(dynamic.getPub_time());
                tv_review.setText("查看全部"+dynamic.getCom_num()+"条评论");
                tv_username.setText(dynamic.getUsername());
                Glide.with(DetailActivity.this).load("http://ktchen.cn"+dynamic.getSrc()).into(ci_head);
                changeButtonStyle(dynamic.isIs_like(), dynamic.isIs_collect());
//                ib_like.setImageResource(dynamic.isIs_like() ? R.drawable.like1 : R.drawable.like2);
//                ib_collect.setImageResource(dynamic.isIs_collect() ? R.drawable.collect1 : R.drawable.collect2);
                ninePhotoLayout.setData(dynamic.getPhotos());
            }
        }
    };

    @Override
    public void onClickNinePhotoItem(BGANinePhotoLayout ninePhotoLayout, View view, int position, String model, List<String> models) {
        photoPreviewWrapper();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (requestCode == PRC_PHOTO_PREVIEW) {
            Toast.makeText(this, "您拒绝了图片预览所需要的相关权限!", Toast.LENGTH_SHORT).show();
        }
    }

    @AfterPermissionGranted(PRC_PHOTO_PREVIEW)
    private void photoPreviewWrapper() {
        if (ninePhotoLayout == null) {
            return;
        }
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            File downloadDir = new File(Environment.getExternalStorageDirectory(), "InsDownload");
            BGAPhotoPreviewActivity.IntentBuilder photoPreviewIntentBuilder = new BGAPhotoPreviewActivity.IntentBuilder(this)
                    .saveImgDir(downloadDir); // 保存图片的目录，如果传 null，则没有保存图片功能
            if (ninePhotoLayout.getItemCount() == 1) {
                // 预览单张图片
                photoPreviewIntentBuilder.previewPhoto(ninePhotoLayout.getCurrentClickItem());
            } else if (ninePhotoLayout.getItemCount() > 1) {
                // 预览多张图片
                photoPreviewIntentBuilder.previewPhotos(ninePhotoLayout.getData())
                        .currentPosition(ninePhotoLayout.getCurrentClickItemPosition()); // 当前预览图片的索引
            }
            startActivity(photoPreviewIntentBuilder.build());
        } else {
            EasyPermissions.requestPermissions(this, "图片预览需要以下权限:\n\n1.访问设备上的照片", PRC_PHOTO_PREVIEW, perms);
        }
    }

    private void changeButtonStyle(final boolean flagLike, final boolean flagCollect) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("DetailActivity", Boolean.toString(flagLike));
                ib_like.setImageResource(flagLike ? R.drawable.like2 : R.drawable.like1);
                ib_collect.setImageResource(flagCollect ? R.drawable.collect2 : R.drawable.collect1);
            }
        });
    }
    private void setLikeStyle(final boolean flag) {
        runOnUiThread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void run() {
                dynamic.setIs_like(flag);
                //ib_like.setImageResource(flag ? R.drawable.like2 : R.drawable.like1);
                if(flag) {
                    AnimatorSet animatorSet = new AnimatorSet();
                    ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(ib_like, "rotation", 0f, 360f);
                    rotationAnim.setDuration(300);
                    rotationAnim.setInterpolator(ACCELERATE_INTERPOLATOR);
                    ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(ib_like, "scaleX", 0.2f, 1f);
                    bounceAnimX.setDuration(300);
                    bounceAnimX.setInterpolator(OVERSHOOT_INTERPOLATOR);
                    ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(ib_like, "scaleY", 0.2f, 1f);
                    bounceAnimY.setDuration(300);
                    bounceAnimY.setInterpolator(OVERSHOOT_INTERPOLATOR);
                    bounceAnimY.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            ib_like.setImageResource(R.drawable.like2);
                        }
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            // heartAnimationsMap.remove(holder);
                            //dispatchChangeFinishedIfAllAnimationsEnded(holder);
                        }
                    });
                    animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim);
                    animatorSet.start();
                }
                else {
                    AnimatorSet animatorSet = new AnimatorSet();
                    ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(ib_like, "rotation", 0f, 360f);
                    rotationAnim.setDuration(300);
                    rotationAnim.setInterpolator(ACCELERATE_INTERPOLATOR);
                    ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(ib_like, "scaleX", 0.2f, 1f);
                    bounceAnimX.setDuration(300);
                    bounceAnimX.setInterpolator(OVERSHOOT_INTERPOLATOR);
                    ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(ib_like, "scaleY", 0.2f, 1f);
                    bounceAnimY.setDuration(300);
                    bounceAnimY.setInterpolator(OVERSHOOT_INTERPOLATOR);
                    bounceAnimY.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            ib_like.setImageResource(R.drawable.like1);
                        }
                        @Override
                        public void onAnimationEnd(Animator animation) {

                        }
                    });
                    animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim);
                    animatorSet.start();
                }
            }
        });
    }
    private void setCollectStyle(final boolean flag) {
        runOnUiThread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void run() {
                dynamic.setIs_collect(flag);
                //ib_collect.setImageResource(flag ? R.drawable.collect2 : R.drawable.collect1);
                if(flag) {
                    AnimatorSet animatorSet = new AnimatorSet();
                    ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(ib_collect, "rotation", 0f, 360f);
                    rotationAnim.setDuration(300);
                    rotationAnim.setInterpolator(ACCELERATE_INTERPOLATOR);
                    ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(ib_collect, "scaleX", 0.2f, 1f);
                    bounceAnimX.setDuration(300);
                    bounceAnimX.setInterpolator(OVERSHOOT_INTERPOLATOR);
                    ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(ib_collect, "scaleY", 0.2f, 1f);
                    bounceAnimY.setDuration(300);
                    bounceAnimY.setInterpolator(OVERSHOOT_INTERPOLATOR);
                    bounceAnimY.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            ib_collect.setImageResource(R.drawable.collect2);
                        }
                        @Override
                        public void onAnimationEnd(Animator animation) {

                        }
                    });
                    animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim);
                }
                else {
                    AnimatorSet animatorSet = new AnimatorSet();
                    ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(ib_collect, "rotation", 0f, 360f);
                    rotationAnim.setDuration(300);
                    rotationAnim.setInterpolator(ACCELERATE_INTERPOLATOR);
                    ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(ib_collect, "scaleX", 0.2f, 1f);
                    bounceAnimX.setDuration(300);
                    bounceAnimX.setInterpolator(OVERSHOOT_INTERPOLATOR);
                    ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(ib_collect, "scaleY", 0.2f, 1f);
                    bounceAnimY.setDuration(300);
                    bounceAnimY.setInterpolator(OVERSHOOT_INTERPOLATOR);
                    bounceAnimY.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            ib_collect.setImageResource(R.drawable.collect1);
                        }
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            // heartAnimationsMap.remove(holder);
                            //dispatchChangeFinishedIfAllAnimationsEnded(holder);
                        }
                    });
                    animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim);
                    animatorSet.start();
                }
            }
        });
    }
}
