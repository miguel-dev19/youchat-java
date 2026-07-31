package cu.alexgi.youchat;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.multidex.BuildConfig;
import androidx.multidex.MultiDexApplication;

import com.bumptech.glide.Glide;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.facebook.FacebookEmojiProvider;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumConfig;
import com.yanzhenjie.album.AlbumFile;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Properties;

import cu.alexgi.youchat.base_datos.DBWorker;
import cu.alexgi.youchat.chatUtils.jobmanager.JobManager;
import cu.alexgi.youchat.items.ItemDesImgPost;
import cu.alexgi.youchat.items.ItemFolderSticker;
import cu.alexgi.youchat.items.ItemTemas;

//import androidx.navigation.NavOptions;

public class YouChatApplication extends MultiDexApplication {

    public static final String idOficial = "youchatoficial";
    public static final String key_oficial = "youchatoficial20";
    public static final int usuMayor = 300;
    public static final int usuMedio = 200;
    public static final int usuMenor = 100;
    public static int anoActual;
    public static String fechaActual;
    public static boolean estaCorriendoPost = false, estaCorriendoBuzon = false;
    public static int cant_msg_inbox = 0;

    public static Context context;
    public static Handler applicationHandler;

    public static Bitmap imageFondoBlur = null;

    public static ArrayList<ItemDesImgPost> itemDesImgPosts;
    public static ArrayList<ItemFolderSticker> carpetasStickers;
    public static StickerManager stickerManager;

    public static ClipboardManager clipboard;
    public static Drawable icon_responder;
    public static int anchoPantalla;
    public static int largoPantalla;
    public static ArrayList<AlbumFile> imagenesChatAlbumFile;
    public static Intent intentReenviar;

    public static MainActivity mainActivity;
    public static ChatService chatService;
    public static BandejaFragment bandejaFragment;
    public static ContactActivity contactActivity;
    public static ContactFragmentCorreo contactFragmentCorreo;
    public static NuevoEstadoTextoActivity nuevoEstadoTextoActivity;
    public static ReenviarActivity reenviarActivity;
    public static ViewImageActivity viewImageActivity;
    public static HistorialPostFragment historialPostFragment;

    public static PrincipalActivity principalActivity;
    public static AddThemeFragment addThemeFragment;
    public static ChatsActivity chatsActivity;
    public static PublicarPostFragment publicarPostFragment;
    public static ChatsActivityCorreo chatsActivityCorreo;
    public static ViewYouPerfilActivity viewYouPerfilActivity;
    public static ViewPerfilActivity ViewPerfilActivity;

    public static Properties propsRecibir;
    public static Properties propsEnviar;

    private static ArrayList<String> listaUsuariosOficiales;

    private static ArrayList<String> correosEstadosPersonales;
    private static ArrayList<String> estadosEstadosPersonales;

    public static boolean estaAndandoChatService(){
        if(chatService!=null) return true;
        else {
            Utils.runOnUIThread(()->{
                if(mainActivity!=null)
                    mainActivity.startChatService();
            });
            return false;
        }
    }

    //rutas
    private static final String CARPETA_PRINCIPAL="YouChat";

    public static final String RUTA_COPIA_BASE_DATOS= Environment.getExternalStorageDirectory()
            +File.separator+CARPETA_PRINCIPAL+File.separator;

    public static String RUTA_STICKERS_CACHE = "";
    public static String RUTA_MULTIMEDIA_CACHE = "";
    public static String RUTA_TEMPORALES_CACHE = "";

    private static final String CARPETA_STICKERS="Stickers";
    private static final String DIRECTORIO_STICKERS=CARPETA_PRINCIPAL+ File.separator+CARPETA_STICKERS;
    public static final String RUTA_STICKERS= Environment.getExternalStorageDirectory() +File.separator+DIRECTORIO_STICKERS+File.separator;
    public static final String RUTA_STICKERS_RECIBIDOS= RUTA_STICKERS+"Stickers recibidos"+File.separator;
//    public static String RUTA_STICKERS_CACHE = RUTA_STICKERS+File.separator+"descomprimidos";

    private static final String CARPETA_IMAGEN_ENVIADA="Imágenes enviadas";
    private static final String DIRECTORIO_IMAGEN_ENVIADA=CARPETA_PRINCIPAL+ File.separator+CARPETA_IMAGEN_ENVIADA;

    public static final String RUTA_IMAGENES_ENVIADAS= Environment.getExternalStorageDirectory()
            +File.separator+DIRECTORIO_IMAGEN_ENVIADA+File.separator;

    private static final String CARPETA_AUDIOS_ENVIADOS="Audios enviados";
    private static final String DIRECTORIO_AUDIOS_ENVIADOS=CARPETA_PRINCIPAL+File.separator+CARPETA_AUDIOS_ENVIADOS;

    public static final String RUTA_AUDIOS_ENVIADOS= Environment.getExternalStorageDirectory()
            +File.separator+DIRECTORIO_AUDIOS_ENVIADOS+File.separator;

    private static final String CARPETA_IMAGEN_RECIBIDA="Imágenes recibidas";
    private static final String DIRECTORIO_IMAGEN_RECIBIDA=CARPETA_PRINCIPAL+File.separator+CARPETA_IMAGEN_RECIBIDA;

    public static final String RUTA_IMAGENES_RECIBIDA= Environment.getExternalStorageDirectory()
            +File.separator+DIRECTORIO_IMAGEN_RECIBIDA+File.separator;

    private static final String CARPETA_AUDIOS_RECIBIDOS="Audios recibidos";
    private static final String DIRECTORIO_AUDIOS_RECIBIDOS=CARPETA_PRINCIPAL+File.separator+CARPETA_AUDIOS_RECIBIDOS;

    public static final String RUTA_AUDIOS_RECIBIDOS= Environment.getExternalStorageDirectory()
            +File.separator+DIRECTORIO_AUDIOS_RECIBIDOS+File.separator;

    private static final String CARPETA_ARCHIVOS_RECIBIDOS="Archivos recibidos";
    private static final String DIRECTORIO_ARCHIVOS_RECIBIDOS=CARPETA_PRINCIPAL+File.separator+CARPETA_ARCHIVOS_RECIBIDOS;

    public static final String RUTA_ARCHIVOS_RECIBIDOS= Environment.getExternalStorageDirectory()
            +File.separator+DIRECTORIO_ARCHIVOS_RECIBIDOS+File.separator;

    private static final String CARPETA_IMAGEN_PERFIL=".Imágenes de prefil";
    private static final String DIRECTORIO_IMAGEN_PERFIL=CARPETA_PRINCIPAL+File.separator+CARPETA_IMAGEN_PERFIL;

    public static final String RUTA_IMAGENES_PERFIL= Environment.getExternalStorageDirectory()
            +File.separator+DIRECTORIO_IMAGEN_PERFIL+File.separator;

    private static final String CARPETA_FONDO_YOUCHAT="Fondos YouChat";
    private static final String DIRECTORIO_FONDO_YOUCHAT=CARPETA_PRINCIPAL+ File.separator+CARPETA_FONDO_YOUCHAT;

    public static final String RUTA_FONDO_YOUCHAT= Environment.getExternalStorageDirectory()
            +File.separator+DIRECTORIO_FONDO_YOUCHAT+File.separator;

    private static final String CARPETA_ADJUNTOS_CORREO="Adjuntos correo";
    private static final String DIRECTORIO_ADJUNTOS_CORREO=CARPETA_PRINCIPAL+ File.separator+CARPETA_ADJUNTOS_CORREO;

    public static final String RUTA_ADJUNTOS_CORREO= Environment.getExternalStorageDirectory()
            +File.separator+DIRECTORIO_ADJUNTOS_CORREO+File.separator;

//    private static final String CARPETA_CACHE=".CACHE";
//    private static final String DIRECTORIO_CACHE=CARPETA_PRINCIPAL+File.separator+CARPETA_CACHE;
//    public static final String RUTA_CACHE= Environment.getExternalStorageDirectory() +File.separator+DIRECTORIO_CACHE+File.separator;

    private static final String CARPETA_ESTADOS="Estados guardados";
    public static final String RUTA_ESTADOS_GUARDADOS = RUTA_IMAGENES_PERFIL+CARPETA_ESTADOS+File.separator;

    //preferencias MEMORIA
    public static String decod = "";
    private static SharedPreferences preferencias;
    public static String descripEscanerBandeja, pieDeFirma, idPosUltPostVisto, IDTemaClaro, IDTemaOscuro, alias, info, correo, pass, telefono, genero,
            provincia, fecha_nacimiento, ruta_img_perfil, ruta_fondo_chat, fechaHoy;
    public static int opcionCopiaSeguridadAutomatica,
            opcionVaciadoAutomaticoMensajes, posVistaBandeja, maxLines, version_info, calidad,
            ruta_drawable, temaApp, mark, version_bd,
            cant_seguidores, curvaGlobosChat, curvaChat, tam_fuente, cantLimitePost, tiempo_progreso,
            cantPostSubidosHoy, cantComentarioPostSubidosHoy;
    public static long tam_max_descarga_correo, tam_max_descarga_now, tam_max_descarga_chat;
    public static boolean addPieFirmaAChat, convertirCorreosMDenGrupos, descargarMsjDeltaLab,
            descargarMsjDimelo, descargarMsjLetterSocial;
    public static boolean notiMenChat, notiCorreoEnt, notiNowEnt, notiReacNow,
            activeEmojisAnimChat, descargaAutMensajesCorreo, descargaAutImagenNow,  descargaAutMultimediaChat, activarBuzon, invertirResChat, limitarPost,
            enviarEnter, orden_contacto_nombre, lectura, notificacion, sonido, activePost,
            activePostDesImg, activePostHistorial, activePostBorrar24, chat_security,
            actualizar_perfil, animaciones_chat, estado_personal, activePerfilPub,
            activar_progreso;
    public static boolean puedeHacerCopiaSeguridad, puedeVaciarBandeja, burbuja_datos, es_beta_tester, avisar_en_linea,
            esta_segundo_plano, son_privados_estados;
    public static boolean hacerBlurFondo, mostrarAvisoOptBatery, mostrarAvisoErrorApk,
            avisar_union_yc, pedir_confirmacion_avisar_union, puedeSubirPost;

