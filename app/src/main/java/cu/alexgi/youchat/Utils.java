package cu.alexgi.youchat;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import cu.alexgi.youchat.FileExplorer.SimpleFileExplorerFragment;
import cu.alexgi.youchat.zip4j.ZipFile;
import cu.alexgi.youchat.zip4j.exception.ZipException;
import cu.alexgi.youchat.zip4j.model.ZipParameters;
import cu.alexgi.youchat.zip4j.model.enums.AesKeyStrength;
import cu.alexgi.youchat.zip4j.model.enums.EncryptionMethod;

import static android.content.Context.VIBRATOR_SERVICE;
import static android.media.AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class Utils {

    public static void runOnUIThread(Runnable runnable) {
        YouChatApplication.applicationHandler.post(runnable);
    }

    public static void runOnUIThread(Runnable runnable, long delay) {
        if (delay == 0) {
            YouChatApplication.applicationHandler.post(runnable);
        } else {
            YouChatApplication.applicationHandler.postDelayed(runnable, delay);
        }
    }

    public static void cancelRunOnUIThread(Runnable runnable) {
        YouChatApplication.applicationHandler.removeCallbacks(runnable);
    }

    public static synchronized void vibrate(Context context, long milliseconds) {
        Vibrator vibe = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
        if (vibe != null) {
            vibe.vibrate(milliseconds);
        }
    }

    public static synchronized String getImageFromUri(Context context, Uri contentUri) {
        String path = null;
        String[] proj = {MediaStore.MediaColumns.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                path = cursor.getString(column_index);
            }
            cursor.close();
        }
        return path;
    }

    public static boolean SavePhotoUri(Context context, Uri imageuri, String destino) {
        try {
            Bitmap selectedImage = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageuri);
            FileOutputStream destination = new FileOutputStream(destino);
            selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, destination);
            destination.close();
        } catch (Exception e) {
            Log.e("error", e.toString());
            return false;
        }
        return esImagen(destino);
    }

    public static boolean SavePhotoUri2(Context context, Uri uri, String destino){
        try {
            ParcelFileDescriptor parcelFileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap selectedImage = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();

            FileOutputStream destination = new FileOutputStream(destino);
            selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, destination);
            destination.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return esImagen(destino);
    }

    public static boolean esImagen(String ruta){
        if(!new File(ruta).exists()) return false;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.outWidth = YouChatApplication.anchoPantalla / 10;
        options.outHeight = YouChatApplication.largoPantalla / 10;

        Bitmap imagen = BitmapFactory.decodeFile(ruta, options);
        if (imagen != null) {
            return true;
        }
        return false;
    }

    /*

mMediaPlayer = new MediaPlayer();
final Object mFocusLock = new Object();

boolean mPlaybackDelayed = false;
boolean mPlaybackNowAuthorized = false;

// ...
int res = mAudioManager.requestAudioFocus(mFocusRequest);
synchronized(mFocusLock) {
    if (res == AUDIOFOCUS_REQUEST_FAILED) {
        mPlaybackNowAuthorized = false;
    } else if (res == AUDIOFOCUS_REQUEST_GRANTED) {
        mPlaybackNowAuthorized = true;
        playbackNow();
    } else if (res == AUDIOFOCUS_REQUEST_DELAYED) {
       mPlaybackDelayed = true;
       mPlaybackNowAuthorized = false;
    }
}

// ...

    * */

    public static synchronized AudioFocusRequest setAudioFocus(AudioManager audioManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioAttributes mPlaybackAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();

            AudioFocusRequest audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAcceptsDelayedFocusGain(false)
                    .setAudioAttributes(mPlaybackAttributes)
                    .build();

            audioManager.requestAudioFocus(audioFocusRequest);
            return audioFocusRequest;
        } else {
            audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        }
        return null;
    }

    public static synchronized void clearAudioFocus(AudioManager audioManager, AudioFocusRequest audioFocusRequest) {
        if (audioManager == null) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (audioFocusRequest != null) audioManager.abandonAudioFocusRequest(audioFocusRequest);
        } else {
            audioManager.abandonAudioFocus(null);
        }
    }

    public static synchronized void reproducirSonido(int sound, Context context) {
        if (!YouChatApplication.sonido) return;
        MediaPlayer mediaPlayer = MediaPlayer.create(context, sound);
        try {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            int vol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            mediaPlayer.setVolume(vol, vol);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.release();
                }
            });
        } catch (IllegalStateException e) {
            e.printStackTrace();
            if (mediaPlayer != null) mediaPlayer.release();
        } catch (Exception e) {
            e.printStackTrace();
            if (mediaPlayer != null) mediaPlayer.release();
        }
    }

    public static synchronized void reproducirSonido2(int sound, Context context) {
        // stop music player
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioAttributes mPlaybackAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();

            AudioFocusRequest audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAcceptsDelayedFocusGain(false)
                    .setAudioAttributes(mPlaybackAttributes)
                    .build();

            int res = audioManager.requestAudioFocus(audioFocusRequest);
            if (res == AUDIOFOCUS_REQUEST_GRANTED) {
                if (playbackNow(context, sound, audioManager))
                    audioManager.abandonAudioFocusRequest(audioFocusRequest);
            }
        } else {
            if (audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT) == AUDIOFOCUS_REQUEST_GRANTED) {
                if (playbackNow(context, sound, audioManager)) audioManager.abandonAudioFocus(null);
            }
        }
    }

    private synchronized static boolean playbackNow(Context context, int sound, AudioManager audioManager) {
        if (!YouChatApplication.sonido) return true;
        MediaPlayer mediaPlayer = MediaPlayer.create(context, sound);
        try {
//            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            int vol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            mediaPlayer.setVolume(vol, vol);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.release();
                }
            });
        } catch (IllegalStateException e) {
            e.printStackTrace();
            if (mediaPlayer != null) mediaPlayer.release();
        } catch (Exception e) {
            e.printStackTrace();
            if (mediaPlayer != null) mediaPlayer.release();
        }
        return true;
    }

    public synchronized static String obtenerOscuroDe(String colorHex) {
        colorHex = colorHex.replace("#", "");
        int r = 0, g = 0, b = 0;

        r = Integer.valueOf(colorHex.substring(2, 4), 16);
        g = Integer.valueOf(colorHex.substring(4, 6), 16);
        b = Integer.valueOf(colorHex.substring(6, 8), 16);

        if (r <= 9) r = 10;
        if (g <= 9) g = 10;
        if (b <= 9) b = 10;

        String rHex = "";
        String gHex = "";
        String bHex = "";

        if ((r + g + b) >= 128) {
            r = r - (r * 40 / 100);
            g = g - (g * 40 / 100);
            b = b - (b * 40 / 100);

            rHex = Integer.toHexString(r);
            gHex = Integer.toHexString(g);
            bHex = Integer.toHexString(b);

            if (rHex.length() == 1) rHex = "0" + rHex;
            if (gHex.length() == 1) gHex = "0" + gHex;
            if (bHex.length() == 1) bHex = "0" + bHex;
        } else {
            r = r + ((256 - r) * 40 / 100);
            g = g + ((256 - g) * 40 / 100);
            b = b + ((256 - b) * 40 / 100);

            rHex = Integer.toHexString(r);
            gHex = Integer.toHexString(g);
            bHex = Integer.toHexString(b);

            if (rHex.length() == 1) rHex = "0" + rHex;
            if (gHex.length() == 1) gHex = "0" + gHex;
            if (bHex.length() == 1) bHex = "0" + bHex;
        }
        return "#FF" + rHex + gHex + bHex;
    }

    public synchronized static boolean crearCacheSticker(File[] listTGS) {
        for (File file : listTGS) {
            try {
                File sticker = Convertidor.obtenerFileStickerCache(file.getAbsolutePath(), "");

                GZIPInputStream zipInputStream = new GZIPInputStream(new FileInputStream(file));
                byte[] buffer = new byte[1024];
                int count;
                FileOutputStream fileOutputStream = new FileOutputStream(sticker);
                while ((count = zipInputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, count);
                }
                fileOutputStream.close();
                zipInputStream.close();
            } catch (IOException e) {
                Log.e("UTILS-UNZIP", e.getMessage());
                e.printStackTrace();
//                return false;
            }
        }
        return true;
    }

    public synchronized static void ocultarKeyBoard(Activity activity) {
        runOnUIThread(() -> {
            View view = activity.getCurrentFocus();
            if (view != null) {
                try {
                    view.clearFocus();
                    InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);

                    if (!inputMethodManager.isActive()) {
                        return;
                    }
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static boolean ocultarKeyBoardEsperar(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            try {
                view.clearFocus();
                InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);

                if (!inputMethodManager.isActive()) {
                    return false;
                }
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public static void ocultarKeyBoard(View view) {
        if (view == null) {
            return;
        }
        try {
            InputMethodManager imm = (InputMethodManager) view.getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (!imm.isActive()) {
                return;
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized static String cargarImgCache(String ruta) {
        if(ruta==null || ruta.isEmpty()) return "";
        File ruta_foto = new File(ruta);
        if (ruta_foto.exists() && Utils.esImagen(ruta)) {
            String img_cache = YouChatApplication.RUTA_IMAGENES_PERFIL + "cache" + ruta_foto.getName();
            File ruta_cache = new File(img_cache);
            if (!ruta_cache.exists()){
                if(crearImgCache(ruta))
                    return ruta;
                return "";
            }
            return img_cache;
        }
        return "";
    }

    public static int dpToPx(@NonNull final Context context, final float dp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics()) + 0.5f);
    }

    public synchronized static String convertirBytes(long n) {
//        String ext = "Kb";
//        double realSize = (double) n/1024;
//        if(realSize > 1024){
//            ext = "Mb";
//            realSize = (double) realSize/1024;
//        }
//        if (realSize > 1024){
//            ext = "Gb";
//            realSize = (double) realSize/1024;
//        }
//        realSize = (double)Math.round(realSize*100)/100;
//        return realSize+" "+ext;

        return convertirBytes(n, false);
    }

    public static String convertirBytes(long size, boolean removeZero) {
        if (size < 1024) {
            return String.format("%d B", size);
        } else if (size < 1024 * 1024) {
            float value = size / 1024.0f;
            if (removeZero && (value - (int) value) * 10 == 0) {
                return String.format("%d KB", (int) value);
            } else {
                return String.format("%.1f KB", value);
            }
        } else if (size < 1024 * 1024 * 1024) {
            float value = size / 1024.0f / 1024.0f;
            if (removeZero && (value - (int) value) * 10 == 0) {
                return String.format("%d MB", (int) value);
            } else {
                return String.format("%.1f MB", value);
            }
        } else {
            float value = size / 1024.0f / 1024.0f / 1024.0f;
            if (removeZero && (value - (int) value) * 10 == 0) {
                return String.format("%d GB", (int) value);
            } else {
                return String.format("%.1f GB", value);
            }
        }
    }

    public static boolean crearImgCache(@NonNull String ruta) {
        File file = new File(ruta);
        String destino = YouChatApplication.RUTA_IMAGENES_PERFIL + "cache" + file.getName();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.outWidth = YouChatApplication.anchoPantalla / 2;
        options.outHeight = YouChatApplication.largoPantalla / 2;

        Bitmap imagen = BitmapFactory.decodeFile(ruta, options);
        if (imagen != null) {
            int alto = imagen.getHeight();
            int ancho = imagen.getWidth();
            int div = Math.max(alto, ancho) / 100;
            if (div == 0) div = 1;

            imagen = Bitmap.createScaledBitmap(imagen, ancho / div, alto / div, true);
            if (imagen != null) {
                try {
                    FileOutputStream fm = new FileOutputStream(destino);
                    imagen.compress(Bitmap.CompressFormat.JPEG, 50, fm);
                    fm.flush();
                    fm.close();
                    return true;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else Utils.borrarFile(file);
        return false;
    }

    public synchronized static boolean hayConnex(Context context) {
        ConnectivityManager conex;
        NetworkInfo state_conex;
        conex = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        state_conex = conex.getActiveNetworkInfo();
        if (state_conex != null && state_conex.isConnected()) return true;
        return false;
    }

    public static String MD5(String md5) {
        if (md5 == null) {
            return null;
        }
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(getStringBytes(md5));
            StringBuilder sb = new StringBuilder();
            for (int a = 0; a < array.length; a++) {
                sb.append(Integer.toHexString((array[a] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] getStringBytes(String src) {
        try {
            return src.getBytes("UTF-8");
        } catch (Exception ignore) {

        }
        return new byte[0];
    }

    public synchronized static void ShowToasty(Activity activity, String texto, int img) {
        final LayoutInflater inflater = activity.getLayoutInflater();
        final View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) activity.findViewById(R.id.toast_layout_root));

        final ImageView image = layout.findViewById(R.id.image);
        final TextView text = layout.findViewById(R.id.text);

        final Toast toast = new Toast(activity);
        toast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 20);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);

        text.setText(texto);
        if (img == 0) image.setVisibility(View.GONE);
        else image.setImageResource(img);

        toast.show();
    }

    public synchronized static void ShowToastAnimated(Activity activity, String texto, int img) {
        final LayoutInflater inflater = activity.getLayoutInflater();
        final View layout = inflater.inflate(R.layout.toast_layout_animated, (ViewGroup) activity.findViewById(R.id.toast_layout_root));

        final LottieAnimationView animation = layout.findViewById(R.id.animation);
        final TextView text = layout.findViewById(R.id.text);

        //animation.setColorFilter(0xFFFFFFFF);
//        animation.setSupportImageTintList(ColorStateList.valueOf(activity.getResources().getColor(R.color.white)));

        final Toast toast = new Toast(activity);
        toast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 20);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);

        text.setText(texto);
        if (img == 0) animation.setVisibility(View.GONE);
        else {
            if (img == R.raw.chats_archive) {
                animation.setRepeatMode(LottieDrawable.RESTART);
                animation.setRepeatCount(7);
            }
            animation.setAnimation(img);
        }



        /*animation.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                //toast.show();
            }

            @Override
            public void onAnimationEnd(Animator animations) {
                animation.removeAnimatorListener(this);
                animation.postDelayed(animation::clearAnimation,500);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });*/
        toast.show();
    }

    public static int[] obtenerRGBde(String colorHex) {
        colorHex = colorHex.replace("#", "");
        int r = 0, g = 0, b = 0;

        r = Integer.valueOf(colorHex.substring(2, 4), 16);
        g = Integer.valueOf(colorHex.substring(4, 6), 16);
        b = Integer.valueOf(colorHex.substring(6, 8), 16);

        return new int[]{r, g, b};
    }

    public static boolean compararIgualdadRGBs(int[] rgb1, int[] rgb2) {
        int difR = Math.abs(rgb1[0] - rgb2[0]);
        int difG = Math.abs(rgb1[1] - rgb2[1]);
        int difB = Math.abs(rgb1[2] - rgb2[2]);
        return (difR + difG + difB) <= 30;
    }

    public static int obtenerColorIntermedio(int[] rgbIni, int[] rgbFin, float porciento) {
        int difR = Math.abs(rgbFin[0] - rgbIni[0]);
        int difG = Math.abs(rgbFin[1] - rgbIni[1]);
        int difB = Math.abs(rgbFin[2] - rgbIni[2]);

        int sumR = (int) ((float) difR * porciento);
        int sumG = (int) ((float) difG * porciento);
        int sumB = (int) ((float) difB * porciento);

        String rHex, gHex, bHex;
        if (rgbIni[0] < rgbFin[0]) rHex = Integer.toHexString(rgbIni[0] + sumR);
        else rHex = Integer.toHexString(rgbIni[0] - sumR);
        if (rgbIni[1] < rgbFin[1]) gHex = Integer.toHexString(rgbIni[1] + sumG);
        else gHex = Integer.toHexString(rgbIni[1] - sumG);
        if (rgbIni[2] < rgbFin[2]) bHex = Integer.toHexString(rgbIni[2] + sumB);
        else bHex = Integer.toHexString(rgbIni[2] - sumB);

        if (rHex.length() == 1) rHex = "0" + rHex;
        if (gHex.length() == 1) gHex = "0" + gHex;
        if (bHex.length() == 1) bHex = "0" + bHex;

        return Color.parseColor("#FF" + rHex + gHex + bHex);
    }

    public static String obtenerInversoDe(String colorHex) {
        colorHex = colorHex.replace("#", "");
        int r = 0, g = 0, b = 0;

        r = Integer.valueOf(colorHex.substring(2, 4), 16);
        g = Integer.valueOf(colorHex.substring(4, 6), 16);
        b = Integer.valueOf(colorHex.substring(6, 8), 16);

//        if(r<=9) r=10;
//        if(g<=9) g=10;
//        if(b<=9) b=10;

        String rHex = "";
        String gHex = "";
        String bHex = "";

        if (r >= 128) r = r - 50;
        else r = r + 50;

        if (g >= 128) g = g - 50;
        else g = g + 50;

        if (b >= 128) b = b - 50;
        else b = b + 50;

        if (r < 0) r = 0;
        if (g < 0) g = 0;
        if (b < 0) b = 0;

        if (r > 255) r = 255;
        if (g > 255) g = 255;
        if (b > 255) b = 255;

        rHex = Integer.toHexString(r);
        gHex = Integer.toHexString(g);
        bHex = Integer.toHexString(b);

        if (rHex.length() == 1) rHex = "0" + rHex;
        if (gHex.length() == 1) gHex = "0" + gHex;
        if (bHex.length() == 1) bHex = "0" + bHex;

        return "#FF" + rHex + gHex + bHex;
    }

    public static boolean comprimirArchivo(File origen, File destino, String pass) {
        try {
            ZipParameters zipParameters = new ZipParameters();
            zipParameters.setEncryptFiles(true);
            zipParameters.setEncryptionMethod(EncryptionMethod.AES);
            zipParameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);

            ZipFile zipFile = new ZipFile(destino, pass.toCharArray());
            zipFile.addFile(origen, zipParameters);
            return true;
        } catch (ZipException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean comprimirArchivo(File origen, File destino, String pass, String newNombre) {
        try {
            ZipParameters zipParameters = new ZipParameters();
            zipParameters.setEncryptFiles(true);
            zipParameters.setEncryptionMethod(EncryptionMethod.AES);
            zipParameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
            zipParameters.setFileNameInZip(newNombre);

            ZipFile zipFile = new ZipFile(destino, pass.toCharArray());
            zipFile.addFile(origen, zipParameters);
            return true;
        } catch (ZipException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int descomprimirArchivo(File origen, String destino, String nombreArchivo, String pass) {
        try {
            new ZipFile(origen, pass.toCharArray())
                    .extractFile(nombreArchivo, destino);
            return 1;
        } catch (ZipException e) {
            e.printStackTrace();
            ZipException.Type tipo = e.getType();
            if (tipo == ZipException.Type.WRONG_PASSWORD)
                return 2;
            else if (tipo == ZipException.Type.FILE_NOT_FOUND)
                return 3;
            else return 4;
        }
    }

    public static int descomprimirArchivo(File origen, String destino,
                                          String nombreArchivo, String nuevoNombreArchivo, String pass) {
        try {
            new ZipFile(origen, pass.toCharArray())
                    .extractFile(nombreArchivo, destino, nuevoNombreArchivo);
            return 1;
        } catch (ZipException e) {
            e.printStackTrace();
            ZipException.Type tipo = e.getType();
            if (tipo == ZipException.Type.WRONG_PASSWORD)
                return 2;
            else if (tipo == ZipException.Type.FILE_NOT_FOUND)
                return 3;
            else return 4;
        }
    }

    public static synchronized String encrypt(String text, String key) throws Exception {
        if (key == null || key.length() != 16) {
            throw new Exception("bad aes key configured");
        }
        Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);

        byte[] array = cipher.doFinal(text.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : array) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    public static synchronized String decrypt(String text, String key) throws Exception {
        if (key == null || key.length() != 16) {
            throw new Exception("bad aes key configured");
        }
        Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, aesKey);

        int len = text.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(text.charAt(i), 16) << 4) + Character.digit(text.charAt(i +
                    1), 16));
        }
        return new String(cipher.doFinal(data));
    }

    public static synchronized String encrypt(String cad) {
        int l = cad.length();
        String result = "";
        for (int i = 0; i < l; i++) {
            int das = (cad.charAt(i)) ^ 0xAA;
            result += (char) (das);
        }
        return result;
    }

    public static synchronized String decrypt(String cad) {
        int l = cad.length();
        String result = "";
        for (int i = 0; i < l; i++) {
            int das = (cad.charAt(i)) ^ 0xAA;
            result += (char) (das);
        }
        return result;
    }

    public static String obtenerExtension(String nombre) {
        String ext = "";
        boolean encontroPto = false;
        int l = nombre.length() - 1;
        for (int i = l; i >= 0; i--) {
            char a = nombre.charAt(i);
            if (a == '/') break;
            if (a == '.') {
                encontroPto = true;
                break;
            } else ext = a + ext;
        }
        if (!encontroPto) ext = "nullGi";
        return ext.toLowerCase();
    }


    public static int calcularCantLimPostXDia(int cantSeguidores) {
        if (YouChatApplication.correo.equals("darimelody@nauta.cu")) return 20;
        else if (YouChatApplication.correo.equals("jperez95@nauta.cu")) return 20;
        else if (YouChatApplication.correo.equals("anthonyruiz@nauta.cu")) return 20;
        else if (YouChatApplication.correo.equals("yoelplasencia1996@nauta.cu")) return 20;

        else if (YouChatApplication.comprobarOficialidad(YouChatApplication.correo)) return 7;
        else if (YouChatApplication.es_beta_tester) return 6;
        else if (cantSeguidores >= YouChatApplication.usuMayor) return 5;
        else if (cantSeguidores >= YouChatApplication.usuMedio) return 4;
        else if (cantSeguidores >= YouChatApplication.usuMenor) return 3;
        return 2;
    }

    public static int calcularCantLimComentarioPostXDia(int cantSeguidores) {
        if (YouChatApplication.comprobarOficialidad(YouChatApplication.correo)) return 10;
        else if (YouChatApplication.es_beta_tester) return 9;
        else if (cantSeguidores >= YouChatApplication.usuMayor) return 8;
        else if (cantSeguidores >= YouChatApplication.usuMedio) return 7;
        else if (cantSeguidores >= YouChatApplication.usuMenor) return 6;
        return 5;
    }

    public static Dialog mostrarDialogCarga(Fragment fragment, Context context, String text) {
        Dialog dialogo = new Dialog(context);
        dialogo.requestWindowFeature(1);
        View mviewe = fragment.getLayoutInflater().inflate(R.layout.dialog_alert_progress, null);
        dialogo.setContentView(mviewe);

        TextView texto_alerta = mviewe.findViewById(R.id.texto_alerta);
        texto_alerta.setText("" + text);

        dialogo.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialogo.setCancelable(false);
        dialogo.show();
        return dialogo;
    }

    public static void cerrarDialogCarga(Dialog dialogo) {
        if (dialogo != null && dialogo.isShowing()) dialogo.dismiss();
    }

    public static void mostrarToastDeConexion(MainActivity mainActivity) {
        Utils.ShowToastAnimated(mainActivity, "Compruebe su conexión", R.raw.ic_ban);
    }

    public static synchronized void resetearImageFondoBitmap(Context context) {
        if (YouChatApplication.hacerBlurFondo) {
            Utils.runOnUIThread(() -> {
                String ruta_fondo_chat = YouChatApplication.ruta_fondo_chat;
                int ruta_drawable = YouChatApplication.ruta_drawable;
                if (ruta_drawable == -1 && new File(ruta_fondo_chat).exists()) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.outWidth = YouChatApplication.anchoPantalla;
                    options.outHeight = YouChatApplication.largoPantalla;
                    options.inSampleSize = 2;

                    YouChatApplication.imageFondoBlur = BlurBuilder.blur(context,
                            BitmapFactory.decodeFile(ruta_fondo_chat, options));
                } else {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.outWidth = YouChatApplication.anchoPantalla;
                    options.outHeight = YouChatApplication.largoPantalla;
                    options.inSampleSize = 2;

                    YouChatApplication.imageFondoBlur = BlurBuilder.blur(context,
                            BitmapFactory.decodeResource(context.getResources(),
                                    Utils.obtenerDrawable(ruta_drawable), options));
                }
            });
        }
    }

    public static synchronized void cargarFondo(Context context, ImageView imageView) {
        if (context == null) return;
        if (YouChatApplication.hacerBlurFondo) {
            if (YouChatApplication.imageFondoBlur != null) {
                Glide.with(context).load(YouChatApplication.imageFondoBlur)
                        .transition(withCrossFade())
                        .into(imageView);
            } else {
                Utils.runOnUIThread(() -> {
                    String ruta_fondo_chat = YouChatApplication.ruta_fondo_chat;
                    int ruta_drawable = YouChatApplication.ruta_drawable;
                    if (ruta_drawable == -1 && new File(ruta_fondo_chat).exists()) {

                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.outWidth = YouChatApplication.anchoPantalla;
                        options.outHeight = YouChatApplication.largoPantalla;
                        options.inSampleSize = 2;

                        YouChatApplication.imageFondoBlur = BlurBuilder.blur(context,
                                BitmapFactory.decodeFile(ruta_fondo_chat, options));
                    } else {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.outWidth = YouChatApplication.anchoPantalla;
                        options.outHeight = YouChatApplication.largoPantalla;
                        options.inSampleSize = 2;

                        YouChatApplication.imageFondoBlur = BlurBuilder.blur(context,
                                BitmapFactory.decodeResource(imageView.getResources(),
                                        Utils.obtenerDrawable(ruta_drawable), options));
                    }


                    if (YouChatApplication.imageFondoBlur != null) {
                        Glide.with(context).load(YouChatApplication.imageFondoBlur)
                                .transition(withCrossFade())
                                .into(imageView);
                    } else {
                        YouChatApplication.setHacerBlurFondo(false);
                        cargarFondo(context, imageView);
                    }
                });
            }
        } else {
            String ruta_fondo_chat = YouChatApplication.ruta_fondo_chat;
            int ruta_drawable = YouChatApplication.ruta_drawable;
            if (ruta_drawable == -1) {
                Glide.with(context)
                        .load(ruta_fondo_chat)
                        .transition(withCrossFade())
                        .error(R.drawable.background_1)
                        .into(imageView);
            } else {
                Glide.with(context).load(Utils.obtenerDrawable(ruta_drawable))
                        .transition(withCrossFade()).into(imageView);
            }
        }
    }

    public static int obtenerDrawable(int ruta_drawable) {
        switch (ruta_drawable) {
            case 0:
                return R.drawable.background_1;
            case 1:
                return R.drawable.background_2;
            case 2:
                return R.drawable.background_3;
            case 3:
                return R.drawable.background_4;
            case 4:
                return R.drawable.background_5;
            case 5:
                return R.drawable.background_6;
            case 6:
                return R.drawable.background_7;
            case 7:
                return R.drawable.background_8;
            default:
                return R.drawable.background_1;
        }
    }

    public static int obtenerColorDadoUnCorreo(String correo) {
        int valor = 0;
        int l = correo.length();
        for (int i = 0; i < l; i++) {
            char a = correo.charAt(i);
            if (a == '@') break;
            valor += a;
        }
        return obtenerColorRandom(valor);
    }

    public static int[] getDominantColor(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int size = width * height;
        int pixels[] = new int[size];

        Bitmap bitmap1 = bitmap.copy(Bitmap.Config.ARGB_4444, false);
        bitmap1.getPixels(pixels, 0, width, 0, 0, width, height);

        final List<HashMap<Integer, Integer>> colorMap = new ArrayList<HashMap<Integer, Integer>>();
        colorMap.add(new HashMap<Integer, Integer>());
        colorMap.add(new HashMap<Integer, Integer>());
        colorMap.add(new HashMap<Integer, Integer>());

        int color = 0;
        int r = 0;
        int g = 0;
        int b = 0;
        Integer rC, gC, bC;

        for (int i = 0; i < pixels.length; i++) {
            color = pixels[i];

            r = Color.red(color);
            g = Color.green(color);
            b = Color.blue(color);

            rC = colorMap.get(0).get(r);
            if (rC == null) rC = 0;
            colorMap.get(0).put(r, ++rC);

            gC = colorMap.get(1).get(g);
            if (gC == null) gC = 0;
            colorMap.get(1).put(g, ++gC);

            bC = colorMap.get(2).get(b);
            if (bC == null) bC = 0;
            colorMap.get(2).put(b, ++bC);
        }
        int[] rgb = new int[3];
        for (int i = 0; i < 3; i++) {
            int max = 0;
            int val = 0;
            for (Map.Entry<Integer, Integer> entry : colorMap.get(i).entrySet()) {
                if (entry.getValue() > max) {
                    max = entry.getValue();
                    val = entry.getKey();
                }
            }
            rgb[i] = val;
        }
//        Color.rgb(r,g,b);
        return rgb;
    }

    public static int obtenerColorRandom(int valor) {
        switch (valor % 17) {
            case 0:
                return Color.parseColor("#E53935");
            case 1:
                return Color.parseColor("#D81B60");
            case 2:
                return Color.parseColor("#8E24AA");
            case 3:
                return Color.parseColor("#5E35B1");
            case 4:
                return Color.parseColor("#3949AB");
            case 5:
                return Color.parseColor("#1E88E5");
            case 6:
                return Color.parseColor("#039BE5");
            case 7:
                return Color.parseColor("#00ACC1");
            case 8:
                return Color.parseColor("#00897B");
            case 9:
                return Color.parseColor("#43A047");
            case 10:
                return Color.parseColor("#7CB342");
            case 11:
                return Color.parseColor("#C0CA33");
            case 12:
                return Color.parseColor("#FDD835");
            case 13:
                return Color.parseColor("#FFB300");
            case 14:
                return Color.parseColor("#FB8C00");
            case 15:
                return Color.parseColor("#F4511E");
            case 16:
                return Color.parseColor("#886558");
            default:
                return Color.parseColor("#E53935");
        }
    }

    public static int obtenerTipoDadounaExtension(String extFile) {
        int tipo;
        if (SimpleFileExplorerFragment.AUDIO.contains(extFile)) tipo = 1;
        else if (SimpleFileExplorerFragment.APK.contains(extFile)) tipo = 2;
        else if (SimpleFileExplorerFragment.VIDEO.contains(extFile)) tipo = 3;
        else if (SimpleFileExplorerFragment.IMAGEN.contains(extFile)) tipo = 4;
        else if (SimpleFileExplorerFragment.TXT.contains(extFile)) tipo = 5;
        else if (SimpleFileExplorerFragment.GIF.contains(extFile)) tipo = 6;
        else if (SimpleFileExplorerFragment.COMPRESS.contains(extFile)) tipo = 7;
        else if (SimpleFileExplorerFragment.XML.contains(extFile)) tipo = 8;
        else if (SimpleFileExplorerFragment.PDF.contains(extFile)) tipo = 9;
        else if (extFile.contains("tgs")) tipo = 10;
        else tipo = 0;
        return tipo;
    }

    public static boolean copyFile(InputStream sourceFile, File destFile) throws IOException {
        OutputStream out = new FileOutputStream(destFile);
        byte[] buf = new byte[4096];
        int len;
        while ((len = sourceFile.read(buf)) > 0) {
            Thread.yield();
            out.write(buf, 0, len);
        }
        out.close();
        return true;
    }

    public static boolean guardarEnGaleria(String ruta) {
        File origen = new File(ruta);
        if (!origen.exists() || origen.isDirectory()) return false;
        File newRuta = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + File.separator + "YouChat");
        boolean exist = newRuta.exists();
        if (!exist) exist = newRuta.mkdirs();
        if (exist) {
            File destino = new File(newRuta, origen.getName());
            boolean seCopio = false;
            try {
                seCopio = cu.alexgi.youchat.Utils.copyFile(origen, destino);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return seCopio;
        }
        return false;
    }

    public static boolean copyFile(File sourceFile, File destFile) throws IOException {
        if (sourceFile.equals(destFile)) {
            return true;
        }
        if (!destFile.exists()) {
            destFile.createNewFile();
        }
        try (FileInputStream source = new FileInputStream(sourceFile);
             FileOutputStream destination = new FileOutputStream(destFile)) {
            destination.getChannel().transferFrom(source.getChannel(), 0, source.getChannel().size());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static Bitmap tomarImagenDeVista(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public static int obtenerRawActualDePos(int pos) {
        switch (pos) {
            case 0:
                return R.raw.like1;
            case 1:
                return R.raw.encanta;
            case 2:
                return R.raw.sonroja;
            case 3:
                return R.raw.divierte;
            case 4:
                return R.raw.asombra;
            case 5:
                return R.raw.entristece;
            case 6:
                return R.raw.enoja;
            case 7:
                return R.raw.wallet_congrats;
            case 8:
                return R.raw.wallet_science;
            case 9:
                return R.raw.tsv_setup_intro;
            case 10:
                return R.raw.new_typing_in_the_office;
            case 11:
                return R.raw.night_good;
            case 12:
                return R.raw.halloweenkin6;
            case 13:
                return R.raw.day_women;
            case 14:
                return R.raw.wallet_allset;
            case 15:
                return R.raw.tsv_setup_mail;
            case 16:
                return R.raw.gift;
            case 17:
                return R.raw.tsv_setup_email_sent;
            case 18:
                return R.raw.tsv_setup_hint;
            default:
                return R.raw.like1;
        }
    }

    public static void borrarFile(@NonNull File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                String[] hijos = file.list();
                for (int i = 0; i < hijos.length; i++) {
                    String hijo = hijos[i];
                    File temp = new File(file, hijo);
                    if (temp.isDirectory()) borrarFile(temp);
                    if (!temp.delete())
                        temp.deleteOnExit();
                }
            }
            if (!file.delete())
                file.deleteOnExit();
        }
    }

    public static synchronized int[] obtenerAnchoLargo(String ruta, float maxAnchoImagen, float maxLargoImagen) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.outWidth = YouChatApplication.anchoPantalla / 2;
            options.outHeight = YouChatApplication.largoPantalla / 2;

            Bitmap bitmap = BitmapFactory.decodeFile(ruta, options);
            if (bitmap != null) {
                int ancho = bitmap.getWidth();
                int largo = bitmap.getHeight();
                if (largo > ancho) {
                    ancho = Math.round(maxLargoImagen * ancho / largo);
                    largo = (int) maxLargoImagen;
                } else {
                    largo = Math.round(maxAnchoImagen * largo / ancho);
                    ancho = (int) maxAnchoImagen;
                }
                return new int[]{ancho, largo};
            } else return new int[]{(int) maxAnchoImagen, (int) maxAnchoImagen};
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new int[]{(int) maxAnchoImagen, (int) maxAnchoImagen};
    }

    public static synchronized int[] obtenerAnchoLargo(String ruta) {
        float maxAnchoImagen = (float) YouChatApplication.anchoPantalla * 0.6f;
        float maxLargoImagen = (float) YouChatApplication.largoPantalla * 0.5f;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.outWidth = YouChatApplication.anchoPantalla / 2;
            options.outHeight = YouChatApplication.largoPantalla / 2;

            Bitmap bitmap = BitmapFactory.decodeFile(ruta, options);
            if (bitmap != null) {
                int ancho = bitmap.getWidth();
                int largo = bitmap.getHeight();
                if (largo > ancho) {
                    ancho = Math.round(maxLargoImagen * ancho / largo);
                    largo = (int) maxLargoImagen;
                } else {
                    largo = Math.round(maxAnchoImagen * largo / ancho);
                    ancho = (int) maxAnchoImagen;
                }
                return new int[]{ancho, largo};
            } else return new int[]{(int) maxAnchoImagen, (int) maxAnchoImagen};
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new int[]{(int) maxAnchoImagen, (int) maxAnchoImagen};
    }

    public static void crearArchivoNoMedia(String rutaCarpeta) {
        if (new File(rutaCarpeta, ".nomedia").exists()) return;
        File file = new File(rutaCarpeta);
        boolean exist = file.exists();
        if (!exist) exist = file.mkdirs();
        if (exist) {
            if (file.isDirectory()) {
                File nomedia = new File(file, ".nomedia");
                try {
                    nomedia.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static int obtenerEmojiAnimado(String idSticker) {
        switch (idSticker) {
            case "1":
                return R.raw.like1;
            case "2":
                return R.raw.encanta;
            case "3":
                return R.raw.sonroja;
            case "4":
                return R.raw.divierte;
            case "5":
                return R.raw.asombra;
            case "6":
                return R.raw.entristece;
            case "7":
                return R.raw.enoja;
            case "8":
                return R.raw.wallet_allset;
            case "9":
                return R.raw.wallet_congrats;
            default:
                return R.raw.like1;
        }
    }

//    private Bitmap getBitmapFromUri(Context context, Uri uri) throws IOException {
//        ParcelFileDescriptor parcelFileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r");
//        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
//        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
//        parcelFileDescriptor.close();
//        return image;
//    }

//    public static void SavePhotoUri(Context context, Uri imageuri, String Filename) {
//        File FilePath = context.getDir(Environment.DIRECTORY_PICTURES, Context.MODE_PRIVATE);
//        try {
//            Bitmap selectedImage = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageuri);
//            String destinationImagePath = FilePath + "/" + Filename;
//            FileOutputStream destination = new FileOutputStream(destinationImagePath);
//            selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, destination);
//            destination.close();
//        } catch (Exception e) {
//            Log.e("error", e.toString());
//        }
//    }


    public static boolean esUnColor(String color){
        if(color==null || color.isEmpty()) return false;
        if(color.charAt(0)!='#') return false;
        int l = color.length();
        if(l!=9) return false;
        color = color.toLowerCase();
        for(int i=1; i<l; i++){
            char a = color.charAt(i);
            if(Character.isDigit(a)) continue;
            if(Character.isLetter(a) && (a>='a' && a<='f')) continue;
            return false;
        }
        return true;
    }
}


