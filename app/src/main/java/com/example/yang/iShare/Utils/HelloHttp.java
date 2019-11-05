package com.example.yang.iShare.Utils;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.yang.iShare.MainApplication;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static android.content.Context.MODE_PRIVATE;

public class HelloHttp {
    public HelloHttp() {

    }

    public static void sendGetRequest(String adress, Map<String, Object> map, okhttp3.Callback callback) {
        //按照开拓的方式处理url
        String url = dealAddress(adress);
        //将map中的数据拼接到url后面
        Set<String> sets = map.keySet();
        for (String set : sets) {
            url = url + "&" + set + "=" + String.valueOf(map.get(set));
        }
        //访问url
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", getAuthorization())
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static String dealAddress(String adress) {
        String AppID = "3309_7liLTsOOWPbEioqY";
        String AppKey = "C5hunqgHUgYo$b6a77dmEls9U3gz5by5aTvHNco+YBKtrQXE8plHI2v4=";
        String timeStamp = DateUtil.getTimeStamp();
        String sign = timeStamp + AppKey;
        sign = MD5Util.encode(sign);
        String url = "http://ins.itstudio.club/" + adress + "/?timestamp=" + timeStamp + "&appid=" + AppID + "&sign=" + sign;
        Log.d("HelloHttp", "new url = " + url);
        return url;
    }

    public static void sendPostRequest(String adress, Map<String, Object> map, okhttp3.Callback callback) {
        //按照开拓的方式处理url
        String url = dealAddress(adress);
        //将map中的数据加入表单中
        FormBody.Builder formBody = new FormBody.Builder();
        Set<String> sets = map.keySet();
        for (String set : sets) {
            formBody.add(set, String.valueOf(map.get(set)));
        }
        RequestBody requestBody = formBody.build();
        //访问url
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .header("Authorization", getAuthorization())
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void sendPutRequest(String adress, Map<String, Object> map, okhttp3.Callback callback) {
        //按照开拓的方式处理url
        String url = dealAddress(adress);
        //将map中的数据加入表单中
        FormBody.Builder formBody = new FormBody.Builder();
        Set<String> sets = map.keySet();
        for (String set : sets) {
            formBody.add(set, String.valueOf(map.get(set)));
        }
        RequestBody requestBody = formBody.build();
        //访问url
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .put(requestBody)
                .header("Authorization", getAuthorization())
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void sendDeleteRequest(String adress, Map<String, Object> map, okhttp3.Callback callback) {
        //按照开拓的方式处理url
        String url = dealAddress(adress);
        //将map中的数据加入表单中
        FormBody.Builder formBody = new FormBody.Builder();
        Set<String> sets = map.keySet();
        for (String set : sets) {
            formBody.add(set, String.valueOf(map.get(set)));
        }
        RequestBody requestBody = formBody.build();
        //访问url
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", getAuthorization())
                .delete(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    private static String getAuthorization() {
        String Authorization = null;
        SharedPreferences mShared;
        mShared = MainApplication.getContext().getSharedPreferences("share", MODE_PRIVATE);
        Map<String, Object> mapParam = (Map<String, Object>) mShared.getAll();
        for (Map.Entry<String, Object> item_map : mapParam.entrySet()) {
            String key = item_map.getKey();
            Object value = item_map.getValue();
            if(key.equals("Authorization")) {
                Authorization = value.toString();
            }
        }
        return Authorization;
    }

    public static void sendFirstPostRequest(String adress, Map<String, Object> map, okhttp3.Callback callback) {
        //按照开拓的方式处理url
        String url = dealAddress(adress);
        //将map中的数据加入表单中
        FormBody.Builder formBody = new FormBody.Builder();
        Set<String> sets = map.keySet();
        for (String set : sets) {
            formBody.add(set, String.valueOf(map.get(set)));
        }
        RequestBody requestBody = formBody.build();
        Log.d("HelloHttp", "postFinalUrl = " + url);
        //访问url
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void sendFirstLongPostRequest(String adress, Map<String, Object> map, okhttp3.Callback callback) {
        //按照开拓的方式处理url
        String url = dealAddress(adress);
        //将map中的数据加入表单中
        FormBody.Builder formBody = new FormBody.Builder();
        Set<String> sets = map.keySet();
        for (String set : sets) {
            formBody.add(set, String.valueOf(map.get(set)));
        }
        RequestBody requestBody = formBody.build();
        Log.d("HelloHttp", "postFinalUrl = " + url);
        //访问url
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient clientWith20sTimeout = client.newBuilder().
                readTimeout(20, TimeUnit.SECONDS).
                build();
        clientWith20sTimeout.newCall(request).enqueue(callback);
    }

    public static void sendFirstGetRequest(String adress, Map<String, Object> map, okhttp3.Callback callback) {
        //按照开拓的方式处理url
        String url = dealAddress(adress);
        //将map中的数据拼接到url后面
        Set<String> sets = map.keySet();
        for (String set : sets) {
            url = url + "&" + set + "=" + String.valueOf(map.get(set));
        }
        Log.d("HelloHttp", "getFinalUrl = " + url);
        //访问url
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void sendFirstLongGetRequest(String adress, Map<String, Object> map, okhttp3.Callback callback) {
        //按照开拓的方式处理url
        String url = dealAddress(adress);
        //将map中的数据拼接到url后面
        Set<String> sets = map.keySet();
        for (String set : sets) {
            url = url + "&" + set + "=" + String.valueOf(map.get(set));
        }
        Log.d("HelloHttp", "getFinalUrl = " + url);
        //访问url
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        OkHttpClient clientWith20sTimeout = client.newBuilder().
                readTimeout(20, TimeUnit.SECONDS).
                build();
        clientWith20sTimeout.newCall(request).enqueue(callback);
    }

    public static void sendSpecialPostRequest(String address, Map<String, Object> urlmap, Map<String, Object> bodymap, okhttp3.Callback callback) {
        //按照开拓的方式处理url
        String url = dealAddress(address);
        //将map中的数据拼接到url后面
        Set<String> sets = urlmap.keySet();
        for (String set : sets) {
            url = url + "&" + set + "=" + String.valueOf(urlmap.get(set));
        }
        Log.d("HelloHttp", "finalUrl="+url);
        //将map中的数据加入表单中
        FormBody.Builder formBody = new FormBody.Builder();
        Set<String> sets2 = bodymap.keySet();
        for (String set : sets2) {
            formBody.add(set, String.valueOf(bodymap.get(set)));
        }
        RequestBody requestBody = formBody.build();
        //访问url
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .header("Authorization", getAuthorization())
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void sendSpecialDeleteRequest(String address, Map<String, Object> urlmap, Map<String, Object> bodymap, okhttp3.Callback callback) {
        //按照开拓的方式处理url
        String url = dealAddress(address);
        //将map中的数据拼接到url后面
        Set<String> sets = urlmap.keySet();
        for (String set : sets) {
            url = url + "&" + set + "=" + String.valueOf(urlmap.get(set));
        }
        Log.d("HelloHttp", "finalUrl="+url);
        //将map中的数据加入表单中
        FormBody.Builder formBody = new FormBody.Builder();
        Set<String> sets2 = bodymap.keySet();
        for (String set : sets2) {
            formBody.add(set, String.valueOf(bodymap.get(set)));
        }
        RequestBody requestBody = formBody.build();
        //访问url
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .delete(requestBody)
                .header("Authorization", getAuthorization())
                .build();
        client.newCall(request).enqueue(callback);
    }
}