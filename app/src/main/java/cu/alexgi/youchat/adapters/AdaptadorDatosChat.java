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
import android.text.style.RelativeSizeSpan;
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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieCompositionFactory;
import com.airbnb.lottie.LottieListener;
import com.airbnb.lottie.LottieTask;
import com.bumptech.glide.Glide;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.card.MaterialCardView;
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

import cu.alexgi.youchat.ChatsActivity;
import cu.alexgi.youchat.CircleImageView;
import cu.alexgi.youchat.Convertidor;
import cu.alexgi.youchat.LinkUtils;
import cu.alexgi.youchat.R;
import cu.alexgi.youchat.Utils;
import cu.alexgi.youchat.YouChatApplication;
import cu.alexgi.youchat.audiowave.AudioWaveView;
import cu.alexgi.youchat.items.ItemChat;
import cu.alexgi.youchat.items.ItemEstado;
import cu.alexgi.youchat.items.ItemPost;
import cu.alexgi.youchat.items.ItemTemas;
import cu.alexgi.youchat.progressbar.DownloadProgressView;
import cu.alexgi.youchat.sticky_headers_data.StickyRecyclerHeadersAdapter;
import cu.alexgi.youchat.views_GI.TextViewFontGenGI;
import cu.alexgi.youchat.views_GI.TextViewFontGenOscuroGI;
import cu.alexgi.youchat.views_GI.TextViewFontResGI;
import cu.alexgi.youchat.views_GI.TextViewMsgDerGI;
import cu.alexgi.youchat.views_GI.TextViewMsgIzqGI;
import cu.alexgi.youchat.views_GI.TextViewPostGI;
import cu.alexgi.youchat.zoominimageview.ZoomInImageViewAttacher;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

import static android.content.Context.POWER_SERVICE;
import static cu.alexgi.youchat.MainActivity.dbWorker;

