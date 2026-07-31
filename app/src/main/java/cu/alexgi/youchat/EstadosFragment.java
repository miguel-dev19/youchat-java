package cu.alexgi.youchat;

import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.EmojiTextView;
import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.api.widget.Widget;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import cu.alexgi.youchat.adapters.AdaptadorDatosEstado;
import cu.alexgi.youchat.items.ItemChat;
import cu.alexgi.youchat.items.ItemContacto;
import cu.alexgi.youchat.items.ItemEstado;
import cu.alexgi.youchat.items.ItemImg;
import cu.alexgi.youchat.items.ItemUsuario;
import cu.alexgi.youchat.photoutil.ImageLoader;
import cu.alexgi.youchat.storiesprogressview.StoriesProgressView;
import cu.alexgi.youchat.swipebackfragment.BaseSwipeBackFragment;
import cu.alexgi.youchat.views_GI.TextViewFontGenGI;

import static cu.alexgi.youchat.MainActivity.context;
import static cu.alexgi.youchat.MainActivity.dbWorker;
import static cu.alexgi.youchat.MainActivity.mainActivity;
import static cu.alexgi.youchat.MainActivity.permisos;

public class EstadosFragment extends BaseSwipeBackFragment implements StoriesProgressView.StoriesListener{

    private OnFragmentInteractionListener mListener;

    private View frag;
    private BottomSheetDialog bottomSheetDialog;
    private static PrincipalActivity principalActivity;
    private int progressSubida, progressTotal;

    //recycler estado
    public RecyclerView lista_estado;
    private LinearLayoutManager linearLayoutManagerEstado;
    private ArrayList<ItemEstado> datos_Estado;
    private AdaptadorDatosEstado adaptadorEstado;

    private int positionPager=0;
    private ArrayList<String> seguidores;
//    private FragmentStateAdapter pagerAdapter;
//    private static ViewPager2 pager_status;
    private ArrayList<String> estados_usuarios;
//    private NavController navController;

    ////////////////////////////////////////////////////////////////////////////////
    private boolean soyYo, entroNext=false, entroPrev=false, hayTexto=false, inTouch=false;
    private String correo;
    private int posActual, tiempoTouch=0;
    private ArrayList<ItemEstado> estados;
    private ImageView estado_fondo,show_texto_estado_imagen;
    private EmojiTextView tv_texto_estado, preview_infotext_estados;
    private View input_estado_send, contenedor_preview_infotext_estados, root_estado,showEdit;
    private ImageView input_estado_reaccionar;//, input_estado_emoji;
    private LottieAnimationView may_reac_1, may_reac_2, may_reac_3;
    private EmojiEditText input_estado_text;
    private EmojiPopup emojiPopup;
    private View animacion_reaccion, skip, reverse, estado_copiar_texto, estado_resubir;
    private LottieAnimationView animacion_icono_reaccion;
    private View contenedor_input_estado_reacciones ,toolbar_visor_estado, answer;//btn_reacciones_detalles,contenedor_input_estado_send
    private TextView tv_cant_total_vistas, tv_total_reacciones, fecha_subida_estado;
    //private View input_estado_see_mi_image;
    private CircleImageView mini_img_perfil_estados;
    private EmojiTextView nombre_usuario_estado;
//    private static final int PROGRESS_COUNT = 6;
    public StoriesProgressView storiesProgressView;
//    private int counter = 0;
//    private ArrayList<StoriesData> mStoriesList = new ArrayList<>();
//    private ArrayList<View> mediaPlayerArrayList = new ArrayList<>();
    long pressTime = 0L;
    long limit = 500L;
    ///////////////////////////////////////////////////////////////////////////////

    public EstadosFragment() {
        // Required empty public constructor
    }

