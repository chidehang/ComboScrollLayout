package com.cdh.nestedscrolling.widget;

import android.content.Context;
import android.util.AndroidException;
import android.util.AndroidRuntimeException;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.NestedScrollingParent2;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.cdh.nestedscrolling.R;

/**
 * Created by chidehang on 2020-01-12
 */
public class ComboScrollLayout extends LinearLayout implements NestedScrollingParent2 {

    /** 处理SwipeRefreshLayout嵌套滑动冲突 */
    private SwipeRefreshLayout refreshLayout;
    private View topView;
    private View contentView;

    private int topHeight;

    private NestedScrollingParentHelper parentHelper = new NestedScrollingParentHelper(this);

    public ComboScrollLayout(Context context) {
        this(context, null);
    }

    public ComboScrollLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ComboScrollLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ComboScrollLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setOrientation(LinearLayout.VERTICAL);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 0) {
            topView = getChildAt(0);
        }
        if (getChildCount() > 1) {
            contentView = getChildAt(1);
        }
        if (topView == null || contentView == null) {
            throw new AndroidRuntimeException("容器中至少需要两个子view");
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (topView != null) {
            topHeight = topView.getMeasuredHeight();
        }
        if (refreshLayout == null && getParent() != null && getParent() instanceof SwipeRefreshLayout) {
            refreshLayout = (SwipeRefreshLayout) getParent();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 调整contentView的高度为父容器高度，使之填充布局，避免父容器滚动后出现空白
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        ViewGroup.LayoutParams lp = contentView.getLayoutParams();
        lp.height = getMeasuredHeight();
        contentView.setLayoutParams(lp);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
        if (contentView != null) {
            // 开始滚动前先停止滚动
            if (contentView instanceof RecyclerView) {
                ((RecyclerView) contentView).stopScroll();
            } else if (contentView instanceof NestedScrollView) {
                ((NestedScrollView) contentView).stopNestedScroll();
            } else if (contentView instanceof ViewPager2) {
                ((ViewPager2) contentView).stopNestedScroll();
            }
        }
        topView.stopNestedScroll();

        boolean handled = (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
        // 若为垂直滚动方向，且topView未完全可见，应由StickyScrollLayout处理滑动，禁用SwipeRefreshLayout。
        if (handled && refreshLayout != null && getScrollY() != 0) {
            refreshLayout.setEnabled(false);
        }

        return handled;
    }

    @Override
    public int getNestedScrollAxes() {
        return parentHelper.getNestedScrollAxes();
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, int type) {
        parentHelper.onNestedScrollAccepted(child, target, axes, type);
    }

    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {
        // 滑动结束，恢复SwipeRefreshLayout
        if (refreshLayout != null) {
            refreshLayout.setEnabled(true);
        }
        parentHelper.onStopNestedScroll(target, type);
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        // 向上滑动。若当前topview可见，需要将topview滑动至不可见
        boolean hideTop = dy > 0 && getScrollY() < topHeight;
        // 向下滑动。若contentView滑动至顶，已不可再滑动，且当前topview未完全可见，则将topview滑动至完全可见
        boolean showTop = dy < 0 &&
                getScrollY() > 0 &&
                !ViewCompat.canScrollVertically(target, -1) &&
                !ViewCompat.canScrollVertically(contentView, -1);

        if (hideTop || showTop) {
            scrollBy(0, dy);
            consumed[1] = dy;
        }
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        if (dyUnconsumed > 0) {
            if (target == topView) {
                // 由topView发起的向上滑动，继续将contentView滑动剩余未消耗的偏移量
                scrollBy(0, dyUnconsumed);
            }
        }
    }

    @Override
    public void scrollTo(int x, int y) {
        // 将StickyScrollLayout自身的滚动范围限制在0～topHeight（即在topview完全可见至完全不可见的范围内滑动）
        if (y < 0) {
            y = 0;
        }
        if (y > topHeight) {
            y = topHeight;
        }
        super.scrollTo(x, y);
    }
}
