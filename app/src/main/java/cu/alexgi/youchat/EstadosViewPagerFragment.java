package cu.alexgi.youchat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.EmojiTextView;
import com.vanniktech.emoji.listeners.OnEmojiPopupDismissListener;
import com.vanniktech.emoji.listeners.OnEmojiPopupShownListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import cu.alexgi.youchat.Popups.PopupOptionEstados;
import cu.alexgi.youchat.Popups.PopupReaccionEstados;
import cu.alexgi.youchat.adapters.AdaptadorDatosEstadoViews;
import cu.alexgi.youchat.adapters.AdaptadorDatosReaccionEstado;
import cu.alexgi.youchat.items.ItemChat;
import cu.alexgi.youchat.items.ItemContacto;
import cu.alexgi.youchat.items.ItemEstado;
import cu.alexgi.youchat.items.ItemReaccionEstado;
import cu.alexgi.youchat.items.ItemUsuario;
import cu.alexgi.youchat.items.ItemVistaEstado;
import cu.alexgi.youchat.storiesprogressview.StoriesProgressView;

import static cu.alexgi.youchat.MainActivity.context;
import static cu.alexgi.youchat.MainActivity.dbWorker;
import static cu.alexgi.youchat.MainActivity.mainActivity;
import static cu.alexgi.youchat.YouChatApplication.principalActivity;

public class EstadosViewPagerFragment extends Fragment implements StoriesProgressView.StoriesListener{

    private static boolean estadoPropio, esNuevo, esUno;
    private final String TAG="EstadosViewPager";
    private static int posGen, posIni;
    private boolean isComplete;
    private static ItemEstado nowSolo;
    private static String correoActual, correoInicial;
    public ViewPager2 pager;
    private BottomSheetBehavior bsb;
    private Animation anim;
    private View bottomSheetInternal, animacion_reaccion, showReaccion, showViews, info_estado, openMenu, toolbar_visor_estado, coordinate_text, input_estado_send, root, view_fondo, arrow_up;
//    private static FragmentStateAdapter adapter;
    private ArrayList<ItemEstado> estadosList;

    private StoriesProgressView story;
    private CircleImageView mini_img_perfil_estados;
    private EmojiTextView preview_text_estados, nombre_usuario_estado;
    private TextView mensaje_respuesta_nombre, mensaje_respuesta_texto, fecha_subida_estado;

    private EmojiEditText input_text;
    private EmojiPopup emojiPopup;
    private ImageView input_estado_reaccionar, emojiButton;

    private TextView tv_cant_total_vistas, tv_total_reacciones;
    private LottieAnimationView animacion_icono_reaccion, may_reac_1, may_reac_2, may_reac_3;

    private ArrayList<ItemStory> storyList;
    public boolean textoLargoShow, estadoDescarga;

    //////////////////////////////////////////////
    private LottieAnimationView anim_emoji_me_gusta, anim_emoji_me_encanta, anim_emoji_me_sonroja,
            anim_emoji_me_divierte, anim_emoji_me_asombra, anim_emoji_me_entristese,
            anim_emoji_me_enoja;

    private RecyclerView lista_info_reacciones;
    private AdaptadorDatosReaccionEstado mAdapter;
    private AdaptadorDatosEstadoViews adaptadorDatosUsuarioView;
    private ArrayList<ItemReaccionEstado> datos_reacciones_estados;
    private ArrayList<ItemVistaEstado> datos_vistas_estados;
    //////////////////////////////////////////////

    public void playStory() {
        Log.e(TAG, "playStory: ");
        if(story!=null && !textoLargoShow && !estadoDescarga) story.resume();
    }
    public void pauseStory() {
        Log.e(TAG, "pauseStory: ");
        if(story!=null) story.pause();
    }

    public void descargarNowActual(OnDownloadNowListener onDownloadNowListener) {
        if(posGen>=0 && posGen<estadosList.size()){
            ItemEstado itemEstado = estadosList.get(posGen);
            YouChatApplication.chatService.descargarNow(itemEstado.getId(),itemEstado.getCorreo(),itemEstado.getUid(),
                    itemEstado.getId_mensaje(), itemEstado.getOrden(), onDownloadNowListener);
        }
    }

    private static class ItemStory {
        private int posEstado;
        private int cantEstados;
        private String correo;
        private String texto;

        public ItemStory(int posEstado, int cantEstados, String correo) {
            this.posEstado = posEstado;
            this.cantEstados = cantEstados;
            this.correo = correo;
            this.texto = "";
        }
    }
    private int getCantEstadoDe(String correo){
        for(int i=0 ; i<storyList.size() ; i++){
            if(correo.equals(storyList.get(i).correo)) return storyList.get(i).cantEstados;
        }
        return 1;
    }

    ViewPager2.OnPageChangeCallback onPageChangeCallback = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            Log.e("onPageSelected", "pos "+position);
            storyList.get(posGen).texto=input_text.getText().toString();
            posGen = position;

            if(!correoActual.equals(estadosList.get(position).getCorreo())){
                correoActual = estadosList.get(position).getCorreo();
                ItemContacto contacto = dbWorker.obtenerContacto(correoActual);
                if(correoActual.equals(YouChatApplication.idOficial)){
                    YouChatApplication.ponerIconOficial(mini_img_perfil_estados);
                }
                else {
                    Glide.with(context)
                            .load(contacto.getRuta_img())
                            .error(R.drawable.profile_white)
                            .into(mini_img_perfil_estados);
                }
                nombre_usuario_estado.setText(contacto.getNombreMostrar());
                input_text.setHint("Responder a "+contacto.getNombreMostrar());
                mensaje_respuesta_nombre.setText(""+contacto.getNombreMostrar());
            }
            resetStoryBar(position);
            fecha_subida_estado.setText("" + Convertidor.convertirFechaAFechaLinda(estadosList.get(position).getFecha())+ ", " + estadosList.get(position).getHora());

