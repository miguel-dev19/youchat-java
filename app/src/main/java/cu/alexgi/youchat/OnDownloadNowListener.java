package cu.alexgi.youchat;

public interface OnDownloadNowListener {
    void OnProgressListener(float progress);
    void OnFailedDownload(boolean esFallida);
    void OnSuccessDownload(String idNow);
}
