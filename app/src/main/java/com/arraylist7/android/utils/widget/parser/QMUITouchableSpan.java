package com.arraylist7.android.utils.widget.parser;


import android.support.v4.view.ViewCompat;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;


/**
 * 可 Touch 的 Span，在
 * <p>
 * 提供设置 span 的文字颜色和背景颜色的功能, 在构造时传入
 * </p>
 */
public abstract class QMUITouchableSpan extends ClickableSpan {
    private boolean isPressed =false;
    private TextViewHtmlParser.SpanShowConfig config;


    public abstract void onSpanClick(View widget);

    @Override
    public final void onClick(View widget) {
        if (ViewCompat.isAttachedToWindow(widget)) {
            onSpanClick(widget);
        }
    }

    public QMUITouchableSpan(TextViewHtmlParser.SpanShowConfig config) {
        this.config = config;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(isPressed ? config.pressedTextColor : config.normalTextColor);
        ds.bgColor = isPressed ? config.pressedBackgroundColor : config.normalBackgroundColor;
        ds.setUnderlineText(config.isShowUnderline);
    }


    public void setPressed(boolean isSelected) {
        isPressed = isSelected;
    }

}
