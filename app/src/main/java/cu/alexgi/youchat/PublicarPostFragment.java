package cu.alexgi.youchat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.listeners.OnEmojiPopupDismissListener;
import com.vanniktech.emoji.listeners.OnEmojiPopupShownListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cu.alexgi.youchat.BottomSheetDialogFragment.BottomSheetDialogFragment_Ayuda_ajustes;
import cu.alexgi.youchat.BottomSheetDialogFragment.BottomSheetDialogFragment_elegir_tema;
import cu.alexgi.youchat.BottomSheetDialogFragment.BottomSheetDialogFragment_fondo_post_tarjeta;
import cu.alexgi.youchat.adapters.AdaptadorDatosStickerPubPost;
import cu.alexgi.youchat.chatUtils.RecentPhotoViewRail;
import cu.alexgi.youchat.items.ItemDetallesTarjeta;
import cu.alexgi.youchat.items.ItemImg;
import cu.alexgi.youchat.items.ItemTemas;
import cu.alexgi.youchat.photoView.photoViewLibrary.Info;
import cu.alexgi.youchat.photoView.photoViewLibrary.PhotoView;
import cu.alexgi.youchat.photoutil.ImageLoader;
import cu.alexgi.youchat.views_GI.TextViewFontGenGI;

import static cu.alexgi.youchat.MainActivity.context;
import static cu.alexgi.youchat.MainActivity.dbWorker;
import static cu.alexgi.youchat.MainActivity.mainActivity;
import static cu.alexgi.youchat.MainActivity.permisos;

public class PublicarPostFragment extends Fragment {

    private static int limitePost, iconPost;
    private String destino;
    private RecentPhotoViewRail recent_photos;
    private LoaderManager loaderManager;
    private ItemTemas temaCompartir;
    private ItemDetallesTarjeta detallesTarjeta;
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
        @Override
        public void afterTextChanged(Editable s) {
            String cad = s.toString();
            tv_tarjeta.setText(cad);
        }
    };

    private RecyclerView lista_sticker;
    private AdaptadorDatosStickerPubPost adaptadorDatosStickerPubPost;
    private ShapeableImageView image;
//    private PhotoView image;
//    private CircleImageView img_perfil;
    private ImageView icon_perfil;
    private View btn_delete_tarjeta, btn_tarjeta, btn_tema, btn_icon,
            btn_image, arrow_up, view_icons, root, btn_send, ll_tarjeta, ll_tema, ll_img, btn_delete;
    private TextView txt_cant, tv_tam_img, tv_name, tv_correo;
    private Animation anim;
    private ImageView emoji, icon;
    private EmojiPopup emojiPopup;
    private EmojiEditText input_post;

    //img linda
    private View frameLayout_visorImg_perfil, cambiar_fondo, tv_modo_uso;
    private PhotoView photoView_visorImg_perfil;
    private Info info_photoView;

    //disenno
    private TextViewFontGenGI tema_nombre, tema_creador;
    private MaterialCardView theme_card, theme_bar, fondo_btn;
    private AppCompatImageView theme_msg_izq, theme_msg_der, theme_radio;
    private TextView theme_btn;
    private ImageView img_fondo_style_theme;

    //tarjeta
    private View background_tarjeta, pos_ninguna, pos_lot_arriba, pos_lot_abajo, pos_lot_izquierda, pos_lot_derecha;
    private TextView tv_tarjeta;
    private LottieAnimationView lottie_arriba, lottie_abajo, lottie_izquierda, lottie_derecha;

    public static PublicarPostFragment newInstance(int r) {
        PublicarPostFragment fragment = new PublicarPostFragment();
        limitePost = r;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_publicar_post, container, false);
    }

    @Override
    public void onDestroy() {
        YouChatApplication.publicarPostFragment = null;
        super.onDestroy();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        YouChatApplication.publicarPostFragment = this;

        adaptadorDatosStickerPubPost = null;
        temaCompartir = null;
        tema_nombre=view.findViewById(R.id.tema_nombre);
        tema_creador=view.findViewById(R.id.tema_creador);
        theme_card=view.findViewById(R.id.theme_card);
        theme_bar=view.findViewById(R.id.theme_bar);
        theme_msg_izq=view.findViewById(R.id.theme_msg_izq);
        theme_msg_der=view.findViewById(R.id.theme_msg_der);
        theme_radio=view.findViewById(R.id.theme_radio);
        theme_btn=view.findViewById(R.id.theme_btn);
        fondo_btn=view.findViewById(R.id.fondo_btn);
        img_fondo_style_theme = view.findViewById(R.id.img_fondo_style_theme);

        detallesTarjeta = null;
        initComponentTarjeta(view);

        tv_modo_uso = view.findViewById(R.id.tv_modo_uso);
        tv_modo_uso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                        .newInstance("Término de uso y condiciones", getResources().getString(R.string.explicarModoUsoPubPost));
                bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
            }
        });

        View bottomSheetInternal = view.findViewById(R.id.coordinate);
        bottomSheetInternal.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_fondo()));
