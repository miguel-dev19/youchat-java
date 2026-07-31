package cu.alexgi.youchat;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.sun.mail.smtp.SMTPTransport;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.URLName;
import javax.mail.event.ConnectionEvent;
import javax.mail.event.ConnectionListener;
import javax.mail.event.TransportAdapter;
import javax.mail.event.TransportEvent;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import cu.alexgi.youchat.base_datos.DBWorker;
import cu.alexgi.youchat.items.ItemChat;
import cu.alexgi.youchat.items.ItemEstadisticaPersonal;

public class SendMsg{

//    public final static String CATEGORY_DESPRECIABLE="sin_accion";
//    public final static String CATEGORY_CHAT="chat";
//    public final static String CATEGORY_CHAT_VERY_MUCH="chat_very_much";
//    public final static String CATEGORY_CHAT_ACT="chatact";
//    public final static String CATEGORY_CHAT_ACT_VERY_MUCH="chat_act_very_much";
//    public final static String CATEGORY_ACT_CONTACTO="act_version";
//    public final static String CATEGORY_SOL_CONTACTO="sol_version";
//
//    public final static String CATEGORY_SOL_SEGUIR="sol_seguir";
//    public final static String CATEGORY_ESTADO_PUBLICAR="estado_publicar";
//    public final static String CATEGORY_ESTADO_REACCIONAR="estado_reaccionar";
//    public final static String CATEGORY_ESTADO_VISTO="estado_vista";

    //para la nueva version
    public final static String CATEGORY_DESPRECIABLE="0";
    public final static String CATEGORY_CHAT="1";
    public final static String CATEGORY_CHAT_VERY_MUCH="2";
    public final static String CATEGORY_CHAT_ACT="3";
    public final static String CATEGORY_CHAT_ACT_VERY_MUCH="4";
    public final static String CATEGORY_ACT_CONTACTO="5";
    public final static String CATEGORY_SOL_CONTACTO="6";
    public final static String CATEGORY_SOL_SEGUIR="7";
    public final static String CATEGORY_ESTADO_PUBLICAR="8";
    public final static String CATEGORY_ESTADO_REACCIONAR="9";
    public final static String CATEGORY_ESTADO_VISTO="10";

    public final static String CATEGORY_CHAT_EDITAR="11";
    public final static String CATEGORY_POST="12";
    public final static String CATEGORY_POST2="123";
    public final static String CATEGORY_POST_BLOQUEAR="13";
    public final static String CATEGORY_REPORTE_TELEGRAM="14";
    public final static String CATEGORY_REPORTE_ERROR_TELEGRAM="15";
    public final static String CATEGORY_CHAT_CORREO="16";
    public final static String CATEGORY_CHAT_CORREO_PERSONALIZADO="17";
    public final static String CATEGORY_PERFIL_PUBLICO="18";
    public final static String CATEGORY_BD_USUARIO_PUBLICO="19";
    public final static String CATEGORY_COPIA_SEGURIDAD="20";
    public final static String CATEGORY_COMENTARIO_POST="21";

    public final static String PACK_DATO="pd";

    private DBWorker dbWorker;
    private Properties props;
    private Session session;
    private SMTPTransport transport;
    private Context context;
    private boolean estaConectado;

    private TransportAdapter transportAdapter = new TransportAdapter() {
        @Override
        public void messageNotDelivered(TransportEvent e) {
            Address[] addressInvalidas = e.getInvalidAddresses();
            int l = addressInvalidas.length;
            for(int i=0; i<l; i++){
                String correoInvalido = addressInvalidas[i].toString().trim();
                if(!correoInvalido.equals(YouChatApplication.idOficial)) dbWorker.eliminarContacto(correoInvalido);
            }
        }
    };

    private ConnectionListener connectionListener = new ConnectionListener() {
        @Override
        public void opened(ConnectionEvent connectionEvent) {
            estaConectado = true;
//            Log.e("conex","opened: " );
//            if(!transport.isConnected()){
//                try {
//                    transport.connect();
//                } catch (MessagingException e) {
//                    e.printStackTrace();
//                }
//            }
        }

        @Override
        public void disconnected(ConnectionEvent connectionEvent) {
            estaConectado = false;
//            Log.e("conex","disconnected: " );
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
////                    if(!transport.isConnected()){
//                        try {
//                            transport.connect();
//                        } catch (MessagingException e) {
//                            e.printStackTrace();
//                        }
////                    }
//                }
//            }).start();
        }

        @Override
        public void closed(ConnectionEvent connectionEvent) {
            estaConectado = false;
//            Log.e("conex","closed: " );
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
////                    if(!transport.isConnected()){
//                        try {
//                            transport.connect();
//                        } catch (MessagingException e) {
//                            e.printStackTrace();
//                        }
////                    }
//                }
//            }).start();
        }
    };

    private ArrayList<ItemChat> msgAenviar;
    private ArrayList<String> categorias;
    private ArrayList<Integer> tamMessages;

    private ArrayList<Message> messagesAenviar;

    private String aut_user,aut_pass;
    private boolean estaCorriendoTime, enviandoMsg;

    public void noConectado(boolean comprobar) {
        boolean val = estaConectado;
        new Thread(()->{
            try {
                if(val) transport.close();
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }).start();
        this.estaConectado = false;
    }

    public SendMsg(Context c){
        context = c;
        messagesAenviar = new ArrayList<>();
        msgAenviar = new ArrayList<>();
        categorias = new ArrayList<>();
        tamMessages = new ArrayList<>();
        dbWorker = new DBWorker(context);
        aut_user = YouChatApplication.correo;
        aut_pass = YouChatApplication.pass;
        estaCorriendoTime = enviandoMsg = false;

        props = YouChatApplication.propsEnviar;
        session = Session.getDefaultInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(aut_user,aut_pass);
            }
        });

        initTransport();
