package com.arraylist7.android.utils.adapter;

import android.content.Context;
import android.widget.CompoundButton;

import com.arraylist7.android.utils.BitmapUtils;
import com.arraylist7.android.utils.ClassUtils;
import com.arraylist7.android.utils.LogUtils;
import com.arraylist7.android.utils.StringUtils;
import com.arraylist7.android.utils.adapter.holder.BaseViewHolder;
import com.arraylist7.android.utils.annotation.DataBind;
import com.arraylist7.android.utils.annotation.LayoutBind;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AutoBindRecyclerViewAdapter<T> extends RecyclerViewAdapter<T> {

    private LayoutBind layout = null;
    private Map<String, DataBind> dataField = new HashMap<>();

    public AutoBindRecyclerViewAdapter(Context context) {
        super(0, context);
    }


    public AutoBindRecyclerViewAdapter(int layoutId,Context context) {
        super(layoutId, context);
    }

    public AutoBindRecyclerViewAdapter(Context context, List<T> data) {
        super(0, context, data);
        init(data.get(0));
    }

    public AutoBindRecyclerViewAdapter(int layoutId, Context context, List<T> data) {
        super(layoutId, context, data);
        init(data.get(0));
    }

    private void init(T t) {
        if (null == t)
            return;
        if (0 == getLayoutId()) {
            layout = t.getClass().getAnnotation(LayoutBind.class);
            setLayoutId(layout.value());
        }
        dataField.clear();
        Field[] fields = t.getClass().getDeclaredFields();
        for (Field f : fields) {
            DataBind dataBind = f.getAnnotation(DataBind.class);
            if (null != dataBind) {
                dataField.put(f.getName(), dataBind);
            }
        }
    }

    @Override
    public void onBindView(int position, BaseViewHolder holder, T model) {
        for (String fieldName : dataField.keySet()) {
            DataBind val = dataField.get(fieldName);
            String setValue = "";
            try {
                setValue = ClassUtils.getValue(model, fieldName) + "";
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            if (-1 != val.setText()) {
                holder.setText(val.setText(), setValue);
            }
            if (-1 != val.setTag()) {
                holder.getView(val.setTag()).setTag(setValue);
            }
            if (-1 != val.setImage()) {
                int w = val.imageWidth();
                int h = val.imageHeight();
                if (-1 != w || -1 != h)
                    BitmapUtils.loadBitmap(setValue, w, h, holder.getImageView(val.setImage()));
                else
                    BitmapUtils.loadBitmap(setValue, holder.getImageView(val.setImage()));
            }
            if (-1 != val.check()) {
                String v = StringUtils.trim(setValue);
                boolean isCheck = (v.equalsIgnoreCase("y") || v.equalsIgnoreCase("yes") || v.equalsIgnoreCase("t") || v.equalsIgnoreCase("true"));
                holder.getView(val.check(), CompoundButton.class).setChecked(isCheck);
            }
        }
    }

    @Override
    public void addData(Collection<T> data) {
        super.addData(data);
        if (!StringUtils.isNullOrEmpty(data))
            init(data.iterator().next());
    }

    @Override
    public void addData(T[] data) {
        super.addData(data);
        if (!StringUtils.isNullOrEmpty(data))
            init(data[0]);
    }

    @Override
    public void addData(T data) {
        super.addData(data);
        if (!StringUtils.isNullOrEmpty(data))
            init(data);
    }

}
