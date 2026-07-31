package cu.alexgi.youchat;

import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;
import com.vanniktech.emoji.EmojiEditText;
import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.api.widget.Widget;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

import cu.alexgi.youchat.BottomSheetDialogFragment.BottomSheetDialogFragment_Photo_perfil;
import cu.alexgi.youchat.base_datos.BDConstantes;
import cu.alexgi.youchat.base_datos.DBWorker;
import cu.alexgi.youchat.photoutil.CameraPhoto;
import cu.alexgi.youchat.photoutil.ImageLoader;
import cu.alexgi.youchat.views_GI.FABGI;
import cu.alexgi.youchat.views_GI.RadioButtonGI;

import static cu.alexgi.youchat.MainActivity.context;
import static cu.alexgi.youchat.photoutil.RealPathUtil.getDataColumn;
import static cu.alexgi.youchat.photoutil.RealPathUtil.isDownloadsDocument;
import static cu.alexgi.youchat.photoutil.RealPathUtil.isExternalStorageDocument;
import static cu.alexgi.youchat.photoutil.RealPathUtil.isMediaDocument;

public class WelcomePerfilActivity extends AppCompatActivity {
    private Permisos permisos;
    private CircleImageView imagen_perfil;
    private CameraPhoto cameraPhoto;
    private String miPathCamera;
    private WelcomePerfilActivity activity;
    private Context context;


    ///configuracion inicial
    private TextView tv_desc_config, tv_carg_bd, tv_bandeja_ent;
//    private SliderGI seekBar_conf_inicial;
    private RadioGroup radioGroup;
    private MaterialCardView efab_verificar_bd, efab_vaciar_be, efab_reintentar_be, efab_cargar_bd;
    private View progres_cir_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_perfil);

//        int mark=YouChatApplication.mark;
//        if(mark==2)
        {
            activity=this;
            context=this;
            permisos = new Permisos(this, this);
            cameraPhoto = new CameraPhoto(this);
            miPathCamera="";

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                try {
                    getWindow().setStatusBarColor(Color.parseColor(Utils.obtenerOscuroDe(YouChatApplication.itemTemas.getColor_barra())));
                    getWindow().setNavigationBarColor(Color.parseColor(Utils.obtenerOscuroDe(YouChatApplication.itemTemas.getColor_barra())));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            findViewById(R.id.root).setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_interior()));
//            TextInputLayout edit_alias_one = findViewById(R.id.edit_alias_one);
//            edit_alias_one.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getColor_btn())));

            tv_desc_config = findViewById(R.id.tv_desc_config);
            radioGroup = findViewById(R.id.radio_group);
//            seekBar_conf_inicial = findViewById(R.id.seekBar_conf_inicial);
//            seekBar_conf_inicial.setValue(2);

