package cu.alexgi.youchat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.MailTo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.vanniktech.emoji.EmojiEditText;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import cu.alexgi.youchat.BottomSheetDialogFragment.BottomSheetDialogFragment_Ayuda_ajustes;
import cu.alexgi.youchat.base_datos.BDConstantes;
import cu.alexgi.youchat.base_datos.DBWorker;
import cu.alexgi.youchat.items.ItemChat;
import cu.alexgi.youchat.items.ItemContacto;
import cu.alexgi.youchat.items.ItemNotificacion;
import cu.alexgi.youchat.items.ItemTemas;
import cu.alexgi.youchat.swipebackfragment.BaseSwipeBackFragment;
import cu.alexgi.youchat.views_GI.CheckBoxGI;

public class MainActivity extends AppCompatActivity implements BaseSwipeBackFragment.OnAddFragmentListener, PrincipalFragment.OnFragmentInteractionListener, EstadosFragment.OnFragmentInteractionListener, PostFragment.OnFragmentInteractionListener{

    private static final String TAG = "MainActivity";

    private boolean estaMinimizada;

    private static final long TIME_ALARM = 6000;
    //    private NavController navController;
    public static Context context;
    public static Permisos permisos;
    public static MainActivity mainActivity;
    public boolean abrirDrawer, irAPersonalizar, irAReenviar, irANotificar, irANotificarCorreo, irAChat;
    public Bundle bundleReenviar, bundleNotificacion, bundleChat;
    private boolean esNecCambiarColorBarraStatus;

    public static DBWorker dbWorker;
    public ArrayList<Fragment> actualFragment;

    private String listaFrag = "UsuariosPublicosFragment, HistorialPostFragment, EstadosViewPagerFragment, ImagePager, VistaMensajeCorreoFragment, NuevoMensajeCorreoFragment, ChatsActivityCorreo, " +
            "AjustesBuzonFragment, " +
            "BandejaFragment, ViewImageActivity, ReenviarActivity, ChatsActivity, ContactActivity, " +
            "ViewYouPerfilActivity, " +
            "NuevoEstadoTextoActivity, PrincipalActivity, AjustesActivity, Acerca_de_Activity" +
            ", AddThemeFragment, AdminEstadosActivity, BloqueadosActivity, EditPerfilActivity, EstadisticasFragment, " +
            "FondoActivity, ImageActivity, PersonalizarChat, SeguidoresActivity, ViewPerfilActivity," +
            "BloqueadosPostFragment, VistaMensajeCorreoFragment"+
            "PublicarPostFragment";

    private void addStackFragment(Fragment f){
        if(f!=null && f.getTag()!=null && listaFrag.contains(f.getTag())) actualFragment.add(0, f);
    }

    @Override
    protected void onResume() {
        super.onResume();
        estaMinimizada = false;
//        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        Intent pend = new Intent(this, MessageReceiver.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, pend,0);
//        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 5000, 6000, pendingIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setTheme(YouChatApplication.estiloActual);

        if(YouChatApplication.mainActivity!=null){
            YouChatApplication.mainActivity.finish();
        }
        YouChatApplication.mainActivity = this;

        dbWorker = new DBWorker(this);

        cambiarColorStatusBar(YouChatApplication.itemTemas.getStatus_bar());
        setContentView(R.layout.activity_main);

        actualFragment = new ArrayList<>();

        getSupportFragmentManager().registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentCreated(@NonNull FragmentManager fm, @NonNull Fragment f, @Nullable Bundle savedInstanceState) {
                super.onFragmentCreated(fm, f, savedInstanceState);
                addStackFragment(f);
            }

            @Override
            public void onFragmentViewDestroyed(@NonNull FragmentManager fm, @NonNull Fragment f) {
                super.onFragmentViewDestroyed(fm, f);
                    Log.e("REGISTRER","Destroy "+f.getTag());
                    if(esNecCambiarColorBarraStatus)
                        cambiarColorStatusBar(YouChatApplication.itemTemas.getStatus_bar());
                    if(f.getTag()!=null && listaFrag.contains(f.getTag())){

                        if(f.getTag().equalsIgnoreCase("ViewImageActivity") && YouChatApplication.viewImageActivity!=null) YouChatApplication.viewImageActivity.hideFragment();
                        else if(f.getTag().equalsIgnoreCase("ReenviarActivity") && YouChatApplication.reenviarActivity!=null) YouChatApplication.reenviarActivity.hideFragment();
                        else if(f.getTag().equalsIgnoreCase("ChatsActivity") && YouChatApplication.chatsActivity!=null) YouChatApplication.chatsActivity.hideFragment();
                        else if(f.getTag().equalsIgnoreCase("ViewYouPerfilActivity") && YouChatApplication.viewYouPerfilActivity!=null) YouChatApplication.viewYouPerfilActivity.hideFragment();

                        if(YouChatApplication.principalActivity!=null) YouChatApplication.principalActivity.onResume();

                        if (actualFragment.size() > 0) {
                            actualFragment.remove(0);
                            if (actualFragment.size() > 0) actualFragment.get(0).onResume();
                        }
                    }

//                actualFragment = null;
            }

        }, true);

//        PrincipalActivity principalActivity = new PrincipalActivity();

        loadFragment(new PrincipalActivity());


