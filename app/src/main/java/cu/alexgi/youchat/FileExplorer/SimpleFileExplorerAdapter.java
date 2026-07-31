package cu.alexgi.youchat.FileExplorer;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cu.alexgi.youchat.CircleImageView;
import cu.alexgi.youchat.R;

import static cu.alexgi.youchat.FileExplorer.FileModelType.DIRECTORY;

class SimpleFileExplorerAdapter extends RecyclerView.Adapter<SimpleFileExplorerViewHolder> {

    private Context context;
    private List<FileModel> filesList;
    private AdapterListener adapterListener;
    private int previousItemSelectedIndex;

    public SimpleFileExplorerAdapter(Context context) {
        this.context = context;
        this.filesList = new ArrayList<>();
        this.previousItemSelectedIndex = -1;
    }

    public void setAdapterListener(AdapterListener adapterListener) {
        this.adapterListener = adapterListener;
    }


    @NonNull
    @Override
    public SimpleFileExplorerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(this.context);
        View fileExplorerListView = layoutInflater.inflate(R.layout.file_explorer_list, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        fileExplorerListView.setLayoutParams(lp);
        return new SimpleFileExplorerViewHolder(fileExplorerListView);
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleFileExplorerViewHolder simpleFileExplorerViewHolder, final int i) {
        FileModel fileModel = this.filesList.get(i);
        simpleFileExplorerViewHolder.backgroundConstraintLayout.setBackgroundColor(ContextCompat.getColor(context,R.color.fondo_blanco));
        this.setTextByFileAbsolutePath(simpleFileExplorerViewHolder.fileAbsolutePathTextView, simpleFileExplorerViewHolder.fileSize, fileModel.getName(), fileModel.getSize());

        this.setImagesByFileType(simpleFileExplorerViewHolder.fileImageView, fileModel.getFileModelType());

        this.setLayoutOnClickListenerByFileType(simpleFileExplorerViewHolder.backgroundConstraintLayout, fileModel.getFileModelType(), fileModel, i);
        this.updateSelectedItemColor(simpleFileExplorerViewHolder.backgroundConstraintLayout, i, fileModel);
    }

    @Override
    public int getItemCount() {
        return this.filesList.size();
    }

    public void loadDirectory(List<FileModel> filesList) {
        this.filesList = new ArrayList<>(filesList);
        Collections.sort(this.filesList, new Comparator<FileModel>() {
            @Override
            public int compare(FileModel o1, FileModel o2) {
                int directorySortResult = o2.getFileModelType().compareTo(o1.getFileModelType());
                if (directorySortResult == 0) {
                    return o1.getAbsolutePath().compareTo(o2.getAbsolutePath());
                }
                return directorySortResult;
            }
        });
        this.notifyDataSetChanged();
    }

    private void setTextByFileAbsolutePath(TextView tv_name, TextView tv_size, String absolutePath, String size) {
        tv_name.setText(absolutePath);
        tv_size.setText(size);
    }

    private void setImagesByFileType(CircleImageView imageView, FileModelType fileModelType) {
        int fileImageId = 0;
        int directoryImageId = 0;
        if (SimpleFileResources.imageFileId == null) {
            fileImageId = SimpleFileResources.defaultImageFileId;
        }
        if (SimpleFileResources.imageDirectoryId == null) {
            directoryImageId = SimpleFileResources.defaultImageDirectoryId;
        }
        switch (fileModelType) {
            case FILE:
                imageView.setImageResource(fileImageId);
                imageView.setCircleBackgroundColor(context.getResources().getColor(R.color.card6));
                imageView.setBorderColor(context.getResources().getColor(R.color.card6));
                break;
            case AUDIO:
                imageView.setImageResource(SimpleFileResources.file_audio);
                imageView.setCircleBackgroundColor(context.getResources().getColor(R.color.card14));
                imageView.setBorderColor(context.getResources().getColor(R.color.card14));
                break;
            case IMAGEN:
                imageView.setImageResource(SimpleFileResources.file_imagen);
                imageView.setCircleBackgroundColor(context.getResources().getColor(R.color.card11));
                imageView.setBorderColor(context.getResources().getColor(R.color.card11));
                break;
            case VIDEO:
                imageView.setImageResource(SimpleFileResources.file_video);
                imageView.setCircleBackgroundColor(context.getResources().getColor(R.color.card1));
                imageView.setBorderColor(context.getResources().getColor(R.color.card1));
                break;
            case APK:
                imageView.setImageResource(SimpleFileResources.file_apk);
                imageView.setCircleBackgroundColor(context.getResources().getColor(R.color.card9));
                imageView.setBorderColor(context.getResources().getColor(R.color.card9));
                break;
            case TXT:
                imageView.setImageResource(SimpleFileResources.file_txt);
                imageView.setCircleBackgroundColor(context.getResources().getColor(R.color.card17));
                imageView.setBorderColor(context.getResources().getColor(R.color.card17));
                break;
            case COMPRESS:
                imageView.setImageResource(SimpleFileResources.file_compress);
                imageView.setCircleBackgroundColor(context.getResources().getColor(R.color.card5));
                imageView.setBorderColor(context.getResources().getColor(R.color.card5));
                break;
            case XML:
                imageView.setImageResource(SimpleFileResources.file_xml);
                imageView.setCircleBackgroundColor(context.getResources().getColor(R.color.card10));
                imageView.setBorderColor(context.getResources().getColor(R.color.card10));
                break;
            case GIF:
                imageView.setImageResource(SimpleFileResources.file_gif);
                imageView.setCircleBackgroundColor(context.getResources().getColor(R.color.card12));
                imageView.setBorderColor(context.getResources().getColor(R.color.card12));
                break;
            case PDF:
                imageView.setImageResource(SimpleFileResources.file_pdf);
                imageView.setCircleBackgroundColor(context.getResources().getColor(R.color.card16));
                imageView.setBorderColor(context.getResources().getColor(R.color.card16));
                break;
            case DIRECTORY:
                imageView.setImageResource(directoryImageId);
                imageView.setCircleBackgroundColor(context.getResources().getColor(R.color.card8));
                imageView.setBorderColor(context.getResources().getColor(R.color.card8));
                break;
            default:
                break;
        }
    }

    private void setLayoutOnClickListenerByFileType(final RelativeLayout layout, final FileModelType fileModelType, final FileModel fileModel, final int index) {
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fileModelType==DIRECTORY) adapterListener.onDirectoryClick(fileModel.getAbsolutePath(), fileModel.getName());
                else{
                    if (fileModel.isSelected()) {
                        fileModel.setSelected(false);
                        adapterListener.onFileClick(fileModel);
                        notifyDataSetChanged();
                    }
                    else{
                        fileModel.setSelected(true);
                        adapterListener.onFileClick(fileModel);
                        previousItemSelectedIndex = index;
                        notifyDataSetChanged();
                    }
                }
            }
        });
    }

    private void updateSelectedItemColor(RelativeLayout layout, final int index, FileModel fileModel) {
        if (this.previousItemSelectedIndex == index) {
            layout.setBackgroundColor(Color.rgb(168, 168, 168));
            fileModel.setSelected(true);
            this.previousItemSelectedIndex = -1;
        } else {
            fileModel.setSelected(false);
        }
    }
}
