package cu.alexgi.youchat;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import cu.alexgi.youchat.FileExplorer.SimpleFileExplorerActivity;
import cu.alexgi.youchat.FileExplorer.SimpleFileExplorerFragment;
import cu.alexgi.youchat.adapters.AdaptadorDatosAdjuntoCorreoEnviar;
import cu.alexgi.youchat.items.ItemAdjuntoCorreo;
import cu.alexgi.youchat.items.ItemChat;
import cu.alexgi.youchat.items.ItemMensajeCorreo;
import cu.alexgi.youchat.items.ItemUsuarioCorreo;
import cu.alexgi.youchat.swipebackfragment.BaseSwipeBackFragment;
import cu.alexgi.youchat.views_GI.EditTextFontGenGI;
import cu.alexgi.youchat.views_GI.TextViewFontGenGI;

import static android.app.Activity.RESULT_OK;
import static cu.alexgi.youchat.MainActivity.context;
import static cu.alexgi.youchat.MainActivity.dbWorker;
import static cu.alexgi.youchat.MainActivity.mainActivity;
import static cu.alexgi.youchat.MainActivity.permisos;

public class NuevoMensajeCorreoFragment extends BaseSwipeBackFragment{

    private RecyclerView list_adjuntos;
    private ArrayList<ItemAdjuntoCorreo> datos_adjunto;
    private AdaptadorDatosAdjuntoCorreoEnviar adaptadorDatosAdjuntoCorreo;

    private long pesoLimiteMax = 22020096;
    private ArrayList<MimeBodyPart> adjuntos;
    private View efab_add_adjunto, enviar_correo;

    private EditTextFontGenGI tv_nc_asunto, tv_contenido;

    private String idMensaje, hora, fecha, orden;
    private static String usuario, correo;
    private static boolean esResponder, esReenviar;
    private static ItemMensajeCorreo mensajeCorreo;

    public NuevoMensajeCorreoFragment() {
    }

    public static NuevoMensajeCorreoFragment newInstance(String usu, String cor) {
        NuevoMensajeCorreoFragment fragment = new NuevoMensajeCorreoFragment();
        usuario = usu;
        correo = cor;
        mensajeCorreo = null;
        esResponder = esReenviar = false;
        return fragment;
    }

    public static NuevoMensajeCorreoFragment newInstance(String usu, String cor,
                                                         int tipo, ItemMensajeCorreo mc) {
        NuevoMensajeCorreoFragment fragment = new NuevoMensajeCorreoFragment();
        usuario = usu;
        correo = cor;
        mensajeCorreo = mc;
        esResponder = esReenviar = false;
        if(tipo==1) //responder
            esResponder = true;
        else if(tipo==2) //reenviar
            esReenviar = true;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return attachToSwipeBack(inflater.inflate(R.layout.fragment_nuevo_mensaje_correo, container, false));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        enviar_correo = view.findViewById(R.id.enviar_correo);
        efab_add_adjunto = view.findViewById(R.id.efab_add_adjunto);
        tv_contenido = view.findViewById(R.id.tv_contenido);
        tv_nc_asunto = view.findViewById(R.id.tv_nc_asunto);
        adjuntos = new ArrayList<>();

        orden = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault()).format(new Date());
        hora = Convertidor.conversionHora(orden);
        fecha = Convertidor.conversionFecha(orden);
        idMensaje = YouChatApplication.correo + "" + orden;

