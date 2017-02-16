package com.arraylist7.android.utils;

import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

/**
 *
 */
public class TypefaceUtils {

	private static Map<String, SoftReference<Typeface>> caches = new HashMap<String, SoftReference<Typeface>>();

	private static Typeface getTypeface(Context context) {
		return getTypeface(context,"fontawesome-webfont.ttf");
	}

	private static Typeface getTypeface(Context context,String fontName) {
		SoftReference<Typeface> cache = caches.get(fontName);
		Typeface font = null;
		if (null == cache || null == (font = cache.get())) {
			font = Typeface.createFromAsset(context.getAssets(), fontName);
			cache = new SoftReference<Typeface>(font);
			caches.put(fontName, cache);
		}
		return font;
	}

	public static void applyFont(Context context, TextView textView, String fontName) {
		textView.setTypeface(getTypeface(context,fontName));
	}

	public static void applyFont(Context context,TextView textView) {
		textView.setTypeface(getTypeface(context));
	}

	public static void setAppDefaultFont(Context context) {
		try {
			final Field staticField = Typeface.class.getDeclaredField("DEFAULT");
			staticField.setAccessible(true);
			staticField.set(null, getTypeface(context));
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
