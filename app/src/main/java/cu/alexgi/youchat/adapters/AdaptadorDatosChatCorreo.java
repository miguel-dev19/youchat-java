package cu.alexgi.youchat.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.GradientDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieCompositionFactory;
import com.airbnb.lottie.LottieListener;
import com.airbnb.lottie.LottieTask;
import com.bumptech.glide.Glide;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.imageview.ShapeableImageView;
import com.vanniktech.emoji.EmojiInformation;
import com.vanniktech.emoji.EmojiTextView;
import com.vanniktech.emoji.EmojiUtils;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.GZIPInputStream;

import cu.alexgi.youchat.ChatsActivityCorreo;
import cu.alexgi.youchat.CircleImageView;
import cu.alexgi.youchat.Convertidor;
import cu.alexgi.youchat.LinkUtils;
import cu.alexgi.youchat.R;
import cu.alexgi.youchat.Utils;
import cu.alexgi.youchat.YouChatApplication;
import cu.alexgi.youchat.audiowave.AudioWaveView;
import cu.alexgi.youchat.items.ItemChat;
import cu.alexgi.youchat.progressbar.DownloadProgressView;
import cu.alexgi.youchat.sticky_headers_data.StickyRecyclerHeadersAdapter;
import cu.alexgi.youchat.zoominimageview.ZoomInImageViewAttacher;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

import static android.content.Context.POWER_SERVICE;
import static cu.alexgi.youchat.MainActivity.dbWorker;

