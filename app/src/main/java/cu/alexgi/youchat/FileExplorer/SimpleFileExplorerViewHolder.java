package cu.alexgi.youchat.FileExplorer;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import cu.alexgi.youchat.CircleImageView;
import cu.alexgi.youchat.R;

class SimpleFileExplorerViewHolder extends RecyclerView.ViewHolder{
    public RelativeLayout backgroundConstraintLayout;
    public TextView fileAbsolutePathTextView;
    public TextView fileSize;
    public CircleImageView fileImageView;


    public SimpleFileExplorerViewHolder(@NonNull View itemView) {
        super(itemView);
        this.backgroundConstraintLayout = itemView.findViewById(R.id.cl_background);
        this.fileAbsolutePathTextView = itemView.findViewById(R.id.tv_file_absolute_path);
        this.fileImageView = itemView.findViewById(R.id.iv_file);
        fileSize = itemView.findViewById(R.id.fileSize);
    }
}
