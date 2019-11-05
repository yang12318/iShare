package com.example.yang.iShare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.View.OnFocusChangeListener;

import com.example.yang.iShare.Utils.HelloHttp;
import com.example.yang.iShare.Utils.MD5Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

public class ChangeCodeActivity extends AppCompatActivity {

    private EditText et_old, et_new,et_confirm;
    private ImageButton ib_back,ib_finish;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changecode);
        et_old = (EditText) findViewById(R.id.et_old);
        et_new = (EditText) findViewById(R.id.et_new);
        et_confirm= (EditText) findViewById(R.id.et_codeconfirm);
        et_old.addTextChangedListener(new JumpTextWatcher(et_old, et_new));
        et_new.addTextChangedListener(new JumpTextWatcher(et_new, et_confirm));
        Drawable db_old=getResources().getDrawable(R.drawable.password);
        db_old.setBounds(0,0,80,80);
        et_old.setCompoundDrawables(db_old,null,null,null);
        Drawable db_new=getResources().getDrawable(R.drawable.password);
        db_new.setBounds(0,0,80,80);
        et_new.setCompoundDrawables(db_new,null,null,null);
        Drawable db_confirm=getResources().getDrawable(R.drawable.password);
        db_confirm.setBounds(0,0,80,80);
        et_confirm.setCompoundDrawables(db_confirm,null,null,null);
        ib_back = (ImageButton)  findViewById(R.id.ib_change_back);
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ib_finish = (ImageButton) findViewById(R.id.ib_change_go);
        ib_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPass = null, newPass = null,conPass = null;
                oldPass = et_old.getText().toString();
                newPass = et_new.getText().toString();
                conPass = et_confirm.getText().toString();
                if(oldPass == null || oldPass.length() <= 0) {
                    showToast("原密码未填写");
                    return;
                }
                if(newPass == null || newPass.length() <= 0) {
                    showToast("新密码未填写");
                    return;
                }
                if(newPass.length() < 6) {
                    showToast("密码长度过短，请换用更复杂的密码");
                    return;
                }
                if(newPass.length() > 18) {
                    showToast("密码长度过长");
                    return;
                }
                if(conPass == null || conPass.length() <= 0) {
                    showToast("请确认密码");
                    return;
                }
                if(!(conPass.equals(newPass))) {
                    showToast("新密码不一致");
                    return;
                }
                oldPass = MD5Util.encode(oldPass);
                newPass = MD5Util.encode(newPass);
                conPass = MD5Util.encode(conPass);
                Map<String, Object> map = new HashMap<>();
                //map.put("id", id);
                map.put("oldPassword", oldPass);
                map.put("password", newPass);
                map.put("password2", conPass);
                HelloHttp.sendPostRequest("api/user/password/change",map,new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("ChangeCodeActivity", "FAILURE");
                        Looper.prepare();
                        Toast.makeText(ChangeCodeActivity.this, "服务器未响应", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseData = response.body().string();
                        Log.d("ChangeCodeActivity", responseData);
                        String result = null;
                        try {
                            result = new JSONObject(responseData).getString("status");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(result.equals("Failure")) {
                            Looper.prepare();
                            Toast.makeText(ChangeCodeActivity.this, "原密码错误",  Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                        else if(result.equals("UnknownError")) {
                            Looper.prepare();
                            Toast.makeText(ChangeCodeActivity.this, "未知错误",  Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                        else if(result.equals("Success")) {
                            Looper.prepare();
                            Toast.makeText(ChangeCodeActivity.this, "修改成功，请重新登录", Toast.LENGTH_SHORT).show();
                            Intent intent1 = new Intent(ChangeCodeActivity.this, LoginActivity.class);
                            startActivity(intent1);
                            Looper.loop();
                        }
                        else {
                            Looper.prepare();
                            Toast.makeText(ChangeCodeActivity.this, result, Toast.LENGTH_SHORT).show();
                            Looper.loop();
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
            String str =  s.toString();
            if(str.indexOf("\r")  >= 0  || str.indexOf("\n") >= 0) {      //发现输入回车或换行
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

    private void showToast(String s) {
        Toast.makeText(ChangeCodeActivity.this, s, Toast.LENGTH_SHORT).show();
    }
}