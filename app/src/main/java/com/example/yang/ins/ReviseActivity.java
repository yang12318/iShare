package com.example.yang.ins;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.DatePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.yang.ins.Utils.HelloHttp;
import com.lljjcoder.style.citypickerview.CityPickerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bingoogolapple.baseadapter.BGABaseAdapterUtil;
import cn.bingoogolapple.photopicker.imageloader.BGAImage;
import cn.bingoogolapple.photopicker.util.BGAPhotoHelper;
import cn.bingoogolapple.photopicker.util.BGAPhotoPickerUtil;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import com.lljjcoder.Interface.OnCityItemClickListener;
import com.lljjcoder.bean.CityBean;
import com.lljjcoder.bean.DistrictBean;
import com.lljjcoder.bean.ProvinceBean;
import com.lljjcoder.citywheel.CityConfig;
import com.lljjcoder.style.citylist.CityListSelectActivity;
import com.lljjcoder.style.citylist.bean.CityInfoBean;
import com.lljjcoder.style.citylist.utils.CityListLoader;
import com.lljjcoder.style.citypickerview.CityPickerView;

import com.bumptech.glide.Glide;
/*
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;



import static android.os.Build.TYPE;*/

public class ReviseActivity extends AppCompatActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks{

    private CircleImageView iv_head;
    private Button  btn_head;
    private EditText et_nickname,et_username,et_bio;
    private TextView tv_birth, tv_location, tv_gender;
    private ImageButton ib_back, ib_finish;
    private BGAPhotoHelper bgaPhotoHelper;
    private String username, nickname, src, birthday, address, introduction = null;
    private int gender = 3, follow_num = 0, concern_num = 0, posts = 0;
    private int UserId = 0;
    private String filename = null;
    CityPickerView mPicker = new CityPickerView();
    private static final int REQUEST_CODE_PERMISSION_CHOOSE_PHOTO = 1;
    private static final int REQUEST_CODE_PERMISSION_TAKE_PHOTO = 2;
    private boolean flag = false;
    private static final int REQUEST_CODE_CHOOSE_PHOTO = 1;
    private static final int REQUEST_CODE_TAKE_PHOTO = 2;
    private static final int REQUEST_CODE_CROP = 3;

