package cu.alexgi.youchat;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.search.SubjectTerm;

import cu.alexgi.youchat.base_datos.DBWorker;
import cu.alexgi.youchat.items.ItemChat;
import cu.alexgi.youchat.items.ItemComentarioPost;
import cu.alexgi.youchat.items.ItemContacto;
import cu.alexgi.youchat.items.ItemEstado;
import cu.alexgi.youchat.items.ItemPost;
import cu.alexgi.youchat.items.ItemUsuario;


public class IntentServiceEstPostGlobales extends IntentService {

    private static final String TAG = "IntentServiceEstPostGl";
    private DBWorker dbWorker;

    private String user, pass;

    public IntentServiceEstPostGlobales() {
        super("YouCHat IntentServiceEstPostGlobales");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        YouChatApplication.estaCorriendoPost = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        YouChatApplication.estaCorriendoPost = false;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(YouChatApplication.estaAndandoChatService()){
            dbWorker = new DBWorker(YouChatApplication.context);
            try {
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
                if(store.isConnected()){
                    inbox = (IMAPFolder)store.getFolder("Inbox");
                    inbox.open(Folder.READ_WRITE);
                    if (inbox.isOpen()){
                        SubjectTerm asunto = new SubjectTerm("hjrk/Estado/Global");
                        Message[] result = inbox.search(asunto);
                        if(result.length>0) procesarEstadosGlobales(result);
                        asunto = new SubjectTerm("kle/Chat/Global");
                        result = inbox.search(asunto);
                        if(result.length>0) procesarMensajesGlobales(result);
                        if(YouChatApplication.activePost){
                            int cantPost = dbWorker.obtenerCantTotalPosts();
                            if(!YouChatApplication.limitarPost
                                    || cantPost<YouChatApplication.cantLimitePost){
                                asunto = new SubjectTerm("ikmjkle/Post/Global");
                                result = inbox.search(asunto);
                                if(result.length>0) procesarPostGlobales(result, cantPost, inbox);
                                if(YouChatApplication.correo.equals("octaviog97@nauta.cu")
                                        || YouChatApplication.correo.equals("alexgi@nauta.cu")
                                        || YouChatApplication.correo.equals("niuvis2019@nauta.cu")){
                                    asunto = new SubjectTerm("ikmik/Post/Prueba");
                                    result = inbox.search(asunto);
                                    if(result.length>0) procesarPostGlobalesPrueba(result, cantPost, inbox);
                                }
                            }
                            if(YouChatApplication.principalActivity!=null){
                                Utils.runOnUIThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        YouChatApplication.principalActivity.detenerRefresh();
                                    }
                                });
                            }
                            asunto = new SubjectTerm("tttikamavel/ComentarioPost/Global");
                            result = inbox.search(asunto);
                            if(result.length>0) procesarComentariosPostGlobales(result);

                            asunto = new SubjectTerm("irfdt/Bloquear/"+YouChatApplication.correo);
                            result = inbox.search(asunto);
                            if(result.length>0){
                                String[] validador = result[0].getHeader(ItemChat.YOUCHAT);
                                if (validador != null) {
                                    YouChatApplication.setPuedeSubirPost(false);
                                }
                            } else if(!YouChatApplication.puedeSubirPost)
                                YouChatApplication.setPuedeSubirPost(true);
                        }
                        inbox.close(false);
                        store.close();
                    }
                }
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }

    private void procesarPostGlobales(Message[] result, int cantPost, IMAPFolder inbox){
        try{
            int l = result.length;
            ArrayList<Message> listaMensajes = new ArrayList<>();
            for(int i=l-1; i>=0; i--){
                String[] datoTemp;
                String stringTemp = "";
                Message currentMessage = result[i];
                if(currentMessage!=null){
                    datoTemp = currentMessage.getHeader(ItemChat.KEY_ID);
                    if (datoTemp != null)
                        stringTemp = datoTemp[0];
                    String idA = stringTemp;
                    if(!idA.equals("")){
                        if(!idA.equals(YouChatApplication.idUltPostGlobalRecibido)){
                            String[] validador = currentMessage.getHeader(ItemChat.YOUCHAT);
                            if (validador != null) {
                                listaMensajes.add(0,currentMessage);
                            }
                        } else break;
                    }
                }
            }
            l = listaMensajes.size();
            if(l>0){
                for(int i=0; i<l; i++){
                    if(!YouChatApplication.limitarPost
                            || cantPost<YouChatApplication.cantLimitePost){
                        String[] datoTemp;
                        String stringTemp = "";
                        Message currentMessage = listaMensajes.get(i);
                        datoTemp = currentMessage.getHeader(ItemChat.KEY_ID);
                        if (datoTemp != null)
                            stringTemp = datoTemp[0];
                        final String idA = stringTemp;
                        YouChatApplication.setIdUltPostGlobalRecibido(idA);

                        Address[] correos = currentMessage.getFrom();
                        if (correos != null)
                            stringTemp = correos[0].toString().trim();
                        if(stringTemp.equals("youchat@nauta.cu")
                                || stringTemp.equals("youchatoficial@gmail.com"))
                            stringTemp = YouChatApplication.idOficial;
                        final String correo = stringTemp;
                        stringTemp = "";

                        datoTemp = currentMessage.getHeader(ItemChat.KEY_ID_MSG_RESP);
                        if (datoTemp != null)
                            stringTemp = datoTemp[0];
                        String[] datos = stringTemp.split("<s,p>");
                        final int tipoUsu = Convertidor.createIntOfString(datos[0]);
                        final int icono = Convertidor.createIntOfString(datos[1]);

                        stringTemp =  "1";
                        datoTemp = currentMessage.getHeader(ItemChat.KEY_TIPO);
                        if (datoTemp != null)
                            stringTemp = datoTemp[0];
                        final int tipoPost = Convertidor.createIntOfString(stringTemp);

                        Date fmsg = currentMessage.getSentDate();
                        SimpleDateFormat fmsgc = new SimpleDateFormat("yyyyMMddHHmmss");
                        String horaReal = fmsgc.format(fmsg);
                        final String hora = Convertidor.conversionHora(horaReal);
                        final String fecha = Convertidor.conversionFecha(horaReal);
                        dbWorker.actualizarUltHoraFechaSinInsertarContactoDe(correo, hora, fecha);

                        if((correo.equals(YouChatApplication.idOficial)
                                || !dbWorker.existeBloqueadoPost(correo, false))
                                && ItemPost.esDeEstaVersion(tipoPost)){
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
                            Date date = new Date();
                            String orden = sdf.format(date);
                            boolean obtenerPost = true;
                            String horaHoy = Convertidor.conversionHora(orden);
                            int intHoraHoy = Convertidor.createIntOfStringHora(horaHoy);

                            Date datHoy = new Date(sdf.parse(sdf.format(new Date())).getTime());
                            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                            Date datEstado = new Date(format.parse(fecha).getTime());

                            long dif = Math.abs(datHoy.getTime() - datEstado.getTime()) / 86400000;
                            if (dif >= 1) {
                                if (dif == 1) {
                                    int intHoraEst = Convertidor.createIntOfStringHora(hora);
                                    if (intHoraHoy > intHoraEst) obtenerPost = false;
                                } else obtenerPost = false;
                            }
                            if (obtenerPost) {
                                int tamMsg = currentMessage.getSize();
                                String nombre = "", msg = "", ruta_Dato = "";
                                int peso_dato = 0;
                                long uid = 0;
                                if(tipoPost==1){
                                    String text = currentMessage.getContent().toString().trim();
                                    datos = text.split("<s,p>");
                                    nombre = datos[0];
                                    if(datos.length>=2)
                                        msg = datos[1];
                                }
                                else if(tipoPost==3 || tipoPost==4){
                                    String text = currentMessage.getContent().toString().trim();
                                    datos = text.split("<s,p>");
                                    nombre = datos[0];
                                    if(datos.length>=2)
                                        msg = datos[1];

                                    String[] tmp = currentMessage.getHeader(SendMsg.PACK_DATO);
                                    if(tmp!=null)
                                        ruta_Dato = tmp[0];
                                }
                                else if(tipoPost==2){
                                    Multipart multi;
                                    multi = (Multipart) currentMessage.getContent();
                                    int cant = multi.getCount();
                                    if(cant>0){
                                        Part unaParte = multi.getBodyPart(0);
                                        tamMsg = unaParte.getSize();
                                        String text = unaParte.getContent().toString().trim();
                                        datos = text.split("<s,p>");
                                        if(datos.length>0)
                                            nombre = datos[0];
                                        if(datos.length>=2)
                                            msg = datos[1];
                                    }
                                    if(cant>=2){
                                        Part unaParte = multi.getBodyPart(1);
                                        peso_dato = unaParte.getSize();
                                        uid = inbox.getUID(currentMessage);

                                        if(YouChatApplication.activePostDesImg){
                                            tamMsg += unaParte.getSize();
                                            String nombres_img = unaParte.getFileName();
                                            if(nombres_img==null || nombres_img.isEmpty())
                                                nombres_img = "img" + orden + ".jpg";
                                            ruta_Dato = YouChatApplication.RUTA_IMAGENES_RECIBIDA + nombres_img;

                                            File file = new File(YouChatApplication.RUTA_IMAGENES_RECIBIDA);
                                            boolean estaCreada = file.exists();
                                            if (!estaCreada)
                                                estaCreada = file.mkdirs();
                                            if (estaCreada) {
                                                MimeBodyPart mbp = (MimeBodyPart) unaParte;
                                                mbp.saveFile(ruta_Dato);
                                            }
                                        }
                                    }
                                }
                                ItemPost newPost = new ItemPost(idA,uid,tipoPost,nombre,correo,tipoUsu,icono,msg,true,ruta_Dato,peso_dato,hora,fecha,orden);
                                dbWorker.insertarNuevoPost(newPost);

                                if (YouChatApplication.burbuja_datos) {
                                    YouChatApplication.consumoBajada += tamMsg;
                                    Intent WIDGET = new Intent("ACTUALIZAR_WIDGET");
                                    LocalBroadcastManager.getInstance(YouChatApplication.chatService).sendBroadcast(WIDGET);
                                }
                                YouChatApplication.addCant_post_rye(1);
                                YouChatApplication.addMega_post_recibidos(tamMsg);
                                if(YouChatApplication.principalActivity!=null){
                                    Utils.runOnUIThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            YouChatApplication.principalActivity.ActualizarPosts(true, true, false);
                                        }
                                    });
                                }
                                cantPost++;
                            }
                        }
                    }
                    else break;
                }
            }
        }catch (MessagingException e){
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void procesarComentariosPostGlobales(Message[] result){
        try{
            int l = result.length;
            ArrayList<Message> listaMensajes = new ArrayList<>();
            for(int i=l-1; i>=0; i--){
                String[] datoTemp;
                String stringTemp = "";
                Message currentMessage = result[i];
                if(currentMessage!=null){
                    datoTemp = currentMessage.getHeader(ItemChat.KEY_ID);
                    if (datoTemp != null)
                        stringTemp = datoTemp[0];
                    String idA = stringTemp;
                    if(idA!=null && !idA.isEmpty()){
                        if(!idA.equals(YouChatApplication.idUltComentarioPostGlobalRecibido)){
                            String[] validador = currentMessage.getHeader(ItemChat.YOUCHAT);
                            if (validador != null) {
                                listaMensajes.add(0,currentMessage);
                            }
                        } else break;
                    }
                }
            }
            l = listaMensajes.size();
            if(l>0){
                for(int i=0; i<l; i++){
                    String[] datoTemp;
                    String stringTemp = "";
                    Message currentMessage = listaMensajes.get(i);
                    datoTemp = currentMessage.getHeader(ItemChat.KEY_ID);
                    if (datoTemp != null)
                        stringTemp = datoTemp[0];
                    final String idA = stringTemp;
                    YouChatApplication.setIdUltComentarioPostGlobalRecibido(idA);

                    Address[] correos = currentMessage.getFrom();
                    if (correos != null)
                        stringTemp = correos[0].toString().trim();
                    final String correo = stringTemp;

                    stringTemp =  "1";
                    datoTemp = currentMessage.getHeader(ItemChat.KEY_TIPO);
                    if (datoTemp != null)
                        stringTemp = datoTemp[0];
                    final int tipoPost = Convertidor.createIntOfString(stringTemp);

                    Date fmsg = currentMessage.getSentDate();
                    SimpleDateFormat fmsgc = new SimpleDateFormat("yyyyMMddHHmmss");
                    String horaReal = fmsgc.format(fmsg);
                    final String hora = Convertidor.conversionHora(horaReal);
                    final String fecha = Convertidor.conversionFecha(horaReal);
                    dbWorker.actualizarUltHoraFechaSinInsertarContactoDe(correo, hora, fecha);

                    if(!dbWorker.existeBloqueadoPost(correo, false)
                            && ItemComentarioPost.esDeEstaVersion(tipoPost)){
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
                        Date date = new Date();
                        String orden = sdf.format(date);
                        JSONObject jsonObject = null;
                        Log.e(TAG, "procesarComentariosPostGlobales: 1");
//                        if(tipoPost==1) {
                            String text = currentMessage.getContent().toString().trim();
                        Log.e(TAG, "procesarComentariosPostGlobales: 2");
                            try {
                                jsonObject = new JSONObject(text);
                                Log.e(TAG, "procesarComentariosPostGlobales: 3");
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e(TAG, "procesarComentariosPostGlobales: "+e.toString() );
                            }
//                        }
                        if (jsonObject!=null) {
                            Log.e(TAG, "procesarComentariosPostGlobales: 4");
                            String idPost =  null;
                            try {
                                idPost = jsonObject.getString("idPost");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if(idPost!=null && dbWorker.existePost(idPost)){
                                int tamMsg = currentMessage.getSize();
                                String nombre = "", mensaje = "";
                                int tipoUsu=0;
                                try {
                                    nombre = jsonObject.getString("alias");
                                    mensaje = jsonObject.getString("mensaje");
                                    tipoUsu = jsonObject.getInt("tipoUsu");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                ItemComentarioPost comentarioPost = new ItemComentarioPost(idA,idPost,tipoPost,
                                        nombre,correo,tipoUsu,1,
                                        mensaje,"",0,
                                        hora,fecha,orden);

                                dbWorker.insertarNuevoComentarioPost(comentarioPost);

                                if (YouChatApplication.burbuja_datos) {
                                    YouChatApplication.consumoBajada += tamMsg;
                                    Intent WIDGET = new Intent("ACTUALIZAR_WIDGET");
                                    LocalBroadcastManager.getInstance(YouChatApplication.chatService).sendBroadcast(WIDGET);
                                }
                                YouChatApplication.addCant_post_rye(1);
                                YouChatApplication.addMega_post_recibidos(tamMsg);
                                if(YouChatApplication.principalActivity!=null){
                                    Utils.runOnUIThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            YouChatApplication.principalActivity.ActualizarPosts(true, true, false);
                                        }
                                    });
                                }
                            }
                        }
                    }
                }
            }
        }catch (MessagingException e){
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void procesarPostGlobalesPrueba(Message[] result, int cantPost, IMAPFolder inbox){
        try{
            int l = result.length;
            ArrayList<Message> listaMensajes = new ArrayList<>();
            for(int i=l-1; i>=0; i--){
                String[] datoTemp;
                String stringTemp = "";
                Message currentMessage = result[i];
                if(currentMessage!=null){
                    datoTemp = currentMessage.getHeader(ItemChat.KEY_ID);
                    if (datoTemp != null)
                        stringTemp = datoTemp[0];
                    String idA = stringTemp;
                    if(!idA.equals("")){
                        if(!idA.equals(YouChatApplication.idUltPostGlobalRecibidoPrueba)){
                            String[] validador = currentMessage.getHeader(ItemChat.YOUCHAT);
                            if (validador != null) {
                                listaMensajes.add(0,currentMessage);
                            }
                        } else break;
                    }
                }
            }
            l = listaMensajes.size();
            if(l>0){
                for(int i=0; i<l; i++){
                    if(!YouChatApplication.limitarPost
                            || cantPost<YouChatApplication.cantLimitePost){
                        String[] datoTemp;
                        String stringTemp = "";
                        Message currentMessage = listaMensajes.get(i);
                        datoTemp = currentMessage.getHeader(ItemChat.KEY_ID);
                        if (datoTemp != null)
                            stringTemp = datoTemp[0];
                        final String idA = stringTemp;
                        YouChatApplication.setIdUltPostGlobalRecibidoPrueba(idA);

                        Address[] correos = currentMessage.getFrom();
                        if (correos != null)
                            stringTemp = correos[0].toString().trim();
                        if(stringTemp.equals("youchat@nauta.cu")
                                || stringTemp.equals("youchatoficial@gmail.com"))
                            stringTemp = YouChatApplication.idOficial;
                        final String correo = stringTemp;
                        stringTemp = "";

                        datoTemp = currentMessage.getHeader(ItemChat.KEY_ID_MSG_RESP);
                        if (datoTemp != null)
                            stringTemp = datoTemp[0];
                        String[] datos = stringTemp.split("<s,p>");
                        final int tipoUsu = Convertidor.createIntOfString(datos[0]);
                        final int icono = Convertidor.createIntOfString(datos[1]);

                        stringTemp =  "1";
                        datoTemp = currentMessage.getHeader(ItemChat.KEY_TIPO);
                        if (datoTemp != null)
                            stringTemp = datoTemp[0];
                        final int tipoPost = Convertidor.createIntOfString(stringTemp);

                        Date fmsg = currentMessage.getSentDate();
                        SimpleDateFormat fmsgc = new SimpleDateFormat("yyyyMMddHHmmss");
                        String horaReal = fmsgc.format(fmsg);
                        final String hora = Convertidor.conversionHora(horaReal);
                        final String fecha = Convertidor.conversionFecha(horaReal);
                        dbWorker.actualizarUltHoraFechaSinInsertarContactoDe(correo, hora, fecha);

                        if((correo.equals(YouChatApplication.idOficial)
                                || !dbWorker.existeBloqueadoPost(correo, false))
                                && ItemPost.esDeEstaVersion(tipoPost)){
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
                            Date date = new Date();
                            String orden = sdf.format(date);
                            boolean obtenerPost = true;
                            String horaHoy = Convertidor.conversionHora(orden);
                            int intHoraHoy = Convertidor.createIntOfStringHora(horaHoy);

                            Date datHoy = new Date(sdf.parse(sdf.format(new Date())).getTime());
                            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                            Date datEstado = new Date(format.parse(fecha).getTime());

                            long dif = Math.abs(datHoy.getTime() - datEstado.getTime()) / 86400000;
                            if (dif >= 1) {
                                if (dif == 1) {
                                    int intHoraEst = Convertidor.createIntOfStringHora(hora);
                                    if (intHoraHoy > intHoraEst) obtenerPost = false;
                                } else obtenerPost = false;
                            }
                            if (obtenerPost) {
                                int tamMsg = currentMessage.getSize();
                                String nombre = "", msg = "", ruta_Dato = "";
                                int peso_dato = 0;
                                long uid = 0;
                                if(tipoPost==1){
                                    String text = currentMessage.getContent().toString().trim();
                                    datos = text.split("<s,p>");
                                    nombre = datos[0];
                                    msg = datos[1];
                                }
                                else if(tipoPost==3 || tipoPost==4){
                                    String text = currentMessage.getContent().toString().trim();
                                    datos = text.split("<s,p>");
                                    nombre = datos[0];
                                    msg = datos[1];

                                    String[] tmp = currentMessage.getHeader(SendMsg.PACK_DATO);
                                    if(tmp!=null)
                                        ruta_Dato = tmp[0];
                                }
                                else if(tipoPost==2){
                                    Multipart multi;
                                    multi = (Multipart) currentMessage.getContent();
                                    int cant = multi.getCount();
                                    if(cant>0){
                                        Part unaParte = multi.getBodyPart(0);
                                        tamMsg = unaParte.getSize();
                                        String text = unaParte.getContent().toString().trim();
                                        datos = text.split("<s,p>");
                                        if(datos.length>0)
                                            nombre = datos[0];
                                        if(datos.length>=2)
                                            msg = datos[1];
                                    }
                                    if(cant>=2){
                                        Part unaParte = multi.getBodyPart(1);
                                        peso_dato = unaParte.getSize();
                                        uid = inbox.getUID(currentMessage);

                                        if(YouChatApplication.activePostDesImg){
                                            tamMsg += unaParte.getSize();
                                            String nombres_img = unaParte.getFileName();
                                            if(nombres_img==null || nombres_img.isEmpty())
                                                nombres_img = "img" + orden + ".jpg";
                                            ruta_Dato = YouChatApplication.RUTA_IMAGENES_RECIBIDA + nombres_img;

                                            File file = new File(YouChatApplication.RUTA_IMAGENES_RECIBIDA);
                                            boolean estaCreada = file.exists();
                                            if (!estaCreada)
                                                estaCreada = file.mkdirs();
                                            if (estaCreada) {
                                                MimeBodyPart mbp = (MimeBodyPart) unaParte;
                                                mbp.saveFile(ruta_Dato);
                                            }
                                        }
                                    }
//                                    for (int j = 0; j < cant; j++) {
//                                        Part unaParte = multi.getBodyPart(j);
//
//                                        if (unaParte.isMimeType("text/*")) {
//                                            String text = currentMessage.getContent().toString().trim();
//                                            datos = text.split("<s,p>");
//                                            if(datos.length>0)
//                                                nombre = datos[0];
//                                            if(datos.length>=2)
//                                                msg = datos[1];
//                                        }
//                                        else {
//                                            peso_dato = unaParte.getSize();
//                                            String nombres_img = unaParte.getFileName();
//                                            if(nombres_img==null || nombres_img.isEmpty())
//                                                nombres_img = "img" + orden + ".jpg";
//                                            ruta_Dato = YouChatApplication.RUTA_IMAGENES_RECIBIDA + nombres_img;
//
//                                            File file = new File(YouChatApplication.RUTA_IMAGENES_RECIBIDA);
//                                            boolean estaCreada = file.exists();
//                                            if (!estaCreada)
//                                                estaCreada = file.mkdirs();
//                                            if (estaCreada) {
//                                                MimeBodyPart mbp = (MimeBodyPart) unaParte;
//                                                mbp.saveFile(ruta_Dato);
//                                            }
//                                        }
//                                    }
                                }

                                ItemPost newPost = new ItemPost(idA,uid,tipoPost,nombre+" (Post de prueba)",correo,tipoUsu,icono,msg,true,ruta_Dato,peso_dato,hora,fecha,orden);
                                dbWorker.insertarNuevoPost(newPost);

                                if (YouChatApplication.burbuja_datos) {
                                    YouChatApplication.consumoBajada += tamMsg;
                                    Intent WIDGET = new Intent("ACTUALIZAR_WIDGET");
                                    LocalBroadcastManager.getInstance(YouChatApplication.chatService).sendBroadcast(WIDGET);
                                }
                                YouChatApplication.addCant_post_rye(1);
                                YouChatApplication.addMega_post_recibidos(tamMsg);
                                if(YouChatApplication.principalActivity!=null){
                                    Utils.runOnUIThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            YouChatApplication.principalActivity.ActualizarPosts(true, true, false);
                                        }
                                    });
                                }
                                cantPost++;
                            }
                        }
                    }
                    else break;
                }
            }
        }catch (MessagingException e){
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void procesarMensajesGlobales(Message[] result){
        try{
            int l = result.length;
            ArrayList<Message> listaMensajes = new ArrayList<>();
            for(int i=l-1; i>=0; i--){
                String[] datoTemp;
                String stringTemp = "";
                Message currentMessage = result[i];
                datoTemp = currentMessage.getHeader(ItemChat.KEY_ID);
                if (datoTemp != null)
                    stringTemp = datoTemp[0];
                String idA = stringTemp;
                if(!idA.equals("")){
                    if(!idA.equals(YouChatApplication.idUltChatGlobalRecibido)){
                        String[] validador = currentMessage.getHeader(ItemChat.YOUCHAT);
                        if (validador != null) {
                            listaMensajes.add(0,currentMessage);
                        }
                    } else break;
                }
            }
            l = listaMensajes.size();
            if(l>0){
                for(int i=0; i<l; i++){
                    Message currentMessage = listaMensajes.get(i);
                    String stringTemp = "";
                    String[] datoTemp = currentMessage.getHeader(ItemChat.KEY_ID);
                    if (datoTemp != null)
                        stringTemp = datoTemp[0];
                    final String idA = stringTemp;
                    final String correo = YouChatApplication.idOficial;

                    Date fmsg = currentMessage.getSentDate();
                    SimpleDateFormat fmsgc = new SimpleDateFormat("yyyyMMddHHmmss");
                    String horaReal = fmsgc.format(fmsg);
                    final String hora = Convertidor.conversionHora(horaReal);
                    final String fecha = Convertidor.conversionFecha(horaReal);

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
                    String orden = sdf.format(new Date());

                    ItemChat chat = Convertidor.createItemChatOfMessage(currentMessage,idA, correo, hora, fecha, orden);
                    if (chat != null) {
                        if (chat.esDeEstaVersionElTipo(chat.getTipo_mensaje())) {

                            if (chat.esMsgTexto() || chat.esContacto() || chat.esTarjeta()) {
                                String text = currentMessage.getContent().toString().trim();
                                chat.setMensaje(text);
                            }
                            else {
                                Multipart multi;
                                multi = (Multipart) currentMessage.getContent();
                                int cant = multi.getCount();
                                for (int j = 0; j < cant; j++) {
                                    Part unaParte = multi.getBodyPart(j);

                                    if (unaParte.isMimeType("text/*")) {
                                        String text = unaParte.getContent().toString().trim();
                                        chat.setMensaje(text);
                                    }
                                    else if (chat.esImagen()) {
                                        String nombres_img = "img" + chat.getOrden() + ".jpg";
                                        String ruta_Dato = YouChatApplication.RUTA_IMAGENES_RECIBIDA + nombres_img;

                                        File file = new File(YouChatApplication.RUTA_IMAGENES_RECIBIDA);
                                        boolean estaCreada = file.exists();
                                        if (!estaCreada)
                                            estaCreada = file.mkdirs();

                                        if (estaCreada) {
                                            MimeBodyPart mbp = (MimeBodyPart) unaParte;
                                            mbp.saveFile(ruta_Dato);
                                        }

                                        chat.setRuta_Dato(ruta_Dato);
                                    }
                                    else if (chat.esAudio()) {
                                        String nombre_aud = "rec" + chat.getOrden() + ".wav";
                                        String ruta_Dato = YouChatApplication.RUTA_AUDIOS_RECIBIDOS + nombre_aud;

                                        File file = new File(YouChatApplication.RUTA_AUDIOS_RECIBIDOS);
                                        boolean estaCreada = file.exists();
                                        if (!estaCreada)
                                            estaCreada = file.mkdirs();

                                        if (estaCreada) {
                                            MimeBodyPart mbp = (MimeBodyPart) unaParte;
                                            mbp.saveFile(ruta_Dato);
                                        }

                                        chat.setRuta_Dato(ruta_Dato);
                                    } else if (chat.esArchivo()) {
                                        String nombre_arc = unaParte.getFileName();
                                        if (nombre_arc.equals(""))
                                            nombre_arc = "archivo" + chat.getOrden() + ".desconocido";
                                        String ruta_Dato = YouChatApplication.RUTA_ARCHIVOS_RECIBIDOS + nombre_arc;

                                        File file = new File(YouChatApplication.RUTA_ARCHIVOS_RECIBIDOS);
                                        boolean estaCreada = file.exists();
                                        if (!estaCreada)
                                            estaCreada = file.mkdirs();

                                        if (estaCreada) {
                                            MimeBodyPart mbp = (MimeBodyPart) unaParte;
                                            mbp.saveFile(ruta_Dato);
                                        }

                                        chat.setRuta_Dato(ruta_Dato);
                                    }
                                }
                            }
                            dbWorker.insertarChat(chat);
                            ItemUsuario usuario = new ItemUsuario(chat.getCorreo());
                            ItemContacto contacto = new ItemContacto(chat.getCorreo(), chat.getCorreo());
                            dbWorker.insertarNuevoUsuario(usuario);
                            dbWorker.insertarNuevoContactoNoVisible(contacto, true);
                            dbWorker.actualizarUltMsgUsuario(chat);

                            if (YouChatApplication.chatsActivity != null &&
                                    correo.equals(YouChatApplication.chatsActivity.getCorreo())) {
                                Utils.runOnUIThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        YouChatApplication.chatsActivity.ActualizarChatMsgRecibido(chat);
                                    }
                                });
                            }
                            else if (YouChatApplication.notificacion
                                    && !dbWorker.estaSilenciado(chat.getCorreo())){
                                dbWorker.actualizarCantMensajesNoVistosX(chat.getCorreo(), 1);
                            }

                            if (YouChatApplication.principalActivity != null) {
                                Utils.runOnUIThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        YouChatApplication.principalActivity
                                                .actualizarNewMsg(correo,1);
                                    }
                                });
                            }
                        }
                    }
                    YouChatApplication.setIdUltChatGlobalRecibido(idA);
                }
            }
        } catch (MessagingException | IOException e){
            e.printStackTrace();
        }
    }

    private void procesarEstadosGlobales(Message[] result){
        try{
            int l = result.length;
            ArrayList<Message> listaMensajes = new ArrayList<>();
            for(int i=l-1; i>=0; i--){
                String[] datoTemp;
                String stringTemp = "";
                Message currentMessage = result[i];
                datoTemp = currentMessage.getHeader(ItemChat.KEY_ID);
                if (datoTemp != null)
                    stringTemp = datoTemp[0];
                String idA = stringTemp;
                if(!idA.equals("")){
                    if(!idA.equals(YouChatApplication.idUltEstGlobalRecibido)){
                        String[] validador = currentMessage.getHeader(ItemChat.YOUCHAT);
                        if (validador != null) {
                            listaMensajes.add(0,currentMessage);
                        }
                    } else break;
                }
            }
            l = listaMensajes.size();
            if(l>0){
                for(int i=0; i<l; i++){
                    String[] datoTemp;
                    String stringTemp = "";
                    Message currentMessage = listaMensajes.get(i);
                    datoTemp = currentMessage.getHeader(ItemChat.KEY_ID);
                    if (datoTemp != null)
                        stringTemp = datoTemp[0];
                    final String idA = stringTemp;
                    final String correo = YouChatApplication.idOficial;

                    Date fmsg = currentMessage.getSentDate();
                    SimpleDateFormat fmsgc = new SimpleDateFormat("yyyyMMddHHmmss");
                    String horaReal = fmsgc.format(fmsg);
                    final String hora = Convertidor.conversionHora(horaReal);
                    final String fecha = Convertidor.conversionFecha(horaReal);

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
                    Date date = new Date();
                    String orden = sdf.format(date);
                    boolean obtenerEstado = true;
                    String horaHoy = Convertidor.conversionHora(orden);
                    int intHoraHoy = Convertidor.createIntOfStringHora(horaHoy);

                    Date datHoy = new Date(sdf.parse(sdf.format(new Date())).getTime());
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                    Date datEstado = new Date(format.parse(fecha).getTime());

                    long dif = (datHoy.getTime() - datEstado.getTime()) / 86400000;
                    if (dif >= 1) {
                        if (dif == 1) {
                            int intHoraEst = Convertidor.createIntOfStringHora(hora);
                            if (intHoraHoy > intHoraEst) obtenerEstado = false;
                        } else obtenerEstado = false;
                    }
                    if (obtenerEstado) {
                        ItemEstado estado = Convertidor.createItemEstadoOfMessage(currentMessage,correo,hora,fecha);
                        if (estado != null) {
                            if (estado.getTipo_estado() != 99) {
                                String text = currentMessage.getContent().toString().trim();
                                estado.setTexto(text);
                            }
                            else {
                                Multipart multi;
                                multi = (Multipart) currentMessage.getContent();
                                int cant = multi.getCount();
                                for (int j = 0; j < cant; j++) {
                                    Part unaParte = multi.getBodyPart(j);

                                    if (unaParte.isMimeType("text/*")) {
                                        String text = unaParte.getContent().toString().trim();
                                        estado.setTexto(text);
                                    } else {
                                        String nombres_img = "est" + estado.getOrden() + ".jpg";
                                        String ruta_Dato = YouChatApplication.RUTA_ESTADOS_GUARDADOS
                                                + nombres_img;

                                        File file = new File(YouChatApplication.RUTA_ESTADOS_GUARDADOS);
                                        boolean estaCreada = file.exists();
                                        if (!estaCreada)
                                            estaCreada = file.mkdirs();

                                        if (estaCreada) {
                                            MimeBodyPart mbp = (MimeBodyPart) unaParte;
                                            mbp.saveFile(ruta_Dato);
                                        }
                                        estado.setRuta_imagen(ruta_Dato);
                                    }
                                }
                            }
                            dbWorker.insertarNuevoEstado(estado);
                            if(YouChatApplication.principalActivity!=null){
                                Utils.runOnUIThread(() -> {
                                    YouChatApplication.principalActivity.ActualizarEstados(true);
                                });
                            }
                        }
                    }
                    YouChatApplication.setIdUltEstGlobalRecibido(idA);
                }
            }
        } catch (MessagingException | ParseException | IOException e){
            e.printStackTrace();
        }
    }
}
