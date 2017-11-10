package com.arraylist7.android.utils.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.arraylist7.android.utils.BitmapUtils;
import com.arraylist7.android.utils.ClassUtils;
import com.arraylist7.android.utils.LogUtils;
import com.arraylist7.android.utils.ViewUtils;
import com.arraylist7.android.utils.adapter.holder.BaseViewHolder;
import com.arraylist7.android.utils.annotation.ItemDataImg;
import com.arraylist7.android.utils.annotation.ItemDataText;
import com.arraylist7.android.utils.annotation.Params;
import com.arraylist7.android.utils.annotation.RArray;
import com.arraylist7.android.utils.annotation.RColor;
import com.arraylist7.android.utils.annotation.RString;
import com.arraylist7.android.utils.annotation.Views;

import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2017/6/19 0019.
 */

public abstract class BaseRecyclerViewAdapter<T, V extends BaseViewHolder> extends RecyclerView.Adapter<V> {
    protected List<T> data = new ArrayList<T>();
    protected Context context;

    private int TYPE_HEADER = 0;
    private int TYPE_NORMAL = 1;
    private int TYPE_FOOTER = 2;

    private int layoutId;
    protected int headerId;
    protected int footerId;

    protected Map<Integer, SoftReference<View>> viewCaches;
    private static BaseViewHolder viewHolder = null;


    public BaseRecyclerViewAdapter(int layoutId, Context context) {
        this(layoutId, context, null);
    }

    public BaseRecyclerViewAdapter(int layoutId, Context context, List<T> data) {
        this.layoutId = layoutId;
        this.context = context;
        if (data != null)
            this.data.addAll(data);
        setHeader(onCreateHeader());
        setFooter(onCreateFooter());
    }


    public View getViewForFlate(int layoutId, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(layoutId, parent, false);
    }


    public boolean isHeaderOrFooter(int position) {
        if ((position == 0 && headerId != 0) || (position == getItemCount() - 1 && footerId != 0)) {
            return true;
        } else
            return false;
    }


    /**
     * 设置头
     *
     * @param resId
     */
    public void setHeader(int resId) {
        this.headerId = resId;
        notifyItemInserted(0);
    }

    /**
     * 创建一个header从写该方法，自动调用
     *
     * @return header布局
     */
    public int onCreateHeader() {
        return 0;
    }


    /**
     * 设置末尾布局
     *
     * @param resId
     */
    public void setFooter(int resId) {
        this.footerId = resId;
        notifyItemInserted(getItemCount());
    }

    /**
     * 创建一个footer重写该方法
     *
     * @return footer对应的布局
     */
    public int onCreateFooter() {
        return 0;
    }


    @Override
    public int getItemCount() {
        int dataCount = data.size();
        if (0 != headerId)
            dataCount++;
        if (0 != footerId)
            dataCount++;
        return dataCount;
    }


    @Override
    public V onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = getViewForFlate(layoutId, parent);
        BaseViewHolder holder = new BaseViewHolder(view);
        ViewUtils.inject(holder, view);
        return (V) holder;
    }

    @Override
    public void onBindViewHolder(V holder, int position) {
        viewHolder = holder;
        T item = getItem(position);
        dataBind(position, item);
    }

    public abstract void dataBind(int position, T model);


    /**
     * 设置TextView的值
     *
     * @param viewId
     * @param text
     * @return
     */
    public TextView setText(int viewId, CharSequence text) {
        TextView textView = getView(viewId, TextView.class);
        textView.setText(text);
        return textView;
    }

    /**
     * 加载图片
     *
     * @param viewId
     * @param url
     * @return
     */
    public ImageView setImage(int viewId, String url) {
        ImageView imgView = getView(viewId, ImageView.class);
        BitmapUtils.loadBitmap(url, imgView);
        return imgView;
    }

    /**
     * 加载图片
     *
     * @param viewId
     * @param url
     * @return
     */
    public ImageView setImage(int viewId, String url, int width, int height) {
        ImageView imgView = getView(viewId, ImageView.class);
        BitmapUtils.loadBitmap(url, imgView, width, height);
        return imgView;
    }

    public View getView(int itemViewId) {
        return getView(itemViewId, View.class);
    }

    public <T extends View> T getView(int itemViewId, Class<T> c) {
        SoftReference<View> cacheView = viewCaches.get(itemViewId);
        T view = null;
        if (null == cacheView || null == cacheView.get()) {
            view = (T) viewHolder.getItemView().findViewById(itemViewId);
            viewCaches.remove(itemViewId);
            viewCaches.put(itemViewId, new SoftReference<View>(view));
        } else {
            view = (T) cacheView.get();
        }
        return view;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return headerId == 0 ? TYPE_NORMAL : TYPE_HEADER;
        } else if (position == getItemCount() - 1) {
            return footerId == 0 ? TYPE_NORMAL : TYPE_FOOTER;
        } else return TYPE_NORMAL;
    }


    /**
     * 除去header，footer后获取item的位置
     *
     * @param position
     * @return
     */
    public int getItemPosition(int position) {
        if (headerId != 0)
            return position - 1;
        else
            return position;
    }

    /**
     * 通过下标获取指定的数据
     *
     * @param position
     * @return 对应的数据
     */
    public T getItem(int position) {
        int index = getItemPosition(position);
        if (0 > index) return null;
        return data.get(index);
    }

    public List<T> getData() {
        return data;
    }

    /**
     * 获取数据源大小
     *
     * @return 数据源大小
     */
    public int getDataSize() {
        return data == null ? 0 : data.size();
    }

    /**
     * 添加数据
     *
     * @param data 待添加数据源
     */
    public void addData(List<T> data) {
        if (data != null) {
            this.data.addAll(data);
        }
    }

    /**
     * 添加数据
     *
     * @param data 待添加数据源
     */
    public void addData(T[] data) {
        if (data != null) {
            addData(Arrays.asList(data));
        }
    }

    /**
     * 清空数据
     */
    public void clear() {
        if (data != null) {
            data.clear();
        }
    }

    /**
     * 移除数据
     *
     * @param position
     */
    public void remove(int position) {
        int index = getItemPosition(position);
        data.remove(index);
        notifyItemRemoved(index);
        notifyItemChanged(index);
    }

    public void removeAfter(int position) {
        int index = getItemPosition(position);
        if (data.size() > index) {
            data = data.subList(0, index);
            notifyItemRemoved(0);
            notifyItemChanged(0);
        }
    }

    public void updataUI() {
        notifyDataSetChanged();
    }

}

