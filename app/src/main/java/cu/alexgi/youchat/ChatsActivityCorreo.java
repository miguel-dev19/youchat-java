package cu.alexgi.youchat;

import android.animation.Animator;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.b44t.messenger.DcEventCenter;
import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.listeners.OnEmojiPopupDismissListener;
import com.vanniktech.emoji.listeners.OnEmojiPopupShownListener;
import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.api.widget.Widget;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import cu.alexgi.youchat.BottomSheetDialogFragment.BottomSheetDialogFragment_Atajos;
import cu.alexgi.youchat.FileExplorer.SimpleFileExplorerActivity;
import cu.alexgi.youchat.FileExplorer.SimpleFileExplorerFragment;
import cu.alexgi.youchat.Popups.PopupMsgChatCorreo;
import cu.alexgi.youchat.ViewPagerPhotoView.CommonUtil;
import cu.alexgi.youchat.ViewPagerPhotoView.PhotoViewPager;
import cu.alexgi.youchat.ViewPagerPhotoView.TransBigImageView;
import cu.alexgi.youchat.ViewPagerPhotoView.TransSmallImageView;
import cu.alexgi.youchat.adapters.AdaptadorDatosChatCorreo;
import cu.alexgi.youchat.audiowave.AudioWaveView;
import cu.alexgi.youchat.audiowave.OnProgressListener;
import cu.alexgi.youchat.chatUtils.AnimatingToggle;
import cu.alexgi.youchat.chatUtils.AttachmentTypeSelector;
import cu.alexgi.youchat.chatUtils.HidingLinearLayout;
import cu.alexgi.youchat.chatUtils.InputAwareLayout;
import cu.alexgi.youchat.chatUtils.InputPanel;
import cu.alexgi.youchat.chatUtils.KeyboardAwareLinearLayout;
import cu.alexgi.youchat.chatUtils.Prefs;
import cu.alexgi.youchat.chatUtils.SendButton;
import cu.alexgi.youchat.chatUtils.ViewUtil;
import cu.alexgi.youchat.chatUtils.camera.QuickAttachmentDrawer;
import cu.alexgi.youchat.items.ItemAdjuntoCorreo;
import cu.alexgi.youchat.items.ItemAtajo;
import cu.alexgi.youchat.items.ItemChat;
import cu.alexgi.youchat.items.ItemContacto;
import cu.alexgi.youchat.items.ItemImg;
import cu.alexgi.youchat.items.ItemMensajeCorreo;
import cu.alexgi.youchat.items.ItemUsuarioCorreo;
import cu.alexgi.youchat.photoView.photoViewLibrary.Info;
import cu.alexgi.youchat.photoView.photoViewLibrary.PhotoView;
import cu.alexgi.youchat.photoutil.CameraPhoto;
import cu.alexgi.youchat.photoutil.ImageLoader;
import cu.alexgi.youchat.progressbar.DownloadProgressView;
import cu.alexgi.youchat.sticky_headers_data.StickyRecyclerHeadersDecoration;
import cu.alexgi.youchat.swipebackfragment.BaseSwipeBackFragment;
import cu.alexgi.youchat.swipebackfragment.SwipeBackLayout;

import static android.app.Activity.RESULT_OK;
import static cu.alexgi.youchat.MainActivity.context;
import static cu.alexgi.youchat.MainActivity.dbWorker;
import static cu.alexgi.youchat.MainActivity.mainActivity;
import static cu.alexgi.youchat.MainActivity.permisos;
//import cu.alexgi.youchat.chatUtils.mms.AttachmentManager;

public class ChatsActivityCorreo extends BaseSwipeBackFragment
        implements NavigationView.OnNavigationItemSelectedListener,
        SearchView.OnQueryTextListener,
        DcEventCenter.DcEventDelegate,
        KeyboardAwareLinearLayout.OnKeyboardShownListener,
        QuickAttachmentDrawer.AttachmentDrawerListener,
        InputPanel.Listener,
        InputPanel.MediaListener{//}, AttachmentManager.AttachmentListener{


//    /////SENSOR//////
//    private SensorManager deviceSensorManager;
//    PowerManager powerManager;
//    PowerManager.WakeLock wakeLock;
//    ////////////////

    ///view pager photo view ini
    private TransSmallImageView transImage;
    private TransBigImageView transBigImage;
    private int[] locationOnScreen;
    private FrameLayout previewParent;
    private PhotoViewPager preview_viewPager;
    private int positionViewPagerPreviewInicial, positionViewPagerPreviewActual;
    private Info info_visorImg_pv;
    private ImageView item_photoView_visorImg_pv;
    ///view pager photo view fin

    protected static final int ACTION_SEND_OUT = 1;
    private static final int ACTION_SAVE_DRAFT = 2;
    private boolean esGrupo;
    private String miPathCamera, groupId, apodo,correo,aut_user, telefono, ruta_img_perfil;
    private View icono_eliminar_answer, borrarTodosMsg, abrirGaleriaChat, buscarChat, abrirMenu, ir_atras, option_agregar_contacto, item_silenciar,item_bloquear, menu_chat, area_menu_chat;
    private TextView tv_item_silenciar,tv_item_bloquear;
    private CameraPhoto cameraPhoto;
    //trabajo con enviar imagenes
    static Bitmap bitmap;
    private static Bundle mibundle;
    //imagenes fin
    private EmojiPopup emojiPopup;
    private ImageView emojiButton;

    private EmojiEditText text_send;
    private Animation anim;
    private CircleImageView mini_img_perfil;
    private int tipo_contacto, cant_Msg_No_Vistos, calidad;
    private boolean estaSilenciado, estaBloqueado;
    private TextView chat_usuario,chat_correo;
    private boolean estaMinimizado;

    ///AUDIO
    private MediaRecorder recorder;
    private MediaPlayer mediaPlayer_audioRec;
    private AudioFocusRequest audioFocusRequest;
    private AudioManager audioManager;
    private File archivo;
    private String nombre_audio, ruta_audio;
    private View contenedor_input_audio_rec, contenedor_input_audio_play;
    private CircleImageView play_audio_rec;//stop_audio_rec,
    private AudioWaveView seekbar_audio_rec;
    private String audio_rec_duracion="";
    private TextView duration_audio_rec;
    private boolean isPlay_audioRec, isPause_audioRec, isEnd_audioRec, estaTimer_audioRec;
    private Timer timer_audioRec;
    private ViewGroup rootView;

    //Para llenar el recycler
    private ArrayList<ItemChat> datos_chat;
    private RecyclerView lista_chat;
    private AdaptadorDatosChatCorreo adaptadorChat;
    private LinearLayoutManager linearLayoutManager;
    private FloatingActionButton fab_bottom_chat;
    private int limite, tamArrAnt;
    private ProgressBar progressBar;
    private ArrayList<ItemChat> datos_chat_buscar;
    private boolean esMovX = false, esMovY = false, isScrolling=false, existEsperar=false;

//    private View fecha_act_chat;
//    private TextView fecha_act_chat_text;

    private TextView fab_bottom_chat_cant_msg_nvisto;
    private ArrayList<AlbumFile> imagenesChatAlbumFile;
    private int ultAccion=0;
    private ItemChat option;

    //para responder
    private MaterialCardView ll_audio, ll_imagen;
    private LinearLayout ll_answer, ll_buscar, ll_contacto;
    private boolean search_mark=false,search_audio=false,search_imagen=false;
    private String id_msg_answer;
    private int tipo_msg_answer;
    private TextView nombre_answer, texto_answer;
    private ShapeableImageView img_answer_chat;
    private ImageView btn_cancelar_buscar;
    private EmojiEditText et_buscar_chat;
    private boolean estaActivoBuscar;
    //para el fondo
    private String ruta_fondo_chat;
    private int ruta_drawable;
    private TopCropImageView chat_fondo;
    //private boolean modoEditorImg=false;
//    private String correoEstPersonal, estadoEstPersonal;
    private String idMsgEstadoCambiar;
    private boolean envioCorrectoMensaje;
    ////////////////Modo seleccionar////////////////
    private View ll_modo_seleccionar_chat, iv_cancelar_selec_chat, iv_reintentar_selec_chat,
            iv_copiar_selec_chat, iv_responder_selec_chat, iv_reenviar_selec_chat,
            iv_borrar_selec_chat;
    private TextView tv_cant_selec_chat;
    private ArrayList<ItemChat> chatSeleccionados;
    ////////////////DELTACHAT////////////////
    private AnimatingToggle buttonToggle;
    private SendButton sendButton;
    private ImageView attachButton, imageAudio;
    private InputAwareLayout container;
    private AttachmentTypeSelector attachmentTypeSelector;
    //private AttachmentManager attachmentManager;
    //private   AudioRecorder          audioRecorder;
    protected HidingLinearLayout quickAttachmentToggle;
    private   QuickAttachmentDrawer  quickAttachmentDrawer;
    ////////////////DELTACHAT////////////////


    public boolean isEsGrupo() {
        return esGrupo;
    }

    public enum UserBehaviour {
        CANCELING,
        LOCKING,
        NONE
    }
    public enum RecordingBehaviour {
        CANCELED,
        LOCKED,
        LOCK_DONE,
        RELEASED,
        STOP
    }
    private String TAG = "ChatsActivity";
    private View imageViewAudio, imageViewLockArrow, imageViewLock, imageViewMic, dustin, dustin_cover, imageViewStop;
    private View layoutSlideCancel, layoutLock, layoutEffect1, layoutEffect2, layoutStop, layoutDelete;//, viewInput;
    private TextView timeText, textViewSlide,btn_cancelar_audio;
    private Animation animBlink, animJump, animJumpFast;
    private boolean isDeleting;
    private boolean stopTrackingAction;
    private Handler handler;
    private int audioTotalTime,durationAudio;
    private TimerTask timerTask;
    private Timer audioTimer;
    private SimpleDateFormat timeFormatter;
    private float lastX, lastY;
    private float firstX, firstY;
    private float directionOffset, cancelOffset, lockOffset;
    private float dp = 0;
    private boolean isLocked = false,tostada= false;
    private UserBehaviour userBehaviour = UserBehaviour.NONE;
    boolean isLayoutDirectionRightToLeft;
    int screenWidth, screenHeight;

    public static ChatsActivityCorreo newInstance(Bundle bundle) {
        ChatsActivityCorreo fragment = new ChatsActivityCorreo();
        mibundle = bundle;
        return fragment;
    }

    public synchronized void initViewAudio(View view) {
        timeFormatter = new SimpleDateFormat("m:ss", Locale.getDefault());
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;
        isLayoutDirectionRightToLeft = getResources().getBoolean(R.bool.is_right_to_left);
        imageViewAudio = view.findViewById(R.id.imageViewAudio);
        imageViewStop = view.findViewById(R.id.imageViewStop);
        imageViewLock = view.findViewById(R.id.imageViewLock);
        imageViewLockArrow = view.findViewById(R.id.imageViewLockArrow);
        textViewSlide = view.findViewById(R.id.textViewSlide);
        timeText = view.findViewById(R.id.textViewTime);
        layoutSlideCancel = view.findViewById(R.id.layoutSlideCancel);
        layoutEffect2 = view.findViewById(R.id.layoutEffect2);
        layoutEffect1 = view.findViewById(R.id.layoutEffect1);
        layoutLock = view.findViewById(R.id.layoutLock);
        imageViewMic = view.findViewById(R.id.imageViewMic);
        dustin = view.findViewById(R.id.dustin);
        dustin_cover = view.findViewById(R.id.dustin_cover);
        btn_cancelar_audio=view.findViewById(R.id.btn_cancelar_audio);
        layoutStop=view.findViewById(R.id.layoutStop);
        layoutDelete=view.findViewById(R.id.layoutDelete);
        handler = new Handler(Looper.getMainLooper());
        dp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
        animBlink = AnimationUtils.loadAnimation(context, R.anim.blink);
        animJump = AnimationUtils.loadAnimation(context, R.anim.jump);
        animJumpFast = AnimationUtils.loadAnimation(context, R.anim.jump_fast);

        info_visorImg_pv = null;

        layoutEffect1.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_barchat()));
        layoutEffect2.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_barchat()));
        setupRecording();
    }

    private void setupRecording() {
        imageViewAudio.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(permisos.requestPermissionAlmacenamiento() && permisos.requestPermissionAudio())
                {
                    if (isDeleting) {Log.e("***onTouch***","isDELETING");  return true;}
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        Log.e("***onTouch***","ACTION_DOWN");
                        cancelOffset = (float) (screenWidth / 2.8);
                        lockOffset = (float) (screenWidth / 2.5);
                        if (firstX == 0) firstX = motionEvent.getRawX();
                        if (firstY == 0) firstY = motionEvent.getRawY();
                        startRecord();
                    }
                    else if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                        Log.e("***onTouch***","ACTION_UP || ACTION_CANCEL");
                        if (motionEvent.getAction() == MotionEvent.ACTION_UP) stopRecording(RecordingBehaviour.RELEASED);
                    }
                    else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                        Log.e("***onTouch***","ACTION_MOVE");
                        if (stopTrackingAction) {Log.e("***onTouch***","STOP_TRACKING"); return true;}
                        UserBehaviour direction = UserBehaviour.NONE;
                        float motionX = Math.abs(firstX - motionEvent.getRawX());
                        float motionY = Math.abs(firstY - motionEvent.getRawY());
                        if (isLayoutDirectionRightToLeft ? (motionX > directionOffset && lastX > firstX && lastY > firstY) : (motionX > directionOffset && lastX < firstX && lastY < firstY)) {
                            if (isLayoutDirectionRightToLeft ? (motionX > motionY && lastX > firstX) : (motionX > motionY && lastX < firstX)) {
                                direction = UserBehaviour.CANCELING;
                                Log.e("***onTouch***","USER BEHAVIOUR CANCELLING");
                            }
                            else if (motionY > motionX && lastY < firstY) {
                                direction = UserBehaviour.LOCKING;
                                Log.e("***onTouch***","LOCKING");
                            }

                        }
                        else if (isLayoutDirectionRightToLeft ? (motionX > motionY && motionX > directionOffset && lastX > firstX) : (motionX > motionY && motionX > directionOffset && lastX < firstX)) direction = UserBehaviour.CANCELING;
                        else if (motionY > motionX && motionY > directionOffset && lastY < firstY) {
                            direction = UserBehaviour.LOCKING;
                            Log.e("***onTouch***","LOCKING");
                        }

                        if (direction == UserBehaviour.CANCELING) {
                            Log.e("***onTouch***","UserBehaviour.CANCELING");
                            if (userBehaviour == UserBehaviour.NONE || motionEvent.getRawY() + imageViewAudio.getWidth() / 2 > firstY) userBehaviour = UserBehaviour.CANCELING;
                            if (userBehaviour == UserBehaviour.CANCELING) translateX(-(firstX - motionEvent.getRawX()));
                        }
                        else if (direction == UserBehaviour.LOCKING) {
                            Log.e("***onTouch***","UserBehaviour.LOCKING");
                            if (userBehaviour == UserBehaviour.NONE || motionEvent.getRawX() + imageViewAudio.getWidth() / 2 > firstX) userBehaviour = UserBehaviour.LOCKING;
                            if (userBehaviour == UserBehaviour.LOCKING) translateY(-(firstY - motionEvent.getRawY()));
                        }
                        lastX = motionEvent.getRawX();
                        lastY = motionEvent.getRawY();
                    }
                    view.onTouchEvent(motionEvent);
                }
                else Utils.ShowToastAnimated(getActivity(),"Permisos de audio y almacenamiento requeridos",R.raw.swipe_disabled);
                return true;
            }
        });
        imageViewStop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(contenedor_input_audio_play.getVisibility()==View.VISIBLE){
                    contenedor_input_audio_play.setVisibility(View.GONE);
                    layoutDelete.setVisibility(View.GONE);
                    imageViewStop.setVisibility(View.GONE);
                    imageViewAudio.setVisibility(View.VISIBLE);
                    layoutStop.setVisibility(View.GONE);

                    ViewUtil.fadeIn(emojiButton,200);
                    ViewUtil.fadeIn(text_send,200);
                    ViewUtil.fadeIn(quickAttachmentToggle,200);
                    emojiButton.setEnabled(true);
                    text_send.setEnabled(true);
                    quickAttachmentToggle.setEnabled(true);

                    durationAudio=0;
                    stopTrackingAction = true;
                    firstX = 0; firstY = 0; lastX = 0;  lastY = 0;
                    userBehaviour = UserBehaviour.NONE;
                    imageViewAudio.animate().scaleX(1f).scaleY(1f).translationX(0).translationY(0).setDuration(500).setInterpolator(new LinearInterpolator()).start();
                    layoutSlideCancel.setTranslationX(0);
                    layoutSlideCancel.setVisibility(View.GONE);

                    layoutLock.setVisibility(View.GONE);
                    layoutLock.setTranslationY(0);
                    imageViewLockArrow.clearAnimation();
                    imageViewLock.clearAnimation();
                    isLocked = false;
                    enviarAudio();
                    stopRecording(RecordingBehaviour.STOP);
                }
                else {
                    isLocked = false;
                    stopRecording(RecordingBehaviour.LOCK_DONE);
                }
            }
        });
        btn_cancelar_audio.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isLocked = false;
                canceled();
                ViewUtil.fadeIn(imageViewAudio,200);
            }
        });
        layoutStop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutStop.setVisibility(View.GONE);
                btn_cancelar_audio.setVisibility(View.GONE);

                timeText.clearAnimation();
                timeText.setVisibility(View.INVISIBLE);
                imageViewMic.setVisibility(View.INVISIBLE);
                layoutEffect2.setVisibility(View.GONE);
                layoutEffect1.setVisibility(View.GONE);
                timerTask.cancel();

                emojiButton.setVisibility(View.GONE);
                text_send.setVisibility(View.GONE);
                quickAttachmentToggle.setVisibility(View.GONE);
                durationAudio=0;

                isLocked = false;
                ViewUtil.fadeIn(layoutDelete,200);
                stopRec(false);
            }
        });
        layoutDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutDelete.setVisibility(View.GONE);
                imageViewStop.setVisibility(View.GONE);
                ViewUtil.fadeIn(imageViewAudio,200);

                ViewUtil.fadeIn(emojiButton,200);
                ViewUtil.fadeIn(text_send,200);
                ViewUtil.fadeIn(quickAttachmentToggle,200);
                emojiButton.setEnabled(true);
                text_send.setEnabled(true);
                quickAttachmentToggle.setEnabled(true);

                durationAudio=0;
                stopTrackingAction = true;
                firstX = 0; firstY = 0; lastX = 0;  lastY = 0;
                userBehaviour = UserBehaviour.NONE;
                imageViewAudio.animate().scaleX(1f).scaleY(1f).translationX(0).translationY(0).setDuration(500).setInterpolator(new LinearInterpolator()).start();
                layoutSlideCancel.setTranslationX(0);
                layoutSlideCancel.setVisibility(View.GONE);

                layoutLock.setVisibility(View.GONE);
                layoutLock.setTranslationY(0);
                imageViewLockArrow.clearAnimation();
                imageViewLock.clearAnimation();

                isLocked = false;
                deleteRec();
                contenedor_input_audio_play.setVisibility(View.GONE);
            }
        });
    }
    private void translateY(float y) {
        if (y < -lockOffset) {
            locked();
            imageViewAudio.setTranslationY(0);
            return;
        }

        if (layoutLock.getVisibility() != View.VISIBLE) {
            layoutLock.setVisibility(View.VISIBLE);
        }

        imageViewAudio.setTranslationY(y);
        layoutLock.setTranslationY(y / 2);
        imageViewAudio.setTranslationX(0);
    }
    private void translateX(float x) {
        if (isLayoutDirectionRightToLeft ? x > cancelOffset : x < -cancelOffset) {
            canceled();
            imageViewAudio.setTranslationX(0);
            layoutSlideCancel.setTranslationX(0);
            return;
        }

        imageViewAudio.setTranslationX(x);
        layoutSlideCancel.setTranslationX(x);
        layoutLock.setTranslationY(0);
        imageViewAudio.setTranslationY(0);
        if (Math.abs(x) < imageViewMic.getWidth() / 2) {
            if (layoutLock.getVisibility() != View.VISIBLE) {
                layoutLock.setVisibility(View.VISIBLE);
            }
        } else {
            if (layoutLock.getVisibility() != View.GONE) {
                layoutLock.setVisibility(View.GONE);
            }
        }
    }
    private void locked() {
        Log.e("***onTouch***","LOCKED");
        stopTrackingAction = true;
        stopRecording(RecordingBehaviour.LOCKED);
        isLocked = true;
    }
    private void canceled() {
        Log.e("***onTouch***","void CANCELED");
        stopTrackingAction = true;
        stopRecording(RecordingBehaviour.CANCELED);
    }
    private void stopRecording(RecordingBehaviour recordingBehaviour){
        Log.e("***onTouch***","STOP_RECORDING");
        stopTrackingAction = true;
        firstX = 0;
        firstY = 0;
        lastX = 0;
        lastY = 0;
        userBehaviour = UserBehaviour.NONE;
        imageViewAudio.animate().scaleX(1f).scaleY(1f).translationX(0).translationY(0).setDuration(500).setInterpolator(new LinearInterpolator()).start();
        layoutSlideCancel.setTranslationX(0);
        layoutSlideCancel.setVisibility(View.GONE);

        layoutLock.setVisibility(View.GONE);
        layoutLock.setTranslationY(0);
        imageViewLockArrow.clearAnimation();
        imageViewLock.clearAnimation();
        if (isLocked) return;

        if (recordingBehaviour == RecordingBehaviour.LOCKED) {
            Log.e("***onTouch***","UserBehaviour.LOCKING");
            imageViewStop.setVisibility(View.VISIBLE);
            imageViewAudio.setVisibility(View.GONE);
            btn_cancelar_audio.setVisibility(View.VISIBLE);
            layoutStop.setVisibility(View.VISIBLE);
            //if (recordingListener != null) recordingListener.onRecordingLocked();
        }
        else if (recordingBehaviour == RecordingBehaviour.CANCELED) {
            Log.e("***onTouch***","RecordingBehaviour.CANCELED");
            deleteRec();

            btn_cancelar_audio.setVisibility(View.GONE);
            layoutStop.setVisibility(View.GONE);
            layoutDelete.setVisibility(View.GONE);

            timeText.clearAnimation();
            timeText.setVisibility(View.INVISIBLE);
            imageViewMic.setVisibility(View.INVISIBLE);
            imageViewStop.setVisibility(View.GONE);
            layoutEffect2.setVisibility(View.GONE);
            layoutEffect1.setVisibility(View.GONE);

            durationAudio=0;
            timerTask.cancel();
            delete();
        }

        else if (recordingBehaviour == RecordingBehaviour.RELEASED || recordingBehaviour == RecordingBehaviour.LOCK_DONE){
            Log.e("***onTouch***","RecordingBehaviour.RELEASED || LOCK DONE");
            btn_cancelar_audio.setVisibility(View.GONE);
            layoutStop.setVisibility(View.GONE);
            layoutDelete.setVisibility(View.GONE);

            if(imageViewAudio.getVisibility()==View.GONE) imageViewAudio.setVisibility(View.VISIBLE);

           //if(emojiButton.getVisibility()==View.INVISIBLE)
            ViewUtil.fadeIn(emojiButton,200);
            ViewUtil.fadeIn(text_send,200);
            ViewUtil.fadeIn(quickAttachmentToggle,200);

            emojiButton.setEnabled(true);
            text_send.setEnabled(true);
            quickAttachmentToggle.setEnabled(true);

            timeText.clearAnimation();
            timeText.setVisibility(View.INVISIBLE);
            imageViewMic.setVisibility(View.INVISIBLE);
            imageViewStop.setVisibility(View.GONE);
            layoutEffect2.setVisibility(View.GONE);
            layoutEffect1.setVisibility(View.GONE);

            timerTask.cancel();

            if(durationAudio<1200){
                if(!tostada){
                    tostada=true;
                    Utils.reproducirSonido(R.raw.rec_error, context);
                    Utils.ShowToastAnimated(getActivity(),"Toca y mantén presionado para grabar",R.raw.voip_unmuted);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tostada=false;
                        }
                    },5000);
                }
                deleteRec();
            }
            else {
                stopRec(true);
            }
            durationAudio=0;
        }
    }
    private void startRecord() {
        fab_bottom_chat.hide();
        Log.e("***onTouch***","STAR_RECORD");

        stopTrackingAction = false;
        imageViewAudio.animate().scaleXBy(0.8f).scaleYBy(0.8f).setDuration(1000).setInterpolator(new OvershootInterpolator()).start();
        timeText.setVisibility(View.VISIBLE);
        layoutLock.setVisibility(View.VISIBLE);

        layoutSlideCancel.setVisibility(View.VISIBLE);
        imageViewMic.setVisibility(View.VISIBLE);
        layoutEffect2.setVisibility(View.VISIBLE);
        layoutEffect1.setVisibility(View.VISIBLE);

        emojiButton.setVisibility(View.INVISIBLE);
        text_send.setVisibility(View.INVISIBLE);
        quickAttachmentToggle.setVisibility(View.INVISIBLE);

        emojiButton.setEnabled(false);
        text_send.setEnabled(false);
        quickAttachmentToggle.setEnabled(false);

        timeText.startAnimation(animBlink);
        imageViewLockArrow.clearAnimation();
        imageViewLock.clearAnimation();
        imageViewLockArrow.startAnimation(animJumpFast);
        imageViewLock.startAnimation(animJump);

        if (audioTimer == null) {
            audioTimer = new Timer();
            timeFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        }

        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        durationAudio+=audioTotalTime+500;
                        timeText.setText(timeFormatter.format(new Date(audioTotalTime * 1000)));
                        audioTotalTime++;
                    }
                });
            }
        };

        audioTotalTime = 0;
        audioTimer.schedule(timerTask, 0, 1000);
        reproducirSonidoParaGrabar(R.raw.grabar);
    }
    private void delete() {
        Log.e("***onTouch***","void DELETED");
        imageViewMic.setVisibility(View.VISIBLE);
        imageViewMic.setRotation(0);
        isDeleting = true;
        imageViewAudio.setEnabled(false);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //stopTrackingAction=false;
                isDeleting = false;
                imageViewAudio.setEnabled(true);
            }
        }, 1500);

        imageViewMic.animate().translationY(-dp * 150).rotation(180).scaleXBy(0.6f).scaleYBy(0.6f).setDuration(600).setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                float displacement = 0;
                if (isLayoutDirectionRightToLeft) {
                    displacement = dp * 40;
                } else {
                    displacement = -dp * 40;
                }
                dustin.setTranslationX(displacement);
                dustin_cover.setTranslationX(displacement);
                dustin_cover.animate().translationX(0).rotation(-120).setDuration(400).setInterpolator(new DecelerateInterpolator()).start();
                dustin.animate().translationX(0).setDuration(400).setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        dustin.setVisibility(View.VISIBLE);
                        dustin_cover.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).start();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