//        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
//            @Override
//            public void onBackStackChanged() {
//                getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount()-1).getId();
//
//                Log.e("onBackStackChanged","");
//            }
//        });


        int mark = YouChatApplication.mark;
        if (mark == 2) {
            startActivity(new Intent(this, WelcomePerfilActivity.class));
            finish();
        }
        else if (mark == 1) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        else if (mark == 0) {
            startActivity(new Intent(this, ViewPagerActivity.class));
            finish();
        }
        else if(mark==4 || dbWorker.obtenerCantidadDescripcionError()>=3){
            YouChatApplication.setMark(4);
            startActivity(new Intent(this, MuchosErroresActivity.class));
            finish();
        }
        else {
            Intent intent = this.getIntent();
            Bundle mibundle = intent.getExtras();

            context = this;
            mainActivity = this;
            permisos = new Permisos(this,this);
            dbWorker.eliminarMsgFecha();

            if (YouChatApplication.context.getPackageName().trim().equals("cu.alexgi.youchat")) {
                Utils.runOnUIThread(()->{
                    if(permisos.requestPermissionAlmacenamiento())
                    {
                        Utils.crearArchivoNoMedia(YouChatApplication.RUTA_IMAGENES_ENVIADAS);
                        Utils.crearArchivoNoMedia(YouChatApplication.RUTA_IMAGENES_RECIBIDA);
                        Utils.crearArchivoNoMedia(YouChatApplication.RUTA_FONDO_YOUCHAT);
                        Utils.crearArchivoNoMedia(YouChatApplication.RUTA_IMAGENES_PERFIL);
                        Utils.crearArchivoNoMedia(YouChatApplication.RUTA_ESTADOS_GUARDADOS);
                        Utils.crearArchivoNoMedia(YouChatApplication.RUTA_STICKERS);
                        Utils.crearArchivoNoMedia(YouChatApplication.RUTA_STICKERS_RECIBIDOS);
                        if(YouChatApplication.puedeHacerCopiaSeguridad){
                            YouChatApplication.setPuedeHacerCopiaSeguridad(false);
                            exportarBaseDatos();
                        }
                        ////////////////////////FILE//////////////////////////////
                        File space = Environment.getExternalStorageDirectory();
                        long free = space.getFreeSpace();

                        if((free/1024)<=204800){
                            long total = space.getTotalSpace();
                            long usado = total-free;

                            Dialog dialog = new Dialog(context);
                            dialog.requestWindowFeature(1);
                            View mview = getLayoutInflater().inflate(R.layout.dialog_space_low, null);
                            dialog.setContentView(mview);

                            TextView text_space = mview.findViewById(R.id.text_space);
                            ProgressBar bar_space = mview.findViewById(R.id.bar_space);
                            TextView usado_space = mview.findViewById(R.id.usado_space);
                            TextView total_space = mview.findViewById(R.id.total_space);
                            View btn_ok = mview.findViewById(R.id.btn_ok);

                            String libre = Utils.convertirBytes(free);
                            SpannableString s = new SpannableString("Tiene solo "+ libre +" de espacio disponible, podría tener problemas al usar la aplicación, se recomienda liberar espacio.");
                            s.setSpan(new StyleSpan(Typeface.BOLD),11,11+(libre.length()),0);
                            text_space.setText(s);

                            usado_space.setText(Utils.convertirBytes(usado));
                            total_space.setText(Utils.convertirBytes(total));
                            bar_space.setMax(100);
                            bar_space.setSecondaryProgress(100);
                            bar_space.setProgress((int)((usado*100)/total));

                            btn_ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                            dialog.setCancelable(false);
                            dialog.show();
                        }
                        ////////////////////////FILE//////////////////////////////
                    }
                });


                Utils.runOnUIThread(YouChatApplication::startPushService);
//                startService(new Intent(context, ChatService.class));
//                verificarFecha();
                verificarWorkerPost();
                verificarWorkerVaciarBuzon();
                verificarWorkerCopiaSeguridad();
                VerificarFestejos();
            }
            else {
                Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(1);
                View mview = getLayoutInflater().inflate(R.layout.dialog_confirm_animado, null);
                dialog.setContentView(mview);
                LottieAnimationView animation = mview.findViewById(R.id.animation);
                TextView text_icono = mview.findViewById(R.id.text_icono);
                TextView text_eliminar = mview.findViewById(R.id.text_eliminar);
                TextView btn_ok = mview.findViewById(R.id.btn_ok);
                View btn_cancel = mview.findViewById(R.id.btn_cancel);
                btn_cancel.setVisibility(View.GONE);
                animation.setAnimation(R.raw.tsv_setup_intro);
                text_icono.setText("¡ATENCIÓN!");
                text_eliminar.setText("Se ha detectado una versión no oficial de YouChat. Por favor, obtenga la app de fuentes seguras.");
                btn_ok.setText("CERRAR");
                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        finish();
                    }
                });
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                dialog.setCancelable(false);
                dialog.show();
            }

            abrirDrawer = irAChat = irAPersonalizar = irAReenviar = irANotificar = irANotificarCorreo = false;
            bundleReenviar = null;
            bundleNotificacion = null;
            bundleChat = null;
            if (mibundle != null && mibundle.getInt("cambio_de_tema", 0) == 1) {
                mibundle.putInt("cambio_de_tema", 0);
                if(YouChatApplication.principalActivity!=null)
                    YouChatApplication.principalActivity.drawer.openDrawer(GravityCompat.START, true);
                else
                    abrirDrawer=true;
            }
            else if (mibundle != null && mibundle.getInt("cambio_de_tema_interno", 0) == 1) {
                mibundle.putInt("cambio_de_tema_interno", 0);
                if(YouChatApplication.principalActivity!=null)
                    YouChatApplication.principalActivity.irAPersonalizar();
                else irAPersonalizar=true;
            }
            else if (intent != null && intent.getAction() != null) {

                if (intent.getAction().equals("NOTI_CHAT") && YouChatApplication.estaAndandoChatService()) {
                    ItemNotificacion itemNotificacion = YouChatApplication.chatService.getItemNoti();
                    if (itemNotificacion != null) {
                        bundleNotificacion = new Bundle();
                        bundleNotificacion.putString("usuario", itemNotificacion.getUsuario());
                        bundleNotificacion.putString("correo", itemNotificacion.getCorreo());
                    }
                    else if(intent.getExtras()!=null){
                        bundleNotificacion = intent.getExtras();
                    }
                    if(bundleNotificacion!=null){
//                        if(YouChatApplication.principalActivity!=null){
//                            YouChatApplication.principalActivity.irAChat(bundleNotificacion);
//                            bundleNotificacion = null;
//                        }
//                        else
                            irANotificar = true;
                    }

                } else if (intent.getAction().equals("NOTI_CORREO")
                        && YouChatApplication.estaAndandoChatService()) {
                    YouChatApplication.chatService.eliminarNotiCorreo();
//                    if(YouChatApplication.principalActivity!=null)
//                        YouChatApplication.principalActivity.irABandejaEntrada();
//                    else
                        irANotificarCorreo = true;
                } else if (intent.getAction().equals(Intent.ACTION_SEND)) {
                    YouChatApplication.intentReenviar = intent;
                    bundleReenviar = new Bundle();
                    bundleReenviar.putString("key",Intent.ACTION_SEND);
//                    if(YouChatApplication.principalActivity!=null){
//                        YouChatApplication.principalActivity.irAReenviar(bundleReenviar);
//                        bundleReenviar = null;
//                    }
//                    else
                        irAReenviar = true;
                }
                else if(intent.getAction().equals(Intent.ACTION_VIEW)
                        && intent.getExtras()!=null
                        && !intent.getExtras().getString("ruta_fondo","lol").equals("lol")){

                    String ruta = intent.getExtras().getString("ruta_fondo","");
                    if(Utils.esImagen(ruta)){
                        YouChatApplication.setRuta_fondo(ruta);
                        if(YouChatApplication.principalActivity!=null)
                            YouChatApplication.principalActivity.cargarFondo();
                        Utils.ShowToastAnimated(mainActivity,"Fondo establecido correctamente",R.raw.contact_check);
                    }
                    else Utils.ShowToastAnimated(mainActivity,"Error al intentar cargar el fondo",R.raw.error);
                }
                else if(intent.getAction().equals(Intent.ACTION_VIEW)
                        && intent.getExtras()!=null
                        && !intent.getExtras().getString("theme","lol").equals("lol")){

                    String theme = intent.getExtras().getString("theme","");
                    ItemTemas tema = Convertidor.createItemTemasOfMensaje(theme);
                    if(tema!=null && tema.temaCorrecto()){
                        if(!dbWorker.existeTemaId(tema.getId())){
                            dbWorker.insertarNuevoTema(tema);
                            Utils.ShowToastAnimated(mainActivity,"Tema agregado con éxito",R.raw.contact_check);
                        }
                        else Utils.ShowToastAnimated(mainActivity,"Este tema ya existe",R.raw.ic_ban);
                    }
                    else Utils.ShowToastAnimated(mainActivity,"El tema es incorrecto",R.raw.ic_ban);
                }
                else if(intent.getAction().equals(Intent.ACTION_VIEW)
                        && intent.getExtras()!=null
                        && !intent.getExtras().getString("lista_usuarios","lol").equals("lol")){

                    String cad = intent.getExtras().getString("lista_usuarios","");
                    if(cad!=null && !cad.equals("")){
                        String[] lista = cad.split(",");
                        int l = lista.length;
                        String text = "";
                        for(int i=0; i<l; i++){
                            text+=i+" - "+lista[i]+"\n";
                        }
                        BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                                .newInstance("Lista de usuarios", text);
                        bsdFragment.show(getSupportFragmentManager(), "BSDialogAyudaAjustes");
                    }
                }
                else if (intent.getAction().equals(Intent.ACTION_VIEW)
                        || intent.getAction().equals(Intent.ACTION_SENDTO)) {
                    try {
                        Uri uri = intent.getData();
                        if (uri != null) {
                            String scheme = uri.getScheme();
                            if (scheme != null && scheme.equals("mailto")) {
                                MailTo mailto = MailTo.parse(uri.toString());
                                String recipientsList = mailto.getTo();
                                if (recipientsList != null && !recipientsList.isEmpty()) {
                                    String[] recipientsArray = recipientsList.split(",");
                                    if (recipientsArray.length >= 1) {
                                        String cor = recipientsArray[0];
                                        if (!cor.equals("")) {

                                            String cad=mailto.getBody();
                                            if(cad==null) cad = "";
                                            String finalCad = cad;

                                            String nom = dbWorker.obtenerNombre(cor);
                                            if (cor.equals(YouChatApplication.correo)
                                                    || dbWorker.existeSiguiendoA(cor)) {
//                                                if(YouChatApplication.principalActivity!=null){
//                                                    YouChatApplication.principalActivity.irAChat(nom,cor,finalCad);
//                                                }
//                                                else {
                                                    irAChat=true;
                                                    bundleChat=new Bundle();
                                                    bundleChat.putString("usuario",nom);
                                                    bundleChat.putString("correo",cor);
                                                    bundleChat.putString("body",finalCad);
//                                                }
                                            } else {
                                                Dialog dialog = new Dialog(context);
                                                dialog.requestWindowFeature(1);
                                                View mview = getLayoutInflater().inflate(R.layout.dialog_confirm, null);
                                                dialog.setContentView(mview);

                                                LinearLayout header = mview.findViewById(R.id.header);
                                                ImageView icono_eliminar = mview.findViewById(R.id.icono_eliminar);
                                                TextView text_icono = mview.findViewById(R.id.text_icono);
                                                TextView text_eliminar = mview.findViewById(R.id.text_eliminar);
                                                TextView btn_ok = mview.findViewById(R.id.btn_ok);
                                                TextView btn_cancel = mview.findViewById(R.id.btn_cancel);

                                                header.setBackgroundResource(R.color.primary);
                                                icono_eliminar.setImageResource(R.drawable.info_circle);
                                                text_icono.setText("Hola");
                                                text_eliminar.setText("¿Qué deseas hacer con " + nom + "?");
                                                btn_ok.setText("Ir a chat");
                                                btn_cancel.setText("Seguir");

                                                btn_ok.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        if(YouChatApplication.principalActivity!=null){
                                                            YouChatApplication.principalActivity.irAChat(nom,cor,finalCad);
                                                        }
                                                        else {
                                                            irAChat=true;
                                                            bundleChat=new Bundle();
                                                            bundleChat.putString("usuario",nom);
                                                            bundleChat.putString("correo",cor);
                                                            bundleChat.putString("body",finalCad);
                                                        }
                                                        dialog.dismiss();
                                                    }
                                                });
                                                btn_cancel.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        if (YouChatApplication.estaAndandoChatService() && YouChatApplication.chatService.hayConex) {
                                                            dialog.dismiss();
                                                            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
                                                            Date date = new Date();
                                                            String fechaEntera = sdf.format(date);
                                                            String hora = Convertidor.conversionHora(fechaEntera);
                                                            String fecha = Convertidor.conversionFecha(fechaEntera);
                                                            ItemChat solicitud = new ItemChat(cor, "1");
                                                            solicitud.setId("-ss-");
                                                            solicitud.setHora(hora);
                                                            solicitud.setFecha(fecha);
                                                            if(YouChatApplication.estaAndandoChatService())
                                                                YouChatApplication.chatService.enviarMensaje(solicitud, SendMsg.CATEGORY_SOL_SEGUIR);
                                                            Utils.ShowToastAnimated(mainActivity, "Solicitud enviada", R.raw.contact_check);
                                                        } else
                                                            Utils.ShowToastAnimated(mainActivity, "Compruebe su conexión", R.raw.ic_ban);
                                                    }
                                                });
                                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                                                dialog.setCancelable(true);
                                                dialog.show();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        else {
                            Bundle bundle = intent.getExtras();
                            if (bundle != null) {
                                String cor = bundle.getString(Intent.EXTRA_EMAIL);
                                if (!cor.equals("")) {
                                    ItemContacto contacto = dbWorker.obtenerContacto(cor);
                                    if (contacto == null)
                                        contacto = new ItemContacto(cor, cor);
                                    if(YouChatApplication.principalActivity!=null){
                                        YouChatApplication.principalActivity.irAChat(contacto.getNombreMostrar(),contacto.getCorreo());
                                    }
                                    else {
                                        irAChat=true;
                                        bundleChat=new Bundle();
                                        bundleChat.putString("usuario",contacto.getNombreMostrar());
                                        bundleChat.putString("correo",contacto.getCorreo());
                                        bundleChat.putString("body","");
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if(YouChatApplication.enviarEnter){
//            if(YouChatApplication.chatsActivity!=null){
//
//            }
//        }
        return super.onKeyDown(keyCode, event);
    }

    public synchronized void cambiarColorStatusBar(String color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                getWindow().setStatusBarColor(Color.parseColor(color));
                getWindow().setNavigationBarColor(Color.parseColor(color));

//                getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private synchronized void verificarWorkerPost(){
//        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(WorkerService.class)
//                .addTag("WORKER-POST-YOUCHAT")
//                .setInitialDelay(24, TimeUnit.HOURS)
//                .build();

//        String id = oneTimeWorkRequest.getStringId();
//        UUID uuid = UUID.fromString(id);
//        WorkManager.getInstance(context).getWorkInfoById(uuid);

        int cantWorkerNoFinalizado = 0;
        try {
            if(WorkManager.getInstance(context).getWorkInfosByTag("WORKER-POST-YOUCHAT").get()!=null
                    && WorkManager.getInstance(context).getWorkInfosByTag("WORKER-POST-YOUCHAT").get().size()>0){
                List<WorkInfo> workInfos = WorkManager.getInstance(context)
                        .getWorkInfosByTag("WORKER-POST-YOUCHAT").get();
                for(int i=0; i<workInfos.size(); i++){
                    if(!workInfos.get(i).getState().isFinished()){
                        cantWorkerNoFinalizado++;
                        break;
                    }
                }
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
            cantWorkerNoFinalizado=-1;
        } catch (InterruptedException e) {
            e.printStackTrace();
            cantWorkerNoFinalizado=-1;
        }
        if(cantWorkerNoFinalizado==-1){
            cantWorkerNoFinalizado=0;
            WorkManager.getInstance(context).cancelAllWorkByTag("WORKER-POST-YOUCHAT");
        }

        if(cantWorkerNoFinalizado==0){
            PeriodicWorkRequest periodicWorkRequest =
                    new PeriodicWorkRequest.Builder(WorkerService.class,
                            24, TimeUnit.HOURS)
                            .addTag("WORKER-POST-YOUCHAT")
                            .build();
            WorkManager.getInstance(YouChatApplication.context).enqueue(periodicWorkRequest);
            YouChatApplication.setCantPostSubidosHoy(0);
            YouChatApplication.setCantComentarioPostSubidosHoy(0);
        }
    }

    private void verificarWorkerVaciarBuzon(){
        boolean existeWorker = false;
        try {
            if(WorkManager.getInstance(context).getWorkInfosByTag("WORKER-VACIAR-BANDEJA").get()!=null
                    && WorkManager.getInstance(context).getWorkInfosByTag("WORKER-VACIAR-BANDEJA").get().size()>0){
                List<WorkInfo> workInfos = WorkManager.getInstance(context)
                        .getWorkInfosByTag("WORKER-VACIAR-BANDEJA").get();
                for(int i=0; i<workInfos.size(); i++){
                    if(!workInfos.get(i).getState().isFinished()){
                        existeWorker = true;
                        break;
                    }
                }
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            if(YouChatApplication.opcionVaciadoAutomaticoMensajes==1 && existeWorker){
                WorkManager.getInstance(context).cancelAllWorkByTag("WORKER-VACIAR-BANDEJA");
            }
            else if(YouChatApplication.opcionVaciadoAutomaticoMensajes==2 && !existeWorker){
                PeriodicWorkRequest periodicWorkRequest =
                        new PeriodicWorkRequest.Builder(WorkerServiceVaciarBandeja.class,
                                1, TimeUnit.DAYS)
                                .addTag("WORKER-VACIAR-BANDEJA")
                                .build();
                WorkManager.getInstance(context).enqueue(periodicWorkRequest);
            }
            else if(YouChatApplication.opcionVaciadoAutomaticoMensajes==3 && !existeWorker){
                PeriodicWorkRequest periodicWorkRequest =
                        new PeriodicWorkRequest.Builder(WorkerServiceVaciarBandeja.class,
                                3, TimeUnit.DAYS)
                                .addTag("WORKER-VACIAR-BANDEJA")
                                .build();
                WorkManager.getInstance(context).enqueue(periodicWorkRequest);
            }
            else if(YouChatApplication.opcionVaciadoAutomaticoMensajes==4 && !existeWorker){
                PeriodicWorkRequest periodicWorkRequest =
                        new PeriodicWorkRequest.Builder(WorkerServiceVaciarBandeja.class,
                                7, TimeUnit.DAYS)
                                .addTag("WORKER-VACIAR-BANDEJA")
                                .build();
                WorkManager.getInstance(context).enqueue(periodicWorkRequest);
            }
        }
    }

    private void verificarWorkerCopiaSeguridad(){
        boolean existeWorker = false;
        try {
            if(WorkManager.getInstance(context).getWorkInfosByTag("WORKER-COPIA-SEGURIDAD").get()!=null
                    && WorkManager.getInstance(context).getWorkInfosByTag("WORKER-COPIA-SEGURIDAD").get().size()>0){
                List<WorkInfo> workInfos = WorkManager.getInstance(context)
                        .getWorkInfosByTag("WORKER-COPIA-SEGURIDAD").get();
                for(int i=0; i<workInfos.size(); i++){
                    if(!workInfos.get(i).getState().isFinished()){
                        existeWorker = true;
                        break;
                    }
                }
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            if(YouChatApplication.opcionCopiaSeguridadAutomatica==1 && existeWorker){
                WorkManager.getInstance(context).cancelAllWorkByTag("WORKER-COPIA-SEGURIDAD");
            }
            else if(YouChatApplication.opcionCopiaSeguridadAutomatica==2 && !existeWorker){
                PeriodicWorkRequest periodicWorkRequest =
                        new PeriodicWorkRequest.Builder(WorkerServiceCopiaSeguridad.class,
                                1, TimeUnit.DAYS)
                                .addTag("WORKER-COPIA-SEGURIDAD")
                                .build();
                WorkManager.getInstance(context).enqueue(periodicWorkRequest);
            }
            else if(YouChatApplication.opcionCopiaSeguridadAutomatica==3 && !existeWorker){
                PeriodicWorkRequest periodicWorkRequest =
                        new PeriodicWorkRequest.Builder(WorkerServiceCopiaSeguridad.class,
                                3, TimeUnit.DAYS)
                                .addTag("WORKER-COPIA-SEGURIDAD")
                                .build();
                WorkManager.getInstance(context).enqueue(periodicWorkRequest);
            }
            else if(YouChatApplication.opcionCopiaSeguridadAutomatica==4 && !existeWorker){
                PeriodicWorkRequest periodicWorkRequest =
                        new PeriodicWorkRequest.Builder(WorkerServiceCopiaSeguridad.class,
                                7, TimeUnit.DAYS)
                                .addTag("WORKER-COPIA-SEGURIDAD")
                                .build();
                WorkManager.getInstance(context).enqueue(periodicWorkRequest);
            }
        }
    }

    private synchronized void verificarFecha() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date datHoy = new Date(sdf.parse(sdf.format(new Date())).getTime());
            String fecHoy = sdf.format(datHoy);

            if(YouChatApplication.fechaHoy.equals(""))
                YouChatApplication.setFechaHoy(fecHoy);

            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            Date datGuardado = new Date(format.parse(YouChatApplication.fechaHoy).getTime());

            long dif = (datHoy.getTime() - datGuardado.getTime()) / 86400000;
            if (dif >= 1) {
                YouChatApplication.setFechaHoy(fecHoy);
                YouChatApplication.setCantPostSubidosHoy(0);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private synchronized void VerificarFestejos() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());
        Date date = new Date();
        String fechaEntera = sdf.format(date);
        fechaEntera = fechaEntera.replace(" ", "");

        if (fechaEntera.equals("31/12")) {
            if (YouChatApplication.felicidades_fin_ano) {
                YouChatApplication.setFelicidades_fin_ano(false);
                Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(1);
                View mview = getLayoutInflater().inflate(R.layout.dialog_sorpresa, null);
                dialog.setContentView(mview);

                LottieAnimationView icono_fiesta = mview.findViewById(R.id.icono_fiesta);
                icono_fiesta.setAnimation(R.raw.wallet_congrats);
                //ImageView icono_fiesta=mview.findViewById(R.id.icono_fiesta);
                TextView text_icono = mview.findViewById(R.id.text_icono);
                TextView text_fiesta = mview.findViewById(R.id.text_fiesta);
                View btn_ok = mview.findViewById(R.id.btn_ok);

                //icono_fiesta.setImageResource(R.drawable.fest2);
                text_icono.setText("Feliz fin de año");
                text_fiesta.setText("El colectivo de YouChat le desea que pase un inolvidable día");

                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                dialog.setCancelable(false);
                dialog.show();
            }
        } else if (!YouChatApplication.felicidades_fin_ano)
            YouChatApplication.setFelicidades_fin_ano(true);

        if (fechaEntera.equals("01/01")) {
            if (YouChatApplication.felicidades_ano_nuevo) {
                YouChatApplication.setFelicidades_ano_nuevo(false);

                Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(1);
                View mview = getLayoutInflater().inflate(R.layout.dialog_sorpresa, null);
                dialog.setContentView(mview);

                LottieAnimationView icono_fiesta = mview.findViewById(R.id.icono_fiesta);
                icono_fiesta.setAnimation(R.raw.wallet_allset);
                //ImageView icono_fiesta=mview.findViewById(R.id.icono_fiesta);
                TextView text_icono = mview.findViewById(R.id.text_icono);
                TextView text_fiesta = mview.findViewById(R.id.text_fiesta);
                View btn_ok = mview.findViewById(R.id.btn_ok);

                //icono_fiesta.setImageResource(R.drawable.fest3);
                text_icono.setText("Feliz año nuevo");
                text_fiesta.setText("El colectivo de YouChat le desea lo mejor para este año");

                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                dialog.setCancelable(false);
                dialog.show();
            }
        } else if (!YouChatApplication.felicidades_ano_nuevo)
            YouChatApplication.setFelicidades_ano_nuevo(true);

        if (fechaEntera.equals("08/03")) {
            if (YouChatApplication.felicidades_dia_mujer && YouChatApplication.genero.equals("Femenino")) {
                YouChatApplication.setFelicidades_dia_mujer(false);

                Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(1);
                View mview = getLayoutInflater().inflate(R.layout.dialog_sorpresa, null);
                dialog.setContentView(mview);

                LottieAnimationView icono_fiesta = mview.findViewById(R.id.icono_fiesta);
                icono_fiesta.setAnimation(R.raw.day_women);
                //ImageView icono_fiesta=mview.findViewById(R.id.icono_fiesta);
                TextView text_icono = mview.findViewById(R.id.text_icono);
                TextView text_fiesta = mview.findViewById(R.id.text_fiesta);
                View btn_ok = mview.findViewById(R.id.btn_ok);

                //icono_fiesta.setImageResource(R.drawable.fest5);
                text_icono.setText("Feliz día de la mujer");
                text_fiesta.setText("El colectivo de YouChat le desea un hermoso día");

                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                dialog.setCancelable(false);
                dialog.show();
            }
        } else if (!YouChatApplication.felicidades_dia_mujer)
            YouChatApplication.setFelicidades_dia_mujer(true);

        if (fechaEntera.equals("14/02")) {
            if (YouChatApplication.felicidades_14_febrero) {
                YouChatApplication.setFelicidades_14_febrero(false);

                Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(1);
                View mview = getLayoutInflater().inflate(R.layout.dialog_sorpresa, null);
                dialog.setContentView(mview);

                LottieAnimationView icono_fiesta = mview.findViewById(R.id.icono_fiesta);
                icono_fiesta.setAnimation(R.raw.aalove_letter);
                //ImageView icono_fiesta=mview.findViewById(R.id.icono_fiesta);
                TextView text_icono = mview.findViewById(R.id.text_icono);
                TextView text_fiesta = mview.findViewById(R.id.text_fiesta);
                View btn_ok = mview.findViewById(R.id.btn_ok);

                //icono_fiesta.setImageResource(R.drawable.fest6);
                text_icono.setText("Feliz San Valentín");
                text_fiesta.setText("El colectivo de YouChat le desea un cálido día");

                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                dialog.setCancelable(false);
                dialog.show();
            }
        } else if (!YouChatApplication.felicidades_14_febrero)
            YouChatApplication.setFelicidades_14_febrero(true);

        if (fechaEntera.equals("24/12")) {
            if (YouChatApplication.felicidades_noche_buena) {
                YouChatApplication.setFelicidades_noche_buena(false);

                Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(1);
                View mview = getLayoutInflater().inflate(R.layout.dialog_sorpresa, null);
                dialog.setContentView(mview);

                LottieAnimationView icono_fiesta = mview.findViewById(R.id.icono_fiesta);
                icono_fiesta.setAnimation(R.raw.night_good);
                //ImageView icono_fiesta=mview.findViewById(R.id.icono_fiesta);
                TextView text_icono = mview.findViewById(R.id.text_icono);
                TextView text_fiesta = mview.findViewById(R.id.text_fiesta);
                View btn_ok = mview.findViewById(R.id.btn_ok);

                //icono_fiesta.setImageResource(R.drawable.fest8);
                text_icono.setText("Feliz noche buena");
                text_fiesta.setText("El colectivo de YouChat le desea una agradable noche en familia");

                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                dialog.setCancelable(false);
                dialog.show();
            }
        } else if (!YouChatApplication.felicidades_noche_buena)
            YouChatApplication.setFelicidades_noche_buena(true);

        if (fechaEntera.equals("31/10")) {
            if (YouChatApplication.felicidades_halloween) {
                YouChatApplication.setFelicidades_halloween(false);

                Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(1);
                View mview = getLayoutInflater().inflate(R.layout.dialog_sorpresa, null);
                dialog.setContentView(mview);

                LottieAnimationView icono_fiesta = mview.findViewById(R.id.icono_fiesta);
                icono_fiesta.setAnimation(R.raw.halloweenkin6);
                //ImageView icono_fiesta=mview.findViewById(R.id.icono_fiesta);
                TextView text_icono = mview.findViewById(R.id.text_icono);
                TextView text_fiesta = mview.findViewById(R.id.text_fiesta);
                View btn_ok = mview.findViewById(R.id.btn_ok);

                //icono_fiesta.setImageResource(R.drawable.fest9);
                text_icono.setText("Feliz Halloween");
                text_fiesta.setText("El colectivo de YouChat le desea una terrorífica noche de brujas");

                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                dialog.setCancelable(false);
                dialog.show();
            }
        } else if (!YouChatApplication.felicidades_halloween)
            YouChatApplication.setFelicidades_halloween(true);

        if (fechaEntera.equals(YouChatApplication.fecha_cumpleanos)) {
            if (YouChatApplication.felicidades_cumpleanos) {
                YouChatApplication.setFelicidades_cumpleanos(false);

                Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(1);
                View mview = getLayoutInflater().inflate(R.layout.dialog_sorpresa, null);
                dialog.setContentView(mview);

                LottieAnimationView icono_fiesta = mview.findViewById(R.id.icono_fiesta);
                icono_fiesta.setAnimation(R.raw.gift);
                //ImageView icono_fiesta=mview.findViewById(R.id.icono_fiesta);
                TextView text_icono = mview.findViewById(R.id.text_icono);
                TextView text_fiesta = mview.findViewById(R.id.text_fiesta);
                View btn_ok = mview.findViewById(R.id.btn_ok);

                //icono_fiesta.setImageResource(R.drawable.fest1);
                text_icono.setText("Feliz cumpleaños");
                text_fiesta.setText("El colectivo de YouChat le desea un excelente día");

                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                dialog.setCancelable(false);
                dialog.show();
            }
        } else if (!YouChatApplication.felicidades_cumpleanos)
            YouChatApplication.setFelicidades_cumpleanos(true);

        if (YouChatApplication.pedir_confirmacion_avisar_union) {
            YouChatApplication.setPedir_confirmacion_avisar_union(false);
            Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(1);
            View mview = getLayoutInflater().inflate(R.layout.dialog_confirm_animado, null);
            dialog.setContentView(mview);

            TextView btn_ok = mview.findViewById(R.id.btn_ok);
            View btn_cancel = mview.findViewById(R.id.btn_cancel);

            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    YouChatApplication.setAvisar_union_yc(true);
                }
            });
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    YouChatApplication.setAvisar_union_yc(false);
                }
            });

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.setCancelable(true);
            dialog.show();
        }
    }


    public void vaciarPila(){
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {

//            Explode explode = null;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                explode = new Explode();
//                explode.setDuration(500);
//                getWindow().setExitTransition(explode);
//                finishAfterTransition();
//            }
//            else

            YouChatApplication.initAll();
            Intent trans = new Intent(context, MainActivity.class);
            Bundle mibundle = new Bundle();
            mibundle.putInt("cambio_de_tema_interno", 1);
            trans.putExtras(mibundle);
            startActivity(trans);
            finish();

//            YouChatApplication.initAll();
//            Intent ss = new Intent(this, MainActivity.class);
//            startActivity(ss);
        }
        else {
            super.onBackPressed();
            vaciarPila();
        }
    }

    public void vaciarPilaCambioTema(){
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
//            Explode explode = null;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                explode = new Explode();
//                explode.setDuration(500);
//                getWindow().setExitTransition(explode);
//                finishAfterTransition();
//            }
//            else
            YouChatApplication.initAll();
            Intent trans = new Intent(context, MainActivity.class);
            Bundle mibundle = new Bundle();
            mibundle.putInt("cambio_de_tema", 1);
            trans.putExtras(mibundle);
            startActivity(trans);
            finish();

        }
        else {
            super.onBackPressed();
            vaciarPilaCambioTema();
        }
    }

    public void atrasFragment(){
//        Log.e("atrasFragment", "ENTRO");
        Utils.ocultarKeyBoard(this);
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) finish();
        else super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
//        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
//            finish();
//        } else
        Utils.ocultarKeyBoard(this);
        if(actualFragment!=null && actualFragment.size()>0 && !actualFragment.get(0).equals("")) {
            Log.e("FRAGMENT ACTUAL ATRAS","tag: "+actualFragment.get(0).getTag());
            if(actualFragment.get(0).getTag()!=null){
                if(actualFragment.get(0).getTag().equalsIgnoreCase("ViewImageActivity") && YouChatApplication.viewImageActivity!=null) YouChatApplication.viewImageActivity.atras();
                else if(actualFragment.get(0).getTag().equalsIgnoreCase("ReenviarActivity") && YouChatApplication.reenviarActivity!=null) YouChatApplication.reenviarActivity.atras();
                else if(actualFragment.get(0).getTag().equalsIgnoreCase("ChatsActivity") && YouChatApplication.chatsActivity!=null) YouChatApplication.chatsActivity.onBackPressed();
                else if(actualFragment.get(0).getTag().equalsIgnoreCase("ContactActivity") && YouChatApplication.contactActivity!=null) YouChatApplication.contactActivity.atras();
                else if(actualFragment.get(0).getTag().equalsIgnoreCase("ViewYouPerfilActivity") && YouChatApplication.viewYouPerfilActivity!=null) YouChatApplication.viewYouPerfilActivity.atras();
                else if(actualFragment.get(0).getTag().equalsIgnoreCase("ViewPerfilActivity") && YouChatApplication.ViewPerfilActivity!=null) YouChatApplication.ViewPerfilActivity.atras();
                else if(actualFragment.get(0).getTag().equalsIgnoreCase("NuevoEstadoTextoActivity") && YouChatApplication.nuevoEstadoTextoActivity!=null) YouChatApplication.nuevoEstadoTextoActivity.atras();
                else if(actualFragment.get(0).getTag().equalsIgnoreCase("PrincipalActivity") && YouChatApplication.principalActivity!=null) YouChatApplication.principalActivity.onBackPressed();
                else if(actualFragment.get(0).getTag().equalsIgnoreCase("AddThemeFragment") && YouChatApplication.addThemeFragment!=null) YouChatApplication.addThemeFragment.atras();
                else if(actualFragment.get(0).getTag().equalsIgnoreCase("BandejaFragment") && YouChatApplication.bandejaFragment!=null) YouChatApplication.bandejaFragment.atras();
                else if(actualFragment.get(0).getTag().equalsIgnoreCase("ChatsActivityCorreo") && YouChatApplication.chatsActivityCorreo!=null) YouChatApplication.chatsActivityCorreo.onBackPressed();
                else if(actualFragment.get(0).getTag().equalsIgnoreCase("PublicarPostFragment") && YouChatApplication.publicarPostFragment!=null) YouChatApplication.publicarPostFragment.atras();
                else super.onBackPressed();
            }
            else super.onBackPressed();
        }
    }

    private void addFragment(Fragment fromFragment, Fragment toFragment) {
        if(Utils.ocultarKeyBoardEsperar(this)){
            Utils.runOnUIThread(()->{
                if(!getSupportFragmentManager().isDestroyed()){
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.move_right_in_activity, R.anim.move_left_out_activity, R.anim.move_left_in_activity, R.anim.move_right_out_activity)
                            .add(R.id.nav_host_fragment, toFragment, toFragment.getClass().getSimpleName())
                            .addToBackStack(toFragment.getClass().getSimpleName());
                    if(fromFragment!=null)
                        fragmentTransaction.hide(fromFragment);
                    fragmentTransaction.commit();
                }
            },300);
        }
        else {
            if(!getSupportFragmentManager().isDestroyed()){
                FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.move_right_in_activity, R.anim.move_left_out_activity, R.anim.move_left_in_activity, R.anim.move_right_out_activity)
                        .add(R.id.nav_host_fragment, toFragment, toFragment.getClass().getSimpleName())
                        .addToBackStack(toFragment.getClass().getSimpleName());
                if(fromFragment!=null)
                    fragmentTransaction.hide(fromFragment);
                fragmentTransaction.commit();
            }
        }
    }

    private void loadFragment(Fragment toFragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.nav_host_fragment, toFragment, toFragment.getClass().getSimpleName())
                .addToBackStack(toFragment.getClass().getSimpleName())
                .commit();
    }

    @Override
    public void onAddFragment(Fragment fromFragment, Fragment toFragment) {
        addFragment(fromFragment, toFragment);
    }

    @Override
    public void onAddFragment(Fragment fromFragment, Fragment toFragment, int enterAnim, int outAnim) {
        if(Utils.ocultarKeyBoardEsperar(this)){
            Utils.runOnUIThread(()->{
                if(!getSupportFragmentManager().isDestroyed()){
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(enterAnim, R.anim.fade_out_fast, R.anim.fade_in_fast, outAnim)
                            .add(R.id.nav_host_fragment, toFragment, toFragment.getClass().getSimpleName())
                            .addToBackStack(toFragment.getClass().getSimpleName());
                    if(fromFragment!=null)
                        fragmentTransaction.hide(fromFragment);
                    fragmentTransaction.commit();
                }
            },300);
        }
        else {
            if(!getSupportFragmentManager().isDestroyed()){
                FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(enterAnim, R.anim.fade_out_fast, R.anim.fade_in_fast, outAnim)
                        .add(R.id.nav_host_fragment, toFragment, toFragment.getClass().getSimpleName())
                        .addToBackStack(toFragment.getClass().getSimpleName());
                if(fromFragment!=null)
                    fragmentTransaction.hide(fromFragment);
                fragmentTransaction.commit();
            }
        }
    }

    public synchronized void mostrarDialogoErrorYouChat(ArrayList<String> mensajeError) {
        dbWorker.eliminarDescripcionesError();
        if(YouChatApplication.mostrarAvisoErrorApk){
            Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(1);
            View mview=getLayoutInflater().inflate(R.layout.dialog_confirm,null);
            dialog.setContentView(mview);

            View deleteForAll = mview.findViewById(R.id.deleteForAll);
            TextView text_icono = mview.findViewById(R.id.text_icono);
            TextView text_eliminar = mview.findViewById(R.id.text_eliminar);
            TextView btn_ok=mview.findViewById(R.id.btn_ok);
            View btn_cancel=mview.findViewById(R.id.btn_cancel);
            CheckBoxGI selectedForAll = mview.findViewById(R.id.selectedForAll);

            text_icono.setText("Alerta");
            deleteForAll.setVisibility(View.VISIBLE);
            selectedForAll.setText("No mostrar más este mensaje");

            text_eliminar.setText("Se ha detectado un error en la aplicación. " +
                    "Puede colaborar con el proyecto enviándonos junto al reporte " +
                    "más detalles acerca de lo que sucedió y si quiere un correo para contactarlo.");

            btn_ok.setText("ACEPTAR");

            ImageView icono_eliminar = mview.findViewById(R.id.icono_eliminar);
            icono_eliminar.setImageResource(R.drawable.danger);

            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if(selectedForAll.isChecked()) YouChatApplication.setMostrarAvisoErrorApk(false);
                    sacarBottomSheetError(mensajeError);
                }
            });
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if(selectedForAll.isChecked()) YouChatApplication.setMostrarAvisoErrorApk(false);
                }
            });

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.setCancelable(true);
            dialog.show();
        }
        else if(!(YouChatApplication.correo.equals("octaviog97@nauta.cu")
                || YouChatApplication.correo.equals("niuvis2019@nauta.cu")
                || YouChatApplication.correo.equals("alexgi@nauta.cu"))){
            String texto="#reporteError\n\n"+mensajeError.get(0)+"\n\n"+mensajeError.get(1);
            ItemChat msg=new ItemChat( "","");
            msg.setMensaje(texto);
            YouChatApplication.chatService.enviarMensaje(msg,SendMsg.CATEGORY_REPORTE_ERROR_TELEGRAM);
        }
    }

    private void sacarBottomSheetError(ArrayList<String> mensajeError) {
        BottomSheetDialog bottomSheetDialog= new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(R.layout.dialog_contactenos_reporte_error);

        TextView tv_descripcion_error_app = bottomSheetDialog.findViewById(R.id.tv_descripcion_error_app);
        tv_descripcion_error_app.setText(mensajeError.get(0));
        final EmojiEditText editext = bottomSheetDialog.findViewById(R.id.editext);
        MaterialCheckBox show_email = bottomSheetDialog.findViewById(R.id.show_email);

        View aceptar = bottomSheetDialog.findViewById(R.id.aceptar);

        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mensaje=editext.getText().toString().trim();
                if(YouChatApplication.estaAndandoChatService()
                        && YouChatApplication.chatService.hayConex){
                    bottomSheetDialog.dismiss();

                    String texto="#reporteError\n\n"+mensajeError.get(0)+"\n\n"+mensajeError.get(1)+"\n\n";
                    if(show_email.isChecked()) texto+=YouChatApplication.correo+"\n";
                    if(!mensaje.isEmpty()) texto+="Descripción del error:\n"+mensaje;

                    ItemChat msg=new ItemChat( "","");
                    msg.setMensaje(texto);
                    YouChatApplication.chatService.enviarMensaje(msg,SendMsg.CATEGORY_REPORTE_ERROR_TELEGRAM);
                    Utils.ShowToastAnimated(mainActivity,"Enviado correctamente",R.raw.contact_check);
                }
                else Utils.ShowToastAnimated(mainActivity,"Compruebe su conexión",R.raw.ic_ban);
            }
        });

        bottomSheetDialog.show();
        View bottomSheetInternal = bottomSheetDialog.findViewById(R.id.design_bottom_sheet);
        BottomSheetBehavior bsb = BottomSheetBehavior.from(bottomSheetInternal);
        bsb.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {}

    @Override
    protected void onStop() {
        super.onStop();
        estaMinimizada = true;
    }

    public void startIntentService() {
        if(!estaMinimizada)
            startService(new Intent(MainActivity.this, IntentServiceEstPostGlobales.class));
        else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                startService(new Intent(MainActivity.this, IntentServiceEstPostGlobales.class));
            }
        }
    }

    public void startChatService() {
        if(!estaMinimizada)
            startService(new Intent(MainActivity.this, ChatService.class));
        else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                startService(new Intent(MainActivity.this, ChatService.class));
            }
        }
    }
    public void exportarBaseDatos() {
        File sd = new File(YouChatApplication.RUTA_COPIA_BASE_DATOS);
        boolean exist = sd.exists();
        if(!exist)
            exist = sd.mkdirs();
        if(exist){
            String nombreBd = BDConstantes.NOMBRE_BASE_DATOS;
            try {
                File data = Environment.getDataDirectory();
                if (sd.canWrite()) {
                    String currentDBPath = "//data//"+getPackageName()+"//databases//"+nombreBd+"";
                    String backupDBPath = "YouChat_BDatos.dbyc";
                    File currentDB = new File(data, currentDBPath);
                    File backupDB = new File(sd, backupDBPath);
                    Utils.borrarFile(backupDB);
                    if (currentDB.exists()) {
                        String pass = Utils.MD5(YouChatApplication.correo+"YouChat");
                        if(pass!=null){
                            Utils.comprimirArchivo(currentDB,backupDB, pass);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}