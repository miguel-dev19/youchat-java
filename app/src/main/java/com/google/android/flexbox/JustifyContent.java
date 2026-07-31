package com.google.android.flexbox;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import androidx.annotation.IntDef;

@IntDef({JustifyContent.FLEX_START, JustifyContent.FLEX_END, JustifyContent.CENTER,
        JustifyContent.SPACE_BETWEEN, JustifyContent.SPACE_AROUND, JustifyContent.SPACE_EVENLY})
@Retention(RetentionPolicy.SOURCE)
public @interface JustifyContent {
    int FLEX_START = 0;
    int FLEX_END = 1;
    int CENTER = 2;
    int SPACE_BETWEEN = 3;
    int SPACE_AROUND = 4;
    int SPACE_EVENLY = 5;
}
