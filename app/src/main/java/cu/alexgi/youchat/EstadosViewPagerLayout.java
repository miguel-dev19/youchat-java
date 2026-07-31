package cu.alexgi.youchat;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.chrisbanes.photoview.PhotoView;
import com.vanniktech.emoji.EmojiTextView;

import java.io.File;

import cu.alexgi.youchat.items.ItemChat;
import cu.alexgi.youchat.items.ItemEstado;
import cu.alexgi.youchat.progressbar.DownloadProgressView;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

import static cu.alexgi.youchat.MainActivity.context;
import static cu.alexgi.youchat.MainActivity.dbWorker;
import static cu.alexgi.youchat.MainActivity.mainActivity;

public class EstadosViewPagerLayout extends Fragment {

    private static ItemEstado itemEstado;
    private static String idActual;

    private String correo;
    private boolean soyYo, entroNext=false, entroPrev=false, hayTexto=false, inTouch=false;
    private int posActual, tiempoTouch=0;
    private PhotoView estado_fondo;
//    private ImageView show_texto_estado_imagen;
    private EmojiTextView tv_texto_estado;
    private View view_descarga, root_estado;
    private TextView tv_tam_max_img;
    private DownloadProgressView progress_img;

    private static EstadosViewPagerFragment estadosViewPagerFragment;
    private static final String TAG= "EstadosViewPagerLayout";
    //private View input_estado_see_mi_image;
    long pressTime = 0L;
    long limit = 500L;

    public EstadosViewPagerLayout() {
        // Required empty public constructor
    }