    public static EstadosFragment newInstance(PrincipalActivity pa) {
        EstadosFragment fragment = new EstadosFragment();
        principalActivity = pa;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        frag = inflater.inflate(R.layout.fragment_estados, container, false);

        progressSubida = progressTotal = 0;
        estados_usuarios = new ArrayList<>();
        lista_estado = frag.findViewById(R.id.lista_estado);

        frag.findViewById(R.id.root).setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_fondo()));

        datos_Estado = new ArrayList<>();

        if(principalActivity==null) Log.e("NULL", "principalActivity");
        if(context==null) Log.e("NULL", "context");
        if(datos_Estado==null) Log.e("NULL", "datos_Estado");

        adaptadorEstado = new AdaptadorDatosEstado(context, datos_Estado, principalActivity);
        linearLayoutManagerEstado = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        lista_estado.setLayoutManager(linearLayoutManagerEstado);
        lista_estado.setAdapter(adaptadorEstado);
        lista_estado.setHasFixedSize(true);

        LlenarEstados();

        return frag;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        navController = Navigation.findNavController(view);
    }

    public synchronized void resubirEstado(ItemEstado estado){
        ArrayList<String> seguidores = dbWorker.obtenerTodosSeguidores();
        int longi = seguidores.size();
        if(YouChatApplication.estaAndandoChatService() && !YouChatApplication.chatService.hayConex)
            Utils.ShowToastAnimated(mainActivity,"Compruebe su conexión",R.raw.ic_ban);
        else if(longi==0)
            Utils.ShowToastAnimated(mainActivity,"Sin seguidores aún, no podrás publicar ningún Now",R.raw.swipe_disabled);
       else {
            int vueltas=longi/20+1;
            progressTotal+=vueltas;
            adaptadorEstado.notifyItemChanged(0,300);
            int vueltas_dadas=0;
            int ultPos=0;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
            Date date = new Date();
            String fechaEntera = sdf.format(date);
            String hora = Convertidor.conversionHora(fechaEntera);
            String fecha = Convertidor.conversionFecha(fechaEntera);
            String idEstado="YouChat/estado/"+YouChatApplication.correo+"/"+fechaEntera;
            String newRuta =YouChatApplication.RUTA_IMAGENES_ENVIADAS+ "est"+fechaEntera+".jpg";
            if(estado.esEstadoImagen()){
                boolean seCopio = false;
                try {
                    File origen = new File(estado.getRuta_imagen());
                    File destino = new File(newRuta);
                    seCopio = Utils.copyFile(origen,destino);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(!seCopio){
                    try {
                        seCopio = ImageLoader.init().comprimirImagen(estado.getRuta_imagen(),newRuta,100);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                if(!seCopio)
                    newRuta = estado.getRuta_imagen();
            }
            while (vueltas_dadas<vueltas){
                String correosNotif="";
                int cant=0;
                for(int i=ultPos; i<longi; i++){
                    if(cant==20){
                        ultPos=i;
                        break;
                    }
                    if(i!=ultPos)
                        correosNotif=correosNotif+",";
                    correosNotif=correosNotif+seguidores.get(i);
                    cant++;
                }

                ItemChat nuevoEstado = new ItemChat(correosNotif,""+estado.getTipo_estado());
                nuevoEstado.setId(idEstado);//ESTADO_CAMPO_ID
                nuevoEstado.setMensaje(estado.getTexto());//ESTADO_CAMPO_TEXTO
                nuevoEstado.setRuta_Dato(newRuta);
                nuevoEstado.setHora(hora);//ESTADO_CAMPO_HORA
                nuevoEstado.setFecha(fecha);//ESTADO_CAMPO_FECHA
                nuevoEstado.setEmisor(""+estado.getEstilo_texto());
                if(YouChatApplication.estaAndandoChatService())
                    YouChatApplication.chatService.enviarMensaje(nuevoEstado,SendMsg.CATEGORY_ESTADO_PUBLICAR);
                vueltas_dadas++;
            }
            ItemEstado estadoNuevo = new ItemEstado(idEstado, YouChatApplication.correo, estado.getTipo_estado(),
                    true, newRuta, estado.getTexto(),
                    0, 0, 0, 0,
                    0, 0, 0,
                    hora, fecha, fechaEntera,estado.getEstilo_texto(),true,0,"",0);
            dbWorker.insertarNuevoEstado(estadoNuevo);
            Utils.ShowToastAnimated(mainActivity,"Compartiendo Now", R.raw.chats_infotip);
        }
    }

    public synchronized void publicarEstado(ArrayList<ItemImg> itemImgs) {
        int longi = seguidores.size();
        if (YouChatApplication.estaAndandoChatService() && !YouChatApplication.chatService.hayConex)
            Utils.ShowToastAnimated(mainActivity, "Compruebe su conexión", R.raw.ic_ban);
        else if (longi == 0)
            Utils.ShowToastAnimated(mainActivity, "Sin seguidores no podrás publicar ningún Now", R.raw.swipe_disabled);
        else{
            int l=itemImgs.size();
            int vueltas = longi / 20 + 1;
            progressTotal+=vueltas*l;
            adaptadorEstado.notifyItemChanged(0,300);
            for(int i=0; i<l; i++){
                String texto = itemImgs.get(i).getTexto();
                String ruta_img = itemImgs.get(i).getRuta();
                int vueltas_dadas = 0;
                int ultPos = 0;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
                Date date = new Date();
                String fechaEntera = sdf.format(date);
                String hora = Convertidor.conversionHora(fechaEntera);
                String fecha = Convertidor.conversionFecha(fechaEntera);
                String idEstado = "YCestado" + YouChatApplication.correo + "" + fechaEntera;
                while (vueltas_dadas < vueltas) {
                    String correosNotif = "";
                    int cant = 0;
                    for (int j = ultPos; j < longi; j++) {
                        if (cant == 20) {
                            ultPos = j;
                            break;
                        }
                        if (j != ultPos)
                            correosNotif = correosNotif + ",";
                        correosNotif = correosNotif + seguidores.get(j);
                        cant++;
                    }

                    ItemChat nuevoEstado = new ItemChat(correosNotif, "99");
                    nuevoEstado.setId(idEstado);//ESTADO_CAMPO_ID
                    nuevoEstado.setRuta_Dato(ruta_img);//ESTADO_CAMPO_RUTA_IMAGEN
                    nuevoEstado.setMensaje(texto);//ESTADO_CAMPO_TEXTO
                    nuevoEstado.setHora(hora);//ESTADO_CAMPO_HORA
                    nuevoEstado.setFecha(fecha);//ESTADO_CAMPO_FECHA
                    if (YouChatApplication.estaAndandoChatService())
                        YouChatApplication.chatService.enviarMensaje(nuevoEstado, SendMsg.CATEGORY_ESTADO_PUBLICAR);
                    vueltas_dadas++;
                }

                ItemEstado estado = new ItemEstado(idEstado, YouChatApplication.correo, 99,
                        true, ruta_img, texto, 0, 0, 0, 0, 0, 0, 0,
                        hora, fecha, fechaEntera,0,true,0,"",0);
                dbWorker.insertarNuevoEstado(estado);
            }
        }
    }

    public synchronized void publicarEstadoTexto(String texto,int colorEstado, int tipoLetra){
        ArrayList<String> seguidores = dbWorker.obtenerTodosSeguidores();
        int longi = seguidores.size();
        int vueltas=longi/20+1;
        progressTotal+=vueltas;
        adaptadorEstado.notifyItemChanged(0,300);
        int vueltas_dadas=0;
        int ultPos=0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
        Date date = new Date();
        String fechaEntera = sdf.format(date);
        String hora = Convertidor.conversionHora(fechaEntera);
        String fecha = Convertidor.conversionFecha(fechaEntera);
        String idEstado="YCestado"+YouChatApplication.correo+""+fechaEntera;
        while (vueltas_dadas<vueltas){
            String correosNotif="";
            int cant=0;
            for(int i=ultPos; i<longi; i++){
                if(cant==20){
                    ultPos=i;
                    break;
                }
                if(i!=ultPos)
                    correosNotif=correosNotif+",";
                correosNotif=correosNotif+seguidores.get(i);
                cant++;
            }

            ItemChat nuevoEstado = new ItemChat(correosNotif,""+colorEstado);
            nuevoEstado.setId(idEstado);//ESTADO_CAMPO_ID
            nuevoEstado.setMensaje(texto);//ESTADO_CAMPO_TEXTO
            nuevoEstado.setHora(hora);//ESTADO_CAMPO_HORA
            nuevoEstado.setFecha(fecha);//ESTADO_CAMPO_FECHA
            nuevoEstado.setEmisor(""+tipoLetra);
            if(YouChatApplication.estaAndandoChatService())
                YouChatApplication.chatService.enviarMensaje(nuevoEstado,SendMsg.CATEGORY_ESTADO_PUBLICAR);
            vueltas_dadas++;
        }

        ItemEstado estado = new ItemEstado(idEstado, YouChatApplication.correo, colorEstado,
                true, "", texto, 0, 0, 0,
                0, 0, 0, 0,
                hora, fecha, fechaEntera,tipoLetra,true,0,"",0);
        dbWorker.insertarNuevoEstado(estado);
    }

    public synchronized void nuevoEstadoTexto() {
        if (YouChatApplication.estaAndandoChatService() && !YouChatApplication.chatService.hayConex)
            Utils.ShowToastAnimated(mainActivity, "Compruebe su conexión", R.raw.ic_ban);
        else if(mAddFragmentListener!=null) {
            NuevoEstadoTextoActivity nuevoEstadoTextoActivity = new NuevoEstadoTextoActivity();
            nuevoEstadoTextoActivity.setOnNowPublicarListener(new NuevoEstadoTextoActivity.OnNowPublicarListener() {
                @Override
                public void onPublicar(String texto, int colorEstado, int tipoLetra) {
                    publicarEstadoTexto(texto,colorEstado,tipoLetra);
                }
            });
            mAddFragmentListener.onAddFragment(principalActivity, nuevoEstadoTextoActivity);
        }
    }

    public synchronized void nuevoEstadoImg() {
        if (YouChatApplication.estaAndandoChatService() && !YouChatApplication.chatService.hayConex)
            Utils.ShowToastAnimated(mainActivity, "Compruebe su conexión", R.raw.ic_ban);
        else {
            seguidores = dbWorker.obtenerTodosSeguidores();
            if (seguidores.size() > 0) {
                if (permisos.requestPermissionAlmacenamiento()){
                    selectImage();
                }
            } else
                Utils.ShowToastAnimated(mainActivity, "Sin seguidores no podrás publicar ningún Now", R.raw.swipe_disabled);
        }
    }

    private synchronized void selectImage() {
        Album.image(this)
                .multipleChoice()
                .camera(false)
                .columnCount(3)
                .selectCount(1000)
                //.checkedList(mAlbumFiles)
                .widget(
                        Widget.newDarkBuilder(context)
                                .title("Galería")
                                .build()
                )
                .onResult(new Action<ArrayList<AlbumFile>>() {
                    @Override
                    public void onAction(@NonNull ArrayList<AlbumFile> result) {
                        comprimirImagen(result);
                    }
                })
                .start();
    }
    public synchronized void comprimirImagen(ArrayList<AlbumFile> result) {
        Dialog dialog = Utils.mostrarDialogCarga(this, context, "Comprimiendo imágenes...");
        Utils.runOnUIThread(()->{
            if (!permisos.requestPermissionAlmacenamiento()){
                Utils.cerrarDialogCarga(dialog);
                return;
            }
            File directorioImagenes = new File(YouChatApplication.RUTA_ESTADOS_GUARDADOS);
            if (!directorioImagenes.exists()) if (!directorioImagenes.mkdirs()){
                Utils.cerrarDialogCarga(dialog);
                return;
            }

            String fechaEntera = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());

            ArrayList<ItemImg> itemImgs = new ArrayList<>();
            int l=result.size();
            for(int i=0; i<l; i++){
                String pp1=result.get(i).getPath();
                String nombre_img="img"+fechaEntera+""+i+".jpg";
                String r1 =YouChatApplication.RUTA_ESTADOS_GUARDADOS+nombre_img;
                try {
                    if(ImageLoader.init().comprimirImagen(pp1,r1,YouChatApplication.calidad)){
                        itemImgs.add(new ItemImg(r1,pp1,YouChatApplication.calidad));
                    }
                } catch (FileNotFoundException e) {
                    Utils.ShowToastAnimated(mainActivity,"Error al cargar la imagen",R.raw.error);
                }
            }
            Utils.cerrarDialogCarga(dialog);
            irAlEditor(itemImgs);
        },300);
    }

    public synchronized void comprimirImagen2(ArrayList<AlbumFile> result) {
        Utils.runOnUIThread(()->{
            if (!permisos.requestPermissionAlmacenamiento()) return;
            File directorioImagenes = new File(YouChatApplication.RUTA_ESTADOS_GUARDADOS);
            if (!directorioImagenes.exists())
                if (!directorioImagenes.mkdirs()) return;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
            Date date = new Date();
            String fechaEntera = sdf.format(date);

            ArrayList<ItemImg> itemImgs = new ArrayList<>();
            int l=result.size();
            for(int i=0; i<l; i++){
                String pp1=result.get(i).getPath();
                String nombre_img="img"+fechaEntera+""+i+".jpg";
                String r1 =YouChatApplication.RUTA_ESTADOS_GUARDADOS+nombre_img;
                try {
                    if(ImageLoader.init().comprimirImagen(pp1,r1,YouChatApplication.calidad))
                        itemImgs.add(new ItemImg(r1,pp1,YouChatApplication.calidad));
                } catch (FileNotFoundException e) {
                    Utils.ShowToastAnimated(mainActivity,"Error al cargar la imagen",R.raw.error);
                }
            }
            irAlEditor(itemImgs);
        });
    }

    private synchronized void irAlEditor(ArrayList<ItemImg> itemImgs) {
        if (itemImgs.size() == 0) return;
        if(mAddFragmentListener!=null)
            mAddFragmentListener.onAddFragment(principalActivity, ViewImageActivity.newInstance(itemImgs, 3));
//        principalActivity.navController.navigate(R.id.viewImageActivity,Convertidor.createBundleOfItemImg(itemImgs, 3));
    }

    private synchronized void procesoActualizarVista() {
        ItemEstado estado = estados.get(posActual);
        estado_resubir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resubirEstado(estado);
            }
        });

        hayTexto=false;
