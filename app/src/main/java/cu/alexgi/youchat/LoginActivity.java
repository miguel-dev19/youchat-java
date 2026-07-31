package cu.alexgi.youchat;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.TrafficStats;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.sun.mail.smtp.SMTPTransport;

import java.util.ArrayList;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import cu.alexgi.youchat.base_datos.DBWorker;
import cu.alexgi.youchat.chatUtils.ViewUtil;
import cu.alexgi.youchat.items.ItemChat;
import cu.alexgi.youchat.items.ItemContacto;

public class LoginActivity extends AppCompatActivity {

    //ProgressBar progressBar_carga_login;
    //TextView textView_carga_login;

    EditText campo_email,campo_pass;
    ExtendedFloatingActionButton go_EditPerfil;

    Boolean show=false;

    Context context;
    LoginActivity activity;
    int cont, tiempo;
    IntentFilter filter;
    ResponseReceiver receiver;
    String id;
    int fin, num;
    DBWorker dbWorker;
    Permisos permisos;
    ArrayList<ItemContacto> contactos;
    boolean yaCargoContactos, yaVerificoCorreo;
    private Dialog mProgressDialog;
    private String aut_user,aut_pass;

    //ImageView login_img_importarContactos;
    //View importar,rayarriba,rayabajo;
    //private TextView login_tv_importarContactos;
    private int importarContactos;

//    MaterialCardView card;
//    View icon;
    LottieAnimationView animationLoad;

    TextInputLayout pass;


    private void setStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(R.style.AppTheme);

//        getWindow().setBackgroundDrawableResource(R.drawable.background_login_blur);
        setContentView(R.layout.activity_login);

        context=this;
        activity=this;
        permisos=new Permisos(this, this);
        permisos.requestAllPermission();

        setStrictMode();

        pass = findViewById(R.id.pass);

//        pass.addOnEndIconChangedListener(new TextInputLayout.OnEndIconChangedListener() {
//            @Override
//            public void onEndIconChanged(@NonNull TextInputLayout textInputLayout, int previousIcon) {
//                if(previousIcon==R.drawable.show) pass.setEndIconDrawable(R.drawable.hide);
//                else pass.setEndIconDrawable(R.drawable.show);
//            }
//        });
//        card=findViewById(R.id.card);
//        icon=findViewById(R.id.icon);

//        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
//            card.setCardElevation(6);
//            icon.setElevation(8);
//        }

        contactos=new ArrayList<>();
        yaCargoContactos=yaVerificoCorreo=false;
        campo_email=findViewById(R.id.campo_email);
        campo_pass=findViewById(R.id.campo_pass);

        animationLoad = findViewById(R.id.animationLoad);
        /*login_tv_importarContactos=findViewById(R.id.login_tv_importarContactos);
        login_img_importarContactos=findViewById(R.id.login_img_importarContactos);
        rayabajo=findViewById(R.id.rayabajo);
        rayarriba=findViewById(R.id.rayarriba);
        importar=findViewById(R.id.importar);*/
        campo_pass.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
        num=0;

        /*login_img_importarContactos.setBackgroundResource(R.drawable.shape_icono_saldo);
        login_img_importarContactos.setImageResource(R.drawable.imp_no_tel);
        login_tv_importarContactos.setText("Solo importar contactos con correo.");*/
        importarContactos=2;

        campo_email.setText(YouChatApplication.correo);
        //if(!campo_email.getText().toString().equals("")) importar.setVisibility(View.GONE);

        aut_user=YouChatApplication.correo;
        aut_pass="";

        dbWorker=new DBWorker(this);
        //progressBar_carga_login = findViewById(R.id.progressBar_carga_login);
        //textView_carga_login = findViewById(R.id.textView_carga_login);

        go_EditPerfil=findViewById(R.id.login);

        // TODO Filtro de acciones que serán alertadas
        filter = new IntentFilter("LOGIN_CARGA");
//        filter.addAction("");
        receiver = new ResponseReceiver();
        LocalBroadcastManager.getInstance(LoginActivity.this).registerReceiver(receiver, filter);


        go_EditPerfil.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                aut_user = campo_email.getText().toString().trim();
                aut_pass = campo_pass.getText().toString().trim();