            input_text.setText(storyList.get(position).texto);
            input_text.setSelection(input_text.length());
            if(!estadosList.get(position).getTexto().isEmpty()) mensaje_respuesta_texto.setText(""+estadosList.get(position).getTexto());
            else {
                mensaje_respuesta_texto.setText("");
                mensaje_respuesta_texto.setHint("<Sin contenido>");
            }
//            mostrarViews();
            verificarReaccion(estadosList.get(posGen).reaccionDelEstado());
            verificarBSDTexto();
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            super.onPageScrollStateChanged(state);
            if(state==0){
                mostrarViews();
                ItemEstado estado = dbWorker.obtenerEstado(estadosList.get(posGen).getId());
                if(!estado.isDescargado()){
                    ocultarViewsDescarga();
                }
                else{
                    mostrarViewsDescarga();
//                    playStory();
                }
            }
            else if(state==1){
                ocultarViews();
                pauseStory();
            }
            if(state==0) Log.e("PageScrollStateChanged", ""+state);
        }
    };

    ViewPager2.OnPageChangeCallback onPageChangeCallbackPropio = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            posGen = position;
            actualizarReacciones(estadosList.get(position));
            resetStoryBar(position);
            fecha_subida_estado.setText("" + Convertidor.convertirFechaAFechaLinda(estadosList.get(position).getFecha())+ ", " + estadosList.get(position).getHora());
            verificarBSDTexto();
            ItemEstado estado = dbWorker.obtenerEstado(estadosList.get(posGen).getId());
            if(!estado.isDescargado()){
                ocultarViewsDescarga();
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            super.onPageScrollStateChanged(state);
            if(state==0){
                mostrarViews();
                playStory();
            }
            else if(state==1){
                ocultarViews();
                pauseStory();
            }
        }
    };

    public void mostrarViewsDescarga() {
        estadoDescarga = false;
        playStory();
        verificarBSDTexto();
        if(bottomSheetInternal.getVisibility()!=View.VISIBLE) bottomSheetInternal.setVisibility(View.VISIBLE);
        if(input_estado_reaccionar.getVisibility()!=View.VISIBLE) input_estado_reaccionar.setVisibility(View.VISIBLE);
        if(animacion_reaccion.getVisibility()!=View.VISIBLE) animacion_reaccion.setVisibility(View.VISIBLE);
        if(openMenu.getVisibility()!=View.VISIBLE) openMenu.setVisibility(View.VISIBLE);
    }

    private void ocultarViewsDescarga() {
        pauseStory();
        ocultarBSDTexto();
        estadoDescarga = true;
        bottomSheetInternal.setVisibility(View.GONE);
        input_estado_reaccionar.setVisibility(View.GONE);
        animacion_reaccion.setVisibility(View.GONE);
        openMenu.setVisibility(View.GONE);
    }

    private void verificarBSDTexto() {
        if(estadosList.get(posGen).esEstadoImagen() && !estadosList.get(posGen).getTexto().isEmpty()){
            String u = estadosList.get(posGen).getTexto();
            if(u.length()>100){
                String salva = u;
                u=u.substring(0,88);
                u+="... Leer más";
                String uFinal=u;
                SpannableString a = new SpannableString(u);
                a.setSpan(new ForegroundColorSpan(Color.parseColor(YouChatApplication.itemTemas.getFont_texto_resaltado())),92, a.length(), 0);
                preview_text_estados.setText(uFinal);
                LinkUtils.autoLink(preview_text_estados, new LinkUtils.OnClickListener() {
                    @Override
                    public void onLinkClicked(String link) {
                        Log.e(TAG, "onLinkClicked: "+link);
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
                    }

                    @Override
                    public void onClicked() {
                        Log.e(TAG, "onClicked: "+preview_text_estados.getText().length());
                        if(preview_text_estados.getText().length()<=100){
                            textoLargoShow=true;
                            story.pause();
                            preview_text_estados.setText(salva);
                        }
                        else {
                            textoLargoShow=false;
                            story.resume();
                            preview_text_estados.setText(a);
                        }
                    }
                });
                preview_text_estados.setText(a);
            }
            else {
                textoLargoShow=false;
                preview_text_estados.setText(u);
            }
            mostrarBSDTexto();
        }
        else {
//            preview_text_estados.setText("");
            textoLargoShow=false;
            if(coordinate_text.getVisibility()!=View.GONE) ocultarBSDTexto();
        }
    }
    public synchronized void ocultarBSDTexto() {
        Utils.runOnUIThread(()->{
            anim = AnimationUtils.loadAnimation(context, R.anim.hide_layout_answer);
            anim.setDuration(200);
            coordinate_text.setVisibility(View.GONE);
            coordinate_text.startAnimation(anim);
        });
    }
    public synchronized void mostrarBSDTexto() {
        Utils.runOnUIThread(()->{
            if(coordinate_text.getVisibility()!=View.VISIBLE){
                anim = AnimationUtils.loadAnimation(context, R.anim.show_layout_answer);
                anim.setDuration(200);
                coordinate_text.setVisibility(View.VISIBLE);
                coordinate_text.startAnimation(anim);
            }
        },200);
    }
    public synchronized void ocultarViews() {
        Utils.runOnUIThread(()->{
            anim = AnimationUtils.loadAnimation(context, R.anim.fade_out_fast);
            anim.setDuration(200);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            toolbar_visor_estado.setVisibility(View.GONE);
            toolbar_visor_estado.startAnimation(anim);
        });
        if(coordinate_text.getVisibility()!=View.GONE) ocultarBSDTexto();
    }
    public synchronized void mostrarViews() {
        if(toolbar_visor_estado.getVisibility()!=View.VISIBLE){
            Utils.runOnUIThread(()->{
                anim = AnimationUtils.loadAnimation(context, R.anim.fade_in_fast);
                anim.setDuration(200);
                toolbar_visor_estado.setVisibility(View.VISIBLE);
                toolbar_visor_estado.startAnimation(anim);
            });
        }
        verificarBSDTexto();
    }

    private void resetStoryBar(int position){
        Utils.runOnUIThread(()->{
//            Log.e(TAG, "position: "+position);
            story.setStoriesCount(storyList.get(position).cantEstados);
            story.startStories(storyList.get(position).posEstado);
//            if(position!=0) story.startStories(storyList.get(position).posEstado);
//            else story.startStories();
        },50);
    }

    public EstadosViewPagerFragment() {
        // Required empty public constructor
    }

    public static EstadosViewPagerFragment newInstance(String iE) {
        EstadosViewPagerFragment fragment = new EstadosViewPagerFragment();
        nowSolo = dbWorker.obtenerEstado(iE);
        esUno=true;
        estadoPropio= nowSolo.getCorreo().equals(YouChatApplication.correo);
        return fragment;
    }

    public static EstadosViewPagerFragment newInstance(ItemEstado iE) {
        EstadosViewPagerFragment fragment = new EstadosViewPagerFragment();
        nowSolo = iE;
        esUno=true;
        estadoPropio= nowSolo.getCorreo().equals(YouChatApplication.correo);
        return fragment;
    }

    private static ArrayList<ItemEstado> dato_estados;
    public static EstadosViewPagerFragment newInstance(String c, ArrayList<ItemEstado> estados, boolean isNew) {
        EstadosViewPagerFragment fragment = new EstadosViewPagerFragment();
        correoInicial = c;
        estadoPropio= c.equals(YouChatApplication.correo);
        esNuevo = isNew;
        if(estadoPropio) esNuevo = false;
        dato_estados = estados;
        esUno=false;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(mainActivity!=null) mainActivity.cambiarColorStatusBar("#ff000000");
        return inflater.inflate(R.layout.fragment_estados_viewpager, container, false);
    }

    @Override
    public void onResume() {
        playStory();
        super.onResume();
    }

    @Override
    public void onPause() {
        if(story!=null) story.pause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if(story!=null) story.destroy();
        if(pager!=null && onPageChangeCallback!=null) pager.unregisterOnPageChangeCallback(onPageChangeCallback);
        if(mainActivity!=null) mainActivity.cambiarColorStatusBar(YouChatApplication.itemTemas.getStatus_bar());
        super.onDestroy();
    }

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    Log.e(TAG, "onTouch: ACTION_UP");
                    v.setOnLongClickListener(onLongClickListener);
                    v.setOnTouchListener(null);
                    playStory();
                    mostrarViews();
                    pager.setUserInputEnabled(true);
                    return false;
            }
            return true;
        }
    };

    private View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if(!textoLargoShow && !estadoDescarga){
                pauseStory();
                ocultarViews();
                pager.setUserInputEnabled(false);
                v.setOnTouchListener(onTouchListener);
                v.setOnLongClickListener(null);
            }
            return true;
        }
    };


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        correoActual = "";
        isComplete=false;
//        estadosList = dbWorker.obtenerTodosLosEstadosOrdenadosXCorreos();
        estadosList = new ArrayList<>();
        storyList = new ArrayList<>();

        posIni=-1;
        preview_text_estados = view.findViewById(R.id.preview_text_estados);
        openMenu=view.findViewById(R.id.openMenu);
        info_estado=view.findViewById(R.id.info_estado);
        coordinate_text = view.findViewById(R.id.coordinate_text);
        story = view.findViewById(R.id.story);
        story.setStoriesListener(this);
        mini_img_perfil_estados = view.findViewById(R.id.mini_img_perfil_estados);
        nombre_usuario_estado = view.findViewById(R.id.nombre_usuario_estado);
        fecha_subida_estado = view.findViewById(R.id.fecha_subida_estado);
        root = view.findViewById(R.id.root);
        toolbar_visor_estado = view.findViewById(R.id.toolbar_visor_estado);

        input_estado_reaccionar = view.findViewById(R.id.input_estado_reaccionar);
        animacion_reaccion = view.findViewById(R.id.animacion_reaccion);
        animacion_icono_reaccion = view.findViewById(R.id.animacion_icono_reaccion);

        view_fondo = view.findViewById(R.id.view_fondo);
        view_fondo.setVisibility(View.GONE);
        view_fondo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bsb.getState()==BottomSheetBehavior.STATE_EXPANDED){
                    view_fondo.setVisibility(View.GONE);
                    bsb.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
        pager = view.findViewById(R.id.pager);
        pager.setAdapter(new ScreenSlidePagerAdapter());
        pager.setPageTransformer(new ZoomOutPageTransformer());

        View view_prev = view.findViewById(R.id.view_prev);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int)(YouChatApplication.anchoPantalla*0.10f), RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);
        view_prev.setLayoutParams(layoutParams);

        View view_next = view.findViewById(R.id.view_next);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams((int)(YouChatApplication.anchoPantalla*0.10f), RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams2.addRule(RelativeLayout.ALIGN_PARENT_END);
        view_next.setLayoutParams(layoutParams2);

        view_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pager!=null){
                    if(pager.getCurrentItem()+1<estadosList.size()) pager.setCurrentItem(pager.getCurrentItem()+1, true);
                    else if(pager.getCurrentItem()+1==estadosList.size()) {
                        v.setEnabled(false);
                        mainActivity.atrasFragment();
                    };
                }
            }
        });
        view_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pager!=null && pager.getCurrentItem()-1>=0) pager.setCurrentItem(pager.getCurrentItem()-1, true);
            }
        });
        view_next.setOnLongClickListener(onLongClickListener);
        view_prev.setOnLongClickListener(onLongClickListener);

        if(esUno){
            if(nowSolo!=null){
                if(estadoPropio){
                    String cor = YouChatApplication.correo;
                    posIni = 0;
                    storyList.add(new ItemStory(0, 1, cor));
                    estadosList.add(nowSolo);
                    posGen = posIni;

                    info_estado.setVisibility(View.VISIBLE);
                    openMenu.setVisibility(View.GONE);
                    animacion_reaccion.setVisibility(View.GONE);
                    input_estado_reaccionar.setVisibility(View.GONE);

                    view.findViewById(R.id.coordinate).setVisibility(View.GONE);
                    View bottomSheetInternal = view.findViewById(R.id.coordinate_status);

                    bsb = BottomSheetBehavior.from(bottomSheetInternal);
                    bsb.setPeekHeight(Utils.dpToPx(context, 0f));
                    bsb.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    bsb.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                        @Override
                        public void onStateChanged(@NonNull View bottomSheet, int newState) {
                            switch (newState) {
                                case BottomSheetBehavior.STATE_DRAGGING:
                                    view_fondo.setVisibility(View.VISIBLE);
                                    if(story!=null) story.pause();
                                    break;
                                case BottomSheetBehavior.STATE_COLLAPSED:
                                    view_fondo.setVisibility(View.GONE);
                                    if(pager!=null) pager.setUserInputEnabled(true);
                                    if(story!=null && !textoLargoShow && !estadoDescarga) story.resume();
                                    break;
                                case BottomSheetBehavior.STATE_HIDDEN:
                                    view_fondo.setVisibility(View.GONE);
                                    if(pager!=null) pager.setUserInputEnabled(true);
                                    if(story!=null && !textoLargoShow && !estadoDescarga) story.resume();
                                    break;
                                case BottomSheetBehavior.STATE_HALF_EXPANDED:
                                    if(pager!=null) pager.setUserInputEnabled(false);
                                    if(story!=null) story.pause();
                                    break;
                                case BottomSheetBehavior.STATE_EXPANDED:
                                    view_fondo.setVisibility(View.VISIBLE);
                                    if(pager!=null) pager.setUserInputEnabled(false);
                                    if(story!=null) story.pause();
                                    break;
                            }
                        }
                        @Override
                        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                            view_fondo.setAlpha(slideOffset);
                        }
                    });
                    pager.registerOnPageChangeCallback(onPageChangeCallbackPropio);