//                text_send.setVisibility(View.VISIBLE);
//                quickAttachmentToggle.setVisibility(View.VISIBLE);

                ViewUtil.fadeIn(text_send,200);
                ViewUtil.fadeIn(quickAttachmentToggle,200);

                text_send.setEnabled(false);
                quickAttachmentToggle.setEnabled(false);

                imageViewMic.animate().translationY(0).scaleX(1).scaleY(1).setDuration(400).setInterpolator(new LinearInterpolator()).setListener(
                        new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                imageViewMic.setVisibility(View.INVISIBLE);
                                imageViewMic.setRotation(0);

                                float displacement = 0;

                                if (isLayoutDirectionRightToLeft) {
                                    displacement = dp * 40;
                                } else {
                                    displacement = -dp * 40;
                                }

                                dustin_cover.animate().rotation(0).setDuration(250).setStartDelay(150).start();
                                dustin.animate().translationX(displacement).setDuration(300).setStartDelay(350).setInterpolator(new DecelerateInterpolator()).start();
                                dustin_cover.animate().translationX(displacement).setDuration(300).setStartDelay(350).setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        /*editTextMessage.setVisibility(View.VISIBLE);
                                        editTextMessage.requestFocus();*/
                                        //viewInput.setVisibility(View.VISIBLE);

                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
//                                                emojiButton.setVisibility(View.VISIBLE);
                                                ViewUtil.fadeIn(emojiButton,200);

                                                emojiButton.setEnabled(true);
                                                text_send.setEnabled(true);
                                                quickAttachmentToggle.setEnabled(true);
                                            }
                                        },400);
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {

                                    }
                                }).start();
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        }
                ).start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }

    private synchronized void initComponents(View view) {
        option = null;
        bitmap = null;
        estaActivoBuscar = false;
        limite = tamArrAnt = 200;
        idMsgEstadoCambiar = "";
        envioCorrectoMensaje = false;
        miPathCamera = "";
        icono_eliminar_answer = view.findViewById(R.id.icono_eliminar_answer);
        icono_eliminar_answer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_answer.setVisibility(View.GONE);
                img_answer_chat.setVisibility(View.GONE);

            }
        });

        borrarTodosMsg = view.findViewById(R.id.borrarTodosMsg);
        borrarTodosMsg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarMenu();
                if(estaActivoBuscar || ll_buscar.getVisibility()==View.VISIBLE) cancelarBuscar();
                if(estaModoSeleccionar()) cancelarModoSeleccionar();
                Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(1);
                View mview=getLayoutInflater().inflate(R.layout.dialog_confirm,null);
                dialog.setContentView(mview);
                View btn_cancel=mview.findViewById(R.id.btn_cancel);
                View btn_ok=mview.findViewById(R.id.btn_ok);
                btn_ok.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(adaptadorChat.estaPlayAudio()) adaptadorChat.detenerPlayAudio();
                        dialog.dismiss();
                        vaciarChat();
                        tamArrAnt=limite;
                        datos_chat.clear();
                        adaptadorChat.notifyDataSetChanged();
                        actualizarRecyclerChat(false);
                        fab_bottom_chat.hide();
                    }
                });
                btn_cancel.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                dialog.setCancelable(true);
                dialog.show();
            }
        });

        abrirGaleriaChat = view.findViewById(R.id.abrirGaleriaChat);
        abrirGaleriaChat.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarMenu();
                if(imagenesChatAlbumFile.size()>0){
                    YouChatApplication.imagenesChatAlbumFile=imagenesChatAlbumFile;
                    if(mAddFragmentListener!=null) mAddFragmentListener.onAddFragment(ChatsActivityCorreo.this, new ImageActivity());
//                    navController.navigate(R.id.imageActivity);
                }
                else Utils.ShowToastAnimated(mainActivity,"No hay imágenes en este chat",R.raw.attach_gallery);
            }
        });

        buscarChat = view.findViewById(R.id.buscarChat);
        buscarChat.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarMenu();
                if(YouChatApplication.animaciones_chat){
                    Utils.runOnUIThread(()->{
                        anim=AnimationUtils.loadAnimation(context,R.anim.show_layout_filtro);
                        ll_audio.setVisibility(View.VISIBLE);
                        ll_audio.startAnimation(anim);
                        ll_imagen.setVisibility(View.VISIBLE);
                        ll_imagen.startAnimation(anim);
                    });
                }
                else {
                    ll_audio.setVisibility(View.VISIBLE);
                    ll_imagen.setVisibility(View.VISIBLE);
                }
                Buscar_Chat("");
            }
        });

        abrirMenu = view.findViewById(R.id.abrirMenu);
        abrirMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                anim=AnimationUtils.loadAnimation(context,R.anim.show_menu);
                menu_chat.setVisibility(View.VISIBLE);
                menu_chat.startAnimation(anim);
                InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(text_send.getWindowToken(), 0);
            }
        });

        ir_atras = view.findViewById(R.id.ir_atras);
        ir_atras.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        chat_fondo = view.findViewById(R.id.chat_fondo);
        progressBar = view.findViewById(R.id.progressBar);
        mini_img_perfil = view.findViewById(R.id.mini_img_perfil);

        chat_usuario = view.findViewById(R.id.chat_usuario);
        chat_correo = view.findViewById(R.id.chat_correo);
        fab_bottom_chat = view.findViewById(R.id.fab_bottom_chat);
        fab_bottom_chat.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.runOnUIThread(()->{
                    int med=(linearLayoutManager.findLastVisibleItemPosition()+linearLayoutManager.findFirstVisibleItemPosition())/2;
                    if(med<30 && YouChatApplication.animaciones_chat) lista_chat.smoothScrollToPosition(0);
                    else lista_chat.scrollToPosition(0);
                    fab_bottom_chat.hide();
                });
            }
        });

        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        contenedor_input_audio_play = view.findViewById(R.id.contenedor_input_audio_play);

        play_audio_rec = view.findViewById(R.id.play_audio_rec);
        play_audio_rec.setCircleBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_btn()));
        play_audio_rec.setBorderColor(Color.parseColor(YouChatApplication.itemTemas.getColor_btn()));

        seekbar_audio_rec = view.findViewById(R.id.seekbar_audio_rec);

        duration_audio_rec = view.findViewById(R.id.duration_audio_rec);

        text_send = view.findViewById(R.id.input_texts);
        text_send.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getFont_barchat()));
        text_send.setHintTextColor(Color.parseColor(YouChatApplication.itemTemas.getFont_barchat_oscuro()));
        ///TODO HACER
        if(YouChatApplication.enviarEnter){
            text_send.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                    Log.e("onEditorAction","TEXT: "+v.getText().toString()+" /actionId: "+actionId+" /EVENT: "+event.toString());
//                if(actionId == EditorInfo.IME_ACTION_DONE) {
                if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER && !v.getText().toString().isEmpty()) {
                    enviarMensaje();
                }
                    return true;
                }
            });
        }


        ll_buscar = view.findViewById(R.id.layout_buscar);
        ll_contacto = view.findViewById(R.id.layout_contacto);
        btn_cancelar_buscar = view.findViewById(R.id.cancel_buscar_chat);
        btn_cancelar_buscar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelarBuscar();
                final InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(et_buscar_chat.getWindowToken(), 0);
            }
        });

        et_buscar_chat = view.findViewById(R.id.buscar_chat);
        ll_answer = view.findViewById(R.id.contenedor_input_answer);
        nombre_answer = view.findViewById(R.id.mensaje_respuesta_nombre);
        texto_answer = view.findViewById(R.id.mensaje_respuesta_texto);
        img_answer_chat = view.findViewById(R.id.img_answer_chat);
        emojiButton = view.findViewById(R.id.input_emoji);
        rootView = view.findViewById(R.id.root_view);
        lista_chat = view.findViewById(R.id.contenedor_chats);
        fab_bottom_chat_cant_msg_nvisto = view.findViewById(R.id.fab_bottom_chat_cant_msg_nvisto);
        item_silenciar = view.findViewById(R.id.item_silenciar);
        item_bloquear = view.findViewById(R.id.item_bloquear);
        tv_item_silenciar = view.findViewById(R.id.tv_item_silenciar);
        tv_item_bloquear = view.findViewById(R.id.tv_item_bloquear);
        option_agregar_contacto = view.findViewById(R.id.option_agregar_contacto);
        menu_chat = view.findViewById(R.id.menu_chat);
        area_menu_chat = view.findViewById(R.id.area_menu_chat);

        //modo selececconar
        ll_modo_seleccionar_chat = view.findViewById(R.id.ll_modo_seleccionar_chat);
        iv_cancelar_selec_chat = view.findViewById(R.id.iv_cancelar_selec_chat);
        iv_reintentar_selec_chat = view.findViewById(R.id.iv_reintentar_selec_chat);
        iv_copiar_selec_chat = view.findViewById(R.id.iv_copiar_selec_chat);
        iv_responder_selec_chat = view.findViewById(R.id.iv_responder_selec_chat);
        iv_reenviar_selec_chat = view.findViewById(R.id.iv_reenviar_selec_chat);
        iv_borrar_selec_chat = view.findViewById(R.id.iv_borrar_selec_chat);
        tv_cant_selec_chat = view.findViewById(R.id.tv_cant_selec_chat);
        inicializarComponentesDeModoSeleccionar();

        fab_bottom_chat_cant_msg_nvisto.getBackground().getCurrent().setColorFilter(new PorterDuffColorFilter(Color.parseColor(YouChatApplication.itemTemas.getColor_btn()), PorterDuff.Mode.SRC_IN));
        area_menu_chat.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (menu_chat.getVisibility() == View.VISIBLE) cerrarMenu();
                return true;
            }
        });

        option_agregar_contacto.setVisibility(View.GONE);

        et_buscar_chat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String espacio = s.toString();
                if (espacio.replace(" ", "").length() > 0) {
                    estaActivoBuscar = true;
                    Buscar(espacio);
                } else {
                    if (espacio.length() > 0) et_buscar_chat.setText("");
                    estaActivoBuscar = false;
                    adaptadorChat = new AdaptadorDatosChatCorreo(context, datos_chat, ChatsActivityCorreo.this);
                    lista_chat.setAdapter(adaptadorChat);
                    ponerStickyHeadersDecoration();
                    activarFuncionesAdaptador();
                    lista_chat.scrollToPosition(0);
                }
            }
        });
        setUpEmojiPopup();
        emojiButton.setOnClickListener(new OnClickListenerEmoji());
        text_send.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public synchronized void afterTextChanged(Editable s) {
                Utils.runOnUIThread(()->{
                    String espacio = s.toString();
                    if (espacio.replace(" ", "").length() > 0) {
                        updateToggleButtonState(true);
                    } else {
                        updateToggleButtonState(false);
                        if (!espacio.equals("")) text_send.setText("");
                    }
                });
            }
        });

        cargarPreferencias();
        if (mibundle != null) {
            apodo = mibundle.getString("usuario", "");
            correo = mibundle.getString("correo", "");
            esGrupo = correo.contains(",");
            groupId=dbWorker.obtenerIdGroupUsuarioCorreo(correo);
            ItemContacto contacto = dbWorker.obtenerContacto(correo);
            if (contacto != null) {
                if (apodo.equals("") || apodo.equals(correo))
                    apodo = contacto.getNombreMostrar();
                ruta_img_perfil = contacto.getRuta_img();
                telefono = contacto.getTelefono();
                estaSilenciado = contacto.isSilenciado();
                estaBloqueado = contacto.isBloqueado();
                tipo_contacto = contacto.getTipo_contacto();
            } else {
                contacto = new ItemContacto(correo, correo);
                contacto.setTipo_contacto(ItemContacto.TIPO_CONTACTO_INVISIBLE);
                contacto.setUsaYouchat(false);
                dbWorker.insertarNuevoContactoNoVisible(contacto, false);
                if (apodo.equals(""))
                    apodo = correo;
                ruta_img_perfil = "";
                telefono = "";
                estaSilenciado = false;
                estaBloqueado = false;
                tipo_contacto = ItemContacto.TIPO_CONTACTO_INVISIBLE;
                option_agregar_contacto.setVisibility(View.VISIBLE);
            }

            if (tipo_contacto == ItemContacto.TIPO_CONTACTO_INVISIBLE) {
                ItemContacto contactoFinal = contacto;
                option_agregar_contacto.setVisibility(View.VISIBLE);
                option_agregar_contacto.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Dialog dialog = new Dialog(context);
                        dialog.requestWindowFeature(1);
                        View mview = getLayoutInflater().inflate(R.layout.dialog_confirm, null);
                        dialog.setContentView(mview);

                        LinearLayout header = mview.findViewById(R.id.header);
                        ImageView icono_eliminar = mview.findViewById(R.id.icono_eliminar);
                        TextView text_icono = mview.findViewById(R.id.text_icono);
                        TextView text_eliminar = mview.findViewById(R.id.text_eliminar);
                        TextView btn_ok = mview.findViewById(R.id.btn_ok);
                        View btn_cancel = mview.findViewById(R.id.btn_cancel);

                        header.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_accento()));
                        icono_eliminar.setImageResource(R.drawable.add_user);
                        text_icono.setText("Añadir contacto");
                        text_eliminar.setText("¿Quieres agregar a " + apodo
                                + " a tu lista de contactos?");
                        btn_ok.setText("Agregar");

                        btn_ok.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                adicionarContactoAlTelefono(contactoFinal);
                            }
                        });
                        btn_cancel.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                        dialog.setCancelable(true);
                        dialog.show();
                    }
                });
            }
            cant_Msg_No_Vistos = dbWorker.obtenerCantMensajeCorreoNoVistoDe(correo);
            dbWorker.marcarComoVistoCorreosNoVistosDe(correo);

            chat_usuario.setText(apodo);
            chat_correo.setText(correo);
            if (estaSilenciado) {
                tv_item_silenciar.setText(R.string.accion_silenciado);
                boolean modoNoche = YouChatApplication.temaApp == 1;
                if (modoNoche) tv_item_silenciar.setTextColor(0xFFA22B28);
                else tv_item_silenciar.setTextColor(0xFFE53935);
//                tv_item_silenciar.setTextColor(Color.RED);
            } else {
                tv_item_silenciar.setText(R.string.accion_silenciar);
                tv_item_silenciar.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getColor_texto()));
            }

            if (estaBloqueado) {
                tv_item_bloquear.setText(R.string.accion_bloqueado);
                boolean modoNoche = YouChatApplication.temaApp == 1;
                if (modoNoche) tv_item_bloquear.setTextColor(0xFFA22B28);
                else tv_item_bloquear.setTextColor(0xFFE53935);
            } else {
                tv_item_bloquear.setText(R.string.accion_bloquear);
                tv_item_bloquear.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getColor_texto()));
            }

            item_silenciar.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    cerrarMenu();
                    Dialog dialog = new Dialog(context);
                    dialog.requestWindowFeature(1);
                    View mview = getLayoutInflater().inflate(R.layout.dialog_confirm, null);
                    dialog.setContentView(mview);

                    LinearLayout header = mview.findViewById(R.id.header);
                    ImageView icono_eliminar = mview.findViewById(R.id.icono_eliminar);
                    TextView text_icono = mview.findViewById(R.id.text_icono);
                    TextView text_eliminar = mview.findViewById(R.id.text_eliminar);
                    TextView btn_ok = mview.findViewById(R.id.btn_ok);
                    View btn_cancel = mview.findViewById(R.id.btn_cancel);

                    header.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_accento()));
                    //icono_eliminar.setImageResource(R.drawable.volume_on);

                    if (estaSilenciado) {
                        icono_eliminar.setImageResource(R.drawable.volume_up);
                        text_icono.setText("Activar notificaciones");
                        text_eliminar.setText("¿Quieres activar las notificaciones de " + apodo + "?");
                    } else {
                        icono_eliminar.setImageResource(R.drawable.volume_off);
                        text_icono.setText("Desactivar notificaciones");
                        text_eliminar.setText("¿Quieres silenciar a " + apodo + "? " + "No se notificarán sus mensajes");
                    }

                    btn_ok.setText("Aceptar");

                    btn_ok.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            estaSilenciado = !estaSilenciado;
                            if (estaSilenciado) {
                                tv_item_silenciar.setText(R.string.accion_silenciado);
                                boolean modoNoche = YouChatApplication.temaApp == 1;
                                if (modoNoche) tv_item_silenciar.setTextColor(0xFFA22B28);
                                else tv_item_silenciar.setTextColor(0xFFE53935);
                                Utils.ShowToastAnimated(mainActivity, "Notificaciones silenciadas", R.raw.ic_mute);
//                                tv_item_silenciar.setTextColor(Color.RED);
                            } else {
                                tv_item_silenciar.setText(R.string.accion_silenciar);
                                tv_item_silenciar.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getColor_texto()));
                                Utils.ShowToastAnimated(mainActivity, "Notificaciones activadas", R.raw.ic_unmute);
                            }
                            dbWorker.actualizarSilenciadoDe(correo, estaSilenciado);
                        }
                    });
                    btn_cancel.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                    dialog.setCancelable(true);
                    dialog.show();
                }
            });

            item_bloquear.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    cerrarMenu();
                    Dialog dialog = new Dialog(context);
                    dialog.requestWindowFeature(1);
                    View mview = getLayoutInflater().inflate(R.layout.dialog_confirm, null);
                    dialog.setContentView(mview);

                    LinearLayout header = mview.findViewById(R.id.header);
                    ImageView icono_eliminar = mview.findViewById(R.id.icono_eliminar);
                    TextView text_icono = mview.findViewById(R.id.text_icono);
                    TextView text_eliminar = mview.findViewById(R.id.text_eliminar);
                    TextView btn_ok = mview.findViewById(R.id.btn_ok);
                    View btn_cancel = mview.findViewById(R.id.btn_cancel);

                    header.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_accento()));
                    icono_eliminar.setImageResource(R.drawable.option_bloquear);

                    if (estaBloqueado) {
                        text_icono.setText("Desbloquear usuario");
                        text_eliminar.setText("¿Quieres desbloquear a " + apodo + "?");
                    } else {
                        text_icono.setText("Bloquear usuario");
                        text_eliminar.setText("¿Quieres bloquear a " + apodo + "? " + "No recibirás ninguno de sus mensaje");
                    }
                    btn_ok.setText("Aceptar");

                    btn_ok.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            estaBloqueado = !estaBloqueado;
                            if (estaBloqueado) {
                                tv_item_bloquear.setText(R.string.accion_bloqueado);
                                boolean modoNoche = YouChatApplication.temaApp == 1;
                                if (modoNoche) tv_item_bloquear.setTextColor(0xFFA22B28);
                                else tv_item_bloquear.setTextColor(0xFFE53935);
                                Utils.ShowToastAnimated(mainActivity, "Chat bloqueado", R.raw.passcode_lock_close);
//                                tv_item_bloquear.setTextColor(Color.RED);
                            } else {
                                tv_item_bloquear.setText(R.string.accion_bloquear);
                                tv_item_bloquear.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getColor_texto()));
                                Utils.ShowToastAnimated(mainActivity, "Chat desbloqueado", R.raw.passcode_lock_open);
                            }
                            dbWorker.actualizarBloqueadoDe(correo, estaBloqueado);
                        }
                    });
                    btn_cancel.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                    dialog.setCancelable(true);
                    dialog.show();
                }
            });

            if(correo.equals(YouChatApplication.idOficial)){
                YouChatApplication.ponerIconOficial(mini_img_perfil);
            }
            else {
                Glide.with(this).load(ruta_img_perfil).error(R.drawable.profile_white).into(mini_img_perfil);
            }

            if (YouChatApplication.chatsActivityCorreo != null)
                getActivity().onBackPressed();
            YouChatApplication.chatsActivityCorreo = this;
        } else getActivity().onBackPressed();

        ll_audio = view.findViewById(R.id.ll_audio);
        ll_audio.setCardBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_barra()));
        ll_audio.setRadius(YouChatApplication.curvaChat<=32?YouChatApplication.curvaChat : 32);
        ll_audio.setStrokeColor(Color.parseColor(YouChatApplication.itemTemas.getColor_barra()));

        ll_audio.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!search_audio) {
                    search_audio=true;
                    if(search_imagen){
                        search_imagen=false;
                        ll_imagen.setStrokeColor(Color.parseColor(YouChatApplication.itemTemas.getColor_barra()));
                    }
                    ll_audio.setStrokeColor(Color.parseColor(YouChatApplication.itemTemas.getFont_barra()));
                    Buscar_Chat(et_buscar_chat.getText().toString());
                    //filtro: audio
                }
                else {
                    search_audio=false;
                    search_mark=true;
                    ll_audio.setStrokeColor(Color.parseColor(YouChatApplication.itemTemas.getColor_barra()));
                    Buscar_Chat(et_buscar_chat.getText().toString());
                }
            }
        });

        ll_imagen = view.findViewById(R.id.ll_imagen);
        ll_imagen.setCardBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_barra()));
        ll_imagen.setRadius(YouChatApplication.curvaChat<=32?YouChatApplication.curvaChat : 32);
        ll_imagen.setStrokeColor(Color.parseColor(YouChatApplication.itemTemas.getColor_barra()));

        ll_imagen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!search_imagen) {
                    search_imagen=true;
                    if(search_audio) {
                        search_audio=false;
                        ll_audio.setStrokeColor(Color.parseColor(YouChatApplication.itemTemas.getColor_barra()));
                    }
                    ll_imagen.setStrokeColor(Color.parseColor(YouChatApplication.itemTemas.getFont_barra()));
                    Buscar_Chat(et_buscar_chat.getText().toString());
                    //filtro: imagen
                }
                else {
                    search_imagen=false;
                    search_mark=true;
                    ll_imagen.setStrokeColor(Color.parseColor(YouChatApplication.itemTemas.getColor_barra()));
                    Buscar_Chat(et_buscar_chat.getText().toString());
                }
            }
        });

        linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, true);
        lista_chat.setLayoutManager(linearLayoutManager);
        lista_chat.setHasFixedSize(false);

        datos_chat = new ArrayList<>();
        adaptadorChat = new AdaptadorDatosChatCorreo(context, datos_chat, ChatsActivityCorreo.this);
        lista_chat.setAdapter(adaptadorChat);
        ponerStickyHeadersDecoration();
        actualizarRecyclerChat(false);
        if (cant_Msg_No_Vistos > 3) lista_chat.scrollToPosition(cant_Msg_No_Vistos);
        else lista_chat.scrollToPosition(0);
        fab_bottom_chat.hide();
        if(YouChatApplication.invertirResChat){
            new ItemTouchHelper(new SwipeControllerCorreoInverso(this)).attachToRecyclerView(lista_chat);
        }
        else {
            new ItemTouchHelper(new SwipeControllerCorreo(this)).attachToRecyclerView(lista_chat);
        }

        lista_chat.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //- arriba    + abajo
                accionesListenerScrollRecycler(recyclerView);
            }
        });
        //send.setOnLongClickListener(onLongClickListenerGrabar);
    }

    public class OnClickListenerEmoji implements OnClickListener{
        @Override
        public void onClick(View view) {
            Log.e("CHATACTIVIT----EMOJI","Click");
            emojiPopup.toggle();
        }
    }

    public synchronized void accionesListenerScrollRecycler(@NonNull RecyclerView recyclerView){
        boolean esInicioOFinal=false;

        if(linearLayoutManager.findFirstVisibleItemPosition()==0){
            esInicioOFinal=true;
            //llego al inicio abajo
            fab_bottom_chat.hide();
            if(cant_Msg_No_Vistos>0){
                Utils.runOnUIThread(()->{
                    vistoAll();
                },3000);
            }
        }
        else if(datos_chat.size()>3) fab_bottom_chat.show();
        if(linearLayoutManager.findLastCompletelyVisibleItemPosition()==datos_chat.size()-1){
            esInicioOFinal=true;
            //llego al fin arriba
            if(datos_chat.size()>=tamArrAnt){
                if(cant_Msg_No_Vistos>0){
                    vistoAll();
                }
                final int temp=datos_chat.size()-1;
                tamArrAnt=datos_chat.size()+limite;
                progressBar.setVisibility(View.VISIBLE);
                lista_chat.stopScroll();
                Utils.runOnUIThread(()->{
                    progressBar.setVisibility(View.GONE);
                    actualizarRecyclerChat(false);
                    lista_chat.scrollToPosition(temp);
                },2000);
            }
        }

        if(!estaActivoBuscar){
            if(cant_Msg_No_Vistos==0 && fab_bottom_chat_cant_msg_nvisto.getVisibility()==View.VISIBLE){
                if(YouChatApplication.animaciones_chat){
                    Animation anim= AnimationUtils.loadAnimation(context,R.anim.fade_out_fast);
                    fab_bottom_chat_cant_msg_nvisto.startAnimation(anim);
                }
                fab_bottom_chat_cant_msg_nvisto.setVisibility(View.GONE);
            }
            else if(cant_Msg_No_Vistos>0){
                int cant=cant_Msg_No_Vistos;
                if(cant>99) fab_bottom_chat_cant_msg_nvisto.setText("99");
                else fab_bottom_chat_cant_msg_nvisto.setText(""+cant);

                if(linearLayoutManager.findFirstVisibleItemPosition()>=cant_Msg_No_Vistos
                        && fab_bottom_chat_cant_msg_nvisto.getVisibility()==View.GONE){
                    if(YouChatApplication.animaciones_chat){
                        Animation anim= AnimationUtils.loadAnimation(context,R.anim.fade_in_fast);
                        fab_bottom_chat_cant_msg_nvisto.startAnimation(anim);
                    }
                    fab_bottom_chat_cant_msg_nvisto.setVisibility(View.VISIBLE);
                }
                else if(linearLayoutManager.findFirstVisibleItemPosition()<cant_Msg_No_Vistos
                        && fab_bottom_chat_cant_msg_nvisto.getVisibility()==View.VISIBLE){
                    if(YouChatApplication.animaciones_chat){
                        Animation anim= AnimationUtils.loadAnimation(context,R.anim.fade_out_fast);
                        fab_bottom_chat_cant_msg_nvisto.startAnimation(anim);
                    }
                    fab_bottom_chat_cant_msg_nvisto.setVisibility(View.GONE);
                }
            }
        }
        else {
            if(fab_bottom_chat_cant_msg_nvisto.getVisibility()==View.VISIBLE)
                fab_bottom_chat_cant_msg_nvisto.setVisibility(View.GONE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return attachToSwipeBack(inflater.inflate(R.layout.activity_chats, container, false));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cameraPhoto = new CameraPhoto(context);

        initializeViews(view);

        Utils.runOnUIThread(()->{
            initComponents(view);
            initViewAudio(view);
        });

        getSwipeBackLayout().addSwipeListener(new SwipeBackLayout.OnSwipeListener() {
            @Override
            public void onDragStateChange(int state) {
                if(state==1)
                {
                    Utils.ocultarKeyBoard(mainActivity);
                }
            }
            @Override
            public void onEdgeTouch(int oritentationEdgeFlag){}
            @Override
            public void onDragScrolled(float scrollPercent){}
        });
    }

    private void ocultarComponentes(View viewGeneral) {
        viewGeneral.findViewById(R.id.input_chat).setVisibility(View.INVISIBLE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        quickAttachmentDrawer.onPause();
    }

    @Override
    public void onResume() {
        quickAttachmentDrawer.onResume();
        if(estaMinimizado){
            estaMinimizado = false;
            if (YouChatApplication.estaAndandoChatService())
                YouChatApplication.chatService.eliminarNotiCorreo();
        }
        super.onResume();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        quickAttachmentDrawer.onConfigurationChanged();
        chat_fondo.setVisibility(View.GONE);
        Utils.cargarFondo(context,chat_fondo);
        chat_fondo.setVisibility(View.VISIBLE);
    }

    private synchronized void vistoAll(){
        cant_Msg_No_Vistos=0;
        dbWorker.marcarComoVistoCorreosNoVistosDe(correo);
        eliminarCantMsgNoLeidos();
    }

    private synchronized void eliminarCantMsgNoLeidos() {
        int l=datos_chat.size()-1;
        for(int i=l; i>=0; i--)
            if(datos_chat.get(i).getTipo_mensaje()==99){
                datos_chat.remove(i);
                if(!estaActivoBuscar) adaptadorChat.notifyItemRemoved(i);
                return;
            }
    }

    public void startRec()
    {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        File path = new File(YouChatApplication.RUTA_AUDIOS_ENVIADOS);
        boolean isCreada=path.exists();
        if(!isCreada)
        {
            isCreada=path.mkdirs();
        }
        if(isCreada){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
            Date date = new Date();
            String fechaEntera = sdf.format(date);
            nombre_audio="rec"+fechaEntera+"-";

            try {
                archivo = File.createTempFile(nombre_audio, ".ycaudio", path);
                //ruta_audio=path+"/"+nombre_audio+".wav";
//                ruta_audio=path+"/"+nombre_audio+".wav";
            } catch (IOException e) {
                e.printStackTrace();
            }
            recorder.setOutputFile(archivo.getAbsolutePath());
            try {
                recorder.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            audioFocusRequest = Utils.setAudioFocus(audioManager);
            recorder.start();
        }
        else Utils.ShowToastAnimated(mainActivity,"Ha ocurrido un error",R.raw.error);
    }
    

    public void stopRec(boolean aEnviar)
    {
        Log.e("***onTouch***","STOP REC");
        if(recorder!=null){
            try {
                recorder.stop();
                recorder.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
            if(audioFocusRequest!=null) Utils.clearAudioFocus(audioManager, audioFocusRequest);

            ruta_audio=archivo.getAbsolutePath();

            mediaPlayer_audioRec=new MediaPlayer();

            File f = new File(ruta_audio);

            if (f.canRead()){
                try {
                    mediaPlayer_audioRec.setDataSource(ruta_audio);
                    mediaPlayer_audioRec.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                long dur=mediaPlayer_audioRec.getDuration()/1000;
                long seg=dur%60;
                long minu=dur/60;

                if(seg==0 && minu==0){
                    Utils.borrarFile(archivo);
                    mediaPlayer_audioRec=null;
                    return;
                }

                else {
                    if (seg<10) audio_rec_duracion=minu+":0"+seg;
                    else audio_rec_duracion=minu+":"+seg;

                    if(aEnviar) enviarAudio();

                    else {
//                        seekbar_audio_rec.setProgress(0);
//                        seekbar_audio_rec.setValue(0);
                        //////////////////////////////////
                        byte [] bytes = new byte[(int) f.length()];
                        FileInputStream fis = null;
                        try {
                            fis = new FileInputStream(f);
                            fis.read(bytes);
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                        finally {
                            if(fis!=null) {
                                try {
                                    fis.close();
                                    seekbar_audio_rec.setRawData(bytes);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        ///////////////////////////////////

                        seekbar_audio_rec.setProgress(0);
//                        seekbar_audio_rec.setMax(mediaPlayer_audioRec.getDuration());
//                        seekbar_audio_rec.setValueTo(mediaPlayer_audioRec.getDuration());
                        duration_audio_rec.setText(audio_rec_duracion);
//                        boolean isPlay_audioRec, isPause_audioRec, isEnd_audioRec, estaTimer_audioRec;
//                        Timer timer_audioRec;

                        play_audio_rec.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(!isPlay_audioRec || isPause_audioRec){
                                    if(adaptadorChat.estaPlayAudio()) adaptadorChat.detenerPlayAudio();
                                    if(!isPlay_audioRec){
                                        play_audio_rec.setImageResource(R.drawable.audio_pause);
                                        isPlay_audioRec=true;

                                        isPause_audioRec=false;
                                        isEnd_audioRec=false;
                                        estaTimer_audioRec=false;

                                        mediaPlayer_audioRec.start();

                                        mediaPlayer_audioRec.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                            @Override
                                            public void onCompletion(MediaPlayer mp) {
                                                isEnd_audioRec=true;
                                                isPlay_audioRec=false;
                                                mediaPlayer_audioRec.pause();
                                                mediaPlayer_audioRec.seekTo(0);
                                                seekbar_audio_rec.setProgress(0);
//                                                seekbar_audio_rec.setValue(0);
                                                play_audio_rec.setImageResource(R.drawable.audio_play);
                                            }
                                        });

                                    }
                                    else if (isPause_audioRec)
                                    {
                                        play_audio_rec.setImageResource(R.drawable.audio_pause);
                                        isPause_audioRec=false;
                                        mediaPlayer_audioRec.start();
                                    }

                                    if(!isEnd_audioRec && !estaTimer_audioRec){
                                        estaTimer_audioRec=true;
                                        timer_audioRec = new Timer();
                                        TimerTask timerTask;
                                        timerTask = new TimerTask() {
                                            @Override
                                            public void run() {
                                                if(isEnd_audioRec)
                                                {
                                                    timer_audioRec.cancel();
                                                    isEnd_audioRec=false;
                                                    estaTimer_audioRec=false;
                                                }
                                                else {
                                                    int currentPosition = mediaPlayer_audioRec.getCurrentPosition();
                                                    float pos = (float)(currentPosition*100)/mediaPlayer_audioRec.getDuration();
                                                    if(pos<0) pos=0;
                                                    if(pos>100) pos=100;
                                                    seekbar_audio_rec.setProgress(pos);
//                                                    seekbar_audio_rec.setValue(currentPosition);
                                                }
                                            }};
                                        timer_audioRec.scheduleAtFixedRate(timerTask, 0, 1000);
                                    }
                                }
                                else{
                                    mediaPlayer_audioRec.pause();
                                    play_audio_rec.setImageResource(R.drawable.audio_play);
                                    isPause_audioRec=true;
                                }
                            }
                        });

                        seekbar_audio_rec.setOnProgressListener(new OnProgressListener() {
                            @Override
                            public void onStartTracking(float progress) {

                            }

                            @Override
                            public void onStopTracking(float progress) {

                            }

                            @Override
                            public void onProgressChanged(float value, boolean fromUser) {
                                if (fromUser && mediaPlayer_audioRec!=null) {
                                    mediaPlayer_audioRec.seekTo((int)value);
                                    seekbar_audio_rec.setProgress(value);
                                }
                            }
                        });

                        /*seekbar_audio_rec.addOnChangeListener(new Slider.OnChangeListener() {
                            @Override
                            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                                if (fromUser && mediaPlayer_audioRec!=null) {
                                    mediaPlayer_audioRec.seekTo((int)value);
                                    slider.setValue(value);
                                }
                            }
                        });*/
                        contenedor_input_audio_play.setVisibility(View.VISIBLE);
                    }
                }
            }
            else {
                Utils.borrarFile(archivo);
            }
        }
    }

    public void deleteRec()
    {
        stopTrackingAction=false;
        Log.e("***onTouch***","DELETE_REC");
        if(contenedor_input_audio_play.getVisibility()==View.VISIBLE){
            Utils.borrarFile(archivo);
        }
        else{
            try {
                //File f = archivo;
                Utils.borrarFile(archivo);
                recorder.stop();
                recorder.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
            Utils.vibrate(context, 50);
        }
    }

    private synchronized void setUpEmojiPopup() {
        Log.e("CHATACTIVIT----EMOJI","Popup");
        if(YouChatApplication.animaciones_chat){
            emojiPopup = EmojiPopup.Builder.fromRootView(rootView)
                    .setOnEmojiPopupShownListener(new OnEmojiPopupShownListener() {
                        @Override
                        public void onEmojiPopupShown() {
                            emojiButton.setImageResource(R.drawable.input_keyboard);
                        }
                    })
                    .setOnEmojiPopupDismissListener(new OnEmojiPopupDismissListener() {
                        @Override
                        public void onEmojiPopupDismiss() {
                            emojiButton.setImageResource(R.drawable.emoji);
                        }
                    })
                    .setKeyboardAnimationStyle(R.style.emoji_fade_animation_style)
                    .setPageTransformer(new PageTransformer())
                    //.build(composeText);
                    .build(text_send, true);
            Log.e("CHATACTIVIT----EMOJI","Builder with animation");
        }
        else {
            emojiPopup = EmojiPopup.Builder.fromRootView(rootView)
                    .setOnEmojiPopupShownListener(new OnEmojiPopupShownListener() {
                        @Override
                        public void onEmojiPopupShown() {
                            emojiButton.setImageResource(R.drawable.input_keyboard);
                        }
                    })
                    .setOnEmojiPopupDismissListener(new OnEmojiPopupDismissListener() {
                        @Override
                        public void onEmojiPopupDismiss() {
                            emojiButton.setImageResource(R.drawable.emoji);
                        }
                    })
                    .setPageTransformer(new PageTransformer())
                    .build(text_send, true);
        }
    }

    @Override
    public void onDestroy() {
        if(datos_chat!=null){
            if(datos_chat.size()>0){
                dbWorker.marcarComoVistoCorreosNoVistosDe(correo);
                if (YouChatApplication.bandejaFragment != null) {
                    Utils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            YouChatApplication.bandejaFragment
                                    .actualizarUsuario(correo);

                        }
                    });
                }
            }
            else{
                vaciarChat();
                if (YouChatApplication.bandejaFragment != null) {
                    Utils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            YouChatApplication.bandejaFragment
                                    .eliminarUsuario(correo);

                        }
                    });
                }
            }
        }

        YouChatApplication.chatsActivityCorreo = null;

        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(adaptadorChat!=null){
            adaptadorChat.detenerPlayAudio();
        }
        estaMinimizado = true;
    }

    public boolean estaMinimizada(){
        return estaMinimizado;
    }

    public void hideFragment(){
//        dbWorker.marcarComoVistoMensajesNoVistos(correo);
//        if(datos_chat.size()==0) vaciarChat();
//        agregarNuevoUsuario();
//        if(datos_chat.size()>0) dbWorker.actualizarUltMsgUsuario(buscarUltMsgAPartirDe(0));
//        String borrador = text_send.getText().toString();
//        dbWorker.actualizarBorrador(correo,borrador);
    }

    public void atras(){
        if(adaptadorChat.estaPlayAudio()) adaptadorChat.detenerPlayAudio();
        mainActivity.atrasFragment();
    }
    
    public void onBackPressed() {
        if(estaImagenEnPhotoView()) cerrarViewPagerPreview();
        else if(quickAttachmentDrawer.isShowing()) container.hideAttachedInput(false);
        else if(estaActivoBuscar || ll_buscar.getVisibility()==View.VISIBLE) {
            cancelarBuscar();
            final InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(et_buscar_chat.getWindowToken(), 0);
        }
        else if(estaModoSeleccionar()) cancelarModoSeleccionar();
        else atras();
    }

    public void cerrarMenu() {
        if(menu_chat.getVisibility()!=View.GONE){
            anim=AnimationUtils.loadAnimation(context,R.anim.fade_out_fast);
            menu_chat.setVisibility(View.GONE);
            menu_chat.startAnimation(anim);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    public synchronized void reproducirSonidoParaGrabar(int sound){
        if(!YouChatApplication.sonido) {
            Utils.vibrate(context, 50);
            startRec();
            return;
        }
        MediaPlayer mediaPlayer = MediaPlayer.create(context,sound);
        try{

            int vol = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
            mediaPlayer.setVolume(vol,vol);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Utils.vibrate(context, 50);
                    mediaPlayer.release();
                    startRec();
                }
            });
        }catch (IllegalStateException e){
            e.printStackTrace();
            if(mediaPlayer!=null) mediaPlayer.release();
        }catch (Exception e){
            e.printStackTrace();
            if(mediaPlayer!=null) mediaPlayer.release();
        }
    }

    public static String getCad() {
        return "\u009A\u0080";
    }

    public synchronized void enviarMensaje(){
        String texto_enviar=text_send.getText().toString().trim();
        enviarMensaje(texto_enviar);
    }
    public synchronized void enviarMensaje(String texto_enviar){
        if(texto_enviar.replace(" ","").length()==0){
            Utils.ShowToastAnimated(mainActivity,"Por favor, entre un mensaje",R.raw.swipe_disabled);
            return;
        }
        else{
            procedimientoComprobatorio();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
            Date date = new Date();
            String fechaEntera = sdf.format(date);

            String id="YCchat"+correo+""+datos_chat.size()+""+fechaEntera;
            String hora = Convertidor.conversionHora(fechaEntera);
            String fecha = Convertidor.conversionFecha(fechaEntera);

            text_send.setText("");

            if(ll_answer.getVisibility()==View.VISIBLE){ //responder
                ll_answer.setVisibility(View.GONE);
                String cad = "";
                ItemMensajeCorreo mensajeCorreoRes = dbWorker.obtenerMensajeCorreo(id_msg_answer);
                if(mensajeCorreoRes!=null) {
                    cad += "\n\nRespondido de:\n";
                    cad += "De: " + mensajeCorreoRes.getRemitente() + "\n";
                    cad += "Para: " + mensajeCorreoRes.getDestinatario() + "\n";
                    cad += "Asunto: " + mensajeCorreoRes.getAsunto() + "\n";
                    cad += "Fecha de envío: " + Convertidor.convertirFechaAFechaLinda(mensajeCorreoRes.getFecha())
                            + ", " + mensajeCorreoRes.getHora() + "\n";
                    cad += "Contenido:\n" + mensajeCorreoRes.getTexto();
                }
                texto_enviar+=cad;
            }
            adaptadorChat.hacerAnim();
            ItemChat newChat=new ItemChat( id,
                    2, 1, correo,
                    texto_enviar,
                    "",
                    hora, fecha, groupId, aut_user, false, fechaEntera,false,
                    "",0,true);

            ItemMensajeCorreo newCorreo = new ItemMensajeCorreo(id, 0L, true, false,
                    false, correo, YouChatApplication.correo, YouChatApplication.alias, correo,
                    "", texto_enviar,ItemChat.ESTADO_ESPERANDO,
                    false, 0, hora, fecha, fechaEntera);
            dbWorker.insertarNuevoMensajeCorreo(newCorreo);
            dbWorker.insertarUsuarioCorreo(new ItemUsuarioCorreo(newCorreo), true);

            if(YouChatApplication.estaAndandoChatService())
                YouChatApplication.chatService.enviarMensaje(newChat,SendMsg.CATEGORY_CHAT_CORREO);
            datos_chat.add(0, newChat);
            adaptadorChat.notifyItemInserted(0);
            lista_chat.scrollToPosition(0);
            if(YouChatApplication.bandejaFragment!=null){
                Utils.runOnUIThread(()->{
                    YouChatApplication.bandejaFragment.addNewCorreo(newCorreo, true);
                });
            }
        }
    }

    public synchronized void enviarImagen(String rutaImg, String texto_enviar){
        procedimientoComprobatorio();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
        Date date = new Date();
        String fechaEntera = sdf.format(date);

        String id="YCchat"+correo+""+datos_chat.size()+""+fechaEntera;
        String hora = Convertidor.conversionHora(fechaEntera);
        String fecha = Convertidor.conversionFecha(fechaEntera);

        adaptadorChat.hacerAnim();

        if(!texto_enviar.trim().isEmpty()){
            ItemChat newChat3=new ItemChat( id, 2, ItemChat.ESTADO_ESPERANDO, correo,
                    texto_enviar,
                    "",
                    hora, fecha, "", aut_user,
                    false, fechaEntera,false,"",0,true);
            datos_chat.add(0, newChat3);
            adaptadorChat.notifyItemInserted(0);
        }
        ItemChat newChat=new ItemChat(id,
                4, 1, correo, texto_enviar,  rutaImg,
                hora, fecha, groupId, aut_user, false, fechaEntera,false,"",0,true);

        ItemMensajeCorreo newCorreo = new ItemMensajeCorreo(id, 0L, true, false,
                false, correo, YouChatApplication.correo, YouChatApplication.alias, correo,
                "", texto_enviar,ItemChat.ESTADO_ESPERANDO,
                false, 0, hora, fecha, fechaEntera);
        dbWorker.insertarNuevoMensajeCorreo(newCorreo);
        dbWorker.insertarUsuarioCorreo(new ItemUsuarioCorreo(newCorreo), true);

        String nombrePart = new File(rutaImg).getName();
        dbWorker.insertarNuevoAdjuntoCorreo(new ItemAdjuntoCorreo(nombrePart + fechaEntera, id, correo, -1,
                rutaImg, 4, (int)new File(rutaImg).length()));

        ItemChat newChat2=new ItemChat(id,
                4, ItemChat.ESTADO_ESPERANDO, correo,
                "",
                rutaImg,
                hora, fecha, "", aut_user,
                false, fechaEntera,false,"",0,true);
        datos_chat.add(0, newChat2);
        adaptadorChat.notifyItemInserted(0);

        AlbumFile temp= new AlbumFile();
        temp.setPath(rutaImg);
        temp.setChecked(false);
        temp.setMediaType(AlbumFile.TYPE_IMAGE);
        imagenesChatAlbumFile.add(temp);

        if(YouChatApplication.estaAndandoChatService())
            YouChatApplication.chatService.enviarMensaje(newChat, SendMsg.CATEGORY_CHAT_CORREO);
        lista_chat.scrollToPosition(0);

        if(YouChatApplication.bandejaFragment!=null){
            Utils.runOnUIThread(()->{
                YouChatApplication.bandejaFragment.addNewCorreo(newCorreo, true);
            });
        }
    }

    public synchronized void enviarAudio(){
        procedimientoComprobatorio();
        Utils.reproducirSonido(R.raw.rec_end, context);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
        Date date = new Date();
        String fechaEntera = sdf.format(date);
        String id="YCchat"+correo+""+datos_chat.size()+""+fechaEntera;
        String hora = Convertidor.conversionHora(fechaEntera);
        String fecha = Convertidor.conversionFecha(fechaEntera);

        adaptadorChat.hacerAnim();
        ItemChat newChat=new ItemChat( id,
                8, 1, correo, audio_rec_duracion,  ruta_audio,
                hora, fecha, groupId, aut_user, false, fechaEntera,false,"",0,true);

        ItemMensajeCorreo newCorreo = new ItemMensajeCorreo(id, 0L, true, false,
                false, correo, YouChatApplication.correo, YouChatApplication.alias, correo,
                "", "",ItemChat.ESTADO_ESPERANDO,
                false, 0, hora, fecha, fechaEntera);
        dbWorker.insertarNuevoMensajeCorreo(newCorreo);
        dbWorker.insertarUsuarioCorreo(new ItemUsuarioCorreo(newCorreo), true);

        String nombrePart = new File(ruta_audio).getName();
        dbWorker.insertarNuevoAdjuntoCorreo(
                new ItemAdjuntoCorreo(nombrePart + fechaEntera, id, correo, -1,
                        ruta_audio, 1, (int)new File(ruta_audio).length()));

        ItemChat newChat2=new ItemChat(id, 8, ItemChat.ESTADO_ESPERANDO, correo,
                "",
                ruta_audio,
                hora, fecha, "", aut_user,
                false, fechaEntera,false,"",0,true);
        datos_chat.add(0, newChat2);
        adaptadorChat.notifyItemInserted(0);

        if(YouChatApplication.estaAndandoChatService())
            YouChatApplication.chatService.enviarMensaje(newChat, SendMsg.CATEGORY_CHAT_CORREO);
        lista_chat.scrollToPosition(0);

        if(YouChatApplication.bandejaFragment!=null){
            Utils.runOnUIThread(()->{
                YouChatApplication.bandejaFragment.addNewCorreo(newCorreo, true);
            });
        }

    }

    public synchronized void enviarArchivo(String rutaArchivo){
        procedimientoComprobatorio();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
        Date date = new Date();
        String fechaEntera = sdf.format(date);

        String id="YCchat"+correo+""+datos_chat.size()+""+fechaEntera;
        String hora = Convertidor.conversionHora(fechaEntera);
        String fecha = Convertidor.conversionFecha(fechaEntera);

        adaptadorChat.hacerAnim();

        ItemChat newChat=new ItemChat(id,
                14,
                1,
                correo,
                "",
                rutaArchivo,//rutaDato
                hora,
                fecha,
                groupId,
                aut_user,
                false,
                fechaEntera,false,"",0,true);

        ItemMensajeCorreo newCorreo = new ItemMensajeCorreo(id, 0L, true, false,
                false, correo, YouChatApplication.correo, YouChatApplication.alias, correo,
                "", "",ItemChat.ESTADO_ESPERANDO,
                false, 0, hora, fecha, fechaEntera);
        dbWorker.insertarNuevoMensajeCorreo(newCorreo);
        dbWorker.insertarUsuarioCorreo(new ItemUsuarioCorreo(newCorreo), true);

        String nombrePart = new File(rutaArchivo).getName();
        dbWorker.insertarNuevoAdjuntoCorreo(
                new ItemAdjuntoCorreo(nombrePart + fechaEntera, id, correo, -1,
                        rutaArchivo,
                        Utils.obtenerTipoDadounaExtension(Utils.obtenerExtension(nombrePart)),
                        (int)new File(rutaArchivo).length()));

        ItemChat newChat2=new ItemChat(id,14, ItemChat.ESTADO_ESPERANDO, correo,
                "",
                rutaArchivo,
                hora, fecha, "", aut_user,
                false, fechaEntera,false,"",0,true);
        datos_chat.add(0, newChat2);
        adaptadorChat.notifyItemInserted(0);

        if(YouChatApplication.estaAndandoChatService())
            YouChatApplication.chatService.enviarMensaje(newChat, SendMsg.CATEGORY_CHAT_CORREO);
        lista_chat.scrollToPosition(0);

        if(YouChatApplication.bandejaFragment!=null){
            Utils.runOnUIThread(()->{
                YouChatApplication.bandejaFragment.addNewCorreo(newCorreo, true);
            });
        }
    }

    public void enviarSticker(String rutaSticker) {
        procedimientoComprobatorio();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
        Date date = new Date();
        String fechaEntera = sdf.format(date);

        String id="YCchat"+correo+""+datos_chat.size()+""+fechaEntera;
        String hora = Convertidor.conversionHora(fechaEntera);
        String fecha = Convertidor.conversionFecha(fechaEntera);

        adaptadorChat.hacerAnim();

        ItemChat newChat=new ItemChat(id,
                20,
                1,
                correo,
                "",
                rutaSticker,//rutaDato
                hora,
                fecha,
                groupId,
                aut_user,
                false,
                fechaEntera,false,"",0,true);

        ItemMensajeCorreo newCorreo = new ItemMensajeCorreo(id, 0L, true, false,
                false, correo, YouChatApplication.correo, YouChatApplication.alias, correo,
                "", "",ItemChat.ESTADO_ESPERANDO,
                false, 0, hora, fecha, fechaEntera);
        dbWorker.insertarNuevoMensajeCorreo(newCorreo);
        dbWorker.insertarUsuarioCorreo(new ItemUsuarioCorreo(newCorreo), true);

        String nombrePart = new File(rutaSticker).getName();
        dbWorker.insertarNuevoAdjuntoCorreo(
                new ItemAdjuntoCorreo(nombrePart + fechaEntera, id, correo, -1,
                        rutaSticker, 10, (int)new File(rutaSticker).length()));

        ItemChat newChat2=new ItemChat(id,20, ItemChat.ESTADO_ESPERANDO, correo,
                "",
                rutaSticker,
                hora, fecha, "", aut_user,
                false, fechaEntera,false,"",0,true);
        datos_chat.add(0,newChat2);
        adaptadorChat.notifyItemInserted(0);

        if(YouChatApplication.estaAndandoChatService())
            YouChatApplication.chatService.enviarMensaje(newChat, SendMsg.CATEGORY_CHAT_CORREO);
        lista_chat.scrollToPosition(0);

        if(YouChatApplication.bandejaFragment!=null){
            Utils.runOnUIThread(()->{
                YouChatApplication.bandejaFragment.addNewCorreo(newCorreo, true);
            });
        }
    }

    public void procedimientoComprobatorio(){
        if(img_answer_chat.getVisibility()==View.VISIBLE) img_answer_chat.setVisibility(View.GONE);
        if(datos_chat.size()==0) agregarNuevoUsuario();
        if(estaActivoBuscar || ll_buscar.getVisibility()==View.VISIBLE) cancelarBuscar();
        if(estaModoSeleccionar()) cancelarModoSeleccionar();
    }

    private synchronized void agregarNuevoUsuario(){
        ItemContacto contacto=new ItemContacto(apodo,correo);
        dbWorker.insertarNuevoContactoNoVisible(contacto, false);
    }

    private synchronized void cargarPreferencias() {
        aut_user = YouChatApplication.correo;
        calidad=YouChatApplication.calidad;

        Utils.cargarFondo(context,chat_fondo);
    }

    public void actualizarRecyclerChat(boolean desdeCero){
        if(desdeCero){
//            datos_chat.clear();
            tamArrAnt=limite;
        }

        int longi = datos_chat.size();
        datos_chat.clear();
        datos_chat.addAll(obtener_y_transformarMensajesCorreos_a_ItemChat(correo,longi,limite));

        adaptadorChat.notifyDataSetChanged();

        activarFuncionesAdaptador();

        obtenerTodasLasImagenesYEnviarMensajesNoEnviados();
        if(cant_Msg_No_Vistos>0 && datos_chat.size()>0){
            if(cant_Msg_No_Vistos>datos_chat.size())
                cant_Msg_No_Vistos=datos_chat.size();
            String msg="";
            if(cant_Msg_No_Vistos==1) msg="1 mensaje no visto";
            else msg=cant_Msg_No_Vistos+" mensajes no visto";
            datos_chat.add(cant_Msg_No_Vistos, new ItemChat("",99, 0, "",
                    msg,"","",datos_chat.get(cant_Msg_No_Vistos-1).getFecha(),
                    "","",false,"",false,"",0,true));
        }
    }

    private ArrayList<ItemChat> obtener_y_transformarMensajesCorreos_a_ItemChat(String correo, int cantAct, int limite) {
        ArrayList<ItemMensajeCorreo> mensajeCorreos = dbWorker.obtenerMensajeCorreoDe(correo);
        ArrayList<ItemChat> chatTemp = new ArrayList<>();
        int contLimite = 0;
        limite = cantAct+limite;
        int l = mensajeCorreos.size();
        for(int i=0; i<l; i++){
            if(contLimite>=limite) break;
            ItemMensajeCorreo mensajeCorreo = mensajeCorreos.get(i);

            if(!mensajeCorreo.getTexto().trim().isEmpty()){
                ItemChat newChat=new ItemChat( mensajeCorreo.getId(),
                        mensajeCorreo.isEsMio()?2:1, mensajeCorreo.getEstado(), correo,
                        mensajeCorreo.getTexto(),
                        "",
                        mensajeCorreo.getHora(), mensajeCorreo.getFecha(), "",
                        mensajeCorreo.getRemitente(),
                        false, mensajeCorreo.getOrden(),false,mensajeCorreo.getId(),
                        mensajeCorreo.getPeso(),true);
                chatTemp.add(newChat);
                contLimite++;
            }

            ArrayList<ItemAdjuntoCorreo> adjuntos = dbWorker.obtenerAdjuntosCorreoDe(mensajeCorreo.getId());
            int lA = adjuntos.size();
            for(int j=0; j<lA; j++){
                if(contLimite>=limite) break;
                ItemAdjuntoCorreo adj = adjuntos.get(j);
                int tipo;
                if(adj.esImagen()) tipo = mensajeCorreo.isEsMio()?4:3;
                else if(adj.esAudio()) tipo = mensajeCorreo.isEsMio()?8:7;
                else if(adj.esSticker()) tipo = mensajeCorreo.isEsMio()?20:19;
                else tipo = mensajeCorreo.isEsMio()?14:13;

                if(mensajeCorreo.isEsMio()){
                    ItemChat newChat=new ItemChat(mensajeCorreo.getId(),
                            tipo, mensajeCorreo.getEstado(), correo,
                            "",
                            adj.getNombre(),
                            mensajeCorreo.getHora(), mensajeCorreo.getFecha(), "",
                            mensajeCorreo.getRemitente(),
                            false, mensajeCorreo.getOrden(),false,mensajeCorreo.getId(),
                            mensajeCorreo.getPeso(),true);
                    chatTemp.add(newChat);
                    contLimite++;
                }
                else {
                    if(new File(YouChatApplication.RUTA_ADJUNTOS_CORREO+adj.getNombre()).exists()){
                        ItemChat newChat=new ItemChat(mensajeCorreo.getId(),
                                tipo, mensajeCorreo.getEstado(), correo,
                                "",
                                adj.getNombre(),
                                mensajeCorreo.getHora(), mensajeCorreo.getFecha(), "",
                                mensajeCorreo.getRemitente(),
                                false, mensajeCorreo.getOrden(),false,
                                adj.getId(),
                                adj.getPeso(),true);
                        chatTemp.add(newChat);
                        contLimite++;
                    }
                    else if(adj.getPosicion()!=-1){
                        ItemChat chatA = new ItemChat(mensajeCorreo.getId(),
                                tipo, mensajeCorreo.getEstado(), correo,
                                "",
                                adj.getNombre(),
                                mensajeCorreo.getHora(), mensajeCorreo.getFecha(), "",
                                mensajeCorreo.getRemitente(),
                                false, mensajeCorreo.getOrden(),false,
                                adj.getId(),adj.getPeso(),false);

//                        String tamString = "Tamaño desconocido";
//                        if(adj.getPeso()>0) tamString = Utils.convertirBytes(adj.getPeso());

//                        tamString = ""+adj.getPeso();

//                        ItemChat chatA = new ItemChat(correo, "");
//                        chatA.setId(mensajeCorreo.getId());
//                        chatA.setTipo_mensaje(81);
//                        chatA.setHora(mensajeCorreo.getHora());
//                        chatA.setFecha(mensajeCorreo.getFecha());
//                        chatA.setOrden(mensajeCorreo.getOrden());
//                        chatA.setEstado(ItemChat.ESTADO_VISTO);
//
//                        if (adj.esAudio())
//                            chatA.setId_msg_resp(tamString + "/audio");
//                        else if (adj.esImagen())
//                            chatA.setId_msg_resp(tamString + "/imagen");
//                        else if (adj.esSticker())
//                            chatA.setId_msg_resp(tamString + "/sticker");
//                        else chatA.setId_msg_resp(tamString + "/archivo");
//
//                        chatA.setRuta_Dato(adj.getId());
//
//                        chatA.setId_mensaje(adj.getId());
//                        chatA.setPeso(adj.getPeso());
//                        chatA.setDescargado(false);

                        chatTemp.add(chatA);
                        contLimite++;
                    }
                    else {
                        ItemChat newChat=new ItemChat(mensajeCorreo.getId(),
                                tipo, mensajeCorreo.getEstado(), correo,
                                "",
                                adj.getNombre(),
                                mensajeCorreo.getHora(), mensajeCorreo.getFecha(), "",
                                mensajeCorreo.getRemitente(),
                                false, mensajeCorreo.getOrden(),false,
                                adj.getId(),
                                adj.getPeso(),true);
                        chatTemp.add(newChat);
                        contLimite++;
                    }

                }
            }
        }
        return chatTemp;
    }

    private synchronized void obtenerTodasLasImagenesYEnviarMensajesNoEnviados(){
        imagenesChatAlbumFile=new ArrayList<>();
        int l=datos_chat.size();
        for(int i=l-1; i>=0; i--){
            ItemChat msg=datos_chat.get(i);
            if (msg.esImagen()){
                String ruta = msg.getRuta_Dato();
                if(msg.esIzq()){
                    ruta = YouChatApplication.RUTA_ADJUNTOS_CORREO+msg.getRuta_Dato();
                }
                if(!ruta.isEmpty() && new File(ruta).exists()){
                    AlbumFile temp= new AlbumFile();
                    temp.setPath(ruta);
                    temp.setChecked(false);
                    temp.setMediaType(AlbumFile.TYPE_IMAGE);
                    imagenesChatAlbumFile.add(temp);
                }
            }
        }
    }

    private int buscarId(String id){
        int l=datos_chat.size();
        for(int i=0; i<l; i++){
            if(datos_chat.get(i).getId().equals(id))
                return i;
        }
        return -1;
    }

    public void MostrarImagen(String ruta, ImageView imageView){
        positionViewPagerPreviewInicial = buscarPosImg(ruta);
        if(positionViewPagerPreviewInicial!=-1){
            if(adaptadorChat.estaPlayAudio()) adaptadorChat.detenerPlayAudio();
            positionViewPagerPreviewActual = positionViewPagerPreviewInicial;
            item_photoView_visorImg_pv = imageView;
            info_visorImg_pv = PhotoView.getImageViewInfo(item_photoView_visorImg_pv);
            ViewPagerAdapter mAdapter = new ViewPagerAdapter(context, imagenesChatAlbumFile);
            preview_viewPager.setAdapter(mAdapter);
            preview_viewPager.setCurrentItem(positionViewPagerPreviewInicial, false);
            previewParent.setVisibility(View.VISIBLE);
            if(mainActivity!=null)
                mainActivity.cambiarColorStatusBar("#ff000000");
            PointF pointF = CommonUtil.getNeedSize(context,
                    info_visorImg_pv.mImgRect.width(), info_visorImg_pv.mImgRect.height());
            float imgHeight = pointF.y;
            float imgWidth = pointF.x;
            Glide.with(context).load(ruta)
                    .override((int) imgWidth, (int) imgHeight).dontAnimate()
                    .into(transBigImage);
            Glide.with(context).load(ruta)
                    .override((int) imgWidth, (int) imgHeight).dontAnimate()
                    .into(transImage);
            int[] location = new int[2];
            item_photoView_visorImg_pv.getLocationOnScreen(location);
            locationOnScreen[0] = location[0];
            locationOnScreen[1] = location[1];
            locationOnScreen[2] = (int) info_visorImg_pv.mImgRect.width();
            locationOnScreen[3] = (int) info_visorImg_pv.mImgRect.height();

            transBigImage.init(locationOnScreen[0], locationOnScreen[1],
                    locationOnScreen[2], locationOnScreen[3]);
            transBigImage.startTrans();
            transBigImage.setTransEnd(new TransBigImageView.TransEnd() {
                @Override
                public void end() {
                    preview_viewPager.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    int buscarPosImg(String ruta){
        int l=imagenesChatAlbumFile.size();
        for(int i=0; i<l; i++)
            if(imagenesChatAlbumFile.get(i).getPath().equals(ruta))
                return i;
        return -1;
    }

    ///////////////////////////////////item opcion/////////////////////////////////inicio

    public synchronized void vaciarChat(){
        dbWorker.eliminarUsuarioCorreo(correo);
    }

    public void Buscar_Chat(String s) {
        Utils.runOnUIThread(()->{
            if(adaptadorChat.estaPlayAudio()) adaptadorChat.detenerPlayAudio();
            if(estaActivoBuscar || ll_buscar.getVisibility()==View.VISIBLE) {
                if(search_audio || search_imagen) {
                    et_buscar_chat.setEnabled(false);
                    Buscar(s);
                    estaActivoBuscar=true;
                }
                else if(search_mark){
                    search_mark=false;
                    et_buscar_chat.setEnabled(true);
                    et_buscar_chat.setText(s);
                    Buscar(s);
                    estaActivoBuscar=false;
                }
                else {
                    cancelarBuscar();
                    et_buscar_chat.setEnabled(true);
                }
            }
            else {
                if(YouChatApplication.animaciones_chat){
                    anim= AnimationUtils.loadAnimation(context,R.anim.show_layout_search);
                    ll_buscar.startAnimation(anim);
                }
                ll_buscar.setVisibility(View.VISIBLE);
                ll_contacto.setVisibility(View.GONE);
                //drawer.closeDrawer(GravityCompat.END);
                et_buscar_chat.setText(s);
                Buscar(s);
                estaActivoBuscar=true;
                if(s.equals("")){
                    estaActivoBuscar=false;
                    et_buscar_chat.setFocusableInTouchMode(true);
                    et_buscar_chat.requestFocus();
                    final InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(et_buscar_chat, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });
    }


    void cancelarBuscar(){
        search_mark=false;
        search_imagen=false;
        search_audio=false;
        if(YouChatApplication.animaciones_chat){
            Utils.runOnUIThread(()->{
                anim=AnimationUtils.loadAnimation(context,R.anim.hide_layout_filtro);
                ll_audio.setVisibility(View.GONE);
                ll_audio.startAnimation(anim);
                ll_imagen.setVisibility(View.GONE);
                ll_imagen.startAnimation(anim);
            });
        }
        else {
            ll_audio.setVisibility(View.GONE);
            ll_imagen.setVisibility(View.GONE);
        }
        ll_audio.setStrokeColor(Color.parseColor(YouChatApplication.itemTemas.getColor_barra()));
        ll_imagen.setStrokeColor(Color.parseColor(YouChatApplication.itemTemas.getColor_barra()));

        adaptadorChat = new AdaptadorDatosChatCorreo(context,datos_chat, ChatsActivityCorreo.this);
        lista_chat.setAdapter(adaptadorChat);
        ponerStickyHeadersDecoration();
        activarFuncionesAdaptador();
        lista_chat.scrollToPosition(0);
        et_buscar_chat.setText("");
        if(YouChatApplication.animaciones_chat){
            anim= AnimationUtils.loadAnimation(context,R.anim.hide_layout_search);
            ll_buscar.startAnimation(anim);
        }
        ll_buscar.setVisibility(View.GONE);
        ll_contacto.setVisibility(View.VISIBLE);
        estaActivoBuscar = false;
    }

    private void ponerStickyHeadersDecoration() {
        int l = lista_chat.getItemDecorationCount();
        if(l>0){
            for(int i=0; i<l; i++)
                lista_chat.removeItemDecorationAt(0);
        }
        StickyRecyclerHeadersDecoration headersDecor =
                new StickyRecyclerHeadersDecoration(adaptadorChat);
        lista_chat.addItemDecoration(headersDecor);
    }

    synchronized void Buscar(String s){
        Utils.runOnUIThread(()->{
            datos_chat_buscar = new ArrayList<>();
            if(s.equals("") && !search_audio && !search_imagen){
                estaActivoBuscar = false;
                return;
            }
            int l=datos_chat.size();
            boolean si_fecha=false;
            if(search_audio)//s.equals("filtro: audio"))
            {
                for(int i=0; i<l; i++){
                    if(datos_chat.get(i).esAudio()){
                        si_fecha=true;
                        datos_chat_buscar.add(datos_chat.get(i));
                    }
                    else if(datos_chat.get(i).getTipo_mensaje()==0 && si_fecha){
                        si_fecha=false;
                        datos_chat_buscar.add(datos_chat.get(i));
                    }
                }
            }
            else if(search_imagen)//s.equals("filtro: imagen"))
            {
                for(int i=0; i<l; i++){
                    if(datos_chat.get(i).esImagen()){
                        si_fecha=true;
                        datos_chat_buscar.add(datos_chat.get(i));
                    }
                    else if(datos_chat.get(i).getTipo_mensaje()==0 && si_fecha){
                        si_fecha=false;
                        datos_chat_buscar.add(datos_chat.get(i));
                    }
                }
            }
            else {
                for(int i=0; i<l; i++){
                    if(!datos_chat.get(i).esAudio() &&
                            datos_chat.get(i).getMensaje().toLowerCase().contains(s.toLowerCase())){
                        si_fecha=true;
                        datos_chat_buscar.add(datos_chat.get(i));
                    }
                    else if(datos_chat.get(i).getTipo_mensaje()==0 && si_fecha){
                        si_fecha=false;
                        datos_chat_buscar.add(datos_chat.get(i));
                    }
                }
            }
            adaptadorChat = new AdaptadorDatosChatCorreo(context,datos_chat_buscar, ChatsActivityCorreo.this);
            lista_chat.setAdapter(adaptadorChat);
            ponerStickyHeadersDecoration();
            activarFuncionesAdaptadorBuscar();
            lista_chat.scrollToPosition(0);
        });
    }

    ///////////////////////////////////item opcion/////////////////////////////////fin

    public void activarFuncionesAdaptador(){
        adaptadorChat.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(estaModoSeleccionar()){
                    ItemChat temp = datos_chat.get(lista_chat.getChildAdapterPosition(v));
                    actualizarSeleccion(temp);
                }
                else {
                    PopupMsgChatCorreo popupMsgChat = new PopupMsgChatCorreo(v, ChatsActivityCorreo.this);
                    if(estaActivoBuscar) option = datos_chat_buscar.get(lista_chat.getChildAdapterPosition(v));
                    else option = datos_chat.get(lista_chat.getChildAdapterPosition(v));
                    popupMsgChat.show(v, option);
                }
            }
        });
        adaptadorChat.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                if(!estaActivoBuscar){
                    ItemChat temp = datos_chat.get(lista_chat.getChildAdapterPosition(v));
                    if(estaModoSeleccionar()){
                        actualizarSeleccion(temp);
                    }else {
                        activarModoSeleccionar();
                        actualizarSeleccion(temp);
                    }
                }
//                if(AdaptadorDatosChat.estaPlayAudio()) AdaptadorDatosChat.detenerPlayAudio();
                //if(contenedor_input_audio.getVisibility()==View.VISIBLE) cancelarBloquearRec();
//                BottomSheetDialogFragment bsdFragment_optionChat;
//                if(estaActivoBuscar) option = datos_chat_buscar.get(lista_chat.getChildAdapterPosition(v));
//                else option = datos_chat.get(lista_chat.getChildAdapterPosition(v));
//                if(option.hayQReintentarEnviar())
//                    bsdFragment_optionChat = BottomSheetDialogFragment_Option_Chat.newInstance(chatsActivity,1);
//                else
//                    bsdFragment_optionChat = BottomSheetDialogFragment_Option_Chat.newInstance(chatsActivity,2);
//
//                bsdFragment_optionChat.show(ChatsActivity.this.getSupportFragmentManager(), "BSDialog_Option_Chat");
                return true;
            }
        });
    }

    public void activarFuncionesAdaptadorBuscar(){
        adaptadorChat.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(estaModoSeleccionar()){
                    ItemChat temp = datos_chat_buscar.get(lista_chat.getChildAdapterPosition(v));
                    actualizarSeleccion(temp);
                }
                else {
                    PopupMsgChatCorreo popupMsgChat = new PopupMsgChatCorreo(v, ChatsActivityCorreo.this);
                    if(estaActivoBuscar) option = datos_chat_buscar.get(lista_chat.getChildAdapterPosition(v));
                    else option = datos_chat.get(lista_chat.getChildAdapterPosition(v));
                    popupMsgChat.show(v, option);
                }
            }
        });
        adaptadorChat.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                if(!estaActivoBuscar){
                    ItemChat temp = datos_chat_buscar.get(lista_chat.getChildAdapterPosition(v));
                    if(estaModoSeleccionar()){
                        actualizarSeleccion(temp);
                    }else {
                        activarModoSeleccionar();
                        actualizarSeleccion(temp);
                    }
                }
