package org.peace.savingtracker.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by peacepassion on 15/10/14.
 */
public class IconListLayout extends FrameLayout {

  private int childWidth = 0;
  private int height = 0;

  public IconListLayout(Context context) {
    this(context, null);
  }

  public IconListLayout(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public IconListLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    childWidth = w / getChildCount();
    height = h;
  }

  @Override protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    for (int i = 0, count = getChildCount(), childL = getPaddingLeft(); i < count; ++i) {
      View child = getChildAt(i);
      int cw = child.getMeasuredWidth(), ch = child.getMeasuredHeight();
      int l = childL + (childWidth - cw) / 2;
      int r = l + cw;
      int t = getPaddingTop() + (height - ch) / 2;
      int b = t + ch;
      child.layout(l, t, r, b);
      childL += childWidth;
    }
  }
}
