package com.cdh.nestedscrolling.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * Created by chidehang on 2020-01-13
 */
public class ComboSwipeRefreshLayout extends SwipeRefreshLayout {

    private float lastX;
    private float lastY;
    private boolean isHorizontalMove;

    private int touchSlop;

    public ComboSwipeRefreshLayout(@NonNull Context context) {
        this(context, null);
    }

    public ComboSwipeRefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                isHorizontalMove = false;
                lastX = ev.getX();
                lastY = ev.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                // 若当前处于水平滑动，则不拦截事件，由child处理
                if (isHorizontalMove) {
                    return false;
                }

                float dx = Math.abs(ev.getX() - lastX);
                float dy = Math.abs(ev.getY() - lastY);
                // 若水平滑动量大于垂直滑动，则标记为水平滑动，不拦截事件
                if (dx > touchSlop && dx > dy) {
                    isHorizontalMove = true;
                    return false;
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isHorizontalMove = false;
                break;

            default:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }
}
