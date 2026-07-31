package cu.alexgi.youchat;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.transition.MaterialContainerTransform;
import com.vanniktech.emoji.EmojiEditText;

import java.util.ArrayList;
import java.util.Arrays;

import cu.alexgi.youchat.adapters.AdaptadorDatosContactoCorreo;
import cu.alexgi.youchat.adapters.AdaptadorDatosContactoCorreoSeleccionado;
import cu.alexgi.youchat.items.ItemContacto;
import cu.alexgi.youchat.items.ItemMensajeCorreo;
import cu.alexgi.youchat.sticky_headers_data.StickyRecyclerHeadersDecoration;
import cu.alexgi.youchat.swipebackfragment.BaseSwipeBackFragment;
import cu.alexgi.youchat.swipebackfragment.SwipeBackLayout;
import cu.alexgi.youchat.views_GI.FABGI;

import static android.app.Activity.RESULT_OK;
import static cu.alexgi.youchat.MainActivity.context;
import static cu.alexgi.youchat.MainActivity.dbWorker;
import static cu.alexgi.youchat.MainActivity.mainActivity;
import static cu.alexgi.youchat.MainActivity.permisos;


public class ContactFragmentCorreo extends BaseSwipeBackFragment {

    public static final String TAG = "ContactFragmentCorreo";
    private int Listsize;
    private ContentResolver cr;
    private boolean act;
    private boolean ordenarXNombre;
    private Animation anim;
    private Dialog mProgressDialog;
    private TextView texto_alerta,list_empty;

    private TextView cant_contacto,texto_orden;
    private View contacto_orden, contacto_nuevo_contacto, contacto_nueva_conversacion
            , area_menu, menuP_contacto, atras_contact, contacto_refresh, contacto_search,
            cancel_buscar_contacto, more_options;

    private ArrayList<ItemContacto> datos_Contacto, datos_Contacto_seleccionados;
    private RecyclerView lista_contacto, lista_contactos_seleccionados;
    private AdaptadorDatosContactoCorreo adaptadorContacto;
    private AdaptadorDatosContactoCorreoSeleccionado adaptadorContactoSeleccionado;

    private RelativeLayout rl_barra_contacto;
    private LinearLayout layout_primario,ll_buscar_contacto;
    private EmojiEditText buscar_contactos;
    
    private FABGI fab_iniciar_conversacion;

    private boolean esNauta;
    private String aliasP, infoP, correoP, telefonoP, generoP, provinciaP, fecha_nacimientoP, ruta_img_perfilP;

    private static ItemMensajeCorreo mensajeCorreo;
    private static int tipo;

    private static BandejaFragment bandejaFragment;
    public static ContactFragmentCorreo newInstance(BandejaFragment pa) {
        bandejaFragment = pa;
        mensajeCorreo = null;
        tipo=-1;
        ContactFragmentCorreo fragment = new ContactFragmentCorreo();
        return fragment;
    }

