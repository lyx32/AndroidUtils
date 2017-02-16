package com.arraylist7.android.utils.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.Button;

import com.arraylist7.android.utils.widget.v4.SwipeProgressBar;


@SuppressLint("InlinedApi")
public class ProgressButton extends Button {

	private int mProgressBarHeight = 5;
	private SwipeProgressBar mProgressBar;
	private boolean mRefreshing = false;

	public ProgressButton(Context context) {
		this(context, null, 0);
	}

	public ProgressButton(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ProgressButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		if (!isInEditMode()) {
			mProgressBar = new SwipeProgressBar(this);
		}
		setColorSchemeResources(android.R.color.holo_blue_light,android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
		
	}

	public void setRefreshing(boolean refreshing) {
		if (mRefreshing != refreshing) {
			mRefreshing = refreshing;
			if (mRefreshing) {
				mProgressBar.start();
			} else {
				mProgressBar.stop();
			}
		}
	}

	public void setColorSchemeResources(int colorRes1, int colorRes2, int colorRes3, int colorRes4) {
		final Resources res = getResources();
		setColorSchemeColors(res.getColor(colorRes1), res.getColor(colorRes2), res.getColor(colorRes3),
				res.getColor(colorRes4));
	}

	public void setColorSchemeColors(int color1, int color2, int color3, int color4) {
		mProgressBar.setColorScheme(color1, color2, color3, color4);
	}

	public boolean isRefreshing() {
		return mRefreshing;
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		if (mProgressBar != null) {
			mProgressBar.draw(canvas);
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		if (mProgressBar != null) {
			mProgressBar.setBounds(0, getHeight()-mProgressBarHeight, getWidth(), getHeight());
		}
	}

	public int getmProgressBarHeight() {
		return mProgressBarHeight;
	}

	public void setmProgressBarHeight(int mProgressBarHeight) {
		this.mProgressBarHeight = mProgressBarHeight;
	}
}


