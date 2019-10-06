package com.example.yang.iShare;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ajguan.library.EasyRefreshLayout;
import com.ajguan.library.LoadModel;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.yang.iShare.Utils.HelloHttp;
import com.example.yang.iShare.adapter.ReviewAdapter;
import com.example.yang.iShare.bean.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

public class CommentActivity extends AppCompatActivity {

    private int postid;
    private List<Review> list;
    private ImageButton ib_back;
    private RecyclerView recyclerView;
    private EasyRefreshLayout easyRefreshLayout;
    private ReviewAdapter adapter;
    private EditText et_send;
    private ImageButton ib_send;
    private int myId = -10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        Intent intent  = getIntent();
        postid = intent.getIntExtra("post_id", 0);
        MainApplication application = MainApplication.getInstance();
        Map<String, Integer> mapParam = application.mInfoMap;
        for(Map.Entry<String, Integer> item_map:mapParam.entrySet()) {
            if(item_map.getKey().equals("id"))
                myId = item_map.getValue();
        }
        if(myId == -10) {
            Toast.makeText(CommentActivity.this, "全局内存中保存的信息为空", Toast.LENGTH_SHORT).show();
        }
        ib_back = (ImageButton) findViewById(R.id.ib_comment_back);
        et_send = (EditText) findViewById(R.id.et_send);
        ib_send = (ImageButton) findViewById(R.id.ib_send);
        ib_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = et_send.getText().toString().trim();
                if(s.length() <= 0 || s == null) {
                    Toast.makeText(CommentActivity.this, "您还没有填写有效评论", Toast.LENGTH_SHORT).show();
                    ib_send.startAnimation(AnimationUtils.loadAnimation(CommentActivity.this, R.anim.shake_error));
                    return;
                }
                if(s.length() <= 4) {
                    Toast.makeText(CommentActivity.this, "您的评论太短，请填写些有意义的评论再来", Toast.LENGTH_SHORT).show();
                    ib_send.startAnimation(AnimationUtils.loadAnimation(CommentActivity.this, R.anim.shake_error));
                    return;
                }
                if(s.length() > 80) {
                    Toast.makeText(CommentActivity.this, "您的评论太长了，字数限制为80字符", Toast.LENGTH_SHORT ).show();
                    ib_send.startAnimation(AnimationUtils.loadAnimation(CommentActivity.this, R.anim.shake_error));
                    return;
                }
                et_send.setText("");
                Map<String, Object> map = new HashMap<>();
                map.put("post_id", postid);
                map.put("content", s);
                HelloHttp.sendPostRequest("api/post/comments", map, new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("CommentActivity", "FAILURE");
                        Looper.prepare();
                        Toast.makeText(CommentActivity.this, "服务器未响应", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseData = response.body().string();
                        Log.d("CommentActivity", responseData);
                        String result = null;
                        try {
                            result = new JSONObject(responseData).getString("status");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(result.equals("Success")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(CommentActivity.this,"发表成功", Toast.LENGTH_SHORT).show();
                                    try {
                                        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                        inputMethodManager.hideSoftInputFromWindow(CommentActivity.this.getCurrentFocus().getWindowToken()
                                                ,InputMethodManager.HIDE_NOT_ALWAYS);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    initData();
                                    initAdapter();
                                }
                            });
                        }
                        else if(result.equals("UnknownError")){
                            Looper.prepare();
                            Toast.makeText(CommentActivity.this,"未知错误", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                        else {
                            Looper.prepare();
                            Toast.makeText(CommentActivity.this, result, Toast.LENGTH_SHORT ).show();
                            Looper.loop();
                        }
                    }
                });

            }
        });
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        adapter = new ReviewAdapter(R.layout.item_comment, list);
        initView();
        initData();
        adapter.setNewData(list);
        initAdapter();
        adapter.bindToRecyclerView(recyclerView);
        adapter.setEmptyView(R.layout.empty_comment);
        adapter.setHeaderFooterEmpty(true, true);
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.rv_comment);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        easyRefreshLayout = (EasyRefreshLayout) findViewById(R.id.easylayout);
        easyRefreshLayout.setLoadMoreModel(LoadModel.NONE);
        easyRefreshLayout.addEasyEvent(new EasyRefreshLayout.EasyEvent() {
            @Override
            public void onLoadMore() {
                //不具备上拉加载功能
            }

            @Override
            public void onRefreshing() {
                initData();
                initAdapter();
                easyRefreshLayout.loadMoreComplete(new EasyRefreshLayout.Event() {
                    @Override
                    public void complete() {
                        adapter.setNewData(list);
                        easyRefreshLayout.refreshComplete();
                    }
                }, 500);
            }
        });
        //recyclerView.addItemDecoration();
    }

    private void initData() {
        Map<String, Object> map = new HashMap<>();
        map.put("post_id", postid);
        list = new ArrayList<>();
        HelloHttp.sendGetRequest("api/post/comments", map, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("CommentActivity", "FAILURE");
                Looper.prepare();
                Toast.makeText(CommentActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                Log.d("CommentActivity", responseData);
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(responseData);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Review myReview = new Review();
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        myReview.setId(jsonObject.getInt("comment_id"));
                        myReview.setCommenter(jsonObject.getString("username"));
                        myReview.setCommenterId(jsonObject.getInt("user_id"));
                        myReview.setPub_time(jsonObject.getString("time"));
                        myReview.setContent(jsonObject.getString("content"));
                        myReview.setSrc("http://ktchen.cn"+jsonObject.getString("profile_picture"));
                        list.add(myReview);
                    }
                    mHandler.sendEmptyMessageDelayed(1, 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                    try {
                        String result = null;
                        result = new JSONObject(responseData).getString("status");
                        Looper.prepare();
                        Toast.makeText(CommentActivity.this, result, Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    private void initAdapter() {
        adapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, final int position) {
            if(myId == list.get(position).getCommenterId()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CommentActivity.this); //定义一个AlertDialog
                String[] strarr = {"复制评论到剪贴板","删除评论","取消"};
                builder.setItems(strarr, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface arg0, int arg1)
                    {
                        if (arg1 == 0) {
                            String s = list.get(position).getContent();
                            s = s + "\n著作权归作者所有。\n" +
                                    "商业转载请联系作者获得授权，非商业转载请注明出处。\n" +
                                    "作者：" +
                                    list.get(position).getCommenter() +
                                    "\n" +
                                    "来源：Instagram";
                            //获取剪贴板管理器：
                            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                            // 创建普通字符型ClipData
                            ClipData mClipData = ClipData.newPlainText("Label", s);
                            // 将ClipData内容放到系统剪贴板里。
                            cm.setPrimaryClip(mClipData);
                            Toast.makeText(CommentActivity.this, "内容已复制到剪贴板", Toast.LENGTH_SHORT ).show();
                        }else if(arg1 == 1){
                            int temp = list.get(position).getId();
                            deleteComment(temp);
                        }
                        else if(arg1 == 2) {
                            return;
                        }
                    }
                });
                builder.show();
            }
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(CommentActivity.this); //定义一个AlertDialog
                String[] strarr = {"复制评论到剪贴板","取消"};
                builder.setItems(strarr, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface arg0, int arg1)
                    {
                        if (arg1 == 0) {
                            String s = list.get(position).getContent();
                            s = s + "\n著作权归作者所有。\n" +
                                    "商业转载请联系作者获得授权，非商业转载请注明出处。\n" +
                                    "作者：" +
                                    list.get(position).getCommenter() +
                                    "\n" +
                                    "来源：Instagram";
                            //获取剪贴板管理器：
                            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                            // 创建普通字符型ClipData
                            ClipData mClipData = ClipData.newPlainText("Label", s);
                            // 将ClipData内容放到系统剪贴板里。
                            if (cm != null) {
                                cm.setPrimaryClip(mClipData);
                                Toast.makeText(CommentActivity.this, "内容已复制到剪贴板", Toast.LENGTH_SHORT ).show();
                            }
                            else {
                                Toast.makeText(CommentActivity.this, "内容为空", Toast.LENGTH_SHORT ).show();
                            }
                        }else if(arg1 == 1){
                            return;
                        }
                    }
                });
                builder.show();
            }
            return true;
            }
        });
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if(view.getId() == R.id.comment_head || view.getId() == R.id.comment_username) {
                    int userId = list.get(position).getCommenterId();
                    if(myId == userId) {
                        //这个人是我自己
                        Intent intent = new Intent(CommentActivity.this, MainActivity.class);
                        intent.putExtra("me_id",userId);
                        startActivity(intent);
                    }
                    else {
                        //这个人不是我
                        Intent intent = new Intent(CommentActivity.this, UserActivity.class);
                        intent.putExtra("userId", userId);
                        startActivity(intent);
                    }
                }
            }
        });
        recyclerView.setAdapter(adapter);
    }
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg)
        {
            if(msg.what == 1)
            {
                adapter.setNewData(list);
            }
        }
    };
    private void deleteComment(int comment_id) {
        Map<String, Object> map = new HashMap<>();
        map.put("comment_id", comment_id);
        HelloHttp.sendDeleteRequest("api/post/comments", map, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("CommentActivity", "FAILURE");
                Looper.prepare();
                Toast.makeText(CommentActivity.this, "服务器未响应", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                Log.d("CommentActivity", responseData);
                String result = null;
                try {
                    result = new JSONObject(responseData).getString("status");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(result.equals("Success")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CommentActivity.this,"删除成功", Toast.LENGTH_SHORT).show();
                            //emmmm想不明白该怎么不刷新只把它删了，我要是把这个item remove了，那是不是说
                            //我在按完删除键以后在响应回来之前刷新了页面，这个时候有新的评论加进来了
                            //换句话说也就是list的position发生了变化，这个时候你这个响应回来了，就删除错了
                            //所以在想到更好的解决方案之前，就先整页刷新吧
                            initData();
                            initAdapter();
                        }
                    });
                }
                else if(result.equals("Failure")) {
                    Looper.prepare();
                    Toast.makeText(CommentActivity.this,"这条评论不是您的", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                else if(result.equals("UnknownError")){
                    Looper.prepare();
                    Toast.makeText(CommentActivity.this,"未知错误", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                else {
                    Looper.prepare();
                    Toast.makeText(CommentActivity.this, result, Toast.LENGTH_SHORT ).show();
                    Looper.loop();
                }
            }
        });
    }

}
