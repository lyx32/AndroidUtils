package com.arraylist7.android.utils.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arraylist7.android.utils.ViewUtils;
import com.arraylist7.android.utils.adapter.holder.BaseViewHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2017/6/19 0019.
 */

public abstract class RecyclerViewAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {
    protected List<T> data = new ArrayList<T>();
    protected Context context;

    protected int TYPE_HEADER = 0;
    protected int TYPE_NORMAL = 1;
    protected int TYPE_FOOTER = 2;

    protected int layoutId;
    protected int headerId;
    protected int footerId;


    public RecyclerViewAdapter(int layoutId, Context context) {
        this(layoutId, context, null);
    }

    public RecyclerViewAdapter(Context context, List<T> data) {
        this(0, context, data);
    }

    public RecyclerViewAdapter(int layoutId, Context context, List<T> data) {
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
        return ((position == 0 && headerId != 0) || (position == getItemCount() - 1 && footerId != 0));
    }

    public int getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(int layoutId) {
        this.layoutId = layoutId;
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
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (TYPE_HEADER == viewType)
            return new BaseViewHolder(BaseViewHolder.TAG_HEADER, getViewForFlate(headerId, parent));
        if (TYPE_FOOTER == viewType)
            return new BaseViewHolder(BaseViewHolder.TAG_FOOTER, getViewForFlate(footerId, parent));
        return new BaseViewHolder(getViewForFlate(layoutId, parent));
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        onBindView(position, holder, getItem(position));
    }

    public abstract void onBindView(int position, BaseViewHolder holder, T model);


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
        if (-1 != index && getItemCount()> index) {
            data.remove(index);
            notifyItemRemoved(index);
//            notifyItemChanged(index);
        }
    }

    public void removeAfter(int position) {
        int index = getItemPosition(position);
        if (-1 != index && getItemCount()> index)  {
            data = data.subList(0, index);
            notifyItemRemoved(0);
//            notifyItemChanged(0);
        }
    }

    public void updataUI() {
        notifyDataSetChanged();
    }

}