//                if(AdaptadorDatosChat.estaPlayAudio()) AdaptadorDatosChat.detenerPlayAudio();
                //if(contenedor_input_audio.getVisibility()==View.VISIBLE) cancelarBloquearRec();
//                BottomSheetDialogFragment bsdFragment_optionChat;
//                if(estaActivoBuscar) option = datos_chat_buscar.get(lista_chat.getChildAdapterPosition(v));
//                else option = datos_chat.get(lista_chat.getChildAdapterPosition(v));
//                if(option.hayQReintentarEnviar())
//                    bsdFragment_optionChat = BottomSheetDialogFragment_Option_Chat.newInstance(chatsActivity,1);
//                else
//                    bsdFragment_optionChat = BottomSheetDialogFragment_Option_Chat.newInstance(chatsActivity,2);
//
//                bsdFragment_optionChat.show(ChatsActivity.this.getSupportFragmentManager(), "BSDialog_Option_Chat");
                return true;
            }
        });
    }

    /////////////////////////////ABRIR IMAGEN///////////////////////////////////////////////
    public void abrircamara(View view) {
        view.setEnabled(false);
        usarCamara();
        view.setEnabled(true);
    }

    public void usarCamara(){
        if(!permisos.requestPermissionCamera()) return;
        if(estaActivoBuscar || ll_buscar.getVisibility()==View.VISIBLE) cancelarBuscar();
        if(estaModoSeleccionar()) cancelarModoSeleccionar();

        File directorio = new File(YouChatApplication.RUTA_IMAGENES_ENVIADAS);
        boolean exist = directorio.exists();
        if(!exist)
            exist = directorio.mkdir();
        if(exist){

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
                    Utils.ShowToastAnimated(mainActivity,"Ocurrió un error al acceder a la cámara",R.raw.error);
                }
            }
            else {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
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
                        photoURI = FileProvider.getUriForFile(context,
                                "cu.alexgi.youchat.fileprovider",
                                photoFile);

                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, 31);
                    }
                }
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String imageFileName = "img" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(YouChatApplication.RUTA_IMAGENES_ENVIADAS);
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
        if(estaActivoBuscar || ll_buscar.getVisibility()==View.VISIBLE) cancelarBuscar();
        if(estaModoSeleccionar()) cancelarModoSeleccionar();
        if(adaptadorChat.estaPlayAudio()) adaptadorChat.detenerPlayAudio();
        if(!permisos.requestPermissionAlmacenamiento()) return;
        selectImage();

    }

    private void selectImage() {
        Album.image(this)
                .multipleChoice()
                .camera(false)
                .columnCount(3)
                .selectCount(1000)
                //.checkedList(mAlbumFiles)
                .widget(
                        Widget.newDarkBuilder(context)
                                .title("Galería")
                                .build()
                )
                .onResult(new Action<ArrayList<AlbumFile>>() {
                    @Override
                    public void onAction(@NonNull ArrayList<AlbumFile> result) {
                        //mAlbumFiles = result;
                        //mAdapter.notifyDataSetChanged(mAlbumFiles);
                        comprimirImagen(result);
                    }
                })
                .start();
    }

    public synchronized void comprimirImagen(ArrayList<AlbumFile> result) {
        Dialog dialog = Utils.mostrarDialogCarga(this, context, "Comprimiendo imágenes...");
        Utils.runOnUIThread(()->{
            if (!permisos.requestPermissionAlmacenamiento()){
                Utils.cerrarDialogCarga(dialog);
                return;
            }
            File directorioImagenes = new File(YouChatApplication.RUTA_ESTADOS_GUARDADOS);
            if (!directorioImagenes.exists()) if (!directorioImagenes.mkdirs()){
                Utils.cerrarDialogCarga(dialog);
                return;
            }

            String fechaEntera = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());

            ArrayList<ItemImg> itemImgs = new ArrayList<>();
            int l=result.size();
            for(int i=0; i<l; i++){
                String pp1=result.get(i).getPath();
                String nombre_img="img"+fechaEntera+""+i+".jpg";
                String r1 =YouChatApplication.RUTA_ESTADOS_GUARDADOS+nombre_img;
                try {
                    if(ImageLoader.init().comprimirImagen(pp1,r1,YouChatApplication.calidad)){
                        itemImgs.add(new ItemImg(r1,pp1,YouChatApplication.calidad));
                    }
                } catch (FileNotFoundException e) {
                    Utils.ShowToastAnimated(mainActivity,"Error al cargar la imagen",R.raw.error);
                }
            }
            Utils.cerrarDialogCarga(dialog);
            irAlEditor(itemImgs);
        },300);
    }

    public synchronized void comprimirImagen2(ArrayList<AlbumFile> result) {
        Utils.runOnUIThread(()->{
            if (!permisos.requestPermissionAlmacenamiento()) return;
            File directorioImagenes = new File(YouChatApplication.RUTA_IMAGENES_ENVIADAS);
            if (!directorioImagenes.exists())
                if (!directorioImagenes.mkdirs()) return;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
            Date date = new Date();
            String fechaEntera = sdf.format(date);

            ArrayList<ItemImg> itemImgs = new ArrayList<>();
            int l=result.size();
            for(int i=0; i<l; i++){
                String pp1=result.get(i).getPath();
                String nombre_img="img"+fechaEntera+""+i+".jpg";
                String r1 =YouChatApplication.RUTA_IMAGENES_ENVIADAS+nombre_img;
                try {
                    if(ImageLoader.init().comprimirImagen(pp1,r1,YouChatApplication.calidad))
                        itemImgs.add(new ItemImg(r1,pp1,YouChatApplication.calidad));
                } catch (FileNotFoundException e) {
                    Utils.ShowToastAnimated(mainActivity,"Error al cargar la imagen",R.raw.error);
                }
            }
            irAlEditor(itemImgs);
        });
    }

    public void irAlEditor(ArrayList<ItemImg> itemImgs) {
        if(itemImgs.size()==0) return;
        if(mAddFragmentListener!=null)
            mAddFragmentListener.onAddFragment(ChatsActivityCorreo.this, ViewImageActivity.newInstance(itemImgs,4));
    }

    public void enviarImagenes(final ArrayList<ItemImg> result) {
        if(result!=null){
            int l =result.size();
            for(int i=0; i<l; i++)
                enviarImagen(result.get(i).getRuta(), result.get(i).getTexto());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode == 1){ //camara

                ArrayList<ItemImg> itemImgs = new ArrayList<>();
                String miPath=cameraPhoto.getPhotoPath();

                try {
                    if(ImageLoader.init().comprimirImagen(miPath,miPath,100)){
                        itemImgs.add(new ItemImg(miPath,miPath,100));
                        irAlEditor(itemImgs);
                    }
                } catch (FileNotFoundException e) {
                    Utils.ShowToastAnimated(mainActivity,"Error al cargar la imagen",R.raw.error);
                }
            }
            else if(requestCode == 31){ //camara para android 7 en adelante
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                File f = new File(miPathCamera);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                context.sendBroadcast(mediaScanIntent);

                ArrayList<ItemImg> itemImgs = new ArrayList<>();


                try {
                    if(ImageLoader.init().comprimirImagen(miPathCamera,miPathCamera,100)){
                        itemImgs.add(new ItemImg(miPathCamera,miPathCamera,100));
                        irAlEditor(itemImgs);
                    }
                } catch (FileNotFoundException e) {
                    Utils.ShowToastAnimated(mainActivity,"Error al cargar la imagen",R.raw.error);
                }
            }
            else if(requestCode == 34){//contactos
                option_agregar_contacto.setVisibility(View.GONE);
                ItemContacto contacto = dbWorker.obtenerContacto(correo);
                if(contacto==null){
                    contacto = new ItemContacto(apodo,correo);
                }
                contacto.setUsaYouchat(true);
                dbWorker.insertarNuevoContacto(contacto);
                Utils.ShowToastAnimated(mainActivity,"Contacto añadido correctamente",R.raw.contact_check);
            }
            else if(requestCode == 70){//archivos

                if(data != null){
                    String rutaFile = data.getStringExtra(SimpleFileExplorerActivity.ON_ACTIVITY_RESULT_KEY);
                    if(rutaFile!=null && !rutaFile.equals("")){
                        File file = new File(rutaFile);
                        if(file.exists()){
                            if(file.length()<=22020096) enviarArchivo(rutaFile);
                            else Utils.ShowToastAnimated(mainActivity,"El archivo excede la capacidad soportada (20mb)",R.raw.contact_check);
                        }
                        else Utils.ShowToastAnimated(mainActivity,"El archivo no existe",R.raw.error);
                    }
                    else Utils.ShowToastAnimated(mainActivity,"Error al cargar el archivo",R.raw.error);
                }

//                if (data != null) {
//                    Uri dataFile = data.getData();
//                    String rutaFile = getPathFromUri(context, dataFile);
//                    if(rutaFile!=null && !rutaFile.equals("")){
//                        File file = new File(rutaFile);
//                        if(file.exists() && file.length()<=22020096) enviarArchivo(rutaFile);
//                        else Utils.ShowToastAnimated(mainActivity,"El archivo excede la capacidad soportada (20mb)",R.raw.contact_check);
//                    }
//                    else Utils.ShowToastAnimated(mainActivity,"Error al cargar el archivo",R.raw.error);
//                }
            }
        }
    }
    //////////////////////////////////////////////////////////////////////////
    public static String getPathFromUri(Context context, Uri uri) {

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

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;}

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());}

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());}

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());}
        //////////////////////////////////////////////////////////////////////////

