package com.arraylist7.android.utils.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.arraylist7.android.utils.LogUtils;
import com.arraylist7.android.utils.OtherUtils;
import com.arraylist7.android.utils.StringUtils;

/**
 * Created by Administrator on 2017/11/23 0023.
 */

@SuppressLint("AppCompatCustomView")
public class NEditText extends EditText {
    public NEditText(Context context) {
        this(context, null);
    }

    public NEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (MotionEvent.ACTION_UP == event.getAction()) {
            this.setFocusableInTouchMode(true);
            this.requestFocus();
            this.setSelectAllOnFocus(true);
            this.selectAll();
        }
        return super.onTouchEvent(event);
    }

    public void setFocus() {
        this.setFocusableInTouchMode(true);
        this.requestFocus();
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
        setOnSubmitListener(new int[]{EditorInfo.IME_ACTION_DONE, EditorInfo.IME_ACTION_GO,
                EditorInfo.IME_ACTION_SEARCH, EditorInfo.IME_ACTION_SEND, EditorInfo.IME_ACTION_UNSPECIFIED}, listener);
    }

    private Long time = 1L;

    public void setOnSubmitListener(final int[] actions, final OnClickListener listener) {
        this.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // 如果是在未弹出键盘时触发了setOnEditorActionListener，在某些pda上actionId会以IME_ACTION_UNSPECIFIED来触发，并且会触发多次
                LogUtils.d("触发actionId=" + actionId);
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
                return false;
            }
        });
    }
}