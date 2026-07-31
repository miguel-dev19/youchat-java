package com.google.android.flexbox;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import androidx.annotation.IntDef;
@IntDef({FlexWrap.NOWRAP, FlexWrap.WRAP, FlexWrap.WRAP_REVERSE})
@Retention(RetentionPolicy.SOURCE)
public @interface FlexWrap {
    int NOWRAP = 0;
    int WRAP = 1;
    int WRAP_REVERSE = 2;
}
