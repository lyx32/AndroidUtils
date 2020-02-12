package com.arraylist7.android.utils.demo.ui;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.arraylist7.android.utils.IntentUtils;
import com.arraylist7.android.utils.StatusBarUtils;
import com.arraylist7.android.utils.StringUtils;
import com.arraylist7.android.utils.ViewUtils;
import com.arraylist7.android.utils.annotation.Views;
import com.arraylist7.android.utils.demo.R;
import com.arraylist7.android.utils.demo.adapter.FontsAdapter;
import com.arraylist7.android.utils.demo.base.Base;
import com.arraylist7.android.utils.demo.model.DemoModel;
import com.arraylist7.android.utils.widget.NEditText;

import java.util.ArrayList;
import java.util.List;

public class Fonts extends Base {


    @Views(R.id.ui_fonts_textView1)
    private TextView textView1;
    @Views(R.id.ui_fonts_editText_1)
    private NEditText editText1;
    @Views(R.id.ui_fonts_button1)
    private Button button1;
    @Views(R.id.ui_fonts_recyclerView1)
    private ListView recyclerView1;


    private FontsAdapter adapter = null;



    public static void instance(Activity from, boolean finish) {
        IntentUtils.activity(from, Fonts.class, finish);
    }


    @Override
    public int getLayoutId() {
        return R.layout.ui_fonts;
    }


    @Override
    public void initWidget() {
        ViewUtils.inject(this);
        StatusBarUtils.setTranslucent(activity,true);
        getSupportActionBar().setTitle("字体");
        getSupportActionBar().setSubtitle("显示设置字体之后的效果");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        recyclerView1.setAdapter(adapter = new FontsAdapter(R.layout.ui_main_item, this));
    }

    @Override
    public void initData() {
        DemoModel model = null;
        List<DemoModel> list = new ArrayList<>();
        String[] img = new String[]{
                "https://assets-cdn.github.com/images/modules/site/logos/airbnb-logo.png",
                "https://assets-cdn.github.com/images/modules/site/logos/sap-logo.png",
                "https://assets-cdn.github.com/images/modules/site/logos/ibm-logo.png",
                "https://assets-cdn.github.com/images/modules/site/logos/google-logo.png",
                "https://assets-cdn.github.com/images/modules/site/logos/paypal-logo.png",
                "https://assets-cdn.github.com/images/modules/site/logos/bloomberg-logo.png",
                "https://assets-cdn.github.com/images/modules/site/logos/spotify-logo.png",
                "https://assets-cdn.github.com/images/modules/site/logos/swift-logo.png",
                "https://assets-cdn.github.com/images/modules/site/logos/facebook-logo.png",
                "https://assets-cdn.github.com/images/modules/site/logos/node-logo.png",
                "https://assets-cdn.github.com/images/modules/site/logos/nasa-logo.png",
                "https://assets-cdn.github.com/images/modules/site/logos/walmart-logo.png"
        };
        for (int i = 1; i < 100; i++) {
            model = new DemoModel();
            model.id = i + "";
            model.name = "name-" + i;
            model.dateTime = StringUtils.getDateTimeNow("yyyy-MM-dd HH:mm:ss.SSS");
            model.picUrl = img[i % img.length];
            list.add(model);
            model = null;
        }
        adapter.addData(list);
        adapter.updateUI();
    }

    @Override
    public void initListener() {
    }
}
