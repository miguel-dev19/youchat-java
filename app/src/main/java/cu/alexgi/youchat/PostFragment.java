package cu.alexgi.youchat;

import android.app.Dialog;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.vanniktech.emoji.EmojiEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import cu.alexgi.youchat.BottomSheetDialogFragment.BottomSheetDialogFragment_comentario_post;
import cu.alexgi.youchat.BottomSheetDialogFragment.BottomSheetDialogFragment_opciones_post;
import cu.alexgi.youchat.adapters.AdaptadorDatosPost;
import cu.alexgi.youchat.items.ItemChat;
import cu.alexgi.youchat.items.ItemComentarioPost;
import cu.alexgi.youchat.items.ItemContacto;
import cu.alexgi.youchat.items.ItemDesImgPost;
import cu.alexgi.youchat.items.ItemDetallesTarjeta;
import cu.alexgi.youchat.items.ItemPost;
import cu.alexgi.youchat.items.ItemTemas;
import cu.alexgi.youchat.items.ItemUsuario;
import cu.alexgi.youchat.views_GI.TextViewFontGenGI;
import cu.alexgi.youchat.views_GI.TextViewFontGenOscuroGI;
import cu.alexgi.youchat.views_GI.TextViewFontResGI;
import cu.alexgi.youchat.views_GI.TextViewPostGI;

import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE;
import static cu.alexgi.youchat.MainActivity.context;
import static cu.alexgi.youchat.MainActivity.dbWorker;
import static cu.alexgi.youchat.MainActivity.mainActivity;
import static cu.alexgi.youchat.YouChatApplication.principalActivity;

public class PostFragment extends Fragment {

    private int val = 0, iconPost;

    private RecyclerView lista_post;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<ItemPost> datos_post;
    private AdaptadorDatosPost adaptadorDatosPost;
    private SwipeRefreshLayout swipeRefreshPost;

    private LottieAnimationView anim;
    private View list_empty;

    private boolean hayDivisorNuevos;

    public PostFragment() {
    }

    public static PostFragment newInstance() {
        PostFragment fragment = new PostFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        hayDivisorNuevos = false;
        list_empty = view.findViewById(R.id.list_empty);
        anim = view.findViewById(R.id.anim);

        view.findViewById(R.id.root).setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_fondo()));

        swipeRefreshPost = view.findViewById(R.id.swipeRefreshPost);
        lista_post = view.findViewById(R.id.lista_post);
        linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        lista_post.setLayoutManager(linearLayoutManager);
        datos_post = new ArrayList<>();
