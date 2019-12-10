package com.arraylist7.android.utils.widget.parser;

import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.view.MotionEvent;
import android.widget.TextView;

import com.arraylist7.android.utils.LogUtils;
import com.arraylist7.android.utils.widget.NTextView;

public class QMUILinkTouchDecorHelper {

    private QMUITouchableSpan mPressedSpan;

    public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mPressedSpan = getPressedSpan(textView, spannable, event);
            if (mPressedSpan != null) {
                mPressedSpan.setPressed(true);
                Selection.setSelection(spannable, spannable.getSpanStart(mPressedSpan),
                        spannable.getSpanEnd(mPressedSpan));
            }
            if (textView instanceof NTextView) {
                NTextView tv = (NTextView) textView;
                tv.setTouchSpanHit(mPressedSpan != null);
            }
            return mPressedSpan != null;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            QMUITouchableSpan touchedSpan = getPressedSpan(textView, spannable, event);
            if (mPressedSpan != null && touchedSpan != mPressedSpan) {
                mPressedSpan.setPressed(false);
                mPressedSpan = null;
                Selection.removeSelection(spannable);
            }
            if (textView instanceof NTextView) {
                NTextView tv = (NTextView) textView;
                tv.setTouchSpanHit(mPressedSpan != null);
            }
            return mPressedSpan != null;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            boolean touchSpanHint = false;
            if (mPressedSpan != null) {
                touchSpanHint = true;
                mPressedSpan.setPressed(false);
                mPressedSpan.onClick(textView);
            }

            mPressedSpan = null;
            Selection.removeSelection(spannable);
            if (textView instanceof NTextView) {
                NTextView tv = (NTextView) textView;
                tv.setTouchSpanHit(touchSpanHint);
            }
            return touchSpanHint;
        } else {
            if (mPressedSpan != null) {
                mPressedSpan.setPressed(false);
            }
            if (textView instanceof NTextView) {
                NTextView tv = (NTextView) textView;
                tv.setTouchSpanHit(false);
            }
            Selection.removeSelection(spannable);
            return false;
        }

    }

    public QMUITouchableSpan getPressedSpan(TextView textView, Spannable spannable, MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        x -= textView.getTotalPaddingLeft();
        y -= textView.getTotalPaddingTop();

        x += textView.getScrollX();
        y += textView.getScrollY();

        Layout layout = textView.getLayout();
        int line = layout.getLineForVertical(y);

        /*
         * BugFix: https://issuetracker.google.com/issues/113348914
         */
        try {
            int off = layout.getOffsetForHorizontal(line, x);
            if (x < layout.getLineLeft(line) || x > layout.getLineRight(line)) {
                // 实际上没点到任何内容
                off = -1;
            }
            QMUITouchableSpan[] link = spannable.getSpans(off, off, QMUITouchableSpan.class);
            QMUITouchableSpan touchedSpan = null;
            if (link.length > 0) {
                touchedSpan = link[0];
            }
            return touchedSpan;
        } catch (IndexOutOfBoundsException e) {
            LogUtils.d(this.toString(), "getPressedSpan", e);
        }
        return null;
    }
}

