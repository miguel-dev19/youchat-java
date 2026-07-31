package cu.alexgi.youchat.items;

import cu.alexgi.youchat.progressbar.DownloadProgressView;

public class ItemDesImgPost {

    private ItemPost post;
    private DownloadProgressView downloadProgressView;

    public ItemDesImgPost(ItemPost post, DownloadProgressView downloadProgressView) {
        this.post = post;
        this.downloadProgressView = downloadProgressView;
    }

    public ItemPost getPost() {
        return post;
    }

    public void setPost(ItemPost post) {
        this.post = post;
    }

    public DownloadProgressView getDownloadProgressView() {
        return downloadProgressView;
    }

    public void setDownloadProgressView(DownloadProgressView downloadProgressView) {
        this.downloadProgressView = downloadProgressView;
    }
}
