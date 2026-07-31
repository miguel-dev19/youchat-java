package com.google.android.flexbox;

import android.os.Parcelable;
import android.view.View;

public interface FlexItem extends Parcelable {
    int ORDER_DEFAULT = 1;
    float FLEX_GROW_DEFAULT = 0f;
    float FLEX_SHRINK_DEFAULT = 1f;
    float FLEX_SHRINK_NOT_SET = 0f;
    float FLEX_BASIS_PERCENT_DEFAULT = -1f;
    int MAX_SIZE = Integer.MAX_VALUE & View.MEASURED_SIZE_MASK;
    int getWidth();
    void setWidth(int width);
    int getHeight();
    void setHeight(int height);
    int getOrder();
    void setOrder(int order);
    float getFlexGrow();
    void setFlexGrow(float flexGrow);
    float getFlexShrink();
    void setFlexShrink(float flexShrink);
    @AlignSelf
    int getAlignSelf();
    void setAlignSelf(@AlignSelf int alignSelf);
    int getMinWidth();
    void setMinWidth(int minWidth);
    int getMinHeight();
    void setMinHeight(int minHeight);
    int getMaxWidth();
    void setMaxWidth(int maxWidth);
    int getMaxHeight();
    void setMaxHeight(int maxHeight);
    boolean isWrapBefore();
    void setWrapBefore(boolean wrapBefore);
    float getFlexBasisPercent();
    void setFlexBasisPercent(float flexBasisPercent);
    int getMarginLeft();
    int getMarginTop();
    int getMarginRight();
    int getMarginBottom();
    int getMarginStart();
    int getMarginEnd();
}
