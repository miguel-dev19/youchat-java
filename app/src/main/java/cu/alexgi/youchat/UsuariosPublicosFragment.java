package cu.alexgi.youchat;

import android.app.Dialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
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

import cu.alexgi.youchat.BottomSheetDialogFragment.BottomSheetDialogFragment_usuario_publico;
import cu.alexgi.youchat.adapters.AdaptadorDatosContactoPublico;
import cu.alexgi.youchat.base_datos2.BDConstantes2;
import cu.alexgi.youchat.base_datos2.DBWorker2;
import cu.alexgi.youchat.items.ItemContactoPublico;
import cu.alexgi.youchat.sticky_headers_data.StickyRecyclerHeadersDecoration;
import cu.alexgi.youchat.swipebackfragment.BaseSwipeBackFragment;
import cu.alexgi.youchat.views_GI.SwitchGI;

import static cu.alexgi.youchat.MainActivity.context;
import static cu.alexgi.youchat.MainActivity.mainActivity;

public class UsuariosPublicosFragment extends BaseSwipeBackFragment {

    private RecyclerView lista_post;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<ItemContactoPublico> datos_contacto_publico;
    private AdaptadorDatosContactoPublico adaptadorDatosContactoPublico;
    private StickyRecyclerHeadersDecoration headersDecor;

    private BottomSheetBehavior bsb;
    private LottieAnimationView anim;
    private View list_empty, actualizar_user_pub, ll_usuario_publico_bus_rec, iv_filtros, option_filtro_cumpleano, borrar_buscador;
    private TextView tv_cant_user_pub, tv_edad_desde, tv_edad_hasta, buscar_usuario_publico;

    private boolean hayDivisorNuevos;

    private AppCompatSpinner spinner_genero, spinner_provincia;
    private String filtroGenero, filtroProvincia;
    private SwitchGI switch_filtro_cumpleano;
    private boolean filtroCumple;
    private int filtroEdadDesde, filtroEdadHasta;
    private String filtroTexto;

