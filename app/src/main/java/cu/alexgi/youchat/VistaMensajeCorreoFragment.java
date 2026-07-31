package cu.alexgi.youchat;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import cu.alexgi.youchat.FileExplorer.SimpleFileExplorerFragment;
import cu.alexgi.youchat.adapters.AdaptadorDatosAdjuntoCorreo;
import cu.alexgi.youchat.items.ItemAdjuntoCorreo;
import cu.alexgi.youchat.items.ItemMensajeCorreo;
import cu.alexgi.youchat.progressbar.DownloadProgressView;
import cu.alexgi.youchat.swipebackfragment.BaseSwipeBackFragment;
import cu.alexgi.youchat.views_GI.TextViewFontGenGI;

import static cu.alexgi.youchat.MainActivity.context;
import static cu.alexgi.youchat.MainActivity.dbWorker;
import static cu.alexgi.youchat.MainActivity.mainActivity;
import static cu.alexgi.youchat.MainActivity.permisos;
import static cu.alexgi.youchat.YouChatApplication.bandejaFragment;

public class VistaMensajeCorreoFragment extends BaseSwipeBackFragment{

    private static ItemMensajeCorreo mensajeCorreo;

    private RecyclerView list_adjuntos;
    private ArrayList<ItemAdjuntoCorreo> datos_adjunto;
    private AdaptadorDatosAdjuntoCorreo adaptadorDatosAdjuntoCorreo;

    private View contenedor_adjuntos, contenedor_mensaje_captura;

    //opciones bottomSheet
    private BottomSheetBehavior bsb;
    private View option_respod, option_respod_todos, option_reenviar, option_leido, option_favorito, option_delete, option_copiar, option_share,
            option_screen_shoot;
    private TextView tv_option_favorito;

    private void irANuevoCorreo(String usu, String cor, int tipo) {
        if(mainActivity!=null){
            if(Utils.ocultarKeyBoardEsperar(mainActivity)){
                getActivity().onBackPressed();
                bandejaFragment.irANuevoCorreo(usu,cor,tipo,mensajeCorreo);
            }
            else {
                getActivity().onBackPressed();
                bandejaFragment.irANuevoCorreo(usu,cor,tipo,mensajeCorreo);
            }
        }else {
            getActivity().onBackPressed();
            bandejaFragment.irANuevoCorreo(usu,cor,tipo,mensajeCorreo);
        }
    }

    private void irANuevoCorreoAContacto(int tipo) {
        if(mainActivity!=null){
            if(Utils.ocultarKeyBoardEsperar(mainActivity)){
                getActivity().onBackPressed();
                bandejaFragment.irANuevoCorreoAContacto(tipo,mensajeCorreo);
            }
            else {
                getActivity().onBackPressed();
                bandejaFragment.irANuevoCorreoAContacto(tipo,mensajeCorreo);
            }
        }else {
            getActivity().onBackPressed();
            bandejaFragment.irANuevoCorreoAContacto(tipo,mensajeCorreo);
        }
    }