//        bottomSheetInternal.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {}
//        });
        BottomSheetBehavior bsb = BottomSheetBehavior.from(bottomSheetInternal);
        arrow_up = view.findViewById(R.id.arrow_up);
        arrow_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bsb.getState()!=BottomSheetBehavior.STATE_EXPANDED)
                    bsb.setState(BottomSheetBehavior.STATE_EXPANDED);
                else bsb.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        bsb.setPeekHeight(Utils.dpToPx(context, 50f));
        bsb.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bsb.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        break;
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                arrow_up.setRotation(slideOffset*180f);
            }
        });

        tv_name = view.findViewById(R.id.tv_name);
        tv_correo = view.findViewById(R.id.tv_correo);

        frameLayout_visorImg_perfil = view.findViewById(R.id.frameLayout_visorImg_perfil);
        photoView_visorImg_perfil = view.findViewById(R.id.photoView_visorImg_perfil);
        photoView_visorImg_perfil.enable();
        photoView_visorImg_perfil.enableRotate();
        photoView_visorImg_perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocultarImagen();
            }
        });

        tv_name.setText(YouChatApplication.alias);
        tv_correo.setText(YouChatApplication.correo);
//        img_perfil = view.findViewById(R.id.img_perfil);
        icon_perfil = view.findViewById(R.id.icon_perfil);
        icon_perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(view_icons.getVisibility()!=View.VISIBLE){
                    bsb.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    mostrarIconos();
                }
                else ocultarIconos();
            }
        });
//        Glide.with(context).load(YouChatApplication.ruta_img_perfil).circleCrop().into(img_perfil);

        tv_tam_img = view.findViewById(R.id.tv_tam_img);
        tv_tam_img.setText("0.00 kb");
        txt_cant = view.findViewById(R.id.txt_cant);
        if(limitePost>0) txt_cant.setText("Puntos restantes: "+limitePost);
        else txt_cant.setText("No tiene puntos para publicar");

        recent_photos = view.findViewById(R.id.recent_photos);
        recent_photos.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_fondo()));
        view_icons = view.findViewById(R.id.view_icons);
//        LayoutInflater inflater = LayoutInflater.from(context);
//        recent_photos = (RecentPhotoViewRail) inflater.inflate(R.layout.view_rail_photos, viewInflate, false);
//        view_icons = inflater.inflate(R.layout.view_iconos_post, viewInflate, false);

        loaderManager = LoaderManager.getInstance(this);
