package com.vanniktech.emoji.emoji;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

public interface EmojiCategory {
  @NonNull Emoji[] getEmojis();
  @DrawableRes int getIcon();
  @StringRes int getCategoryName();
}
