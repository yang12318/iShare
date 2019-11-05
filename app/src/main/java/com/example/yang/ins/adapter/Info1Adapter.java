package com.example.yang.ins.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.yang.ins.R;
import com.example.yang.ins.bean.Info1;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Info1Adapter extends BaseQuickAdapter<Info1, BaseViewHolder> {

    public Info1Adapter(int layoutResId, @Nullable List<Info1> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Info1 item) {
        helper.setText(R.id.about_follow_username, item.getUserName());
        helper.setText(R.id.about_follow_time, item.getTime());
        helper.setText(R.id.about_follow_like, "给帖子点了赞");
        Glide.with(mContext).load("http://ktchen.cn" + item.getSrc()).into((CircleImageView) helper.getView(R.id.about_follow_head));
        Glide.with(mContext).load("http://ktchen.cn" + item.getPhoto_0()).into((ImageView) helper.getView(R.id.about_follow_picture));
        helper.addOnClickListener(R.id.about_follow_username);
        helper.addOnClickListener(R.id.about_follow_head);
        helper.addOnClickListener(R.id.about_follow_picture);
    }
}
