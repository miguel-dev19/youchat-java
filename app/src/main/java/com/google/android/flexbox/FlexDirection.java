package com.google.android.flexbox;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

@IntDef({FlexDirection.ROW, FlexDirection.ROW_REVERSE, FlexDirection.COLUMN,
        FlexDirection.COLUMN_REVERSE})
@Retention(RetentionPolicy.SOURCE)
public @interface FlexDirection {

    int ROW = 0;
    int ROW_REVERSE = 1;
    int COLUMN = 2;
    int COLUMN_REVERSE = 3;
}
