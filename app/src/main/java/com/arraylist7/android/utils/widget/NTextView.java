package com.arraylist7.android.utils.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.AppCompatTextView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.method.Touch;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

import com.arraylist7.android.utils.listener.HtmlParserListener;
import com.arraylist7.android.utils.listener.HtmlParserListenerAdapter;
import com.arraylist7.android.utils.widget.parser.QMUILinkTouchDecorHelper;
import com.arraylist7.android.utils.widget.parser.TextViewHtmlParser;

public class NTextView extends AppCompatTextView {


    private TextViewHtmlParser defaultTextViewHtmlParser = new TextViewHtmlParser();
    private HtmlParserListener htmlParserListener = new HtmlParserListenerAdapter();


    /**
     * 记录当前 Touch 事件对应的点是不是点在了 span 上面
     */
    protected boolean mTouchSpanHit;

    /**
     * 记录每次真正传入的press，每次更改mTouchSpanHint，需要再调用一次setPressed，确保press状态正确
     */
    private boolean mIsPressedRecord = false;
    /**
     * TextView是否应该消耗事件
     */
    private boolean mNeedForceEventToParent = false;

    public NTextView(Context context) {
        super(context);
        init(context);
    }

    public NTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        setHighlightColor(Color.TRANSPARENT);
        setMovementMethod(new LinkMovementMethod() {
            private QMUILinkTouchDecorHelper sHelper = new QMUILinkTouchDecorHelper();

            @Override
            public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
                return sHelper.onTouchEvent(widget, buffer, event) || Touch.onTouchEvent(widget, buffer, event);
            }
        });
        setNeedForceEventToParent(true);
    }


    /**
     * 设置文字前景色
     *
     * @param startIndex
     * @param endIndex
     * @param color
     */
    public void setTextForegroundColor(int startIndex, int endIndex, int color) {
        SpannableString spannableString = new SpannableString(getText());
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(color);
        spannableString.setSpan(colorSpan, startIndex, endIndex, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        setText(spannableString);
    }

    /**
     * 设置文字背景色
     *
     * @param startIndex
     * @param endIndex
     * @param color
     */
    public void setTextBackgroundColor(int startIndex, int endIndex, int color) {
        SpannableString spannableString = new SpannableString(getText());
        BackgroundColorSpan colorSpan = new BackgroundColorSpan(color);
        spannableString.setSpan(colorSpan, startIndex, endIndex, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        setText(spannableString);
    }


    public void setNeedForceEventToParent(boolean needForceEventToParent) {
        mNeedForceEventToParent = needForceEventToParent;
        setFocusable(!needForceEventToParent);
        setClickable(!needForceEventToParent);
        setLongClickable(!needForceEventToParent);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!(getText() instanceof Spannable)) {
            return super.onTouchEvent(event);
        }
        mTouchSpanHit = true;
        // 调用super.onTouchEvent,会走到QMUILinkTouchMovementMethod
        // 会走到QMUILinkTouchMovementMethod#onTouchEvent会修改mTouchSpanHint
        boolean ret = super.onTouchEvent(event);
        if (mNeedForceEventToParent) {
            return mTouchSpanHit;
        }
        return ret;
    }

    public void setTouchSpanHit(boolean hit) {
        if (mTouchSpanHit != hit) {
            mTouchSpanHit = hit;
            setPressed(mIsPressedRecord);
        }
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean performClick() {
        if (!mTouchSpanHit && !mNeedForceEventToParent) {
            return super.performClick();
        }
        return false;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean performLongClick() {
        if (!mTouchSpanHit && !mNeedForceEventToParent) {
            return super.performLongClick();
        }
        return false;
    }

    @Override
    public final void setPressed(boolean pressed) {
        mIsPressedRecord = pressed;
        if (!mTouchSpanHit) {
            onSetPressed(pressed);
        }
    }

    protected void onSetPressed(boolean pressed) {
        super.setPressed(pressed);
    }


    public void setHtmlText(String htmlText) {
        setHtmlText(htmlText, htmlParserListener);
    }

    public void setHtmlText(String htmlText, HtmlParserListener listener) {
        setText(defaultTextViewHtmlParser.parse(getContext(), htmlText, listener));
    }

    public void setHtmlParserListener(HtmlParserListener htmlParserListener) {
        this.htmlParserListener = htmlParserListener;
    }

    public TextViewHtmlParser getDefaultTextViewHtmlParser() {
        return defaultTextViewHtmlParser;
    }

    public void setDefaultTextViewHtmlParser(TextViewHtmlParser defaultTextViewHtmlParser) {
        this.defaultTextViewHtmlParser = defaultTextViewHtmlParser;
    }
}
