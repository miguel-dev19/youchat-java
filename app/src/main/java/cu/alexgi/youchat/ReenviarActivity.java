package cu.alexgi.youchat;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import cu.alexgi.youchat.adapters.AdaptadorDatosUsuarioReenviar;
import cu.alexgi.youchat.items.ItemChat;
import cu.alexgi.youchat.items.ItemUsuario;
import cu.alexgi.youchat.photoutil.ImageLoader;
import cu.alexgi.youchat.swipebackfragment.BaseSwipeBackFragment;
import cu.alexgi.youchat.views_GI.TextViewBarGI;

import static cu.alexgi.youchat.MainActivity.context;
import static cu.alexgi.youchat.MainActivity.dbWorker;
import static cu.alexgi.youchat.MainActivity.mainActivity;

public class ReenviarActivity extends BaseSwipeBackFragment {
//    private NavController navController;
    private ArrayList<ItemUsuario> datos_Usuario_reenviar;
    private RecyclerView lista_usuario_reenviar;
    private AdaptadorDatosUsuarioReenviar adaptadorUsuario_reenviar;
    private FloatingActionButton fab_reenviar;
    private TextView cant_selec_reenviar;
    static boolean[] pos_seleccionadas;
    static int cant_seleccionados, total, colorTema;
    private TextViewBarGI reenvAct_titulo;

