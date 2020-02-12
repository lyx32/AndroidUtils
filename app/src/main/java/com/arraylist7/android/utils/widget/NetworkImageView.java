package com.arraylist7.android.utils.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.AppCompatImageView;

import com.arraylist7.android.utils.BitmapUtils;
import com.arraylist7.android.utils.LogUtils;
import com.arraylist7.android.utils.R;
import com.arraylist7.android.utils.StringUtils;

public class NetworkImageView extends AppCompatImageView {

    private String url;

    public NetworkImageView(Context context) {
        this(context, null);
    }

    public NetworkImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NetworkImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NetworkImageView, defStyleAttr, 0);
        url = a.getString(R.styleable.NetworkImageView_url);
        a.recycle();
        setUrl(url);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String urlOrPath) {
        ViewGroup.LayoutParams lp = this.getLayoutParams();
        int w = 0;
        int h = 0;
        if (null != lp) {
            w = lp.width;
            h = lp.height;
        }
        setUrl(urlOrPath, w, h);
    }

    public void setUrl(String urlOrPath, int width, int height) {
        this.url = urlOrPath;
        if (!StringUtils.isNullOrEmpty(url)) {
            if (null == BitmapUtils.getPicasso()) {
                BitmapUtils.init(getContext());
            }
            if (!StringUtils.in(width, 0, -1, -2) && !StringUtils.in(height, 0, -1, -2))
                BitmapUtils.loadBitmap(url, width, height, this);
            else
                BitmapUtils.loadBitmap(url, this);
        }
    }

    public void setUrl(Uri uri) {
        setUrl(uri.getPath());
    }
}
