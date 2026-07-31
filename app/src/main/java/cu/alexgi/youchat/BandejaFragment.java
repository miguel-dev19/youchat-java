package cu.alexgi.youchat;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigationrail.NavigationRailView;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import cu.alexgi.youchat.adapters.AdaptadorDatosMensajeCorreo;
import cu.alexgi.youchat.adapters.AdaptadorDatosUsuarioCorreo;
import cu.alexgi.youchat.items.ItemMensajeCorreo;
import cu.alexgi.youchat.items.ItemUsuarioCorreo;
import cu.alexgi.youchat.progressbar.DownloadProgressView;
import cu.alexgi.youchat.swipebackfragment.BaseSwipeBackFragment;
import cu.alexgi.youchat.views_GI.FABGI;
import cu.alexgi.youchat.views_GI.TextViewFontGenGI;

import static cu.alexgi.youchat.MainActivity.context;
import static cu.alexgi.youchat.MainActivity.dbWorker;
import static cu.alexgi.youchat.MainActivity.mainActivity;

public class BandejaFragment extends BaseSwipeBackFragment {

    private BandejaFragment bandejaFragment;
    private NavigationRailView navigation_rail;
    private RecyclerView lista_emails;
    private View rl_contenedor_nav_rail, abrir_nav_rail, ll_modo_seleccionar,
            rl_barra_contacto, cancelarModoSeleccionarBtn, eliminarItemModoSeleccionar, selTodoItemModoSeleccionar;

    private AdaptadorDatosUsuarioCorreo adaptadorDatosUsuarioCorreo;
    private ArrayList<ItemUsuarioCorreo> datos_usuario;

    private AdaptadorDatosMensajeCorreo adaptadorDatosMensajeCorreo;
    private ArrayList<ItemMensajeCorreo> datos_mensaje;

    private LinearProgressIndicator linearProgressBar;

    private FABGI fab_nuevo_correo;

    private Dialog dialogoInbox;
    private TextView list_empty,cant_nuevos_correos, titulo;
    private int anchoNavigationRail;
//    private ArrayList<> datos_emails;

    private TextView main_ms_cant;
    private int cantElemtSel;

    public static BandejaFragment newInstance() {
        BandejaFragment fragment = new BandejaFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        YouChatApplication.bandejaFragment = this;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        YouChatApplication.bandejaFragment = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bandeja, container, false);
        return attachToSwipeBack(view);
//        return
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cantElemtSel = 0;
        bandejaFragment = this;
        rl_barra_contacto = view.findViewById(R.id.rl_barra_contacto);
        ll_modo_seleccionar = view.findViewById(R.id.ll_modo_seleccionar);
        cant_nuevos_correos = view.findViewById(R.id.cant_nuevos_correos);
        list_empty = view.findViewById(R.id.list_empty);
        navigation_rail = view.findViewById(R.id.navigation_rail);
        lista_emails = view.findViewById(R.id.lista_emails);
        rl_contenedor_nav_rail = view.findViewById(R.id.rl_contenedor_nav_rail);
        abrir_nav_rail = view.findViewById(R.id.abrir_nav_rail);
        linearProgressBar = view.findViewById(R.id.linearProgressBar);
        titulo = view.findViewById(R.id.titulo);
        fab_nuevo_correo = view.findViewById(R.id.fab_nuevo_correo);
        cancelarModoSeleccionarBtn = view.findViewById(R.id.cancelarModoSeleccionarBtn);
        main_ms_cant = view.findViewById(R.id.main_ms_cant);
        eliminarItemModoSeleccionar = view.findViewById(R.id.eliminarItemModoSeleccionar);
        selTodoItemModoSeleccionar = view.findViewById(R.id.selTodoItemModoSeleccionar);

        cancelarModoSeleccionarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                desactivarModoSeleccionar();
            }
        });

        eliminarItemModoSeleccionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                borrarElementosSeleccionados();
            }
        });

        selTodoItemModoSeleccionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarTodo();
            }
        });

        if(YouChatApplication.estaCorriendoBuzon)
            linearProgressBar.setVisibility(View.VISIBLE);
        else linearProgressBar.setVisibility(View.GONE);

        if (YouChatApplication.estaAndandoChatService()) {
            YouChatApplication.chatService.eliminarNotiCorreo();
        }

        fab_nuevo_correo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nuevaConversacion();
            }
        });

        navigation_rail.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_barra()));
        navigation_rail.setItemIconTintList(ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getFont_barra())));
        navigation_rail.setItemTextColor(ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getFont_barra())));
        navigation_rail.setItemRippleColor(ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getFont_barra_oscuro())));

        lista_emails.setLayoutManager(new LinearLayoutManager(context,RecyclerView.VERTICAL, false));
        view.findViewById(R.id.atras).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.atrasFragment();
            }
        });
        view.findViewById(R.id.root).setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_interior()));

        actualizarBadgeDeVaciar();

        switch (YouChatApplication.posVistaBandeja){
            case 0: navigation_rail.setSelectedItemId(R.id.menu_navrail_vista_favoritos); cargarMensajesFavoritos(); break;
            case 1: navigation_rail.setSelectedItemId(R.id.menu_navrail_vista_usuario); cargarUsuarios(); break;
            case 2: navigation_rail.setSelectedItemId(R.id.menu_navrail_vista_email_rec); cargarMensajesRecibidos(); break;
            case 3: navigation_rail.setSelectedItemId(R.id.menu_navrail_vista_email_env); cargarMensajesEnviados(); break;
            default:
                navigation_rail.setSelectedItemId(R.id.menu_navrail_vista_usuario);
                cargarUsuarios();
                YouChatApplication.setPosVistaBandeja(1);
                break;

        }

        navigation_rail.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_navrail_vista_favoritos:
                        if(YouChatApplication.posVistaBandeja!=0){
                            YouChatApplication.setPosVistaBandeja(0);
                            cargarMensajesFavoritos();
                        }
                        return true;
                    case R.id.menu_navrail_vista_usuario:
                        if(YouChatApplication.posVistaBandeja!=1){
                            YouChatApplication.setPosVistaBandeja(1);
                            cargarUsuarios();
                        }
                        return true;
                    case R.id.menu_navrail_vista_email_rec:
                        if(YouChatApplication.posVistaBandeja!=2){
                            YouChatApplication.setPosVistaBandeja(2);
                            cargarMensajesRecibidos();
                        }
                        return true;
                    case R.id.menu_navrail_vista_email_env:
                        if(YouChatApplication.posVistaBandeja!=3){
                            YouChatApplication.setPosVistaBandeja(3);
                            cargarMensajesEnviados();
                        }
                        return true;
//                    case R.id.menu_navrail_borrar:
//                        if(estaAbiertoNavRail()) cerrarNavRail();
//                        YouChatApplication.setIdUltCorreoBuzonRecibido("");
//                        dbWorker.eliminarTodoBuzon();
//                        Toast.makeText(context, "programar", Toast.LENGTH_SHORT).show();
//                        return false;
                    case R.id.menu_navrail_vaciar:
                        if(estaAbiertoNavRail()) cerrarNavRail();
                        mostrarDialogoVaciarBandeja();
                        return false;
                    case R.id.menu_navrail_ajustes:
                        if(estaAbiertoNavRail()) cerrarNavRail();
                        if(mAddFragmentListener!=null)
                            mAddFragmentListener.onAddFragment(BandejaFragment.this, new AjustesBuzonFragment());
                        return false;
                }
                return false;
            }
        });

        abrir_nav_rail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(estaAbiertoNavRail()) cerrarNavRail();
                else abrirNavRail();
            }
        });