//        adaptadorDatosPost = new AdaptadorDatosPost(context, datos_post, this);
        adaptadorDatosPost = new AdaptadorDatosPost(context, datos_post);
        lista_post.setAdapter(adaptadorDatosPost);

        adaptadorDatosPost.setOnItemLongClick(new AdaptadorDatosPost.OnItemLongClick() {
            @Override
            public void onLongClick(ItemPost post) {
                showDialog(post);
            }
            @Override
            public void abrirVisorImagen(String ruta) {
                abrirVisorImagenEn(ruta);
            }

            @Override
            public void descargarImgPost(ItemDesImgPost itemDesImgPost) {
                descargarImagenPost(itemDesImgPost);
            }

            @Override
            public void scrollUp() {
                scrollToUp();
            }

            @Override
            public void abrirPerfilDe(String nombre, String correo) {
                if(principalActivity!=null)
                    principalActivity.irAperfil(nombre,correo,false);
            }

            @Override
            public void abrirBottomSheetComentarios(ArrayList<ItemComentarioPost> list_comentario) {
                abrirBottomSheet(list_comentario);
            }

            @Override
            public void responderPost(ItemPost post) {
                mostrarBottomSheetResponderPost(post);
            }

            @Override
            public void compartirPost(ItemPost post) {
                compartirPostPor(post);
            }

            @Override
            public void comentarPost(ItemPost post, String cad) {
                enviarComentarioPost(post,cad);
            }
        });

        actualizarListaPost(false, false);

        lista_post.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                funcionesScroll();
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(lista_post);

        swipeRefreshPost.setColorSchemeColors(Color.parseColor(YouChatApplication.itemTemas.getColor_btn()));
        swipeRefreshPost.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(YouChatApplication.estaAndandoChatService()){
                    if(YouChatApplication.chatService.hayConex){
                        if(!YouChatApplication.estaCorriendoPost){
                            int cantPost = dbWorker.obtenerCantTotalPosts();
                            if(!YouChatApplication.limitarPost
                                    || cantPost<YouChatApplication.cantLimitePost){
                                if(datos_post!=null && datos_post.size()>0)
                                    YouChatApplication.setIdPosUltPostVisto(datos_post.get(0).getId());
                                context.startService(new Intent(context,IntentServiceEstPostGlobales.class));
                            }
                            else {
                                Utils.runOnUIThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Utils.ShowToastAnimated(mainActivity,
                                                "Ha alcanzado el límite de Post "
                                                        +cantPost +"/"+YouChatApplication.cantLimitePost,
                                                R.raw.ic_ban);
                                        swipeRefreshPost.setRefreshing(false);
                                    }
                                });
                            }
                        }
                        else{
                            Utils.ShowToastAnimated(mainActivity,
                                    "Ya existe un proceso corriendo",
                                    R.raw.ic_ban);
                            swipeRefreshPost.setRefreshing(false);
                        }
                    }
                    else{
                        Utils.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                Utils.mostrarToastDeConexion(mainActivity);
                                swipeRefreshPost.setRefreshing(false);
                            }
                        });
                    }
                }
            }
        });
    }

    private void abrirBottomSheet(ArrayList<ItemComentarioPost> list_comentario) {
        BottomSheetDialogFragment_comentario_post bbb = BottomSheetDialogFragment_comentario_post.newInstance(context,list_comentario);
        bbb.setOnItemClickListener(new BottomSheetDialogFragment_comentario_post.OnItemClickListener() {
            @Override
            public void abrirPerfil(String nombre, String correo) {
                if(principalActivity!=null)
                    principalActivity.irAperfil(nombre,correo,false);
            }
        });
        bbb.show(getParentFragmentManager(),"BottomSheetDialogFragment_comentario_post");
    }

    private synchronized void funcionesScroll(){
        if(hayDivisorNuevos && !swipeRefreshPost.isRefreshing()){
            if(linearLayoutManager.findFirstCompletelyVisibleItemPosition()==0){
                Utils.runOnUIThread(()->{
                    if(hayDivisorNuevos){
                        for(int i=0; i<datos_post.size(); i++){
                            if(datos_post.get(i).getTipo_post()==-2){
                                datos_post.remove(i);
                                adaptadorDatosPost.notifyItemRemoved(i);
                                if(datos_post.size()<10 && datos_post.size()>0
                                        && datos_post.get(datos_post.size()-1).getTipo_post()==-1){
                                    int position = datos_post.size()-1;
                                    datos_post.remove(position);
                                    adaptadorDatosPost.notifyItemRemoved(position);
                                }
                            }
                        }
                        hayDivisorNuevos = false;
                    }
                },1000);

            }
        }
    }

    public void detenerRefresh(){
        Utils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if(swipeRefreshPost!=null && swipeRefreshPost.isRefreshing()){
                    swipeRefreshPost.setRefreshing(false);
                    if(datos_post!=null && datos_post.size()>0)
                        YouChatApplication.setIdPosUltPostVisto(datos_post.get(0).getId());
                }
            }
        }, 300);
    }

    public void showDialog(ItemPost item){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(1);
        View mview=getLayoutInflater().inflate(R.layout.dialog_options_post,null);
        dialog.setContentView(mview);

        View tv_guardar_galeria, tv_chat, tv_copiar, tv_reporte, tv_eliminar, tv_block_ll, tv_seguir_ll;
        TextViewFontGenGI tv_block, tv_seguir;
        tv_chat = mview.findViewById(R.id.tv_chat);
        tv_seguir_ll = mview.findViewById(R.id.tv_seguir_ll);
        tv_seguir = mview.findViewById(R.id.tv_seguir);
        tv_copiar = mview.findViewById(R.id.tv_copiar);
        tv_guardar_galeria = mview.findViewById(R.id.tv_guardar_galeria);
        tv_block_ll = mview.findViewById(R.id.tv_block_ll);
        tv_block = mview.findViewById(R.id.tv_block);
        tv_reporte = mview.findViewById(R.id.tv_reporte);
        tv_eliminar = mview.findViewById(R.id.tv_eliminar);

        if(item.getCorreo().equals(YouChatApplication.idOficial)){
            tv_block_ll.setVisibility(View.GONE);
        }
        else {
            if(dbWorker.existeBloqueadoPost(item.getCorreo()))
                tv_block.setText("Desbloquear");
        }

        boolean lo_sigues=dbWorker.existeSiguiendoA(item.getCorreo());
        if(lo_sigues) tv_seguir.setText("Dejar de seguir");
        else tv_seguir.setText("Seguir");

        if(item.getCorreo().equals(YouChatApplication.idOficial)
                || item.getCorreo().equals(YouChatApplication.correo)){
//            tv_responder.setVisibility(View.GONE);
            tv_chat.setVisibility(View.GONE);
            tv_seguir_ll.setVisibility(View.GONE);
            tv_reporte.setVisibility(View.GONE);
        }

        tv_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(principalActivity!=null) principalActivity.irAChat(item.getNombre(), item.getCorreo());
            }
        });
        tv_seguir_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(YouChatApplication.estaAndandoChatService() && YouChatApplication.chatService.hayConex){
                    dialog.dismiss();
                    if(lo_sigues){
                        mostrarDialogoDejarDeSeguir(item.getNombre(),item.getCorreo());
                    }else {
                        mostrarDialogoSeguir(item.getNombre(),item.getCorreo());
//                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
//                        Date date = new Date();
//                        String fechaEntera = sdf.format(date);
//                        String hora = Convertidor.conversionHora(fechaEntera);
//                        String fecha = Convertidor.conversionFecha(fechaEntera);
//                        ItemChat solicitud = new ItemChat(item.getCorreo(),"1");
//                        solicitud.setId("-ss-");
//                        solicitud.setHora(hora);
//                        solicitud.setFecha(fecha);
//                        YouChatApplication.chatService.enviarMensaje(solicitud,SendMsg.CATEGORY_SOL_SEGUIR);
//                        Utils.ShowToastAnimated(mainActivity,"Solicitud enviada",R.raw.contact_check);
                    }
                }
                else Utils.ShowToastAnimated(mainActivity,"Compruebe su conexión",R.raw.ic_ban);
            }
        });
        tv_copiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ClipData c = ClipData.newPlainText("YouChatCopy", item.getTexto());
                YouChatApplication.clipboard.setPrimaryClip(c);
                Utils.ShowToastAnimated(mainActivity,"Texto copiado al portapapeles",R.raw.voip_invite);
            }
        });
        if(item.esTipoImagen() && new File(item.getRuta_dato()).exists()){
            tv_guardar_galeria.setVisibility(View.VISIBLE);
            tv_guardar_galeria.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    boolean seGuardo = Utils.guardarEnGaleria(item.getRuta_dato());
                    if(seGuardo) Utils.ShowToastAnimated(mainActivity,"Imagen guardada con éxito", R.raw.contact_check);
                    else Utils.ShowToastAnimated(mainActivity,"Error al guardar la imagen", R.raw.error);
                }
            });
        }
        tv_block_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(dbWorker.existeBloqueadoPost(item.getCorreo()))
                    dbWorker.eliminarBloqueadoPost(item.getCorreo());
                else dbWorker.insertarNuevoBloqueadoPost(item.getCorreo());
            }
        });
        tv_reporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(YouChatApplication.estaAndandoChatService() && YouChatApplication.chatService.hayConex){
                    dialog.dismiss();
                    mostrarBottomSheetReportePost(item.getFicha());
                }
                else Utils.mostrarToastDeConexion(mainActivity);

            }
        });
        tv_eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                int position = buscarIDPost(item.getId());
                if(position!=-1){
                    if(YouChatApplication.activePostHistorial)
                        dbWorker.ocultarPost(datos_post.get(position).getId());
                    else dbWorker.eliminarPost(datos_post.get(position).getId());

                    datos_post.remove(position);
                    adaptadorDatosPost.notifyItemRemoved(position);

                    if(datos_post.size()==0){
                        anim.setAnimation(R.raw.new_empty_file);
                        anim.playAnimation();
                        anim.setVisibility(View.VISIBLE);
                        list_empty.setVisibility(View.VISIBLE);
                    }
                    else {
                        anim.setAnimation((Animation) null);
                        anim.cancelAnimation();
                        anim.setVisibility(View.GONE);
                        list_empty.setVisibility(View.GONE);
                        if(position==0)
                            YouChatApplication.setIdPosUltPostVisto(datos_post.get(0).getId());
                        if(hayDivisorNuevos){
                            for(int i=0; i<datos_post.size(); i++){
                                if(datos_post.get(i).getTipo_post()==-2){
                                    datos_post.remove(i);
                                    adaptadorDatosPost.notifyItemRemoved(i);
                                }
                            }
                            hayDivisorNuevos = false;
                        }
                        if(datos_post.size()<10 && datos_post.size()>0
                                && datos_post.get(datos_post.size()-1).getTipo_post()==-1){
                            position = datos_post.size()-1;
                            datos_post.remove(position);
                            adaptadorDatosPost.notifyItemRemoved(position);
                        }
                    }
                }
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCancelable(true);
        dialog.show();
    }

    private void mostrarBottomSheetResponderPost(ItemPost post) {
        BottomSheetDialog bottomSheetDialog= new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_responder_post);

        ImageView foto_noti, user_type;
        TextViewFontGenGI correo_noti;
        TextViewPostGI mensaje_noti;
        TextViewFontGenOscuroGI hora_noti;
        TextViewFontResGI nombre_noti;

        foto_noti = bottomSheetDialog.findViewById(R.id.foto_noti);
        nombre_noti = bottomSheetDialog.findViewById(R.id.nombre_noti);
        correo_noti = bottomSheetDialog.findViewById(R.id.correo_noti);
        mensaje_noti = bottomSheetDialog.findViewById(R.id.mensaje_noti);
        hora_noti = bottomSheetDialog.findViewById(R.id.hora_noti);
        user_type = bottomSheetDialog.findViewById(R.id.user_type);

        if(post.getTipo_usuario()==5 || post.getTipo_usuario()==4){
            user_type.setImageResource(R.drawable.verified_profile);
            user_type.setVisibility(View.VISIBLE);
        }
        else if(post.getTipo_usuario()==3){
            user_type.setImageResource(R.drawable.vip_crown_line);
            user_type.setVisibility(View.VISIBLE);
        }
        else if(post.getTipo_usuario()==2){
            user_type.setImageResource(R.drawable.vip_diamond_line);
            user_type.setVisibility(View.VISIBLE);
        }
        else if(post.getTipo_usuario()==1){
            user_type.setImageResource(R.drawable.award_line);
            user_type.setVisibility(View.VISIBLE);
        }
        else user_type.setVisibility(View.GONE);

        switch (post.getIcono()){
            case 1:
                Glide.with(context).load(R.drawable.noti1).dontAnimate().into(foto_noti);
                break;
            case 2:
                Glide.with(context).load(R.drawable.noti2).dontAnimate().into(foto_noti);
                break;
            case 3:
                Glide.with(context).load(R.drawable.noti3).dontAnimate().into(foto_noti);
                break;
            case 4:
                Glide.with(context).load(R.drawable.noti4).dontAnimate().into(foto_noti);
                break;
            case 5:
                Glide.with(context).load(R.drawable.noti5).dontAnimate().into(foto_noti);
                break;
            case 6:
                Glide.with(context).load(R.drawable.noti6).dontAnimate().into(foto_noti);
                break;
            case 7:
                Glide.with(context).load(R.drawable.noti7).dontAnimate().into(foto_noti);
                break;
            case 8:
                Glide.with(context).load(R.drawable.noti8).dontAnimate().into(foto_noti);
                break;
            case 9:
                Glide.with(context).load(R.drawable.noti9).dontAnimate().into(foto_noti);
                break;
            case 10:
                Glide.with(context).load(R.drawable.noti10).dontAnimate().into(foto_noti);
                break;
            case 11:
                Glide.with(context).load(R.drawable.noti11).dontAnimate().into(foto_noti);
                break;
            case 12:
                Glide.with(context).load(R.drawable.noti12).dontAnimate().into(foto_noti);
                break;
            case 13:
                Glide.with(context).load(R.drawable.noti13).dontAnimate().into(foto_noti);
                break;
            case 14:
                Glide.with(context).load(R.drawable.noti14).dontAnimate().into(foto_noti);
                break;
            case 15:
                Glide.with(context).load(R.drawable.noti15).dontAnimate().into(foto_noti);
                break;
            case 16:
                Glide.with(context).load(R.drawable.noti16).dontAnimate().into(foto_noti);
                break;
            default: Glide.with(context).load(R.drawable.noti1).dontAnimate().into(foto_noti);
        }
        nombre_noti.setText(post.getNombre());
        correo_noti.setText(post.getCorreo());
        mensaje_noti.setText(post.getTexto());
        hora_noti.setText(Convertidor.convertirFechaAFechaLinda(post.getFecha())+", "+post.getHora());

        final EmojiEditText editext = bottomSheetDialog.findViewById(R.id.editext);
        View aceptar = bottomSheetDialog.findViewById(R.id.aceptar);

        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mensaje=editext.getText().toString().trim();
                if(mensaje.isEmpty()) Utils.ShowToastAnimated(mainActivity, "El mensaje no puede estar vacío", R.raw.chats_infotip);
                else {
                    if(YouChatApplication.estaAndandoChatService()
                            && YouChatApplication.chatService.hayConex){
                        bottomSheetDialog.dismiss();
                        String correo = post.getCorreo();
                        ItemUsuario usuario = new ItemUsuario(correo);
                        ItemContacto contacto = new ItemContacto(correo, correo);
                        dbWorker.insertarNuevoUsuario(usuario);
                        dbWorker.insertarNuevoContactoNoVisible(contacto, true);

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
                        Date date = new Date();
                        String orden = sdf.format(date);

                        String id = "YouChat/chat/" + correo + "/24/" + orden;
                        String hora = Convertidor.conversionHora(orden);
                        String fecha = Convertidor.conversionFecha(orden);

                        ItemChat newChat = new ItemChat(id,
                                24, 1, correo, mensaje, "",
                                hora, fecha, post.getId(),
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
                    }
                    else Utils.ShowToastAnimated(mainActivity,"Compruebe su conexión",R.raw.ic_ban);
                }
            }
        });

        bottomSheetDialog.show();
        View bottomSheetInternal = bottomSheetDialog.findViewById(R.id.design_bottom_sheet);
        BottomSheetBehavior bsb = BottomSheetBehavior.from(bottomSheetInternal);
        bsb.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void mostrarDialogoSeguir(String nombre, String correo) {
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
        icono_eliminar.setImageResource(R.drawable.add_user);
        text_icono.setText("Seguir");
        text_eliminar.setText("¿Quieres seguir a "+nombre+"? Se enviará una solicitud pidiendo ser " +
                "parte de su lista de seguidores, estará en sus manos aceptarte o no.");
        btn_ok.setText("ACEPTAR");

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
                Date date = new Date();
                String fechaEntera = sdf.format(date);
                String hora = Convertidor.conversionHora(fechaEntera);
                String fecha = Convertidor.conversionFecha(fechaEntera);
                ItemChat solicitud = new ItemChat(correo,"1");
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

    private void mostrarBottomSheetReportePost(String ficha) {
        BottomSheetDialog bottomSheetDialog= new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(R.layout.dialog_contactenos_reporte_post);

        final EmojiEditText editext = bottomSheetDialog.findViewById(R.id.editext);
        MaterialCheckBox show_email = bottomSheetDialog.findViewById(R.id.show_email);

        View aceptar = bottomSheetDialog.findViewById(R.id.aceptar);

        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mensaje=editext.getText().toString().trim();
                if(mensaje.length()<=5) Utils.ShowToastAnimated(mainActivity, "Mensaje muy corto", R.raw.chats_infotip);
                else if(mensaje.length()>500) Utils.ShowToastAnimated(mainActivity, "Mensaje muy largo", R.raw.chats_infotip);
                else{
                    if(YouChatApplication.estaAndandoChatService()
                            && YouChatApplication.chatService.hayConex){
                        bottomSheetDialog.dismiss();

                        String texto="#reportePost\n"+ficha;
                        if(show_email.isChecked()) texto+=YouChatApplication.correo+"\n";
                        texto+="Motivo del reporte:\n"+mensaje;

                        ItemChat msg=new ItemChat( "","");
                        msg.setMensaje(texto);
                        YouChatApplication.chatService.enviarMensaje(msg,SendMsg.CATEGORY_REPORTE_TELEGRAM);
                        Utils.ShowToastAnimated(mainActivity,"Enviado correctamente",R.raw.contact_check);
                    }
                    else Utils.ShowToastAnimated(mainActivity,"Compruebe su conexión",R.raw.ic_ban);
                }
            }
        });

        bottomSheetDialog.show();
        View bottomSheetInternal = bottomSheetDialog.findViewById(R.id.design_bottom_sheet);
        BottomSheetBehavior bsb = BottomSheetBehavior.from(bottomSheetInternal);
        bsb.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private int buscarIDPost(String id) {
        if(id.isEmpty()) return -1;
        if(datos_post==null) return -1;
        int l = datos_post.size();
        for(int i=0 ; i<l ; i++){
            if(id.equals(datos_post.get(i).getId())){
                return i;
            }
        }
        return -1;
    }

    public synchronized void actualizarListaPost(boolean hacerScroll, boolean guardar) {
        if(principalActivity!=null)
            PrincipalActivity.badgeInTabs(0,3);
        ArrayList<ItemPost> newPost = dbWorker.obtenerPostsNuevos();
        datos_post.clear();

        adaptadorDatosPost.notifyDataSetChanged();
        datos_post.addAll(newPost);
        if(datos_post.size()==0){
            anim.setAnimation(R.raw.new_empty_file);
            anim.playAnimation();
            anim.setVisibility(View.VISIBLE);
            list_empty.setVisibility(View.VISIBLE);
        }
        else {
            anim.setAnimation((Animation) null);
            anim.cancelAnimation();
            anim.setVisibility(View.GONE);
            list_empty.setVisibility(View.GONE);
            if(datos_post.size()>=10) datos_post.add(new ItemPost(-1));

            int pos = buscarIDPost(YouChatApplication.idPosUltPostVisto);
            if(pos>0){
                hayDivisorNuevos = true;
                if(pos>=datos_post.size()) pos=datos_post.size()-1;
                datos_post.add(pos, new ItemPost(-2, pos));
                if(hacerScroll) lista_post.scrollToPosition(pos);
                if(guardar) YouChatApplication.setIdPosUltPostVisto(datos_post.get(0).getId());
            } else hayDivisorNuevos = false;
        }
        adaptadorDatosPost.notifyDataSetChanged();
    }

    public synchronized void crearPost(int x){
        if(YouChatApplication.puedeSubirPost) principalActivity.abrirNuevoPost(x);
        else Utils.ShowToastAnimated(mainActivity,"No puede publicar Post",R.raw.swipe_disabled);
    }

    /*public synchronized void crearPostPrueba(){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(1);
        View mviewe = getLayoutInflater().inflate(R.layout.dialog_send_post, null);
        dialog.setContentView(mviewe);

        iconPost = 1;
        View tv_icon_post,ll_icon_post1, ll_icon_post2, ll_icon_post3;
        final ImageView icon=mviewe.findViewById(R.id.icon);
        View cancelar,enviar,noti1,noti2,noti3,noti4,noti5,noti6,noti7,noti8,noti9,noti10,noti11,noti12,noti13,noti14,noti15;
        final EditTextFontGenGI texto=mviewe.findViewById(R.id.texto);
        enviar=mviewe.findViewById(R.id.aceptar);
        cancelar=mviewe.findViewById(R.id.cancelar);
        noti1=mviewe.findViewById(R.id.noti1);
        noti2=mviewe.findViewById(R.id.noti2);
        noti3=mviewe.findViewById(R.id.noti3);
        noti4=mviewe.findViewById(R.id.noti4);
        noti5=mviewe.findViewById(R.id.noti5);
        noti6=mviewe.findViewById(R.id.noti6);
        noti7=mviewe.findViewById(R.id.noti7);
        noti8=mviewe.findViewById(R.id.noti8);
        noti9=mviewe.findViewById(R.id.noti9);
        noti10=mviewe.findViewById(R.id.noti10);
        noti11=mviewe.findViewById(R.id.noti11);
        noti12=mviewe.findViewById(R.id.noti12);
        noti13=mviewe.findViewById(R.id.noti13);
        noti14=mviewe.findViewById(R.id.noti14);
        noti15=mviewe.findViewById(R.id.noti15);

        ll_icon_post1=mviewe.findViewById(R.id.ll_icon_post1);
        ll_icon_post2=mviewe.findViewById(R.id.ll_icon_post2);
        ll_icon_post3=mviewe.findViewById(R.id.ll_icon_post3);
        tv_icon_post=mviewe.findViewById(R.id.tv_icon_post);
        TextViewFontGenGI tv_cant_letra_post;
        tv_cant_letra_post=mviewe.findViewById(R.id.tv_cant_letra_post);
        texto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                int longi = s.length();
                tv_cant_letra_post.setText(String.format("%03d/256",longi));
            }
        });

        if(YouChatApplication.es_beta_tester
                || YouChatApplication.comprobarOficialidad(YouChatApplication.correo)){
            ll_icon_post1.setVisibility(View.VISIBLE);
            ll_icon_post2.setVisibility(View.VISIBLE);
            ll_icon_post3.setVisibility(View.VISIBLE);
            tv_icon_post.setVisibility(View.VISIBLE);
        }
        else{
            int cantSeguidores = dbWorker.obtenerCantSeguidores();
            if(cantSeguidores>=YouChatApplication.usuMenor){
                ll_icon_post1.setVisibility(View.VISIBLE);
                tv_icon_post.setVisibility(View.VISIBLE);
            }
            if(cantSeguidores>=YouChatApplication.usuMedio) ll_icon_post2.setVisibility(View.VISIBLE);
            if(cantSeguidores>=YouChatApplication.usuMayor) ll_icon_post3.setVisibility(View.VISIBLE);
        }

        noti1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iconPost=1;
                icon.setImageResource(R.drawable.noti1);
            }
        });
        noti2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iconPost=2;
                icon.setImageResource(R.drawable.noti2);
            }
        });
        noti3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iconPost=3;
                icon.setImageResource(R.drawable.noti3);
            }
        });
        noti4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iconPost=4;
                icon.setImageResource(R.drawable.noti4);
            }
        });
        noti5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iconPost=5;
                icon.setImageResource(R.drawable.noti5);
            }
        });
        noti6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iconPost=6;
                icon.setImageResource(R.drawable.noti6);
            }
        });
        noti7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iconPost=7;
                icon.setImageResource(R.drawable.noti7);
            }
        });
        noti8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iconPost=8;
                icon.setImageResource(R.drawable.noti8);
            }
        });
        noti9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iconPost=9;
                icon.setImageResource(R.drawable.noti9);
            }
        });
        noti10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iconPost=10;
                icon.setImageResource(R.drawable.noti10);
            }
        });
        noti11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iconPost=11;
                icon.setImageResource(R.drawable.noti11);
            }
        });
        noti12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iconPost=12;
                icon.setImageResource(R.drawable.noti12);
            }
        });
        noti13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iconPost=13;
                icon.setImageResource(R.drawable.noti13);
            }
        });
        noti14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iconPost=14;
                icon.setImageResource(R.drawable.noti14);
            }
        });
        noti15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iconPost=15;
                icon.setImageResource(R.drawable.noti15);
            }
        });

        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mensaje=texto.getText().toString().trim();
                if(!mensaje.isEmpty()) {
                    if(YouChatApplication.estaAndandoChatService() && YouChatApplication.chatService.hayConex){
                        enviarPostPrueba(iconPost,mensaje);
                        dialog.dismiss();
                    }
                    else Utils.ShowToastAnimated(mainActivity, "Compruebe su conexión", R.raw.ic_ban);
                }
                else Utils.ShowToastAnimated(mainActivity,"Debe escribir algo",R.raw.error);
            }
        });
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCancelable(true);
        dialog.show();
    }*/

    public static synchronized void enviarPost(int icono, String texto){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
        Date date = new Date();
        String fechaEntera = sdf.format(date);
        String idPost=YouChatApplication.correo+""+fechaEntera;
        int tipoUsu = 0;
        int cantSeguidores = dbWorker.obtenerCantSeguidores();
        if(YouChatApplication.comprobarOficialidad(YouChatApplication.correo)) tipoUsu=5;
        else if(YouChatApplication.es_beta_tester) tipoUsu=4;
        else if(cantSeguidores>=YouChatApplication.usuMayor) tipoUsu=3;
        else if(cantSeguidores>=YouChatApplication.usuMedio) tipoUsu=2;
        else if(cantSeguidores>=YouChatApplication.usuMenor) tipoUsu=1;

        ItemChat newPostChat = new ItemChat("",tipoUsu+"<s,p>"+icono);
        newPostChat.setTipo_mensaje(1);
        newPostChat.setMensaje(YouChatApplication.alias+"<s,p>"+texto);
        newPostChat.setId(idPost);

        if(YouChatApplication.estaAndandoChatService())
            YouChatApplication.chatService.enviarMensaje(newPostChat,SendMsg.CATEGORY_POST);
        Utils.ShowToastAnimated(mainActivity,"Enviando Post",R.raw.contact_check);

    }

    public static synchronized void enviarPostImagen(int icono, String texto, String ruta){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
        Date date = new Date();
        String fechaEntera = sdf.format(date);
        String idPost=YouChatApplication.correo+""+fechaEntera;
        int tipoUsu = 0;
        int cantSeguidores = dbWorker.obtenerCantSeguidores();
        if(YouChatApplication.comprobarOficialidad(YouChatApplication.correo)) tipoUsu=5;
        else if(YouChatApplication.es_beta_tester) tipoUsu=4;
        else if(cantSeguidores>=YouChatApplication.usuMayor) tipoUsu=3;
        else if(cantSeguidores>=YouChatApplication.usuMedio) tipoUsu=2;
        else if(cantSeguidores>=YouChatApplication.usuMenor) tipoUsu=1;

        ItemChat newPostChat = new ItemChat("",tipoUsu+"<s,p>"+icono);
        if(new File(ruta).exists()){
            newPostChat.setRuta_Dato(ruta);
            newPostChat.setTipo_mensaje(2);
        }
        else newPostChat.setTipo_mensaje(1);

        newPostChat.setMensaje(YouChatApplication.alias+"<s,p>"+texto);
        newPostChat.setId(idPost);

        if(YouChatApplication.estaAndandoChatService())
            YouChatApplication.chatService.enviarMensaje(newPostChat,SendMsg.CATEGORY_POST);
        Utils.ShowToastAnimated(mainActivity,"Enviando Post",R.raw.contact_check);

    }

    public static void enviarPostTema(int icono, String texto, ItemTemas temaCompartir) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
        Date date = new Date();
        String fechaEntera = sdf.format(date);
        String idPost=YouChatApplication.correo+""+fechaEntera;
        int tipoUsu = 0;
        int cantSeguidores = dbWorker.obtenerCantSeguidores();
        if(YouChatApplication.comprobarOficialidad(YouChatApplication.correo)) tipoUsu=5;
        else if(YouChatApplication.es_beta_tester) tipoUsu=4;
        else if(cantSeguidores>=YouChatApplication.usuMayor) tipoUsu=3;
        else if(cantSeguidores>=YouChatApplication.usuMedio) tipoUsu=2;
        else if(cantSeguidores>=YouChatApplication.usuMenor) tipoUsu=1;

        ItemChat newPostChat = new ItemChat("",tipoUsu+"<s,p>"+icono);
        newPostChat.setRuta_Dato(temaCompartir.temaToMensaje());
        newPostChat.setTipo_mensaje(3);

        newPostChat.setMensaje(YouChatApplication.alias+"<s,p>"+texto);
        newPostChat.setId(idPost);

        if(YouChatApplication.estaAndandoChatService())
            YouChatApplication.chatService.enviarMensaje(newPostChat,SendMsg.CATEGORY_POST);
        Utils.ShowToastAnimated(mainActivity,"Enviando Post",R.raw.contact_check);
    }

    public static void enviarPostTarjeta(int icono, String texto, ItemDetallesTarjeta detallesTarjeta) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
        Date date = new Date();
        String fechaEntera = sdf.format(date);
        String idPost=YouChatApplication.correo+""+fechaEntera;
        int tipoUsu = 0;
        int cantSeguidores = dbWorker.obtenerCantSeguidores();
        if(YouChatApplication.comprobarOficialidad(YouChatApplication.correo)) tipoUsu=5;
        else if(YouChatApplication.es_beta_tester) tipoUsu=4;
        else if(cantSeguidores>=YouChatApplication.usuMayor) tipoUsu=3;
        else if(cantSeguidores>=YouChatApplication.usuMedio) tipoUsu=2;
        else if(cantSeguidores>=YouChatApplication.usuMenor) tipoUsu=1;

        ItemChat newPostChat = new ItemChat("",tipoUsu+"<s,p>"+icono);
        newPostChat.setRuta_Dato(detallesTarjeta.detallesTarjetaToMensaje());
        newPostChat.setTipo_mensaje(4);

        newPostChat.setMensaje(YouChatApplication.alias+"<s,p>"+texto+"<s,p>");
        newPostChat.setId(idPost);

        if(YouChatApplication.estaAndandoChatService())
            YouChatApplication.chatService.enviarMensaje(newPostChat,SendMsg.CATEGORY_POST);
        Utils.ShowToastAnimated(mainActivity,"Enviando Post",R.raw.contact_check);
    }
    /////////////////////////////////////////////////////////
    public static synchronized void enviarPostPrueba(int icono, String texto){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
        Date date = new Date();
        String fechaEntera = sdf.format(date);
        String idPost=YouChatApplication.correo+""+fechaEntera;
        int tipoUsu = 0;
        int cantSeguidores = dbWorker.obtenerCantSeguidores();
        if(YouChatApplication.comprobarOficialidad(YouChatApplication.correo)) tipoUsu=5;
        else if(YouChatApplication.es_beta_tester) tipoUsu=4;
        else if(cantSeguidores>=YouChatApplication.usuMayor) tipoUsu=3;
        else if(cantSeguidores>=YouChatApplication.usuMedio) tipoUsu=2;
        else if(cantSeguidores>=YouChatApplication.usuMenor) tipoUsu=1;

        ItemChat newPostChat = new ItemChat("",tipoUsu+"<s,p>"+icono);
        newPostChat.setTipo_mensaje(1);
        newPostChat.setMensaje(YouChatApplication.alias+"<s,p>"+texto);
        newPostChat.setId(idPost);

        if(YouChatApplication.estaAndandoChatService())
            YouChatApplication.chatService.enviarMensaje(newPostChat,SendMsg.CATEGORY_POST2);
        Utils.ShowToastAnimated(mainActivity,"Enviando Post",R.raw.contact_check);

    }

    public static synchronized void enviarPostPruebaImagen(int icono, String texto, String ruta){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
        Date date = new Date();
        String fechaEntera = sdf.format(date);
        String idPost=YouChatApplication.correo+""+fechaEntera;
        int tipoUsu = 0;
        int cantSeguidores = dbWorker.obtenerCantSeguidores();
        if(YouChatApplication.comprobarOficialidad(YouChatApplication.correo)) tipoUsu=5;
        else if(YouChatApplication.es_beta_tester) tipoUsu=4;
        else if(cantSeguidores>=YouChatApplication.usuMayor) tipoUsu=3;
        else if(cantSeguidores>=YouChatApplication.usuMedio) tipoUsu=2;
        else if(cantSeguidores>=YouChatApplication.usuMenor) tipoUsu=1;

        ItemChat newPostChat = new ItemChat("",tipoUsu+"<s,p>"+icono);
        if(new File(ruta).exists()){
            newPostChat.setRuta_Dato(ruta);
            newPostChat.setTipo_mensaje(2);
        }
        else newPostChat.setTipo_mensaje(1);

        newPostChat.setMensaje(YouChatApplication.alias+"<s,p>"+texto);
        newPostChat.setId(idPost);

        if(YouChatApplication.estaAndandoChatService())
            YouChatApplication.chatService.enviarMensaje(newPostChat,SendMsg.CATEGORY_POST2);
        Utils.ShowToastAnimated(mainActivity,"Enviando Post",R.raw.contact_check);

    }

    public static void enviarPostPruebaTema(int icono, String texto, ItemTemas temaCompartir) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
        Date date = new Date();
        String fechaEntera = sdf.format(date);
        String idPost=YouChatApplication.correo+""+fechaEntera;
        int tipoUsu = 0;
        int cantSeguidores = dbWorker.obtenerCantSeguidores();
        if(YouChatApplication.comprobarOficialidad(YouChatApplication.correo)) tipoUsu=5;
        else if(YouChatApplication.es_beta_tester) tipoUsu=4;
        else if(cantSeguidores>=YouChatApplication.usuMayor) tipoUsu=3;
        else if(cantSeguidores>=YouChatApplication.usuMedio) tipoUsu=2;
        else if(cantSeguidores>=YouChatApplication.usuMenor) tipoUsu=1;

        ItemChat newPostChat = new ItemChat("",tipoUsu+"<s,p>"+icono);
        newPostChat.setRuta_Dato(temaCompartir.temaToMensaje());
        newPostChat.setTipo_mensaje(3);

        newPostChat.setMensaje(YouChatApplication.alias+"<s,p>"+texto);
        newPostChat.setId(idPost);

        if(YouChatApplication.estaAndandoChatService())
            YouChatApplication.chatService.enviarMensaje(newPostChat,SendMsg.CATEGORY_POST2);
        Utils.ShowToastAnimated(mainActivity,"Enviando Post",R.raw.contact_check);
    }

    public static void enviarPostPruebaTarjeta(int icono, String texto, ItemDetallesTarjeta detallesTarjeta) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
        Date date = new Date();
        String fechaEntera = sdf.format(date);
        String idPost=YouChatApplication.correo+""+fechaEntera;
        int tipoUsu = 0;
        int cantSeguidores = dbWorker.obtenerCantSeguidores();
        if(YouChatApplication.comprobarOficialidad(YouChatApplication.correo)) tipoUsu=5;
        else if(YouChatApplication.es_beta_tester) tipoUsu=4;
        else if(cantSeguidores>=YouChatApplication.usuMayor) tipoUsu=3;
        else if(cantSeguidores>=YouChatApplication.usuMedio) tipoUsu=2;
        else if(cantSeguidores>=YouChatApplication.usuMenor) tipoUsu=1;

        ItemChat newPostChat = new ItemChat("",tipoUsu+"<s,p>"+icono);
        newPostChat.setRuta_Dato(detallesTarjeta.detallesTarjetaToMensaje());
        newPostChat.setTipo_mensaje(4);

        newPostChat.setMensaje(YouChatApplication.alias+"<s,p>"+texto+"<s,p>");
        newPostChat.setId(idPost);

        if(YouChatApplication.estaAndandoChatService())
            YouChatApplication.chatService.enviarMensaje(newPostChat,SendMsg.CATEGORY_POST2);
        Utils.ShowToastAnimated(mainActivity,"Enviando Post",R.raw.contact_check);
    }

    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

        @Override
        public void onChildDraw(Canvas c,
                                RecyclerView recyclerView,
                                RecyclerView.ViewHolder viewHolder,
                                float dX, float dY,
                                int actionState, boolean isCurrentlyActive) {

            if (actionState == ACTION_STATE_SWIPE) {
                if(elPuedeSwipear(viewHolder.getAbsoluteAdapterPosition())){
//                dX_Global=dX;
//                setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
            int position = viewHolder.getAbsoluteAdapterPosition();
            if(position!=-1){
                if(datos_post.get(position).getTipo_post()!=-1 && datos_post.get(position).getTipo_post()!=-2){
                    if(YouChatApplication.activePostHistorial)
                        dbWorker.ocultarPost(datos_post.get(position).getId());
                    else dbWorker.eliminarPost(datos_post.get(position).getId());

                    datos_post.remove(position);
                    adaptadorDatosPost.notifyItemRemoved(position);
                    if(datos_post.size()==0){
                        anim.setAnimation(R.raw.new_empty_file);
                        anim.playAnimation();
                        anim.setVisibility(View.VISIBLE);
                        list_empty.setVisibility(View.VISIBLE);
                    }
                    else {
                        anim.setAnimation((Animation) null);
                        anim.cancelAnimation();
                        anim.setVisibility(View.GONE);
                        list_empty.setVisibility(View.GONE);
                        if(position==0)
                            YouChatApplication.setIdPosUltPostVisto(datos_post.get(0).getId());
                        if(hayDivisorNuevos){
                            for(int i=0; i<datos_post.size(); i++){
                                if(datos_post.get(i).getTipo_post()==-2){
                                    datos_post.remove(i);
                                    adaptadorDatosPost.notifyItemRemoved(i);
                                }
                            }
                            hayDivisorNuevos = false;
                        }
                        if(datos_post.size()<10 && datos_post.size()>0
                                && datos_post.get(datos_post.size()-1).getTipo_post()==-1){
                            position = datos_post.size()-1;
                            datos_post.remove(position);
                            adaptadorDatosPost.notifyItemRemoved(position);
                        }
                    }
                }
            }

//            if(datos_notificacion.size()==0){
//                noti_vacia.setVisibility(View.VISIBLE);
//            }
//            else noti_vacia.setVisibility(View.GONE);
        }
    };

    public void scrollToUp() {
        if(lista_post!=null){
            if(datos_post.size()<=20) lista_post.smoothScrollToPosition(0);
            else lista_post.scrollToPosition(0);
        }
    }

    public boolean elPuedeSwipear(int pos){
        ItemPost post = null;
        if(pos!=-1) post = datos_post.get(pos);
        if(post!=null){
            if(post.getTipo_post()==-1 || post.getTipo_post()==-2) return false;
            else return true;
        }
        return false;
    }

    public void sacarBottomSheetOpciones(int cantLimite) {
        BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_opciones_post.newInstance(this,cantLimite);
        bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyuda");
    }

    public void eliminarTodosPost() {
        if(datos_post.size()>0){
            lista_post.scrollToPosition(0);
            while (datos_post.size()>0){
                if(datos_post.get(0).getTipo_post()!=-1
                        && datos_post.get(0).getTipo_post()!=-2){
                    if(YouChatApplication.activePostHistorial)
                        dbWorker.ocultarPost(datos_post.get(0).getId());
                    else dbWorker.eliminarPost(datos_post.get(0).getId());
                }
                datos_post.remove(0);
                adaptadorDatosPost.notifyItemRemoved(0);
            }
            anim.setAnimation(R.raw.new_empty_file);
            anim.playAnimation();
            anim.setVisibility(View.VISIBLE);
            list_empty.setVisibility(View.VISIBLE);
        }
    }

    public void abrirVisorImagenEn(String ruta_dato) {
        if(principalActivity!=null) principalActivity.abrirPreview(ruta_dato);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    ///para la descarga de imagenes en el post
    public void descargarImagenPost(ItemDesImgPost itemDesImgPost){
        YouChatApplication.itemDesImgPosts.add(itemDesImgPost);
        context.startService(new Intent(context,IntentServiceDesImgPostGlobales.class));
    }

    public synchronized void actualizarPostDescargado(String id, String rutaDato){
        int pos = buscarIDPost(id);
        if(pos!=-1){
            datos_post.get(pos).setRuta_dato(rutaDato);
            adaptadorDatosPost.notifyItemChanged(pos,7);
        }
    }

    public synchronized void descargaImgFallida(String id, boolean correoNoEncontrado){
        if(!correoNoEncontrado) Utils.ShowToastAnimated(mainActivity,"Error al intentar descargar",R.raw.error);
        else Utils.ShowToastAnimated(mainActivity,"Mensaje no encontrado",R.raw.error);
        int pos = buscarIDPost(id);
        if(pos!=-1){
            adaptadorDatosPost.notifyItemChanged(pos,1);
        }
    }

    private void compartirPostPor(ItemPost post) {
        if(post.esTipoImagen()){
            File file = new File(post.getRuta_dato());
            if(file.exists()){
                Uri uri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    uri = FileProvider.getUriForFile(context,
                            "cu.alexgi.youchat.fileprovider",file);
                else uri = Uri.fromFile(file);

                Intent mShareIntent = new Intent();
                mShareIntent.setAction(Intent.ACTION_SEND);
                mShareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mShareIntent.setType("image/*");
                if(!post.getTexto().isEmpty())
                    mShareIntent.putExtra(Intent.EXTRA_TEXT, post.getTexto());
                mShareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                context.startActivity(mShareIntent);
            }else {
                Intent mShareIntent = new Intent();
                mShareIntent.setAction(Intent.ACTION_SEND);
                mShareIntent.setType("text/plain");
                mShareIntent.putExtra(Intent.EXTRA_TEXT, post.getTexto());
                context.startActivity(Intent.createChooser(mShareIntent,"Compartir por:"));
            }
        }
        else {
            Intent mShareIntent = new Intent();
            mShareIntent.setAction(Intent.ACTION_SEND);
            mShareIntent.setType("text/plain");
            mShareIntent.putExtra(Intent.EXTRA_TEXT, post.getTexto());
            context.startActivity(Intent.createChooser(mShareIntent,"Compartir por:"));
        }
    }

    private void enviarComentarioPost(ItemPost post, String mensaje){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
        Date date = new Date();
        String fechaEntera = sdf.format(date);
        String idPost=YouChatApplication.correo+""+fechaEntera;
        int tipoUsu = 0;
        int cantSeguidores = dbWorker.obtenerCantSeguidores();
        if(YouChatApplication.comprobarOficialidad(YouChatApplication.correo)) tipoUsu=5;
        else if(YouChatApplication.es_beta_tester) tipoUsu=4;
        else if(cantSeguidores>=YouChatApplication.usuMayor) tipoUsu=3;
        else if(cantSeguidores>=YouChatApplication.usuMedio) tipoUsu=2;
        else if(cantSeguidores>=YouChatApplication.usuMenor) tipoUsu=1;

        ItemChat newPostChat = new ItemChat("","");
        newPostChat.setId(idPost);
        newPostChat.setTipo_mensaje(1);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("idPost",post.getId());
            jsonObject.put("tipoUsu",tipoUsu);
            jsonObject.put("alias",YouChatApplication.alias);
            jsonObject.put("mensaje",mensaje);
            newPostChat.setMensaje(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        finally {
            YouChatApplication.chatService.enviarMensaje(newPostChat,SendMsg.CATEGORY_COMENTARIO_POST);
            Utils.ShowToastAnimated(mainActivity,"Enviando comentario",R.raw.contact_check);
        }
    }
}