package com.arraylist7.android.utils;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.arraylist7.android.utils.listener.AnimationAdapter;

public final class AnimatorUtils {

    private AnimatorUtils() {
        //No instances.
    }

    public static void anim(View view, Animation anim) {
        if (null != view && null != anim)
            view.startAnimation(anim);
    }


    public static void alphaAnimation(View view, float fromAlpha, float toAlpha, long durationMillis) {
        alphaAnimation(view, fromAlpha, toAlpha, durationMillis, null);
    }

    public static void alphaAnimation(View view, float fromAlpha, float toAlpha, long durationMillis, Animation.AnimationListener listener) {
        Animation anim = getAlphaAnimation(fromAlpha, toAlpha, durationMillis);
        if (null != listener)
            anim.setAnimationListener(listener);
        view.startAnimation(anim);
    }


    /**
     * 等比缩放
     *
     * @param view
     * @param oldScaleXY
     * @param newScaleXY
     * @param durationMillis
     */
    public static void scaleAnimation(View view, float oldScaleXY, float newScaleXY, long durationMillis) {
        scaleAnimation(view, oldScaleXY, newScaleXY, durationMillis, null);
    }

    /**
     * 等比缩放
     *
     * @param view
     * @param oldScaleXY
     * @param newScaleXY
     * @param durationMillis
     * @param listener
     */
    public static void scaleAnimation(View view, float oldScaleXY, float newScaleXY, long durationMillis, Animation.AnimationListener listener) {
        Animation anim = new ScaleAnimation(oldScaleXY, newScaleXY, oldScaleXY, newScaleXY, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(durationMillis);
        if (null != listener)
            anim.setAnimationListener(listener);
        view.startAnimation(anim);
    }


    /**
     * @param view
     * @param fromXDelta
     * @param toXDelta
     * @param fromYDelta
     * @param toYDelta
     * @param durationMillis
     */
    public static void translateAnimation(View view, float fromXDelta, float toXDelta, float fromYDelta, float toYDelta, long durationMillis) {
        translateAnimation(view, fromXDelta, toXDelta, fromYDelta, toYDelta, durationMillis, null);
    }

    /**
     * @param view
     * @param fromXDelta
     * @param toXDelta
     * @param fromYDelta
     * @param toYDelta
     * @param durationMillis
     * @param listener
     */
    public static void translateAnimation(View view, float fromXDelta, float toXDelta, float fromYDelta, float toYDelta, long durationMillis, Animation.AnimationListener listener) {
        Animation anim = getTranslateAnimation(fromXDelta, toXDelta, fromYDelta, toYDelta, durationMillis);
        if (null != listener)
            anim.setAnimationListener(listener);
        view.startAnimation(anim);
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
        TranslateAnimation translate = new TranslateAnimation(fromXDelta, toXDelta, fromYDelta, toYDelta);
        translate.setDuration(durationMillis);
        translate.setFillAfter(true);
        return translate;
    }


    /**
     * 点击动画
     *
     * @return
     */
    public static void clickAnimation(View view) {
        clickAnimation(view, 1.15F);
    }

    /**
     * 点击动画
     *
     * @param scaleXY
     * @return
     */
    public static void clickAnimation(View view, float scaleXY) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.5F, 1F);
        alphaAnimation.setDuration(200);
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.7F, scaleXY, 0.7F, scaleXY, Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F);
        scaleAnimation.setDuration(200);
        scaleAnimation.setInterpolator(new AccelerateInterpolator());
        ScaleAnimation scaleAnimation2 = new ScaleAnimation(scaleXY, 0.8F, scaleXY, 0.8F, Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F);
        scaleAnimation2.setDuration(300);
        scaleAnimation.setInterpolator(new DecelerateInterpolator());
        ScaleAnimation scaleAnimation3 = new ScaleAnimation(0.8F, 1F, 0.8F, 1F, Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F);
        scaleAnimation3.setDuration(100);
        scaleAnimation.setInterpolator(new DecelerateInterpolator());

        AnimationSet set = new AnimationSet(true);
        set.addAnimation(alphaAnimation);
        set.addAnimation(scaleAnimation);
        set.addAnimation(scaleAnimation2);
        set.addAnimation(scaleAnimation3);
        set.setInterpolator(new AccelerateInterpolator());
        view.clearAnimation();
        view.startAnimation(set);
    }

    /**
     * /**
     * 抖动动画
     *
     * @return
     */
    public static void shakeAnimation(View view) {
        shakeAnimation(view, 10);
    }

    /**
     * /**
     * 抖动动画
     *
     * @param repeatCount
     * @return
     */
    public static void shakeAnimation(View view, int repeatCount) {
        TranslateAnimation animation = new TranslateAnimation(-10, 10, 0, 0);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(37);
        animation.setRepeatCount(repeatCount);
        animation.setRepeatMode(Animation.REVERSE);
        animation.setFillAfter(false);
        view.clearAnimation();
        view.startAnimation(animation);
    }
}
