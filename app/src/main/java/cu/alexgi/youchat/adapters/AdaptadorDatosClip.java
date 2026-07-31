package cu.alexgi.youchat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.impl.OnItemClickListener;

import java.util.List;

import cu.alexgi.youchat.R;


public class AdaptadorDatosClip extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater mInflater;
    private OnItemClickListener mItemClickListener;

    private List<AlbumFile> mAlbumFiles;

    public AdaptadorDatosClip(Context context, OnItemClickListener itemClickListener) {
        this.mInflater = LayoutInflater.from(context);
        this.mItemClickListener = itemClickListener;
    }

    public void notifyDataSetChanged(List<AlbumFile> imagePathList) {
        this.mAlbumFiles = imagePathList;
        super.notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return AlbumFile.TYPE_IMAGE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case AlbumFile.TYPE_IMAGE: {
                return new ImageViewHolder(mInflater.inflate(R.layout.layout_img_clip, parent, false), mItemClickListener);
            }
            default: {
                throw new AssertionError("This should not be the case.");
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case AlbumFile.TYPE_IMAGE: {
                ((ImageViewHolder) holder).setData(mAlbumFiles.get(position));
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mAlbumFiles == null ? 0 : mAlbumFiles.size();
    }

    private static class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final OnItemClickListener mItemClickListener;
        private ImageView mIvImage;

        ImageViewHolder(View itemView, OnItemClickListener itemClickListener) {
            super(itemView);
            this.mItemClickListener = itemClickListener;
            this.mIvImage = itemView.findViewById(R.id.iv_album_content_image);
            itemView.setOnClickListener(this);
        }

        public void setData(AlbumFile albumFile) {
            Album.getAlbumConfig().
                    getAlbumLoader().
                    load(mIvImage, albumFile);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }
}
