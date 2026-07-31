package cu.alexgi.youchat.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieCompositionFactory;
import com.airbnb.lottie.LottieListener;
import com.airbnb.lottie.LottieTask;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ViewTarget;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.search.SubjectTerm;

import cu.alexgi.youchat.R;
import cu.alexgi.youchat.Utils;
import cu.alexgi.youchat.YouChatApplication;
import cu.alexgi.youchat.items.ItemSticker;
import io.reactivex.disposables.CompositeDisposable;

import static cu.alexgi.youchat.MainActivity.context;
import static cu.alexgi.youchat.MainActivity.mainActivity;

public class AdaptadorDatosStickers extends RecyclerView.Adapter<AdaptadorDatosStickers.ViewHolderDatos> {

    private static CompositeDisposable disposable;
    private ArrayList<ItemSticker> itemStickers;

    public AdaptadorDatosStickers(ArrayList<ItemSticker> is) {
        itemStickers = is;
        disposable = new CompositeDisposable();
    }

    @Override
    public ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_sticker_list,null, false);
        return new ViewHolderDatos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderDatos holder, int position) {
        holder.AsignarDatos(itemStickers.get(position));//.get(position));
    }

    @Override
    public int getItemCount() {
        return itemStickers.size();
    }


    public static class ViewHolderDatos extends RecyclerView.ViewHolder {
        LottieAnimationView stickerAnimation;

        public ViewHolderDatos(@NonNull View itemView) {
            super(itemView);
            stickerAnimation = itemView.findViewById(R.id.stickerAnimation);
        }

        public synchronized void AsignarDatos(ItemSticker sticker){
            Utils.runOnUIThread(()->{
                if(sticker.isTGS()){
//                    File file = new File(sticker.getRutaCache());
//                    if(file.exists()){
//                        CargarTGSAsyncTask aaa = new CargarTGSAsyncTask(sticker, stickerAnimation);
//                        aaa.execute();
//                    }
//                    else {
//                        stickerAnimation.setOnClickListener(null);
//                        stickerAnimation.setOnLongClickListener(null);
//                    }
                    Glide.with(stickerAnimation)
                            .load(R.drawable.sticker_empty_focus_gris)
                            .into(stickerAnimation);
                    File file = new File(sticker.getRutaCache());
                    if(file.exists()){
                        try {
                            InputStream inputStream = new FileInputStream(file);
                            LottieTask<LottieComposition> l = LottieCompositionFactory
                                    .fromJsonInputStream(inputStream, sticker.getRutaOriginal());
                            l.addListener(new LottieListener<LottieComposition>() {
                                @Override
                                public void onResult(LottieComposition result) {
                                    stickerAnimation.setComposition(result);

                                    stickerAnimation.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if(!stickerAnimation.isAnimating()){
                                                if(YouChatApplication.chatsActivity!=null)
                                                    YouChatApplication.chatsActivity.enviarSticker(sticker.getRutaOriginal());
                                                else if(YouChatApplication.chatsActivityCorreo!=null)
                                                    YouChatApplication.chatsActivityCorreo.enviarSticker(sticker.getRutaOriginal());
                                            }
                                        }
                                    });
                                    stickerAnimation.setOnLongClickListener(new View.OnLongClickListener() {
                                        @Override
                                        public boolean onLongClick(View v) {
                                            stickerAnimation.playAnimation();
                                            return true;
                                        }
                                    });
                                }
                            });
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        stickerAnimation.setOnClickListener(null);
                        stickerAnimation.setOnLongClickListener(null);
                    }
                }
                else {
                    Glide.with(stickerAnimation)
                            .load(sticker.getRutaOriginal())
                            .placeholder(R.drawable.sticker_empty_focus_gris)
                            .error(R.drawable.sticker_empty_focus_gris)
                            .into(stickerAnimation);
                    stickerAnimation.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(!stickerAnimation.isAnimating()){
                                if(YouChatApplication.chatsActivity!=null)
                                    YouChatApplication.chatsActivity.enviarSticker(sticker.getRutaOriginal());
                                else if(YouChatApplication.chatsActivityCorreo!=null)
                                    YouChatApplication.chatsActivityCorreo.enviarSticker(sticker.getRutaOriginal());
                            }
                        }
                    });
                    stickerAnimation.setOnLongClickListener(null);
                }
            });
        }

        private class CargarTGSAsyncTask extends AsyncTask<String, String, String> {

            private ItemSticker sticker;
            private LottieAnimationView stickerAnimation;

            public CargarTGSAsyncTask(ItemSticker s, LottieAnimationView sA){
                sticker = s;
                stickerAnimation = sA;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                try {
                    File file = new File(sticker.getRutaCache());
                    InputStream inputStream = new FileInputStream(file);
                    LottieTask<LottieComposition> l = LottieCompositionFactory
                            .fromJsonInputStream(inputStream, sticker.getRutaOriginal());
                    l.addListener(new LottieListener<LottieComposition>() {
                        @Override
                        public void onResult(LottieComposition result) {
                            stickerAnimation.setComposition(result);
                            stickerAnimation.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(!stickerAnimation.isAnimating()){
                                        if(YouChatApplication.chatsActivity!=null)
                                            YouChatApplication.chatsActivity.enviarSticker(sticker.getRutaOriginal());
                                        else if(YouChatApplication.chatsActivityCorreo!=null)
                                            YouChatApplication.chatsActivityCorreo.enviarSticker(sticker.getRutaOriginal());
                                    }
                                }
                            });
                            stickerAnimation.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {
                                    stickerAnimation.playAnimation();
                                    return true;
                                }
                            });
                        }
                    });
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);
            }

            @Override
            protected void onPostExecute(String result){

            }
        }
    }
}
