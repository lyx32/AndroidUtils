package com.arraylist7.android.utils.adapter.holder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.arraylist7.android.utils.BitmapUtils;
import com.arraylist7.android.utils.IOUtils;
import com.arraylist7.android.utils.LogUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/4 0004.
 */

public class BaseViewHolder extends RecyclerView.ViewHolder {

    private String tag = "TAG_ITEM";
    public static String TAG_HEADER = "TAG_HEADER";
    public static String TAG_FOOTER = "TAG_FOOTER";
    public static String TAG_ITEM = "TAG_ITEM";
    private Map<String, WeakReference<View>> caches = new HashMap<>();
    private Map<String, WeakReference<Bitmap>> assetsCaches = new HashMap<>();



    public BaseViewHolder(View itemView) {
        this(TAG_ITEM, itemView);
    }

    public BaseViewHolder(String tag, View itemView) {
        super(itemView);
        this.tag = tag;
    }

    public BaseViewHolder(Context context, int layoutId) {
        this(context,TAG_ITEM, layoutId);
    }

    public BaseViewHolder(Context context,String tag, int layoutId) {
        super(LayoutInflater.from(context).inflate(layoutId,null));
        this.tag = tag;
    }

    public View getItemView() {
        return this.itemView;
    }


    public LinearLayout getLinearLayout(int id) {
        return getView(id, LinearLayout.class);
    }

    public RelativeLayout getRelativeLayout(int id) {
        return getView(id, RelativeLayout.class);
    }

    public Button getButton(int id) {
        return getView(id, Button.class);
    }

    public TextView getTextView(int id) {
        return getView(id, TextView.class);
    }

    public ImageView getImageView(int id) {
        return getView(id, ImageView.class);
    }

    public View getView(int id) {
        return getView(id, View.class);
    }

    public <T> T getView(int id, Class<T> c) {
        View view = null;
        WeakReference<View> soft = caches.get(id + "");
        if (null != soft && null != soft.get())
            view = soft.get();
        else {
            view = this.itemView.findViewById(id);
            caches.put(id + "", new WeakReference<View>(view));
        }
        return (T) view;
    }

    public View visibility(int viewId) {
        View view = getView(viewId);
        view.setVisibility(View.VISIBLE);
        return view;
    }

    public View gone(int viewId) {
        View view = getView(viewId);
        view.setVisibility(View.GONE);
        return view;
    }

    public TextView setText(int viewId, String text) {
        TextView textView = getTextView(viewId);
        textView.setText(text);

        return textView;
    }

    public ImageView setImageResource(int viewId, int resId) {
        ImageView imageView = getView(viewId, ImageView.class);
        imageView.setImageResource(resId);
        return imageView;
    }

    public ImageView setImageBitmap(int viewId, Bitmap bitmap) {
        ImageView imageView = getView(viewId, ImageView.class);
        imageView.setImageBitmap(bitmap);
        return imageView;
    }


    public ImageView setImageAssets(int viewId, String fileName) {
        ImageView imageView = getView(viewId, ImageView.class);
        synchronized (imageView) {
            WeakReference<Bitmap> soft = assetsCaches.get(viewId + "_" + fileName);
            if (null != soft && null != soft.get()) {
                LogUtils.i("setImageAssets(" + viewId + ",\"" + fileName + "\") reader cache!");
                imageView.setImageBitmap(soft.get());
            } else {
                LogUtils.i("setImageAssets(" + viewId + ",\"" + fileName + "\") no cache load assets file !");
                InputStream in = null;
                try {
                    in = itemView.getContext().getAssets().open(fileName);
                    Bitmap bitmap = BitmapFactory.decodeStream(in);
                    imageView.setImageBitmap(bitmap);
                    assetsCaches.remove(viewId + "_" + fileName);
                    assetsCaches.put(viewId + "_" + fileName, new WeakReference<Bitmap>(bitmap));
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    IOUtils.close(in);
                }
            }
        }
        return imageView;
    }

    public ImageView loadBitmap(int viewId, String url) {
        return loadBitmap(viewId, url, 0, 0);
    }

    public ImageView loadBitmap(int viewId, String url, int width, int height) {
        ImageView imageView = getImageView(viewId);
        BitmapUtils.loadBitmap(url, width, height, imageView);
        return imageView;
    }

    public String getTag() {
        return tag;
    }

    public boolean isHeader() {
        return tag.equals(TAG_HEADER);
    }

    public boolean isFooter() {
        return tag.equals(TAG_FOOTER);
    }

    public boolean isItem() {
        return tag.equals(TAG_ITEM);
    }
}