//            resetStoryBar(posIni);

                    correoActual = correoInicial;
                    Glide.with(context).load(YouChatApplication.ruta_img_perfil)
                            .error(R.drawable.profile_white)
                            .into(mini_img_perfil_estados);

                    if(YouChatApplication.alias.isEmpty()) nombre_usuario_estado.setText(YouChatApplication.correo);
                    nombre_usuario_estado.setText(YouChatApplication.alias);

                    tv_total_reacciones = view.findViewById(R.id.tv_total_reacciones);
                    tv_cant_total_vistas = view.findViewById(R.id.tv_cant_total_vistas);
                    may_reac_1 = view.findViewById(R.id.may_reac_1);
                    may_reac_2 = view.findViewById(R.id.may_reac_2);
                    may_reac_3 = view.findViewById(R.id.may_reac_3);

                    actualizarReacciones(estadosList.get(posIni));
                    lista_info_reacciones=view.findViewById(R.id.lista_info_reacciones);
                    showReaccion = view.findViewById(R.id.showReaccion);
                    showViews = view.findViewById(R.id.showViews);

                    anim_emoji_me_gusta = view.findViewById(R.id.anim_emoji_me_gusta);
                    anim_emoji_me_encanta = view.findViewById(R.id.anim_emoji_me_encanta);
                    anim_emoji_me_sonroja = view.findViewById(R.id.anim_emoji_me_sonroja);
                    anim_emoji_me_divierte = view.findViewById(R.id.anim_emoji_me_divierte);
                    anim_emoji_me_asombra = view.findViewById(R.id.anim_emoji_me_asombra);
                    anim_emoji_me_entristese = view.findViewById(R.id.anim_emoji_me_entristese);
                    anim_emoji_me_enoja = view.findViewById(R.id.anim_emoji_me_enoja);

                    Utils.runOnUIThread(()->{
                        anim_emoji_me_gusta.setAnimation(R.raw.like1);
                        anim_emoji_me_encanta.setAnimation(R.raw.encanta);
                        anim_emoji_me_sonroja.setAnimation(R.raw.sonroja);
                        anim_emoji_me_divierte.setAnimation(R.raw.divierte);
                        anim_emoji_me_asombra.setAnimation(R.raw.asombra);
                        anim_emoji_me_entristese.setAnimation(R.raw.entristece);
                        anim_emoji_me_enoja.setAnimation(R.raw.enoja);

                        View.OnClickListener onClick = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ((LottieAnimationView)v).playAnimation();
                            }
                        };
                        anim_emoji_me_gusta.setOnClickListener(onClick);
                        anim_emoji_me_encanta.setOnClickListener(onClick);
                        anim_emoji_me_sonroja.setOnClickListener(onClick);
                        anim_emoji_me_divierte.setOnClickListener(onClick);
                        anim_emoji_me_asombra.setOnClickListener(onClick);
                        anim_emoji_me_entristese.setOnClickListener(onClick);
                        anim_emoji_me_enoja.setOnClickListener(onClick);
                    });


                    showReaccion.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showBSD(view, 0, estadosList.get(posGen));
                            bsb.setState(BottomSheetBehavior.STATE_EXPANDED);
                        }
                    });

                    showViews.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showBSD(view, 1, estadosList.get(posGen));
                            bsb.setState(BottomSheetBehavior.STATE_EXPANDED);
                        }
                    });
                }
                else{
                    String cor = nowSolo.getCorreo();
                    posIni = 0;
                    storyList.add(new ItemStory(0, 1, cor));
                    estadosList.add(nowSolo);
                    posGen = posIni;

                    mensaje_respuesta_nombre=view.findViewById(R.id.mensaje_respuesta_nombre);
                    mensaje_respuesta_texto=view.findViewById(R.id.mensaje_respuesta_texto);
                    input_text=view.findViewById(R.id.input_text);
                    emojiButton=view.findViewById(R.id.input_emoji);
                    setUpEmojiPopup();
                    emojiButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            emojiPopup.toggle();
                        }
                    });

                    info_estado.setVisibility(View.GONE);
                    openMenu.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            PopupOptionEstados popupOptionEstados = new PopupOptionEstados(view, estadosList.get(posGen));
                            popupOptionEstados.setOnDismiss(new PopupOptionEstados.onDismissListener() {
                                @Override
                                public void onDismiss() {
                                    playStory();
                                }

                                @Override
                                public void onShow() {
                                    pauseStory();
                                }
                            });
                            popupOptionEstados.show(view);
                        }
                    });

                    bottomSheetInternal = view.findViewById(R.id.coordinate);
                    view.findViewById(R.id.coordinate_status).setVisibility(View.GONE);
                    bsb = BottomSheetBehavior.from(bottomSheetInternal);

                    arrow_up = view.findViewById(R.id.arrow_up);
                    arrow_up.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(bsb.getState()!=BottomSheetBehavior.STATE_EXPANDED){
                                view_fondo.setVisibility(View.VISIBLE);
                                bsb.setState(BottomSheetBehavior.STATE_EXPANDED);
                            }
                            else {
                                view_fondo.setVisibility(View.GONE);
                                bsb.setState(BottomSheetBehavior.STATE_COLLAPSED);
                            }
                        }
                    });
                    bsb.setPeekHeight(Utils.dpToPx(context, 110f));
                    bsb.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    bsb.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                        @Override
                        public void onStateChanged(@NonNull View bottomSheet, int newState) {
                            switch (newState) {
                                case BottomSheetBehavior.STATE_DRAGGING:
                                    view_fondo.setVisibility(View.VISIBLE);
                                    if(story!=null) story.pause();
                                    break;
                                case BottomSheetBehavior.STATE_COLLAPSED:
                                    view_fondo.setVisibility(View.GONE);
                                    if(pager!=null) pager.setUserInputEnabled(true);
                                    if(story!=null && !textoLargoShow && !estadoDescarga) story.resume();
                                    Utils.ocultarKeyBoard(mainActivity);
                                    break;
                                case BottomSheetBehavior.STATE_HIDDEN:
                                    view_fondo.setVisibility(View.GONE);
                                    if(pager!=null) pager.setUserInputEnabled(true);
                                    if(story!=null && !textoLargoShow && !estadoDescarga) story.resume();
                                    break;
                                case BottomSheetBehavior.STATE_HALF_EXPANDED:
                                    if(pager!=null) pager.setUserInputEnabled(false);
                                    if(story!=null) story.pause();
                                    break;
                                case BottomSheetBehavior.STATE_EXPANDED:
                                    if(pager!=null) pager.setUserInputEnabled(false);
                                    if(story!=null) story.pause();
                                    sacarTeclado();
                                    break;
                            }
                        }

                        @Override
                        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                            arrow_up.setRotation(slideOffset*180f);
                            view_fondo.setAlpha(slideOffset);
                        }
                    });

                    input_estado_send = view.findViewById(R.id.input_estado_send);
                    input_estado_send.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String cad = input_text.getText().toString();
                            if (cad.length() > 0){
                                ItemUsuario usuario = new ItemUsuario(correoActual);
                                ItemContacto contacto = new ItemContacto(correoActual, correoActual);
                                dbWorker.insertarNuevoUsuario(usuario);
                                dbWorker.insertarNuevoContactoNoVisible(contacto, true);

                                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
                                Date date = new Date();
                                String orden = sdf.format(date);

                                String id = "YouChat/chat/" + correoActual + "/18/" + orden;
                                String hora = Convertidor.conversionHora(orden);
                                String fecha = Convertidor.conversionFecha(orden);

                                input_text.setText("");

                                view_fondo.setVisibility(View.GONE);
                                bsb.setState(BottomSheetBehavior.STATE_COLLAPSED);

                                ItemChat newChat = new ItemChat(id,
                                        18, 1, correoActual, cad, "",
                                        hora, fecha, estadosList.get(posGen).getId(),
                                        YouChatApplication.correo, false, orden,false
                                        ,"",0,true);

                                if (YouChatApplication.estaAndandoChatService())
                                    YouChatApplication.chatService.enviarMensaje(newChat, SendMsg.CATEGORY_CHAT);
                                dbWorker.insertarChat(newChat);
                                dbWorker.actualizarUltMsgUsuario(newChat);
                                if(onMensajeEnviado!=null)
                                    onMensajeEnviado.Enviado(newChat);
                                Utils.runOnUIThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (principalActivity != null)
                                        principalActivity.actualizarNewMsg(correoActual,0);
                                    }
                                });
                            }
                            else Utils.ShowToastAnimated(mainActivity, "No puede estar vacío", R.raw.ic_ban);
                        }
                    });
                    pager.registerOnPageChangeCallback(onPageChangeCallback);

                    correoActual = estadosList.get(posIni).getCorreo();
                    ItemContacto contacto = dbWorker.obtenerContacto(correoActual);
                    if(correoActual.equals(YouChatApplication.idOficial)){
                        YouChatApplication.ponerIconOficial(mini_img_perfil_estados);
                    }
                    else {
                        Glide.with(context)
                                .load(contacto.getRuta_img())
                                .error(R.drawable.profile_white)
                                .into(mini_img_perfil_estados);
                    }

                    nombre_usuario_estado.setText(contacto.getNombreMostrar());
                    mensaje_respuesta_nombre.setText(""+contacto.getNombreMostrar());

                    input_text.setHint("Responder a "+contacto.getNombreMostrar());
                    if(!estadosList.get(posIni).getTexto().isEmpty()) mensaje_respuesta_texto.setText(""+estadosList.get(posIni).getTexto());
                    else {
                        mensaje_respuesta_texto.setText("");
                        mensaje_respuesta_texto.setHint("<Sin contenido>");
                    }
                }
            }
        }
        else {
            if(estadoPropio){
                if(YouChatApplication.estaAndandoChatService()){
                    YouChatApplication.chatService.eliminarNotiNowReaccion();
                }
//            int l=dato_estados.size();
//            String cor="";
//            for(int i=0; i<l; i++){
//                if(!cor.equals(dato_estados.get(i).getCorreo())){
//                    cor = dato_estados.get(i).getCorreo();
//
//                    if (cor.equals(YouChatApplication.correo) && dato_estados.get(i).getTipo_estado()<=99)
//                    {
//                        ArrayList<ItemEstado> estadosTemp = dbWorker.obtenerEstadosDe(cor);
//                        if(cor.equals(correoInicial) && posIni==-1){
//                            posIni = estadosList.size();
//                        }
//                        int lj = estadosTemp.size();
//                        for(int j=0; j<lj; j++){
//                            storyList.add(new ItemStory(j, lj, cor));
//                        }
//                        estadosList.addAll(estadosTemp);
//                    }
//                }
//            }
//            if(posIni==-1) posIni=0;

                String cor = YouChatApplication.correo;
                ArrayList<ItemEstado> estadosTemp = dbWorker.obtenerEstadosDe(cor);
                posIni = 0;
                int lj = estadosTemp.size();
                for(int j=0; j<lj; j++){
                    storyList.add(new ItemStory(j, lj, cor));
                }
                estadosList.addAll(estadosTemp);
                posGen = posIni;

                info_estado.setVisibility(View.VISIBLE);
                openMenu.setVisibility(View.GONE);
                animacion_reaccion.setVisibility(View.GONE);
                input_estado_reaccionar.setVisibility(View.GONE);

                view.findViewById(R.id.coordinate).setVisibility(View.GONE);
                View bottomSheetInternal = view.findViewById(R.id.coordinate_status);

                bsb = BottomSheetBehavior.from(bottomSheetInternal);
                bsb.setPeekHeight(Utils.dpToPx(context, 0f));
                bsb.setState(BottomSheetBehavior.STATE_COLLAPSED);
                bsb.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull View bottomSheet, int newState) {
                        switch (newState) {
                            case BottomSheetBehavior.STATE_DRAGGING:
                                view_fondo.setVisibility(View.VISIBLE);
                                if(story!=null) story.pause();
                                break;
                            case BottomSheetBehavior.STATE_COLLAPSED:
                                view_fondo.setVisibility(View.GONE);
                                if(pager!=null) pager.setUserInputEnabled(true);
                                if(story!=null && !textoLargoShow && !estadoDescarga) story.resume();
                                break;
                            case BottomSheetBehavior.STATE_HIDDEN:
                                view_fondo.setVisibility(View.GONE);
                                if(pager!=null) pager.setUserInputEnabled(true);
                                if(story!=null && !textoLargoShow && !estadoDescarga) story.resume();
                                break;
                            case BottomSheetBehavior.STATE_HALF_EXPANDED:
                                if(pager!=null) pager.setUserInputEnabled(false);
                                if(story!=null) story.pause();
                                break;
                            case BottomSheetBehavior.STATE_EXPANDED:
                                view_fondo.setVisibility(View.VISIBLE);
                                if(pager!=null) pager.setUserInputEnabled(false);
                                if(story!=null) story.pause();
                                break;
                        }
                    }
                    @Override
                    public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                        view_fondo.setAlpha(slideOffset);
                    }
                });
                pager.registerOnPageChangeCallback(onPageChangeCallbackPropio);
