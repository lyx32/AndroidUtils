package com.arraylist7.android.utils.listener;

import com.arraylist7.android.utils.adapter.RecyclerViewAdapter;

public interface OnRecyclerViewItemClickListener<T> {

    public void onItemClick(RecyclerViewAdapter<T> adapter, T model, int containsHeaderPosition, int onlyItemPosition);
}
