package com.example.yang.ins;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import org.java_websocket.WebSocketImpl;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.drafts.Draft_75;
import org.java_websocket.drafts.Draft_76;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wlf(Andy)
 * @datetime 2016-02-16 09:28 GMT+8
 * @email 411086563@qq.com
 */
public class ChatClientActivity extends AppCompatActivity implements OnClickListener {

    private ScrollView svChat;
    private EditText etDetails;
    private EditText etMessage;
    private ImageButton btnSend, back;
    private String address = "ws://ktchen.cn:8003/ws/chat/123/";
    private String username;
    private WebSocketClient client;// 连接客户端
    private DraftInfo selectDraft;// 连接协议

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_client);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        back =(ImageButton) findViewById(R.id.ib_chat_back);
        svChat = (ScrollView) findViewById(R.id.svChat);
        etDetails = (EditText) findViewById(R.id.etDetails);
        etMessage = (EditText) findViewById(R.id.etMessage);
        btnSend = (ImageButton) findViewById(R.id.btnSend);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        selectDraft = new DraftInfo("Draft_17", new Draft_17());
        try {
            client = new WebSocketClient(new URI(address), selectDraft.draft) {
                @Override
                public void onOpen(final ServerHandshake serverHandshakeData) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            etDetails.append("已经连接到服务器\n\n");
                            Log.e("wlf", "已经连接到服务器【" + getURI() + "】");
                            btnSend.setEnabled(true);
                        }
                    });
                }

                @Override
                public void onMessage(String message) {
                    Log.d("CCA", message);
                    message = unicodeToString(message);
                    final String finalMessage = message;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonObject = new JSONObject(finalMessage);
                                String name = jsonObject.getString("name");
                                String content = name + ":\t\t" + jsonObject.getString("message") + "\n";
                                SpannableString ss = new SpannableString(content);
                                ss.setSpan(new StyleSpan(Typeface.BOLD), 0, name.length()+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                ss.setSpan(new ForegroundColorSpan(Color.parseColor("#2b5a83")), 0,name.length()+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                etDetails.append(ss);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

                @Override
                public void onClose(final int code, final String reason, final boolean remote) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            etDetails.append("断开服务器连接【" + getURI() + "，状态码： " + code + "，断开原因：" + reason +
                                    "】\n");
                            Log.e("wlf", "断开服务器连接【" + getURI() + "，状态码： " + code + "，断开原因：" + reason + "】");
                            btnSend.setEnabled(false);
                        }
                    });
                }

                @Override
                public void onError(final Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            etDetails.append("连接发生了异常【异常原因：" + e + "】\n");
                            Log.e("wlf", "连接发生了异常【异常原因：" + e + "】");
                            btnSend.setEnabled(false);
                        }
                    });
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
        client.connect();
        btnSend.setOnClickListener(this);
        WebSocketImpl.DEBUG = true;
        System.setProperty("java.net.preferIPv6Addresses", "false");
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSend:
                String s = etMessage.getText().toString().trim();
                if(s.length() <= 0 || s == null) {
                    Toast.makeText(ChatClientActivity.this, "待发送消息为空", Toast.LENGTH_SHORT).show();
                    btnSend.startAnimation(AnimationUtils.loadAnimation(ChatClientActivity.this, R.anim.shake_error));
                    return;
                }
                try {
                    if (client != null) {
                        String message = etMessage.getText().toString().trim();
                        message = "{\"message\":\"" + message + "\",";
                        message = message + "\"name\":\""+username+"\"}";
                        Log.d("ChatClient", message);
                        client.send(message);
                        svChat.post(new Runnable() {
                            @Override
                            public void run() {
                                svChat.fullScroll(View.FOCUS_DOWN);
                                etMessage.setText("");
                                etMessage.requestFocus();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (client != null) {
            client.close();
        }
    }

    private class DraftInfo {

        private final String draftName;
        private final Draft draft;

        public DraftInfo(String draftName, Draft draft) {
            this.draftName = draftName;
            this.draft = draft;
        }

        @Override
        public String toString() {
            return draftName;
        }
    }

    public static String unicodeToString(String str) {
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            str = str.replace(matcher.group(1), ch+"" );
        }
        return str;
    }

}