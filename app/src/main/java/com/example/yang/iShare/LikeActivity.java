package com.example.yang.iShare;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.ajguan.library.EasyRefreshLayout;
import com.ajguan.library.LoadModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.yang.iShare.Utils.HelloHttp;
import com.example.yang.iShare.adapter.AlbumAdapter;
import com.example.yang.iShare.bean.Dynamic;
import com.example.yang.iShare.bean.Person;

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

public class LikeActivity extends AppCompatActivity {

    private List<Dynamic> mDynamicList;
    private RecyclerView recyclerView;
    private ImageButton ib_back;
    private EasyRefreshLayout easyRefreshLayout;
    private AlbumAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like);
        adapter = new AlbumAdapter(R.layout.item_album, mDynamicList);
        ib_back = (ImageButton) findViewById(R.id.ib_like_back);
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initView();
        initData();
        adapter.setNewData(mDynamicList);
        initAdapter();
        adapter.bindToRecyclerView(recyclerView);
        adapter.setEmptyView(R.layout.empty_like);
        adapter.setHeaderFooterEmpty(true, true);
    }
    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.rv_like);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        //recyclerView.addItemDecoration();
        easyRefreshLayout = (EasyRefreshLayout) findViewById(R.id.el_like);
        easyRefreshLayout.setLoadMoreModel(LoadModel.NONE);
        easyRefreshLayout.addEasyEvent(new EasyRefreshLayout.EasyEvent() {
            @Override
            public void onLoadMore() {

            }

            @Override
            public void onRefreshing() {
                initData();
                initAdapter();
                easyRefreshLayout.loadMoreComplete(new EasyRefreshLayout.Event() {
                    @Override
                    public void complete() {
                        adapter.setNewData(mDynamicList);
                        easyRefreshLayout.refreshComplete();
                    }
                }, 500);
            }
        });
    }
    private void initData() {
        Map<String, Object> map = new HashMap<>();
        mDynamicList = new ArrayList<>();
        HelloHttp.sendGetRequest("api/post/dianzan",map,new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("LikeActivity", "FAILURE");
                Looper.prepare();
                Toast.makeText(LikeActivity.this, "服务器未响应", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                Log.d("LikeActivity", responseData);
                try {
                    JSONObject jsonObject1 = new JSONObject(responseData);
                    JSONArray jsonArray = jsonObject1.getJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Dynamic dynamic = new Dynamic();
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        dynamic.setId(jsonObject.getInt("post_id"));
                        dynamic.setPost_userId(jsonObject.getInt("post_user_id"));
                        dynamic.setIs_multi(jsonObject.getBoolean("is_many"));
                        dynamic.setPhoto0("http://ktchen.cn"+jsonObject.getString("photo_0"));
                        mDynamicList.add(dynamic);
                    }
                    mHandler.sendEmptyMessageDelayed(1, 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                    String result = null;
                    try {
                        result = new JSONObject(responseData).getString("status");
                        Looper.prepare();
                        Toast.makeText(LikeActivity.this, result, Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }
    @SuppressWarnings("unchecked")
    private void initAdapter() {
        //firstAdapter.openLoadAnimation();
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(LikeActivity.this, DetailActivity.class);
                int id = mDynamicList.get(position).getId();
                int user_id = mDynamicList.get(position).getPost_userId();
                intent.putExtra("user_id", user_id);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @SuppressLint("CheckResult")
        @Override
        public void handleMessage(Message msg)
        {
            if(msg.what == 1)
            {
                adapter.setNewData(mDynamicList);
            }
        }
    };
}