//            resetStoryBar(posIni);

                correoActual = correoInicial;
                Glide.with(context).load(YouChatApplication.ruta_img_perfil)
                        .error(R.drawable.profile_white)
                        .into(mini_img_perfil_estados);

                if(YouChatApplication.alias.isEmpty()) nombre_usuario_estado.setText(YouChatApplication.correo);
                nombre_usuario_estado.setText(YouChatApplication.alias);

                tv_total_reacciones = view.findViewById(R.id.tv_total_reacciones);
                tv_cant_total_vistas = view.findViewById(R.id.tv_cant_total_vistas);
                may_reac_1 = view.findViewById(R.id.may_reac_1);
                may_reac_2 = view.findViewById(R.id.may_reac_2);
                may_reac_3 = view.findViewById(R.id.may_reac_3);

                actualizarReacciones(estadosList.get(posIni));
                lista_info_reacciones=view.findViewById(R.id.lista_info_reacciones);
                showReaccion = view.findViewById(R.id.showReaccion);
                showViews = view.findViewById(R.id.showViews);

                anim_emoji_me_gusta = view.findViewById(R.id.anim_emoji_me_gusta);
                anim_emoji_me_encanta = view.findViewById(R.id.anim_emoji_me_encanta);
                anim_emoji_me_sonroja = view.findViewById(R.id.anim_emoji_me_sonroja);
                anim_emoji_me_divierte = view.findViewById(R.id.anim_emoji_me_divierte);
                anim_emoji_me_asombra = view.findViewById(R.id.anim_emoji_me_asombra);
                anim_emoji_me_entristese = view.findViewById(R.id.anim_emoji_me_entristese);
                anim_emoji_me_enoja = view.findViewById(R.id.anim_emoji_me_enoja);

                Utils.runOnUIThread(()->{
                    anim_emoji_me_gusta.setAnimation(R.raw.like1);
                    anim_emoji_me_encanta.setAnimation(R.raw.encanta);
                    anim_emoji_me_sonroja.setAnimation(R.raw.sonroja);
                    anim_emoji_me_divierte.setAnimation(R.raw.divierte);
                    anim_emoji_me_asombra.setAnimation(R.raw.asombra);
                    anim_emoji_me_entristese.setAnimation(R.raw.entristece);
                    anim_emoji_me_enoja.setAnimation(R.raw.enoja);

                    View.OnClickListener onClick = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((LottieAnimationView)v).playAnimation();
                        }
                    };
                    anim_emoji_me_gusta.setOnClickListener(onClick);
                    anim_emoji_me_encanta.setOnClickListener(onClick);
                    anim_emoji_me_sonroja.setOnClickListener(onClick);
                    anim_emoji_me_divierte.setOnClickListener(onClick);
                    anim_emoji_me_asombra.setOnClickListener(onClick);
                    anim_emoji_me_entristese.setOnClickListener(onClick);
                    anim_emoji_me_enoja.setOnClickListener(onClick);
                });


                showReaccion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showBSD(view, 0, estadosList.get(posGen));
                        bsb.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                });

                showViews.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showBSD(view, 1, estadosList.get(posGen));
                        bsb.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                });
            }
            else{
                if(esNuevo){
                    int l=dato_estados.size();
                    String cor="";
                    for(int i=0; i<l; i++){
                        cor = dato_estados.get(i).getCorreo();
                        if (!cor.equals(YouChatApplication.correo) && dato_estados.get(i).getTipo_estado()<=99)
                        {
                            ArrayList<ItemEstado> estadosTemp = dbWorker.obtenerEstadosNuevosDe(cor);
                            if(cor.equals(correoInicial) && posIni==-1){
                                posIni = estadosList.size();
                            }
                            int lj = estadosTemp.size();
                            if(lj>0){
                                for(int j=0; j<lj; j++){
                                    storyList.add(new ItemStory(j, lj, cor));
                                }
                                estadosList.addAll(estadosTemp);
                            }
                        }
                    }
                    if(posIni==-1) posIni=0;
                }
                else {
                    int l=dato_estados.size();
                    String cor="";
                    for(int i=0; i<l; i++){
                        cor = dato_estados.get(i).getCorreo();
                        if (!cor.equals(YouChatApplication.correo) && dato_estados.get(i).getTipo_estado()<=99)
                        {
                            ArrayList<ItemEstado> estadosTemp = dbWorker.obtenerEstadosDe(cor);
                            if(cor.equals(correoInicial) && posIni==-1){
                                posIni = estadosList.size();
                            }
                            int lj = estadosTemp.size();
//                            boolean entro = false;
                            for(int j=0; j<lj; j++){
                                storyList.add(new ItemStory(j, lj, cor));
//                                if(cor.equals(correoInicial)
//                                        && !estadosTemp.get(j).isEsta_visto()
//                                        && posIni!=-1
//                                        && !entro){
//                                    entro=true;
//                                    posIni += j;
//                                }
                            }
                            estadosList.addAll(estadosTemp);
//                    storyList.add(new ItemStory(estadosTemp.size(), cor));
                        }
                    }
                    if(posIni==-1) posIni=0;
                }

                posGen = posIni;

                mensaje_respuesta_nombre=view.findViewById(R.id.mensaje_respuesta_nombre);
                mensaje_respuesta_texto=view.findViewById(R.id.mensaje_respuesta_texto);
                input_text=view.findViewById(R.id.input_text);
                emojiButton=view.findViewById(R.id.input_emoji);
                setUpEmojiPopup();
                emojiButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        emojiPopup.toggle();
                    }
                });

                info_estado.setVisibility(View.GONE);
                openMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PopupOptionEstados popupOptionEstados = new PopupOptionEstados(view, estadosList.get(posGen));
                        popupOptionEstados.setOnDismiss(new PopupOptionEstados.onDismissListener() {
                            @Override
                            public void onDismiss() {
                                playStory();
                            }

                            @Override
                            public void onShow() {
                                pauseStory();
                            }
                        });
                        popupOptionEstados.show(view);
                    }
                });

                bottomSheetInternal = view.findViewById(R.id.coordinate);
                view.findViewById(R.id.coordinate_status).setVisibility(View.GONE);
                bsb = BottomSheetBehavior.from(bottomSheetInternal);

                arrow_up = view.findViewById(R.id.arrow_up);
                arrow_up.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(bsb.getState()!=BottomSheetBehavior.STATE_EXPANDED){
                            view_fondo.setVisibility(View.VISIBLE);
                            bsb.setState(BottomSheetBehavior.STATE_EXPANDED);
                        }
                        else {
                            view_fondo.setVisibility(View.GONE);
                            bsb.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        }
                    }
                });
                bsb.setPeekHeight(Utils.dpToPx(context, 110f));
                bsb.setState(BottomSheetBehavior.STATE_COLLAPSED);
                bsb.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull View bottomSheet, int newState) {
                        switch (newState) {
                            case BottomSheetBehavior.STATE_DRAGGING:
                                view_fondo.setVisibility(View.VISIBLE);
                                if(story!=null) story.pause();
                                break;
                            case BottomSheetBehavior.STATE_COLLAPSED:
                                view_fondo.setVisibility(View.GONE);
                                if(pager!=null) pager.setUserInputEnabled(true);
                                if(story!=null && !textoLargoShow && !estadoDescarga) story.resume();
                                Utils.ocultarKeyBoard(mainActivity);
                                break;
                            case BottomSheetBehavior.STATE_HIDDEN:
                                view_fondo.setVisibility(View.GONE);
                                if(pager!=null) pager.setUserInputEnabled(true);
                                if(story!=null && !textoLargoShow && !estadoDescarga) story.resume();
                                break;
                            case BottomSheetBehavior.STATE_HALF_EXPANDED:
                                if(pager!=null) pager.setUserInputEnabled(false);
                                if(story!=null) story.pause();
                                break;
                            case BottomSheetBehavior.STATE_EXPANDED:
                                if(pager!=null) pager.setUserInputEnabled(false);
                                if(story!=null) story.pause();
                                sacarTeclado();
                                break;
                        }
                    }

                    @Override
                    public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                        arrow_up.setRotation(slideOffset*180f);
                        view_fondo.setAlpha(slideOffset);
                    }
                });

                input_estado_send = view.findViewById(R.id.input_estado_send);
                input_estado_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String cad = input_text.getText().toString();
                        if (cad.length() > 0){
                            ItemUsuario usuario = new ItemUsuario(correoActual);
                            ItemContacto contacto = new ItemContacto(correoActual, correoActual);
                            dbWorker.insertarNuevoUsuario(usuario);
                            dbWorker.insertarNuevoContactoNoVisible(contacto, true);

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
                            Date date = new Date();
                            String orden = sdf.format(date);

                            String id = "YouChat/chat/" + correoActual + "/18/" + orden;
                            String hora = Convertidor.conversionHora(orden);
                            String fecha = Convertidor.conversionFecha(orden);
                            input_text.setText("");

                            view_fondo.setVisibility(View.GONE);
                            bsb.setState(BottomSheetBehavior.STATE_COLLAPSED);

                            ItemChat newChat = new ItemChat(id,
                                    18, 1, correoActual, cad, "",
                                    hora, fecha, estadosList.get(posGen).getId(),
                                    YouChatApplication.correo, false, orden,false
                                    ,"",0,true);

                            if (YouChatApplication.estaAndandoChatService())
                                YouChatApplication.chatService.enviarMensaje(newChat, SendMsg.CATEGORY_CHAT);
                            dbWorker.insertarChat(newChat);
                            dbWorker.actualizarUltMsgUsuario(newChat);
                            if(onMensajeEnviado!=null)
                                onMensajeEnviado.Enviado(newChat);
                            if (principalActivity != null) {
                                Utils.runOnUIThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        principalActivity.actualizarNewMsg(correoActual,0);
                                    }
                                });
                            }
                        }
                        else Utils.ShowToastAnimated(mainActivity, "No puede estar vacío", R.raw.ic_ban);
                    }
                });
                pager.registerOnPageChangeCallback(onPageChangeCallback);

                correoActual = estadosList.get(posIni).getCorreo();
                ItemContacto contacto = dbWorker.obtenerContacto(correoActual);
                if(correoActual.equals(YouChatApplication.idOficial)){
                    YouChatApplication.ponerIconOficial(mini_img_perfil_estados);
                }
                else {
                    Glide.with(context)
                            .load(contacto.getRuta_img())
                            .error(R.drawable.profile_white)
                            .into(mini_img_perfil_estados);
                }

                nombre_usuario_estado.setText(contacto.getNombreMostrar());
                mensaje_respuesta_nombre.setText(""+contacto.getNombreMostrar());

                input_text.setHint("Responder a "+contacto.getNombreMostrar());
                if(!estadosList.get(posIni).getTexto().isEmpty()) mensaje_respuesta_texto.setText(""+estadosList.get(posIni).getTexto());
                else {
                    mensaje_respuesta_texto.setText("");
                    mensaje_respuesta_texto.setHint("<Sin contenido>");
                }
            }
        }


        fecha_subida_estado.setText("" + Convertidor.convertirFechaAFechaLinda(estadosList.get(posIni).getFecha())+ ", " + estadosList.get(posIni).getHora());

        if(posIni>=0 && posIni<estadosList.size()) pager.setCurrentItem(posIni,false);
        else pager.setCurrentItem(0);
        ItemEstado estado = dbWorker.obtenerEstado(estadosList.get(posGen).getId());
        if(!estado.isDescargado()){
            ocultarViewsDescarga();
        }
    }

    private void sacarTeclado(){
        input_text.setFocusableInTouchMode(true);
        input_text.requestFocus();
        InputMethodManager inputMethodManager= (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(input_text, InputMethodManager.SHOW_IMPLICIT);
    }

    private void setUpEmojiPopup() {
        emojiPopup = EmojiPopup.Builder.fromRootView(root)
                .setOnEmojiPopupShownListener(new OnEmojiPopupShownListener() {
                    @Override
                    public void onEmojiPopupShown() {
                        emojiButton.setImageResource(R.drawable.input_keyboard);
                    }
                })
                .setOnEmojiPopupDismissListener(new OnEmojiPopupDismissListener() {
                    @Override
                    public void onEmojiPopupDismiss() {
                        emojiButton.setImageResource(R.drawable.input_emoji);
                    }
                })
                .setKeyboardAnimationStyle(R.style.emoji_fade_animation_style)
                .setPageTransformer(new PageTransformer())
                .build(input_text,false);
    }

    @Override
    public void onNext() {
        Log.e(TAG, "onNext: ");
        if(posGen+1<estadosList.size()) pager.setCurrentItem(posGen+1, true);
//        else onComplete();
    }

    @Override
    public void onPrev() {
        Log.e(TAG, "onPrev: ");
        if(posGen-1>=0) pager.setCurrentItem(posGen-1, true);
//        else onComplete();
    }

    @Override
    public void onComplete() {
        Log.e(TAG, "onComplete: ");
        if(!isComplete){
            isComplete = true;
            if(posGen+1<estadosList.size()) {
                Log.e(TAG, "setCurrentItem: ");
                pager.setCurrentItem(posGen+1, true);
            }
            else {
                Log.e(TAG, "atrasFragment: ");
                mainActivity.atrasFragment();
            }
            Utils.runOnUIThread(()->{
                isComplete = false;
            },1000);
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter() {
            super(EstadosViewPagerFragment.this);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return EstadosViewPagerLayout.newInstance(EstadosViewPagerFragment.this, dbWorker.obtenerEstado(estadosList.get(position).getId()));
        }

        @Override
        public int getItemCount() {
            return estadosList.size();
        }
    }

    private synchronized void actualizarReacciones(ItemEstado estado) {
        tv_total_reacciones.setText(""+estado.totalReacciones());

        may_reac_1.setVisibility(View.GONE);
        may_reac_2.setVisibility(View.GONE);
        may_reac_3.setVisibility(View.GONE);

        int[] reacPopup = estado.obtenerTresReaccionesPopulares();
        if(reacPopup[0]!=0){
            may_reac_1.setVisibility(View.VISIBLE);
            switch (reacPopup[0]){
                case 1:
                    may_reac_1.setAnimation(R.raw.like1);
                    break;
                case 2:
                    may_reac_1.setAnimation(R.raw.encanta);
                    break;
                case 3:
                    may_reac_1.setAnimation(R.raw.sonroja);
                    break;
                case 4:
                    may_reac_1.setAnimation(R.raw.divierte);
                    break;
                case 5:
                    may_reac_1.setAnimation(R.raw.asombra);
                    break;
                case 6:
                    may_reac_1.setAnimation(R.raw.entristece);
                    break;
                case 7:
                    may_reac_1.setAnimation(R.raw.enoja);
                    break;
            }
        }
        if(reacPopup[1]!=0){
            may_reac_2.setVisibility(View.VISIBLE);

            switch (reacPopup[1]){
                case 1:
                    may_reac_2.setAnimation(R.raw.like1);
                    break;
                case 2:
                    may_reac_2.setAnimation(R.raw.encanta);
                    break;
                case 3:
                    may_reac_2.setAnimation(R.raw.sonroja);
                    break;
                case 4:
                    may_reac_2.setAnimation(R.raw.divierte);
                    break;
                case 5:
                    may_reac_2.setAnimation(R.raw.asombra);
                    break;
                case 6:
                    may_reac_2.setAnimation(R.raw.entristece);
                    break;
                case 7:
                    may_reac_2.setAnimation(R.raw.enoja);
                    break;
            }
        }
        if(reacPopup[2]!=0){
            may_reac_3.setVisibility(View.VISIBLE);

            switch (reacPopup[2]){
                case 1:
                    may_reac_3.setAnimation(R.raw.like1);
                    break;
                case 2:
                    may_reac_3.setAnimation(R.raw.encanta);
                    break;
                case 3:
                    may_reac_3.setAnimation(R.raw.sonroja);
                    break;
                case 4:
                    may_reac_3.setAnimation(R.raw.divierte);
                    break;
                case 5:
                    may_reac_3.setAnimation(R.raw.asombra);
                    break;
                case 6:
                    may_reac_3.setAnimation(R.raw.entristece);
                    break;
                case 7:
                    may_reac_3.setAnimation(R.raw.enoja);
                    break;
            }
        }
        tv_cant_total_vistas.setText(dbWorker.obtenerCantVistasEstadosDe(estado.getId())+"");
    }

    private void showBSD(View v, int tipo, ItemEstado estado){
        if(tipo==0)
        {
            v.findViewById(R.id.ll_reacc).setVisibility(View.VISIBLE);
            v.findViewById(R.id.view).setVisibility(View.VISIBLE);
            v.findViewById(R.id.tv_visto_por).setVisibility(View.GONE);

            TextView tv_cant_me_gusta_bs = v.findViewById(R.id.tv_cant_me_gusta_bs);
            TextView tv_cant_me_encanta_bs = v.findViewById(R.id.tv_cant_me_encanta_bs);
            TextView tv_cant_me_sonroja_bs = v.findViewById(R.id.tv_cant_me_sonroja_bs);
            TextView tv_cant_me_divierte_bs = v.findViewById(R.id.tv_cant_me_divierte_bs);
            TextView tv_cant_me_asombra_bs = v.findViewById(R.id.tv_cant_me_asombra_bs);
            TextView tv_cant_me_entristese_bs = v.findViewById(R.id.tv_cant_me_entristese_bs);
            TextView tv_cant_me_enoja_bs = v.findViewById(R.id.tv_cant_me_enoja_bs);

            tv_cant_me_gusta_bs.setText(""+estado.getCant_me_gusta());
            tv_cant_me_encanta_bs.setText(""+estado.getCant_me_encanta());
            tv_cant_me_sonroja_bs.setText(""+estado.getCant_me_sonroja());
            tv_cant_me_divierte_bs.setText(""+estado.getCant_me_divierte());
            tv_cant_me_asombra_bs.setText(""+estado.getCant_me_asombra());
            tv_cant_me_entristese_bs.setText(""+estado.getCant_me_entristese());
            tv_cant_me_enoja_bs.setText(""+estado.getCant_me_enoja());

            lista_info_reacciones.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL,false));
            datos_reacciones_estados = dbWorker.obtenerReaccionesEstadosDelEstado(estado.getId());
            mAdapter = new AdaptadorDatosReaccionEstado(context,datos_reacciones_estados);
            lista_info_reacciones.setAdapter(mAdapter);
        }
        else
        {
            v.findViewById(R.id.ll_reacc).setVisibility(View.GONE);
            v.findViewById(R.id.view).setVisibility(View.GONE);
            v.findViewById(R.id.tv_visto_por).setVisibility(View.VISIBLE);

            lista_info_reacciones.setLayoutManager(new LinearLayoutManager(context,RecyclerView.VERTICAL,true));
            datos_vistas_estados = dbWorker.obtenerVistasEstadosDe(estado.getId());//.obtenerReaccionesEstadosDelEstado(estado.getId());
            adaptadorDatosUsuarioView = new AdaptadorDatosEstadoViews(context,datos_vistas_estados);
            lista_info_reacciones.setAdapter(adaptadorDatosUsuarioView);
        }
    }

    public void enviarReaccion(int reaccion) {
        if (YouChatApplication.estaAndandoChatService()) {
            if (YouChatApplication.chatService.hayConex) {
                input_estado_reaccionar.setVisibility(View.GONE);
                input_estado_reaccionar.setOnClickListener(null);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
                Date date = new Date();
                String fechaEntera = sdf.format(date);
                String hora = Convertidor.conversionHora(fechaEntera);
                String fecha = Convertidor.conversionFecha(fechaEntera);
                ItemChat msgReaccion;

                switch (reaccion) {
                    case 1:
                        input_estado_reaccionar.setImageDrawable(null);
                        estadosList.get(posGen).setCant_me_gusta(1);
                        animacion_icono_reaccion.setAnimation(R.raw.like1);
                        animacion_icono_reaccion.playAnimation();
                        Utils.reproducirSonido(R.raw.like, context);
                        dbWorker.sumarUnaReaccion(estadosList.get(posGen).getId(), "1", 1);
                        msgReaccion = new ItemChat(correoActual, estadosList.get(posGen).getId());
                        msgReaccion.setId("1");
                        msgReaccion.setHora(hora);
                        msgReaccion.setFecha(fecha);
                        YouChatApplication.chatService.enviarMensaje(msgReaccion, SendMsg.CATEGORY_ESTADO_REACCIONAR);
                        break;
                    case 2:
                        input_estado_reaccionar.setImageDrawable(null);
                        estadosList.get(posGen).setCant_me_encanta(1);
                        animacion_icono_reaccion.setAnimation(R.raw.encanta);
                        animacion_icono_reaccion.playAnimation();
                        Utils.reproducirSonido(R.raw.like, context);
                        dbWorker.sumarUnaReaccion(estadosList.get(posGen).getId(), "2", 1);
                        msgReaccion = new ItemChat(correoActual, estadosList.get(posGen).getId());
                        msgReaccion.setId("2");
                        msgReaccion.setHora(hora);
                        msgReaccion.setFecha(fecha);
                        YouChatApplication.chatService.enviarMensaje(msgReaccion, SendMsg.CATEGORY_ESTADO_REACCIONAR);
                        break;
                    case 3:
                        input_estado_reaccionar.setImageDrawable(null);
                        estadosList.get(posGen).setCant_me_sonroja(1);
                        animacion_icono_reaccion.setAnimation(R.raw.sonroja);
                        animacion_icono_reaccion.playAnimation();
                        Utils.reproducirSonido(R.raw.like, context);
                        dbWorker.sumarUnaReaccion(estadosList.get(posGen).getId(), "3", 1);
                        msgReaccion = new ItemChat(correoActual, estadosList.get(posGen).getId());
                        msgReaccion.setId("3");
                        msgReaccion.setHora(hora);
                        msgReaccion.setFecha(fecha);
                        YouChatApplication.chatService.enviarMensaje(msgReaccion, SendMsg.CATEGORY_ESTADO_REACCIONAR);
                        break;
                    case 4:
                        input_estado_reaccionar.setImageDrawable(null);
                        estadosList.get(posGen).setCant_me_divierte(1);
                        animacion_icono_reaccion.setAnimation(R.raw.divierte);
                        animacion_icono_reaccion.playAnimation();
                        Utils.reproducirSonido(R.raw.risita, context);
                        dbWorker.sumarUnaReaccion(estadosList.get(posGen).getId(), "4", 1);
                        msgReaccion = new ItemChat(correoActual, estadosList.get(posGen).getId());
                        msgReaccion.setId("4");
                        msgReaccion.setHora(hora);
                        msgReaccion.setFecha(fecha);
                        YouChatApplication.chatService.enviarMensaje(msgReaccion, SendMsg.CATEGORY_ESTADO_REACCIONAR);
                        break;
                    case 5:
                        input_estado_reaccionar.setImageDrawable(null);
                        estadosList.get(posGen).setCant_me_asombra(1);
                        animacion_icono_reaccion.setAnimation(R.raw.asombra);
                        animacion_icono_reaccion.playAnimation();
                        Utils.reproducirSonido(R.raw.wow, context);
                        dbWorker.sumarUnaReaccion(estadosList.get(posGen).getId(), "5", 1);
                        msgReaccion = new ItemChat(correoActual, estadosList.get(posGen).getId());
                        msgReaccion.setId("5");
                        msgReaccion.setHora(hora);
                        msgReaccion.setFecha(fecha);
                        YouChatApplication.chatService.enviarMensaje(msgReaccion, SendMsg.CATEGORY_ESTADO_REACCIONAR);
                        break;
                    case 6:
                        input_estado_reaccionar.setImageDrawable(null);
                        estadosList.get(posGen).setCant_me_entristese(1);
                        animacion_icono_reaccion.setAnimation(R.raw.entristece);
                        animacion_icono_reaccion.playAnimation();
                        Utils.reproducirSonido(R.raw.triston, context);
                        dbWorker.sumarUnaReaccion(estadosList.get(posGen).getId(), "6", 1);
                        msgReaccion = new ItemChat(correoActual, estadosList.get(posGen).getId());
                        msgReaccion.setId("6");
                        msgReaccion.setHora(hora);
                        msgReaccion.setFecha(fecha);
                        YouChatApplication.chatService.enviarMensaje(msgReaccion, SendMsg.CATEGORY_ESTADO_REACCIONAR);
                        break;
                    case 7:
                        input_estado_reaccionar.setImageDrawable(null);
                        estadosList.get(posGen).setCant_me_enoja(1);
                        animacion_icono_reaccion.setAnimation(R.raw.enoja);
                        animacion_icono_reaccion.playAnimation();
                        Utils.reproducirSonido(R.raw.like, context);
                        dbWorker.sumarUnaReaccion(estadosList.get(posGen).getId(), "7", 1);
                        msgReaccion = new ItemChat(correoActual, estadosList.get(posGen).getId());
                        msgReaccion.setId("7");
                        msgReaccion.setHora(hora);
                        msgReaccion.setFecha(fecha);
                        YouChatApplication.chatService.enviarMensaje(msgReaccion, SendMsg.CATEGORY_ESTADO_REACCIONAR);
                        break;
                    default:
                        input_estado_reaccionar.setImageResource(R.drawable.reac0);
                        animacion_icono_reaccion.setImageDrawable(null);
                        input_estado_reaccionar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                PopupReaccionEstados popupReaccionEstados = new PopupReaccionEstados(v, EstadosViewPagerFragment.this);
                                popupReaccionEstados.setOnDismiss(new PopupReaccionEstados.onDismissListener() {
                                    @Override
                                    public void onDismiss() {
                                        playStory();
                                    }

                                    @Override
                                    public void onShow() {
                                        pauseStory();
                                    }
                                });
                                popupReaccionEstados.show(v);
                            }
                        });
                }
            } else
                Utils.ShowToastAnimated(mainActivity, "Compruebe su conexión", R.raw.ic_ban);
        }
    }
    public void verificarReaccion(int reaccion) {
        input_estado_reaccionar.setOnClickListener(null);
        animacion_icono_reaccion.setImageDrawable(null);
        switch (reaccion) {
            case 1:
                input_estado_reaccionar.setImageDrawable(null);
                animacion_icono_reaccion.setAnimation(R.raw.like1);
                animacion_icono_reaccion.playAnimation();
                break;
            case 2:
                input_estado_reaccionar.setImageDrawable(null);
                animacion_icono_reaccion.setAnimation(R.raw.encanta);
                animacion_icono_reaccion.playAnimation();
                break;
            case 3:
                input_estado_reaccionar.setImageDrawable(null);
                animacion_icono_reaccion.setAnimation(R.raw.sonroja);
                animacion_icono_reaccion.playAnimation();
                break;
            case 4:
                input_estado_reaccionar.setImageDrawable(null);
                animacion_icono_reaccion.setAnimation(R.raw.divierte);
                animacion_icono_reaccion.playAnimation();
                break;
            case 5:
                input_estado_reaccionar.setImageDrawable(null);
                animacion_icono_reaccion.setAnimation(R.raw.asombra);
                animacion_icono_reaccion.playAnimation();
                break;
            case 6:
                input_estado_reaccionar.setImageDrawable(null);
                animacion_icono_reaccion.setAnimation(R.raw.entristece);
                animacion_icono_reaccion.playAnimation();
                break;
            case 7:
                input_estado_reaccionar.setImageDrawable(null);
                animacion_icono_reaccion.setAnimation(R.raw.enoja);
                animacion_icono_reaccion.playAnimation();
                break;
            default:
                input_estado_reaccionar.setImageResource(R.drawable.reac0);
                input_estado_reaccionar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupReaccionEstados popupReaccionEstados = new PopupReaccionEstados(v, EstadosViewPagerFragment.this);
                        popupReaccionEstados.setOnDismiss(new PopupReaccionEstados.onDismissListener() {
                            @Override
                            public void onDismiss() {
                                playStory();
                            }

                            @Override
                            public void onShow() {
                                pauseStory();
                            }
                        });
                        popupReaccionEstados.show(v);
                    }
                });
        }
    }

    private class ZoomOutPageTransformer implements ViewPager2.PageTransformer{
        float MIN_SCALE=0.85f;
        float MIN_ALPHA=0.5f;

        @Override
        public void transformPage(@NonNull View view, float position) {
            int pageWidth=view.getWidth();
            int pageHeight=view.getHeight();
            if(position<-1) view.setAlpha(0f);
            else if(position<=1){
                float scaleFactor = Math.max(MIN_SCALE,1-Math.abs(position));
                float vertMargin = pageHeight*(1-scaleFactor)/2;
                float horzMargin = pageWidth*(1-scaleFactor)/2;

                if(position<0) view.setTranslationX(horzMargin - vertMargin/2);
                else view.setTranslationX(-horzMargin + vertMargin/2);

                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                view.setAlpha(MIN_ALPHA+(scaleFactor-MIN_SCALE)/(1-MIN_SCALE)*(1-MIN_ALPHA));
            }
            else view.setAlpha(0f);
        }


        /*
        * ////////////////////////
        float DEFAULT_TRANS_X = .0f;
        float DEFAULT_TRANS_FACTOR = 1.2f;
        float SCALE_FACTOR = .14f;
        float DEFAULT_SCALE = 1f;
        float ALPHA_FACTOR = .3f;
        float DEFAULT_ALPHA = 1f;

        @Override
        public void transformPage(@NonNull View view, float position) {
            int pageWidth=view.getWidth();
            int pageHeight=view.getHeight();
//            Log.e("transformPage", "position: "+position);
            if(!correoActual.isEmpty()) correoActual = estadosList.get(posGen).getCorreo();
            else if(!estadosList.get(posGen+1).getCorreo().equals(correoActual)){
                float scaleFactor = -SCALE_FACTOR * position + DEFAULT_SCALE;
                float alphaFactor = -ALPHA_FACTOR * position + DEFAULT_ALPHA;

                if(position<=0f){
                    view.setTranslationX(DEFAULT_TRANS_X);
                    view.setScaleX(DEFAULT_SCALE);
                    view.setScaleY(DEFAULT_SCALE);
                    view.setAlpha(DEFAULT_ALPHA+position);
                }
                if(position<=1){
                    view.setTranslationX(-(pageWidth / DEFAULT_TRANS_FACTOR) * position);
                    view.setScaleX(scaleFactor);
                    view.setScaleY(scaleFactor);
                    view.setAlpha(alphaFactor);
                }
                else{
                    view.setTranslationX(DEFAULT_TRANS_X);
                    view.setScaleX(DEFAULT_SCALE);
                    view.setScaleY(DEFAULT_SCALE);
                    view.setAlpha(DEFAULT_ALPHA);
                }
            }
            else{
                if(position<-1) view.setAlpha(0f);
                else if(position<=1){
                    float scaleFactor = Math.max(MIN_SCALE,1-Math.abs(position));
                    float vertMargin = pageHeight*(1-scaleFactor)/2;
                    float horzMargin = pageWidth*(1-scaleFactor)/2;

                    if(position<0) view.setTranslationX(horzMargin - vertMargin/2);
                    else view.setTranslationX(-horzMargin + vertMargin/2);

                    view.setScaleX(scaleFactor);
                    view.setScaleY(scaleFactor);

                    view.setAlpha(MIN_ALPHA+(scaleFactor-MIN_SCALE)/(1-MIN_SCALE)*(1-MIN_ALPHA));
                }
                else view.setAlpha(0f);
            }
        }*/
    }

    /*private class ZoomOutPageTransformer implements ViewPager2.PageTransformer{
        float MIN_SCALE=0.85f;
        float MIN_ALPHA=0.5f;

        @Override
        public void transformPage(@NonNull View view, float position) {
            int pageWidth=view.getWidth();
            int pageHeight=view.getHeight();
            //if(correoActual.isEmpty() || !correoActual.equals(estadosList.get(posGen).getCorreo())) correoActual = estadosList.get(posGen).getCorreo();
            if(isSwipeToLeft){//PALANTE
//                Log.e("isSwipeToLeft", "posGen: "+posGen+" *** "+correoActual+" > "+estadosList.get(posGen+1).getCorreo());
                if(posGen+1<estadosList.size() && !estadosList.get(posGen+1).getCorreo().equals(correoActual)) trans=true;
                else trans=false;
            }
            else {//DETRAS
//                Log.e("isSwipeToLeft", "posGen: "+posGen+" *** "+correoActual+" > "+estadosList.get(posGen-1).getCorreo());
                if(posGen-1>=0 && !estadosList.get(posGen-1).getCorreo().equals(correoActual)) trans=true;
                else trans=false;
            }

            if(trans){
                if(position<-1) view.setAlpha(0f);
                else if(position<=1){
                    float scaleFactor = Math.max(MIN_SCALE,1-Math.abs(position));
                    float vertMargin = pageHeight*(1-scaleFactor)/2;
                    float horzMargin = pageWidth*(1-scaleFactor)/2;

                    if(position<0) view.setTranslationX(horzMargin - vertMargin/2);
                    else view.setTranslationX(-horzMargin + vertMargin/2);

                    view.setScaleX(scaleFactor);
                    view.setScaleY(scaleFactor);

                    view.setAlpha(MIN_ALPHA+(scaleFactor-MIN_SCALE)/(1-MIN_SCALE)*(1-MIN_ALPHA));
                }
                else view.setAlpha(0f);
            }
            else{
                view.setTranslationX(0f);
                view.setScaleX(1f);
                view.setScaleY(1f);
                view.setAlpha(1f);
            }
        }

        *//*
        * ////////////////////////
        float DEFAULT_TRANS_X = .0f;
        float DEFAULT_TRANS_FACTOR = 1.2f;
        float SCALE_FACTOR = .14f;
        float DEFAULT_SCALE = 1f;
        float ALPHA_FACTOR = .3f;
        float DEFAULT_ALPHA = 1f;

        @Override
        public void transformPage(@NonNull View view, float position) {
            int pageWidth=view.getWidth();
            int pageHeight=view.getHeight();
//            Log.e("transformPage", "position: "+position);
            if(!correoActual.isEmpty()) correoActual = estadosList.get(posGen).getCorreo();
            else if(!estadosList.get(posGen+1).getCorreo().equals(correoActual)){
                float scaleFactor = -SCALE_FACTOR * position + DEFAULT_SCALE;
                float alphaFactor = -ALPHA_FACTOR * position + DEFAULT_ALPHA;

                if(position<=0f){
                    view.setTranslationX(DEFAULT_TRANS_X);
                    view.setScaleX(DEFAULT_SCALE);
                    view.setScaleY(DEFAULT_SCALE);
                    view.setAlpha(DEFAULT_ALPHA+position);
                }
                if(position<=1){
                    view.setTranslationX(-(pageWidth / DEFAULT_TRANS_FACTOR) * position);
                    view.setScaleX(scaleFactor);
                    view.setScaleY(scaleFactor);
                    view.setAlpha(alphaFactor);
                }
                else{
                    view.setTranslationX(DEFAULT_TRANS_X);
                    view.setScaleX(DEFAULT_SCALE);
                    view.setScaleY(DEFAULT_SCALE);
                    view.setAlpha(DEFAULT_ALPHA);
                }
            }
            else{
                if(position<-1) view.setAlpha(0f);
                else if(position<=1){
                    float scaleFactor = Math.max(MIN_SCALE,1-Math.abs(position));
                    float vertMargin = pageHeight*(1-scaleFactor)/2;
                    float horzMargin = pageWidth*(1-scaleFactor)/2;

                    if(position<0) view.setTranslationX(horzMargin - vertMargin/2);
                    else view.setTranslationX(-horzMargin + vertMargin/2);

                    view.setScaleX(scaleFactor);
                    view.setScaleY(scaleFactor);

                    view.setAlpha(MIN_ALPHA+(scaleFactor-MIN_SCALE)/(1-MIN_SCALE)*(1-MIN_ALPHA));
                }
                else view.setAlpha(0f);
            }
        }*//*
    }*/
    /* TODO HACIA DELANTE O ATRAS VIEWPAGER2
    * @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            if(estadoScroll==-1){
                estadoScroll=1;
                isSwipeToLeft = position + positionOffset > sumPositionAndPositionOffset;
            }
            if(positionOffsetPixelsAnt==-1)
                positionOffsetPixelsAnt = positionOffsetPixels;
            if(Math.abs(positionOffsetPixelsAnt-positionOffsetPixels)<50) estadoScroll=-1;

            sumPositionAndPositionOffset = position + positionOffset;
        }
    * */

    private OnMensajeEnviado onMensajeEnviado;
    public void setOnMensajeEnviado(OnMensajeEnviado onMensajeEnviado){
        this.onMensajeEnviado = onMensajeEnviado;
    }
    public interface OnMensajeEnviado{
        void Enviado(ItemChat chat);
    }
}