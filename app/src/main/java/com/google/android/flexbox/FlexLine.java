package com.google.android.flexbox;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class FlexLine {

    FlexLine() {
    }

    int mLeft = Integer.MAX_VALUE;

    int mTop = Integer.MAX_VALUE;

    int mRight = Integer.MIN_VALUE;

    int mBottom = Integer.MIN_VALUE;

    /** @see #getMainSize() */
    int mMainSize;
    int mDividerLengthInMainSize;
    int mCrossSize;
    int mItemCount;
    int mGoneItemCount;
    float mTotalFlexGrow;
    float mTotalFlexShrink;
    int mMaxBaseline;
    int mSumCrossSizeBefore;
    List<Integer> mIndicesAlignSelfStretch = new ArrayList<>();
    int mFirstIndex;
    int mLastIndex;
    boolean mAnyItemsHaveFlexGrow;
    boolean mAnyItemsHaveFlexShrink;
    public int getMainSize() {
        return mMainSize;
    }
    @SuppressWarnings("WeakerAccess")
    public int getCrossSize() {
        return mCrossSize;
    }
    @SuppressWarnings("WeakerAccess")
    public int getItemCount() {
        return mItemCount;
    }
    @SuppressWarnings("WeakerAccess")
    public int getItemCountNotGone() {
        return mItemCount - mGoneItemCount;
    }
    @SuppressWarnings("WeakerAccess")
    public float getTotalFlexGrow() {
        return mTotalFlexGrow;
    }
    @SuppressWarnings("WeakerAccess")
    public float getTotalFlexShrink() {
        return mTotalFlexShrink;
    }
    public int getFirstIndex() {
        return mFirstIndex;
    }
    void updatePositionFromView(View view, int leftDecoration, int topDecoration,
            int rightDecoration, int bottomDecoration) {
        FlexItem flexItem = (FlexItem) view.getLayoutParams();
        mLeft = Math.min(mLeft, view.getLeft() - flexItem.getMarginLeft() - leftDecoration);
        mTop = Math.min(mTop, view.getTop() - flexItem.getMarginTop() - topDecoration);
        mRight = Math.max(mRight, view.getRight() + flexItem.getMarginRight() + rightDecoration);
        mBottom = Math
                .max(mBottom, view.getBottom() + flexItem.getMarginBottom() + bottomDecoration);
    }
}
