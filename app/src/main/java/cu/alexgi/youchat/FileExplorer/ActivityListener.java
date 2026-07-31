package cu.alexgi.youchat.FileExplorer;

import java.io.Serializable;

interface ActivityListener extends Serializable {
    void onDirectoryChanged(String absolutePath, String name);
    void onFileSelect(FileModel fileModel);
    void onBackButtonPressed(String absolutePath);
}