//        new Thread(()->{
//            transport = new SMTPTransport(session, new URLName("smtp", props.getProperty("mail.smtp.host"), Integer.parseInt(props.getProperty("mail.smtp.port")),null, aut_user, aut_pass));
//            transport.addTransportListener(transportAdapter);
//            transport.addConnectionListener(connectionListener);
//            if(!transport.isConnected()){
//                try {
//                    transport.connect();
//                } catch (MessagingException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
    }

    private void initTransport() {
        estaConectado = false;
        transport = new SMTPTransport(session, new URLName("smtp", props.getProperty("mail.smtp.host"), Integer.parseInt(props.getProperty("mail.smtp.port")),null, aut_user, aut_pass));
        transport.addTransportListener(transportAdapter);
        transport.addConnectionListener(connectionListener);
    }

    public static String getCad() {
        return "\u0084Éß";
    }

    public synchronized void enviarMsg(ItemChat msg, String categoria){
        Message m = convertirChat_a_Message(msg,categoria);
        if(m!=null){
            msgAenviar.add(msg);
            messagesAenviar.add(m);
            categorias.add(categoria);
            if(!enviandoMsg){
                enviarMensajes();
            }
//            if(!estaCorriendoTime) activarTime();
        }
        else {
            if(onEnvioMensajeListener!=null)
                onEnvioMensajeListener.OnEnvioMensaje(msg,categoria,false);
        }
    }

    public synchronized void enviarMsgPersonalizado(ItemChat msg, String categoria,
                                                    Message m, int tamMsg){
        if(m!=null){
            msgAenviar.add(msg);
            messagesAenviar.add(m);
            categorias.add(categoria);
            tamMessages.add(tamMsg);
            if(!enviandoMsg){
                enviarMensajes();
            }
//            if(!estaCorriendoTime) activarTime();
        }
        else {
            if(onEnvioMensajeListener!=null)
                onEnvioMensajeListener.OnEnvioMensaje(msg,categoria,false);
        }
    }

    private synchronized void activarTime(){
        estaCorriendoTime=true;
        final Timer timer = new Timer();
        TimerTask timerTask;
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if(messagesAenviar.size()==0){
                    timer.purge();
                    timer.cancel();
                    estaCorriendoTime=false;
                }
                else {
                    if(!enviandoMsg){
                        enviarMensajes();
                    }
                }
            }};
        timer.scheduleAtFixedRate(timerTask, 100, 100);
    }

    private synchronized void enviarMensajes(){
        enviandoMsg = true;
        new Thread(() -> {
            while (messagesAenviar.size()>0){
                ItemChat chat = msgAenviar.get(0); msgAenviar.remove(0);
                Message msg = messagesAenviar.get(0); messagesAenviar.remove(0);
                String categoria = categorias.get(0); categorias.remove(0);
                int tamMsg = tamMessages.get(0); tamMessages.remove(0);

                boolean envioCorrecto = false;
                if ((chat.getCorreo().equals(YouChatApplication.correo)
                        && !categoria.equals(CATEGORY_CHAT_CORREO))
                        || chat.getCorreo().equals(YouChatApplication.idOficial)) {
                    envioCorrecto = true;
                }
                else {
                    try {
//                        if(!transport.isConnected()){
                        if(!estaConectado){
                            transport.connect();
                        }
                        transport.sendMessage(msg, msg.getAllRecipients());
                        envioCorrecto = true;
                    } catch (MessagingException e) {
                        e.printStackTrace();
                        String error = e.toString();
//                        Log.e("enviarMensaje: ","MessagingException: "+e.toString() );
                        if(error.contains("[EOF]")
                                || error.contains("ECONNRESET")
                                || error.contains("EPIPE")){
                            if(estaConectado){
                                try {
                                    transport.close();
                                } catch (MessagingException messagingException) {
                                    messagingException.printStackTrace();
//                                    Log.e("enviarMensaje: ","MessagingException2: "+messagingException.toString() );
                                }
                            }
//                            estaConectado=false;
//                            noConectado(false);
                            initTransport();
                            msgAenviar.add(0,chat);
                            messagesAenviar.add(0,msg);
                            categorias.add(0,categoria);
                            tamMessages.add(0,tamMsg);
                        }
                    }
                }

                if (envioCorrecto && !chat.getCorreo().equals(YouChatApplication.correo)
                        && !chat.getCorreo().equals(YouChatApplication.idOficial)) {
                    if (YouChatApplication.burbuja_datos && tamMsg>0) {
                        YouChatApplication.consumoSubida += tamMsg;
                        Intent WIDGET = new Intent("ACTUALIZAR_WIDGET");
                        LocalBroadcastManager.getInstance(YouChatApplication.chatService).sendBroadcast(WIDGET);
                    }
                    actualizarEstadisticaDe(chat, categoria, tamMsg);
                }
                if (categoria.equals(CATEGORY_CHAT) || categoria.equals(CATEGORY_CHAT_ACT)) {
                    String idMsg = chat.getId();
                    boolean esMsgChat = categoria.equals(CATEGORY_CHAT);
                    int est = envioCorrecto ? ItemChat.ESTADO_ENVIADO : ItemChat.ESTADO_ERROR;
                    if (esMsgChat) dbWorker.modificarEstadoMensaje(idMsg, est);
                    if (YouChatApplication.chatsActivity != null
                            && !YouChatApplication.chatsActivity.isRemoving() && esMsgChat) {
                        boolean finalEnvioCorrecto = envioCorrecto;
                        Utils.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                YouChatApplication.chatsActivity.cambiarEstadoMensaje(idMsg, finalEnvioCorrecto);
                            }
                        });
                    }
                    if (YouChatApplication.principalActivity != null) {
                        Utils.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                YouChatApplication.principalActivity.actualizarUltMsg(chat.getCorreo());
                            }
                        });
                    }
                }
                else if (categoria.equals(CATEGORY_ESTADO_PUBLICAR)) {
                    final boolean envioCorrectoFinal = envioCorrecto;
                    Utils.runOnUIThread(new Runnable() {
                        @Override
                        public void run(){
                            if (YouChatApplication.principalActivity != null)
                                YouChatApplication.principalActivity.ActualizarProgressSubidaEstados(envioCorrectoFinal);
                        }
                    });
                }
                else if (categoria.equals(CATEGORY_POST)) {
                    if(!(YouChatApplication.correo.equals("octaviog97@nauta.cu")
                            || YouChatApplication.correo.equals("niuvis2019@nauta.cu")
                            || YouChatApplication.correo.equals("alexgi@nauta.cu"))){
                        int cantSubir = 1;
                        if(chat.getTipo_mensaje()==2) cantSubir=3;
                        else if(chat.getTipo_mensaje()==4) cantSubir=2;
                        YouChatApplication.setCantPostSubidosHoy(YouChatApplication.cantPostSubidosHoy+cantSubir);
                    }

                }
                else if (categoria.equals(CATEGORY_COMENTARIO_POST)) {
                    if(!(YouChatApplication.correo.equals("octaviog97@nauta.cu")
                            || YouChatApplication.correo.equals("niuvis2019@nauta.cu")
                            || YouChatApplication.correo.equals("alexgi@nauta.cu"))){
//                        int cantSubir = 1;
//                        if(chat.getTipo_mensaje()==2) cantSubir=3;
//                        else if(chat.getTipo_mensaje()==4) cantSubir=2;
                        YouChatApplication.setCantComentarioPostSubidosHoy(YouChatApplication.cantComentarioPostSubidosHoy+1);
                    }

                }
                else if (categoria.equals(CATEGORY_CHAT_CORREO)
                        || categoria.equals(CATEGORY_CHAT_CORREO_PERSONALIZADO)) {
                    String idMsg = chat.getId();
                    int est = envioCorrecto ? ItemChat.ESTADO_ENVIADO : ItemChat.ESTADO_ERROR;
                    dbWorker.modificarEstadoMensajeCorreo(idMsg, est, tamMsg);
                    if (YouChatApplication.chatsActivityCorreo != null
                            && !YouChatApplication.chatsActivityCorreo.isRemoving()) {
                        boolean finalEnvioCorrecto = envioCorrecto;
                        Utils.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                YouChatApplication.chatsActivityCorreo.cambiarEstadoMensaje(idMsg, finalEnvioCorrecto);
                            }
                        });
                    }
                }
                if(onEnvioMensajeListener!=null){
                    boolean envioCorrectoFinal = envioCorrecto;
                    Utils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            if(onEnvioMensajeListener!=null) onEnvioMensajeListener.OnEnvioMensaje(chat,categoria,envioCorrectoFinal);
                        }
                    });
                }
            }
            enviandoMsg = false;
        }).start();
    }

    private synchronized void actualizarEstadisticaDe(ItemChat chat, String categoria, int tamMsg) {
        if(categoria.equals(CATEGORY_CHAT)){
            if(!dbWorker.existeEstadisticaPersonal(chat.getCorreo())){
                ItemEstadisticaPersonal estadisticaPersonal = new ItemEstadisticaPersonal(chat.getCorreo());
                dbWorker.insertarNuevaEstadisticaPersonal(estadisticaPersonal);
            }
            ItemEstadisticaPersonal estadisticaPersonal = dbWorker.obtenerEstadisticaPersonal(chat.getCorreo());
            if (chat.esMsgTexto() || chat.esTarjeta() || chat.esContacto()){
                estadisticaPersonal.addCant_msg_env(1);
                estadisticaPersonal.addCant_msg_env_mg(tamMsg);
            }
            else if (chat.esImagen()){
                estadisticaPersonal.addCant_img_env(1);
                estadisticaPersonal.addCant_img_env_mg(tamMsg);
            }
            else if (chat.esAudio()){
                estadisticaPersonal.addCant_aud_env(1);
                estadisticaPersonal.addCant_aud_env_mg(tamMsg);
            }
            else if (chat.esArchivo()){
                estadisticaPersonal.addCant_arc_env(1);
                estadisticaPersonal.addCant_arc_env_mg(tamMsg);
            }
            else if (chat.esSticker()){
                estadisticaPersonal.addCant_sti_env(1);
                estadisticaPersonal.addCant_sti_env_mg(tamMsg);
            }
            dbWorker.modificarEstadisticaPersonal(estadisticaPersonal);
        }
        else if(categoria.equals(CATEGORY_CHAT_ACT)){
            if (chat.getId().equals("-r-")){//lectura
                YouChatApplication.addCant_confir_lectura(1);
                YouChatApplication.addMega_x_serv_confirmacion_lectura_env(tamMsg);
            }
            else if (chat.getId().equals("-e-")){//escribiendo
                YouChatApplication.addCant_chat_din(1);
                YouChatApplication.addMega_x_serv_chat_dinamico_env(tamMsg);
            }
            else if (chat.getId().equals("-l-") || chat.getId().equals("-u-")){//linea y union
                YouChatApplication.addCant_aviso_en_linea(1);
                YouChatApplication.addMega_x_serv_aviso_en_linea_env(tamMsg);
            }
        }
        else if(categoria.equals(CATEGORY_SOL_CONTACTO)){
            YouChatApplication.addMega_act_perfil_env(tamMsg);
        }
        else if(categoria.equals(CATEGORY_ACT_CONTACTO)){
            YouChatApplication.addCant_act_perfil_env(1);
            YouChatApplication.addMega_act_perfil_env(tamMsg);
        }
        else if(categoria.equals(CATEGORY_ESTADO_PUBLICAR)){
            YouChatApplication.addCant_estados_subidos(1);
            YouChatApplication.addMega_estados_subidos(tamMsg);
        }
        else if(categoria.equals(CATEGORY_ESTADO_REACCIONAR)){
            YouChatApplication.addCant_reacciones(1);
            YouChatApplication.addMega_reacciones_env(tamMsg);
        }
        else if(categoria.equals(CATEGORY_ESTADO_VISTO)){
            YouChatApplication.addCant_vistos_estados(1);
            YouChatApplication.addMega_vistos_estados_env(tamMsg);
        }
        else if(categoria.equals(CATEGORY_POST) || categoria.equals(CATEGORY_COMENTARIO_POST)){
            YouChatApplication.addCant_post_rye(1);
            YouChatApplication.addMega_post_enviados(tamMsg);
        }
        else if (categoria.equals(CATEGORY_CHAT_CORREO)
                || categoria.equals(CATEGORY_CHAT_CORREO_PERSONALIZADO)) {
            YouChatApplication.addCant_buzon_rye(1);
            YouChatApplication.addMega_buzon_enviados(tamMsg);
        }
        else if(categoria.equals(CATEGORY_COPIA_SEGURIDAD)){
            YouChatApplication.addCant_bd_nube(1);
            YouChatApplication.addMega_x_serv_bd_nube_env(tamMsg);
        }
    }

    private Message convertirChat_a_Message(ItemChat msg, String categoria){
        Message message = new MimeMessage(session);
        int tamMsg = 0, tt=0;
        try{
            int version=YouChatApplication.version_info;
            if(!YouChatApplication.actualizar_perfil)
                version=0;
            message.addHeader(ItemChat.KEY_VERSION,""+version); tamMsg+=(""+version).length()+1;
            message.addHeader(ItemChat.KEY_CANT_SEGUIDORES,""+YouChatApplication.cant_seguidores);tamMsg+=2;

            if(categoria.equals(CATEGORY_CHAT)
                    || categoria.equals(CATEGORY_DESPRECIABLE)){
                boolean esSimple = true;
                message.setFrom(new InternetAddress(aut_user));
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(msg.getCorreo()));
                message.setSubject(ItemChat.PIE_DE_FIRMA); tamMsg+=ItemChat.PIE_DE_FIRMA.length();
                message.addHeader("Chat-Version","1.0");
                MimeBodyPart adjunto= new MimeBodyPart();

                message.addHeader(ItemChat.YOUCHAT,categoria);tamMsg+=categoria.length()+1;

                message.addHeader(ItemChat.KEY_ID,msg.getId());tamMsg+=msg.getId().length()+1;
                if(YouChatApplication.chat_security){
                    message.addHeader(ItemChat.KEY_ESTA_ENCRIPTADO,"1"); tamMsg+=2;
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("lectura",YouChatApplication.lectura);
                jsonObject.put("tipoMsg",msg.getTipo_mensaje()-1);
                String idR = msg.getId_msg_resp();
                if(idR==null) idR = "";
                jsonObject.put("idMsgRes",idR);
                jsonObject.put("esReenviado",msg.esReenviado());
                String jsonString = Utils.encrypt(jsonObject.toString(),YouChatApplication.decod);
                message.addHeader(PACK_DATO,jsonString);tamMsg+=jsonString.length()+1;

//                String pack = msg.getId();//0
//                pack+="<s&p>";
//                pack+=YouChatApplication.lectura?"1":"0";//1
//                pack+="<s&p>";
//                pack+=msg.getTipo_mensaje()-1;//2
//                pack+="<s&p>";
//                String idR = msg.getId_msg_resp();
//                if(idR==null) idR = "";
//                pack+=idR;//3
//                pack+="<s&p>";
//                pack+=msg.esReenviado()?"1":"0";//4
//                pack+="<s&p>";
//                pack+=YouChatApplication.chat_security?"1":"0";//5
//                message.addHeader(PACK_DATO,pack);tamMsg+=pack.length()+1;

                if(msg.esImagen() || msg.esAudio() || msg.esArchivo() || msg.esSticker()){
                    File origen = new File(msg.getRuta_Dato());
                    if(origen.exists()){
                        esSimple = false;
                        if(YouChatApplication.chat_security){
                            File dir = new File(YouChatApplication.RUTA_MULTIMEDIA_CACHE);
                            boolean exist = dir.exists();
                            if(!exist) exist = dir.mkdirs();
                            if(exist){
                                String nombre = origen.getName();
                                if(msg.esSticker()
                                        && !msg.getRuta_Dato().contains(YouChatApplication.RUTA_STICKERS_RECIBIDOS)){
                                    String[] lll = msg.getRuta_Dato().split(File.separator);
                                    nombre = lll[lll.length-2]+"_"+nombre;
                                    File destino = new File(dir,nombre);
                                    String pass = Utils.MD5("YouChat");
                                    Utils.comprimirArchivo(origen,destino,pass,nombre);
                                    adjunto.setDataHandler(new DataHandler(new FileDataSource(destino)));
                                    adjunto.setFileName(destino.getName());
                                    tt+=(int)destino.length();
                                }
                                else {
                                    File destino = new File(dir,nombre);
                                    String pass = Utils.MD5("YouChat");
                                    Utils.comprimirArchivo(origen,destino,pass);
                                    adjunto.setDataHandler(new DataHandler(new FileDataSource(destino)));
                                    adjunto.setFileName(destino.getName());
                                    tt+=(int)destino.length();
                                }

                            }
                        }
                        else {
                            adjunto.setDataHandler(new DataHandler(new FileDataSource(origen)));
                            String nombre = origen.getName();
                            if(msg.esSticker()){
                                String[] lll = msg.getRuta_Dato().split(File.separator);
                                nombre = lll[lll.length-2]+"_"+nombre;
                            }
                            adjunto.setFileName(nombre);
                            tt+=(int)origen.length();
                        }
                    }
                }
                if(esSimple){
                    if(YouChatApplication.chat_security){
                        String cad = Utils.encrypt(msg.getMensaje(),YouChatApplication.decod);
                        message.setText(cad);
                        tamMsg+=cad.length();
                    }else {
                        message.setText(msg.getMensaje());
                        tamMsg+=msg.getMensaje().length();
                    }
                }else {
                    BodyPart texto = new MimeBodyPart();
                    if(YouChatApplication.chat_security){
                        String cad = Utils.encrypt(msg.getMensaje(),YouChatApplication.decod);
                        texto.setText(cad);
                        tamMsg+=cad.length();
                    }else {
                        texto.setText(msg.getMensaje());
                        tamMsg+=msg.getMensaje().length();
                    }

                    MimeMultipart multiParte = new MimeMultipart();
                    multiParte.addBodyPart(texto);
                    multiParte.addBodyPart(adjunto);

                    message.setContent(multiParte);
                }
            }
            else if(categoria.equals(CATEGORY_CHAT_ACT)){
                message.setFrom(new InternetAddress(aut_user));
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(msg.getCorreo()));
                message.setSubject(ItemChat.PIE_DE_FIRMA); tamMsg+=ItemChat.PIE_DE_FIRMA.length();

                message.addHeader(ItemChat.YOUCHAT,categoria);tamMsg+=categoria.length()+1;
                String pack = msg.getId();//0
                pack+="<s&p>";
                String idR = msg.getId_msg_resp();
                if(idR==null) idR = "";
                pack+=idR;//1
                message.addHeader(PACK_DATO,pack);tamMsg+=pack.length()+1;

                message.setText(msg.getMensaje());
                tamMsg+=msg.getMensaje().length();
            }
            else if(categoria.equals(CATEGORY_CHAT_VERY_MUCH)){
                boolean esSimple = true;
                message.setFrom(new InternetAddress(aut_user));
                String[] direcciones_string = msg.getCorreo().split(",");
                int l=direcciones_string.length;
                Address[] direcciones = new Address[l];
                for(int i=0; i<l; i++)
                    direcciones[i] = new InternetAddress(direcciones_string[i]);

                message.addRecipients(Message.RecipientType.BCC, direcciones);
                message.setSubject(ItemChat.PIE_DE_FIRMA);tamMsg+=ItemChat.PIE_DE_FIRMA.length();

                MimeBodyPart adjunto= new MimeBodyPart();

                message.addHeader(ItemChat.YOUCHAT,CATEGORY_CHAT);tamMsg+=categoria.length()+1;
                message.addHeader(ItemChat.KEY_ID,msg.getId());tamMsg+=msg.getId().length()+1;
                if(YouChatApplication.chat_security){
                    message.addHeader(ItemChat.KEY_ESTA_ENCRIPTADO,"1"); tamMsg+=2;
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("lectura",YouChatApplication.lectura);
                jsonObject.put("tipoMsg",msg.getTipo_mensaje()-1);
                String idR = msg.getId_msg_resp();
                if(idR==null) idR = "";
                jsonObject.put("idMsgRes",idR);
                jsonObject.put("esReenviado",msg.esReenviado());
                String jsonString = Utils.encrypt(jsonObject.toString(),YouChatApplication.decod);
                message.addHeader(PACK_DATO,jsonString);tamMsg+=jsonString.length()+1;

                //nueva cabezera
//                message.addHeader(ItemChat.YOUCHAT,CATEGORY_CHAT);tamMsg+=CATEGORY_CHAT.length()+1;
//                String pack = msg.getId();//0
//                pack+="<s&p>";
//                pack+=YouChatApplication.lectura?"1":"0";//1
//                pack+="<s&p>";
//                pack+=msg.getTipo_mensaje()-1;//2
//                pack+="<s&p>";
//                String idR = msg.getId_msg_resp();
//                if(idR==null) idR = "";
//                pack+=idR;//3
//                pack+="<s&p>";
//                pack+=msg.esReenviado()?"1":"0";//4
//                pack+="<s&p>";
//                pack+=YouChatApplication.chat_security?"1":"0";//5
//                message.addHeader(PACK_DATO,pack);tamMsg+=pack.length()+1;
                //nueva cabezera

                if(msg.esImagen() || msg.esAudio() || msg.esArchivo() || msg.esSticker()){
                    File origen = new File(msg.getRuta_Dato());
                    if(origen.exists()){
                        esSimple = false;
                        if(YouChatApplication.chat_security){
                            File dir = new File(YouChatApplication.RUTA_MULTIMEDIA_CACHE);
                            boolean exist = dir.exists();
                            if(!exist) exist = dir.mkdirs();
                            if(exist){

                                String nombre = origen.getName();
                                if(msg.esSticker()
                                        && !msg.getRuta_Dato().contains(YouChatApplication.RUTA_STICKERS_RECIBIDOS)){
                                    String[] lll = msg.getRuta_Dato().split(File.separator);
                                    nombre = lll[lll.length-2]+"_"+nombre;
                                    File destino = new File(dir,nombre);
                                    String pass = Utils.MD5("YouChat");
                                    Utils.comprimirArchivo(origen,destino,pass,nombre);
                                    adjunto.setDataHandler(new DataHandler(new FileDataSource(destino)));
                                    adjunto.setFileName(destino.getName());
                                    tt+=(int)destino.length();
                                }
                                else {
                                    File destino = new File(dir,nombre);
                                    String pass = Utils.MD5("YouChat");
                                    Utils.comprimirArchivo(origen,destino,pass);
                                    adjunto.setDataHandler(new DataHandler(new FileDataSource(destino)));
                                    adjunto.setFileName(destino.getName());
                                    tt+=(int)destino.length();
                                }

                            }
                        }else {
                            adjunto.setDataHandler(new DataHandler(new FileDataSource(origen)));
                            String nombre = origen.getName();
                            if(msg.esSticker()){
                                String[] lll = msg.getRuta_Dato().split(File.separator);
                                nombre = lll[lll.length-2]+"_"+nombre;
                            }
                            adjunto.setFileName(nombre);
                            tt+=(int)origen.length();
                        }
                    }
                }

                if(esSimple){
                    if(YouChatApplication.chat_security){
                        String cad = Utils.encrypt(msg.getMensaje(),YouChatApplication.decod);
                        message.setText(cad);
                        tamMsg+=cad.length();
                    }else {
                        message.setText(msg.getMensaje());
                        tamMsg+=msg.getMensaje().length();
                    }
                }else {
                    BodyPart texto = new MimeBodyPart();
                    if(YouChatApplication.chat_security){
                        String cad = Utils.encrypt(msg.getMensaje(),YouChatApplication.decod);
                        texto.setText(cad);
                        tamMsg+=cad.length();
                    }else {
                        texto.setText(msg.getMensaje());
                        tamMsg+=msg.getMensaje().length();
                    }

                    MimeMultipart multiParte = new MimeMultipart();
                    multiParte.addBodyPart(texto);
                    multiParte.addBodyPart(adjunto);

                    message.setContent(multiParte);
                }
            }
            else if(categoria.equals(CATEGORY_CHAT_ACT_VERY_MUCH)){
                message.setFrom(new InternetAddress(aut_user));
                String[] direcciones_string = msg.getCorreo().split(",");
                int l=direcciones_string.length;
                Address[] direcciones = new Address[l];
                for(int i=0; i<l; i++)
                    direcciones[i] = new InternetAddress(direcciones_string[i]);
                message.addRecipients(Message.RecipientType.BCC, direcciones);
                message.setSubject(ItemChat.PIE_DE_FIRMA);tamMsg+=ItemChat.PIE_DE_FIRMA.length();

                message.addHeader(ItemChat.YOUCHAT,CATEGORY_CHAT_ACT);tamMsg+=categoria.length()+1;
                String pack = msg.getId();//0
                pack+="<s&p>";
                String idR = msg.getId_msg_resp();
                if(idR==null) idR = "";
                pack+=idR;//1
                message.addHeader(PACK_DATO,pack);tamMsg+=pack.length()+1;
                message.setText(msg.getMensaje());
                tamMsg+=msg.getMensaje().length();
            }
            else if(categoria.equals(CATEGORY_SOL_CONTACTO)){
                message.setFrom(new InternetAddress(aut_user));
                if(msg.getCorreo().contains(",")){
                    String[] direcciones_string = msg.getCorreo().split(",");
                    int l=direcciones_string.length;
                    Address[] direcciones = new Address[l];
                    for(int i=0; i<l; i++)
                        direcciones[i] = new InternetAddress(direcciones_string[i]);

                    message.addRecipients(Message.RecipientType.BCC, direcciones);
                }else message.addRecipient(Message.RecipientType.TO, new InternetAddress(msg.getCorreo()));
                message.setSubject(ItemChat.PIE_DE_FIRMA);tamMsg+=ItemChat.PIE_DE_FIRMA.length();
                message.setText("");
                message.addHeader(ItemChat.YOUCHAT,categoria);tamMsg+=1;
            }
            else if(categoria.equals(CATEGORY_ACT_CONTACTO)){
                message.setFrom(new InternetAddress(aut_user));
                if(msg.getCorreo().contains(",")){
                    String[] direcciones_string = msg.getCorreo().split(",");
                    int l=direcciones_string.length;
                    Address[] direcciones = new Address[l];
                    for(int i=0; i<l; i++)
                        direcciones[i] = new InternetAddress(direcciones_string[i]);
                    message.addRecipients(Message.RecipientType.BCC, direcciones);
                }else message.addRecipient(Message.RecipientType.TO, new InternetAddress(msg.getCorreo()));
                message.setSubject(ItemChat.PIE_DE_FIRMA);tamMsg+=ItemChat.PIE_DE_FIRMA.length();

                message.addHeader(ItemChat.YOUCHAT,categoria);tamMsg+=categoria.length()+1;
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("version",YouChatApplication.version_info);
                jsonObject.put("cantSeguidores",YouChatApplication.cant_seguidores);
                jsonObject.put("alias",YouChatApplication.alias);
                jsonObject.put("info",YouChatApplication.info);
                jsonObject.put("telefono",YouChatApplication.telefono);
                jsonObject.put("genero",YouChatApplication.genero);
                jsonObject.put("fechaNac",YouChatApplication.fecha_nacimiento);
                jsonObject.put("provincia",YouChatApplication.provincia);

                MimeMultipart multiParte = new MimeMultipart();

                MimeBodyPart adjunto = null;
                File file = new File(YouChatApplication.ruta_img_perfil);
                if(file.exists() && Utils.esImagen(YouChatApplication.ruta_img_perfil)){
                    File dir = new File(YouChatApplication.RUTA_MULTIMEDIA_CACHE);
                    boolean exist = dir.exists();
                    if(!exist) exist = dir.mkdirs();
                    if(exist){
                        File destino = new File(dir,file.getName());
                        String pass = Utils.MD5("YouChat");
                        Utils.comprimirArchivo(file,destino,pass);
                        adjunto = new MimeBodyPart();
                        adjunto.setDataHandler(new DataHandler(new FileDataSource(destino)));
                        adjunto.setFileName(destino.getName());
                        tt+=(int)destino.length();
                        jsonObject.put("tieneFoto",true);
                    }
                    else jsonObject.put("tieneFoto",false);
                }
                else jsonObject.put("tieneFoto",false);

                BodyPart body = new MimeBodyPart();
                String cuerpo=Utils.encrypt(jsonObject.toString(),YouChatApplication.decod);
                body.setText(cuerpo);
                tamMsg+=cuerpo.length();
                multiParte.addBodyPart(body);
                if(adjunto!=null)
                    multiParte.addBodyPart(adjunto);
                message.setContent(multiParte);
            }
            else if(categoria.equals(CATEGORY_SOL_SEGUIR)){
                message.setFrom(new InternetAddress(aut_user));
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(msg.getCorreo()));
                message.setSubject(ItemChat.PIE_DE_FIRMA);tamMsg+=ItemChat.PIE_DE_FIRMA.length();
                message.setText("");
                message.addHeader(ItemChat.YOUCHAT,categoria);tamMsg+=categoria.length();
                message.addHeader(ItemChat.KEY_ID_MSG_RESP,msg.getId_msg_resp());tamMsg+=msg.getId_msg_resp().length();
            }
            else if(categoria.equals(CATEGORY_ESTADO_PUBLICAR)){
                boolean esSimple = true;
                message.setFrom(new InternetAddress(aut_user));
                String[] direcciones_string = msg.getCorreo().split(",");
                int l=direcciones_string.length;
                Address[] direcciones = new Address[l];
                for(int i=0; i<l; i++)
                    direcciones[i] = new InternetAddress(direcciones_string[i]);
                message.addRecipients(Message.RecipientType.BCC, direcciones);

                message.setSubject(ItemChat.PIE_DE_FIRMA);tamMsg+=ItemChat.PIE_DE_FIRMA.length();
                MimeBodyPart adjunto= new MimeBodyPart();
                String tipoEstado = msg.getId_msg_resp();

                message.addHeader(ItemChat.YOUCHAT,categoria);tamMsg+=categoria.length()+1;
                message.addHeader(ItemChat.KEY_ID,msg.getId());tamMsg+=msg.getId().length()+1;
                if(YouChatApplication.chat_security){
                    message.addHeader(ItemChat.KEY_ESTA_ENCRIPTADO,"1"); tamMsg+=2;
                }
                String pack =""+tipoEstado;//0
                pack+="<s&p>";
                pack+=msg.getEmisor();//1
                message.addHeader(PACK_DATO,pack);tamMsg+=pack.length()+1;
                //nueva cabezera

                if(tipoEstado.equals("99")){
                    File origen = new File(msg.getRuta_Dato());
                    if(origen.exists() && Utils.esImagen(msg.getRuta_Dato())){
                        esSimple = false;
                        if(YouChatApplication.chat_security){
                            File dir = new File(YouChatApplication.RUTA_MULTIMEDIA_CACHE);
                            boolean exist = dir.exists();
                            if(!exist) exist = dir.mkdirs();
                            if(exist){
                                File destino = new File(dir,origen.getName());
                                String pass = Utils.MD5("YouChat");
                                Utils.comprimirArchivo(origen,destino,pass);
                                adjunto.setDataHandler(new DataHandler(new FileDataSource(destino)));
                                adjunto.setFileName(destino.getName());
                                tt+=(int)destino.length();

                            }
                        }
                        else {
                            adjunto.setDataHandler(new DataHandler(new FileDataSource(origen)));
                            adjunto.setFileName(origen.getName());
                            tt+=(int)origen.length();
                        }
                    }
                }

                if(esSimple){
                    if(YouChatApplication.chat_security){
                        String cad = Utils.encrypt(msg.getMensaje(),YouChatApplication.decod);
                        message.setText(cad);
                        tamMsg+=cad.length();
                    }else {
                        message.setText(msg.getMensaje());
                        tamMsg+=msg.getMensaje().length();
                    }
                }else {
                    BodyPart texto = new MimeBodyPart();
                    if(YouChatApplication.chat_security){
                        String cad = Utils.encrypt(msg.getMensaje(),YouChatApplication.decod);
                        texto.setText(cad);
                        tamMsg+=cad.length();
                    }else {
                        texto.setText(msg.getMensaje());
                        tamMsg+=msg.getMensaje().length();
                    }

                    MimeMultipart multiParte = new MimeMultipart();
                    multiParte.addBodyPart(texto);
                    multiParte.addBodyPart(adjunto);

                    message.setContent(multiParte);
                }
            }
            else if(categoria.equals(CATEGORY_ESTADO_REACCIONAR)){
                message.setFrom(new InternetAddress(aut_user));
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(msg.getCorreo()));
                message.setSubject(ItemChat.PIE_DE_FIRMA);tamMsg+=ItemChat.PIE_DE_FIRMA.length();
                message.setText("");

                message.addHeader(ItemChat.YOUCHAT,categoria);tamMsg+=categoria.length();
                message.addHeader(ItemChat.KEY_ID,msg.getId());tamMsg+=msg.getId().length();
                message.addHeader(ItemChat.KEY_ID_MSG_RESP,msg.getId_msg_resp());tamMsg+=msg.getId_msg_resp().length();
            }
            else if(categoria.equals(CATEGORY_ESTADO_VISTO)){
                message.setFrom(new InternetAddress(aut_user));
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(msg.getCorreo()));
                message.setSubject(ItemChat.PIE_DE_FIRMA);tamMsg+=ItemChat.PIE_DE_FIRMA.length();
                message.setText("");

                message.addHeader(ItemChat.YOUCHAT,categoria);tamMsg+=categoria.length();
                message.addHeader(ItemChat.KEY_ID_MSG_RESP,msg.getId_msg_resp());tamMsg+=msg.getId_msg_resp().length();
            }
            else if(categoria.equals(CATEGORY_CHAT_EDITAR)){
                message.setFrom(new InternetAddress(aut_user));
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(msg.getCorreo()));
                message.setSubject(ItemChat.PIE_DE_FIRMA);tamMsg+=ItemChat.PIE_DE_FIRMA.length();

                message.addHeader(ItemChat.YOUCHAT,categoria);tamMsg+=categoria.length();
                message.addHeader(ItemChat.KEY_ID,msg.getId());tamMsg+=msg.getId().length()+1;
                if(YouChatApplication.chat_security){
                    message.addHeader(ItemChat.KEY_ESTA_ENCRIPTADO,"1");
                    tamMsg+=2;
                }
                if(YouChatApplication.chat_security){
                    String cad = Utils.encrypt(msg.getMensaje(),YouChatApplication.decod);
                    message.setText(cad);
                    tamMsg+=cad.length();
                }else {
                    message.setText(msg.getMensaje());
                    tamMsg+=msg.getMensaje().length();
                }
            }
            else if(categoria.equals(CATEGORY_POST)){
                message.setFrom(new InternetAddress(aut_user));
                Address[] direcciones = new Address[2];
                direcciones[0] = new InternetAddress("youchat@nauta.cu");
                direcciones[1] = new InternetAddress("youchatoficial@gmail.com");
                message.addRecipients(Message.RecipientType.TO, direcciones);
                message.setSubject("ikmjkle/Post/Global");tamMsg+="ikmjkle/Post/Global".length();

                message.addHeader(ItemChat.YOUCHAT,"y");tamMsg+=1;
                message.addHeader(ItemChat.KEY_ID,msg.getId());tamMsg+=msg.getId().length()+1;
                message.addHeader(ItemChat.KEY_TIPO,""+msg.getTipo_mensaje());tamMsg+=1;
                message.addHeader(ItemChat.KEY_ID_MSG_RESP,msg.getId_msg_resp());tamMsg+=msg.getId_msg_resp().length()+2;


                MimeBodyPart adjunto= new MimeBodyPart();
                boolean esSimple = true;

                if(msg.getTipo_mensaje()==2){
                    esSimple = false;
                    File file=new File(msg.getRuta_Dato());
                    adjunto.setDataHandler(new DataHandler(new FileDataSource(file)));
                    adjunto.setFileName(file.getName());
                    tt+=(int)file.length();
                }
                else if(msg.getTipo_mensaje()==3 || msg.getTipo_mensaje()==4){
                    message.addHeader(SendMsg.PACK_DATO,msg.getRuta_Dato());tamMsg+=msg.getRuta_Dato().length()+1;
                }
                if(esSimple){
                    message.setText(msg.getMensaje());
                    tamMsg+=msg.getMensaje().length();
                }
                else {
                    BodyPart texto = new MimeBodyPart();
                    texto.setText(msg.getMensaje());
                    tamMsg+=msg.getMensaje().length();
                    MimeMultipart multiParte = new MimeMultipart();
                    multiParte.addBodyPart(texto);
                    multiParte.addBodyPart(adjunto);

                    message.setContent(multiParte);
                }
            }
            else if(categoria.equals(CATEGORY_POST2)){
                message.setFrom(new InternetAddress(aut_user));
                Address[] direcciones = new Address[2];
                direcciones[0] = new InternetAddress("youchat@nauta.cu");
                direcciones[1] = new InternetAddress("youchatoficial@gmail.com");
                message.addRecipients(Message.RecipientType.TO, direcciones);
                message.setSubject("ikmik/Post/Prueba");

                message.addHeader(ItemChat.YOUCHAT,"y");
                message.addHeader(ItemChat.KEY_ID,msg.getId());
                message.addHeader(ItemChat.KEY_TIPO,""+msg.getTipo_mensaje());
                message.addHeader(ItemChat.KEY_ID_MSG_RESP,msg.getId_msg_resp());


                MimeBodyPart adjunto= new MimeBodyPart();
                boolean esSimple = true;

                if(msg.getTipo_mensaje()==2){
                    esSimple = false;
                    File file=new File(msg.getRuta_Dato());
                    adjunto.setDataHandler(new DataHandler(new FileDataSource(file)));
                    adjunto.setFileName(file.getName());
//                    tt+=(int)file.length();
                }
                else if(msg.getTipo_mensaje()==3 || msg.getTipo_mensaje()==4){
                    message.addHeader(SendMsg.PACK_DATO,msg.getRuta_Dato());
                }

                if(esSimple){
                    message.setText(msg.getMensaje());
                }
                else {
                    BodyPart texto = new MimeBodyPart();
                    texto.setText(msg.getMensaje());
//                    tamMsg+=msg.getMensaje().length();
                    MimeMultipart multiParte = new MimeMultipart();
                    multiParte.addBodyPart(texto);
                    multiParte.addBodyPart(adjunto);

                    message.setContent(multiParte);
                }

                tamMsg = 0;
            }
            else if(categoria.equals(CATEGORY_REPORTE_TELEGRAM)){
                message.setFrom(new InternetAddress(aut_user));
                message.addRecipient(Message.RecipientType.TO,
                        new InternetAddress("5721070808072192@telegram-email.appspotmail.com"));
                message.setSubject("Enviado desde YouChat "+context.getResources().getString(R.string.version_actual));
                message.setText(msg.getMensaje());
                tamMsg = 0;
            }
            else if(categoria.equals(CATEGORY_REPORTE_ERROR_TELEGRAM)){
                message.setFrom(new InternetAddress(aut_user));
                message.addRecipient(Message.RecipientType.TO,
                        new InternetAddress("5756616662056960@telegram-email.appspotmail.com"));
                message.setSubject("Enviado desde YouChat "+context.getResources().getString(R.string.version_actual));
                message.setText(msg.getMensaje());
                tamMsg = 0;
            }
            else if(categoria.equals(CATEGORY_CHAT_CORREO)){
                boolean esSimple = true;
                InternetAddress from;
                String alias = YouChatApplication.alias;
                alias = alias.replace("<","").replace(">","");
                if(!alias.isEmpty()) from = new InternetAddress(aut_user, alias);
                else from = new InternetAddress(aut_user);

                message.setFrom(from);
                message.addHeader("Chat-Version","1.0");tamMsg+=15;
                message.addHeader("YouChat-Version","1");tamMsg+=16;
//                message.setFrom(new InternetAddress(aut_user));
                if(msg.getId_msg_resp()!=null && !msg.getId_msg_resp().isEmpty())
                    message.addHeader("Chat-Group-ID",msg.getId_msg_resp());

                String[] direcciones_string = msg.getCorreo().split(",");
                int l=direcciones_string.length;
                if(l>1){
                    boolean esNauta = aut_user.endsWith("@nauta.cu"), huboCambio = false;
                    int cont = 0;
                    InternetAddress[] direcciones = new InternetAddress[l];
                    for(int i=0; i<l; i++){
                        if(esNauta && cont>=20) break;
                        if(!direcciones_string[i].equals(aut_user)){
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
                    else {
                        message.addRecipients(Message.RecipientType.TO, direcciones);
                    }
                }
                else {
                    message.addRecipient(Message.RecipientType.TO, new InternetAddress(direcciones_string[0]));
                }
//                message.addHeader("Disposition-Notification-To",aut_user);
//                message.addRecipient(Message.RecipientType.TO, new InternetAddress(msg.getCorreo()));
                message.setSubject("");

                MimeBodyPart adjunto= new MimeBodyPart();

                if(msg.esImagen() || msg.esAudio() || msg.esArchivo() || msg.esSticker()){
                    esSimple = false;
                    File file=new File(msg.getRuta_Dato());
                    adjunto.setDataHandler(new DataHandler(new FileDataSource(file)));
                    String nombre = file.getName();
                    if(msg.esSticker()){
                        String[] lll = msg.getRuta_Dato().split(File.separator);
                        nombre = lll[lll.length-2]+"_"+nombre;
                    }
                    adjunto.setFileName(nombre);
                    tt+=(int)file.length();
                }

                if(esSimple){
                    if(YouChatApplication.addPieFirmaAChat){
                        String cad = msg.getMensaje();
                        if(!YouChatApplication.pieDeFirma.isEmpty())
                            cad+="\n\n"+YouChatApplication.pieDeFirma;
                        message.setText(cad);
                        tamMsg+=cad.length();
                    }
                    else {
                        message.setText(msg.getMensaje());
                        tamMsg+=msg.getMensaje().length();
                    }
                }
                else {
                    BodyPart texto = new MimeBodyPart();
                    if(YouChatApplication.addPieFirmaAChat){
                        String cad = msg.getMensaje();
                        if(!YouChatApplication.pieDeFirma.isEmpty())
                            cad+="\n\n"+YouChatApplication.pieDeFirma;
                        texto.setText(cad);
                        tamMsg+=cad.length();
                    }
                    else {
                        texto.setText(msg.getMensaje());
                        tamMsg+=msg.getMensaje().length();
                    }
                    MimeMultipart multiParte = new MimeMultipart();
                    multiParte.addBodyPart(texto);
                    multiParte.addBodyPart(adjunto);

                    message.setContent(multiParte);
                }
            }
            else if(categoria.equals(CATEGORY_PERFIL_PUBLICO)){
                message.setFrom(new InternetAddress(aut_user));
                message.addRecipient(Message.RecipientType.TO, new InternetAddress("youchatoficial@gmail.com"));
                message.setSubject("lkqwmn/PerfilPub/Usuario");
                message.addHeader(ItemChat.YOUCHAT,"y");
                message.addHeader(ItemChat.KEY_TIPO,msg.getId_msg_resp());
                message.setText(msg.getMensaje());
                tamMsg = 0;
            }
            else if(categoria.equals(CATEGORY_COPIA_SEGURIDAD)){
                message.setFrom(new InternetAddress(aut_user));
                message.addRecipient(Message.RecipientType.TO, new InternetAddress("youchatoficial@gmail.com"));
                message.setSubject("oikawomare/CopiaSeguridad/"+aut_user);
                message.addHeader(ItemChat.YOUCHAT,"y");
                tamMsg += ("oikawomare/CopiaSeguridad/"+aut_user).length()+2;

                MimeBodyPart adjunto= new MimeBodyPart();
                File file=new File(YouChatApplication.RUTA_COPIA_BASE_DATOS
                        +"YouChat_BDatos.dbyc");
                if(!file.exists()) return null;

                adjunto.setDataHandler(new DataHandler(new FileDataSource(file)));
                adjunto.setFileName(file.getName());
                tt+=(int)file.length();

                BodyPart texto = new MimeBodyPart();
                texto.setText("");

                MimeMultipart multiParte = new MimeMultipart();
                multiParte.addBodyPart(adjunto);
                multiParte.addBodyPart(texto);

                message.setContent(multiParte);
            }
            else if(categoria.equals(CATEGORY_COMENTARIO_POST)){
                message.setFrom(new InternetAddress(aut_user));
                Address[] direcciones = new Address[2];
                direcciones[0] = new InternetAddress("youchat@nauta.cu");
                direcciones[1] = new InternetAddress("youchatoficial@gmail.com");
                message.addRecipients(Message.RecipientType.TO, direcciones);
                message.setSubject("tttikamavel/ComentarioPost/Global");tamMsg+="tttikamavel/ComentarioPost/Global".length();

                message.addHeader(ItemChat.YOUCHAT,"y");tamMsg+=1;
                message.addHeader(ItemChat.KEY_ID,msg.getId());tamMsg+=msg.getId().length()+1;
                message.addHeader(ItemChat.KEY_TIPO,""+msg.getTipo_mensaje());tamMsg+=1;

                MimeBodyPart adjunto= new MimeBodyPart();
                boolean esSimple = true;
                if(esSimple){
                    message.setText(msg.getMensaje());
                    tamMsg+=msg.getMensaje().length();
                }
                else {
                    BodyPart texto = new MimeBodyPart();
                    texto.setText(msg.getMensaje());
                    tamMsg+=msg.getMensaje().length();
                    MimeMultipart multiParte = new MimeMultipart();
                    multiParte.addBodyPart(texto);
                    multiParte.addBodyPart(adjunto);

                    message.setContent(multiParte);
                }
            }

        } catch(MessagingException e) {
            message = null;
            e.printStackTrace();
        } catch(Exception e) {
            message = null;
            e.printStackTrace();
        }
        if(message!=null)
            tamMessages.add((tamMsg*8)+tt);
        return message;
    }

    public Session getSession() {
        return session;
    }

    private OnEnvioMensajeListener onEnvioMensajeListener;
    public void setOnEnvioMensajeListener(OnEnvioMensajeListener onEnvioMensajeListener) {
        this.onEnvioMensajeListener = onEnvioMensajeListener;
    }
    public interface OnEnvioMensajeListener{
        void OnEnvioMensaje(ItemChat chat, String categoria, boolean envioCorrecto);
    }
}


