package cu.alexgi.youchat;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiTextView;

import java.io.File;
import java.util.ArrayList;

import cu.alexgi.youchat.adapters.AdaptadorDatosUsuario;
import cu.alexgi.youchat.items.ItemContacto;
import cu.alexgi.youchat.items.ItemUsuario;
import cu.alexgi.youchat.swipebackfragment.BaseSwipeBackFragment;
import cu.alexgi.youchat.views_GI.CardViewDialogGI;

import static cu.alexgi.youchat.MainActivity.context;
import static cu.alexgi.youchat.MainActivity.dbWorker;
import static cu.alexgi.youchat.MainActivity.mainActivity;
import static cu.alexgi.youchat.YouChatApplication.principalActivity;


public class PrincipalFragment extends BaseSwipeBackFragment {
    private OnFragmentInteractionListener mListener;

    private View frag;
    private String activo="inactivo";
    private View img_empty_chat,text_vacio;
    public int cantNoVisto;

    //recycler usuario
    public RecyclerView lista_usuario;
    private LinearLayoutManager linearLayoutManagerChat;
    public ArrayList<ItemUsuario> datos_Usuario;
    public AdaptadorDatosUsuario adaptadorUsuario;
    private ArrayList<String> correosAActualizar;
    private ArrayList<Integer> cantAActualizar;

    public boolean modoSeleccionar, modoBuscar;
    private boolean estaActualizando;
    private boolean esVisible_Info, esVisible_Borrar;

    //modo seleccionar
    private String[] ids_seleccionados;
    public boolean[] pos_seleccionadas;
    public int cant_seleccionados;
    private int cantNoAnclados;
    private boolean necesitaActualizar;

    public boolean esParaAnclar;

    public PrincipalFragment() {
        // Required empty public constructor
    }

    public static PrincipalFragment newInstance(String param1, String param2) {
        PrincipalFragment fragment = new PrincipalFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activo="activo";
        cantNoVisto=0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        frag=inflater.inflate(R.layout.fragment_principal, container, false);

        frag.findViewById(R.id.root).setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_fondo()));

        correosAActualizar = new ArrayList<>();
        cantAActualizar = new ArrayList<>();
        activo="activo";
        lista_usuario = frag.findViewById(R.id.lista_usuario);
        img_empty_chat = frag.findViewById(R.id.img_empty_chat);
        text_vacio = frag.findViewById(R.id.text_vacio);

