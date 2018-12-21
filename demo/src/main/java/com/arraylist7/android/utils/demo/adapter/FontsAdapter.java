package com.arraylist7.android.utils.demo.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.arraylist7.android.utils.BitmapUtils;
import com.arraylist7.android.utils.adapter.NListAdapter;
import com.arraylist7.android.utils.adapter.RecyclerViewAdapter;
import com.arraylist7.android.utils.adapter.holder.BaseViewHolder;
import com.arraylist7.android.utils.demo.R;
import com.arraylist7.android.utils.demo.model.DemoModel;

public class FontsAdapter extends NListAdapter<DemoModel> {

    public FontsAdapter(int layoutId, Context context) {
        super(layoutId, context);
    }

    @Override
    public void bindData(int position, View convertView, ViewGroup parent, DemoModel model) {
        setText(R.id.ui_main_item_id,model.id);
        setText(R.id.ui_main_item_name,model.name);
        setText(R.id.ui_main_item_datetime,model.dateTime);
        setImage(R.id.ui_main_item_img,model.picUrl);
        // BitmapUtils.loadBitmap(model.picUrl,getImageView(R.id.ui_main_item_img));
    }
}