//    public void EsperarParaElEditor(int tiempo, final ArrayList<ItemImg> itemImgs){
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                irAlEditor(itemImgs);
//            }
//        },tiempo);
//    }

    private void sacarTeclado(){
        text_send.setFocusableInTouchMode(true);
        text_send.requestFocus();
        InputMethodManager inputMethodManager= (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(text_send, InputMethodManager.SHOW_IMPLICIT);
    }

    private synchronized void updateToggleButtonState(boolean m) {
        if(quickAttachmentToggle!=null && buttonToggle!=null){
            if(m && quickAttachmentToggle.isShown()){
                buttonToggle.display(sendButton);
                quickAttachmentToggle.hide();
            }
            else if(!m && !quickAttachmentToggle.isShown()){
                buttonToggle.display(imageAudio);
                quickAttachmentToggle.show();
                quickAttachmentToggle.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onAttachmentDrawerStateChanged(QuickAttachmentDrawer.DrawerState drawerState) {
        if (drawerState == QuickAttachmentDrawer.DrawerState.COLLAPSED) {
            container.hideAttachedInput(true);
        }
    }

    @Override
    public void onImageCapture(@NonNull final byte[] imageBytes) {
        Bytes_A_Imagen(imageBytes);

    }
    public synchronized void Bytes_A_Imagen(byte[] ImgBytes)
    {
        Utils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                ArrayList<ItemImg> itemImgs = new ArrayList<>();
                if(permisos.requestPermissionAlmacenamiento()) {
                    File ruta = new File(YouChatApplication.RUTA_IMAGENES_ENVIADAS);
                    boolean existe = ruta.exists();
                    if(!existe) existe = ruta.mkdirs();
                    if(existe){
                        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                        String imageFileName = "img" + timeStamp + ".jpg";
                        String destino = YouChatApplication.RUTA_IMAGENES_ENVIADAS + imageFileName;

                        try {
                            FileOutputStream outputStream = new FileOutputStream(new File(YouChatApplication.RUTA_IMAGENES_ENVIADAS,imageFileName));//getApplicationContext().openFileOutput("foto.png",Context.MODE_PRIVATE);
                            outputStream.write(ImgBytes);
                            outputStream.flush();
                            outputStream.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            Utils.ShowToastAnimated(mainActivity, "Archivo no encontrado", R.raw.error);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Utils.ShowToastAnimated(mainActivity, "Ha ocurrido un error", R.raw.error);
                        }

                        try {
                            if (ImageLoader.init().comprimirImagen(destino, destino, 100)) {
                                itemImgs.add(new ItemImg(destino, destino, 100));
                                irAlEditor(itemImgs);
                            } else Utils.ShowToastAnimated(mainActivity, "Error al crear la imagen", R.raw.error);
                        } catch (FileNotFoundException e) {
                            Utils.ShowToastAnimated(mainActivity, "Error al cargar la imagen", R.raw.error);
                        }
                    }
                    else Utils.ShowToastAnimated(mainActivity,"No se pudo crear la ruta",R.raw.error);
                }
                else Utils.ShowToastAnimated(mainActivity,"Permiso de almacenamiento requerido",R.raw.error);
                quickAttachmentDrawer.hide(false);
            }
        });


        ///TODO*** DE IMAGE_BYTES A BITMAP
        //Bitmap bmp = BitmapFactory.decodeByteArray(ImgBytes, 0, ImgBytes.length);
        //CircleImageView image = findViewById(R.id.mini_img_perfil);
        //image.setImageBitmap(Bitmap.createScaledBitmap(bmp, image.getWidth(), image.getHeight(), false));
    }

    @Override
    public void onCameraFail() {
        Utils.ShowToastAnimated(mainActivity,"Cámara no disponible",R.raw.error);
        quickAttachmentDrawer.hide(false);
        quickAttachmentToggle.disable();
    }

    @Override
    public void onCameraStart() {}

    @Override
    public void onCameraStop() {}

    @Override
    public void onMediaSelected(@NonNull Uri uri, String contentType) {
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void handleEvent(int eventId, Object data1, Object data2) {

    }

    @Override
    public void onKeyboardShown() {

    }

    private class AttachButtonListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            handleAddAttachment();
        }
    }

    private class AttachButtonLongClickListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View v) {
            return sendButton.performLongClick();
        }
    }

    private void handleAddAttachment() {
        if (attachmentTypeSelector == null) {
            attachmentTypeSelector = new AttachmentTypeSelector(context, getActivity().getSupportLoaderManager(),null,0);
        }
        attachmentTypeSelector.show(mainActivity, attachButton);
    }

    private void initializeEnabledCheck() {
        //inputPanel.setEnabled(enabled);
        sendButton.setEnabled(true);
        attachButton.setEnabled(true);
    }

    private void initializeViews(View view) {
        positionViewPagerPreviewInicial = positionViewPagerPreviewActual = 0;
        transImage = view.findViewById(R.id.transImage);
        transBigImage = view.findViewById(R.id.transBigImage);
        previewParent = view.findViewById(R.id.previewParent);
        preview_viewPager = view.findViewById(R.id.preview_viewPager);
        locationOnScreen = new int[4];

        preview_viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                positionViewPagerPreviewActual = position;
//                positionViewPagerPreview = position;
//                int w = galleryView.getChildAt(position).getWidth();
//                int h = galleryView.getChildAt(position).getHeight();

//                String ruta = imagenesChatAlbumFile.get(position).getPath();
//                Bitmap bitmap = BitmapFactory.decodeFile(ruta);
//                int ancho = bitmap.getWidth();
//                int largo = bitmap.getHeight();
//                Glide.with(context).load(ruta)
//                        .override(ancho, largo).dontAnimate().into(transImage);
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        buttonToggle = view.findViewById(R.id.button_toggle);
        sendButton = view.findViewById(R.id.send_button);
        imageAudio = view.findViewById(R.id.imageAudio);
        attachButton = view.findViewById(R.id.attach_button);
        container = view.findViewById(R.id.layout_container);
        quickAttachmentDrawer = view.findViewById(R.id.quick_attachment_drawer);
        quickAttachmentToggle = view.findViewById(R.id.quick_attachment_toggle);
        ImageView quickCameraToggle = view.findViewById(R.id.quick_camera_toggle);
        container.addOnKeyboardShownListener(this);
        attachmentTypeSelector = null;
        SendButtonListener        sendButtonListener        = new SendButtonListener();
        attachButton.setOnClickListener(new AttachButtonListener());
        attachButton.setOnLongClickListener(new AttachButtonLongClickListener());
        sendButton.setOnClickListener(sendButtonListener);
//        sendButton.setEnabled(true);
        sendButton.addOnTransportChangedListener((newTransport, manuallySelected) -> {
          buttonToggle.getBackground().invalidateSelf();
        });
        if (QuickAttachmentDrawer.isDeviceSupported(context)) {
          quickAttachmentDrawer.setListener(this);
          quickCameraToggle.setOnClickListener(new QuickCameraToggleListener());
        } else {
          quickCameraToggle.setVisibility(View.GONE);
          quickCameraToggle.setEnabled(false);
        }

        String backgroundImagePath = Prefs.getBackgroundImagePath(context);
        if(!backgroundImagePath.isEmpty()) {
          Drawable image = Drawable.createFromPath(backgroundImagePath);
          getActivity().getWindow().setBackgroundDrawable(image);
        }
  }

    private class SendButtonListener implements OnClickListener, TextView.OnEditorActionListener {
        @Override
        public void onClick(View v) {
            enviarMensaje();
        }

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            
            return false;
        }
    }

    private class QuickCameraToggleListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            if (!quickAttachmentDrawer.isShowing()) {
                if(permisos.requestPermissionCamera())
                {
                    if(estaActivoBuscar) cancelarBuscar();
                    if(estaModoSeleccionar()) cancelarModoSeleccionar();
                    container.show(text_send, quickAttachmentDrawer);
                    quickAttachmentDrawer.onResume();
                }
                else Utils.ShowToastAnimated(mainActivity,"Permiso de camara requerido",R.raw.swipe_disabled);
            } else {
                container.hideAttachedInput(false);
            }
        }
    }

    public synchronized void borrarMensajeCorreo(String idMsg){
        dbWorker.eliminarMensajeCorreo(idMsg);
        boolean parar = false;
        for(int i=0; i<datos_chat.size(); i++){
            if(datos_chat.get(i).getId().equals(idMsg)){
                parar = true;
                datos_chat.remove(i);
                if(!estaActivoBuscar) adaptadorChat.notifyItemRemoved(i);
            }
            else if(parar) break;
        }
        if (YouChatApplication.bandejaFragment != null) {
            Utils.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    if(YouChatApplication.posVistaBandeja==1)
                        YouChatApplication.bandejaFragment
                                .cargarUsuarios();
                }
            });
        }
    }
    public synchronized void ActualizarEstadoRecibido(String id){
        int l = datos_chat.size();
        boolean parar = false;
        for(int i=0; i<l; i++){
            if(datos_chat.get(i).getId().equals(id)){
                parar = true;
                datos_chat.get(i).setEstado(ItemChat.ESTADO_RECIBIDO);
                if(!estaActivoBuscar) adaptadorChat.notifyItemChanged(i, 7);
            }
            else if(parar) break;
        }
    }

    public synchronized void convertirActualizarChatMsgRecibido(ItemMensajeCorreo mensajeCorreo,
                                                                ArrayList<ItemAdjuntoCorreo> adjuntoCorreos){
        ArrayList<ItemChat> nuevosChat = new ArrayList<>();
        if(!mensajeCorreo.getTexto().trim().isEmpty()){
            ItemChat newChat=new ItemChat( mensajeCorreo.getId(),
                    mensajeCorreo.isEsMio()?2:1, mensajeCorreo.getEstado(), correo,
                    mensajeCorreo.getTexto(),
                    "",
                    mensajeCorreo.getHora(), mensajeCorreo.getFecha(), "",
                    mensajeCorreo.getRemitente(),
                    false, mensajeCorreo.getOrden(),false,
                    mensajeCorreo.getId(),  mensajeCorreo.getPeso(), true);
            nuevosChat.add(newChat);
        }
        int lA = adjuntoCorreos.size();
        for(int j=0; j<lA; j++){
            ItemAdjuntoCorreo adj = adjuntoCorreos.get(j);
            int tipo;
            if(adj.esImagen()) tipo = mensajeCorreo.isEsMio()?4:3;
            else if(adj.esAudio()) tipo = mensajeCorreo.isEsMio()?8:7;
            else if(adj.esSticker()) tipo = mensajeCorreo.isEsMio()?20:19;
            else tipo = mensajeCorreo.isEsMio()?14:13;

            if(mensajeCorreo.isEsMio()){
                ItemChat newChat=new ItemChat(mensajeCorreo.getId(),
                        tipo, mensajeCorreo.getEstado(), correo,
                        "",
                        adj.getNombre(),
                        mensajeCorreo.getHora(), mensajeCorreo.getFecha(), "",
                        mensajeCorreo.getRemitente(),
                        false, mensajeCorreo.getOrden(),false,
                        adj.getId(),  adj.getPeso(), true);
                nuevosChat.add(newChat);
            }
            else {
                if(new File(YouChatApplication.RUTA_ADJUNTOS_CORREO+adj.getNombre()).exists()){
                    ItemChat newChat=new ItemChat(mensajeCorreo.getId(),
                            tipo, mensajeCorreo.getEstado(), correo,
                            "",
                            adj.getNombre(),
                            mensajeCorreo.getHora(), mensajeCorreo.getFecha(), "",
                            mensajeCorreo.getRemitente(),
                            false, mensajeCorreo.getOrden(),false,
                            adj.getId(),  adj.getPeso(), true);
                    nuevosChat.add(newChat);
                }
                else if(adj.getPosicion()!=-1){
                    ItemChat newChat=new ItemChat(mensajeCorreo.getId(),
                            tipo, mensajeCorreo.getEstado(), correo,
                            "",
                            adj.getNombre(),
                            mensajeCorreo.getHora(), mensajeCorreo.getFecha(), "",
                            mensajeCorreo.getRemitente(),
                            false, mensajeCorreo.getOrden(),false,
                            adj.getId(),  adj.getPeso(), false);
                    nuevosChat.add(newChat);
                }
                else {
                    ItemChat newChat=new ItemChat(mensajeCorreo.getId(),
                            tipo, mensajeCorreo.getEstado(), correo,
                            "",
                            adj.getNombre(),
                            mensajeCorreo.getHora(), mensajeCorreo.getFecha(), "",
                            mensajeCorreo.getRemitente(),
                            false, mensajeCorreo.getOrden(),false,
                            adj.getId(),  adj.getPeso(), true);
                    nuevosChat.add(newChat);
                }

            }
        }

        int longiNC = nuevosChat.size();
        if(longiNC>0){
            for(int i=0; i<longiNC; i++)
                ActualizarChatMsgRecibido(nuevosChat.get(i));
        }
    }
    public synchronized void ActualizarChatMsgRecibido(ItemChat msg){
        boolean notifica=false;
        if((linearLayoutManager.findFirstVisibleItemPosition()>8
        && cant_Msg_No_Vistos==0) || cant_Msg_No_Vistos>0)
            notifica=true;

        if(!notifica)
            adaptadorChat.hacerAnim();
        datos_chat.add(0,msg);
        if(!estaActivoBuscar) adaptadorChat.notifyItemInserted(0);
//        if(!estaActivoBuscar) adaptadorChat.notifyItemInserted(0);

        Utils.reproducirSonido(R.raw.sound_out, context);

        if(cant_Msg_No_Vistos>0) actualizarCantMngNoVistos(1);
        else if(notifica) crearCantMngNoVistos(1);
        else lista_chat.scrollToPosition(0);

        if(msg.esImagen()) obtenerTodasLasImagenesYEnviarMensajesNoEnviados();
    }

    private synchronized void crearCantMngNoVistos(int size){
        cant_Msg_No_Vistos=size;
        if(cant_Msg_No_Vistos>0){
            if(cant_Msg_No_Vistos>datos_chat.size())
                cant_Msg_No_Vistos=datos_chat.size();
            String msg="";
            if(cant_Msg_No_Vistos==1) msg="1 mensaje no visto";
            else msg=cant_Msg_No_Vistos+" mensajes no visto";
            datos_chat.add(cant_Msg_No_Vistos, new ItemChat("",99, 0, "",
                    msg,"","",datos_chat.get(cant_Msg_No_Vistos-1).getFecha(),"",
                    "", false, "",false, "",0,true));
            if(!estaActivoBuscar) adaptadorChat.notifyItemInserted(cant_Msg_No_Vistos);

            int cant=cant_Msg_No_Vistos;
            if(cant>99) fab_bottom_chat_cant_msg_nvisto.setText("99");
            else fab_bottom_chat_cant_msg_nvisto.setText(""+cant);
            fab_bottom_chat.show();
            if(fab_bottom_chat_cant_msg_nvisto.getVisibility()==View.GONE){
                if(YouChatApplication.animaciones_chat){
                    Animation anim= AnimationUtils.loadAnimation(context,R.anim.fade_in_fast);
                    fab_bottom_chat_cant_msg_nvisto.startAnimation(anim);
                }
                fab_bottom_chat_cant_msg_nvisto.setVisibility(View.VISIBLE);
            }
        }
    }
    private synchronized void actualizarCantMngNoVistos(int size) {
        cant_Msg_No_Vistos+=size;

        if(cant_Msg_No_Vistos>datos_chat.size())
            cant_Msg_No_Vistos=datos_chat.size();

        int cant=cant_Msg_No_Vistos;
        if(cant>99) fab_bottom_chat_cant_msg_nvisto.setText("99");
        else fab_bottom_chat_cant_msg_nvisto.setText(""+cant);
        fab_bottom_chat.show();
        if(fab_bottom_chat_cant_msg_nvisto.getVisibility()==View.GONE){
            if(YouChatApplication.animaciones_chat){
                Animation anim= AnimationUtils.loadAnimation(context,R.anim.fade_in_fast);
                fab_bottom_chat_cant_msg_nvisto.startAnimation(anim);
            }
            fab_bottom_chat_cant_msg_nvisto.setVisibility(View.VISIBLE);
        }

        for(int i=0; i<datos_chat.size(); i++)
            if(datos_chat.get(i).getTipo_mensaje()==99){
                String msg="";
                if(cant_Msg_No_Vistos==1) msg="1 mensaje no visto";
                else msg=cant_Msg_No_Vistos+" mensajes no visto";
                datos_chat.get(i).setMensaje(msg);
                if(!estaActivoBuscar) adaptadorChat.notifyItemChanged(i, 7);
                return;
            }
    }
    private ItemChat buscarUltMsgAPartirDe(int ini) {
        int l=datos_chat.size();
        for(int i=ini; i<l; i++)
            if(datos_chat.get(i).getTipo_mensaje()!=0 && datos_chat.get(i).getTipo_mensaje()!=99)
                return datos_chat.get(i);
        return null;
    }
    public void responderMsg(){
        if(option.getTipo_mensaje()==81 || option.getTipo_mensaje()==83){
            Utils.ShowToastAnimated(mainActivity,"Acción no soportada para este mensaje",R.raw.error);
            return;
        }
        if(estaActivoBuscar || ll_buscar.getVisibility()==View.VISIBLE) cancelarBuscar();
        if(estaModoSeleccionar()) cancelarModoSeleccionar();
        ll_answer.setVisibility(View.VISIBLE);

        if(option.getTipo_mensaje()%2==1)
            nombre_answer.setText(apodo);
        else nombre_answer.setText("Yo");
        id_msg_answer = option.getId();
        tipo_msg_answer = option.getTipo_mensaje();

        if(option.esEstadoRespondido()) texto_answer.setText("Estado: "+option.getMensaje());
        else if(option.esTarjeta()) texto_answer.setText("Tarjeta: "+option.getMensaje());
        else if(option.esArchivo()) texto_answer.setText("Archivo");
        else if(option.esContacto()) texto_answer.setText("Contacto: "+option.getMensaje());
        else if(option.esAudio()) texto_answer.setText("Audio "+option.getMensaje());
        else if(option.esSticker()) texto_answer.setText("Sticker");
        else if(option.esTema()) texto_answer.setText("Tema");
        else if(option.esImagen()){
            img_answer_chat.setVisibility(View.VISIBLE);
            String ruta_img_answer = option.getRuta_Dato();
            Glide.with(context)
                    .load(ruta_img_answer)
                    .error(R.drawable.image_placeholder)
                    .into(img_answer_chat);
            if(tipo_msg_answer==3 || tipo_msg_answer==4)
                texto_answer.setText("Imagen");
            else texto_answer.setText(option.getMensaje());
        }
        else texto_answer.setText(option.getMensaje());

        if(img_answer_chat.getVisibility()==View.VISIBLE && !option.esImagen()){
            img_answer_chat.setVisibility(View.GONE);
            img_answer_chat.setImageResource(R.drawable.image_placeholder);
        }
        sacarTeclado();
    }

    public boolean elPuedeSwipear(View view){
        if(view!=null){
            ItemChat chat = null;
            int pos = lista_chat.getChildAdapterPosition(view);

            if(estaActivoBuscar && pos!=-1) chat = datos_chat_buscar.get(pos);
            else if(pos!=-1) chat = datos_chat.get(pos);

            if(chat!=null) return chat.puedeSwipear();
            return false;
        }
        return false;
    }

    public boolean elPuedeSwipear(int pos){
        ItemChat chat = null;

        if(estaActivoBuscar && pos!=-1) chat = datos_chat_buscar.get(pos);
        else if(pos!=-1) chat = datos_chat.get(pos);

        if(chat!=null) return chat.puedeSwipear();
        return false;
    }
    public void responderMsg(View v){
        if(v!=null){
            int pos = lista_chat.getChildAdapterPosition(v);
            if(pos!=-1){
                if(estaActivoBuscar) option = datos_chat_buscar.get(pos);
                else option = datos_chat.get(pos);
                ll_answer.setVisibility(View.VISIBLE);

                if(option.getTipo_mensaje()%2==1)
                    nombre_answer.setText(apodo);
                else nombre_answer.setText("Yo");
                id_msg_answer = option.getId();
                tipo_msg_answer = option.getTipo_mensaje();

                if(option.esEstadoRespondido()) texto_answer.setText("Estado: "+option.getMensaje());
                else if(option.esTarjeta()) texto_answer.setText("Tarjeta: "+option.getMensaje());
                else if(option.esArchivo()) texto_answer.setText("Archivo");
                else if(option.esContacto()) texto_answer.setText("Contacto: "+option.getMensaje());
                else if(option.esAudio()) texto_answer.setText("Audio "+option.getMensaje());
                else if(option.esSticker()) texto_answer.setText("Sticker");
                else if(option.esTema()) texto_answer.setText("Tema");
                else if(option.esImagen()){
                    img_answer_chat.setVisibility(View.VISIBLE);
                    String ruta_img_answer = option.getRuta_Dato();
                    Glide.with(context)
                            .load(ruta_img_answer)
                            .error(R.drawable.image_placeholder)
                            .into(img_answer_chat);
                    if(tipo_msg_answer==3 || tipo_msg_answer==4)
                        texto_answer.setText("Imagen");
                    else texto_answer.setText(option.getMensaje());
                }
                else texto_answer.setText(option.getMensaje());

                if(img_answer_chat.getVisibility()==View.VISIBLE && !option.esImagen()){
                    img_answer_chat.setVisibility(View.GONE);
                    img_answer_chat.setImageResource(R.drawable.image_placeholder);
                }
                sacarTeclado();
            }
        }
    }

    public void reenviarMsg(){
        if(option.getTipo_mensaje()==81 || option.getTipo_mensaje()==83){
            Utils.ShowToastAnimated(mainActivity,"Acción no soportada para este mensaje",R.raw.error);
            return;
        }
        if(estaActivoBuscar || ll_buscar.getVisibility()==View.VISIBLE) cancelarBuscar();
        if(estaModoSeleccionar()) cancelarModoSeleccionar();
        if(mAddFragmentListener!=null) mAddFragmentListener.onAddFragment(ChatsActivityCorreo.this, ReenviarActivity.newInstance(option,true));
//        navController.navigate(R.id.reenviarActivity,bundle);
    }

    public void guardarImagen() {
        if(option!=null){
            boolean seGuardo = Utils.guardarEnGaleria(option.getRuta_Dato());
            if(seGuardo) cu.alexgi.youchat.Utils.ShowToastAnimated(mainActivity,"Imagen guardada con éxito", R.raw.contact_check);
            else cu.alexgi.youchat.Utils.ShowToastAnimated(mainActivity,"Error al guardar la imagen", R.raw.error);
        }
    }

    public void copiarMsg(){
        if(option.getTipo_mensaje()==81 || option.getTipo_mensaje()==83){
            Utils.ShowToastAnimated(mainActivity,"Acción no soportada para este mensaje",R.raw.error);
            return;
        }
        if(estaActivoBuscar || ll_buscar.getVisibility()==View.VISIBLE) cancelarBuscar();
        if(estaModoSeleccionar()) cancelarModoSeleccionar();

        ClipData c = ClipData.newPlainText("YouChatCopy", option.getMensaje());
        YouChatApplication.clipboard.setPrimaryClip(c);
        Utils.ShowToastAnimated(mainActivity,"Texto copiado al portapapeles",R.raw.voip_invite);
    }

    public void reintentarEnviarMsg(){
        if(estaActivoBuscar || ll_buscar.getVisibility()==View.VISIBLE) cancelarBuscar();
        if(estaModoSeleccionar()) cancelarModoSeleccionar();
        reintentarEnviarMensajeCorreo(option.getId());
    }

    //TODO: cambiar procesa a igual como esta en SendMsg
    private synchronized void reintentarEnviarMensajeCorreo(String id){
        if(YouChatApplication.estaAndandoChatService() && YouChatApplication.chatService.hayConex){
            int l = datos_chat.size();
            boolean parar = false;
            for(int i=0; i<l; i++){
                if(datos_chat.get(i).getId().equals(id)){
                    parar = true;
                    datos_chat.get(i).setEstado(ItemChat.ESTADO_ESPERANDO);
                    adaptadorChat.notifyItemChanged(i, 7);

                }
                else if(parar) break;
            }

            Message message = new MimeMessage(YouChatApplication.chatService.getSession());
            int tamMsg = 0, tt = 0;
            try {
                boolean esSimple = true;
                String alias = YouChatApplication.alias;
                alias = alias.replace("<","").replace(">","");
                InternetAddress from;
                if(!alias.isEmpty()){
                    try {
                        from = new InternetAddress(aut_user, alias);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        from = new InternetAddress(aut_user);
                    }
                }
                else from = new InternetAddress(aut_user);

                String[] direcciones_string = correo.split(",");
                int lA=direcciones_string.length;
                if(lA>1){
                    boolean esNauta = aut_user.endsWith("@nauta.cu"), huboCambio = false;
                    int cont = 0;
                    InternetAddress[] direcciones = new InternetAddress[l];
                    for(int i=0; i<lA; i++){
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

                message.setSubject("");

                String texto = "";
                ItemMensajeCorreo mensajeCorreo = dbWorker.obtenerMensajeCorreo(id);
                if(!mensajeCorreo.getTexto().trim().isEmpty()){
                    texto = mensajeCorreo.getTexto().trim();
                }

                ArrayList<MimeBodyPart> adjuntos = new ArrayList<>();
                ArrayList<ItemAdjuntoCorreo> adjuntoCorreos = dbWorker.obtenerAdjuntosCorreoDe(id);
                lA = adjuntoCorreos.size();
                for(int j=0; j<lA; j++){
                    ItemAdjuntoCorreo adj = adjuntoCorreos.get(j);
                    File file=new File(adj.getNombre());
                    if(file.exists()){
                        esSimple = false;
                        MimeBodyPart adjunto= new MimeBodyPart();
                        adjunto.setDataHandler(new DataHandler(new FileDataSource(file)));
                        String nombre = file.getName();
                        if(adj.esSticker()){
                            String[] lll = adj.getNombre().split(File.separator);
                            nombre = nombre+"<sp>"+lll[lll.length-2]+".tgs";
                        }
                        adjunto.setFileName(nombre);
                        adjunto.setDisposition(Part.ATTACHMENT);

                        adjuntos.add(adjunto);
                    }
                }

                if(esSimple){
                    if(YouChatApplication.addPieFirmaAChat){
                        String cad = texto;
                        if(!YouChatApplication.pieDeFirma.isEmpty())
                            cad+="\n\n"+YouChatApplication.pieDeFirma;
                        message.setText(cad);
                        tamMsg+=cad.length();
                    }
                    else {
                        message.setText(texto);
                        tamMsg+=texto.length();
                    }
                }else {
                    BodyPart textoP = new MimeBodyPart();
                    textoP.setText(texto);
                    tamMsg+=texto.length();

                    MimeMultipart multiParte = new MimeMultipart();
                    multiParte.addBodyPart(textoP);

                    int lAdj = adjuntos.size();
                    for(int i=0; i<lAdj; i++){
                        tt+=(int)adjuntos.get(i).getSize();
                        multiParte.addBodyPart(adjuntos.get(i));
                    }

                    message.setContent(multiParte);
                }
                tamMsg = (tamMsg*8)+tt;
            } catch (MessagingException e) {
                e.printStackTrace();
                message=null;
            }
            if(message!=null){
                ItemChat temp = new ItemChat(correo,"");
                temp.setId(id);
                YouChatApplication.chatService.enviarMensajePersonalizado(temp,
                        SendMsg.CATEGORY_CHAT_CORREO_PERSONALIZADO,
                        message,tamMsg);
            }
        }
        else Utils.mostrarToastDeConexion(mainActivity);
    }

    public void eliminarMsg(){
        if(estaActivoBuscar || ll_buscar.getVisibility()==View.VISIBLE) cancelarBuscar();
        if(estaModoSeleccionar()) cancelarModoSeleccionar();
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(1);
        View mview=getLayoutInflater().inflate(R.layout.dialog_confirm,null);
        dialog.setContentView(mview);

        TextView text_icono = mview.findViewById(R.id.text_icono);
        TextView text_eliminar = mview.findViewById(R.id.text_eliminar);
        View btn_ok=mview.findViewById(R.id.btn_ok);
        View btn_cancel=mview.findViewById(R.id.btn_cancel);

        text_icono.setText("Eliminar mensaje");
        text_eliminar.setText("¿Deseas eliminar este mensaje?");

        btn_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                borrarMensajeCorreo(option.getId());
            }
        });
        btn_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCancelable(true);
        dialog.show();
    }

    public synchronized void cambiarEstadoMensaje(String idMsg, boolean envioCorrecto){
        int l=datos_chat.size();
        boolean cerrar = false;
        for(int i=0; i<datos_chat.size(); i++){
            if(datos_chat.get(i).getId().equals(idMsg)){
                if(envioCorrecto) datos_chat.get(i).setEstado(ItemChat.ESTADO_ENVIADO);
                else datos_chat.get(i).setEstado(ItemChat.ESTADO_ERROR);
                if(!estaActivoBuscar) adaptadorChat.notifyItemChanged(i, 7);
                cerrar=true;
            }
            else if(cerrar) break;
        }
    }

    public String getCorreo(){
        if(YouChatApplication.chatsActivityCorreo==null)
            return "";
        return correo;
    }

    //////////////////////////archivos//////////////////////////////////////
    public void buscarArchivo() {
        if(!permisos.requestPermissionAlmacenamiento()) return;

        Intent intent = new Intent(context, SimpleFileExplorerActivity.class);
        startActivityForResult(intent, 70);


//        Intent intent = new Intent();
//        intent.setType("*/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Selecciona un archivo"), 69);
    }

    public void abrirArchivoEn(String rutaArchivo){
        if(!permisos.requestPermissionAlmacenamiento()) return;
        File file = new File(rutaArchivo);
        if(file.exists()){
            String ext= Utils.obtenerExtension(file.getName());
            String tipo;
            if(SimpleFileExplorerFragment.AUDIO.contains(ext)) tipo = "audio/*";
            else if(SimpleFileExplorerFragment.APK.contains(ext)) tipo = "application/vnd.android.package-archive";
            else if(SimpleFileExplorerFragment.VIDEO.contains(ext)) tipo = "video/*";
            else if(SimpleFileExplorerFragment.IMAGEN.contains(ext)) tipo = "image/*";
            else if(SimpleFileExplorerFragment.TXT.contains(ext)) tipo = "text/*";
            else if(SimpleFileExplorerFragment.GIF.contains(ext)) tipo = "image/gif";
            else if(SimpleFileExplorerFragment.COMPRESS.contains(ext)) tipo = "*/*";
            else if(SimpleFileExplorerFragment.XML.contains(ext)) tipo = "text/html";
            else if(SimpleFileExplorerFragment.PDF.contains(ext)) tipo = "application/pdf";
            else tipo = "*/*";

            if(tipo.equals("text/html")){
                if(mAddFragmentListener!=null)
                    mAddFragmentListener.onAddFragment(ChatsActivityCorreo.this, Web_view_fragment.newInstance(rutaArchivo));
            }
            else if(tipo.equals("image/*") || tipo.equals("image/gif")){
                if(mAddFragmentListener!=null)
                    mAddFragmentListener.onAddFragment(ChatsActivityCorreo.this,
                            ImagePager.newInstance(rutaArchivo));
            }
            else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                    Uri uri = FileProvider.getUriForFile(context,
                            "cu.alexgi.youchat.fileprovider",file);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setDataAndType(uri, tipo);
                    startActivity(intent);
                }
                else {
                    Uri uri = Uri.fromFile(file);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, tipo);
                    startActivity(intent);
                }
            }


