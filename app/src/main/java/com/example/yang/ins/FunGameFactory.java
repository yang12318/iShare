package com.example.yang.ins;

import android.content.Context;
import android.util.AttributeSet;

import com.example.yang.ins.HitBlockView;

/**
 * Created by Hitomis on 2016/3/10.
 * email:196425254@qq.com
 */
public class FunGameFactory {

    // Refused to use enum

    static final int HITBLOCK = 0;

    static com.example.yang.ins.FunGameView createFunGameView(Context context, AttributeSet attributeSet, int type) {
        FunGameView funGameView = null;
        switch (type) {
            case HITBLOCK:
                funGameView = new HitBlockView(context, attributeSet);
                break;
            default:
                funGameView = new HitBlockView(context, attributeSet);
        }
        return funGameView;
    }
}
