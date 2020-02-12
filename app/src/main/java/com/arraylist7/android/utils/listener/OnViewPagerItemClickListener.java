package com.arraylist7.android.utils.listener;

import com.arraylist7.android.utils.adapter.ViewPagerAdapter;

public interface OnViewPagerItemClickListener<T> {

    public void onItemClick(ViewPagerAdapter<T> adapter, T model, int position);
}