@SuppressLint("RestrictedApi")
public class AdaptadorDatosChatCorreo extends RecyclerView.Adapter<AdaptadorDatosChatCorreo.ViewHolderDatos>
        implements View.OnClickListener, View.OnLongClickListener, StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {

    /////SENSOR//////
    private SensorManager deviceSensorManager;
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    private Sensor sensor;
    ////////////////

    private static final String TAG = "AdaptadorDatosChat";
    private ChatsActivityCorreo chatsActivity;
    private boolean modoNoche= YouChatApplication.temaApp==1;

    private MediaPlayer Audioproximity;
    private AudioManager audioManager;
    private AudioFocusRequest audioFocusRequest;
    static String rutaActualGlobal;
//    static AudioWaveView seekbarActual;
//    static CircleImageView playActual;

    private boolean pause = false;
    static MediaPlayer mpGlobal;
    private Timer timerGlobal;
    static AudioWaveView audio_seekbar_global;
    static CircleImageView audio_play_global;
    private Context context;
    private PorterDuffColorFilter contenedorMsgResDer, contenedorMsgResIzq;
    private float maxAnchoImagen = (float) YouChatApplication.anchoPantalla*0.6f;
    private float maxLargoImagen = (float) YouChatApplication.largoPantalla*0.4f;

    private boolean modoSeleccion;
    private boolean stopCall = false;

    public void setModoSeleccion(boolean modoSeleccion) {
        this.modoSeleccion = modoSeleccion;
    }

    static ArrayList<ItemChat> listaDatos;
    private View.OnClickListener listener;
    private  View.OnLongClickListener onLongClickListener;

    private static final int View_item_text_izq=1;
    private static final int View_item_text_der=2;
    private static final int View_item_img_izq=3;
    private static final int View_item_img_der=4;
    private static final int View_item_audio_izq=7;
    private static final int View_item_audio_der=8;
    private static final int View_item_archivo_izq=13;
    private static final int View_item_archivo_der=14;
    private static final int View_item_sticker_izq=19;
    private static final int View_item_sticker_der=20;

    private static final int View_item_text_izq_emoji=25;
    private static final int View_item_text_der_emoji=26;
    private static final int View_item_text_izq_emoji_animado=27;
    private static final int View_item_text_der_emoji_animado=28;

    private static final int View_item_msg_no_leido=99;

    private boolean iniAnim=false;
    float curva =(float) YouChatApplication.curvaGlobosChat;
    private int tam_fuente;
    private ColorStateList stateListColorBtn, stateListColorMsgIzq, stateListFontMsgIzq, stateListColorMsgDer, stateListFontMsgDer,stateListEstadoViewBlanco,stateListEstadoViewRojo;

    public void hacerAnim(){
        iniAnim=true;
    }

    private SensorEventListener proximityListener = new SensorEventListener() {
        @Override
        public synchronized void onSensorChanged(SensorEvent event) {
            if(event.values[0]==0.0){
                if(mpGlobal!=null && mpGlobal.isPlaying()
                        && (wakeLock!=null && !wakeLock.isHeld())
                        && !rutaActualGlobal.isEmpty()){
                    apagarPantalla();
                }
            }
            else if(event.values[0]!=0.0){
                if(wakeLock!=null && wakeLock.isHeld()){
                    encenderPantalla();
                }
            }
        }
        @Override
        public void onAccuracyChanged(Sensor arg0, int arg1) {}
    };

    private void apagarPantalla() {
        try {
            if(Audioproximity!=null){
                if(Audioproximity.isPlaying())
                    Audioproximity.pause();
                Audioproximity.setOnCompletionListener(null);
                Audioproximity.release();
                Audioproximity=null;
            }

            Audioproximity = new MediaPlayer();
            mpGlobal.pause();
            if(timerGlobal!=null){
                timerGlobal.cancel();
                timerGlobal=null;
            }
            if(audio_play_global != null) audio_play_global.setImageResource(R.drawable.audio_play);
            int pos = mpGlobal.getCurrentPosition();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Audioproximity.setAudioAttributes(new AudioAttributes.Builder().setLegacyStreamType(AudioManager.STREAM_VOICE_CALL)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION).build());
            }
            else Audioproximity.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);

            Audioproximity.setDataSource(rutaActualGlobal);
            Audioproximity.prepare();

            Audioproximity.seekTo(pos>101 ? pos-100 : pos);
            Audioproximity.start();

            Audioproximity.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if(Audioproximity==null) Audioproximity = mp;
                    if(wakeLock!=null && wakeLock.isHeld()){
                        wakeLock.release();
                    }
                    rutaActualGlobal="";
                    Audioproximity.setOnCompletionListener(null);
                    if(Audioproximity.isPlaying())
                        Audioproximity.pause();
                    Audioproximity.release();
                    Audioproximity = null;
                    Utils.clearAudioFocus(audioManager, audioFocusRequest);
                    if(mpGlobal!=null){
                        mpGlobal.setOnCompletionListener(null);
                        if(mpGlobal.isPlaying())
                            mpGlobal.pause();
                        if(timerGlobal!=null){
                            timerGlobal.cancel();
                            timerGlobal=null;
                        }
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                            mpGlobal.setAudioAttributes(new AudioAttributes.Builder().setLegacyStreamType(AudioManager.STREAM_MUSIC)
//                                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//                                    .setUsage(AudioAttributes.USAGE_MEDIA).build());
//                        }
//                        else mpGlobal.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mpGlobal.seekTo(0);
                        audio_seekbar_global.setProgress(0);
                        audio_play_global.setImageResource(R.drawable.audio_play);
                        mpGlobal=null;
                        releaseComponents();
                    }
                }
            });
            wakeLock.acquire(mpGlobal.getDuration()!=-1?(mpGlobal.getDuration()+5000L):600000L);
        }catch (IllegalStateException | IOException e) {
            Log.e(TAG,"-> "+e.toString());
            e.printStackTrace();
        }
    }

    private void encenderPantalla() {
        wakeLock.release();

        if(Audioproximity!=null){
            Audioproximity.setOnCompletionListener(null);
            if(Audioproximity.isPlaying())
                Audioproximity.pause();

            int pos = Audioproximity.getCurrentPosition();
            mpGlobal.seekTo(pos);
            if(audio_seekbar_global != null)
                audio_seekbar_global.setProgress((float)(pos*100)/mpGlobal.getDuration());

            Audioproximity.release();
            Audioproximity = null;
        }
        releaseComponents();
    }

    private synchronized void reproducirAudio(final MediaPlayer mp, String ruta
            ,CircleImageView audio_play, AudioWaveView audio_seekbar){

        if(mpGlobal==null){
            initMpGlobal(mp, ruta, audio_play, audio_seekbar);
        }
        else {
            if(!mp.equals(mpGlobal)){
                mpGlobal.setOnCompletionListener(null);
                if(mpGlobal.isPlaying())
                    mpGlobal.pause();
                if(timerGlobal!=null){
                    timerGlobal.cancel();
                    timerGlobal=null;
                }
                mpGlobal.seekTo(0);
                audio_seekbar_global.setProgress(0);
                audio_play_global.setImageResource(R.drawable.audio_play);
                mpGlobal=null;
                initMpGlobal(mp, ruta, audio_play, audio_seekbar);
            }
        }
    }

    private synchronized void playOrPause(){
        if(mpGlobal.isPlaying()){
            mpGlobal.pause();
            releaseComponents();
            audio_play_global.setImageResource(R.drawable.audio_play);
            Utils.clearAudioFocus(audioManager, audioFocusRequest);
            if(timerGlobal!=null){
                timerGlobal.cancel();
                timerGlobal = null;
            }
        }
        else {
            mpGlobal.start();
            runComponents();
            audio_play_global.setImageResource(R.drawable.audio_pause);
            audioFocusRequest=Utils.setAudioFocus(audioManager);
            if(timerGlobal!=null){
                timerGlobal.cancel();
                timerGlobal = null;
            }
            timerGlobal = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    if(mpGlobal!=null && mpGlobal.isPlaying() && audio_seekbar_global!=null){
                        float pos = (float)(mpGlobal.getCurrentPosition() *100)/mpGlobal.getDuration();
                        if(pos<0) pos=0;
                        else if(pos>100) pos=100;
                        audio_seekbar_global.setProgress(pos);
                    }
                }};
            timerGlobal.scheduleAtFixedRate(timerTask, 0, 100);
        }
    }

    private void initMpGlobal(final MediaPlayer mp, String ruta
            ,CircleImageView audio_play, AudioWaveView audio_seekbar) {
        mpGlobal=mp;
        audio_seekbar_global=audio_seekbar;
        audio_play_global=audio_play;
        audio_play_global.setImageResource(R.drawable.audio_pause);

        releaseComponents();
        runComponents();

        audioFocusRequest=Utils.setAudioFocus(audioManager);

        mpGlobal.start();

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            mpGlobal.setAudioAttributes(new AudioAttributes.Builder().setLegacyStreamType(AudioManager.STREAM_MUSIC)
//                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//                    .setUsage(AudioAttributes.USAGE_MEDIA).build());
//        }
//        else mpGlobal.setAudioStreamType(AudioManager.STREAM_MUSIC);

        rutaActualGlobal = ruta;
        mpGlobal.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(mpGlobal==null) mpGlobal = mp;
                if(timerGlobal!=null){
                    timerGlobal.cancel();
                    timerGlobal=null;
                }
                mpGlobal.setOnCompletionListener(null);
                mpGlobal.pause();
//                mpGlobal.stop();
                mpGlobal.seekTo(0);
                audio_seekbar_global.setProgress(0);
                audio_play_global.setImageResource(R.drawable.audio_play);

                audio_seekbar_global=null;
                audio_play_global=null;
//                mpGlobal.release();
                mpGlobal=null;
                Utils.clearAudioFocus(audioManager, audioFocusRequest);
            }
        });

        if(timerGlobal!=null){
            timerGlobal.cancel();
            timerGlobal=null;
        }
        timerGlobal = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if(mpGlobal!=null && mpGlobal.isPlaying() && audio_seekbar_global!=null){
                    float pos = (float)(mpGlobal.getCurrentPosition() *100)/mpGlobal.getDuration();
                    if(pos<0) pos=0;
                    else if(pos>100) pos=100;
                    audio_seekbar_global.setProgress(pos);
                }
            }};
        timerGlobal.scheduleAtFixedRate(timerTask, 0, 100);
    }

    private void setStatusImg(int est, AppCompatImageView estadoView) {
        switch(est){
            case ItemChat.ESTADO_ESPERANDO:
                estadoView.setImageResource(R.drawable.time_circle);
                estadoView.setSupportImageTintList(stateListFontMsgDer);
                break;
            case ItemChat.ESTADO_ERROR:
                estadoView.setImageResource(R.drawable.msg_est_error);
                estadoView.setSupportImageTintList(stateListColorBtn);
                break;
            case ItemChat.ESTADO_ENVIADO:
                estadoView.setImageResource(R.drawable.msg_est_enviado);
                estadoView.setSupportImageTintList(stateListFontMsgDer);
                break;
            case ItemChat.ESTADO_RECIBIDO:
                estadoView.setImageResource(R.drawable.msg_est_recibido);
                estadoView.setSupportImageTintList(stateListFontMsgDer);
                break;
            case ItemChat.ESTADO_VISTO:
                estadoView.setImageResource(R.drawable.msg_est_recibido);
                estadoView.setSupportImageTintList(stateListColorBtn);
                break;
        }
    }

    private void runComponents(){
        if(Audioproximity!=null){
            if(Audioproximity.isPlaying())
                Audioproximity.pause();
            Audioproximity.release();
            Audioproximity = null;
        }
        int field = 0x00000020;
        try {
            field = PowerManager.class.getField("PROXIMITY_SCREEN_OFF_WAKE_LOCK").getInt(null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(field, context.getPackageName());
        deviceSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        List<Sensor> sensorList = deviceSensorManager.getSensorList(Sensor.TYPE_PROXIMITY);
        if(sensorList.size()>0){
            sensor = sensorList.get(0);
            deviceSensorManager.registerListener(proximityListener, sensor, 0, null);
//            Audioproximity = new MediaPlayer();
        }
    }

    private void releaseComponents(){
        if(wakeLock!=null && wakeLock.isHeld()) wakeLock.release();
        if(Audioproximity!=null){
            if(Audioproximity.isPlaying()) Audioproximity.stop();
            Audioproximity.release();
        }
        if(deviceSensorManager!=null && proximityListener!=null && sensor!=null)
            deviceSensorManager.unregisterListener(proximityListener, sensor);
    }

    public AdaptadorDatosChatCorreo(Context c, ArrayList<ItemChat> listaDatos, ChatsActivityCorreo ca) {
        this.listaDatos = listaDatos;
        context = c;
        chatsActivity=ca;
        modoSeleccion=false;

        audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);

        tam_fuente=YouChatApplication.tam_fuente;
//        else Toast.makeText(context, "DISPOSITIVO NO SOPORTA SENSOR", Toast.LENGTH_LONG).show();

        contenedorMsgResIzq = new PorterDuffColorFilter(Color.parseColor(YouChatApplication.itemTemas.getColor_msg_izq_oscuro()), PorterDuff.Mode.SRC_IN);
        contenedorMsgResDer = new PorterDuffColorFilter(Color.parseColor(YouChatApplication.itemTemas.getColor_msg_der_oscuro()), PorterDuff.Mode.SRC_IN);

        stateListColorBtn = ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getColor_msg_der_audio()));
        
        stateListColorMsgDer = ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getColor_msg_der()));
        stateListFontMsgDer = ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getFont_msg_der()));

        stateListColorMsgIzq = ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getColor_msg_izq()));
        stateListFontMsgIzq = ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getFont_msg_izq()));
        
        stateListEstadoViewBlanco = ColorStateList.valueOf(chatsActivity.getResources().getColor(R.color.texto_blanco_to_gris));
        stateListEstadoViewRojo = ColorStateList.valueOf(chatsActivity.getResources().getColor(R.color.temaRojoAccent));
    }

    @Override
    public ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view;
        if(viewType==View_item_text_izq)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_mensaje_izq,null, false);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            return new ViewHolderDatosTextIzq(view);
        }
        else if(viewType==View_item_text_der)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_mensaje_der,null, false);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            return new ViewHolderDatosTextDer(view);
        }
        else if(viewType==View_item_img_izq)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_imagen_izq,null, false);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            return new ViewHolderDatosImgIzq(view);
        }
        else if(viewType==View_item_img_der)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_imagen_der,null, false);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            return new ViewHolderDatosImgDer(view);
        }

        else if(viewType==View_item_audio_izq)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_audio_izq,null, false);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            return new ViewHolderDatosAudioIzq(view);
        }
        else if(viewType==View_item_audio_der)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_audio_der,null, false);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            return new ViewHolderDatosAudioDer(view);
        }

        else if(viewType==View_item_archivo_izq)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_file_izq,null, false);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            return new ViewHolderDatosArchivoIzq(view);
        }
        else if(viewType==View_item_archivo_der)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_file_der,null, false);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            return new ViewHolderDatosArchivoDer(view);
        }
        else if(viewType==View_item_sticker_izq)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_sticker_izq,null, false);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            return new ViewHolderDatosStickerIzq(view);
        }
        else if(viewType==View_item_sticker_der)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_sticker_der,null, false);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            return new ViewHolderDatosStickerDer(view);
        }
        else if(viewType==View_item_text_izq_emoji)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_mensaje_izq_emoji,null, false);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            return new ViewHolderDatosTextIzqEmoji(view);
        }
        else if(viewType==View_item_text_der_emoji)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_mensaje_der_emoji,null, false);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            return new ViewHolderDatosTextDerEmoji(view);
        }
        else if(viewType==View_item_text_izq_emoji_animado)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_sticker_izq,null, false);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            return new ViewHolderDatosTextIzqEmojiAnimado(view);
        }
        else if(viewType==View_item_text_der_emoji_animado)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_sticker_der,null, false);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            return new ViewHolderDatosTextDerEmojiAnimado(view);
        }
        else
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_mensaje_fecha,null, false);
            return new ViewHolderDatosMsgNoLeidos(view);
        }
    }

    @Override
    public int getItemViewType(int pos) {
        int tipo = listaDatos.get(pos).getTipo_mensajeReal();
        if(tipo==1)
            return View_item_text_izq;
        else if(tipo==2)
            return View_item_text_der;
        else if(tipo==3)
            return View_item_img_izq;
        else if(tipo==4)
            return View_item_img_der;
        else if(tipo==7)
            return View_item_audio_izq;
        else if(tipo==8)
            return View_item_audio_der;
        else if(tipo==13)
            return View_item_archivo_izq;
        else if(tipo==14)
            return View_item_archivo_der;
        else if(tipo==19)
            return View_item_sticker_izq;
        else if(tipo==20)
            return View_item_sticker_der;
        else if(tipo==25)
            return View_item_text_izq_emoji;
        else if(tipo==26)
            return View_item_text_der_emoji;
        else if(tipo==27)
            return View_item_text_izq_emoji_animado;
        else if(tipo==28)
            return View_item_text_der_emoji_animado;
        else
            return View_item_msg_no_leido;
    }

    @Override
    public void onBindViewHolder(final ViewHolderDatos holder, final int position) {
        ItemChat chat = listaDatos.get(position);
        boolean margen;
        switch (holder.getItemViewType()) {
            case 1: //mensaje izquierda
                margen= position >= listaDatos.size() - 1 || !listaDatos.get(position + 1).esIzq();

                holder.itemView.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getSel_msj()));

                ViewHolderDatosTextIzq vhdti = (ViewHolderDatosTextIzq) holder;
                vhdti.AsignarDatos(margen, chat);
                setAnimationIzq(holder.itemView);
                break;
            case 2: //mensaje derecha
                margen= position >= listaDatos.size() - 1 || !listaDatos.get(position + 1).esDer();

                ViewHolderDatosTextDer vhdtd = (ViewHolderDatosTextDer) holder;
                vhdtd.AsignarDatos(margen, chat);
                setAnimationDer(holder.itemView);
                break;
            case 3: //imagen izquierda
                if(position<listaDatos.size()-1 && listaDatos.get(position+1).esIzq())
                    margen=false;
                else margen=true;

                ViewHolderDatosImgIzq vhdii = (ViewHolderDatosImgIzq) holder;
                vhdii.AsignarDatos(margen, chat);
                setAnimationIzq(holder.itemView);
                break;
            case 4: //imagen derecha
                if(position<listaDatos.size()-1 && listaDatos.get(position+1).esDer())
                    margen=false;
                else margen=true;

                ViewHolderDatosImgDer vhdid = (ViewHolderDatosImgDer) holder;
                vhdid.AsignarDatos(margen, chat);
                setAnimationDer(holder.itemView);
                break;
            case 7: //audio izquierda
                if(position<listaDatos.size()-1 && listaDatos.get(position+1).esIzq())
                    margen=false;
                else margen=true;

                ViewHolderDatosAudioIzq vhdai = (ViewHolderDatosAudioIzq) holder;
                vhdai.AsignarDatos(margen, chat);
                setAnimationIzq(holder.itemView);
                break;
            case 8: //audio derecha
                if(position<listaDatos.size()-1 && listaDatos.get(position+1).esDer())
                    margen=false;
                else margen=true;

                ViewHolderDatosAudioDer vhdad = (ViewHolderDatosAudioDer) holder;
                vhdad.AsignarDatos(margen, chat);
                setAnimationDer(holder.itemView);
                break;

            case 13: //archivo izquierda
                if(position<listaDatos.size()-1 && listaDatos.get(position+1).esIzq())
                    margen=false;
                else margen=true;

                ViewHolderDatosArchivoIzq vhdfi = (ViewHolderDatosArchivoIzq) holder;
                vhdfi.AsignarDatos(margen, chat);
                setAnimationIzq(holder.itemView);
                break;
            case 14: //archivo derecha
                if(position<listaDatos.size()-1 && listaDatos.get(position+1).esDer())
                    margen=false;
                else margen=true;

                ViewHolderDatosArchivoDer vhdfd = (ViewHolderDatosArchivoDer) holder;
                vhdfd.AsignarDatos(margen, chat);
                setAnimationDer(holder.itemView);
                break;

            case 19: //sticker izquierda

                ViewHolderDatosStickerIzq vhdsi = (ViewHolderDatosStickerIzq) holder;
                vhdsi.AsignarDatos(chat);

                setAnimationIzq(holder.itemView);
                break;
            case 20: //sticker derecha

                ViewHolderDatosStickerDer vhdsd = (ViewHolderDatosStickerDer) holder;
                vhdsd.AsignarDatos(chat);

                setAnimationDer(holder.itemView);
                break;
            case 25: //mensaje izquierda emoji
                margen= position >= listaDatos.size() - 1 || !listaDatos.get(position + 1).esIzq();

                ViewHolderDatosTextIzqEmoji vhdtie = (ViewHolderDatosTextIzqEmoji) holder;
                vhdtie.AsignarDatos(margen, chat);
                setAnimationIzq(holder.itemView);
                break;
            case 26: //mensaje derecha emoji
                margen= position >= listaDatos.size() - 1 || !listaDatos.get(position + 1).esDer();

                ViewHolderDatosTextDerEmoji vhdtde = (ViewHolderDatosTextDerEmoji) holder;
                vhdtde.AsignarDatos(margen, chat);
                setAnimationDer(holder.itemView);
                break;
            case 27: //mensaje izquierda emoji

                ViewHolderDatosTextIzqEmojiAnimado vhdtiea = (ViewHolderDatosTextIzqEmojiAnimado) holder;
                vhdtiea.AsignarDatos(chat);
                setAnimationIzq(holder.itemView);
                break;
            case 28: //mensaje derecha emoji

                ViewHolderDatosTextDerEmojiAnimado vhdtdea = (ViewHolderDatosTextDerEmojiAnimado) holder;
                vhdtdea.AsignarDatos(chat);
                setAnimationDer(holder.itemView);
                break;

            case 99: //mensajes no visto
                ViewHolderDatosMsgNoLeidos vhdmnl = (ViewHolderDatosMsgNoLeidos) holder;
                vhdmnl.AsignarDatos(chat.getMensaje());
                break;
        }
        //holder.AsignarDatos(listaDatos.get(position).getUser(), listaDatos.get(position).getInfo(), listaDatos.get(position).getFoto());
    }

    private void setAnimationIzq(View v){
        if(iniAnim){
            iniAnim=false;
            if(YouChatApplication.animaciones_chat){
                Animation anim= AnimationUtils.loadAnimation(v.getContext(),R.anim.right_in);
                v.startAnimation(anim);
            }
        }
    }
    private void setAnimationDer(View v){
        if(iniAnim){
            iniAnim=false;
            if(YouChatApplication.animaciones_chat){
                Animation anim= AnimationUtils.loadAnimation(v.getContext(),R.anim.left_in);
                v.startAnimation(anim);
            }
        }
    }

    private synchronized byte[] readBytesAudio(InputStream input, int len) throws IOException {
        int total = 0;
        byte[] bytes = new byte[len];
        while (total < len) {
            int current = input.read(bytes, total, len - total);
            if (current > 0) {
                total += current;
            } else {
                throw new EOFException();
            }
        }
        input.close();
        return bytes;

        ///OTRA VIA
        //                byte [] bytes = new byte[(int) f.length()];
//                FileInputStream fis = null;
//                try {
//
//                    fis = new FileInputStream(f);
//                    fis.read(bytes);
//                }
//                catch
//                (IOException e) {
//                    e.printStackTrace();
//                }
//                finally {
//                    if(fis!=null) {
//                        try {
//                            fis.close();
//                            audio_seekbar_der.setRawData(bytes);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
    }

    private static int posId(String id){
        for(int i=0; i<listaDatos.size(); i++)
            if(listaDatos.get(i).getId().equals(id))
                return i;
        return -1;
    }
    //sticky header
    @Override
    public long getHeaderId(int position) {
        if(listaDatos.size()>0 &&
                (listaDatos.get(position).esMsg() || listaDatos.get(position).getTipo_mensaje()==99)){
            int idSticky = 0;
            String fecha = listaDatos.get(position).getFecha();
            if(fecha.length()!=10) return -1;
//            dd/MM/yyyy
            int dd = (fecha.charAt(0)-48)*10 + (fecha.charAt(1)-48);
            int mm = (fecha.charAt(3)-48)*10+(fecha.charAt(4)-48);
            int aa = (fecha.charAt(6)-48)*1000+(fecha.charAt(7)-48)*100+(fecha.charAt(8)-48)*10+(fecha.charAt(9)-48);
            idSticky = aa*10000+mm*100+dd;
            return idSticky;
        }else return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_mensaje_fecha, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        View view = holder.itemView;
        TextView fecha_global = view.findViewById(R.id.fecha_global);
        fecha_global.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getFont_msg_fecha()));

        String fech = listaDatos.get(position).getFecha();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date = new Date();
        String fechaEntera = sdf.format(date);
        if(fech.equals(fechaEntera)) fecha_global.setText("hoy");
        else if(fech.length()>0){
            int dd = (fech.charAt(0)-48)*10 + (fech.charAt(1)-48);
            int m=(fech.charAt(3)-48)*10+(fech.charAt(4)-48);
            String mm ="";
            switch (m){
                case 1: mm=" de enero"; break;
                case 2: mm=" de febrero"; break;
                case 3: mm=" de marzo"; break;
                case 4: mm=" de abril"; break;
                case 5: mm=" de mayo"; break;
                case 6: mm=" de junio"; break;
                case 7: mm=" de julio"; break;
                case 8: mm=" de agosto"; break;
                case 9: mm=" de septiembre"; break;
                case 10: mm=" de octubre"; break;
                case 11: mm=" de noviembre"; break;
                case 12: mm=" de diciembre"; break;
                default: mm=" del calendario apocalíptico";
            }
            fecha_global.setText(dd+mm);
        }
    }
    //sticky header fin

    @Override
    public int getItemCount() {
        return listaDatos.size();
    }

    public void setOnClickListener(View.OnClickListener l)
    {
        listener=l;
    }
    public void setOnLongClickListener(View.OnLongClickListener l){
        onLongClickListener = l;
    }

    @Override
    public void onClick(View v) {
        if(listener!=null)
        {
            listener.onClick(v);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if(onLongClickListener!=null){
            onLongClickListener.onLongClick(v);
            return true;
        }
        return false;
    }

    public boolean estaPlayAudio(){
        if(mpGlobal!=null && mpGlobal.isPlaying()){
            return true;
        }
        else return false;
    }

    public void detenerPlayAudio(){
        if((wakeLock!=null && wakeLock.isHeld()) || (Audioproximity!=null && Audioproximity.isPlaying())) return;
        else {
            if(mpGlobal!=null){
                if(mpGlobal.isPlaying())
                    mpGlobal.pause();
                mpGlobal.seekTo(0);
                mpGlobal.setOnCompletionListener(null);
                if(timerGlobal!=null){
                    timerGlobal.cancel();
                    timerGlobal = null;
                }
                audio_seekbar_global.setProgress(0);
                audio_seekbar_global=null;
                audio_play_global.setImageResource(R.drawable.audio_play);
                audio_play_global=null;
                mpGlobal=null;
            }
            Utils.clearAudioFocus(audioManager, audioFocusRequest);
            releaseComponents();
        }
    }

    ////////////////////////////////////////////////////////////ViewHolderDatos///////////////////////////////////////////////////
    public class ViewHolderDatos extends RecyclerView.ViewHolder {

        public ViewHolderDatos(@NonNull View itemView) {
            super(itemView);
        }

        public void AsignarDatos(){}
    }

    ////////////////////////////////////////////////////////////ViewHolderDatosTextIzq///////////////////////////////////////////////////
    public class ViewHolderDatosTextIzq extends ViewHolderDatos{

        //lo q esta en el xml del chat izq
        private View est_reenviado, tv_es_editado;
//        private LinearLayout lllll;
        private EmojiTextView mensaje_izq, mensaje_nombre_izq;
        private TextView hora_izq;
        private AppCompatImageView corner_izq;
//        private MaterialCheckBox cb_modo_seleccion;
        private FlexboxLayout fondo_msg_chat;
        private View background_mensaje;

        public ViewHolderDatosTextIzq(@NonNull View itemView) {
            super(itemView);
            mensaje_izq = itemView.findViewById(R.id.mensaje_izq);
            hora_izq = itemView.findViewById(R.id.hora_izq);
            //fondo_msg_chat=itemView.findViewById(R.id.fondo_msg_chat);
//            lllll=itemView.findViewById(R.id.lllll);
            fondo_msg_chat=itemView.findViewById(R.id.fondo_mensaje_izq);
            est_reenviado=itemView.findViewById(R.id.est_reenviado);
            tv_es_editado=itemView.findViewById(R.id.tv_es_editado);
//            cb_modo_seleccion = itemView.findViewById(R.id.cb_modo_seleccion);
            corner_izq=itemView.findViewById(R.id.corner_izq);
            fondo_msg_chat = itemView.findViewById(R.id.fondo_mensaje_izq);
            background_mensaje = itemView.findViewById(R.id.background_mensaje);
            mensaje_nombre_izq = itemView.findViewById(R.id.mensaje_nombre_izq);
        }

        public synchronized void AsignarDatos(boolean margen, ItemChat chat) {
            String u = chat.getMensaje();
            String hor = chat.getHora();
            boolean esReenviado = chat.EsReenviado();

            if(esReenviado) est_reenviado.setVisibility(View.VISIBLE);
            else est_reenviado.setVisibility(View.GONE);

            if(chat.esEditado()) tv_es_editado.setVisibility(View.VISIBLE);
            else tv_es_editado.setVisibility(View.GONE);

            if(modoSeleccion){
//                if(cb_modo_seleccion.getVisibility()!=View.VISIBLE)
//                    cb_modo_seleccion.setVisibility(View.VISIBLE);
                if(chat.estaSeleccionado()){
//                    cb_modo_seleccion.setChecked(true);
                    background_mensaje.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getSel_msj()));
                }
                else{
//                    cb_modo_seleccion.setChecked(false);
                    background_mensaje.setBackgroundColor(Color.TRANSPARENT);
                }
            }else {//if(cb_modo_seleccion.getVisibility()!=View.GONE){
//                cb_modo_seleccion.setVisibility(View.GONE);
                background_mensaje.setBackgroundColor(Color.TRANSPARENT);
            }

            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(Color.parseColor(YouChatApplication.itemTemas.getColor_msg_izq()));
            if(margen){
                corner_izq.setVisibility(View.VISIBLE);
                corner_izq.setSupportImageTintList(stateListColorMsgIzq);
                drawable.setCornerRadii(new float[]{
                        0, 0, curva, curva, curva, curva, curva, curva
                });
            }
            else{
                corner_izq.setVisibility(View.INVISIBLE);
                drawable.setCornerRadii(new float[]{
                        curva, curva, curva, curva, curva, curva, curva, curva
                });
            }

            EmojiInformation emojiInformation = EmojiUtils.emojiInformation(u);
            int res;
            hora_izq.setText(hor);

            if(emojiInformation.isOnlyEmojis && emojiInformation.emojis.size() < 6) {
                res = R.dimen.emoji_size_only_emojis;
                mensaje_izq.setEmojiSizeRes(res, false);
                mensaje_izq.setTextSize(tam_fuente);

                mensaje_izq.setText(u);
            }
            else {
                res = R.dimen.emoji_size_default;
                mensaje_izq.setEmojiSizeRes(res, false);
                mensaje_izq.setTextSize(tam_fuente);

                if(u.length()>500){
                    String text = u;
                    text=text.substring(0,488);
                    text+="... Leer más";
                    String uFinal=text;
                    SpannableString a = new SpannableString(text);
                    a.setSpan(new ForegroundColorSpan(Color.parseColor(YouChatApplication.itemTemas.getFont_texto_resaltado())),492, a.length(), 0);
                    mensaje_izq.setText(uFinal);
                    LinkUtils.autoLink(mensaje_izq, new LinkUtils.OnClickListener() {
                        @Override
                        public void onLinkClicked(String link) {
                            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
                        }
                        @Override
                        public void onClicked() {
                            mensaje_izq.setText(u);
                            fondo_msg_chat.setBackground(drawable);
                        }
                    });
                    mensaje_izq.setText(a);
                }
                else mensaje_izq.setText(u);
            }

            if(chatsActivity.isEsGrupo()){
                mensaje_nombre_izq.setText(dbWorker.obtenerNombreMensajeCorreo(chat.getId(), chat.getEmisor()));
                mensaje_nombre_izq.setTextSize(tam_fuente);
            } else mensaje_nombre_izq.setVisibility(View.GONE);

            fondo_msg_chat.setBackground(drawable);
        }
    }

    ////////////////////////////////////////////////////////////ViewHolderDatosTextDer///////////////////////////////////////////////////
    public class ViewHolderDatosTextDer extends ViewHolderDatos {

        //lo q esta en el xml del chat der
        View fondo_mensaje_der,est_reenviado,tv_es_editado;
        EmojiTextView mensaje_der;
        TextView  hora_der;
        AppCompatImageView estadoView, corner_der;
        LinearLayout fondo_hora_der;
        View background_mensaje;
//        MaterialCheckBox cb_modo_seleccion;

        public ViewHolderDatosTextDer(@NonNull View itemView) {
            super(itemView);
            mensaje_der = itemView.findViewById(R.id.mensaje_der);
            estadoView = itemView.findViewById(R.id.estadoText);
            hora_der = itemView.findViewById(R.id.hora_der);
            background_mensaje= itemView.findViewById(R.id.background_mensaje);
            fondo_hora_der = itemView.findViewById(R.id.fondo_hora_der);
            fondo_mensaje_der=itemView.findViewById(R.id.fondo_mensaje_der);
            est_reenviado=itemView.findViewById(R.id.est_reenviado);
            corner_der=itemView.findViewById(R.id.corner_der);
            tv_es_editado=itemView.findViewById(R.id.tv_es_editado);
//            cb_modo_seleccion = itemView.findViewById(R.id.cb_modo_seleccion);
        }

        public synchronized void AsignarDatos(boolean margen, ItemChat chat) {
            String u = chat.getMensaje();
            int est = chat.getEstado();
            String hor = chat.getHora();
            boolean esReenviado = chat.EsReenviado();

            if(esReenviado) est_reenviado.setVisibility(View.VISIBLE);
            else est_reenviado.setVisibility(View.GONE);

            if(chat.esEditado()) tv_es_editado.setVisibility(View.VISIBLE);
            else tv_es_editado.setVisibility(View.GONE);

            if(modoSeleccion){
                if(chat.estaSeleccionado()) background_mensaje.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getSel_msj()));
                else background_mensaje.setBackgroundColor(Color.TRANSPARENT);
            }
            else background_mensaje.setBackgroundColor(Color.TRANSPARENT);


            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(Color.parseColor(YouChatApplication.itemTemas.getColor_msg_der()));
            if(margen){
                corner_der.setVisibility(View.VISIBLE);
                corner_der.setSupportImageTintList(stateListColorMsgDer);
                drawable.setCornerRadii(new float[]{
                        curva, curva, 0, 0, curva, curva, curva, curva
                });
            }
            else{
                corner_der.setVisibility(View.INVISIBLE);
                drawable.setCornerRadii(new float[]{
                        curva, curva, curva, curva, curva, curva, curva, curva
                });
            }

            EmojiInformation emojiInformation = EmojiUtils.emojiInformation(u);
            int res;
            setStatusImg(est, estadoView);
            hora_der.setText(hor);

            if(emojiInformation.isOnlyEmojis && emojiInformation.emojis.size() < 6) {
                res = R.dimen.emoji_size_only_emojis;
                mensaje_der.setEmojiSizeRes(res, false);
                mensaje_der.setTextSize(tam_fuente);

                mensaje_der.setText(u);
            }
            else {
                res = R.dimen.emoji_size_default;
                mensaje_der.setEmojiSizeRes(res, false);
                mensaje_der.setTextSize(tam_fuente);

                if(u.length()>500){
                    String text = u;
                    text=text.substring(0,488);
                    text+="... Leer más";
                    String uFinal=text;
                    SpannableString a = new SpannableString(text);
                    a.setSpan(new ForegroundColorSpan(Color.parseColor(YouChatApplication.itemTemas.getFont_texto_resaltado())),492, a.length(), 0);
                    mensaje_der.setText(uFinal);
                    LinkUtils.autoLink(mensaje_der, new LinkUtils.OnClickListener() {
                        @Override
                        public void onLinkClicked(String link) {
                            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
                        }
                        @Override
                        public void onClicked() {
                            mensaje_der.setText(u);
                            fondo_mensaje_der.setBackground(drawable);
                        }
                    });
                    mensaje_der.setText(a);
                }
                else mensaje_der.setText(u);
            }

            fondo_mensaje_der.setBackground(drawable);
        }
    }

    ////////////////////////////////////////////////////////////ViewHolderDatosImgIzq///////////////////////////////////////////////////
    public class ViewHolderDatosImgIzq extends ViewHolderDatos {

        //lo q esta en el xml del chat der
        View fondo_msg_chat, tv_es_editado;
        EmojiTextView mensaje_img_izq, mensaje_nombre_izq;
        TextView hora_img_izq, tv_tam_img;
        ShapeableImageView imagen_izq;
        AppCompatImageView corner_izq;
        private View background_mensaje;
        private View contendor_view_descarga;
        private DownloadProgressView progress_view;
        private TextView tv_tam_max;

        public ViewHolderDatosImgIzq(@NonNull View itemView) {
            super(itemView);
            mensaje_img_izq = itemView.findViewById(R.id.mensaje_img_izq);
            imagen_izq = itemView.findViewById(R.id.imagen_izq);
            hora_img_izq=itemView.findViewById(R.id.hora_img_izq);
            fondo_msg_chat=itemView.findViewById(R.id.fondo_msg_chat);
            background_mensaje= itemView.findViewById(R.id.background_mensaje);
            tv_tam_img = itemView.findViewById(R.id.tv_tam_img);
            corner_izq=itemView.findViewById(R.id.corner_izq);
            tv_es_editado=itemView.findViewById(R.id.tv_es_editado);
            mensaje_nombre_izq = itemView.findViewById(R.id.mensaje_nombre_izq);

            contendor_view_descarga=itemView.findViewById(R.id.contendor_view_descarga);
            progress_view=itemView.findViewById(R.id.progress_view);
            tv_tam_max=itemView.findViewById(R.id.tv_tam_max);
        }

        @SuppressLint("RestrictedApi")
        public synchronized void AsignarDatos(boolean margen, ItemChat chat) {
            String id = chat.getId();
            String u = chat.getMensaje();
            String ruta = YouChatApplication.RUTA_ADJUNTOS_CORREO+chat.getRuta_Dato();
            String hor = chat.getHora();
            boolean esReenviado = chat.EsReenviado();

            if(chatsActivity.isEsGrupo()){
                mensaje_nombre_izq.setText(dbWorker.obtenerNombreMensajeCorreo(chat.getId(), chat.getEmisor()));
                mensaje_nombre_izq.setTextSize(tam_fuente);
            } else mensaje_nombre_izq.setVisibility(View.GONE);

            Log.e(TAG, "AsignarDatos: "+ruta);

            if(modoSeleccion){
                if(chat.estaSeleccionado()) background_mensaje.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getSel_msj()));
                else background_mensaje.setBackgroundColor(Color.TRANSPARENT);
            }
            else background_mensaje.setBackgroundColor(Color.TRANSPARENT);

            if(chat.esEditado()) tv_es_editado.setVisibility(View.VISIBLE);
            else tv_es_editado.setVisibility(View.GONE);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(Color.parseColor(YouChatApplication.itemTemas.getColor_msg_izq()));
            if(margen){
                corner_izq.setVisibility(View.VISIBLE);
                corner_izq.setSupportImageTintList(stateListColorMsgIzq);
                drawable.setCornerRadii(new float[]{
                        0, 0, curva, curva, curva, curva, curva, curva
                });
            }
            else{
                corner_izq.setVisibility(View.INVISIBLE);
                drawable.setCornerRadii(new float[]{
                        curva, curva, curva, curva, curva, curva, curva, curva
                });
            }

            hora_img_izq.setText(hor);
            mensaje_img_izq.setTextSize(tam_fuente);

            if(u.replace(" ","").length()!=0){
                EmojiInformation emojiInformation = EmojiUtils.emojiInformation(u);
                int res;

                if(emojiInformation.isOnlyEmojis && emojiInformation.emojis.size() <= 2) {
                    res = R.dimen.emoji_size_single_emoji;
                    mensaje_img_izq.setEmojiSizeRes(res, false);
                    mensaje_img_izq.setTextSize(tam_fuente);

                    mensaje_img_izq.setText(u);
                }
                else if(emojiInformation.isOnlyEmojis && emojiInformation.emojis.size() < 6) {
                    res = R.dimen.emoji_size_only_emojis;
                    mensaje_img_izq.setEmojiSizeRes(res, false);
                    mensaje_img_izq.setTextSize(tam_fuente);

                    mensaje_img_izq.setText(u);
                }
                else {
                    res = R.dimen.emoji_size_default;
                    mensaje_img_izq.setEmojiSizeRes(res, false);
                    mensaje_img_izq.setTextSize(tam_fuente);

                    if(u.length()>500){
                        String text = u;
                        text=text.substring(0,488);
                        text+="... Leer más";
                        String uFinal=text;
                        SpannableString a = new SpannableString(text);
                        a.setSpan(new ForegroundColorSpan(Color.parseColor(YouChatApplication.itemTemas.getFont_texto_resaltado())),492, a.length(), 0);
                        mensaje_img_izq.setText(uFinal);
                        LinkUtils.autoLink(mensaje_img_izq, new LinkUtils.OnClickListener() {
                            @Override
                            public void onLinkClicked(String link) {
                                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
                            }
                            @Override
                            public void onClicked() {
                                mensaje_img_izq.setText(u);
                                fondo_msg_chat.setBackground(drawable);
                            }
                        });
                        mensaje_img_izq.setText(a);
                    }
                    else mensaje_img_izq.setText(u);
                }
                mensaje_img_izq.setVisibility(View.VISIBLE);
            }
            else mensaje_img_izq.setVisibility(View.GONE);

            File file=new File(ruta);
            if(!file.exists()){
                tv_tam_img.setVisibility(View.GONE);
                int[] al = new int[]{(int) maxAnchoImagen,(int) maxAnchoImagen};
//                imagen_izq.setLayoutParams(new RelativeLayout.LayoutParams(al[0],al[1]));
//                imagen_izq.setImageResource(R.drawable.image_placeholder);
                Glide.with(context)
                        .load(R.drawable.image_placeholder).dontAnimate().override(al[0],al[1])
                        .error(R.drawable.image_placeholder).into(imagen_izq);
                new ZoomInImageViewAttacher(imagen_izq,false,true);
                imagen_izq.setOnClickListener(null);

                if(!chat.isDescargado()){
                    contendor_view_descarga.setVisibility(View.VISIBLE);
                    tv_tam_max.setText(Utils.convertirBytes(chat.getPeso()));
                    progress_view.setProgress(0);
                    progress_view.setDownloading(false);
                    progress_view.ponerClick();
                    progress_view.setProgressListener(new Function1<Boolean, Unit>() {
                        @Override
                        public Unit invoke(Boolean aBoolean) {
                            progress_view.quitarClick();
                            chatsActivity.descargarCorreo(chat,progress_view);
                            return null;
                        }
                    });
                }
                else {
                    contendor_view_descarga.setVisibility(View.GONE);
                }
            }
            else {
                if(contendor_view_descarga.getVisibility()==View.VISIBLE){
                    contendor_view_descarga.setVisibility(View.GONE);
                }
                int[] al = Utils.obtenerAnchoLargo(ruta);
//                imagen_izq.setLayoutParams(new RelativeLayout.LayoutParams(al[0],al[1]));
                Glide.with(context)
                        .load(ruta).dontAnimate().override(al[0],al[1])
                        .error(R.drawable.image_placeholder).into(imagen_izq);
                new ZoomInImageViewAttacher(imagen_izq,true);

                tv_tam_img.setVisibility(View.VISIBLE);
                tv_tam_img.setText(Utils.convertirBytes(file.length()));

                imagen_izq.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(modoSeleccion){
                            chatsActivity.actualizarSeleccion(chat);
                        }
                        else chatsActivity.MostrarImagen(ruta,(ImageView) imagen_izq);
                    }
                });
            }

            fondo_msg_chat.setBackground(drawable);

        }
    }

    ////////////////////////////////////////////////////////////ViewHolderDatosImgDer///////////////////////////////////////////////////
    public class ViewHolderDatosImgDer extends ViewHolderDatos {

        //lo q esta en el xml del chat der
        View fondo_msg_chat,est_reenviado;

        EmojiTextView mensaje_img_der;
        TextView hora_img_der, tv_tam_img;
        ShapeableImageView imagen_der;
        AppCompatImageView estadoView, corner_der;
        View progress_bar_carga, tv_es_editado;
        private View background_mensaje;


        public ViewHolderDatosImgDer(@NonNull View itemView) {
            super(itemView);
            mensaje_img_der = itemView.findViewById(R.id.mensaje_img_der);
            imagen_der = itemView.findViewById(R.id.imagen_der);
//            imagen_der.setImageDrawable(null);
            estadoView = itemView.findViewById(R.id.estadoImg);
            hora_img_der=itemView.findViewById(R.id.hora_img_der);
            fondo_msg_chat=itemView.findViewById(R.id.fondo_msg_chat);
            est_reenviado=itemView.findViewById(R.id.est_reenviado);
            background_mensaje= itemView.findViewById(R.id.background_mensaje);
            tv_tam_img = itemView.findViewById(R.id.tv_tam_img);
            progress_bar_carga = itemView.findViewById(R.id.progress_bar_carga);
            corner_der=itemView.findViewById(R.id.corner_der);
            tv_es_editado=itemView.findViewById(R.id.tv_es_editado);
        }

        public synchronized void AsignarDatos(boolean margen, ItemChat chat) {
            String id = chat.getId();
            String u = chat.getMensaje();
            String ruta = chat.getRuta_Dato();
            int est = chat.getEstado();
            String hor = chat.getHora();
            boolean esReenviado = chat.EsReenviado();

            Log.e(TAG, "AsignarDatos: ID: "+ruta);

            if(modoSeleccion){
                if(chat.estaSeleccionado()) background_mensaje.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getSel_msj()));
                else background_mensaje.setBackgroundColor(Color.TRANSPARENT);
            }
            else background_mensaje.setBackgroundColor(Color.TRANSPARENT);

            if(esReenviado) est_reenviado.setVisibility(View.VISIBLE);
            else est_reenviado.setVisibility(View.GONE);

            if(chat.esEditado()) tv_es_editado.setVisibility(View.VISIBLE);
            else tv_es_editado.setVisibility(View.GONE);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(Color.parseColor(YouChatApplication.itemTemas.getColor_msg_der()));
            if(margen){
                corner_der.setVisibility(View.VISIBLE);
                corner_der.setSupportImageTintList(stateListColorMsgDer);
                drawable.setCornerRadii(new float[]{
                        curva, curva, 0, 0, curva, curva, curva, curva
                });
            }
            else{
                corner_der.setVisibility(View.INVISIBLE);
                drawable.setCornerRadii(new float[]{
                        curva, curva, curva, curva, curva, curva, curva, curva
                });
            }

            EmojiInformation emojiInformation = EmojiUtils.emojiInformation(u);
            int res;
            setStatusImg(est, estadoView);
            hora_img_der.setText(hor);
            if(est==ItemChat.ESTADO_ESPERANDO) progress_bar_carga.setVisibility(View.VISIBLE);
            else progress_bar_carga.setVisibility(View.GONE);

            if(u.replace(" ","").length()!=0){
                if(emojiInformation.isOnlyEmojis && emojiInformation.emojis.size() <= 2) {
                    res = R.dimen.emoji_size_single_emoji;
                    mensaje_img_der.setEmojiSizeRes(res, false);
                    mensaje_img_der.setTextSize(tam_fuente);

                    mensaje_img_der.setText(u);
                }
                else if(emojiInformation.isOnlyEmojis && emojiInformation.emojis.size() < 6) {
                    res = R.dimen.emoji_size_only_emojis;
                    mensaje_img_der.setEmojiSizeRes(res, false);
                    mensaje_img_der.setTextSize(tam_fuente);

                    mensaje_img_der.setText(u);
                }
                else {
                    res = R.dimen.emoji_size_default;
                    mensaje_img_der.setEmojiSizeRes(res, false);
                    mensaje_img_der.setTextSize(tam_fuente);

                    if(u.length()>500){
                        String text = u;
                        text=text.substring(0,488);
                        text+="... Leer más";
                        String uFinal=text;
                        SpannableString a = new SpannableString(text);
                        a.setSpan(new ForegroundColorSpan(Color.parseColor(YouChatApplication.itemTemas.getFont_texto_resaltado())),492, a.length(), 0);
                        mensaje_img_der.setText(uFinal);
                        LinkUtils.autoLink(mensaje_img_der, new LinkUtils.OnClickListener() {
                            @Override
                            public void onLinkClicked(String link) {
                                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
                            }
                            @Override
                            public void onClicked() {
                                mensaje_img_der.setText(u);
                                fondo_msg_chat.setBackground(drawable);
                            }
                        });
                        mensaje_img_der.setText(a);
                    }
                    else mensaje_img_der.setText(u);
                }
                mensaje_img_der.setVisibility(View.VISIBLE);
            }
            else mensaje_img_der.setVisibility(View.GONE);

            File file = new File(ruta);
            if (!file.exists()) {
                tv_tam_img.setVisibility(View.GONE);
                int[] al = new int[]{(int) maxAnchoImagen,(int) maxAnchoImagen};
//                imagen_der.setLayoutParams(new RelativeLayout.LayoutParams(al[0],al[1]));
//                imagen_der.setImageResource(R.drawable.image_placeholder);
                Glide.with(context)
                        .load(R.drawable.image_placeholder).dontAnimate().override(al[0],al[1])
                        .error(R.drawable.image_placeholder).into(imagen_der);
                new ZoomInImageViewAttacher(imagen_der,false,true);
                imagen_der.setOnClickListener(null);
            }
            else {
                int[] al = Utils.obtenerAnchoLargo(ruta);
//                imagen_der.setLayoutParams(new RelativeLayout.LayoutParams(al[0],al[1]));
                Glide.with(context)
                        .load(ruta).dontAnimate().override(al[0],al[1])
                        .error(R.drawable.image_placeholder).into(imagen_der);
                new ZoomInImageViewAttacher(imagen_der,true);

                tv_tam_img.setVisibility(View.VISIBLE);
                tv_tam_img.setText(Utils.convertirBytes(file.length()));

                imagen_der.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(modoSeleccion){
                            chatsActivity.actualizarSeleccion(chat);
                        }
                        else chatsActivity.MostrarImagen(ruta,(ImageView) imagen_der);
                    }
                });
            }

            fondo_msg_chat.setBackground(drawable);
        }
    }

    ////////////////////////////////////////////////////////////ViewHolderDatosAudioIzq///////////////////////////////////////////////////
    public class ViewHolderDatosAudioIzq extends ViewHolderDatos{

        View fondo_msg_chat;
        AppCompatImageView corner_izq;
        
        CircleImageView audio_play_izq;
        TextView audio_duration_izq,hora_izq;
        AudioWaveView audio_seekbar_izq;

        private EmojiTextView mensaje_nombre_izq;

        private View background_mensaje;
        boolean isPlay, pause, end, estaTimer;
        int currentPosition;
        Timer timer;

        private DownloadProgressView progress_view;

        public ViewHolderDatosAudioIzq(@NonNull View itemView) {
            super(itemView);
            audio_play_izq=itemView.findViewById(R.id.audio_play_izq);
            audio_seekbar_izq=itemView.findViewById(R.id.audio_seekbar_izq);
            audio_duration_izq=itemView.findViewById(R.id.audio_duration_izq);
            hora_izq=itemView.findViewById(R.id.hora_izq);
            fondo_msg_chat=itemView.findViewById(R.id.fondo_msg_chat);
            background_mensaje= itemView.findViewById(R.id.background_mensaje);
            corner_izq=itemView.findViewById(R.id.corner_izq);
            mensaje_nombre_izq = itemView.findViewById(R.id.mensaje_nombre_izq);
            progress_view=itemView.findViewById(R.id.progress_view);
        }

        public synchronized void AsignarDatos(boolean margen, ItemChat chat) {
            String ruta = YouChatApplication.RUTA_ADJUNTOS_CORREO+chat.getRuta_Dato();
            String hor = chat.getHora();
            boolean esReenviado = chat.EsReenviado();

            if(chatsActivity.isEsGrupo()){
                mensaje_nombre_izq.setText(dbWorker.obtenerNombreMensajeCorreo(chat.getId(), chat.getEmisor()));
                mensaje_nombre_izq.setTextSize(tam_fuente);
            } else mensaje_nombre_izq.setVisibility(View.GONE);

            if(modoSeleccion){
                if(chat.estaSeleccionado()) background_mensaje.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getSel_msj()));
                else background_mensaje.setBackgroundColor(Color.TRANSPARENT);
            }
            else background_mensaje.setBackgroundColor(Color.TRANSPARENT);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(Color.parseColor(YouChatApplication.itemTemas.getColor_msg_izq()));
            if(margen){
                corner_izq.setVisibility(View.VISIBLE);
                corner_izq.setSupportImageTintList(stateListColorMsgIzq);
                drawable.setCornerRadii(new float[]{
                        0, 0, curva, curva, curva, curva, curva, curva
                });
            }
            else{
                corner_izq.setVisibility(View.INVISIBLE);
                drawable.setCornerRadii(new float[]{
                        curva, curva, curva, curva, curva, curva, curva, curva
                });
            }

            MediaPlayer mp = new MediaPlayer();
            File f = new File(ruta);
            if (f.canRead()) {
                if(progress_view.getVisibility()==View.VISIBLE)
                    progress_view.setVisibility(View.GONE);
                if(audio_play_izq.getVisibility()!=View.VISIBLE)
                    audio_play_izq.setVisibility(View.VISIBLE);
                isPlay=false;
                audio_play_izq.setImageResource(R.drawable.audio_play);
                audio_play_izq.setEnabled(true);
                audio_seekbar_izq.setEnabled(true);
                audio_seekbar_izq.setProgress(0);
                try {
                    mp.setDataSource(ruta);
                    mp.prepare();
                    audio_seekbar_izq.setRawData(readBytesAudio(new FileInputStream(f), (int)f.length()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                long dur=mp.getDuration()/1000;
                long seg=dur%60;
                long minu=dur/60;
                if (seg<10) audio_duration_izq.setText(minu+":0"+seg);
                else audio_duration_izq.setText(minu+":"+seg);

                audio_play_izq.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(modoSeleccion){
                            chatsActivity.actualizarSeleccion(chat);
                        }
                        else {
                            if(mpGlobal!=null && mpGlobal.equals(mp)){
                                playOrPause();
                            }
                            else {
                                reproducirAudio(mp, ruta, audio_play_izq, audio_seekbar_izq);
                            }
                        }
                    }
                });
                audio_seekbar_izq.setOnProgressChanged(new Function2<Float, Boolean, Unit>() {
                    @Override
                    public Unit invoke(Float progress, Boolean byUser) {
                        if (byUser && mp.equals(mpGlobal)) {
                            float value = (float)(mpGlobal.getDuration()*progress)/100;
                            mpGlobal.seekTo((int) value);
                        }
                        return null;
                    }
                });
            }
            else {
                if(chat.isDescargado()){
                    if(progress_view.getVisibility()==View.VISIBLE)
                        progress_view.setVisibility(View.GONE);
                    if(audio_play_izq.getVisibility()!=View.VISIBLE)
                        audio_play_izq.setVisibility(View.VISIBLE);
                    audio_play_izq.setImageResource(R.drawable.send_error);
                    audio_play_izq.setEnabled(false);
                    audio_seekbar_izq.setEnabled(false);
                    audio_duration_izq.setText("");
                }
                else {
                    audio_play_izq.setVisibility(View.INVISIBLE);
                    progress_view.setVisibility(View.VISIBLE);
                    audio_duration_izq.setText(Utils.convertirBytes(chat.getPeso()));
                    progress_view.ponerClick();
                    progress_view.setProgress(0);
                    progress_view.setDownloading(false);
                    progress_view.setProgressListener(new Function1<Boolean, Unit>() {
                        @Override
                        public Unit invoke(Boolean aBoolean) {
                            progress_view.quitarClick();
                            chatsActivity.descargarCorreo(chat,progress_view);
                            return null;
                        }
                    });
                }

            }
            fondo_msg_chat.setBackground(drawable);
        }
    }

    ////////////////////////////////////////////////////////////ViewHolderDatosAudioDer///////////////////////////////////////////////////
    public class ViewHolderDatosAudioDer extends ViewHolderDatos {

        View fondo_msg_chat, est_reenviado;
        CircleImageView audio_play_der;
        AppCompatImageView estadoView, corner_der;
        TextView audio_duration_der,hora_der;
//        Slider audio_seekbar_der;
        AudioWaveView audio_seekbar_der;

        boolean isPlay, pause;
        private View background_mensaje;

        public ViewHolderDatosAudioDer(@NonNull View itemView) {
            super(itemView);
            audio_play_der=itemView.findViewById(R.id.audio_play_der);
            audio_seekbar_der=itemView.findViewById(R.id.audio_seekbar_der);
            audio_duration_der=itemView.findViewById(R.id.audio_duration_der);
            hora_der=itemView.findViewById(R.id.hora_der);
            estadoView=itemView.findViewById(R.id.estadoText);
            fondo_msg_chat=itemView.findViewById(R.id.fondo_msg_chat);
            est_reenviado=itemView.findViewById(R.id.est_reenviado);
            background_mensaje= itemView.findViewById(R.id.background_mensaje);
            corner_der=itemView.findViewById(R.id.corner_der);
        }

        public synchronized void AsignarDatos(boolean margen, ItemChat chat) {
            String ruta = chat.getRuta_Dato();
            String hor = chat.getHora();
            int est = chat.getEstado();
            boolean esReenviado = chat.EsReenviado();

            if(modoSeleccion){
                if(chat.estaSeleccionado()) background_mensaje.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getSel_msj()));
                else background_mensaje.setBackgroundColor(Color.TRANSPARENT);
            }
            else background_mensaje.setBackgroundColor(Color.TRANSPARENT);

            if(esReenviado) est_reenviado.setVisibility(View.VISIBLE);
            else est_reenviado.setVisibility(View.GONE);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(Color.parseColor(YouChatApplication.itemTemas.getColor_msg_der()));
            if(margen){
                corner_der.setVisibility(View.VISIBLE);
                corner_der.setSupportImageTintList(stateListColorMsgDer);
                drawable.setCornerRadii(new float[]{
                        curva, curva, 0, 0, curva, curva, curva, curva
                });
            }
            else{
                corner_der.setVisibility(View.INVISIBLE);
                drawable.setCornerRadii(new float[]{
                        curva, curva, curva, curva, curva, curva, curva, curva
                });
            }

            setStatusImg(est, estadoView);
            hora_der.setText(hor);

            final MediaPlayer mp = new MediaPlayer();
            File f = new File(ruta);
            if (f.canRead()) {
                isPlay=false;
                audio_play_der.setImageResource(R.drawable.audio_play);
                audio_play_der.setEnabled(true);
                audio_seekbar_der.setEnabled(true);
                audio_seekbar_der.setProgress(0);
                try {
                    mp.setDataSource(ruta);
                    mp.prepare();
                    audio_seekbar_der.setRawData(readBytesAudio(new FileInputStream(f), (int)f.length()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                long dur=mp.getDuration()/1000;
                long seg=dur%60;
                long minu=dur/60;
                if (seg<10) audio_duration_der.setText(minu+":0"+seg);
                else audio_duration_der.setText(minu+":"+seg);
                audio_seekbar_der.setProgress(0);

                audio_play_der.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(modoSeleccion){
                            chatsActivity.actualizarSeleccion(chat);
                        }
                        else {
                            if(mpGlobal!=null && mpGlobal.equals(mp)){
                                playOrPause();
                            }
                            else {
                                reproducirAudio(mp, ruta, audio_play_der, audio_seekbar_der);
                            }
                        }
                    }
                });
                audio_seekbar_der.setOnProgressChanged(new Function2<Float, Boolean, Unit>() {
                    @Override
                    public Unit invoke(Float progress, Boolean byUser) {
                        if (byUser && mp.equals(mpGlobal)) {
                            float value = (float)(mpGlobal.getDuration()*progress)/100;
                            mpGlobal.seekTo((int) value);
                        }
                        return null;
                    }
                });

            }
            else {
                audio_play_der.setImageResource(R.drawable.send_error);
                audio_play_der.setEnabled(false);
                audio_seekbar_der.setEnabled(false);
            }
            fondo_msg_chat.setBackground(drawable);
        }
    }

    ////////////////////////////////////////////////////////////ViewHolderDatosArchivoIzq///////////////////////////////////////////////////
    public class ViewHolderDatosArchivoIzq extends ViewHolderDatos{

        View fondo_msg_chat;

        ImageView file_image_izq;
        TextView hora_izq, file_nombre_izq, file_peso_izq, file_ext_izq;
        EmojiTextView mensaje_nombre_izq;
        AppCompatImageView corner_izq;
        private View background_mensaje;

        private DownloadProgressView progress_view;

        public ViewHolderDatosArchivoIzq(@NonNull View itemView) {
            super(itemView);
            hora_izq=itemView.findViewById(R.id.hora_izq);
            fondo_msg_chat=itemView.findViewById(R.id.fondo_msg_chat);
            background_mensaje= itemView.findViewById(R.id.background_mensaje);
            file_nombre_izq=itemView.findViewById(R.id.file_nombre_izq);
            file_peso_izq=itemView.findViewById(R.id.file_peso_izq);
            file_ext_izq=itemView.findViewById(R.id.file_ext_izq);

            corner_izq=itemView.findViewById(R.id.corner_izq);
            mensaje_nombre_izq = itemView.findViewById(R.id.mensaje_nombre_izq);

            file_image_izq=itemView.findViewById(R.id.file_image_izq);
            progress_view=itemView.findViewById(R.id.progress_view);
        }

        public synchronized void AsignarDatos(boolean margen, ItemChat chat) {

            String rutaArchivo = YouChatApplication.RUTA_ADJUNTOS_CORREO+chat.getRuta_Dato();
             String hor = chat.getHora();

            if(chatsActivity.isEsGrupo()){
                mensaje_nombre_izq.setText(dbWorker.obtenerNombreMensajeCorreo(chat.getId(), chat.getEmisor()));
                mensaje_nombre_izq.setTextSize(tam_fuente);
            } else mensaje_nombre_izq.setVisibility(View.GONE);

            if(modoSeleccion){
                if(chat.estaSeleccionado()) background_mensaje.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getSel_msj()));
                else background_mensaje.setBackgroundColor(Color.TRANSPARENT);
            }
            else background_mensaje.setBackgroundColor(Color.TRANSPARENT);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(Color.parseColor(YouChatApplication.itemTemas.getColor_msg_izq()));
            if(margen){
                corner_izq.setVisibility(View.VISIBLE);
                corner_izq.setSupportImageTintList(stateListColorMsgIzq);
                drawable.setCornerRadii(new float[]{
                        0, 0, curva, curva, curva, curva, curva, curva
                });
            }
            else{
                corner_izq.setVisibility(View.INVISIBLE);
                drawable.setCornerRadii(new float[]{
                        curva, curva, curva, curva, curva, curva, curva, curva
                });
            }

            hora_izq.setText(hor);
            file_nombre_izq.setTextSize(tam_fuente);

            File file = new File(rutaArchivo);
            if(file.exists()){
                progress_view.setVisibility(View.GONE);
                file_image_izq.setVisibility(View.VISIBLE);
                file_ext_izq.setVisibility(View.VISIBLE);
                String extension = rutaArchivo.substring(rutaArchivo.lastIndexOf(".")+1).toUpperCase();
                file_ext_izq.setText(extension);
                file_nombre_izq.setText(file.getName());
                file_peso_izq.setText(Utils.convertirBytes(file.length()));

                fondo_msg_chat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.setEnabled(false);
                        if(modoSeleccion){
                            chatsActivity.actualizarSeleccion(chat);
                        }
                        else chatsActivity.abrirArchivoEn(rutaArchivo);
                        v.setEnabled(true);
                    }
                });
            }
            else {
                if(chat.isDescargado()){
                    progress_view.setVisibility(View.GONE);
                    file_image_izq.setVisibility(View.VISIBLE);
                    file_ext_izq.setVisibility(View.GONE);
                    file_nombre_izq.setText("No encontrado");
                    file_peso_izq.setText("0Kb");
                }
                else {
                    progress_view.setVisibility(View.VISIBLE);
                    file_image_izq.setVisibility(View.INVISIBLE);
                    file_ext_izq.setVisibility(View.GONE);
                    file_nombre_izq.setText("Toque para descargar");
                    file_peso_izq.setText(Utils.convertirBytes(chat.getPeso()));

                    progress_view.ponerClick();
                    progress_view.setProgress(0);
                    progress_view.setDownloading(false);
                    progress_view.setProgressListener(new Function1<Boolean, Unit>() {
                        @Override
                        public Unit invoke(Boolean aBoolean) {
                            progress_view.quitarClick();
                            chatsActivity.descargarCorreo(chat,progress_view);
                            return null;
                        }
                    });
                }

            }
            fondo_msg_chat.setBackground(drawable);
        }
    }

    ////////////////////////////////////////////////////////////ViewHolderDatosArchivoDer///////////////////////////////////////////////////
    public class ViewHolderDatosArchivoDer extends ViewHolderDatos {

        View fondo_msg_chat, est_reenviado;

        AppCompatImageView estadoView, corner_der;
        TextView hora_der, file_nombre_der, file_peso_der, file_ext_der;
        View progress_bar_carga;
        private View background_mensaje;

        public ViewHolderDatosArchivoDer(@NonNull View itemView) {
            super(itemView);
            hora_der=itemView.findViewById(R.id.hora_der);
            estadoView=itemView.findViewById(R.id.estadoText);
            fondo_msg_chat=itemView.findViewById(R.id.fondo_msg_chat);
            est_reenviado=itemView.findViewById(R.id.est_reenviado);
            background_mensaje= itemView.findViewById(R.id.background_mensaje);
            file_nombre_der=itemView.findViewById(R.id.file_nombre_der);
            file_peso_der=itemView.findViewById(R.id.file_peso_der);
            file_ext_der=itemView.findViewById(R.id.file_ext_der);

            progress_bar_carga = itemView.findViewById(R.id.progress_bar_carga);
            corner_der=itemView.findViewById(R.id.corner_der);
        }

        public synchronized void AsignarDatos(boolean margen, ItemChat chat) {

            String rutaArchivo = chat.getRuta_Dato();
            String hor = chat.getHora();
            int est = chat.getEstado();
            boolean esReenviado = chat.EsReenviado();

            if(modoSeleccion){
                if(chat.estaSeleccionado()) background_mensaje.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getSel_msj()));
                else background_mensaje.setBackgroundColor(Color.TRANSPARENT);
            }
            else background_mensaje.setBackgroundColor(Color.TRANSPARENT);

            if(esReenviado) est_reenviado.setVisibility(View.VISIBLE);
            else est_reenviado.setVisibility(View.GONE);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(Color.parseColor(YouChatApplication.itemTemas.getColor_msg_der()));
            if(margen){
                corner_der.setVisibility(View.VISIBLE);
                corner_der.setSupportImageTintList(stateListColorMsgDer);
                drawable.setCornerRadii(new float[]{
                        curva, curva, 0, 0, curva, curva, curva, curva
                });
            }
            else{
                corner_der.setVisibility(View.INVISIBLE);
                drawable.setCornerRadii(new float[]{
                        curva, curva, curva, curva, curva, curva, curva, curva
                });
            }

            file_nombre_der.setTextSize(tam_fuente);
            hora_der.setText(hor);
            setStatusImg(est, estadoView);
            if(est==ItemChat.ESTADO_ESPERANDO) progress_bar_carga.setVisibility(View.VISIBLE);
            else progress_bar_carga.setVisibility(View.GONE);

            File file = new File(rutaArchivo);
            if(file.exists()){
                file_ext_der.setVisibility(View.VISIBLE);
                String extension = rutaArchivo.substring(rutaArchivo.lastIndexOf(".")+1).toUpperCase();
                file_ext_der.setText(extension);

                file_nombre_der.setText(file.getName());
                file_peso_der.setText(Utils.convertirBytes(file.length()));

                fondo_msg_chat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.setEnabled(false);
                        if(modoSeleccion){
                            chatsActivity.actualizarSeleccion(chat);
                        }
                        else chatsActivity.abrirArchivoEn(rutaArchivo);
                        v.setEnabled(true);
                    }
                });
            }else {
                file_ext_der.setVisibility(View.GONE);
                file_nombre_der.setText("No encontrado");
                file_peso_der.setText("0Kb");
            }
            fondo_msg_chat.setBackground(drawable);
        }
    }

    ////////////////////////////////////////////////////////////ViewHolderDatosStickerIzq///////////////////////////////////////////////////
    public class ViewHolderDatosStickerIzq extends ViewHolderDatos {

        //lo q esta en el xml del chat izq
        LottieAnimationView sticker_izq;
        View view1,view2;
        TextView hora_izq;
        private View background_mensaje;

        private View contendor_view_descarga, sticker_empty;
        private DownloadProgressView progress_view;
        private TextView tv_tam_max;

        public ViewHolderDatosStickerIzq(@NonNull View itemView) {
            super(itemView);
            sticker_izq = itemView.findViewById(R.id.sticker_izq);
            view1 = itemView.findViewById(R.id.view1);
            view2 = itemView.findViewById(R.id.view2);
            hora_izq = itemView.findViewById(R.id.hora_izq);
            background_mensaje= itemView.findViewById(R.id.background_mensaje);

            sticker_empty=itemView.findViewById(R.id.sticker_empty);
            contendor_view_descarga=itemView.findViewById(R.id.contenedor_view_descarga);
            progress_view=itemView.findViewById(R.id.progress_view);
            tv_tam_max=itemView.findViewById(R.id.tv_tam_max);
        }

        public synchronized void AsignarDatos(ItemChat chat) {
            boolean reenviado = chat.EsReenviado();
            String hor = chat.getHora();
            String idSticker = chat.getId_msg_resp();

            if(modoSeleccion){
                if(chat.estaSeleccionado()) background_mensaje.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getSel_msj()));
                else background_mensaje.setBackgroundColor(Color.TRANSPARENT);
            }
            else background_mensaje.setBackgroundColor(Color.TRANSPARENT);

            File tgs = new File(YouChatApplication.RUTA_ADJUNTOS_CORREO+chat.getRuta_Dato());
            if(tgs.exists()){
                contendor_view_descarga.setVisibility(View.GONE);
                sticker_empty.setVisibility(View.GONE);
                File stickerCache =  Convertidor.obtenerFileStickerCache(tgs.getParent(), tgs.getName());
                boolean exist = true;
                try {
                    if(stickerCache!=null && !stickerCache.exists()){
                        GZIPInputStream zipInputStream = new GZIPInputStream(new FileInputStream(tgs));
                        byte[] buffer = new byte[1024];
                        int count;
                        FileOutputStream fileOutputStream = new FileOutputStream(stickerCache);
                        while ((count = zipInputStream.read(buffer)) != -1) {
                            fileOutputStream.write(buffer, 0, count);
                        }
                        fileOutputStream.close();
                        zipInputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    exist = false;
                }
                if(exist && stickerCache!=null && !stickerCache.isDirectory()){
                    try {
                        InputStream inputStream = new FileInputStream(stickerCache);
                        LottieTask<LottieComposition> l = LottieCompositionFactory
                                .fromJsonInputStream(inputStream, null);
                        l.addListener(new LottieListener<LottieComposition>() {
                            @Override
                            public void onResult(LottieComposition result) {
                                sticker_izq.setComposition(result);
                                if(YouChatApplication.animaciones_chat)
                                    sticker_izq.resumeAnimation();
                                sticker_izq.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(modoSeleccion){
                                            chatsActivity.actualizarSeleccion(chat);
                                        }
                                        else{
                                            if(sticker_izq.isAnimating()) sticker_izq.pauseAnimation();
                                            else sticker_izq.resumeAnimation();
                                        }
                                    }
                                });
                            }
                        });
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
            else {
                sticker_empty.setVisibility(View.VISIBLE);
                if(!chat.isDescargado()){
                    contendor_view_descarga.setVisibility(View.VISIBLE);
                    tv_tam_max.setText(Utils.convertirBytes(chat.getPeso()));
                    progress_view.setProgress(0);
                    progress_view.setDownloading(false);
                    progress_view.ponerClick();
                    progress_view.setProgressListener(new Function1<Boolean, Unit>() {
                        @Override
                        public Unit invoke(Boolean aBoolean) {
                            progress_view.quitarClick();
                            chatsActivity.descargarCorreo(chat,progress_view);
                            return null;
                        }
                    });
                }
                else {
                    contendor_view_descarga.setVisibility(View.GONE);
                }
            }

            if(reenviado){
                view1.setVisibility(View.VISIBLE);
                view2.setVisibility(View.VISIBLE);
            }
            else{
                view1.setVisibility(View.GONE);
                view2.setVisibility(View.GONE);
            }
            hora_izq.setText(hor);
        }
    }

    ////////////////////////////////////////////////////////////ViewHolderDatosStickerDer///////////////////////////////////////////////////
    public class ViewHolderDatosStickerDer extends ViewHolderDatos {

        //lo q esta en el xml del chat izq
        LottieAnimationView sticker_der;
        View view1,view2;
        TextView hora_der;
        AppCompatImageView estadoView;
        private View background_mensaje, sticker_empty;

        public ViewHolderDatosStickerDer(@NonNull View itemView) {
            super(itemView);
            sticker_der = itemView.findViewById(R.id.sticker_der);
            view1 = itemView.findViewById(R.id.view1);
            view2 = itemView.findViewById(R.id.view2);
            hora_der = itemView.findViewById(R.id.hora_der);
            estadoView = itemView.findViewById(R.id.estadoImg);
            background_mensaje= itemView.findViewById(R.id.background_mensaje);
            sticker_empty= itemView.findViewById(R.id.sticker_empty);
        }

        @SuppressLint("RestrictedApi")
        public synchronized void AsignarDatos(ItemChat chat) {
            boolean reenviado = chat.EsReenviado();
            String hor = chat.getHora();
            int est = chat.getEstado();
            String idSticker = chat.getId_msg_resp();

            if(modoSeleccion){
                if(chat.estaSeleccionado()) background_mensaje.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getSel_msj()));
                else background_mensaje.setBackgroundColor(Color.TRANSPARENT);
            }
            else background_mensaje.setBackgroundColor(Color.TRANSPARENT);

            File tgs = new File(chat.getRuta_Dato());
            if(tgs.exists()){
                sticker_empty.setVisibility(View.GONE);
                File stickerCache =  Convertidor.obtenerFileStickerCache(tgs.getParent(), tgs.getName());
                boolean exist = true;
                try {
                    if(stickerCache!=null && !stickerCache.exists()){
                        GZIPInputStream zipInputStream = new GZIPInputStream(new FileInputStream(tgs));
                        byte[] buffer = new byte[1024];
                        int count;
                        FileOutputStream fileOutputStream = new FileOutputStream(stickerCache);
                        while ((count = zipInputStream.read(buffer)) != -1) {
                            fileOutputStream.write(buffer, 0, count);
                        }
                        fileOutputStream.close();
                        zipInputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    exist = false;
                }
                if(exist && stickerCache!=null && !stickerCache.isDirectory()){
                    try {
                        InputStream inputStream = new FileInputStream(stickerCache);
                        LottieTask<LottieComposition> l = LottieCompositionFactory
                                .fromJsonInputStream(inputStream, null);
                        l.addListener(new LottieListener<LottieComposition>() {
                            @Override
                            public void onResult(LottieComposition result) {
                                sticker_der.setComposition(result);
                                if(YouChatApplication.animaciones_chat)
                                    sticker_der.resumeAnimation();
                                sticker_der.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(modoSeleccion){
                                            chatsActivity.actualizarSeleccion(chat);
                                        }
                                        else{
                                            if(sticker_der.isAnimating()) sticker_der.pauseAnimation();
                                            else sticker_der.resumeAnimation();
                                        }
                                    }
                                });
                            }
                        });
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
            else sticker_empty.setVisibility(View.VISIBLE);

            if(reenviado){
                view1.setVisibility(View.VISIBLE);
                view2.setVisibility(View.VISIBLE);
            }
            else{
                view1.setVisibility(View.GONE);
                view2.setVisibility(View.GONE);
            }

            if(est==ItemChat.ESTADO_ESPERANDO) {
                estadoView.setImageResource(R.drawable.time_circle);
                estadoView.setSupportImageTintList(stateListEstadoViewBlanco);
            }
            else if(est==ItemChat.ESTADO_ERROR)
            {
                estadoView.setImageResource(R.drawable.msg_est_error);
                estadoView.setSupportImageTintList(stateListColorBtn);
            }
            else if(est==ItemChat.ESTADO_ENVIADO)
            {
                estadoView.setImageResource(R.drawable.msg_est_enviado);
                estadoView.setSupportImageTintList(stateListEstadoViewBlanco);
            }
            else if(est==ItemChat.ESTADO_RECIBIDO)
            {
                estadoView.setImageResource(R.drawable.msg_est_recibido);
                estadoView.setSupportImageTintList(stateListEstadoViewBlanco);
            }
            else {
                estadoView.setImageResource(R.drawable.msg_est_recibido);
                estadoView.setSupportImageTintList(stateListColorBtn);
            }

            hora_der.setText(hor);
        }
    }

    ////////////////////////////////////////////////////////////ViewHolderDatosTextIzqEmoji///////////////////////////////////////////////////
    public class ViewHolderDatosTextIzqEmoji extends ViewHolderDatos {

        private View view1,view2;
        private EmojiTextView mensaje_izq;
        private TextView hora_izq;
        private AppCompatImageView corner_izq;
        private View background_mensaje;

        public ViewHolderDatosTextIzqEmoji(@NonNull View itemView) {
            super(itemView);
            mensaje_izq = itemView.findViewById(R.id.mensaje_izq);
            hora_izq = itemView.findViewById(R.id.hora_izq);
            view1=itemView.findViewById(R.id.view1);
            view2=itemView.findViewById(R.id.view2);
            corner_izq=itemView.findViewById(R.id.corner_izq);
            background_mensaje = itemView.findViewById(R.id.background_mensaje);
        }

        public synchronized void AsignarDatos(boolean margen, ItemChat chat) {
            String u = chat.getMensaje();
            String hor = chat.getHora();
            boolean esReenviado = chat.EsReenviado();

            if(esReenviado){
                view1.setVisibility(View.VISIBLE);
                view2.setVisibility(View.VISIBLE);
            }
            else{
                view1.setVisibility(View.GONE);
                view2.setVisibility(View.GONE);
            }
            hora_izq.setText(hor);

            if(modoSeleccion){
                if(chat.estaSeleccionado()){
                    background_mensaje.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getSel_msj()));
                }
                else{
//                    cb_modo_seleccion.setChecked(false);
                    background_mensaje.setBackgroundColor(Color.TRANSPARENT);
                }
            }else {//if(cb_modo_seleccion.getVisibility()!=View.GONE){
//                cb_modo_seleccion.setVisibility(View.GONE);
                background_mensaje.setBackgroundColor(Color.TRANSPARENT);
            }

            corner_izq.setVisibility(View.INVISIBLE);

            int res = R.dimen.emoji_size_single_emoji;
            mensaje_izq.setEmojiSizeRes(res, false);
            mensaje_izq.setText(u);
            mensaje_izq.setTextSize(tam_fuente);

        }
    }

    ////////////////////////////////////////////////////////////ViewHolderDatosTextDerEmoji///////////////////////////////////////////////////
    public class ViewHolderDatosTextDerEmoji extends ViewHolderDatos {

        View est_reenviado, view1,view2;
        EmojiTextView mensaje_der;
        TextView  hora_der;
        AppCompatImageView corner_der,estadoView;
        View background_mensaje;

        public ViewHolderDatosTextDerEmoji(@NonNull View itemView) {
            super(itemView);
            mensaje_der = itemView.findViewById(R.id.mensaje_der);
            estadoView = itemView.findViewById(R.id.estadoImg);
            hora_der = itemView.findViewById(R.id.hora_der);
            background_mensaje= itemView.findViewById(R.id.background_mensaje);
            est_reenviado=itemView.findViewById(R.id.est_reenviado);
            corner_der=itemView.findViewById(R.id.corner_der);
            view1=itemView.findViewById(R.id.view1);
            view2=itemView.findViewById(R.id.view2);
        }

        public synchronized void AsignarDatos(boolean margen, ItemChat chat) {
            String u = chat.getMensaje();
            int est = chat.getEstado();
            String hor = chat.getHora();
            view1.setVisibility(View.GONE);
            view2.setVisibility(View.GONE);

            if(modoSeleccion){
                if(chat.estaSeleccionado()) background_mensaje.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getSel_msj()));
                else background_mensaje.setBackgroundColor(Color.TRANSPARENT);
            }
            else background_mensaje.setBackgroundColor(Color.TRANSPARENT);

            setStatusImg(est, estadoView);
            hora_der.setText(hor);

            int res = R.dimen.emoji_size_single_emoji;
            mensaje_der.setEmojiSizeRes(res, false);
            mensaje_der.setTextSize(tam_fuente);
            mensaje_der.setText(u);
            corner_der.setVisibility(View.INVISIBLE);
        }
    }

    ////////////////////////////////////////////////////////////ViewHolderDatosTextIzqEmojiAnimado///////////////////////////////////////////////////
    public class ViewHolderDatosTextIzqEmojiAnimado extends ViewHolderDatos {

        //lo q esta en el xml del chat izq
        LottieAnimationView sticker_izq;
        View view1,view2;
        TextView hora_izq;
        private View background_mensaje;

        public ViewHolderDatosTextIzqEmojiAnimado(@NonNull View itemView) {
            super(itemView);
            sticker_izq = itemView.findViewById(R.id.sticker_izq);
            view1 = itemView.findViewById(R.id.view1);
            view2 = itemView.findViewById(R.id.view2);
            hora_izq = itemView.findViewById(R.id.hora_izq);
            background_mensaje= itemView.findViewById(R.id.background_mensaje);
        }

        public synchronized void AsignarDatos(ItemChat chat) {
            boolean reenviado = chat.EsReenviado();
            String hor = chat.getHora();
            String idSticker = chat.getId_msg_resp();

            if(modoSeleccion){
                if(chat.estaSeleccionado()) background_mensaje.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getSel_msj()));
                else background_mensaje.setBackgroundColor(Color.TRANSPARENT);
            }
            else background_mensaje.setBackgroundColor(Color.TRANSPARENT);

            sticker_izq.setAnimation(Utils.obtenerEmojiAnimado(idSticker));
            if(YouChatApplication.animaciones_chat)
                sticker_izq.resumeAnimation();
            sticker_izq.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(modoSeleccion){
                        chatsActivity.actualizarSeleccion(chat);
                    }
                    else{
                        if(sticker_izq.isAnimating()) sticker_izq.pauseAnimation();
                        else sticker_izq.resumeAnimation();
                    }
                }
            });

            if(reenviado){
                view1.setVisibility(View.VISIBLE);
                view2.setVisibility(View.VISIBLE);
            }
            else{
                view1.setVisibility(View.GONE);
                view2.setVisibility(View.GONE);
            }
            hora_izq.setText(hor);
        }
    }

    ////////////////////////////////////////////////////////////ViewHolderDatosTextDerEmojiAnimado///////////////////////////////////////////////////
    public class ViewHolderDatosTextDerEmojiAnimado extends ViewHolderDatos {

        //lo q esta en el xml del chat izq
        LottieAnimationView sticker_der;
        View view1,view2;
        TextView hora_der;
        AppCompatImageView estadoView;
        private View background_mensaje;

        public ViewHolderDatosTextDerEmojiAnimado(@NonNull View itemView) {
            super(itemView);
            sticker_der = itemView.findViewById(R.id.sticker_der);
            view1 = itemView.findViewById(R.id.view1);
            view2 = itemView.findViewById(R.id.view2);
            hora_der = itemView.findViewById(R.id.hora_der);
            estadoView = itemView.findViewById(R.id.estadoImg);
            background_mensaje= itemView.findViewById(R.id.background_mensaje);
        }

        @SuppressLint("RestrictedApi")
        public synchronized void AsignarDatos(ItemChat chat) {
            boolean reenviado = chat.EsReenviado();
            String hor = chat.getHora();
            int est = chat.getEstado();
            String idSticker = chat.getId_msg_resp();

            if(modoSeleccion){
                if(chat.estaSeleccionado()) background_mensaje.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getSel_msj()));
                else background_mensaje.setBackgroundColor(Color.TRANSPARENT);
            }
            else background_mensaje.setBackgroundColor(Color.TRANSPARENT);

            sticker_der.setAnimation(Utils.obtenerEmojiAnimado(idSticker));

            if(YouChatApplication.animaciones_chat)
                sticker_der.resumeAnimation();
            sticker_der.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(modoSeleccion){
                        chatsActivity.actualizarSeleccion(chat);
                    }
                    else{
                        if(sticker_der.isAnimating()) sticker_der.pauseAnimation();
                        else sticker_der.resumeAnimation();
                    }
                }
            });

            view1.setVisibility(View.GONE);
            view2.setVisibility(View.GONE);

            if(est==ItemChat.ESTADO_ESPERANDO) {
                estadoView.setImageResource(R.drawable.time_circle);
                estadoView.setSupportImageTintList(stateListEstadoViewBlanco);
            }
            else if(est==ItemChat.ESTADO_ERROR)
            {
                estadoView.setImageResource(R.drawable.msg_est_error);
                estadoView.setSupportImageTintList(stateListColorBtn);
            }
            else if(est==ItemChat.ESTADO_ENVIADO)
            {
                estadoView.setImageResource(R.drawable.msg_est_enviado);
                estadoView.setSupportImageTintList(stateListEstadoViewBlanco);
            }
            else if(est==ItemChat.ESTADO_RECIBIDO)
            {
                estadoView.setImageResource(R.drawable.msg_est_recibido);
                estadoView.setSupportImageTintList(stateListEstadoViewBlanco);
            }
            else {
                estadoView.setImageResource(R.drawable.msg_est_recibido);
                estadoView.setSupportImageTintList(stateListColorBtn);
            }

            hora_der.setText(hor);
        }
    }

    ////////////////////////////////////////////////////////////ViewHolderDatosMsgNoLeidos///////////////////////////////////////////////////
    public class ViewHolderDatosMsgNoLeidos extends ViewHolderDatos {
        TextView fecha_global;
        
        public ViewHolderDatosMsgNoLeidos(@NonNull View itemView) {
            super(itemView);
            fecha_global = itemView.findViewById(R.id.fecha_global);
        }

        public synchronized void AsignarDatos(String text) {
            fecha_global.setText(text);
            fecha_global.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getFont_msg_fecha()));
        }
    }
}