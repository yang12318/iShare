package com.example.yang.iShare;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.ajguan.library.EasyRefreshLayout;
import com.ajguan.library.LoadModel;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.example.yang.iShare.Utils.HelloHttp;
import com.example.yang.iShare.adapter.AboutMeAdapter;
import com.example.yang.iShare.adapter.Info1Adapter;
import com.example.yang.iShare.bean.Info1;

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

import static android.content.Context.MODE_PRIVATE;

public class HomeMeFragment extends Fragment{
    private List<Info1> mInfoList;
    private RecyclerView recyclerView;
    private EasyRefreshLayout easyRefreshLayout;
    private View view;
    private String mtd_id;
    private static int myId = -10;
    private AboutMeAdapter madapter;

    public static HomeMeFragment newInstance(String mtd_id) {
        HomeMeFragment f = new HomeMeFragment();
        Bundle b = new Bundle();
        b.putString("id", mtd_id);
        f.setArguments(b);
        return f;
    }

    public HomeMeFragment() {

    }
    //    @Override
//    protected int getLayoutResId() {
//        return super.getLayoutResId();
//    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
//        if (arguments != null) {
//            mtd_id = arguments.getString("id");
//        }

    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home_me, container, false);
        Log.e("HomeMe", "onCreateView");
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
        if(myId == -10) {
            Toast.makeText(getActivity(), "全局内存中保存的信息为空", Toast.LENGTH_SHORT).show();
        }
        madapter = new AboutMeAdapter(mInfoList,myId);
        madapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
