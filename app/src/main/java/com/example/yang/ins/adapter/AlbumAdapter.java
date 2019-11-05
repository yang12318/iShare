package com.example.yang.ins.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.yang.ins.R;
import com.example.yang.ins.bean.Dynamic;

import java.util.List;

public class AlbumAdapter extends BaseQuickAdapter<Dynamic, BaseViewHolder> {
    public AlbumAdapter(int layoutResId, @Nullable List<Dynamic> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Dynamic item) {
        Glide.with(mContext).load(item.getPhoto0()).into((ImageView) helper.getView(R.id.iv_picture));
        helper.addOnClickListener(R.id.iv_picture);
        helper.setVisible(R.id.iv_multi,item.isIs_multi());
    }
}