    private void initComponentesBottomSheet(View view) {
        option_favorito = view.findViewById(R.id.option_favorito);
        option_leido = view.findViewById(R.id.option_leido);
        tv_option_favorito = view.findViewById(R.id.tv_option_favorito);
        option_delete = view.findViewById(R.id.option_delete);
        option_copiar = view.findViewById(R.id.option_copiar);
        option_share = view.findViewById(R.id.option_share);
        option_screen_shoot = view.findViewById(R.id.option_screen_shoot);

        option_respod = view.findViewById(R.id.option_respod);
        option_respod_todos = view.findViewById(R.id.option_respod_todos);
        option_reenviar = view.findViewById(R.id.option_reenviar);

        option_respod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irANuevoCorreo(mensajeCorreo.getNombre(),mensajeCorreo.getRemitente(),1);
            }
        });
        option_respod_todos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irANuevoCorreo(mensajeCorreo.getNombre(),mensajeCorreo.getCorreo(),1);
            }
        });
        option_reenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irANuevoCorreoAContacto(2);
            }
        });

        if(mensajeCorreo.isEsFavorito()) tv_option_favorito.setText("Quitar de\nfavoritos");
        else tv_option_favorito.setText("Añadir a\nfavoritos");

        option_leido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbWorker.marcarComoNoVistoCorreoPor(mensajeCorreo.getId());
                if(bandejaFragment!=null)
                    bandejaFragment.marcarComoVisto(mensajeCorreo.getId(), true);
                if(bsb.getState()!=BottomSheetBehavior.STATE_COLLAPSED)
                    bsb.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        option_favorito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mensajeCorreo.setEsFavorito(!mensajeCorreo.isEsFavorito());
                dbWorker.modificarFavoritoMensajeCorreo(mensajeCorreo.getId(), mensajeCorreo.isEsFavorito());
                if(mensajeCorreo.isEsFavorito()) tv_option_favorito.setText("Quitar de\nfavoritos");
                else tv_option_favorito.setText("Añadir a\nfavoritos");
                if(bsb.getState()!=BottomSheetBehavior.STATE_COLLAPSED)
                    bsb.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        option_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bandejaFragment!=null)
                    bandejaFragment.eliminarMensaje(mensajeCorreo.getId());
                dbWorker.eliminarMensajeCorreo(mensajeCorreo.getId());
                mainActivity.atrasFragment();
            }
        });

        option_copiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mensajeCorreo.getTexto().isEmpty()){
                    ClipData c = ClipData.newPlainText("YouChatCopy", mensajeCorreo.getTexto());
                    YouChatApplication.clipboard.setPrimaryClip(c);
                    Utils.ShowToastAnimated(mainActivity,"Texto copiado al portapapeles",R.raw.voip_invite);
                }
                else Utils.ShowToastAnimated(mainActivity,"No existe texto para copiar",R.raw.error);
                if(bsb.getState()!=BottomSheetBehavior.STATE_COLLAPSED)
                    bsb.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        option_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mensajeCorreo.getTexto().isEmpty()){
                    Intent mShareIntent = new Intent();
                    mShareIntent.setAction(Intent.ACTION_SEND);
                    mShareIntent.setType("text/plain");
                    mShareIntent.putExtra(Intent.EXTRA_TEXT, mensajeCorreo.getTexto());
                    startActivity(Intent.createChooser(mShareIntent,"Compartir texto con:"));
                }
                else Utils.ShowToastAnimated(mainActivity,"No existe texto para compartir",R.raw.error);
                if(bsb.getState()!=BottomSheetBehavior.STATE_COLLAPSED)
                    bsb.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        option_screen_shoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bsb.getState()!=BottomSheetBehavior.STATE_COLLAPSED)
                    bsb.setState(BottomSheetBehavior.STATE_COLLAPSED);
                if(permisos.requestPermissionAlmacenamiento()){
                    Bitmap screenShoot = Utils.tomarImagenDeVista(contenedor_mensaje_captura);
                    try {
                        String fechaEntera = new SimpleDateFormat("yyyyMMddHHmmss",
                                Locale.getDefault()).format(new Date());
                        File ruta = new File(YouChatApplication.RUTA_ADJUNTOS_CORREO);
                        if(!ruta.exists()) ruta.mkdirs();
                        if(ruta.exists()){
                            File file = new File(YouChatApplication.RUTA_ADJUNTOS_CORREO+"scrennshoot"+fechaEntera+".jpg");
                            FileOutputStream outputStream = new FileOutputStream(file);
                            screenShoot.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                            outputStream.flush();
                            outputStream.close();

                            Uri uri;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                                uri = FileProvider.getUriForFile(context,
                                        "cu.alexgi.youchat.fileprovider",file);
                            else uri = Uri.fromFile(file);

                            Intent mShareIntent = new Intent();
                            mShareIntent.setAction(Intent.ACTION_SEND);
                            mShareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mShareIntent.setType("image/*");
                            mShareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                            startActivity(mShareIntent);
                        }
//                        startActivity(Intent.createChooser(mShareIntent,"Compartir captura con:"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public VistaMensajeCorreoFragment() {
    }

    public static VistaMensajeCorreoFragment newInstance(ItemMensajeCorreo mensaje) {
        VistaMensajeCorreoFragment fragment = new VistaMensajeCorreoFragment();
        mensajeCorreo = mensaje;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return attachToSwipeBack(inflater.inflate(R.layout.fragment_vista_mensaje_correo, container, false));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.root)
                .setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_interior()));

        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.atrasFragment();
            }
        });


        contenedor_mensaje_captura = view.findViewById(R.id.contenedor_mensaje_captura);
        contenedor_mensaje_captura.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_interior()));

        View bottomSheetInternal = view.findViewById(R.id.coordinate);
        bsb = BottomSheetBehavior.from(bottomSheetInternal);

        bsb.setPeekHeight(Utils.dpToPx(context, 60f));
        bsb.setState(BottomSheetBehavior.STATE_COLLAPSED);
        /*bsb.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {}
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {}
        });*/

        initComponentesBottomSheet(view);

        if(mensajeCorreo.isEsNuevo()){
            dbWorker.marcarComoVistoCorreoNoVistosPor(mensajeCorreo.getId());
            if(bandejaFragment!=null)
                bandejaFragment.marcarComoVisto(mensajeCorreo.getId(), false);
        }

        if(mensajeCorreo.isEsMio())
            ((TextView)view.findViewById(R.id.titulo)).setText("Correo saliente");
        else ((TextView)view.findViewById(R.id.titulo)).setText("Correo entrante");

        String primeraLetra = (""+mensajeCorreo.getCorreo().charAt(0)).toUpperCase();
        ((TextView)view.findViewById(R.id.tv_primera_letra)).setText(primeraLetra);
        ((CircleImageView)view.findViewById(R.id.preview_chat_image))
                .setCircleBackgroundColor(Utils.obtenerColorDadoUnCorreo(mensajeCorreo.getCorreo()));
        ((TextViewFontGenGI)view.findViewById(R.id.tv_correo_de))
                .setText("De: "+mensajeCorreo.getNombre()+"\nCorreo: "+mensajeCorreo.getRemitente());
        ((TextViewFontGenGI)view.findViewById(R.id.tv_para))
                .setText(mensajeCorreo.getDestinatario());

        SpannableString s = new SpannableString("Asunto: "+mensajeCorreo.getAsunto());
        s.setSpan(new StyleSpan(Typeface.BOLD), 0, 7, 0);
        ((TextViewFontGenGI)view.findViewById(R.id.tv_asunto_de))
                .setText(s);

        ((TextViewFontGenGI)view.findViewById(R.id.tv_contenido))
                .setText(mensajeCorreo.getTexto());
        ((TextViewFontGenGI)view.findViewById(R.id.tv_fecha_de))
                .setText(Convertidor.convertirFechaAFechaLinda(mensajeCorreo.getFecha())+", "+mensajeCorreo.getHora());
        ((TextViewFontGenGI)view.findViewById(R.id.tv_peso))
                .setText(""+Utils.convertirBytes(mensajeCorreo.getPeso()));

        contenedor_adjuntos = view.findViewById(R.id.contenedor_adjuntos);
        list_adjuntos = view.findViewById(R.id.list_adjuntos);
        list_adjuntos.setLayoutManager(new LinearLayoutManager(context,RecyclerView.VERTICAL,false));

        datos_adjunto = dbWorker.obtenerAdjuntosCorreoDe(mensajeCorreo.getId());
        if(datos_adjunto.size()>0){
            adaptadorDatosAdjuntoCorreo =
                    new AdaptadorDatosAdjuntoCorreo(context, datos_adjunto, mensajeCorreo.isEsMio(),this);
            list_adjuntos.setAdapter(adaptadorDatosAdjuntoCorreo);
        }
        else contenedor_adjuntos.setVisibility(View.GONE);
    }

    public void abrirArchivoEn(String rutaArchivo){
        if(!permisos.requestPermissionAlmacenamiento()) return;
        File file = new File(rutaArchivo);
        if(file.exists()){
            String ext= Utils.obtenerExtension(file.getName());
            String tipo;
            if(SimpleFileExplorerFragment.AUDIO.contains(ext)) tipo = "audio/*";
            else if(SimpleFileExplorerFragment.APK.contains(ext)) tipo = "application/vnd.android.package-archive";
            else if(SimpleFileExplorerFragment.VIDEO.contains(ext)) tipo = "video/*";
            else if(SimpleFileExplorerFragment.IMAGEN.contains(ext)) tipo = "image/*";
            else if(SimpleFileExplorerFragment.TXT.contains(ext)) tipo = "text/*";
            else if(SimpleFileExplorerFragment.GIF.contains(ext)) tipo = "image/gif";
            else if(SimpleFileExplorerFragment.COMPRESS.contains(ext)) tipo = "*/*";
            else if(SimpleFileExplorerFragment.XML.contains(ext)) tipo = "text/html";
            else if(SimpleFileExplorerFragment.PDF.contains(ext)) tipo = "application/pdf";
            else tipo = "*/*";

            if(tipo.equals("text/html")){
                if(mAddFragmentListener!=null)
                    mAddFragmentListener.onAddFragment(VistaMensajeCorreoFragment.this,
                            Web_view_fragment.newInstance(rutaArchivo));
            }
            else if(tipo.equals("image/*") || tipo.equals("image/gif")){
                if(mAddFragmentListener!=null)
                    mAddFragmentListener.onAddFragment(VistaMensajeCorreoFragment.this,
                            ImagePager.newInstance(rutaArchivo));
            }
            else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                    Uri uri = FileProvider.getUriForFile(context,
                            "cu.alexgi.youchat.fileprovider",file);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setDataAndType(uri, tipo);
                    startActivity(intent);
                }
                else {
                    Uri uri = Uri.fromFile(file);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, tipo);
                    startActivity(intent);
                }
            }
        }
    }

    public synchronized void descargarCorreo(ItemAdjuntoCorreo adjuntoCorreo, DownloadProgressView downloadProgressView){
        if(YouChatApplication.estaAndandoChatService()){
            if(!YouChatApplication.chatService.estaDescargandoCorreo(mensajeCorreo.getId())){
                Toast.makeText(context, "Descargando", Toast.LENGTH_SHORT).show();
                YouChatApplication.chatService.descargarMensajeCorreoAdjunto(VistaMensajeCorreoFragment.this,
                        mensajeCorreo,adjuntoCorreo,downloadProgressView);
            }
            else Utils.ShowToastAnimated(mainActivity,"Ya se está descargando este mensaje",R.raw.error);
        }
    }

    public synchronized void descargaFallida(String idChat, String idAdj, boolean esFallida){
        if(mensajeCorreo.getId().equals(idChat)){
            if(esFallida) Utils.ShowToastAnimated(mainActivity,"Error al intentar descargar",R.raw.error);
            else Utils.ShowToastAnimated(mainActivity,"Mensaje no encontrado",R.raw.error);
            int l = datos_adjunto.size();
            for(int i=0; i<l; i++){
                if(datos_adjunto.get(i).getId().equals(idAdj)){
                    adaptadorDatosAdjuntoCorreo.notifyItemChanged(i,7);
                    break;
                }
            }
        }
    }

    public void ActualizarMsgDescargado(String idMsgDescargar, String idAdjunto) {
        if(mensajeCorreo.getId().equals(idMsgDescargar)){
            int l = datos_adjunto.size();
            for(int i=0; i<l; i++){
                if(datos_adjunto.get(i).getId().equals(idAdjunto)){
                    adaptadorDatosAdjuntoCorreo.notifyItemChanged(i,7);
                    break;
                }
            }
            Utils.ShowToastAnimated(mainActivity,"Descarga con éxito",R.raw.contact_check);
        }
    }
}