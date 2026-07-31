package cu.alexgi.youchat.FileExplorer;


import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cu.alexgi.youchat.R;
import cu.alexgi.youchat.Utils;


public class SimpleFileExplorerFragment extends Fragment implements AdapterListener {

    private RecyclerView recyclerView;
    private SimpleFileExplorerAdapter fileExplorerAdapter;
    private String selectedAbsolutePath;
    private ActivityListener activityListener;

    public static final String AUDIO = "wav,mp3,m4a,ogg,aac,mpg4,wmv,mid,mka,mpeg4,wma,opus,ycaudio";
    public static final String IMAGEN = "jpg,png,jpeg,ico";
    public static final String VIDEO = "mp4,vob,avi,mpg,3gp";
    public static final String APK ="apk";
    public static final String TXT = "txt,doc,docx,ppt,pptx,xls,xlsx";
    public static final String COMPRESS = "zip,rar,rar4";
    public static final String XML = "html,xml";
    public static final String GIF = "gif,tiff,bmp";
    public static final String PDF = "pdf";

    public SimpleFileExplorerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.file_explorer_fragment, container, false);

        this.initViews(view);
        this.loadDirectory();

        return view;
    }

    private void initRecyclerView(View view) {
        this.recyclerView = view.findViewById(R.id.recycler_file_explorer);
        this.fileExplorerAdapter = new SimpleFileExplorerAdapter(getContext());
        this.fileExplorerAdapter.setAdapterListener(this);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setAdapter(this.fileExplorerAdapter);
//        this.recyclerView.addItemDecoration(new DividerItemDecoration(this.recyclerView.getContext(), DividerItemDecoration.VERTICAL));
    }

    private void loadDirectory() {
        File root = Environment.getExternalStorageDirectory();

        if (this.selectedAbsolutePath != null) {
            root = new File(this.selectedAbsolutePath);
        } else {
            this.selectedAbsolutePath = root.getAbsolutePath();
        }

        List<FileModel> fileModelList = new ArrayList<>();


        final File[] listFiles = root.listFiles();
        if (root.isDirectory() && listFiles != null) {
            for (File file : listFiles) {
                if (file.isDirectory()) {
                    fileModelList.add(new FileModel(file.getAbsolutePath(), FileModelType.DIRECTORY));
                } else {
                    final File parent = file.getParentFile();
                    if (parent == null) {
                        continue;
                    }
                    String ext= Utils.obtenerExtension(file.getName());

                    String extend = "Kb";
                    long size = file.length();
                    double realSize = (double) size/1024;
                    if(realSize > 1024){
                        extend = "Mb";
                        realSize = (double) realSize/1024;
                    }
                    if(realSize > 1024){
                        extend = "Gb";
                        realSize = (double) realSize/1024;
                    }
                    realSize = (double)Math.round(realSize*100)/100;

                    if(AUDIO.contains(ext)) fileModelList.add(new FileModel(file.getAbsolutePath(), parent.getAbsolutePath(), FileModelType.AUDIO, realSize+" "+extend));
                    else if(APK.contains(ext)) fileModelList.add(new FileModel(file.getAbsolutePath(), parent.getAbsolutePath(), FileModelType.APK, realSize+" "+extend));
                    else if(VIDEO.contains(ext)) fileModelList.add(new FileModel(file.getAbsolutePath(), parent.getAbsolutePath(), FileModelType.VIDEO, realSize+" "+extend));
                    else if(IMAGEN.contains(ext)) fileModelList.add(new FileModel(file.getAbsolutePath(), parent.getAbsolutePath(), FileModelType.IMAGEN, realSize+" "+extend));
                    else if(TXT.contains(ext)) fileModelList.add(new FileModel(file.getAbsolutePath(), parent.getAbsolutePath(), FileModelType.TXT, realSize+" "+extend));
                    else if(GIF.contains(ext)) fileModelList.add(new FileModel(file.getAbsolutePath(), parent.getAbsolutePath(), FileModelType.GIF, realSize+" "+extend));
                    else if(COMPRESS.contains(ext)) fileModelList.add(new FileModel(file.getAbsolutePath(), parent.getAbsolutePath(), FileModelType.COMPRESS, realSize+" "+extend));
                    else if(XML.contains(ext)) fileModelList.add(new FileModel(file.getAbsolutePath(), parent.getAbsolutePath(), FileModelType.XML, realSize+" "+extend));
                    else if(PDF.contains(ext)) fileModelList.add(new FileModel(file.getAbsolutePath(), parent.getAbsolutePath(), FileModelType.PDF, realSize+" "+extend));
                    else fileModelList.add(new FileModel(file.getAbsolutePath(), parent.getAbsolutePath(), FileModelType.FILE, realSize+" "+extend));
                }
            }
        } else {
            fileModelList.add(new FileModel(root.getAbsolutePath(), FileModelType.DIRECTORY));
        }

        this.updateRecyclerList(fileModelList);
    }

    private void updateRecyclerList(List<FileModel> fileModels) {
        this.fileExplorerAdapter.loadDirectory(fileModels);
    }


    private void initViews(View view) {
        this.initRecyclerView(view);
    }


    @Override
    public void onDirectoryClick(String selectedAbsolutePath, String name) {
        this.activityListener.onDirectoryChanged(selectedAbsolutePath, name);
    }

    @Override
    public void onFileClick(FileModel fileModel) {
        this.activityListener.onFileSelect(fileModel);
    }

    void setListeners(ActivityListener activityListener) {
        this.activityListener = activityListener;
    }

    void setDirectory(String dir) {
        this.selectedAbsolutePath = dir;
    }

    @Override
    public void onStart() {
        super.onStart();
        this.activityListener.onBackButtonPressed(this.selectedAbsolutePath);
    }
}