//            ext = rutaArchivo.substring(rutaArchivo.lastIndexOf(".")+1);
//            if(ext!=null){
//                tipo = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
//                if(tipo!=null) tipo = "*/*";
//            }
//            else tipo = "*/*";
//            Log.e("FILE", "EXT: "+ext+"/ TIPO: "+tipo);
        }
    }
    //////////////////contactos
    private void adicionarContactoAlTelefono(ItemContacto contacto){
        Intent intent = new Intent(Intent.ACTION_INSERT_OR_EDIT);
        intent.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
        String nombreC =  contacto.getNombreMostrar();
        String correoC = contacto.getCorreo();
        String telefono = contacto.getTelefono();
        intent.putExtra(ContactsContract.Intents.Insert.NAME, nombreC);
        if(!telefono.equals(""))
            intent.putExtra(ContactsContract.Intents.Insert.PHONE, telefono);
        intent.putExtra(ContactsContract.Intents.Insert.EMAIL, correoC);
        intent.putExtra("finishActivityOnSaveCompleted", true);
        startActivityForResult(intent, 34);
    }

    ///////////////////////////descargas
    public synchronized void descargarCorreo(ItemChat chat, DownloadProgressView downloadProgressView){
        String idChat = chat.getId();
        if(!YouChatApplication.chatService.hayConex){
            Utils.mostrarToastDeConexion(mainActivity);
            downloadProgressView.setDownloading(false);
            downloadProgressView.ponerClick();
        }
        else{
            for(int i=0; i<datos_chat.size(); i++){
                if(datos_chat.get(i).getId().equals(idChat)
                        && datos_chat.get(i).getRuta_Dato().equals(chat.getRuta_Dato())){
//                    datos_chat.get(i).setEsReenviado(true);
//                    adaptadorChat.notifyItemChanged(i,7);
                    if(YouChatApplication.estaAndandoChatService()){
                        if(!YouChatApplication.chatService.estaDescargandoCorreo(idChat)){
                            YouChatApplication.chatService.descargarMensajeCorreo(idChat,chat.getId_mensaje(),downloadProgressView);
                        }
                        else Utils.ShowToastAnimated(mainActivity,"Ya se está descargando este mensaje",R.raw.error);
                    }
                    break;
                }
            }
        }
    }

    public synchronized void ActualizarMsgDescargado(String idChat, String idAdj) {
        for(int i=0; i<datos_chat.size(); i++){
            if(datos_chat.get(i).getId().equals(idChat)
                    && datos_chat.get(i).getId_mensaje().equals(idAdj)){
                datos_chat.get(i).setDescargado(true);
                adaptadorChat.notifyItemChanged(i,7);
                if(datos_chat.get(i).esImagen())
                    obtenerTodasLasImagenesYEnviarMensajesNoEnviados();
//                ItemMensajeCorreo mensajeCorreo = dbWorker.obtenerMensajeCorreo(idChat);
//                ItemAdjuntoCorreo adj = dbWorker.obtenerAdjuntosCorreo(idAdj);
//                if(adj!=null && mensajeCorreo!=null){
//                    int tipo;
//                    if(adj.esImagen()) tipo = 3;
//                    else if(adj.esAudio()) tipo = 7;
//                    else if(adj.esSticker()) tipo = 19;
//                    else tipo = 13;
//                    ItemChat newChat;
//                    if(new File(YouChatApplication.RUTA_ADJUNTOS_CORREO+adj.getNombre()).exists()){
//                        newChat=new ItemChat(mensajeCorreo.getId(),
//                                tipo, mensajeCorreo.getEstado(), correo,
//                                "",
//                                adj.getNombre(),
//                                mensajeCorreo.getHora(), mensajeCorreo.getFecha(), "",
//                                mensajeCorreo.getRemitente(),
//                                false, mensajeCorreo.getOrden(),false,
//                                adj.getId(),adj.getPeso(),true);
//                    }
//                    else if(adj.getPosicion()!=-1){
//                        newChat=new ItemChat(mensajeCorreo.getId(),
//                                tipo, mensajeCorreo.getEstado(), correo,
//                                "",
//                                adj.getNombre(),
//                                mensajeCorreo.getHora(), mensajeCorreo.getFecha(), "",
//                                mensajeCorreo.getRemitente(),
//                                false, mensajeCorreo.getOrden(),false,
//                                adj.getId(),adj.getPeso(),false);
//
////                        String tamString = "Tamaño desconocido";
////                        if(adj.getPeso()>0) tamString = Utils.convertirBytes(adj.getPeso());
//
////                        newChat = new ItemChat(correo, "");
////                        newChat.setId(mensajeCorreo.getId());
////                        newChat.setTipo_mensaje(81);
////                        newChat.setHora(mensajeCorreo.getHora());
////                        newChat.setFecha(mensajeCorreo.getFecha());
////                        newChat.setOrden(mensajeCorreo.getOrden());
////                        newChat.setEstado(ItemChat.ESTADO_VISTO);
////
////                        if (adj.esAudio())
////                            newChat.setId_msg_resp(tamString + "/audio");
////                        else if (adj.esImagen())
////                            newChat.setId_msg_resp(tamString + "/imagen");
////                        else if (adj.esSticker())
////                            newChat.setId_msg_resp(tamString + "/sticker");
////                        else newChat.setId_msg_resp(tamString + "/archivo");
////
////                        newChat.setRuta_Dato(adj.getId());
////
////                        newChat.setId_mensaje(adj.getId());
////                        newChat.setPeso(adj.getPeso());
////                        newChat.setDescargado(false);
//                    }
//                    else {
//                        newChat=new ItemChat(mensajeCorreo.getId(),
//                                tipo, mensajeCorreo.getEstado(), correo,
//                                "",
//                                adj.getNombre(),
//                                mensajeCorreo.getHora(), mensajeCorreo.getFecha(), "",
//                                mensajeCorreo.getRemitente(),
//                                false, mensajeCorreo.getOrden(),false,
//                                adj.getId(),adj.getPeso(),true);
//                    }
//                    datos_chat.remove(i);
//                    if(!estaActivoBuscar) adaptadorChat.notifyItemRemoved(i);
//                    datos_chat.add(i,newChat);
//                    if(!estaActivoBuscar) adaptadorChat.notifyItemInserted(i);
//                    if(adj.esImagen())
//                        obtenerTodasLasImagenesYEnviarMensajesNoEnviados();
//                }
//                else {
//                    datos_chat.get(i).setEsReenviado(false);
//                    adaptadorChat.notifyItemChanged(i,7);
//                }
//                break;
            }
        }
    }

    public synchronized void descargaFallida(String idChat, String idAdj, boolean esFallida){
        if(esFallida) Utils.ShowToastAnimated(mainActivity,"Error al intentar descargar",R.raw.error);
        else Utils.ShowToastAnimated(mainActivity,"Mensaje no encontrado",R.raw.error);
        for(int i=0; i<datos_chat.size(); i++){
            if(datos_chat.get(i).getId().equals(idChat)
                    && datos_chat.get(i).getRuta_Dato().equals(idAdj)){
                datos_chat.get(i).setEsReenviado(false);
                adaptadorChat.notifyItemChanged(i,7);
                break;
            }
        }
    }

    ///modo seleccionar
    private void inicializarComponentesDeModoSeleccionar() {
        chatSeleccionados = new ArrayList<>();
        iv_cancelar_selec_chat.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelarModoSeleccionar();
            }
        });
        iv_reintentar_selec_chat.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int l = chatSeleccionados.size();
                for(int i=0; i<l; i++){
                    ItemChat temp = chatSeleccionados.get(i);
                    if(temp.hayQReintentarEnviar()){
                        reintentarEnviarMensajeCorreo(temp.getId());
                    }
                }
                cancelarModoSeleccionar();
            }
        });
        iv_copiar_selec_chat.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int l = chatSeleccionados.size();
                String cad = "";
                for(int i=0; i<l; i++){
                    ItemChat temp = chatSeleccionados.get(i);
                    if(temp.esMsgTexto() || temp.esImagen() || temp.esTarjeta()){
                        if(!temp.getMensaje().equals("")){
                            if(!cad.equals(""))
                                cad += "\n----------\n";
                            cad += temp.getMensaje();
                        }
                    }
                }
                if(!cad.equals("")){
                    ClipData c = ClipData.newPlainText("YouChatCopy", cad);
                    YouChatApplication.clipboard.setPrimaryClip(c);
                    Utils.ShowToastAnimated(mainActivity,"Texto copiado al portapapeles",R.raw.voip_invite);
                } else Utils.ShowToastAnimated(mainActivity,"No existe texto para copiar",R.raw.error);
                cancelarModoSeleccionar();
            }
        });
        iv_responder_selec_chat.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                option = chatSeleccionados.get(0);
                responderMsg();