//        swipe = frag.findViewById(R.id.swipe);
//        swipe.setColorSchemeResources(R.color.card1, R.color.card3, R.color.card5, R.color.card7);


        modoSeleccionar=false;
        modoBuscar=false;

        datos_Usuario = new ArrayList<>();
        adaptadorUsuario = new AdaptadorDatosUsuario(context, datos_Usuario, YouChatApplication.principalActivity);
        linearLayoutManagerChat = new LinearLayoutManager(context, RecyclerView.VERTICAL,false);
        lista_usuario.setLayoutManager(linearLayoutManagerChat);
        lista_usuario.setAdapter(adaptadorUsuario);
        lista_usuario.setHasFixedSize(true);


        /*lista_usuario.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                accionesListenerListaUsuario(dy);
            }
        });*/

        actualizarRecyclerUsuarios();

        estaActualizando=false;
        necesitaActualizar=false;
        ids_seleccionados = new String[0];
        pos_seleccionadas = new boolean[0];
        cant_seleccionados = 0;
        cantNoAnclados=0;

        return frag;
    }

    /*private synchronized void accionesListenerListaUsuario(int dy) {
        if(dy<0) PrincipalActivity.fab(true);
        else if(dy>0) PrincipalActivity.fab(false);
    }*/

    public synchronized void actualizarRecyclerUsuarios(){
        if(!modoSeleccionar){
            if(estaActualizando) return;
            estaActualizando=true;
            necesitaActualizar=false;

            ArrayList<ItemUsuario> usuarios = dbWorker.obtenerUsuarios();
//            ArrayList<ItemUsuario> usuarios = dbWorker.obtenerUsuariosOrdenadosPorAnclados();

            reordenarAnclados(usuarios);
            datos_Usuario.clear();
            datos_Usuario.addAll(usuarios);
            adaptadorUsuario.notifyDataSetChanged();

            AsignarEventosAlAdaptador();

            if(datos_Usuario.size()==0) {
                img_empty_chat.setVisibility(View.VISIBLE);
                text_vacio.setVisibility(View.VISIBLE);
                lista_usuario.setVisibility(View.GONE);
            } else if(img_empty_chat.getVisibility()==View.VISIBLE){
                img_empty_chat.setVisibility(View.GONE);
                text_vacio.setVisibility(View.GONE);
                lista_usuario.setVisibility(View.VISIBLE);
            }
            estaActualizando=false;
        }
        else necesitaActualizar=true;
    }

    private void reordenarAnclados(ArrayList<ItemUsuario> usuarios){
        int l=usuarios.size();
        cantNoVisto=0;
        int cont=0;
        for(int i=0; i<l; i++){
            if(usuarios.get(i).getCant_mensajes()>0) cantNoVisto++;
            if(usuarios.get(i).EsAnclado()){
                ItemUsuario temp = usuarios.get(i);
                usuarios.remove(i);
                usuarios.add(cont,temp);
                cont++;
            }
        }
        PrincipalActivity.badgeInTabs(cantNoVisto,1);
        //cantidadMsgNoVisto(cantNoVisto);
//        adaptadorUsuario.notifyDataSetChanged();
    }

    public synchronized void actualizarBadge(){
        Utils.runOnUIThread(()->{
            cantNoVisto=0;
            for(int i=0; i<datos_Usuario.size(); i++){
                if(datos_Usuario.get(i).getCant_mensajes()>0) cantNoVisto++;
            }
            PrincipalActivity.badgeInTabs(cantNoVisto,1);
        });
    }

    public synchronized void borrarItemSeleccionados(){
        int l=pos_seleccionadas.length;
        int cant=0;
        for(int i=l-1; i>=0; i--){
            if(pos_seleccionadas[i]){
                cant++;
                datos_Usuario.remove(i);
                adaptadorUsuario.notifyItemRemoved(i);
                dbWorker.eliminarUsuario(ids_seleccionados[i], true, true);
            }
        }
        if(datos_Usuario.size()==0)
        {
            img_empty_chat.setVisibility(View.VISIBLE);
            text_vacio.setVisibility(View.VISIBLE);
            lista_usuario.setVisibility(View.GONE);
        } else if(img_empty_chat.getVisibility()==View.VISIBLE){
            img_empty_chat.setVisibility(View.GONE);
            text_vacio.setVisibility(View.GONE);
            lista_usuario.setVisibility(View.VISIBLE);
        }
        cancelarModoSeleccionar(false, false);
        if(cant==1) Utils.ShowToastAnimated(mainActivity,"Chat eliminado",R.raw.ic_delete);
        else Utils.ShowToastAnimated(mainActivity,cant+" chats eliminados",R.raw.ic_delete);
    }

    /*private synchronized void cantidadMsgNoVisto(int cant){ PrincipalActivity.badgeInTabs(cant,1); }*/

    @Override
    public void onResume() {
        super.onResume();
        Log.e("YOUCHAT PRINCIPAL FRAG","ON RESUME");
//        YouChatApplication.principalActivity.ActualizarUsuarios();
//        actualizarRecyclerUsuarios();
    }

    public synchronized void AsignarEventosAlAdaptador(){

        adaptadorUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modoSeleccionar){
                    int pos_item_seleccionado = lista_usuario.getChildAdapterPosition(v);
                    if(pos_seleccionadas[pos_item_seleccionado]){
                        ids_seleccionados[pos_item_seleccionado] = "";
                        pos_seleccionadas[pos_item_seleccionado] = false;
                        cant_seleccionados--;
                        boolean estaAnclado = datos_Usuario.get(pos_item_seleccionado).EsAnclado();
                        if(!estaAnclado) cantNoAnclados--;
                        estaAnclado = cantNoAnclados != 0;
                        setCantMsgSeleccionados(cant_seleccionados,estaAnclado);

                        //int pos=lista_usuario.getChildAdapterPosition(v);
                        datos_Usuario.get(pos_item_seleccionado).setEstaSeleccionado(false);

                        AdaptadorDatosUsuario.ViewHolderDatos viewItemSeleccionado
                                = (AdaptadorDatosUsuario.ViewHolderDatos) lista_usuario.getChildViewHolder(v);
                        if(viewItemSeleccionado!=null) viewItemSeleccionado.seleccionar(false);
                    }
                    else {
                        ids_seleccionados[pos_item_seleccionado] = datos_Usuario.get(pos_item_seleccionado).getCorreo();
                        pos_seleccionadas[pos_item_seleccionado] = true;
                        cant_seleccionados++;
                        boolean estaAnclado = datos_Usuario.get(pos_item_seleccionado).EsAnclado();
                        if(!estaAnclado) cantNoAnclados++;
                        estaAnclado = cantNoAnclados != 0;
                       setCantMsgSeleccionados(cant_seleccionados,estaAnclado);

                        //int pos=lista_usuario.getChildAdapterPosition(v);
                        datos_Usuario.get(pos_item_seleccionado).setEstaSeleccionado(true);

                        AdaptadorDatosUsuario.ViewHolderDatos viewItemSeleccionado
                                = (AdaptadorDatosUsuario.ViewHolderDatos) lista_usuario.getChildViewHolder(v);
                        if(viewItemSeleccionado!=null) viewItemSeleccionado.seleccionar(true);
                    }
                }
                else{
                    v.setEnabled(false);
                    int posAd = lista_usuario.getChildAdapterPosition(v);
                    if(posAd>=0){
                        principalActivity.desactivarLupa();
                        String cor=datos_Usuario.get(posAd).getCorreo();
                        String usu=dbWorker.obtenerNombre(cor);
                        goToChat(usu,cor);
                    }
                    v.setEnabled(true);
                }

            }
        });
        adaptadorUsuario.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if(modoSeleccionar){
                    int pos_item_seleccionado = lista_usuario.getChildAdapterPosition(v);
                    if(pos_seleccionadas[pos_item_seleccionado] && pos_item_seleccionado>=0){
                        ids_seleccionados[pos_item_seleccionado] = "";
                        pos_seleccionadas[pos_item_seleccionado] = false;
                        cant_seleccionados--;
                        boolean estaAnclado = datos_Usuario.get(pos_item_seleccionado).EsAnclado();
                        if(!estaAnclado) cantNoAnclados--;
                        estaAnclado = cantNoAnclados != 0;
                        setCantMsgSeleccionados(cant_seleccionados,estaAnclado);

                        int pos=lista_usuario.getChildAdapterPosition(v);
                        datos_Usuario.get(pos_item_seleccionado).setEstaSeleccionado(false);

                        AdaptadorDatosUsuario.ViewHolderDatos viewItemSeleccionado
                                = (AdaptadorDatosUsuario.ViewHolderDatos) lista_usuario.getChildViewHolder(v);
                        if(viewItemSeleccionado!=null) viewItemSeleccionado.seleccionar(false);
                    }
                    else if(pos_item_seleccionado>=0){
                        ids_seleccionados[pos_item_seleccionado] = datos_Usuario.get(pos_item_seleccionado).getCorreo();
                        pos_seleccionadas[pos_item_seleccionado] = true;
                        cant_seleccionados++;

                        boolean estaAnclado = datos_Usuario.get(pos_item_seleccionado).EsAnclado();
                        if(!estaAnclado) cantNoAnclados++;
                        estaAnclado = cantNoAnclados != 0;
                        setCantMsgSeleccionados(cant_seleccionados,estaAnclado);
                        int pos=lista_usuario.getChildAdapterPosition(v);
                        datos_Usuario.get(pos_item_seleccionado).setEstaSeleccionado(true);

                        AdaptadorDatosUsuario.ViewHolderDatos viewItemSeleccionado
                                = (AdaptadorDatosUsuario.ViewHolderDatos) lista_usuario.getChildViewHolder(v);
                        if(viewItemSeleccionado!=null) viewItemSeleccionado.seleccionar(true);
                    }
                }
                else {
                    modoSeleccionar=true;
                    //YouChatApplication.principalActivity.modoSeleccionar=true;
                    activarModoSeleccionar();
                    int l = datos_Usuario.size();
                    ids_seleccionados = new String[l];
                    pos_seleccionadas = new boolean[l];
                    cantNoAnclados = 0;
                    for (int i=0; i<l; i++) pos_seleccionadas[i]=false;

                    int pos_item_seleccionado = lista_usuario.getChildAdapterPosition(v);
                    if(pos_item_seleccionado<0) pos_item_seleccionado=0;
                    ids_seleccionados[pos_item_seleccionado] = datos_Usuario.get(pos_item_seleccionado).getCorreo();
                    pos_seleccionadas[pos_item_seleccionado] = true;
                    cant_seleccionados = 1;
                    boolean estaAnclado = datos_Usuario.get(pos_item_seleccionado).EsAnclado();
                    if(!estaAnclado) cantNoAnclados++;
                    estaAnclado = cantNoAnclados != 0;
                    setCantMsgSeleccionados(cant_seleccionados,estaAnclado);

                    int pos=lista_usuario.getChildAdapterPosition(v);
                    datos_Usuario.get(pos_item_seleccionado).setEstaSeleccionado(true);

                    AdaptadorDatosUsuario.ViewHolderDatos viewItemSeleccionado
                            = (AdaptadorDatosUsuario.ViewHolderDatos) lista_usuario.getChildViewHolder(v);
                    if(viewItemSeleccionado!=null) viewItemSeleccionado.seleccionar(true);
                }
                return true;
            }
        });
    }

    private void activarModoSeleccionar(){
        PrincipalActivity.ll_modo_seleccionar.setVisibility(View.VISIBLE);
        PrincipalActivity.ll_usuario.setVisibility(View.GONE);
    }

    public void setCantMsgSeleccionados(int cant, boolean epa){
        esParaAnclar=epa;
        if(esParaAnclar) PrincipalActivity.main_ms_anclar.setImageResource(R.drawable.anclar);
        else PrincipalActivity.main_ms_anclar.setImageResource(R.drawable.anclar_no);
        PrincipalActivity.main_ms_cant.setText(""+cant);
        //YouChatApplication.principalActivity.cant_seleccionados=cant;
        if(cant==0) cancelarModoSeleccionar(false, false);
    }

    public void cancelarSeleccionar(boolean necAct, boolean necActAll){
        int l=datos_Usuario.size();
        modoSeleccionar=false;
        if(necAct) {
            for (int i=0; i<l; i++){
                if(pos_seleccionadas[i]){
                    datos_Usuario.get(i).setEstaSeleccionado(false);
                    adaptadorUsuario.notifyItemChanged(i,7);
                    try{
                        AdaptadorDatosUsuario.ViewHolderDatos viewItemSeleccionado = (AdaptadorDatosUsuario.ViewHolderDatos) lista_usuario.findViewHolderForAdapterPosition(i);
                        if(viewItemSeleccionado!=null)
                            viewItemSeleccionado.seleccionar(false);
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
//        if(necActAll || necesitaActualizar) actualizarRecyclerUsuarios();
        ids_seleccionados=new String[0];
        pos_seleccionadas=new boolean[0];
        cant_seleccionados=0;
        cantNoAnclados=0;
        if(necActAll){
            correosAActualizar.clear();
            cantAActualizar.clear();
            actualizarRecyclerUsuarios();
//            actualizarBadge();
        }
        else actualizarNuevosMensajes();
    }

    private synchronized void actualizarNuevosMensajes() {
        if(correosAActualizar.size()>0){
            int longi = correosAActualizar.size();
            for(int i=0; i<longi; i++){
                actualizarNewMsgDe(correosAActualizar.get(i), cantAActualizar.get(i));
            }
            correosAActualizar.clear();
            cantAActualizar.clear();
        } else actualizarBadge();
    }

    public void cancelarModoSeleccionar(boolean necAct, boolean necActAll) {
        cancelarSeleccionar( necAct, necActAll);
        PrincipalActivity.ll_modo_seleccionar.setVisibility(View.GONE);
        PrincipalActivity.ll_usuario.setVisibility(View.VISIBLE);
    }

    public synchronized void actualizarEstadoUltMsgDe(String correoAct){
        int pos = buscarId(correoAct);
        if(pos!=-1){
//            datos_Usuario.get(pos).setUlt_msg_estado(ItemChat.ESTADO_RECIBIDO);
            adaptadorUsuario.notifyItemChanged(pos,7);
        }
    }

    public synchronized void actualizarNewMsgDe(String correoAct, int cant){
        if(modoSeleccionar || modoBuscar){
            addCorreoActualizar(correoAct,cant);
        }
        else {
            int pos = buscarId(correoAct);
            ItemUsuario usuario;
            if(pos!=-1){
                usuario = datos_Usuario.get(pos);
                datos_Usuario.remove(pos);
                adaptadorUsuario.notifyItemRemoved(pos);
            }
            else {
                usuario = dbWorker.obtenerUsuario(correoAct);
                if(usuario==null) usuario = new ItemUsuario(correoAct);
            }
            usuario.setCant_mensajes(usuario.getCant_mensajes()+cant);
            insertarUsuarioChat(usuario);
        }
    }

    private synchronized void addCorreoActualizar(String correoAct, int cant){
        for(int i=0; i<correosAActualizar.size(); i++){
            if(correosAActualizar.get(i).equals(correoAct)){
                cantAActualizar.set(i,cantAActualizar.get(i)+cant);
                return;
            }
        }
        correosAActualizar.add(correoAct);
        cantAActualizar.add(cant);
    }

    private synchronized void insertarUsuarioChat(ItemUsuario usuario) {
        if(datos_Usuario==null) datos_Usuario = new ArrayList<>();
        if(datos_Usuario.size()==0){
            if(img_empty_chat.getVisibility()==View.VISIBLE){
                img_empty_chat.setVisibility(View.GONE);
                text_vacio.setVisibility(View.GONE);
                lista_usuario.setVisibility(View.VISIBLE);
            }
            datos_Usuario.add(0,usuario);
            adaptadorUsuario.notifyItemInserted(0);
        }
        else if(usuario.EsAnclado()){
            datos_Usuario.add(0,usuario);
            adaptadorUsuario.notifyItemInserted(0);
        }
        else {
            for(int i=0; i<datos_Usuario.size(); i++){
                if(!datos_Usuario.get(i).EsAnclado()){
                    datos_Usuario.add(i,usuario);
                    adaptadorUsuario.notifyItemInserted(i);
                    break;
                }
            }
        }
        actualizarBadge();
    }

    private int buscarId(String cor){
        if(datos_Usuario==null) return -1;
        int l=datos_Usuario.size();
        for(int i=0; i<l; i++){
            if(datos_Usuario.get(i).getCorreo().equals(cor))
                return i;
        }
        return -1;
    }


    public void MostrarPreviewChat(ItemUsuario usuarioInfo){
        if(!modoSeleccionar){
            String cor = usuarioInfo.getCorreo();
            String usu=cor;
            ItemContacto contacto = dbWorker.obtenerContacto(cor);
            String rut="";
            final String inf;
            int cantSeg = 0;
            if(contacto!=null){
                usu=contacto.getNombreMostrar();
                rut=contacto.getRuta_img();
                inf=contacto.getInfo();
                cantSeg = contacto.getCant_seguidores();
            }
            else rut=inf="";
            if(cor.equals(YouChatApplication.correo))
                cantSeg = YouChatApplication.cant_seguidores;
            final boolean estaAnclado = usuarioInfo.EsAnclado();

            Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(1);
            View mview=getLayoutInflater().inflate(R.layout.dialog_option_image_preview,null);
            dialog.setContentView(mview);


            final EmojiEditText preview_info = mview.findViewById(R.id.preview_infotext);
            final CardViewDialogGI layout_eliminar = mview.findViewById(R.id.layout_eliminar);
            final View button_eliminar = mview.findViewById(R.id.button_eliminar);

            EmojiTextView preview_nombre = mview.findViewById(R.id.preview_nombre);

            final ImageView preview_image=mview.findViewById(R.id.preview_image);
            if(cor.equals(YouChatApplication.idOficial))
                YouChatApplication.ponerIconOficial(preview_image);
            else
                Glide.with(this).load(rut).error(R.drawable.profile_white).into(preview_image);

            preview_info.setText(inf);
            preview_nombre.setText(usu);
            esVisible_Info=false;
            esVisible_Borrar=false;

            ImageView btn_go_chat=mview.findViewById(R.id.preview_chat);
            ImageView preview_perfil=mview.findViewById(R.id.preview_perfil);
            ImageView btn_info=mview.findViewById(R.id.preview_info);
            ImageView preview_anclar=mview.findViewById(R.id.preview_anclar);
            ImageView btn_delete=mview.findViewById(R.id.preview_delete);
            if(estaAnclado) preview_anclar.setImageResource(R.drawable.anclar_no);
            else preview_anclar.setImageResource(R.drawable.anclar);

            TextView cant_seguidores_preview = mview.findViewById(R.id.cant_seguidores_preview);
            cant_seguidores_preview.setText(""+cantSeg);

            final String finalRut = rut;
            final String finalUsu = usu;
            preview_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!finalRut.equals("")){
                        File file =  new File(finalRut);
                        if(!file.exists()){
                            Utils.ShowToastAnimated(mainActivity,"No existe la imagen", R.raw.error);
                        }
                        else{
                            dialog.dismiss();
                            if(principalActivity!=null)
                                principalActivity.abrirPreview(finalRut);
//                            previewImage(finalRut);
                        }
                    }
                }
            });

            btn_go_chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    goToChat(finalUsu,cor);
                }
            });
            preview_perfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle mibundle=new Bundle();
                    mibundle.putString("usuario",finalUsu);
                    mibundle.putString("correo",cor);
                    mibundle.putBoolean("vieneDeChat", false);
                    if(mAddFragmentListener!=null) mAddFragmentListener.onAddFragment(principalActivity, ViewPerfilActivity.newInstance(mibundle));