//        loaderManager = mainActivity.getSupportLoaderManager();
        recent_photos.setListener(new RecentPhotoSelectedListener());
        btn_image=view.findViewById(R.id.btn_image);
        if (permisos.requestPermissionAlmacenamiento()) {
            loaderManager.initLoader(1, null, recent_photos);

            btn_image.setVisibility(View.VISIBLE);
            btn_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(limitePost>=3){
                        bsb.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        mostrarImgs();
                        btn_tema.setVisibility(View.GONE);
                        btn_image.setVisibility(View.GONE);
                        btn_tarjeta.setVisibility(View.GONE);
                    }
                    else Utils.ShowToastAnimated(mainActivity,"Necesita al menos 3 puntos", R.raw.ic_ban);
                }
            });
        }
        else btn_image.setVisibility(View.GONE);

        image = view.findViewById(R.id.image);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!destino.isEmpty()) previewImage();
            }
        });

        ll_tarjeta = view.findViewById(R.id.ll_tarjeta);
        ll_tema = view.findViewById(R.id.ll_tema);
        ll_img = view.findViewById(R.id.ll_img);
        btn_delete_tarjeta = view.findViewById(R.id.btn_delete_tarjeta);
        btn_delete = view.findViewById(R.id.btn_delete);
        btn_delete_tarjeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_tarjeta.setVisibility(View.GONE);
                btn_tema.setVisibility(View.VISIBLE);
                btn_image.setVisibility(View.VISIBLE);
                btn_tarjeta.setVisibility(View.VISIBLE);
                input_post.removeTextChangedListener(textWatcher);
                detallesTarjeta = null;
            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image.setImageDrawable(null);
                destino="";
                input_post.setHint("¿Qué estás pensando?");
                anim=AnimationUtils.loadAnimation(context, R.anim.fade_out_system);
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        ll_img.setVisibility(View.GONE);
                        btn_tema.setVisibility(View.VISIBLE);
                        btn_image.setVisibility(View.VISIBLE);
                        btn_tarjeta.setVisibility(View.VISIBLE);
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
                ll_img.startAnimation(anim);
            }
        });
        input_post = view.findViewById(R.id.input_post);
        icon = view.findViewById(R.id.icon);
        btn_icon = view.findViewById(R.id.btn_icon);
        btn_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                hacer comprobacion
                if(view_icons.getVisibility()!=View.VISIBLE){
                    bsb.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    mostrarIconos();
                }
                else ocultarIconos();
            }
        });

        btn_tema = view.findViewById(R.id.btn_tema);
        btn_tema.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(view_icons.getVisibility()!=View.VISIBLE){
                    bsb.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                sacarBottomSheetTema();
            }
        });
        btn_tarjeta = view.findViewById(R.id.btn_tarjeta);
        btn_tarjeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(limitePost>=2){
                    if(view_icons.getVisibility()!=View.VISIBLE)
                        bsb.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    mostrarLayoutTarjeta();
                }
                else Utils.ShowToastAnimated(mainActivity,"Necesita al menos 2 puntos", R.raw.ic_ban);

            }
        });

        view.findViewById(R.id.ir_atras).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.atrasFragment();
            }
        });
        view_icons.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_fondo()));
        root = view.findViewById(R.id.root);
        root.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_fondo()));

        emoji=view.findViewById(R.id.emoji);
        emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view_icons.getVisibility()!=View.VISIBLE){
                    bsb.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                emojiPopup.toggle();
            }
        });

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                setUpEmojiPopup(view);
            }
        });

        btn_send=view.findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(limitePost>0){
                    String mensaje = input_post.getText().toString().trim();
                    if(YouChatApplication.estaAndandoChatService() && YouChatApplication.chatService.hayConex){
                    if(destino!=null && !destino.isEmpty()){
                        PostFragment.enviarPostImagen(iconPost,mensaje, destino);
//                        PostFragment.enviarPostPruebaImagen(iconPost,mensaje, destino);
                        getActivity().onBackPressed();
                    }
                    else if(temaCompartir!=null){
                        PostFragment.enviarPostTema(iconPost, mensaje, temaCompartir);
//                        PostFragment.enviarPostPruebaTema(iconPost, mensaje, temaCompartir);
                        getActivity().onBackPressed();
                    }
                    else if(detallesTarjeta!=null){
                        PostFragment.enviarPostTarjeta(iconPost,input_post.getText().toString(), detallesTarjeta);
//                        PostFragment.enviarPostPruebaTarjeta(iconPost,input_post.getText().toString(), detallesTarjeta);
                        getActivity().onBackPressed();
                    }
                    else {
                        if(!mensaje.isEmpty()) {
                            PostFragment.enviarPost(iconPost,mensaje);
//                            PostFragment.enviarPostPrueba(iconPost,mensaje);
                            getActivity().onBackPressed();
                        }
                        else Utils.ShowToastAnimated(mainActivity,"Escriba algo...",R.raw.chats_infotip);
                    }
                }
                    else Utils.ShowToastAnimated(mainActivity, "Compruebe su conexión", R.raw.ic_ban);
                }
                else Utils.ShowToastAnimated(mainActivity,"Necesita al menos 1 punto para publicar", R.raw.ic_ban);

            }
        });

        /////////////////////////ICONOS///////////////////
        View ll_icon_post2, ll_icon_post3;
        ll_icon_post2=view.findViewById(R.id.ll_icon_post2);
        ll_icon_post3=view.findViewById(R.id.ll_icon_post3);
        TextViewFontGenGI tv_cant_letra_post;
        tv_cant_letra_post=view.findViewById(R.id.tv_cant_letra_post);
        input_post.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                int longi = s.length();
                tv_cant_letra_post.setText(String.format("%03d/500",longi));
            }
        });

        if(YouChatApplication.es_beta_tester || YouChatApplication.comprobarOficialidad(YouChatApplication.correo)){
            ll_icon_post2.setVisibility(View.VISIBLE);
            ll_icon_post3.setVisibility(View.VISIBLE);
//            ll_icon_post4.setVisibility(View.VISIBLE);
        }
        else{
            int cantSeguidores = dbWorker.obtenerCantSeguidores();
            if(cantSeguidores>=YouChatApplication.usuMenor) ll_icon_post2.setVisibility(View.VISIBLE);
            if(cantSeguidores>=YouChatApplication.usuMedio) ll_icon_post3.setVisibility(View.VISIBLE);
//            if(cantSeguidores>=YouChatApplication.usuMayor) ll_icon_post4.setVisibility(View.VISIBLE);
        }

        iconPost = 1;
        View noti1,noti2,noti3,noti4,noti5,noti6,noti7,noti8,noti9,noti10,noti11,noti12,noti13,noti14,noti15;
        noti1=view.findViewById(R.id.noti1);
        noti2=view.findViewById(R.id.noti2);
        noti3=view.findViewById(R.id.noti3);
        noti4=view.findViewById(R.id.noti4);
        noti5=view.findViewById(R.id.noti5);
        noti6=view.findViewById(R.id.noti6);
        noti7=view.findViewById(R.id.noti7);
        noti8=view.findViewById(R.id.noti8);
        noti9=view.findViewById(R.id.noti9);
        noti10=view.findViewById(R.id.noti10);
        noti11=view.findViewById(R.id.noti11);
        noti12=view.findViewById(R.id.noti12);
        noti13=view.findViewById(R.id.noti13);
        noti14=view.findViewById(R.id.noti14);
        noti15=view.findViewById(R.id.noti15);
        noti1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iconPost=1;
                ocultarIconos();
                icon.setImageResource(R.drawable.noti1);
                icon_perfil.setImageResource(R.drawable.noti1);
            }
        });
        noti2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iconPost=2;
                ocultarIconos();
                icon.setImageResource(R.drawable.noti2);
                icon_perfil.setImageResource(R.drawable.noti2);
            }
        });
        noti3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iconPost=3;
                ocultarIconos();
                icon.setImageResource(R.drawable.noti3);
                icon_perfil.setImageResource(R.drawable.noti3);
            }
        });
        noti4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iconPost=4;
                ocultarIconos();
                icon.setImageResource(R.drawable.noti4);
                icon_perfil.setImageResource(R.drawable.noti4);
            }
        });
        noti5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iconPost=5;
                ocultarIconos();
                icon.setImageResource(R.drawable.noti5);
                icon_perfil.setImageResource(R.drawable.noti5);
            }
        });
        noti6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iconPost=6;
                ocultarIconos();
                icon.setImageResource(R.drawable.noti6);
                icon_perfil.setImageResource(R.drawable.noti6);
            }
        });
        noti7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iconPost=7;
                ocultarIconos();
                icon.setImageResource(R.drawable.noti7);
                icon_perfil.setImageResource(R.drawable.noti7);
            }
        });
        noti8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iconPost=8;
                ocultarIconos();
                icon.setImageResource(R.drawable.noti8);
                icon_perfil.setImageResource(R.drawable.noti8);
            }
        });
        noti9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iconPost=9;
                ocultarIconos();
                icon.setImageResource(R.drawable.noti9);
                icon_perfil.setImageResource(R.drawable.noti9);
            }
        });
        noti10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iconPost=10;
                ocultarIconos();
                icon.setImageResource(R.drawable.noti10);
                icon_perfil.setImageResource(R.drawable.noti10);
            }
        });
        noti11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iconPost=11;
                ocultarIconos();
                icon.setImageResource(R.drawable.noti11);
                icon_perfil.setImageResource(R.drawable.noti11);
            }
        });
        noti12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iconPost=12;
                ocultarIconos();
                icon.setImageResource(R.drawable.noti12);
                icon_perfil.setImageResource(R.drawable.noti12);
            }
        });
        noti13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iconPost=13;
                ocultarIconos();
                icon.setImageResource(R.drawable.noti13);
                icon_perfil.setImageResource(R.drawable.noti13);
            }
        });
        noti14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iconPost=14;
                ocultarIconos();
                icon.setImageResource(R.drawable.noti14);
                icon_perfil.setImageResource(R.drawable.noti14);
            }
        });
        noti15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iconPost=15;
                ocultarIconos();
                icon.setImageResource(R.drawable.noti15);
                icon_perfil.setImageResource(R.drawable.noti15);
            }
        });
        /////////////////////////ICONOS///////////////////
    }

    private void initComponentTarjeta(View view) {
        background_tarjeta = view.findViewById(R.id.background_tarjeta);
        cambiar_fondo=view.findViewById(R.id.cambiar_fondo);
        lista_sticker = view.findViewById(R.id.lista_sticker);
        tv_tarjeta = view.findViewById(R.id.tv_tarjeta);
        lottie_arriba = view.findViewById(R.id.lottie_arriba);
        lottie_abajo = view.findViewById(R.id.lottie_abajo);
        lottie_izquierda = view.findViewById(R.id.lottie_izquierda);
        lottie_derecha = view.findViewById(R.id.lottie_derecha);

        pos_ninguna = view.findViewById(R.id.pos_ninguna);
        pos_lot_arriba = view.findViewById(R.id.pos_lot_arriba);
        pos_lot_abajo = view.findViewById(R.id.pos_lot_abajo);
        pos_lot_izquierda = view.findViewById(R.id.pos_lot_izquierda);
        pos_lot_derecha = view.findViewById(R.id.pos_lot_derecha);

        lista_sticker.setLayoutManager(new LinearLayoutManager(context,RecyclerView.HORIZONTAL, false));

        background_tarjeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sacarTeclado();
            }
        });

        cambiar_fondo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sacarBottomSheetFondoTarjeta();
            }
        });

        lottie_arriba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lottie_arriba.playAnimation();
            }
        });
        lottie_abajo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lottie_abajo.playAnimation();
            }
        });
        lottie_izquierda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lottie_izquierda.playAnimation();
            }
        });
        lottie_derecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lottie_derecha.playAnimation();
            }
        });

        pos_ninguna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(detallesTarjeta.getPosLottie()!=-1){
                    cambiarPosLottie(-1);
                }
            }
        });
        pos_lot_arriba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(detallesTarjeta.getPosLottie()!=0){
                    cambiarPosLottie(0);
                }
            }
        });
        pos_lot_abajo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(detallesTarjeta.getPosLottie()!=1){
                    cambiarPosLottie(1);
                }
            }
        });
        pos_lot_izquierda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(detallesTarjeta.getPosLottie()!=2){
                    cambiarPosLottie(2);
                }
            }
        });
        pos_lot_derecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(detallesTarjeta.getPosLottie()!=3){
                    cambiarPosLottie(3);
                }
            }
        });
    }

    private void sacarBottomSheetFondoTarjeta() {
        BottomSheetDialogFragment_fondo_post_tarjeta bsd = BottomSheetDialogFragment_fondo_post_tarjeta.newInstance(this);
        bsd.show(getParentFragmentManager(),"BottomSheetDialogFragment_fondo_post_tarjeta");
    }

    private void cambiarPosLottie(int pos) {
        switch (detallesTarjeta.getPosLottie()){
            case 0:
                lottie_arriba.clearAnimation();
                lottie_arriba.setVisibility(View.GONE);
                break;
            case 1:
                lottie_abajo.clearAnimation();
                lottie_abajo.setVisibility(View.GONE);
                break;
            case 2:
                lottie_izquierda.clearAnimation();
                lottie_izquierda.setVisibility(View.GONE);
                break;
            case 3:
                lottie_derecha.clearAnimation();
                lottie_derecha.setVisibility(View.GONE);
                break;
        }
        detallesTarjeta.setPosLottie(pos);
        switch (detallesTarjeta.getPosLottie()){
            case 0:
                lottie_arriba.setAnimation(Utils.obtenerRawActualDePos(detallesTarjeta.getTipoLottie()));
                lottie_arriba.setVisibility(View.VISIBLE);
                break;
            case 1:
                lottie_abajo.setAnimation(Utils.obtenerRawActualDePos(detallesTarjeta.getTipoLottie()));
                lottie_abajo.setVisibility(View.VISIBLE);
                break;
            case 2:
                lottie_izquierda.setAnimation(Utils.obtenerRawActualDePos(detallesTarjeta.getTipoLottie()));
                lottie_izquierda.setVisibility(View.VISIBLE);
                break;
            case 3:
                lottie_derecha.setAnimation(Utils.obtenerRawActualDePos(detallesTarjeta.getTipoLottie()));
                lottie_derecha.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void ponerSticker(int pos) {
        if(detallesTarjeta.getTipoLottie()!=pos){
            detallesTarjeta.setTipoLottie(pos);
            switch (detallesTarjeta.getPosLottie()){
                case 0:
                    lottie_arriba.setAnimation(Utils.obtenerRawActualDePos(detallesTarjeta.getTipoLottie()));
                    break;
                case 1:
                    lottie_abajo.setAnimation(Utils.obtenerRawActualDePos(detallesTarjeta.getTipoLottie()));
                    break;
                case 2:
                    lottie_izquierda.setAnimation(Utils.obtenerRawActualDePos(detallesTarjeta.getTipoLottie()));
                    break;
                case 3:
                    lottie_derecha.setAnimation(Utils.obtenerRawActualDePos(detallesTarjeta.getTipoLottie()));
                    break;
            }
        }
    }

    private void mostrarLayoutTarjeta() {
        ll_tarjeta.setVisibility(View.VISIBLE);
        btn_tema.setVisibility(View.GONE);
        btn_image.setVisibility(View.GONE);
        btn_tarjeta.setVisibility(View.GONE);

        if(adaptadorDatosStickerPubPost==null){
            adaptadorDatosStickerPubPost = new AdaptadorDatosStickerPubPost(this);
            lista_sticker.setAdapter(adaptadorDatosStickerPubPost);
        }

        String cad = input_post.getText().toString().trim();
        tv_tarjeta.setText(cad);
        input_post.addTextChangedListener(textWatcher);

        detallesTarjeta = new ItemDetallesTarjeta(0,0,4);
        lottie_arriba.setVisibility(View.GONE);
        lottie_abajo.setVisibility(View.GONE);
        lottie_izquierda.setVisibility(View.GONE);
        lottie_derecha.setVisibility(View.GONE);

        ponerColorFondo(detallesTarjeta.getColorFondo());

        switch (detallesTarjeta.getPosLottie()){
            case 0:
                lottie_arriba.setAnimation(Utils.obtenerRawActualDePos(detallesTarjeta.getTipoLottie()));
                lottie_arriba.setVisibility(View.VISIBLE);
                break;
            case 1:
                lottie_abajo.setAnimation(Utils.obtenerRawActualDePos(detallesTarjeta.getTipoLottie()));
                lottie_abajo.setVisibility(View.VISIBLE);
                break;
            case 2:
                lottie_izquierda.setAnimation(Utils.obtenerRawActualDePos(detallesTarjeta.getTipoLottie()));
                lottie_izquierda.setVisibility(View.VISIBLE);
                break;
            case 3:
                lottie_derecha.setAnimation(Utils.obtenerRawActualDePos(detallesTarjeta.getTipoLottie()));
                lottie_derecha.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void sacarBottomSheetTema() {
        BottomSheetDialogFragment_elegir_tema bsd = BottomSheetDialogFragment_elegir_tema.newInstance(this);
        bsd.show(getParentFragmentManager(),"BottomSheetDialogFragment_elegir_tema");
    }

    private void previewImage() {
        File file =  new File(destino);
        if(!file.exists()) return;
        info_photoView = PhotoView.getImageViewInfo(image);
        image.setVisibility(View.INVISIBLE);
        frameLayout_visorImg_perfil.setVisibility(View.VISIBLE);
        Glide.with(context).load(destino).error(R.drawable.placeholder).into(photoView_visorImg_perfil);
        photoView_visorImg_perfil.animaFrom(info_photoView);
    }
    private void ocultarImagen() {
        photoView_visorImg_perfil.animaTo(info_photoView, new Runnable() {
            @Override
            public void run() {
                image.setVisibility(View.VISIBLE);
                frameLayout_visorImg_perfil.setVisibility(View.GONE);
            }
        });
    }

    private void mostrarImgs(){
        ocultarIconos();
//        recent_photos.setVisibility(View.VISIBLE);
//        anim = AnimationUtils.loadAnimation(context, R.anim.show_layout_carrucel);
//        anim.setFillAfter(true);
//        recent_photos.startAnimation(anim);
        recent_photos.animate()
                .translationYBy(-Utils.dpToPx(context,100))
//                .translationY(-Utils.dpToPx(context,100))
//                .y(-Utils.dpToPx(context,100))
//                .yBy(-Utils.dpToPx(context,100))
                .setDuration(500)
                .start();
    }
    private void ocultarImgs(){
//        if(recent_photos.getVisibility()!=View.GONE){
//            anim=AnimationUtils.loadAnimation(context, R.anim.hide_layout_carrucel);
//            anim.setFillAfter(true);
//            anim.setAnimationListener(new Animation.AnimationListener() {
//                @Override
//                public void onAnimationStart(Animation animation) {}
//
//                @Override
//                public void onAnimationEnd(Animation animation) {
////                    recent_photos.setVisibility(View.GONE);
//                }
//                @Override
//                public void onAnimationRepeat(Animation animation) {}
//            });
//            recent_photos.startAnimation(anim);
//        }

        recent_photos.animate()
                .translationYBy(Utils.dpToPx(context,100))
//                .translationY(-Utils.dpToPx(context,100))
//                .y(-Utils.dpToPx(context,100))
//                .yBy(-Utils.dpToPx(context,100))
                .setDuration(500)
                .start();
    }

    private void mostrarIconos(){
        view_icons.setVisibility(View.VISIBLE);
        view_icons.startAnimation(AnimationUtils.loadAnimation(context, R.anim.show_layout_answer));
    }
    private void ocultarIconos(){
        if(view_icons.getVisibility()!=View.GONE){
            anim=AnimationUtils.loadAnimation(context, R.anim.hide_layout_answer);
            anim.setAnimationListener(new Animation.AnimationListener(){
                @Override
                public void onAnimationStart(Animation animation){}

                @Override
                public void onAnimationEnd(Animation animation){
                    view_icons.setVisibility(View.GONE);
                }
                @Override
                public void onAnimationRepeat(Animation animation){}
            });
            view_icons.startAnimation(anim);
        }
    }

    private void setUpEmojiPopup(View root) {
        emojiPopup = EmojiPopup.Builder.fromRootView(root)
                .setOnEmojiPopupShownListener(new OnEmojiPopupShownListener() {
                    @Override
                    public void onEmojiPopupShown() {
                        emoji.setImageResource(R.drawable.input_keyboard);
                    }
                })
                .setOnEmojiPopupDismissListener(new OnEmojiPopupDismissListener() {
                    @Override
                    public void onEmojiPopupDismiss() {
                        emoji.setImageResource(R.drawable.input_emoji);
                    }
                })
                .setKeyboardAnimationStyle(R.style.emoji_fade_animation_style)
                .setPageTransformer(new PageTransformer())
                .build(input_post,false);
    }

    @SuppressLint("RestrictedApi")
    public void compartirTema(ItemTemas tema) {
        ll_tema.setVisibility(View.VISIBLE);
        btn_tema.setVisibility(View.GONE);
        btn_image.setVisibility(View.GONE);
        btn_tarjeta.setVisibility(View.GONE);

        temaCompartir = tema;
        //disenno
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

        fondo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temaCompartir = null;
                ll_tema.setVisibility(View.GONE);
                btn_tema.setVisibility(View.VISIBLE);
                btn_image.setVisibility(View.VISIBLE);
                btn_tarjeta.setVisibility(View.VISIBLE);
            }
        });
        theme_card.setCardBackgroundColor(Color.parseColor(tema.getColor_fondo()));
        theme_bar.setCardBackgroundColor(Color.parseColor(tema.getColor_barra()));
        theme_msg_izq.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor(tema.getColor_msg_izq())));
        theme_msg_der.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor(tema.getColor_msg_der())));
        theme_radio.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor(tema.getColor_accento())));
    }

    private class RecentPhotoSelectedListener implements RecentPhotoViewRail.OnItemClickedListener {
        @Override
        public synchronized void onItemClicked(Uri uri) {
            Utils.runOnUIThread(()->{
                String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                String imageFileName = "img" + timeStamp +".jpg";
                destino=YouChatApplication.RUTA_IMAGENES_ENVIADAS+imageFileName;
                if(Utils.SavePhotoUri(context,uri,destino)){
                    String origen = destino;
                    imageFileName = "img" + timeStamp +"2.jpg";
                    destino=YouChatApplication.RUTA_IMAGENES_ENVIADAS+imageFileName;
                    if(new File(origen).exists()){
                        try {
                            if(ImageLoader.init().comprimirImagen(origen, destino, 20)){
                                ocultarImgs();
                                ll_img.setVisibility(View.VISIBLE);
                                ll_img.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in_system));
                                int[] al = Utils.obtenerAnchoLargo(destino);
                                Glide.with(context)
                                        .load(destino).override(al[0],al[1])
                                        .error(R.drawable.placeholder).into(image);
//                        Glide.with(context).load(destino).into(image);
                                tv_tam_img.setText(Utils.convertirBytes(new File(destino).length()));
                                input_post.setHint("Añada un comentario...");
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            Utils.ShowToastAnimated(MainActivity.mainActivity,"Error al cargar la imagen",R.raw.error);
                        }
                    } else Utils.ShowToastAnimated(MainActivity.mainActivity,"Error al intentar obtener imagen",R.raw.error);
                }
                else if(Utils.SavePhotoUri2(context,uri,destino)){
                    String origen = destino;
                    imageFileName = "img" + timeStamp +"2.jpg";
                    destino=YouChatApplication.RUTA_IMAGENES_ENVIADAS+imageFileName;
                    if(new File(origen).exists()){
                        try {
                            if(ImageLoader.init().comprimirImagen(origen, destino, 20)){
                                ocultarImgs();
                                ll_img.setVisibility(View.VISIBLE);
                                ll_img.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in_system));
                                int[] al = Utils.obtenerAnchoLargo(destino);
                                Glide.with(context)
                                        .load(destino).override(al[0],al[1])
                                        .error(R.drawable.placeholder).into(image);
//                        Glide.with(context).load(destino).into(image);
                                tv_tam_img.setText(Utils.convertirBytes(new File(destino).length()));
                                input_post.setHint("Añada un comentario...");
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            Utils.ShowToastAnimated(MainActivity.mainActivity,"Error al cargar la imagen",R.raw.error);
                        }
                    } else Utils.ShowToastAnimated(MainActivity.mainActivity,"Error al intentar obtener imagen",R.raw.error);
                }
                else{
                    String origen = Utils.getImageFromUri(context,uri);
                    if(Utils.esImagen(origen)){
                        try {
                    if(ImageLoader.init().comprimirImagen(origen, destino, 20)){
                        ocultarImgs();
                        ll_img.setVisibility(View.VISIBLE);
                        ll_img.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in_system));
                        int[] al = Utils.obtenerAnchoLargo(destino);
                        Glide.with(context)
                                .load(destino).override(al[0],al[1])
                                .error(R.drawable.placeholder).into(image);
//                        Glide.with(context).load(destino).into(image);
                        tv_tam_img.setText(Utils.convertirBytes(new File(destino).length()));
                        input_post.setHint("Añada un comentario...");
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    destino="";
                    Utils.ShowToastAnimated(MainActivity.mainActivity,"Error al cargar la imagen",R.raw.error);
                }
                    }
                    else Utils.ShowToastAnimated(MainActivity.mainActivity,"Error al intentar obtener imagen",R.raw.error);
                }
            });
        }
    }

    private float maxAnchoImagen = (float) YouChatApplication.anchoPantalla*0.6f;
    private float maxLargoImagen = (float) YouChatApplication.largoPantalla*0.4f;

    public void atras() {
        if(frameLayout_visorImg_perfil.getVisibility()==View.VISIBLE)
            ocultarImagen();
        else {
            mainActivity.atrasFragment();
        }
    }

    public synchronized void ponerColorFondo(int tipo_estado) {
        detallesTarjeta.setColorFondo(tipo_estado);
        if (tipo_estado < 30) {
            background_tarjeta.setBackgroundResource(0);
            int colorTarjeta = ContextCompat.getColor(context, R.color.card5);
            switch (tipo_estado) {
                case 0:
                    colorTarjeta = ContextCompat.getColor(context, R.color.card1);
                    break;
                case 1:
                    colorTarjeta = ContextCompat.getColor(context, R.color.card2);
                    break;
                case 2:
                    colorTarjeta = ContextCompat.getColor(context, R.color.card3);
                    break;
                case 3:
                    colorTarjeta = ContextCompat.getColor(context, R.color.card4);
                    break;
                case 4:
                    colorTarjeta = ContextCompat.getColor(context, R.color.card5);
                    break;
                case 5:
                    colorTarjeta = ContextCompat.getColor(context, R.color.card6);
                    break;
                case 6:
                    colorTarjeta = ContextCompat.getColor(context, R.color.card7);
                    break;
                case 7:
                    colorTarjeta = ContextCompat.getColor(context, R.color.card8);
                    break;
                case 8:
                    colorTarjeta = ContextCompat.getColor(context, R.color.card9);
                    break;
                case 9:
                    colorTarjeta = ContextCompat.getColor(context, R.color.card10);
                    break;
                case 10:
                    colorTarjeta = ContextCompat.getColor(context, R.color.card11);
                    break;
                case 11:
                    colorTarjeta = ContextCompat.getColor(context, R.color.card12);
                    break;
                case 12:
                    colorTarjeta = ContextCompat.getColor(context, R.color.card13);
                    break;
                case 13:
                    colorTarjeta = ContextCompat.getColor(context, R.color.card14);
                    break;
                case 14:
                    colorTarjeta = ContextCompat.getColor(context, R.color.card15);
                    break;
                case 15:
                    colorTarjeta = ContextCompat.getColor(context, R.color.card16);
                    break;
            }
            background_tarjeta.setBackgroundColor(colorTarjeta);
        } else {
            background_tarjeta.setBackgroundColor(0);
            switch (tipo_estado) {
                case 30:
                    background_tarjeta
                            .setBackgroundResource(R.drawable.shape_fondo_estado_texto_1);
                    break;
                case 31:
                    background_tarjeta
                            .setBackgroundResource(R.drawable.shape_fondo_estado_texto_2);
                    break;
                case 32:
                    background_tarjeta
                            .setBackgroundResource(R.drawable.shape_fondo_estado_texto_3);
                    break;
                case 33:
                    background_tarjeta
                            .setBackgroundResource(R.drawable.shape_fondo_estado_texto_4);
                    break;
                case 34:
                    background_tarjeta
                            .setBackgroundResource(R.drawable.shape_fondo_estado_texto_5);
                    break;
                case 35:
                    background_tarjeta
                            .setBackgroundResource(R.drawable.shape_fondo_estado_texto_6);
                    break;
                case 36:
                    background_tarjeta
                            .setBackgroundResource(R.drawable.shape_fondo_estado_texto_7);
                    break;
                case 37:
                    background_tarjeta
                            .setBackgroundResource(R.drawable.shape_fondo_estado_texto_8);
                    break;
                case 38:
                    background_tarjeta
                            .setBackgroundResource(R.drawable.shape_fondo_estado_texto_9);
                    break;
                case 39:
                    background_tarjeta
                            .setBackgroundResource(R.drawable.shape_fondo_estado_texto_10);
                    break;
                default:
                    background_tarjeta
                            .setBackgroundResource(R.drawable.shape_fondo_estado_texto_1);
            }
        }
    }

    private void sacarTeclado(){
        input_post.setFocusableInTouchMode(true);
        input_post.requestFocus();
        InputMethodManager inputMethodManager= (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(input_post, InputMethodManager.SHOW_IMPLICIT);
    }
}
