/*
 * Copyright 2016 Yan Zhenjie.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yanzhenjie.album.app.album;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.radiobutton.MaterialRadioButton;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.AlbumFolder;
import com.yanzhenjie.album.impl.OnItemClickListener;

import java.util.List;

import cu.alexgi.youchat.R;

class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderViewHolder> {

    private LayoutInflater mInflater;
    private List<AlbumFolder> mAlbumFolders;
    private ColorStateList mSelector;

    private OnItemClickListener mItemClickListener;

    public FolderAdapter(Context context, List<AlbumFolder> mAlbumFolders, ColorStateList buttonTint) {
        this.mInflater = LayoutInflater.from(context);
        this.mSelector = buttonTint;
        this.mAlbumFolders = mAlbumFolders;
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    @Override
    public FolderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FolderViewHolder(mInflater.inflate(R.layout.album_item_dialog_folder, parent, false),
                mSelector,
                new OnItemClickListener() {

                    private int oldPosition = 0;

                    @Override
                    public void onItemClick(View view, int position) {
                        if (mItemClickListener != null)
                            mItemClickListener.onItemClick(view, position);

                        AlbumFolder albumFolder = mAlbumFolders.get(position);
                        if (!albumFolder.isChecked()) {
                            albumFolder.setChecked(true);
                            mAlbumFolders.get(oldPosition).setChecked(false);
                            notifyItemChanged(oldPosition);
                            notifyItemChanged(position);
                            oldPosition = position;
                        }
                    }
                });
    }

    @Override
    public void onBindViewHolder(FolderViewHolder holder, int position) {
        final int newPosition = holder.getAdapterPosition();
        holder.setData(mAlbumFolders.get(newPosition));
    }

    @Override
    public int getItemCount() {
        return mAlbumFolders == null ? 0 : mAlbumFolders.size();
    }

    static class FolderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private OnItemClickListener mItemClickListener;

        private ImageView mIvImage,folder_select;
        private TextView mTvTitle, mTvNumber;
        private MaterialRadioButton mCheckBox;

        private FolderViewHolder(View itemView, ColorStateList selector, OnItemClickListener itemClickListener) {
            super(itemView);

            this.mItemClickListener = itemClickListener;

            mIvImage = itemView.findViewById(R.id.iv_gallery_preview_image);
            mTvTitle = itemView.findViewById(R.id.tv_gallery_preview_title);
            mTvNumber = itemView.findViewById(R.id.tv_gallery_preview_number);
            mCheckBox = itemView.findViewById(R.id.rb_gallery_preview_check);
            folder_select= itemView.findViewById(R.id.folder_select);

            itemView.setOnClickListener(this);

            mCheckBox.setSupportButtonTintList(selector);
        }

        public void setData(AlbumFolder albumFolder) {
            List<AlbumFile> albumFiles = albumFolder.getAlbumFiles();
            /*mTvTitle.setText("(" + albumFiles.size() + ") " + albumFolder.getName());*/
            mTvTitle.setText(albumFolder.getName());
            mTvNumber.setText("(" + albumFiles.size() + ")" );
            boolean isChecked=albumFolder.isChecked();
            mCheckBox.setChecked(isChecked);
            if(isChecked) folder_select.setVisibility(View.VISIBLE);
            else folder_select.setVisibility(View.GONE);

            Album.getAlbumConfig().getAlbumLoader().load(mIvImage, albumFiles.get(0));
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null)
                mItemClickListener.onItemClick(v, getAdapterPosition());
        }
    }

}