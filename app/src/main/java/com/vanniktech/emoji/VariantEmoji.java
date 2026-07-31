package com.vanniktech.emoji;

import androidx.annotation.NonNull;
import com.vanniktech.emoji.emoji.Emoji;

public interface VariantEmoji {

  @NonNull Emoji getVariant(Emoji desiredEmoji);
  void addVariant(@NonNull Emoji newVariant);
  void persist();
}