//        animacion_reaccion.setVisibility(View.GONE);
        if(!soyYo){
            input_estado_reaccionar.setVisibility(View.VISIBLE);
            animacion_reaccion.setVisibility(View.VISIBLE);
            answer.setVisibility(View.VISIBLE);
        }
        else contenedor_input_estado_reacciones.setVisibility(View.VISIBLE);

        estado_copiar_texto.setVisibility(View.INVISIBLE);
        show_texto_estado_imagen.setAnimation(null);
        show_texto_estado_imagen.setVisibility(View.GONE);
//        show_texto_estado_imagen.setImageResource(R.drawable.arrow_up_2);
        contenedor_preview_infotext_estados.setVisibility(View.GONE);
        tv_texto_estado.setText("");
        tv_texto_estado.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Roboto Medium.ttf"));
        preview_infotext_estados.setText("");
        tv_texto_estado.setVisibility(View.VISIBLE);
        show_texto_estado_imagen.setOnClickListener(null);
        estado_fondo.setImageResource(0);
//        contenedor_input_estado.setVisibility(View.VISIBLE);
        estado_fondo.setVisibility(View.INVISIBLE);
        root_estado.setBackgroundColor(Color.BLACK);
        fecha_subida_estado.setText("" + Convertidor.convertirFechaAFechaLinda(estado.getFecha())+ ", " + estado.getHora());

        if (!estado.isEsta_visto()) {
            if(YouChatApplication.estaAndandoChatService() && !estado.getCorreo().equals(YouChatApplication.idOficial)){
                if(YouChatApplication.chatService.hayConex){
                    ItemChat msgReaccion = new ItemChat(correo, estados.get(posActual).getId());
                    YouChatApplication.chatService.enviarMensaje(msgReaccion, SendMsg.CATEGORY_ESTADO_VISTO);
                }
            }
            estados.get(posActual).setEsta_visto(true);
            dbWorker.marcarEstadoComoVisto(estado.getId());
        }

        if (estado.getTipo_estado() == 99) {
            verificarSiExisteTextoEnImagen(estado.getTexto());
            String rutaImg = estado.getRuta_imagen();
            File file = new File(rutaImg);
            if (file.exists()) {
                Glide.with(context).load(rutaImg)
                        .error(R.drawable.image_placeholder)
                        .into(estado_fondo);
            }
            else estado_fondo.setImageResource(R.drawable.image_placeholder);
            estado_fondo.setVisibility(View.VISIBLE);

        }
        else {
            show_texto_estado_imagen.setVisibility(View.GONE);
            String texto = estado.getTexto();
            estado_copiar_texto.setVisibility(View.VISIBLE);
            estado_copiar_texto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String clip = texto;
                    ClipData c = ClipData.newPlainText("YouChatCopy", clip);
                    YouChatApplication.clipboard.setPrimaryClip(c);
                    Utils.ShowToastAnimated(mainActivity,"Texto copiado al portapapeles",R.raw.voip_invite);
                }
            });
            procesoVerificarTamTexto(texto, tv_texto_estado);
            ponerColorFondo(estado.getTipo_estado());
            switch (estado.getEstilo_texto()){
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
        if (soyYo) actualizarReacciones(estado);
        else verificarReaccion(estado.reaccionDelEstado());
    }
    private void verificarSiExisteTextoEnImagen(final String texto) {
        tv_texto_estado.setVisibility(View.GONE);
        if (texto.length() > 0) {
            estado_copiar_texto.setVisibility(View.VISIBLE);
            estado_copiar_texto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String clip = texto;
                    ClipData c = ClipData.newPlainText("YouChatCopy", clip);
                    YouChatApplication.clipboard.setPrimaryClip(c);
                    Utils.ShowToastAnimated(mainActivity,"Texto copiado al portapapeles",R.raw.voip_invite);
                }
            });
            preview_infotext_estados.setText(texto);
            hayTexto=true;
            show_texto_estado_imagen.setVisibility(View.VISIBLE);
            show_texto_estado_imagen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(contenedor_preview_infotext_estados.getVisibility()==View.GONE){
                        Animation anim=AnimationUtils.loadAnimation(context,R.anim.show_layout_answer);
                        contenedor_preview_infotext_estados.setVisibility(View.VISIBLE);
                        contenedor_preview_infotext_estados.startAnimation(anim);
                        show_texto_estado_imagen.setVisibility(View.GONE);
                        show_texto_estado_imagen.setAnimation(null);
                        if(!soyYo){
                            answer.setVisibility(View.GONE);
                            input_estado_reaccionar.setVisibility(View.GONE);
                        }
                        else contenedor_input_estado_reacciones.setVisibility(View.GONE);
                        storiesProgressView.pause();
                    }
                }
            });
        }
        else contenedor_preview_infotext_estados.setVisibility(View.GONE);
    }
    private synchronized void actualizarReacciones(ItemEstado estado) {
        tv_total_reacciones.setText(""+estado.totalReacciones());

        may_reac_1.setVisibility(View.GONE);
        may_reac_2.setVisibility(View.GONE);
        may_reac_3.setVisibility(View.GONE);

        int[] reacPopup = estado.obtenerTresReaccionesPopulares();
        if(reacPopup[0]!=0){
            may_reac_1.setVisibility(View.VISIBLE);
            switch (reacPopup[0]){
                case 1:
                    may_reac_1.setAnimation(R.raw.like1);
                    break;
                case 2:
                    may_reac_1.setAnimation(R.raw.encanta);
                    break;
                case 3:
                    may_reac_1.setAnimation(R.raw.sonroja);
                    break;
                case 4:
                    may_reac_1.setAnimation(R.raw.divierte);
                    break;
                case 5:
                    may_reac_1.setAnimation(R.raw.asombra);
                    break;
                case 6:
                    may_reac_1.setAnimation(R.raw.entristece);
                    break;
                case 7:
                    may_reac_1.setAnimation(R.raw.enoja);
                    break;
            }
        }
        if(reacPopup[1]!=0){
            may_reac_2.setVisibility(View.VISIBLE);

            switch (reacPopup[1]){
                case 1:
                    may_reac_2.setAnimation(R.raw.like1);
                    break;
                case 2:
                    may_reac_2.setAnimation(R.raw.encanta);
                    break;
                case 3:
                    may_reac_2.setAnimation(R.raw.sonroja);
                    break;
                case 4:
                    may_reac_2.setAnimation(R.raw.divierte);
                    break;
                case 5:
                    may_reac_2.setAnimation(R.raw.asombra);
                    break;
                case 6:
                    may_reac_2.setAnimation(R.raw.entristece);
                    break;
                case 7:
                    may_reac_2.setAnimation(R.raw.enoja);
                    break;
            }
        }
        if(reacPopup[2]!=0){
            may_reac_3.setVisibility(View.VISIBLE);

            switch (reacPopup[2]){
                case 1:
                    may_reac_3.setAnimation(R.raw.like1);
                    break;
                case 2:
                    may_reac_3.setAnimation(R.raw.encanta);
                    break;
                case 3:
                    may_reac_3.setAnimation(R.raw.sonroja);
                    break;
                case 4:
                    may_reac_3.setAnimation(R.raw.divierte);
                    break;
                case 5:
                    may_reac_3.setAnimation(R.raw.asombra);
                    break;
                case 6:
                    may_reac_3.setAnimation(R.raw.entristece);
                    break;
                case 7:
                    may_reac_3.setAnimation(R.raw.enoja);
                    break;
            }
        }

        int cantViews = dbWorker.obtenerCantVistasEstadosDe(estado.getId());
        tv_cant_total_vistas.setText(cantViews+"");
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
    public void enviarReaccion(int reaccion) {
        if (YouChatApplication.estaAndandoChatService()) {
            if (YouChatApplication.chatService.hayConex) {

//                animacion_reaccion.setVisibility(View.VISIBLE);
                input_estado_reaccionar.setVisibility(View.GONE);

                input_estado_reaccionar.setOnClickListener(null);
                Animation anim;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
                Date date = new Date();
                String fechaEntera = sdf.format(date);
                String hora = Convertidor.conversionHora(fechaEntera);
                String fecha = Convertidor.conversionFecha(fechaEntera);
                ItemChat msgReaccion;

                switch (reaccion) {
                    case 1:
                        input_estado_reaccionar.setImageDrawable(null);
//                        input_estado_reaccionar.setImageResource(R.drawable.reac1);
                        estados.get(posActual).setCant_me_gusta(1);
                        animacion_icono_reaccion.setAnimation(R.raw.like1);
                        animacion_icono_reaccion.playAnimation();
                        Utils.reproducirSonido(R.raw.like, context);
//                        anim = AnimationUtils.loadAnimation(context, R.anim.animation_reac_show);
//                        animacion_reaccion.startAnimation(anim);
//                        anim = AnimationUtils.loadAnimation(context, R.anim.rotate_reaccion);
//                        anim.setRepeatMode(Animation.REVERSE);
//                        animacion_icono_reaccion.startAnimation(anim);

                        dbWorker.sumarUnaReaccion(estados.get(posActual).getId(), "1", 1);
                        msgReaccion = new ItemChat(correo, estados.get(posActual).getId());
                        msgReaccion.setId("1");
                        msgReaccion.setHora(hora);
                        msgReaccion.setFecha(fecha);
                        YouChatApplication.chatService.enviarMensaje(msgReaccion, SendMsg.CATEGORY_ESTADO_REACCIONAR);
                        break;
                    case 2:
                        input_estado_reaccionar.setImageDrawable(null);
//                        input_estado_reaccionar.setImageResource(R.drawable.reac2);
                        estados.get(posActual).setCant_me_encanta(1);
                        animacion_icono_reaccion.setAnimation(R.raw.encanta);
                        animacion_icono_reaccion.playAnimation();
                        Utils.reproducirSonido(R.raw.like, context);
//                        anim = AnimationUtils.loadAnimation(context, R.anim.animation_reac_show);
//                        animacion_reaccion.startAnimation(anim);
//                        anim = AnimationUtils.loadAnimation(context, R.anim.rotate_reaccion);
//                        anim.setRepeatMode(Animation.REVERSE);
//                        animacion_icono_reaccion.startAnimation(anim);
//                        Utils.ShowToastAnimated(mainActivity, "Reacción enviada", R.raw.contact_check);

                        dbWorker.sumarUnaReaccion(estados.get(posActual).getId(), "2", 1);
                        msgReaccion = new ItemChat(correo, estados.get(posActual).getId());
                        msgReaccion.setId("2");
                        msgReaccion.setHora(hora);
                        msgReaccion.setFecha(fecha);
                        YouChatApplication.chatService.enviarMensaje(msgReaccion, SendMsg.CATEGORY_ESTADO_REACCIONAR);
                        break;
                    case 3:
                        input_estado_reaccionar.setImageDrawable(null);
//                        input_estado_reaccionar.setImageResource(R.drawable.reac3);
                        estados.get(posActual).setCant_me_sonroja(1);
                        animacion_icono_reaccion.setAnimation(R.raw.sonroja);
                        animacion_icono_reaccion.playAnimation();
                        Utils.reproducirSonido(R.raw.like, context);
//                        anim = AnimationUtils.loadAnimation(context, R.anim.animation_reac_show);
//                        animacion_reaccion.startAnimation(anim);
//                        anim = AnimationUtils.loadAnimation(context, R.anim.rotate_reaccion);
//                        anim.setRepeatMode(Animation.REVERSE);
//                        animacion_icono_reaccion.startAnimation(anim);
//                        Utils.ShowToastAnimated(mainActivity, "Reacción enviada", R.raw.contact_check);

                        dbWorker.sumarUnaReaccion(estados.get(posActual).getId(), "3", 1);
                        msgReaccion = new ItemChat(correo, estados.get(posActual).getId());
                        msgReaccion.setId("3");
                        msgReaccion.setHora(hora);
                        msgReaccion.setFecha(fecha);
                        YouChatApplication.chatService.enviarMensaje(msgReaccion, SendMsg.CATEGORY_ESTADO_REACCIONAR);
                        break;
                    case 4:
                        input_estado_reaccionar.setImageDrawable(null);
//                        input_estado_reaccionar.setImageResource(R.drawable.reac4);
                        estados.get(posActual).setCant_me_divierte(1);
                        animacion_icono_reaccion.setAnimation(R.raw.divierte);
                        animacion_icono_reaccion.playAnimation();
                        Utils.reproducirSonido(R.raw.risita, context);
//                        anim = AnimationUtils.loadAnimation(context, R.anim.animation_reac_show);
//                        animacion_reaccion.startAnimation(anim);
//                        anim = AnimationUtils.loadAnimation(context, R.anim.rotate_reaccion);
//                        anim.setRepeatMode(Animation.REVERSE);
//                        animacion_icono_reaccion.startAnimation(anim);
//                        Utils.ShowToastAnimated(mainActivity, "Reacción enviada", R.raw.contact_check);

                        dbWorker.sumarUnaReaccion(estados.get(posActual).getId(), "4", 1);
                        msgReaccion = new ItemChat(correo, estados.get(posActual).getId());
                        msgReaccion.setId("4");
                        msgReaccion.setHora(hora);
                        msgReaccion.setFecha(fecha);
                        YouChatApplication.chatService.enviarMensaje(msgReaccion, SendMsg.CATEGORY_ESTADO_REACCIONAR);
                        break;
                    case 5:
                        input_estado_reaccionar.setImageDrawable(null);
//                        input_estado_reaccionar.setImageResource(R.drawable.reac5);
                        estados.get(posActual).setCant_me_asombra(1);
                        animacion_icono_reaccion.setAnimation(R.raw.asombra);
                        animacion_icono_reaccion.playAnimation();
                        Utils.reproducirSonido(R.raw.wow, context);
//                        anim = AnimationUtils.loadAnimation(context, R.anim.animation_reac_show);
//                        animacion_reaccion.startAnimation(anim);
//                        anim = AnimationUtils.loadAnimation(context, R.anim.rotate_reaccion);
//                        anim.setRepeatMode(Animation.REVERSE);
//                        animacion_icono_reaccion.startAnimation(anim);
//                        Utils.ShowToastAnimated(mainActivity, "Reacción enviada", R.raw.contact_check);

                        dbWorker.sumarUnaReaccion(estados.get(posActual).getId(), "5", 1);
                        msgReaccion = new ItemChat(correo, estados.get(posActual).getId());
                        msgReaccion.setId("5");
                        msgReaccion.setHora(hora);
                        msgReaccion.setFecha(fecha);
                        YouChatApplication.chatService.enviarMensaje(msgReaccion, SendMsg.CATEGORY_ESTADO_REACCIONAR);
                        break;
                    case 6:
                        input_estado_reaccionar.setImageDrawable(null);
//                        input_estado_reaccionar.setImageResource(R.drawable.reac6);
                        estados.get(posActual).setCant_me_entristese(1);
                        animacion_icono_reaccion.setAnimation(R.raw.entristece);
                        animacion_icono_reaccion.playAnimation();
                        Utils.reproducirSonido(R.raw.triston, context);
//                        anim = AnimationUtils.loadAnimation(context, R.anim.animation_reac_show);
//                        animacion_reaccion.startAnimation(anim);
//                        anim = AnimationUtils.loadAnimation(context, R.anim.rotate_reaccion);
//                        anim.setRepeatMode(Animation.REVERSE);
//                        animacion_icono_reaccion.startAnimation(anim);
//                        Utils.ShowToastAnimated(mainActivity, "Reacción enviada", R.raw.contact_check);

                        dbWorker.sumarUnaReaccion(estados.get(posActual).getId(), "6", 1);
                        msgReaccion = new ItemChat(correo, estados.get(posActual).getId());
                        msgReaccion.setId("6");
                        msgReaccion.setHora(hora);
                        msgReaccion.setFecha(fecha);
                        YouChatApplication.chatService.enviarMensaje(msgReaccion, SendMsg.CATEGORY_ESTADO_REACCIONAR);
                        break;
                    case 7:
                        input_estado_reaccionar.setImageDrawable(null);
//                        input_estado_reaccionar.setImageResource(R.drawable.reac7);
                        estados.get(posActual).setCant_me_enoja(1);
                        animacion_icono_reaccion.setAnimation(R.raw.enoja);
                        animacion_icono_reaccion.playAnimation();
                        Utils.reproducirSonido(R.raw.like, context);
//                        anim = AnimationUtils.loadAnimation(context, R.anim.animation_reac_show);
//                        animacion_reaccion.startAnimation(anim);
//                        anim = AnimationUtils.loadAnimation(context, R.anim.rotate_reaccion);
//                        anim.setRepeatMode(Animation.REVERSE);
//                        animacion_icono_reaccion.startAnimation(anim);
//                        Utils.ShowToastAnimated(mainActivity, "Reacción enviada", R.raw.contact_check);

                        dbWorker.sumarUnaReaccion(estados.get(posActual).getId(), "7", 1);
                        msgReaccion = new ItemChat(correo, estados.get(posActual).getId());
                        msgReaccion.setId("7");
                        msgReaccion.setHora(hora);
                        msgReaccion.setFecha(fecha);
                        YouChatApplication.chatService.enviarMensaje(msgReaccion, SendMsg.CATEGORY_ESTADO_REACCIONAR);
                        break;
                    default:
                        input_estado_reaccionar.setImageResource(R.drawable.reac0);
                        animacion_icono_reaccion.setImageDrawable(null);
                        input_estado_reaccionar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                storiesProgressView.pause();
//                                PopupReaccionEstados popupReaccionEstados = new PopupReaccionEstados(v, principalActivity);
//                                popupReaccionEstados.show(v);
                            }
                        });
                }
            } else
                Utils.ShowToastAnimated(mainActivity, "Compruebe su conexión", R.raw.ic_ban);
        }
    }
    public void verificarReaccion(int reaccion) {
        input_estado_reaccionar.setOnClickListener(null);
        animacion_icono_reaccion.setImageDrawable(null);
        switch (reaccion) {
            case 1:
                input_estado_reaccionar.setImageDrawable(null);
//                input_estado_reaccionar.setImageResource(R.drawable.reac1);
                animacion_icono_reaccion.setAnimation(R.raw.like1);
                animacion_icono_reaccion.playAnimation();
                break;
            case 2:
                input_estado_reaccionar.setImageDrawable(null);
//                input_estado_reaccionar.setImageResource(R.drawable.reac2);
                animacion_icono_reaccion.setAnimation(R.raw.encanta);
                animacion_icono_reaccion.playAnimation();
                break;
            case 3:
                input_estado_reaccionar.setImageDrawable(null);
//                input_estado_reaccionar.setImageResource(R.drawable.reac3);
                animacion_icono_reaccion.setAnimation(R.raw.sonroja);
                animacion_icono_reaccion.playAnimation();
                break;
            case 4:
                input_estado_reaccionar.setImageDrawable(null);
//                input_estado_reaccionar.setImageResource(R.drawable.reac4);
                animacion_icono_reaccion.setAnimation(R.raw.divierte);
                animacion_icono_reaccion.playAnimation();
                break;
            case 5:
                input_estado_reaccionar.setImageDrawable(null);
//                input_estado_reaccionar.setImageResource(R.drawable.reac5);
                animacion_icono_reaccion.setAnimation(R.raw.asombra);
                animacion_icono_reaccion.playAnimation();
                break;
            case 6:
                input_estado_reaccionar.setImageDrawable(null);
//                input_estado_reaccionar.setImageResource(R.drawable.reac6);
                animacion_icono_reaccion.setAnimation(R.raw.entristece);
                animacion_icono_reaccion.playAnimation();
                break;
            case 7:
                input_estado_reaccionar.setImageDrawable(null);
//                input_estado_reaccionar.setImageResource(R.drawable.reac7);
                animacion_icono_reaccion.setAnimation(R.raw.enoja);
                animacion_icono_reaccion.playAnimation();
                break;
            default:
                input_estado_reaccionar.setImageResource(R.drawable.reac0);
//                animacion_icono_reaccion.setImageDrawable(null);
                input_estado_reaccionar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("ESTADOS","ONCLICK");
//                        storiesProgressView.pause();
//                        PopupReaccionEstados popupReaccionEstados = new PopupReaccionEstados(v, principalActivity);
//                        popupReaccionEstados.show(v);
                    }
                });
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
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    long now = System.currentTimeMillis();
                    inTouch=false;
                    reverse.setOnTouchListener(null);
                    skip.setOnTouchListener(null);
                    if(contenedor_preview_infotext_estados.getVisibility()!=View.VISIBLE)
                    {
                        toolbar_visor_estado.setVisibility(View.VISIBLE);
                        if(hayTexto) show_texto_estado_imagen.setVisibility(View.VISIBLE);
                        storiesProgressView.resume();
                        if(!soyYo){
                            answer.setVisibility(View.VISIBLE);
                            input_estado_reaccionar.setVisibility(View.VISIBLE);
                            animacion_reaccion.setVisibility(View.VISIBLE);
                        }
                        else contenedor_input_estado_reacciones.setVisibility(View.VISIBLE);

                    }
                    tiempoTouch=0;
                    return limit < now - pressTime;
            }
            return true;
        }
    };


    @Override
    public void onNext() {
        if(!entroNext)
        {
            inTouch=false;
            reverse.setOnTouchListener(null);
            skip.setOnTouchListener(null);
            entroNext=true;
            Log.e("onNext", "***OK***");
            storiesProgressView.skip();
            ++posActual;
            tiempoTouch=0;
            if (posActual < estados.size()) {
                Log.e("onNext", "***SIGUIENTE FOTO***");
                procesoActualizarVista();
                entroNext=false;
            }
            else onComplete();
        }
    }
    @Override
    public void onPrev() {
        if(!entroPrev)
        {
            inTouch=false;
            reverse.setOnTouchListener(null);
            skip.setOnTouchListener(null);
            entroPrev=true;
            Log.e("onPrev", "***OK***");
            storiesProgressView.reverse();
            --posActual;
            tiempoTouch=0;
            if (posActual >= 0) {
                Log.e("onPrev", "***ANTERIOR FOTO***");
                procesoActualizarVista();
                entroPrev=false;
            }
            else onComplete();
        }
    }
    @Override
    public void onComplete() {
        inTouch=false;
        reverse.setOnTouchListener(null);
        skip.setOnTouchListener(null);

        entroNext=false;
        entroPrev=false;
        storiesProgressView.destroy();
        bottomSheetDialog.dismiss();
        LlenarEstados();
    }
    public void playStories() {
        if(storiesProgressView!=null) storiesProgressView.resume();
    }
    /////////////////////////////////////////////////////////////////////////////////////////////

    public synchronized void abrirVisorEstadosDe(String correo_user, boolean isNew){
        if(mAddFragmentListener!=null)
            mAddFragmentListener.onAddFragment(principalActivity,
                    EstadosViewPagerFragment.newInstance(correo_user,datos_Estado, isNew),
                    R.anim.show_layout_answer, R.anim.hide_layout_answer);
    }

    public synchronized void abrirVisorEstadosDe2(String correo_user) {
        bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(R.layout.fragment_view_status);
        bottomSheetDialog.setDismissWithAnimation(true);

        posActual = 0;
        soyYo = false;
        correo=correo_user;

        estado_copiar_texto = bottomSheetDialog.findViewById(R.id.estado_copiar_texto);
        estado_resubir = bottomSheetDialog.findViewById(R.id.estado_resubir);
        root_estado = bottomSheetDialog.findViewById(R.id.root_estado);
        estado_fondo = bottomSheetDialog.findViewById(R.id.estados_fondo);
        tv_texto_estado = bottomSheetDialog.findViewById(R.id.tv_texto_estado);
        preview_infotext_estados = bottomSheetDialog.findViewById(R.id.preview_infotext_estados);
//        contenedor_input_estado = bottomSheetDialog.findViewById(R.id.contenedor_input_estado);
//        input_estado_send = bottomSheetDialog.findViewById(R.id.input_estado_send);
        contenedor_preview_infotext_estados = bottomSheetDialog.findViewById(R.id.contenedor_preview_infotext_estados);
        input_estado_reaccionar = bottomSheetDialog.findViewById(R.id.input_estado_reaccionar);
//        input_estado_emoji = bottomSheetDialog.findViewById(R.id.input_estado_emoji);
//        input_estado_text = bottomSheetDialog.findViewById(R.id.input_estado_text);
        animacion_reaccion = bottomSheetDialog.findViewById(R.id.animacion_reaccion);
        animacion_icono_reaccion = bottomSheetDialog.findViewById(R.id.animacion_icono_reaccion);
        mini_img_perfil_estados = bottomSheetDialog.findViewById(R.id.mini_img_perfil_estados);
        nombre_usuario_estado = bottomSheetDialog.findViewById(R.id.nombre_usuario_estado);
//        contenedor_input_estado_send = bottomSheetDialog.findViewById(R.id.contenedor_input_estado_send);
        contenedor_input_estado_reacciones = bottomSheetDialog.findViewById(R.id.contenedor_input_estado_reacciones);
        fecha_subida_estado = bottomSheetDialog.findViewById(R.id.fecha_subida_estado);
        //btn_reacciones_detalles = bottomSheetDialog.findViewById(R.id.btn_reacciones_detalles);

        tv_total_reacciones = bottomSheetDialog.findViewById(R.id.tv_total_reacciones);
        tv_cant_total_vistas = bottomSheetDialog.findViewById(R.id.tv_cant_total_vistas);
        may_reac_1 = bottomSheetDialog.findViewById(R.id.may_reac_1);
        may_reac_2 = bottomSheetDialog.findViewById(R.id.may_reac_2);
        may_reac_3 = bottomSheetDialog.findViewById(R.id.may_reac_3);

//        input_estado_see_mi_image = bottomSheetDialog.findViewById(R.id.input_estado_see_mi_image);
        storiesProgressView = bottomSheetDialog.findViewById(R.id.story);
        toolbar_visor_estado=bottomSheetDialog.findViewById(R.id.toolbar_visor_estado);
        answer=bottomSheetDialog.findViewById(R.id.answer);

        answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {}
        });

        showEdit=bottomSheetDialog.findViewById(R.id.showEdit);
        showEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.pause();
                Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(1);
                View mview=getLayoutInflater().inflate(R.layout.dialog_send_msg_estado,null);
                dialog.setContentView(mview);

                input_estado_send = mview.findViewById(R.id.input_estado_send);
                input_estado_text = mview.findViewById(R.id.input_estado_text);

                input_estado_text.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getFont_barchat()));
                input_estado_text.setHintTextColor(Color.parseColor(YouChatApplication.itemTemas.getFont_barchat_oscuro()));


                input_estado_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String cad = input_estado_text.getText().toString();
                        if (cad.length() > 0) {
                            ItemUsuario usuario = new ItemUsuario(correo);
                            ItemContacto contacto = new ItemContacto(correo, correo);
                            dbWorker.insertarNuevoUsuario(usuario);
                            dbWorker.insertarNuevoContactoNoVisible(contacto, true);

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
                            Date date = new Date();
                            String orden = sdf.format(date);

                            String id = "YouChat/chat/" + correo + "/18/" + orden;
                            String hora = Convertidor.conversionHora(orden);
                            String fecha = Convertidor.conversionFecha(orden);

                            input_estado_text.setText("");

                            ItemChat newChat = new ItemChat(id,
                                    18, 1, correo, cad, "",
                                    hora, fecha, estados.get(posActual).getId(),
                                    YouChatApplication.correo, false, orden,false
                                    ,"",0,true);

                            if (YouChatApplication.estaAndandoChatService())
                                YouChatApplication.chatService.enviarMensaje(newChat, SendMsg.CATEGORY_CHAT);
                            dbWorker.insertarChat(newChat);
                            dbWorker.actualizarUltMsgUsuario(newChat);
                            if (YouChatApplication.principalActivity != null) {
                                Utils.runOnUIThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        YouChatApplication.principalActivity
                                                .actualizarNewMsg(correo,0);
                                    }
                                });
                            }
                            dialog.dismiss();
                        }
                        else Utils.ShowToastAnimated(mainActivity, "No puede estar vacío", R.raw.ic_ban);
                    }
                });
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        storiesProgressView.resume();
                    }
                });
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                dialog.setCancelable(true);
                dialog.show();

                input_estado_text.setFocusableInTouchMode(true);
                input_estado_text.requestFocus();
                InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(input_estado_text, InputMethodManager.SHOW_IMPLICIT);
                if(!inputMethodManager.isActive()) inputMethodManager.showSoftInput(input_estado_text, InputMethodManager.SHOW_IMPLICIT);
                if(!inputMethodManager.isActive(input_estado_text)) inputMethodManager.showSoftInput(input_estado_text, InputMethodManager.SHOW_IMPLICIT);
            }
        });


        show_texto_estado_imagen = bottomSheetDialog.findViewById(R.id.show_text);
        estados = dbWorker.obtenerEstadosDe(correo);
        int cantEst = estados.size();
        storiesProgressView.setStoriesCount(cantEst);

        if(YouChatApplication.activar_progreso){
            int pos=0;
            storiesProgressView.setStoryDuration((YouChatApplication.tiempo_progreso) * 1000);
            for(int i=0 ; i<estados.size() ; i++)
            {
                if(!estados.get(i).isEsta_visto()){
                    pos=i;
                    posActual=i;
                    break;
                }
            }
            if(pos!=0) storiesProgressView.startStories(pos);
            else storiesProgressView.startStories();
            storiesProgressView.resume();
        }
        storiesProgressView.setStoriesListener(this);
        /*else {
            storiesProgressView.startStories();
            storiesProgressView.pause();
        }*/



        inTouch = false;
        reverse = bottomSheetDialog.findViewById(R.id.reverse);
        reverse.setOnTouchListener(null);
        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                if(contenedor_preview_infotext_estados.getVisibility()==View.VISIBLE){
                    storiesProgressView.resume();
                    Animation anim= AnimationUtils.loadAnimation(context,R.anim.hide_layout_answer);
                    contenedor_preview_infotext_estados.startAnimation(anim);
                    contenedor_preview_infotext_estados.setVisibility(View.GONE);
                    show_texto_estado_imagen.setVisibility(View.VISIBLE);
                    answer.setVisibility(View.VISIBLE);

                    if(!soyYo){
                        answer.setVisibility(View.VISIBLE);
                        input_estado_reaccionar.setVisibility(View.VISIBLE);
                        animacion_reaccion.setVisibility(View.VISIBLE);
                    }
                    else contenedor_input_estado_reacciones.setVisibility(View.VISIBLE);
                }
                else if(!inTouch) onPrev();
                v.setEnabled(true);
            }
        });
        reverse.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                storiesProgressView.pause();
                if(contenedor_preview_infotext_estados.getVisibility()!=View.VISIBLE && !inTouch){
                    inTouch=true;
                    reverse.setOnTouchListener(onTouchListener);

                    toolbar_visor_estado.setVisibility(View.GONE);
                    answer.setVisibility(View.GONE);
                    show_texto_estado_imagen.setVisibility(View.GONE);

                    animacion_reaccion.setVisibility(View.GONE);
                    input_estado_reaccionar.setVisibility(View.GONE);
                    contenedor_input_estado_reacciones.setVisibility(View.GONE);
                }
                return false;
            }
        });
