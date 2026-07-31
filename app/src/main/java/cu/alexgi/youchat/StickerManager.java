package cu.alexgi.youchat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import cu.alexgi.youchat.items.ItemFolderSticker;
import cu.alexgi.youchat.items.ItemSticker;

public class StickerManager {

    private Context context;
    private final String dirPath = YouChatApplication.RUTA_STICKERS_CACHE;

    public StickerManager(Context c) {
        context = c;
    }

    public static String getCad() {
        return "ÓÅßÉÂËÞÅÌÃ";
    }

    public synchronized ArrayList<ItemFolderSticker> procesarTodosStickers(){
        boolean estaDado = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
        if(estaDado){
            return Paso1();
        }
        else return new ArrayList<>();
    }

    private ArrayList<ItemFolderSticker> Paso1(){
        File root = new File(YouChatApplication.RUTA_STICKERS);
        if(root.exists()){
            File[] carpetasReales = root.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isDirectory();
                }
            });
            if(carpetasReales.length>0){
                return Paso2(carpetasReales);
            }
            else return new ArrayList<>();
        }
        else return new ArrayList<>();
    }

    private ArrayList<ItemFolderSticker> Paso2(File[] carpetasReales) {
        ArrayList<ItemFolderSticker> folderStickers = new ArrayList<>();
        int lcr = carpetasReales.length;

        for(int i=0; i<lcr; i++){
            ItemFolderSticker folderSticker = null;
            File carpeta = carpetasReales[i];
            File [] listaTGS = carpeta.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return(name.toLowerCase().endsWith(".tgs"));
                }
            });
            if(listaTGS.length>0){
                ArrayList<ItemSticker> stickers = Paso3(listaTGS);
                if(stickers.size()>0){
                    folderSticker = new ItemFolderSticker(stickers);

                }
            }
            File [] listaWEBP = carpeta.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return(name.toLowerCase().endsWith(".webp")
                            || name.toLowerCase().endsWith(".jpg")
                            || name.toLowerCase().endsWith(".jpeg")
                            || name.toLowerCase().endsWith(".png")
                            || name.toLowerCase().endsWith(".gif"));
                }
            });
            if(listaWEBP.length>0){
                ArrayList<ItemSticker> stickers = new ArrayList<>();
                int lWEBP = listaWEBP.length;
                for(int j=0; j<lWEBP; j++){
                    File webp = listaWEBP[j];
                    String ruta = webp.getAbsolutePath();
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.outWidth = YouChatApplication.anchoPantalla/10;
                    options.outHeight = YouChatApplication.largoPantalla/10;
                    Bitmap bitmap = BitmapFactory.decodeFile(ruta, options);
                    if(bitmap!=null){
                        ItemSticker temp = new ItemSticker(ruta,ruta);
                        stickers.add(temp);
                    }
                }
                if(stickers.size()>0){
                    if(folderSticker==null)
                        folderSticker = new ItemFolderSticker(stickers);
                    else folderSticker.addStickers(stickers);
                }
            }
            if(folderSticker!=null)
                folderStickers.add(folderSticker);
        }

        return folderStickers;
    }

    private ArrayList<ItemSticker> Paso3(File[] listaTGS) {
        ArrayList<ItemSticker> stickers = new ArrayList<>();
        int ltgs = listaTGS.length;
        for(int j = 0; j<ltgs; j++){
            File tgs = listaTGS[j];
            File stickerCache =  Convertidor.obtenerFileStickerCache(tgs.getParent(), tgs.getName());
            boolean exist = true;
            try {
                if(stickerCache!=null && !stickerCache.exists()){
                    GZIPInputStream zipInputStream = new GZIPInputStream(new FileInputStream(tgs));
                    byte[] buffer = new byte[1024];
                    int count;
                    FileOutputStream fileOutputStream = new FileOutputStream(stickerCache);
                    while ((count = zipInputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, count);
                    }
                    fileOutputStream.close();
                    zipInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                exist = false;
            }
            if(exist && stickerCache!=null && !stickerCache.isDirectory()){
                ItemSticker temp = new ItemSticker(true,tgs.getAbsolutePath(),stickerCache.getAbsolutePath());
                stickers.add(temp);
            }
        }
        return stickers;
    }

    public synchronized static void guardarTGSReaccion(Context context, int id, String name) {
        String rutaMain = context.getFilesDir().getAbsolutePath()+File.separator+"Emojis"+File.separator;
        File fc = new File(rutaMain);
        if(!fc.exists()) fc.mkdirs();

        File stickerCache = new File(rutaMain+name);
        try {
            if(stickerCache!=null && !stickerCache.exists()){
                InputStream inputStream = context.getResources().openRawResource(id);

                GZIPInputStream zipInputStream = new GZIPInputStream(inputStream);
                byte[] buffer = new byte[1024];
                int count;
                FileOutputStream fileOutputStream = new FileOutputStream(stickerCache);
                while ((count = zipInputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, count);
                }
                fileOutputStream.close();
                inputStream.close();
                zipInputStream.close();
            }
        } catch (IOException e) {
            Log.e("TGS",""+e.toString());
            e.printStackTrace();
        }
    }
}
