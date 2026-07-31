package cu.alexgi.youchat;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.sun.mail.imap.AppendUID;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPMessage;
import com.sun.mail.imap.IMAPStore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.activation.DataHandler;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.event.MessageChangedEvent;
import javax.mail.event.MessageChangedListener;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.search.SubjectTerm;

import cu.alexgi.youchat.base_datos.DBWorker;
import cu.alexgi.youchat.items.ItemAdjuntoCorreo;
import cu.alexgi.youchat.items.ItemChat;
import cu.alexgi.youchat.items.ItemContacto;
import cu.alexgi.youchat.items.ItemEstadisticaPersonal;
import cu.alexgi.youchat.items.ItemEstado;
import cu.alexgi.youchat.items.ItemMensajeCorreo;
import cu.alexgi.youchat.items.ItemNotificacion;
import cu.alexgi.youchat.items.ItemReaccionEstado;
import cu.alexgi.youchat.items.ItemUsuario;
import cu.alexgi.youchat.items.ItemUsuarioCorreo;
import cu.alexgi.youchat.items.ItemVistaEstado;
import cu.alexgi.youchat.progressbar.DownloadProgressView;
import cu.alexgi.youchat.views_GI.TextViewFontGenGI;

import static cu.alexgi.youchat.MainActivity.mainActivity;
import static cu.alexgi.youchat.SendMsg.CATEGORY_ACT_CONTACTO;
import static cu.alexgi.youchat.SendMsg.CATEGORY_CHAT;
import static cu.alexgi.youchat.SendMsg.CATEGORY_CHAT_ACT;
import static cu.alexgi.youchat.SendMsg.CATEGORY_CHAT_EDITAR;
import static cu.alexgi.youchat.SendMsg.CATEGORY_ESTADO_PUBLICAR;
import static cu.alexgi.youchat.SendMsg.CATEGORY_ESTADO_REACCIONAR;
import static cu.alexgi.youchat.SendMsg.CATEGORY_ESTADO_VISTO;
import static cu.alexgi.youchat.SendMsg.CATEGORY_SOL_CONTACTO;
import static cu.alexgi.youchat.SendMsg.CATEGORY_SOL_SEGUIR;

public class ChatService extends Service {
    private static final String TAG = "ChatService";

    private int cantReaccionesNow, cantEntrantesNow;
    private Context context;
    private DBWorker dbWorker;
    private TimerTask timerTaskGlobal;
    private boolean usaGmail;
    private boolean esPrimeraVez;
    public int tiempoDeBorrar;
    public boolean hayConex;
    public int estadoConex;
    private String CHANNEL_ID = "youchat_notification";
    private static ChatService chatService;
    private ConnectivityManager conex;
    private NetworkInfo state_conex;
    public String aut_user, aut_pass;
    private Session session;
    private Properties props;
    public IMAPFolder inbox;
    private IMAPStore store;
    public SendMsg sendMsg;
    public String nxdiag, gxdiag, nxfaq;
    private NotificationManager mNotifyMgr;
    private ArrayList<ItemNotificacion> itemNotificacionArrayList;
    private ArrayList<String> listaIdMsgDescargando, listaIdMsgDescargandoCorreo;
    private ArrayList<String> listaContactosActualizar;
    private ArrayList<String> listaContactosSolicitud;
    boolean yaExist, confirmoEnvioCorreo, mensajeEnCola;

    private WindowManager mWindowManager;
    public View mFloatingView;
    private AppCompatTextView tv_subida, tv_bajada;
    private IntentFilter filter;
    private ResponseReceiver receiver;

    private MessageCountAdapter countAdapter = new MessageCountAdapter() {
        @Override
        public void messagesAdded(MessageCountEvent e) {
            Message[] message = e.getMessages();
            getEmails(message, false, true);
        }
    };

    public ChatService() {
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void sincronizarTodosMensajes() {
        new Thread(()->{
            if(inbox!=null){
                if(inbox.isOpen()){
                    if(YouChatApplication.bandejaFragment!=null){
                        Utils.runOnUIThread(()->{
                            if (YouChatApplication.bandejaFragment != null)
                                YouChatApplication.bandejaFragment.mostrarBarraProgress();
                        });
                    }
                    try {
                        Message[] messages = inbox.getMessages();
                        getEmails(messages, true, true);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
    }

    public void escanearBandejaEntrada(TextView textView, LinearProgressIndicator progressIndicator) {
        new Thread(()->{
            if(inbox!=null){
                if(inbox.isOpen()){
                    try {
                        Message[] messages = inbox.getMessages();
                        int cantCorreos = messages.length;
                        long pesoTotal = 0;
                        for(int i=0; i<cantCorreos; i++){
                            pesoTotal += messages[i].getSize();
                            int iFinal = i;
                            long pesoFinal = pesoTotal;
                            Utils.runOnUIThread(()->{
                                if(textView!=null)
                                    textView.setText("Mensajes procesados: "+iFinal+"/"+cantCorreos+"\n"
                                            +"Peso actual: "+Utils.convertirBytes(pesoFinal)+"\n");
                                if(progressIndicator!=null){
                                    float per = pesoFinal;
                                    if(usaGmail){
                                        //5gb = 5368709120
                                        per/=5f;//gb
                                        per/=1024f;//mb
                                        per/=1024f;//kb
                                        per/=1024f;//b
                                        per*=100f;
                                    }
                                    else {
                                        //100mb = 104857600
                                        per/=100;//mb
                                        per/=1024f;//kb
                                        per/=1024f;//b
                                        per*=100f;
                                    }
                                    if(per<0) per=0;
                                    else if(per>100) per=100;
                                    progressIndicator.setProgress((int)per);
                                }
                            });
                        }

                        String fechaEntera = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.getDefault()).format(new Date());
                        String cad = "Mensajes en total: "+cantCorreos+"\n"
                                +"Peso total: "+Utils.convertirBytes(pesoTotal)+"/"+(usaGmail?"5GB":"100MB")+"\n"
                                +"Fecha del escaner: "+fechaEntera;
                        YouChatApplication.setDescripEscanerBandeja(cad);
                        float per = pesoTotal;
                        if(usaGmail){
                            //5gb = 5368709120
                            per/=5f;//gb
                            per/=1024f;//mb
                            per/=1024f;//kb
                            per/=1024f;//b
                            per*=100f;
                        }
                        else {
                            //100mb = 104857600
                            per/=100;//mb
                            per/=1024f;//kb
                            per/=1024f;//b
                            per*=100f;
                        }
                        per = Math.round(per);
                        if(per<0) per=0;
                        else if(per>100) per=100;
                        int perFinal = (int) per;
                        YouChatApplication.setProgressEscanerBandeja(perFinal);
                        Utils.runOnUIThread(()->{
                            if(textView!=null)
                                textView.setText(cad);
                            if(progressIndicator!=null){
                                progressIndicator.setProgress(perFinal);
                            }
                        });
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private class ResponseReceiver extends BroadcastReceiver {
        private ResponseReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                final String action = intent.getAction();
                final String accion_actualizar = "ACTUALIZAR_WIDGET";
                if (accion_actualizar.equals(action)) {
                    if(mFloatingView!=null)
                    {
//                        String extendSubida = "Kb";
//                        long sizeSubida = YouChatApplication.consumoSubida;
//                        double realSizeSubida = (double) sizeSubida/1024;
//                        if(realSizeSubida > 1024){
//                            extendSubida = "Mb";
//                            realSizeSubida = (double) realSizeSubida/1024;
//                        }
//                        realSizeSubida = (double)Math.round(realSizeSubida*100)/100;

//                        String extendBajada = "Kb";
//                        long sizeBajada = YouChatApplication.consumoBajada;
//                        double realSizeBajada = (double) sizeBajada/1024;
//                        if(realSizeBajada > 1024){
//                            extendBajada = "Mb";
//                            realSizeBajada = (double) realSizeBajada/1024;
//                        }
//                        realSizeBajada = (double)Math.round(realSizeBajada*100)/100;

                        tv_subida.setText(Utils.convertirBytes(YouChatApplication.consumoSubida));
                        tv_bajada.setText(Utils.convertirBytes(YouChatApplication.consumoBajada));
//                        tv_subida.setText(realSizeSubida+" "+extendSubida);
//                        tv_bajada.setText(realSizeBajada+" "+extendBajada);
                    }
                }
            }
        }
    }

    @Override
    public void onCreate() {
        chatService = this;
        context = this;
        dbWorker = new DBWorker(context);
        yaExist = false;

        setTheme(R.style.AppTheme);

        filter = new IntentFilter("ACTUALIZAR_WIDGET");
        receiver = new ResponseReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }

    public synchronized void removeWidget(){
        if (mFloatingView != null && mWindowManager!=null) {
            mWindowManager.removeView(mFloatingView);
            mFloatingView=null;
        }
    }

    @SuppressLint("RestrictedApi")
    public synchronized void buildWidget(){
        Log.e("buildWidget","ENTRO");
        removeWidget();

        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null);
        //Add the view to the window.
        int Flag;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
            Flag = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        else Flag = WindowManager.LayoutParams.TYPE_PHONE;

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                Flag,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        //Specify the view position
        params.gravity = Gravity.TOP | Gravity.START;        //Initially view will be added to top-left corner
        params.x = YouChatApplication.anchoPantalla-20;
        params.y = 100;

        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);


//        final View collapsedView = mFloatingView.findViewById(R.id.collapse_view);
//        final View expandedView = mFloatingView.findViewById(R.id.expanded_container);
        MaterialCardView collapsedView = mFloatingView.findViewById(R.id.collapse_view);
        collapsedView.setCardBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_burbuja()));

        tv_subida = mFloatingView.findViewById(R.id.tv_subida);
        tv_bajada = mFloatingView.findViewById(R.id.tv_bajada);

        tv_subida.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getFont_burbuja()));
        tv_bajada.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getFont_burbuja()));
        tv_subida.setSupportCompoundDrawablesTintList(ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getFont_burbuja())));
        tv_bajada.setSupportCompoundDrawablesTintList(ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getFont_burbuja())));

        /*ImageView playButton = mFloatingView.findViewById(R.id.play_btn);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChatService.this, "Playing the song.", Toast.LENGTH_LONG).show();
            }
        });

        ImageView nextButton = mFloatingView.findViewById(R.id.next_btn);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChatService.this, "Playing next song.", Toast.LENGTH_LONG).show();
            }
        });

        ImageView prevButton = mFloatingView.findViewById(R.id.prev_btn);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChatService.this, "Playing previous song.", Toast.LENGTH_LONG).show();
            }
        });

        ImageView closeButton = mFloatingView.findViewById(R.id.close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collapsedView.setVisibility(View.VISIBLE);
//                expandedView.setVisibility(View.GONE);
            }
        });

        ImageView openButton = mFloatingView.findViewById(R.id.open_button);
        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Open the application  click.
                Intent intent = new Intent(ChatService.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
                //close the service and remove view from the view hierarchy
//                stopSelf();
            }
        });*/

        //Drag and move floating view using user's touch action.
        mFloatingView.findViewById(R.id.root_container).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        //remember the initial position.
                        initialX = params.x;
                        initialY = params.y;

                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
