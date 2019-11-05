package com.example.yang.iShare;
import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.TextSwitcher;
import android.widget.Toast;

import com.ajguan.library.EasyRefreshLayout;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.yang.iShare.Utils.HelloHttp;
import com.example.yang.iShare.bean.Dynamic;

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

public class HomeFragment extends Fragment implements EasyPermissions.PermissionCallbacks, BGANinePhotoLayout.Delegate{

    private int last_post_id = 0;
    private static int myId = -10;
    private List<Dynamic> list;
    private RecyclerView recyclerView;
    private DynamicAdapter adapter;
    private EasyRefreshLayout easyRefreshLayout;
    BGANinePhotoLayout mCurrentClickNpl;
    private static final int PRC_PHOTO_PREVIEW = 1;
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);
    private View view;
    public static HomeFragment newInstance(String param1) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString("agrs1", param1);
        fragment.setArguments(args);
        return fragment;
    }

    public HomeFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        MainApplication app = MainApplication.getInstance();
        Map<String, Integer> mapParam = app.mInfoMap;
        for(Map.Entry<String, Integer> item_map:mapParam.entrySet()) {
            if(item_map.getKey().equals("id")) {
                myId = item_map.getValue();
            }
        }
        if(myId == -10) {
            Toast.makeText(getActivity(), "全局内存中保存的信息为空", Toast.LENGTH_SHORT).show();
        }
        adapter = new DynamicAdapter(R.layout.item_dynamic, list,myId);
        initView();
        initData();
        adapter.setNewData(list);
        initAdapter();
        adapter.bindToRecyclerView(recyclerView);
        adapter.setEmptyView(R.layout.empty_home);
        adapter.setHeaderFooterEmpty(true, true);
        return view;
    }

    private void initView() {
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_home);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        easyRefreshLayout = (EasyRefreshLayout) view.findViewById(R.id.easylayout);
        easyRefreshLayout.addEasyEvent(new EasyRefreshLayout.EasyEvent() {
            @Override
            public void onLoadMore() {
                Map<String, Object> map = new HashMap<>();
                map.put("page", "0");
                map.put("post_id", last_post_id);
                HelloHttp.sendGetRequest("api/dynamic", map, new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("HomeFragment", "FAILURE");
                        Looper.prepare();
                        Toast.makeText(getContext(), "服务器错误", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseData = response.body().string();
                        Log.d("HomeFragment", responseData);
                        try{
                            JSONObject jsonObject1 = new JSONObject(responseData);
                            String result = jsonObject1.getString("status");
                            if(result.equals("Success")) {
                                JSONArray jsonArray1 = jsonObject1.getJSONArray("result");
                                JSONArray jsonArray2 = jsonObject1.getJSONArray("photoList");
                                final int length1 = jsonArray1.length();
                                for(int i = 0; i < length1; i++) {
                                    JSONObject jsonObject = jsonArray1.getJSONObject(i);
                                    final Dynamic dynamic = new Dynamic();
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
                                    JSONArray jsonArray = jsonArray2.getJSONArray(i);
                                    ArrayList<String> arrayList = new ArrayList<>();
                                    ArrayList<String> thumbList = new ArrayList<>();
                                    final int length2 = jsonArray.length();
                                    for (int j = 0; j < length2; j++) {
                                        JSONObject jsonObject2 = jsonArray.getJSONObject(j);
                                        arrayList.add("http://ktchen.cn" + jsonObject2.getString("photo"));
                                        thumbList.add("http://ktchen.cn" + jsonObject2.getString("photo_thumbnail"));
                                    }
                                    dynamic.setPhotos(arrayList);
                                    dynamic.setThumbnails(thumbList);
                                    list.add(dynamic);
                                    mHandler.sendEmptyMessageDelayed(1, 0);
                                }
                                last_post_id = list.get(list.size()-1).getId();
                            }
                            else if(result.equals("null")){
                                Looper.prepare();
                                Toast.makeText(getActivity(), "没有更多数据了", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                            else {
                                Looper.prepare();
                                Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
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
                        adapter.notifyDataSetChanged();
                    }
                }, 3000);
            }

            @Override
            public void onRefreshing() {
                initData();
                last_post_id = 0;
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
    }

    private void initData() {
        list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("page", "1");
        HelloHttp.sendGetRequest("api/dynamic", map, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("HomeFragment", "FAILURE");
                Looper.prepare();
                Toast.makeText(getContext(), "服务器错误", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                Log.d("HomeFragment", responseData);
                try{
                    JSONObject jsonObject1 = new JSONObject(responseData);
                    String result = jsonObject1.getString("status");
                    if(result.equals("Success")) {
                        JSONArray jsonArray1 = jsonObject1.getJSONArray("result");
                        JSONArray jsonArray2 = jsonObject1.getJSONArray("photoList");
                        final int length1 = jsonArray1.length();
                        for(int i = 0; i < length1; i++) {
                            JSONObject jsonObject = jsonArray1.getJSONObject(i);
                            final Dynamic dynamic = new Dynamic();
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
                            JSONArray jsonArray = jsonArray2.getJSONArray(i);
                            ArrayList<String> arrayList = new ArrayList<>();
                            ArrayList<String> thumbList = new ArrayList<>();
                            final int length2 = jsonArray.length();
                            for (int j = 0; j < length2; j++) {
                                JSONObject jsonObject2 = jsonArray.getJSONObject(j);
                                arrayList.add("http://ktchen.cn" + jsonObject2.getString("photo"));
                                thumbList.add("http://ktchen.cn" + jsonObject2.getString("photo_thumbnail"));
                            }
                            dynamic.setPhotos(arrayList);
                            dynamic.setThumbnails(thumbList);
                            list.add(dynamic);
                            mHandler.sendEmptyMessageDelayed(1, 0);
                        }
                        if (list.size() != 0) {
                            last_post_id = list.get(list.size()-1).getId();
                        }
                    }
                    else {
                        Looper.prepare();
                        Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initAdapter() {
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, final int position) {
            if (view.getId() == R.id.tv_username || view.getId() == R.id.ci_head) {
                int userId = list.get(position).getUserId();
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
            else if(view.getId() == R.id.ib_comment || view.getId() == R.id.tv_comment) {
                Intent intent = new Intent(getActivity(), CommentActivity.class);
                intent.putExtra("post_id", list.get(position).getId());
                startActivity(intent);
            }
            else if(view.getId() == R.id.ib_like) {
                int pk = list.get(position).getId();
                final boolean flag = list.get(position).isIs_like();
                Map<String, Object> map = new HashMap<>();
                map.put("pk", pk);
                if(!flag){
                    //未点赞
                    setLikeStyle(true,position);
                    HelloHttp.sendPostRequest("api/post/dianzan", map, new okhttp3.Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e("Detail", "FAILURE");
                            Looper.prepare();
                            setLikeStyle(false,position);
                            Snackbar.make(getView(),"服务器未响应",Snackbar.LENGTH_SHORT).show();
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
                                setLikeStyle(false,position);
                                e.printStackTrace();
                            }
                            if(result.equals("Success")) {
                                Looper.prepare();
                                Snackbar.make(getView(),"点赞成功",Snackbar.LENGTH_SHORT).show();
                                list.get(position).setLikes_num(list.get(position).getLikes_num()+1);
                                addLikeNum(position);
                                //setLikeStyle(true,position);
                                Looper.loop();
                            }
                            else if(result.equals("Failure")) {
                                Looper.prepare();
                                setLikeStyle(false,position);
                                Snackbar.make(getView(),"记录已存在",Snackbar.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                            else if(result.equals("UnknownError")){
                                Looper.prepare();
                                setLikeStyle(false,position);
                                Snackbar.make(getView(),"未知错误",Snackbar.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                            else {
                                Looper.prepare();
                                setLikeStyle(false, position);
                                Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                        }
                    });
                }
                else {
                    //已点赞
                    setLikeStyle(false,position);
                    HelloHttp.sendDeleteRequest("api/post/dianzan", map, new okhttp3.Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e("Detail", "FAILURE");
                            Looper.prepare();
                            setLikeStyle(true,position);
                            Snackbar.make(getView(),"服务器未响应",Snackbar.LENGTH_SHORT).show();
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
                                setLikeStyle(true,position);
                                e.printStackTrace();
                            }
                            if(result.equals("Success")) {
                                Looper.prepare();
                                //list.get(position).setIs_like(false);
                                //setLikeStyle(false,position);
                                list.get(position).setLikes_num(list.get(position).getLikes_num()-1);
                                addLikeNum(position);
                                Snackbar.make(getView(),"取消点赞",Snackbar.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                            else if(result.equals("Failure")) {
                                Looper.prepare();
                                setLikeStyle(true,position);
                                Snackbar.make(getView(),"记录不存在",Snackbar.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                            else if(result.equals("UnknownError")){
                                Looper.prepare();
                                setLikeStyle(true,position);
                                Snackbar.make(getView(),"未知错误",Snackbar.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                            else {
                                Looper.prepare();
                                setLikeStyle(true, position);
                                Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                        }
                    });
                }
            }
            else if(view.getId() == R.id.ib_collect) {
                boolean flag = list.get(position).isIs_collect();
                if(!flag){
                    //未收藏
                    int pk = list.get(position).getId();
                    Map<String, Object> map = new HashMap<>();
                    map.put("post_id", pk);
                    setCollectStyle(true,position);
                    HelloHttp.sendPostRequest("api/post/like", map, new okhttp3.Callback() {

                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e("Detail", "FAILURE");
                            Looper.prepare();
                            setCollectStyle(false,position);
                            Toast.makeText(getActivity(), "服务器未响应", Toast.LENGTH_SHORT).show();
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
                                setCollectStyle(false,position);
                                e.printStackTrace();
                            }
                            if (result != null) {
                                if(result.equals("Success")) {
                                    Looper.prepare();
                                    Snackbar.make(getView(),"收藏成功",Snackbar.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                                else if(result.equals("Failure")) {
                                    Looper.prepare();
                                    setCollectStyle(false,position);
                                    Toast.makeText(getActivity(),"记录已存在", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                                else if(result.equals("UnknownError")){
                                    Looper.prepare();
                                    setCollectStyle(false,position);
                                    Toast.makeText(getActivity(),"未知错误", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                                else {
                                    Looper.prepare();
                                    setCollectStyle(false, position);
                                    Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                            }
                        }
                    });
                }
                else {
                    //已收藏
                    int pk = list.get(position).getId();
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", pk);
                    setCollectStyle(false,position);
                    HelloHttp.sendDeleteRequest("api/post/like", map, new okhttp3.Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e("Detail", "FAILURE");
                            Looper.prepare();
                            setCollectStyle(true,position);
                            Toast.makeText(getActivity(), "服务器未响应", Toast.LENGTH_SHORT).show();
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
                                setCollectStyle(true,position);
                                e.printStackTrace();
                            }
                            if (result != null) {
                                if(result.equals("Success")) {
                                    Looper.prepare();
                                    Snackbar.make(getView(),"取消收藏成功",Snackbar.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                                else if(result.equals("Failure")) {
                                    Looper.prepare();
                                    setCollectStyle(true, position);
                                    Toast.makeText(getActivity(),"记录不存在", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                                else if(result.equals("UnknownError")){
                                    Looper.prepare();
                                    setCollectStyle(true,position);
                                    Toast.makeText(getActivity(),"未知错误", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                                else {
                                    Looper.prepare();
                                    setCollectStyle(true, position);
                                    Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                            }
                        }
                    });
                }
            }
            else if(view.getId() == R.id.ib_menu) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()); //定义一个AlertDialog
                String[] strarr = {"删除动态","取消"};
                builder.setItems(strarr, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface arg0, int arg1)
                    {
                    if (arg1 == 0) {
                        Map<String, Object> map = new HashMap<>();
                        Map<String, Object> urlmap = new HashMap<>();
                        urlmap.put("pk", list.get(position).getId());
                        HelloHttp.sendSpecialDeleteRequest("api/dynamic", urlmap, map, new okhttp3.Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.e("DetailActivity", "FAILURE");
                                Looper.prepare();
                                Toast.makeText(getActivity(), "服务器错误", Toast.LENGTH_SHORT).show();
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
                                        Snackbar.make(getView(),"删除成功",Snackbar.LENGTH_SHORT).show();
                                        initData();
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                initAdapter();
                                            }
                                        });
                                        Looper.loop();
                                    }
                                    else if(result.equals("Failure")) {
                                        Looper.prepare();
                                        Snackbar.make(getView(),"删除失败",Snackbar.LENGTH_SHORT).show();
                                        Looper.loop();
                                    }
                                    else {
                                        Looper.prepare();
                                        Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
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

    @Override
    public void onClickNinePhotoItem(BGANinePhotoLayout ninePhotoLayout, View view, int position, String model, List<String> models) {
        mCurrentClickNpl = ninePhotoLayout;
        photoPreviewWrapper();
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (requestCode == PRC_PHOTO_PREVIEW) {
            Toast.makeText(getContext(), "您拒绝了图片预览所需要的相关权限!", Toast.LENGTH_SHORT).show();
        }
    }

    @AfterPermissionGranted(PRC_PHOTO_PREVIEW)
    private void photoPreviewWrapper() {
        if (mCurrentClickNpl == null) {
            return;
        }
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            File downloadDir = new File(Environment.getExternalStorageDirectory(), "InsDownload");
            BGAPhotoPreviewActivity.IntentBuilder photoPreviewIntentBuilder = new BGAPhotoPreviewActivity.IntentBuilder(getActivity())
                    .saveImgDir(downloadDir); // 保存图片的目录，如果传 null，则没有保存图片功能
            if (mCurrentClickNpl.getItemCount() == 1) {
                // 预览单张图片
                photoPreviewIntentBuilder.previewPhoto(mCurrentClickNpl.getCurrentClickItem());
            } else if (mCurrentClickNpl.getItemCount() > 1) {
                // 预览多张图片
                photoPreviewIntentBuilder.previewPhotos(mCurrentClickNpl.getData())
                        .currentPosition(mCurrentClickNpl.getCurrentClickItemPosition()); // 当前预览图片的索引
            }
            startActivity(photoPreviewIntentBuilder.build());
        } else {
            EasyPermissions.requestPermissions(this, "图片预览需要以下权限:\n\n1.访问设备上的照片", PRC_PHOTO_PREVIEW, perms);
        }
    }

    class DynamicAdapter extends BaseQuickAdapter<Dynamic, BaseViewHolder> {
        private int myId;
        public DynamicAdapter(int layoutResId, @Nullable List<Dynamic> data, int myId) {
            super(layoutResId, data);
            this.myId = myId;
        }

        @Override
        protected void convert(BaseViewHolder helper, Dynamic item) {
            if (mContext == null) {
                return;
            }
            else {
            Glide.with(mContext).load("http://ktchen.cn"+item.getSrc()).into((CircleImageView) helper.getView(R.id.ci_head));}
            helper.setText(R.id.tv_username, item.getUsername());
            helper.setText(R.id.tv_like2, item.getLikes_num()+"次赞");
            if (TextUtils.isEmpty(item.getIntroduction())) {
                helper.setGone(R.id.tv_detail, false);
            }
            else {
                helper.setVisible(R.id.tv_detail, true);
                String s1 = item.getUsername();
                String s2 = s1 + ":\t" + item.getIntroduction();
                SpannableString ss = new SpannableString(s2);
                ss.setSpan(new ForegroundColorSpan(Color.parseColor("#2b5a83")), 0,s1.length()+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                helper.setText(R.id.tv_detail, ss);
            }
            helper.setText(R.id.tv_comment, "查看全部"+item.getCom_num()+"条评论");
            helper.setText(R.id.tv_time, item.getPub_time());
            helper.addOnClickListener(R.id.ib_like);
            helper.addOnClickListener(R.id.ib_comment);
            helper.addOnClickListener(R.id.ib_collect);
            helper.addOnClickListener(R.id.tv_comment);
            helper.addOnClickListener(R.id.ci_head);
            helper.addOnClickListener(R.id.tv_username);
            helper.addOnClickListener(R.id.ib_menu);
            if (item.getUserId() == myId) {
                helper.setGone(R.id.ib_menu, true);
            } else {
                helper.setVisible(R.id.ib_menu, false);
            }
            if(item.isIs_like()) {
                helper.setImageResource(R.id.ib_like, R.drawable.like2);
            }
            else {
                helper.setImageResource(R.id.ib_like, R.drawable.like1);
            }
            if(item.isIs_collect()) {
                helper.setImageResource(R.id.ib_collect, R.drawable.collect2);
            }
            else {
                helper.setImageResource(R.id.ib_collect, R.drawable.collect1);
            }
            BGANinePhotoLayout ninePhotoLayout = helper.getView(R.id.npl_item_moment_photos);
            ninePhotoLayout.setDelegate(HomeFragment.this);
            ninePhotoLayout.setData(item.getThumbnails());
        }
    }
    private void setLikeStyle(final boolean flag, final int position) {
        getActivity().runOnUiThread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void run() {
                list.get(position).setIs_like(flag);
                final ImageButton ib_like = (ImageButton) adapter.getViewByPosition(recyclerView, position, R.id.ib_like);
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
    private void setCollectStyle(final boolean flag, final int position) {
        getActivity().runOnUiThread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void run() {
                list.get(position).setIs_collect(flag);
                final ImageButton ib_collect = (ImageButton) adapter.getViewByPosition(recyclerView, position, R.id.ib_collect);
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
                    animatorSet.start();

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

                        }
                    });
                    animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim);
                    animatorSet.start();
                }
            }
        });
    }

    private void addLikeNum(final int position) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextSwitcher ts = (TextSwitcher) adapter.getViewByPosition(recyclerView, position, R.id.tv_like);
                ts.setText(Integer.toString(list.get(position).getLikes_num())+"次赞");
            }
        });
    }
}
