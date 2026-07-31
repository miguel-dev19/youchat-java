package cu.alexgi.youchat.photoutil;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.IOException;

public class CameraPhoto {

    final String TAG = this.getClass().getSimpleName();

    private String photoPath;

    public String getPhotoPath() {
        return photoPath;
    }

    private Context context;
    public CameraPhoto(Context context){
        this.context = context;
    }

    public Intent takePhotoIntent(String dir, String nom) throws IOException {
        Intent in = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (in.resolveActivity(context.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = createImageFile(dir,nom);

            // Continue only if the File was successfully created
            if (photoFile != null) {
                in.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            }
        }
        return in;
    }

    private File createImageFile(String dir, String nom) throws IOException {

        File storageDir=new File(dir);
        boolean isCreada=storageDir.exists();
        File image;

        if(!isCreada)
        {
            isCreada=storageDir.mkdirs();
        }
        if(isCreada)
        {
            image=new File(dir+nom);
//            image = File.createTempFile(
//                    nombre_img,  /* prefix */
//                    ".jpg",         /* suffix */
//                    storageDir      /* directory */
//            );
//            photoPath = image.getAbsolutePath();
            photoPath = dir+nom;
        }
        else throw new IOException("nada");


        return image;
    }

    public void addToGallery() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(photoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }
}
