package com.yanzhenjie.album;

import android.content.Context;

public interface ItemAction<T> {

    void onAction(Context context, T item);
}