    private DBWorker2 dbWorker2;
    private Dialog dialog;
    private UsuariosPublicosFragment usuariosPublicosFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return attachToSwipeBack(inflater.inflate(R.layout.fragment_usuarios_publicos, container, false));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.root).setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_fondo()));

        filtroTexto = "";
        filtroGenero = "";
        filtroProvincia = "";
        filtroCumple = false;
        filtroEdadDesde =  filtroEdadHasta = -1;
        usuariosPublicosFragment = this;

        spinner_genero = view.findViewById(R.id.spinner_genero);
        ArrayList<String> lista = new ArrayList<>();
        lista.add("Ninguno");
        lista.add("Masculino");
        lista.add("Femenino");
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(context,R.layout.layout_spinner, R.id.name, lista);
        spinner_genero.setAdapter(adapter);
        spinner_genero.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0: filtroGenero = ""; buscar(); break;
                    case 1: filtroGenero = "masculino"; buscar(); break;
                    case 2: filtroGenero = "femenino"; buscar(); break;
                    default:
                        filtroGenero = "";
                        buscar();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_provincia = view.findViewById(R.id.spinner_provincia);
        ArrayList<String> lista2 = new ArrayList<>();
        lista2.add("Ninguna");
        lista2.add("Pinar del Río");
        lista2.add("Artemisa");
        lista2.add("Mayabeque");
        lista2.add("La Habana");
        lista2.add("Matanzas");
        lista2.add("Cienfuegos");
        lista2.add("Villa Clara");
        lista2.add("Sancti Spíritus");
        lista2.add("Ciego de Ávila");
        lista2.add("Camagüey");
        lista2.add("Las Tunas");
        lista2.add("Holguín");
        lista2.add("Granma");
        lista2.add("Santiago de Cuba");
        lista2.add("Guantánamo");
        lista2.add("Isla de la Juventud");
        ArrayAdapter<CharSequence> adapter2 = new ArrayAdapter(context,R.layout.layout_spinner, R.id.name, lista2);
        spinner_provincia.setAdapter(adapter2);
        spinner_provincia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0: filtroProvincia = ""; buscar(); break;
                    case 1: filtroProvincia = "pinar del río"; buscar(); break;
                    case 2: filtroProvincia = "artemisa"; buscar(); break;
                    case 3: filtroProvincia = "mayabeque"; buscar(); break;
                    case 4: filtroProvincia = "la habana"; buscar(); break;
                    case 5: filtroProvincia = "matanzas"; buscar(); break;
                    case 6: filtroProvincia = "cienfuegos"; buscar(); break;
                    case 7: filtroProvincia = "villa clara"; buscar(); break;
                    case 8: filtroProvincia = "sancti spíritus"; buscar(); break;
                    case 9: filtroProvincia = "ciego de ávila"; buscar(); break;
                    case 10: filtroProvincia = "camagüey"; buscar(); break;
                    case 11: filtroProvincia = "las tunas"; buscar(); break;
                    case 12: filtroProvincia = "holguín"; buscar(); break;
                    case 13: filtroProvincia = "granma"; buscar(); break;
                    case 14: filtroProvincia = "santiago de cuba"; buscar(); break;
                    case 15: filtroProvincia = "guantánamo"; buscar(); break;
                    case 16: filtroProvincia = "isla de la juventud"; buscar(); break;
                    default:
                        filtroProvincia = "";
                        buscar();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        switch_filtro_cumpleano = view.findViewById(R.id.switch_filtro_cumpleano);
        option_filtro_cumpleano = view.findViewById(R.id.option_filtro_cumpleano);
        option_filtro_cumpleano.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filtroCumple = !filtroCumple;
                switch_filtro_cumpleano.setChecked(filtroCumple);
                buscar();
            }
        });

        tv_edad_desde = view.findViewById(R.id.tv_edad_desde);
        tv_edad_hasta = view.findViewById(R.id.tv_edad_hasta);
        tv_edad_desde.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String cad = s.toString().trim();
                if(cad.isEmpty()) filtroEdadDesde=-1;
                else filtroEdadDesde = Convertidor.createIntOfString(cad);
                buscar();
            }
        });
        tv_edad_hasta.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String cad = s.toString().trim();
                if(cad.isEmpty()) filtroEdadHasta=-1;
                else filtroEdadHasta = Convertidor.createIntOfString(cad);
                buscar();
            }
        });

        buscar_usuario_publico = view.findViewById(R.id.buscar_usuario_publico);
        buscar_usuario_publico.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(bsb.getState()!=BottomSheetBehavior.STATE_COLLAPSED)
                    bsb.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
            @Override
            public void afterTextChanged(Editable s) {
                filtroTexto = s.toString().trim().toLowerCase();
                buscar();
            }
        });
        view.findViewById(R.id.borrar_buscador).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(buscar_usuario_publico.length()>0)
                    buscar_usuario_publico.setText("");
            }
        });

        iv_filtros = view.findViewById(R.id.iv_filtros);
