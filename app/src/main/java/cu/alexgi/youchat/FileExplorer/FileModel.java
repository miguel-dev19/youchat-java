package cu.alexgi.youchat.FileExplorer;

class FileModel {
    private String absolutePath;
    private String directoryPath;
    private String size;
    private FileModelType fileModelType;
    private boolean isSelected;

    public FileModel(String absolutePath, FileModelType fileModelType) {
        this.absolutePath = absolutePath;
        this.directoryPath = this.absolutePath;
        this.fileModelType = fileModelType;
        this.isSelected = false;
    }

    public FileModel(String absolutePath, String directoryPath, FileModelType fileModelType, String size) {
        this(absolutePath, fileModelType);
        this.directoryPath = directoryPath;
        this.size=size;
    }

    public String getName(){
        String nombre = "";
        int l = absolutePath.length()-1;
        for(int i=l; i>=0; i--){
            char a = absolutePath.charAt(i);
            if(a=='/') break;
            else nombre = a + nombre;
        }
        return nombre;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public FileModelType getFileModelType() {
        return fileModelType;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getDirectoryPath() {
        return directoryPath;
    }

    public void setDirectoryPath(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    public void setFileModelType(FileModelType fileModelType) {
        this.fileModelType = fileModelType;
    }
}