    public static EstadosViewPagerLayout newInstance(EstadosViewPagerFragment e, ItemEstado i) {
        EstadosViewPagerLayout fragment = new EstadosViewPagerLayout();
        itemEstado = i;
        idActual = i.getId();
        estadosViewPagerFragment = e;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_estado_viewpager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View bottomSheetDialog, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(bottomSheetDialog, savedInstanceState);

        posActual = 0;
        soyYo = false;
        if(itemEstado!=null) correo = itemEstado.getCorreo();

        progress_img = bottomSheetDialog.findViewById(R.id.progress_img);
        tv_tam_max_img = bottomSheetDialog.findViewById(R.id.tv_tam_max_img);
        view_descarga = bottomSheetDialog.findViewById(R.id.view_descarga);

        root_estado = bottomSheetDialog.findViewById(R.id.root_estado);
        estado_fondo = bottomSheetDialog.findViewById(R.id.estados_fondo);
        tv_texto_estado = bottomSheetDialog.findViewById(R.id.tv_texto_estado);
//        contenedor_input_estado_reacciones = bottomSheetDialog.findViewById(R.id.contenedor_input_estado_reacciones);

        bottomSheetDialog.setOnLongClickListener(onLongClickListener);
        tv_texto_estado.setOnLongClickListener(onLongClickListener);
        estado_fondo.setOnLongClickListener(onLongClickListener);

//        show_texto_estado_imagen = bottomSheetDialog.findViewById(R.id.show_text);
        inTouch = false;
        if (correo.equals("")) onDestroy();

        else {
            if (correo.equals(YouChatApplication.correo)) {
                soyYo = true;
            }
            else {
                soyYo = false;
            }

//            ItemContacto contacto = dbWorker.obtenerContacto(correo);
//            if (contacto == null) {
//                contacto = new ItemContacto(correo, correo);
//            }
            procesoActualizarVista(true);
        }
    }
    private synchronized void procesoActualizarVista(boolean actual) {
        if(actual)
            itemEstado = dbWorker.obtenerEstado(idActual);
        hayTexto=false;
        view_descarga.setVisibility(View.GONE);
        tv_texto_estado.setText("");
        tv_texto_estado.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Roboto Medium.ttf"));
        tv_texto_estado.setVisibility(View.VISIBLE);
        estado_fondo.setImageResource(0);
        estado_fondo.setVisibility(View.INVISIBLE);
        root_estado.setBackgroundColor(Color.BLACK);

        if(!itemEstado.isDescargado()){
            view_descarga.setVisibility(View.VISIBLE);
            progress_img.ponerClick();
            progress_img.setDownloading(false);
            progress_img.setProgress(0);
            progress_img.setProgressListener(new Function1<Boolean, Unit>() {
                @Override
                public Unit invoke(Boolean it) {
                    if(YouChatApplication.estaAndandoChatService()
                            && YouChatApplication.chatService.hayConex){
                        progress_img.setDownloading(true);
                        progress_img.quitarClick();
                        progress_img.setProgress(0);
                        OnDownloadNowListener onDownloadNowListener = new OnDownloadNowListener() {
                            @Override
                            public void OnProgressListener(float progress) {
                                Utils.runOnUIThread(()->{
                                    progress_img.setProgress(progress);
                                });
                            }

                            @Override
                            public void OnFailedDownload(boolean esFallida) {
                                Utils.runOnUIThread(()->{
                                    if(esFallida) Utils.ShowToastAnimated(mainActivity,"Error al intentar descargar",R.raw.error);
                                    else Utils.ShowToastAnimated(mainActivity,"Now no encontrado",R.raw.error);
                                    progress_img.setDownloading(false);
                                    progress_img.setProgress(0);
                                    progress_img.ponerClick();
                                });
                            }

                            @Override
                            public void OnSuccessDownload(String idNow) {
                                Utils.runOnUIThread(()->{
//                                    if(idNow.equals(itemEstado.getId())){
                                        itemEstado = dbWorker.obtenerEstado(idNow);
                                        progress_img.setDownloading(false);
                                        progress_img.setProgress(0);
                                        procesoActualizarVista(false);
                                        estadosViewPagerFragment.mostrarViewsDescarga();
//                                    }
                                });
                            }
                        };
                        estadosViewPagerFragment.descargarNowActual(onDownloadNowListener);
                    }
                    else{
                        progress_img.setDownloading(false);
                        progress_img.setProgress(0);
                        Utils.mostrarToastDeConexion(mainActivity);
                    }
                    return null;
                }
            });
            tv_tam_max_img.setText(Utils.convertirBytes(itemEstado.getPeso_img()));

            if (!itemEstado.isEsta_visto()){
                itemEstado.setEsta_visto(true);
                dbWorker.marcarEstadoComoVisto(itemEstado.getId());
            }
        }
        else{
            if (!itemEstado.isEsta_visto()){
                if(YouChatApplication.estaAndandoChatService() && !itemEstado.getCorreo().equals(YouChatApplication.idOficial)){
                    if(YouChatApplication.chatService.hayConex) {
                        ItemChat msgReaccion = new ItemChat(correo, itemEstado.getId());
                        YouChatApplication.chatService.enviarMensaje(msgReaccion, SendMsg.CATEGORY_ESTADO_VISTO);
                    }
                }
                itemEstado.setEsta_visto(true);
                dbWorker.marcarEstadoComoVisto(itemEstado.getId());
            }

            if (itemEstado.getTipo_estado() == 99) {
                verificarSiExisteTextoEnImagen(itemEstado.getTexto());
                String rutaImg = itemEstado.getRuta_imagen();
                File file = new File(rutaImg);
                if (file.exists()) {
                    Glide.with(context).load(rutaImg)
                            .error(R.drawable.image_placeholder)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(estado_fondo);
                }
                else estado_fondo.setImageResource(R.drawable.image_placeholder);
                estado_fondo.setVisibility(View.VISIBLE);

            }
            else{
                String texto = itemEstado.getTexto();
                procesoVerificarTamTexto(texto, tv_texto_estado);
                ponerColorFondo(itemEstado.getTipo_estado());
                switch (itemEstado.getEstilo_texto()){
                    case 0: tv_texto_estado.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Roboto Medium.ttf"));
                        break;
                    case 1: tv_texto_estado.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Burnstown Dam.otf"));
                        break;
                    case 2: tv_texto_estado.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/comicz.ttf"));
                        break;
                    case 3: tv_texto_estado.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Inkfree.ttf"));
                        break;
                    case 4: tv_texto_estado.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/mw_bold.ttf"));
                        break;
                    case 5: tv_texto_estado.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Norican Regular.ttf"));
                        break;
                    case 6: tv_texto_estado.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Oswald Heavy.ttf"));
                        break;
                    case 7: tv_texto_estado.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Thunder Pants.otf"));
                        break;
                    default: tv_texto_estado.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Roboto Medium.ttf"));
                }
            }
        }
    }
    private void verificarSiExisteTextoEnImagen(final String texto) {
        tv_texto_estado.setVisibility(View.GONE);
        if (texto.length() > 0) {
//            preview_infotext_estados.setText(texto);
            hayTexto=true;
        }
//        else contenedor_preview_infotext_estados.setVisibility(View.GONE);
    }