@SuppressLint("RestrictedApi")
public class AdaptadorDatosChat extends RecyclerView.Adapter<AdaptadorDatosChat.ViewHolderDatos>
        implements View.OnClickListener, View.OnLongClickListener, StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {

    /////SENSOR//////
    private SensorManager deviceSensorManager;
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    private Sensor sensor;
    ////////////////

    private static final String TAG = "AdaptadorDatosChat";
    private ChatsActivity chatsActivity;
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

    public void setModoSeleccion(boolean modoSeleccion) {
        this.modoSeleccion = modoSeleccion;
    }

    private static ArrayList<ItemChat> listaDatos;
    private View.OnClickListener listener;
    private  View.OnLongClickListener onLongClickListener;

//    private static final int View_item_fecha=0;
    private static final int View_item_text_izq=1;
    private static final int View_item_text_der=2;
    private static final int View_item_img_izq=3;
    private static final int View_item_img_der=4;
    private static final int View_item_text_izq_resp=5;
    private static final int View_item_text_der_resp=6;
    private static final int View_item_audio_izq=7;
    private static final int View_item_audio_der=8;
    private static final int View_item_audio_izq_resp=9;
    private static final int View_item_audio_der_resp=10;
    private static final int View_item_contacto_izq=11;
    private static final int View_item_contacto_der=12;
    private static final int View_item_archivo_izq=13;
    private static final int View_item_archivo_der=14;
    private static final int View_item_tarjeta_izq=15;
    private static final int View_item_tarjeta_der=16;
    private static final int View_item_estado_izq_resp=17;
    private static final int View_item_estado_der_resp=18;
    private static final int View_item_sticker_izq=19;
    private static final int View_item_sticker_der=20;

    private static final int View_item_msg_tema_izq=21;
    private static final int View_item_msg_tema_der=22;

    private static final int View_item_post_izq_resp=23;
    private static final int View_item_post_der_resp=24;

    private static final int View_item_text_izq_emoji=25;
    private static final int View_item_text_der_emoji=26;
    private static final int View_item_text_izq_emoji_animado=27;
    private static final int View_item_text_der_emoji_animado=28;

    private static final int View_item_msg_solicitud_seguir=83;

    private static final int View_item_msg_union_yc=97;

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

    public AdaptadorDatosChat(Context c, ArrayList<ItemChat> l, ChatsActivity ca) {
        listaDatos = l;
        context = c;
        chatsActivity = ca;
        modoSeleccion = false;

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
        
        stateListEstadoViewBlanco = ColorStateList.valueOf(context.getResources().getColor(R.color.texto_blanco_to_gris));
        stateListEstadoViewRojo = ColorStateList.valueOf(context.getResources().getColor(R.color.temaRojoAccent));
    }

    @Override
    public ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view;
//        if(viewType==View_item_fecha)
//        {
//            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_mensaje_fecha,null, false);
//            view.setEnabled(false);
//            return new ViewHolderDatosFecha(view);
//        }
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
        else if(viewType==View_item_text_izq_resp)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_mensaje_answer_izq,null, false);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            return new ViewHolderDatosTextIzqResp(view);
        }
        else if(viewType==View_item_text_der_resp)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_mensaje_answer_der,null, false);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            return new ViewHolderDatosTextDerResp(view);
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
        else if(viewType==View_item_audio_izq_resp)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_audio_answer_izq,null, false);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            return new ViewHolderDatosAudioIzqResp(view);
        }
        else if(viewType==View_item_audio_der_resp)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_audio_answer_der,null, false);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            return new ViewHolderDatosAudioDerResp(view);
        }

        else if(viewType==View_item_contacto_izq)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_contact_izq,null, false);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            return new ViewHolderDatosContactoIzq(view);
        }
        else if(viewType==View_item_contacto_der)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_contact_der,null, false);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            return new ViewHolderDatosContactoDer(view);
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
        else if(viewType==View_item_tarjeta_izq)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_tarjeta_izq,null, false);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            return new ViewHolderDatosTarjetaIzq(view);
        }
        else if(viewType==View_item_tarjeta_der)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_tarjeta_der,null, false);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            return new ViewHolderDatosTarjetaDer(view);
        }
        else if(viewType==View_item_estado_izq_resp)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_mensaje_answer_izq,null, false);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            return new ViewHolderDatosEstadoIzqResp(view);
        }
        else if(viewType==View_item_estado_der_resp)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_mensaje_answer_der,null, false);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            return new ViewHolderDatosEstadoDerResp(view);
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
        else if(viewType==View_item_msg_tema_izq)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_mensaje_tema_izq,null, false);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            return new ViewHolderDatosMensajeTemaIzq(view);
        }
        else if(viewType==View_item_msg_tema_der)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_mensaje_tema_der,null, false);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            return new ViewHolderDatosMensajeTemaDer(view);
        }
        else if(viewType==View_item_post_izq_resp)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_mensaje_post_answer_izq,null, false);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            return new ViewHolderDatosPostIzqResp(view);
        }
        else if(viewType==View_item_post_der_resp)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_mensaje_post_answer_der,null, false);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            return new ViewHolderDatosPostDerResp(view);
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
        else if(viewType==View_item_msg_solicitud_seguir)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_msg_solicitud_seguir_izq,null, false);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            return new ViewHolderDatosMensajeSolicitudSeguir(view);
        }
        else if(viewType==View_item_msg_union_yc)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_mensaje_fecha,null, false);
            return new ViewHolderDatosUnionYouChat(view);
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
//        if(tipo==0)
//            return View_item_fecha;
        if(tipo==1)
            return View_item_text_izq;
        else if(tipo==2)
            return View_item_text_der;
        else if(tipo==3)
            return View_item_img_izq;
        else if(tipo==4)
            return View_item_img_der;
        else if(tipo==5)
            return View_item_text_izq_resp;
        else if(tipo==6)
            return View_item_text_der_resp;
        else if(tipo==7)
            return View_item_audio_izq;
        else if(tipo==8)
            return View_item_audio_der;
        else if(tipo==9)
            return View_item_audio_izq_resp;
        else if(tipo==10)
            return View_item_audio_der_resp;
        else if(tipo==11)
            return View_item_contacto_izq;
        else if(tipo==12)
            return View_item_contacto_der;
        else if(tipo==13)
            return View_item_archivo_izq;
        else if(tipo==14)
            return View_item_archivo_der;
        else if(tipo==15)
            return View_item_tarjeta_izq;
        else if(tipo==16)
            return View_item_tarjeta_der;
        else if(tipo==17)
            return View_item_estado_izq_resp;
        else if(tipo==18)
            return View_item_estado_der_resp;
        else if(tipo==19)
            return View_item_sticker_izq;
        else if(tipo==20)
            return View_item_sticker_der;
        else if(tipo==21)
            return View_item_msg_tema_izq;
        else if(tipo==22)
            return View_item_msg_tema_der;
        else if(tipo==23)
            return View_item_post_izq_resp;
        else if(tipo==24)
            return View_item_post_der_resp;
        else if(tipo==25)
            return View_item_text_izq_emoji;
        else if(tipo==26)
            return View_item_text_der_emoji;
        else if(tipo==27)
            return View_item_text_izq_emoji_animado;
        else if(tipo==28)
            return View_item_text_der_emoji_animado;
        else if(tipo==83)
            return View_item_msg_solicitud_seguir;
        else if(tipo==97)
            return View_item_msg_union_yc;
        else
            return View_item_msg_no_leido;
    }

    @Override
    public void onBindViewHolder(final ViewHolderDatos holder, final int position) {
        ItemChat chat = listaDatos.get(position);
        boolean margen;
        switch (holder.getItemViewType()) {
            /*case 0: //fecha
                ViewHolderDatosFecha vhdf = (ViewHolderDatosFecha) holder;
                vhdf.AsignarDatos(chat.getFecha());
                break;*/
            case 1: //mensaje izquierda
                margen= position >= listaDatos.size() - 1 || !listaDatos.get(position + 1).esIzq();

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
            case 5: //mensaje izquierda responder
                if(position<listaDatos.size()-1 && listaDatos.get(position+1).esIzq())
                    margen=false;
                else margen=true;

                ViewHolderDatosTextIzqResp vhdtir = (ViewHolderDatosTextIzqResp) holder;
                String id_res_izq=chat.getId_msg_resp();
                int pos_izq = posId(id_res_izq);

                if(pos_izq!=-1) vhdtir.AsignarDatos(margen, chat, true, id_res_izq, listaDatos.get(pos_izq).getEmisor(),
                        listaDatos.get(pos_izq).getTipo_mensaje(), listaDatos.get(pos_izq).getMensaje(),
                        listaDatos.get(pos_izq).getRuta_Dato());

                else vhdtir.AsignarDatos(margen, chat, false, id_res_izq, "Desconocido", -1, "", "");
                setAnimationIzq(holder.itemView);
                break;
            case 6: //mensaje derecha responder
                if(position<listaDatos.size()-1 && listaDatos.get(position+1).esDer())
                    margen=false;
                else margen=true;

                ViewHolderDatosTextDerResp vhdtdr = (ViewHolderDatosTextDerResp) holder;
                String id_res_der=chat.getId_msg_resp();
                int pos_der = posId(id_res_der);
                if(pos_der!=-1) vhdtdr.AsignarDatos(margen, chat, true, id_res_der, listaDatos.get(pos_der).getEmisor(),
                        listaDatos.get(pos_der).getTipo_mensaje(), listaDatos.get(pos_der).getMensaje(),
                        listaDatos.get(pos_der).getRuta_Dato());

                else vhdtdr.AsignarDatos(margen, chat, false, id_res_der, "Desconocido", -1, "", "");
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
            case 9://audio izquierda responder
                if(position<listaDatos.size()-1 && listaDatos.get(position+1).esIzq())
                    margen=false;
                else margen=true;

                ViewHolderDatosAudioIzqResp vhdair = (ViewHolderDatosAudioIzqResp) holder;
                String id_res_aizq=listaDatos.get(position).getId_msg_resp();
                int pos_aizq = posId(id_res_aizq);
                if(pos_aizq!=-1) vhdair.AsignarDatos(margen, chat, true, id_res_aizq, listaDatos.get(pos_aizq).getEmisor(),
                        listaDatos.get(pos_aizq).getTipo_mensaje(), listaDatos.get(pos_aizq).getMensaje(),
                        listaDatos.get(pos_aizq).getRuta_Dato());
                else vhdair.AsignarDatos(margen, chat, false, id_res_aizq, "Desconocido",-1,"","");
                setAnimationIzq(holder.itemView);
                break;
            case 10: //audio derecha responder
                if(position<listaDatos.size()-1 && listaDatos.get(position+1).esDer())
                    margen=false;
                else margen=true;

                ViewHolderDatosAudioDerResp vhdadr = (ViewHolderDatosAudioDerResp) holder;
                String id_res_ader=listaDatos.get(position).getId_msg_resp();
                int pos_ader = posId(id_res_ader);
                if(pos_ader!=-1) vhdadr.AsignarDatos(margen, chat, true, id_res_ader, listaDatos.get(pos_ader).getEmisor(), listaDatos.get(pos_ader).getTipo_mensaje(),
                        listaDatos.get(pos_ader).getMensaje(), listaDatos.get(pos_ader).getRuta_Dato());

                else vhdadr.AsignarDatos(margen, chat, false, id_res_ader, "Desconocido", -1,
                        "", "");
                setAnimationDer(holder.itemView);
                break;

            case 11: //contacto izquierda
                if(position<listaDatos.size()-1 && listaDatos.get(position+1).esIzq())
                    margen=false;
                else margen=true;

                ViewHolderDatosContactoIzq vhdci = (ViewHolderDatosContactoIzq) holder;
                vhdci.AsignarDatos(margen, chat);
                setAnimationIzq(holder.itemView);
                break;
            case 12: //contacto derecha
                if(position<listaDatos.size()-1 && listaDatos.get(position+1).esDer())
                    margen=false;
                else margen=true;

                ViewHolderDatosContactoDer vhdcd = (ViewHolderDatosContactoDer) holder;
                vhdcd.AsignarDatos(margen, chat);
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
            case 15: //tarjeta izquierda
                ViewHolderDatosTarjetaIzq vhdtai = (ViewHolderDatosTarjetaIzq) holder;
                vhdtai.AsignarDatos(chat);
                setAnimationIzq(holder.itemView);
                break;
            case 16: //tarjeta derecha
                ViewHolderDatosTarjetaDer vhdtad = (ViewHolderDatosTarjetaDer) holder;
                vhdtad.AsignarDatos(chat);
                setAnimationDer(holder.itemView);
                break;
            case 17: //estado izquierda responder
                if(position<listaDatos.size()-1 && listaDatos.get(position+1).esIzq())
                    margen=false;
                else margen=true;

                ViewHolderDatosEstadoIzqResp vhdeir = (ViewHolderDatosEstadoIzqResp) holder;
                vhdeir.AsignarDatos(margen, chat);

                setAnimationIzq(holder.itemView);
                break;
            case 18: //estado derecha responder
                if(position<listaDatos.size()-1 && listaDatos.get(position+1).esDer())
                    margen=false;
                else margen=true;

                ViewHolderDatosEstadoDerResp vhdedr = (ViewHolderDatosEstadoDerResp) holder;
                vhdedr.AsignarDatos(margen, chat);

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

            case 21: //mensaje tema izquierda
                if(position<listaDatos.size()-1 && listaDatos.get(position+1).esIzq())
                    margen=false;
                else margen=true;

                ViewHolderDatosMensajeTemaIzq vhdmti = (ViewHolderDatosMensajeTemaIzq) holder;
                vhdmti.AsignarDatos(margen, chat);

                setAnimationIzq(holder.itemView);
                break;
            case 22: //mensaje tema derecha
                if(position<listaDatos.size()-1 && listaDatos.get(position+1).esDer())
                    margen=false;
                else margen=true;

                ViewHolderDatosMensajeTemaDer vhdmtd = (ViewHolderDatosMensajeTemaDer) holder;
                vhdmtd.AsignarDatos(margen, chat);

                setAnimationDer(holder.itemView);
                break;

            case 23: //mensaje post respondido izquierda
                if(position<listaDatos.size()-1 && listaDatos.get(position+1).esIzq())
                    margen=false;
                else margen=true;

                ViewHolderDatosPostIzqResp vhdpir = (ViewHolderDatosPostIzqResp) holder;
                vhdpir.AsignarDatos(margen, chat);

                setAnimationIzq(holder.itemView);
                break;
            case 24: //mensaje post respondido derecha
                if(position<listaDatos.size()-1 && listaDatos.get(position+1).esDer())
                    margen=false;
                else margen=true;

                ViewHolderDatosPostDerResp vhdpdr = (ViewHolderDatosPostDerResp) holder;
                vhdpdr.AsignarDatos(margen, chat);

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

            case 83: //mensaje solicitud seguidor
                if(position<listaDatos.size()-1 && listaDatos.get(position+1).esIzq())
                    margen=false;
                else margen=true;

                ViewHolderDatosMensajeSolicitudSeguir vhdmss = (ViewHolderDatosMensajeSolicitudSeguir) holder;
                vhdmss.AsignarDatos(margen, chat);
                setAnimationIzq(holder.itemView);
                break;

            case 97: //mensajes union
                ViewHolderDatosUnionYouChat vhduyc = (ViewHolderDatosUnionYouChat) holder;
                vhduyc.AsignarDatos(chat.getCorreo());
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

//    private synchronized byte[] readBytesAudio1(String ruta){
//        final AudioInputStream audio = AudioSystem.getAudioInputStream(new File(ruta));
//        final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
//
//        byte[] buffer = new byte[4096];
//        int counter;
//        while((counter = audio.read(buffer,0,buffer.length)) != -1){
//            if(counter>0){
//                byteOut.write(buffer, 0, count);
//            }
//        }
//        audio.close();
//        byteOut.close();
//
//        return byteOut.toByteArray();
//    }
//
//    private synchronized byte[] readBytesAudio2(String ruta){
//        final AudioInputStream audio = AudioSystem.getAudioInputStream(new File(ruta));
//
//        AudioSystem.write(audio, AudioFileFormat.Type.WAV, byteOut);audio.close();
//
//        return ((ByteArrayOutputStream) byteOut).toByteArray();
//    }

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

    ////////////////////////////////////////////////////////////ViewHolderDatosFecha///////////////////////////////////////////////////
/*    public class ViewHolderDatosFecha extends ViewHolderDatos {

        //lo q esta en el xml del mensaje fecha
        TextView mensaje_fecha;


        public ViewHolderDatosFecha(@NonNull View itemView) {
            super(itemView);
            mensaje_fecha = itemView.findViewById(R.id.fecha_global);
        }

        public synchronized void AsignarDatos(String fech) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = new Date();
            String fechaEntera = sdf.format(date);
            if(fech.equals(fechaEntera))
                mensaje_fecha.setText("hoy");
            else{
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
                mensaje_fecha.setText(dd+mm);
            }
        }
    }*/

    ////////////////////////////////////////////////////////////ViewHolderDatosTextIzq///////////////////////////////////////////////////
    public class ViewHolderDatosTextIzq extends ViewHolderDatos{

        //lo q esta en el xml del chat izq
        private View est_reenviado, tv_es_editado;
//        private LinearLayout lllll;
        private EmojiTextView mensaje_izq;
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
            }else {
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

        public void AsignarDatos(boolean margen, ItemChat chat) {
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
        View fondo_msg_chat, est_reenviado, tv_es_editado;
        EmojiTextView mensaje_img_izq;
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
            est_reenviado=itemView.findViewById(R.id.est_reenviado);
            background_mensaje= itemView.findViewById(R.id.background_mensaje);
            tv_tam_img = itemView.findViewById(R.id.tv_tam_img);
            corner_izq=itemView.findViewById(R.id.corner_izq);
            tv_es_editado=itemView.findViewById(R.id.tv_es_editado);

            contendor_view_descarga=itemView.findViewById(R.id.contendor_view_descarga);
            progress_view=itemView.findViewById(R.id.progress_view);
            tv_tam_max=itemView.findViewById(R.id.tv_tam_max);
        }

        @SuppressLint("RestrictedApi")
        public synchronized void AsignarDatos(boolean margen, ItemChat chat) {
            String id = chat.getId();
            String u = chat.getMensaje();
            String ruta = chat.getRuta_Dato();
            String hor = chat.getHora();
            boolean esReenviado = chat.EsReenviado();

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

    ////////////////////////////////////////////////////////////ViewHolderDatosTextIzqResp///////////////////////////////////////////////////
    public class ViewHolderDatosTextIzqResp extends ViewHolderDatos {

        //lo q esta en el xml del chat izq
        private View fondo_msg_chat, contenedor_mensaje_respuesta_izq, answer_view;
        private EmojiTextView mensaje_izq, mensaje_respuesta_nombre_izq, mensaje_respuesta_texto_izq;
        private TextView hora_izq;
        private ImageView mensaje_respuesta_img_izq;
        private AppCompatImageView corner_izq;
        private View background_mensaje, tv_es_editado;



        public ViewHolderDatosTextIzqResp(@NonNull View itemView) {
            super(itemView);
            mensaje_izq = itemView.findViewById(R.id.mensaje_izq);
            hora_izq = itemView.findViewById(R.id.hora_izq);
            mensaje_respuesta_nombre_izq=itemView.findViewById(R.id.mensaje_respuesta_nombre_izq);
            mensaje_respuesta_texto_izq=itemView.findViewById(R.id.mensaje_respuesta_texto_izq);
            mensaje_respuesta_img_izq=itemView.findViewById(R.id.mensaje_respuesta_img_izq);
            fondo_msg_chat=itemView.findViewById(R.id.fondo_msg_chat);
            //fondo_msg_chat=itemView.findViewById(R.id.fondo_msg_chat);
            contenedor_mensaje_respuesta_izq=itemView.findViewById(R.id.contenedor_mensaje_respuesta_izq);
            corner_izq=itemView.findViewById(R.id.corner_izq);
            background_mensaje= itemView.findViewById(R.id.background_mensaje);
            tv_es_editado=itemView.findViewById(R.id.tv_es_editado);
            answer_view = itemView.findViewById(R.id.answer_view);
        }

        public synchronized void AsignarDatos(boolean margen, ItemChat chat, boolean exist , String idAnt,
                                 String ur, int tipo, String msg_res, String ruta_img_res) {
            String u = chat.getMensaje();
            String hor = chat.getHora();
             String idAct = chat.getId();

            answer_view.getBackground().getCurrent().setColorFilter(new PorterDuffColorFilter(Color.parseColor(YouChatApplication.itemTemas.getFont_msg_izq()), PorterDuff.Mode.SRC_IN));

            if(chat.esEditado()) tv_es_editado.setVisibility(View.VISIBLE);
            else tv_es_editado.setVisibility(View.GONE);

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

            mensaje_respuesta_img_izq.setVisibility(View.GONE);
            String nomRes;
            if(!exist) nomRes=ur;
            else if(ur.equals(YouChatApplication.correo))
                nomRes="Yo";
            else {
                nomRes = dbWorker.obtenerNombre(ur);
                if(nomRes.equals(""))
                    nomRes=ur;
            }
            mensaje_respuesta_nombre_izq.setText(nomRes);
            res = R.dimen.emoji_size_default;
            mensaje_respuesta_nombre_izq.setEmojiSizeRes(res, false);

            contenedor_mensaje_respuesta_izq.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setEnabled(false);
                    if(modoSeleccion){
                        chatsActivity.actualizarSeleccion(chat);
                    }
                    else chatsActivity.scrollToMensajeRespondido(idAct,idAnt);
                    v.setEnabled(true);
                }
            });

            if(exist){
                mensaje_respuesta_texto_izq.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getFont_msg_izq()));
                if(tipo==7 || tipo==8 || tipo==9 || tipo==10){
                    mensaje_respuesta_texto_izq.setText("Audio "+msg_res);
                }
                else if(tipo==19 || tipo==20){
                    mensaje_respuesta_texto_izq.setText("Sticker");
                }
                else if(tipo==11 || tipo==12){
                    mensaje_respuesta_texto_izq.setText("Contacto "+msg_res);
                }
                else if(tipo==13 || tipo==14){
                    mensaje_respuesta_texto_izq.setText("Archivo "+msg_res);
                }
                else if(tipo==21 || tipo==22){
                    mensaje_respuesta_texto_izq.setText("Tema");
                }
                else if(tipo==3 || tipo==4){
                    mensaje_respuesta_img_izq.setVisibility(View.VISIBLE);
                    Glide.with(mensaje_respuesta_img_izq.getContext()).load(ruta_img_res).
                            error(R.drawable.image_placeholder).into(mensaje_respuesta_img_izq);

                    if(msg_res.equals("")) mensaje_respuesta_texto_izq.setText("Imagen");
                    else {
                        emojiInformation = EmojiUtils.emojiInformation(msg_res);

                        if (emojiInformation.isOnlyEmojis && emojiInformation.emojis.size() < 6) {
                            res = R.dimen.emoji_size_only_emojis;
                        } else {
                            res = R.dimen.emoji_size_default;
                        }
                        mensaje_respuesta_texto_izq.setEmojiSizeRes(res, false);
                        mensaje_respuesta_texto_izq.setText(msg_res);
                    }
                }
                else {
                    emojiInformation = EmojiUtils.emojiInformation(msg_res);

                    if (emojiInformation.isOnlyEmojis && emojiInformation.emojis.size() < 6) {
                        res = R.dimen.emoji_size_only_emojis;
                    } else {
                        res = R.dimen.emoji_size_default;
                    }
                    mensaje_respuesta_texto_izq.setEmojiSizeRes(res, false);
                    mensaje_respuesta_texto_izq.setText(msg_res);
                }
            }
            else {
                res = R.dimen.emoji_size_default;
                mensaje_respuesta_img_izq.setVisibility(View.GONE);
                mensaje_respuesta_texto_izq.setEmojiSizeRes(res, false);
                mensaje_respuesta_texto_izq.setText("Mensaje no encontrado");
                if(modoNoche) mensaje_respuesta_texto_izq.setTextColor(0xFFA22B28);
                else mensaje_respuesta_texto_izq.setTextColor(0xFFE53935);
            }

            fondo_msg_chat.setBackground(drawable);
        }
    }

    ////////////////////////////////////////////////////////////ViewHolderDatosTextDerResp///////////////////////////////////////////////////
    public class ViewHolderDatosTextDerResp extends ViewHolderDatos {

        //lo q esta en el xml del chat der
        View fondo_msg_chat, fondo_mensaje_der, contenedor_mensaje_respuesta_der, answer_view;
        EmojiTextView mensaje_der, mensaje_respuesta_nombre_der,mensaje_respuesta_texto_der;
        TextView hora_der;
        AppCompatImageView estadoView, corner_der;
        ImageView mensaje_respuesta_img_der;
        private View background_mensaje, tv_es_editado;


        public ViewHolderDatosTextDerResp(@NonNull View itemView) {
            super(itemView);
            mensaje_der = itemView.findViewById(R.id.mensaje_der);
            estadoView = itemView.findViewById(R.id.estadoText);
            hora_der = itemView.findViewById(R.id.hora_der);
            mensaje_respuesta_nombre_der=itemView.findViewById(R.id.mensaje_respuesta_nombre_der);
            mensaje_respuesta_texto_der=itemView.findViewById(R.id.mensaje_respuesta_texto_der);
            mensaje_respuesta_img_der=itemView.findViewById(R.id.mensaje_respuesta_img_der);
            background_mensaje= itemView.findViewById(R.id.background_mensaje);
            fondo_mensaje_der=itemView.findViewById(R.id.fondo_mensaje_der);
            fondo_msg_chat=itemView.findViewById(R.id.fondo_msg_chat);
            contenedor_mensaje_respuesta_der=itemView.findViewById(R.id.contenedor_mensaje_respuesta_der);
            tv_es_editado=itemView.findViewById(R.id.tv_es_editado);
            corner_der=itemView.findViewById(R.id.corner_der);
            answer_view = itemView.findViewById(R.id.answer_view);
        }

        public synchronized void AsignarDatos(boolean margen, ItemChat chat, boolean exist, String idAnt,
                                 String ur, int tipo, String msg_res, String ruta_img_res) {
            String u = chat.getMensaje();
            int est = chat.getEstado();
            String hor = chat.getHora();
            String idAct = chat.getId();
            answer_view.getBackground().getCurrent().setColorFilter(new PorterDuffColorFilter(Color.parseColor(YouChatApplication.itemTemas.getFont_msg_der()), PorterDuff.Mode.SRC_IN));

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

            mensaje_respuesta_img_der.setVisibility(View.GONE);
            String nomRes;
            if(!exist) nomRes=ur;
            else if(ur.equals(YouChatApplication.correo))
                nomRes="Yo";
            else {
                nomRes = dbWorker.obtenerNombre(ur);
                if(nomRes.equals(""))
                    nomRes=ur;
            }
            mensaje_respuesta_nombre_der.setText(nomRes);
            res = R.dimen.emoji_size_default;
            mensaje_respuesta_nombre_der.setEmojiSizeRes(res, false);

            contenedor_mensaje_respuesta_der.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setEnabled(false);
                    if(modoSeleccion){
                        chatsActivity.actualizarSeleccion(chat);
                    }
                    else chatsActivity.scrollToMensajeRespondido(idAct,idAnt);
                    v.setEnabled(true);
                }
            });

            if(exist){
                mensaje_respuesta_texto_der.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getFont_msg_der()));
                if(tipo==7 || tipo==8 || tipo==9 || tipo==10){
                    mensaje_respuesta_texto_der.setText("Audio "+msg_res);
                }
                else if(tipo==19 || tipo==20){
                    mensaje_respuesta_texto_der.setText("Sticker");
                }
                else if(tipo==11 || tipo==12){
                    mensaje_respuesta_texto_der.setText("Contacto "+msg_res);
                }
                else if(tipo==13 || tipo==14){
                    mensaje_respuesta_texto_der.setText("Archivo "+msg_res);
                }
                else if(tipo==21 || tipo==22){
                    mensaje_respuesta_texto_der.setText("Tema");
                }
                else if(tipo==3 || tipo==4){
                    mensaje_respuesta_img_der.setVisibility(View.VISIBLE);
                    Glide.with(mensaje_respuesta_img_der.getContext()).load(ruta_img_res).
                            error(R.drawable.image_placeholder).into(mensaje_respuesta_img_der);

                    if(msg_res.equals("")) mensaje_respuesta_texto_der.setText("Imagen");
                    else {
                        emojiInformation = EmojiUtils.emojiInformation(msg_res);

                        if (emojiInformation.isOnlyEmojis && emojiInformation.emojis.size() < 6) {
                            res = R.dimen.emoji_size_only_emojis;
                        } else {
                            res = R.dimen.emoji_size_default;
                        }
                        mensaje_respuesta_texto_der.setEmojiSizeRes(res, false);
                        mensaje_respuesta_texto_der.setText(msg_res);
                    }
                }
                else {
                    emojiInformation = EmojiUtils.emojiInformation(msg_res);

                    if (emojiInformation.isOnlyEmojis && emojiInformation.emojis.size() < 6) {
                        res = R.dimen.emoji_size_only_emojis;
                    } else {
                        res = R.dimen.emoji_size_default;
                    }
                    mensaje_respuesta_texto_der.setEmojiSizeRes(res, false);
                    mensaje_respuesta_texto_der.setText(msg_res);
                }
            }
            else {
                res = R.dimen.emoji_size_default;
                mensaje_respuesta_img_der.setVisibility(View.GONE);
                mensaje_respuesta_texto_der.setEmojiSizeRes(res, false);
                mensaje_respuesta_texto_der.setText("Mensaje no encontrado");
                if(modoNoche) mensaje_respuesta_texto_der.setTextColor(0xFFA22B28);
                else mensaje_respuesta_texto_der.setTextColor(0xFFE53935);
            }

            fondo_mensaje_der.setBackground(drawable);
        }
    }

    ////////////////////////////////////////////////////////////ViewHolderDatosAudioIzq///////////////////////////////////////////////////
    public class ViewHolderDatosAudioIzq extends ViewHolderDatos{

        View fondo_msg_chat, est_reenviado;
        AppCompatImageView corner_izq;
        
        CircleImageView audio_play_izq;
        TextView audio_duration_izq,hora_izq;
        AudioWaveView audio_seekbar_izq;

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
            est_reenviado=itemView.findViewById(R.id.est_reenviado);
            background_mensaje= itemView.findViewById(R.id.background_mensaje);
            corner_izq=itemView.findViewById(R.id.corner_izq);
            progress_view=itemView.findViewById(R.id.progress_view);
        }

        public synchronized void AsignarDatos(boolean margen, ItemChat chat) {
            String ruta = chat.getRuta_Dato();
            String hor = chat.getHora();
            boolean esReenviado = chat.EsReenviado();

            if(modoSeleccion){
                if(chat.estaSeleccionado()) background_mensaje.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getSel_msj()));
                else background_mensaje.setBackgroundColor(Color.TRANSPARENT);
            }
            else background_mensaje.setBackgroundColor(Color.TRANSPARENT);

            if(esReenviado) est_reenviado.setVisibility(View.VISIBLE);
            else est_reenviado.setVisibility(View.GONE);

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
            hora_izq.setText(hor);
            File f = new File(ruta);
            if (f.exists() && f.canRead()) {
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
                    audio_play_der.setImageResource(R.drawable.send_error);
                    audio_play_der.setEnabled(false);
                    audio_seekbar_der.setEnabled(false);
                }
                finally {
                    long dur=mp.getDuration()/1000;
                    long seg=dur%60;
                    long minu=dur/60;
                    if (seg<10) audio_duration_der.setText(minu+":0"+seg);
                    else audio_duration_der.setText(minu+":"+seg);
                    audio_seekbar_der.setProgress(0);

                audio_play_der.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (modoSeleccion) {
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
            }
            else {
                audio_play_der.setImageResource(R.drawable.send_error);
                audio_play_der.setEnabled(false);
                audio_seekbar_der.setEnabled(false);
            }
            fondo_msg_chat.setBackground(drawable);
        }
    }

    ////////////////////////////////////////////////////////////ViewHolderDatosAudioIzqResp///////////////////////////////////////////////////
    public class ViewHolderDatosAudioIzqResp extends ViewHolderDatos {

        View fondo_msg_chat, contenedor_mensaje_respuesta_izq, answer_view;
        AppCompatImageView corner_izq;
        
        CircleImageView audio_play_izq;
        TextView audio_duration_izq,hora_izq;
        AudioWaveView audio_seekbar_izq;

        EmojiTextView mensaje_respuesta_nombre_izq, mensaje_respuesta_texto_izq;
        //TextView  mensaje_respuesta_id_act, mensaje_respuesta_id_ant;
        ImageView mensaje_respuesta_img_izq;
        private View background_mensaje;

        boolean isPlay, pause;
        private DownloadProgressView progress_view;

        public ViewHolderDatosAudioIzqResp(@NonNull View itemView) {
            super(itemView);
            audio_play_izq=itemView.findViewById(R.id.audio_play_izq);
            audio_seekbar_izq=itemView.findViewById(R.id.audio_seekbar_izq);
            audio_duration_izq=itemView.findViewById(R.id.audio_duration_izq);
            hora_izq=itemView.findViewById(R.id.hora_izq);
            background_mensaje= itemView.findViewById(R.id.background_mensaje);
            corner_izq=itemView.findViewById(R.id.corner_izq);

            mensaje_respuesta_nombre_izq=itemView.findViewById(R.id.mensaje_respuesta_nombre_izq);
            mensaje_respuesta_texto_izq=itemView.findViewById(R.id.mensaje_respuesta_texto_izq);
            mensaje_respuesta_img_izq=itemView.findViewById(R.id.mensaje_respuesta_img_izq);

            fondo_msg_chat=itemView.findViewById(R.id.fondo_msg_chat);
            contenedor_mensaje_respuesta_izq=itemView.findViewById(R.id.contenedor_mensaje_respuesta_izq);
            answer_view = itemView.findViewById(R.id.answer_view);
            progress_view = itemView.findViewById(R.id.progress_view);
        }

        public synchronized void AsignarDatos(boolean margen, ItemChat chat,
                                 boolean exist, String idAnt, String ur, int tipo,
                                 String msg_res, String ruta_img_res) {
            String ruta = chat.getRuta_Dato();
            String hor = chat.getHora();
            String idAct = chat.getId();

            answer_view.getBackground().getCurrent().setColorFilter(new PorterDuffColorFilter(Color.parseColor(YouChatApplication.itemTemas.getFont_msg_izq()), PorterDuff.Mode.SRC_IN));

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

            final MediaPlayer mp = new MediaPlayer();
            File f = new File(ruta);
            if (f.exists() && f.canRead()) {
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
                audio_seekbar_izq.setProgress(0);


                audio_play_izq.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (modoSeleccion) {
                            chatsActivity.actualizarSeleccion(chat);
                        }
                        else{
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

            mensaje_respuesta_img_izq.setVisibility(View.GONE);
            hora_izq.setText(hor);

            String nomRes;
            if(!exist) nomRes=ur;
            else if(ur.equals(YouChatApplication.correo))
                nomRes="Yo";
            else {
                nomRes = dbWorker.obtenerNombre(ur);
                if(nomRes.equals(""))
                    nomRes=ur;
            }
            mensaje_respuesta_nombre_izq.setText(nomRes);
            int res = R.dimen.emoji_size_default;
            mensaje_respuesta_nombre_izq.setEmojiSizeRes(res, false);

            contenedor_mensaje_respuesta_izq.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setEnabled(false);
                    if(modoSeleccion){
                        chatsActivity.actualizarSeleccion(chat);
                    }
                    else chatsActivity.scrollToMensajeRespondido(idAct,idAnt);
                    v.setEnabled(true);
                }
            });

            if(exist){
                mensaje_respuesta_texto_izq.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getFont_msg_izq()));
                if(tipo==7 || tipo==8 || tipo==9 || tipo==10){
                    mensaje_respuesta_texto_izq.setText("Audio "+msg_res);
                }
                else if(tipo==19 || tipo==20){
                    mensaje_respuesta_texto_izq.setText("Sticker");
                }
                else if(tipo==11 || tipo==12){
                    mensaje_respuesta_texto_izq.setText("Contacto "+msg_res);
                }
                else if(tipo==13 || tipo==14){
                    mensaje_respuesta_texto_izq.setText("Archivo "+msg_res);
                }
                else if(tipo==21 || tipo==22){
                    mensaje_respuesta_texto_izq.setText("Tema");
                }
                else if(tipo==3 || tipo==4){
                    mensaje_respuesta_img_izq.setVisibility(View.VISIBLE);
                    Glide.with(mensaje_respuesta_img_izq.getContext()).load(ruta_img_res).
                            error(R.drawable.image_placeholder).into(mensaje_respuesta_img_izq);

                    if(msg_res.equals("")) mensaje_respuesta_texto_izq.setText("Imagen");
                    else {
                        EmojiInformation emojiInformation = EmojiUtils.emojiInformation(msg_res);
                        if (emojiInformation.isOnlyEmojis && emojiInformation.emojis.size() < 6) {
                            res = R.dimen.emoji_size_only_emojis;
                        } else {
                            res = R.dimen.emoji_size_default;
                        }
                        mensaje_respuesta_texto_izq.setEmojiSizeRes(res, false);
                        mensaje_respuesta_texto_izq.setText(msg_res);
                    }
                }
                else {
                    EmojiInformation emojiInformation = EmojiUtils.emojiInformation(msg_res);
                    if (emojiInformation.isOnlyEmojis && emojiInformation.emojis.size() < 6) {
                        res = R.dimen.emoji_size_only_emojis;
                    } else {
                        res = R.dimen.emoji_size_default;
                    }
                    mensaje_respuesta_texto_izq.setEmojiSizeRes(res, false);
                    mensaje_respuesta_texto_izq.setText(msg_res);
                }
            }
            else {
                res = R.dimen.emoji_size_default;
                mensaje_respuesta_img_izq.setVisibility(View.GONE);
                mensaje_respuesta_texto_izq.setEmojiSizeRes(res, false);
                mensaje_respuesta_texto_izq.setText("Mensaje no encontrado");
                if(modoNoche) mensaje_respuesta_texto_izq.setTextColor(0xFFA22B28);
                else mensaje_respuesta_texto_izq.setTextColor(0xFFE53935);
            }
            fondo_msg_chat.setBackground(drawable);
        }
    }

    ////////////////////////////////////////////////////////////ViewHolderDatosAudioDerResp///////////////////////////////////////////////////
    public class ViewHolderDatosAudioDerResp extends ViewHolderDatos {

        View fondo_msg_chat, contenedor_mensaje_respuesta_der, answer_view;

        CircleImageView audio_play_der;
        TextView audio_duration_der,hora_der;
        AudioWaveView audio_seekbar_der;

        EmojiTextView mensaje_respuesta_nombre_der,mensaje_respuesta_texto_der;
        ImageView mensaje_respuesta_img_der;

        AppCompatImageView estadoView, corner_der;

        boolean isPlay, pause;
        private View background_mensaje;

        public ViewHolderDatosAudioDerResp(@NonNull View itemView) {
            super(itemView);
            audio_play_der=itemView.findViewById(R.id.audio_play_der);
            audio_seekbar_der=itemView.findViewById(R.id.audio_seekbar_der);
            audio_duration_der=itemView.findViewById(R.id.audio_duration_der);
            hora_der=itemView.findViewById(R.id.hora_der);
            estadoView=itemView.findViewById(R.id.estadoText);
            mensaje_respuesta_nombre_der=itemView.findViewById(R.id.mensaje_respuesta_nombre_der);
            mensaje_respuesta_texto_der=itemView.findViewById(R.id.mensaje_respuesta_texto_der);
            mensaje_respuesta_img_der=itemView.findViewById(R.id.mensaje_respuesta_img_der);
            fondo_msg_chat=itemView.findViewById(R.id.fondo_msg_chat);
            contenedor_mensaje_respuesta_der=itemView.findViewById(R.id.contenedor_mensaje_respuesta_der);
            corner_der=itemView.findViewById(R.id.corner_der);
            background_mensaje= itemView.findViewById(R.id.background_mensaje);
            answer_view = itemView.findViewById(R.id.answer_view);
        }

        public synchronized void AsignarDatos(boolean margen, ItemChat chat, boolean exist, String idAnt,
                                 String ur, int tipo, String msg_res, String ruta_img_res) {
            String ruta = chat.getRuta_Dato();
            String hor = chat.getHora();
            int est = chat.getEstado();
            String idAct = chat.getId();
            answer_view.getBackground().getCurrent().setColorFilter(new PorterDuffColorFilter(Color.parseColor(YouChatApplication.itemTemas.getFont_msg_der()), PorterDuff.Mode.SRC_IN));

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
                        if (modoSeleccion) {
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

            String nomRes;
            if(!exist) nomRes=ur;
            else if(ur.equals(YouChatApplication.correo))
                nomRes="Yo";
            else {
                nomRes = dbWorker.obtenerNombre(ur);
                if(nomRes.equals(""))
                    nomRes=ur;
            }
            mensaje_respuesta_nombre_der.setText(nomRes);
            int res = R.dimen.emoji_size_default;
            mensaje_respuesta_nombre_der.setEmojiSizeRes(res, false);

            contenedor_mensaje_respuesta_der.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setEnabled(false);
                    if(modoSeleccion){
                        chatsActivity.actualizarSeleccion(chat);
                    }
                    else chatsActivity.scrollToMensajeRespondido(idAct,idAnt);
                    v.setEnabled(true);
                }
            });

            if(exist){
                mensaje_respuesta_texto_der.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getFont_msg_der()));
                if(tipo==7 || tipo==8 || tipo==9 || tipo==10){
                    mensaje_respuesta_texto_der.setText("Audio "+msg_res);
                }
                else if(tipo==19 || tipo==20){
                    mensaje_respuesta_texto_der.setText("Sticker");
                }
                else if(tipo==11 || tipo==12){
                    mensaje_respuesta_texto_der.setText("Contacto "+msg_res);
                }
                else if(tipo==13 || tipo==14){
                    mensaje_respuesta_texto_der.setText("Archivo "+msg_res);
                }
                else if(tipo==21 || tipo==22){
                    mensaje_respuesta_texto_der.setText("Tema");
                }
                else if(tipo==3 || tipo==4){
                    mensaje_respuesta_img_der.setVisibility(View.VISIBLE);
                    Glide.with(mensaje_respuesta_img_der.getContext()).load(ruta_img_res).
                            error(R.drawable.image_placeholder).into(mensaje_respuesta_img_der);

                    if(msg_res.equals("")) mensaje_respuesta_texto_der.setText("Imagen");
                    else{
                        EmojiInformation emojiInformation = EmojiUtils.emojiInformation(msg_res);
                        if (emojiInformation.isOnlyEmojis && emojiInformation.emojis.size() < 6) {
                            res = R.dimen.emoji_size_only_emojis;
                        } else {
                            res = R.dimen.emoji_size_default;
                        }
                        mensaje_respuesta_texto_der.setEmojiSizeRes(res, false);
                        mensaje_respuesta_texto_der.setText(msg_res);
                    }
                }
                else {
                    EmojiInformation emojiInformation = EmojiUtils.emojiInformation(msg_res);
                    if (emojiInformation.isOnlyEmojis && emojiInformation.emojis.size() < 6) {
                        res = R.dimen.emoji_size_only_emojis;
                    } else {
                        res = R.dimen.emoji_size_default;
                    }
                    mensaje_respuesta_texto_der.setEmojiSizeRes(res, false);
                    mensaje_respuesta_texto_der.setText(msg_res);
                }
            }
            else {
                res = R.dimen.emoji_size_default;
                mensaje_respuesta_img_der.setVisibility(View.GONE);
                mensaje_respuesta_texto_der.setEmojiSizeRes(res, false);
                mensaje_respuesta_texto_der.setText("Mensaje no encontrado");
                if(modoNoche) mensaje_respuesta_texto_der.setTextColor(0xFFA22B28);
                else mensaje_respuesta_texto_der.setTextColor(0xFFE53935);
            }
            fondo_msg_chat.setBackground(drawable);
        }
    }

    ////////////////////////////////////////////////////////////ViewHolderDatosContactoIzq///////////////////////////////////////////////////
    public class ViewHolderDatosContactoIzq extends ViewHolderDatos{

        View fondo_msg_chat, est_reenviado;

        TextView hora_izq, contact_nombre_izq, contact_correo_izq;
        AppCompatImageView corner_izq;
        private View background_mensaje;

        public ViewHolderDatosContactoIzq(@NonNull View itemView) {
            super(itemView);
            hora_izq=itemView.findViewById(R.id.hora_izq);
            fondo_msg_chat=itemView.findViewById(R.id.fondo_msg_chat);
            est_reenviado=itemView.findViewById(R.id.est_reenviado);
            background_mensaje= itemView.findViewById(R.id.background_mensaje);
            contact_nombre_izq=itemView.findViewById(R.id.contact_nombre_izq);
            contact_correo_izq=itemView.findViewById(R.id.contact_correo_izq);
            corner_izq=itemView.findViewById(R.id.corner_izq);
        }

        public synchronized void AsignarDatos(boolean margen, ItemChat chat) {

            String nombreContacto = chat.getMensaje();
            String correoContacto = chat.getId_msg_resp();
            String hor = chat.getHora();
            boolean esReenviado = chat.EsReenviado();
            if(modoSeleccion){
                if(chat.estaSeleccionado()) background_mensaje.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getSel_msj()));
                else background_mensaje.setBackgroundColor(Color.TRANSPARENT);
            }
            else background_mensaje.setBackgroundColor(Color.TRANSPARENT);

            if(esReenviado) est_reenviado.setVisibility(View.VISIBLE);
            else est_reenviado.setVisibility(View.GONE);

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
            contact_nombre_izq.setTextSize(tam_fuente);
            if(nombreContacto.equals(""))
                nombreContacto=correoContacto;
            contact_nombre_izq.setText(nombreContacto);
            contact_correo_izq.setText(correoContacto);
            final String nombreContactoFinal = nombreContacto;
            fondo_msg_chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setEnabled(false);
                    if(modoSeleccion){
                        chatsActivity.actualizarSeleccion(chat);
                    }
                    else chatsActivity.mostrarDetallesContacto(nombreContactoFinal,correoContacto);
                    v.setEnabled(true);
                }
            });
            fondo_msg_chat.setBackground(drawable);
        }
    }

    ////////////////////////////////////////////////////////////ViewHolderDatosContactoDer///////////////////////////////////////////////////
    public class ViewHolderDatosContactoDer extends ViewHolderDatos {

        View fondo_msg_chat, est_reenviado;
        AppCompatImageView estadoView, corner_der;
        TextView hora_der, contact_nombre_der, contact_correo_der;
        private View background_mensaje;

        public ViewHolderDatosContactoDer(@NonNull View itemView) {
            super(itemView);
            hora_der=itemView.findViewById(R.id.hora_der);
            estadoView=itemView.findViewById(R.id.estadoText);
            fondo_msg_chat=itemView.findViewById(R.id.fondo_msg_chat);
            est_reenviado=itemView.findViewById(R.id.est_reenviado);
            background_mensaje= itemView.findViewById(R.id.background_mensaje);
            contact_nombre_der=itemView.findViewById(R.id.contact_nombre_der);
            contact_correo_der=itemView.findViewById(R.id.contact_correo_der);
            corner_der=itemView.findViewById(R.id.corner_der);
        }

        public synchronized void AsignarDatos(boolean margen, ItemChat chat) {

            String nombreContacto = chat.getMensaje();
            String correoContacto = chat.getId_msg_resp();
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

            contact_nombre_der.setTextSize(tam_fuente);
            hora_der.setText(hor);
            setStatusImg(est, estadoView);
            if(nombreContacto.equals(""))
                nombreContacto=correoContacto;
            contact_nombre_der.setText(nombreContacto);
            contact_correo_der.setText(correoContacto);
            final String nombreContactoFinal = nombreContacto;
            fondo_msg_chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setEnabled(false);
                    if(modoSeleccion){
                        chatsActivity.actualizarSeleccion(chat);
                    }
                    else chatsActivity.mostrarDetallesContacto(nombreContactoFinal,correoContacto);
                    v.setEnabled(true);
                }
            });
            fondo_msg_chat.setBackground(drawable);
        }
    }

    ////////////////////////////////////////////////////////////ViewHolderDatosArchivoIzq///////////////////////////////////////////////////
    public class ViewHolderDatosArchivoIzq extends ViewHolderDatos{

        View fondo_msg_chat, est_reenviado;

        ImageView file_image_izq;
        TextView hora_izq, file_nombre_izq, file_peso_izq, file_ext_izq;
        AppCompatImageView corner_izq;
        private View background_mensaje;

        private DownloadProgressView progress_view;

        public ViewHolderDatosArchivoIzq(@NonNull View itemView) {
            super(itemView);
            hora_izq=itemView.findViewById(R.id.hora_izq);
            fondo_msg_chat=itemView.findViewById(R.id.fondo_msg_chat);
            est_reenviado=itemView.findViewById(R.id.est_reenviado);
            background_mensaje= itemView.findViewById(R.id.background_mensaje);
            file_nombre_izq=itemView.findViewById(R.id.file_nombre_izq);
            file_peso_izq=itemView.findViewById(R.id.file_peso_izq);
            file_ext_izq=itemView.findViewById(R.id.file_ext_izq);

            corner_izq=itemView.findViewById(R.id.corner_izq);
            file_image_izq=itemView.findViewById(R.id.file_image_izq);
            progress_view=itemView.findViewById(R.id.progress_view);
        }

        public synchronized void AsignarDatos(boolean margen, ItemChat chat) {

            String rutaArchivo = chat.getRuta_Dato();
             String hor = chat.getHora();
             boolean esReenviado = chat.EsReenviado();
            if(esReenviado) est_reenviado.setVisibility(View.VISIBLE);
            else est_reenviado.setVisibility(View.GONE);

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

    ////////////////////////////////////////////////////////////ViewHolderDatosTarjetaIzq///////////////////////////////////////////////////
    public class ViewHolderDatosTarjetaIzq extends ViewHolderDatos{

        MaterialCardView fondo_msg_chat;
        EmojiTextView mensaje_izq;
        TextView hora_izq;
        private View background_mensaje;

        public ViewHolderDatosTarjetaIzq(@NonNull View itemView) {
            super(itemView);
            background_mensaje= itemView.findViewById(R.id.background_mensaje);
            fondo_msg_chat=itemView.findViewById(R.id.fondo_msg_chat);
            mensaje_izq=itemView.findViewById(R.id.mensaje_izq);
            hora_izq=itemView.findViewById(R.id.hora_izq);
        }

        public synchronized void AsignarDatos(ItemChat chat) {

            String texto_tarjeta = chat.getMensaje();
            String hor = chat.getHora();
            String color = chat.getId_msg_resp();

            if(modoSeleccion){
                if(chat.estaSeleccionado()) background_mensaje.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getSel_msj()));
                else background_mensaje.setBackgroundColor(Color.TRANSPARENT);
            }

            else background_mensaje.setBackgroundColor(Color.TRANSPARENT);
            int colorTarjeta=ContextCompat.getColor(context, R.color.card5);
            switch (color)
            {
                case "0": colorTarjeta=ContextCompat.getColor(context, R.color.card1);
                    break;
                case "1": colorTarjeta=ContextCompat.getColor(context, R.color.card2);
                    break;
                case "2": colorTarjeta=ContextCompat.getColor(context, R.color.card3);
                    break;
                case "3": colorTarjeta=ContextCompat.getColor(context, R.color.card4);
                    break;
                case "4": colorTarjeta=ContextCompat.getColor(context, R.color.card5);
                    break;
                case "5": colorTarjeta=ContextCompat.getColor(context, R.color.card6);
                    break;
                case "6": colorTarjeta=ContextCompat.getColor(context, R.color.card7);
                    break;
                case "7": colorTarjeta=ContextCompat.getColor(context, R.color.card8);
                    break;
                case "8": colorTarjeta=ContextCompat.getColor(context, R.color.card9);
                    break;
                case "9": colorTarjeta=ContextCompat.getColor(context, R.color.card10);
                    break;
                case "10": colorTarjeta=ContextCompat.getColor(context, R.color.card11);
                    break;
                case "11": colorTarjeta=ContextCompat.getColor(context, R.color.card12);
                    break;
                case "12": colorTarjeta=ContextCompat.getColor(context, R.color.card13);
                    break;
                case "13": colorTarjeta=ContextCompat.getColor(context, R.color.card14);
                    break;
                case "14": colorTarjeta=ContextCompat.getColor(context, R.color.card15);
                    break;
                case "15": colorTarjeta=ContextCompat.getColor(context, R.color.card16);
                    break;
            }
            fondo_msg_chat.setCardBackgroundColor(colorTarjeta);
            mensaje_izq.setText(texto_tarjeta);
            hora_izq.setText(hor);
        }
    }

    ////////////////////////////////////////////////////////////ViewHolderDatosTarjetaDer///////////////////////////////////////////////////
    public class ViewHolderDatosTarjetaDer extends ViewHolderDatos {

        MaterialCardView fondo_mensaje_der;
        EmojiTextView mensaje_der;
        AppCompatImageView estadoView;
        TextView hora_der;
        private View background_mensaje;

        public ViewHolderDatosTarjetaDer (@NonNull View itemView) {
            super(itemView);
            fondo_mensaje_der=itemView.findViewById(R.id.fondo_mensaje_der);
            mensaje_der=itemView.findViewById(R.id.mensaje_der);
            hora_der=itemView.findViewById(R.id.hora_der);
            background_mensaje= itemView.findViewById(R.id.background_mensaje);
            estadoView=itemView.findViewById(R.id.estadoText);
        }

        @SuppressLint("RestrictedApi")
        public synchronized void AsignarDatos(ItemChat chat) {

            String texto_tarjeta = chat.getMensaje();
            String hor = chat.getHora();
            int est = chat.getEstado();
            String color = chat.getId_msg_resp();
            hora_der.setText(hor);

            if(modoSeleccion){
                if(chat.estaSeleccionado()) background_mensaje.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getSel_msj()));
                else background_mensaje.setBackgroundColor(Color.TRANSPARENT);
            }
            else background_mensaje.setBackgroundColor(Color.TRANSPARENT);

            setStatusImg(est, estadoView);

            int colorTarjeta=ContextCompat.getColor(context, R.color.card5);
            switch (color)
            {
                case "0": colorTarjeta=ContextCompat.getColor(context, R.color.card1);
                    break;
                case "1": colorTarjeta=ContextCompat.getColor(context, R.color.card2);
                    break;
                case "2": colorTarjeta=ContextCompat.getColor(context, R.color.card3);
                    break;
                case "3": colorTarjeta=ContextCompat.getColor(context, R.color.card4);
                    break;
                case "4": colorTarjeta=ContextCompat.getColor(context, R.color.card5);
                    break;
                case "5": colorTarjeta=ContextCompat.getColor(context, R.color.card6);
                    break;
                case "6": colorTarjeta=ContextCompat.getColor(context, R.color.card7);
                    break;
                case "7": colorTarjeta=ContextCompat.getColor(context, R.color.card8);
                    break;
                case "8": colorTarjeta=ContextCompat.getColor(context, R.color.card9);
                    break;
                case "9": colorTarjeta=ContextCompat.getColor(context, R.color.card10);
                    break;
                case "10": colorTarjeta=ContextCompat.getColor(context, R.color.card11);
                    break;
                case "11": colorTarjeta=ContextCompat.getColor(context, R.color.card12);
                    break;
                case "12": colorTarjeta=ContextCompat.getColor(context, R.color.card13);
                    break;
                case "13": colorTarjeta=ContextCompat.getColor(context, R.color.card14);
                    break;
                case "14": colorTarjeta=ContextCompat.getColor(context, R.color.card15);
                    break;
                case "15": colorTarjeta=ContextCompat.getColor(context, R.color.card16);
                    break;
            }
            fondo_mensaje_der.setCardBackgroundColor(colorTarjeta);

            mensaje_der.setText(texto_tarjeta);
        }
    }

    ////////////////////////////////////////////////////////////ViewHolderDatosEstadoIzqResp///////////////////////////////////////////////////
    public class ViewHolderDatosEstadoIzqResp extends ViewHolderDatos {

        //lo q esta en el xml del chat izq
        View fondo_msg_chat, contenedor_mensaje_respuesta_izq;
        EmojiTextView mensaje_izq, mensaje_respuesta_nombre_izq, mensaje_respuesta_texto_izq;
        TextView hora_izq;
        ImageView mensaje_respuesta_img_izq;
        AppCompatImageView corner_izq;
        private View background_mensaje, tv_es_editado;


        public ViewHolderDatosEstadoIzqResp(@NonNull View itemView) {
            super(itemView);
            mensaje_izq = itemView.findViewById(R.id.mensaje_izq);
            hora_izq = itemView.findViewById(R.id.hora_izq);
            mensaje_respuesta_nombre_izq=itemView.findViewById(R.id.mensaje_respuesta_nombre_izq);
            mensaje_respuesta_texto_izq=itemView.findViewById(R.id.mensaje_respuesta_texto_izq);
            mensaje_respuesta_img_izq=itemView.findViewById(R.id.mensaje_respuesta_img_izq);
            fondo_msg_chat=itemView.findViewById(R.id.fondo_msg_chat);
            contenedor_mensaje_respuesta_izq=itemView.findViewById(R.id.contenedor_mensaje_respuesta_izq);
            background_mensaje= itemView.findViewById(R.id.background_mensaje);
            corner_izq=itemView.findViewById(R.id.corner_izq);
            tv_es_editado=itemView.findViewById(R.id.tv_es_editado);
        }

        public synchronized void AsignarDatos(boolean margen, ItemChat chat) {
            String u = chat.getMensaje();
            String hor = chat.getHora();
            String idEstado = chat.getId_msg_resp();

            if(chat.esEditado()) tv_es_editado.setVisibility(View.VISIBLE);
            else tv_es_editado.setVisibility(View.GONE);

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

            EmojiInformation emojiInformation = EmojiUtils.emojiInformation(u);
            int res;

            if (emojiInformation.isOnlyEmojis && emojiInformation.emojis.size() <=2){
                res = R.dimen.emoji_size_single_emoji;
                mensaje_izq.setEmojiSizeRes(res, false);
                mensaje_izq.setTextSize(tam_fuente);

                mensaje_izq.setText(u);
            }
            else if(emojiInformation.isOnlyEmojis && emojiInformation.emojis.size() < 6) {
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

            mensaje_respuesta_img_izq.setVisibility(View.GONE);
            hora_izq.setText(hor);

            boolean exist = true;
            ItemEstado estado = dbWorker.obtenerEstado(idEstado);
            if(estado==null)
                exist = false;

            contenedor_mensaje_respuesta_izq.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setEnabled(false);
                    if(modoSeleccion){
                        chatsActivity.actualizarSeleccion(chat);
                    }
                    else{
                        chatsActivity.abrirVisorEstado(estado);
//                        if(estado!=null)
//                            chatsActivity.abrirVisorEstado(estado.getCorreo(),estado.getId());
//                        else chatsActivity.abrirVisorEstado("","");
                    }
                    v.setEnabled(true);
                }
            });

            String nomRes;
            if(exist){
                if(estado.getCorreo().equals(YouChatApplication.correo))
                    nomRes="Yo";
                else
                    nomRes = dbWorker.obtenerNombre(estado.getCorreo());
            }
            else nomRes = "Usuario no encontrado";
            mensaje_respuesta_nombre_izq.setText(nomRes);
            res = R.dimen.emoji_size_default;
            mensaje_respuesta_nombre_izq.setEmojiSizeRes(res, false);

            if(exist){
                mensaje_respuesta_texto_izq.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getFont_msg_izq()));
                if(estado.getTipo_estado()==99){
                    mensaje_respuesta_img_izq.setVisibility(View.VISIBLE);
                    Glide.with(mensaje_respuesta_img_izq.getContext()).load(estado.getRuta_imagen()).
                            error(R.drawable.image_placeholder).into(mensaje_respuesta_img_izq);

                    if(estado.getTexto().equals("")) mensaje_respuesta_texto_izq.setText("Estado");
                    else {
                        emojiInformation = EmojiUtils.emojiInformation(estado.getTexto());

                        if (emojiInformation.isOnlyEmojis && emojiInformation.emojis.size() < 6) {
                            res = R.dimen.emoji_size_only_emojis;
                        } else {
                            res = R.dimen.emoji_size_default;
                        }
                        mensaje_respuesta_texto_izq.setEmojiSizeRes(res, false);
                        mensaje_respuesta_texto_izq.setText(estado.getTexto());
                    }
                }
                else {
                    emojiInformation = EmojiUtils.emojiInformation(estado.getTexto());

                    if (emojiInformation.isOnlyEmojis && emojiInformation.emojis.size() < 6) {
                        res = R.dimen.emoji_size_only_emojis;
                    } else {
                        res = R.dimen.emoji_size_default;
                    }
                    mensaje_respuesta_texto_izq.setEmojiSizeRes(res, false);
                    mensaje_respuesta_texto_izq.setText("Estado: "+estado.getTexto());
                }
            }
            else {
                res = R.dimen.emoji_size_default;
                mensaje_respuesta_img_izq.setVisibility(View.GONE);
                mensaje_respuesta_texto_izq.setEmojiSizeRes(res, false);
                mensaje_respuesta_texto_izq.setText("Estado no encontrado");
                if(modoNoche) mensaje_respuesta_texto_izq.setTextColor(0xFFA22B28);
                else mensaje_respuesta_texto_izq.setTextColor(0xFFE53935);
            }
            fondo_msg_chat.setBackground(drawable);
        }
    }

    ////////////////////////////////////////////////////////////ViewHolderDatosEstadoDerResp///////////////////////////////////////////////////
    public class ViewHolderDatosEstadoDerResp extends ViewHolderDatos {

        //lo q esta en el xml del chat der
        View fondo_msg_chat, fondo_mensaje_der, contenedor_mensaje_respuesta_der;
        EmojiTextView mensaje_der, mensaje_respuesta_nombre_der,mensaje_respuesta_texto_der;
        TextView hora_der;
        AppCompatImageView estadoView, corner_der;

        ImageView mensaje_respuesta_img_der;
        private View background_mensaje, tv_es_editado;

        public ViewHolderDatosEstadoDerResp(@NonNull View itemView) {
            super(itemView);
            mensaje_der = itemView.findViewById(R.id.mensaje_der);
            estadoView = itemView.findViewById(R.id.estadoText);
            hora_der = itemView.findViewById(R.id.hora_der);
            mensaje_respuesta_nombre_der=itemView.findViewById(R.id.mensaje_respuesta_nombre_der);
            mensaje_respuesta_texto_der=itemView.findViewById(R.id.mensaje_respuesta_texto_der);
            mensaje_respuesta_img_der=itemView.findViewById(R.id.mensaje_respuesta_img_der);
            background_mensaje= itemView.findViewById(R.id.background_mensaje);
            fondo_mensaje_der=itemView.findViewById(R.id.fondo_mensaje_der);
            fondo_msg_chat=itemView.findViewById(R.id.fondo_msg_chat);
            contenedor_mensaje_respuesta_der=itemView.findViewById(R.id.contenedor_mensaje_respuesta_der);
            corner_der=itemView.findViewById(R.id.corner_der);
            tv_es_editado=itemView.findViewById(R.id.tv_es_editado);
        }

        public synchronized void AsignarDatos(boolean margen, ItemChat chat) {
            String u = chat.getMensaje();
            int est = chat.getEstado();
            String hor = chat.getHora();
            String idEstado = chat.getId_msg_resp();

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

            mensaje_der.setTextSize(tam_fuente);

            EmojiInformation emojiInformation = EmojiUtils.emojiInformation(u);
            int res;

            if (emojiInformation.isOnlyEmojis && emojiInformation.emojis.size() <=2){
                res = R.dimen.emoji_size_single_emoji;
                mensaje_der.setEmojiSizeRes(res, false);
                mensaje_der.setTextSize(tam_fuente);

                mensaje_der.setText(u);
            }
            else if(emojiInformation.isOnlyEmojis && emojiInformation.emojis.size() < 6) {
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

            setStatusImg(est, estadoView);

            mensaje_respuesta_img_der.setVisibility(View.GONE);
            hora_der.setText(hor);


            boolean exist = true;
            ItemEstado estado = dbWorker.obtenerEstado(idEstado);
            if(estado==null) exist = false;

            contenedor_mensaje_respuesta_der.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setEnabled(false);
                    if(modoSeleccion){
                        chatsActivity.actualizarSeleccion(chat);
                    }
                    else{
                        chatsActivity.abrirVisorEstado(estado);
//                        if(estado!=null) chatsActivity.abrirVisorEstado(estado.getCorreo(),estado.getId());
//                        else chatsActivity.abrirVisorEstado("","");
                    }
                    v.setEnabled(true);
                }
            });

            String nomRes;
            if(exist){
                if(estado.getCorreo().equals(YouChatApplication.correo))
                    nomRes="Yo";
                else
                    nomRes = dbWorker.obtenerNombre(estado.getCorreo());
            }
            else nomRes = "Usuario no encontrado";

            mensaje_respuesta_nombre_der.setText(nomRes);
            res = R.dimen.emoji_size_default;
            mensaje_respuesta_nombre_der.setEmojiSizeRes(res, false);

            if(exist){
                mensaje_respuesta_texto_der.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getFont_msg_der()));
                if(estado.getTipo_estado()==99){
                    mensaje_respuesta_img_der.setVisibility(View.VISIBLE);
                    Glide.with(mensaje_respuesta_img_der.getContext()).load(estado.getRuta_imagen()).
                            error(R.drawable.image_placeholder).into(mensaje_respuesta_img_der);

                    if(estado.getTexto().equals("")) mensaje_respuesta_texto_der.setText("Estado");
                    else {
                        emojiInformation = EmojiUtils.emojiInformation(estado.getTexto());

                        if (emojiInformation.isOnlyEmojis && emojiInformation.emojis.size() < 6) {
                            res = R.dimen.emoji_size_only_emojis;
                        } else {
                            res = R.dimen.emoji_size_default;
                        }
                        mensaje_respuesta_texto_der.setEmojiSizeRes(res, false);
                        mensaje_respuesta_texto_der.setText(estado.getTexto());
                    }
                }
                else {
                    emojiInformation = EmojiUtils.emojiInformation(estado.getTexto());

                    if (emojiInformation.isOnlyEmojis && emojiInformation.emojis.size() < 6) {
                        res = R.dimen.emoji_size_only_emojis;
                    } else {
                        res = R.dimen.emoji_size_default;
                    }
                    mensaje_respuesta_texto_der.setEmojiSizeRes(res, false);
                    mensaje_respuesta_texto_der.setText("Estado: "+estado.getTexto());
                }
            }
            else {
                res = R.dimen.emoji_size_default;
                mensaje_respuesta_img_der.setVisibility(View.GONE);
                mensaje_respuesta_texto_der.setEmojiSizeRes(res, false);
                mensaje_respuesta_texto_der.setText("Estado no encontrado");
                if(modoNoche) mensaje_respuesta_texto_der.setTextColor(0xFFA22B28);
                else mensaje_respuesta_texto_der.setTextColor(0xFFE53935);
            }
            fondo_mensaje_der.setBackground(drawable);
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

            String rutaDato = chat.getRuta_Dato();
            File tgs = new File(rutaDato);
            if(tgs.exists()){
                contendor_view_descarga.setVisibility(View.GONE);
                sticker_empty.setVisibility(View.GONE);
                if(rutaDato.toLowerCase().endsWith(".tgs")){
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
                    Glide.with(context)
                            .load(rutaDato)
                            .error(R.drawable.sticker_empty_focus)
                            .into(sticker_izq);
                    sticker_izq.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(modoSeleccion){
                                chatsActivity.actualizarSeleccion(chat);
                            }
                        }
                    });
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

            String rutaDato = chat.getRuta_Dato();
            File tgs = new File(rutaDato);
            if(tgs.exists()){
                sticker_empty.setVisibility(View.GONE);
                if(rutaDato.toLowerCase().endsWith(".tgs")){
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
                else {
                    Glide.with(context)
                            .load(rutaDato)
                            .error(R.drawable.sticker_empty_focus)
                            .into(sticker_der);
                    sticker_der.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(modoSeleccion){
                                chatsActivity.actualizarSeleccion(chat);
                            }
                        }
                    });
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

    ////////////////////////////////////////////////////////////ViewHolderDatosMensajeTemaIzq///////////////////////////////////////////////////
    public class ViewHolderDatosMensajeTemaIzq extends ViewHolderDatos {

        View fondo_msg_chat, est_reenviado;
        TextView hora_img_izq;
        AppCompatImageView corner_izq;
        private View background_mensaje;

        //disenno
        private TextViewMsgIzqGI tema_nombre, tema_creador;
        private MaterialCardView theme_card, theme_bar, fondo_btn;
        private AppCompatImageView theme_msg_izq, theme_msg_der, theme_radio;
        private TextView theme_btn;
        private ImageView img_fondo_style_theme;

        public ViewHolderDatosMensajeTemaIzq(@NonNull View itemView) {
            super(itemView);
            hora_img_izq=itemView.findViewById(R.id.hora_img_izq);
            fondo_msg_chat=itemView.findViewById(R.id.fondo_msg_chat);
            est_reenviado=itemView.findViewById(R.id.est_reenviado);
            background_mensaje= itemView.findViewById(R.id.background_mensaje);
            corner_izq=itemView.findViewById(R.id.corner_izq);

            tema_nombre=itemView.findViewById(R.id.tema_nombre);
            tema_creador=itemView.findViewById(R.id.tema_creador);
            theme_card=itemView.findViewById(R.id.theme_card);
            theme_bar=itemView.findViewById(R.id.theme_bar);
            theme_msg_izq=itemView.findViewById(R.id.theme_msg_izq);
            theme_msg_der=itemView.findViewById(R.id.theme_msg_der);
            theme_radio=itemView.findViewById(R.id.theme_radio);
            theme_btn=itemView.findViewById(R.id.theme_btn);
            fondo_btn=itemView.findViewById(R.id.fondo_btn);
            img_fondo_style_theme = itemView.findViewById(R.id.img_fondo_style_theme);
        }

        @SuppressLint("RestrictedApi")
        public synchronized void AsignarDatos(boolean margen, ItemChat chat) {
            String id = chat.getId();
            String u = chat.getMensaje();
            String ruta = chat.getRuta_Dato();
            String hor = chat.getHora();
            boolean esReenviado = chat.EsReenviado();

            if(modoSeleccion){
                if(chat.estaSeleccionado()) background_mensaje.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getSel_msj()));
                else background_mensaje.setBackgroundColor(Color.TRANSPARENT);
            }
            else background_mensaje.setBackgroundColor(Color.TRANSPARENT);

            if(esReenviado) est_reenviado.setVisibility(View.VISIBLE);
            else est_reenviado.setVisibility(View.GONE);

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

            //disenno
            ItemTemas tema = Convertidor.createItemTemasOfMensaje(chat.getMensaje());
            if(tema!=null){
                tema_nombre.setText(tema.getNombre());
                tema_creador.setText(tema.getCreador());

                SpannableString s;
                s = new SpannableString("Nombre:\n"+tema.getNombre());
                s.setSpan(new RelativeSizeSpan(0.7f), 0, 7, 0);
//            s.setSpan(new RelativeSizeSpan(1.1f), 7, s.length(), 0);
                tema_nombre.setText(s);

                String nombre = dbWorker.obtenerNombre(tema.getCreador());

                s = new SpannableString("Creado por:\n"+nombre);
                s.setSpan(new RelativeSizeSpan(0.7f), 0, 11, 0);
                s.setSpan(new RelativeSizeSpan(0.9f), 11, s.length(), 0);
                tema_creador.setText(s);

                if(!tema.getRutaImg().isEmpty()){
                    if(new File(YouChatApplication.RUTA_FONDO_YOUCHAT+tema.getRutaImg()).exists()){
                        img_fondo_style_theme.setVisibility(View.VISIBLE);
                        Glide.with(context)
                                .load(YouChatApplication.RUTA_FONDO_YOUCHAT+tema.getRutaImg())
                                .error(0)
                                .into(img_fondo_style_theme);
                    }
                    else img_fondo_style_theme.setVisibility(View.GONE);
                }
                else img_fondo_style_theme.setVisibility(View.GONE);

                theme_btn.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getColor_msg_izq_audio()));
                fondo_btn.setStrokeColor(Color.parseColor(YouChatApplication.itemTemas.getColor_msg_izq_audio()));
                fondo_btn.setRadius(YouChatApplication.curvaChat<=32?YouChatApplication.curvaChat:32);

                if(dbWorker.existeTemaId(tema.getId())){
                    fondo_btn.setVisibility(View.INVISIBLE);
                    fondo_btn.setOnClickListener(null);
                }
                else {
                    fondo_btn.setVisibility(View.VISIBLE);
                    fondo_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dbWorker.insertarNuevoTema(tema);
                            fondo_btn.setVisibility(View.INVISIBLE);
                            fondo_btn.setOnClickListener(null);
                        }
                    });
                }
                theme_card.setCardBackgroundColor(Color.parseColor(tema.getColor_fondo()));
                theme_bar.setCardBackgroundColor(Color.parseColor(tema.getColor_barra()));
                theme_msg_izq.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor(tema.getColor_msg_izq())));
                theme_msg_der.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor(tema.getColor_msg_der())));
                theme_radio.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor(tema.getColor_accento())));
            }
            else {
                tema_creador.setText("Error, tema no compatible para esta versión");
                fondo_btn.setVisibility(View.INVISIBLE);
            }


            fondo_msg_chat.setBackground(drawable);
        }
    }

    ////////////////////////////////////////////////////////////ViewHolderDatosMensajeTemaDer///////////////////////////////////////////////////
    public class ViewHolderDatosMensajeTemaDer extends ViewHolderDatos {

        View fondo_msg_chat,est_reenviado;

        TextView hora_img_der;
        AppCompatImageView estadoView, corner_der;
        private View background_mensaje;

        //disenno
        private TextViewMsgDerGI tema_nombre, tema_creador;
        private MaterialCardView theme_card, theme_bar, fondo_btn;
        private AppCompatImageView theme_msg_izq, theme_msg_der, theme_radio;
        private TextView theme_btn;
        private ImageView img_fondo_style_theme;

        public ViewHolderDatosMensajeTemaDer(@NonNull View itemView) {
            super(itemView);
            estadoView = itemView.findViewById(R.id.estadoImg);
            hora_img_der=itemView.findViewById(R.id.hora_img_der);
            fondo_msg_chat=itemView.findViewById(R.id.fondo_msg_chat);
            est_reenviado=itemView.findViewById(R.id.est_reenviado);
            background_mensaje= itemView.findViewById(R.id.background_mensaje);
            corner_der=itemView.findViewById(R.id.corner_der);

            tema_nombre=itemView.findViewById(R.id.tema_nombre);
            tema_creador=itemView.findViewById(R.id.tema_creador);
            theme_card=itemView.findViewById(R.id.theme_card);
            theme_bar=itemView.findViewById(R.id.theme_bar);
            theme_msg_izq=itemView.findViewById(R.id.theme_msg_izq);
            theme_msg_der=itemView.findViewById(R.id.theme_msg_der);
            theme_radio=itemView.findViewById(R.id.theme_radio);
            theme_btn=itemView.findViewById(R.id.theme_btn);
            fondo_btn=itemView.findViewById(R.id.fondo_btn);
            img_fondo_style_theme = itemView.findViewById(R.id.img_fondo_style_theme);
        }

        public synchronized void AsignarDatos(boolean margen, ItemChat chat) {
            int est = chat.getEstado();
            String hor = chat.getHora();
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

            fondo_msg_chat.setBackground(drawable);

            setStatusImg(est, estadoView);
            hora_img_der.setText(hor);

            //disenno
            ItemTemas tema = Convertidor.createItemTemasOfMensaje(chat.getMensaje());
            if(tema!=null){
                SpannableString s;
                s = new SpannableString("Nombre:\n"+tema.getNombre());
                s.setSpan(new RelativeSizeSpan(0.7f), 0, 7, 0);
//            s.setSpan(new RelativeSizeSpan(1.1f), 7, s.length(), 0);
                tema_nombre.setText(s);

                String nombre = dbWorker.obtenerNombre(tema.getCreador());

                s = new SpannableString("Creado por:\n"+nombre);
                s.setSpan(new RelativeSizeSpan(0.7f), 0, 11, 0);
                s.setSpan(new RelativeSizeSpan(0.9f), 11, s.length(), 0);
                tema_creador.setText(s);

                if(!tema.getRutaImg().isEmpty()){
                    if(new File(YouChatApplication.RUTA_FONDO_YOUCHAT+tema.getRutaImg()).exists()){
                        img_fondo_style_theme.setVisibility(View.VISIBLE);
                        Glide.with(context)
                                .load(YouChatApplication.RUTA_FONDO_YOUCHAT+tema.getRutaImg())
                                .error(0)
                                .into(img_fondo_style_theme);
                    }
                    else img_fondo_style_theme.setVisibility(View.GONE);
                }
                else img_fondo_style_theme.setVisibility(View.GONE);

                theme_btn.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getColor_msg_der_audio()));
                fondo_btn.setStrokeColor(Color.parseColor(YouChatApplication.itemTemas.getColor_msg_der_audio()));
                fondo_btn.setRadius(YouChatApplication.curvaChat<=35?YouChatApplication.curvaChat:35);

                if(dbWorker.existeTemaId(tema.getId())){
                    fondo_btn.setVisibility(View.INVISIBLE);
                    fondo_btn.setOnClickListener(null);
                }
                else {
                    fondo_btn.setVisibility(View.VISIBLE);
                    fondo_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dbWorker.insertarNuevoTema(tema);
                            fondo_btn.setVisibility(View.INVISIBLE);
                            fondo_btn.setOnClickListener(null);
                        }
                    });
                }
                theme_card.setCardBackgroundColor(Color.parseColor(tema.getColor_fondo()));
                theme_bar.setCardBackgroundColor(Color.parseColor(tema.getColor_barra()));
                theme_msg_izq.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor(tema.getColor_msg_izq())));
                theme_msg_der.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor(tema.getColor_msg_der())));
                theme_radio.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor(tema.getColor_accento())));
            }
            else {
                tema_creador.setText("Error, tema no compatible para esta versión");
                fondo_btn.setVisibility(View.INVISIBLE);
            }


            fondo_msg_chat.setBackground(drawable);
        }
    }

    ////////////////////////////////////////////////////////////ViewHolderDatosPostIzqResp///////////////////////////////////////////////////
    public class ViewHolderDatosPostIzqResp extends ViewHolderDatos {

        //lo q esta en el xml del chat izq
        View fondo_msg_chat;
        EmojiTextView mensaje_izq;
        TextView hora_izq;
        AppCompatImageView corner_izq;
        private View background_mensaje, tv_es_editado;

        //post
        private ImageView foto_noti, user_type;
        private TextViewFontGenGI correo_noti;
        private TextViewPostGI mensaje_noti;
        private TextViewFontGenOscuroGI hora_noti;
        private TextViewFontResGI nombre_noti;
        private View fondo_msg_post;


        public ViewHolderDatosPostIzqResp(@NonNull View itemView) {
            super(itemView);
            mensaje_izq = itemView.findViewById(R.id.mensaje_izq);
            hora_izq = itemView.findViewById(R.id.hora_izq);
            fondo_msg_chat=itemView.findViewById(R.id.fondo_msg_chat);
            background_mensaje= itemView.findViewById(R.id.background_mensaje);
            corner_izq=itemView.findViewById(R.id.corner_izq);
            tv_es_editado=itemView.findViewById(R.id.tv_es_editado);

            //post
            foto_noti = itemView.findViewById(R.id.foto_noti);
            nombre_noti = itemView.findViewById(R.id.nombre_noti);
            correo_noti = itemView.findViewById(R.id.correo_noti);
            mensaje_noti = itemView.findViewById(R.id.mensaje_noti);
            hora_noti = itemView.findViewById(R.id.hora_noti);
            user_type = itemView.findViewById(R.id.user_type);
            fondo_msg_post = itemView.findViewById(R.id.fondo_msg_post);
        }

        public synchronized void AsignarDatos(boolean margen, ItemChat chat) {
            String u = chat.getMensaje();
            String hor = chat.getHora();
            String idPost = chat.getId_msg_resp();

            if(chat.esEditado()) tv_es_editado.setVisibility(View.VISIBLE);
            else tv_es_editado.setVisibility(View.GONE);

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

            EmojiInformation emojiInformation = EmojiUtils.emojiInformation(u);
            int res;

            if (emojiInformation.isOnlyEmojis && emojiInformation.emojis.size() <=2){
                res = R.dimen.emoji_size_single_emoji;
                mensaje_izq.setEmojiSizeRes(res, false);
                mensaje_izq.setTextSize(tam_fuente);

                mensaje_izq.setText(u);
            }
            else if(emojiInformation.isOnlyEmojis && emojiInformation.emojis.size() < 6) {
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

            hora_izq.setText(hor);
            boolean exist = true;
            ItemPost post = dbWorker.obtenerPost(idPost);
            if(post==null)
                exist = false;

            if(exist){
//                fondo_msg_post.setVisibility(View.VISIBLE);
                if(post.getTipo_usuario()==5 || post.getTipo_usuario()==4){
                    user_type.setImageResource(R.drawable.verified_profile);
                    user_type.setVisibility(View.VISIBLE);
                }
                else if(post.getTipo_usuario()==3){
                    user_type.setImageResource(R.drawable.vip_crown_line);
                    user_type.setVisibility(View.VISIBLE);
                }
                else if(post.getTipo_usuario()==2){
                    user_type.setImageResource(R.drawable.vip_diamond_line);
                    user_type.setVisibility(View.VISIBLE);
                }
                else if(post.getTipo_usuario()==1){
                    user_type.setImageResource(R.drawable.award_line);
                    user_type.setVisibility(View.VISIBLE);
                }
                else user_type.setVisibility(View.GONE);

                switch (post.getIcono()){
                    case 1:
                        Glide.with(context).load(R.drawable.noti1).dontAnimate().into(foto_noti);
                        break;
                    case 2:
                        Glide.with(context).load(R.drawable.noti2).dontAnimate().into(foto_noti);
                        break;
                    case 3:
                        Glide.with(context).load(R.drawable.noti3).dontAnimate().into(foto_noti);
                        break;
                    case 4:
                        Glide.with(context).load(R.drawable.noti4).dontAnimate().into(foto_noti);
                        break;
                    case 5:
                        Glide.with(context).load(R.drawable.noti5).dontAnimate().into(foto_noti);
                        break;
                    case 6:
                        Glide.with(context).load(R.drawable.noti6).dontAnimate().into(foto_noti);
                        break;
                    case 7:
                        Glide.with(context).load(R.drawable.noti7).dontAnimate().into(foto_noti);
                        break;
                    case 8:
                        Glide.with(context).load(R.drawable.noti8).dontAnimate().into(foto_noti);
                        break;
                    case 9:
                        Glide.with(context).load(R.drawable.noti9).dontAnimate().into(foto_noti);
                        break;
                    case 10:
                        Glide.with(context).load(R.drawable.noti10).dontAnimate().into(foto_noti);
                        break;
                    case 11:
                        Glide.with(context).load(R.drawable.noti11).dontAnimate().into(foto_noti);
                        break;
                    case 12:
                        Glide.with(context).load(R.drawable.noti12).dontAnimate().into(foto_noti);
                        break;
                    case 13:
                        Glide.with(context).load(R.drawable.noti13).dontAnimate().into(foto_noti);
                        break;
                    case 14:
                        Glide.with(context).load(R.drawable.noti14).dontAnimate().into(foto_noti);
                        break;
                    case 15:
                        Glide.with(context).load(R.drawable.noti15).dontAnimate().into(foto_noti);
                        break;
                    case 16:
                        Glide.with(context).load(R.drawable.noti16).dontAnimate().into(foto_noti);
                        break;
                    default: Glide.with(context).load(R.drawable.noti1).dontAnimate().into(foto_noti);
                }
                nombre_noti.setText(post.getNombre());
                correo_noti.setText(post.getCorreo());
                mensaje_noti.setText(post.getTexto());
                hora_noti.setText(Convertidor.convertirFechaAFechaLinda(post.getFecha())+", "+post.getHora());
            }
            else {
//                fondo_msg_post.setVisibility(View.GONE);
                user_type.setVisibility(View.GONE);
                Glide.with(context).load(R.drawable.noti1).dontAnimate().into(foto_noti);
                nombre_noti.setText("");
                correo_noti.setText("");
                mensaje_noti.setText("Post no encontrado");
                hora_noti.setText("");
            }
            fondo_msg_chat.setBackground(drawable);
        }
    }

    ////////////////////////////////////////////////////////////ViewHolderDatosPostDerResp///////////////////////////////////////////////////
    public class ViewHolderDatosPostDerResp extends ViewHolderDatos {

        //lo q esta en el xml del chat der
        View fondo_msg_chat, fondo_mensaje_der;
        EmojiTextView mensaje_der;
        TextView hora_der;
        AppCompatImageView estadoView, corner_der;

        private View background_mensaje, tv_es_editado;

        //post
        private ImageView foto_noti, user_type;
        private TextViewFontGenGI correo_noti;
        private TextViewPostGI mensaje_noti;
        private TextViewFontGenOscuroGI hora_noti;
        private TextViewFontResGI nombre_noti;
        private View fondo_msg_post;

        public ViewHolderDatosPostDerResp(@NonNull View itemView) {
            super(itemView);
            mensaje_der = itemView.findViewById(R.id.mensaje_der);
            estadoView = itemView.findViewById(R.id.estadoText);
            hora_der = itemView.findViewById(R.id.hora_der);
            background_mensaje= itemView.findViewById(R.id.background_mensaje);
            fondo_mensaje_der=itemView.findViewById(R.id.fondo_mensaje_der);
            fondo_msg_chat=itemView.findViewById(R.id.fondo_msg_chat);
            corner_der=itemView.findViewById(R.id.corner_der);
            tv_es_editado=itemView.findViewById(R.id.tv_es_editado);

            //post
            foto_noti = itemView.findViewById(R.id.foto_noti);
            nombre_noti = itemView.findViewById(R.id.nombre_noti);
            correo_noti = itemView.findViewById(R.id.correo_noti);
            mensaje_noti = itemView.findViewById(R.id.mensaje_noti);
            hora_noti = itemView.findViewById(R.id.hora_noti);
            user_type = itemView.findViewById(R.id.user_type);
            fondo_msg_post = itemView.findViewById(R.id.fondo_msg_post);
        }

        public synchronized void AsignarDatos(boolean margen, ItemChat chat) {
            String u = chat.getMensaje();
            int est = chat.getEstado();
            String hor = chat.getHora();
            String idPost = chat.getId_msg_resp();

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

            if (emojiInformation.isOnlyEmojis && emojiInformation.emojis.size() <=2){
                res = R.dimen.emoji_size_single_emoji;
                mensaje_der.setEmojiSizeRes(res, false);
                mensaje_der.setTextSize(tam_fuente);

                mensaje_der.setText(u);
            }
            else if(emojiInformation.isOnlyEmojis && emojiInformation.emojis.size() < 6) {
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

            boolean exist = true;
            ItemPost post = dbWorker.obtenerPost(idPost);
            if(post==null)
                exist = false;

            if(exist){
                fondo_msg_post.setVisibility(View.VISIBLE);
                if(post.getTipo_usuario()==5 || post.getTipo_usuario()==4){
                    user_type.setImageResource(R.drawable.verified_profile);
                    user_type.setVisibility(View.VISIBLE);
                }
                else if(post.getTipo_usuario()==3){
                    user_type.setImageResource(R.drawable.vip_crown_line);
                    user_type.setVisibility(View.VISIBLE);
                }
                else if(post.getTipo_usuario()==2){
                    user_type.setImageResource(R.drawable.vip_diamond_line);
                    user_type.setVisibility(View.VISIBLE);
                }
                else if(post.getTipo_usuario()==1){
                    user_type.setImageResource(R.drawable.award_line);
                    user_type.setVisibility(View.VISIBLE);
                }
                else user_type.setVisibility(View.GONE);

                switch (post.getIcono()){
                    case 1:
                        Glide.with(context).load(R.drawable.noti1).dontAnimate().into(foto_noti);
                        break;
                    case 2:
                        Glide.with(context).load(R.drawable.noti2).dontAnimate().into(foto_noti);
                        break;
                    case 3:
                        Glide.with(context).load(R.drawable.noti3).dontAnimate().into(foto_noti);
                        break;
                    case 4:
                        Glide.with(context).load(R.drawable.noti4).dontAnimate().into(foto_noti);
                        break;
                    case 5:
                        Glide.with(context).load(R.drawable.noti5).dontAnimate().into(foto_noti);
                        break;
                    case 6:
                        Glide.with(context).load(R.drawable.noti6).dontAnimate().into(foto_noti);
                        break;
                    case 7:
                        Glide.with(context).load(R.drawable.noti7).dontAnimate().into(foto_noti);
                        break;
                    case 8:
                        Glide.with(context).load(R.drawable.noti8).dontAnimate().into(foto_noti);
                        break;
                    case 9:
                        Glide.with(context).load(R.drawable.noti9).dontAnimate().into(foto_noti);
                        break;
                    case 10:
                        Glide.with(context).load(R.drawable.noti10).dontAnimate().into(foto_noti);
                        break;
                    case 11:
                        Glide.with(context).load(R.drawable.noti11).dontAnimate().into(foto_noti);
                        break;
                    case 12:
                        Glide.with(context).load(R.drawable.noti12).dontAnimate().into(foto_noti);
                        break;
                    case 13:
                        Glide.with(context).load(R.drawable.noti13).dontAnimate().into(foto_noti);
                        break;
                    case 14:
                        Glide.with(context).load(R.drawable.noti14).dontAnimate().into(foto_noti);
                        break;
                    case 15:
                        Glide.with(context).load(R.drawable.noti15).dontAnimate().into(foto_noti);
                        break;
                    case 16:
                        Glide.with(context).load(R.drawable.noti16).dontAnimate().into(foto_noti);
                        break;
                    default: Glide.with(context).load(R.drawable.noti1).dontAnimate().into(foto_noti);
                }
                nombre_noti.setText(post.getNombre());
                correo_noti.setText(post.getCorreo());
                mensaje_noti.setText(post.getTexto());
                hora_noti.setText(Convertidor.convertirFechaAFechaLinda(post.getFecha())+", "+post.getHora());
            }
            else {
                fondo_msg_post.setVisibility(View.GONE);
            }
            fondo_mensaje_der.setBackground(drawable);
        }
    }

    ////////////////////////////////////////////////////////////ViewHolderDatosTextIzqEmoji///////////////////////////////////////////////////
    public class ViewHolderDatosTextIzqEmoji extends ViewHolderDatos{

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
            boolean esReenviado = chat.EsReenviado();

            if(esReenviado){
                view1.setVisibility(View.VISIBLE);
                view2.setVisibility(View.VISIBLE);
            }
            else{
                view1.setVisibility(View.GONE);
                view2.setVisibility(View.GONE);
            }


            if(modoSeleccion){
                if(chat.estaSeleccionado()) background_mensaje.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getSel_msj()));
                else background_mensaje.setBackgroundColor(Color.TRANSPARENT);
            }
            else background_mensaje.setBackgroundColor(Color.TRANSPARENT);

            hora_der.setText(hor);
            setStatusImg(est, estadoView);

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

            if(reenviado){
                view1.setVisibility(View.VISIBLE);
                view2.setVisibility(View.VISIBLE);
            }
            else{
                view1.setVisibility(View.GONE);
                view2.setVisibility(View.GONE);
            }
            setStatusImg(est, estadoView);

            hora_der.setText(hor);
        }
    }

    ////////////////////////////////////////////////////////////ViewHolderDatosMensajeSolicitudSeguir///////////////////////////////////////////////////
    public class ViewHolderDatosMensajeSolicitudSeguir extends ViewHolderDatos {

        private TextView tv_correo_seguir, txt_ok, txt_cancel;
        private MaterialCardView btn_cancel, btn_ok;
        private View fondo_msg_chat;
        AppCompatImageView corner_izq;
        private View background_mensaje;

        public ViewHolderDatosMensajeSolicitudSeguir(@NonNull View itemView) {
            super(itemView);

            tv_correo_seguir=itemView.findViewById(R.id.tv_correo_seguir);
            btn_cancel=itemView.findViewById(R.id.btn_cancel);
            btn_ok=itemView.findViewById(R.id.btn_ok);
            fondo_msg_chat=itemView.findViewById(R.id.fondo_msg_chat);
            background_mensaje= itemView.findViewById(R.id.background_mensaje);
            corner_izq=itemView.findViewById(R.id.corner_izq);

            txt_ok=itemView.findViewById(R.id.txt_ok);
            txt_cancel=itemView.findViewById(R.id.txt_cancel);
        }

        public synchronized void AsignarDatos(boolean margen, ItemChat chat){
            String idChat = chat.getId();
            String correo = chat.getCorreo();

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

            tv_correo_seguir.setText(dbWorker.obtenerNombre(correo));
            tv_correo_seguir.setTextSize(tam_fuente);

            txt_ok.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getColor_msg_izq_audio()));
            btn_ok.setStrokeColor(Color.parseColor(YouChatApplication.itemTemas.getColor_msg_izq_audio()));
            btn_ok.setRadius(YouChatApplication.curvaChat<=35?YouChatApplication.curvaChat:35);

            txt_cancel.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getColor_msg_izq_audio()));
            btn_cancel.setStrokeColor(Color.parseColor(YouChatApplication.itemTemas.getColor_msg_izq_audio()));
            btn_cancel.setRadius(YouChatApplication.curvaChat<=35?YouChatApplication.curvaChat:35);

            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setEnabled(false);
                    if(modoSeleccion){
                        chatsActivity.actualizarSeleccion(chat);
                    }
                    else chatsActivity.aceptarSolicitudSeguir( idChat, correo);
                    v.setEnabled(true);
                }
            });

            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setEnabled(false);
                    if(modoSeleccion){
                        chatsActivity.actualizarSeleccion(chat);
                    }
                    else chatsActivity.cancelarSolicitudSeguir(idChat);
                    v.setEnabled(true);
                }
            });
            fondo_msg_chat.setBackground(drawable);
        }
    }

    ////////////////////////////////////////////////////////////ViewHolderDatosUnionYouChat///////////////////////////////////////////////////
    public class ViewHolderDatosUnionYouChat extends ViewHolderDatos {
        TextView fecha_global;
        
        public ViewHolderDatosUnionYouChat(@NonNull View itemView) {
            super(itemView);
            fecha_global = itemView.findViewById(R.id.fecha_global);
        }

        public synchronized void AsignarDatos(String cor) {
            fecha_global.setText("¡"+dbWorker.obtenerNombre(cor)+" se ha unido a YouChat!");
            fecha_global.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getFont_msg_fecha()));
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