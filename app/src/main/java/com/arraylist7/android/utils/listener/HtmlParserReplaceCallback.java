package com.arraylist7.android.utils.listener;

import android.content.Context;

import com.arraylist7.android.utils.aenum.HtmlParserTypeEnum;

public interface HtmlParserReplaceCallback {

    public CharSequence replaceHtml(Context context, HtmlParserTypeEnum htmlType, String href, String text);
}
