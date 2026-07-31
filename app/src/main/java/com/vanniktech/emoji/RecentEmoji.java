package com.vanniktech.emoji;

import androidx.annotation.NonNull;

import com.vanniktech.emoji.emoji.Emoji;

import java.util.Collection;

public interface RecentEmoji {
  @NonNull Collection<Emoji> getRecentEmojis();
  void addEmoji(@NonNull Emoji emoji);
  void persist();
}