    /*private String imagepath = null;

    public static final String TAG = "ReviseActivity";
    CityPickerView mPicker = new CityPickerView();*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revise);
        File takePhotoDir = new File(Environment.getExternalStorageDirectory(), "InsTakePhoto");
        bgaPhotoHelper = new BGAPhotoHelper(takePhotoDir);
        mPicker.init(this);
        btn_head = (Button) findViewById(R.id.revise_image);
        iv_head = (CircleImageView) findViewById(R.id.head_revise);
        et_nickname = (EditText) findViewById(R.id.revise_nickname);
        et_username = (EditText) findViewById(R.id.revise_username);
        et_bio = (EditText) findViewById(R.id.revise_bio);
        tv_birth = (TextView) findViewById(R.id.revise_birth);
        tv_gender = (TextView) findViewById(R.id.revise_gender);
        tv_location = (TextView) findViewById(R.id.revise_location);
        ib_back = (ImageButton) findViewById(R.id.ib_revise_back);
        ib_finish = (ImageButton) findViewById(R.id.ib_revise_finish);
        btn_head.setOnClickListener(this);
        iv_head.setOnClickListener(this);
        tv_location.setOnClickListener(this);
        tv_gender.setOnClickListener(this);
        tv_birth.setOnClickListener(this);
        ib_finish.setOnClickListener(this);
        ib_back.setOnClickListener(this);
        et_nickname.setOnClickListener(this);
        et_username.setOnClickListener(this);
        et_bio.setOnClickListener(this);
        Drawable db_nickname=getResources().getDrawable(R.drawable.nickname);
        db_nickname.setBounds(0,0,75,75);
        et_nickname.setCompoundDrawables(db_nickname,null,null,null);
        Drawable db_username=getResources().getDrawable(R.drawable.username);
        db_username.setBounds(0,0,75,75);
        et_username.setCompoundDrawables(db_username,null,null,null);
        Drawable db_bio=getResources().getDrawable(R.drawable.bio);
        db_bio.setBounds(0,0,75,75);
        et_bio.setCompoundDrawables(db_bio,null,null,null);
        Drawable db_birth=getResources().getDrawable(R.drawable.birth);
        db_birth.setBounds(0,0,75,75);
        tv_birth.setCompoundDrawables(db_birth,null,null,null);
        Drawable db_gender=getResources().getDrawable(R.drawable.gender);
        db_gender.setBounds(0,0,75,75);
        tv_gender.setCompoundDrawables(db_gender,null,null,null);
        Drawable db_location=getResources().getDrawable(R.drawable.location);
        db_location.setBounds(0,0,75,75);
        tv_location.setCompoundDrawables(db_location,null,null,null);
        SharedPreferences mShared;
        mShared = MainApplication.getContext().getSharedPreferences("share", MODE_PRIVATE);
        Map<String, Object> mapParam = (Map<String, Object>) mShared.getAll();
        for (Map.Entry<String, Object> item_map : mapParam.entrySet()) {
            String key = item_map.getKey();
            Object value = item_map.getValue();
            if(key.equals("id")) {
                UserId = Integer.valueOf(value.toString());
            }
        }
        if(UserId == 0) {
            Toast.makeText(ReviseActivity.this, "全局内存中保存的信息为空", Toast.LENGTH_SHORT).show();
        }
        else {
            Map<String, Object> map = new HashMap<>();
            HelloHttp.sendGetRequest("api/user/detail/"+Integer.toString(UserId), map, new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("UserActivity", "FAILURE");
                    Looper.prepare();
                    Toast.makeText(ReviseActivity.this, "服务器未响应", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseData = response.body().string();
                    Log.d("ReviseActivity", responseData);
                    try {
                        JSONObject jsonObject1 = new JSONObject(responseData);
                        posts = jsonObject1.getInt("post_num");
                        JSONObject jsonObject = jsonObject1.getJSONObject("result");
                        username = jsonObject.getString("username");
                        nickname = jsonObject.getString("nickname");
                        gender = jsonObject.getInt("gender");
                        birthday = jsonObject.getString("birthday");
                        follow_num = jsonObject.getInt("following_num");
                        concern_num = jsonObject.getInt("followed_num");
                        src = jsonObject.getString("profile_picture");
                        src = "http://ktchen.cn" + src;
                        Log.d("MeFragment", src);
                        address = jsonObject.getString("address");
                        introduction = jsonObject.getString("introduction");
                        mHandler.sendEmptyMessageDelayed(1, 0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        String result = null;
                        try {
                            result = new JSONObject(responseData).getString("status");
                            Looper.prepare();
                            Toast.makeText(ReviseActivity.this, result, Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState);
        BGAPhotoHelper.onSaveInstanceState(bgaPhotoHelper, outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        BGAPhotoHelper.onRestoreInstanceState(bgaPhotoHelper, savedInstanceState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.revise_image || view.getId() == R.id.head_revise) {
            registerForContextMenu(view);
            openContextMenu(view);
            unregisterForContextMenu(view);
        }
        else if(view.getId() == R.id.revise_birth) {
            Calendar c = Calendar.getInstance();
            String birthday = null;
            new BirthActivity(ReviseActivity.this, 0, new BirthActivity.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker DatePicker, int Year, int MonthOfYear,
                                      int DayOfMonth) {
                    String birthday = String.format("%d-%d-%d", Year, MonthOfYear + 1,DayOfMonth);
                    tv_birth.setText(birthday);
                    //调用接口
                }
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), false).show();

        }
        else if(view.getId() == R.id.revise_gender) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this); //定义一个AlertDialog
            String[] strarr = {"男","女","保密"};
            builder.setItems(strarr, new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface arg0, int arg1)
                {
                    String sex = "保密";
                    if (arg1 == 0) {
                        sex = "男";
                    }else if(arg1 == 1){
                        sex = "女";
                    }
                    else {
                        sex = "保密";
                    }
                    tv_gender.setText(sex);
                    //调用接口
                    String gender = tv_gender.getText().toString();
                    if(gender.equals("男"))
                        gender = "M";
                    else if(gender.equals("女"))
                        gender = "F";
                    else if(gender.equals("保密"))
                        gender = "S";
                }
            });
            builder.show();
        }
        else if(view.getId() == R.id.revise_location) {
            CityConfig cityConfig = new CityConfig.Builder()
                    .title("选择城市")//标题
                    .titleTextSize(18)//标题文字大小
                    .titleTextColor("#585858")//标题文字颜  色
                    .titleBackgroundColor("#E9E9E9")//标题栏背景色
                    .confirTextColor("#585858")//确认按钮文字颜色
                    .confirmText("确定")//确认按钮文字
                    .confirmTextSize(16)//确认按钮文字大小
                    .cancelTextColor("#585858")//取消按钮文字颜色
                    .cancelText("取消")//取消按钮文字
                    .cancelTextSize(16)//取消按钮文字大小
                    .setCityWheelType(CityConfig.WheelType.PRO_CITY)//显示类，只显示省份一级，显示省市两级还是显示省市区三级
                    .showBackground(true)//是否显示半透明背景
                    .visibleItemsCount(7)//显示item的数量
                    .province("山东省")//默认显示的省份
                    .city("青岛市")//默认显示省份下面的城市
                    //.district("崂山区")//默认显示省市下面的区县数据
                    .provinceCyclic(false)//省份滚轮是否可以循环滚动
                    .cityCyclic(false)//城市滚轮是否可以循环滚动
                    .districtCyclic(false)//区县滚轮是否循环滚动
                    //.setCustomItemLayout(R.layout.item_city)//自定义item的布局
                    //.setCustomItemTextViewId(R.id.item_city_name_tv)//自定义item布局里面的textViewid
                    .drawShadows(false)//滚轮不显示模糊效果
                    .setLineColor("#03a9f4")//中间横线的颜色
                    .setLineHeigh(5)//中间横线的高度
                    .setShowGAT(true)//是否显示港澳台数据，默认不显示
                    .build();
            mPicker.setConfig(cityConfig);
            mPicker.setOnCityItemClickListener(new OnCityItemClickListener() {
                @Override
                public void onSelected(ProvinceBean province, CityBean city, DistrictBean district) {
                    String Province = null;
                    String City = null;
                    if (province != null) {
                        Province = province.getName();
                    }
                    if (city != null) {
                        City = city.getName();
                    }
                    if (district != null) {
                    }
                    String location = Province + "-" + City;
                    if (Province.equals(City))
                        location = Province;
                    tv_location.setText(location);
                    //调用接口
                }
            });
            mPicker.showCityPicker( );
        }

        else if(view.getId() == R.id.ib_revise_back) {
            finish();
        }
        else if(view.getId() == R.id.ib_revise_finish) {
            //调用接口
            final String nickname = et_nickname.getText().toString().trim();
            if(nickname.length() <= 0 || nickname == null) {
                showToast("您未填写有效昵称");
                return;
            }
            if(nickname.length() < 3 || nickname.length() > 15) {
                showToast("昵称长度应为3-15");
                return;
            }
            final String introduction = et_bio.getText().toString().trim();
            if (introduction.length() > 100) {
                showToast("您的个人简介过长，长度限制为0-100");
                return;
            }
            Pattern p = Pattern.compile("(\r?\n(\\s*\r?\n)+)");
            Matcher m = p.matcher(introduction);
            final String introduction2 = m.replaceAll("\r\n");
            final String birthday = tv_birth.getText().toString();
            final String location = tv_location.getText().toString();
            final String gender = tv_gender.getText().toString();
            int sex = 3;
            if(gender.equals("男")) {
                sex = 1;
            }
            else if(gender.equals("女")) {
                sex = 2;
            }
            else {
                sex = 3;
            }
            final int finalSex = sex;
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
                    multipartBodyBuilder.addFormDataPart("nickname", nickname);
                    multipartBodyBuilder.addFormDataPart("address", location);
                    multipartBodyBuilder.addFormDataPart("introduction", introduction2);
                    multipartBodyBuilder.addFormDataPart("gender", Integer.toString(finalSex));
                    multipartBodyBuilder.addFormDataPart("birthday", birthday);
                    if(flag) {
                        File file = new File(filename);
                        multipartBodyBuilder.addFormDataPart("profile_picture", filename, RequestBody.create(MediaType.parse("image/jpg"), file));
                    }
                    RequestBody requestBody = multipartBodyBuilder.build();
                    String url = HelloHttp.dealAddress("api/user/detail");
                    Request request = new Request.Builder()
                            .url(url)
                            .header("Authorization", Authorization)
                            .put(requestBody)
                            .build();
                    Response response;
                    OkHttpClient okHttpClient = new OkHttpClient();
                    OkHttpClient clientWith300sTimeout = okHttpClient.newBuilder().readTimeout(300, TimeUnit.SECONDS).build();
                    clientWith300sTimeout.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e("ReviseActivity", "FAILURE");
                            Looper.prepare();
                            Toast.makeText(ReviseActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
                            Looper.loop();
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
                                    Toast.makeText(ReviseActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                                else if(result.equals("UnknownError")) {
                                    Toast.makeText(ReviseActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
                                }
                                Looper.loop();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }).start();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.photo_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_album) {
            choosePhoto();
        } else if (id == R.id.menu_take_photo) {
            takePhoto();
        }
        return true;
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    @AfterPermissionGranted(REQUEST_CODE_PERMISSION_CHOOSE_PHOTO)
    public void choosePhoto() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            startActivityForResult(bgaPhotoHelper.getChooseSystemGalleryIntent(), REQUEST_CODE_CHOOSE_PHOTO);
        } else {
            EasyPermissions.requestPermissions(this, "请开启存储空间权限，以正常使用Instagram", REQUEST_CODE_PERMISSION_CHOOSE_PHOTO, perms);
        }
    }

    @AfterPermissionGranted(REQUEST_CODE_PERMISSION_TAKE_PHOTO)
    public void takePhoto() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {
            try {
                startActivityForResult(bgaPhotoHelper.getTakePhotoIntent(), REQUEST_CODE_TAKE_PHOTO);
            } catch (Exception e) {
                BGAPhotoPickerUtil.show(R.string.bga_pp_not_support_take_photo);
            }
        } else {
            EasyPermissions.requestPermissions(this, "请开启存储空间和相机权限，以正常使用Instagram", REQUEST_CODE_PERMISSION_TAKE_PHOTO, perms);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_CHOOSE_PHOTO) {
                try {
                    startActivityForResult(bgaPhotoHelper.getCropIntent(bgaPhotoHelper.getFilePathFromUri(data.getData()), 200, 200), REQUEST_CODE_CROP);
                } catch (Exception e) {
                    bgaPhotoHelper.deleteCropFile();
                    BGAPhotoPickerUtil.show(R.string.bga_pp_not_support_crop);
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_CODE_TAKE_PHOTO) {
                try {
                    startActivityForResult(bgaPhotoHelper.getCropIntent(bgaPhotoHelper.getCameraFilePath(), 200, 200), REQUEST_CODE_CROP);
                } catch (Exception e) {
                    bgaPhotoHelper.deleteCameraFile();
                    bgaPhotoHelper.deleteCropFile();
                    BGAPhotoPickerUtil.show(R.string.bga_pp_not_support_crop);
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_CODE_CROP) {
                BGAImage.display(iv_head, R.mipmap.bga_pp_ic_holder_light, bgaPhotoHelper.getCropFilePath(), BGABaseAdapterUtil.dp2px(200));
                flag = true;
                filename = bgaPhotoHelper.getCropFilePath();
            }
        } else {
            if (requestCode == REQUEST_CODE_CROP) {
                bgaPhotoHelper.deleteCameraFile();
                bgaPhotoHelper.deleteCropFile();
            }
        }
    }

    private void showToast(String s) {
        Toast.makeText(ReviseActivity.this, s, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @RequiresApi(api = Build.VERSION_CODES.M)
        @SuppressLint("CheckResult")
        @Override
        public void handleMessage(Message msg)
        {
            if(msg.what == 1)
            {
                et_nickname.setText(nickname);
                et_username.setText(username);
                if(introduction == null || introduction.equals("-") || introduction.equals("")) {
                    introduction = "这个人很懒，还没有填写个人简介";
                }
                et_bio.setText(introduction);
                tv_birth.setText(birthday);
                tv_location.setText(address);
                if(gender == 1) {
                    tv_gender.setText("男");
                }
                else if(gender == 2) {
                    tv_gender.setText("女");
                }
                else{
                    tv_gender.setText("保密");
                }
                filename = src;
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.empty_like);
                requestOptions.error(R.drawable.empty_like);
                Glide.with(ReviseActivity.this)
                        .setDefaultRequestOptions(requestOptions)
                        .load(src).into(iv_head);
                ib_finish.setVisibility(View.VISIBLE);
            }
        }
    };
}
