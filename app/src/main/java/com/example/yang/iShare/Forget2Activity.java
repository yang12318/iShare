package com.example.yang.iShare;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.yang.iShare.Utils.HelloHttp;
import com.example.yang.iShare.Utils.MD5Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

public class Forget2Activity extends AppCompatActivity {

    private String email = null;
    private EditText et_pass, et_repass, et_code;
    private Button btn_code;
    private ImageButton ib_back, ib_finish;
    private String hashkey = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget2);
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        ib_back = (ImageButton) findViewById(R.id.ib_forget2_back);
        ib_finish = (ImageButton) findViewById(R.id.ib_forget2_go);
        btn_code = (CountDownTimerButton) findViewById(R.id.btn_code);
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        et_code = (EditText) findViewById(R.id.et_code);
        et_pass = (EditText) findViewById(R.id.et_newcode);
        et_repass = (EditText) findViewById(R.id.et_confirm);
        Drawable db_code=getResources().getDrawable(R.drawable.password);
        db_code.setBounds(0,0,85,85);
        et_code.setCompoundDrawables(db_code,null,null,null);
        Drawable db_pass=getResources().getDrawable(R.drawable.password);
        db_pass.setBounds(0,0,85,85);
        et_pass.setCompoundDrawables(db_pass,null,null,null);
        Drawable db_repass=getResources().getDrawable(R.drawable.password);
        db_repass.setBounds(0,0,85,85);
        et_repass.setCompoundDrawables(db_repass,null,null,null);
        btn_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> map = new HashMap<>();
                map.put("email", email);
                HelloHttp.sendFirstLongGetRequest("api/user/password", map, new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("Forget2Activity", "FAILURE");
                        Looper.prepare();
                        Toast.makeText(Forget2Activity.this, "服务器错误", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseData = response.body().string();
                        Log.d("Forget2Activity", responseData);
                        String result = null;
                        try {
                            result = new JSONObject(responseData).getString("status");
                            if(result.equals("Success")) {
                                hashkey = new JSONObject(responseData).getString("hashkey");
                            }
                            else if(result.equals("NotExist")) {
                                Looper.prepare();
                                Toast.makeText(Forget2Activity.this, "邮箱不存在", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                            else if(result.equals("UnknownError")) {
                                Looper.prepare();
                                Toast.makeText(Forget2Activity.this, "未知错误", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                            else {
                                Looper.prepare();
                                Toast.makeText(Forget2Activity.this, result, Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                Toast.makeText(Forget2Activity.this, "已向您的邮箱发送验证码，请前往查收", Toast.LENGTH_SHORT).show();

            }
        });
        ib_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String captcha = et_code.getText().toString();
                String pass = et_pass.getText().toString();
                String repass = et_repass.getText().toString();
                if(hashkey == null || hashkey.length() <= 0) {
                    Toast.makeText(Forget2Activity.this, "您还未向邮箱发送验证码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(captcha.length() <= 0 || captcha == null) {
                    Toast.makeText(Forget2Activity.this, "您还未填写验证码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(pass.length() <= 0 || pass == null) {
                    Toast.makeText(Forget2Activity.this, "您还未填写新密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(repass.length() <= 0 || repass == null) {
                    Toast.makeText(Forget2Activity.this, "您还未填写确认密码框", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(pass.length() <6 || repass.length() < 6) {
                    Toast.makeText(Forget2Activity.this, "新密码过于简单，清换用安全程度更高的密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(pass.length() > 18 || repass.length() > 18) {
                    Toast.makeText(Forget2Activity.this, "密码长度过长，限制为18位以下", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!(pass.equals(repass))) {
                    Toast.makeText(Forget2Activity.this, "新旧密码不同", Toast.LENGTH_SHORT).show();
                    return;
                }
                pass = MD5Util.encode(pass);
                repass = MD5Util.encode(repass);
                Map<String, Object> map = new HashMap<>();
                map.put("captcha", captcha);
                map.put("hashkey", hashkey);
                map.put("password", pass);
                map.put("password2", repass);
                HelloHttp.sendFirstPostRequest("api/user/password", map, new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("Forget2Activity", "FAILURE");
                        Looper.prepare();
                        Toast.makeText(Forget2Activity.this, "服务器错误", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseData = response.body().string();
                        Log.d("Forget2Activity", responseData);
                        String result = null;
                        try {
                            result = new JSONObject(responseData).getString("status");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(result.equals("Failure")) {
                            Looper.prepare();
                            Toast.makeText(Forget2Activity.this, "验证码错误",  Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                        else if(result.equals("UnknownError")) {
                            Looper.prepare();
                            Toast.makeText(Forget2Activity.this, "未知错误",  Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                        else if(result.equals("Success")) {
                            Looper.prepare();
                            Toast.makeText(Forget2Activity.this, "密码修改成功，请前往主页重新登录", Toast.LENGTH_SHORT).show();
                            Intent intent1 = new Intent(Forget2Activity.this, LoginActivity.class);
                            startActivity(intent1);
                            Looper.loop();
                        }
                        else {
                            Looper.prepare();
                            Toast.makeText(Forget2Activity.this, result, Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                });
            }
        });
    }
}