    //ESTADISTICAS GRAL
    public static int nivelDifuminadoBlur, cant_buzon_rye, cant_estados_subidos, cant_post_rye, cant_act_perfil_env, cant_confir_lectura,
            cant_reacciones, cant_vistos_estados, cant_aviso_en_linea, cant_bd_nube, cant_chat_din;
    public static long mega_buzon_recibidos, mega_buzon_enviados, mega_estados_subidos, mega_post_recibidos, mega_post_enviados, mega_act_perfil_env,
            mega_reacciones_env, mega_reacciones_rec, mega_vistos_estados_env, mega_vistos_estados_rec;
    public static long mega_x_serv_confirmacion_lectura_env, mega_x_serv_confirmacion_lectura_rec, mega_x_serv_chat_dinamico_env, mega_x_serv_chat_dinamico_rec,
            mega_x_serv_aviso_en_linea_env, mega_x_serv_aviso_en_linea_rec, mega_x_serv_bd_nube_env, mega_x_serv_bd_nube_rec;

    public static long consumoSubida, consumoBajada;

    public static boolean felicidades_fin_ano, felicidades_ano_nuevo,
            felicidades_dia_mujer, felicidades_14_febrero,
            felicidades_cumpleanos, felicidades_noche_buena,
            felicidades_halloween;
    public static String fecha_cumpleanos, idUltCorreoBuzonRecibido, idUltEstGlobalRecibido, idUltChatGlobalRecibido,
            idUltPostGlobalRecibido, idUltComentarioPostGlobalRecibido, idUltPostGlobalRecibidoPrueba;

    public static ItemTemas itemTemas;
    public static int colorTemaActual, estiloActual, progressEscanerBandeja;

    private JobManager jobManager;
//    public static NavOptions.Builder optionsIzqaDer;

    public synchronized static void obtenerColorAccento() {
        colorTemaActual=R.color.defectoAccent;
        estiloActual=R.style.AppTheme;
        if(itemTemas!=null){
//            Log.e("COLOR",""+itemTemas.getColor_accento());
            switch (itemTemas.getColor_accento().toLowerCase()){

                case "#ffb71c1c": colorTemaActual=R.color.temaRojoAccent;
                    estiloActual = R.style.ThemeRojo; break;

                case "#ffd81b60": colorTemaActual=R.color.temaRosadoAccent;
                    estiloActual = R.style.ThemeRosado; break;

                case "#ff039be5": colorTemaActual=R.color.temaAzulCelesteAccent;
                    estiloActual = R.style.ThemeAzulceleste; break;

                case "#ff43a047": colorTemaActual=R.color.temaVerdeAccent;
                    estiloActual = R.style.ThemeVerde; break;

                case "#fffb8c00": colorTemaActual=R.color.temaNaranjaAccent;
                    estiloActual = R.style.ThemeNaranja; break;

                case "#ff886558": colorTemaActual=R.color.temaMarronAccent;
                    estiloActual = R.style.ThemeMarron; break;

                case "#ff9c27b0": colorTemaActual=R.color.temaPurple;
                    estiloActual = R.style.ThemePurple; break;

                case "#ff673ab7": colorTemaActual=R.color.temaDeep_purple;
                    estiloActual = R.style.ThemeDeep_purple; break;

                case "#ff00bcd4": colorTemaActual=R.color.temaCyan;
                    estiloActual = R.style.ThemeCyan; break;

                case "#ff009688": colorTemaActual=R.color.temaTeal;
                    estiloActual = R.style.ThemeTeal; break;

                case "#ff4caf50": colorTemaActual=R.color.temaGreen;
                    estiloActual = R.style.ThemeGreen; break;

                case "#ff8bc34a": colorTemaActual=R.color.temaLight_green;
                    estiloActual = R.style.ThemeLight_green; break;

                case "#ffffeb3b": colorTemaActual=R.color.temaYellow;
                    estiloActual = R.style.ThemeYellow; break;

                case "#ff607d8b": colorTemaActual=R.color.temaBlue_grey;
                    estiloActual = R.style.ThemeBlue_grey; break;
            }
        }

//        switch (){
//            case 1:
//            case 2:
//            case 3:
//            case 4:
//            case 5:
//            case 6:
//
//            case 7:
//            case 8:
//            case 9:
//            case 10:
//            case 11:
//            case 12:
//            case 13:
//            case 14:
//        }
//        return colorTema;
    }

    public static void ponerIconOficial(ImageView imageView) {
        Glide.with(context).load(R.drawable.iconycoficial).into(imageView);
//        imageView.setImageResource(R.drawable.iconycoficial);
    }

    public static void resetearCantPostSubidosHoy() {
        cantPostSubidosHoy = preferencias.getInt("cantPostSubidosHoy", 0);
        cantComentarioPostSubidosHoy = preferencias.getInt("cantComentarioPostSubidosHoy", 0);
    }

    public static String obtenerStringInfoPerfil() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("alias",alias);
            jsonObject.put("info",info);
            jsonObject.put("telefono",telefono);
            jsonObject.put("genero",genero);
            jsonObject.put("provincia",provincia);
            jsonObject.put("fecha_nac",fecha_nacimiento);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

//  public static void guardarMsgEnviar(ItemChat msg){
//    msg_reenviar=msg;
//  }

    public JobManager getJobManager() {
        return jobManager;
    }

    private void initializeJobManager() {
        this.jobManager = JobManager.newBuilder(this)
                .withName("TextSecureJobs")
                .withConsumerThreads(5)
                .build();
    }

    public static void startPushService() {
        try {
            context.startService(new Intent(context, ChatService.class));
        } catch (Throwable ignore) {}
    }

