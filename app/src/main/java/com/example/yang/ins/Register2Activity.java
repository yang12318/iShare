package com.example.yang.ins;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.yang.ins.Utils.HelloHttp;
import com.example.yang.ins.Utils.MD5Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

public class Register2Activity extends AppCompatActivity implements TextWatcher {

    private EditText et_password,et_username;
    private Button btn_finish;
    private RelativeLayout relativeLayout;
    private AnimationDrawable anim;
    private String email, nickname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        nickname = intent.getStringExtra("nickname");
        btn_finish = (Button) findViewById(R.id.btn_finish);
        et_password = (EditText) findViewById(R.id.et_passInput);
        et_username = (EditText) findViewById(R.id.et_userInput);
        et_username.addTextChangedListener(this);
        et_password.addTextChangedListener(this);
        et_username.addTextChangedListener(new Register2Activity.JumpTextWatcher(et_username, et_password));
        RelativeLayout container = (RelativeLayout) findViewById(R.id.register2_container);
        container.setBackgroundResource(R.drawable.animation_list);
        anim = (AnimationDrawable) container.getBackground();
        anim.setEnterFadeDuration(6000);
        anim.setExitFadeDuration(2000);
        anim.start();
        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = et_password.getText().toString();
                final String username = et_username.getText().toString();
                if (username.length() <= 0 || username == null) {
                    showToast("未填写全名");
                    return;
                }
                if (password.length() <= 0 || password == null) {
                    showToast("未填写密码");
                    return;
                }
                if (username.length() < 2 || username.length() > 15) {
                    showToast("全名格式不合法，长度应为2-15");
                    return;
                }
                if (password.length() < 6) {
                    showToast("密码长度过短，请换用更复杂的密码");
                    return;
                }
                if (password.length() > 18) {
                    showToast("密码长度过长");
                    return;
                }
                final Map<String, Object> map = new HashMap<>();
                map.put("account", username);
                password = MD5Util.encode(password);
                final String finalPassword = password;
                HelloHttp.sendFirstPostRequest("api/user/checkout", map, new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("Register2Activity", "FAILURE");
                        Looper.prepare();
                        showToast("服务器错误！");
                        Looper.loop();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseData = response.body().string();
                        Log.d("Register2Activity", responseData);
                        String result = null;
                        try {
                            result = new JSONObject(responseData).getString("status");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (result.equals("Exist")) {
                            Looper.prepare();
                            showToast("重名！");
                            Looper.loop();
                            return;
                        } else if (result.equals("NotExist")) {
                            Map<String, Object> map2 = new HashMap<>();
                            map2.put("username", username);
                            map2.put("email", email);
                            map2.put("nickname", nickname);
                            map2.put("password", finalPassword);
                            HelloHttp.sendFirstLongPostRequest("api/user/register", map2, new okhttp3.Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Log.e("Register2Activity", "FAILURE");
                                    Looper.prepare();
                                    showToast("服务器错误！");
                                    Looper.loop();
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    String responseData = response.body().string();
                                    Log.d("Register2Activity", responseData);
                                    String result = null;
                                    try {
                                        result = new JSONObject(responseData).getString("status");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    if (result.equals("Success")) {
                                        Looper.prepare();
                                        Toast.makeText(Register2Activity.this, "注册成功，请前往邮箱激活账号", Toast.LENGTH_SHORT).show();
                                        Intent intent1 = new Intent(Register2Activity.this, LoginActivity.class);
                                        startActivity(intent1);
                                        Looper.loop();
                                    } else {
                                        Looper.prepare();
                                        showToast(result);
                                        Looper.loop();
                                    }
                                }
                            });
                        } else {
                            showToast(result);
                        }
                    }
                });
            }
        });
    }
    private class JumpTextWatcher implements TextWatcher {
        private EditText mThisView = null;
        private View mNextView = null;

        public JumpTextWatcher(EditText vThis, View vNext) {
            super();
            mThisView = vThis;
            if(vNext != null) {
                mNextView = vNext;
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String str = s.toString();
            if(str.indexOf("\r") >= 0 || str.indexOf("\n") >= 0) {      //发现输入回车或换行
                mThisView.setText(str.replace("\r", "").replace("\n", ""));
                if(mNextView != null) {
                    mNextView.requestFocus();
                    if(mNextView instanceof EditText) {         //让光标自动移动到编辑框的文本末尾
                        EditText et = (EditText) mNextView;
                        et.setSelection(et.getText().length());
                    }
                }
            }
        }
    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        setButtonStyle(false);//在这里重复设置，以保证清除任意EditText中的内容，按钮重新变回不可点击状态
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!(et_username.getText().toString().equals("") || et_password.getText().toString().equals(""))){
            setButtonStyle(true);
        }
    }
    private void setButtonStyle(final boolean flag1) {
        runOnUiThread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void run() {
                if(flag1) {
                    btn_finish.setTextColor(Color.BLACK);
                    btn_finish.setBackground(getResources().getDrawable(R.drawable.buttonshape1));
                }
                else {
                    btn_finish.setTextColor(Color.parseColor("#50000000"));
                    btn_finish.setBackground(getResources().getDrawable(R.drawable.buttonshape));
                }
            }
        });
    }
    private void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}
