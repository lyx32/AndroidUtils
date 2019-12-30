package com.arraylist7.android.utils.demo.adapter;

import android.content.Context;

import com.arraylist7.android.utils.adapter.RecyclerViewAdapter;
import com.arraylist7.android.utils.adapter.holder.BaseViewHolder;
import com.arraylist7.android.utils.demo.R;
import com.arraylist7.android.utils.demo.model.DemoModel;

public class DemoAdapter extends RecyclerViewAdapter<DemoModel> {

    public DemoAdapter(int layoutId, Context context) {
        super(layoutId, context);
    }

    @Override
    public void onBindView(int position, BaseViewHolder holder, DemoModel model) {
        holder.setText(R.id.ui_main_item_id,model.id);
        holder.setText(R.id.ui_main_item_name,model.name);
        holder.setText(R.id.ui_main_item_datetime,model.dateTime);
//        BitmapUtils.loadBitmap(model.picUrl,holder.getImageView(R.id.ui_main_item_img));
    }
}