//        recyclerView.setAdapter(madapter);
        //adapter = new Info1Adapter(R.layout.item_about_follow, mInfoList);
        initView();
        //initData();
        madapter.setNewData(mInfoList);
        //initAdapter();
        madapter.bindToRecyclerView(recyclerView);
        madapter.setEmptyView(R.layout.empty_home);
        madapter.setHeaderFooterEmpty(true, true);
        Bundle bundle = getArguments();
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.e("HomeMeFragment", "onResume");
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initView() {
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_home_me);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        easyRefreshLayout = (EasyRefreshLayout) view.findViewById(R.id.el_home_me);
        easyRefreshLayout.setLoadMoreModel(LoadModel.NONE);
        easyRefreshLayout.addEasyEvent(new EasyRefreshLayout.EasyEvent() {
            @Override
            public void onLoadMore() {
                //不具备上拉加载功能
            }

            @Override
            public void onRefreshing() {
                //initData();
                //initAdapter();
                easyRefreshLayout.loadMoreComplete(new EasyRefreshLayout.Event() {
                    @Override
                    public void complete() {
//                        madapter.setNewData(mInfoList);
                        easyRefreshLayout.refreshComplete();
                    }
                }, 500);
            }
        });
    }

    /*private void initData() {
        Map<String, Object> map = new HashMap<>();
        mInfoList = new ArrayList<>();
        HelloHttp.sendGetRequest("api/user/messages", map, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("AboutMeFragment", "FAILURE");
                Looper.prepare();
                Toast.makeText(getActivity(), "服务器错误", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                Log.d("AboutMeFragment", responseData);
                try {
                    JSONObject jsonObject1 = new JSONObject(responseData);
                    JSONArray jsonArray = null;
                    jsonArray = jsonObject1.getJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Info1 info1 = new Info1();
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        info1.setMs_type(jsonObject.getInt("messageType"));
                        switch (info1.getMs_type()){
                            case 1:{
                                info1.setUserId(jsonObject.getInt("user_id"));
                                info1.setSrc(jsonObject.getString("profile_picture"));
                                info1.setUserName(jsonObject.getString("username"));
                                info1.setIsFollowed(jsonObject.getBoolean("is_guanzhu"));
                                mInfoList.add(info1);
                                break;
                            }
                            case 2:{
                                info1.setUserId(jsonObject.getInt("user_id"));
                                info1.setPostId(jsonObject.getInt("post_id"));
                                info1.setPhoto_0(jsonObject.getString("photo_0"));
                                info1.setPost_userId(jsonObject.getInt("post_user_id"));
                                info1.setSrc(jsonObject.getString("profile_picture"));
                                info1.setUserName(jsonObject.getString("username"));
                                mInfoList.add(info1);
                                break;
                            }
                            case 3:{
                                info1.setUserId(jsonObject.getInt("user_id"));
                                info1.setPostId(jsonObject.getInt("post_id"));
                                info1.setSrc(jsonObject.getString("profile_picture"));
                                info1.setContent(jsonObject.getString("content"));
                                info1.setPhoto_0(jsonObject.getString("photo_0"));
                                info1.setPost_userId(jsonObject.getInt("post_user_id"));
                                info1.setUserName(jsonObject.getString("username"));
                                mInfoList.add(info1);
                                break;
                            }
                        }
                    }
                    mHandler.sendEmptyMessageDelayed(1, 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                    try {
                        String result = null;
                        result = new JSONObject(responseData).getString("status");
                        Looper.prepare();
                        Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }*/

    /*private void initAdapter() {
        madapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, final int position) {
                if (view.getId() == R.id.about_me_username || view.getId() == R.id.me_username || view.getId() == R.id.tv_me_username || view.getId() == R.id.about_me_head || view.getId() == R.id.me_head || view.getId() == R.id.ci_me_head) {
                    int userId = mInfoList.get(position).getUserId();
                    if(myId == userId) {
                        //这个人是我自己
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.putExtra("me_id",userId );
                        startActivity(intent);
                    }
                    else {
                        //这个人不是我
                        Intent intent = new Intent(getActivity(), UserActivity.class);
                        intent.putExtra("userId", userId);
                        startActivity(intent);
                    }
                }
                else if(view.getId() == R.id.about_me_picture || view.getId() == R.id.iv_me_picture || view.getId() == R.id.tv_me_comment) {
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra("id", mInfoList.get(position).getPostId());
                    intent.putExtra("user_id",mInfoList.get(position).getPost_userId());
                    startActivity(intent);
                }
                else if(view.getId() == R.id.btn_follow){
                    int id = mInfoList.get(position).getUserId();
                    boolean flag = mInfoList.get(position).getIsFollowed();
                    Map<String, Object> map = new HashMap<>();
                    map.put("pk", id);
                    if(flag) {
                        //已点赞
                        changeStyle(false, position);
                        HelloHttp.sendDeleteRequest("api/user/followyou", map, new okhttp3.Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.e("FollowActivity", "FAILURE");
                                changeStyle(true, position);
                                Looper.prepare();
                                Toast.makeText(getActivity(), "服务器错误", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }

                            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String responseData = response.body().string();
                                Log.d("FollowActivity", responseData);
                                String result = null;
                                try {
                                    result = new JSONObject(responseData).getString("status");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    changeStyle(true, position);
                                }
                                if(result.equals("Success")) {
                                    Looper.prepare();
                                    Toast.makeText(getActivity(), "已取消关注", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                                else {
                                    changeStyle(true, position);
                                    if(result.equals("UnknownError")) {
                                        Looper.prepare();
                                        Toast.makeText(getActivity(), "未知错误", Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    }
                                    else {
                                        Looper.prepare();
                                        Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT ).show();
                                        Looper.loop();
                                    }
                                }
                            }
                        });
                    }
                    else {
                        //没有关注
                        changeStyle(true, position);
                        HelloHttp.sendPostRequest("api/user/followyou", map, new okhttp3.Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.e("FollowActivity", "FAILURE");
                                changeStyle(false, position);
                                Looper.prepare();
                                Toast.makeText(getActivity(), "服务器错误", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }

                            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String responseData = response.body().string();
                                Log.d("FollowActivity", responseData);
                                String result = null;
                                try {
                                    result = new JSONObject(responseData).getString("status");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    changeStyle(false, position);
                                }
                                if(result != null && result.equals("Success")) {
                                    Looper.prepare();
                                    Toast.makeText(getActivity(), "关注成功", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                                else {
                                    changeStyle(false, position);
                                    if(result.equals("UnknownError")) {
                                        Looper.prepare();
                                        Toast.makeText(getActivity(), "未知错误", Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    }
                                    else if(result.equals("Failure")) {
                                        Looper.prepare();
                                        Toast.makeText(getActivity(), "错误：重复的关注请求，已取消关注", Toast.LENGTH_SHORT).show();
                                        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
                                        Looper.loop();
                                    }
                                    else {
                                        Looper.prepare();
                                        Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT ).show();
                                        Looper.loop();
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
        recyclerView.setAdapter(madapter);
    }*/
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @SuppressLint("CheckResult")
        @Override
        public void handleMessage(Message msg)
        {
            if(msg.what == 1)
            {
                madapter.setNewData(mInfoList);
            }
        }
    };

}