//        card_view_filtros = view.findViewById(R.id.card_view_filtros);
        ll_usuario_publico_bus_rec = view.findViewById(R.id.ll_usuario_publico_bus_rec);
        actualizar_user_pub = view.findViewById(R.id.actualizar_user_pub);
        tv_cant_user_pub = view.findViewById(R.id.tv_cant_user_pub);
        view.findViewById(R.id.atras_bloqueados).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        iv_filtros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bsb!=null){
                    if(bsb.getState()!=BottomSheetBehavior.STATE_EXPANDED)
                        bsb.setState(BottomSheetBehavior.STATE_EXPANDED);
                    else bsb.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        hayDivisorNuevos = false;
        list_empty = view.findViewById(R.id.list_empty);
        anim = view.findViewById(R.id.anim);

        lista_post = view.findViewById(R.id.lista_post);
        linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        lista_post.setLayoutManager(linearLayoutManager);

        dbWorker2 = new DBWorker2(context);

        actualizarListaUsuariosPublicos();

        actualizar_user_pub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(YouChatApplication.estaAndandoChatService() && YouChatApplication.chatService.hayConex){
                    actualizar_user_pub.setEnabled(false);
                    actualizar_user_pub.setVisibility(View.INVISIBLE);
//                    Utils.ShowToastAnimated(mainActivity,"Por favor espere, el proceso puede demorar varios segundos", R.raw.ic_ban);

                    ObtenerUsuariosPubGmailAsyncTask aaa = new ObtenerUsuariosPubGmailAsyncTask();
                    aaa.execute();
                }
                else Utils.mostrarToastDeConexion(mainActivity);
            }
        });


        View bottomSheetInternal = view.findViewById(R.id.bsd_view);
        bsb = BottomSheetBehavior.from(bottomSheetInternal);

        bsb.setPeekHeight(0);
        bsb.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bsb.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });
    }

    private void actualizarListaUsuariosPublicos() {
        lista_post.removeItemDecoration(headersDecor);
        datos_contacto_publico = dbWorker2.obtenerContactosOrdenadosXNombre(YouChatApplication.orden_contacto_nombre);
        adaptadorDatosContactoPublico = new AdaptadorDatosContactoPublico(datos_contacto_publico);
        lista_post.setAdapter(adaptadorDatosContactoPublico);
        headersDecor = new StickyRecyclerHeadersDecoration(adaptadorDatosContactoPublico);
        lista_post.addItemDecoration(headersDecor);

        adaptadorDatosContactoPublico.setOnItemClickListener(new AdaptadorDatosContactoPublico.OnItemClickListener() {
            @Override
            public void OnItemClick(ItemContactoPublico contactoPublico) {
                if(bsb.getState()!=BottomSheetBehavior.STATE_COLLAPSED)
                    bsb.setState(BottomSheetBehavior.STATE_COLLAPSED);

                BottomSheetDialogFragment_usuario_publico aaa =
                        BottomSheetDialogFragment_usuario_publico.newInstance(context,contactoPublico);
                aaa.setOnClickListener(new BottomSheetDialogFragment_usuario_publico.OnClickListener() {
                    @Override
                    public void OnIrAChatSelected(String alias, String correo) {
                        IrAChat(alias,correo);
                    }
                });
                aaa.show(getParentFragmentManager(),"BottomSheetDialogFragment_usuario_publico");
            }
        });

        if(datos_contacto_publico.size()==0){
            anim.setAnimation(R.raw.new_empty_file);
            anim.playAnimation();
            anim.setVisibility(View.VISIBLE);
            list_empty.setVisibility(View.VISIBLE);
            ll_usuario_publico_bus_rec.setVisibility(View.GONE);
        }
        else {
            anim.setAnimation((Animation) null);
            anim.cancelAnimation();
            anim.setVisibility(View.GONE);
            list_empty.setVisibility(View.GONE);
            ll_usuario_publico_bus_rec.setVisibility(View.VISIBLE);
        }

        tv_cant_user_pub.setText(datos_contacto_publico.size()+" en total");
    }

    private void IrAChat(String alias, String correo) {
        Utils.runOnUIThread(()->{
            Bundle mibundle=new Bundle();
            mibundle.putString("usuario",alias);
            mibundle.putString("correo",correo);
            if(mAddFragmentListener!=null)
                mAddFragmentListener.onAddFragment(UsuariosPublicosFragment.this, ChatsActivity.newInstance(mibundle));
        });
    }

    private class ObtenerUsuariosPubGmailAsyncTask extends AsyncTask<String, String, String> {

        boolean exito;

        public ObtenerUsuariosPubGmailAsyncTask(){
            exito = false;
            dialog = Utils.mostrarDialogCarga(usuariosPublicosFragment, context, "Actualizando lista...");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String user, pass;
                pass = YouChatApplication.chatService.getNxfaq();
                Session session;
                IMAPStore store;
                Properties props = new Properties();
                IMAPFolder inbox;
                if(YouChatApplication.correo.endsWith("@nauta.cu")){
                    props.setProperty("mail.store.protocol", "imap");
                    props.setProperty("mail.imap.host", "imap.nauta.cu");
                    props.setProperty("mail.imap.port", "143");
                    user = YouChatApplication.chatService.getNxdiag();
                    session = Session.getDefaultInstance(props, new Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(user, pass);
                        }
                    });
                    store = (IMAPStore) session.getStore("imap");
                    store.connect("imap.nauta.cu", user, pass);
                }
                else{
                    props.setProperty("mail.imap.starttls.enable", "false");
                    props.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                    props.setProperty("mail.imap.socketFactory.fallback", "false");
                    props.setProperty("mail.imap.port", "993");
                    props.setProperty("mail.imap.socketFactory.port", "993");
                    user = YouChatApplication.chatService.getGxdiag();
                    session = Session.getInstance(props);
                    session.setDebug(true);
                    store = (IMAPStore) session.getStore("imap");
                    store.connect("imap.gmail.com", user, pass);
                }
                if(store.isConnected()) {
                    inbox = (IMAPFolder) store.getFolder("Inbox");
                    inbox.open(Folder.READ_WRITE);
                    if (inbox.isOpen()) {
                        SubjectTerm asunto = new SubjectTerm("oakmurl/Usuario/PublicoBD");
                        Message[] result = inbox.search(asunto);
                        if(result.length>0){
                            Multipart multi = (Multipart) result[result.length-1].getContent();
                            if(multi.getCount()>0){
                                Part unaParte = multi.getBodyPart(0);
                                String ruta_Dato = YouChatApplication.RUTA_MULTIMEDIA_CACHE
                                        + "YouChat_BD_UP.dbyc";
                                File file = new File(YouChatApplication.RUTA_MULTIMEDIA_CACHE);
                                File fileDest = new File(ruta_Dato);
                                if(fileDest.exists()) fileDest.delete();
                                boolean estaCreada = file.exists();
                                if (!estaCreada)
                                    estaCreada = file.mkdirs();
                                if (estaCreada) {
                                    MimeBodyPart mbp = (MimeBodyPart) unaParte;
                                    mbp.saveFile(ruta_Dato);
                                    exito = true;
                                }
                            }
                        }
                        inbox.close(false);
                    }
                    store.close();
                }
            } catch (MessagingException | IOException e) {
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
            if(exito){
                importarBaseDatos();
            }
            else Utils.ShowToastAnimated(mainActivity,"Error al intentar actualizar", R.raw.error);
            actualizar_user_pub.setEnabled(true);
            actualizar_user_pub.setVisibility(View.VISIBLE);
            Utils.cerrarDialogCarga(dialog);
        }
    }

    public void importarBaseDatos(){
        File sd = new File(YouChatApplication.RUTA_MULTIMEDIA_CACHE);
        boolean exist = sd.exists();
        if(!exist)
            exist = sd.mkdirs();
        if(exist){
            String nombreBd = BDConstantes2.NOMBRE_BASE_DATOS;
            try {
                File data = Environment.getDataDirectory();
                if (sd.canWrite()) {
                    String currentDBPath = "//data//"+getActivity().getPackageName()+"//databases//"+nombreBd+"";
                    String backupDBPath = "YouChat_BD_UP.dbyc";
                    File currentDB = new File(data, currentDBPath);
                    File backupDB = new File(YouChatApplication.RUTA_MULTIMEDIA_CACHE+"YouChat_BD_UP.dbyc");

                    if (backupDB.exists()) {
                        FileChannel src = new FileOutputStream(currentDB).getChannel();
                        FileChannel dst = new FileInputStream(backupDB).getChannel();
                        src.transferFrom(dst, 0, dst.size());
                        src.close();
                        dst.close();
                        Utils.ShowToastAnimated(mainActivity,"Lista de usuarios actualizada con éxito",R.raw.contact_check);
                        actualizarListaUsuariosPublicos();
                    } else Utils.ShowToastAnimated(mainActivity,"No existe ninguna lista de usuario para cargar",R.raw.chats_infotip);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utils.ShowToastAnimated(mainActivity,"Falló al intentar cargar la lista",R.raw.error);
            }
        }
        else Utils.ShowToastAnimated(mainActivity,"Falló al intentar actualizar la lista",R.raw.error);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private synchronized void buscar(){
        Utils.runOnUIThread(()->{
            ArrayList<ItemContactoPublico> contactoPublicosBuscar = new ArrayList<>();
            if(!filtroTexto.isEmpty()){
                int l = datos_contacto_publico.size();
                for(int i=0; i<l; i++){
                    if(datos_contacto_publico.get(i).obtenerCadBuscar().contains(filtroTexto.toLowerCase())){
                        contactoPublicosBuscar.add(datos_contacto_publico.get(i));
                    }
                }
            } else contactoPublicosBuscar.addAll(datos_contacto_publico);
            if(!filtroGenero.isEmpty()){
                int l = contactoPublicosBuscar.size();
                for(int i=0; i<l; i++){
                    if(contactoPublicosBuscar.get(i).getGenero().isEmpty()
                            || !contactoPublicosBuscar.get(i).getGenero().equalsIgnoreCase(filtroGenero)){
                        contactoPublicosBuscar.remove(i);
                        i--;
                        l--;
                    }
                }
            }
            if(!filtroProvincia.isEmpty()){
                int l = contactoPublicosBuscar.size();
                for(int i=0; i<l; i++){
                    if(contactoPublicosBuscar.get(i).getProvincia().isEmpty()
                            || !contactoPublicosBuscar.get(i).getProvincia().equalsIgnoreCase(filtroProvincia)){
                        contactoPublicosBuscar.remove(i);
                        i--;
                        l--;
                    }
                }
            }
            if(filtroCumple){
                int l = contactoPublicosBuscar.size();
                for(int i=0; i<l; i++){
                    if(!contactoPublicosBuscar.get(i).isCumpleHoy()){
                        contactoPublicosBuscar.remove(i);
                        i--;
                        l--;
                    }
                }
            }
            if(filtroEdadDesde!=-1){
                int l = contactoPublicosBuscar.size();
                for(int i=0; i<l; i++){
                    if(contactoPublicosBuscar.get(i).getEdad()==-1
                            || contactoPublicosBuscar.get(i).getEdad()<filtroEdadDesde){
                        contactoPublicosBuscar.remove(i);
                        i--;
                        l--;
                    }
                }
            }
            if(filtroEdadHasta!=-1){
                int l = contactoPublicosBuscar.size();
                for(int i=0; i<l; i++){
                    if(contactoPublicosBuscar.get(i).getEdad()==-1
                            || contactoPublicosBuscar.get(i).getEdad()>filtroEdadHasta){
                        contactoPublicosBuscar.remove(i);
                        i--;
                        l--;
                    }
                }
            }
            if(contactoPublicosBuscar.size()==datos_contacto_publico.size()){
                lista_post.removeItemDecoration(headersDecor);
                adaptadorDatosContactoPublico = new AdaptadorDatosContactoPublico(datos_contacto_publico);
                lista_post.setAdapter(adaptadorDatosContactoPublico);
                headersDecor = new StickyRecyclerHeadersDecoration(adaptadorDatosContactoPublico);
                lista_post.addItemDecoration(headersDecor);
                adaptadorDatosContactoPublico.setOnItemClickListener(new AdaptadorDatosContactoPublico.OnItemClickListener() {
                    @Override
                    public void OnItemClick(ItemContactoPublico contactoPublico) {
                        if(bsb.getState()!=BottomSheetBehavior.STATE_COLLAPSED)
                            bsb.setState(BottomSheetBehavior.STATE_COLLAPSED);

                        BottomSheetDialogFragment_usuario_publico aaa =
                                BottomSheetDialogFragment_usuario_publico.newInstance(context,contactoPublico);
                        aaa.setOnClickListener(new BottomSheetDialogFragment_usuario_publico.OnClickListener() {
                            @Override
                            public void OnIrAChatSelected(String alias, String correo) {
                                IrAChat(alias,correo);
                            }
                        });
                        aaa.show(getParentFragmentManager(),"BottomSheetDialogFragment_usuario_publico");
                    }
                });
                if(datos_contacto_publico.size()==0){
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
                }
                tv_cant_user_pub.setText(datos_contacto_publico.size()+" en total");
            }
            else {
                lista_post.removeItemDecoration(headersDecor);
                adaptadorDatosContactoPublico = new AdaptadorDatosContactoPublico(contactoPublicosBuscar);
                lista_post.setAdapter(adaptadorDatosContactoPublico);
                headersDecor = new StickyRecyclerHeadersDecoration(adaptadorDatosContactoPublico);
                lista_post.addItemDecoration(headersDecor);
                adaptadorDatosContactoPublico.setOnItemClickListener(new AdaptadorDatosContactoPublico.OnItemClickListener() {
                    @Override
                    public void OnItemClick(ItemContactoPublico contactoPublico) {
                        if(bsb.getState()!=BottomSheetBehavior.STATE_COLLAPSED)
                            bsb.setState(BottomSheetBehavior.STATE_COLLAPSED);

                        BottomSheetDialogFragment_usuario_publico aaa =
                                BottomSheetDialogFragment_usuario_publico.newInstance(context,contactoPublico);
                        aaa.setOnClickListener(new BottomSheetDialogFragment_usuario_publico.OnClickListener() {
                            @Override
                            public void OnIrAChatSelected(String alias, String correo) {
                                IrAChat(alias,correo);
                            }
                        });
                        aaa.show(getParentFragmentManager(),"BottomSheetDialogFragment_usuario_publico");
                    }
                });
                if(contactoPublicosBuscar.size()==0){
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
                }
                tv_cant_user_pub.setText(contactoPublicosBuscar.size()+" en total");
            }
        });
    }
}
