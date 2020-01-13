package com.cdh.nestedscrolling.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.Scroller;

import androidx.annotation.Nullable;
import androidx.core.view.NestedScrollingChild2;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.ViewCompat;

/**
 * Created by chidehang on 2020-01-12
 */
public class ComboChildLayout extends LinearLayout implements NestedScrollingChild2 {

    private int orientation;
    // touch滑动相关参数
    private int lastX = -1, lastY = -1;
    private final int[] offset = new int[2];
    private final int[] consumed = new int[2];

    // fling滑动相关参数
    private boolean isFling;
    private final int minFlingVelocity, maxFlingVelocity;
    private Scroller scroller;
    private VelocityTracker velocityTracker;
    private int lastFlingX, lastFlingY;
    private final int[] flingConsumed = new int[2];

    private NestedScrollingChildHelper childHelper = new NestedScrollingChildHelper(this);

    public ComboChildLayout(Context context) {
        this(context, null);
    }

    public ComboChildLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ComboChildLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ComboChildLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        orientation = getOrientation();
        setNestedScrollingEnabled(true);
        // 获取当前页面配置信息
        ViewConfiguration config = ViewConfiguration.get(context);
        // 设置系统默认最小和最大加速度
        minFlingVelocity = config.getScaledMinimumFlingVelocity();
        maxFlingVelocity = config.getScaledMaximumFlingVelocity();
        scroller = new Scroller(context);
    }

    @Override
    public boolean startNestedScroll(int axes, int type) {
        return childHelper.startNestedScroll(axes, type);
    }

    @Override
    public void stopNestedScroll(int type) {
        childHelper.stopNestedScroll(type);
    }

    @Override
    public boolean hasNestedScrollingParent(int type) {
        return childHelper.hasNestedScrollingParent(type);
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable int[] offsetInWindow, int type) {
        if (orientation == VERTICAL) {
            dxUnconsumed = 0;
        } else {
            dyUnconsumed = 0;
        }
        return childHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, @Nullable int[] consumed, @Nullable int[] offsetInWindow, int type) {
        if (orientation == VERTICAL) {
            dx = 0;
        } else {
            dy = 0;
        }
        return childHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type);
    }

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        childHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return childHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        cancelFling();
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }
        // 追踪触摸点移动加速度
        velocityTracker.addMovement(event);

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastX = (int) event.getX();
                lastY = (int) event.getY();
                // 通知parent根据滑动方向和滑动类型进行启用嵌套滑动
                if (orientation == VERTICAL) {
                    startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_TOUCH);
                } else {
                    startNestedScroll(ViewCompat.SCROLL_AXIS_HORIZONTAL, ViewCompat.TYPE_TOUCH);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                int curX = (int) event.getX();
                int curY = (int) event.getY();
                // 计算滑动偏移量，起始坐标-当前坐标
                int dx = lastX - curX;
                int dy = lastY - curY;

                // 优先将滑动偏移量交由parent处理，
                if (dispatchNestedPreScroll(dx, dy, consumed, offset, ViewCompat.TYPE_TOUCH)) {
                    // 滑动偏移量减去parent消耗的量
                    dx -= consumed[0];
                    dy -= consumed[1];
                }

                int consumedX = 0;
                int consumedY = 0;
                // 自身或child处理滑动偏移
                if (orientation == VERTICAL) {
                    consumedY = childConsumedY(consumedY);
                } else {
                    consumedX = childConsumedX(consumedX);
                }

                // 滑动偏移量减去自身或child消耗的量，然后再交由parent处理
                dispatchNestedScroll(consumedX, consumedY, dx-consumedX, dy-consumedY, null, ViewCompat.TYPE_TOUCH);

                lastX = curX;
                lastY = curY;
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // 通知parent滑动结束
                stopNestedScroll(ViewCompat.TYPE_TOUCH);

                if (velocityTracker != null) {
                    // 计算触摸点加速度
                    velocityTracker.computeCurrentVelocity(1000, maxFlingVelocity);
                    // 获取xy轴加速度
                    int vx = (int) velocityTracker.getXVelocity();
                    int vy = (int) velocityTracker.getYVelocity();
                    fling(vx, vy);
                    velocityTracker.clear();
                }

                lastX = -1;
                lastY = -1;
                break;

            default:
                break;
        }

        return true;
    }

    private boolean fling(int velocityX, int velocityY) {
        if (Math.abs(velocityX) < minFlingVelocity && Math.abs(velocityY) < minFlingVelocity) {
            // 加速度过小，则不进行fling
            return false;
        }

        // 通知parent根据滑动方向和滑动类型进行启用嵌套滑动
        if (orientation == VERTICAL) {
            startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_NON_TOUCH);
        } else {
            startNestedScroll(ViewCompat.SCROLL_AXIS_HORIZONTAL, ViewCompat.TYPE_NON_TOUCH);
        }

        // 限制加速度值范围不超过maxFlingVelocity
        velocityX = Math.max(-maxFlingVelocity, Math.min(velocityX, maxFlingVelocity));
        velocityY = Math.max(-maxFlingVelocity, Math.min(velocityY, maxFlingVelocity));
        doFling(velocityX, velocityY);
        return true;
    }

    private void doFling(int velocityX, int velocityY) {
        isFling = true;
        // 将加速度值交由scroller计算
        scroller.fling(0, 0, velocityX, velocityY, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
        // 触发执行computeScroll()
        postInvalidate();
    }

    @Override
    public void computeScroll() {
        if (isFling && scroller.computeScrollOffset()) {
            // 获取scroller计算出的当前滚动距离
            int x = scroller.getCurrX();
            int y = scroller.getCurrY();
            // 计算滚动偏移量，起始坐标-当前坐标
            int dx = lastFlingX - x;
            int dy = lastFlingY - y;
            lastFlingX = x;
            lastFlingY = y;

            // 处理消耗滚动偏移量逻辑同ACTION_MOVE（触摸类型为非用户触摸）
            if (dispatchNestedPreScroll(dx, dy, flingConsumed, null, ViewCompat.TYPE_NON_TOUCH)) {
                dx -= flingConsumed[0];
                dy -= flingConsumed[1];
            }

            int flingX = 0;
            int flingY = 0;
            // 自身或子view处理fling
            if (orientation == VERTICAL) {
                flingX = childFlingX(dx);
            } else {
                flingY = childFlingY(dy);
            }

            dispatchNestedScroll(flingX, flingY, dx-flingX, dy-flingY, null, ViewCompat.TYPE_NON_TOUCH);

            // 触发再次执行computeScroll()
            postInvalidate();
        } else {
            stopNestedScroll(ViewCompat.TYPE_NON_TOUCH);
            cancelFling();
        }
    }

    protected boolean canScroll() {
        return true;
    }

    protected void cancelFling() {
        isFling = false;
        lastFlingX = 0;
        lastFlingY = 0;
    }

    /**
     * 进行fling
     * @param dx 可以滚动的偏移量
     * @return 实际滚动消耗的偏移量
     */
    protected int childFlingX(int dx) {
        return 0;
    }

    /**
     * 进行fling
     * @param dy 可以滚动的偏移量
     * @return 实际滚动消耗的偏移量
     */
    protected int childFlingY(int dy) {
        return 0;
    }

    /**
     * 进行滚动
     * @param dx 可以滚动的偏移量
     * @return 实际滚动消耗的偏移量
     */
    protected int childConsumedX(int dx) {
        return 0;
    }

    /**
     * 进行滚动
     * @param dy 可以滚动的偏移量
     * @return 实际滚动消耗的偏移量
     */
    protected int childConsumedY(int dy) {
        return 0;
    }
}
