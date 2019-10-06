package com.example.yang.iShare.adapter;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.yang.iShare.R;
import com.example.yang.iShare.bean.Review;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewAdapter extends BaseQuickAdapter<Review, BaseViewHolder> {

    public ReviewAdapter(int layoutResId, List<Review> list) {
        super(layoutResId, list);
    }

    protected void convert(BaseViewHolder helper, Review item) {
        helper.setText(R.id.comment_detail, item.getContent());
        helper.setText(R.id.comment_time, item.getPub_time());
        helper.setText(R.id.comment_username, item.getCommenter());
        Glide.with(mContext).load(item.getSrc()).into((CircleImageView) helper.getView(R.id.comment_head));
        helper.addOnClickListener(R.id.cv_comment);
        helper.addOnClickListener(R.id.comment_head);
        helper.addOnClickListener(R.id.comment_username);
    }
}