    private synchronized void ponerColorFondo(int tipo_estado) {
        if (tipo_estado < 30) {
            root_estado.setBackgroundResource(0);
            int colorTarjeta = ContextCompat.getColor(context, R.color.card5);
            switch (tipo_estado) {
                case 0:
                    colorTarjeta = ContextCompat.getColor(context, R.color.card1);
                    break;
                case 1:
                    colorTarjeta = ContextCompat.getColor(context, R.color.card2);
                    break;
                case 2:
                    colorTarjeta = ContextCompat.getColor(context, R.color.card3);
                    break;
                case 3:
                    colorTarjeta = ContextCompat.getColor(context, R.color.card4);
                    break;
                case 4:
                    colorTarjeta = ContextCompat.getColor(context, R.color.card5);
                    break;
                case 5:
                    colorTarjeta = ContextCompat.getColor(context, R.color.card6);
                    break;
                case 6:
                    colorTarjeta = ContextCompat.getColor(context, R.color.card7);
                    break;
                case 7:
                    colorTarjeta = ContextCompat.getColor(context, R.color.card8);
                    break;
                case 8:
                    colorTarjeta = ContextCompat.getColor(context, R.color.card9);
                    break;
                case 9:
                    colorTarjeta = ContextCompat.getColor(context, R.color.card10);
                    break;
                case 10:
                    colorTarjeta = ContextCompat.getColor(context, R.color.card11);
                    break;
                case 11:
                    colorTarjeta = ContextCompat.getColor(context, R.color.card12);
                    break;
                case 12:
                    colorTarjeta = ContextCompat.getColor(context, R.color.card13);
                    break;
                case 13:
                    colorTarjeta = ContextCompat.getColor(context, R.color.card14);
                    break;
                case 14:
                    colorTarjeta = ContextCompat.getColor(context, R.color.card15);
                    break;
                case 15:
                    colorTarjeta = ContextCompat.getColor(context, R.color.card16);
                    break;
            }
            root_estado.setBackgroundColor(colorTarjeta);
        } else {
            root_estado.setBackgroundColor(0);
            switch (tipo_estado) {
                case 30:
                    root_estado
                            .setBackgroundResource(R.drawable.shape_fondo_estado_texto_1);
                    break;
                case 31:
                    root_estado
                            .setBackgroundResource(R.drawable.shape_fondo_estado_texto_2);
                    break;
                case 32:
                    root_estado
                            .setBackgroundResource(R.drawable.shape_fondo_estado_texto_3);
                    break;
                case 33:
                    root_estado
                            .setBackgroundResource(R.drawable.shape_fondo_estado_texto_4);
                    break;
                case 34:
                    root_estado
                            .setBackgroundResource(R.drawable.shape_fondo_estado_texto_5);
                    break;
                case 35:
                    root_estado
                            .setBackgroundResource(R.drawable.shape_fondo_estado_texto_6);
                    break;
                case 36:
                    root_estado
                            .setBackgroundResource(R.drawable.shape_fondo_estado_texto_7);
                    break;
                case 37:
                    root_estado
                            .setBackgroundResource(R.drawable.shape_fondo_estado_texto_8);
                    break;
                case 38:
                    root_estado
                            .setBackgroundResource(R.drawable.shape_fondo_estado_texto_9);
                    break;
                case 39:
                    root_estado
                            .setBackgroundResource(R.drawable.shape_fondo_estado_texto_10);
                    break;
                default:
                    root_estado
                            .setBackgroundResource(R.drawable.shape_fondo_estado_texto_1);
            }
        }

    }
    private synchronized void procesoVerificarTamTexto(String cad, EmojiTextView texto_estado) {
        int l = cad.length();
        int rango = l / 20;
        if (rango == 0) {
            texto_estado.setTextSize(40);
            texto_estado.setEmojiSizeRes(R.dimen.estado_size_40, false);
        } else if (rango == 1) {
            texto_estado.setTextSize(38);
            texto_estado.setEmojiSizeRes(R.dimen.estado_size_38, false);
        } else if (rango == 2) {
            texto_estado.setTextSize(36);
            texto_estado.setEmojiSizeRes(R.dimen.estado_size_36, false);
        } else if (rango == 3) {
            texto_estado.setTextSize(34);
            texto_estado.setEmojiSizeRes(R.dimen.estado_size_34, false);
        } else if (rango == 4) {
            texto_estado.setTextSize(32);
            texto_estado.setEmojiSizeRes(R.dimen.estado_size_32, false);
        } else if (rango == 5) {
            texto_estado.setTextSize(30);
            texto_estado.setEmojiSizeRes(R.dimen.estado_size_30, false);
        } else if (rango == 6) {
            texto_estado.setTextSize(28);
            texto_estado.setEmojiSizeRes(R.dimen.estado_size_28, false);
        } else if (rango == 7) {
            texto_estado.setTextSize(26);
            texto_estado.setEmojiSizeRes(R.dimen.estado_size_26, false);
        } else if (rango == 8) {
            texto_estado.setTextSize(24);
            texto_estado.setEmojiSizeRes(R.dimen.estado_size_24, false);
        } else if (rango == 9) {
            texto_estado.setTextSize(22);
            texto_estado.setEmojiSizeRes(R.dimen.estado_size_22, false);
        } else if (rango == 10) {
            texto_estado.setTextSize(20);
            texto_estado.setEmojiSizeRes(R.dimen.estado_size_20, false);
        } else if (rango == 11) {
            texto_estado.setTextSize(18);
            texto_estado.setEmojiSizeRes(R.dimen.estado_size_18, false);
        } else {
            texto_estado.setTextSize(16);
            texto_estado.setEmojiSizeRes(R.dimen.estado_size_16, false);
        }
        Utils.runOnUIThread(()->{
            SpannableString s = new SpannableString(cad);
            int largo = s.length();
            int fin;
            for (int i = 0; i < largo; i++) {
                if (s.charAt(i) == '#') {
                    if (i + 1 <= largo - 1 && Character.isLetterOrDigit(s.charAt(i + 1))) {
                        fin = i+1;
                        for (int j=i+1 ; j<largo ; j++) {
                            if (Character.isLetterOrDigit(s.charAt(j)) || s.charAt(j)=='_') fin++;
                            else break;
                        }
                        s.setSpan(new ForegroundColorSpan(Color.parseColor(YouChatApplication.itemTemas.getFont_texto_resaltado())), i, fin, 0);
                    }
                }
            }
            texto_estado.setText(s);
        });
    }

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    Log.e(TAG, "onTouch: ACTION_UP");
                    v.setOnLongClickListener(onLongClickListener);
                    v.setOnTouchListener(null);
                    estadosViewPagerFragment.playStory();
                    estadosViewPagerFragment.mostrarViews();
                    estadosViewPagerFragment.pager.setUserInputEnabled(true);
                    return false;
            }
            return true;
        }
    };

    private View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if(!estadosViewPagerFragment.textoLargoShow && !estadosViewPagerFragment.estadoDescarga){
                estadosViewPagerFragment.pauseStory();
                estadosViewPagerFragment.ocultarViews();
                estadosViewPagerFragment.pager.setUserInputEnabled(false);
                v.setOnTouchListener(onTouchListener);
                v.setOnLongClickListener(null);
            }
            return false;
        }
    };
}