    @Override
    public void onCreate() {
        super.onCreate();

        EmojiManager.install(new FacebookEmojiProvider());
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().build());
        }
        context=this;
        RUTA_STICKERS_CACHE = getFilesDir().getAbsolutePath()+File.separator+DIRECTORIO_STICKERS;
        RUTA_MULTIMEDIA_CACHE = getFilesDir().getAbsolutePath()+File.separator+DIRECTORIO_ARCHIVOS_RECIBIDOS;
        RUTA_TEMPORALES_CACHE = getFilesDir().getAbsolutePath()+File.separator+".temp"+File.separator;

        obtenerAno_y_FechaActual();

        initializeJobManager();
        preferencias=getSharedPreferences("Memoria", Context.MODE_PRIVATE);
        cargarPreferencias();


        Thread.setDefaultUncaughtExceptionHandler(new FeedBack(this,correo));

        initAll();

        applicationHandler = new Handler(getMainLooper());

        chatService=null;
        icon_responder = ContextCompat.getDrawable(this,R.drawable.icon_chat_answer2);
        anchoPantalla=this.getResources().getDisplayMetrics().widthPixels;
        largoPantalla=this.getResources().getDisplayMetrics().heightPixels;
        decod = Utils.decrypt(ViewYouPerfilActivity.getCad()+"ËÞòòÓ"+ReenviarActivity.getCad());

        clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        Album.initialize(AlbumConfig.newBuilder(this)
                .setAlbumLoader(new MediaLoader())
                .setLocale(Locale.getDefault())
                .build()
        );

        actualizarPropiedades();

        itemDesImgPosts = new ArrayList<>();

        //manejo de sticker
        carpetasStickers = new ArrayList<>();
        stickerManager = new StickerManager(this);
        procesarSticker();

        listaUsuariosOficiales=new ArrayList<>();
        listaUsuariosOficiales.add("youchatoficial");
        listaUsuariosOficiales.add("youchat@nauta.cu");
        listaUsuariosOficiales.add("octaviog97@nauta.cu");
        listaUsuariosOficiales.add("alexgi@nauta.cu");
        listaUsuariosOficiales.add("niuvis2019@nauta.cu");
        listaUsuariosOficiales.add("fidel.legra@nauta.cu");
        listaUsuariosOficiales.add("liannega@nauta.cu");
        listaUsuariosOficiales.add("niuvis.corbacho@nauta.cu");
        listaUsuariosOficiales.add("naty001226@nauta.cu");
        listaUsuariosOficiales.add("s.aguilar@nauta.cu");
        listaUsuariosOficiales.add("viviana.aldana@nauta.cu");
        listaUsuariosOficiales.add("orly2018@nauta.cu");
        listaUsuariosOficiales.add("97rey@nauta.cu");

        //Beta testers
        listaUsuariosOficiales.add("ernesto.chiong@nauta.cu");//chiong
        listaUsuariosOficiales.add("raidelmis@nauta.cu");//yordi
        listaUsuariosOficiales.add("cristianjbc@nauta.cu");
        listaUsuariosOficiales.add("miele1611@nauta.cu");
        listaUsuariosOficiales.add("letania.leto@nauta.cu");
        listaUsuariosOficiales.add("ordiel2005@nauta.cu");
        listaUsuariosOficiales.add("wiliam2016@nauta.cu");
        listaUsuariosOficiales.add("yordanisgils@gmail.com");
        listaUsuariosOficiales.add("cristianpena06@nauta.cu");
        listaUsuariosOficiales.add("santor@nauta.cu");
        listaUsuariosOficiales.add("claudia999@nauta.cu");
        listaUsuariosOficiales.add("arte.alexeylopez@nauta.cu");
        listaUsuariosOficiales.add("danielmilian96@nauta.cu");
        listaUsuariosOficiales.add("mikepacheco@nauta.cu");
        listaUsuariosOficiales.add("joelm93@nauta.cu");
        listaUsuariosOficiales.add("betocv@nauta.cu");
        listaUsuariosOficiales.add("darieldlinares@nauta.cu");
        listaUsuariosOficiales.add("djbola@nauta.cu");//raul letterSocial
        listaUsuariosOficiales.add("anthonyruiz@nauta.cu");//tony
        listaUsuariosOficiales.add("darimelody@nauta.cu");
        listaUsuariosOficiales.add("maryanis.noval@nauta.cu");//samuel
    }


    public synchronized static void initAll(){
        DBWorker dbWorker = new DBWorker(context);
//        dbWorker.eliminarTodosTema();
        if(!dbWorker.existeTemaId("4o")){
            dbWorker.insertarNuevoTema(new ItemTemas("1c", "YouChat", 1, false, "", "YouChat",
                    "#FFFFFFFF", "#FF3F51B5", "#FFFFFFFF", "#FF200e32",
                    "#FFD3D3D3", "#FF3F51B5", "#FFE6E6E6", "#66000000",
                    "#FF3F51B5", "#FF3F51B5", "#FF200e32", "#FFFFFFFF",
                    "#FF200e32", "#FFFFFFFF", "#FF3F51B5", "#FFFFFFFF",
                    "#E6272727", "#FF3F51B5", "#FFFFFFFF", "#FFFFFFFF",
                    "#FF200e32", "#4D74C31A", Utils.obtenerOscuroDe("#FFFFFFFF")));

            dbWorker.insertarNuevoTema(new ItemTemas("2c", "WhatsUp", 1, false, "", "YouChat",
                    "#ff075e54", "#ff00cc3f", "#ffffffff", "#ff19262d",
                    "#FFD3D3D3", "#ffffffff", "#ffe1ffc7", "#ffd4eaf4",
                    "#FF009688", "#ff6cb3a7", "#ffffffff", "#ff212121",
                    "#ff1d211a", "#ff555e62", "#FF009688", "#FFFFFFFF",
                    "#E6272727", "#ff075e54", "#ffffffff", "#ffffffff",
                    "#FF200e32", "#66b6d8e0", Utils.obtenerOscuroDe("#ff075e54")));

            dbWorker.insertarNuevoTema(new ItemTemas("3c", "Telegrama", 1, false, "", "YouChat",
                    "#ff527da3", "#ff65a9e0", "#fff7f7f7", "#ff3f3f3f",
                    "#FFD3D3D3", "#ffffffff", "#ffefffde", "#66a8b5c0",
                    "#FF00bcd4", "#ff8d9597", "#ffffffff", "#ff000000",
                    "#ff000000", "#ffffffff", "#FF00bcd4", "#FFFFFFFF",
                    "#E6272727", "#ff527da3", "#ffffffff", "#ffffffff",
                    "#FF200e32", "#66aecce3", Utils.obtenerOscuroDe("#ff527da3")));

            dbWorker.insertarNuevoTema(new ItemTemas("4c", "RedBird", 1, false, "", "YouChat",
                    "#ffb71c1c", "#ffb71c1c", "#ffffffff", "#ff17262a",
                    "#FFD3D3D3", "#ffffffff", "#fffff9da", "#66000000",
                    "#FFB71C1C", "#ffb71c1c", "#ffffffff", "#ff17262a",
                    "#ff17262a", "#ffffffff", "#FFB71C1C", "#FFFFFFFF",
                    "#E6272727", "#ffb71c1c", "#ffffffff", "#ffffffff",
                    "#FF200e32", "#66db8e88", Utils.obtenerOscuroDe("#ffb71c1c")));

            dbWorker.insertarNuevoTema(new ItemTemas("1o", "YouChat", 1, true, "", "YouChat",
                    "#ff242736", "#ff373B6F", "#ff242736", "#ffBDBDBD",
                    "#ff1B1919", "#ff373B6F", "#FF242736", "#66000000",
                    "#ff373B6F", "#ff373B6F", "#ffBDBDBD", "#FFFFFFFF",
                    "#ffBDBDBD", "#FFFFFFFF", "#FF3F51B5", "#ff242736",
                    "#E6272727", "#ff373B6F", "#ffBDBDBD", "#ff242736",
                    "#ffBDBDBD", "#4D74C31A", Utils.obtenerOscuroDe("#ff242736")));

//            //
//            dbWorker.insertarNuevoTema(new ItemTemas("1c", "YouChat", 1, false, "", "YouChat",
//                    "#FFFFFFFF", "#FF3F51B5", "#FFFFFFFF", "#FF200e32",
//                    "#FFD3D3D3", "#FF3F51B5", "#FFE6E6E6", "#66000000",
//                    "#FF3F51B5", "#FF3F51B5", "#FF200e32", "#FFFFFFFF",
//                    "#FF200e32", "#FFFFFFFF", "#FF3F51B5", "#FFFFFFFF",
//                    "#E6272727", "#FF3F51B5", "#FFFFFFFF", "#FFFFFFFF",
//                    "#FF200e32", "#4D74C31A"));//

            dbWorker.insertarNuevoTema(new ItemTemas("2o", "WhatsUp", 1, true, "", "YouChat",
                    "#ff222d36", "#ff00af9c", "#ff101d24", "#ffd4d6d7",
                    "#ff00221f", "#ff222e35", "#ff054740", "#ff1e2a30",
                    "#FF009688", "#ff979ca0", "#ffffffff", "#ffd6d8da",
                    "#ffd2dbdb", "#ff9da2a5", "#FF009688", "#ff242736",
                    "#E6272727", "#ff222d36", "#ffffffff", "#ff2c373d",
                    "#ffa2a7aa", "#66094547", Utils.obtenerOscuroDe("#ff222d36")));

            dbWorker.insertarNuevoTema(new ItemTemas("3o", "Telegrama", 1, true, "", "YouChat",
                    "#ff212d3b", "#ff5fa3de", "#ff1d2733", "#fff5f6f6",
                    "#ff161c1f", "#ff232e3b", "#ff3e618a", "#6625303c",
                    "#FF607d8b", "#ff848a96", "#ffffffff", "#fffafafa",
                    "#fffafafa", "#ffffffff", "#FF607d8b", "#ff242736",
                    "#E6272727", "#ff212d3b", "#ffffffff", "#ff212d3b",
                    "#ff6a7d90", "#66213346", Utils.obtenerOscuroDe("#ff212d3b")));

            dbWorker.insertarNuevoTema(new ItemTemas("4o", "RedBird", 1, true, "", "YouChat",
                    "#ff121212", "#ff823d3b", "#ff121212", "#ffe0e0e0",
                    "#ff1B1919", "#ff23343e", "#ff823c36", "#663f2e2e",
                    "#FFB71C1C", "#ff823d3b", "#ffffffff", "#ffe3e5e6",
                    "#ffefe6e5", "#ffffffff", "#FFB71C1C", "#ff242736",
                    "#E6272727", "#ff121212", "#ffffffff", "#ff23343e",
                    "#ffadadad", "#665c1717", Utils.obtenerOscuroDe("#ff121212")));
        }
        if(temaApp==1) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            itemTemas = dbWorker.obtenerTema(IDTemaOscuro, true);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            itemTemas = dbWorker.obtenerTema(IDTemaClaro, false);
        }
