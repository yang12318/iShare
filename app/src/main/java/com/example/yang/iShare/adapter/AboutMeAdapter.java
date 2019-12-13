package com.example.yang.iShare.adapter;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.yang.iShare.R;
import com.example.yang.iShare.bean.Info1;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AboutMeAdapter extends BaseMultiItemQuickAdapter<Info1, BaseViewHolder> {
    private int myId;
    public AboutMeAdapter(@Nullable List<Info1> data, int myId) {
        super(data);
        addItemType(1, R.layout.item_about_me_concern);
        addItemType(2, R.layout.item_about_me_like);
        addItemType(3, R.layout.item_about_me_comment);
        this.myId = myId;
    }
    @Override
    protected void convert(BaseViewHolder helper, Info1 item) {
        switch (helper.getItemViewType()){
            case 1:
                helper.setText(R.id.me_username, item.getUserName());
                Glide.with(mContext).load("http://ins.itstudio.club"+item.getSrc()).into((CircleImageView) helper.getView(R.id.me_head));
                helper.addOnClickListener(R.id.btn_follow);
                helper.addOnClickListener(R.id.me_head);
                helper.addOnClickListener(R.id.me_username);
                if (item.getUserId() == myId) {
                    helper.setGone(R.id.btn_follow, false);
                } else {
                    helper.setVisible(R.id.btn_follow, true);
                    if(item.getIsFollowed()) {
                        helper.setText(R.id.btn_follow, "关注中");
                        helper.setTextColor(R.id.btn_follow, Color.BLACK);
                        helper.setBackgroundRes(R.id.btn_follow, R.drawable.buttonshape2);
                    }
                    else {
                        helper.setText(R.id.btn_follow, "关注");
                        helper.setBackgroundRes(R.id.btn_follow, R.drawable.buttonshape3);
                        helper.setTextColor(R.id.btn_follow, Color.WHITE);
                    }
                }
                break;
            case 2:
                helper.setText(R.id.about_me_username, item.getUserName());
                Glide.with(mContext).load("http://ins.itstudio.club"+item.getPhoto_0()).into((ImageView) helper.getView(R.id.about_me_picture));
                Glide.with(mContext).load("http://ins.itstudio.club"+item.getSrc()).into((CircleImageView) helper.getView(R.id.about_me_head));
                helper.addOnClickListener(R.id.about_me_username);
                helper.addOnClickListener(R.id.about_me_head);
                helper.addOnClickListener(R.id.about_me_picture);
                break;
            case 3:
                helper.setText(R.id.tv_me_username, item.getUserName());
                Glide.with(mContext).load("http://ins.itstudio.club"+item.getPhoto_0()).into((ImageView) helper.getView(R.id.iv_me_picture));
                Glide.with(mContext).load("http://ins.itstudio.club"+item.getSrc()).into((CircleImageView) helper.getView(R.id.ci_me_head));
                helper.setText(R.id.tv_me_comment, "评论了："+item.getContent());
                helper.addOnClickListener(R.id.tv_me_username);
                helper.addOnClickListener(R.id.ci_me_head);
                helper.addOnClickListener(R.id.iv_me_picture);
                helper.addOnClickListener(R.id.tv_me_comment);
                break;
        }
    }

}