        view.findViewById(R.id.root).setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_interior()));
        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.atrasFragment();
            }
        });

        efab_add_adjunto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarArchivo();
            }
        });

        enviar_correo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    enviarCorreo();
                } catch (MessagingException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                    Utils.ShowToastAnimated(mainActivity,"Ha ocurrido un error", R.raw.error);
                }
            }
        });



        ((TextViewFontGenGI)view.findViewById(R.id.tv_nc_de))
                .setText(YouChatApplication.correo);
        ((TextViewFontGenGI)view.findViewById(R.id.tv_nc_para))
                .setText(correo);

        list_adjuntos = view.findViewById(R.id.list_adjuntos);
        list_adjuntos.setLayoutManager(new LinearLayoutManager(context,RecyclerView.VERTICAL,false));

        datos_adjunto = new ArrayList<>();
        adaptadorDatosAdjuntoCorreo =
                new AdaptadorDatosAdjuntoCorreoEnviar(context, datos_adjunto, this);
        list_adjuntos.setAdapter(adaptadorDatosAdjuntoCorreo);

        if(esResponder){
            tv_nc_asunto.setText("Re: "+mensajeCorreo.getAsunto());
            String cad = "";
            cad += "\n\nRespondido de:\n";
            cad += "De: " + mensajeCorreo.getRemitente() + "\n";
            cad += "Para: " + mensajeCorreo.getDestinatario() + "\n";
            cad += "Asunto: " + mensajeCorreo.getAsunto() + "\n";
            cad += "Fecha de envío: " + Convertidor.convertirFechaAFechaLinda(mensajeCorreo.getFecha())
                    + ", " + mensajeCorreo.getHora() + "\n";
            cad += "Contenido:\n" + mensajeCorreo.getTexto();
            if(!YouChatApplication.pieDeFirma.isEmpty())
                tv_contenido.setText("\n\n"+YouChatApplication.pieDeFirma+cad);
            else tv_contenido.setText(cad);
        }
        else if(esReenviar){
            tv_nc_asunto.setText(mensajeCorreo.getAsunto());
            tv_contenido.setText(tv_contenido.getText()+mensajeCorreo.getTexto());
        }
        else if(!YouChatApplication.pieDeFirma.isEmpty())
            tv_contenido.setText("\n\n"+YouChatApplication.pieDeFirma);
    }

    private void enviarCorreo() throws MessagingException, UnsupportedEncodingException {
        String cadContenido = tv_contenido.getText().toString().trim();
        if(cadContenido.isEmpty() && datos_adjunto.size()==0)
            Utils.ShowToastAnimated(mainActivity,"Debe escribir o adjuntar algo", R.raw.error);
        else {
            if(YouChatApplication.estaAndandoChatService()
                    && YouChatApplication.chatService.hayConex){
                Message message = new MimeMessage(YouChatApplication.chatService.getSession());
                int tamMsg = 0, tt=0;
                InternetAddress from;
                String alias = YouChatApplication.alias;
                alias = alias.replace("<","").replace(">","");
                if(!alias.isEmpty()){
                    from = new InternetAddress(YouChatApplication.correo, alias);
                }
                else {
                    from = new InternetAddress(YouChatApplication.correo);
                }
                message.setFrom(from);
                message.addHeader("Chat-Version","1.0");
                message.addHeader("YouChat-Version","1");

                String idGroupUsuarioCorreo = dbWorker.obtenerIdGroupUsuarioCorreo(correo);
                if(idGroupUsuarioCorreo!=null && !idGroupUsuarioCorreo.isEmpty())
                    message.addHeader("Chat-Group-ID",idGroupUsuarioCorreo);

                String[] direcciones_string = correo.split(",");
                int l=direcciones_string.length;
                if(l>1){
                    boolean esNauta = YouChatApplication.correo.endsWith("@nauta.cu"), huboCambio = false;
                    int cont = 0;
                    InternetAddress[] direcciones = new InternetAddress[l];
                    for(int i=0; i<l; i++){
                        if(esNauta && cont>=20) break;
                        if(!direcciones_string[i].equals(YouChatApplication.correo)){
                            direcciones[cont++] = new InternetAddress(direcciones_string[i]);
                        }
                        else huboCambio = true;
                    }
                    if(huboCambio){
                        InternetAddress[] direcciones2 = new InternetAddress[cont];
                        for(int i=0; i<cont; i++){
                            direcciones2[i] = direcciones[i];
                        }
                        message.addRecipients(Message.RecipientType.TO, direcciones2);
                    }
                    else message.addRecipients(Message.RecipientType.TO, direcciones);
                }
                else message.addRecipient(Message.RecipientType.TO, new InternetAddress(direcciones_string[0]));

                String asunto = tv_nc_asunto.getText().toString().trim();
                if(asunto!=null && !asunto.isEmpty())
                    message.setSubject(asunto);
                else{
                    asunto = "";
                    message.setSubject("");
                }

                if(adjuntos.size()==0){
                    message.setText(cadContenido);
                    tamMsg+=cadContenido.length();
                }else {
                    BodyPart texto = new MimeBodyPart();
                    texto.setText(cadContenido);
                    tamMsg += cadContenido.length();
                    MimeMultipart multiParte = new MimeMultipart();
                    multiParte.addBodyPart(texto);

                    int lAdj = adjuntos.size();
                    for(int i=0; i<lAdj; i++){
                        tt+=(int)adjuntos.get(i).getSize();
                        multiParte.addBodyPart(adjuntos.get(i));
                    }
                    message.setContent(multiParte);
                }

                tamMsg = (tamMsg*8)+tt;

                if(message!=null){

                    ItemMensajeCorreo newCorreo = new ItemMensajeCorreo(idMensaje,
                            0L, true, false,
                            false, correo, YouChatApplication.correo, YouChatApplication.alias,
                            correo, asunto, cadContenido, ItemChat.ESTADO_ESPERANDO,
                            false, tamMsg, hora, fecha, orden);
                    dbWorker.insertarNuevoMensajeCorreo(newCorreo);
                    dbWorker.insertarUsuarioCorreo(new ItemUsuarioCorreo(newCorreo), true);

                    int lAdj = datos_adjunto.size();
                    for(int i=0; i<lAdj; i++){
                        dbWorker.insertarNuevoAdjuntoCorreo(datos_adjunto.get(i));
                    }

                    ItemChat temp = new ItemChat(correo,"");
                    temp.setId(idMensaje);
                    YouChatApplication.chatService.enviarMensajePersonalizado(temp,
                            SendMsg.CATEGORY_CHAT_CORREO_PERSONALIZADO,
                            message,tamMsg);

                    if(YouChatApplication.bandejaFragment!=null
                            && YouChatApplication.posVistaBandeja==3)
                        YouChatApplication.bandejaFragment.addNewCorreo(newCorreo,true);
                    mainActivity.atrasFragment();
                }
            }
            else Utils.mostrarToastDeConexion(mainActivity);

        }
    }

    public void buscarArchivo() {
        if(!permisos.requestPermissionAlmacenamiento()) return;

        Intent intent = new Intent(context, SimpleFileExplorerActivity.class);
        startActivityForResult(intent, 71);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode == 71){//archivos
                if(data != null){
                    String rutaFile = data.getStringExtra(SimpleFileExplorerActivity.ON_ACTIVITY_RESULT_KEY);
                    if(rutaFile!=null && !rutaFile.equals("")){
                        File file = new File(rutaFile);
                        if(file.exists()){
                            if(file.length()<=pesoLimiteMax){
                                adjuntarArchivo(rutaFile);
                            }
                            else Utils.ShowToastAnimated(mainActivity,"El tamaño máximo excede la capacidad soportada (20mb)",R.raw.contact_check);
                        }
                        else Utils.ShowToastAnimated(mainActivity,"El archivo no existe",R.raw.error);
                    }
                    else Utils.ShowToastAnimated(mainActivity,"Error al cargar el archivo",R.raw.error);
                }
            }
        }
    }

    private void adjuntarArchivo(String rutaFile) {
        try {
            MimeBodyPart adjunto= new MimeBodyPart();
            File file=new File(rutaFile);
            adjunto.setDataHandler(new DataHandler(new FileDataSource(file)));
            adjunto.setFileName(file.getName());
            adjunto.setDisposition(Part.ATTACHMENT);

            String orden = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault()).format(new Date());

            String nombrePart = file.getName();
            if (nombrePart == null || nombrePart.isEmpty())
                nombrePart = "adjunto" + orden +".sinExt";
            ItemAdjuntoCorreo adj = new ItemAdjuntoCorreo(nombrePart + orden, idMensaje, correo, -1,
                    rutaFile,
                    Utils.obtenerTipoDadounaExtension(Utils.obtenerExtension(nombrePart)),
                    (int)file.length());
            datos_adjunto.add(adj);
            adaptadorDatosAdjuntoCorreo.notifyItemInserted(datos_adjunto.size()-1);
            adjuntos.add(adjunto);
            pesoLimiteMax -= file.length();

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void quitarAdjunto(String id) {
        int l = datos_adjunto.size();
        for(int i=0; i<l; i++){
            if(datos_adjunto.get(i).getId().equals(id)){
                pesoLimiteMax += datos_adjunto.get(i).getPeso();
                adjuntos.remove(i);
                datos_adjunto.remove(i);
                adaptadorDatosAdjuntoCorreo.notifyItemRemoved(i);
                break;
            }
        }
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
                    mAddFragmentListener.onAddFragment(NuevoMensajeCorreoFragment.this,
                            Web_view_fragment.newInstance(rutaArchivo));
            }
            else if(tipo.equals("image/*") || tipo.equals("image/gif")){
                if(mAddFragmentListener!=null)
                    mAddFragmentListener.onAddFragment(NuevoMensajeCorreoFragment.this,
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
}