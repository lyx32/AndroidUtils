package com.arraylist7.android.utils.widget;

import android.app.Activity;
import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatEditText;

import com.arraylist7.android.utils.LogUtils;
import com.arraylist7.android.utils.OtherUtils;
import com.arraylist7.android.utils.StringUtils;

/**
 * Created by Administrator on 2017/11/23 0023.
 */

public class NEditText extends AppCompatEditText {
    public NEditText(Context context) {
        this(context, null);
    }

    public NEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 这里为了避免没有设置setOnSubmitListener时，软键盘不消失的bug
        setOnSubmitListener(null,null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (MotionEvent.ACTION_UP == event.getAction()) {
            setFocusAndSelectAll();
        }
        return super.onTouchEvent(event);
    }

    public void setFocus() {
        this.setFocusableInTouchMode(true);
        this.requestFocus();
        this.setSelection(getText().length());
    }

    public void setFocusAndSelectAll() {
        this.setFocusableInTouchMode(true);
        this.requestFocus();
        this.setSelectAllOnFocus(true);
        this.selectAll();
    }

    public void setNextView(final View nextView) {
        this.setOnSubmitListener(new int[]{EditorInfo.IME_ACTION_NEXT}, new OnClickListener() {
            @Override
            public void onClick(View v) {
                nextView.setFocusableInTouchMode(true);
                nextView.requestFocus();
            }
        });
    }

    public void setNextView(Activity activity, int nextViewId) {
        setNextView(activity.findViewById(nextViewId));
    }

    public void setNextView(View rootView, int nextViewId) {
        setNextView(rootView.findViewById(nextViewId));
    }

    public void setOnSubmitListener(final OnClickListener listener) {
        // 如果是在未弹出键盘时触发了setOnEditorActionListener，在某些pda上actionId会以IME_ACTION_UNSPECIFIED来触发，并且会触发多次
        setOnSubmitListener(new int[]{EditorInfo.IME_ACTION_DONE, EditorInfo.IME_ACTION_GO,
                EditorInfo.IME_ACTION_SEARCH, EditorInfo.IME_ACTION_SEND, EditorInfo.IME_ACTION_UNSPECIFIED}, listener);
    }

    private Long time = 1L;

    public void setOnSubmitListener(final int[] actions, final OnClickListener listener) {
        this.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (StringUtils.isNullOrEmpty(actions) || null == listener) {
                    OtherUtils.hideKeyboard(getContext(), NEditText.this);
                    return false;
                }
                if (EditorInfo.IME_ACTION_UNSPECIFIED == actionId) {
                    synchronized (NEditText.this) {
                        LogUtils.d("(System.currentTimeMillis() - time)=" + (System.currentTimeMillis() - time));
                        if ((System.currentTimeMillis() - time) < 300)
                            return true;
                    }
                    time = System.currentTimeMillis();
                }
                for (int action : actions) {
                    if (actionId == action) {
                        if (null != listener) {
                            listener.onClick(v);
                            if (action != EditorInfo.IME_ACTION_NEXT) {
                                OtherUtils.hideKeyboard(getContext(), NEditText.this);
                            }
                            return true;
                        }
                    }
                }
                OtherUtils.hideKeyboard(getContext(), NEditText.this);
                return false;
            }
        });
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
}
