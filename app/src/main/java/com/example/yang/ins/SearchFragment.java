package com.example.yang.ins;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ajguan.library.EasyRefreshLayout;
import com.ajguan.library.LoadModel;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.yang.ins.Utils.HelloHttp;
import com.example.yang.ins.adapter.FollowPersonAdapter;
import com.example.yang.ins.bean.Person;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class SearchFragment extends Fragment {
    private View view;
    private Button btn_search;
    private EditText et_search;
    private List<Person> list;
    private RecyclerView recyclerView;
    private EasyRefreshLayout easyRefreshLayout;
    private FollowPersonAdapter adapter;
    private int last_user_id = 0;
    private int myId = -10;
    private String last_string = null;
    public static SearchFragment newInstance(String param1) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString("agrs1", param1);
        fragment.setArguments(args);
        return fragment;
    }

    public SearchFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);
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
            Toast.makeText(getContext(), "全局内存中保存的信息为空", Toast.LENGTH_SHORT).show();
        }
        list = new ArrayList<>();
        btn_search = (Button) view.findViewById(R.id.btn_search);
        et_search = (EditText) view.findViewById(R.id.et_search);
        et_search.setText("");
        Drawable db_nickname=getResources().getDrawable(R.drawable.search2);
        db_nickname.setBounds(0,0,75,75);
        et_search.setCompoundDrawables(db_nickname,null,null,null);
        adapter = new FollowPersonAdapter(R.layout.item_follow, list, myId);
        initView();
        initAdapter();
        adapter.bindToRecyclerView(recyclerView);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = et_search.getText().toString().trim();
                if(s == null || s.length() <= 0) {
                    showToast("您还未填写要搜索的内容");
                    return;
                }
                if(s.length() > 15) {
                    showToast("您搜索的关键字长度过长");
                    return;
                }
                adapter.setEmptyView(R.layout.empty_list);
                adapter.setHeaderFooterEmpty(true, true);
                last_string = s;
                initData(s);
                initAdapter();
            }
        });
        return view;
    }

    private void showToast(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }

    private void initView() {
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_search);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        easyRefreshLayout = (EasyRefreshLayout) view.findViewById(R.id.easylayout);
        easyRefreshLayout.setEnablePullToRefresh(false);
        easyRefreshLayout.addEasyEvent(new EasyRefreshLayout.EasyEvent() {
            @Override
            public void onLoadMore() {
                Map<String, Object> map = new HashMap<>();
                map.put("searchType", "user");
                map.put("keyword", last_string);
                Map<String, Object> map2 = new HashMap<>();
                map2.put("page", 0);
                map2.put("user_id", last_user_id);
                HelloHttp.sendSpecialPostRequest("api/search", map2, map, new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                       Log.e("SearchFragment", "FAILURE");
                       Looper.prepare();
                       showToast("服务器错误");
                       Looper.loop();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseData = response.body().string();
                        Log.d("SearchFragment", responseData);
                        try {
                            JSONObject jsonObject1 = new JSONObject(responseData);
                            String result = jsonObject1.getString("status");
                            if(result.equals("Success")) {
                                JSONArray jsonArray = jsonObject1.getJSONArray("result");
                                int length = jsonArray.length();
                                for(int i = 0; i < length; i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    Person person = new Person();
                                    person.setId(jsonObject.getInt("user_id"));
                                    person.setName(jsonObject.getString("username"));
                                    person.setIsFollowed(jsonObject.getBoolean("is_guanzhu"));
                                    person.setSrc(jsonObject.getString("profile_picture"));
                                    person.setNickname(jsonObject.getString("nickname"));
                                    list.add(person);
                                }
                                last_user_id = list.get(list.size()-1).getId();
                                mHandler.sendEmptyMessageDelayed(1, 0);
                            }
                            else if(result.equals("null")) {
                                Looper.prepare();
                                Toast.makeText(getContext(),"没有更多了", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                                return;
                            }
                            else {
                                Looper.prepare();
                                showToast(result);
                                Looper.loop();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                easyRefreshLayout.loadMoreComplete(new EasyRefreshLayout.Event() {
                    @Override
                    public void complete() {
                        adapter.setNewData(list);
                        adapter.notifyDataSetChanged();
                    }
                }, 500);
            }

            @Override
            public void onRefreshing() {
                //easyRefreshLayout.refreshComplete();
            }
        });
    }

    private void initData(String s) {
        list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("searchType", "user");
        map.put("keyword", s);
        Map<String, Object> map2 = new HashMap<>();
        map2.put("page", 1);
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken()
                    ,InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HelloHttp.sendSpecialPostRequest("api/search", map2, map, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("SearchFragment", "FAILURE");
                Looper.prepare();
                showToast("服务器错误");
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                list.clear();
                String responseData = response.body().string();
                Log.d("SearchFragment", responseData);
                try {
                    JSONObject jsonObject1 = new JSONObject(responseData);
                    String result = jsonObject1.getString("status");
                    if(result.equals("Success")) {
                        JSONArray jsonArray = jsonObject1.getJSONArray("result");
                        int length = jsonArray.length();
                        for(int i = 0; i < length; i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Person person = new Person();
                            person.setId(jsonObject.getInt("user_id"));
                            person.setName(jsonObject.getString("username"));
                            person.setIsFollowed(jsonObject.getBoolean("is_guanzhu"));
                            person.setSrc(jsonObject.getString("profile_picture"));
                            person.setNickname(jsonObject.getString("nickname"));
                            list.add(person);
                        }
                        last_user_id = list.get(list.size()-1).getId();
                        mHandler.sendEmptyMessageDelayed(1, 0);
                    }
                    else if(result.equals("null")) {
                        Looper.prepare();
                        Toast.makeText(getContext(),"没有搜索到您找寻找的内容，减少关键字再试试吧", Toast.LENGTH_SHORT).show();
                        mHandler.sendEmptyMessageDelayed(1, 0);
                        Looper.loop();
                        return;
                    }
                    else {
                        Looper.prepare();
                        showToast(result);
                        mHandler.sendEmptyMessageDelayed(1, 0);
                        Looper.loop();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessageDelayed(1, 0);
                }
            }
        });
    }

    private void initAdapter() {
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onItemChildClick(final BaseQuickAdapter adapter, final View view, final int position) {
            if (view.getId() == R.id.follow_cancel) {
                int id = list.get(position).getId();
                boolean flag = list.get(position).getIsFollowed();
                Map<String, Object> map = new HashMap<>();
                map.put("pk", id);
                if(flag) {
                    changeStyle(false, position);
                    HelloHttp.sendDeleteRequest("api/user/followyou", map, new okhttp3.Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e("FollowActivity", "FAILURE");
                            changeStyle(true, position);
                            Looper.prepare();
                            Snackbar.make(view,"服务器错误",Snackbar.LENGTH_SHORT).show();
                            Looper.loop();
                        }

                        @TargetApi(Build.VERSION_CODES.KITKAT)
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
                                Snackbar.make(view,"已取消关注",Snackbar.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                            else {
                                changeStyle(true, position);
                                if(result.equals("UnknownError")) {
                                    Looper.prepare();
                                    Snackbar.make(view,"未知错误",Snackbar.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                                else {
                                    Looper.prepare();
                                    Toast.makeText(getContext(), result, Toast.LENGTH_SHORT ).show();
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
                            Snackbar.make(view,"服务器错误",Snackbar.LENGTH_SHORT).show();
                            Looper.loop();
                        }

                        @TargetApi(Build.VERSION_CODES.KITKAT)
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
                                Snackbar.make(view,"关注成功",Snackbar.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                            else {
                                changeStyle(false, position);
                                if(result.equals("UnknownError")) {
                                    Looper.prepare();
                                    Snackbar.make(view,"未知错误",Snackbar.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                                else if(result.equals("Failure")) {
                                    Looper.prepare();
                                    Snackbar.make(view,"错误：重复的关注请求，已取消关注",Snackbar.LENGTH_SHORT).show();
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
            else if(view.getId() == R.id.follow_head || view.getId() == R.id.follow_nickname || view.getId() == R.id.follow_username) {
                int userId = list.get(position).getId();
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void changeStyle(final boolean flag, final int position) {
        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void run() {
                if(flag) {
                    list.get(position).setIsFollowed(true);
                    Button btn = (Button) adapter.getViewByPosition(recyclerView, position, R.id.follow_cancel);
                    if (btn != null) {
                        btn.setText("关注中");
                        btn.setTextColor(Color.BLACK);
                        btn.setBackground(getResources().getDrawable(R.drawable.buttonshape2));
                    }
                }
                else {
                    list.get(position).setIsFollowed(false);
                    Button btn = (Button) adapter.getViewByPosition(recyclerView, position, R.id.follow_cancel);
                    if (btn != null) {
                        btn.setText("关注");
                        btn.setTextColor(Color.WHITE);
                        btn.setBackground(getResources().getDrawable(R.drawable.buttonshape3));
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        et_search.setText("");
    }
}