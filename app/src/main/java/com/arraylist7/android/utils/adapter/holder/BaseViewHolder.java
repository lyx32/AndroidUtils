package com.arraylist7.android.utils.adapter.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/4 0004.
 */

public class BaseViewHolder extends RecyclerView.ViewHolder {

    private Map<String, View> caches = new HashMap<>();

    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    public View getItemView() {
        return this.itemView;
    }


    public LinearLayout findLinearLayoutById(int id) {
        return findViewById(id, LinearLayout.class);
    }

    public RelativeLayout findRelativeLayoutById(int id) {
        return findViewById(id, RelativeLayout.class);
    }

    public Button findButtonById(int id) {
        return findViewById(id, Button.class);
    }

    public TextView findTextViewById(int id) {
        return findViewById(id, TextView.class);
    }

    public ImageView findImageViewById(int id) {
        return findViewById(id, ImageView.class);
    }

    public View findViewById(int id) {
        return findViewById(id,View.class);
    }

    public <T> T findViewById(int id, Class c) {
        if (!caches.containsKey(id + "")) {
            View view = this.itemView.findViewById(id);
            caches.put(id + "", view);
        }
        return (T)caches.get(id+"");
    }

}