//                    case MotionEvent.ACTION_UP:
//                        int Xdiff = (int) (event.getRawX() - initialTouchX);
//                        int Ydiff = (int) (event.getRawY() - initialTouchY);
//
//                        if (Xdiff < 10 && Ydiff < 10) {
//                            if (isViewCollapsed()) {
//                                collapsedView.setVisibility(View.GONE);
////                                expandedView.setVisibility(View.VISIBLE);
//                            }
//                        }
//                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);


                        //Update the layout with new X & Y coordinate
                        if(mFloatingView!=null) mWindowManager.updateViewLayout(mFloatingView, params);
                        return true;
                }
                return false;
            }
        });

        Intent WIDGET = new Intent("ACTUALIZAR_WIDGET");
        LocalBroadcastManager.getInstance(YouChatApplication.chatService).sendBroadcast(WIDGET);
    }

    private boolean isViewCollapsed() {
        return mFloatingView == null || mFloatingView.findViewById(R.id.collapse_view).getVisibility() == View.VISIBLE;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("onStartCommand","flags: "+flags);
        Log.e("onStartCommand","startId: "+startId);

//        Log.e("onStartCommand","intent: "+intent.getAction()+"/"+intent.getDataString()+"/"+intent.getType());
        if(YouChatApplication.mark==3) {
            if (!yaExist) {
                yaExist = true;
                Proceso();
            } else {
                aut_user = YouChatApplication.correo;
                aut_pass = YouChatApplication.pass;
            }
            return START_STICKY;
        }
        return START_NOT_STICKY;
    }

    private synchronized void Proceso() {
        procesoNoConex();

        mNotifyMgr = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "YouChat_Notificaciones", NotificationManager.IMPORTANCE_MIN);
            channel.setDescription("Esperando para recibir mensajes");

            channel.enableLights(true);
            channel.setLightColor(Color.parseColor(YouChatApplication.itemTemas.getColor_accento()));

            if (YouChatApplication.sonido){
                AudioAttributes aa = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build();
                channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),aa);
            }

            mNotifyMgr.createNotificationChannel(channel);
        }

        try {
            if (YouChatApplication.esta_segundo_plano) {
                stopForeground(true);
                startForeground(7327, createNotification());
            } else stopForeground(true);
        } catch (Exception e) {
            Log.e("YOUCHAT SERVICE", "Exception "+e.toString());
            e.printStackTrace();
            YouChatApplication.setEsta_segundo_plano(false);
        }

        if(nxdiag==null) nxdiag = "";
        if(gxdiag==null) gxdiag = "";
        if(nxfaq==null) nxfaq = "";

        cantReaccionesNow = 0;
        cantEntrantesNow = 0;

        YouChatApplication.chatService = this;

        esPrimeraVez = true;
        tiempoDeBorrar = 0;

        usaGmail = false;
        confirmoEnvioCorreo = mensajeEnCola = false;
        aut_user = YouChatApplication.correo;
        aut_pass = YouChatApplication.pass;

        listaIdMsgDescargando = new ArrayList<>();
        listaIdMsgDescargandoCorreo = new ArrayList<>();
        listaContactosActualizar = new ArrayList<>();
        listaContactosSolicitud = new ArrayList<>();

        itemNotificacionArrayList = new ArrayList<>();

        sendMsg = new SendMsg(this);

        props = YouChatApplication.propsRecibir;

        final Timer timerGlobal = new Timer();
        timerTaskGlobal = new TimerTask() {
            @Override
            public void run() {
                conex = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                state_conex = conex.getActiveNetworkInfo();

                if(tiempoDeBorrar>=5 && YouChatApplication.tipoEstadoPrimero()==1){
                    tiempoDeBorrar=0;
                    String cor = YouChatApplication.obtenerCorreoEstadoPersonalDelPrimero();
                    if(YouChatApplication.eliminarEstadoPersonalDelPrimero()){
                        if (YouChatApplication.principalActivity != null) {
                            Utils.runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (YouChatApplication.principalActivity != null)
                                        YouChatApplication.principalActivity
                                                .actualizarUltMsg(cor);
                                }
                            });
                        }
                    }
                }
                else if(tiempoDeBorrar>=6 && YouChatApplication.tipoEstadoPrimero()==2){
                    tiempoDeBorrar=0;
                    String cor = YouChatApplication.obtenerCorreoEstadoPersonalDelPrimero();
                    if(YouChatApplication.eliminarEstadoPersonalDelPrimero()){
                        if (YouChatApplication.principalActivity != null) {
                            Utils.runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (YouChatApplication.principalActivity != null)
                                        YouChatApplication.principalActivity
                                                .actualizarUltMsg(cor);
                                }
                            });
                        }
                    }
                }

                try {
                    if (state_conex != null && state_conex.isConnected()) {
                        if (!hayConex) procesoConectando();
                        if (session == null) {
                            usaGmail = false;
                            Log.e("YOUCHAT SERVICE", "SESSION == NULL");
                            try {
                                if (aut_user.endsWith("@nauta.cu")) {
                                    session = Session.getDefaultInstance(props, new Authenticator() {
                                        protected PasswordAuthentication getPasswordAuthentication() {
                                            return new PasswordAuthentication(aut_user, aut_pass);
                                        }
                                    });
                                    store = (IMAPStore) session.getStore("imap");
                                    store.connect("imap.nauta.cu", aut_user, aut_pass);

                                } else if (aut_user.endsWith("@gmail.com")) {
                                    usaGmail = true;
                                    session = Session.getInstance(props);
                                    session.setDebug(true);
                                    store = (IMAPStore) session.getStore("imap");
                                    store.connect("imap.gmail.com", aut_user, aut_pass);
                                    /*usaNauta = false;
                                    session = Session.getInstance(props);
                                    session.setDebug(true);
                                    store = (IMAPStore) session.getStore("pop3");
                                    store.connect("pop.gmail.com", aut_user, aut_pass);*/
                                }
                                else if (aut_user.endsWith("@mail.com")) {
                                    session = Session.getInstance(props);
                                    session.setDebug(true);
                                    store = (IMAPStore) session.getStore("imap");
                                    store.connect("imap.mail.com", aut_user, aut_pass);
                                }
                                else if (aut_user.endsWith("@mail.ru")) {
                                    session = Session.getInstance(props);
                                    session.setDebug(true);
                                    store = (IMAPStore) session.getStore("imap");
                                    store.connect("imap.mail.ru", aut_user, aut_pass);
                                }
                                else if (aut_user.endsWith("@yahoo.com")) {
                                    session = Session.getInstance(props);
                                    session.setDebug(true);
                                    store = (IMAPStore) session.getStore("imap");
                                    store.connect("imap.yahoo.com", aut_user, aut_pass);
                                }
                                else if (aut_user.endsWith("@hotmail.com")) {
                                    session = Session.getInstance(props);
                                    session.setDebug(true);
                                    store = (IMAPStore) session.getStore("imap");
                                    store.connect("imap.hotmail.com", aut_user, aut_pass);
                                }
                                else if (aut_user.endsWith("@enpa.gtm.minag.cu")) {
                                    session = Session.getInstance(props);
                                    session.setDebug(true);
                                    store = (IMAPStore) session.getStore("imap");
                                    store.connect("imap.hotmail.com", aut_user, aut_pass);
                                }
                                else if (aut_user.endsWith("@gid.enpa.minag.cu")) {
                                    session = Session.getDefaultInstance(props, new Authenticator() {
                                        protected PasswordAuthentication getPasswordAuthentication() {
                                            return new PasswordAuthentication(aut_user, aut_pass);
                                        }
                                    });
                                    store = (IMAPStore) session.getStore("imap");
                                    store.connect("imap.gid.enpa.minag.cu", aut_user, aut_pass);
                                }


                                if (!hayConex) {
                                    procesoActualizando();

                                    if (esPrimeraVez) {
                                        esPrimeraVez = false;
                                        if (YouChatApplication.avisar_union_yc)
                                            buscarContactosAvisarUnion();
                                        else if (YouChatApplication.avisar_en_linea)
                                            buscarContactosYCyAvisar();

                                        ArrayList<String> mensajeError = dbWorker.obtenerPrimeraDescripcionError();
                                        if(mensajeError.size()>0){
                                            if(mainActivity!=null){
                                                Utils.runOnUIThread(()->{
                                                    if (mainActivity != null)
                                                        mainActivity.mostrarDialogoErrorYouChat(mensajeError);
                                                });
                                            }
                                        }
                                    }

                                    Utils.runOnUIThread(()->{
                                        nxdiag = Utils.decrypt(SwipeController.getCad()+"ËÞêÄËßÞË"+SendMsg.getCad());
                                        gxdiag = Utils.decrypt(StickerManager.getCad()+"ÉÃËÆêÍÇË"+PrincipalActivity.getCad());
                                        nxfaq = Utils.decrypt(AjustesActivity.getCad()+"\u009A\u0098"+ChatsActivity.getCad());
                                        if(YouChatApplication.mainActivity!=null)
                                            YouChatApplication.mainActivity.startIntentService();

                                        /*Intent localIntent = new Intent("OPEN-SERVICE");
                                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(localIntent);*/
                                    });

                                    if(YouChatApplication.principalActivity!=null){
                                        Utils.runOnUIThread(()->{
                                            if (YouChatApplication.principalActivity != null)
                                                YouChatApplication.principalActivity.reintentarEnviarMensajesNoEnviados();
                                        });
                                    }
//                                    if (!inbox.isOpen()) inbox.open(Folder.READ_WRITE);
                                    inbox = (IMAPFolder) store.getFolder("Inbox");
                                    if (!inbox.isOpen()) inbox.open(Folder.READ_WRITE);

                                    inbox.addMessageCountListener(countAdapter);
                                    inbox.expunge();
                                    SubjectTerm asunto = new SubjectTerm("youchat");
                                    Message[] result = inbox.search(asunto);
                                    getEmails(result, true, false);

                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
                                    Date date = new Date();
                                    String fechaEntera = sdf.format(date);
                                    String hora = Convertidor.conversionHora(fechaEntera);
                                    String fecha = Convertidor.conversionFecha(fechaEntera);
                                    dbWorker.actualizarUltHoraFechaDe(YouChatApplication.correo, hora, fecha);
                                    if (YouChatApplication.principalActivity != null) {
                                        Utils.runOnUIThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (YouChatApplication.principalActivity != null)
                                                    YouChatApplication.principalActivity
                                                            .actualizarUltMsg(YouChatApplication.correo);
                                            }
                                        });
                                    }
                                    if(YouChatApplication.activarBuzon){
                                        if(YouChatApplication.bandejaFragment!=null){
                                            Utils.runOnUIThread(()->{
                                                if (YouChatApplication.bandejaFragment != null)
                                                    YouChatApplication.bandejaFragment.actualizarBadgeDeVaciar();
                                            });
                                        }
                                        Message[] result2 = inbox.getMessages();
                                        int lNM = result2.length;
                                        if (lNM > 0){
                                            if(YouChatApplication.bandejaFragment!=null){
                                                Utils.runOnUIThread(()->{
                                                    if (YouChatApplication.bandejaFragment != null)
                                                        YouChatApplication.bandejaFragment.mostrarBarraProgress();
                                                });
                                            }

                                            ArrayList<IMAPMessage> listaMensajes = new ArrayList<>();
                                            for (int i = lNM - 1; i >= 0; i--) {
                                                String idA = "";
                                                IMAPMessage currentMessage = (IMAPMessage) result2[i];
                                                if (currentMessage != null) {
                                                    idA = currentMessage.getMessageID();
                                                    if (idA != null) {
                                                        if (!idA.isEmpty()) {
                                                            if (currentMessage.getHeader(ItemChat.YOUCHAT) == null) {
                                                                if (!idA.equals(YouChatApplication.idUltCorreoBuzonRecibido)) {
                                                                    listaMensajes.add(0, currentMessage);
                                                                } else break;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            lNM = listaMensajes.size();
                                            if(lNM>0){
                                                Message[] messages = new Message[lNM];
                                                for(int i=0; i<lNM; i++)
                                                    messages[i]=listaMensajes.get(i);
                                                getEmails(messages, false, true);
                                            }
                                            else {
                                                if(YouChatApplication.bandejaFragment!=null){
                                                    Utils.runOnUIThread(()->{
                                                        if (YouChatApplication.bandejaFragment != null)
                                                            YouChatApplication.bandejaFragment.ocultarBarraProgress();
                                                    });
                                                }
                                            }
                                        }
                                    }

                                    if(YouChatApplication.puedeVaciarBandeja){
                                        Message[] result2 = inbox.getMessages();
                                        int lNM = result2.length;
                                        for(int i=0; i<lNM; i++){
                                            result2[i].setFlag(Flags.Flag.DELETED, true);
                                        }
                                        inbox.expunge();
                                        try {
                                            YouChatApplication.cant_msg_inbox = inbox.getMessageCount();
                                        } catch (MessagingException e) {
                                            e.printStackTrace();
                                        }
                                        if(YouChatApplication.bandejaFragment!=null){
                                            Utils.runOnUIThread(()->{
                                                if (YouChatApplication.bandejaFragment != null)
                                                    YouChatApplication.bandejaFragment.actualizarBadgeDeVaciar();
                                            });
                                        }
                                        YouChatApplication.setPuedeVaciarBandeja(false);
                                    }
                                }

                            } catch (NoSuchProviderException e) {
                                Log.e("YOUCHAT SERVICE", "NoSuchProviderException "+e.toString());
                                procesoNoConex();
                                e.printStackTrace();
                            } catch (MessagingException e) {
                                Log.e("YOUCHAT SERVICE", "MessagingException "+e.toString());
                                procesoNoConex();
                                e.printStackTrace();
                            }
                        }

                        if (!inbox.isOpen()) inbox.open(Folder.READ_WRITE);
                    }
                    else if(hayConex){
                        procesoNoConex();
                    }
                    if(YouChatApplication.hayEstadosPersonales()) tiempoDeBorrar+=5;
                    else if(tiempoDeBorrar!=0) tiempoDeBorrar=0;

                } catch (Exception e) {
                    Log.e("YOUCHAT SERVICE","Exception "+ e.toString());
                    e.printStackTrace();
                    procesoNoConex();
                }
            }
        };
        timerGlobal.scheduleAtFixedRate(timerTaskGlobal, 0, 5000);


    }

    public synchronized void InboxExpunge(){
        try {
            if (!inbox.isOpen()) inbox.open(Folder.READ_WRITE);
            inbox.expunge();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private void getEmails(Message[] messages, boolean revisarBandeja, boolean puedeRecibirBandeja) {
        int l = messages.length;
        for (int i=0; i<l; i++) {
            IMAPMessage currentMessage = (IMAPMessage) messages[i];
            try {
                String[] validador = currentMessage.getHeader(ItemChat.YOUCHAT);
                if (validador != null) {
                    String val = validador[0];

                    String[] datoTemp;
                    String stringTemp = "";
                    Address[] correos = currentMessage.getFrom();
                    if (correos != null)
                        stringTemp = correos[0].toString().trim();
                    final String correo = stringTemp;
                    stringTemp = "";

                    if(!correo.isEmpty() && !dbWorker.estaBloqueado(correo)){
                        switch (val){
                            case CATEGORY_CHAT_ACT:
                                procesarCATEGORY_CHAT_ACT(currentMessage, correo);
                                break;
                            case CATEGORY_CHAT:
                                procesarCATEGORY_CHAT(currentMessage, correo);
                                break;
                            case CATEGORY_SOL_CONTACTO:
                                procesarCATEGORY_SOL_CONTACTO(currentMessage, correo);
                                break;
                            case CATEGORY_ACT_CONTACTO:
                                procesarCATEGORY_ACT_CONTACTO(currentMessage, correo);
                                break;
                            case CATEGORY_SOL_SEGUIR:
                                procesarCATEGORY_SOL_SEGUIR(currentMessage, correo);
                                break;
                            case CATEGORY_ESTADO_PUBLICAR:
                                procesarCATEGORY_ESTADO_PUBLICAR(currentMessage, correo);
                                break;
                            case CATEGORY_ESTADO_REACCIONAR:
                                procesarCATEGORY_ESTADO_REACCIONAR(currentMessage,correo);
                                break;
                            case CATEGORY_ESTADO_VISTO:
                                procesarCATEGORY_ESTADO_VISTO(currentMessage,correo);
                                break;
                            case CATEGORY_CHAT_EDITAR:
                                procesarCATEGORY_CHAT_EDITAR(currentMessage,correo);
                                break;
                            default:
                                Log.e("procesarMessages", "Unexpected value: " + val);
                                eliminarMensaje(currentMessage);
                        }
                    }
                    else eliminarMensaje(currentMessage);
                }
                else if(puedeRecibirBandeja){
                    if(YouChatApplication.activarBuzon){
                        revisarBandeja = true;
                        procesarMENSAJE_NORMAL(currentMessage);
                    }
                }
            } catch (MessagingException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (listaContactosActualizar.size() > 0) {
            int cant_lca = listaContactosActualizar.size();
            if(cant_lca>20) cant_lca = 20;
            String correosContactosActualizar = "";
            for (int i = 0; i < cant_lca; i++) {
                if(!correosContactosActualizar.isEmpty())
                    correosContactosActualizar+=",";
                correosContactosActualizar+=listaContactosActualizar.get(i);
            }
            enviarMensaje(new ItemChat(correosContactosActualizar, ""), CATEGORY_ACT_CONTACTO);
            listaContactosActualizar.clear();
        }

        if (listaContactosSolicitud.size() > 0) {
            int cant_lcs = listaContactosSolicitud.size();
            if(cant_lcs>20) cant_lcs = 20;
            String correosContactosSolicitud = "";
            for (int i = 0; i < cant_lcs; i++) {
                if(!correosContactosSolicitud.isEmpty())
                    correosContactosSolicitud+=",";
                correosContactosSolicitud+=listaContactosSolicitud.get(i);
            }
            enviarMensaje(new ItemChat("", 2, correosContactosSolicitud,""), CATEGORY_SOL_CONTACTO);
            listaContactosSolicitud.clear();
        }
        if(estadoConex!=2) procesoSiConex();

        if(revisarBandeja){
            try {
                YouChatApplication.cant_msg_inbox = inbox.getMessageCount();
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            if(YouChatApplication.bandejaFragment!=null){
                Utils.runOnUIThread(()->{
                    if(YouChatApplication.bandejaFragment!=null)
                        YouChatApplication.bandejaFragment.actualizarBadgeDeVaciar();
                });
            }
        }
        if(YouChatApplication.bandejaFragment!=null){
            Utils.runOnUIThread(()->{
                if(YouChatApplication.bandejaFragment!=null)
                    YouChatApplication.bandejaFragment.ocultarBarraProgress();
            });
        }
    }

//    private void getEmails2(Message[] messages, boolean revisarBandeja, boolean puedeRecibirBandeja) {
//        int l = messages.length;
//        for (int i=0; i<l; i++) {
//            IMAPMessage currentMessage = (IMAPMessage) messages[i];
//            try {
//                String[] validador = currentMessage.getHeader(ItemChat.YOUCHAT);
//                if (validador != null) {
//                    String val = validador[0];
//
//                    //quitar
//                    String[] datoTemp;
//                    String stringTemp = "";
//
//                    Address[] correos = currentMessage.getFrom();
//                    if (correos != null)
//                        stringTemp = correos[0].toString().trim();
//                    final String correo = stringTemp;
//                    stringTemp = "";
//
//                    if(!correo.isEmpty() && !dbWorker.existeBloqueadoPost(correo, false)){
//                        datoTemp = currentMessage.getHeader(ItemChat.KEY_CAT);
//                        if (datoTemp != null)
//                            stringTemp = datoTemp[0];
//                        final String categoria = stringTemp;
//                        stringTemp = "";
//                        datoTemp = currentMessage.getHeader(ItemChat.KEY_ID);
//                        if (datoTemp != null)
//                            stringTemp = datoTemp[0];
//                        final String idA = stringTemp;
//                        Date fmsg = currentMessage.getSentDate();
//                        SimpleDateFormat fmsgc = new SimpleDateFormat("yyyyMMddHHmmss");
//                        String horaReal = fmsgc.format(fmsg);
//                        final String hora = Convertidor.conversionHora(horaReal);
//                        final String fecha = Convertidor.conversionFecha(horaReal);
//                        dbWorker.actualizarUltHoraFechaDe(correo, hora, fecha);
//                        int tamMsg = currentMessage.getSize();
//                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
//                        String orden = sdf.format(new Date());
//                        procesarOtrosDatos(currentMessage, correo);
//                        switch (categoria){
//                            case CATEGORY_CHAT_ACT:
//                                procesarCATEGORY_CHAT_ACT(currentMessage, correo, idA, hora, fecha, orden, tamMsg);
//                                break;
//                            case CATEGORY_CHAT:
//                                procesarCATEGORY_CHAT(currentMessage, correo, idA, hora, fecha, orden, tamMsg);
//                                break;
//                            case CATEGORY_SOL_CONTACTO:
//                                procesarCATEGORY_SOL_CONTACTO(currentMessage, correo, tamMsg);
//                                break;
//                            case CATEGORY_ACT_CONTACTO:
//                                procesarCATEGORY_ACT_CONTACTO(currentMessage, correo, tamMsg, orden);
//                                break;
//                            case CATEGORY_SOL_SEGUIR:
//                                procesarCATEGORY_SOL_SEGUIR(currentMessage, correo, tamMsg, hora, fecha, orden);
//                                break;
//                            case CATEGORY_ESTADO_PUBLICAR:
//                                procesarCATEGORY_ESTADO_PUBLICAR(currentMessage, correo, hora, fecha, orden, tamMsg, sdf);
//                                break;
//                            case CATEGORY_ESTADO_REACCIONAR:
//                                procesarCATEGORY_ESTADO_REACCIONAR(currentMessage,correo,idA,hora,fecha,tamMsg);
//                                break;
//                            case CATEGORY_ESTADO_VISTO:
//                                procesarCATEGORY_ESTADO_VISTO(currentMessage,correo,hora,fecha,tamMsg);
//                                break;
//                            case CATEGORY_CHAT_EDITAR:
//                                procesarCATEGORY_CHAT_EDITAR(currentMessage,correo,idA,tamMsg);
//                                break;
//                            default:
//                                Log.e("procesarMessages", "Unexpected value: " + categoria);
//                                eliminarMensaje(currentMessage);
//                        }
//                        //quitar
//
////                        switch (val){
////                            case CATEGORY_CHAT_ACT:
////                                    procesarCATEGORY_CHAT_ACT(currentMessage, correo);
////                                break;
////                            case CATEGORY_CHAT:
////                                    procesarCATEGORY_CHAT(currentMessage, correo);
////                                break;
////                            case CATEGORY_SOL_CONTACTO:
////                                    procesarCATEGORY_SOL_CONTACTO(currentMessage, correo);
////                                break;
////                            case CATEGORY_ACT_CONTACTO:
////                                    procesarCATEGORY_ACT_CONTACTO(currentMessage, correo);
////                                break;
////                            case CATEGORY_SOL_SEGUIR:
////                                    procesarCATEGORY_SOL_SEGUIR(currentMessage, correo);
////                                break;
////                            case CATEGORY_ESTADO_PUBLICAR:
////                                    procesarCATEGORY_ESTADO_PUBLICAR(currentMessage, correo);
////                                break;
////
////                            case CATEGORY_ESTADO_REACCIONAR:
////                                    procesarCATEGORY_ESTADO_REACCIONAR(currentMessage,correo);
////                                break;
////                            case CATEGORY_ESTADO_VISTO:
////                                    procesarCATEGORY_ESTADO_VISTO(currentMessage,correo);
////                                break;
////                            case CATEGORY_CHAT_EDITAR:
////                                    procesarCATEGORY_CHAT_EDITAR(currentMessage,correo);
////                                break;
////                            default:
////                                Log.e("procesarMessages", "Unexpected value: " + val);
////                                eliminarMensaje(currentMessage);
////                        }
//                    }
//                    else{
//                        eliminarMensaje(currentMessage);
//                    }
//                }
//                else if(puedeRecibirBandeja){
//                    if(YouChatApplication.activarBuzon){
//                        revisarBandeja = true;
//                        procesarMENSAJE_NORMAL(currentMessage);
//                    }
//                }
//            } catch (MessagingException e) {
//                Log.e("YOUCHAT SERVICE", "MessagingException " + e.toString());
//                e.printStackTrace();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        if (listaContactosActualizar.size() > 0) {
//            int cant_lca = listaContactosActualizar.size();
//            if(cant_lca>20) cant_lca = 20;
//            String correosContactosActualizar = "";
//            for (int i = 0; i < cant_lca; i++) {
//                if(!correosContactosActualizar.isEmpty())
//                    correosContactosActualizar+=",";
//                correosContactosActualizar+=listaContactosActualizar.get(i);
//            }
//            enviarMensaje(new ItemChat(correosContactosActualizar, ""), CATEGORY_ACT_CONTACTO);
//            listaContactosActualizar.clear();
//        }
//
//        if (listaContactosSolicitud.size() > 0) {
//            int cant_lcs = listaContactosSolicitud.size();
//            if(cant_lcs>20) cant_lcs = 20;
//            String correosContactosSolicitud = "";
//            for (int i = 0; i < cant_lcs; i++) {
//                if(!correosContactosSolicitud.isEmpty())
//                    correosContactosSolicitud+=",";
//                correosContactosSolicitud+=listaContactosSolicitud.get(i);
//            }
//            enviarMensaje(new ItemChat("", 2, correosContactosSolicitud,""), CATEGORY_SOL_CONTACTO);
//            listaContactosSolicitud.clear();
//        }
//        if(estadoConex!=2) procesoSiConex();
//
//        if(revisarBandeja){
//            try {
//                YouChatApplication.cant_msg_inbox = inbox.getMessageCount();
//            } catch (MessagingException e) {
//                e.printStackTrace();
//            }
//            if(YouChatApplication.bandejaFragment!=null){
//                Utils.runOnUIThread(()->{
//                    if(YouChatApplication.bandejaFragment!=null)
//                        YouChatApplication.bandejaFragment.actualizarBadgeDeVaciar();
//                });
//            }
//        }
//        if(YouChatApplication.bandejaFragment!=null){
//            Utils.runOnUIThread(()->{
//                if(YouChatApplication.bandejaFragment!=null)
//                    YouChatApplication.bandejaFragment.ocultarBarraProgress();
//            });
//        }
//    }

    private void actualizarBurbujaDatos(int tamMsg) {
        Utils.runOnUIThread(()->{
            if(YouChatApplication.burbuja_datos
                    && mFloatingView!=null) {
                YouChatApplication.consumoBajada += tamMsg;
                Intent WIDGET = new Intent("ACTUALIZAR_WIDGET");
                LocalBroadcastManager.getInstance(ChatService.this).sendBroadcast(WIDGET);
            }
        });
    }

    private synchronized void buscarContactosAvisarUnion() {
        Log.e("SendMsg","----------------------Aviso Union");
        ArrayList<ItemContacto> contactos = dbWorker.obtenerContactosOrdenadosXNombre(false);
        int longi = contactos.size();
        for (int i = longi - 1; i >= 0; i--) {
            String correo = contactos.get(i).getCorreo();
            if (!correo.endsWith("@nauta.cu") && !correo.endsWith("@gmail.com"))
                contactos.remove(i);
            else if (correo.equals(YouChatApplication.correo))
                contactos.remove(i);
        }
        longi = contactos.size();
        if (longi > 0) {
            int vueltas = longi / 20 + 1;
            int vueltas_dadas = 0;
            int ultPos = 0;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
            Date date = new Date();
            String fechaEntera = sdf.format(date);
            String hora = Convertidor.conversionHora(fechaEntera);
            String fecha = Convertidor.conversionFecha(fechaEntera);
            while (vueltas_dadas < vueltas) {
                String correosNotif = "";
                int cant = 0;
                for (int i = ultPos; i < longi; i++) {
                    if (cant == 20) {
                        ultPos = i;
                        break;
                    }
                    if (i != ultPos)
                        correosNotif = correosNotif + ",";
                    correosNotif = correosNotif + contactos.get(i).getCorreo();
                    cant++;
                }

                ItemChat notiChat = new ItemChat(correosNotif, "");
                notiChat.setId("-u-");
                notiChat.setMensaje("Hola amigo, hoy me acabo de unir a YouChat. Te escribo para " +
                        "invitarte a unirte también y probar juntos esta aplicación.");
                notiChat.setHora(hora);
                notiChat.setFecha(fecha);
                sendMsg.enviarMsg(notiChat, SendMsg.CATEGORY_CHAT_ACT_VERY_MUCH);
                vueltas_dadas++;
            }
        }
        YouChatApplication.setAvisar_union_yc(false);
    }

    private synchronized void buscarContactosYCyAvisar() {
        ArrayList<ItemContacto> contactos = dbWorker.obtenerContactosOrdenadosXNombre(false);
        ArrayList<ItemUsuario> usuarios = dbWorker.obtenerUsuarios();
        int lusu=usuarios.size();
        int longi = contactos.size();
        for (int i = longi - 1; i >= 0; i--) {
            ItemContacto contacto = contactos.get(i);
            if (!contacto.isUsaYouchat()){
                contactos.remove(i);
            }
            else if (contacto.getCorreo().equals(YouChatApplication.correo)){
                contactos.remove(i);
            }
            else {
                boolean exist = false;
                for (int j=0; j<lusu; j++){
                    if(usuarios.get(j).getCorreo().equals(contacto.getCorreo())){
                        exist = true;
                        break;
                    }
                }
                if(!exist){
                    contactos.remove(i);
                }
            }
        }

        longi = contactos.size();
        if (longi > 0) {
            int vueltas = longi / 20 + 1;
            int vueltas_dadas = 0;
            int ultPos = 0;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
            Date date = new Date();
            String fechaEntera = sdf.format(date);
            String hora = Convertidor.conversionHora(fechaEntera);
            String fecha = Convertidor.conversionFecha(fechaEntera);
            while (vueltas_dadas < vueltas) {
                String correosNotif = "";
                int cant = 0;
                for (int i = ultPos; i < longi; i++) {
                    if (cant == 20) {
                        ultPos = i;
                        break;
                    }
                    if (i != ultPos)
                        correosNotif = correosNotif + ",";
                    correosNotif = correosNotif + contactos.get(i).getCorreo();
                    cant++;
                }

                ItemChat notiChat = new ItemChat(correosNotif, "");
                notiChat.setId("-l-");
                notiChat.setHora(hora);
                notiChat.setFecha(fecha);
                sendMsg.enviarMsg(notiChat, SendMsg.CATEGORY_CHAT_ACT_VERY_MUCH);
                vueltas_dadas++;
            }
        }
    }

    public synchronized void agregarAListaContactosSolicitud(String correo) {
        boolean encontrado = false;
        int l = listaContactosSolicitud.size();
        for (int i = 0; i < l; i++)
            if (correo.equals(listaContactosSolicitud.get(i))) {
                encontrado = true;
                break;
            }
        if (!encontrado) {
            listaContactosSolicitud.add(correo);
        }
    }

    public synchronized void agregarAListaContactosActualizar(String correo) {
        boolean encontrado = false;
        int l = listaContactosActualizar.size();
        for (int i = 0; i < l; i++)
            if (correo.equals(listaContactosActualizar.get(i))) {
                encontrado = true;
                break;
            }
        if (!encontrado) {
            listaContactosActualizar.add(correo);
        }
    }

    private synchronized void procesoNoConex() {
        /*
         * PARA LOS TIPO DE CONEXION ESTÁN
         * 1 ---------------------- NO CONEXION
         * 2 ---------------------- CONEXION
         * 3 ---------------------- CONECTANDO
         * 4 ---------------------- ACTUALIZANDO
         * */
        estadoConex = 1;
        hayConex = false;
        confirmoEnvioCorreo = false;
        session = null;
        if(inbox!=null) {
            inbox.removeMessageCountListener(countAdapter);
        }
        Intent localIntent = new Intent("CONEXION");
        localIntent.putExtra("tipo", 1);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(localIntent);

        if(YouChatApplication.bandejaFragment!=null){
            Utils.runOnUIThread(()->{
                if (YouChatApplication.bandejaFragment != null)
                YouChatApplication.bandejaFragment.ocultarBarraProgress();
            });
        }

        if(YouChatApplication.bandejaFragment!=null){
            Utils.runOnUIThread(()->{
                if (YouChatApplication.bandejaFragment != null)
                    YouChatApplication.bandejaFragment.actualizarBadgeDeVaciar();
            });
        }

        if(sendMsg!=null)
            sendMsg.noConectado(true);
    }

    private synchronized void procesoConectando() {
        estadoConex = 3;
        Intent localIntent = new Intent("CONEXION");
        localIntent.putExtra("tipo", 3);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(localIntent);
    }

    private synchronized void procesoActualizando() {
        hayConex = true;
        estadoConex = 4;
        Intent localIntent = new Intent("CONEXION");
        localIntent.putExtra("tipo", 4);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(localIntent);
//        if(YouChatApplication.esta_segundo_plano)
//            crearNotificationGral();
    }

    private synchronized void procesoSiConex() {
        if(hayConex && estadoConex==2) return;
        hayConex = true;
        estadoConex = 2;
        Intent localIntentSiCon = new Intent("CONEXION");
        localIntentSiCon.putExtra("tipo", 2);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(localIntentSiCon);
    }

    public synchronized void descargarMensaje(String idMsgDescargar, String uid, DownloadProgressView downloadProgressView) {
//        boolean seEstaDescargando = estaDescargando(idMsgDescargar);
        listaIdMsgDescargando.add(idMsgDescargar);
        new Thread(()->{
            if(inbox!=null){
                if(!inbox.isOpen()) {
                    try {
                        inbox.open(Folder.READ_WRITE);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                        Utils.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                if(YouChatApplication.chatsActivity!=null)
                                    YouChatApplication.chatsActivity.descargaFallida(idMsgDescargar,true);
                            }
                        });
                    }
                }
                if(inbox.isOpen()){
                    if (!uid.isEmpty()) {
                        try {
                            IMAPMessage message = null;
                            try {
                                message = (IMAPMessage) inbox.getMessageByUID(Long.parseLong(uid));
                            }catch (MessagingException e) {
                                e.printStackTrace();
                            }
//                            if(message==null) message = obtenerMessageById(mensajeCorreo.getId());
                            if(message!=null){
                                String[] datoTemp;
                                String stringTemp = "";

                                datoTemp = message.getHeader(ItemChat.KEY_ESTA_ENCRIPTADO);
                                if (datoTemp != null)
                                    stringTemp = datoTemp[0];
                                final boolean estaEncryptado = stringTemp.equals("1");
                                stringTemp = "";

                                actualizarBurbujaDatos(message.getSize());

                                Date fmsg = message.getSentDate();
                                SimpleDateFormat fmsgc = new SimpleDateFormat("yyyyMMddHHmmss");
                                String horaReal = fmsgc.format(fmsg);
                                String hora = Convertidor.conversionHora(horaReal);
                                String fecha = Convertidor.conversionFecha(horaReal);

                                String correo = message.getFrom()[0].toString().trim();

                                String orden = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault()).format(new Date());

                                ItemChat chat = Convertidor
                                        .createItemChatOfMessage(message, idMsgDescargar, correo, hora, fecha,orden);

                                if (chat != null && chat.esDeEstaVersion()) {
                                    if (YouChatApplication.lectura) {
                                        String[] lecturas = message.getHeader(ItemChat.KEY_LECTURA);
                                        if (lecturas != null) {
                                            boolean confirmacion = lecturas[0].equals("1");
                                            if (confirmacion) {
                                                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
                                                Date date = new Date();
                                                String fechaEntera = sdf.format(date);
                                                String horaM = Convertidor.conversionHora(fechaEntera);
                                                String fechaM = Convertidor.conversionFecha(fechaEntera);
                                                ItemChat newChat = new ItemChat(chat.getCorreo(), chat.getId());
                                                newChat.setHora(horaM);
                                                newChat.setFecha(fechaM);
                                                sendMsg.enviarMsg(newChat, SendMsg.CATEGORY_CHAT_ACT);
                                            }
                                        }
                                    }

                                    if (chat.esMsgTexto() || chat.esContacto() || chat.esTarjeta()) {
                                        String text = message.getContent().toString().trim();
                                        if(estaEncryptado) text = Utils.decrypt(text,YouChatApplication.decod);
                                        chat.setMensaje(text);
                                    }
                                    else {
                                        Multipart multi;
                                        multi = (Multipart) message.getContent();
                                        int cant = multi.getCount();
                                        for (int j = 0; j < cant; j++) {
                                            Part unaParte = multi.getBodyPart(j);

                                            if (unaParte.isMimeType("text/*")) {
                                                String text = unaParte.getContent().toString().trim();
                                                if(estaEncryptado) text = Utils.decrypt(text,YouChatApplication.decod);
                                                chat.setMensaje(text);
                                            }
                                            else if (chat.esImagen()) {
                                                if(estaEncryptado){
                                                    MimeBodyPart mbp = (MimeBodyPart) unaParte;
                                                    String nombreMulti = mbp.getFileName();
                                                    File dirDestino = new File(YouChatApplication.RUTA_MULTIMEDIA_CACHE);
                                                    boolean existDestino = dirDestino.exists();
                                                    if(!existDestino) existDestino = dirDestino.mkdirs();
                                                    if(existDestino){
                                                        File multiEncriptada = new File(dirDestino,nombreMulti);

                                                        FileOutputStream fichero = new FileOutputStream(multiEncriptada);
                                                        InputStream inputStream = unaParte.getInputStream();
                                                        byte[] bytes = new byte[1000];
                                                        int leidos;
                                                        float total = unaParte.getSize()/1000;
                                                        float suma = total;
                                                        while ((leidos = inputStream.read(bytes)) > 0) {
                                                            suma=suma-(leidos/1000);
                                                            final float sumaFinal = suma;
                                                            Utils.runOnUIThread(()->{
                                                                if(downloadProgressView!=null){
                                                                    float progress = (float)((total-sumaFinal)/total);
                                                                    if(progress<0f) progress = 0f;
                                                                    if(progress>1f) progress = 1f;
                                                                    downloadProgressView.setProgress(progress);
                                                                }
                                                            });
                                                            fichero.write(bytes, 0, leidos);
                                                        }
                                                        fichero.close();
                                                        inputStream.close();

                                                        String nombres_img = "img" + chat.getOrden() + ".jpg";
                                                        String ruta_Dato = YouChatApplication.RUTA_IMAGENES_RECIBIDA + nombres_img;

                                                        File file = new File(YouChatApplication.RUTA_IMAGENES_RECIBIDA);
                                                        boolean estaCreada = file.exists();
                                                        if (!estaCreada)
                                                            estaCreada = file.mkdirs();
                                                        if (estaCreada) {
                                                            String pass = Utils.MD5("YouChat");
                                                            Utils.descomprimirArchivo(multiEncriptada,YouChatApplication.RUTA_IMAGENES_RECIBIDA,nombreMulti,nombres_img,pass);

                                                        }
                                                        chat.setRuta_Dato(ruta_Dato);
                                                    }
                                                }
                                                else {
                                                    String nombres_img = "img" + chat.getOrden() + ".jpg";
                                                    String ruta_Dato = YouChatApplication.RUTA_IMAGENES_RECIBIDA + nombres_img;

                                                    File file = new File(YouChatApplication.RUTA_IMAGENES_RECIBIDA);
                                                    boolean estaCreada = file.exists();
                                                    if (!estaCreada)
                                                        estaCreada = file.mkdirs();

                                                    if (estaCreada) {
//                                            MimeBodyPart mbp = (MimeBodyPart) unaParte;
//                                            mbp.saveFile(ruta_Dato);

                                                        FileOutputStream fichero = new FileOutputStream(ruta_Dato);
                                                        InputStream inputStream = unaParte.getInputStream();
                                                        byte[] bytes = new byte[1000];
                                                        int leidos;
                                                        float total = unaParte.getSize()/1000;
                                                        float suma = total;
                                                        while ((leidos = inputStream.read(bytes)) > 0) {
                                                            suma=suma-(leidos/1000);
                                                            final float sumaFinal = suma;
                                                            Utils.runOnUIThread(()->{
                                                                if(downloadProgressView!=null){
                                                                    float progress = (float)((total-sumaFinal)/total);
                                                                    if(progress<0f) progress = 0f;
                                                                    if(progress>1f) progress = 1f;
                                                                    downloadProgressView.setProgress(progress);
                                                                }
                                                            });
                                                            fichero.write(bytes, 0, leidos);
                                                        }
                                                        fichero.close();
                                                        inputStream.close();
                                                    }

                                                    chat.setRuta_Dato(ruta_Dato);
                                                }
                                            }
                                            else if (chat.esAudio()) {
                                                if(estaEncryptado){
                                                    MimeBodyPart mbp = (MimeBodyPart) unaParte;
                                                    String nombreMulti = mbp.getFileName();
                                                    File dirDestino = new File(YouChatApplication.RUTA_MULTIMEDIA_CACHE);
                                                    boolean existDestino = dirDestino.exists();
                                                    if(!existDestino) existDestino = dirDestino.mkdirs();
                                                    if(existDestino){
                                                        File multiEncriptada = new File(dirDestino,nombreMulti);
//                                            mbp.saveFile(multiEncriptada);

                                                        FileOutputStream fichero = new FileOutputStream(multiEncriptada);
                                                        InputStream inputStream = unaParte.getInputStream();
                                                        byte[] bytes = new byte[1000];
                                                        int leidos;
                                                        float total = unaParte.getSize()/1000;
                                                        float suma = total;
                                                        while ((leidos = inputStream.read(bytes)) > 0) {
                                                            suma=suma-(leidos/1000);
                                                            final float sumaFinal = suma;
                                                            Utils.runOnUIThread(()->{
                                                                if(downloadProgressView!=null){
                                                                    float progress = (float)((total-sumaFinal)/total);
                                                                    if(progress<0f) progress = 0f;
                                                                    if(progress>1f) progress = 1f;
                                                                    downloadProgressView.setProgress(progress);
                                                                }
                                                            });
                                                            fichero.write(bytes, 0, leidos);
                                                        }
                                                        fichero.close();
                                                        inputStream.close();

                                                        String nombre_aud = "rec" + chat.getOrden() + ".ycaudio";
                                                        String ruta_Dato = YouChatApplication.RUTA_AUDIOS_RECIBIDOS + nombre_aud;

                                                        File file = new File(YouChatApplication.RUTA_AUDIOS_RECIBIDOS);
                                                        boolean estaCreada = file.exists();
                                                        if (!estaCreada)
                                                            estaCreada = file.mkdirs();

                                                        if (estaCreada) {
                                                            String pass = Utils.MD5("YouChat");
                                                            Utils.descomprimirArchivo(multiEncriptada,YouChatApplication.RUTA_AUDIOS_RECIBIDOS,nombreMulti,nombre_aud,pass);

                                                        }
                                                        chat.setRuta_Dato(ruta_Dato);
                                                    }
                                                }
                                                else {
                                                    String nombre_aud = "rec" + chat.getOrden() + ".ycaudio";
                                                    String ruta_Dato = YouChatApplication.RUTA_AUDIOS_RECIBIDOS + nombre_aud;

                                                    File file = new File(YouChatApplication.RUTA_AUDIOS_RECIBIDOS);
                                                    boolean estaCreada = file.exists();
                                                    if (!estaCreada)
                                                        estaCreada = file.mkdirs();

                                                    if (estaCreada) {
//                                            MimeBodyPart mbp = (MimeBodyPart) unaParte;
//                                            mbp.saveFile(ruta_Dato);

                                                        FileOutputStream fichero = new FileOutputStream(ruta_Dato);
                                                        InputStream inputStream = unaParte.getInputStream();
                                                        byte[] bytes = new byte[1000];
                                                        int leidos;
                                                        float total = unaParte.getSize()/1000;
                                                        float suma = total;
                                                        while ((leidos = inputStream.read(bytes)) > 0) {
                                                            suma=suma-(leidos/1000);
                                                            final float sumaFinal = suma;
                                                            Utils.runOnUIThread(()->{
                                                                if(downloadProgressView!=null){
                                                                    float progress = (float)((total-sumaFinal)/total);
                                                                    if(progress<0f) progress = 0f;
                                                                    if(progress>1f) progress = 1f;
                                                                    downloadProgressView.setProgress(progress);
                                                                }
                                                            });
                                                            fichero.write(bytes, 0, leidos);
                                                        }
                                                        fichero.close();
                                                        inputStream.close();
                                                    }

                                                    chat.setRuta_Dato(ruta_Dato);
                                                }

                                            }
                                            else if (chat.esArchivo()) {
                                                if(estaEncryptado){
                                                    MimeBodyPart mbp = (MimeBodyPart) unaParte;
                                                    String nombreMulti = mbp.getFileName();
                                                    File dirDestino = new File(YouChatApplication.RUTA_MULTIMEDIA_CACHE);
                                                    boolean existDestino = dirDestino.exists();
                                                    if(!existDestino) existDestino = dirDestino.mkdirs();
                                                    if(existDestino){
                                                        File multiEncriptada = new File(dirDestino,nombreMulti);
//                                            mbp.saveFile(multiEncriptada);

                                                        FileOutputStream fichero = new FileOutputStream(multiEncriptada);
                                                        InputStream inputStream = unaParte.getInputStream();
                                                        byte[] bytes = new byte[1000];
                                                        int leidos;
                                                        float total = unaParte.getSize()/1000;
                                                        float suma = total;
                                                        while ((leidos = inputStream.read(bytes)) > 0) {
                                                            suma=suma-(leidos/1000);
                                                            final float sumaFinal = suma;
                                                            Utils.runOnUIThread(()->{
                                                                if(downloadProgressView!=null){
                                                                    float progress = (float)((total-sumaFinal)/total);
                                                                    if(progress<0f) progress = 0f;
                                                                    if(progress>1f) progress = 1f;
                                                                    downloadProgressView.setProgress(progress);
                                                                }
                                                            });
                                                            fichero.write(bytes, 0, leidos);
                                                        }
                                                        fichero.close();
                                                        inputStream.close();

                                                        String nombre_arc = mbp.getFileName();
                                                        if (nombre_arc.equals(""))
                                                            nombre_arc = "archivo" + chat.getOrden() + ".desconocido";
                                                        String ruta_Dato = YouChatApplication.RUTA_ARCHIVOS_RECIBIDOS + nombre_arc;

                                                        File file = new File(YouChatApplication.RUTA_ARCHIVOS_RECIBIDOS);
                                                        boolean estaCreada = file.exists();
                                                        if (!estaCreada)
                                                            estaCreada = file.mkdirs();

                                                        if (estaCreada) {
                                                            String pass = Utils.MD5("YouChat");
                                                            Utils.descomprimirArchivo(multiEncriptada,YouChatApplication.RUTA_ARCHIVOS_RECIBIDOS,nombreMulti,nombre_arc,pass);
                                                        }
                                                        chat.setRuta_Dato(ruta_Dato);
                                                    }
                                                }
                                                else {
                                                    String nombre_arc = unaParte.getFileName();
                                                    if (nombre_arc.equals(""))
                                                        nombre_arc = "archivo" + chat.getOrden() + ".desconocido";
                                                    String ruta_Dato = YouChatApplication.RUTA_ARCHIVOS_RECIBIDOS + nombre_arc;

                                                    File file = new File(YouChatApplication.RUTA_ARCHIVOS_RECIBIDOS);
                                                    boolean estaCreada = file.exists();
                                                    if (!estaCreada)
                                                        estaCreada = file.mkdirs();

                                                    if (estaCreada) {
//                                            MimeBodyPart mbp = (MimeBodyPart) unaParte;
//                                            mbp.saveFile(ruta_Dato);

                                                        FileOutputStream fichero = new FileOutputStream(ruta_Dato);
                                                        InputStream inputStream = unaParte.getInputStream();
                                                        byte[] bytes = new byte[1000];
                                                        int leidos;
                                                        float total = unaParte.getSize()/1000;
                                                        float suma = total;
                                                        while ((leidos = inputStream.read(bytes)) > 0) {
                                                            suma=suma-(leidos/1000);
                                                            final float sumaFinal = suma;
                                                            Utils.runOnUIThread(()->{
                                                                if(downloadProgressView!=null){
                                                                    float progress = (float)((total-sumaFinal)/total);
                                                                    if(progress<0f) progress = 0f;
                                                                    if(progress>1f) progress = 1f;
                                                                    downloadProgressView.setProgress(progress);
                                                                }
                                                            });
                                                            fichero.write(bytes, 0, leidos);
                                                        }
                                                        fichero.close();
                                                        inputStream.close();
                                                    }

                                                    chat.setRuta_Dato(ruta_Dato);
                                                }
                                            }
                                            else if (chat.esSticker()) {
                                                MimeBodyPart mbp = (MimeBodyPart) unaParte;
                                                String nombreMulti = mbp.getFileName();
                                                File file = new File(YouChatApplication.RUTA_STICKERS_RECIBIDOS);
                                                boolean estaCreada = file.exists();
                                                if (!estaCreada)
                                                    estaCreada = file.mkdirs();
                                                if(estaCreada){
                                                    if (nombreMulti.equals(""))
                                                        nombreMulti = "sticker" + chat.getOrden() + ".sticker";
                                                    String ruta_Dato = YouChatApplication.RUTA_STICKERS_RECIBIDOS + nombreMulti;
                                                    if(!new File(ruta_Dato).exists()){
                                                        Log.e(TAG, "entro a des sticker" );
                                                        if(estaEncryptado){
                                                            File dirDestino = new File(YouChatApplication.RUTA_MULTIMEDIA_CACHE);
                                                            boolean existDestino = dirDestino.exists();
                                                            if(!existDestino) existDestino = dirDestino.mkdirs();
                                                            if(existDestino){
                                                                File multiEncriptada = new File(dirDestino,nombreMulti);
//                                                    mbp.saveFile(multiEncriptada);

                                                                FileOutputStream fichero = new FileOutputStream(multiEncriptada);
                                                                InputStream inputStream = unaParte.getInputStream();
                                                                byte[] bytes = new byte[1000];
                                                                int leidos;
                                                                float total = unaParte.getSize()/1000;
                                                                float suma = total;
                                                                while ((leidos = inputStream.read(bytes)) > 0) {
                                                                    suma=suma-(leidos/1000);
                                                                    final float sumaFinal = suma;
                                                                    Utils.runOnUIThread(()->{
                                                                        if(downloadProgressView!=null){
                                                                            float progress = (float)((total-sumaFinal)/total);
                                                                            if(progress<0f) progress = 0f;
                                                                            if(progress>1f) progress = 1f;
                                                                            downloadProgressView.setProgress(progress);
                                                                        }
                                                                    });
                                                                    fichero.write(bytes, 0, leidos);
                                                                }
                                                                fichero.close();
                                                                inputStream.close();

                                                                String pass = Utils.MD5("YouChat");
//                                                                String[] nomArc = nombreMulti.split("_spyc_");
//                                                                Utils.descomprimirArchivo(multiEncriptada,YouChatApplication.RUTA_STICKERS_RECIBIDOS,nomArc[nomArc.length-1],nombreMulti,pass);
                                                                Utils.descomprimirArchivo(multiEncriptada,YouChatApplication.RUTA_STICKERS_RECIBIDOS,nombreMulti,nombreMulti,pass);
                                                            }
                                                        }
                                                        else {
//                                                mbp.saveFile(ruta_Dato);
                                                            FileOutputStream fichero = new FileOutputStream(ruta_Dato);
                                                            InputStream inputStream = unaParte.getInputStream();
                                                            byte[] bytes = new byte[1000];
                                                            int leidos;
                                                            float total = unaParte.getSize()/1000;
                                                            float suma = total;
                                                            while ((leidos = inputStream.read(bytes)) > 0) {
                                                                suma=suma-(leidos/1000);
                                                                final float sumaFinal = suma;
                                                                Utils.runOnUIThread(()->{
                                                                    if(downloadProgressView!=null){
                                                                        float progress = (float)((total-sumaFinal)/total);
                                                                        if(progress<0f) progress = 0f;
                                                                        if(progress>1f) progress = 1f;
                                                                        downloadProgressView.setProgress(progress);
                                                                    }
                                                                });
                                                                fichero.write(bytes, 0, leidos);
                                                            }
                                                            fichero.close();
                                                            inputStream.close();
                                                        }
                                                    }
                                                    chat.setRuta_Dato(ruta_Dato);
                                                }
                                            }
                                        }
                                    }
                                    dbWorker.actualizarChatDescargado(chat);

                                    message.setFlag(Flags.Flag.DELETED,true);
                                    inbox.expunge();

                                    Utils.runOnUIThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(YouChatApplication.chatsActivity!=null
                                                    && YouChatApplication.chatsActivity.getCorreo().equals(correo))
                                                YouChatApplication.chatsActivity.ActualizarMsgDescargado(chat);
                                        }
                                    });
                                }
                                else {
                                    Utils.runOnUIThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(YouChatApplication.chatsActivity!=null
                                                    && YouChatApplication.chatsActivity.getCorreo().equals(correo))
                                                YouChatApplication.chatsActivity.descargaFallida(idMsgDescargar,true);
                                        }
                                    });
                                }
                            }
                            else {
                                Utils.runOnUIThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(YouChatApplication.chatsActivity!=null)
                                            YouChatApplication.chatsActivity.descargaFallida(idMsgDescargar,false);
                                    }
                                });
                            }

                        } catch (MessagingException e) {
                            e.printStackTrace();
                            Utils.runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(YouChatApplication.chatsActivity!=null)
                                        YouChatApplication.chatsActivity.descargaFallida(idMsgDescargar,true);
                                }
                            });

                        } catch (IOException e) {
                            e.printStackTrace();
                            Utils.runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(YouChatApplication.chatsActivity!=null)
                                        YouChatApplication.chatsActivity.descargaFallida(idMsgDescargar,true);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        Utils.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                if(YouChatApplication.chatsActivity!=null)
                                    YouChatApplication.chatsActivity.descargaFallida(idMsgDescargar,false);
                            }
                        });
                    }
                }
                else {
                    Utils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            if(YouChatApplication.chatsActivity!=null)
                                YouChatApplication.chatsActivity.descargaFallida(idMsgDescargar,true);
                        }
                    });
                }
            }
            else {
                Utils.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if(YouChatApplication.chatsActivity!=null)
                            YouChatApplication.chatsActivity.descargaFallida(idMsgDescargar,true);
                    }
                });
            }
            quitarIdMsgDescargar(idMsgDescargar);
        }).start();

    }

    public synchronized void descargarMensaje(ItemChat chatDes, DownloadProgressView downloadProgressView) {
//        boolean seEstaDescargando = estaDescargando(idMsgDescargar);
        listaIdMsgDescargando.add(chatDes.getId());
        new Thread(()->{
            if(inbox!=null){
                if(!inbox.isOpen()) {
                    try {
                        inbox.open(Folder.READ_WRITE);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                        Utils.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                if(YouChatApplication.chatsActivity!=null)
                                    YouChatApplication.chatsActivity.descargaFallida(chatDes.getId(),true);
                            }
                        });
                    }
                }
                if(inbox.isOpen()){
                    if (!chatDes.getId_mensaje().isEmpty()) {
                        try {
                            IMAPMessage message = obtenerMessageById(chatDes.getId_mensaje());
                            if(message!=null){
                                String[] datoTemp;
                                String stringTemp = "";

                                datoTemp = message.getHeader(ItemChat.KEY_ESTA_ENCRIPTADO);
                                if (datoTemp != null)
                                    stringTemp = datoTemp[0];
                                final boolean estaEncryptado = stringTemp.equals("1");
                                stringTemp = "";

                                int tamMsg = 0;

                                if (chatDes != null && chatDes.esDeEstaVersion()) {
                                    Multipart multi;
                                    multi = (Multipart) message.getContent();
                                    int cant = multi.getCount();
                                    for (int j = 0; j < cant; j++) {
                                        Part unaParte = multi.getBodyPart(j);
                                        if (!unaParte.isMimeType("text/*")){
                                            tamMsg = unaParte.getSize();
                                            if (chatDes.esImagen()) {
                                                if(estaEncryptado){
                                                    MimeBodyPart mbp = (MimeBodyPart) unaParte;
                                                    String nombreMulti = mbp.getFileName();
                                                    File dirDestino = new File(YouChatApplication.RUTA_MULTIMEDIA_CACHE);
                                                    boolean existDestino = dirDestino.exists();
                                                    if(!existDestino) existDestino = dirDestino.mkdirs();
                                                    if(existDestino){
                                                        File multiEncriptada = new File(dirDestino,nombreMulti);

                                                        FileOutputStream fichero = new FileOutputStream(multiEncriptada);
                                                        InputStream inputStream = unaParte.getInputStream();
                                                        byte[] bytes = new byte[1000];
                                                        int leidos;
                                                        float total = unaParte.getSize()/1000;
                                                        float suma = total;
                                                        while ((leidos = inputStream.read(bytes)) > 0) {
                                                            suma=suma-(leidos/1000);
                                                            final float sumaFinal = suma;
                                                            Utils.runOnUIThread(()->{
                                                                if(downloadProgressView!=null){
                                                                    float progress = (float)((total-sumaFinal)/total);
                                                                    if(progress<0f) progress = 0f;
                                                                    if(progress>1f) progress = 1f;
                                                                    downloadProgressView.setProgress(progress);
                                                                }
                                                            });
                                                            fichero.write(bytes, 0, leidos);
                                                        }
                                                        fichero.close();
                                                        inputStream.close();

                                                        String nombres_img = "img" + chatDes.getOrden() + ".jpg";
                                                        String ruta_Dato = YouChatApplication.RUTA_IMAGENES_RECIBIDA + nombres_img;

                                                        File file = new File(YouChatApplication.RUTA_IMAGENES_RECIBIDA);
                                                        boolean estaCreada = file.exists();
                                                        if (!estaCreada)
                                                            estaCreada = file.mkdirs();
                                                        if (estaCreada) {
                                                            String pass = Utils.MD5("YouChat");
                                                            Utils.descomprimirArchivo(multiEncriptada,YouChatApplication.RUTA_IMAGENES_RECIBIDA,nombreMulti,nombres_img,pass);

                                                        }
                                                        chatDes.setRuta_Dato(ruta_Dato);
                                                    }
                                                }
                                                else {
                                                    String nombres_img = "img" + chatDes.getOrden() + ".jpg";
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
                                                        float total = unaParte.getSize()/1000;
                                                        float suma = total;
                                                        while ((leidos = inputStream.read(bytes)) > 0) {
                                                            suma=suma-(leidos/1000);
                                                            final float sumaFinal = suma;
                                                            Utils.runOnUIThread(()->{
                                                                if(downloadProgressView!=null){
                                                                    float progress = (float)((total-sumaFinal)/total);
                                                                    if(progress<0f) progress = 0f;
                                                                    if(progress>1f) progress = 1f;
                                                                    downloadProgressView.setProgress(progress);
                                                                }
                                                            });
                                                            fichero.write(bytes, 0, leidos);
                                                        }
                                                        fichero.close();
                                                        inputStream.close();
                                                    }

                                                    chatDes.setRuta_Dato(ruta_Dato);
                                                }
                                            }
                                            else if (chatDes.esAudio()) {
                                                if(estaEncryptado){
                                                    MimeBodyPart mbp = (MimeBodyPart) unaParte;
                                                    String nombreMulti = mbp.getFileName();
                                                    File dirDestino = new File(YouChatApplication.RUTA_MULTIMEDIA_CACHE);
                                                    boolean existDestino = dirDestino.exists();
                                                    if(!existDestino) existDestino = dirDestino.mkdirs();
                                                    if(existDestino){
                                                        File multiEncriptada = new File(dirDestino,nombreMulti);

                                                        FileOutputStream fichero = new FileOutputStream(multiEncriptada);
                                                        InputStream inputStream = unaParte.getInputStream();
                                                        byte[] bytes = new byte[1000];
                                                        int leidos;
                                                        float total = unaParte.getSize()/1000;
                                                        float suma = total;
                                                        while ((leidos = inputStream.read(bytes)) > 0) {
                                                            suma=suma-(leidos/1000);
                                                            final float sumaFinal = suma;
                                                            Utils.runOnUIThread(()->{
                                                                if(downloadProgressView!=null){
                                                                    float progress = (float)((total-sumaFinal)/total);
                                                                    if(progress<0f) progress = 0f;
                                                                    if(progress>1f) progress = 1f;
                                                                    downloadProgressView.setProgress(progress);
                                                                }
                                                            });
                                                            fichero.write(bytes, 0, leidos);
                                                        }
                                                        fichero.close();
                                                        inputStream.close();

                                                        String nombre_aud = "rec" + chatDes.getOrden() + ".ycaudio";
                                                        String ruta_Dato = YouChatApplication.RUTA_AUDIOS_RECIBIDOS + nombre_aud;

                                                        File file = new File(YouChatApplication.RUTA_AUDIOS_RECIBIDOS);
                                                        boolean estaCreada = file.exists();
                                                        if (!estaCreada)
                                                            estaCreada = file.mkdirs();

                                                        if (estaCreada) {
                                                            String pass = Utils.MD5("YouChat");
                                                            Utils.descomprimirArchivo(multiEncriptada,YouChatApplication.RUTA_AUDIOS_RECIBIDOS,nombreMulti,nombre_aud,pass);

                                                        }
                                                        chatDes.setRuta_Dato(ruta_Dato);
                                                    }
                                                }
                                                else {
                                                    String nombre_aud = "rec" + chatDes.getOrden() + ".ycaudio";
                                                    String ruta_Dato = YouChatApplication.RUTA_AUDIOS_RECIBIDOS + nombre_aud;

                                                    File file = new File(YouChatApplication.RUTA_AUDIOS_RECIBIDOS);
                                                    boolean estaCreada = file.exists();
                                                    if (!estaCreada)
                                                        estaCreada = file.mkdirs();

                                                    if (estaCreada) {

                                                        FileOutputStream fichero = new FileOutputStream(ruta_Dato);
                                                        InputStream inputStream = unaParte.getInputStream();
                                                        byte[] bytes = new byte[1000];
                                                        int leidos;
                                                        float total = unaParte.getSize()/1000;
                                                        float suma = total;
                                                        while ((leidos = inputStream.read(bytes)) > 0) {
                                                            suma=suma-(leidos/1000);
                                                            final float sumaFinal = suma;
                                                            Utils.runOnUIThread(()->{
                                                                if(downloadProgressView!=null){
                                                                    float progress = (float)((total-sumaFinal)/total);
                                                                    if(progress<0f) progress = 0f;
                                                                    if(progress>1f) progress = 1f;
                                                                    downloadProgressView.setProgress(progress);
                                                                }
                                                            });
                                                            fichero.write(bytes, 0, leidos);
                                                        }
                                                        fichero.close();
                                                        inputStream.close();
                                                    }

                                                    chatDes.setRuta_Dato(ruta_Dato);
                                                }

                                            }
                                            else if (chatDes.esArchivo()) {
                                                if(estaEncryptado){
                                                    MimeBodyPart mbp = (MimeBodyPart) unaParte;
                                                    String nombreMulti = mbp.getFileName();
                                                    File dirDestino = new File(YouChatApplication.RUTA_MULTIMEDIA_CACHE);
                                                    boolean existDestino = dirDestino.exists();
                                                    if(!existDestino) existDestino = dirDestino.mkdirs();
                                                    if(existDestino){
                                                        File multiEncriptada = new File(dirDestino,nombreMulti);

                                                        FileOutputStream fichero = new FileOutputStream(multiEncriptada);
                                                        InputStream inputStream = unaParte.getInputStream();
                                                        byte[] bytes = new byte[1000];
                                                        int leidos;
                                                        float total = unaParte.getSize()/1000;
                                                        float suma = total;
                                                        while ((leidos = inputStream.read(bytes)) > 0) {
                                                            suma=suma-(leidos/1000);
                                                            final float sumaFinal = suma;
                                                            Utils.runOnUIThread(()->{
                                                                if(downloadProgressView!=null){
                                                                    float progress = (float)((total-sumaFinal)/total);
                                                                    if(progress<0f) progress = 0f;
                                                                    if(progress>1f) progress = 1f;
                                                                    downloadProgressView.setProgress(progress);
                                                                }
                                                            });
                                                            fichero.write(bytes, 0, leidos);
                                                        }
                                                        fichero.close();
                                                        inputStream.close();

                                                        String nombre_arc = mbp.getFileName();
                                                        if (nombre_arc.equals(""))
                                                            nombre_arc = "archivo" + chatDes.getOrden() + ".desconocido";
                                                        String ruta_Dato = YouChatApplication.RUTA_ARCHIVOS_RECIBIDOS + nombre_arc;

                                                        File file = new File(YouChatApplication.RUTA_ARCHIVOS_RECIBIDOS);
                                                        boolean estaCreada = file.exists();
                                                        if (!estaCreada)
                                                            estaCreada = file.mkdirs();

                                                        if (estaCreada) {
                                                            String pass = Utils.MD5("YouChat");
                                                            Utils.descomprimirArchivo(multiEncriptada,YouChatApplication.RUTA_ARCHIVOS_RECIBIDOS,nombreMulti,nombre_arc,pass);
                                                        }
                                                        chatDes.setRuta_Dato(ruta_Dato);
                                                    }
                                                }
                                                else {
                                                    String nombre_arc = unaParte.getFileName();
                                                    if (nombre_arc.equals(""))
                                                        nombre_arc = "archivo" + chatDes.getOrden() + ".desconocido";
                                                    String ruta_Dato = YouChatApplication.RUTA_ARCHIVOS_RECIBIDOS + nombre_arc;

                                                    File file = new File(YouChatApplication.RUTA_ARCHIVOS_RECIBIDOS);
                                                    boolean estaCreada = file.exists();
                                                    if (!estaCreada)
                                                        estaCreada = file.mkdirs();

                                                    if (estaCreada) {

                                                        FileOutputStream fichero = new FileOutputStream(ruta_Dato);
                                                        InputStream inputStream = unaParte.getInputStream();
                                                        byte[] bytes = new byte[1000];
                                                        int leidos;
                                                        float total = unaParte.getSize()/1000;
                                                        float suma = total;
                                                        while ((leidos = inputStream.read(bytes)) > 0) {
                                                            suma=suma-(leidos/1000);
                                                            final float sumaFinal = suma;
                                                            Utils.runOnUIThread(()->{
                                                                if(downloadProgressView!=null){
                                                                    float progress = (float)((total-sumaFinal)/total);
                                                                    if(progress<0f) progress = 0f;
                                                                    if(progress>1f) progress = 1f;
                                                                    downloadProgressView.setProgress(progress);
                                                                }
                                                            });
                                                            fichero.write(bytes, 0, leidos);
                                                        }
                                                        fichero.close();
                                                        inputStream.close();
                                                    }

                                                    chatDes.setRuta_Dato(ruta_Dato);
                                                }
                                            }
                                            else if (chatDes.esSticker()) {
                                                MimeBodyPart mbp = (MimeBodyPart) unaParte;
                                                String nombreMulti = unaParte.getFileName();
                                                File file = new File(YouChatApplication.RUTA_STICKERS_RECIBIDOS);
                                                boolean estaCreada = file.exists();
                                                if (!estaCreada)
                                                    estaCreada = file.mkdirs();
                                                if(estaCreada){
                                                    if (nombreMulti==null || nombreMulti.isEmpty())
                                                        nombreMulti = "sticker" + chatDes.getOrden() + ".sticker";
                                                    String ruta_Dato = YouChatApplication.RUTA_STICKERS_RECIBIDOS + nombreMulti;
                                                    if(!new File(ruta_Dato).exists()){
                                                        if(estaEncryptado){
                                                            File dirDestino = new File(YouChatApplication.RUTA_MULTIMEDIA_CACHE);
                                                            boolean existDestino = dirDestino.exists();
                                                            if(!existDestino) existDestino = dirDestino.mkdirs();
                                                            if(existDestino){
                                                                File multiEncriptada = new File(dirDestino,nombreMulti);
                                                                mbp.saveFile(multiEncriptada);

                                                                String pass = Utils.MD5("YouChat");
//                                                    String[] nomArc = nombreMulti.split("_spyc_");
//                                                    Utils.descomprimirArchivo(multiEncriptada,YouChatApplication.RUTA_STICKERS_RECIBIDOS,nomArc[nomArc.length-1],nombreMulti,pass);
                                                                Utils.descomprimirArchivo(multiEncriptada,YouChatApplication.RUTA_STICKERS_RECIBIDOS,nombreMulti,nombreMulti,pass);
                                                            }
                                                        }
                                                        else {
                                                            mbp.saveFile(ruta_Dato);
                                                        }
                                                    }
                                                    else tamMsg=0;
                                                    chatDes.setRuta_Dato(ruta_Dato);
                                                }
                                            }
                                            break;
                                        }
                                    }

                                    actualizarBurbujaDatos(tamMsg);
                                    actualizarEstadisticaChatDe(chatDes,CATEGORY_CHAT,tamMsg);
                                    chatDes.setDescargado(true);
                                    dbWorker.actualizarChatDescargadoNuevo(chatDes);

                                    eliminarMensaje(message);

                                    Utils.runOnUIThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(YouChatApplication.chatsActivity!=null
                                                    && YouChatApplication.chatsActivity.getCorreo().equals(chatDes.getCorreo()))
                                                YouChatApplication.chatsActivity.ActualizarMsgDescargado(chatDes);
                                        }
                                    });
                                }
                                else {
                                    Utils.runOnUIThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(YouChatApplication.chatsActivity!=null
                                                    && YouChatApplication.chatsActivity.getCorreo().equals(chatDes.getCorreo()))
                                                YouChatApplication.chatsActivity.descargaFallida(chatDes.getId(),true);
                                        }
                                    });
                                }
                            }
                            else {
                                Utils.runOnUIThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(YouChatApplication.chatsActivity!=null)
                                            YouChatApplication.chatsActivity.descargaFallida(chatDes.getId(),false);
                                    }
                                });
                            }

                        } catch (MessagingException e) {
                            e.printStackTrace();
                            Utils.runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(YouChatApplication.chatsActivity!=null)
                                        YouChatApplication.chatsActivity.descargaFallida(chatDes.getId(),true);
                                }
                            });

                        } catch (IOException e) {
                            e.printStackTrace();
                            Utils.runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(YouChatApplication.chatsActivity!=null)
                                        YouChatApplication.chatsActivity.descargaFallida(chatDes.getId(),true);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        Utils.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                if(YouChatApplication.chatsActivity!=null)
                                    YouChatApplication.chatsActivity.descargaFallida(chatDes.getId(),false);
                            }
                        });
                    }
                }
                else {
                    Utils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            if(YouChatApplication.chatsActivity!=null)
                                YouChatApplication.chatsActivity.descargaFallida(chatDes.getId(),true);
                        }
                    });
                }
            }
            else {
                Utils.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if(YouChatApplication.chatsActivity!=null)
                            YouChatApplication.chatsActivity.descargaFallida(chatDes.getId(),true);
                    }
                });
            }
            quitarIdMsgDescargar(chatDes.getId());
        }).start();

    }

    public boolean estaDescargando(String idMsgDescargar){
        int l = listaIdMsgDescargando.size();
        for(int i=0; i<l; i++){
            if(idMsgDescargar.equals(listaIdMsgDescargando.get(i))){
                return true;
            }
        }
        return false;
    }

    private synchronized void quitarIdMsgDescargar(String idMsgDescargar){
        int l = listaIdMsgDescargando.size();
        for(int i=0; i<l; i++){
            if(idMsgDescargar.equals(listaIdMsgDescargando.get(i))){
                listaIdMsgDescargando.remove(i);
                return;
            }
        }
    }

    //TODO: actualizar proceso con el de ChatService
    public synchronized void descargarMensajeCorreo(
            String idMsgDescargar, String idAdjunto, DownloadProgressView downloadProgressView) {
        listaIdMsgDescargandoCorreo.add(idMsgDescargar);
        new Thread(()->{
            boolean descargo = false;
            if(inbox!=null){
                if(!inbox.isOpen()) {
                    try {
                        inbox.open(Folder.READ_WRITE);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                        Utils.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                if(YouChatApplication.chatsActivityCorreo!=null)
                                    YouChatApplication.chatsActivityCorreo.descargaFallida(idMsgDescargar, idAdjunto,true);
                            }
                        });
                    }
                }
                if(inbox.isOpen()){
                    try {
                        ItemMensajeCorreo mensajeCorreo = dbWorker.obtenerMensajeCorreo(idMsgDescargar);
                        if(mensajeCorreo!=null){
                            IMAPMessage message = null;
                            try {
                                message = (IMAPMessage) inbox.getMessageByUID(mensajeCorreo.getUid());
                            }catch (MessagingException e) {
                                e.printStackTrace();
                            }
                            if(message==null) message = obtenerMessageById(mensajeCorreo.getId());
                            if(message!=null){
                                ItemAdjuntoCorreo adjuntoCorreo = dbWorker.obtenerAdjuntosCorreo(idAdjunto);
                                Object contenido = message.getContent();
                                if (contenido instanceof Multipart && adjuntoCorreo!=null) {
                                    Multipart mp = (Multipart) contenido;
                                    int numPart = mp.getCount();
                                    int pos = adjuntoCorreo.getPosicion();
                                    if(pos>=0 && pos<numPart){
                                        Part part = mp.getBodyPart(pos);
                                        String disposition = part.getDisposition();
                                        if (disposition.equalsIgnoreCase(Part.ATTACHMENT) ||
                                                disposition.equalsIgnoreCase(Part.INLINE)) {
                                            String ruta_Dato = YouChatApplication.RUTA_ADJUNTOS_CORREO + adjuntoCorreo.getNombre();
                                            File file = new File(YouChatApplication.RUTA_ADJUNTOS_CORREO);
                                            boolean estaCreada = file.exists();
                                            if (!estaCreada)
                                                estaCreada = file.mkdirs();
                                            if (estaCreada) {
                                                FileOutputStream fichero = new FileOutputStream(ruta_Dato);
                                                InputStream inputStream = part.getInputStream();
                                                byte[] bytes = new byte[1000];
                                                int leidos;
                                                float total = part.getSize()/1000;
                                                float suma = total;
                                                while ((leidos = inputStream.read(bytes)) > 0) {
                                                    suma=suma-(leidos/1000);
                                                    final float sumaFinal = suma;
                                                    Utils.runOnUIThread(()->{
                                                        if(downloadProgressView!=null){
                                                            float progress = (float)((total-sumaFinal)/total);
                                                            if(progress<0f) progress = 0f;
                                                            if(progress>1f) progress = 1f;
                                                            downloadProgressView.setProgress(progress);
                                                        }
                                                    });
                                                    fichero.write(bytes, 0, leidos);
                                                }
                                                fichero.close();
                                                inputStream.close();
                                            }
                                            int tamReal = part.getSize();
                                            actualizarBurbujaDatos(tamReal);
                                            YouChatApplication.addMega_buzon_recibidos(tamReal);

                                            Utils.runOnUIThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if(YouChatApplication.chatsActivityCorreo!=null
                                                            && YouChatApplication.chatsActivityCorreo.getCorreo().equals(mensajeCorreo.getCorreo()))
                                                        YouChatApplication.chatsActivityCorreo.ActualizarMsgDescargado(idMsgDescargar,idAdjunto);
                                                }
                                            });
                                            descargo = true;
                                        }
                                    }
                                }
                            }
                            else {
                                descargo=true;
                                Utils.runOnUIThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(YouChatApplication.chatsActivityCorreo!=null
                                                && YouChatApplication.chatsActivityCorreo.getCorreo().equals(mensajeCorreo.getCorreo()))
                                            YouChatApplication.chatsActivityCorreo.descargaFallida(idMsgDescargar, idAdjunto,false);
                                    }
                                });
                            }
                        }
                    } catch (MessagingException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            if(!descargo){
                Utils.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if(YouChatApplication.chatsActivityCorreo!=null)
                            YouChatApplication.chatsActivityCorreo.descargaFallida(idMsgDescargar, idAdjunto,true);
                    }
                });
            }
            quitarIdMsgDescargarCorreo(idMsgDescargar);
        }).start();

    }

    private IMAPMessage obtenerMessageById(String id) {
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

    //TODO: aqui tmb
    public synchronized void descargarMensajeCorreoAdjunto(VistaMensajeCorreoFragment vistaMensajeCorreoFragment,
                                                           ItemMensajeCorreo mensajeCorreo,
                                                           ItemAdjuntoCorreo adjuntoCorreo,
                                                           DownloadProgressView downloadProgressView) {
        String idMsgDescargar = mensajeCorreo.getId();
        String idAdjunto = adjuntoCorreo.getId();
        listaIdMsgDescargandoCorreo.add(idMsgDescargar);
        new Thread(()->{
            boolean descargo = false;
            if(inbox!=null){
                if(!inbox.isOpen()) {
                    try {
                        inbox.open(Folder.READ_WRITE);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                        Utils.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                if(vistaMensajeCorreoFragment!=null)
                                    vistaMensajeCorreoFragment.descargaFallida(idMsgDescargar, idAdjunto,true);
                            }
                        });
                    }
                }
                if(inbox.isOpen()){
                    try {
                        IMAPMessage message = null;
                        try {
                            message = (IMAPMessage) inbox.getMessageByUID(mensajeCorreo.getUid());
                        }catch (MessagingException e) {
                            e.printStackTrace();
                        }
                        if(message==null) message = obtenerMessageById(mensajeCorreo.getId());
                        if(message!=null){
                            Object contenido = message.getContent();
                            if (contenido instanceof Multipart) {
                                Multipart mp = (Multipart) contenido;
                                int numPart = mp.getCount();
                                int pos = adjuntoCorreo.getPosicion();
                                if(pos>=0 && pos<numPart){
                                    Part part = mp.getBodyPart(pos);
                                    String disposition = part.getDisposition();
                                    if (disposition.equalsIgnoreCase(Part.ATTACHMENT) ||
                                            disposition.equalsIgnoreCase(Part.INLINE)) {
                                        String ruta_Dato = YouChatApplication.RUTA_ADJUNTOS_CORREO + adjuntoCorreo.getNombre();
                                        File file = new File(YouChatApplication.RUTA_ADJUNTOS_CORREO);
                                        boolean estaCreada = file.exists();
                                        if (!estaCreada)
                                            estaCreada = file.mkdirs();
                                        if (estaCreada) {
                                            FileOutputStream fichero = new FileOutputStream(ruta_Dato);
                                            InputStream inputStream = part.getInputStream();
                                            byte[] bytes = new byte[1000];
                                            int leidos;
                                            float total = part.getSize()/1000;
                                            float suma = total;
                                            while ((leidos = inputStream.read(bytes)) > 0) {
                                                suma=suma-(leidos/1000);
                                                final float sumaFinal = suma;
                                                Utils.runOnUIThread(()->{
                                                    if(downloadProgressView!=null){
                                                        float progress = (float)((total-sumaFinal)/total);
                                                        if(progress<0f) progress = 0f;
                                                        if(progress>1f) progress = 1f;
                                                        downloadProgressView.setProgress(progress);
                                                    }
                                                });
                                                fichero.write(bytes, 0, leidos);
                                            }
                                            fichero.close();
                                            inputStream.close();
                                        }

                                        YouChatApplication.addMega_buzon_recibidos(part.getSize());

                                        Utils.runOnUIThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if(vistaMensajeCorreoFragment!=null)
                                                    vistaMensajeCorreoFragment.ActualizarMsgDescargado(idMsgDescargar,idAdjunto);
                                            }
                                        });
                                        descargo = true;
                                    }
                                }
                            }
                        }
                        else {
                            Utils.runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(vistaMensajeCorreoFragment!=null)
                                        vistaMensajeCorreoFragment.descargaFallida(idMsgDescargar, idAdjunto,false);
                                }
                            });
                        }
                    } catch (MessagingException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            if(!descargo){
                Utils.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if(vistaMensajeCorreoFragment!=null)
                            vistaMensajeCorreoFragment.descargaFallida(idMsgDescargar, idAdjunto,true);
                    }
                });
            }
            quitarIdMsgDescargarCorreo(idMsgDescargar);
        }).start();

    }

    public boolean estaDescargandoCorreo(String idMsgDescargar){
        int l = listaIdMsgDescargandoCorreo.size();
        for(int i=0; i<l; i++){
            if(idMsgDescargar.equals(listaIdMsgDescargandoCorreo.get(i))){
                return true;
            }
        }
        return false;
    }

    private synchronized void quitarIdMsgDescargarCorreo(String idMsgDescargar){
        int l = listaIdMsgDescargandoCorreo.size();
        for(int i=0; i<l; i++){
            if(idMsgDescargar.equals(listaIdMsgDescargandoCorreo.get(i))){
                listaIdMsgDescargandoCorreo.remove(i);
                return;
            }
        }
    }

    public void descargarNow(String idNow, String correo, long uid, String id_mensaje, String orden,
                             OnDownloadNowListener onDownloadNowListener) {
        new Thread(()->{
            boolean descargo = false;
            if(inbox!=null){
                if(!inbox.isOpen()) {
                    try {
                        inbox.open(Folder.READ_WRITE);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                        if(onDownloadNowListener!=null)
                            onDownloadNowListener.OnFailedDownload(true);
                    }
                }
                if(inbox.isOpen()){
                    try {
                        Log.e(TAG, "descargarNow: id: "+id_mensaje+" uid: "+uid );
                        IMAPMessage message = null;
                        try {
                            message = (IMAPMessage) inbox.getMessageByUID(uid);
                        }catch (MessagingException e) {
                            e.printStackTrace();
                        }
                        if(message==null){
                            message = obtenerMessageById(id_mensaje);
                        }
                        if(message!=null){
                            Object contenido = message.getContent();
                            String valTemp = "";
                            if (contenido instanceof Multipart) {
                                String[] datoTemp = message.getHeader(ItemChat.KEY_ESTA_ENCRIPTADO);
                                if (datoTemp != null)
                                    valTemp = datoTemp[0];
                                final boolean estaEncryptado = valTemp.equals("1");

                                Multipart mp = (Multipart) contenido;
                                int numPart = mp.getCount();
                                if(numPart>=2){
                                    String ruta_Dato="";
                                    Part unaParte = mp.getBodyPart(1);
                                    int tamMsg = unaParte.getSize();
                                    if(estaEncryptado){
                                        MimeBodyPart mbp = (MimeBodyPart) unaParte;
                                        String nombreMulti = mbp.getFileName();
                                        File dirDestino = new File(YouChatApplication.RUTA_MULTIMEDIA_CACHE);
                                        boolean existDestino = dirDestino.exists();
                                        if(!existDestino) existDestino = dirDestino.mkdirs();
                                        if(existDestino){
                                            File multiEncriptada = new File(dirDestino,nombreMulti);

                                            FileOutputStream fichero = new FileOutputStream(multiEncriptada);
                                            InputStream inputStream = unaParte.getInputStream();
                                            byte[] bytes = new byte[1000];
                                            int leidos;
                                            float total = unaParte.getSize()/1000;
                                            float suma = total;
                                            while ((leidos = inputStream.read(bytes)) > 0) {
                                                suma=suma-(leidos/1000);
                                                if(onDownloadNowListener!=null){
                                                    float progress = (float)((total-suma)/total);
                                                    if(progress<0f) progress = 0f;
                                                    if(progress>1f) progress = 1f;
                                                    onDownloadNowListener.OnProgressListener(progress);
                                                }
                                                fichero.write(bytes, 0, leidos);
                                            }
                                            fichero.close();
                                            inputStream.close();

                                            String nombres_img = "est" + orden + ".jpg";
                                            ruta_Dato = YouChatApplication.RUTA_ESTADOS_GUARDADOS
                                                    + nombres_img;

                                            File file = new File(YouChatApplication.RUTA_ESTADOS_GUARDADOS);
                                            boolean estaCreada = file.exists();
                                            if (!estaCreada)
                                                estaCreada = file.mkdirs();

                                            if (estaCreada) {
                                                String pass = Utils.MD5("YouChat");
                                                Utils.descomprimirArchivo(multiEncriptada,YouChatApplication.RUTA_ESTADOS_GUARDADOS,nombreMulti,nombres_img,pass);
                                                descargo=true;
                                            }
                                        }
                                    }
                                    else {
                                        String nombres_img = "est" + orden + ".jpg";
                                        ruta_Dato = YouChatApplication.RUTA_ESTADOS_GUARDADOS
                                                + nombres_img;

                                        File file = new File(YouChatApplication.RUTA_ESTADOS_GUARDADOS);
                                        boolean estaCreada = file.exists();
                                        if (!estaCreada)
                                            estaCreada = file.mkdirs();

                                        if (estaCreada) {
                                            FileOutputStream fichero = new FileOutputStream(ruta_Dato);
                                            InputStream inputStream = unaParte.getInputStream();
                                            byte[] bytes = new byte[1000];
                                            int leidos;
                                            float total = unaParte.getSize()/1000;
                                            float suma = total;
                                            while ((leidos = inputStream.read(bytes)) > 0) {
                                                suma=suma-(leidos/1000);
                                                if(onDownloadNowListener!=null){
                                                    float progress = (float)((total-suma)/total);
                                                    if(progress<0f) progress = 0f;
                                                    if(progress>1f) progress = 1f;
                                                    onDownloadNowListener.OnProgressListener(progress);
                                                }
                                                fichero.write(bytes, 0, leidos);
                                            }
                                            fichero.close();
                                            inputStream.close();
                                            descargo=true;
                                        }
                                    }

                                    if(descargo){
                                        Utils.runOnUIThread(()->{
                                            actualizarBurbujaDatos(tamMsg);
                                            if(!dbWorker.existeEstadisticaPersonal(correo)){
                                                ItemEstadisticaPersonal estadisticaPersonal = new ItemEstadisticaPersonal(correo);
                                                dbWorker.insertarNuevaEstadisticaPersonal(estadisticaPersonal);
                                            }
                                            ItemEstadisticaPersonal estadisticaPersonal = dbWorker.obtenerEstadisticaPersonal(correo);
                                            estadisticaPersonal.addCant_est_rec_mg(tamMsg);
                                            dbWorker.modificarEstadisticaPersonal(estadisticaPersonal);
                                        });
                                        eliminarMensaje(message);
                                        dbWorker.actualizarEstadoDescargado(idNow,ruta_Dato);
                                        if(onDownloadNowListener!=null)
                                            onDownloadNowListener.OnSuccessDownload(idNow);
                                    }
                                }
                            }
                        }
                        else {
                            if(onDownloadNowListener!=null)
                                onDownloadNowListener.OnFailedDownload(false);
                        }
                    } catch (MessagingException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            if(!descargo){
                if(onDownloadNowListener!=null)
                    onDownloadNowListener.OnFailedDownload(true);
            }
        }).start();
    }


    public synchronized void VaciarInbox(AjustesActivity ajustesActivity,
                                         TextViewFontGenGI texto_cant,
                                         DownloadProgressView progressbar_vaciar_inbox) {
        new Thread(()->{
            try {
                if(inbox!=null){
                    if (!inbox.isOpen()) inbox.open(Folder.READ_WRITE);
                    if(inbox.isOpen()){
                        Message[] messages = inbox.getMessages();
                        int longi = messages.length;
                        Utils.runOnUIThread(()->{
                            if(texto_cant!=null)
                                texto_cant.setText("0/"+longi);
                        });
                        for (int i = 0; i < longi; i++) {
                            Message msgTemp = messages[i];
                            msgTemp.setFlag(Flags.Flag.DELETED, true);
                            final int iFinal = i;
                            Utils.runOnUIThread(()->{
                                if(texto_cant!=null)
                                    texto_cant.setText(iFinal+"/"+longi);
                                if(progressbar_vaciar_inbox!=null){
                                    float progress = (float)((float)(iFinal)/longi);
                                    if(progress<0) progress=0;
                                    if(progress>1) progress = 1;
                                    progressbar_vaciar_inbox.setProgress(progress);
                                }
                            });
                        }
                        inbox.expunge();
                        YouChatApplication.cant_msg_inbox = 0;
                        if(usaGmail && false){
                            String nameCarpetaTodos = obtenerNombreCarpetaTodos();
                            if(!nameCarpetaTodos.isEmpty()){
                                IMAPFolder child = (IMAPFolder) store.getFolder(nameCarpetaTodos);
                                if(child!=null){
                                    child.open(Folder.READ_WRITE);
                                    if(child.isOpen()){
                                        Message[] messages2 = child.getMessages();
                                        int longi2 = messages2.length;
                                        Utils.runOnUIThread(()->{
                                            if(texto_cant!=null)
                                                texto_cant.setText("0/"+longi2);
                                        });
                                        for (int i = 0; i < longi2; i++) {
                                            Message msgTemp = messages2[i];
                                            msgTemp.setFlag(Flags.Flag.DELETED, true);
//                                            child.expunge(new Message[]{msgTemp});
                                            final int iFinal = i;
                                            Utils.runOnUIThread(()->{
                                                if(texto_cant!=null)
                                                    texto_cant.setText(iFinal+"/"+longi2);
                                                if(progressbar_vaciar_inbox!=null){
                                                    float progress = (float)((float)(iFinal)/longi2);
                                                    if(progress<0) progress=0;
                                                    if(progress>1) progress = 1;
                                                    progressbar_vaciar_inbox.setProgress(progress);
                                                }
                                            });
                                        }
                                        child.close(true);
                                        Utils.runOnUIThread(()->{
                                            if(ajustesActivity!=null)
                                                ajustesActivity.vaciadoInboxFinalizado(longi, longi2);
                                        });
                                    }
                                    else {
                                        Utils.runOnUIThread(()->{
                                            if(ajustesActivity!=null)
                                                ajustesActivity.vaciadoInboxFinalizado(longi, -1);
                                        });
                                    }
                                }
                                else {
                                    Utils.runOnUIThread(()->{
                                        if(ajustesActivity!=null)
                                            ajustesActivity.vaciadoInboxFinalizado(longi, -1);
                                    });
                                }
                            }
                            else {
                                Utils.runOnUIThread(()->{
                                    if(ajustesActivity!=null)
                                        ajustesActivity.vaciadoInboxFinalizado(longi, -1);
                                });
                            }

                        }
                        else {
                            Utils.runOnUIThread(()->{
                                if(ajustesActivity!=null)
                                    ajustesActivity.vaciadoInboxFinalizado(longi);
                            });
                        }
                    }
                    else {
                        Utils.runOnUIThread(()->{
                            if(ajustesActivity!=null)
                                ajustesActivity.vaciadoInboxFinalizado(-1);
                        });
                    }
                }
                else {
                    Utils.runOnUIThread(()->{
                        if(ajustesActivity!=null)
                            ajustesActivity.vaciadoInboxFinalizado(-1);
                    });
                }
            } catch (MessagingException e) {
                Log.e(TAG, "MessagingException: "+e.toString());
                e.printStackTrace();
                Utils.runOnUIThread(()->{
                    if(ajustesActivity!=null)
                        ajustesActivity.vaciadoInboxFinalizado(-1);
                });
            }
        }).start();
    }

    public synchronized void VaciarInbox(BandejaFragment bandejaFragment,
                                         TextViewFontGenGI texto_cant,
                                         DownloadProgressView progressbar_vaciar_inbox) {
        new Thread(()->{
            try {
                if(inbox!=null){
                    if (!inbox.isOpen()) inbox.open(Folder.READ_WRITE);
                    if(inbox.isOpen()){
                        Message[] messages = inbox.getMessages();
                        int longi = messages.length;
                        Utils.runOnUIThread(()->{
                            if(texto_cant!=null)
                                texto_cant.setText("0/"+longi);
                        });
                        for (int i = 0; i < longi; i++) {
                            Message msgTemp = messages[i];
                            msgTemp.setFlag(Flags.Flag.DELETED, true);
                            final int iFinal = i;
                            Utils.runOnUIThread(()->{
                                if(texto_cant!=null)
                                    texto_cant.setText(iFinal+"/"+longi);
                                if(progressbar_vaciar_inbox!=null){
                                    float progress = (float)((float)(iFinal)/longi);
                                    if(progress<0) progress=0;
                                    if(progress>1) progress = 1;
                                    progressbar_vaciar_inbox.setProgress(progress);
                                }
                            });
                        }
                        inbox.expunge();
                        YouChatApplication.cant_msg_inbox = 0;
                        Utils.runOnUIThread(()->{
                            if(bandejaFragment!=null)
                                bandejaFragment.vaciadoInboxFinalizado(longi);
                        });
                    }
                    else {
                        Utils.runOnUIThread(()->{
                            if(bandejaFragment!=null)
                                bandejaFragment.vaciadoInboxFinalizado(-1);
                        });
                    }
                }
                else {
                    Utils.runOnUIThread(()->{
                        if(bandejaFragment!=null)
                            bandejaFragment.vaciadoInboxFinalizado(-1);
                    });
                }
            } catch (MessagingException e) {
                Log.e(TAG, "MessagingException: "+e.toString());
                e.printStackTrace();
                Utils.runOnUIThread(()->{
                    if(bandejaFragment!=null)
                        bandejaFragment.vaciadoInboxFinalizado(-1);
                });
            }
        }).start();
    }

    private String obtenerNombreCarpetaTodos() {
        try {
            IMAPFolder child = (IMAPFolder) store.getDefaultFolder();
            if(child!=null){
                Folder[] folders1 = child.list();
                if(folders1!=null){
                    int l1 = folders1.length;
                    for(int i=0; i<l1; i++){
                        Folder[] subFolders = folders1[i].list();
                        int l2 = subFolders.length;
                        for(int j=0; j<l2; j++){
                            if(subFolders[j].getFullName().toLowerCase()
                                    .equals("[gmail]/todos")){
                                return "[Gmail]/Todos";
                            }
                            if(subFolders[j].getFullName().toLowerCase()
                                    .equals("[gmail]/all")){
                                return "[Gmail]/All";
                            }
                            if(subFolders[j].getFullName().toLowerCase()
                                    .equals("[gmail]/all mail")){
                                return "[Gmail]/All Mail";
                            }
                        }
                    }
                }
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String obtenerNombreCarpetaPapelera() {
        try {
            IMAPFolder child = (IMAPFolder) store.getDefaultFolder();
            if(child!=null){
                Folder[] folders1 = child.list();
                if(folders1!=null){
                    int l1 = folders1.length;
                    for(int i=0; i<l1; i++){
                        Folder[] subFolders = folders1[i].list();
                        int l2 = subFolders.length;
                        for(int j=0; j<l2; j++){
                            if(subFolders[j].getFullName().toLowerCase()
                                    .equals("[gmail]/papelera")){
                                return "[Gmail]/Papelera";
                            }
                            if(subFolders[j].getFullName().toLowerCase()
                                    .equals("[gmail]/trash")){
                                return "[Gmail]/Trash";
                            }
                        }
                    }
                }
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return "";
    }

    ///////NOTIFICACIONES///////
    public void crearNotificationCorreo(){
        if(!YouChatApplication.notiCorreoEnt) return;

//        BitmapDrawable d = (BitmapDrawable) getResources().getDrawable(R.drawable.message);
//        d.setColorFilter(Color.parseColor("#FFF"), PorterDuff.Mode.SRC_IN);
//
//        Bitmap bitmap = d.getBitmap();
//        Bitmap bitmap = ((BitmapDrawable) d).getBitmap();

        Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.message)).getBitmap();

        String cantMsg;
        int cant = dbWorker.obtenerCantMensajeCorreoNoVistoTotal();
        if (cant == 0) cant = 1;
        if (cant == 1) cantMsg = "1 nuevo correo";
        else cantMsg = cant + " nuevos correos";

        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        i.setAction("NOTI_CORREO");
        PendingIntent pendingIntent =
                PendingIntent.getActivity(getApplicationContext(), 0, i, 0);

        NotificationCompat.Builder notificacion = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.notification_icon)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setTicker(cant==1?"Nuevo correo entrante":"Nuevos correos entrantes")
                .setLargeIcon(bitmap)
                .setContentTitle("Bandeja de entrada")
                .setContentText(cant==1?"Nuevo correo entrante":"Nuevos correos entrantes")
                .setContentInfo(cantMsg)
                .setVibrate(new long[]{100, 250, 100, 500})
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            notificacion.setLights(Color.parseColor(YouChatApplication.itemTemas.getColor_accento()),
                    2000, 2000);
        }

        if (YouChatApplication.sonido){
            notificacion.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        }

        mNotifyMgr.notify(9988, notificacion.build());
    }

    public void eliminarNotiCorreo(){
        cantEntrantesNow=0;
        if(mNotifyMgr!=null)
            mNotifyMgr.cancel(9988);
    }

    public void crearNotificationNowEntrante(){
        if(!YouChatApplication.notiNowEnt) return;
        if(cantEntrantesNow<0) cantEntrantesNow=0;
        cantEntrantesNow++;

        Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.activity_white)).getBitmap();

        String cantMsg;
        if (cantEntrantesNow == 1) cantMsg = "1 nuevo Now";
        else cantMsg = cantEntrantesNow + " nuevos Now";

        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        i.setAction("NOTI_NOW_ENTRANTE");
        PendingIntent pendingIntent =
                PendingIntent.getActivity(getApplicationContext(), 0, i, 0);

        NotificationCompat.Builder notificacion = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.notification_icon)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setTicker(cantEntrantesNow==1?"Nuevo Now entrante":"Nuevos Now entrantes")
                .setLargeIcon(bitmap)
                .setContentTitle("Now")
                .setContentText(cantEntrantesNow==1?"Nuevo Now entrante":"Nuevos Now entrantes")
                .setContentInfo(cantMsg)
                .setVibrate(new long[]{100, 250, 100, 500})
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            notificacion.setLights(Color.parseColor(YouChatApplication.itemTemas.getColor_accento()),
                    2000, 2000);
        }

        if (YouChatApplication.sonido){
            notificacion.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        }

        mNotifyMgr.notify(9966, notificacion.build());
    }

    public void eliminarNotiNowEntrante(){
        if(mNotifyMgr!=null)
            mNotifyMgr.cancel(9966);
    }

    public void crearNotificationNowReaccion(){
        if(!YouChatApplication.notiReacNow) return;

        if(cantReaccionesNow<0) cantReaccionesNow=0;
        cantReaccionesNow++;
        Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.star)).getBitmap();

        String cantMsg;
        if (cantReaccionesNow == 1) cantMsg = "una nueva reacción";
        else cantMsg = cantReaccionesNow + " nuevas reacciones";

        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        i.setAction("NOTI_NOW_REACCION");
        PendingIntent pendingIntent =
                PendingIntent.getActivity(getApplicationContext(), 0, i, 0);

        NotificationCompat.Builder notificacion = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.notification_icon)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setTicker(cantReaccionesNow==1?"Nueva reacción a tus now":"Nuevas reacciones a tus Now")
                .setLargeIcon(bitmap)
                .setContentTitle("Reacciones de los Now")
                .setContentText(cantReaccionesNow==1?"Nueva reacción a tus now":"Nuevas reacciones a tus Now")
                .setContentInfo(cantMsg)
                .setVibrate(new long[]{100, 250, 100, 500})
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            notificacion.setLights(Color.parseColor(YouChatApplication.itemTemas.getColor_accento()),
                    2000, 2000);
        }

        if (YouChatApplication.sonido){
            notificacion.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        }

        mNotifyMgr.notify(9977, notificacion.build());
    }

    public void eliminarNotiNowReaccion(){
        cantReaccionesNow=0;
        if(mNotifyMgr!=null)
            mNotifyMgr.cancel(9977);
    }

    public void crearNotification(ItemChat msg) {
        if(!YouChatApplication.notiMenChat) return;
        String cor = msg.getCorreo();
        String usu = dbWorker.obtenerNombre(cor);

        String rutaImg = dbWorker.obtenerRutaImg(cor);
        Bitmap bitmap;
        String cache = Utils.cargarImgCache(rutaImg);
        if(cache.isEmpty()){
            bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.contacts)).getBitmap();
        }
        else {
            bitmap = BitmapFactory.decodeFile(cache);
            if (bitmap == null)
                bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.contacts)).getBitmap();
        }

        String cantMsg = "";
        int cant = dbWorker.cantMsgNoVistos(cor);
        if (cant == 0) cant = 1;
        if (cant == 1) cantMsg = "1 nuevo mensaje";
        else cantMsg = cant + " nuevos mensajes";

        String texto = "";
        if (msg.esAudio())
            texto = "(Audio)" + msg.getMensaje();
        else if (msg.esImagen())
            texto = "(Imagen) " + msg.getMensaje();
        else if (msg.esSticker())
            texto = "(Sticker)";
        else if (msg.esTarjeta())
            texto = "(Tarjeta) "+ msg.getMensaje();
        else if (msg.esArchivo())
            texto = "(Archivo)";
        else if (msg.esContacto())
            texto = "(Contacto)";
        else if (msg.esTema())
            texto = "(Tema)";
        else
            texto = msg.getMensaje();

        ItemNotificacion itemNotificacion = new ItemNotificacion(usu, cor, texto, rutaImg, cant);

        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        i.setAction("NOTI_CHAT");
        i.putExtra("usuario", usu);
        i.putExtra("correo", cor);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(getApplicationContext(), 0, i, 0);

        NotificationCompat.Builder notificacion = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.notification_icon)
                .setTicker("Nuevo mensaje de " + usu)
                .setLargeIcon(bitmap)
                .setContentTitle(usu)
                .setContentText(texto)
                .setContentInfo(cantMsg)
                .setVibrate(new long[]{100, 250, 100, 500})
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            notificacion.setLights(Color.parseColor(YouChatApplication.itemTemas.getColor_accento()),
                    2000, 2000);
        }

        if (YouChatApplication.sonido){
            notificacion.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
//            notificacion.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
//                    Notification.STREAM_DEFAULT);
//            notificacion.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        }

        insertarNotificacion(itemNotificacion);

        if (itemNotificacionArrayList.size() > 1) {
            int cantTotal = totalMensajes();
            NotificationCompat.InboxStyle inboxStyleNoti
                    = new NotificationCompat.InboxStyle();
            //inboxStyleNoti = new Notification.InboxStyle();
            inboxStyleNoti.setBigContentTitle(cantTotal + " nuevos mensajes de:");
            int l = itemNotificacionArrayList.size();
            for (int j = 0; j < l; j++)
                inboxStyleNoti.addLine(itemNotificacionArrayList.get(j).getUsuario()
                        + "(" + itemNotificacionArrayList.get(j).getCant() + ")" + ": " +
                        itemNotificacionArrayList.get(j).getMensaje());
            notificacion.setStyle(inboxStyleNoti);
        }
        mNotifyMgr.notify(9999, notificacion.build());
    }

    private void recrearNotificacion() {

        if (itemNotificacionArrayList.size() == 0) {
            mNotifyMgr.cancel(9999);
            return;
        }

        if (!YouChatApplication.notificacion) return;
        if(!YouChatApplication.notiMenChat) return;

        ItemNotificacion itemNotificacion = itemNotificacionArrayList.get(0);
        String usu = itemNotificacion.getUsuario();
        String texto = itemNotificacion.getMensaje();
        int cant = itemNotificacion.getCant();
        String cantMsg = "";
        if (cant == 0) cant = 1;
        if (cant == 1) cantMsg = "1 nuevo mensaje";
        else cantMsg = cant + " nuevos mensajes";

        Bitmap bitmap;
        String cache = Utils.cargarImgCache(itemNotificacion.getRutaImg());
        if(cache.isEmpty()){
            bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.contacts)).getBitmap();
        }
        else {
            bitmap = BitmapFactory.decodeFile(cache);
            if (bitmap == null)
                bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.contacts)).getBitmap();
        }


        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        i.setAction("NOTI_CHAT");
        i.putExtra("usuario", itemNotificacion.getUsuario());
        i.putExtra("correo", itemNotificacion.getCorreo());
        PendingIntent pendingIntent =
                PendingIntent.getActivity(getApplicationContext(), 0, i, 0);

        NotificationCompat.Builder notificacion = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setSmallIcon(R.drawable.notification_icon)
                .setLargeIcon(bitmap)
                .setContentTitle(usu)
                .setContentText(texto)
                .setContentInfo(cantMsg)
                .setLights(0xFF3F51B5, 1000, 2000)
                .setAutoCancel(true);

        insertarNotificacion(itemNotificacion);

        if (itemNotificacionArrayList.size() > 1) {
            int cantTotal = totalMensajes();
            NotificationCompat.InboxStyle inboxStyleNoti
                    = new NotificationCompat.InboxStyle();
            inboxStyleNoti.setBigContentTitle(cantTotal + " nuevos mensajes de:");
            int l = itemNotificacionArrayList.size();
            for (int j = 0; j < l; j++)
                inboxStyleNoti.addLine(itemNotificacionArrayList.get(j).getUsuario()
                        + "(" + itemNotificacionArrayList.get(j).getCant() + ")" + ": " +
                        itemNotificacionArrayList.get(j).getMensaje());
            notificacion.setStyle(inboxStyleNoti);
        }
        mNotifyMgr.notify(9999, notificacion.build());
    }

    private int totalMensajes() {
        int sum = 0;
        int l = itemNotificacionArrayList.size();
        for (int i = 0; i < l; i++)
            sum += itemNotificacionArrayList.get(i).getCant();
        return sum;
    }

    private Notification createNotification() {
        // a notification _must_ contain a small icon, a title and a text, see https://developer.android.com/guide/topics/ui/notifiers/notifications.html#Required
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ID);

        builder.setContentTitle(getString(R.string.app_name));
        builder.setContentText("Servicio establecido en segundo plano");
        builder.setPriority(NotificationCompat.PRIORITY_MIN);
        builder.setWhen(0);
        builder.setSmallIcon(R.drawable.iconycvector9_2_1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID);
        }
        return builder.build();
    }

    private void insertarNotificacion(ItemNotificacion itemNotificacion) {
        String cor = itemNotificacion.getCorreo();
        boolean encontrado = false;
        int l = itemNotificacionArrayList.size();
        for (int i = 0; i < l; i++)
            if (cor.equals(itemNotificacionArrayList.get(i).getCorreo())) {
                encontrado = true;
                itemNotificacionArrayList.remove(i);
                itemNotificacionArrayList.add(0, itemNotificacion);
                break;
            }
        if (!encontrado) {
            itemNotificacionArrayList.add(0, itemNotificacion);
        }
    }

    public void cerrarNotificacionDe(String correo) {
        if (chatService == null) return;
        int pos = buscarNotiID(correo);
        if (pos != -1) {
            itemNotificacionArrayList.remove(pos);
            recrearNotificacion();
        } else if (itemNotificacionArrayList.size() == 0) {
            mNotifyMgr.cancel(9999);
        }
    }

    private int buscarNotiID(String cor) {
        int l = itemNotificacionArrayList.size();
        for (int i = 0; i < l; i++)
            if (cor.equals(itemNotificacionArrayList.get(i).getCorreo())) {
                return i;
            }
        return -1;
    }

    public ItemNotificacion getItemNoti() {
        if (chatService == null || itemNotificacionArrayList==null) return null;
        if(itemNotificacionArrayList.size()>0){
            ItemNotificacion temp = itemNotificacionArrayList.get(0);
            itemNotificacionArrayList.remove(0);
            recrearNotificacion();
            return temp;
        }
        return null;
    }
    ///////NOTIFICACIONES///////

    @Override
    public void onDestroy() {
        removeWidget();
        if (timerTaskGlobal != null) timerTaskGlobal.cancel();
        yaExist = false;
        chatService = null;

        new Thread(()->{
            if(inbox!=null){
                inbox.removeMessageCountListener(countAdapter);
                if(inbox.isOpen()){
                    try {
                        inbox.close(true);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }
            }
            if(store!=null){
                if(store.isConnected()){
                    try {
                        store.close();
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();


//        Log.e(TAG, "onDestroy");

//        IntentFilter filter = new IntentFilter();
//        filter.addAction("cu.youchat.reboot");
//        context.registerReceiver(new MessageReceiver(context, intent), filter);

        if(YouChatApplication.mark==3) {
            Intent intent = new Intent("cu.youchat.reboot");
            sendBroadcast(intent, "android.youchat.reboot");
        }
    }

    public void enviarMensaje(ItemChat msg, String categoria) {
        if (sendMsg == null) sendMsg = new SendMsg(this);
        sendMsg.enviarMsg(msg, categoria);
    }
    public void enviarMensajePersonalizado(ItemChat msg, String categoria, Message m, int tamMsg) {
        if (sendMsg == null) sendMsg = new SendMsg(this);
        sendMsg.enviarMsgPersonalizado(msg, categoria, m, tamMsg);
    }

    public Session getSession(){
        if (sendMsg == null) sendMsg = new SendMsg(this);
        return sendMsg.getSession();
    }

    public void activarSegundoPlano() {
        try {
            stopForeground(true);
            startForeground(7327, createNotification());
        } catch (Exception e) {
            Log.e("YOUCHAT SERVICE", "Exception "+e.toString());
            e.printStackTrace();
            YouChatApplication.setEsta_segundo_plano(false);
        }
    }

    public void detenerSegundoPlano() {
        try {
            stopForeground(true);
        } catch (Exception e) {
            Log.e("YOUCHAT SERVICE", "Exception "+e.toString());
            e.printStackTrace();
            YouChatApplication.setEsta_segundo_plano(false);

        }
    }

    private synchronized void actualizarEstadisticaChatDe(ItemChat chat, String categoria, int tamMsg) {
        Utils.runOnUIThread(()->{
            if(categoria.equals(SendMsg.CATEGORY_CHAT)){
                if(!dbWorker.existeEstadisticaPersonal(chat.getCorreo())){
                    ItemEstadisticaPersonal estadisticaPersonal = new ItemEstadisticaPersonal(chat.getCorreo());
                    dbWorker.insertarNuevaEstadisticaPersonal(estadisticaPersonal);
                }
                ItemEstadisticaPersonal estadisticaPersonal = dbWorker.obtenerEstadisticaPersonal(chat.getCorreo());
                if (chat.esMsgTexto() || chat.esTarjeta() || chat.esContacto()){
                    estadisticaPersonal.addCant_msg_rec(1);
                    estadisticaPersonal.addCant_msg_rec_mg(tamMsg);
                }
                else if (chat.esImagen()){
                    estadisticaPersonal.addCant_img_rec(1);
                    estadisticaPersonal.addCant_img_rec_mg(tamMsg);
                }
                else if (chat.esAudio()){
                    estadisticaPersonal.addCant_aud_rec(1);
                    estadisticaPersonal.addCant_aud_rec_mg(tamMsg);
                }
                else if (chat.esArchivo()){
                    estadisticaPersonal.addCant_arc_rec(1);
                    estadisticaPersonal.addCant_arc_rec_mg(tamMsg);
                }
                else if (chat.esSticker()){
                    estadisticaPersonal.addCant_sti_rec(1);
                    estadisticaPersonal.addCant_sti_rec_mg(tamMsg);
                }
                dbWorker.modificarEstadisticaPersonal(estadisticaPersonal);
            }
        });

    }

    private synchronized void actualizarEstadisticaDe(String correo, String id, String categoria, int tamMsg) {
        if(categoria.equals(SendMsg.CATEGORY_CHAT_ACT)){
            if (id.equals("-r-")){//lectura
                YouChatApplication.addCant_confir_lectura(1);
                YouChatApplication.addMega_x_serv_confirmacion_lectura_rec(tamMsg);
            }
            else if (id.equals("-e-")){//escribiendo
                YouChatApplication.addCant_chat_din(1);
                YouChatApplication.addMega_x_serv_chat_dinamico_rec(tamMsg);
            }
            else if (id.equals("-l-") || id.equals("-u-")){//linea y union
                YouChatApplication.addCant_aviso_en_linea(1);
                YouChatApplication.addMega_x_serv_aviso_en_linea_rec(tamMsg);
            }
        }
        else if(categoria.equals(SendMsg.CATEGORY_SOL_CONTACTO)){
            if(!dbWorker.existeEstadisticaPersonal(correo)){
                ItemEstadisticaPersonal estadisticaPersonal = new ItemEstadisticaPersonal(correo);
                dbWorker.insertarNuevaEstadisticaPersonal(estadisticaPersonal);
            }
            ItemEstadisticaPersonal estadisticaPersonal = dbWorker.obtenerEstadisticaPersonal(correo);
            estadisticaPersonal.addCant_act_per_rec_mg(tamMsg);
            dbWorker.modificarEstadisticaPersonal(estadisticaPersonal);
        }
        else if(categoria.equals(SendMsg.CATEGORY_ACT_CONTACTO)){
            if(!dbWorker.existeEstadisticaPersonal(correo)){
                ItemEstadisticaPersonal estadisticaPersonal = new ItemEstadisticaPersonal(correo);
                dbWorker.insertarNuevaEstadisticaPersonal(estadisticaPersonal);
            }
            ItemEstadisticaPersonal estadisticaPersonal = dbWorker.obtenerEstadisticaPersonal(correo);
            estadisticaPersonal.addCant_act_per_rec_mg(tamMsg);
            estadisticaPersonal.addCant_act_per_rec(1);
            dbWorker.modificarEstadisticaPersonal(estadisticaPersonal);
        }
        else if(categoria.equals(SendMsg.CATEGORY_ESTADO_PUBLICAR)){
            if(!dbWorker.existeEstadisticaPersonal(correo)){
                ItemEstadisticaPersonal estadisticaPersonal = new ItemEstadisticaPersonal(correo);
                dbWorker.insertarNuevaEstadisticaPersonal(estadisticaPersonal);
            }
            ItemEstadisticaPersonal estadisticaPersonal = dbWorker.obtenerEstadisticaPersonal(correo);
            estadisticaPersonal.addCant_est_rec_mg(tamMsg);
            estadisticaPersonal.addCant_est_rec(1);
            dbWorker.modificarEstadisticaPersonal(estadisticaPersonal);
        }
        else if(categoria.equals(SendMsg.CATEGORY_ESTADO_REACCIONAR)){
            YouChatApplication.addCant_reacciones(1);
            YouChatApplication.addMega_reacciones_rec(tamMsg);
        }
        else if(categoria.equals(SendMsg.CATEGORY_ESTADO_VISTO)){
            YouChatApplication.addCant_vistos_estados(1);
            YouChatApplication.addMega_vistos_estados_rec(tamMsg);
        }
        else if(categoria.equals(SendMsg.CATEGORY_CHAT_EDITAR)){
            if(!dbWorker.existeEstadisticaPersonal(correo)){
                ItemEstadisticaPersonal estadisticaPersonal = new ItemEstadisticaPersonal(correo);
                dbWorker.insertarNuevaEstadisticaPersonal(estadisticaPersonal);
            }
            ItemEstadisticaPersonal estadisticaPersonal = dbWorker.obtenerEstadisticaPersonal(correo);
            estadisticaPersonal.addCant_msg_rec(1);
            estadisticaPersonal.addCant_msg_rec_mg(tamMsg);
            dbWorker.modificarEstadisticaPersonal(estadisticaPersonal);
        }
    }

    public String getNxdiag() {
        return nxdiag;
    }

    public String getGxdiag() {
        return gxdiag;
    }

    public String getNxfaq() {
        return nxfaq;
    }

    public synchronized void borrarMensajeCarpetaTodoGmail(Message[] message){
        new Thread(()->{
            try {
                if(inbox!=null && inbox.isOpen()){
                    AppendUID[] appendUIDS = inbox.appendUIDMessages(message);
                    if(appendUIDS!=null && appendUIDS[0]!=null){
                        AppendUID appendUID = appendUIDS[0];
                        long uid = appendUID.uid;

                        IMAPFolder child = (IMAPFolder) store.getFolder("INBOX/all");
                        child.open(Folder.READ_WRITE);

//                        Folder trashFolder = store.getFolder("INBOX.Trash");
//                        trashFolder.open (Folder.READ_WRITE);
//                        Folder INBOXFolder = store.getFolder("INBOX");
//                        INBOXFolder.open (Folder.READ_ONLY);
//
//                        INBOXFolder.copyMessages(message, trashFolder);
//                        INBOXFolder.close(false);
//                        trashFolder.close(false);

                        IMAPMessage copieMessage = (IMAPMessage) child.getMessageByUID(uid);
                        if(copieMessage!=null){
                            copieMessage.setFlag(Flags.Flag.DELETED, true);
                            child.close(true);
                        }
                        inbox.expunge(message);
                    }
                }
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }).run();
    }

    public synchronized void probandoAllGamil(){
        new Thread(()->{
            try {
                if(inbox!=null && inbox.isOpen()){
                    IMAPFolder folder = (IMAPFolder) store.getDefaultFolder()
                            .getFolder("[Gmail]/All Mail");
                    if(folder!=null){
                        folder.open(Folder.READ_WRITE);
//                        folder.list();
                        int cant = folder.getMessageCount();
                        Log.e("probandoAllGamil: ", ""+cant);
                    }else Log.e(TAG, "probandoAllGamil: no encontro carpeta" );
                }
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }).run();
    }

    //procesadores
    private synchronized void procesarCATEGORY_CHAT_ACT(IMAPMessage message,
                                                        String correo, String idA,
                                                        String hora, String fecha,
                                                        String orden, int tamMsg){
        Utils.runOnUIThread(()->{
            new Thread(()->{
                try{
                    String valTemp = "";
                    String[] temp;
                    actualizarBurbujaDatos(tamMsg);
                    switch (idA){
                        case "-r-"://recibido
                            temp = message.getHeader(ItemChat.KEY_ID_MSG_RESP);
                            if(temp!=null)
                                valTemp=temp[0];
                            String text = valTemp;
                            if (!text.isEmpty() && YouChatApplication.lectura) {
                                actualizarEstadisticaDe(correo,"-r-",CATEGORY_CHAT_ACT,tamMsg);
                                dbWorker.modificarEstadoMensaje(text, ItemChat.ESTADO_RECIBIDO);
                                if (YouChatApplication.chatsActivity != null
                                        && YouChatApplication.chatsActivity.getCorreo().equals(correo)) {
                                    Utils.runOnUIThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(YouChatApplication.chatsActivity!=null)
                                                YouChatApplication.chatsActivity.ActualizarEstadoRecibido(text);
                                        }
                                    });
                                }
                                if (YouChatApplication.principalActivity != null
                                        && YouChatApplication.lectura) {
                                    Utils.runOnUIThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (YouChatApplication.principalActivity != null)
                                                YouChatApplication.principalActivity
                                                    .actualizarUltMsg(correo);
                                        }
                                    });
                                }
                            }
                            break;
                        case "-clear-"://borrar mensaje para todos
                            temp = message.getHeader(ItemChat.KEY_ID_MSG_RESP);
                            if(temp!=null)
                                valTemp=temp[0];
                            String idMsgBorrar = valTemp;
                            dbWorker.eliminarMsg(idMsgBorrar);
                            if (YouChatApplication.chatsActivity != null
                                    && YouChatApplication.chatsActivity.getCorreo()
                                    .equals(correo)) {
                                Utils.runOnUIThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (YouChatApplication.chatsActivity != null)
                                            YouChatApplication.chatsActivity.borrarMensaje(idMsgBorrar);
                                    }
                                });
                            }
                            break;
                        case "-ce-"://borrar estado para todos
                            temp = message.getHeader(ItemChat.KEY_ID_MSG_RESP);
                            if(temp!=null)
                                valTemp=temp[0];
                            String idEstBorrar = valTemp;
                            dbWorker.eliminarElEstadosDe(idEstBorrar);
                            if(YouChatApplication.principalActivity!=null){
                                Utils.runOnUIThread(()->{
                                    if (YouChatApplication.principalActivity != null)
                                        YouChatApplication.principalActivity.ActualizarEstados(false);
                                });
                            }
                            break;
                        case "-e-"://estado personal
                            actualizarEstadisticaDe(correo,"-e-",CATEGORY_CHAT_ACT,tamMsg);
                            if (YouChatApplication.estado_personal) {
                                temp = message.getHeader(ItemChat.KEY_ID_MSG_RESP);
                                if(temp!=null)
                                    valTemp=temp[0];
                                String estadoPersonal = valTemp;
                                YouChatApplication.agregarCorreoyEstadoPersonal(correo, estadoPersonal);
                                if (!estadoPersonal.equals("")) {
                                    if (YouChatApplication.chatsActivity != null) {
                                        Utils.runOnUIThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (YouChatApplication.chatsActivity != null)
                                                    YouChatApplication.chatsActivity
                                                            .introducirEstadoPersonal(correo,
                                                        estadoPersonal, hora, fecha);
                                            }
                                        });
                                    }
                                    if (YouChatApplication.principalActivity != null) {
                                        Utils.runOnUIThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (YouChatApplication.principalActivity != null)
                                                    YouChatApplication.principalActivity
                                                        .actualizarUltMsg(correo);
                                            }
                                        });
                                    }
                                }
                            }
                            break;
                        case "-l-"://linea
                            actualizarEstadisticaDe(correo,"-l-",CATEGORY_CHAT_ACT,tamMsg);
                            if (YouChatApplication.principalActivity != null) {
                                Utils.runOnUIThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (YouChatApplication.principalActivity != null)
                                            YouChatApplication.principalActivity
                                                .actualizarUltMsg(correo);
                                    }
                                });
                            }
                            break;
                        case "-u-"://uni'on
                            actualizarEstadisticaDe(correo,"-u-",CATEGORY_CHAT_ACT,tamMsg);
                            ItemContacto contacto = dbWorker.obtenerContacto(correo);
                            if (contacto != null
                                    && contacto.getTipo_contacto() == ItemContacto.TIPO_CONTACTO) {

                                ItemUsuario usuario = new ItemUsuario(correo);
                                String id = "YouChat/chat/" + correo + "/97/" + orden;
                                ItemChat chat = new ItemChat(correo, "");
                                chat.setId(id);
                                chat.setTipo_mensaje(97);
                                chat.setEstado(ItemChat.ESTADO_VISTO);
                                chat.setOrden(orden);

                                dbWorker.insertarChat(chat);
                                dbWorker.insertarNuevoUsuario(usuario);
                                dbWorker.modificarUsoYCContacto(correo, 1);
                                dbWorker.actualizarUltMsgUsuario(chat);

                                if (YouChatApplication.principalActivity != null) {
                                    Utils.runOnUIThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (YouChatApplication.principalActivity != null)
                                                YouChatApplication.principalActivity
                                                    .actualizarNewMsg(correo,1);
                                        }
                                    });
                                }
                            }
                            break;
                    }
                    eliminarMensaje(message);
                } catch (MessagingException e) {
                    e.printStackTrace();
                    eliminarMensaje(message);
                }
            }).start();
        });

    }

    private synchronized void procesarCATEGORY_CHAT_ACT(IMAPMessage message,
                                                        String correo){
        Utils.runOnUIThread(()->{
            new Thread(()->{
                try{
                    String horaReal = new SimpleDateFormat("yyyyMMddHHmmss").format(message.getSentDate());
                    final String hora = Convertidor.conversionHora(horaReal);
                    final String fecha = Convertidor.conversionFecha(horaReal);
                    procesarOtrosDatos(message, correo, hora, fecha);
                    int tamMsg = message.getSize();
                    String[] datosTemp = message.getHeader(SendMsg.PACK_DATO);
                    String[] pack;
                    if(datosTemp!=null) {
                        pack = datosTemp[0].split("<s&p>");
                        String idA = pack[0];
                        actualizarBurbujaDatos(tamMsg);
                        switch (idA){
                            case "-r-"://recibido
                                String text = pack[1];
                                if (!text.isEmpty() && YouChatApplication.lectura) {
                                    actualizarEstadisticaDe(correo,"-r-",CATEGORY_CHAT_ACT,tamMsg);
                                    dbWorker.modificarEstadoMensaje(text, ItemChat.ESTADO_RECIBIDO);
                                    if (YouChatApplication.chatsActivity != null
                                            && YouChatApplication.chatsActivity.getCorreo().equals(correo)) {
                                        Utils.runOnUIThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (YouChatApplication.chatsActivity != null)
                                                    YouChatApplication.chatsActivity.ActualizarEstadoRecibido(text);
                                            }
                                        });
                                    }
                                    if (YouChatApplication.principalActivity != null
                                            && YouChatApplication.lectura) {
                                        Utils.runOnUIThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (YouChatApplication.principalActivity != null)
                                                    YouChatApplication.principalActivity
                                                        .actualizarUltMsg(correo);
                                            }
                                        });
                                    }
                                }
                                break;
                            case "-clear-"://borrar mensaje para todos
                                String idMsgBorrar = pack[1];
                                dbWorker.eliminarMsg(idMsgBorrar);
                                if (YouChatApplication.chatsActivity != null
                                        && YouChatApplication.chatsActivity.getCorreo()
                                        .equals(correo)) {
                                    Utils.runOnUIThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (YouChatApplication.chatsActivity != null)
                                                YouChatApplication.chatsActivity.borrarMensaje(idMsgBorrar);
                                        }
                                    });
                                }
                                break;
                            case "-ce-"://borrar estado para todos
                                String idEstBorrar = pack[1];
                                dbWorker.eliminarElEstadosDe(idEstBorrar);
                                if(YouChatApplication.principalActivity!=null){
                                    Utils.runOnUIThread(()->{
                                        if (YouChatApplication.principalActivity != null)
                                            YouChatApplication.principalActivity.ActualizarEstados(false);
                                    });
                                }
                                break;
                            case "-e-"://estado personal
                                actualizarEstadisticaDe(correo,"-e-",CATEGORY_CHAT_ACT,tamMsg);
                                if (YouChatApplication.estado_personal) {
                                    String estadoPersonal = pack[1];
                                    YouChatApplication.agregarCorreoyEstadoPersonal(correo, estadoPersonal);
                                    if (!estadoPersonal.equals("")) {
                                        if (YouChatApplication.chatsActivity != null) {
                                            Utils.runOnUIThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (YouChatApplication.chatsActivity != null)
                                                        YouChatApplication.chatsActivity.introducirEstadoPersonal(correo,
                                                            estadoPersonal, hora, fecha);
                                                }
                                            });
                                        }
                                        if (YouChatApplication.principalActivity != null) {
                                            Utils.runOnUIThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (YouChatApplication.principalActivity != null)
                                                        YouChatApplication.principalActivity
                                                            .actualizarUltMsg(correo);
                                                }
                                            });
                                        }
                                    }
                                }
                                break;
                            case "-l-"://linea
                                actualizarEstadisticaDe(correo,"-l-",CATEGORY_CHAT_ACT,tamMsg);
                                if (YouChatApplication.principalActivity != null) {
                                    Utils.runOnUIThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (YouChatApplication.principalActivity != null)
                                                YouChatApplication.principalActivity
                                                    .actualizarUltMsg(correo);
                                        }
                                    });
                                }
                                break;
                            case "-u-"://uni'on
                                actualizarEstadisticaDe(correo,"-u-",CATEGORY_CHAT_ACT,tamMsg);
                                ItemContacto contacto = dbWorker.obtenerContacto(correo);
                                if (contacto != null
                                        && contacto.getTipo_contacto() == ItemContacto.TIPO_CONTACTO) {

                                    String orden = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault()).format(new Date());

                                    ItemUsuario usuario = new ItemUsuario(correo);
                                    String id = "YouChat/chat/" + correo + "/97/" + orden;
                                    ItemChat chat = new ItemChat(correo, "");
                                    chat.setId(id);
                                    chat.setTipo_mensaje(97);
                                    chat.setEstado(ItemChat.ESTADO_VISTO);
                                    chat.setOrden(orden);

                                    dbWorker.insertarChat(chat);
                                    dbWorker.insertarNuevoUsuario(usuario);
                                    dbWorker.modificarUsoYCContacto(correo, 1);
                                    dbWorker.actualizarUltMsgUsuario(chat);

                                    if (YouChatApplication.principalActivity != null) {
                                        Utils.runOnUIThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (YouChatApplication.principalActivity != null)
                                                    YouChatApplication.principalActivity
                                                        .actualizarNewMsg(correo,1);
                                            }
                                        });
                                    }
                                }
                                break;
                        }
                    }
                    eliminarMensaje(message);
                } catch (MessagingException e) {
                    e.printStackTrace();
                    eliminarMensaje(message);
                }
            }).start();
        });

    }

    private synchronized void procesarCATEGORY_CHAT(IMAPMessage message,
                                                    String correo, String idA,
                                                    String hora, String fecha,
                                                    String orden, int tamMsg){
        try{
            if(!dbWorker.existeChat(idA)){
                boolean puedeBorrar = true;
                String valTemp = "0";
                String[] datoTemp = message.getHeader(ItemChat.KEY_ESTA_ENCRIPTADO);
                if (datoTemp != null)
                    valTemp = datoTemp[0];
                final boolean estaEncryptado = valTemp.equals("1");
                ItemChat chat = Convertidor.createItemChatOfMessage(message, idA, correo, hora, fecha, orden);
                if (chat != null) {
                    if (chat.esDeEstaVersion()) {
                        if (YouChatApplication.lectura) {
                            datoTemp = message.getHeader(ItemChat.KEY_LECTURA);
                            if (datoTemp != null) {
                                boolean confirmacion = datoTemp[0].equals("1");
                                Utils.runOnUIThread(()->{
                                    if (confirmacion) {
                                        String horaN = Convertidor.conversionHora(orden);
                                        String fechaN = Convertidor.conversionFecha(orden);
                                        ItemChat newChat = new ItemChat(chat.getCorreo(), chat.getId());
                                        newChat.setHora(horaN);
                                        newChat.setFecha(fechaN);
                                        sendMsg.enviarMsg(newChat, CATEGORY_CHAT_ACT);
                                    }
                                });
                            }
                        }
                        if (chat.esMsgTexto() || chat.esContacto()
                                || chat.esTarjeta() || chat.esTema()) {
                            String text = message.getContent().toString().trim();
                            if(estaEncryptado) text = Utils.decrypt(text,YouChatApplication.decod);
                            chat.setMensaje(text);
                        }
                        else {
                            Multipart multi;
                            multi = (Multipart) message.getContent();
                            int cant = multi.getCount();
                            for (int j = 0; j < cant; j++) {
                                Part unaParte = multi.getBodyPart(j);
                                if (unaParte.isMimeType("text/*")) {
                                    String text = unaParte.getContent().toString().trim();
                                    if(estaEncryptado) text = Utils.decrypt(text,YouChatApplication.decod);
                                    chat.setMensaje(text);
                                }
                                else{
                                    int size = unaParte.getSize();
                                    if (!YouChatApplication.descargaAutMultimediaChat
                                            || size / 1024 > YouChatApplication.tam_max_descarga_chat) {
                                        puedeBorrar = false;
                                        tamMsg-=size;
                                        puedeBorrar = false;
                                        chat.setId_mensaje(message.getMessageID());
                                        chat.setPeso(size);
                                        chat.setDescargado(false);

                                    }
                                    else {
                                        if (chat.esImagen()) {
                                            if(estaEncryptado){
                                                MimeBodyPart mbp = (MimeBodyPart) unaParte;
                                                String nombreMulti = mbp.getFileName();
                                                File dirDestino = new File(YouChatApplication.RUTA_MULTIMEDIA_CACHE);
                                                boolean existDestino = dirDestino.exists();
                                                if(!existDestino) existDestino = dirDestino.mkdirs();
                                                if(existDestino){
                                                    File multiEncriptada = new File(dirDestino,nombreMulti);
                                                    mbp.saveFile(multiEncriptada);

                                                    String nombres_img = "img" + chat.getOrden() + ".jpg";
                                                    String ruta_Dato = YouChatApplication.RUTA_IMAGENES_RECIBIDA + nombres_img;

                                                    File file = new File(YouChatApplication.RUTA_IMAGENES_RECIBIDA);
                                                    boolean estaCreada = file.exists();
                                                    if (!estaCreada)
                                                        estaCreada = file.mkdirs();
                                                    if (estaCreada) {
                                                        String pass = Utils.MD5("YouChat");
                                                        Utils.descomprimirArchivo(multiEncriptada,YouChatApplication.RUTA_IMAGENES_RECIBIDA,nombreMulti,nombres_img,pass);
                                                    }
                                                    chat.setRuta_Dato(ruta_Dato);
                                                }
                                            }else {
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
                                        }
                                        else if (chat.esAudio()) {
                                            if(estaEncryptado){
                                                MimeBodyPart mbp = (MimeBodyPart) unaParte;
                                                String nombreMulti = mbp.getFileName();
                                                File dirDestino = new File(YouChatApplication.RUTA_MULTIMEDIA_CACHE);
                                                boolean existDestino = dirDestino.exists();
                                                if(!existDestino) existDestino = dirDestino.mkdirs();
                                                if(existDestino){
                                                    File multiEncriptada = new File(dirDestino,nombreMulti);
                                                    mbp.saveFile(multiEncriptada);

                                                    String nombre_aud = "rec" + chat.getOrden() + ".ycaudio";
                                                    String ruta_Dato = YouChatApplication.RUTA_AUDIOS_RECIBIDOS + nombre_aud;

                                                    File file = new File(YouChatApplication.RUTA_AUDIOS_RECIBIDOS);
                                                    boolean estaCreada = file.exists();
                                                    if (!estaCreada)
                                                        estaCreada = file.mkdirs();

                                                    if (estaCreada) {
                                                        String pass = Utils.MD5("YouChat");
                                                        Utils.descomprimirArchivo(multiEncriptada,YouChatApplication.RUTA_AUDIOS_RECIBIDOS,nombreMulti,nombre_aud,pass);
                                                    }
                                                    chat.setRuta_Dato(ruta_Dato);
                                                }
                                            }
                                            else {
                                                String nombre_aud = "rec" + chat.getOrden() + ".ycaudio";
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
                                            }

                                        }
                                        else if (chat.esArchivo()) {
                                            if(estaEncryptado){
                                                MimeBodyPart mbp = (MimeBodyPart) unaParte;
                                                String nombreMulti = mbp.getFileName();
                                                File dirDestino = new File(YouChatApplication.RUTA_MULTIMEDIA_CACHE);
                                                boolean existDestino = dirDestino.exists();
                                                if(!existDestino) existDestino = dirDestino.mkdirs();
                                                if(existDestino){
                                                    File multiEncriptada = new File(dirDestino,nombreMulti);
                                                    mbp.saveFile(multiEncriptada);

                                                    String nombre_arc = mbp.getFileName();
                                                    if (nombre_arc.equals(""))
                                                        nombre_arc = "archivo" + chat.getOrden() + ".desconocido";
                                                    String ruta_Dato = YouChatApplication.RUTA_ARCHIVOS_RECIBIDOS + nombre_arc;

                                                    File file = new File(YouChatApplication.RUTA_ARCHIVOS_RECIBIDOS);
                                                    boolean estaCreada = file.exists();
                                                    if (!estaCreada)
                                                        estaCreada = file.mkdirs();

                                                    if (estaCreada) {
                                                        String pass = Utils.MD5("YouChat");
                                                        Utils.descomprimirArchivo(multiEncriptada,YouChatApplication.RUTA_ARCHIVOS_RECIBIDOS,nombreMulti,nombre_arc,pass);
                                                    }
                                                    chat.setRuta_Dato(ruta_Dato);
                                                }
                                            }
                                            else {
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
                                        else if (chat.esSticker()) {
                                            MimeBodyPart mbp = (MimeBodyPart) unaParte;
                                            String nombreMulti = unaParte.getFileName();
                                            File file = new File(YouChatApplication.RUTA_STICKERS_RECIBIDOS);
                                            boolean estaCreada = file.exists();
                                            if (!estaCreada)
                                                estaCreada = file.mkdirs();
                                            if(estaCreada){
                                                if (nombreMulti==null || nombreMulti.isEmpty())
                                                    nombreMulti = "sticker" + chat.getOrden() + ".sticker";
                                                String ruta_Dato = YouChatApplication.RUTA_STICKERS_RECIBIDOS + nombreMulti;
                                                if(!new File(ruta_Dato).exists()){
                                                    if(estaEncryptado){
                                                        File dirDestino = new File(YouChatApplication.RUTA_MULTIMEDIA_CACHE);
                                                        boolean existDestino = dirDestino.exists();
                                                        if(!existDestino) existDestino = dirDestino.mkdirs();
                                                        if(existDestino){
                                                            File multiEncriptada = new File(dirDestino,nombreMulti);
                                                            mbp.saveFile(multiEncriptada);

                                                            String pass = Utils.MD5("YouChat");
//                                                    String[] nomArc = nombreMulti.split("_spyc_");
//                                                    Utils.descomprimirArchivo(multiEncriptada,YouChatApplication.RUTA_STICKERS_RECIBIDOS,nomArc[nomArc.length-1],nombreMulti,pass);
                                                            Utils.descomprimirArchivo(multiEncriptada,YouChatApplication.RUTA_STICKERS_RECIBIDOS,nombreMulti,nombreMulti,pass);
                                                        }
                                                    }
                                                    else {
                                                        mbp.saveFile(ruta_Dato);
                                                    }
                                                }
                                                else tamMsg-=size;
                                                chat.setRuta_Dato(ruta_Dato);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        actualizarBurbujaDatos(tamMsg);
                        actualizarEstadisticaChatDe(chat,CATEGORY_CHAT,tamMsg);
                        Utils.runOnUIThread(()->{
                            dbWorker.insertarChat(chat);
                            ItemUsuario usuario = new ItemUsuario(chat.getCorreo());
                            ItemContacto contacto = new ItemContacto(chat.getCorreo(), chat.getCorreo());
                            dbWorker.insertarNuevoUsuario(usuario);
                            dbWorker.insertarNuevoContactoNoVisible(contacto, true);
                            dbWorker.actualizarUltMsgUsuario(chat);

                            YouChatApplication.eliminarEstadoPersonalSiExisteDe(chat.getCorreo());
                            tiempoDeBorrar = 0;

                            if (YouChatApplication.chatsActivity != null &&
                                    correo.equals(YouChatApplication.chatsActivity.getCorreo())) {
                                Utils.runOnUIThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(YouChatApplication.chatsActivity!=null)
                                            YouChatApplication.chatsActivity.ActualizarChatMsgRecibido(chat);
                                    }
                                });
                                if(YouChatApplication.chatsActivity.estaMinimizada()){
                                    if (YouChatApplication.notificacion
                                            && !dbWorker.estaSilenciado(chat.getCorreo())){
                                        crearNotification(chat);
                                    }
                                }
                            }
                            else {
                                dbWorker.actualizarCantMensajesNoVistosX(chat.getCorreo(), 1);
                                if (YouChatApplication.notificacion
                                        && !dbWorker.estaSilenciado(chat.getCorreo())){
                                    crearNotification(chat);
                                }
                            }

                            if (YouChatApplication.principalActivity != null) {
                                Utils.runOnUIThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (YouChatApplication.principalActivity != null)
                                            YouChatApplication.principalActivity
                                                    .actualizarNewMsg(correo,1);
                                    }
                                });
                            }
                        });
                    }
                }
                if(puedeBorrar)
                    eliminarMensaje(message);
            }
            else if(dbWorker.estaDescargadoChat(idA)) eliminarMensaje(message);

        } catch (MessagingException e) {
            e.printStackTrace();
            eliminarMensaje(message);
        } catch (IOException e) {
            e.printStackTrace();
            eliminarMensaje(message);
        } catch (Exception e) {
            e.printStackTrace();
            eliminarMensaje(message);
        }
    }

    private synchronized void procesarCATEGORY_CHAT(IMAPMessage message, String correo){
        try{
            String stringTemp = "";
            String[] datoTemp = message.getHeader(ItemChat.KEY_ID);
            if (datoTemp != null)
                stringTemp = datoTemp[0];
            final String idA = stringTemp;
            if(!idA.isEmpty() && !dbWorker.existeChat(idA)){
                boolean puedeBorrar = true;
                String horaReal = new SimpleDateFormat("yyyyMMddHHmmss").format(message.getSentDate());
                final String hora = Convertidor.conversionHora(horaReal);
                final String fecha = Convertidor.conversionFecha(horaReal);
                procesarOtrosDatos(message, correo, hora, fecha);
                datoTemp = message.getHeader(SendMsg.PACK_DATO);
                if(datoTemp!=null){
                    String jsonString = Utils.decrypt(datoTemp[0],YouChatApplication.decod);
                    JSONObject jsonObject = new JSONObject(jsonString);
                    if(jsonObject!=null){
                        int tamMsg = message.getSize();
                        String orden = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault()).format(new Date());
                        ItemChat chat = new ItemChat(idA,jsonObject.getInt("tipoMsg"),
                                ItemChat.ESTADO_RECIBIDO,correo,"","",
                                hora,fecha,jsonObject.getString("idMsgRes")
                                ,correo,jsonObject.getBoolean("esReenviado"),orden, false
                                ,message.getMessageID(),message.getSize(),true);
                        stringTemp = "0";
                        datoTemp = message.getHeader(ItemChat.KEY_ESTA_ENCRIPTADO);
                        if (datoTemp != null)
                            stringTemp = datoTemp[0];
                        final boolean estaEncryptado = stringTemp.equals("1");
                        if (chat != null) {
                            if (chat.esDeEstaVersion()) {
                                if (YouChatApplication.lectura) {
                                    Utils.runOnUIThread(()->{
                                        try {
                                            if (jsonObject.getBoolean("lectura")) {
                                                String horaN = Convertidor.conversionHora(orden);
                                                String fechaN = Convertidor.conversionFecha(orden);
                                                ItemChat newChat = new ItemChat(chat.getCorreo(), chat.getId());
                                                newChat.setHora(horaN);
                                                newChat.setFecha(fechaN);
                                                sendMsg.enviarMsg(newChat, CATEGORY_CHAT_ACT);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    });
                                }
                                if (chat.esMsgTexto() || chat.esContacto()
                                        || chat.esTarjeta() || chat.esTema()) {
                                    String text = message.getContent().toString().trim();
                                    if(estaEncryptado) text = Utils.decrypt(text,YouChatApplication.decod);
                                    chat.setMensaje(text);
                                }
                                else {
                                    Multipart multi;
                                    multi = (Multipart) message.getContent();
                                    int cant = multi.getCount();
                                    for (int j = 0; j < cant; j++) {
                                        Part unaParte = multi.getBodyPart(j);
                                        if (unaParte.isMimeType("text/*")) {
                                            String text = unaParte.getContent().toString().trim();
                                            if(estaEncryptado) text = Utils.decrypt(text,YouChatApplication.decod);
                                            chat.setMensaje(text);
                                        }
                                        else{
                                            int size = unaParte.getSize();
                                            if (!YouChatApplication.descargaAutMultimediaChat
                                                    || size / 1024 > YouChatApplication.tam_max_descarga_chat) {
                                                puedeBorrar = false;
                                                tamMsg-=size;
                                                chat.setId_mensaje(message.getMessageID());
                                                chat.setPeso(size);
                                                chat.setDescargado(false);

                                            }
                                            else {
                                                if (chat.esImagen()) {
                                                    if(estaEncryptado){
                                                        MimeBodyPart mbp = (MimeBodyPart) unaParte;
                                                        String nombreMulti = mbp.getFileName();
                                                        File dirDestino = new File(YouChatApplication.RUTA_MULTIMEDIA_CACHE);
                                                        boolean existDestino = dirDestino.exists();
                                                        if(!existDestino) existDestino = dirDestino.mkdirs();
                                                        if(existDestino){
                                                            File multiEncriptada = new File(dirDestino,nombreMulti);
                                                            mbp.saveFile(multiEncriptada);

                                                            String nombres_img = "img" + chat.getOrden() + ".jpg";
                                                            String ruta_Dato = YouChatApplication.RUTA_IMAGENES_RECIBIDA + nombres_img;

                                                            File file = new File(YouChatApplication.RUTA_IMAGENES_RECIBIDA);
                                                            boolean estaCreada = file.exists();
                                                            if (!estaCreada)
                                                                estaCreada = file.mkdirs();
                                                            if (estaCreada) {
                                                                String pass = Utils.MD5("YouChat");
                                                                Utils.descomprimirArchivo(multiEncriptada,YouChatApplication.RUTA_IMAGENES_RECIBIDA,nombreMulti,nombres_img,pass);
                                                            }
                                                            chat.setRuta_Dato(ruta_Dato);
                                                        }
                                                    }else {
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
                                                }
                                                else if (chat.esAudio()) {
                                                    if(estaEncryptado){
                                                        MimeBodyPart mbp = (MimeBodyPart) unaParte;
                                                        String nombreMulti = mbp.getFileName();
                                                        File dirDestino = new File(YouChatApplication.RUTA_MULTIMEDIA_CACHE);
                                                        boolean existDestino = dirDestino.exists();
                                                        if(!existDestino) existDestino = dirDestino.mkdirs();
                                                        if(existDestino){
                                                            File multiEncriptada = new File(dirDestino,nombreMulti);
                                                            mbp.saveFile(multiEncriptada);

                                                            String nombre_aud = "rec" + chat.getOrden() + ".ycaudio";
                                                            String ruta_Dato = YouChatApplication.RUTA_AUDIOS_RECIBIDOS + nombre_aud;

                                                            File file = new File(YouChatApplication.RUTA_AUDIOS_RECIBIDOS);
                                                            boolean estaCreada = file.exists();
                                                            if (!estaCreada)
                                                                estaCreada = file.mkdirs();

                                                            if (estaCreada) {
                                                                String pass = Utils.MD5("YouChat");
                                                                Utils.descomprimirArchivo(multiEncriptada,YouChatApplication.RUTA_AUDIOS_RECIBIDOS,nombreMulti,nombre_aud,pass);
                                                            }
                                                            chat.setRuta_Dato(ruta_Dato);
                                                        }
                                                    }
                                                    else {
                                                        String nombre_aud = "rec" + chat.getOrden() + ".ycaudio";
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
                                                    }

                                                }
                                                else if (chat.esArchivo()) {
                                                    if(estaEncryptado){
                                                        MimeBodyPart mbp = (MimeBodyPart) unaParte;
                                                        String nombreMulti = mbp.getFileName();
                                                        File dirDestino = new File(YouChatApplication.RUTA_MULTIMEDIA_CACHE);
                                                        boolean existDestino = dirDestino.exists();
                                                        if(!existDestino) existDestino = dirDestino.mkdirs();
                                                        if(existDestino){
                                                            File multiEncriptada = new File(dirDestino,nombreMulti);
                                                            mbp.saveFile(multiEncriptada);

                                                            String nombre_arc = mbp.getFileName();
                                                            if (nombre_arc.equals(""))
                                                                nombre_arc = "archivo" + chat.getOrden() + ".desconocido";
                                                            String ruta_Dato = YouChatApplication.RUTA_ARCHIVOS_RECIBIDOS + nombre_arc;

                                                            File file = new File(YouChatApplication.RUTA_ARCHIVOS_RECIBIDOS);
                                                            boolean estaCreada = file.exists();
                                                            if (!estaCreada)
                                                                estaCreada = file.mkdirs();

                                                            if (estaCreada) {
                                                                String pass = Utils.MD5("YouChat");
                                                                Utils.descomprimirArchivo(multiEncriptada,YouChatApplication.RUTA_ARCHIVOS_RECIBIDOS,nombreMulti,nombre_arc,pass);
                                                            }
                                                            chat.setRuta_Dato(ruta_Dato);
                                                        }
                                                    }
                                                    else {
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
                                                else if (chat.esSticker()) {
                                                    MimeBodyPart mbp = (MimeBodyPart) unaParte;
                                                    String nombreMulti = unaParte.getFileName();
                                                    File file = new File(YouChatApplication.RUTA_STICKERS_RECIBIDOS);
                                                    boolean estaCreada = file.exists();
                                                    if (!estaCreada)
                                                        estaCreada = file.mkdirs();
                                                    if(estaCreada){
                                                        if (nombreMulti==null || nombreMulti.isEmpty())
                                                            nombreMulti = "sticker" + chat.getOrden() + ".sticker";
                                                        String ruta_Dato = YouChatApplication.RUTA_STICKERS_RECIBIDOS + nombreMulti;
                                                        if(!new File(ruta_Dato).exists()){
                                                            if(estaEncryptado){
                                                                File dirDestino = new File(YouChatApplication.RUTA_MULTIMEDIA_CACHE);
                                                                boolean existDestino = dirDestino.exists();
                                                                if(!existDestino) existDestino = dirDestino.mkdirs();
                                                                if(existDestino){
                                                                    File multiEncriptada = new File(dirDestino,nombreMulti);
                                                                    mbp.saveFile(multiEncriptada);

                                                                    String pass = Utils.MD5("YouChat");
//                                                    String[] nomArc = nombreMulti.split("_spyc_");
//                                                    Utils.descomprimirArchivo(multiEncriptada,YouChatApplication.RUTA_STICKERS_RECIBIDOS,nomArc[nomArc.length-1],nombreMulti,pass);
                                                                    Utils.descomprimirArchivo(multiEncriptada,YouChatApplication.RUTA_STICKERS_RECIBIDOS,nombreMulti,nombreMulti,pass);
                                                                }
                                                            }
                                                            else {
                                                                mbp.saveFile(ruta_Dato);
                                                            }
                                                        }
                                                        else tamMsg-=size;
                                                        chat.setRuta_Dato(ruta_Dato);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                actualizarBurbujaDatos(tamMsg);
                                actualizarEstadisticaChatDe(chat,CATEGORY_CHAT,tamMsg);
                                Utils.runOnUIThread(()->{
                                    dbWorker.insertarChat(chat);
                                    ItemUsuario usuario = new ItemUsuario(chat.getCorreo());
                                    ItemContacto contacto = new ItemContacto(chat.getCorreo(), chat.getCorreo());
                                    dbWorker.insertarNuevoUsuario(usuario);
                                    dbWorker.insertarNuevoContactoNoVisible(contacto, true);
                                    dbWorker.actualizarUltMsgUsuario(chat);

                                    YouChatApplication.eliminarEstadoPersonalSiExisteDe(chat.getCorreo());
                                    tiempoDeBorrar = 0;

                                    if (YouChatApplication.chatsActivity != null &&
                                            correo.equals(YouChatApplication.chatsActivity.getCorreo())) {
                                        Utils.runOnUIThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if(YouChatApplication.chatsActivity!=null)
                                                    YouChatApplication.chatsActivity.ActualizarChatMsgRecibido(chat);
                                            }
                                        });
                                        if(YouChatApplication.chatsActivity.estaMinimizada()){
                                            if (YouChatApplication.notificacion
                                                    && !dbWorker.estaSilenciado(chat.getCorreo())){
                                                crearNotification(chat);
                                            }
                                        }
                                    }
                                    else {
                                        dbWorker.actualizarCantMensajesNoVistosX(chat.getCorreo(), 1);
                                        if (YouChatApplication.notificacion
                                                && !dbWorker.estaSilenciado(chat.getCorreo())){
                                            crearNotification(chat);
                                        }
                                    }

                                    if (YouChatApplication.principalActivity != null) {
                                        Utils.runOnUIThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (YouChatApplication.principalActivity != null)
                                                    YouChatApplication.principalActivity
                                                            .actualizarNewMsg(correo,1);
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    }
                }
                if(puedeBorrar)
                    eliminarMensaje(message);
            }
            else if(idA.isEmpty() || dbWorker.estaDescargadoChat(idA)) eliminarMensaje(message);

        } catch (MessagingException e) {
            e.printStackTrace();
            eliminarMensaje(message);
        } catch (IOException e) {
            e.printStackTrace();
            eliminarMensaje(message);
        } catch (Exception e) {
            e.printStackTrace();
            eliminarMensaje(message);
        }
    }

    private synchronized void procesarCATEGORY_CHAT(IMAPMessage message, String correo, boolean a){
        try{
            String horaReal = new SimpleDateFormat("yyyyMMddHHmmss").format(message.getSentDate());
            final String hora = Convertidor.conversionHora(horaReal);
            final String fecha = Convertidor.conversionFecha(horaReal);
            int tamMsg = message.getSize();
            String orden = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault()).format(new Date());

            procesarOtrosDatos(message, correo, hora, fecha);

            String[] datosTemp = message.getHeader(SendMsg.PACK_DATO);
            String[] pack;
            if(datosTemp!=null){
                pack = datosTemp[0].split("<s&p>");
                String idA = pack[0];
                boolean puedeBorrar = true;
                if(!dbWorker.existeChat(idA)){
                    final boolean estaEncryptado = pack[5].equals("1");
                    ItemChat chat = new ItemChat(idA,Convertidor.createIntOfString(pack[2]),
                            ItemChat.ESTADO_RECIBIDO,correo,"","",
                        hora,fecha,pack[3],correo,pack[4].equals("1"),orden, false
                            ,message.getMessageID(),message.getSize(),true);
                    if (chat.esDeEstaVersion()) {
                        actualizarBurbujaDatos(tamMsg);
                        actualizarEstadisticaChatDe(chat,CATEGORY_CHAT,tamMsg);
                        if (YouChatApplication.lectura) {
                            boolean confirmacion = pack[1].equals("1");
                            Utils.runOnUIThread(()->{
                                if (confirmacion) {
                                    String horaN = Convertidor.conversionHora(orden);
                                    String fechaN = Convertidor.conversionFecha(orden);
                                    ItemChat newChat = new ItemChat(chat.getCorreo(), chat.getId());
                                    newChat.setHora(horaN);
                                    newChat.setFecha(fechaN);
                                    sendMsg.enviarMsg(newChat, CATEGORY_CHAT_ACT);
                                }
                            });
                        }

                        if (chat.esMsgTexto() || chat.esContacto()
                                || chat.esTarjeta() || chat.esTema()) {
                            String text = message.getContent().toString().trim();
                            if(estaEncryptado) text = Utils.decrypt(text,YouChatApplication.decod);
                            chat.setMensaje(text);
                        }
                        else {
                            Multipart multi;
                            multi = (Multipart) message.getContent();
                            int cant = multi.getCount();
                            for (int j = 0; j < cant; j++) {
                                Part unaParte = multi.getBodyPart(j);
                                if (unaParte.isMimeType("text/*")) {
                                    String text = unaParte.getContent().toString().trim();
                                    if(estaEncryptado) text = Utils.decrypt(text,YouChatApplication.decod);
                                    chat.setMensaje(text);
                                }
                                else if (chat.esImagen()) {
                                    if(estaEncryptado){
                                        MimeBodyPart mbp = (MimeBodyPart) unaParte;
                                        String nombreMulti = mbp.getFileName();
                                        File dirDestino = new File(YouChatApplication.RUTA_MULTIMEDIA_CACHE);
                                        boolean existDestino = dirDestino.exists();
                                        if(!existDestino) existDestino = dirDestino.mkdirs();
                                        if(existDestino){
                                            File multiEncriptada = new File(dirDestino,nombreMulti);
                                            mbp.saveFile(multiEncriptada);

                                            String nombres_img = "img" + chat.getOrden() + ".jpg";
                                            String ruta_Dato = YouChatApplication.RUTA_IMAGENES_RECIBIDA + nombres_img;

                                            File file = new File(YouChatApplication.RUTA_IMAGENES_RECIBIDA);
                                            boolean estaCreada = file.exists();
                                            if (!estaCreada)
                                                estaCreada = file.mkdirs();
                                            if (estaCreada) {
                                                String pass = Utils.MD5("YouChat");
                                                Utils.descomprimirArchivo(multiEncriptada,YouChatApplication.RUTA_IMAGENES_RECIBIDA,nombreMulti,nombres_img,pass);

                                            }
                                            chat.setRuta_Dato(ruta_Dato);
                                        }
                                    }else {
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
                                }
                                else if (chat.esAudio()) {
                                    if(estaEncryptado){
                                        MimeBodyPart mbp = (MimeBodyPart) unaParte;
                                        String nombreMulti = mbp.getFileName();
                                        File dirDestino = new File(YouChatApplication.RUTA_MULTIMEDIA_CACHE);
                                        boolean existDestino = dirDestino.exists();
                                        if(!existDestino) existDestino = dirDestino.mkdirs();
                                        if(existDestino){
                                            File multiEncriptada = new File(dirDestino,nombreMulti);
                                            mbp.saveFile(multiEncriptada);

                                            String nombre_aud = "rec" + chat.getOrden() + ".ycaudio";
                                            String ruta_Dato = YouChatApplication.RUTA_AUDIOS_RECIBIDOS + nombre_aud;

                                            File file = new File(YouChatApplication.RUTA_AUDIOS_RECIBIDOS);
                                            boolean estaCreada = file.exists();
                                            if (!estaCreada)
                                                estaCreada = file.mkdirs();

                                            if (estaCreada) {
                                                String pass = Utils.MD5("YouChat");
                                                Utils.descomprimirArchivo(multiEncriptada,YouChatApplication.RUTA_AUDIOS_RECIBIDOS,nombreMulti,nombre_aud,pass);

                                            }
                                            chat.setRuta_Dato(ruta_Dato);
                                        }
                                    }
                                    else {
                                        String nombre_aud = "rec" + chat.getOrden() + ".ycaudio";
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
                                    }

                                }
                                else if (chat.esArchivo()) {
                                    if(estaEncryptado){
                                        MimeBodyPart mbp = (MimeBodyPart) unaParte;
                                        String nombreMulti = mbp.getFileName();
                                        File dirDestino = new File(YouChatApplication.RUTA_MULTIMEDIA_CACHE);
                                        boolean existDestino = dirDestino.exists();
                                        if(!existDestino) existDestino = dirDestino.mkdirs();
                                        if(existDestino){
                                            File multiEncriptada = new File(dirDestino,nombreMulti);
                                            mbp.saveFile(multiEncriptada);

                                            String nombre_arc = mbp.getFileName();
                                            if (nombre_arc.equals(""))
                                                nombre_arc = "archivo" + chat.getOrden() + ".desconocido";
                                            String ruta_Dato = YouChatApplication.RUTA_ARCHIVOS_RECIBIDOS + nombre_arc;

                                            File file = new File(YouChatApplication.RUTA_ARCHIVOS_RECIBIDOS);
                                            boolean estaCreada = file.exists();
                                            if (!estaCreada)
                                                estaCreada = file.mkdirs();

                                            if (estaCreada) {
                                                String pass = Utils.MD5("YouChat");
                                                Utils.descomprimirArchivo(multiEncriptada,YouChatApplication.RUTA_ARCHIVOS_RECIBIDOS,nombreMulti,nombre_arc,pass);

                                            }
                                            chat.setRuta_Dato(ruta_Dato);
                                        }
                                    }
                                    else {
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
                                else if (chat.esSticker()) {
                                    MimeBodyPart mbp = (MimeBodyPart) unaParte;
                                    String nombreMulti = mbp.getFileName();
                                    File file = new File(YouChatApplication.RUTA_STICKERS_RECIBIDOS);
                                    boolean estaCreada = file.exists();
                                    if (!estaCreada)
                                        estaCreada = file.mkdirs();
                                    if(estaCreada){
                                        if (nombreMulti.equals(""))
                                            nombreMulti = "sticker" + chat.getOrden() + ".sticker";
                                        String ruta_Dato = YouChatApplication.RUTA_STICKERS_RECIBIDOS + nombreMulti;
                                        if(!new File(ruta_Dato).exists()){
                                            Log.e(TAG, "entro a des sticker" );
                                            if(estaEncryptado){
                                                File dirDestino = new File(YouChatApplication.RUTA_MULTIMEDIA_CACHE);
                                                boolean existDestino = dirDestino.exists();
                                                if(!existDestino) existDestino = dirDestino.mkdirs();
                                                if(existDestino){
                                                    File multiEncriptada = new File(dirDestino,nombreMulti);
                                                    mbp.saveFile(multiEncriptada);

                                                    String pass = Utils.MD5("YouChat");
//                                                    String[] nomArc = nombreMulti.split("_spyc_");
//                                                    Utils.descomprimirArchivo(multiEncriptada,YouChatApplication.RUTA_STICKERS_RECIBIDOS,nomArc[nomArc.length-1],nombreMulti,pass);
                                                    Utils.descomprimirArchivo(multiEncriptada,YouChatApplication.RUTA_STICKERS_RECIBIDOS,nombreMulti,nombreMulti,pass);
                                                }
                                            }
                                            else {
                                                mbp.saveFile(ruta_Dato);
                                            }
                                        }
                                        chat.setRuta_Dato(ruta_Dato);
                                    }
                                }
                            }
                        }

                        Utils.runOnUIThread(()->{
                            dbWorker.insertarChat(chat);
                            ItemUsuario usuario = new ItemUsuario(chat.getCorreo());
                            ItemContacto contacto = new ItemContacto(chat.getCorreo(), chat.getCorreo());
                            dbWorker.insertarNuevoUsuario(usuario);
                            dbWorker.insertarNuevoContactoNoVisible(contacto, true);
                            dbWorker.actualizarUltMsgUsuario(chat);

                            YouChatApplication.eliminarEstadoPersonalSiExisteDe(chat.getCorreo());
                            tiempoDeBorrar = 0;

                            if (YouChatApplication.chatsActivity != null &&
                                    correo.equals(YouChatApplication.chatsActivity.getCorreo())) {
                                Utils.runOnUIThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (YouChatApplication.chatsActivity != null)
                                            YouChatApplication.chatsActivity.ActualizarChatMsgRecibido(chat);
                                    }
                                });
                                if(YouChatApplication.chatsActivity.estaMinimizada()){
                                    if (YouChatApplication.notificacion
                                            && !dbWorker.estaSilenciado(chat.getCorreo())){
                                        crearNotification(chat);
                                    }
                                }
                            }
                            else {
                                dbWorker.actualizarCantMensajesNoVistosX(chat.getCorreo(), 1);
                                if (YouChatApplication.notificacion
                                        && !dbWorker.estaSilenciado(chat.getCorreo())){
                                    crearNotification(chat);
                                }
                            }

                            if (YouChatApplication.principalActivity != null) {
                                Utils.runOnUIThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (YouChatApplication.principalActivity != null)
                                            YouChatApplication.principalActivity
                                                .actualizarNewMsg(correo,1);
                                    }
                                });
                            }
                        });
                    }

                    if(puedeBorrar)
                        eliminarMensaje(message);
                }
                else if(dbWorker.estaDescargadoChat(idA)) eliminarMensaje(message);
            }
            else eliminarMensaje(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            eliminarMensaje(message);
        } catch (IOException e) {
            e.printStackTrace();
            eliminarMensaje(message);
        } catch (Exception e) {
            e.printStackTrace();
            eliminarMensaje(message);
        }
    }

    private synchronized void procesarCATEGORY_SOL_CONTACTO(IMAPMessage message,
                                                            String correo, int tamMsg){
        if(YouChatApplication.actualizar_perfil){
            actualizarBurbujaDatos(tamMsg);
            actualizarEstadisticaDe(correo,"",CATEGORY_SOL_CONTACTO,tamMsg);
            ItemContacto contacto = dbWorker.obtenerContacto(correo);
            if (contacto != null && contacto.esUsuario()){
                agregarAListaContactosActualizar(correo);
            }
        }
        eliminarMensaje(message);
    }

    private synchronized void procesarCATEGORY_SOL_CONTACTO(IMAPMessage message,String correo){
        if(YouChatApplication.actualizar_perfil){
            int tamMsg = 0;
            try {
                tamMsg = message.getSize();
                actualizarBurbujaDatos(tamMsg);
                actualizarEstadisticaDe(correo,"",CATEGORY_SOL_CONTACTO,tamMsg);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            ItemContacto contacto = dbWorker.obtenerContacto(correo);
            if (contacto != null && contacto.esUsuario()){
                agregarAListaContactosActualizar(correo);
            }
        }
        eliminarMensaje(message);
    }

    private synchronized void procesarCATEGORY_ACT_CONTACTO(IMAPMessage message,
                                                            String correo, int tamMsg,
                                                            String orden){
        try{
            if(YouChatApplication.actualizar_perfil){
                String valTemp = "0";
                String[] datoTemp = message.getHeader(ItemChat.KEY_ESTA_ENCRIPTADO);
                if (datoTemp != null)
                    valTemp = datoTemp[0];
                final boolean estaEncryptado = valTemp.equals("1");
                String[] versionContacto = message.getHeader(ItemChat.KEY_VERSION);
                if (versionContacto != null) {
                    int version = Convertidor.createIntOfString(versionContacto[0]);
                    int versionAct = dbWorker.obtenerVersionContacto(correo);
                    if (version != versionAct) {
                        actualizarBurbujaDatos(tamMsg);
                        actualizarEstadisticaDe(correo,"",CATEGORY_ACT_CONTACTO,tamMsg);
                        ItemContacto contactoActual = dbWorker.obtenerContacto(correo);
                        if (contactoActual == null)
                            contactoActual = new ItemContacto(correo, correo);
                        ItemContacto contactoActualizar =
                                Convertidor.createItemContactoOfMessage(message);
                        contactoActual.setVersion(contactoActualizar.getVersion());
                        boolean tieneAlias, tieneInfo, tieneImg;
                        datoTemp = message.getHeader(ItemChat.PERFIL_KEY_TIENE_ALIAS);
                        if (datoTemp != null)
                            tieneAlias = datoTemp[0].equals("1");
                        else tieneAlias = false;

                        datoTemp= message.getHeader(ItemChat.PERFIL_KEY_TIENE_INFO);
                        if (datoTemp != null)
                            tieneInfo = datoTemp[0].equals("1");
                        else tieneInfo = false;

                        datoTemp = message.getHeader(ItemChat.PERFIL_KEY_TIENE_IMG);
                        if (datoTemp != null)
                            tieneImg = datoTemp[0].equals("1");
                        else tieneImg = false;

                        Multipart multi;
                        try {
                            multi = (Multipart) message.getContent();

                            Part unaParte = multi.getBodyPart(0);

                            if (tieneAlias || tieneInfo) {
                                String text = unaParte.getContent().toString().trim();
                                if(estaEncryptado) text = Utils.decrypt(text,YouChatApplication.decod);
                                if (tieneAlias && !tieneInfo) {
                                    contactoActualizar.setAlias(text);
                                } else if (!tieneAlias) {
                                    contactoActualizar.setInfo(text);
                                } else {
                                    String[] datosUser = text.split("<-/s/->");
                                    contactoActualizar.setAlias(datosUser[0]);
                                    contactoActualizar.setInfo(datosUser[1]);
                                }
                            }
                            if (tieneImg) {
                                unaParte = multi.getBodyPart(1);
                                MimeBodyPart mbp = (MimeBodyPart) unaParte;
                                String nombreMulti = mbp.getFileName();
                                File directorioImagenes = new File(YouChatApplication.RUTA_IMAGENES_PERFIL);
                                boolean existe = directorioImagenes.exists();
                                if (!existe)
                                    existe = directorioImagenes.mkdirs();
                                if (existe) {
                                    if(!new File(directorioImagenes,nombreMulti).exists()){
                                        if(estaEncryptado){
                                            File dirDestino = new File(YouChatApplication.RUTA_MULTIMEDIA_CACHE);
                                            boolean existDestino = dirDestino.exists();
                                            if(!existDestino) existDestino = dirDestino.mkdirs();
                                            if(existDestino){
                                                File multiEncriptada = new File(dirDestino,nombreMulti);
                                                mbp.saveFile(multiEncriptada);
//                                                String nombre_img = correo
//                                                        .replace(".", "")
//                                                        .replace("@", "") + orden + ".jpg";
                                                String rutaImg = YouChatApplication.RUTA_IMAGENES_PERFIL + nombreMulti;

                                                String pass = Utils.MD5("YouChat");
                                                Utils.descomprimirArchivo(multiEncriptada,YouChatApplication.RUTA_IMAGENES_PERFIL,nombreMulti,nombreMulti,pass);

                                                contactoActualizar.setRuta_img(rutaImg);
                                                Utils.crearImgCache(rutaImg);
                                            }
                                        }
                                        else {
//                                            String nombre_img = correo
//                                                    .replace(".", "")
//                                                    .replace("@", "") + orden + ".jpg";
                                            String rutaImg = YouChatApplication.RUTA_IMAGENES_PERFIL + nombreMulti;

                                            mbp.saveFile(rutaImg);
                                            contactoActualizar.setRuta_img(rutaImg);
                                            Utils.crearImgCache(rutaImg);
                                        }
                                    } else{
                                        String rutaImg = YouChatApplication.RUTA_IMAGENES_PERFIL + nombreMulti;
                                        contactoActualizar.setRuta_img(rutaImg);
                                        Utils.crearImgCache(rutaImg);
                                    }
                                }
                            }
                        } catch (IOException e) {
                            Log.e("YOUCHAT SERVICE", "IOException " + e.toString());
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (!contactoActualizar.getAlias().trim().equals(""))
                            contactoActual.setAlias(contactoActualizar.getAlias());
                        if (!contactoActualizar.getInfo().trim().equals(""))
                            contactoActual.setInfo(contactoActualizar.getInfo());
                        if (!contactoActualizar.getTelefono().equals(""))
                            contactoActual.setTelefono(contactoActualizar.getTelefono());
                        if (!contactoActualizar.getGenero().equals(""))
                            contactoActual.setGenero(contactoActualizar.getGenero());
                        if (!contactoActualizar.getFecha_nac().equals(""))
                            contactoActual.setFecha_nac(contactoActualizar.getFecha_nac());
                        if (!contactoActualizar.getProvincia().equals(""))
                            contactoActual.setProvincia(contactoActualizar.getProvincia());
                        contactoActual.setCant_seguidores(contactoActualizar.getCant_seguidores());
                        if (!contactoActualizar.getRuta_img().equals("")) {
                            Utils.borrarFile(new File(contactoActual.getRuta_img()));
                            contactoActual.setRuta_img(contactoActualizar.getRuta_img());
                        }

                        dbWorker.actualizarContacto(contactoActual);
                        if (YouChatApplication.principalActivity != null) {
                            Utils.runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (YouChatApplication.principalActivity != null)
                                        YouChatApplication.principalActivity
                                            .actualizarUltMsg(correo);
                                }
                            });
                        }
                    }
                }
            }
            eliminarMensaje(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            eliminarMensaje(message);
        }

    }

    private synchronized void procesarCATEGORY_ACT_CONTACTO(IMAPMessage message, String correo){
        Utils.runOnUIThread(()->{
            new Thread(()->{
                try{
                    String horaReal = new SimpleDateFormat("yyyyMMddHHmmss").format(message.getSentDate());
                    final String hora = Convertidor.conversionHora(horaReal);
                    final String fecha = Convertidor.conversionFecha(horaReal);
                    int tamMsg = message.getSize();
                    procesarOtrosDatos(message, correo, hora, fecha);
                    if(YouChatApplication.actualizar_perfil){
                        Multipart multi;
                        try {
                            multi = (Multipart) message.getContent();
                            Part unaParte = multi.getBodyPart(0);
                            String jsonString =  Utils.decrypt(unaParte.getContent().toString().trim(),YouChatApplication.decod);
                            JSONObject jsonObject = new JSONObject(jsonString);
                            if(jsonObject!=null){
                                int version = jsonObject.getInt("version");
                                int versionAct = dbWorker.obtenerVersionContacto(correo);
                                if (version != versionAct) {
                                    ItemContacto contactoActual = dbWorker.obtenerContacto(correo);
                                    if (contactoActual == null)
                                        contactoActual = new ItemContacto(correo, correo);
                                    ItemContacto contactoActualizar = new ItemContacto(correo, correo);
                                    contactoActual.setVersion(version);
                                    contactoActual.setCant_seguidores(jsonObject.getInt("cantSeguidores"));

                                    contactoActualizar.setAlias(jsonObject.getString("alias"));
                                    contactoActualizar.setInfo(jsonObject.getString("info"));
                                    contactoActualizar.setTelefono(jsonObject.getString("telefono"));
                                    contactoActualizar.setGenero(jsonObject.getString("genero"));
                                    contactoActualizar.setFecha_nac(jsonObject.getString("fechaNac"));
                                    contactoActualizar.setProvincia(jsonObject.getString("provincia"));

                                    if (jsonObject.getBoolean("tieneFoto")){
                                        unaParte = multi.getBodyPart(1);
                                        MimeBodyPart mbp = (MimeBodyPart) unaParte;
                                        String nombreMulti = mbp.getFileName();
                                        String rutaImg = YouChatApplication.RUTA_IMAGENES_PERFIL + nombreMulti;
                                        File directorioImagenes = new File(YouChatApplication.RUTA_IMAGENES_PERFIL);
                                        boolean existe = directorioImagenes.exists();
                                        if (!existe)
                                            existe = directorioImagenes.mkdirs();
                                        if (existe) {
                                            if(!new File(rutaImg).exists()){
                                                File dirDestino = new File(YouChatApplication.RUTA_MULTIMEDIA_CACHE);
                                                boolean existDestino = dirDestino.exists();
                                                if(!existDestino) existDestino = dirDestino.mkdirs();
                                                if(existDestino){
                                                    File multiEncriptada = new File(dirDestino,nombreMulti);
                                                    mbp.saveFile(multiEncriptada);
                                                    String pass = Utils.MD5("YouChat");
                                                    Utils.descomprimirArchivo(multiEncriptada,YouChatApplication.RUTA_IMAGENES_PERFIL,nombreMulti,nombreMulti,pass);
                                                    contactoActualizar.setRuta_img(rutaImg);
                                                    Utils.crearImgCache(rutaImg);
                                                }
                                            }
                                            else{
                                                tamMsg -= unaParte.getSize();
                                                contactoActualizar.setRuta_img(rutaImg);
                                            }
                                        }
                                    }

                                    actualizarBurbujaDatos(tamMsg);
                                    actualizarEstadisticaDe(correo,"",CATEGORY_ACT_CONTACTO,tamMsg);

                                    if (!contactoActualizar.getAlias().trim().equals(""))
                                        contactoActual.setAlias(contactoActualizar.getAlias());
                                    if (!contactoActualizar.getInfo().trim().equals(""))
                                        contactoActual.setInfo(contactoActualizar.getInfo());
                                    if (!contactoActualizar.getTelefono().equals(""))
                                        contactoActual.setTelefono(contactoActualizar.getTelefono());
                                    if (!contactoActualizar.getGenero().equals(""))
                                        contactoActual.setGenero(contactoActualizar.getGenero());
                                    if (!contactoActualizar.getFecha_nac().equals(""))
                                        contactoActual.setFecha_nac(contactoActualizar.getFecha_nac());
                                    if (!contactoActualizar.getProvincia().equals(""))
                                        contactoActual.setProvincia(contactoActualizar.getProvincia());
                                    if (!contactoActualizar.getRuta_img().equals("")
                                            && !contactoActualizar.getRuta_img().equals(contactoActual.getRuta_img())) {
                                        Utils.borrarFile(new File(contactoActual.getRuta_img()));
                                        contactoActual.setRuta_img(contactoActualizar.getRuta_img());
                                    }

                                    dbWorker.actualizarContacto(contactoActual);
                                    if (YouChatApplication.principalActivity != null) {
                                        Utils.runOnUIThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (YouChatApplication.principalActivity != null)
                                                    YouChatApplication.principalActivity
                                                            .actualizarUltMsg(correo);
                                            }
                                        });
                                    }
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    eliminarMensaje(message);
                } catch (MessagingException e) {
                    e.printStackTrace();
                    eliminarMensaje(message);
                }
            }).start();
        });
    }

    private synchronized void procesarCATEGORY_SOL_SEGUIR(IMAPMessage message,
                                                          String correo, int tamMsg,
                                                          String hora, String fecha,
                                                          String orden){
        Utils.runOnUIThread(()->{
            new Thread(()->{
                try{
                    String tipo = "";
                    String[] datoTemp = message.getHeader(ItemChat.KEY_ID_MSG_RESP);
                    if (datoTemp != null) tipo = datoTemp[0];
                    if (tipo.equals("0")){
                        actualizarBurbujaDatos(tamMsg);
                        dbWorker.eliminarSeguidor(correo);
                    }
                    else if (tipo.equals("1") && !dbWorker.existeSeguidor(correo)) {
                        actualizarBurbujaDatos(tamMsg);
                        if (YouChatApplication.son_privados_estados) {
                            String idAN = "YouChat/chat/" + correo + "/83/" + orden;

                            ItemUsuario usuario = new ItemUsuario(correo);
                            ItemChat chatA = new ItemChat(correo, "");
                            chatA.setId(idAN);
                            chatA.setTipo_mensaje(83);
                            chatA.setHora(hora);
                            chatA.setFecha(fecha);
                            chatA.setOrden(orden);
                            chatA.setEstado(ItemChat.ESTADO_VISTO);

                            dbWorker.insertarChat(chatA);
                            dbWorker.insertarNuevoUsuario(usuario);
                            dbWorker.modificarUsoYCContacto(correo, 1);
                            dbWorker.actualizarCantMensajesNoVistosX(correo, 1);
                            dbWorker.actualizarUltMsgUsuario(chatA);

                            if (YouChatApplication.chatsActivity != null &&
                                    correo.equals(YouChatApplication.chatsActivity.getCorreo())) {
                                Utils.runOnUIThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (YouChatApplication.chatsActivity != null)
                                            YouChatApplication.chatsActivity.ActualizarChatMsgRecibido(chatA);
                                    }
                                });
                            }
                        } else dbWorker.insertarSeguidor(correo);
                    }
                    eliminarMensaje(message);
                } catch (MessagingException e) {
                    e.printStackTrace();
                    eliminarMensaje(message);
                }
            }).start();
        });
    }

    private synchronized void procesarCATEGORY_SOL_SEGUIR(IMAPMessage message,String correo){
        Utils.runOnUIThread(()->{
            new Thread(()->{
                try{
                    String horaReal = new SimpleDateFormat("yyyyMMddHHmmss").format(message.getSentDate());
                    final String hora = Convertidor.conversionHora(horaReal);
                    final String fecha = Convertidor.conversionFecha(horaReal);
                    int tamMsg = message.getSize();
                    String orden = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault()).format(new Date());
                    procesarOtrosDatos(message, correo, hora, fecha);

                    String tipo = "";
                    String[] datoTemp = message.getHeader(ItemChat.KEY_ID_MSG_RESP);
                    if (datoTemp != null) tipo = datoTemp[0];
                    if (tipo.equals("0")){
                        actualizarBurbujaDatos(tamMsg);
                        dbWorker.eliminarSeguidor(correo);
                    }
                    else if (tipo.equals("1") && !dbWorker.existeSeguidor(correo)) {
                        actualizarBurbujaDatos(tamMsg);
                        if (YouChatApplication.son_privados_estados) {
                            String idAN = "YouChat/chat/" + correo + "/83/" + orden;

                            ItemUsuario usuario = new ItemUsuario(correo);
                            ItemChat chatA = new ItemChat(correo, "");
                            chatA.setId(idAN);
                            chatA.setTipo_mensaje(83);
                            chatA.setHora(hora);
                            chatA.setFecha(fecha);
                            chatA.setOrden(orden);
                            chatA.setEstado(ItemChat.ESTADO_VISTO);

                            dbWorker.insertarChat(chatA);
                            dbWorker.insertarNuevoUsuario(usuario);
                            dbWorker.modificarUsoYCContacto(correo, 1);
                            dbWorker.actualizarCantMensajesNoVistosX(correo, 1);
                            dbWorker.actualizarUltMsgUsuario(chatA);

                            if (YouChatApplication.chatsActivity != null &&
                                    correo.equals(YouChatApplication.chatsActivity.getCorreo())) {
                                Utils.runOnUIThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (YouChatApplication.chatsActivity != null)
                                            YouChatApplication.chatsActivity.ActualizarChatMsgRecibido(chatA);
                                    }
                                });
                            }
                            Utils.runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (YouChatApplication.principalActivity != null)
                                        YouChatApplication.principalActivity
                                                .actualizarNewMsg(correo,1);
                                }
                            });
                        } else dbWorker.insertarSeguidor(correo);
                    }
                    eliminarMensaje(message);
                } catch (MessagingException e) {
                    e.printStackTrace();
                    eliminarMensaje(message);
                }
            }).start();
        });
    }

    private synchronized void procesarCATEGORY_ESTADO_PUBLICAR(IMAPMessage message,
                                                               String correo,
                                                               String hora, String fecha,
                                                               String orden, int tamMsg,
                                                               SimpleDateFormat sdf){
        dbWorker.insertarSiguiendoA(correo);
        String id = "";
        String[] ids = new String[0];
        try {
            ids = message.getHeader(ItemChat.KEY_ID);
        } catch (MessagingException e) {
            e.printStackTrace();
            ids = null;
        }
        finally {
            try{
                if(ids!=null && ids.length>0){
                    id=ids[0];
                }
                if(!id.isEmpty()){
                    if(!dbWorker.existeEstado(id)){
                        boolean puedeBorrar = true;
                        boolean obtenerEstado = true;
                        String horaHoy = Convertidor.conversionHora(orden);
                        int intHoraHoy = Convertidor.createIntOfStringHora(horaHoy);

                        Date datHoy = new Date(sdf.parse(sdf.format(new Date())).getTime());
                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                        Date datEstado = new Date(format.parse(fecha).getTime());

                        long dif = Math.abs(datHoy.getTime() - datEstado.getTime()) / 86400000;
                        if (dif >= 1) {
                            if (dif == 1) {
                                int intHoraEst = Convertidor.createIntOfStringHora(hora);
                                if (intHoraHoy > intHoraEst) obtenerEstado = false;
                            } else obtenerEstado = false;
                        }
                        if (obtenerEstado) {
                            ItemEstado estado = Convertidor.createItemEstadoOfMessage(message,id,correo,hora,fecha);
                            if (estado != null) {
                                String valTemp = "0";
                                String[] datoTemp = message.getHeader(ItemChat.KEY_ESTA_ENCRIPTADO);
                                if (datoTemp != null)
                                    valTemp = datoTemp[0];
                                final boolean estaEncryptado = valTemp.equals("1");
                                if (estado.getTipo_estado() != 99) {
                                    String text = message.getContent().toString().trim();
                                    if(estaEncryptado) text = Utils.decrypt(text,YouChatApplication.decod);
                                    estado.setTexto(text);
                                }
                                else {
                                    Multipart multi;
                                    multi = (Multipart) message.getContent();
                                    int cant = multi.getCount();
                                    for (int j = 0; j < cant; j++) {
                                        Part unaParte = multi.getBodyPart(j);
                                        if (unaParte.isMimeType("text/*")) {
                                            String text = unaParte.getContent().toString().trim();
                                            if(estaEncryptado) text = Utils.decrypt(text,YouChatApplication.decod);
                                            estado.setTexto(text);
                                        } else {
                                            int size = unaParte.getSize();
                                            if (!YouChatApplication.descargaAutImagenNow
                                                    || size / 1024 > YouChatApplication.tam_max_descarga_now) {
                                                tamMsg-=size;
                                                puedeBorrar = false;
                                                estado.setDescargado(false);
                                                estado.setPeso_img(size);
                                                estado.setUid(inbox.getUID(message));
                                                estado.setId_mensaje(message.getMessageID());
                                            }
                                            else{
                                                estado.setDescargado(true);
                                                if(estaEncryptado){
                                                    MimeBodyPart mbp = (MimeBodyPart) unaParte;
                                                    String nombreMulti = mbp.getFileName();
                                                    File dirDestino = new File(YouChatApplication.RUTA_MULTIMEDIA_CACHE);
                                                    boolean existDestino = dirDestino.exists();
                                                    if(!existDestino) existDestino = dirDestino.mkdirs();
                                                    if(existDestino){
                                                        File multiEncriptada = new File(dirDestino,nombreMulti);
                                                        mbp.saveFile(multiEncriptada);

                                                        String nombres_img = "est" + estado.getOrden() + ".jpg";
                                                        String ruta_Dato = YouChatApplication.RUTA_ESTADOS_GUARDADOS
                                                                + nombres_img;

                                                        File file = new File(YouChatApplication.RUTA_ESTADOS_GUARDADOS);
                                                        boolean estaCreada = file.exists();
                                                        if (!estaCreada)
                                                            estaCreada = file.mkdirs();

                                                        if (estaCreada) {
                                                            String pass = Utils.MD5("YouChat");
                                                            Utils.descomprimirArchivo(multiEncriptada,YouChatApplication.RUTA_ESTADOS_GUARDADOS,nombreMulti,nombres_img,pass);

                                                        }
                                                        estado.setRuta_imagen(ruta_Dato);
                                                    }
                                                }
                                                else {
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
                                    }
                                }
                                actualizarBurbujaDatos(tamMsg);
                                actualizarEstadisticaDe(correo,"",CATEGORY_ESTADO_PUBLICAR,tamMsg);

                                dbWorker.insertarNuevoEstado(estado);
                                ItemContacto contacto = new ItemContacto(correo, correo);
                                dbWorker.insertarNuevoContactoNoVisible(contacto, true);
                                if(YouChatApplication.principalActivity!=null){
                                    if (YouChatApplication.notificacion
                                            && !YouChatApplication.principalActivity.estaEnTabNow()){
                                        crearNotificationNowEntrante();
                                    }
                                    Utils.runOnUIThread(() -> {
                                        if (YouChatApplication.principalActivity != null)
                                            YouChatApplication.principalActivity.ActualizarEstados(true);
                                    });
                                }
                                else if (YouChatApplication.notificacion){
                                    crearNotificationNowEntrante();
                                }
                            }
                        }
                        if(puedeBorrar)
                            eliminarMensaje(message);
                    }
//                    else if(dbWorker.estaDescargadoEstado(id)) eliminarMensaje(message);
                }
            } catch (MessagingException e) {
                e.printStackTrace();
                eliminarMensaje(message);
            } catch (ParseException e) {
                e.printStackTrace();
                eliminarMensaje(message);
            } catch (IOException e) {
                e.printStackTrace();
                eliminarMensaje(message);
            } catch (Exception e) {
                e.printStackTrace();
                eliminarMensaje(message);
            }
        }
    }

    private synchronized void procesarCATEGORY_ESTADO_PUBLICAR(IMAPMessage message,
                                                               String correo){
        dbWorker.insertarSiguiendoA(correo);
        String id = "";
        String[] ids = new String[0];
        try {
            ids = message.getHeader(ItemChat.KEY_ID);
        } catch (MessagingException e) {
            e.printStackTrace();
            ids = null;
        }
        finally {
            try{
                if(ids!=null && ids.length>0){
                    id=ids[0];
                }
                if(!id.isEmpty()){
                    if(!dbWorker.existeEstado(id)){

                        String horaReal = new SimpleDateFormat("yyyyMMddHHmmss").format(message.getSentDate());
                        final String hora = Convertidor.conversionHora(horaReal);
                        final String fecha = Convertidor.conversionFecha(horaReal);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
                        String orden = sdf.format(new Date());

                        boolean puedeBorrar = true;
                        boolean obtenerEstado = true;
                        String horaHoy = Convertidor.conversionHora(orden);
                        int intHoraHoy = Convertidor.createIntOfStringHora(horaHoy);

                        Date datHoy = new Date(sdf.parse(sdf.format(new Date())).getTime());
                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                        Date datEstado = new Date(format.parse(fecha).getTime());

                        long dif = Math.abs(datHoy.getTime() - datEstado.getTime()) / 86400000;
                        if (dif >= 1) {
                            if (dif == 1) {
                                int intHoraEst = Convertidor.createIntOfStringHora(hora);
                                if (intHoraHoy > intHoraEst) obtenerEstado = false;
                            } else obtenerEstado = false;
                        }
                        if (obtenerEstado) {
                            int tamMsg = message.getSize();
                            procesarOtrosDatos(message, correo, hora, fecha);
                            String[] datosTemp = message.getHeader(SendMsg.PACK_DATO);
                            String[] pack;
                            int tipo_estado = 0, estilo_texto = 0;
                            if(datosTemp!=null) {
                                pack = datosTemp[0].split("<s&p>");
                                if(pack.length>=1)
                                    tipo_estado=Convertidor.createIntOfString(pack[0]);
                                if(pack.length>=2)
                                    estilo_texto=Convertidor.createIntOfString(pack[1]);
                            }
                            ItemEstado estado = new ItemEstado(id,correo,tipo_estado,false,"","",
                                    0,0,0,0,
                                    0,0,0,
                                    hora,fecha,orden,estilo_texto,true,0, "",0);
                            if (estado != null) {
                                String valTemp = "0";
                                String[] datoTemp = message.getHeader(ItemChat.KEY_ESTA_ENCRIPTADO);
                                if (datoTemp != null)
                                    valTemp = datoTemp[0];
                                final boolean estaEncryptado = valTemp.equals("1");
                                if (estado.getTipo_estado() != 99) {
                                    String text = message.getContent().toString().trim();
                                    if(estaEncryptado) text = Utils.decrypt(text,YouChatApplication.decod);
                                    estado.setTexto(text);
                                }
                                else {
                                    Multipart multi;
                                    multi = (Multipart) message.getContent();
                                    int cant = multi.getCount();
                                    for (int j = 0; j < cant; j++) {
                                        Part unaParte = multi.getBodyPart(j);
                                        if (unaParte.isMimeType("text/*")) {
                                            String text = unaParte.getContent().toString().trim();
                                            if(estaEncryptado) text = Utils.decrypt(text,YouChatApplication.decod);
                                            estado.setTexto(text);
                                        } else {
                                            int size = unaParte.getSize();
                                            if (!YouChatApplication.descargaAutImagenNow
                                                    || size / 1024 > YouChatApplication.tam_max_descarga_now) {
                                                tamMsg-=size;
                                                puedeBorrar = false;
                                                estado.setDescargado(false);
                                                estado.setPeso_img(size);
                                                estado.setUid(inbox.getUID(message));
                                                estado.setId_mensaje(message.getMessageID());
                                            }
                                            else{
                                                estado.setDescargado(true);
                                                if(estaEncryptado){
                                                    MimeBodyPart mbp = (MimeBodyPart) unaParte;
                                                    String nombreMulti = mbp.getFileName();
                                                    File dirDestino = new File(YouChatApplication.RUTA_MULTIMEDIA_CACHE);
                                                    boolean existDestino = dirDestino.exists();
                                                    if(!existDestino) existDestino = dirDestino.mkdirs();
                                                    if(existDestino){
                                                        File multiEncriptada = new File(dirDestino,nombreMulti);
                                                        mbp.saveFile(multiEncriptada);

                                                        String nombres_img = "est" + estado.getOrden() + ".jpg";
                                                        String ruta_Dato = YouChatApplication.RUTA_ESTADOS_GUARDADOS
                                                                + nombres_img;

                                                        File file = new File(YouChatApplication.RUTA_ESTADOS_GUARDADOS);
                                                        boolean estaCreada = file.exists();
                                                        if (!estaCreada)
                                                            estaCreada = file.mkdirs();

                                                        if (estaCreada) {
                                                            String pass = Utils.MD5("YouChat");
                                                            Utils.descomprimirArchivo(multiEncriptada,YouChatApplication.RUTA_ESTADOS_GUARDADOS,nombreMulti,nombres_img,pass);

                                                        }
                                                        estado.setRuta_imagen(ruta_Dato);
                                                    }
                                                }
                                                else {
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
                                    }
                                }
                                actualizarBurbujaDatos(tamMsg);
                                actualizarEstadisticaDe(correo,"",CATEGORY_ESTADO_PUBLICAR,tamMsg);

                                dbWorker.insertarNuevoEstado(estado);
                                ItemContacto contacto = new ItemContacto(correo, correo);
                                dbWorker.insertarNuevoContactoNoVisible(contacto, true);
                                if(YouChatApplication.principalActivity!=null){
                                    if (YouChatApplication.notificacion
                                            && !YouChatApplication.principalActivity.estaEnTabNow()){
                                        crearNotificationNowEntrante();
                                    }
                                    Utils.runOnUIThread(() -> {
                                        if (YouChatApplication.principalActivity != null)
                                            YouChatApplication.principalActivity.ActualizarEstados(true);
                                    });
                                }
                                else if (YouChatApplication.notificacion){
                                    crearNotificationNowEntrante();
                                }
                            }
                        }
                        if(puedeBorrar)
                            eliminarMensaje(message);
                    }
                }
            } catch (MessagingException e) {
                e.printStackTrace();
                eliminarMensaje(message);
            } catch (ParseException e) {
                e.printStackTrace();
                eliminarMensaje(message);
            } catch (IOException e) {
                e.printStackTrace();
                eliminarMensaje(message);
            } catch (Exception e) {
                e.printStackTrace();
                eliminarMensaje(message);
            }
        }
    }

    private synchronized void procesarCATEGORY_ESTADO_PUBLICAR(IMAPMessage message,
                                                               String correo, boolean a){
        try{
            boolean puedeBorrar = true;
            dbWorker.insertarSiguiendoA(correo);

            String horaReal = new SimpleDateFormat("yyyyMMddHHmmss").format(message.getSentDate());
            final String hora = Convertidor.conversionHora(horaReal);
            final String fecha = Convertidor.conversionFecha(horaReal);
            int tamMsg = message.getSize();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
            String orden = sdf.format(new Date());
            procesarOtrosDatos(message, correo, hora, fecha);

            String[] datosTemp = message.getHeader(SendMsg.PACK_DATO);
            String[] pack;
            if(datosTemp!=null) {
                pack = datosTemp[0].split("<s&p>");
                String idA = pack[0];
                if (false && tamMsg / 1024 > YouChatApplication.tam_max_descarga_now) {
                    boolean obtenerEstado = true;
                    String horaHoy = Convertidor.conversionHora(orden);
                    int intHoraHoy = Convertidor.createIntOfStringHora(horaHoy);

                    Date datHoy = new Date(sdf.parse(sdf.format(new Date())).getTime());
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                    Date datEstado = new Date(format.parse(fecha).getTime());

                    long dif = Math.abs(datHoy.getTime() - datEstado.getTime()) / 86400000;
                    if (dif >= 1) {
                        if (dif == 1) {
                            int intHoraEst = Convertidor.createIntOfStringHora(hora);
                            if (intHoraHoy > intHoraEst) obtenerEstado = false;
                        } else obtenerEstado = false;
                    }
                    if (obtenerEstado) {
                        puedeBorrar = false;
                    }
                }
                else {
                    boolean obtenerEstado = true;
                    String horaHoy = Convertidor.conversionHora(orden);
                    int intHoraHoy = Convertidor.createIntOfStringHora(horaHoy);

                    Date datHoy = new Date(sdf.parse(sdf.format(new Date())).getTime());
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                    Date datEstado = new Date(format.parse(fecha).getTime());

                    long dif = Math.abs(datHoy.getTime() - datEstado.getTime()) / 86400000;
                    if (dif >= 1) {
                        if (dif == 1) {
                            int intHoraEst = Convertidor.createIntOfStringHora(hora);
                            if (intHoraHoy > intHoraEst) obtenerEstado = false;
                        } else obtenerEstado = false;
                    }
                    if (obtenerEstado) {
                        ItemEstado estado = new ItemEstado(pack[0],correo,
                                Convertidor.createIntOfString(pack[1]),false,
                                "","",
                                0,0,0,
                                0,0,0,0,
                                hora,fecha,orden,Convertidor.createIntOfString(pack[2]),true,0,"",0);
                        final boolean estaEncryptado = pack[3].equals("1");
                        actualizarBurbujaDatos(tamMsg);
                        actualizarEstadisticaDe(correo,"",CATEGORY_ESTADO_PUBLICAR,tamMsg);
                        if (estado.getTipo_estado() != 99) {
                            String text = message.getContent().toString().trim();
                            if(estaEncryptado) text = Utils.decrypt(text,YouChatApplication.decod);
                            estado.setTexto(text);
                        }
                        else {
                            Multipart multi;
                            multi = (Multipart) message.getContent();
                            int cant = multi.getCount();
                            for (int j = 0; j < cant; j++) {
                                Part unaParte = multi.getBodyPart(j);

                                if (unaParte.isMimeType("text/*")) {
                                    String text = unaParte.getContent().toString().trim();
                                    if(estaEncryptado) text = Utils.decrypt(text,YouChatApplication.decod);
                                    estado.setTexto(text);
                                } else {
                                    if(estaEncryptado){
                                        unaParte = multi.getBodyPart(1);
                                        MimeBodyPart mbp = (MimeBodyPart) unaParte;
                                        String nombreMulti = mbp.getFileName();
                                        File dirDestino = new File(YouChatApplication.RUTA_MULTIMEDIA_CACHE);
                                        boolean existDestino = dirDestino.exists();
                                        if(!existDestino) existDestino = dirDestino.mkdirs();
                                        if(existDestino){
                                            File multiEncriptada = new File(dirDestino,nombreMulti);
                                            mbp.saveFile(multiEncriptada);

                                            String nombres_img = "est" + estado.getOrden() + ".jpg";
                                            String ruta_Dato = YouChatApplication.RUTA_ESTADOS_GUARDADOS
                                                    + nombres_img;

                                            File file = new File(YouChatApplication.RUTA_ESTADOS_GUARDADOS);
                                            boolean estaCreada = file.exists();
                                            if (!estaCreada)
                                                estaCreada = file.mkdirs();

                                            if (estaCreada) {
                                                String pass = Utils.MD5("YouChat");
                                                Utils.descomprimirArchivo(multiEncriptada,YouChatApplication.RUTA_ESTADOS_GUARDADOS,nombreMulti,nombres_img,pass);

                                            }
                                            estado.setRuta_imagen(ruta_Dato);
                                        }
                                    }
                                    else {
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
                        }
                        dbWorker.insertarNuevoEstado(estado);
                        ItemContacto contacto = new ItemContacto(correo, correo);
                        dbWorker.insertarNuevoContactoNoVisible(contacto, true);
                        if(YouChatApplication.principalActivity!=null){
                            if (YouChatApplication.notificacion
                                    && !YouChatApplication.principalActivity.estaEnTabNow()){
                                crearNotificationNowEntrante();
                            }
                            Utils.runOnUIThread(() -> {
                                if (YouChatApplication.principalActivity != null)
                                    YouChatApplication.principalActivity.ActualizarEstados(true);
                            });
                        }
                        else if (YouChatApplication.notificacion){
                            crearNotificationNowEntrante();
                        }
                    }
                }
            }
            if(puedeBorrar)
                eliminarMensaje(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            eliminarMensaje(message);
        } catch (ParseException e) {
            e.printStackTrace();
            eliminarMensaje(message);
        } catch (IOException e) {
            e.printStackTrace();
            eliminarMensaje(message);
        } catch (Exception e) {
            e.printStackTrace();
            eliminarMensaje(message);
        }

    }

    private synchronized void procesarCATEGORY_ESTADO_REACCIONAR(IMAPMessage message,
                                                                 String correo, String idA,
                                                                 String hora, String fecha,
                                                                 int tamMsg){
        Utils.runOnUIThread(()->{
            new Thread(()->{
                try{
                    String idEstado = "";
                    String[] datoTemp = message.getHeader(ItemChat.KEY_ID_MSG_RESP);
                    if (datoTemp != null) idEstado = datoTemp[0];
                    if (!idA.equals("") && !idEstado.equals("")) {
                        actualizarBurbujaDatos(tamMsg);
                        actualizarEstadisticaDe(correo,"",CATEGORY_ESTADO_REACCIONAR,tamMsg);
                        int tipoInt = Convertidor.createIntOfString(idA);
                        int cantReacDe = dbWorker.obtenerCantReacciones(idEstado, tipoInt);
                        if (cantReacDe != -1) {
                            dbWorker.sumarUnaReaccion(idEstado, idA, cantReacDe + 1);
                            dbWorker.insertarNuevaReaccionEstado
                                    (new ItemReaccionEstado(idEstado, correo, tipoInt, hora, fecha));
                            if (YouChatApplication.notificacion){
                                crearNotificationNowReaccion();
                            }
                        }
                    }
                    eliminarMensaje(message);
                } catch (MessagingException e) {
                    e.printStackTrace();
                    eliminarMensaje(message);
                }
            }).start();
        });
    }

    private synchronized void procesarCATEGORY_ESTADO_REACCIONAR(IMAPMessage message,String correo){
        Utils.runOnUIThread(()->{
            new Thread(()->{
                try{
                    String idEstado = "", idA = "";
                    String[] datoTemp = message.getHeader(ItemChat.KEY_ID);
                    if (datoTemp != null) idA = datoTemp[0];
                    datoTemp = message.getHeader(ItemChat.KEY_ID_MSG_RESP);
                    if (datoTemp != null) idEstado = datoTemp[0];

                    if (!idA.equals("") && !idEstado.equals("")) {

                        String horaReal = new SimpleDateFormat("yyyyMMddHHmmss").format(message.getSentDate());
                        final String hora = Convertidor.conversionHora(horaReal);
                        final String fecha = Convertidor.conversionFecha(horaReal);
                        int tamMsg = message.getSize();
                        procesarOtrosDatos(message, correo, hora, fecha);

                        actualizarBurbujaDatos(tamMsg);
                        actualizarEstadisticaDe(correo,"",CATEGORY_ESTADO_REACCIONAR,tamMsg);
                        int tipoInt = Convertidor.createIntOfString(idA);
                        int cantReacDe = dbWorker.obtenerCantReacciones(idEstado, tipoInt);
                        if (cantReacDe != -1) {
                            dbWorker.sumarUnaReaccion(idEstado, idA, cantReacDe + 1);
                            dbWorker.insertarNuevaReaccionEstado
                                    (new ItemReaccionEstado(idEstado, correo, tipoInt, hora, fecha));
                            if (YouChatApplication.notificacion){
                                crearNotificationNowReaccion();
                            }
                        }
                    }
                    eliminarMensaje(message);
                } catch (MessagingException e) {
                    e.printStackTrace();
                    eliminarMensaje(message);
                }
            }).start();
        });
    }

    private synchronized void procesarCATEGORY_ESTADO_VISTO(IMAPMessage message,
                                                            String correo,
                                                            String hora, String fecha,
                                                            int tamMsg){

        Utils.runOnUIThread(()->{
            new Thread(()->{
                try{
                    String idEstado2;
                    idEstado2 = "";
                    String[] datoTemp = message.getHeader(ItemChat.KEY_ID_MSG_RESP);
                    if (datoTemp != null) idEstado2 = datoTemp[0];
                    if (!idEstado2.equals("")) {
                        actualizarBurbujaDatos(tamMsg);
                        actualizarEstadisticaDe(correo,"",CATEGORY_ESTADO_VISTO,tamMsg);
                        ItemVistaEstado newVistaEst = new ItemVistaEstado(idEstado2, correo, hora, fecha);
                        dbWorker.insertarNuevaVistaEstado(newVistaEst);
                    }
                    eliminarMensaje(message);
                } catch (MessagingException e) {
                    e.printStackTrace();
                    eliminarMensaje(message);
                }
            }).start();
        });
    }

    private synchronized void procesarCATEGORY_ESTADO_VISTO(IMAPMessage message,String correo){

        Utils.runOnUIThread(()->{
            new Thread(()->{
                try{
                    String horaReal = new SimpleDateFormat("yyyyMMddHHmmss").format(message.getSentDate());
                    final String hora = Convertidor.conversionHora(horaReal);
                    final String fecha = Convertidor.conversionFecha(horaReal);
                    int tamMsg = message.getSize();
                    procesarOtrosDatos(message, correo, hora, fecha);

                    String idEstado2;
                    idEstado2 = "";
                    String[] datoTemp = message.getHeader(ItemChat.KEY_ID_MSG_RESP);
                    if (datoTemp != null) idEstado2 = datoTemp[0];
                    if (!idEstado2.equals("")) {
                        actualizarBurbujaDatos(tamMsg);
                        actualizarEstadisticaDe(correo,"",CATEGORY_ESTADO_VISTO,tamMsg);
                        ItemVistaEstado newVistaEst = new ItemVistaEstado(idEstado2, correo, hora, fecha);
                        dbWorker.insertarNuevaVistaEstado(newVistaEst);
                    }
                    eliminarMensaje(message);
                } catch (MessagingException e) {
                    e.printStackTrace();
                    eliminarMensaje(message);
                }
            }).start();
        });
    }

    private synchronized void procesarCATEGORY_CHAT_EDITAR(IMAPMessage message,
                                                           String correo, String idA,
                                                           int tamMsg){
        Utils.runOnUIThread(()->{
                new Thread(()->{
                    try{
                        if (!idA.equals("")) {
                            String valTemp = "0";
                            String[] datoTemp = message.getHeader(ItemChat.KEY_ESTA_ENCRIPTADO);
                            if (datoTemp != null)
                                valTemp = datoTemp[0];
                            final boolean estaEncryptado = valTemp.equals("1");

                            actualizarBurbujaDatos(tamMsg);
                            actualizarEstadisticaDe(correo,"",CATEGORY_CHAT_EDITAR,tamMsg);

                            String text = message.getContent().toString().trim();
                            if(estaEncryptado) text = Utils.decrypt(text,YouChatApplication.decod);
                            final String textEdit = text;

                            dbWorker.editarMensajeChat(idA,textEdit);

                            if (YouChatApplication.chatsActivity != null
                                    && YouChatApplication.chatsActivity.getCorreo().equals(correo)) {
                                Utils.runOnUIThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (YouChatApplication.chatsActivity != null)
                                            YouChatApplication.chatsActivity.actualizarMensajeEditado(idA, textEdit);
                                    }
                                });
                            }
                        }
                        eliminarMensaje(message);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                        eliminarMensaje(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                        eliminarMensaje(message);
                    }
                }).start();
        });
    }

    private synchronized void procesarCATEGORY_CHAT_EDITAR(IMAPMessage message,String correo){
        Utils.runOnUIThread(()->{
                new Thread(()->{
                    try{
                        String horaReal = new SimpleDateFormat("yyyyMMddHHmmss").format(message.getSentDate());
                        final String hora = Convertidor.conversionHora(horaReal);
                        final String fecha = Convertidor.conversionFecha(horaReal);
                        int tamMsg = message.getSize();
                        procesarOtrosDatos(message, correo, hora, fecha);

                        String valTemp = "";
                        String[] datoTemp = message.getHeader(ItemChat.KEY_ID);
                        if (datoTemp != null)
                            valTemp = datoTemp[0];
                        final String idA = valTemp;

                        if (!idA.isEmpty()) {
                            valTemp = "0";
                            datoTemp = message.getHeader(ItemChat.KEY_ESTA_ENCRIPTADO);
                            if (datoTemp != null)
                                valTemp = datoTemp[0];
                            final boolean estaEncryptado = valTemp.equals("1");

                            actualizarBurbujaDatos(tamMsg);
                            actualizarEstadisticaDe(correo,"",CATEGORY_CHAT_EDITAR,tamMsg);

                            String text = message.getContent().toString().trim();
                            if(estaEncryptado) text = Utils.decrypt(text,YouChatApplication.decod);
                            final String textEdit = text;

                            dbWorker.editarMensajeChat(idA,textEdit);

                            if (YouChatApplication.chatsActivity != null
                                    && YouChatApplication.chatsActivity.getCorreo().equals(correo)) {
                                Utils.runOnUIThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (YouChatApplication.chatsActivity != null)
                                            YouChatApplication.chatsActivity.actualizarMensajeEditado(idA, textEdit);
                                    }
                                });
                            }
                        }
                        eliminarMensaje(message);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                        eliminarMensaje(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                        eliminarMensaje(message);
                    }
                }).start();
        });
    }

    private synchronized void procesarOtrosDatos(IMAPMessage currentMessage, String correo) {

        Utils.runOnUIThread(()->{
            new Thread(()->{
                try{
                    int cant_seguidores = -1;
                    String[] datoTemp = currentMessage.getHeader(ItemChat.KEY_CANT_SEGUIDORES);
                    if (datoTemp != null)
                        cant_seguidores = Convertidor.createIntOfString(datoTemp[0]);
                    if (cant_seguidores >= 0) {
                        dbWorker.actualizarCantSeguidoresDe(correo, cant_seguidores);
                    }

                    if (YouChatApplication.actualizar_perfil) {
                        datoTemp = currentMessage.getHeader(ItemChat.KEY_VERSION);
                        if (datoTemp != null) {
                            int version = Convertidor.createIntOfString(datoTemp[0]);
                            int versionAct = dbWorker.obtenerVersionContacto(correo);
                            if (version != versionAct && version > 0) {
                                agregarAListaContactosSolicitud(correo);
                            }
                        }
                    }
                }catch (MessagingException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        });
    }

    private synchronized void procesarOtrosDatos(IMAPMessage currentMessage, String correo,
                                                 String hora, String fecha) {

        Utils.runOnUIThread(()->{
            new Thread(()->{
                try{
                    dbWorker.actualizarUltHoraFechaDe(correo, hora, fecha);
                    int cant_seguidores = -1;
                    String[] datoTemp = currentMessage.getHeader(ItemChat.KEY_CANT_SEGUIDORES);
                    if (datoTemp != null)
                        cant_seguidores = Convertidor.createIntOfString(datoTemp[0]);
                    if (cant_seguidores >= 0) {
                        dbWorker.actualizarCantSeguidoresDe(correo, cant_seguidores);
                    }

                    if (YouChatApplication.actualizar_perfil) {
                        datoTemp = currentMessage.getHeader(ItemChat.KEY_VERSION);
                        if (datoTemp != null) {
                            int version = Convertidor.createIntOfString(datoTemp[0]);
                            int versionAct = dbWorker.obtenerVersionContacto(correo);
                            if (version != versionAct && version > 0) {
                                agregarAListaContactosSolicitud(correo);
                            }
                        }
                    }
                }catch (MessagingException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        });
    }

    private synchronized void procesarMENSAJE_NORMAL(IMAPMessage currentMessage){
        try{
            if(currentMessage==null) return;
            if(YouChatApplication.bandejaFragment!=null){
                Utils.runOnUIThread(()->{
                    if(YouChatApplication.bandejaFragment!=null)
                        YouChatApplication.bandejaFragment.mostrarBarraProgress();
                });
            }
            //////////////////////////////////////////
//            Log.e(TAG, "procesarHeaders: ini----------------------------------------");
//            Enumeration a = currentMessage.getAllHeaderLines();
//            if(a!=null){
//                while (a.hasMoreElements()){
//                    Object o = a.nextElement();
//                    if(o!=null){
//                        if(o instanceof String){
//                            Log.e(TAG, "procesarHeaders: String "+(String) o);
//                        }
//                        else{
//                            Log.e(TAG, "procesarHeaders: no encontrado ningun tipo: "+ o.toString());
//                        }
//                    }
//                }
//            }
//            Log.e(TAG, "procesarHeaders: ----------------------------------------fin");
            //////////////////////////////////////////
            String id = currentMessage.getMessageID();
            String correo, nombre;
            if (currentMessage.getFrom() != null && currentMessage.getFrom().length>0){
                InternetAddress from = (InternetAddress) currentMessage.getFrom()[0];
                nombre = from.getPersonal();
                correo = from.getAddress();
                if(nombre==null || nombre.isEmpty())
                    nombre = correo;
            }
            else nombre = correo = "Correo no encontrado";
            String remitente = correo;

            Date fmsg = currentMessage.getSentDate();
            SimpleDateFormat fmsgc = new SimpleDateFormat("yyyyMMddHHmmss");
            String horaReal = fmsgc.format(fmsg);
            final String hora = Convertidor.conversionHora(horaReal);
            final String fecha = Convertidor.conversionFecha(horaReal);
            String orden = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault()).format(new Date());

            if(id==null || id.isEmpty()) id = correo+horaReal;
            YouChatApplication.setIdUltCorreoBuzonRecibido(id);
            if(!dbWorker.estaBloqueado(correo) && !dbWorker.existeMensajeCorreo(id)){
                boolean sePuedeObtener = true;

                String asunto = currentMessage.getSubject();

                if(!YouChatApplication.descargarMsjDimelo){
                    if(asunto!=null && !asunto.isEmpty())
                        sePuedeObtener = !(asunto.contains("<j&t>"));
                }
                if(!YouChatApplication.descargarMsjDeltaLab){
                        sePuedeObtener = currentMessage.getHeader("Chat-Version")==null;
                        if(!sePuedeObtener)
                            sePuedeObtener = currentMessage.getHeader("YouChat-Version")!=null;
                }
                if(sePuedeObtener){
                    currentMessage.setFlag(Flags.Flag.SEEN, true);
                    Long uid = inbox.getUID(currentMessage);
                    String destinatario = "";
                    boolean esGrupo = false;
                    Address[] destinatarios = currentMessage.getAllRecipients();
                    if (destinatarios != null && destinatarios.length > 0) {
                        int lDest = destinatarios.length;
                        if(lDest>1){
                            if(YouChatApplication.convertirCorreosMDenGrupos){
                                esGrupo = true;
                                correo = destinatario = ordenarDestinatarios(
                                        !correo.equals("Correo no encontrado")?correo:"", destinatarios);
                            }
                            else {
                                for (int j = 0; j < lDest; j++) {
                                    String dest = ((InternetAddress)destinatarios[j]).getAddress();
                                    if (!destinatario.isEmpty()) destinatario += ",";
                                    destinatario += dest;
                                }
                            }
                        }
                        else {
                            destinatario = ((InternetAddress)destinatarios[0]).getAddress();
                        }
                    }
                    else destinatario = YouChatApplication.correo;
                    int peso = currentMessage.getSize();
                    int tamReal = peso;

                    String nombreGroup = nombre;
                    String groupId = "";
                    if(esGrupo){
                        if(currentMessage.getHeader("Chat-Group-Name")!=null)
                            nombreGroup = currentMessage.getHeader("Chat-Group-Name")[0]
                                    .replace("=","")
                                    .replace("?","")
                                    .replace("utf-8q","");
                        else if(currentMessage.getHeader("Content-Description")!=null){
                            String stemp = currentMessage.getHeader("Content-Description")[0];
                            String[] stemp2 = stemp.split("<j&t>");
                            if(stemp2.length>=2)
                                nombreGroup = stemp2[1];
                        }

                        if(currentMessage.getHeader("Chat-Group-ID")!=null)
                            groupId = currentMessage.getHeader("Chat-Group-ID")[0];
                    }

                    ArrayList<ItemAdjuntoCorreo> adjuntos = new ArrayList<>();
                    String texto = "";
                    Object contenido = currentMessage.getContent();
                    if (contenido instanceof String) {
                        if (currentMessage.getContentType().contains("text/html")) {
                            File file = new File(YouChatApplication.RUTA_ADJUNTOS_CORREO);
                            boolean exist = file.exists();
                            if(!exist) exist = file.mkdirs();
                            if(exist){
                                String nomAdj = "adjunto"+horaReal+".html";
                                DataHandler dh = currentMessage.getDataHandler();
                                if(dh!=null){
                                    OutputStream os =
                                            new FileOutputStream(YouChatApplication.RUTA_ADJUNTOS_CORREO+nomAdj);
                                    dh.writeTo(os);
                                    os.close();
                                    File htmlFile = new File(YouChatApplication.RUTA_ADJUNTOS_CORREO+nomAdj);
                                    if(htmlFile.exists() && htmlFile.length()>0){
                                        ItemAdjuntoCorreo adj = new ItemAdjuntoCorreo(nomAdj + horaReal, id, correo, -1, nomAdj,
                                                8 ,(int)(htmlFile.length()));
                                        dbWorker.insertarNuevoAdjuntoCorreo(adj);
                                        adjuntos.add(adj);
                                    }
                                }
                            }
                        } else {
                            texto = (String) contenido;
                            if(texto!=null && !texto.isEmpty())
                                texto = texto.trim();
                        }
                    }
                    else if (contenido instanceof Multipart) {
                        Multipart mp = (Multipart) contenido;
                        int numPart = mp.getCount();
                        for (int j = 0; j < numPart; j++) {
                            Part part = mp.getBodyPart(j);
                            String disposition = part.getDisposition();
                            if (disposition == null) {
                                if (part.isMimeType("multipart/alternative")
                                        || part.isMimeType("text/plain")) {
                                    if (part.isMimeType("multipart/alternative")) {
                                        Multipart mp2 = (Multipart) part.getContent();
                                        Part part2 = mp2.getBodyPart(0);
                                        texto = (String) part2.getContent();
                                        if(texto!=null && !texto.isEmpty())
                                            texto = texto.trim();
                                    } else{
                                        texto = (String) part.getContent();
                                        if(texto!=null && !texto.isEmpty())
                                            texto = texto.trim();
                                    }
                                } else {
                                    if (part.isMimeType("text/html")) {
                                        File file = new File(YouChatApplication.RUTA_ADJUNTOS_CORREO);
                                        boolean exist = file.exists();
                                        if(!exist) exist = file.mkdirs();
                                        if(exist){
                                            String nomAdj = "adjunto"+horaReal+".html";
                                            MimeBodyPart mbp = (MimeBodyPart)part;
                                            if(mbp!=null) {
                                                mbp.saveFile(YouChatApplication.RUTA_ADJUNTOS_CORREO + nomAdj);
                                                File htmlFile = new File(YouChatApplication.RUTA_ADJUNTOS_CORREO+nomAdj);
                                                if(htmlFile.exists() && htmlFile.length()>0){
                                                    ItemAdjuntoCorreo adj = new ItemAdjuntoCorreo(nomAdj + horaReal, id, correo, -1, nomAdj,
                                                            8 ,(int)(htmlFile.length()));
                                                    dbWorker.insertarNuevoAdjuntoCorreo(adj);
                                                    adjuntos.add(adj);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            else if (disposition.equalsIgnoreCase(Part.ATTACHMENT) ||
                                    disposition.equalsIgnoreCase(Part.INLINE)) {
                                int partSize = part.getSize();
                                String nombrePart = part.getFileName();
                                if (nombrePart == null || nombrePart.isEmpty())
                                    nombrePart = "adjunto" + horaReal +".sinExt";
                                ItemAdjuntoCorreo adj = new ItemAdjuntoCorreo(nombrePart + horaReal, id, correo, j,
                                        nombrePart,
                                        Utils.obtenerTipoDadounaExtension(Utils.obtenerExtension(nombrePart)),
                                        partSize);
                                dbWorker.insertarNuevoAdjuntoCorreo(adj);
                                adjuntos.add(adj);
                                if(YouChatApplication.descargaAutMensajesCorreo
                                        && partSize/1024<YouChatApplication.tam_max_descarga_correo){
                                    MimeBodyPart mbp = (MimeBodyPart) part;
                                    mbp.saveFile(YouChatApplication.RUTA_ADJUNTOS_CORREO+nombrePart);
                                } else tamReal -= partSize;
                            }
                        }

                    }
                    dbWorker.insertarUsuarioCorreo(
                            new ItemUsuarioCorreo(correo,nombreGroup,groupId,hora,fecha,orden), false);

                    ItemMensajeCorreo newCorreo = new ItemMensajeCorreo(id, uid, false, true,
                            false, correo, remitente, nombre, destinatario, asunto, texto,
                            ItemChat.ESTADO_ENVIADO, false, peso, hora, fecha, orden);
                    dbWorker.insertarNuevoMensajeCorreo(newCorreo);

                    actualizarBurbujaDatos(tamReal);
                    YouChatApplication.addCant_buzon_rye(1);
                    YouChatApplication.addMega_buzon_recibidos(tamReal);

                    if(YouChatApplication.bandejaFragment!=null){
                        Utils.runOnUIThread(()->{
                            if (YouChatApplication.bandejaFragment != null)
                                YouChatApplication.bandejaFragment.addNewCorreo(newCorreo, false);
                        });
                    }
                    if(YouChatApplication.chatsActivityCorreo!=null){
                        if(YouChatApplication.chatsActivityCorreo.getCorreo().equals(correo)){
                            Utils.runOnUIThread(()->{
                                if (YouChatApplication.chatsActivityCorreo != null)
                                YouChatApplication.chatsActivityCorreo.convertirActualizarChatMsgRecibido(newCorreo, adjuntos);
                            });
                            if(YouChatApplication.chatsActivityCorreo.estaMinimizada()){
                                if (YouChatApplication.notificacion
                                        && !dbWorker.estaSilenciado(correo)){
                                    crearNotificationCorreo();
                                }
                            }
                        }
                        else {
                            if (YouChatApplication.notificacion
                                    && !dbWorker.estaSilenciado(correo)){
                                crearNotificationCorreo();
                            }
                        }
                    }
                    else if (YouChatApplication.notificacion
                            && !dbWorker.estaSilenciado(correo)){
                        crearNotificationCorreo();
                    }

                    Utils.runOnUIThread(()->{
                        if (YouChatApplication.principalActivity != null)
                            YouChatApplication.principalActivity.actualizarBadgeCantMensajesNuevos();
                    });
                }
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void eliminarMensaje(@NonNull IMAPMessage message){
        if(inbox==null) return;
        try {
            message.setFlag(Flags.Flag.DELETED, true);
            inbox.expunge(new Message[]{message});
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private String ordenarDestinatarios(String correo, Address[] destinatarios) {
        int lDest = destinatarios.length, fin = destinatarios.length;
        if(!correo.isEmpty()) lDest++;
        String[] destinatariosString = new String[lDest];
        int pos = 0;
        if(!correo.isEmpty()) destinatariosString[pos++] = correo;
        for (int i = 0; i < fin; i++) {
            destinatariosString[pos++] = ((InternetAddress)destinatarios[i]).getAddress();
        }
        Arrays.sort(destinatariosString);
        String destinatario = "";
        for(int i=0; i<pos; i++){
            if (!destinatario.isEmpty()) destinatario += ",";
            destinatario += destinatariosString[i];
        }
        return destinatario;
    }

}

/*
3 metodos para tratar con los adjuntos:

FileOutputStream fichero = new FileOutputStream(ruta_Dato);
InputStream imagen = unaParte.getInputStream();
byte[] bytes = new byte[1000];
int leidos = 0;
while ((leidos = imagen.read(bytes)) > 0) {
fichero.write(bytes, 0, leidos);
}

DataHandler dh = unaParte.getDataHandler();
OutputStream os = new FileOutputStream (ruta_Dato);
dh.writeTo(os);
os.close();

MimeBodyPart mbp = (MimeBodyPart)unaParte;
mbp.saveFile(ruta_Dato);
 */

/*
dimelo
08-24 22:53:57.671 18796-20389/cu.alexgi.youchat E/ChatService: procesarHeaders: ini----------------------------------------
08-24 22:53:57.791 18796-20389/cu.alexgi.youchat E/ChatService: procesarHeaders: String Return-Path: <liannega@nauta.cu>
08-24 22:53:57.791 18796-20389/cu.alexgi.youchat E/ChatService: procesarHeaders: String Delivered-To: <niuvis2019@nauta.cu>
08-24 22:53:57.791 18796-20389/cu.alexgi.youchat E/ChatService: procesarHeaders: String Subject: 0<j&t>1<j&t>185<j&t>y<j&t>-1<j&t>false<j&t>null<j&t>0<j&t>false<j&t>0<j&t>1629860033628
08-24 22:53:57.791 18796-20389/cu.alexgi.youchat E/ChatService: procesarHeaders: String Content-Type: text/plain; charset=us-ascii
08-24 22:53:57.791 18796-20389/cu.alexgi.youchat E/ChatService: procesarHeaders: String Content-Transfer-Encoding: 7bit
08-24 22:53:57.791 18796-20389/cu.alexgi.youchat E/ChatService: procesarHeaders: ----------------------------------------fin

dimelo grupo
08-24 22:54:52.651 18796-20389/cu.alexgi.youchat E/ChatService: procesarHeaders: ini----------------------------------------
08-24 22:54:52.771 18796-20389/cu.alexgi.youchat E/ChatService: procesarHeaders: String Return-Path: <liannega@nauta.cu>
08-24 22:54:52.771 18796-20389/cu.alexgi.youchat E/ChatService: procesarHeaders: String Delivered-To: <niuvis2019@nauta.cu>
08-24 22:54:52.771 18796-20389/cu.alexgi.youchat E/ChatService: procesarHeaders: String Subject: 1<j&t>1<j&t>186<j&t>y<j&t>liannegaATnautaPOINTcu_1629847889918<j&t>false<j&t>Gggg<j&t>0<j&t>true<j&t>0<j&t>1629860087198
08-24 22:54:52.771 18796-20389/cu.alexgi.youchat E/ChatService: procesarHeaders: String Content-Type: text/plain; charset=us-ascii
08-24 22:54:52.771 18796-20389/cu.alexgi.youchat E/ChatService: procesarHeaders: String Content-Transfer-Encoding: 7bit
08-24 22:54:52.771 18796-20389/cu.alexgi.youchat E/ChatService: procesarHeaders: String Content-Description: 0<j&t>Gggg
08-24 22:54:52.771 18796-20389/cu.alexgi.youchat E/ChatService: procesarHeaders: ----------------------------------------fin



deltalab
08-24 22:56:27.621 18796-20389/cu.alexgi.youchat E/ChatService: procesarHeaders: ini----------------------------------------
08-24 22:56:27.751 18796-20389/cu.alexgi.youchat E/ChatService: procesarHeaders: String Return-Path: <octaviog97@nauta.cu>
08-24 22:56:27.751 18796-20389/cu.alexgi.youchat E/ChatService: procesarHeaders: String Delivered-To: <niuvis2019@nauta.cu>
08-24 22:56:27.751 18796-20389/cu.alexgi.youchat E/ChatService: procesarHeaders: String Chat-Disposition-Notification-To: octaviog97@nauta.cu
08-24 22:56:27.751 18796-20389/cu.alexgi.youchat E/ChatService: procesarHeaders: String In-Reply-To: <253731624.43.1629848350449.JavaMail.root@localhost>
08-24 22:56:27.751 18796-20389/cu.alexgi.youchat E/ChatService: procesarHeaders: String Chat-Version: 1.0
08-24 22:56:27.751 18796-20389/cu.alexgi.youchat E/ChatService: procesarHeaders: String Message-ID: <Mr.uvvUpP39pSw.J3fu72mrScj@nauta.cu>
08-24 22:56:27.751 18796-20389/cu.alexgi.youchat E/ChatService: procesarHeaders: ----------------------------------------fin

deltalab grupo
08-24 22:56:07.601 18796-20389/cu.alexgi.youchat E/ChatService: procesarHeaders: ini----------------------------------------
08-24 22:56:07.731 18796-20389/cu.alexgi.youchat E/ChatService: procesarHeaders: String Return-Path: <octaviog97@nauta.cu>
08-24 22:56:07.731 18796-20389/cu.alexgi.youchat E/ChatService: procesarHeaders: String Delivered-To: <niuvis2019@nauta.cu>
08-24 22:56:07.731 18796-20389/cu.alexgi.youchat E/ChatService: procesarHeaders: String Chat-Disposition-Notification-To: octaviog97@nauta.cu
08-24 22:56:07.731 18796-20389/cu.alexgi.youchat E/ChatService: procesarHeaders: String Chat-Group-ID: c8AIdqH7eL7
08-24 22:56:07.731 18796-20389/cu.alexgi.youchat E/ChatService: procesarHeaders: String Chat-Group-Name: =?utf-8?q?yyy?=
08-24 22:56:07.731 18796-20389/cu.alexgi.youchat E/ChatService: procesarHeaders: String In-Reply-To: <Gr.c8AIdqH7eL7.yHqRnFi_6s7@nauta.cu>
08-24 22:56:07.731 18796-20389/cu.alexgi.youchat E/ChatService: procesarHeaders: String Chat-Version: 1.0
08-24 22:56:07.731 18796-20389/cu.alexgi.youchat E/ChatService: procesarHeaders: String Message-ID: <Gr.c8AIdqH7eL7.Z8DKm12nxzq@nauta.cu>
08-24 22:56:07.731 18796-20389/cu.alexgi.youchat E/ChatService: procesarHeaders: ----------------------------------------fin
*/

//    public void sendNotification(View view) {
//
//        String replyLabel = "Enter your reply here";
//        RemoteInput remoteInput =
//                new RemoteInput.Builder(KEY_TEXT_REPLY)
//                        .setLabel(replyLabel)
//                        .build();
//
//        Intent resultIntent = new Intent(this, DirectReplyActivity.class);
//
//        PendingIntent resultPendingIntent =
//                PendingIntent.getActivity(
//                        this,
//                        0,
//                        resultIntent,
//                        PendingIntent.FLAG_UPDATE_CURRENT
//                );
//
//        final Icon icon =
//                Icon.createWithResource(DirectReplyActivity.this,
//                        android.R.drawable.ic_dialog_info);
//
//        Notification.Action replyAction =
//                new Notification.Action.Builder(
//                        icon,
//                        "Reply", resultPendingIntent)
//                        .addRemoteInput(remoteInput)
//                        .build();
//
//        Notification newMessageNotification =
//                new Notification.Builder(this, channelID)
//                        .setColor(ContextCompat.getColor(this,
//                                R.color.colorPrimary))
//                        .setSmallIcon(
//                                android.R.drawable.ic_dialog_info)
//                        .setContentTitle("My Notification")
//                        .setContentText("This is a test message")
//                        .addAction(replyAction).build();
//
//        NotificationManager notificationManager =
//                (NotificationManager)
//                        getSystemService(Context.NOTIFICATION_SERVICE);
//
//        notificationManager.notify(notificationId,
//                newMessageNotification);
//    }﻿