//                        YouChatApplication.principalActivity.navController.navigate(R.id.viewPerfilActivity,mibundle,YouChatApplication.TransIzqaDer());
                    dialog.dismiss();
                }
            });
            btn_info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(esVisible_Info) {
                        preview_image.setClickable(true);
                        Animation anim= AnimationUtils.loadAnimation(context,R.anim.hide_layout_answer);
                        preview_info.startAnimation(anim);
                        preview_info.setVisibility(View.GONE);
                    }
                    else {
                        preview_image.setClickable(false);
                        Animation anim=AnimationUtils.loadAnimation(context,R.anim.show_layout_answer);
                        preview_info.setVisibility(View.VISIBLE);
                        preview_info.startAnimation(anim);
                    }
                    esVisible_Info=!esVisible_Info;
                }
            });
            preview_anclar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(estaAnclado){
                        dbWorker.modificarUsuarioAnclado(cor,0);
                        actualizarRecyclerUsuarios();
                        Utils.ShowToastAnimated(mainActivity,"Chat desanclado",R.raw.ic_unpin);
                    }
                    else {
                        dbWorker.modificarUsuarioAnclado(cor,1);
                        actualizarRecyclerUsuarios();
                        Utils.ShowToastAnimated(mainActivity,"Chat anclado",R.raw.ic_pin);
                    }
                    //if(act_cancel_buscar) cancelar_buscar();
                    dialog.dismiss();
                }
            });
            button_eliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    BorrarUsuario(cor);
                    //if(PrincipalActivity.act_cancel_buscar) PrincipalActivity.cancelar_buscar();
                }
            });
            btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(esVisible_Borrar) layout_eliminar.setVisibility(View.GONE);
                    else layout_eliminar.setVisibility(View.VISIBLE);
                    esVisible_Borrar=!esVisible_Borrar;
                }
            });

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.setCancelable(true);
            dialog.show();

        }
    }

    private void previewImage(String ruta_img) {
//        File file =  new File(ruta_img);
//        if(!file.exists()){
//            Utils.ShowToastAnimated(mainActivity,"No existe la imagen", R.raw.error);
//            return;
//        }

//        ArrayList<AlbumFile> imagenesChatAlbumFile=new ArrayList<>();
//        AlbumFile albumFile = new AlbumFile();
//        albumFile.setPath(ruta_img);
//        imagenesChatAlbumFile.add(albumFile);
//
//        Album.galleryAlbum(this)
//                .checkable(false)
//                .checkedList(imagenesChatAlbumFile)
//                .currentPosition(0)
//                .widget(
//                        Widget.newDarkBuilder(context)
//                                .title("Imagen de perfil")
//                                .build()
//                )
//                .onResult(new Action<ArrayList<AlbumFile>>() {
//                    @Override
//                    public void onAction(@NonNull ArrayList<AlbumFile> result) {
//                    }
//                })
//                .start();
    }

    public void goToChat(String usu,String cor){
        Bundle mibundle=new Bundle();
        mibundle.putString("usuario",usu);
        mibundle.putString("correo",cor);
        if(mAddFragmentListener!=null)
            mAddFragmentListener.onAddFragment(principalActivity, ChatsActivity.newInstance(mibundle));
//        YouChatApplication.principalActivity.navController.navigate(R.id.chatsActivity,mibundle,YouChatApplication.TransIzqaDer());
    }

    public void BorrarUsuario(String cor){

        int pos = buscarId(cor);
        if(pos!=-1){
            datos_Usuario.remove(pos);
            adaptadorUsuario.notifyItemRemoved(pos);
        }
        dbWorker.eliminarUsuario( cor, true, true);
        if(datos_Usuario.size()==0)
        {
            img_empty_chat.setVisibility(View.VISIBLE);
            text_vacio.setVisibility(View.VISIBLE);
            lista_usuario.setVisibility(View.GONE);
        }
        else if(img_empty_chat.getVisibility()==View.VISIBLE){
            img_empty_chat.setVisibility(View.GONE);
            text_vacio.setVisibility(View.GONE);
            lista_usuario.setVisibility(View.VISIBLE);
        }
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

    public void scrollPosNoLeido() {
        int l = datos_Usuario.size();
        int pos = -1;
        for (int i=0; i<l; i++){
            if(datos_Usuario.get(i).getCant_mensajes()>0){
                pos=i;
                break;
            }
        }
        if(pos!=-1){
            lista_usuario.scrollToPosition(pos);
        }
    }

    public void borrarCantMsgDe(String correo) {
        int pos = buscarId(correo);
        if(pos!=-1){
            if(datos_Usuario.get(pos).getCant_mensajes()>0){
                datos_Usuario.get(pos).setCant_mensajes(0);
                adaptadorUsuario.notifyItemChanged(pos,7);
            }
        }
    }

    public void activarModoBuscar() {
        modoBuscar = true;
    }

    public void desactivarModoBuscar() {
        modoBuscar = false;
        AsignarEventosAlAdaptador();
        actualizarNuevosMensajes();
    }

    public synchronized void borrarUsuario(String correo) {
        for(int i=0; i<datos_Usuario.size(); i++){
            if(datos_Usuario.get(i).getCorreo().equals(correo)){
                datos_Usuario.remove(i);
                adaptadorUsuario.notifyItemRemoved(i);
                break;
            }
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
