package com.google.android.flexbox;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import androidx.annotation.IntDef;

@IntDef({AlignItems.FLEX_START, AlignItems.FLEX_END, AlignItems.CENTER,
        AlignItems.BASELINE, AlignItems.STRETCH, AlignSelf.AUTO})
@Retention(RetentionPolicy.SOURCE)
public @interface AlignSelf {

    int AUTO = -1;
    int FLEX_START = AlignItems.FLEX_START;
    int FLEX_END = AlignItems.FLEX_END;
    int CENTER = AlignItems.CENTER;
    int BASELINE = AlignItems.BASELINE;
    int STRETCH = AlignItems.STRETCH;
}