//        tv_texto_estado.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                storiesProgressView.pause();
//                if(contenedor_preview_infotext_estados.getVisibility()!=View.VISIBLE && !inTouch){
//                    inTouch=true;
//                    reverse.setOnTouchListener(onTouchListener);
//
//                    toolbar_visor_estado.setVisibility(View.GONE);
//                    answer.setVisibility(View.GONE);
//                    show_texto_estado_imagen.setVisibility(View.GONE);
//
//                    animacion_reaccion.setVisibility(View.GONE);
//                    input_estado_reaccionar.setVisibility(View.GONE);
//                    contenedor_input_estado_reacciones.setVisibility(View.GONE);
//                }
//                return false;
//            }
//        });

        skip = bottomSheetDialog.findViewById(R.id.skip);
        skip.setOnTouchListener(null);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                if(contenedor_preview_infotext_estados.getVisibility()==View.VISIBLE){
                    storiesProgressView.resume();
                    Animation anim= AnimationUtils.loadAnimation(context,R.anim.hide_layout_answer);
                    contenedor_preview_infotext_estados.startAnimation(anim);
                    contenedor_preview_infotext_estados.setVisibility(View.GONE);
                    show_texto_estado_imagen.setVisibility(View.VISIBLE);

                    if(!soyYo){
                        answer.setVisibility(View.VISIBLE);
                        input_estado_reaccionar.setVisibility(View.VISIBLE);
                        animacion_reaccion.setVisibility(View.VISIBLE);
                    }
                    else contenedor_input_estado_reacciones.setVisibility(View.VISIBLE);
                }
                else if(!inTouch) onNext();
                v.setEnabled(true);
            }
        });
        skip.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                storiesProgressView.pause();
                if(contenedor_preview_infotext_estados.getVisibility()!=View.VISIBLE && !inTouch){
                    inTouch=true;
                    skip.setOnTouchListener(onTouchListener);

                    toolbar_visor_estado.setVisibility(View.GONE);
                    answer.setVisibility(View.GONE);
                    show_texto_estado_imagen.setVisibility(View.GONE);


                    animacion_reaccion.setVisibility(View.GONE);
                    input_estado_reaccionar.setVisibility(View.GONE);
                    contenedor_input_estado_reacciones.setVisibility(View.GONE);
                }
                return false;
            }
        });


        if (correo.equals("")) {
            storiesProgressView.destroy();
            bottomSheetDialog.dismiss();
            LlenarEstados();
        }


        if (estados.size() == 0) {
            storiesProgressView.destroy();
            bottomSheetDialog.dismiss();
            LlenarEstados();
        } else {
            if (correo.equals(YouChatApplication.correo)) {
                answer.setVisibility(View.GONE);
                input_estado_reaccionar.setVisibility(View.GONE);
                contenedor_input_estado_reacciones.setVisibility(View.VISIBLE);
                soyYo = true;
            }
            else {
                contenedor_input_estado_reacciones.setVisibility(View.GONE);
                soyYo = false;
            }

            ItemContacto contacto = dbWorker.obtenerContacto(correo);
            if (contacto == null) {
                contacto = new ItemContacto(correo, correo);
            }
            if(correo.equals(YouChatApplication.idOficial)){
                YouChatApplication.ponerIconOficial(mini_img_perfil_estados);
            }
            else {
                Glide.with(context)
                        .load(contacto.getRuta_img())
                        .error(R.drawable.profile_white)
                        .into(mini_img_perfil_estados);
            }

            nombre_usuario_estado.setText(contacto.getNombreMostrar());

            procesoActualizarVista();
        }
        bottomSheetDialog.show();

        View bottomSheetInternal = bottomSheetDialog.findViewById(R.id.design_bottom_sheet);
        BottomSheetBehavior bsb = BottomSheetBehavior.from(bottomSheetInternal);
        bsb.setState(BottomSheetBehavior.STATE_EXPANDED);

        bsb.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        bsb.setState(BottomSheetBehavior.STATE_HIDDEN);
                        break;
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                        bsb.setState(BottomSheetBehavior.STATE_HIDDEN);
                        break;

                    case BottomSheetBehavior.STATE_HIDDEN:
                        LlenarEstados();
                        tiempoTouch=0;
                        inTouch=false;
                        reverse.setOnTouchListener(null);
                        skip.setOnTouchListener(null);
                        bottomSheetDialog.dismiss();
                        break;

                    case BottomSheetBehavior.STATE_EXPANDED:
                        tiempoTouch=0;
                        inTouch=false;
                        reverse.setOnTouchListener(null);
                        skip.setOnTouchListener(null);
                        if(storiesProgressView!=null && contenedor_preview_infotext_estados.getVisibility()!=View.VISIBLE) {
                            storiesProgressView.resume();
                            toolbar_visor_estado.setVisibility(View.VISIBLE);
                            if(hayTexto) show_texto_estado_imagen.setVisibility(View.VISIBLE);
                            if(!soyYo){
                                answer.setVisibility(View.VISIBLE);
                                input_estado_reaccionar.setVisibility(View.VISIBLE);
                                animacion_reaccion.setVisibility(View.VISIBLE);
                            }
                            else contenedor_input_estado_reacciones.setVisibility(View.VISIBLE);
                        }
                        break;

                   /*

                    case BottomSheetBehavior.STATE_DRAGGING:
                        nuevoEstado = "STATE_DRAGGING";
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        nuevoEstado = "STATE_SETTLING";
                        break;*/
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    public void abrirAdminEstados() {
        if(mAddFragmentListener!=null) mAddFragmentListener.onAddFragment(principalActivity, new AdminEstadosActivity());
//        YouChatApplication.principalActivity.navController.navigate(R.id.adminEstadosActivity);
    }


    public synchronized void LlenarEstados() {
        if(principalActivity!=null) principalActivity.badgeInTabs(0, 2);
        ArrayList<ItemEstado> estadosAct = dbWorker.obtenerTodosLosEstadosOrdenadosXnoVistos();
        int l = estadosAct.size();
        int posN=-1, posG=-1;
        datos_Estado.clear();
        adaptadorEstado.notifyDataSetChanged();
        datos_Estado.add(0, new ItemEstado(-1));
        if (l > 0) {
            for (int i = 0; i < l; i++) {
                String cor = estadosAct.get(i).getCorreo();
                int cant = 0;//cant nuevos
                int cantG = 1; //cant gral

                if (!estadosAct.get(i).isEsta_visto()){
                    cant++;
                    posN=1;
                }

                for (int j = i + 1; j < l; j++) {
                    if (cor.equals(estadosAct.get(j).getCorreo())) {
                        if (!estadosAct.get(j).isEsta_visto()) cant++;
                        estadosAct.remove(j);
                        j--;
                        l--;
                        cantG++;
                    }
                }
                if (cor.equals(YouChatApplication.correo)) {
                    datos_Estado.remove(0);
                    datos_Estado.add(0, estadosAct.get(i));
                    estados_usuarios.add(0,cor);
                }
                else {
                    ItemEstado estTemp = estadosAct.get(i);
                    estTemp.setCant_me_gusta(cant);
                    estTemp.setCant_me_encanta(cantG);
                    datos_Estado.add(estTemp);
                    estados_usuarios.add(cor);
                    if(posG==-1 && cant==0) posG=datos_Estado.size()-1;
                }
            }
            if(posN!=-1){
                ItemEstado dividerRecents = new ItemEstado(111);
                datos_Estado.add(1,dividerRecents);
            }
            if(posG!=-1){
                if(posN!=-1) posG++;
                ItemEstado dividerVistos = new ItemEstado(222);
                datos_Estado.add(posG,dividerVistos);
            }

            adaptadorEstado.notifyDataSetChanged();
        }
        else adaptadorEstado.notifyItemInserted(0);

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void showDialog(ItemEstado estado) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(1);
        View mview=getLayoutInflater().inflate(R.layout.dialog_options_post,null);
        dialog.setContentView(mview);

        View tv_chat, tv_copiar, tv_reporte, tv_eliminar, tv_block_ll, tv_seguir_ll;
        TextViewFontGenGI tv_seguir;

        tv_chat = mview.findViewById(R.id.tv_chat);
        tv_seguir_ll = mview.findViewById(R.id.tv_seguir_ll);
        tv_seguir = mview.findViewById(R.id.tv_seguir);
        tv_copiar = mview.findViewById(R.id.tv_copiar);
        tv_block_ll = mview.findViewById(R.id.tv_block_ll);
        tv_reporte = mview.findViewById(R.id.tv_reporte);
        tv_eliminar = mview.findViewById(R.id.tv_eliminar);

        tv_copiar.setVisibility(View.GONE);
        tv_reporte.setVisibility(View.GONE);
        tv_eliminar.setVisibility(View.GONE);
        tv_block_ll.setVisibility(View.GONE);

        if(estado.getCorreo().equals(YouChatApplication.idOficial)) tv_seguir_ll.setVisibility(View.GONE);
        else if(estado.getCorreo().equals(YouChatApplication.correo)){
            tv_chat.setVisibility(View.GONE);
            tv_seguir_ll.setVisibility(View.GONE);
        }
        tv_seguir.setText("Dejar de seguir");
        String nombre = dbWorker.obtenerNombre(estado.getCorreo());

        tv_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(principalActivity!=null) principalActivity.irAChat(nombre, estado.getCorreo());
            }
        });
        tv_seguir_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(YouChatApplication.estaAndandoChatService() && YouChatApplication.chatService.hayConex){
                    dialog.dismiss();
                    mostrarDialogoDejarDeSeguir(nombre,estado.getCorreo());
                }
                else Utils.ShowToastAnimated(mainActivity,"Compruebe su conexión",R.raw.ic_ban);
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCancelable(true);
        dialog.show();
    }

    private void mostrarDialogoDejarDeSeguir(String nombre, String correo) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(1);
        View mview=getLayoutInflater().inflate(R.layout.dialog_confirm,null);
        dialog.setContentView(mview);

        LinearLayout header=mview.findViewById(R.id.header);
        ImageView icono_eliminar = mview.findViewById(R.id.icono_eliminar);
        TextView text_icono = mview.findViewById(R.id.text_icono);
        TextView text_eliminar = mview.findViewById(R.id.text_eliminar);
        TextView btn_ok=mview.findViewById(R.id.btn_ok);
        View btn_cancel=mview.findViewById(R.id.btn_cancel);

        header.setBackgroundResource(YouChatApplication.colorTemaActual);

        icono_eliminar.setImageResource(R.drawable.remove_seguir);
        text_icono.setText("Dejar de seguir");
        text_eliminar.setText("¿Quieres dejar de seguir a "+nombre+"? Se eliminarán todos " +
                "sus estados y se eliminará de su lista de seguidores.");
        btn_ok.setText("ACEPTAR");

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                dbWorker.eliminarTodosLosEstadosDe(correo);
                dbWorker.eliminarSiguiendoA(correo);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
                Date date = new Date();
                String fechaEntera = sdf.format(date);
                String hora = Convertidor.conversionHora(fechaEntera);
                String fecha = Convertidor.conversionFecha(fechaEntera);
                ItemChat solicitud = new ItemChat(correo,"0");
                solicitud.setId("-ss-");
                solicitud.setHora(hora);
                solicitud.setFecha(fecha);
                YouChatApplication.chatService.enviarMensaje(solicitud,SendMsg.CATEGORY_SOL_SEGUIR);
                Utils.ShowToastAnimated(mainActivity,"Solicitud enviada",R.raw.contact_check);
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCancelable(true);
        dialog.show();
    }

    public void actualizarProgressSubidaNow(boolean seSubio){
        progressSubida++;
        if(adaptadorEstado!=null){
            adaptadorEstado.notifyItemChanged(0,300);
        }
        else if(progressTotal==0 || progressTotal<=progressSubida){
            resetProgressSubida();
        }
        if(!seSubio)
            Utils.ShowToastAnimated(mainActivity,"Error al intentar subir el Now",R.raw.error);
    }
    public int getProgressSubida() {
        return progressSubida;
    }
    public int getProgressTotal() {
        return progressTotal;
    }
    public void resetProgressSubida(){
        progressSubida = progressTotal = 0;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
