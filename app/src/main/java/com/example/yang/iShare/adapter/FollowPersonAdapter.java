package com.example.yang.iShare.adapter;

import android.graphics.Color;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.yang.iShare.R;
import com.example.yang.iShare.bean.Person;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FollowPersonAdapter extends BaseQuickAdapter<Person, BaseViewHolder> {
    private int myId;
    public FollowPersonAdapter(int layoutResId, List<Person> list, int myId) {
        super(layoutResId, list);
        this.myId = myId;
    }

    @Override
    protected void convert(BaseViewHolder helper, Person item) {
        helper.setText(R.id.follow_username, item.getName());
        helper.setText(R.id.follow_nickname, item.getNickname());
        Glide.with(mContext).load("http://ktchen.cn"+item.getSrc()).into((CircleImageView) helper.getView(R.id.follow_head));
        helper.addOnClickListener(R.id.follow_cancel);
        helper.addOnClickListener(R.id.follow_head);
        helper.addOnClickListener(R.id.follow_username);
        helper.addOnClickListener(R.id.follow_nickname);
        if(item.getId() == myId) {
            helper.setGone(R.id.follow_cancel, false);
        }
        else {
            helper.setVisible(R.id.follow_cancel, true);
            if (item.getIsFollowed()) {
                helper.setText(R.id.follow_cancel, "关注中");
                helper.setTextColor(R.id.follow_cancel, Color.BLACK);
                helper.setBackgroundRes(R.id.follow_cancel, R.drawable.buttonshape2);
            } else {
                helper.setText(R.id.follow_cancel, "关注");
                helper.setBackgroundRes(R.id.follow_cancel, R.drawable.buttonshape3);
                helper.setTextColor(R.id.follow_cancel, Color.WHITE);
            }
        }
    }
}
