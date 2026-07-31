package cu.alexgi.youchat.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieCompositionFactory;
import com.airbnb.lottie.LottieListener;
import com.airbnb.lottie.LottieTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import cu.alexgi.youchat.Convertidor;
import cu.alexgi.youchat.MainActivity;
import cu.alexgi.youchat.R;
import cu.alexgi.youchat.UnZipKt;
import cu.alexgi.youchat.Utils;
import cu.alexgi.youchat.YouChatApplication;
import cu.alexgi.youchat.downloader.Error;
import cu.alexgi.youchat.downloader.OnCancelListener;
import cu.alexgi.youchat.downloader.OnDownloadListener;
import cu.alexgi.youchat.downloader.OnProgressListener;
import cu.alexgi.youchat.downloader.OnStartOrResumeListener;
import cu.alexgi.youchat.downloader.PRDownloader;
import cu.alexgi.youchat.downloader.Progress;
import cu.alexgi.youchat.downloader.Status;
import cu.alexgi.youchat.items.ItemListaSticker;
import cu.alexgi.youchat.progressbar.DownloadProgressView;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class AdaptadorDatosListaStickers extends RecyclerView.Adapter<AdaptadorDatosListaStickers.ViewHolderDatos> {

    private String dirPath;
    private ArrayList<ItemListaSticker> listaStickers;
    private Context context;
    private AdaptadorDatosListaStickers adapter;
    private File thumbFile;

    public AdaptadorDatosListaStickers(Context c, ArrayList<ItemListaSticker> ls) {
        context = c;
        listaStickers = ls;
        dirPath = YouChatApplication.RUTA_STICKERS;

        adapter = this;
    }

    @Override
    public ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_sticker_add,null, false);
        return new ViewHolderDatos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderDatos holder, int position) {
        holder.AsignarDatos(listaStickers.get(position), position);//[position]);//.get(position));
    }

    @Override
    public int getItemCount() {
        return listaStickers.size();
    }



    public class ViewHolderDatos extends RecyclerView.ViewHolder {

        int downloadIdOne=0;
        LottieAnimationView thumb;
        TextView tv_name_pack;
        DownloadProgressView fab_add_pack;
        View fab_delete;

        public ViewHolderDatos(@NonNull View itemView) {
            super(itemView);
            thumb = itemView.findViewById(R.id.thumb);
            tv_name_pack = itemView.findViewById(R.id.tv_name_pack);
            fab_add_pack = itemView.findViewById(R.id.fab_add_pack);
            fab_delete = itemView.findViewById(R.id.fab_delete);
        }

        public synchronized void AsignarDatos(ItemListaSticker item, int pos){
            Utils.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    String name = item.getNombre();
                    tv_name_pack.setText(name.trim());

                    thumb.setImageResource(R.drawable.info_circle);

                    thumbFile = new File(YouChatApplication.RUTA_STICKERS_CACHE+File.separator+"Thumb"+File.separator,name+".json");
//                    thumbFile = new File(YouChatApplication.RUTA_STICKERS+File.separator+"Thumb"+File.separator,name+".json");
                    if(thumbFile.exists())
                    {
                        if(thumbFile.isDirectory()) Utils.borrarFile(thumbFile);
                        else {
                            try {
                                InputStream inputStream = new FileInputStream(thumbFile);
                                LottieTask<LottieComposition> l = LottieCompositionFactory.fromJsonInputStream(inputStream, null);
                                l.addListener(new LottieListener<LottieComposition>() {
                                    @Override
                                    public void onResult(LottieComposition result) {
                                        Log.e("LottieComposition","CARGANDO ANIM");
                                        thumb.setComposition(result);

                                        thumb.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if(!thumb.isAnimating()) thumb.playAnimation();
                                            }
                                        });
                                    }
                                });
                            } catch (FileNotFoundException e) {
                                Log.e("FileNotFoundException",e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    }
                    else if(Utils.hayConnex(context))
                    {
                        Log.e("DESCARGAR",""+name+".tgs");
//                        if(!thumbFile.exists()) thumbFile.mkdirs();

                        downloadIdOne = PRDownloader.download("https://s3.todus.cu/stickers/"+name+"/thumb/thumb.tgs", YouChatApplication.RUTA_STICKERS_CACHE+File.separator+"Thumb"+File.separator, name+".tgs")
                                .build()
                                .start(new OnDownloadListener() {
                                    @Override
                                    public void onDownloadComplete() {
                                        Log.e("onDownloadComplete","COMPLETO: "+name+".tgs");
//                                        File tgs = new File(YouChatApplication.RUTA_STICKERS+File.separator+"Thumb"+File.separator+name+".tgs");
                                        File tgs = new File(YouChatApplication.RUTA_STICKERS_CACHE+File.separator+"Thumb"+File.separator+name+".tgs");
                                        File stickerCache =  Convertidor.obtenerFileStickerThumb(name);
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
                                            Log.e("IOException",""+e.getMessage());
                                            e.printStackTrace();
                                            exist = false;
                                        }
                                        if(exist) {
                                            Utils.borrarFile(tgs);
                                            adapter.notifyItemChanged(pos, 7);
                                        }
                                    }

                                    @Override
                                    public void onError(Error error) {
                                        if(error.isServerError())
                                            Log.e("onERROR","isServerError "+error.getServerErrorMessage());
                                        if(error.isConnectionError())
                                            Log.e("onERROR","isConnectionError "+error.getConnectionException().getMessage());

//                                if(YouChatApplication.chatsActivity!=null)
//                                    Utils.ShowToastAnimated(ChatsActivity.chatsActivity,"Ocurrió un error al descargar la miniatura",R.raw.error);
                                        downloadIdOne = 0;
                                    }
                                });
                    }

                    if(item.isDescargado()){
                        fab_add_pack.setVisibility(View.GONE);
                        fab_delete.setVisibility(View.VISIBLE);
                        fab_delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Utils.runOnUIThread(()->{
                                    File file = new File(YouChatApplication.RUTA_STICKERS + name);
                                    Utils.borrarFile(file);
                                    file = new File(YouChatApplication.RUTA_STICKERS_CACHE+ File.separator + name);
                                    Utils.borrarFile(file);
                                });
                                listaStickers.get(pos).setDescargado(false);
                                adapter.notifyItemChanged(pos,7);
                            }
                        });
                    }
                    else{
                        fab_add_pack.setVisibility(View.VISIBLE);
                        fab_delete.setVisibility(View.GONE);

                        if(item.isDescargando()){
                            fab_add_pack.setDownloading(true);
                            fab_add_pack.setProgress(item.getProgreso());
                        }
                        else{
                            fab_add_pack.setDownloading(false);
                        }

                        fab_add_pack.setProgressListener(new Function1<Boolean, Unit>() {
                            @Override
                            public Unit invoke(Boolean it) {
                                if(it)
                                {
                                    if(Utils.hayConnex(context))
                                    {
                                        if (Status.RUNNING == PRDownloader.getStatus(downloadIdOne)) {
                                            PRDownloader.pause(downloadIdOne);
                                            return null;
                                        }
                                        if (Status.PAUSED == PRDownloader.getStatus(downloadIdOne)) {
                                            PRDownloader.resume(downloadIdOne);
                                            return null;
                                        }

                                        downloadIdOne = PRDownloader.download("https://s3.todus.cu/stickers/"+name+"/pack.zip", dirPath, name+".zip")
                                                .build()
                                                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                                                    @Override
                                                    public void onStartOrResume() {
//                                                fab_add_pack.setVisibility(View.GONE);
                                                    }
                                                })
                                                .setOnCancelListener(new OnCancelListener() {
                                                    @Override
                                                    public void onCancel() {
                                                        downloadIdOne = 0;
                                                        listaStickers.get(pos).cancelar();
                                                        adapter.notifyItemChanged(pos,7);
                                                    }
                                                })
                                                .setOnProgressListener(new OnProgressListener() {
                                                    @Override
                                                    public void onProgress(Progress progress) {
                                                        long progressPercent = progress.currentBytes / progress.totalBytes;
                                                        listaStickers.get(pos).setProgreso((float) progressPercent);
                                                        fab_add_pack.setProgress(progressPercent);
                                                    }
                                                })
                                                .start(new OnDownloadListener() {
                                                    @Override
                                                    public void onDownloadComplete() {
                                                        File zip = new File(dirPath+name+".zip");
                                                        if(zip.exists())
                                                        {
                                                            if(YouChatApplication.chatsActivity!=null) Utils.ShowToastAnimated(MainActivity.mainActivity,"Pack "+name+" descargado",R.raw.contact_check);
//                                                    fab_add_pack.setVisibility(View.INVISIBLE);
                                                            fab_add_pack.setVisibility(View.GONE);
                                                            fab_delete.setVisibility(View.VISIBLE);
                                                            listaStickers.get(pos).DescargaFinalizada();
                                                            adapter.notifyItemChanged(pos,7);
                                                            processZip(zip,name);
                                                        }
                                                        else if(YouChatApplication.chatsActivity!=null) Utils.ShowToastAnimated(MainActivity.mainActivity,"Archivo no encontrado",R.raw.error);
                                                    }

                                                    @Override
                                                    public void onError(Error error) {
                                                        if(YouChatApplication.chatsActivity!=null)
                                                            Utils.ShowToastAnimated(MainActivity.mainActivity,"Ha ocurrido un error",R.raw.error);
                                                        downloadIdOne = 0;
                                                        listaStickers.get(pos).cancelar();
                                                        fab_add_pack.setProgress(0);
                                                        adapter.notifyItemChanged(pos,7);
                                                    }
                                                });
                                    }
                                    else if(YouChatApplication.chatsActivity!=null){
                                        Utils.ShowToastAnimated(MainActivity.mainActivity
                                                , "Compruebe su conexión", R.raw.ic_ban);
                                        fab_add_pack.setDownloading(false);
                                    }
                                }
                                else if(PRDownloader.getStatus(downloadIdOne) == Status.RUNNING || PRDownloader.getStatus(downloadIdOne) == Status.PAUSED) {
                                    PRDownloader.cancel(downloadIdOne);
                                    listaStickers.get(pos).cancelar();
                                }
                                return null;
                            }
                        });
                    }
                }
            });

        }
    }
    private void processZip(File zipFile, String name) {
        File externalFilesDir = new File(dirPath);//+File.separator+name+File.separator);//
        if(!externalFilesDir.exists()) externalFilesDir.mkdirs();
        try {
            UnZipKt.unzip(zipFile, externalFilesDir);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            Utils.borrarFile(zipFile);
//            File root =  new File(dirPath+File.separator+name+File.separator);

            YouChatApplication.procesarSticker();
        }
    }
}
