package com.arraylist7.android.utils.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.arraylist7.android.utils.BitmapUtils;
import com.arraylist7.android.utils.R;
import com.arraylist7.android.utils.StringUtils;

public class NetworkImageView extends ImageView {
	
	public NetworkImageView(Context context) {
		this(context, null);
	}

	public NetworkImageView(Context context, AttributeSet attrs) {
		super(context, attrs, 0);
	}

	public NetworkImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		super.setScaleType(ScaleType.CENTER_CROP);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NetworkImageView, defStyleAttr, 0);
		String url = a.getString(R.styleable.NetworkImageView_url);
		int preview = a.getResourceId(R.styleable.NetworkImageView_preview, -1);
		a.recycle();
		if (-1 != preview)
			this.setImageResource(preview);
		if(!StringUtils.isNullOrEmpty(url))
			setUrl(url);
	}

	public synchronized void setUrl(String url) {
		BitmapUtils.loadBitmap(url, this);
	}

}