//            YouChatApplication.configuracion2();
//            tv_desc_config.setText("Configuración básica, consumo normal:\n" +
//                    "Serán activadas sólo las funciones más importantes para el uso de la aplicación.\n" +
//                    "Todos estos cambios pueden ser modificados luego en los ajustes de la apk.");

            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    Animation a = AnimationUtils.loadAnimation(context,R.anim.fade_in_fast);
                    switch (checkedId){
                        case R.id.radio_baja: YouChatApplication.configuracion1();
                            tv_desc_config.setText("Serán desactivadas todas las funciones que consuman datos de la aplicación.\n" +
                                    "Todos estos ajustes pueden ser modificados luego.");
                            break;
                        case R.id.radio_normal: YouChatApplication.configuracion2();
                            tv_desc_config.setText("Serán activadas sólo las funciones más importantes para el uso de la aplicación.\n" +
                                    "Todos estos ajustes pueden ser modificados luego.");
                            break;
                        case R.id.radio_alta: YouChatApplication.configuracion3();
                            tv_desc_config.setText("Serán activadas todas las funciones que consuman datos (con moderación en la configuración)," +
                                    " para una mejor experiencia y uso.\n" +
                                    "Todos estos ajustes pueden ser modificados luego.");
                            break;
                        default:
                            YouChatApplication.configuracion2();
                            tv_desc_config.setText("Serán activadas sólo las funciones más importantes para el uso de la aplicación.\n" +
                                    "Todos estos ajustes pueden ser modificados luego.");
                    }
                    tv_desc_config.startAnimation(a);
                }
            });
            ((RadioButtonGI)findViewById(R.id.radio_normal)).setChecked(true);

            /*seekBar_conf_inicial.addOnChangeListener(new Slider.OnChangeListener() {
                @Override
                public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                    if(fromUser){
                        Animation a = AnimationUtils.loadAnimation(context,R.anim.fade_in_fast);
                        switch ((int)value){
                            case 1: YouChatApplication.configuracion1();
                                tv_desc_config.setText("Sin configuración, bajo consumo:\n" +
                                        "Serán desactivadas todas las funciones que consuman datos de la aplicación.\n" +
                                        "Todos estos cambios pueden ser modificados luego en los ajustes de la apk.");
                                break;
                            case 2: YouChatApplication.configuracion2();
                                tv_desc_config.setText("Configuración básica, consumo normal:\n" +
                                        "Serán activadas sólo las funciones más importantes para el uso de la aplicación.\n" +
                                        "Todos estos cambios pueden ser modificados luego en los ajustes de la apk.");
                            break;
                            case 3: YouChatApplication.configuracion3();
                                tv_desc_config.setText("Configuración estrella, alto consumo:\n" +
                                        "Serán activadas todas las funciones que consuman datos (con moderación en la configuración)," +
                                        " de la aplicación para una mejor experiencia y uso.\n" +
                                        "Todos estos cambios pueden ser modificados luego en los ajustes de la apk.");
                            break;
                        }
                        tv_desc_config.startAnimation(a);
                    }
                }
            });*/


            tv_carg_bd = findViewById(R.id.tv_carg_bd);
            efab_verificar_bd = findViewById(R.id.efab_verificar_bd);
            efab_verificar_bd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    verificarExisteBaseDatos();
                }
            });
            efab_cargar_bd = findViewById(R.id.efab_cargar_bd);
            efab_cargar_bd.setVisibility(View.GONE);
            efab_cargar_bd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    importarBaseDatos();
                }
            });

            tv_bandeja_ent = findViewById(R.id.tv_bandeja_ent);
            efab_reintentar_be = findViewById(R.id.efab_reintentar_be);
            progres_cir_bar = findViewById(R.id.progres_cir_bar);
            efab_vaciar_be = findViewById(R.id.efab_vaciar_be);
            efab_vaciar_be.setVisibility(View.GONE);
            efab_vaciar_be.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    efab_vaciar_be.setVisibility(View.GONE);
                    progres_cir_bar.setVisibility(View.VISIBLE);
                    tv_bandeja_ent.setText("Vaciando bandeja, por favor espere...");
                    VaciarBandejaAsyncTask aaa = new VaciarBandejaAsyncTask();
                    aaa.execute();
                }
            });
            efab_reintentar_be.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reintentarVerificarCorreo();
                }
            });
            reintentarVerificarCorreo();


            ((TextView)findViewById(R.id.btn_verificar)).setTextColor(Color.WHITE);
            efab_verificar_bd.setStrokeColor(Color.parseColor(YouChatApplication.itemTemas.getColor_btn()));
            efab_verificar_bd.setCardBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_btn()));
            efab_verificar_bd.setRadius(YouChatApplication.curvaChat<=32?YouChatApplication.curvaChat:32);

            ((TextView)findViewById(R.id.btn_cargar)).setTextColor(Color.WHITE);
            efab_cargar_bd.setStrokeColor(Color.parseColor(YouChatApplication.itemTemas.getColor_btn()));
            efab_cargar_bd.setCardBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_btn()));
            efab_cargar_bd.setRadius(YouChatApplication.curvaChat<=32?YouChatApplication.curvaChat:32);

            ((TextView)findViewById(R.id.btn_reintentar)).setTextColor(Color.WHITE);
            efab_reintentar_be.setStrokeColor(Color.parseColor(YouChatApplication.itemTemas.getColor_btn()));
            efab_reintentar_be.setCardBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_btn()));
            efab_reintentar_be.setRadius(YouChatApplication.curvaChat<=32?YouChatApplication.curvaChat:32);

            ((TextView)findViewById(R.id.btn_vaciar)).setTextColor(Color.WHITE);
            efab_vaciar_be.setStrokeColor(Color.parseColor(YouChatApplication.itemTemas.getColor_btn()));
            efab_vaciar_be.setCardBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_btn()));
            efab_vaciar_be.setRadius(YouChatApplication.curvaChat<=32?YouChatApplication.curvaChat:32);

            imagen_perfil = findViewById(R.id.imagen_perfil);
            imagen_perfil.setBorderColor(Color.parseColor(YouChatApplication.itemTemas.getColor_btn()));
            Glide.with(context).load(YouChatApplication.ruta_img_perfil).error(R.drawable.profile_white).into(imagen_perfil);

            FABGI fab_camera = findViewById(R.id.fab_camera);
            fab_camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Photo_perfil.newInstance(WelcomePerfilActivity.this);
                    bsdFragment.show(getSupportFragmentManager(), "BSDialogPhotoPerfil");
                }
            });

            EmojiEditText ed_alias = findViewById(R.id.text_alias_one);
            ed_alias.setText(YouChatApplication.alias);
            ed_alias.setSelection(ed_alias.length());

            FloatingActionButton go_Main = findViewById(R.id.btn_editar_perfil);
            go_Main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String one_alias = ed_alias.getText().toString();
                    YouChatApplication.setAlias(one_alias);
                    YouChatApplication.setMark(3);

                    startActivity(new Intent(context, MainActivity.class));
                    overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
                    finish();
                }
            });
        }
