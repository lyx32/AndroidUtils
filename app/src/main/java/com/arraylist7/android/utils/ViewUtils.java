package com.arraylist7.android.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.arraylist7.android.utils.annotation.Params;
import com.arraylist7.android.utils.annotation.Views;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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
                bundle = intent.getBundleExtra(IntentUtils.DATA_BUNDLE_KEY);
        } else if (object instanceof android.app.Fragment) {
            bundle = ((android.app.Fragment) object).getArguments();
        } else if (object instanceof android.support.v4.app.Fragment) {
            bundle = ((android.support.v4.app.Fragment) object).getArguments();
        }
        return bundle;
    }

    private static void injectObject(Object object, ViewSource iv) {
        if (null == object) {
            LogUtils.e("要注入的对象为空");
            return;
        }
        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        Bundle bundle = getBundle(object);
        Set<String> keys = null;
        if (null != bundle) {
            keys = bundle.keySet();
        }
        for (Field field : fields) {
            Views aView = field.getAnnotation(Views.class);
            Params params = field.getAnnotation(Params.class);
            if (null != aView) {
                View findView = iv.findViewById(aView.value());
                if (null == findView) {
                    LogUtils.e(getFieldInfo(field) + " 注入id无效");
                    break;
                }
                field.setAccessible(true);
                try {
                    field.set(object, findView);
                } catch (Throwable e) {
                    String injectViewName = findView.getClass().toString().replaceFirst("class", "");
                    LogUtils.e(getFieldInfo(field) + " 将要注入" + injectViewName);
                }
            }
            if (null != params) {
                if (null == keys) {
                    field.setAccessible(true);
                    Object val = params.def();
                    try {
                        field.set(object, val);
                    } catch (Throwable e) {
                        LogUtils.e(getFieldInfo(field) + " 绑定def参数：" + val + " 错误。");
                    }
                } else {
                    for (String key : keys) {
                        if (key.equalsIgnoreCase(params.value())) {
                            field.setAccessible(true);
                            try {
                                Object val = bundle.get(key);
                                if (StringUtils.isNullOrEmpty(val))
                                    val = params.def();
                                field.set(object, val);
                            } catch (Throwable e) {
                                LogUtils.e(getFieldInfo(field) + " 绑定参数：" + key + " 错误。");
                            }
                            break;
                        }
                    }
                }
            }
        }
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
    private Activity activity;
    private View view;

    public ViewSource(Activity activity) {
        super();
        this.activity = activity;
    }

    public ViewSource(View view) {
        super();
        this.view = view;
    }

    public View findViewById(int id) {
        return null == activity ? view.findViewById(id) : activity.findViewById(id);
    }
}