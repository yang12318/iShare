package com.example.yang.iShare;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.ajguan.library.EasyRefreshLayout;
import com.ajguan.library.LoadModel;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.yang.iShare.Utils.HelloHttp;
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


public class AboutFollowFragment extends Fragment{
    private List<Info1> mInfoList;
    private RecyclerView recyclerView;
    private EasyRefreshLayout easyRefreshLayout;
    private View view;
    private Info1Adapter adapter;
    private String mtd_id;

    public static AboutFollowFragment newInstance(String mtd_id) {
        AboutFollowFragment f = new AboutFollowFragment();
        Bundle b = new Bundle();
        b.putString("id", mtd_id);
        f.setArguments(b);
        return f;
    }

    public AboutFollowFragment() {

    }

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
        view = inflater.inflate(R.layout.fragment_about_follow, container, false);
        Log.e("AboutFollow", "onCreateView");
        Bundle bundle = getArguments();
        adapter = new Info1Adapter(R.layout.item_about_follow, mInfoList);
        adapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        initView();
        initData();
        adapter.setNewData(mInfoList);
        initAdapter();
        adapter.bindToRecyclerView(recyclerView);
        adapter.setEmptyView(R.layout.empty_about_follow);
        adapter.setHeaderFooterEmpty(true, true);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("AboutFollowFragment", "onResume");
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initView() {
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_about_follow);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        easyRefreshLayout = (EasyRefreshLayout) view.findViewById(R.id.easylayout);
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
//                        adapter.setNewData(mInfoList);
                        easyRefreshLayout.refreshComplete();
                    }
                }, 500);
            }
        });
    }

    private void initData() {
        Map<String, Object> map = new HashMap<>();
        mInfoList = new ArrayList<>();
        HelloHttp.sendGetRequest("api/user/friendmessage", map, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("AboutFollowFragment", "FAILURE");
                Looper.prepare();
                Toast.makeText(getActivity(), "服务器错误", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                Log.d("AboutFollowFragment", responseData);
                try {
                    JSONObject jsonObject1 = new JSONObject(responseData);
                    JSONArray jsonArray = null;
                    jsonArray = jsonObject1.getJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Info1 info1 = new Info1();
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        info1.setUserId(jsonObject.getInt("user_id"));
                        info1.setPostId(jsonObject.getInt("post_id"));
                        info1.setSrc(jsonObject.getString("profile_picture"));
                        info1.setTime(jsonObject.getString("time"));
                        info1.setPost_userId(jsonObject.getInt("post_user_id"));
                        info1.setPhoto_0(jsonObject.getString("photo_0"));
                        info1.setUserName(jsonObject.getString("username"));
                        mInfoList.add(info1);
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
    }

    private void initAdapter() {
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.about_follow_username || view.getId() == R.id.about_follow_head) {
                    int myId = -9;
                    int userId = mInfoList.get(position).getUserId();
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
                        Toast.makeText(getActivity(), "全局内存中变量为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
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
                else if(view.getId() == R.id.about_follow_picture) {
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra("id", mInfoList.get(position).getPostId());
                    intent.putExtra("user_id",mInfoList.get(position).getPost_userId());
                    startActivity(intent);
                }
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
                adapter.setNewData(mInfoList);
            }
        }
    };
}
