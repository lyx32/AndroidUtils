package com.arraylist7.android.utils.listener;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.arraylist7.android.utils.BitmapUtils;

/**
 * 
 * 
 * 功能：优化滑动加载图片<br>
 * 时间：2015年12月24日<br>
 * 备注：<br>
 * @author ke
 *
 */
public class OnNScrollListener implements OnScrollListener {

	private AbsListView listView;
	private OnScrollListener listener;

	public OnNScrollListener(AbsListView listView) {
		super();
		this.listView = listView;
	}

	public OnNScrollListener(AbsListView listView, OnScrollListener listener) {
		super();
		this.listView = listView;
		this.listener = listener;
	}

	@Override
	public void onScrollStateChanged(AbsListView absListView, int state) {
		if (state == SCROLL_STATE_IDLE || state == SCROLL_STATE_TOUCH_SCROLL) {
			BitmapUtils.getPicasso().resumeTag(listView);
		} else {
			BitmapUtils.getPicasso().pauseTag(listView);
		}
		if (null != listener)
			listener.onScrollStateChanged(absListView, state);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (null != listener)
			listener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
	}

}
