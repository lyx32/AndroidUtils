package com.arraylist7.android.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class TypefaceUtils {

    private static Typeface DEFAULT_SANS_SERIF_FONT = null;
    private static Map<String, SoftReference<Typeface>> caches = new HashMap<String, SoftReference<Typeface>>();

    public synchronized static Typeface getAssetsTypeface(Context context, String fontName) {
        SoftReference<Typeface> cache = caches.get(fontName);
        Typeface font = null;
        if (null == cache || null == (font = cache.get())) {
            font = Typeface.createFromAsset(context.getAssets(), fontName);
            cache = new SoftReference<Typeface>(font);
            caches.put(fontName, cache);
        }
        return font;
    }


    public synchronized static Typeface getPathTypeface(Context context, String fontPath) {
        SoftReference<Typeface> cache = caches.get(fontPath);
        Typeface font = null;
        if (null == cache || null == (font = cache.get())) {
            font = Typeface.createFromFile(fontPath);
            cache = new SoftReference<Typeface>(font);
            caches.put(fontPath, cache);
        }
        return font;
    }

    public static void applyAssetsFont(Context context, View view, String fontPath) {
        if (view instanceof TextView) {
            ((TextView) view).setTypeface(getAssetsTypeface(context, fontPath));
        } else if (view instanceof ViewGroup) {
            int end = ((ViewGroup) view).getChildCount();
            for (int i = 0; i < end; i++) {
                View child = ((ViewGroup) view).getChildAt(i);
                applyAssetsFont(context, child, fontPath);
            }
        }
    }

    public static void applyPathFont(Context context, View view, String fontPath) {
        if (view instanceof TextView) {
            ((TextView) view).setTypeface(getPathTypeface(context, fontPath));
        } else if (view instanceof ViewGroup) {
            int end = ((ViewGroup) view).getChildCount();
            for (int i = 0; i < end; i++) {
                View child = ((ViewGroup) view).getChildAt(i);
                applyPathFont(context, child, fontPath);
            }
        }
    }

    /**
     * 需要将在应用主题内增加<item name="android:typeface">sans</item>，并且要在此之后加载的组件才能生效，如果某些View不需要全局字体，可以设置textView.setTypeface();和android:typeface=""取值不是SANS_SERIF即可
     *
     * @param context
     * @param fontName
     */
    public static void setAssetsDefaultFont(Context context, String fontName) {
        try {
            if (null == DEFAULT_SANS_SERIF_FONT)
                DEFAULT_SANS_SERIF_FONT = ClassUtils.getValue(Typeface.class, "SANS_SERIF");
            ClassUtils.setValue(Typeface.class, "SANS_SERIF", TypefaceUtils.getAssetsTypeface(context, fontName));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }


    /**
     * 需要将在应用主题内增加<item name="android:typeface">sans</item>，并且要在此之后加载的组件才能生效，如果某些View不需要全局字体，可以设置textView.setTypeface();和android:typeface=""取值不是SANS_SERIF即可
     *
     * @param context
     * @param fontName
     */
    public static void setPathDefaultFont(Context context, String fontName) {
        try {
            if (null == DEFAULT_SANS_SERIF_FONT)
                DEFAULT_SANS_SERIF_FONT = ClassUtils.getValue(Typeface.class, "SANS_SERIF");
            ClassUtils.setValue(Typeface.class, "SANS_SERIF", TypefaceUtils.getPathTypeface(context, fontName));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    /**
     * 还愿默认字体
     * @param context
     */
    public static void clearDefaultFont(Context context) {
        if (null != DEFAULT_SANS_SERIF_FONT) {
            try {
                ClassUtils.setValue(Typeface.class, "SANS_SERIF", DEFAULT_SANS_SERIF_FONT);
                DEFAULT_SANS_SERIF_FONT = null;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }
}
