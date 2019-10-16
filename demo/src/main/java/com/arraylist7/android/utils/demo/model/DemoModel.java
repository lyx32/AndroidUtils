package com.arraylist7.android.utils.demo.model;

import com.arraylist7.android.utils.annotation.DataBind;
import com.arraylist7.android.utils.annotation.LayoutBind;
import com.arraylist7.android.utils.demo.R;

@LayoutBind(R.layout.ui_main_item)
public class DemoModel {
    @DataBind(setText = R.id.ui_main_item_id)
    public String id;
    @DataBind(setText = R.id.ui_main_item_name)
    public String name;
    @DataBind(setText = R.id.ui_main_item_datetime, setTag = R.id.ui_main_item_datetime)
    public String dateTime;
    @DataBind(setImage = R.id.ui_main_item_img)
    public String picUrl;
}
