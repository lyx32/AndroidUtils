package com.arraylist7.android.utils.listener;

import com.arraylist7.android.utils.aenum.HtmlParserTypeEnum;

public interface HtmlParserReplaceCallback {

    public CharSequence replaceHtml(HtmlParserTypeEnum htmlType, String href, String text);
}