//        if(!itemTemas.temaCorrecto()){
//            dbWorker.eliminarTema(itemTemas.getId());
//            if(temaApp==1) itemTemas = dbWorker.obtenerTema("1c", true);
//            else itemTemas = dbWorker.obtenerTema("1o", false);
//            obtenerColorAccento();
//        }
//        else
        obtenerColorAccento();

        consumoSubida=0;
        consumoBajada=0;

        principalActivity=null;
        addThemeFragment=null;
        chatsActivity=null;
        viewYouPerfilActivity=null;

        correosEstadosPersonales=new ArrayList<>();
        estadosEstadosPersonales=new ArrayList<>();
    }

    public static YouChatApplication getInstance(Context context) {
        return (YouChatApplication) context.getApplicationContext();
    }

    private static void actualizarPropiedades(){
        propsEnviar = new Properties();
        if(correo.endsWith("@nauta.cu")){
            propsEnviar.put("mail.smtp.host", "smtp.nauta.cu");
            propsEnviar.put("mail.smtp.auth", "true");
            propsEnviar.put("mail.smtp.port", "25");
        }
        else if(correo.endsWith("@gmail.com")){
            propsEnviar.put("mail.smtp.host", "smtp.gmail.com");
            propsEnviar.put("mail.smtp.socketFactory.port", "465");
            propsEnviar.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            propsEnviar.put("mail.smtp.auth", "true");
            propsEnviar.put("mail.smtp.port", "465");
        }
        else if(correo.endsWith("@mail.com")){
            propsEnviar.put("mail.smtp.host", "smtp.mail.com");
            propsEnviar.put("mail.smtp.socketFactory.port", "587");
            propsEnviar.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            propsEnviar.put("mail.smtp.auth", "true");
            propsEnviar.put("mail.smtp.port", "587");
        }
        else if(correo.endsWith("@mail.ru")){
            propsEnviar.put("mail.smtp.host", "smtp.mail.ru");
            propsEnviar.put("mail.smtp.socketFactory.port", "465");
            propsEnviar.put("mail.smtp.port", "465");
        }
        else if(correo.endsWith("@yahoo.com")){
            propsEnviar.put("mail.smtp.host", "smtp.mail.yahoo.com");
            propsEnviar.put("mail.smtp.socketFactory.port", "587");
            propsEnviar.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            propsEnviar.put("mail.smtp.auth", "true");
            propsEnviar.put("mail.smtp.port", "587");
        }
        else if(correo.endsWith("@hotmail.com")){
            propsEnviar.put("mail.smtp.host", "smtp-mail.outlook.com");
            propsEnviar.put("mail.smtp.socketFactory.port", "587");
            propsEnviar.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            propsEnviar.put("mail.smtp.port", "587");
        }
        else if(correo.endsWith("@enpa.gtm.minag.cu")) {
            propsEnviar.put("mail.smtp.host", "server.enpa.gtm.minag.cu");
            propsEnviar.put("mail.smtp.starttls.enable", "true");
//            propsEnviar.put("mail.smtp.socketFactory.port", "587");
//            propsEnviar.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            propsEnviar.put("mail.smtp.port", "25");
        }
        else if(correo.endsWith("@gid.enpa.minag.cu")) {
            propsEnviar.put("mail.smtp.host", "smtp.gid.enpa.minag.cu");
            propsEnviar.put("mail.smtp.auth", "false");
            propsEnviar.put("mail.smtp.port", "25");
        }

        /*MAIL.COM

pop.mail.com   995
SSl/TLS "sí"
smtp.mail.com 587
Encrypted Method: STARTLS

imap.mail.com  993
SSL/TLS
smtp.mail.com  587

*************************************************
MAIL.RU

pop3.mail.ru 995
imap.mail.ru 993
smtp.mail.ru 465

************************************************
YAHOO.COM

imap.mail.yahoo.com 993
smtp.mail.yahoo.com 465 o 587
SSL sí
autenticación sí

pop.mail.yahoo.com 995

**************************************************
HOTMAIL

IMAP
Server: imap-mail.outlook.com
SSL: true-implicit
Port: 993 (default)
User: pat@hotmail.com

Server: pop-mail.outlook.com
SSL: true-implicit
Port: 995 (default)
User: pat@hotmail.com

Server: smtp-mail.outlook.com
SSL: true-explicit
Port: 587 (default)
User: pat@hotmail.com
***********************************************************

AOL.COM

Protocol 	Server Settings	Port Settings
POP 3	 Incoming mail server (POP3): pop.aol.com
 Outgoing mail server (SMTP): smtp.aol.com	POP3-995-SSL
SMTP-465-SSL

IMAP	 Incoming mail server (IMAP): imap.aol.com
 Outgoing mail server (SMTP): smtp.aol.com	IMAP-993-SSL
SMTP-465-SSL*/

        propsRecibir = new Properties();
        if(correo.endsWith("@nauta.cu")){
//      propsRecibir.setProperty("mail.store.protocol", "pop3");
//      propsRecibir.setProperty("mail.pop3.host", "pop.nauta.cu");
//      propsRecibir.setProperty("mail.pop3.port", "110");

            propsRecibir.setProperty("mail.store.protocol", "imap");
            propsRecibir.setProperty("mail.imap.host", "imap.nauta.cu");
            propsRecibir.setProperty("mail.imap.port", "143");
        }
        else if(correo.endsWith("@gmail.com")){
          propsRecibir.setProperty("mail.imap.starttls.enable", "false");
          propsRecibir.setProperty("mail.imap.socketFactory.class","javax.net.ssl.SSLSocketFactory");
          propsRecibir.setProperty("mail.imap.socketFactory.fallback", "false");
          propsRecibir.setProperty("mail.imap.port","993");
          propsRecibir.setProperty("mail.imap.socketFactory.port", "993");

//            props.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//            props.setProperty("mail.imap.socketFactory.fallback", "false");
//            props.setProperty("mail.imap.host", "imap.gmail.com");
//            props.setProperty("mail.imap.port", "993");
//            props.setProperty("mail.imap.connectiontimeout", "5000");
//            props.setProperty("mail.imap.timeout", "5000");

//            "mail.imaps.host" : "imap.gmail.com"
//            "mail.store.protocol" : "imaps"
//            "mail.imaps.port" : "993"

//            props.setProperty("mail.imaps.host", "imap.gmail.com");
//            props.setProperty("mail.imaps.port", "993");
//            props.setProperty("mail.imaps.connectiontimeout", "5000");
//            props.setProperty("mail.imaps.timeout", "5000");

            //////////////////////////////////////////
//      inputProtocol = "imap";
//      inputHost = "imap.gmail.com";
//      inputPort = "993";
//      inputAuth = "true";
//      inputSsl = "true";

//      propsRecibir.setProperty("mail.store.protocol", "imap");
//      propsRecibir.setProperty("mail.imap.host", "imap.gmail.com");
//      propsRecibir.setProperty("mail.imap.port", "993");

            /*propsRecibir.setProperty("mail.pop3.starttls.enable", "false");
            propsRecibir.setProperty("mail.pop3.socketFactory.class","javax.net.ssl.SSLSocketFactory" );
            propsRecibir.setProperty("mail.pop3.socketFactory.fallback", "false");
            propsRecibir.setProperty("mail.pop3.port","995");
            propsRecibir.setProperty("mail.pop3.socketFactory.port", "995");*/
        }
        else if(correo.endsWith("@mail.com")) {
            propsRecibir.setProperty("mail.store.protocol", "imap");
            propsRecibir.setProperty("mail.imap.host", "imap.mail.com");
            propsRecibir.setProperty("mail.imap.port", "993");
        }
        else if(correo.endsWith("@mail.ru")) {
            propsRecibir.setProperty("mail.store.protocol", "imap");
            propsRecibir.setProperty("mail.imap.host", "imap.mail.ru");
            propsRecibir.setProperty("mail.imap.port", "993");
        }
        else if(correo.endsWith("@yahoo.com")) {
            propsRecibir.setProperty("mail.store.protocol", "imap");
            propsRecibir.setProperty("mail.imap.host", "imap.mail.yahoo.com");
            propsRecibir.setProperty("mail.imap.port", "993");
        }
        else if(correo.endsWith("@hotmail.com")) {
            propsRecibir.setProperty("mail.store.protocol", "imap");
            propsRecibir.setProperty("mail.imap.host", "imap-mail.outlook.com");
            propsRecibir.setProperty("mail.imap.port", "993");
        }
        else if(correo.endsWith("@enpa.gtm.minag.cu")) {
            propsRecibir.setProperty("mail.store.protocol", "imap");
            propsRecibir.setProperty("mail.imap.auth", "false");
            propsRecibir.setProperty("mail.imap.host", "server.enpa.gtm.minag.cu");
            propsRecibir.setProperty("mail.imap.port", "143");
//            propsRecibir.setProperty("mail.imap.socketFactory.class","javax.net.ssl.SSLSocketFactory");
        }
        else if(correo.endsWith("@gid.enpa.minag.cu")) {
            propsRecibir.setProperty("mail.store.protocol", "imap");
            propsRecibir.put("mail.imap.host", "imap.gid.enpa.minag.cu");
            propsRecibir.put("mail.imap.auth", "true");
            propsRecibir.put("mail.imap.port", "143");
            propsRecibir.setProperty("mail.imap.socketFactory.port", "143");
            propsRecibir.setProperty("mail.imap.starttls.enable", "true");
            propsRecibir.setProperty("mail.imap.socketFactory.class","javax.net.ssl.SSLSocketFactory");
            propsRecibir.setProperty("mail.imap.socketFactory.fallback", "false");
        }

    }

    public static synchronized void agregarCorreoyEstadoPersonal(String correoUser, String estadoUser){
        if(correoUser.equals("") || estadoUser.equals("")) return;
        correosEstadosPersonales.add(correoUser);
        estadosEstadosPersonales.add(estadoUser);
    }

    public static String obtenerEstadoPersonalSiExisteDe(String correoUser){
        int l=correosEstadosPersonales.size();
        for(int i=0; i<l; i++){
            if(correoUser.equals(correosEstadosPersonales.get(i))){
                return estadosEstadosPersonales.get(i);
            }
        }
        return "";
    }

    public static boolean hayEstadosPersonales(){
        return correosEstadosPersonales.size()>0;
    }

    public static int tipoEstadoPrimero(){
        if(correosEstadosPersonales.size()==0) return 0;
        if(estadosEstadosPersonales.get(0).equals("1")) return 1;
        if(estadosEstadosPersonales.get(0).equals("2")) return 2;
        return 0;
    }

    public static void eliminarEstadoPersonalSiExisteDe(String correoUser){
        int l=correosEstadosPersonales.size();
        for(int i=0; i<l; i++){
            if(correoUser.equals(correosEstadosPersonales.get(i))){
                correosEstadosPersonales.remove(i);
                estadosEstadosPersonales.remove(i);
                return;
            }
        }
    }

    public static boolean eliminarEstadoPersonalDelPrimero(){
        if(correosEstadosPersonales.size()>0){
            correosEstadosPersonales.remove(0);
            estadosEstadosPersonales.remove(0);
            return true;
        }
        return false;
    }

    public static String obtenerCorreoEstadoPersonalDelPrimero(){
        if(correosEstadosPersonales.size()>0){
            return correosEstadosPersonales.get(0);
        }
        return "";
    }

    /*public synchronized static NavOptions TransIzqaDer(){
        return optionsIzqaDer.build();
    }*/

    public synchronized static boolean comprobarOficialidad(String correo) {
        int l = listaUsuariosOficiales.size();
        for (int i = 0; i < l; i++)
            if (listaUsuariosOficiales.get(i).equals(correo))
                return true;

        return false;
    }

    public static void cargarPreferencias() {
        maxLines = preferencias.getInt("maxLines",2);
        alias = preferencias.getString("alias", "");
        correo = preferencias.getString("correo", "");
        pass = preferencias.getString("pass", "");
        calidad = preferencias.getInt("calidad", 20);
        ruta_img_perfil = preferencias.getString("ruta_img_perfil", "");
        info = preferencias.getString("info", "");
        telefono = preferencias.getString("telefono", "");
        genero = preferencias.getString("genero", "");
        provincia = preferencias.getString("provincia", "");
        fecha_nacimiento = preferencias.getString("fecha_nacimiento", "");
        cant_seguidores = preferencias.getInt("cant_seguidores",0);
        version_info = preferencias.getInt("version_info", 1);
        orden_contacto_nombre = preferencias.getBoolean("orden_contacto_nombre", true);
        enviarEnter = preferencias.getBoolean("enviarEnter", false);
        limitarPost = preferencias.getBoolean("limitarPost", true);
        invertirResChat = preferencias.getBoolean("invertirResChat", false);
        activarBuzon = preferencias.getBoolean("activarBuzon", false);
        descargaAutMensajesCorreo = preferencias.getBoolean("descargaAutMensajesCorreo", false);
        descargaAutImagenNow = preferencias.getBoolean("descargaAutImagenNow", false);
        descargaAutMultimediaChat = preferencias.getBoolean("descargaAutMultimediaChat", false);
        puedeSubirPost= preferencias.getBoolean("puedeSubirPost", true);

        addPieFirmaAChat= preferencias.getBoolean("addPieFirmaAChat", false);
        convertirCorreosMDenGrupos= preferencias.getBoolean("convertirCorreosMDenGrupos", false);
        descargarMsjDeltaLab= preferencias.getBoolean("descargarMsjDeltaLab", true);
        descargarMsjDimelo= preferencias.getBoolean("descargarMsjDimelo", true);
        descargarMsjLetterSocial= preferencias.getBoolean("descargarMsjLetterSocial", true);

        ruta_fondo_chat = preferencias.getString("ruta_fondo_chat", "");
        ruta_drawable = preferencias.getInt("ruta_drawable", 0);
        temaApp = preferencias.getInt("temaApp", 0);
        posVistaBandeja = preferencias.getInt("posVistaBandeja", 1);
        opcionVaciadoAutomaticoMensajes = preferencias.getInt("opcionVaciadoAutomaticoMensajes", 1);
        opcionCopiaSeguridadAutomatica = preferencias.getInt("opcionCopiaSeguridadAutomatica", 1);

        idPosUltPostVisto = preferencias.getString("idPosUltPostVisto", "");
        descripEscanerBandeja = preferencias.getString("descripEscanerBandeja", "Toque el botón para hacer su primer escaneo");
        pieDeFirma = preferencias.getString("pieDeFirma", "Enviado desde mi aplicación de mensajería YouChat ;)");

        IDTemaClaro = preferencias.getString("IDTemaClaro", "1c");
        IDTemaOscuro = preferencias.getString("IDTemaOscuro", "1o");

        mark=preferencias.getInt("mark",0);
        lectura=preferencias.getBoolean("lectura",true);
        notificacion=preferencias.getBoolean("notificacion",true);
        sonido=preferencias.getBoolean("sonido",true);
        actualizar_perfil=preferencias.getBoolean("actualizar_perfil",true);
        chat_security=preferencias.getBoolean("chat_security",true);
        activePost=preferencias.getBoolean("activePost",true);
        activePostDesImg=preferencias.getBoolean("activePostDesImg",false);
        activePostBorrar24=preferencias.getBoolean("activePostBorrar24",false);

        notiMenChat=preferencias.getBoolean("notiMenChat",true);
        notiCorreoEnt=preferencias.getBoolean("notiCorreoEnt",true);
        notiNowEnt=preferencias.getBoolean("notiNowEnt",true);
        notiReacNow=preferencias.getBoolean("notiReacNow",true);

        activeEmojisAnimChat=preferencias.getBoolean("activeEmojisAnimChat",true);
        activePerfilPub=preferencias.getBoolean("activePerfilPub",false);
        activePostHistorial=preferencias.getBoolean("activePostHistorial",true);
        animaciones_chat=preferencias.getBoolean("animaciones_chat",true);
        estado_personal=preferencias.getBoolean("estado_personal", true);
        version_bd=preferencias.getInt("version_bd",8);
        es_beta_tester=preferencias.getBoolean("es_beta_tester", false);
        burbuja_datos=preferencias.getBoolean("burbuja_datos", false);
        puedeVaciarBandeja=preferencias.getBoolean("puedeVaciarBandeja", false);
        puedeHacerCopiaSeguridad=preferencias.getBoolean("puedeHacerCopiaSeguridad", false);
        avisar_en_linea=preferencias.getBoolean("avisar_en_linea", false);
        avisar_union_yc=preferencias.getBoolean("avisar_union_yc", false);
        mostrarAvisoOptBatery=preferencias.getBoolean("mostrarAvisoOptBatery", true);
        mostrarAvisoErrorApk=preferencias.getBoolean("mostrarAvisoErrorApk", true);
        hacerBlurFondo=preferencias.getBoolean("hacerBlurFondo", false);
        pedir_confirmacion_avisar_union=preferencias.getBoolean("pedir_confirmacion_avisar_union", true);
        esta_segundo_plano=preferencias.getBoolean("esta_segundo_plano",false);
        son_privados_estados=preferencias.getBoolean("son_privados_estados",true);
        tam_max_descarga_correo=preferencias.getLong("tam_max_descarga_correo",128);
        tam_max_descarga_now=preferencias.getLong("tam_max_descarga_now",128);
        tam_max_descarga_chat=preferencias.getLong("tam_max_descarga_chat",128);

        fechaHoy=preferencias.getString("fechaHoy","");
        cantPostSubidosHoy=preferencias.getInt("cantPostSubidosHoy",0);
        cantComentarioPostSubidosHoy=preferencias.getInt("cantComentarioPostSubidosHoy",0);

        tam_fuente=preferencias.getInt("tam_fuente",15);
        curvaChat=preferencias.getInt("curvaChat",20);
        curvaGlobosChat=preferencias.getInt("curvaGlobosChat",20);

        activar_progreso=preferencias.getBoolean("activar_progreso",true);
        tiempo_progreso=preferencias.getInt("tiempo_progreso",5);
        cantLimitePost=preferencias.getInt("cantLimitePost",20);
        nivelDifuminadoBlur=preferencias.getInt("nivelDifuminadoBlur",3);
        progressEscanerBandeja=preferencias.getInt("progressEscanerBandeja",0);

        felicidades_fin_ano=preferencias.getBoolean("felicidades_fin_ano", true);
        felicidades_ano_nuevo=preferencias.getBoolean("felicidades_ano_nuevo", true);
        felicidades_dia_mujer=preferencias.getBoolean("felicidades_dia_mujer", true);
        felicidades_14_febrero=preferencias.getBoolean("felicidades_14_febrero", true);
        felicidades_cumpleanos=preferencias.getBoolean("felicidades_cumpleanos", true);
        felicidades_noche_buena=preferencias.getBoolean("felicidades_noche_buena", true);
        felicidades_halloween=preferencias.getBoolean("felicidades_halloween", true);
        fecha_cumpleanos=preferencias.getString("fecha_cumpleanos", "");
        idUltCorreoBuzonRecibido=preferencias.getString("idUltCorreoBuzonRecibido", "");
        idUltEstGlobalRecibido=preferencias.getString("idUltEstGlobalRecibido", "");
        idUltChatGlobalRecibido=preferencias.getString("idUltChatGlobalRecibido", "");
        idUltPostGlobalRecibido=preferencias.getString("idUltPostGlobalRecibido", "");
        idUltComentarioPostGlobalRecibido=preferencias.getString("idUltComentarioPostGlobalRecibido", "");
        idUltPostGlobalRecibidoPrueba=preferencias.getString("idUltPostGlobalRecibidoPrueba", "");

        //estadisticas
        cant_buzon_rye=preferencias.getInt("cant_buzon_rye",0);
        cant_estados_subidos=preferencias.getInt("cant_estados_subidos",0);
        cant_post_rye=preferencias.getInt("cant_post_rye",0);
        cant_act_perfil_env=preferencias.getInt("cant_act_perfil_env",0);
        cant_confir_lectura=preferencias.getInt("cant_confir_lectura",0);
        cant_reacciones=preferencias.getInt("cant_reacciones",0);
        cant_vistos_estados=preferencias.getInt("cant_vistos_estados",0);
        cant_aviso_en_linea=preferencias.getInt("cant_aviso_en_linea",0);
        cant_bd_nube=preferencias.getInt("cant_bd_nube",0);
        cant_chat_din=preferencias.getInt("cant_chat_din",0);
        mega_estados_subidos=preferencias.getLong("mega_estados_subidos",0);
        mega_post_recibidos=preferencias.getLong("mega_post_recibidos",0);
        mega_post_enviados=preferencias.getLong("mega_post_enviados",0);
        mega_buzon_recibidos=preferencias.getLong("mega_buzon_recibidos",0);
        mega_buzon_enviados=preferencias.getLong("mega_buzon_enviados",0);
        mega_act_perfil_env=preferencias.getLong("mega_act_perfil_env",0);
        mega_reacciones_env=preferencias.getLong("mega_reacciones_env",0);
        mega_vistos_estados_env=preferencias.getLong("mega_vistos_estados_env",0);
        mega_reacciones_rec=preferencias.getLong("mega_reacciones_rec",0);
        mega_vistos_estados_rec=preferencias.getLong("mega_vistos_estados_rec",0);
        mega_x_serv_confirmacion_lectura_env=preferencias.getLong("mega_x_serv_confirmacion_lectura_env",0);
        mega_x_serv_confirmacion_lectura_rec=preferencias.getLong("mega_x_serv_confirmacion_lectura_rec",0);
        mega_x_serv_chat_dinamico_env=preferencias.getLong("mega_x_serv_chat_dinamico_env",0);
        mega_x_serv_aviso_en_linea_env=preferencias.getLong("mega_x_serv_aviso_en_linea_env",0);
        mega_x_serv_bd_nube_env=preferencias.getLong("mega_x_serv_bd_nube_env",0);
        mega_x_serv_chat_dinamico_rec=preferencias.getLong("mega_x_serv_chat_dinamico_rec",0);
        mega_x_serv_aviso_en_linea_rec=preferencias.getLong("mega_x_serv_aviso_en_linea_rec",0);
        mega_x_serv_bd_nube_rec=preferencias.getLong("mega_x_serv_bd_nube_rec",0);
    }

    /*public static void setTheme(int[] colores){
        color_barra = colores[0];
        color_btn = colores[1];
        color_fondo = colores[2];
        color_texto = colores[3];
        color_interior = colores[4];
        color_msg_izq = colores[5];
        color_msg_der = colores[6];
        color_msg_fecha = colores[7];
        color_accento = colores[8];

        color_ico_gen = colores[10];

        font_barra = colores[11];
        font_msg_izq = colores[12];
        font_msg_der = colores[13];
        font_msg_fecha = colores[14];
        font_ico = colores[15];
        SharedPreferences.Editor editor=preferencias.edit();

        editor.putInt("color_barra", color_barra);
        editor.putInt("color_btn", color_btn);
        editor.putInt("color_fondo", color_fondo);
        editor.putInt("color_texto", color_texto);
        editor.putInt("color_interior", color_interior);
        editor.putInt("color_msg_izq", color_msg_izq);
        editor.putInt("color_msg_der", color_msg_der);
        editor.putInt("color_msg_fecha", color_msg_fecha);
        editor.putInt("color_accento", color_accento);
        editor.putInt("color_ico_gen", color_ico_gen);

        editor.putInt("font_barra", font_barra);
        editor.putInt("font_msg_izq", font_msg_izq);
        editor.putInt("font_msg_der", font_msg_der);
        editor.putInt("font_msg_fecha", font_msg_fecha);
        editor.putInt("font_ico", font_ico);

        editor.apply();
    }*/

    public static void setMaxLines(int x){
        maxLines = x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putInt("maxLines", maxLines);
        editor.apply();
    }
    public static void setPosVistaBandeja(int x){
        posVistaBandeja = x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putInt("posVistaBandeja", posVistaBandeja);
        editor.apply();
    }
    public static void setOpcionVaciadoAutomaticoMensajes(int x){
        opcionVaciadoAutomaticoMensajes = x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putInt("opcionVaciadoAutomaticoMensajes", opcionVaciadoAutomaticoMensajes);
        editor.apply();
    }
    public static void setOpcionCopiaSeguridadAutomatica(int x){
        opcionCopiaSeguridadAutomatica = x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putInt("opcionCopiaSeguridadAutomatica", opcionCopiaSeguridadAutomatica);
        editor.apply();
    }

    public static void setAlias(String x) {
        alias = x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString("alias", alias);
        editor.apply();
        setVersion_info();
    }

    public static void setInfo(String x) {
        info = x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString("info", info);
        editor.apply();
        setVersion_info();
    }

    public static void setCorreo(String x) {
        if(!correo.equals(x)){
            setPedir_confirmacion_avisar_union(true);
            setEs_beta_tester(false);
        }
        correo = x;
        actualizarPropiedades();
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString("correo", correo);
        editor.apply();
    }

    public static void setPass(String x) {
        pass = x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString("pass", pass);
        editor.apply();
    }

    public static void setTelefono(String x) {
        telefono = x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString("telefono", telefono);
        editor.apply();
        setVersion_info();
    }

    public static void setGenero(String x) {
        genero = x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString("genero", genero);
        editor.apply();
        setVersion_info();
    }

    public static void setProvincia(String x) {
        provincia = x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString("provincia", provincia);
        editor.apply();
        setVersion_info();
    }

    public static void setFecha_nacimiento(String x) {
        fecha_nacimiento = x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString("fecha_nacimiento", fecha_nacimiento);
        editor.apply();
        setVersion_info();
    }

    public static void setCant_seguidores(int x){
        cant_seguidores = x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putInt("cant_seguidores", cant_seguidores);
        editor.apply();
    }

    public static void setRuta_img_perfil(String x) {
        ruta_img_perfil = x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString("ruta_img_perfil", ruta_img_perfil);
        editor.apply();
        setVersion_info();
    }

    public static void setVersion_info() {
        version_info++;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putInt("version_info", version_info);
        editor.apply();
    }

    public static void setEnviarEnter(boolean x) {
        enviarEnter = x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putBoolean("enviarEnter", enviarEnter);
        editor.apply();
    }

    public static void setLimitarPost(boolean x) {
        limitarPost = x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putBoolean("limitarPost", limitarPost);
        editor.apply();
    }

    public static void setInvertirResChat(boolean x) {
        invertirResChat = x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putBoolean("invertirResChat", invertirResChat);
        editor.apply();
    }
    public static void setActivarBuzon(boolean x) {
        activarBuzon = x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putBoolean("activarBuzon", activarBuzon);
        editor.apply();
    }
    public static void setDescargaAutMensajesCorreo(boolean x) {
        descargaAutMensajesCorreo = x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putBoolean("descargaAutMensajesCorreo", descargaAutMensajesCorreo);
        editor.apply();
    }
    public static void setDescargaAutImagenNow(boolean x) {
        descargaAutImagenNow = x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putBoolean("descargaAutImagenNow", descargaAutImagenNow);
        editor.apply();
    }
    public static void setDescargaAutMultimediaChat(boolean x) {
        descargaAutMultimediaChat = x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putBoolean("descargaAutMultimediaChat", descargaAutMultimediaChat);
        editor.apply();
    }
    public static void setDescargarMsjDeltaLab(boolean x) {
        descargarMsjDeltaLab = x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putBoolean("descargarMsjDeltaLab", descargarMsjDeltaLab);
        editor.apply();
    }
    public static void setConvertirCorreosMDenGrupos(boolean x) {
        convertirCorreosMDenGrupos = x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putBoolean("convertirCorreosMDenGrupos", convertirCorreosMDenGrupos);
        editor.apply();
    }
    public static void setAddPieFirmaAChat(boolean x) {
        addPieFirmaAChat = x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putBoolean("addPieFirmaAChat", addPieFirmaAChat);
        editor.apply();
    }
    public static void setDescargarMsjDimelo(boolean x) {
        descargarMsjDimelo = x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putBoolean("descargarMsjDimelo", descargarMsjDimelo);
        editor.apply();
    }
    public static void setDescargarMsjLetterSocial(boolean x) {
        descargarMsjLetterSocial = x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putBoolean("descargarMsjLetterSocial", descargarMsjLetterSocial);
        editor.apply();
    }

    public static void setOrdenarXNombre(boolean x) {
        orden_contacto_nombre = x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putBoolean("orden_contacto_nombre", orden_contacto_nombre);
        editor.apply();
    }
    public static void setBurbuja_datos(boolean x) {
        burbuja_datos = x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putBoolean("burbuja_datos", burbuja_datos);
        editor.apply();
    }
    public static void setPuedeVaciarBandeja(boolean x) {
        puedeVaciarBandeja = x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putBoolean("puedeVaciarBandeja", puedeVaciarBandeja);
        editor.apply();
    }
    public static void setPuedeHacerCopiaSeguridad(boolean x) {
        puedeHacerCopiaSeguridad = x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putBoolean("puedeHacerCopiaSeguridad", puedeHacerCopiaSeguridad);
        editor.apply();
    }
    public static void setPuedeSubirPost(boolean x) {
        puedeSubirPost = x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putBoolean("puedeSubirPost", puedeSubirPost);
        editor.apply();
    }

    public static void setCalidad(int x) {
        calidad = x;
        if(calidad<10) calidad=10;
        else if(calidad>100) calidad=100;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putInt("calidad", calidad);
        editor.apply();
    }

    public static void setRuta_fondo(String x){
        ruta_fondo_chat=x;
        setRuta_fondo(-1);
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString("ruta_fondo_chat", ruta_fondo_chat);
        editor.apply();
    }
    public static void setRuta_fondo(int x){
        ruta_drawable=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putInt("ruta_drawable", ruta_drawable);
        editor.apply();
    }
    public static void setTemaApp(int x){
        temaApp=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putInt("temaApp", temaApp);
        editor.apply();
    }
    public static void setIDTemaClaro(String x){
        IDTemaClaro=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString("IDTemaClaro", IDTemaClaro);
        editor.apply();
    }
    public static void setIdPosUltPostVisto(String x){
        idPosUltPostVisto=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString("idPosUltPostVisto", idPosUltPostVisto);
        editor.apply();
    }
    public static void setPieDeFirma(String x){
        pieDeFirma=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString("pieDeFirma", pieDeFirma);
        editor.apply();
    }
    public static void setDescripEscanerBandeja(String x){
        descripEscanerBandeja=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString("descripEscanerBandeja", descripEscanerBandeja);
        editor.apply();
    }
    public static void setIDTemaOscuro(String x){
        IDTemaOscuro=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString("IDTemaOscuro", IDTemaOscuro);
        editor.apply();
    }
    public static void setIdUltCorreoBuzonRecibido(String x){
        idUltCorreoBuzonRecibido=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString("idUltCorreoBuzonRecibido", idUltCorreoBuzonRecibido);
        editor.apply();
    }
    public static void setIdUltEstGlobalRecibido(String x){
        idUltEstGlobalRecibido=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString("idUltEstGlobalRecibido", idUltEstGlobalRecibido);
        editor.apply();
    }
    public static void setIdUltChatGlobalRecibido(String x){
        idUltChatGlobalRecibido=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString("idUltChatGlobalRecibido", idUltChatGlobalRecibido);
        editor.apply();
    }
    public static void setIdUltPostGlobalRecibido(String x){
        idUltPostGlobalRecibido=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString("idUltPostGlobalRecibido", idUltPostGlobalRecibido);
        editor.apply();
    }
    public static void setIdUltComentarioPostGlobalRecibido(String x){
        idUltComentarioPostGlobalRecibido=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString("idUltComentarioPostGlobalRecibido", idUltComentarioPostGlobalRecibido);
        editor.apply();
    }
    public static void setIdUltPostGlobalRecibidoPrueba(String x){
        idUltPostGlobalRecibidoPrueba=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString("idUltPostGlobalRecibidoPrueba", idUltPostGlobalRecibidoPrueba);
        editor.apply();
    }

    public static void setMark(int t){
        mark=t;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putInt("mark", mark);
        editor.apply();
    }
    public static void setLectura(boolean l){
        lectura=l;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putBoolean("lectura", lectura);
        editor.apply();
    }
    public static void setNotificacion(boolean l){
        notificacion=l;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putBoolean("notificacion", notificacion);
        editor.apply();
    }
    public static void setSonido(boolean l){
        sonido=l;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putBoolean("sonido", sonido);
        editor.apply();
    }
    public static void setActualizar_perfil(boolean a){
        actualizar_perfil=a;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putBoolean("actualizar_perfil", actualizar_perfil);
        editor.apply();
    }

    public static void setChat_security(boolean a){
        chat_security=a;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putBoolean("chat_security", chat_security);
        editor.apply();
    }
    public static void setActivePost(boolean a){
        activePost=a;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putBoolean("activePost", activePost);
        editor.apply();
    }

    public static void setActivePostDesImg(boolean a){
        activePostDesImg=a;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putBoolean("activePostDesImg", activePostDesImg);
        editor.apply();
    }
    public static void setActivePostHistorial(boolean a){
        activePostHistorial=a;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putBoolean("activePostHistorial", activePostHistorial);
        editor.apply();
    }
    public static void setActivePostBorrar24(boolean a){
        activePostBorrar24=a;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putBoolean("activePostBorrar24", activePostBorrar24);
        editor.apply();
    }
    public static void setNotiMenChat(boolean a){
        notiMenChat=a;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putBoolean("notiMenChat", notiMenChat);
        editor.apply();
    }
    public static void setNotiCorreoEnt(boolean a){
        notiCorreoEnt=a;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putBoolean("notiCorreoEnt", notiCorreoEnt);
        editor.apply();
    }
    public static void setNotiNowEnt(boolean a){
        notiNowEnt=a;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putBoolean("notiNowEnt", notiNowEnt);
        editor.apply();
    }
    public static void setNotiReacNow(boolean a){
        notiReacNow=a;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putBoolean("notiReacNow", notiReacNow);
        editor.apply();
    }

    public static void setActiveEmojisAnimChat(boolean a){
        activeEmojisAnimChat=a;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putBoolean("activeEmojisAnimChat", activeEmojisAnimChat);
        editor.apply();
    }
    public static void setActivePerfilPub(boolean a){
        activePerfilPub=a;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putBoolean("activePerfilPub", activePerfilPub);
        editor.apply();
    }
    public static void setAnimaciones_chat(boolean a){
        animaciones_chat=a;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putBoolean("animaciones_chat", animaciones_chat);
        editor.apply();
    }
    public static void setEstado_personal(boolean a){
        estado_personal=a;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putBoolean("estado_personal", estado_personal);
        editor.apply();
    }
    public static void setVersion_bd(int x){
        version_bd=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putInt("version_bd", version_bd);
        editor.apply();
    }
    public static void setEs_beta_tester(boolean x){
        es_beta_tester=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putBoolean("es_beta_tester", es_beta_tester);
        editor.apply();
    }
    public static void setAvisar_en_linea(boolean x){
        avisar_en_linea=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putBoolean("avisar_en_linea", avisar_en_linea);
        editor.apply();
    }
    public static void setAvisar_union_yc(boolean x){
        avisar_union_yc=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putBoolean("avisar_union_yc", avisar_union_yc);
        editor.apply();
    }
    public static void setMostrarAvisoOptBatery(boolean x){
        mostrarAvisoOptBatery=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putBoolean("mostrarAvisoOptBatery", mostrarAvisoOptBatery);
        editor.apply();
    }
    public static void setMostrarAvisoErrorApk(boolean x){
        mostrarAvisoErrorApk=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putBoolean("mostrarAvisoErrorApk", mostrarAvisoErrorApk);
        editor.apply();
    }
    public static void setHacerBlurFondo(boolean x){
        hacerBlurFondo=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putBoolean("hacerBlurFondo", hacerBlurFondo);
        editor.apply();
    }
    public static void setPedir_confirmacion_avisar_union(boolean x){
        pedir_confirmacion_avisar_union=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putBoolean("pedir_confirmacion_avisar_union", pedir_confirmacion_avisar_union);
        editor.apply();
    }
    public static void setEsta_segundo_plano(boolean x){
        esta_segundo_plano=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putBoolean("esta_segundo_plano", esta_segundo_plano);
        editor.apply();
    }

    public static void setSon_privados_estados(boolean x){
        son_privados_estados=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putBoolean("son_privados_estados", son_privados_estados);
        editor.apply();
    }

    public static void setTam_max_descarga_correo(long x){
        tam_max_descarga_correo=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putLong("tam_max_descarga_correo", tam_max_descarga_correo);
        editor.apply();
    }
    public static void setTam_max_descarga_now(long x){
        tam_max_descarga_now=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putLong("tam_max_descarga_now", tam_max_descarga_now);
        editor.apply();
    }
    public static void setTam_max_descarga_chat(long x){
        tam_max_descarga_chat=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putLong("tam_max_descarga_chat", tam_max_descarga_chat);
        editor.apply();
    }

    public static void setCurvaChat(int x){
        curvaChat=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putInt("curvaChat", curvaChat);
        editor.apply();
    }

    public static void setCurvaGlobosChat(int x){
        curvaGlobosChat=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putInt("curvaGlobosChat", curvaGlobosChat);
        editor.apply();
    }

    public static void setTam_Fuente(int x){
        tam_fuente=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putInt("tam_fuente", tam_fuente);
        editor.apply();
    }
    public static void setCantPostSubidosHoy(int x){
        cantPostSubidosHoy=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putInt("cantPostSubidosHoy", cantPostSubidosHoy);
        editor.apply();
    }
    public static void setCantComentarioPostSubidosHoy(int x){
        cantComentarioPostSubidosHoy=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putInt("cantComentarioPostSubidosHoy", cantComentarioPostSubidosHoy);
        editor.apply();
    }
    public static void setFechaHoy(String x) {
        fechaHoy = x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString("fechaHoy", fechaHoy);
        editor.apply();
        setVersion_info();
    }

    public static void setActivar_progreso(boolean x){
        activar_progreso=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putBoolean("activar_progreso", activar_progreso);
        editor.apply();
    }
    public static void setTiempo_progreso(int x){
        tiempo_progreso=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putInt("tiempo_progreso", tiempo_progreso);
        editor.apply();
    }

    public static void setCantLimitePost(int x){
        cantLimitePost=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putInt("cantLimitePost", cantLimitePost);
        editor.apply();
    }
    public static void setNivelDifuminadoBlur(int x){
        nivelDifuminadoBlur=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putInt("nivelDifuminadoBlur", nivelDifuminadoBlur);
        editor.apply();
    }
    public static void setProgressEscanerBandeja(int x){
        progressEscanerBandeja=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putInt("progressEscanerBandeja", progressEscanerBandeja);
        editor.apply();
    }

    /////////////////////////////////////////festejos///////////////////////////////
    public static void setFelicidades_fin_ano(boolean x){
        felicidades_fin_ano=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putBoolean("felicidades_fin_ano", felicidades_fin_ano);
        editor.apply();
    }
    public static void setFelicidades_ano_nuevo(boolean x){
        felicidades_ano_nuevo=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putBoolean("felicidades_ano_nuevo", felicidades_ano_nuevo);
        editor.apply();
    }
    public static void setFelicidades_dia_mujer(boolean x){
        felicidades_dia_mujer=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putBoolean("felicidades_dia_mujer", felicidades_dia_mujer);
        editor.apply();
    }
    public static void setFelicidades_14_febrero(boolean x){
        felicidades_14_febrero=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putBoolean("felicidades_14_febrero", felicidades_14_febrero);
        editor.apply();
    }
    public static void setFelicidades_cumpleanos(boolean x){
        felicidades_cumpleanos=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putBoolean("felicidades_cumpleanos", felicidades_cumpleanos);
        editor.apply();
    }
    public static void setFelicidades_noche_buena(boolean x){
        felicidades_noche_buena=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putBoolean("felicidades_noche_buena", felicidades_noche_buena);
        editor.apply();
    }
    public static void setFelicidades_halloween(boolean x){
        felicidades_halloween=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putBoolean("felicidades_halloween", felicidades_halloween);
        editor.apply();
    }
    public static void setFecha_cumpleanos(String x){
        fecha_cumpleanos=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString("fecha_cumpleanos", fecha_cumpleanos);
        editor.apply();
    }
