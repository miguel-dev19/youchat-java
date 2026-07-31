package cu.alexgi.youchat;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPMessage;
import com.sun.mail.imap.IMAPStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

import cu.alexgi.youchat.base_datos.DBWorker;
import cu.alexgi.youchat.items.ItemDesImgPost;
import cu.alexgi.youchat.progressbar.DownloadProgressView;


public class IntentServiceDesImgPostGlobales extends IntentService {

    private static final String TAG = "IntentServiceDesImgPost";
    private int cantABorrar;
    private boolean huboNuevosMsg, huboNuevosEst;
    private DBWorker dbWorker;

    private String user, pass;

    private ItemDesImgPost itemDesImgPost;
    private DownloadProgressView downloadProgressView;

    public IntentServiceDesImgPostGlobales() {
        super("YouCHat IntentServiceDesImgPostGlobales");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(YouChatApplication.itemDesImgPosts.size()>0){
            boolean descargaConExito = false;
            itemDesImgPost = YouChatApplication.itemDesImgPosts.get(0);
            YouChatApplication.itemDesImgPosts.remove(0);
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
                        inbox = (IMAPFolder) store.getFolder("Inbox");
                        if (!inbox.isOpen()) inbox.open(Folder.READ_WRITE);
                        if(inbox.isOpen()){

                            IMAPMessage currentMessage = null;
                            try {
                                currentMessage = (IMAPMessage) inbox.getMessageByUID(itemDesImgPost.getPost().getUid());
                            }catch (MessagingException e) {
                                e.printStackTrace();
                            }
                            if(currentMessage==null) currentMessage = obtenerMessageById(inbox, itemDesImgPost.getPost().getId());
                            if(currentMessage!=null){
                                try {
                                    Multipart multi = (Multipart) currentMessage.getContent();
                                    int cant = multi.getCount();
                                    if(cant>=2){
                                        Part unaParte = multi.getBodyPart(1);
                                        String nombres_img = unaParte.getFileName();
                                        if(nombres_img==null || nombres_img.isEmpty()){
                                            nombres_img = "img" + itemDesImgPost.getPost().getOrden() + ".jpg";
                                        }
                                        String ruta_Dato = YouChatApplication.RUTA_IMAGENES_RECIBIDA + nombres_img;

                                        File file = new File(YouChatApplication.RUTA_IMAGENES_RECIBIDA);
                                        boolean estaCreada = file.exists();
                                        if (!estaCreada)
                                            estaCreada = file.mkdirs();
                                        if (estaCreada) {
                                            FileOutputStream fichero = new FileOutputStream(ruta_Dato);
                                            InputStream inputStream = unaParte.getInputStream();
                                            byte[] bytes = new byte[1000];
                                            int leidos;
                                            int total = unaParte.getSize()/1000;
                                            int suma = total;
                                            while ((leidos = inputStream.read(bytes)) > 0) {
                                                suma=suma-(leidos/1000);
                                                final int sumaFinal = suma;
                                                Utils.runOnUIThread(()->{
                                                    if(downloadProgressView!=null){
                                                        float progress = (float)((total-sumaFinal)/total);
                                                        if(progress<0f) progress = 0f;
                                                        if(progress>100f) progress = 100f;
                                                        downloadProgressView.setProgress(progress);
                                                    }
                                                });
                                                fichero.write(bytes, 0, leidos);
                                            }
                                            fichero.close();
                                            inputStream.close();

                                            descargaConExito = true;
                                            int tamMsg = unaParte.getSize();
                                            YouChatApplication.addMega_post_recibidos(tamMsg);
                                            if (YouChatApplication.burbuja_datos) {
                                                YouChatApplication.consumoBajada += tamMsg;
                                                Intent WIDGET = new Intent("ACTUALIZAR_WIDGET");
                                                LocalBroadcastManager.getInstance(YouChatApplication.chatService).sendBroadcast(WIDGET);
                                            }
                                            dbWorker.actualizarPost(itemDesImgPost.getPost().getId(),ruta_Dato);
                                            Utils.runOnUIThread(()->{
                                                if(YouChatApplication.principalActivity!=null){
                                                    YouChatApplication
                                                            .principalActivity
                                                            .actualizarImgPostDescargada(itemDesImgPost.getPost().getId(),ruta_Dato);
                                                }
                                            });
                                            Utils.runOnUIThread(()->{
                                                if(YouChatApplication.historialPostFragment!=null) {
                                                    YouChatApplication
                                                            .historialPostFragment
                                                            .actualizarPostDescargado(itemDesImgPost.getPost().getId(), ruta_Dato);
                                                }
                                            });
                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            else {
                                Utils.runOnUIThread(()->{
                                    if(YouChatApplication.principalActivity!=null){
                                        YouChatApplication
                                                .principalActivity
                                                .actualizarDescargaFallidaImgPost(itemDesImgPost.getPost().getId(),true);
                                    }
                                });
                                Utils.runOnUIThread(()->{
                                    if(YouChatApplication.historialPostFragment!=null) {
                                        YouChatApplication
                                                .historialPostFragment
                                                .descargaImgFallida(itemDesImgPost.getPost().getId(), true);
                                    }
                                });
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

            if(!descargaConExito){
                Utils.runOnUIThread(()->{
                    if(YouChatApplication.principalActivity!=null){
                        YouChatApplication
                                .principalActivity
                                .actualizarDescargaFallidaImgPost(itemDesImgPost.getPost().getId(),false);
                    }
                });
                Utils.runOnUIThread(()->{
                    if(YouChatApplication.historialPostFragment!=null) {
                        YouChatApplication
                                .historialPostFragment
                                .descargaImgFallida(itemDesImgPost.getPost().getId(), false);
                    }
                });
            }
        }
    }

    private IMAPMessage obtenerMessageById(IMAPFolder inbox, String id) {
        if(id.isEmpty()) return null;
        try{
            Message[] messages = inbox.getMessages();
            int l=messages.length;
            for(int i=l-1; i>=0; i--){
                IMAPMessage message = (IMAPMessage) messages[i];
                if(message!=null && message.getMessageID().equals(id))
                    return message;
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
