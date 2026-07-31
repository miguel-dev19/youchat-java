package cu.alexgi.youchat.photoutil;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

public class GalleryPhoto {

    final String TAG = this.getClass().getSimpleName();

    private Context context;

    public void setPhotoUri(Uri photoUri) {
        this.photoUri = photoUri;
    }

    private Uri photoUri;

    public GalleryPhoto(Context context){
        this.context = context;
    }

    public Intent openGalleryIntent(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        return Intent.createChooser(intent, getChooserTitle());
    }

    public String getChooserTitle(){
        return "Selecciona una imagen";
    }

    public String getPath() {

        String path;
        if (Build.VERSION.SDK_INT < 19){
            path = RealPathUtil.getRealPathFromURI_API11to18(context, photoUri);
        }
        else path = RealPathUtil.getRealPathFromURI_API19(context, photoUri);
        return path;
    }

}
