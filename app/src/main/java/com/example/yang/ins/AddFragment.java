package com.example.yang.ins;

import android.Manifest;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yang.ins.Utils.HelloHttp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerPreviewActivity;
import cn.bingoogolapple.photopicker.widget.BGANinePhotoLayout;
import cn.bingoogolapple.photopicker.widget.BGASortableNinePhotoLayout;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class AddFragment extends Fragment implements EasyPermissions.PermissionCallbacks, BGANinePhotoLayout.Delegate, View.OnClickListener, BGASortableNinePhotoLayout.Delegate {

    private static final int PRC_PHOTO_PICKER = 1;

    private static final int RC_CHOOSE_PHOTO = 1;
    private static final int RC_PHOTO_PREVIEW = 2;

    private static final String EXTRA_MOMENT = "EXTRA_MOMENT";
    private BGASortableNinePhotoLayout mPhotosSnpl;
    private EditText et_add;
    private TextView tv_publish;

    public static AddFragment newInstance(String param1) {
        AddFragment fragment = new AddFragment();
        Bundle args = new Bundle();
        args.putString("agrs1", param1);
        fragment.setArguments(args);
        return fragment;
    }

    public AddFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);
        et_add = (EditText) view.findViewById(R.id.et_add);
        tv_publish = (TextView) view.findViewById(R.id.tv_publish);
        tv_publish.setOnClickListener(this);
        mPhotosSnpl = view.findViewById(R.id.snpl_moment_add_photos);
        mPhotosSnpl.setDelegate(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.tv_publish) {
            final String content = et_add.getText().toString().trim();
            if(mPhotosSnpl.getItemCount() == 0) {
                Snackbar.make(getView(),"必须选择照片",Snackbar.LENGTH_SHORT).show();
                return;
            }
            tv_publish.setEnabled(false);
            tv_publish.setTextColor(Color.GRAY);
            //调用接口
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //try {
                        SharedPreferences mShared;
                        mShared = MainApplication.getContext().getSharedPreferences("share", MODE_PRIVATE);
                        String Authorization = null;
                        Map<String, Object> mapParam = (Map<String, Object>) mShared.getAll();
                        for (Map.Entry<String, Object> item_map : mapParam.entrySet()) {
                            String key = item_map.getKey();
                            Object value = item_map.getValue();
                            if(key.equals("Authorization")) {
                                Authorization = value.toString();
                            }
                        }
                        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
                        multipartBodyBuilder.setType(MultipartBody.FORM);
                        multipartBodyBuilder.addFormDataPart("photo_num", Integer.toString(mPhotosSnpl.getItemCount()));
                        multipartBodyBuilder.addFormDataPart("introduction", content);
                        List<String> list = mPhotosSnpl.getData();
                        for(int i = 0 ; i < list.size() ; i++) {
                            String filename = list.get(i);
                            File file = new File(filename);
                            multipartBodyBuilder.addFormDataPart("photo_"+Integer.toString(i), filename, RequestBody.create(MediaType.parse("image/jpg"), file));
                        }
                        RequestBody requestBody = multipartBodyBuilder.build();
                        String url = HelloHttp.dealAddress("api/dynamic");
                        Request request = new Request.Builder()
                                .url(url)
                                .header("Authorization", Authorization)
                                .post(requestBody)
                                .build();
                        Response response;
                        OkHttpClient okHttpClient = new OkHttpClient();
                        OkHttpClient clientWith300sTimeout = okHttpClient.newBuilder().readTimeout(300, TimeUnit.SECONDS).build();

                        clientWith300sTimeout.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.e("AddFragment", "上传图片失败");
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getContext(), "服务器错误", Toast.LENGTH_SHORT).show();
                                        tv_publish.setEnabled(true);
                                        tv_publish.setTextColor(Color.parseColor("#1296db"));
                                    }
                                });
                            }
                            @Override
                            public void onResponse(Call call, final Response response) throws IOException {
                                String responseData = response.body().string();
                                Log.d("AddFragment", responseData);
                                String result = null;
                                try {
                                    result = new JSONObject(responseData).getString("status");
                                    Looper.prepare();
                                    if(result.equals("Success")) {
                                        Toast.makeText(getActivity(), "发表成功", Toast.LENGTH_SHORT).show();
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                et_add.setText("");
                                            }
                                        });
                                        Intent intent = new Intent(getActivity(), MainActivity.class);
                                        startActivity(intent);
                                    }
                                    else if(result.equals("UnknownError")) {
                                        Toast.makeText(getActivity(), "发布失败", Toast.LENGTH_SHORT).show();
                                        tv_publish.setEnabled(true);
                                        tv_publish.setTextColor(Color.parseColor("#1296db"));
                                    }
                                    Looper.loop();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    tv_publish.setEnabled(true);
                                    tv_publish.setTextColor(Color.parseColor("#1296db"));
                                }
                            }
                        });
                }
            }).start();
        }
    }

    @Override
    public void onClickNinePhotoItem(BGANinePhotoLayout ninePhotoLayout, View view, int position, String model, List<String> models) {

    }

    @Override
    public void onClickAddNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, ArrayList<String> models) {
        choicePhotoWrapper();
    }

    @Override
    public void onClickDeleteNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, String model, ArrayList<String> models) {
        mPhotosSnpl.removeItem(position);
    }

    @Override
    public void onClickNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, String model, ArrayList<String> models) {
        Intent photoPickerPreviewIntent = new BGAPhotoPickerPreviewActivity.IntentBuilder(getActivity())
                .previewPhotos(models) // 当前预览的图片路径集合
                .selectedPhotos(models) // 当前已选中的图片路径集合
                .maxChooseCount(mPhotosSnpl.getMaxItemCount()) // 图片选择张数的最大值
                .currentPosition(position) // 当前预览图片的索引
                .isFromTakePhoto(false) // 是否是拍完照后跳转过来
                .build();
        startActivityForResult(photoPickerPreviewIntent, RC_PHOTO_PREVIEW);
    }

    @Override
    public void onNinePhotoItemExchanged(BGASortableNinePhotoLayout sortableNinePhotoLayout, int fromPosition, int toPosition, ArrayList<String> models) {
        Toast.makeText(getActivity(), "照片排序已改变", Toast.LENGTH_SHORT).show();
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
        if (requestCode == PRC_PHOTO_PICKER) {
            Toast.makeText(getActivity(), "您拒绝了图片选择所需要的相关权限!", Toast.LENGTH_SHORT).show();
        }
    }

    @AfterPermissionGranted(PRC_PHOTO_PICKER)
    private void choicePhotoWrapper() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(getActivity(), perms)) {
            // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话就没有拍照功能
            File takePhotoDir = new File(Environment.getExternalStorageDirectory(), "InsTakePhoto");

            Intent photoPickerIntent = new BGAPhotoPickerActivity.IntentBuilder(getActivity())
                    .cameraFileDir(takePhotoDir) // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话则不开启图库里的拍照功能
                    .maxChooseCount(mPhotosSnpl.getMaxItemCount() - mPhotosSnpl.getItemCount()) // 图片选择张数的最大值
                    .selectedPhotos(null) // 当前已选中的图片路径集合
                    .pauseOnScroll(false) // 滚动列表时是否暂停加载图片
                    .build();
            startActivityForResult(photoPickerIntent, RC_CHOOSE_PHOTO);
        } else {
            EasyPermissions.requestPermissions(this, "图片选择需要以下权限:\n\n1.访问设备上的照片\n\n2.拍照", PRC_PHOTO_PICKER, perms);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == RC_CHOOSE_PHOTO) {
            mPhotosSnpl.addMoreData(BGAPhotoPickerActivity.getSelectedPhotos(data));
        } else if (requestCode == RC_PHOTO_PREVIEW) {
            mPhotosSnpl.setData(BGAPhotoPickerPreviewActivity.getSelectedPhotos(data));
        }
    }

}