//        navigation_rail.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override public void onGlobalLayout() {
//                navigation_rail.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                anchoNavigationRail = Utils.dpToPx(context,navigation_rail.getWidth());
//            }
//        });
    }

    private void nuevaConversacion() {
        if(mAddFragmentListener!=null)
            mAddFragmentListener
                    .onAddFragment(BandejaFragment.this,
                            ContactFragmentCorreo.newInstance(BandejaFragment.this));
    }

    private void abrirNavRail() {
        fab_nuevo_correo.hide();
        rl_contenedor_nav_rail.setVisibility(View.VISIBLE);
        navigation_rail.startAnimation(AnimationUtils.loadAnimation(context,R.anim.show_search));
        rl_contenedor_nav_rail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (estaAbiertoNavRail()) cerrarNavRail();
                return true;
            }
        });
    }

    private void cerrarNavRail() {
        rl_contenedor_nav_rail.setOnTouchListener(null);
        Animation anim = AnimationUtils.loadAnimation(context,R.anim.hide_search);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                rl_contenedor_nav_rail.setVisibility(View.GONE);
                fab_nuevo_correo.show();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        navigation_rail.startAnimation(anim);
    }

    private boolean estaAbiertoNavRail(){
        return rl_contenedor_nav_rail.getVisibility() == View.VISIBLE;
    }

    private synchronized void cargarMensajesFavoritos(){
        titulo.setText("Mensajes favoritos");
        adaptadorDatosUsuarioCorreo = null;
        datos_usuario = null;
        datos_mensaje = dbWorker.obtenerMensajeFavoritos();
        adaptadorDatosMensajeCorreo = new AdaptadorDatosMensajeCorreo(context,datos_mensaje);
        lista_emails.setAdapter(adaptadorDatosMensajeCorreo);
        adaptadorDatosMensajeCorreo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = lista_emails.getChildAdapterPosition(v);
                if(pos!=-1){
                    if(estaModoSeleccionar()){
                        seleccionarElementoEn(pos);
                    } else {
                        if(mAddFragmentListener!=null)
                            mAddFragmentListener.onAddFragment(BandejaFragment.this,
                                    VistaMensajeCorreoFragment.newInstance(datos_mensaje.get(pos)));
                    }
                }
            }
        });
        adaptadorDatosMensajeCorreo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int pos = lista_emails.getChildAdapterPosition(v);
                if(pos!=-1){
                    if(estaModoSeleccionar()){
                        seleccionarElementoEn(pos);
                    }
                    else {
                        activarModoSeleccionar();
                        seleccionarElementoEn(pos);
                    }
                }
                return false;
            }
        });
        int l = datos_mensaje.size();
        if(l>0){
            lista_emails.setVisibility(View.VISIBLE);
            lista_emails.startAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_in_fast));
            list_empty.setVisibility(View.GONE);
            if(l==1) cant_nuevos_correos.setText("1 mensaje favorito");
            else cant_nuevos_correos.setText(l+" mensajes favoritos");
        }
        else {
            lista_emails.setVisibility(View.GONE);
            list_empty.setVisibility(View.VISIBLE);
            cant_nuevos_correos.setText("0 mensajes favoritos");
        }
    }

    public synchronized void cargarUsuarios(){
        titulo.setText("Chats");
        adaptadorDatosMensajeCorreo = null;
        datos_mensaje = null;
        datos_usuario = dbWorker.obtenerUsuarioCorreos();
        adaptadorDatosUsuarioCorreo = new AdaptadorDatosUsuarioCorreo(context,datos_usuario);
        lista_emails.setAdapter(adaptadorDatosUsuarioCorreo);
        adaptadorDatosUsuarioCorreo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = lista_emails.getChildAdapterPosition(v);
                if(pos!=-1){
                    if(estaModoSeleccionar()){
                        seleccionarElementoEn(pos);
                    } else {
                        Bundle mibundle=new Bundle();
                        mibundle.putString("usuario",datos_usuario.get(pos).getNombre());
                        mibundle.putString("correo",datos_usuario.get(pos).getCorreo());
                        if(mAddFragmentListener!=null)
                            mAddFragmentListener.onAddFragment(BandejaFragment.this, ChatsActivityCorreo.newInstance(mibundle));

                    }
                }
            }
        });
        adaptadorDatosUsuarioCorreo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int pos = lista_emails.getChildAdapterPosition(v);
                if(pos!=-1){
                    if(estaModoSeleccionar()){
                        seleccionarElementoEn(pos);
                    }
                    else {
                        activarModoSeleccionar();
                        seleccionarElementoEn(pos);
                    }
                }
                return false;
            }
        });
        int l = datos_usuario.size();
        if(l>0){
            lista_emails.setVisibility(View.VISIBLE);
            lista_emails.startAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_in_fast));
            list_empty.setVisibility(View.GONE);
            if(l==1) cant_nuevos_correos.setText("1 usuario");
            else cant_nuevos_correos.setText(l+" usuarios");
        }
        else {
            lista_emails.setVisibility(View.GONE);
            list_empty.setVisibility(View.VISIBLE);
            cant_nuevos_correos.setText("0 usuarios");
        }
    }

    private synchronized void cargarMensajesRecibidos(){
        titulo.setText("Mensajes recibidos");
        adaptadorDatosUsuarioCorreo = null;
        datos_usuario = null;
        datos_mensaje = dbWorker.obtenerMensajeCorreo(false);
        adaptadorDatosMensajeCorreo = new AdaptadorDatosMensajeCorreo(context,datos_mensaje);
        lista_emails.setAdapter(adaptadorDatosMensajeCorreo);
        adaptadorDatosMensajeCorreo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = lista_emails.getChildAdapterPosition(v);
                if(pos!=-1){
                    if(estaModoSeleccionar()){
                        seleccionarElementoEn(pos);
                    } else {
                        if(mAddFragmentListener!=null)
                            mAddFragmentListener.onAddFragment(BandejaFragment.this,
                                    VistaMensajeCorreoFragment.newInstance(datos_mensaje.get(pos)));
                    }
                }
            }
        });
        adaptadorDatosMensajeCorreo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int pos = lista_emails.getChildAdapterPosition(v);
                if(pos!=-1){
                    if(estaModoSeleccionar()){
                        seleccionarElementoEn(pos);
                    }
                    else {
                        activarModoSeleccionar();
                        seleccionarElementoEn(pos);
                    }
                }
                return false;
            }
        });
        int l = datos_mensaje.size();
        if(l>0){
            lista_emails.setVisibility(View.VISIBLE);
            lista_emails.startAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_in_fast));
            list_empty.setVisibility(View.GONE);
            if(l==1) cant_nuevos_correos.setText("1 mensaje recibido");
            else cant_nuevos_correos.setText(l+" mensajes recibidos");
        }
        else {
            lista_emails.setVisibility(View.GONE);
            list_empty.setVisibility(View.VISIBLE);
            cant_nuevos_correos.setText("0 mensajes recibidos");
        }
    }

    private synchronized void cargarMensajesEnviados(){
        titulo.setText("Mensajes enviados");
        adaptadorDatosUsuarioCorreo = null;
        datos_usuario = null;
        datos_mensaje = dbWorker.obtenerMensajeCorreo(true);
        adaptadorDatosMensajeCorreo = new AdaptadorDatosMensajeCorreo(context,datos_mensaje);
        lista_emails.setAdapter(adaptadorDatosMensajeCorreo);
        adaptadorDatosMensajeCorreo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = lista_emails.getChildAdapterPosition(v);
                if(pos!=-1){
                    if(estaModoSeleccionar()){
                        seleccionarElementoEn(pos);
                    } else {
                        if(mAddFragmentListener!=null)
                            mAddFragmentListener.onAddFragment(BandejaFragment.this,
                                    VistaMensajeCorreoFragment.newInstance(datos_mensaje.get(pos)));
                    }
                }
            }
        });
        adaptadorDatosMensajeCorreo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int pos = lista_emails.getChildAdapterPosition(v);
                if(pos!=-1){
                    if(estaModoSeleccionar()){
                        seleccionarElementoEn(pos);
                    }
                    else {
                        activarModoSeleccionar();
                        seleccionarElementoEn(pos);
                    }
                }
                return false;
            }
        });
        int l = datos_mensaje.size();
        if(l>0){
            lista_emails.setVisibility(View.VISIBLE);
            lista_emails.startAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_in_fast));
            list_empty.setVisibility(View.GONE);
            if(l==1) cant_nuevos_correos.setText("1 mensaje enviado");
            else cant_nuevos_correos.setText(l+" mensajes enviados");
        }
        else {
            lista_emails.setVisibility(View.GONE);
            list_empty.setVisibility(View.VISIBLE);
            cant_nuevos_correos.setText("0 mensajes enviados");
        }
    }

    private void mostrarDialogoVaciarBandeja(){
        Dialog dialogo = new Dialog(context);
        dialogo.requestWindowFeature(1);
        View mview=getLayoutInflater().inflate(R.layout.dialog_confirm,null);
        dialogo.setContentView(mview);

        ImageView img=mview.findViewById(R.id.icono_eliminar);
        TextView text_icono=mview.findViewById(R.id.text_icono);
        TextView text_eliminar=mview.findViewById(R.id.text_eliminar);
        View btn_cancel=mview.findViewById(R.id.btn_cancel);
        View btn_ok=mview.findViewById(R.id.btn_ok);

        img.setImageResource(R.drawable.option_inbox);
        text_icono.setText("Vaciar bandeja");
        text_eliminar.setText("¿Desea eliminar todos los correos de su bandeja de entrada?");

        actualizarBadgeDeVaciar();

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(YouChatApplication.estaAndandoChatService()
                        && YouChatApplication.chatService.hayConex){
                    dialogo.dismiss();

                    dialogoInbox = new Dialog(context);
                    dialogoInbox.requestWindowFeature(1);
                    View mviewe=getLayoutInflater().inflate(R.layout.dialog_clear_inbox_progress,null);
                    dialogoInbox.setContentView(mviewe);

                    TextViewFontGenGI texto_cant = mviewe.findViewById(R.id.texto_cant);
                    DownloadProgressView progressbar_vaciar_inbox = mviewe.findViewById(R.id.progressbar_vaciar_inbox);

                        /*texto_alerta=mviewe.findViewById(R.id.texto_alerta);
                        texto_alerta.setText("Vaciando bandeja de entrada...");*/

                    dialogoInbox.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                    dialogoInbox.setCancelable(false);
                    dialogoInbox.show();

                    progressbar_vaciar_inbox.quitarClick();
                    progressbar_vaciar_inbox.setDownloading(true);
                    progressbar_vaciar_inbox.setProgress(0f);

                    YouChatApplication.chatService.VaciarInbox(bandejaFragment,
                            texto_cant, progressbar_vaciar_inbox);
                }
                else Utils.ShowToastAnimated(mainActivity,"Compruebe su conexión",R.raw.ic_ban);
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogo.dismiss();
            }
        });

        dialogo.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialogo.setCancelable(true);
        dialogo.show();
    }

    public synchronized void ocultarBarraProgress(){
        if(linearProgressBar!=null && linearProgressBar.getVisibility()==View.VISIBLE)
            linearProgressBar.setVisibility(View.GONE);
    }

    public synchronized void mostrarBarraProgress(){
        if(linearProgressBar!=null && linearProgressBar.getVisibility()==View.GONE)
            linearProgressBar.setVisibility(View.VISIBLE);
    }

    public void actualizarUsuario(String correo) {
        if(YouChatApplication.posVistaBandeja==1){
            int pos = -1;
            for(int i=0; i<datos_usuario.size(); i++){
                if(datos_usuario.get(i).getCorreo().equals(correo)){
                    pos = i;
                    adaptadorDatosUsuarioCorreo.notifyItemChanged(i, 7);
                    break;
                }
            }
            if(pos == -1){
                String orden = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault()).format(new Date());
                final String hora = Convertidor.conversionHora(orden);
                final String fecha = Convertidor.conversionFecha(orden);
                datos_usuario.add(0, new ItemUsuarioCorreo(correo,correo,"",hora,fecha,orden));
                adaptadorDatosUsuarioCorreo.notifyItemInserted(0);

                if(datos_usuario.size()==1) cant_nuevos_correos.setText("1 usuario");
                else cant_nuevos_correos.setText(datos_usuario.size()+" usuarios");

                if(list_empty.getVisibility() == View.VISIBLE){
                    lista_emails.setVisibility(View.VISIBLE);
                    lista_emails.startAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_in_fast));
                    list_empty.setVisibility(View.GONE);
                }
            }
        }
    }

    public void eliminarUsuario(String correo) {
        if(YouChatApplication.posVistaBandeja==1 && datos_usuario!=null){
            for(int i=0; i<datos_usuario.size(); i++){
                if(datos_usuario.get(i).getCorreo().equals(correo)){
                    datos_usuario.remove(i);
                    adaptadorDatosUsuarioCorreo.notifyItemRemoved(i);
                    break;
                }
            }
            if(datos_usuario.size()==1) cant_nuevos_correos.setText("1 usuario");
            else cant_nuevos_correos.setText(datos_usuario.size()+" usuarios");
            if(datos_usuario.size()==0){
                lista_emails.setVisibility(View.GONE);
                list_empty.setVisibility(View.VISIBLE);
            }
        }
    }

    public void eliminarMensaje(String id) {
        if(YouChatApplication.posVistaBandeja!=1 && datos_mensaje!=null){
            for(int i=0; i<datos_mensaje.size(); i++){
                if(datos_mensaje.get(i).getId().equals(id)){
                    datos_mensaje.remove(i);
                    adaptadorDatosMensajeCorreo.notifyItemRemoved(i);
                    break;
                }
            }
            if(YouChatApplication.posVistaBandeja==0){
                if(datos_mensaje.size()>0){
                    if(datos_mensaje.size()==1) cant_nuevos_correos.setText("1 mensaje favorito");
                    else cant_nuevos_correos.setText(datos_mensaje.size()+" mensajes favoritos");
                }
                else {
                    lista_emails.setVisibility(View.GONE);
                    list_empty.setVisibility(View.VISIBLE);
                    cant_nuevos_correos.setText("0 mensajes favoritos");
                }
            }
            else if(YouChatApplication.posVistaBandeja==2){
                if(datos_mensaje.size()>0){
                    if(datos_mensaje.size()==1) cant_nuevos_correos.setText("1 mensaje recibido");
                    else cant_nuevos_correos.setText(datos_mensaje.size()+" mensajes recibidos");
                }
                else{
                    lista_emails.setVisibility(View.GONE);
                    list_empty.setVisibility(View.VISIBLE);
                    cant_nuevos_correos.setText("0 mensajes recibidos");
                }
            }
            else if(YouChatApplication.posVistaBandeja==3){
                if(datos_mensaje.size()>0){
                    if(datos_mensaje.size()==1) cant_nuevos_correos.setText("1 mensaje enviado");
                    else cant_nuevos_correos.setText(datos_mensaje.size()+" mensajes enviados");
                }
                else {
                    lista_emails.setVisibility(View.GONE);
                    list_empty.setVisibility(View.VISIBLE);
                    cant_nuevos_correos.setText("0 mensajes enviados");
                }
            }
        }
    }

    public synchronized void addNewCorreo(ItemMensajeCorreo newCorreo, boolean esMio){
        if(YouChatApplication.posVistaBandeja==1){
            if(list_empty.getVisibility() == View.VISIBLE){
                lista_emails.setVisibility(View.VISIBLE);
                lista_emails.startAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_in_fast));
                list_empty.setVisibility(View.GONE);
            }
            if(datos_usuario.size()==0){
                datos_usuario.add(0, new ItemUsuarioCorreo(newCorreo));
                adaptadorDatosUsuarioCorreo.notifyItemInserted(0);
            }
            else {
                if(datos_usuario.get(0).getCorreo().equals(newCorreo.getCorreo())){
                    datos_usuario.get(0).modificar(newCorreo);
                    adaptadorDatosUsuarioCorreo.notifyItemChanged(0,7);
                }
                else {
                    int pos = -1;
                    for(int i=1; i<datos_usuario.size(); i++){
                        if(datos_usuario.get(i).getCorreo().equals(newCorreo.getCorreo())){
                            pos = i;
                            datos_usuario.get(i).modificar(newCorreo);
                            ItemUsuarioCorreo temp = datos_usuario.get(i);
                            datos_usuario.remove(i);
                            adaptadorDatosUsuarioCorreo.notifyItemRemoved(i);
                            datos_usuario.add(0,temp);
                            adaptadorDatosUsuarioCorreo.notifyItemInserted(0);
                            break;
                        }
                    }
                    if(pos == -1){
                        datos_usuario.add(0, new ItemUsuarioCorreo(newCorreo));
                        adaptadorDatosUsuarioCorreo.notifyItemInserted(0);
                    }
                }
            }
            if(datos_usuario.size()==1) cant_nuevos_correos.setText("1 usuario");
            else cant_nuevos_correos.setText(datos_usuario.size()+" usuarios");
        }
        else if(YouChatApplication.posVistaBandeja==2 && !esMio){
            datos_mensaje.add(0,newCorreo);
            adaptadorDatosMensajeCorreo.notifyItemInserted(0);
            lista_emails.scrollToPosition(0);
            if(list_empty.getVisibility() == View.VISIBLE){
                lista_emails.setVisibility(View.VISIBLE);
                lista_emails.startAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_in_fast));
                list_empty.setVisibility(View.GONE);
            }
            if(datos_mensaje.size()==1) cant_nuevos_correos.setText("1 mensaje recibido");
            else cant_nuevos_correos.setText(datos_mensaje.size()+" mensajes recibidos");
        }
        else if(YouChatApplication.posVistaBandeja==3 && esMio){
            datos_mensaje.add(0,newCorreo);
            adaptadorDatosMensajeCorreo.notifyItemInserted(0);
            lista_emails.scrollToPosition(0);
            if(list_empty.getVisibility() == View.VISIBLE){
                lista_emails.setVisibility(View.VISIBLE);
                lista_emails.startAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_in_fast));
                list_empty.setVisibility(View.GONE);
            }
            if(datos_mensaje.size()==1) cant_nuevos_correos.setText("1 mensaje enviado");
            else cant_nuevos_correos.setText(datos_mensaje.size()+" mensajes enviados");
        }
    }

    public void actualizarBadgeDeVaciar() {
        if(navigation_rail!=null){
            if(YouChatApplication.estaAndandoChatService() && YouChatApplication.chatService.hayConex){
                navigation_rail.getOrCreateBadge(R.id.menu_navrail_vaciar).setVisible(true);
                navigation_rail.getOrCreateBadge(R.id.menu_navrail_vaciar).setNumber(YouChatApplication.cant_msg_inbox);
                navigation_rail.getOrCreateBadge(R.id.menu_navrail_vaciar).setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getFont_barra()));
                navigation_rail.getOrCreateBadge(R.id.menu_navrail_vaciar).setBadgeTextColor(Color.parseColor(YouChatApplication.itemTemas.getColor_barra()));
            }
            else navigation_rail.getOrCreateBadge(R.id.menu_navrail_vaciar).setVisible(false);
        }
    }

    public void vaciadoInboxFinalizado(int cant){
        if (dialogoInbox != null){
            dialogoInbox.dismiss();
            dialogoInbox = null;
            actualizarBadgeDeVaciar();
            if (cant == -1) Utils.ShowToastAnimated(mainActivity,"Ha ocurrido un error",R.raw.error);
            else if (cant == 0) Utils.ShowToastAnimated(mainActivity,"Vaciado finalizado",R.raw.chats_unarchive);
            else if (cant == 1) Utils.ShowToastAnimated(mainActivity,"Vaciado finalizado, 1 mensaje eliminado",R.raw.chats_unarchive);
            else Utils.ShowToastAnimated(mainActivity,"Vaciado finalizado, "+ cant +" mensajes eliminados",R.raw.chats_unarchive);
        }
    }

    public void atras() {
        if(estaModoSeleccionar()) desactivarModoSeleccionar();
        else if(estaAbiertoNavRail()) cerrarNavRail();
        else mainActivity.atrasFragment();
    }

    public void irAChat(String usu, String cor) {
        Bundle mibundle=new Bundle();
        mibundle.putString("usuario",usu);
        mibundle.putString("correo",cor);
        if(mAddFragmentListener!=null)
            mAddFragmentListener.onAddFragment(BandejaFragment.this, ChatsActivityCorreo.newInstance(mibundle));
    }

    public void irANuevoCorreo(String usu, String cor) {
        if(mAddFragmentListener!=null)
            mAddFragmentListener.onAddFragment(BandejaFragment.this,
                    NuevoMensajeCorreoFragment.newInstance(usu,cor));
    }

    public void irANuevoCorreo(String usu, String cor, int tipo, ItemMensajeCorreo mc) {
        if(mAddFragmentListener!=null)
            mAddFragmentListener.onAddFragment(BandejaFragment.this,
                    NuevoMensajeCorreoFragment.newInstance(usu,cor,tipo,mc));
    }

    public void irANuevoCorreoAContacto(int tipo, ItemMensajeCorreo mc) {
        if(mAddFragmentListener!=null)
            mAddFragmentListener
                    .onAddFragment(BandejaFragment.this,
                            ContactFragmentCorreo.newInstance(BandejaFragment.this,tipo,mc));
    }

    public void marcarComoVisto(String id, boolean esNuevo) {
        if(YouChatApplication.posVistaBandeja!=1){
            if(datos_mensaje!=null){
                int l = datos_mensaje.size();
                for(int i=0; i<l; i++){
                    if(datos_mensaje.get(i).getId().equals(id)){
                        datos_mensaje.get(i).setEsNuevo(esNuevo);
                        adaptadorDatosMensajeCorreo.notifyItemChanged(i,7);
                        break;
                    }
                }
            }
        }
    }

    //modo seleccionar
    public boolean estaModoSeleccionar(){
        return ll_modo_seleccionar.getVisibility()==View.VISIBLE;
    }

    public void activarModoSeleccionar(){
        ll_modo_seleccionar.setVisibility(View.VISIBLE);
        rl_barra_contacto.setVisibility(View.GONE);
        fab_nuevo_correo.hide();
    }
    public void desactivarModoSeleccionar(){
        ll_modo_seleccionar.setVisibility(View.GONE);
        rl_barra_contacto.setVisibility(View.VISIBLE);
        fab_nuevo_correo.show();
        if(cantElemtSel>0){
            if(YouChatApplication.posVistaBandeja==1){
                for(int i=0; i<datos_usuario.size(); i++){
                    if(datos_usuario.get(i).isSeleccionado()){
                        datos_usuario.get(i).setSeleccionado(false);
                        adaptadorDatosUsuarioCorreo.notifyItemChanged(i,2);
                    }
                }
            } else {
                for(int i=0; i<datos_mensaje.size(); i++){
                    if(datos_mensaje.get(i).isSeleccionado()){
                        datos_mensaje.get(i).setSeleccionado(false);
                        adaptadorDatosMensajeCorreo.notifyItemChanged(i,2);
                    }
                }
            }
        }
        cantElemtSel = 0;
        main_ms_cant.setText(""+cantElemtSel);
    }

    private void seleccionarElementoEn(int pos) {
        if(YouChatApplication.posVistaBandeja==1){
            if(datos_usuario.get(pos).isSeleccionado()){
                cantElemtSel--;
                main_ms_cant.setText(""+cantElemtSel);
                datos_usuario.get(pos).setSeleccionado(false);
                adaptadorDatosUsuarioCorreo.notifyItemChanged(pos,2);
                if(cantElemtSel==0){
                    desactivarModoSeleccionar();
                }
            }
            else{
                cantElemtSel++;
                main_ms_cant.setText(""+cantElemtSel);
                datos_usuario.get(pos).setSeleccionado(true);
                adaptadorDatosUsuarioCorreo.notifyItemChanged(pos,1);
            }
        }
        else {
            if(datos_mensaje.get(pos).isSeleccionado()){
                cantElemtSel--;
                main_ms_cant.setText(""+cantElemtSel);
                datos_mensaje.get(pos).setSeleccionado(false);
                adaptadorDatosMensajeCorreo.notifyItemChanged(pos,2);
                if(cantElemtSel==0){
                    desactivarModoSeleccionar();
                }
            }
            else{
                cantElemtSel++;
                main_ms_cant.setText(""+cantElemtSel);
                datos_mensaje.get(pos).setSeleccionado(true);
                adaptadorDatosMensajeCorreo.notifyItemChanged(pos,1);
            }
        }
    }

    private void borrarElementosSeleccionados() {
        cantElemtSel = 0;
        if(YouChatApplication.posVistaBandeja==1){
            for(int i=0; i<datos_usuario.size(); i++){
                if(datos_usuario.get(i).isSeleccionado()){
                    dbWorker.eliminarUsuarioCorreo(datos_usuario.get(i).getCorreo());
                    datos_usuario.remove(i);
                    adaptadorDatosUsuarioCorreo.notifyItemRemoved(i);
                    i--;
                }
            }
            if(datos_usuario.size()>0){
                if(datos_usuario.size()==1) cant_nuevos_correos.setText("1 usuario");
                else cant_nuevos_correos.setText(datos_usuario.size()+" usuarios");
            }
            else {
                lista_emails.setVisibility(View.GONE);
                list_empty.setVisibility(View.VISIBLE);
                cant_nuevos_correos.setText("0 usuarios");
            }
        } else {
            for(int i=0; i<datos_mensaje.size(); i++){
                if(datos_mensaje.get(i).isSeleccionado()){
                    dbWorker.eliminarMensajeCorreo(datos_mensaje.get(i).getId());
                    datos_mensaje.remove(i);
                    adaptadorDatosMensajeCorreo.notifyItemRemoved(i);
                    i--;
                }
            }
            if(YouChatApplication.posVistaBandeja==0){
                if(datos_mensaje.size()>0){
                    if(datos_mensaje.size()==1) cant_nuevos_correos.setText("1 mensaje favorito");
                    else cant_nuevos_correos.setText(datos_mensaje.size()+" mensajes favoritos");
                }
                else {
                    lista_emails.setVisibility(View.GONE);
                    list_empty.setVisibility(View.VISIBLE);
                    cant_nuevos_correos.setText("0 mensajes favoritos");
                }
            }
            else if(YouChatApplication.posVistaBandeja==2){
                if(datos_mensaje.size()>0){
                    if(datos_mensaje.size()==1) cant_nuevos_correos.setText("1 mensaje recibido");
                    else cant_nuevos_correos.setText(datos_mensaje.size()+" mensajes recibidos");
                }
                else{
                    lista_emails.setVisibility(View.GONE);
                    list_empty.setVisibility(View.VISIBLE);
                    cant_nuevos_correos.setText("0 mensajes recibidos");
                }
            }
            else if(YouChatApplication.posVistaBandeja==3){
                if(datos_mensaje.size()>0){
                    if(datos_mensaje.size()==1) cant_nuevos_correos.setText("1 mensaje enviado");
                    else cant_nuevos_correos.setText(datos_mensaje.size()+" mensajes enviados");
                }
                else {
                    lista_emails.setVisibility(View.GONE);
                    list_empty.setVisibility(View.VISIBLE);
                    cant_nuevos_correos.setText("0 mensajes enviados");
                }
            }
        }
        desactivarModoSeleccionar();
    }


    private void seleccionarTodo(){
        if(YouChatApplication.posVistaBandeja==1){
            if(cantElemtSel == datos_usuario.size()) desactivarModoSeleccionar();
            else {
                for(int i=0; i<datos_usuario.size(); i++){
                    datos_usuario.get(i).setSeleccionado(true);
                    adaptadorDatosUsuarioCorreo.notifyItemChanged(i,1);
                }
                cantElemtSel = datos_usuario.size();
                main_ms_cant.setText(""+cantElemtSel);
            }

        } else {
            if(cantElemtSel == datos_mensaje.size()) desactivarModoSeleccionar();
            else {
                for(int i=0; i<datos_mensaje.size(); i++){
                    datos_mensaje.get(i).setSeleccionado(true);
                    adaptadorDatosMensajeCorreo.notifyItemChanged(i,1);
                }
                cantElemtSel = datos_mensaje.size();
                main_ms_cant.setText(""+cantElemtSel);
            }
        }
    }
}
