package com.arraylist7.android.utils.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import com.arraylist7.android.utils.adapter.holder.BaseViewHolder;
import com.arraylist7.android.utils.listener.OnViewPagerItemClickListener;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewPagerAdapter<T> extends PagerAdapter {

    private Context context;
    private int layoutId = -1;
    private List<T> data = new ArrayList<>();
    private Map<String, SoftReference<View>> caches = new HashMap<>();

    private OnViewPagerItemClickListener<T> listener;

    /**
     * 适用布局文件
     *
     * @param context
     * @param layoutId
     * @param data
     */
    public ViewPagerAdapter(Context context, int layoutId, List<T> data) {
        this.context = context;
        this.layoutId = layoutId;
        this.data = data;
    }

    /**
     * 适用自己new View
     *
     * @param context
     * @param data    data里面只能放View
     */
    public ViewPagerAdapter(Context context, List<T> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup view, int position, Object object) {
        view.removeView((View) object);
    }

    // 当要显示的图片可以进行缓存的时候，会调用这个方法进行显示图片的初始化，我们将要显示的ImageView加入到ViewGroup中，然后作为返回值返回即可
    @Override
    public Object instantiateItem(ViewGroup viewGroup, final int position) {
        View view = null;
        if (-1 != layoutId) {
            SoftReference<View> sView = caches.get(layoutId + "_" + position);
            if (null != sView && null != sView.get()) {
                view = sView.get();
            } else {
                caches.remove(layoutId + "_" + position);
                view = LayoutInflater.from(context).inflate(layoutId, null);
                onBindView(position, new BaseViewHolder(view), data.get(position));
                caches.put(layoutId + "_" + position, new SoftReference<View>(view));
            }
        } else {
            view = (View) data.get(position);
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener)
                    listener.onItemClick(ViewPagerAdapter.this, data.get(position), position);
            }
        });
        viewGroup.addView(view);
        return view;
    }

    /**
     * 绑定View ，只适用new ViewPagerAdapter(int layoutId, List<T> data)
     *
     * @param position
     * @param holder
     * @param model
     */
    public void onBindView(int position, BaseViewHolder holder, T model) {

    }


    /**
     * 添加数据
     *
     * @param data 待添加数据源
     */
    public void addData(Collection<T> data) {
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
     * 添加数据
     *
     * @param data 待添加数据源
     */
    public void addData(T data) {
        if (data != null) {
            this.data.add(data);
        }
    }

    /**
     * 清空数据
     */
    public void clear() {
        if (data != null) {
            data.clear();
        }
        caches.clear();
    }


    public void setOnItemClickListener(OnViewPagerItemClickListener<T> listener) {
        this.listener = listener;
    }
}