    private static Bundle mibundle;
    private static boolean esParaReenviar;
    private static ArrayList<ItemChat> chatsReenviar;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        YouChatApplication.reenviarActivity = null;
    }

    public static ReenviarActivity newInstance(Bundle bundle, boolean esReenviar) {
        ReenviarActivity fragment = new ReenviarActivity();
        mibundle = bundle;
        chatsReenviar = new ArrayList<>();
        esParaReenviar = esReenviar;
        return fragment;
    }

    public static ReenviarActivity newInstance(ArrayList<ItemChat> chats, boolean esReenviar) {
        ReenviarActivity fragment = new ReenviarActivity();
        mibundle = null;
        chatsReenviar = chats;
        esParaReenviar = esReenviar;
        return fragment;
    }

    public static ReenviarActivity newInstance(ItemChat chat, boolean esReenviar) {
        ReenviarActivity fragment = new ReenviarActivity();
        mibundle = null;
        chatsReenviar = new ArrayList<>();
        chatsReenviar.add(chat);
        esParaReenviar = esReenviar;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return attachToSwipeBack(inflater.inflate(R.layout.activity_reenviar, container, false));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        YouChatApplication.reenviarActivity = this;

        view.findViewById(R.id.root).setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_fondo()));
        view.findViewById(R.id.atras_reenviar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        if(!esParaReenviar){
            reenvAct_titulo = view.findViewById(R.id.reenvAct_titulo);
            reenvAct_titulo.setText("Compartir a...");
        }

//        navController = Navigation.findNavController(view);
        lista_usuario_reenviar = view.findViewById(R.id.rv_selecc_list);
        lista_usuario_reenviar.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
//        datos_Usuario_reenviar
//        datos_Usuario_reenviar = YouChatApplication.datos_Usuario_act;
//        if(datos_Usuario_reenviar==null)
        datos_Usuario_reenviar = dbWorker.obtenerUsuarios();
        int l=datos_Usuario_reenviar.size();
        int cont=0;
        for(int i=0; i<l; i++){
            if(datos_Usuario_reenviar.get(i).EsAnclado()){
                ItemUsuario temp = datos_Usuario_reenviar.get(i);
                datos_Usuario_reenviar.remove(i);
                datos_Usuario_reenviar.add(cont,temp);
                cont++;
            }
        }
        adaptadorUsuario_reenviar = new AdaptadorDatosUsuarioReenviar(context, datos_Usuario_reenviar);
        lista_usuario_reenviar.setAdapter(adaptadorUsuario_reenviar);
        AsignarEventosAlAdaptador();

        cant_selec_reenviar= view.findViewById(R.id.cant_selec_reenviar);

//        chatsReenviar = new ArrayList<>();
        if(mibundle!=null)
            comprobarIntent(mibundle);
        //navController.navigateUp();
        if(chatsReenviar.size()>0) {
            fab_reenviar = view.findViewById(R.id.fab_reenviar);
            fab_reenviar.hide();
            fab_reenviar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(chatsReenviar.size()>0){
                        fab_reenviar.setEnabled(false);
                        Utils.runOnUIThread(()->{
                            int n = chatsReenviar.size();
                            for(int j=0; j<n; j++){
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
                                Date date = new Date();
                                String fechaEntera = sdf.format(date);
                                String hora = conversionHora(fechaEntera);
                                String fecha = conversionFecha(fechaEntera);
                                int l=datos_Usuario_reenviar.size();
                                for (int i=0; i<l; i++)
                                    if(pos_seleccionadas[i])
                                        enviarMensajeA(datos_Usuario_reenviar.get(i).getCorreo(),
                                                fechaEntera, hora, fecha, chatsReenviar.get(j));
                            }
                        });
                        atras();
//                        Esperar(cant_seleccionados);
                    }
                }
            });
            pos_seleccionadas = new boolean[l];
            for (int i=0; i<l; i++) pos_seleccionadas[i]=false;
            cant_seleccionados = 0;
            if(l<5) total=l;
            else total=5;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.runOnUIThread(()->{
            if(chatsReenviar.size()==0) mainActivity.atrasFragment();
        });

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void comprobarIntent(Bundle bundle){
        try {
            if(bundle==null) return;
            String action=bundle.getString("key","");
            if(action.equals(Intent.ACTION_SEND)){
                Intent intent = YouChatApplication.intentReenviar;
//                String type=intent.getString("Type");
                String type = intent.getType();
                Log.e("reenviar","1 "+type);
                if(type==null || type.equals("")) return;
                else if(type.contains("text/plain") || type.contains("message/rfc822")){
                    Log.e("reenviar","2");
//                    String cad=intent.getString("StringExtra");
                    String cad=intent.getStringExtra(Intent.EXTRA_TEXT);
                    if(cad!=null && !cad.trim().equals("")){
                        Log.e("reenviar","3");
//                        return new ItemChat("",2,"",cad);
                        chatsReenviar.add(new ItemChat("",2,"",cad));
                        return;
                    }
                    else return;
                }
                else if(type.contains("image/*")){
                    Log.e("reenviar","4");
                    String cad=intent.getStringExtra(Intent.EXTRA_TEXT);
                    if(cad==null) cad="";
                    Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                    if(imageUri != null){
                        Log.e("reenviar","5 "+imageUri.getPath());
                        ItemChat msg = new ItemChat("",4,"",cad);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                        Date date = new Date();
                        String fechaEntera = sdf.format(date);
                        String nombre_img="img"+fechaEntera+".jpg";
                        String rutaImg =YouChatApplication.RUTA_IMAGENES_ENVIADAS+nombre_img;
                        String origen;
                        if(new File(imageUri.getPath()).exists()) origen=imageUri.getPath();
                        else origen = Utils.getImageFromUri(context,imageUri);

                        if(!new File(origen).exists() || !Utils.esImagen(origen)){
                            if(Utils.SavePhotoUri(context,imageUri,rutaImg)){
                                origen = rutaImg;
                                nombre_img="img"+fechaEntera+"2.jpg";
                                rutaImg =YouChatApplication.RUTA_IMAGENES_ENVIADAS+nombre_img;
                                try {
                                    ImageLoader.init().comprimirImagen(origen,rutaImg,YouChatApplication.calidad);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                                finally {
                                    msg.setRuta_Dato(rutaImg);
                                    chatsReenviar.add(msg);
                                }
                            }
                            else if(Utils.SavePhotoUri2(context,imageUri,rutaImg)){
                                origen = rutaImg;
                                nombre_img="img"+fechaEntera+"2.jpg";
                                rutaImg =YouChatApplication.RUTA_IMAGENES_ENVIADAS+nombre_img;
                                try {
                                    ImageLoader.init().comprimirImagen(origen,rutaImg,YouChatApplication.calidad);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                                finally {
                                    msg.setRuta_Dato(rutaImg);
                                    chatsReenviar.add(msg);
                                }
                            }
                        }
                        else {
                            try {
                                ImageLoader.init().comprimirImagen(origen,rutaImg,YouChatApplication.calidad);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            finally {
                                msg.setRuta_Dato(rutaImg);
                                chatsReenviar.add(msg);
                            }
                        }
                        return;
                    }
                    else return;
                }
                else return;
            }
            else return;
        }catch(Exception e) {
            e.printStackTrace();
        }
        return;
    }

    public void enviarMensajeA(String correo, String fechaEntera, String hora, String fecha, ItemChat chatReenviar){
        String id="YouChat/chat/"+correo+"/0/"+fechaEntera;
        int tipo = chatReenviar.getTipo_mensaje();
        if(tipo%2==1) tipo++;
        if(tipo==6 || tipo==18) tipo=2;
        else if(tipo==10) tipo=8;
        ItemChat newChat;
        if(tipo==12 || tipo==16){
            newChat=new ItemChat(id,
                    tipo,
                    1,
                    correo,
                    chatReenviar.getMensaje(),
                    "",
                    hora,
                    fecha,
                    chatReenviar.getId_msg_resp(),
                    YouChatApplication.correo,
                    esParaReenviar,
                    fechaEntera,false
                    ,"",0,true);
        }
        else {
            newChat=new ItemChat(id,
                    tipo,
                    1,
                    correo,
                    chatReenviar.getMensaje(),
                    chatReenviar.getRuta_Dato(),
                    hora,
                    fecha,
                    "",
                    YouChatApplication.correo,
                    esParaReenviar,
                    fechaEntera,false
                    ,"",0,true);
        }
        dbWorker.insertarChat(newChat);
        dbWorker.actualizarUltMsgUsuario(newChat);
        if (YouChatApplication.chatsActivity != null &&
                correo.equals(YouChatApplication.chatsActivity.getCorreo())) {
            Utils.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    YouChatApplication.chatsActivity.ActualizarChatMsgRecibido(newChat);
                }
            });
        }
        if (YouChatApplication.principalActivity != null) {
            Utils.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    YouChatApplication.principalActivity
                            .actualizarNewMsg(correo,0);
                }
            });
        }
        if(YouChatApplication.estaAndandoChatService())
            YouChatApplication.chatService.enviarMensaje(newChat,SendMsg.CATEGORY_CHAT);
    }

    public static String getCad() {
        return "ÅßÉÂËÞ";
    }

    private String conversionFecha(String fecha) {
        //0 1 2 3 4 5 6 7 8 9 0 1 2 3 4
        //y y y y M M d d H H m m s s
        String dd=fecha.charAt(6)+""+fecha.charAt(7);
        String mm=fecha.charAt(4)+""+fecha.charAt(5);
        String aa=fecha.charAt(0)+""+fecha.charAt(1)+""+fecha.charAt(2)+""+fecha.charAt(3);
        return dd+"/"+mm+"/"+aa;
    }

    private String conversionHora(String fecha) {
        //0 1 2 3 4 5 6 7 8 9 0 1 2 3 4
        //y y y y M M d d H H m m s s
        int h = (fecha.charAt(8)-48)*10+(fecha.charAt(9)-48);
        String m = fecha.charAt(10)+""+fecha.charAt(11);
        if(h==0) return "12:"+m+" am";
        else if(h>12) return (h-12)+":"+m+" pm";
        else if(h==12) return "12:"+m+" pm";
        return h+":"+m+" am";
    }

    public void AsignarEventosAlAdaptador(){
        adaptadorUsuario_reenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    int pos_item_seleccionado = lista_usuario_reenviar.getChildAdapterPosition(v);
                    if(pos_seleccionadas[pos_item_seleccionado]){
                        pos_seleccionadas[pos_item_seleccionado] = false;
                        cant_seleccionados--;
                        cant_selec_reenviar.setText(cant_seleccionados+"/"+total);
                        if(cant_seleccionados==0 && cant_selec_reenviar.getVisibility()!=View.GONE){
                            cant_selec_reenviar.setVisibility(View.GONE);
                            fab_reenviar.hide();
                        }

                        int pos=lista_usuario_reenviar.getChildAdapterPosition(v);
                        datos_Usuario_reenviar.get(pos).setEstaSeleccionado(false);
                        AdaptadorDatosUsuarioReenviar.ViewHolderDatos viewItemSeleccionado
                                = (AdaptadorDatosUsuarioReenviar.ViewHolderDatos) lista_usuario_reenviar.getChildViewHolder(v);
                        viewItemSeleccionado.seleccionar(false);
                    }
                    else if(cant_seleccionados<total){
                        pos_seleccionadas[pos_item_seleccionado] = true;
                        cant_seleccionados++;
                        cant_selec_reenviar.setText(cant_seleccionados+"/"+total);
                        if(cant_seleccionados!=0 && cant_selec_reenviar.getVisibility()!=View.VISIBLE){
                            cant_selec_reenviar.setVisibility(View.VISIBLE);
                            fab_reenviar.show();
                        }

                        int pos=lista_usuario_reenviar.getChildAdapterPosition(v);
                        datos_Usuario_reenviar.get(pos).setEstaSeleccionado(true);
                        AdaptadorDatosUsuarioReenviar.ViewHolderDatos viewItemSeleccionado
                                = (AdaptadorDatosUsuarioReenviar.ViewHolderDatos) lista_usuario_reenviar.getChildViewHolder(v);
                        viewItemSeleccionado.seleccionar(true);
                    }

            }
        });
    }

    public void hideFragment(){

    }

    public void atras() {
//        if(cant_seleccionados>0){
//            int l=datos_Usuario_reenviar.size();
//            for (int i=0; i<l; i++){
//                if(pos_seleccionadas[i]){
//                    datos_Usuario_reenviar.get(i).setEstaSeleccionado(false);
//                    adaptadorUsuario_reenviar.notifyItemChanged(i);
//                    try{
//                        AdaptadorDatosUsuarioReenviar.ViewHolderDatos viewItemSeleccionado
//                                = (AdaptadorDatosUsuarioReenviar.ViewHolderDatos) lista_usuario_reenviar.findViewHolderForAdapterPosition(i);
//                        if(viewItemSeleccionado!=null)
//                            viewItemSeleccionado.seleccionar(false);
//                    }catch (NullPointerException e){
//                        e.printStackTrace();
//                    }
//                    catch (Exception e){
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
        mainActivity.atrasFragment();
//        getActivity().onBackPressed();
//        navController.navigateUp();
    }

    void Esperar(int tiempo){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                atras();
            }
        },tiempo);
    }
}
