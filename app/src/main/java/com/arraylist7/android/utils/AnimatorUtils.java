package com.arraylist7.android.utils;

import android.animation.PropertyValuesHolder;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.arraylist7.android.utils.adapter.AnimationAdapter;

public final class AnimatorUtils {

    private AnimatorUtils() {
        //No instances.
    }



    /**
     * 旋转 Rotate
     */
    public static Animation getRotateAnimation(float fromDegrees, float toDegrees, long durationMillis) {
        RotateAnimation rotate = new RotateAnimation(fromDegrees, toDegrees, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(durationMillis);
        rotate.setFillAfter(true);
        return rotate;
    }

    /**
     * 透明度 Alpha
     */
    public static Animation getAlphaAnimation(float fromAlpha, float toAlpha, long durationMillis) {
        AlphaAnimation alpha = new AlphaAnimation(fromAlpha, toAlpha);
        alpha.setDuration(durationMillis);
        alpha.setFillAfter(true);
        return alpha;
    }

    /**
     * 缩放 Scale
     */
    public static Animation getScaleAnimation(float scaleXY, long durationMillis) {
        ScaleAnimation scale = new ScaleAnimation(1.0f, scaleXY, 1.0f, scaleXY, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(durationMillis);
        return scale;
    }

    /**
     * 位移 Translate
     */
    public static Animation getTranslateAnimation(float fromXDelta, float toXDelta, float fromYDelta, float toYDelta, long durationMillis) {
        TranslateAnimation translate = new TranslateAnimation(fromXDelta,toXDelta, fromYDelta, toYDelta);
        translate.setDuration(durationMillis);
        translate.setFillAfter(true);
        return translate;
    }


    /**
     * 点击动画
     * @return
     */
    public static void clickAnimation(View view) {
        clickAnimation(view,1.15F,200L);
    }

    /**
     * 点击动画
     * @param scaleXY
     * @param durationMillis
     * @return
     */
    public static void clickAnimation(View view,float scaleXY, long durationMillis) {
        AnimationSet set = new AnimationSet(true);
        set.addAnimation(getScaleAnimation(scaleXY, durationMillis));
        set.setInterpolator(new LinearInterpolator());
        set.setDuration(durationMillis);
        set.setFillAfter(false);
        view.clearAnimation();
        view.startAnimation(set);
    }

    /**
     * /**
     * 抖动动画
     * @return
     */
    public static void shakeAnimation(View view){
        shakeAnimation(view,7,70L);
    }

    /**
     * /**
     * 抖动动画
     * @param repeatCount
     * @param durationMillis
     * @return
     */
    public static void shakeAnimation(View view,int repeatCount,long durationMillis){
        TranslateAnimation animation = new TranslateAnimation(0, -15, 0, 0);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(durationMillis);
        animation.setRepeatCount(repeatCount);
        animation.setRepeatMode(Animation.REVERSE);
        view.clearAnimation();
        view.startAnimation(animation);
    }
}
