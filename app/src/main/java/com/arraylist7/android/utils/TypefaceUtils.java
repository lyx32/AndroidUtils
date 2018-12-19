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

    private static Map<String, SoftReference<Typeface>> caches = new HashMap<String, SoftReference<Typeface>>();

    public synchronized static Typeface getTypeface(Context context, String fontName) {
        SoftReference<Typeface> cache = caches.get(fontName);
        Typeface font = null;
        if (null == cache || null == (font = cache.get())) {
            font = Typeface.createFromAsset(context.getAssets(), fontName);
            cache = new SoftReference<Typeface>(font);
            caches.put(fontName, cache);
        }
        return font;
    }

    public static void applyFont(Context context, View view, String fontName) {
        if (view instanceof TextView) {
            ((TextView) view).setTypeface(getTypeface(context, fontName));
        } else if (view instanceof ViewGroup) {
            int end = ((ViewGroup) view).getChildCount();
            for (int i = 0; i < end; i++) {
                View child = ((ViewGroup) view).getChildAt(i);
                applyFont(context, child, fontName);
            }
        }
    }
}
