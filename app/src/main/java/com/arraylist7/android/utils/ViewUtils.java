package com.arraylist7.android.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.arraylist7.android.utils.annotation.Params;
import com.arraylist7.android.utils.annotation.RArray;
import com.arraylist7.android.utils.annotation.RColor;
import com.arraylist7.android.utils.annotation.RString;
import com.arraylist7.android.utils.annotation.Views;

import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        List<String> keys = null;
        if (null != bundle) {
            keys = StringUtils.asList(bundle.keySet());
        } else {
            keys = new ArrayList<>();
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
                    LogUtils.e(getFieldInfo(field) + " 注入" + injectViewName + " 失败");
                }
                String setText = aView.setText();
                String setTag = aView.setTag();
                if (!StringUtils.isNullOrEmpty(setText)) {
                    Object val = bundle.get(setText);
                    if (StringUtils.isNullOrEmpty(val)) {
                        LogUtils.d(getFieldInfo(field) + " 绑定setText错误，不能找到参数key=" + setText + "。");
                    } else {
                        if (findView instanceof TextView) {
                            ((TextView) findView).setText(val + "");
                        } else {
                            LogUtils.d(getFieldInfo(field) + " 绑定setText错误，该View不支持setText。");
                        }
                    }
                }
                if (!StringUtils.isNullOrEmpty(setTag)) {
                    Object val = bundle.get(setTag);
                    if (StringUtils.isNullOrEmpty(val)) {
                        LogUtils.d(getFieldInfo(field) + " 绑定setTag错误，不能找到参数key=" + setTag + "。");
                    } else {
                        findView.setTag(val + "");
                    }
                }
            }
            // 注入参数
            if (null != params) {
                for (String key : keys) {
                    if (key.equalsIgnoreCase(params.value())) {
                        Object val = bundle.get(key);
                        try {
                            if (!StringUtils.isNullOrEmpty(val)) {
                                field.setAccessible(true);
                                field.set(object, val);
                            }
                        } catch (Throwable e) {
                            LogUtils.e(getFieldInfo(field) + " 绑定参数：" + key + " 错误。");
                        }
                        int setText = params.setText();
                        int setTag = params.setTag();
                        if (-1 != setText) {
                            View view = vs.findViewById(setText);
                            if (null == view) {
                                LogUtils.e(getFieldInfo(field) + " 参数" + key + "绑定setText失败。");
                            } else {
                                if (view instanceof TextView) {
                                    ((TextView) view).setText(val.toString());
                                }
                            }
                        }
                        if (-1 != setTag) {
                            View view = vs.findViewById(setTag);
                            if (null == view) {
                                LogUtils.e(getFieldInfo(field) + " 参数" + key + "绑定setTag失败。");
                            } else {
                                view.setTag(val);
                            }
                        }
                        break;
                    }
                }
            }
            // 注入r.array
            if (null != array) {
                boolean isStringArray = field.getGenericType().toString().contains("String");
                String[] array_string = null;
                int[] array_int = null;
                if (isStringArray) {
                    array_string = vs.getResources().getStringArray(array.value());
                } else {
                    if (array.isResources()) {
                        TypedArray typedArray = vs.getResources().obtainTypedArray(array.value());
                        int end = typedArray.length();
                        array_int = new int[end];
                        for (int i = 0; i < end; i++) {
                            array_int[i] = typedArray.getResourceId(i, 0);
                        }
                    } else {
                        array_int = vs.getResources().getIntArray(array.value());
                    }
                }
                if (0 == StringUtils.len(array_string) && 0 == StringUtils.len(array_int)) {
                    LogUtils.e(getFieldInfo(field) + " 注入R.array无效");
                    continue;
                }
                try {
                    field.setAccessible(true);
                    field.set(object, (0 == StringUtils.len(array_string) ? array_int : array_string));
                } catch (Throwable e) {
                    LogUtils.e(getFieldInfo(field) + " 注入R.array：" + array.value() + " 失败");
                }
            }
            // 注入r.string
            if (null != string) {
                String strings = vs.getResources().getString(string.value());
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
                int colors = vs.getResources().getColor(color.value());
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
    private List<SoftReference<View>> list = new ArrayList<>();
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
        View view = null;
        for (SoftReference<View> softView : list) {
            if (null != softView) {
                if (null != softView.get()) {
                    if (id == softView.get().getId()) {
                        return softView.get();
                    }
                }
            }
        }
        view = null == this.activity ? this.view.findViewById(id) : this.activity.findViewById(id);
        list.add(new SoftReference<View>(view));
        return view;
    }

    public Context getContext() {
        return context;
    }

    public Resources getResources() {
        return activity == null ? context.getResources() : activity.getResources();
    }

    public void clear() {
        for (SoftReference<View> softView : list) {
            softView.clear();
        }
        list.clear();
        list = null;
        context = null;
        activity = null;
        view = null;
    }
}