package cu.alexgi.youchat.FileExplorer;

import java.io.Serializable;

interface AdapterListener extends Serializable {
    void onDirectoryClick(String selectedAbsolutePath, String name);
    void onFileClick(FileModel fileModel);
}
