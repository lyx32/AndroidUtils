package com.arraylist7.android.utils.demo.ui;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;

import com.arraylist7.android.utils.IntentUtils;
import com.arraylist7.android.utils.LogUtils;
import com.arraylist7.android.utils.UiUtils;
import com.arraylist7.android.utils.ViewUtils;
import com.arraylist7.android.utils.annotation.Views;
import com.arraylist7.android.utils.demo.App;
import com.arraylist7.android.utils.demo.R;
import com.arraylist7.android.utils.demo.base.Base;
import com.arraylist7.android.utils.listener.HtmlParserListenerAdapter;
import com.arraylist7.android.utils.widget.NTextView;

public class Html extends Base {

    @Views(R.id.ui_html_linear)
    private LinearLayout linear;
    @Views(R.id.ui_html_textview1)
    private NTextView textView1;


    public static void instance(Activity from, boolean finish) {
        IntentUtils.activity(from, Html.class, finish);
    }

    @Override
    public int getLayoutId() {
        return R.layout.ui_html;
    }

    @Override
    public void initWidget() {
        ViewUtils.inject(this);
    }

    @Override
    public void initData() {
        textView1.setHtmlText(
                "测试一哈<a href='tel:13688419729'>给我打电话</a>,<a href='mailto:123456789@qq.com'>999@qq.com</a>999@qq.com,          " +
                        "<a href=\"/%E5%BC%A0%E5%AE%B6%E5%8F%A3%E5%B8%82\">#测试话题#</a>" +
                        "<a href=\"/%E5%BC%A0%E5%AE%B6%E5%8F%A3%E5%B8%82\" title=\"张家口市\">@张姐姐</a>" +
                        "<img src='http://www.sina.com/1.jpg' alt='新上传的图片'/>");
    }

    @Override
    public void initListener() {
        textView1.setHtmlParserListener(new HtmlParserListenerAdapter() {
            @Override
            public void onLinkClick(String href, String text) {
                UiUtils.showLong(App.getContext(), "我点击了链接！" + text);
            }

            @Override
            public void onTelephoneClick(String href, String text) {
                UiUtils.showLong(App.getContext(), "我点击了电话！" + text);
            }

            @Override
            public void onEmailClick(String href, String text) {
                UiUtils.showLong(App.getContext(), "我点击了Email！" + text);
            }

            @Override
            public void onAtuserClick(String href, String text) {
                UiUtils.showLong(App.getContext(), "我点击了@！" + text);
            }

            @Override
            public void onTopicClick(String href, String text) {
                UiUtils.showLong(App.getContext(), "我点击了话题！" + text);
            }

            @Override
            public void onImageClick(String src, String alt) {
                UiUtils.showLong(App.getContext(), "我点击了图片！" + alt);
            }
        });

        linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UiUtils.showShort(App.getContext(), "11111111");
            }
        });
    }
}