//        else finish();
    }

    private void reintentarVerificarCorreo() {
        if(Utils.hayConnex(context)){
            progres_cir_bar.setVisibility(View.VISIBLE);
            tv_bandeja_ent.setText("Obteniendo cantidad de correos y peso total...");
            efab_reintentar_be.setVisibility(View.GONE);

            VerificarBandejaAsyncTask aa = new VerificarBandejaAsyncTask();
            aa.execute();
        } else {
            progres_cir_bar.setVisibility(View.GONE);
            efab_reintentar_be.setVisibility(View.VISIBLE);
            tv_bandeja_ent.setText("Falló al escanear la bandeja, vuelva a intentar.");
        }
    }

    private class VerificarBandejaAsyncTask extends AsyncTask<String, String, String> {

        int cantCorreos;
        long pesoTotal;
        String aut_user, aut_pass;
        boolean analisisExitoso;

        public VerificarBandejaAsyncTask() {
            cantCorreos = -1;
            pesoTotal = 0;
            aut_user = YouChatApplication.correo;
            aut_pass = YouChatApplication.pass;
            analisisExitoso = false;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try{
                Session session;
                IMAPStore store;
                Properties props = new Properties();
                IMAPFolder inbox;
                if(YouChatApplication.correo.endsWith("@nauta.cu")){
                    props.setProperty("mail.store.protocol", "imap");
                    props.setProperty("mail.imap.host", "imap.nauta.cu");
                    props.setProperty("mail.imap.port", "143");
                    session = Session.getDefaultInstance(props, new Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(aut_user, aut_pass);
                        }
                    });
                    store = (IMAPStore) session.getStore("imap");
                    store.connect("imap.nauta.cu", aut_user, aut_pass);
                }
                else{
                    props.setProperty("mail.imap.starttls.enable", "false");
                    props.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                    props.setProperty("mail.imap.socketFactory.fallback", "false");
                    props.setProperty("mail.imap.port", "993");
                    props.setProperty("mail.imap.socketFactory.port", "993");
                    session = Session.getInstance(props);
                    session.setDebug(true);
                    store = (IMAPStore) session.getStore("imap");
                    store.connect("imap.gmail.com", aut_user, aut_pass);
                }

                if(store.isConnected()) {
                    inbox = (IMAPFolder) store.getFolder("Inbox");
                    inbox.open(Folder.READ_WRITE);
                    if (inbox.isOpen()) {
                        Message[] messages = inbox.getMessages();
                        cantCorreos = messages.length;
                        analisisExitoso = true;
                        for(int i=0; i<cantCorreos; i++){
                            pesoTotal += messages[i].getSize();
                        }
                        inbox.close(false);
                    }
                    store.close();
                }
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            } catch (MessagingException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            if(analisisExitoso){
                progres_cir_bar.setVisibility(View.GONE);
                efab_reintentar_be.setVisibility(View.GONE);
                efab_vaciar_be.setVisibility(View.VISIBLE);
                if(cantCorreos==1)
                    tv_bandeja_ent.setText("Encontrado1 1 correo, con peso total igual a "+Utils.convertirBytes(pesoTotal));
                tv_bandeja_ent.setText("Encontrados "+cantCorreos+" correos, con peso total igual a "+Utils.convertirBytes(pesoTotal));
            }
            else {
                progres_cir_bar.setVisibility(View.GONE);
                efab_reintentar_be.setVisibility(View.VISIBLE);
                tv_bandeja_ent.setText("Falló al intentar escanear la bandeja, vuelva a intentar.");
            }
        }
    }

    private class VaciarBandejaAsyncTask extends AsyncTask<String, String, String> {

        boolean analisisExitoso;

        public VaciarBandejaAsyncTask() {
            analisisExitoso = false;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try{
                String aut_user = YouChatApplication.correo;
                String aut_pass = YouChatApplication.pass;
                Session session;
                IMAPStore store;
                Properties props = new Properties();
                IMAPFolder inbox;
                if(YouChatApplication.correo.endsWith("@nauta.cu")){
                    props.setProperty("mail.store.protocol", "imap");
                    props.setProperty("mail.imap.host", "imap.nauta.cu");
                    props.setProperty("mail.imap.port", "143");
                    session = Session.getDefaultInstance(props, new Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(aut_user, aut_pass);
                        }
                    });
                    store = (IMAPStore) session.getStore("imap");
                    store.connect("imap.nauta.cu", aut_user, aut_pass);
                }
                else{
                    props.setProperty("mail.imap.starttls.enable", "false");
                    props.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                    props.setProperty("mail.imap.socketFactory.fallback", "false");
                    props.setProperty("mail.imap.port", "993");
                    props.setProperty("mail.imap.socketFactory.port", "993");
                    session = Session.getInstance(props);
                    session.setDebug(true);
                    store = (IMAPStore) session.getStore("imap");
                    store.connect("imap.gmail.com", aut_user, aut_pass);
                }

                if(store.isConnected()) {
                    inbox = (IMAPFolder) store.getFolder("Inbox");
                    inbox.open(Folder.READ_WRITE);
                    if (inbox.isOpen()) {
                        Message[] messages = inbox.getMessages();
                        int l = messages.length;
                        for(int i=0; i<l; i++){
                            messages[i].setFlag(Flags.Flag.DELETED,true);
                        }
                        inbox.close(true);
                        analisisExitoso = true;
                    }
                    store.close();
                }
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            if(analisisExitoso)
                Utils.ShowToastAnimated(activity,"Vaciado finalizado con éxito",R.raw.contact_check);
            else Utils.ShowToastAnimated(activity,"Error al intentar vaciar la bandeja",R.raw.error);

            progres_cir_bar.setVisibility(View.GONE);
            efab_reintentar_be.setVisibility(View.VISIBLE);
            tv_bandeja_ent.setText("Volver a intentar escanear la bandeja.");
        }
    }

    public void usarCamara(){
        if(!permisos.requestPermissionCamera()) return;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                Date date = new Date();
                String fechaEntera = sdf.format(date);
                String nombre_img="img"+fechaEntera+".jpg";
                startActivityForResult(cameraPhoto
                        .takePhotoIntent(YouChatApplication.RUTA_IMAGENES_ENVIADAS, nombre_img), 1);
                cameraPhoto.addToGallery();
            } catch (IOException e) {
                Utils.ShowToastAnimated(WelcomePerfilActivity.this,"Ocurrió un error al acceder a la cámara",R.raw.error);
            }
        }
        else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Uri photoURI;
                    photoURI = FileProvider.getUriForFile(this,
                            "cu.alexgi.youchat.fileprovider",
                            photoFile);

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, 31);
                }
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String imageFileName = "img" + timeStamp + "_";
        File storageDir = getExternalFilesDir(YouChatApplication.RUTA_IMAGENES_ENVIADAS);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        miPathCamera = image.getAbsolutePath();
        return image;
    }

    public void abrirGaleria() {
        if(!permisos.requestPermissionAlmacenamiento()) return;
        //startActivityForResult(galleryPhoto.openGalleryIntent(), 10);
        selectImage();
    }

    private void selectImage() {
        Album.image(this)
                .singleChoice()
                .camera(false)
                .columnCount(3)
                .widget(
                        Widget.newDarkBuilder(this)
                                .title("Seleccione una foto de perfil")
                                .build()
                )
                .onResult(new Action<ArrayList<AlbumFile>>() {
                    @Override
                    public void onAction(@NonNull ArrayList<AlbumFile> result) {
                        if(result!=null && result.size()>0){
                            guardarImgAGaleria(result.get(0).getPath());
                        }
                        else Utils.ShowToastAnimated(WelcomePerfilActivity.this,"Error al cargar la imagen",R.raw.error);
                    }
                })
                .start();
    }

    private void guardarImgAGaleria(String photoPath) {
        File directorioImagenes = new File(YouChatApplication.RUTA_IMAGENES_PERFIL);
        if (!directorioImagenes.exists())
            directorioImagenes.mkdirs();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        Date date = new Date();
        String fechaEntera = sdf.format(date);
        String nombre_img=YouChatApplication.correo.replace(".","").replace("@","")+fechaEntera+".jpg";
        String miPath=YouChatApplication.RUTA_IMAGENES_PERFIL+nombre_img;

        try {
            if(ImageLoader.init().comprimirImagen(photoPath,miPath,50)){
                Glide.with(context).load(miPath).error(R.drawable.profile_white).into(imagen_perfil);
                YouChatApplication.setRuta_img_perfil(miPath);
            }
        } catch (FileNotFoundException e) {
            Utils.ShowToastAnimated(WelcomePerfilActivity.this,"Error al cargar la imagen",R.raw.error);
        }
    }

    public void borrarImgPerfil(){
        Utils.borrarFile(new File(YouChatApplication.ruta_img_perfil));
        YouChatApplication.setRuta_img_perfil("");
        Glide.with(this).load(R.drawable.profile_white).into(imagen_perfil);
    }

    private ActivityResultLauncher<Void> resultCamera = registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), new ActivityResultCallback<Bitmap>() {
        @Override
        public void onActivityResult(Bitmap result) {
            if(result!=null) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                    Date date = new Date();
                    String fechaEntera = sdf.format(date);
                    String nombre_img=YouChatApplication.correo.replace(".","").replace("@","")+fechaEntera+".jpg";
                    String miPath=YouChatApplication.RUTA_IMAGENES_PERFIL+nombre_img;

                    File fileVerif = new File (YouChatApplication.RUTA_IMAGENES_PERFIL);
                    boolean creado = fileVerif.exists();
                    if(!creado) creado = fileVerif.mkdirs();
                    if(creado){
                        File file = new File(miPath);
                        FileOutputStream fileOutputStream = new FileOutputStream(file, false);
                        result.compress(Bitmap.CompressFormat.JPEG, 80, fileOutputStream);
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        YouChatApplication.setRuta_img_perfil(miPath);
                        Glide.with(context).load(miPath).error(R.drawable.default_avatar).into(imagen_perfil);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    public synchronized static String getPathFromUri(Context context, Uri uri) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] { split[1] };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;}


    private ActivityResultLauncher<String> resultGallery = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            if (result != null) {
                String ruta_img_gal = getPathFromUri(context, result);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                Date date = new Date();
                String fechaEntera = sdf.format(date);
                String nombre_img=YouChatApplication.correo.replace(".","").replace("@","")+fechaEntera+".jpg";
                String miPath=YouChatApplication.RUTA_IMAGENES_PERFIL+nombre_img;

                File file = new File(YouChatApplication.RUTA_IMAGENES_PERFIL);
                boolean exist = file.exists();
                if (!exist) exist = file.mkdirs();
                if (exist) {
                    try {
                        if (ImageLoader.init().comprimirImagen(ruta_img_gal, miPath, 30)) {
                            YouChatApplication.setRuta_img_perfil(miPath);
                            Glide.with(context).load(miPath).error(R.drawable.profile_white).into(imagen_perfil);
                        }
                    } catch (FileNotFoundException e) {}
                }
            }
        }
    });

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==1) {//camara
//                ArrayList<ItemImg> itemImgs = new ArrayList<>();
                String miPath=cameraPhoto.getPhotoPath();
                Glide.with(this).load(miPath).into(imagen_perfil);
//                try {
//                    if(ImageLoader.init().comprimirImagen(miPath,miPath,calidad)){
//                        itemImgs.add(new ItemImg(miPath));
////                        EsperarParaElEditor(100, itemImgs);
//                        irAlEditor(itemImgs);
//                    }
//                } catch (FileNotFoundException e) {
//                    Utils.ShowToastAnimated(WelcomePerfilActivity.this,"Error al cargar la imagen",R.raw.error);
//                }
            }
            else if(requestCode == 31){ //camara para android 7 en adelante

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                File f = new File(miPathCamera);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                sendBroadcast(mediaScanIntent);

                Glide.with(this).load(miPathCamera).into(imagen_perfil);
//                ArrayList<ItemImg> itemImgs = new ArrayList<>();

//                try {
//                    if(ImageLoader.init().comprimirImagen(miPathCamera,miPathCamera,calidad)){
//                        itemImgs.add(new ItemImg(miPathCamera));
////                        EsperarParaElEditor(100, itemImgs);
//                        irAlEditor(itemImgs);
//                    }
//                } catch (FileNotFoundException e) {
//                    Utils.ShowToastAnimated(WelcomePerfilActivity.this,"Error al cargar la imagen",R.raw.error);
//                }
            }
        }
    }



    public void verificarExisteBaseDatos(){
        if(!new Permisos(activity,context).requestPermissionAlmacenamiento()) return;
        File sd = new File(YouChatApplication.RUTA_COPIA_BASE_DATOS);
        boolean exist = sd.exists();
        if(!exist) exist = sd.mkdirs();
        if(exist){
            try {
                String backupDBPath = "YouChat_BDatos.dbyc";
                File backupDB = new File(sd, backupDBPath);
                if (backupDB.exists()){
                    tv_carg_bd.setText("Copia de seguridad en: "+backupDB.getPath());
                    efab_cargar_bd.setVisibility(View.VISIBLE);
                    Utils.ShowToastAnimated(activity,"Existe una copia de seguridad local",R.raw.contact_check);
                }
                else{
                    tv_carg_bd.setText("No existen copias de seguridad");
                    Utils.ShowToastAnimated(activity,"No existe ninguna copia de seguridad local",R.raw.chats_infotip);
                }
            } catch (Exception e) {
                Utils.ShowToastAnimated(activity,"Falló al intentar buscar la copia de seguridad",R.raw.error);
                e.printStackTrace();
            }
        }
        else Utils.ShowToastAnimated(activity,"Falló al intentar buscar una copia de seguridad",R.raw.error);
    }

    public void importarBaseDatos(){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(1);
        View mview=getLayoutInflater().inflate(R.layout.dialog_confirm_db,null);
        dialog.setContentView(mview);

        View btn_cancel=mview.findViewById(R.id.btn_cancel);
        View btn_ok=mview.findViewById(R.id.btn_ok);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(!new Permisos(activity,context).requestPermissionAlmacenamiento()) return;
                File sd = new File(YouChatApplication.RUTA_COPIA_BASE_DATOS);
                boolean exist = sd.exists();
                if(!exist)
                    exist = sd.mkdirs();
                if(exist){
                    String nombreBd = BDConstantes.NOMBRE_BASE_DATOS;
                    try {
//                        File data = Environment.getDataDirectory();
                        if (sd.canWrite()) {
//                            String currentDBPath = "//data//"+getActivity().getPackageName()+"//databases//"+nombreBd+"";
                            String currentDBPath = "/data/"+activity.getPackageName()+"/databases/";
                            String backupDBPath = "YouChat_BDatos.dbyc";
//                            File currentDB = new File(data, currentDBPath);
                            File backupDB = new File(sd, backupDBPath);
                            if (backupDB.exists()){
                                String pass = Utils.MD5(YouChatApplication.correo+"YouChat");
                                if(pass!=null){
                                    int result = Utils.descomprimirArchivo(backupDB, Environment.getDataDirectory()+currentDBPath,nombreBd, pass);
                                    if(result==1){
                                        int versionBD=new DBWorker(context).obtenerVersionBD();
                                        if(versionBD<5) versionBD=5;
                                        if(versionBD!=YouChatApplication.version_bd){
                                            YouChatApplication.setVersion_bd(versionBD);
                                            new DBWorker(context);
                                        }
                                        Utils.ShowToastAnimated(activity,"Copia de seguridad cargada con éxito",R.raw.contact_check);
                                    }
                                    else if(result==2)
                                        Utils.ShowToastAnimated(activity,"Esta copia de seguridad no le pertenece a este correo",R.raw.error);
                                    else if(result==3)
                                        Utils.ShowToastAnimated(activity,"Copia de seguridad no encontrada o archivo dañado",R.raw.error);
                                    else
                                        Utils.ShowToastAnimated(activity,"Falló al intentar cargar la copia de seguridad",R.raw.error);
                                } else Utils.ShowToastAnimated(activity,"Falló al intentar guardar la copia de seguridad",R.raw.error);
                            } else Utils.ShowToastAnimated(activity,"No existe ninguna copia de seguridad para cargar",R.raw.chats_infotip);
                        }
                    } catch (Exception e) {
                        Utils.ShowToastAnimated(activity,"Falló al intentar cargar la copia de seguridad",R.raw.error);
                        e.printStackTrace();
                    }
                }
                else Utils.ShowToastAnimated(activity,"Falló al intentar cargar la copia de seguridad",R.raw.error);
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCancelable(true);
        dialog.show();
    }
}