package com.dryseed.dslive.utils;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dryseed.dslive.DsApplication;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 *  Created by caiminming on 2017/11/7.
 */
public class ImgUtils {

    public static void load(String url, ImageView targetView) {
        Glide.with(DsApplication.getContext())
                .load(url)
                .into(targetView);
    }

    public static void load(int resId, ImageView targetView) {
        Glide.with(DsApplication.getContext())
                .load(resId)
                .into(targetView);
    }

    public static void loadRound(String url, ImageView targetView) {
        Glide.with(DsApplication.getContext())
                .load(url)
                .bitmapTransform(new CropCircleTransformation(DsApplication.getContext()))
                .into(targetView);
    }

    public static void loadRound(int resId, ImageView targetView) {
        Glide.with(DsApplication.getContext())
                .load(resId)
                .bitmapTransform(new CropCircleTransformation(DsApplication.getContext()))
                .into(targetView);
    }
}
