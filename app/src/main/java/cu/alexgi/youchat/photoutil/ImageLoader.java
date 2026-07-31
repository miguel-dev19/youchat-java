package cu.alexgi.youchat.photoutil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageLoader {

    private String filePath, filePathDestiny;

    private static ImageLoader instance;

    private int width = 128, height = 128; //default

    protected ImageLoader(){
    }


    public static ImageLoader init(){
        if(instance == null){
            synchronized (ImageLoader.class) {
                if(instance == null){
                    instance = new ImageLoader();
                }
            }
        }
        return instance;
    }

    public ImageLoader from(String filePath) {
        this.filePath = filePath;
        return instance;
    }

    public ImageLoader to(String filePath) {
        this.filePathDestiny = filePath;
        return instance;
    }

    public ImageLoader requestSize(int width, int height) {
        this.height = width;
        this.width = height;
        return instance;
    }


    public Bitmap getBitmap(int calidad) throws FileNotFoundException {

        File file = new File(filePath);
        if(!file.exists()){
            throw new FileNotFoundException();
        }


//        final BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//
//        BitmapFactory.decodeFile(filePath, options);
//
//        options.inSampleSize = calculateInSampleSize(options, width, height);
//
//        options.inJustDecodeBounds = false;
//        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);


        compressImage(filePath,filePathDestiny, calidad, Bitmap.CompressFormat.JPEG);
        Bitmap bitmap = BitmapFactory.decodeFile(filePathDestiny);
        return bitmap;
    }

    public void guardarImagen(String origen, String destino){
        Bitmap bitmap = BitmapFactory.decodeFile(origen);
        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(destino));
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }

    public boolean comprimirImagen(String origen, String destino, int calidad) throws FileNotFoundException {
        File file = new File(origen);
        if(!file.exists()){
            throw new FileNotFoundException();
        }
        return compressImage( origen, destino, calidad, Bitmap.CompressFormat.JPEG);
    }

    ////////////////////////////ver ojo pa comprimir sin virar la img con bitmap
    private boolean compressImage(String imagePath, String newPath, int quality, Bitmap.CompressFormat format) {
        float maxWidth;
        float maxHeight;
        String str = imagePath;
        Bitmap scaledBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(str, options);
        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
        if (actualHeight == 0) {
            String str2 = newPath;
            Bitmap.CompressFormat compressFormat = format;
            BitmapFactory.Options options2 = options;
        } else if (actualWidth == 0) {
            String str3 = newPath;
            Bitmap.CompressFormat compressFormat2 = format;
            BitmapFactory.Options options3 = options;
        } else {
            if (actualHeight > actualWidth) {
                maxHeight = 640.0f;
                maxWidth = 480.0f;
            } else if (actualHeight < actualWidth) {
                maxHeight = 480.0f;
                maxWidth = 640.0f;
            } else {
                maxHeight = 480.0f;
                maxWidth = 480.0f;
            }
            float imgRatio = (float) (actualWidth / actualHeight);
            float maxRatio = maxWidth / maxHeight;
            if (((float) actualHeight) > maxHeight || ((float) actualWidth) > maxWidth) {
                if (imgRatio < maxRatio) {
                    actualWidth = (int) (((float) actualWidth) * (maxHeight / ((float) actualHeight)));
                    actualHeight = (int) maxHeight;
                } else if (imgRatio > maxRatio) {
                    actualHeight = (int) (((float) actualHeight) * (maxWidth / ((float) actualWidth)));
                    actualWidth = (int) maxWidth;
                } else {
                    actualHeight = (int) maxHeight;
                    actualWidth = (int) maxWidth;
                }
            }
            options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inTempStorage = new byte[16384];
            try {
                bmp = BitmapFactory.decodeFile(str, options);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
            if (actualHeight == 0) {
                Bitmap.CompressFormat compressFormat3 = format;
                BitmapFactory.Options options4 = options;
                int i = actualHeight;
                int i2 = actualWidth;
                float f = maxHeight;
                String str4 = newPath;
                int actualWidth2 = quality;
            } else if (actualWidth == 0) {
                Bitmap.CompressFormat compressFormat4 = format;
                BitmapFactory.Options options5 = options;
                int i3 = actualHeight;
                int i4 = actualWidth;
                float f2 = maxHeight;
                String str5 = newPath;
                int actualWidth3 = quality;
            } else {
                try {
                    scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
                } catch (OutOfMemoryError exception) {
                    exception.printStackTrace();
                }
                float ratioX = ((float) actualWidth) / ((float) options.outWidth);
                float ratioY = ((float) actualHeight) / ((float) options.outHeight);
                float middleX = ((float) actualWidth) / 2.0f;
                float middleY = ((float) actualHeight) / 2.0f;
                BitmapFactory.Options options6 = options;
                Matrix scaleMatrix = new Matrix();
                scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);
                int i5 = actualHeight;
                Canvas canvas = new Canvas(scaledBitmap);
                canvas.setMatrix(scaleMatrix);
                Matrix matrix = scaleMatrix;
                int i6 = actualWidth;
                float f3 = maxHeight;
                canvas.drawBitmap(bmp, middleX - ((float) (bmp.getWidth() / 2)), middleY - ((float) (bmp.getHeight() / 2)), new Paint(2));
                try {
                    FileOutputStream fm = new FileOutputStream(newPath);
                    withCorrectRotation(scaledBitmap, str).compress(format, quality, fm);
                    fm.close();
                } catch (FileNotFoundException e2) {
                    e2.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    private Bitmap withCorrectRotation(Bitmap bitmap, String imagePath) {
        try {
            int orientation = new ExifInterface(imagePath).getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90.0f);
            } else if (orientation == 3) {
                matrix.postRotate(180.0f);
            } else if (orientation == 8) {
                matrix.postRotate(270.0f);
            }
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
            return bitmap;
        }
    }

    public Drawable getImageDrawable() throws FileNotFoundException{
        File file = new File(filePath);
        if(!file.exists()){
            throw new FileNotFoundException();
        }
        Drawable drawable = Drawable.createFromPath(filePath);
        return drawable;
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

}