////Estadistica
    public static void addCant_estados_subidos(int x){
        cant_estados_subidos+=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putInt("cant_estados_subidos", cant_estados_subidos);
        editor.apply();
    }
    public static void addCant_post_rye(int x){
        cant_post_rye+=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putInt("cant_post_rye", cant_post_rye);
        editor.apply();
    }
    public static void addCant_buzon_rye(int x){
        cant_buzon_rye+=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putInt("cant_buzon_rye", cant_buzon_rye);
        editor.apply();
    }
    public static void addCant_act_perfil_env(int x){
        cant_act_perfil_env+=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putInt("cant_act_perfil_env", cant_act_perfil_env);
        editor.apply();
    }
    public static void addCant_reacciones(int x){
        cant_reacciones+=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putInt("cant_reacciones", cant_reacciones);
        editor.apply();
    }
    public static void addCant_vistos_estados(int x){
        cant_vistos_estados+=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putInt("cant_vistos_estados", cant_vistos_estados);
        editor.apply();
    }
    public static void addCant_aviso_en_linea(int x){
        cant_aviso_en_linea+=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putInt("cant_aviso_en_linea", cant_aviso_en_linea);
        editor.apply();
    }
    public static void addCant_confir_lectura(int x){
        cant_confir_lectura+=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putInt("cant_confir_lectura", cant_confir_lectura);
        editor.apply();
    }
    public static void addCant_bd_nube(int x){
        cant_bd_nube+=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putInt("cant_bd_nube", cant_bd_nube);
        editor.apply();
    }
    public static void addCant_chat_din(int x){
        cant_chat_din+=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putInt("cant_chat_din", cant_chat_din);
        editor.apply();
    }
    public static void addMega_estados_subidos(int x){
        mega_estados_subidos+=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putLong("mega_estados_subidos", mega_estados_subidos);
        editor.apply();
    }
    public static void addMega_post_recibidos(int x){
        mega_post_recibidos+=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putLong("mega_post_recibidos", mega_post_recibidos);
        editor.apply();
    }
    public static void addMega_post_enviados(int x){
        mega_post_enviados+=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putLong("mega_post_enviados", mega_post_enviados);
        editor.apply();
    }
    public static void addMega_buzon_recibidos(int x){
        mega_buzon_recibidos+=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putLong("mega_buzon_recibidos", mega_buzon_recibidos);
        editor.apply();
    }
    public static void addMega_buzon_enviados(int x){
        mega_buzon_enviados+=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putLong("mega_buzon_enviados", mega_buzon_enviados);
        editor.apply();
    }
    public static void addMega_act_perfil_env(int x){
        mega_act_perfil_env+=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putLong("mega_act_perfil_env", mega_act_perfil_env);
        editor.apply();
    }
    public static void addMega_reacciones_env(int x){
        mega_reacciones_env+=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putLong("mega_reacciones_env", mega_reacciones_env);
        editor.apply();
    }
    public static void addMega_vistos_estados_env(int x){
        mega_vistos_estados_env+=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putLong("mega_vistos_estados_env", mega_vistos_estados_env);
        editor.apply();
    }
    public static void addMega_reacciones_rec(int x){
        mega_reacciones_rec+=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putLong("mega_reacciones_rec", mega_reacciones_rec);
        editor.apply();
    }
    public static void addMega_vistos_estados_rec(int x){
        mega_vistos_estados_rec+=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putLong("mega_vistos_estados_rec", mega_vistos_estados_rec);
        editor.apply();
    }
    public static void addMega_x_serv_confirmacion_lectura_env(int x){
        mega_x_serv_confirmacion_lectura_env+=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putLong("mega_x_serv_confirmacion_lectura_env", mega_x_serv_confirmacion_lectura_env);
        editor.apply();
    }
    public static void addMega_x_serv_confirmacion_lectura_rec(int x){
        mega_x_serv_confirmacion_lectura_rec+=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putLong("mega_x_serv_confirmacion_lectura_rec", mega_x_serv_confirmacion_lectura_rec);
        editor.apply();
    }
    public static void addMega_x_serv_chat_dinamico_env(int x){
        mega_x_serv_chat_dinamico_env+=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putLong("mega_x_serv_chat_dinamico_env", mega_x_serv_chat_dinamico_env);
        editor.apply();
    }
    public static void addMega_x_serv_aviso_en_linea_env(int x){
        mega_x_serv_aviso_en_linea_env+=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putLong("mega_x_serv_aviso_en_linea_env", mega_x_serv_aviso_en_linea_env);
        editor.apply();
    }
    public static void addMega_x_serv_bd_nube_env(int x){
        mega_x_serv_bd_nube_env+=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putLong("mega_x_serv_bd_nube_env", mega_x_serv_bd_nube_env);
        editor.apply();
    }

    public static void addMega_x_serv_chat_dinamico_rec(int x){
        mega_x_serv_chat_dinamico_rec+=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putLong("mega_x_serv_chat_dinamico_rec", mega_x_serv_chat_dinamico_rec);
        editor.apply();
    }
    public static void addMega_x_serv_aviso_en_linea_rec(int x){
        mega_x_serv_aviso_en_linea_rec+=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putLong("mega_x_serv_aviso_en_linea_rec", mega_x_serv_aviso_en_linea_rec);
        editor.apply();
    }
    public static void addMega_x_serv_bd_nube_rec(int x){
        mega_x_serv_bd_nube_rec+=x;
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putLong("mega_x_serv_bd_nube_rec", mega_x_serv_bd_nube_rec);
        editor.apply();
    }

    public static void resetearEstadisticasGral(){
        cant_estados_subidos = 0;
        addCant_estados_subidos(0);
        cant_post_rye = 0;
        addCant_post_rye(0);
        cant_buzon_rye = 0;
        addCant_buzon_rye(0);
        cant_act_perfil_env = 0;
        addCant_act_perfil_env(0);
        cant_reacciones = 0;
        addCant_reacciones(0);
        cant_vistos_estados = 0;
        addCant_vistos_estados(0);
        cant_confir_lectura = 0;
        addCant_confir_lectura(0);
        cant_aviso_en_linea=0;
        addCant_aviso_en_linea(0);
        cant_bd_nube=0;
        addCant_bd_nube(0);
        cant_chat_din=0;
        addCant_chat_din(0);
        mega_estados_subidos = 0;
        addMega_estados_subidos(0);
        mega_post_recibidos = 0;
        addMega_post_recibidos(0);
        mega_post_enviados = 0;
        addMega_post_enviados(0);
        mega_buzon_recibidos = 0;
        addMega_buzon_recibidos(0);
        mega_buzon_enviados = 0;
        addMega_buzon_enviados(0);
        mega_act_perfil_env = 0;
        addMega_act_perfil_env(0);
        mega_reacciones_env = 0;
        addMega_reacciones_env(0);
        mega_vistos_estados_env = 0;
        addMega_vistos_estados_env(0);
        mega_reacciones_rec = 0;
        addMega_reacciones_rec(0);
        mega_vistos_estados_rec = 0;
        addMega_vistos_estados_rec(0);
        mega_x_serv_confirmacion_lectura_env = 0;
        addMega_x_serv_confirmacion_lectura_env(0);
        mega_x_serv_confirmacion_lectura_rec = 0;
        addMega_x_serv_confirmacion_lectura_rec(0);
        mega_x_serv_chat_dinamico_env = 0;
        addMega_x_serv_chat_dinamico_env(0);
        mega_x_serv_aviso_en_linea_env = 0;
        addMega_x_serv_aviso_en_linea_env(0);
        mega_x_serv_bd_nube_env = 0;
        addMega_x_serv_bd_nube_env(0);
        mega_x_serv_chat_dinamico_rec = 0;
        addMega_x_serv_chat_dinamico_rec(0);
        mega_x_serv_aviso_en_linea_rec = 0;
        addMega_x_serv_aviso_en_linea_rec(0);
        mega_x_serv_bd_nube_rec = 0;
        addMega_x_serv_bd_nube_rec(0);
    }

    public synchronized static void procesarSticker(){
        carpetasStickers = stickerManager.procesarTodosStickers();
    }


    public static void configuracion1() {
        setActivePost(false);
        setCantLimitePost(10);
        setLimitarPost(true);
        setActivePostDesImg(false);
        setActualizar_perfil(false);
        setAvisar_en_linea(false);
        setCalidad(10);
        setLectura(false);
        setEstado_personal(false);
        setTam_max_descarga_chat(64);
        setTam_max_descarga_now(64);
        setTam_max_descarga_correo(64);
        setDescargaAutMultimediaChat(false);
        setDescargaAutImagenNow(false);
        setDescargaAutMensajesCorreo(false);
    }

    public static void configuracion2() {
        setActivePost(true);
        setCantLimitePost(20);
        setLimitarPost(true);
        setActivePostDesImg(false);
        setActualizar_perfil(false);
        setAvisar_en_linea(false);
        setCalidad(30);
        setLectura(true);
        setEstado_personal(true);
        setTam_max_descarga_chat(64);
        setTam_max_descarga_now(64);
        setTam_max_descarga_correo(64);
        setDescargaAutMultimediaChat(true);
        setDescargaAutImagenNow(true);
        setDescargaAutMensajesCorreo(true);
    }

    public static void configuracion3() {
        setActivePost(true);
        setCantLimitePost(50);
        setLimitarPost(false);
        setActivePostDesImg(true);
        setActualizar_perfil(true);
        setAvisar_en_linea(true);
        setCalidad(60);
        setLectura(true);
        setEstado_personal(true);
        setTam_max_descarga_chat(256);
        setTam_max_descarga_now(256);
        setTam_max_descarga_correo(256);
        setDescargaAutMultimediaChat(true);
        setDescargaAutImagenNow(true);
        setDescargaAutMensajesCorreo(true);
    }

    private void obtenerAno_y_FechaActual(){
        final Calendar c = Calendar.getInstance();
        anoActual = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        String dd = ""+day;
        String mm;
        switch (month){
            case 0: mm=" de enero"; break;
            case 1: mm=" de febrero"; break;
            case 2: mm=" de marzo"; break;
            case 3: mm=" de abril"; break;
            case 4: mm=" de mayo"; break;
            case 5: mm=" de junio"; break;
            case 6: mm=" de julio"; break;
            case 7: mm=" de agosto"; break;
            case 8: mm=" de septiembre"; break;
            case 9: mm=" de octubre"; break;
            case 10: mm=" de noviembre"; break;
            case 11: mm=" de diciembre"; break;
            default: mm=" del calendario apocalíptico";
        }
        fechaActual = dd+mm;
    }

}