//                cancelarModoSeleccionar();
            }
        });
        iv_reenviar_selec_chat.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int l = chatSeleccionados.size();
                ArrayList<ItemChat> datos_chat_reenviar = new ArrayList<>();
                for(int i=0; i<l; i++){
                    ItemChat temp = chatSeleccionados.get(i);
                    if(!temp.hayQReintentarEnviar()){
                        datos_chat_reenviar.add(temp);
                    }
                }
                if(mAddFragmentListener!=null) mAddFragmentListener.onAddFragment(ChatsActivityCorreo.this, ReenviarActivity.newInstance(datos_chat_reenviar,true));
//                navController.navigate(R.id.reenviarActivity, bundle);
                cancelarModoSeleccionar();
            }
        });
        iv_borrar_selec_chat.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(estaActivoBuscar || ll_buscar.getVisibility()==View.VISIBLE) cancelarBuscar();
                if(estaModoSeleccionar()) cancelarModoSeleccionar();
                Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(1);
                View mview=getLayoutInflater().inflate(R.layout.dialog_confirm,null);
                dialog.setContentView(mview);

                TextView text_icono = mview.findViewById(R.id.text_icono);
                TextView text_eliminar = mview.findViewById(R.id.text_eliminar);
                View btn_ok=mview.findViewById(R.id.btn_ok);
                View btn_cancel=mview.findViewById(R.id.btn_cancel);

                text_icono.setText("Eliminar mensaje");
                if(chatSeleccionados.size()==1)
                    text_eliminar.setText("¿Deseas eliminar este mensaje?");
                else text_eliminar.setText("¿Deseas eliminar estos "+chatSeleccionados.size()+" mensajes?");

                btn_ok.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        int l = chatSeleccionados.size();
                        for(int i=0; i<l; i++){
                            borrarMensajeCorreo(chatSeleccionados.get(i).getId());
                        }
                        cancelarModoSeleccionar();
                    }
                });
                btn_cancel.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        cancelarModoSeleccionar();
                    }
                });

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                dialog.setCancelable(false);
                dialog.show();
            }
        });
    }

    public boolean estaModoSeleccionar(){
        if(ll_modo_seleccionar_chat.getVisibility()==View.VISIBLE)
            return true;
        return false;
    }

    public synchronized void activarModoSeleccionar(){
        if(estaActivoBuscar) cancelarBuscar();
        if(adaptadorChat.estaPlayAudio()) adaptadorChat.detenerPlayAudio();
        ll_modo_seleccionar_chat.setVisibility(View.VISIBLE);
        ll_contacto.setVisibility(View.GONE);
        chatSeleccionados = new ArrayList<>();
        tv_cant_selec_chat.setText(""+chatSeleccionados.size());
        int l = datos_chat.size();
        for(int i=0; i<l; i++){
            datos_chat.get(i).setSeleccionado(false);
        }
        adaptadorChat.setModoSeleccion(true);
        adaptadorChat.notifyDataSetChanged();
    }

    public synchronized void cancelarModoSeleccionar(){
        ll_modo_seleccionar_chat.setVisibility(View.GONE);
        ll_contacto.setVisibility(View.VISIBLE);
        tv_cant_selec_chat.setText("0");
        adaptadorChat.setModoSeleccion(false);
        adaptadorChat.notifyDataSetChanged();
    }
    public boolean verificarSiEstaSeleccionado(String idChat){
        int l = chatSeleccionados.size();
        for(int i=0; i<l; i++){
            if(chatSeleccionados.get(i).getId().equals(idChat))
                return true;
        }
        return false;
    }
    public synchronized void actualizarSeleccion(ItemChat chat){
        if(chat.estaSeleccionado()){
            int l = chatSeleccionados.size();
            for(int i=0; i<l; i++){
                if(chatSeleccionados.get(i).getId().equals(chat.getId())){
                    chatSeleccionados.remove(i);
                    break;
                }
            }
            String id = chat.getId();
            l = datos_chat.size();
            boolean parar = false;
            for(int i=0; i<l; i++){
                if(datos_chat.get(i).getId().equals(id)){
                    parar = true;
                    datos_chat.get(i).setSeleccionado(false);
                    adaptadorChat.notifyItemChanged(i,7);
                }
                else if(parar) break;
            }
        }
        else {
            chatSeleccionados.add(chat);
            String id = chat.getId();
            int l = datos_chat.size();
            boolean parar = false;
            for(int i=0; i<l; i++){
                if(datos_chat.get(i).getId().equals(id)){
                    parar = true;
                    datos_chat.get(i).setSeleccionado(true);
                    adaptadorChat.notifyItemChanged(i,7);
                }
                else if(parar) break;
            }
        }

        int l=chatSeleccionados.size();
        anim = AnimationUtils.loadAnimation(context,R.anim.fade_in_fast);
        tv_cant_selec_chat.setAnimation(anim);
        tv_cant_selec_chat.setText(""+l);

        if(l==1){
            ItemChat temp = chatSeleccionados.get(0);
            if((temp.esMsgTexto() || temp.esImagen())
                    && temp.esDer() && !temp.hayQReintentarEnviar())

            if(temp.puedeSwipear()) iv_responder_selec_chat.setVisibility(View.VISIBLE);
            else iv_responder_selec_chat.setVisibility(View.GONE);

            if(temp.hayQReintentarEnviar()) iv_reintentar_selec_chat.setVisibility(View.VISIBLE);
            else iv_reintentar_selec_chat.setVisibility(View.GONE);

            if(temp.esMsgTexto() || temp.esImagen() || temp.esTarjeta())
                iv_copiar_selec_chat.setVisibility(View.VISIBLE);
            else iv_copiar_selec_chat.setVisibility(View.GONE);
        }
        else if(l>0){
            iv_responder_selec_chat.setVisibility(View.GONE);

            iv_reintentar_selec_chat.setVisibility(View.GONE);
            iv_copiar_selec_chat.setVisibility(View.GONE);
            iv_reenviar_selec_chat.setVisibility(View.GONE);

            for(int i=0; i<l; i++){
                ItemChat temp = chatSeleccionados.get(i);
                if(temp.hayQReintentarEnviar()
                        && iv_reintentar_selec_chat.getVisibility()!=View.VISIBLE)
                    iv_reintentar_selec_chat.setVisibility(View.VISIBLE);
                if(iv_copiar_selec_chat.getVisibility()!=View.VISIBLE){
                    if(temp.esMsgTexto() || temp.esImagen() || temp.esTarjeta())
                        iv_copiar_selec_chat.setVisibility(View.VISIBLE);
                }
                if(iv_reenviar_selec_chat.getVisibility()!=View.VISIBLE
                        && !temp.hayQReintentarEnviar())
                    iv_reenviar_selec_chat.setVisibility(View.VISIBLE);
            }
        }
        else cancelarModoSeleccionar();
    }

    class ViewPagerAdapter extends PagerAdapter {
        private Context mContext;
        private ArrayList<AlbumFile> list;

        public ViewPagerAdapter(Context context, ArrayList<AlbumFile> list) {
            this.mContext = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
        @Override
        public Object instantiateItem(ViewGroup container, final int i) {
            PhotoView photoView = new PhotoView(mContext);
            photoView.enable();
            photoView.enableRotate();

//            final PhotoViewAttacher attacher = new PhotoViewAttacher(photoView);

            Glide.with(mContext).load(list.get(i).getPath())
                    .dontAnimate()
                    .error(R.drawable.placeholder).into(photoView);
//            attacher.update();
            photoView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
//                    attacher.setScale(1, false);
                    cerrarViewPagerPreview();
                }
            });
            container.addView(photoView);
            return photoView;
        }
    }

    public boolean estaImagenEnPhotoView(){
        return previewParent.getVisibility()==View.VISIBLE;
    }

    public synchronized void cerrarViewPagerPreview() {
        if(mainActivity!=null)
            mainActivity.cambiarColorStatusBar(YouChatApplication.itemTemas.getStatus_bar());
        if(positionViewPagerPreviewInicial!=positionViewPagerPreviewActual){
            item_photoView_visorImg_pv.setVisibility(View.VISIBLE);
            Animation anim = AnimationUtils.loadAnimation(context,R.anim.hide_layout_preview_image_chat);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}
                @Override
                public void onAnimationEnd(Animation animation) {
                    preview_viewPager.setVisibility(View.GONE);
                    previewParent.setVisibility(View.GONE);
                }
                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            previewParent.startAnimation(anim);
        }
        else {
            preview_viewPager.setVisibility(View.GONE);
            transImage.setExitEnd(new TransSmallImageView.ExitEnd() {
                @Override
                public void end() {
                    item_photoView_visorImg_pv.setVisibility(View.VISIBLE);
                    previewParent.setVisibility(View.GONE);
                }
            });
            transImage.exit(locationOnScreen[0], locationOnScreen[1],
                    locationOnScreen[2], locationOnScreen[3]);
        }
    }

    public void mostrarBSDAtajo() {
        BottomSheetDialogFragment_Atajos aaa = BottomSheetDialogFragment_Atajos.newInstance(false,true);
        aaa.setOnAtajoClickListener(new BottomSheetDialogFragment_Atajos.OnAtajoClickListener() {
            @Override
            public void OnClick(ItemAtajo atajo) {
                enviarMensaje(atajo.getComando());
            }
            @Override
            public void OnAddAtajo() {
                Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(1);
                View mview=getLayoutInflater().inflate(R.layout.dialog_options_add_atajo,null);
                dialog.setContentView(mview);

                TextView et_atajo_com = mview.findViewById(R.id.et_atajo_com);
                TextView et_atajo_des = mview.findViewById(R.id.et_atajo_des);
                View efab_guardar_atajo = mview.findViewById(R.id.efab_guardar_atajo);

                efab_guardar_atajo.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String comando = et_atajo_com.getText().toString();
                        String descrip = et_atajo_des.getText().toString();
                        if(!comando.trim().isEmpty()){
                            dialog.dismiss();
                            dbWorker.insertarNuevoAtajo(new ItemAtajo(comando,descrip));
                            Utils.ShowToastAnimated(mainActivity,"Atajo guardado con éxito",R.raw.contact_check);
                        }
                        else Utils.ShowToastAnimated(mainActivity,"El atajo no puede estar vacío",R.raw.error);
                    }
                });

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                dialog.setCancelable(true);
                dialog.show();
            }
        });
        aaa.show(getParentFragmentManager(),"BottomSheetDialogFragment_Atajos");
    }
}