/*
 * Copyright (C) 2011 Whisper Systems
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cu.alexgi.youchat.chatUtils.mms;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.b44t.messenger.DcMsg;

import cu.alexgi.youchat.chatUtils.MediaUtil;
import cu.alexgi.youchat.chatUtils.attachments.Attachment;
import cu.alexgi.youchat.chatUtils.attachments.DcAttachment;


public class ImageSlide extends Slide {

  @SuppressWarnings("unused")
  private static final String TAG = ImageSlide.class.getSimpleName();

  public ImageSlide(@NonNull Context context, @NonNull DcMsg dcMsg) {
    super(context, new DcAttachment(dcMsg));
  }

  public ImageSlide(@NonNull Context context, @NonNull Attachment attachment) {
    super(context, attachment);
  }

  public ImageSlide(Context context, Uri uri, long size, int width, int height) {
    super(context, constructAttachmentFromUri(context, uri, MediaUtil.IMAGE_JPEG, size, width, height, uri, null, false));
  }

  @Override
  public @Nullable Uri getThumbnailUri() {
    return getUri();
  }

  @Override
  public boolean hasImage() {
    return true;
  }
}