    public static ContactFragmentCorreo newInstance(BandejaFragment pa, int t, ItemMensajeCorreo mc) {
        bandejaFragment = pa;
        mensajeCorreo = mc;
        tipo=t;
        ContactFragmentCorreo fragment = new ContactFragmentCorreo();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return attachToSwipeBack(inflater.inflate(R.layout.fragment_contact_correo, container, false));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        YouChatApplication.contactFragmentCorreo=null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        YouChatApplication.contactFragmentCorreo = this;
        texto_orden=view.findViewById(R.id.texto_orden);
        cargarPreferencias();
        act=false;

        if(tipo==2) ((TextView)view.findViewById(R.id.titulo)).setText("Reenviar a...");

        esNauta = YouChatApplication.correo.endsWith("@nauta.cu");

        getSwipeBackLayout().addSwipeListener(new SwipeBackLayout.OnSwipeListener() {
            @Override
            public void onDragStateChange(int state) {
                if(state==1)
                {
                    Utils.ocultarKeyBoard(mainActivity);
                }
            }
            @Override
            public void onEdgeTouch(int oritentationEdgeFlag){}
            @Override
            public void onDragScrolled(float scrollPercent){}
        });
//        navController = Navigation.findNavController(view);

        RelativeLayout root_view_contact_activity = view.findViewById(R.id.root_view_contact_activity);
        root_view_contact_activity.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_fondo()));
        list_empty=view.findViewById(R.id.list_empty);
        buscar_contactos = view.findViewById(R.id.buscar_contacto);
        layout_primario=view.findViewById(R.id.layout_primario);
        cant_contacto = view.findViewById(R.id.cant_contacto);
        rl_barra_contacto = view.findViewById(R.id.rl_barra_contacto);
        ll_buscar_contacto = view.findViewById(R.id.ll_buscar_contacto);
        menuP_contacto = view.findViewById(R.id.menuP_contacto);
        fab_iniciar_conversacion = view.findViewById(R.id.fab_iniciar_conversacion);
        fab_iniciar_conversacion.hide();
        
        fab_iniciar_conversacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nuevaConversacion();
            }
        });

        contacto_orden = view.findViewById(R.id.contacto_orden);
        contacto_orden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ordenarXNombre){
                    YouChatApplication.setOrdenarXNombre(false);
                    ordenarXNombre=false;
                    actualizarListaContactos();
                    texto_orden.setText("Ordenar por nombre");
                }
                else {
                    YouChatApplication.setOrdenarXNombre(true);
                    ordenarXNombre=true;
                    actualizarListaContactos();
                    texto_orden.setText("Ordenar por correo");
                }
                cerrarMenu();
            }
        });

        contacto_nuevo_contacto = view.findViewById(R.id.contacto_nuevo_contacto);
        contacto_nuevo_contacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarMenu();
                if (!permisos.requestPermissionContactos()) return;
                Intent intent = new Intent(Intent.ACTION_INSERT);
                intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                intent.putExtra("finishActivityOnSaveCompleted", true);
                startActivityForResult(intent, 33);
            }
        });

        contacto_nueva_conversacion = view.findViewById(R.id.contacto_nueva_conversacion);
        contacto_nueva_conversacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(menuP_contacto.getVisibility()==View.VISIBLE) cerrarMenu();

                Dialog dialogo = new Dialog(context);
                dialogo.requestWindowFeature(1);
                View mviewe=getLayoutInflater().inflate(R.layout.dialog_confirm_new_chat,null);
                dialogo.setContentView(mviewe);

                EditText et_codigo=mviewe.findViewById(R.id.et_codigo);
                View btn_ok=mviewe.findViewById(R.id.btn_ok);
                View btn_cancel=mviewe.findViewById(R.id.btn_cancel);

                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String cad = et_codigo.getText().toString().trim();
                        if(cad.length()>1 && cad.contains("@")){
                            dialogo.dismiss();
                            Utils.runOnUIThread(()->{
                                if(YouChatApplication.posVistaBandeja==1) irAChat(cad,cad);
                                else irANuevoCorreo(cad,cad);

                            });
                        }
                        else Utils.ShowToastAnimated(mainActivity,"Debe de ingresar un correo",R.raw.chats_infotip);
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
        });

        contacto_refresh = view.findViewById(R.id.contacto_refresh);
        contacto_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarMenu();
                if(!permisos.requestPermissionContactos()) return;
                ObtenerContactosAsyncTask task = new ObtenerContactosAsyncTask();
                task.execute();
            }
        });

        contacto_search = view.findViewById(R.id.contacto_search);
        contacto_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarMenu();
                anim= AnimationUtils.loadAnimation(context,R.anim.show_layout_search);
                ll_buscar_contacto.startAnimation(anim);
                ll_buscar_contacto.setVisibility(View.VISIBLE);
                anim= AnimationUtils.loadAnimation(context,R.anim.hide_layout_contact);
                layout_primario.startAnimation(anim);
                layout_primario.setVisibility(View.GONE);
                rl_barra_contacto.setVisibility(View.GONE);

                act=true;

                buscar_contactos.setFocusableInTouchMode(true);
                buscar_contactos.requestFocus();
                final InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(buscar_contactos, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        cancel_buscar_contacto = view.findViewById(R.id.cancel_buscar_contacto);
        cancel_buscar_contacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelar();
                final InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(buscar_contactos.getWindowToken(), 0);
            }
        });

        more_options = view.findViewById(R.id.more_options);
        more_options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anim=AnimationUtils.loadAnimation(context,R.anim.show_menu);
                menuP_contacto.setVisibility(View.VISIBLE);
                menuP_contacto.startAnimation(anim);
            }
        });

        atras_contact = view.findViewById(R.id.atras_contact);
        atras_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(menuP_contacto.getVisibility()==View.GONE){
                    atras();
                }
            }
        });

        area_menu = view.findViewById(R.id.area_menu);
        area_menu.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(menuP_contacto.getVisibility()==View.VISIBLE) cerrarMenu();
                return true;
            }
        });


        buscar_contactos.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String espacio = s.toString();
                if(espacio.replace(" ","").length()>0){
                    Buscar(espacio);
                }
                else
                {
                    if(espacio.length()>0) buscar_contactos.setText("");
                    adaptadorContacto = new AdaptadorDatosContactoCorreo(datos_Contacto,ContactFragmentCorreo.this);
                    lista_contacto.setAdapter(adaptadorContacto);
                    StickyRecyclerHeadersDecoration headersDecor =
                            new StickyRecyclerHeadersDecoration(adaptadorContacto);
                    lista_contacto.addItemDecoration(headersDecor);
                    cant_contacto.setText(datos_Contacto.size()+" Contactos");
                    activarFuncionAdaptador();
                }
            }
        });

        cr = getActivity().getContentResolver();
        datos_Contacto_seleccionados = new ArrayList<>();
        lista_contactos_seleccionados = view.findViewById(R.id.lista_contactos_seleccionados);
        lista_contactos_seleccionados.setLayoutManager(
                new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        adaptadorContactoSeleccionado = new AdaptadorDatosContactoCorreoSeleccionado(datos_Contacto_seleccionados);
        lista_contactos_seleccionados.setAdapter(adaptadorContactoSeleccionado);
        adaptadorContactoSeleccionado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(menuP_contacto.getVisibility()==View.GONE){
                    int posAd = lista_contactos_seleccionados.getChildAdapterPosition(v);
                    if(posAd!=-1){
                        actualizarSeleccionContacto(
                                datos_Contacto_seleccionados.get(posAd),
                                false, posAd);
                    }
                }
            }
        });

        lista_contacto = view.findViewById(R.id.lista_contactos);
        lista_contacto.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        datos_Contacto = new ArrayList<>();
        adaptadorContacto = new AdaptadorDatosContactoCorreo(datos_Contacto,ContactFragmentCorreo.this);
        lista_contacto.setAdapter(adaptadorContacto);
        StickyRecyclerHeadersDecoration headersDecor =
                new StickyRecyclerHeadersDecoration(adaptadorContacto);
        lista_contacto.addItemDecoration(headersDecor);
        actualizarListaContactos();
    }

    private void nuevaConversacion() {
        if(YouChatApplication.posVistaBandeja==1){
            if(datos_Contacto_seleccionados.size()>1){
                int l = datos_Contacto_seleccionados.size();
                String[] destinatariosString = new String[l];
                for(int i=0; i<l; i++){
                    destinatariosString[i] = datos_Contacto_seleccionados.get(i).getCorreo();
                }
                Arrays.sort(destinatariosString);
                String destinatario = "";
                for(int i=0; i<l; i++){
                    if (!destinatario.isEmpty()) destinatario += ",";
                    destinatario += destinatariosString[i];
                }
                irAChat(destinatario,destinatario);
            }
            else if(datos_Contacto_seleccionados.size()==1){
                irAChat(datos_Contacto_seleccionados.get(0).getNombreMostrar(),
                        datos_Contacto_seleccionados.get(0).getCorreo());
            }
        }else {
            if(datos_Contacto_seleccionados.size()>1){
                int l = datos_Contacto_seleccionados.size();
                String[] destinatariosString = new String[l];
                for(int i=0; i<l; i++){
                    destinatariosString[i] = datos_Contacto_seleccionados.get(i).getCorreo();
                }
                Arrays.sort(destinatariosString);
                String destinatario = "";
                for(int i=0; i<l; i++){
                    if (!destinatario.isEmpty()) destinatario += ",";
                    destinatario += destinatariosString[i];
                }
                irANuevoCorreo(destinatario,destinatario);
            }
            else if(datos_Contacto_seleccionados.size()==1){
                irANuevoCorreo(datos_Contacto_seleccionados.get(0).getNombreMostrar(),
                        datos_Contacto_seleccionados.get(0).getCorreo());
            }
        }
    }

    private void actualizarSeleccionContacto(ItemContacto contacto, boolean sumar, int pos) {
        if(sumar){
            if(contacto.getCorreo().equals(YouChatApplication.correo))
                Utils.ShowToastAnimated(mainActivity,"No te puedes escribir a ti mismo", R.raw.error);
            else {
                int l = datos_Contacto_seleccionados.size();
                if(esNauta && l>=20)
                    Utils.ShowToastAnimated(mainActivity, "No puedes tener más de 20 destinatarios", R.raw.ic_ban);
                else {
                    boolean exist = false;
                    for(int i=0; i<l; i++){
                        if(datos_Contacto_seleccionados.get(i).getCorreo().equals(contacto.getCorreo())){
                            exist = true;
                            break;
                        }
                    }
                    if(!exist){
                        adaptadorContactoSeleccionado.hacerAnim();
                        datos_Contacto_seleccionados.add(contacto);
                        adaptadorContactoSeleccionado.notifyItemInserted(l);
                        lista_contactos_seleccionados.scrollToPosition(datos_Contacto_seleccionados.size()-1);
                    }
                }
            }
        }else {
            datos_Contacto_seleccionados.remove(pos);
            adaptadorContactoSeleccionado.notifyItemRemoved(pos);
        }
        if(datos_Contacto_seleccionados.size()>0){
            if(!fab_iniciar_conversacion.isShown())
                fab_iniciar_conversacion.show();
        }
        else if(fab_iniciar_conversacion.isShown())
            fab_iniciar_conversacion.hide();

    }

    private void irAChat(String usu, String cor) {
        if(mainActivity!=null){
            if(Utils.ocultarKeyBoardEsperar(mainActivity)){
                getActivity().onBackPressed();
                bandejaFragment.irAChat(usu,cor);
            }
            else {
                getActivity().onBackPressed();
                bandejaFragment.irAChat(usu,cor);
            }
        }else {
            getActivity().onBackPressed();
            bandejaFragment.irAChat(usu,cor);
        }
    }

    private void irANuevoCorreo(String usu, String cor) {
        if(tipo!=-1 && mensajeCorreo!=null) irANuevoCorreo(usu,cor,tipo);
        else {
            if(mainActivity!=null){
                if(Utils.ocultarKeyBoardEsperar(mainActivity)){
                    getActivity().onBackPressed();
                    bandejaFragment.irANuevoCorreo(usu,cor);
                }
                else {
                    getActivity().onBackPressed();
                    bandejaFragment.irANuevoCorreo(usu,cor);
                }
            }else {
                getActivity().onBackPressed();
                bandejaFragment.irANuevoCorreo(usu,cor);
            }
        }
    }

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    private void cerrarMenu(){
        anim=AnimationUtils.loadAnimation(context,R.anim.fade_out_fast);
        menuP_contacto.setVisibility(View.GONE);
        menuP_contacto.startAnimation(anim);
        setSharedElementEnterTransition(new MaterialContainerTransform());
    }

    public void actualizarListaContactos(){
        ArrayList<ItemContacto> cargaContactos = dbWorker.obtenerContactosOrdenadosXNombre(ordenarXNombre);
        //adaptadorContacto = new AdaptadorDatosContactoCorreo(datos_Contacto, 2);
        //lista_contacto.setAdapter(adaptadorContacto);

        datos_Contacto.clear();
        datos_Contacto.addAll(cargaContactos);

        cant_contacto.setText(datos_Contacto.size()+" Contactos");
        if(datos_Contacto.size()==0){
            lista_contacto.setVisibility(View.GONE);
            list_empty.setVisibility(View.VISIBLE);
        } else {
            lista_contacto.setVisibility(View.VISIBLE);
            list_empty.setVisibility(View.GONE);
            activarFuncionAdaptador();
//            ponerAlanteContactosYouChat();
            adaptadorContacto.notifyDataSetChanged();
        }
    }

    private void ponerAlanteContactosYouChat() {
        int l=datos_Contacto.size();
        int pos=0;
        for(int i=0; i<l; i++){
            if(datos_Contacto.get(i).isUsaYouchat()){
                ItemContacto temp = datos_Contacto.get(i);
                datos_Contacto.remove(i);
                datos_Contacto.add(pos,temp);
                pos++;
            }
        }
        if(pos>0){
            ItemContacto dividerYC = new ItemContacto("Contactos con YouChat");
            datos_Contacto.add(0,dividerYC);
            if(pos<l-2){
                ItemContacto dividerCo = new ItemContacto("Otros contactos");
                datos_Contacto.add(pos+1,dividerCo);
            }
        }
    }

    void Buscar(String s){
        final ArrayList<ItemContacto> datos_Contacto_buscar = new ArrayList<>();
        for(int i=0; i<datos_Contacto.size(); i++){
            if(datos_Contacto.get(i).getAlias().toLowerCase().contains(s.toLowerCase())
                    || datos_Contacto.get(i).getNombre_personal().toLowerCase().contains(s.toLowerCase())
                    || datos_Contacto.get(i).getCorreo().toLowerCase().contains(s.toLowerCase()))
                datos_Contacto_buscar.add(datos_Contacto.get(i));
        }

        Listsize=datos_Contacto_buscar.size();
        if(Listsize==0){
            //mostrar cartel de que no existe ningun contacto
            lista_contacto.setVisibility(View.GONE);
            list_empty.setVisibility(View.VISIBLE);
        }
        else {
            lista_contacto.setVisibility(View.VISIBLE);
            list_empty.setVisibility(View.GONE);
            adaptadorContacto = new AdaptadorDatosContactoCorreo(datos_Contacto_buscar,ContactFragmentCorreo.this);
            lista_contacto.setAdapter(adaptadorContacto);
            StickyRecyclerHeadersDecoration headersDecor =
                    new StickyRecyclerHeadersDecoration(adaptadorContacto);
            lista_contacto.addItemDecoration(headersDecor);

            adaptadorContacto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int posAd = lista_contacto.getChildAdapterPosition(v);
                    if(posAd!=-1){
                        actualizarSeleccionContacto(
                                datos_Contacto_buscar.get(posAd),
                                true, posAd);
                    }
                }
            });
        }
    }

    void cancelar(){
        if(datos_Contacto.size()==0){
            //mostrar cartel de que no existe ningun contacto
            lista_contacto.setVisibility(View.GONE);
            list_empty.setVisibility(View.VISIBLE);
        }
        else {
            lista_contacto.setVisibility(View.VISIBLE);
            list_empty.setVisibility(View.GONE);

            adaptadorContacto = new AdaptadorDatosContactoCorreo(datos_Contacto,ContactFragmentCorreo.this);
            lista_contacto.setAdapter(adaptadorContacto);
            StickyRecyclerHeadersDecoration headersDecor =
                    new StickyRecyclerHeadersDecoration(adaptadorContacto);
            lista_contacto.addItemDecoration(headersDecor);
            activarFuncionAdaptador();
        }

        cant_contacto.setText(datos_Contacto.size()+" Contactos");
        buscar_contactos.setText("");
        anim= AnimationUtils.loadAnimation(context,R.anim.show_layout_contact);
        layout_primario.startAnimation(anim);
        layout_primario.setVisibility(View.VISIBLE);
        anim= AnimationUtils.loadAnimation(context,R.anim.hide_layout_search);
        ll_buscar_contacto.startAnimation(anim);
        ll_buscar_contacto.setVisibility(View.GONE);
        rl_barra_contacto.setVisibility(View.VISIBLE);
        act = false;
    }

    public void atras() {
        if(menuP_contacto.getVisibility()==View.VISIBLE) cerrarMenu();
        else if(act) cancelar();
        else mainActivity.atrasFragment();
//        else {
//            getActivity().onBackPressed();
//            navController.navigateUp();
//        }
    }

    private int buscarId(String cor){
        int l=datos_Contacto.size();
        for(int i=0; i<l; i++){
            if(datos_Contacto.get(i).getCorreo().equals(cor))
                return i;
        }
        return -1;
    }


    private void adicionarContactoAlTelefono(Context context, ItemContacto contacto){
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);

        String nombreC =  contacto.getNombreMostrar();
        String telefonoC = contacto.getTelefono();
        String correoC = contacto.getCorreo();

        if(!nombreC.equals("")){
            intent.putExtra(ContactsContract.Intents.Insert.NAME, nombreC);
        }
        if(!telefonoC.equals("")){
            intent.putExtra(ContactsContract.Intents.Insert.PHONE, telefonoC);
            intent.putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_MOBILE);
        }
        if(!correoC.equals("")){
            intent.putExtra(ContactsContract.Intents.Insert.EMAIL, correoC);
            intent.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK);
        }

        intent.putExtra("finishActivityOnSaveCompleted", true);
        startActivityForResult(intent, 33);
    }

    private boolean existeEsteCorreo(String cor){
        int l=datos_Contacto.size();
        for(int i=0; i<l; i++)
            if(datos_Contacto.get(i).getCorreo().equals(cor))
                return true;
        return false;
    }

    private void cargarPreferencias() {
        aliasP = YouChatApplication.alias;
        correoP = YouChatApplication.correo;
        ruta_img_perfilP = YouChatApplication.ruta_img_perfil;
        infoP = YouChatApplication.info;
        telefonoP = YouChatApplication.telefono;
        generoP = YouChatApplication.genero;
        provinciaP = YouChatApplication.provincia;
        fecha_nacimientoP = YouChatApplication.fecha_nacimiento;

        ordenarXNombre= YouChatApplication.orden_contacto_nombre;
        if(ordenarXNombre) texto_orden.setText("Ordenar por correo");
        else texto_orden.setText("Ordenar por nombre");
    }

    public void activarFuncionAdaptador(){
        adaptadorContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(menuP_contacto.getVisibility()==View.GONE){
                    int posAd = lista_contacto.getChildAdapterPosition(v);
                    if(posAd!=-1){
                        actualizarSeleccionContacto(
                                datos_Contacto.get(posAd),
                                true, posAd);
                    }
                }
            }
        });
    }

    private void showLoading(@NonNull String message) {

        mProgressDialog = new Dialog(context);
        mProgressDialog.requestWindowFeature(1);
        View mview=getLayoutInflater().inflate(R.layout.dialog_alert_progress,null);
        mProgressDialog.setContentView(mview);

        texto_alerta=mview.findViewById(R.id.texto_alerta);
        texto_alerta.setText(message);

        mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    private void hideLoading() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    private class ObtenerContactosAsyncTask extends AsyncTask<String, String, String> {

        ArrayList<ItemContacto> contactos_actualizar;

        public ObtenerContactosAsyncTask() {
            contactos_actualizar = new ArrayList<>();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading("Por favor, espere...");
        }

        @Override
        protected String doInBackground(String... params) {
            contactos_actualizar = obtenerContactos();
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            hideLoading();
            if(contactos_actualizar!=null){
                int l=contactos_actualizar.size();
                int contAdd=0;
                int contMod=0;
                for(int i=0; i<l; i++){
                    String correoAct = contactos_actualizar.get(i).getCorreo();
                    boolean exist = existeEsteCorreo(correoAct);
                    if(!exist){
                        dbWorker.insertarNuevoContacto(contactos_actualizar.get(i));
                        contAdd++;
                    }
                    else{
                        int pos = buscarId(correoAct);
                        String nombreAct = contactos_actualizar.get(i).getNombre_personal();
                        if(pos!=-1 && !nombreAct.equals(datos_Contacto.get(pos).getNombre_personal())){
                            contMod++;
                            dbWorker.actualizarNombrePersonalDe(correoAct, nombreAct);
                        }
                    }
                }
                String textoMostrar="";
                if(contAdd==0) textoMostrar = "Ningún contacto nuevo encontrado\n";
                else if(contAdd==1) textoMostrar ="1 nuevo contacto añadido\n";
                else textoMostrar = contAdd+" nuevos contactos añadidos\n";

                if(contMod==0) textoMostrar = textoMostrar+"Ningún contacto modificado";
                else if(contMod==1) textoMostrar =textoMostrar+"1 contacto modificado";
                else textoMostrar = textoMostrar+""+contMod+" contactos modificados";

                if(contAdd>0 || contMod>0)
                    actualizarListaContactos();
                Utils.ShowToastAnimated(mainActivity,textoMostrar ,R.raw.attach_contact);
            }
        }
    }

    ArrayList<ItemContacto> obtenerContactos(){
        ArrayList<ItemContacto> contactos_actualizar = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // get the contact's information
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String email = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));

                if(email!=null && email.contains("@")){
                    if(name.equals("") || name==null) name=email;
                    contactos_actualizar.add(0, new ItemContacto("", name, email,ItemContacto.TIPO_CONTACTO,0, "", "", "",
                            "", "", "", "", "", false, false, false, 0));
                }
            } while (cursor.moveToNext());
        }
        // clean up cursor
        cursor.close();
        return contactos_actualizar;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 33) {
            if (resultCode == RESULT_OK) {
                ObtenerContactosAsyncTask task = new ObtenerContactosAsyncTask();
                task.execute();
            }
        }
    }
}