                if(aut_user.length()==0) Utils.ShowToastAnimated(activity,"El correo no puede estar vacío",R.raw.error);
                else if(!aut_user.endsWith("@nauta.cu") && !aut_user.endsWith("@gmail.com") && !aut_user.endsWith("@mail.com") && !aut_user.endsWith("@mail.ru") && !aut_user.endsWith("@yahoo.com") && !aut_user.endsWith("@hotmail.com") && !aut_user.endsWith("@enpa.gtm.minag.cu") && !aut_user.endsWith("@gid.enpa.minag.cu"))
                    Utils.ShowToastAnimated(activity,"Debe ser un correo Gmail, Nauta, Yahoo, Mail.com, Mail.ru, Hotmail, o GID.enpa",R.raw.chats_infotip);
                else if(aut_pass.length()==0) Utils.ShowToastAnimated(activity,"La contraseña no puede estar vacía",R.raw.error);

                else{
                    //progressBar_carga_login.setVisibility(View.VISIBLE);
                    //progressBar_carga_login.setIndeterminate(true);
                    //textView_carga_login.setVisibility(View.VISIBLE);
                    //card.setVisibility(View.GONE);
                    //icon.setVisibility(View.GONE);

                    ViewUtil.fadeOut(findViewById(R.id.card),50);
                    ViewUtil.fadeOut(findViewById(R.id.icon),50);

                    ViewUtil.fadeIn(findViewById(R.id.animationLoad),100);

                    animationLoad.playAnimation();

                    go_EditPerfil.hide();
                    campo_pass.setEnabled(false);
                    campo_email.setEnabled(false);

                    /*importar.setVisibility(View.GONE);
                    rayabajo.setVisibility(View.GONE);
                    rayarriba.setVisibility(View.GONE);*/

                    ComprobarCorreo();
                }
            }
        });

    }

    private void ObtenerTodosContactosTelefono(){
        if(permisos.requestPermissionContactos()){
            if(!yaCargoContactos){
                ObtenerContactosAsyncTask task = new ObtenerContactosAsyncTask();
                task.execute();
            }
            else {
                fin = contactos.size();
                //progressBar_carga_login.setMax(fin*100+200);
                cont=1;
                if(fin>=10) tiempo=100;
                else tiempo=1000-(fin*100);
                Proceso();
            }
        }
        else ErrorEnElLogin("No tiene permiso para acceder a los contactos");
    }

    protected void showLoading(@NonNull String message) {

        mProgressDialog= new Dialog(this);
        mProgressDialog.requestWindowFeature(1);
        View mview=getLayoutInflater().inflate(R.layout.dialog_alert_progress,null);
        mProgressDialog.setContentView(mview);

        TextView texto_alerta=mview.findViewById(R.id.texto_alerta);
        texto_alerta.setText(message);

        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    protected void hideLoading() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    private class ObtenerContactosAsyncTask extends AsyncTask<String, String, String> {

        public ObtenerContactosAsyncTask() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*if(importarContactos==1 || importarContactos==2)
                showLoading("Importando contactos. Por favor, espere.");
            else
                showLoading("Verificando elección. Por favor, espere.");*/
        }

        @Override
        protected String doInBackground(String... params) {
            if(importarContactos==1)
                obtenerContactosConTelefono();
            else if(importarContactos==2)
                obtenerContactos();
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            //hideLoading();
            yaCargoContactos=true;
            fin = contactos.size();
            //progressBar_carga_login.setMax(fin*100+200);
            cont=1;
            if(fin>=10) tiempo=100;
            else tiempo=1000-(fin*100);
            Proceso();
        }
    }

    private void ComprobarCorreo()
    {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(campo_email.getWindowToken(), 0);
        inputMethodManager.hideSoftInputFromWindow(campo_pass.getWindowToken(), 0);

        //progressBar_carga_login.setIndeterminate(false);
        //progressBar_carga_login.setProgress(0);
        if(yaVerificoCorreo){
            /*if(importarContactos==1 || importarContactos==2)
                textView_carga_login.setText("Importando contactos...");
            else
                textView_carga_login.setText("Verificando elección...");*/

            ObtenerTodosContactosTelefono();
        }
        else {
            id="YouChat/login/"+aut_user+""+num;
            num++;

            ItemChat newChat=new ItemChat( id,
                    2, 1, aut_user, "",  "",
                    "", "", "", "", false, "",false,"",0,true);
            SendAsyncTask sendAsyncTask = new SendAsyncTask();
            sendAsyncTask.cargarMsg(newChat);
            sendAsyncTask.execute();
        }
    }

    private class SendAsyncTask extends AsyncTask<String, String, String> {

        private ItemChat msg;
        private boolean envioCorrecto;
        private Properties props;
        private Session session;


        public SendAsyncTask() {
            msg = null;
        }

        public void cargarMsg(ItemChat m){
            msg = m;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            props = new Properties();
            if(aut_user.endsWith("@nauta.cu")){
                props.put("mail.smtp.host", "smtp.nauta.cu");
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.port", "25");
            }
            else if(aut_user.endsWith("@gmail.com")){
                props.put("mail.smtp.host", "smtp.gmail.com");
//                props.put("mail.smtp.socketFactory.port", "465");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.port", "465");

//                props.put("mail.smtp.host", "smtp.gmail.com");
//                props.put("mail.smtp.port", "587");
////                props.put("mail.smtp.auth", "true");
//                props.put("mail.smtp.starttls.enable", "true");
                Log.e("GMAIL",""+props.getProperty("mail.smtp.port")+" *** "+props.getProperty("mail.smtp.host"));
            }
            else if(aut_user.endsWith("@mail.com")){
                props.put("mail.smtp.host", "smtp.mail.com");
                props.put("mail.smtp.socketFactory.port", "587");
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.port", "587");
            }
            else if(aut_user.endsWith("@mail.ru")){
                props.put("mail.smtp.host", "smtp.mail.ru");
                props.put("mail.smtp.port", "465");
            }
            else if(aut_user.endsWith("@yahoo.com")){
                props.put("mail.smtp.host", "smtp.mail.yahoo.com");
//                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.port", "25");
            }
            else if(aut_user.endsWith("@hotmail.com")){
                props.put("mail.smtp.host", "smtp.live.com");
                props.put("mail.smtp.port", "465");
            }
            else if(aut_user.endsWith("@enpa.gtm.minag.cu")) {
                props.put("mail.smtp.host", "smtp.gid.enpa.minag.cu");
//                props.put("mail.smtp.host", "server.enpa.gtm.minag.cu");
//                props.put("mail.smtp.host", "172.16.201.5");
                props.put("mail.smtp.auth", "false");
//                props.put("mail.smtp.starttls.enable", "true");
//                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                props.put("mail.smtp.port", "25");
            }
            else if(aut_user.endsWith("@gid.enpa.minag.cu")) {
                props.put("mail.smtp.host", "smtp.gid.enpa.minag.cu");
                props.put("mail.smtp.auth", "false");
                props.put("mail.smtp.port", "25");
            }

            session = Session.getDefaultInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(aut_user,aut_pass);
                }
            });

            envioCorrecto = true;
        }

        @Override
        protected String doInBackground(String... params) {
            if(msg!=null){
                try{
                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(aut_user));
                    message.addRecipient(Message.RecipientType.TO, new InternetAddress(msg.getCorreo()));
                    message.setSubject("");

                    message.setText(msg.getMensaje());

                    message.addHeader(ItemChat.YOUCHAT,"youchat");
                    message.addHeader(ItemChat.KEY_CAT,SendMsg.CATEGORY_DESPRECIABLE);
                    message.addHeader(ItemChat.KEY_LECTURA,"0");
                    message.addHeader(ItemChat.KEY_ID,msg.getId());
                    message.addHeader(ItemChat.KEY_TIPO,"2");
                    message.addHeader(ItemChat.KEY_CORREO,msg.getCorreo());
                    message.addHeader(ItemChat.KEY_HORA,"");
                    message.addHeader(ItemChat.KEY_FECHA,"");
                    message.addHeader(ItemChat.KEY_ORDEN,"");
                    message.addHeader(ItemChat.KEY_ID_MSG_RESP,"");
                    message.addHeader(ItemChat.KEY_REENVIADO,""+msg.getReenviado());

                    TrafficStats.setThreadStatsTag(1);

                    SMTPTransport transport =
                            new SMTPTransport(session,
                                    new URLName("smtp",
                                            props.getProperty("mail.smtp.host"),
                                            Integer.parseInt(props.getProperty("mail.smtp.port")),
                                            null, aut_user, aut_pass));
                    if (!transport.isConnected()) transport.connect();
                    if(transport.isConnected()){
                        transport.sendMessage(message, message.getAllRecipients());
                        envioCorrecto=true;
                    } else envioCorrecto=false;
                } catch(MessagingException e) {
                    Log.e("GMAIL",""+e.toString());
                    envioCorrecto=false;
                    e.printStackTrace();
                } catch(Exception e) {
                    Log.e("GMAIL",""+e.toString());
                    envioCorrecto=false;
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result){
            if(msg!=null){
                if(envioCorrecto){
                    yaVerificoCorreo=true;
                    /*if(importarContactos==1 || importarContactos==2)
                        textView_carga_login.setText("Importando contactos...");
                    else
                        textView_carga_login.setText("Verificando elección...");*/
                    Esperar(500);
                }
                else {
                    ErrorEnElLogin("Error al verificar el correo");
                }
                msg=null;
            }
        }
    }

    void Esperar(int tiempo){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ObtenerTodosContactosTelefono();
            }
        },tiempo);
    }

    void Proceso(){
//        hideLoading();
        final Timer timer = new Timer();
        TimerTask timerTask;
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if(contactos.size()==0){//cont>=fin){
                    YouChatApplication.setCorreo(aut_user);
                    YouChatApplication.setPass(aut_pass);
                    YouChatApplication.setMark(2);
                   // progressBar_carga_login.setProgress(cont*100+200);
                    timer.cancel();
                    Intent trans=new Intent(LoginActivity.this, WelcomePerfilActivity.class);
                    startActivity(trans);
                    finish();
                }

                if(contactos.size()>0){
                    ItemContacto contacto=contactos.get(0);
                    contactos.remove(0);

                    String contacto_nombre = contacto.getNombre_personal();

                    Intent localIntent = new Intent("LOGIN_CARGA");
                    localIntent.putExtra("valor", contacto_nombre);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(localIntent);

                    //progressBar_carga_login.setProgress(cont*100+200);
                    dbWorker.insertarNuevoContacto(contacto);
                }
                cont++;
            }};
        timer.scheduleAtFixedRate(timerTask, tiempo, tiempo);
    }

    private void obtenerContactos(){
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // get the contact's information
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String email = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));

                if(email!=null && email.contains("@")){
                    if(name.equals("")) name=email;
                    contactos.add(0, new ItemContacto( "", name, email,ItemContacto.TIPO_CONTACTO,0, "", "", "",
                            "", "", "", "", "", false, false, false, 0));
                }
            } while (cursor.moveToNext());
        }
        // clean up cursor
        cursor.close();
    }

    private void obtenerContactosConTelefono(){
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // get the contact's information
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                int hasPhone = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                // get the user's email address
                String email = null;
                Cursor ce = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);
                if (ce != null && ce.moveToFirst()) {
                    email = ce.getString(ce.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                }
                ce.close();
                if(email==null)
                    continue;
                // get the user's phone number
                String phone = null;
                if (hasPhone > 0) {
                    Cursor cp = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    if (cp != null && cp.moveToFirst()) {
                        phone = cp.getString(cp.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                    cp.close();
                }

                if(email.contains("@")){
                    if(name.equals("")) name=email;
                    if(phone==null) phone="";
                    contactos.add(0, new ItemContacto( "", name, email,ItemContacto.TIPO_CONTACTO,0, "", "", phone,
                            "", "", "", "", "", false, false, false, 0));
                }
            } while (cursor.moveToNext());
        }
        // clean up cursor
        cursor.close();
    }

    private class ResponseReceiver extends BroadcastReceiver {
        private ResponseReceiver() {}

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent!=null) {
                final String action = intent.getAction();
                final String accion_login = "LOGIN_CARGA";
                if (accion_login.equals(action)) {
                    String valor = intent.getStringExtra("valor");
                    //textView_carga_login.setText(valor);
                }
            }
        }
    }

    private void ErrorEnElLogin(String error){

        /*progressBar_carga_login.setProgress(0);
        progressBar_carga_login.setVisibility(View.GONE);
        textView_carga_login.setVisibility(View.GONE);*/
        ViewUtil.fadeIn(findViewById(R.id.card),200);
        ViewUtil.fadeIn(findViewById(R.id.icon),200);

        ViewUtil.fadeOut(findViewById(R.id.animationLoad),100);

        animationLoad.pauseAnimation();

        go_EditPerfil.show();
        campo_pass.setEnabled(true);
        campo_email.setEnabled(true);

        Utils.ShowToastAnimated(activity,error,R.raw.swipe_disabled);
        /*importar.setVisibility(View.VISIBLE);
        rayabajo.setVisibility(View.VISIBLE);
        rayarriba.setVisibility(View.VISIBLE);*/
    }

    public void modificarImportarContactos(int i){
        importarContactos = i;
        /*if(importarContactos==1){
            login_img_importarContactos.setBackgroundResource(R.drawable.shape_icono_error);
            login_img_importarContactos.setImageResource(R.drawable.imp_tel);
            login_tv_importarContactos.setText("Importar contactos con correo y su número de teléfono.");
        }
        else if(importarContactos==2) {
            login_img_importarContactos.setBackgroundResource(R.drawable.shape_icono_saldo);
            login_img_importarContactos.setImageResource(R.drawable.imp_no_tel);
            login_tv_importarContactos.setText("Solo importar contactos con correo.");
        }
        else{
            login_img_importarContactos.setBackgroundResource(R.drawable.shape_icono_inbox);
            login_img_importarContactos.setImageResource(R.drawable.imp_empty);
            login_tv_importarContactos.setText("No importar contactos.");
        }*/

    }
}
