package com.arraylist7.android.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.arraylist7.android.utils.annotation.Params;
import com.arraylist7.android.utils.annotation.RArray;
import com.arraylist7.android.utils.annotation.RColor;
import com.arraylist7.android.utils.annotation.RString;
import com.arraylist7.android.utils.annotation.Views;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

public final class ViewUtils {

    private ViewUtils() {
    }

    public static void inject(Activity activity) {
        injectObject(activity, new ViewSource(activity));
    }

    public static void inject(Object obj, View view) {
        injectObject(obj, new ViewSource(view));
    }

    private static Bundle getBundle(Object object) {
        Bundle bundle = null;
        if (object instanceof Activity) {
            Intent intent = ((Activity) object).getIntent();
            if (null != intent)
                bundle = intent.getExtras();
        } else if (object instanceof android.app.Fragment) {
            bundle = ((android.app.Fragment) object).getArguments();
        } else if (object instanceof android.support.v4.app.Fragment) {
            bundle = ((android.support.v4.app.Fragment) object).getArguments();
        }
        return bundle;
    }

    private static void injectObject(Object object, ViewSource vs) {
        if (null == object) {
            LogUtils.e("要注入的对象为空");
            return;
        }
        Class<?> clazz = object.getClass();
        Field[] fields = ClassUtils.getDeclaredFields(clazz);
        Bundle bundle = getBundle(object);
        Set<String> keys = null;
        if (null != bundle) {
            keys = bundle.keySet();
        }
        for (Field field : fields) {
            Views aView = field.getAnnotation(Views.class);
            Params params = field.getAnnotation(Params.class);
            RArray array = field.getAnnotation(RArray.class);
            RString string = field.getAnnotation(RString.class);
            RColor color = field.getAnnotation(RColor.class);
            // 注入View
            if (null != aView) {
                View findView = vs.findViewById(aView.value());
                if (null == findView) {
                    LogUtils.e(getFieldInfo(field) + " 注入id无效");
                    continue;
                }
                try {
                    field.setAccessible(true);
                    field.set(object, findView);
                } catch (Throwable e) {
                    String injectViewName = findView.getClass().toString().replaceFirst("class", "");
                    LogUtils.e(getFieldInfo(field) + " 注入" + injectViewName+" 失败");
                }
            }
            // 注入参数
            if (null != params) {
                if (null != keys && null != keys.iterator()) {
                    for (String key : keys) {
                        if (key.equalsIgnoreCase(params.value())) {
                            try {
                                Object val = bundle.get(key);
                                if (!StringUtils.isNullOrEmpty(val)) {
                                    field.setAccessible(true);
                                    field.set(object, val);
                                }
                            } catch (Throwable e) {
                                LogUtils.e(getFieldInfo(field) + " 绑定参数：" + key + " 错误。");
                            }
                            break;
                        }
                    }
                }
            }
            // 注入r.array
            if (null != array) {
                String[] arrays = vs.getContext().getResources().getStringArray(array.value());
                if (null == arrays && 0 != StringUtils.len(arrays)) {
                    LogUtils.e(getFieldInfo(field) + " 注入R.array无效");
                    continue;
                }
                try {
                    field.setAccessible(true);
                    field.set(object, arrays);
                } catch (Throwable e) {
                    LogUtils.e(getFieldInfo(field) + " 注入R.array：" + array.value() + " 失败");
                }
            }
            // 注入r.string
            if (null != string) {
                String strings = vs.getContext().getResources().getString(string.value());
                if (null == strings) {
                    LogUtils.e(getFieldInfo(field) + " 注入R.string无效");
                    continue;
                }
                try {
                    field.setAccessible(true);
                    field.set(object, strings);
                } catch (Throwable e) {
                    LogUtils.e(getFieldInfo(field) + " 注入R.string：" + string.value() + " 失败");
                }
            }
            // 注入r.color
            if (null != color) {
                int colors = vs.getContext().getResources().getColor(color.value());
                if (0 >= colors) {
                    LogUtils.e(getFieldInfo(field) + " 注入R.color无效");
                    continue;
                }
                try {
                    field.setAccessible(true);
                    field.set(object, colors);
                } catch (Throwable e) {
                    LogUtils.e(getFieldInfo(field) + " 注入R.color：" + color.value() + " 失败");
                }
            }
        }
        vs.clear();
    }


    private static String getFieldInfo(Field field) {
        String modifier = Modifier.toString(field.getModifiers());
        String genericString[] = field.toGenericString().split(" ");
        String type = genericString[1];
        String name = field.getName();
        return modifier + " " + type + " " + name;
    }
}

class ViewSource {
    private Context context;
    private Activity activity;
    private View view;

    public ViewSource(Activity activity) {
        super();
        this.activity = activity;
        this.context = activity;
    }

    public ViewSource(View view) {
        super();
        this.view = view;
        this.context = view.getContext();
    }

    public View findViewById(int id) {
        return null == activity ? view.findViewById(id) : activity.findViewById(id);
    }

    public Context getContext() {
        return context;
    }

    public void clear() {
        context = null;
        activity = null;
        view = null;
    }
}