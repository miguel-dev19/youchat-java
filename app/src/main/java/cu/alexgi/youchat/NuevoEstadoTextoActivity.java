package cu.alexgi.youchat;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.listeners.OnEmojiPopupDismissListener;
import com.vanniktech.emoji.listeners.OnEmojiPopupShownListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import cu.alexgi.youchat.adapters.AdaptadorColorTarjeta;
import cu.alexgi.youchat.adapters.AdaptadorDatosFondoEstadoTexto;
import cu.alexgi.youchat.items.ItemChat;
import cu.alexgi.youchat.items.ItemEstado;
import cu.alexgi.youchat.swipebackfragment.BaseSwipeBackFragment;
import cu.alexgi.youchat.swipebackfragment.SwipeBackLayout;

import static cu.alexgi.youchat.MainActivity.context;
import static cu.alexgi.youchat.MainActivity.dbWorker;
import static cu.alexgi.youchat.MainActivity.mainActivity;

public class NuevoEstadoTextoActivity extends BaseSwipeBackFragment {

    private ImageView emojiButton;
    private EmojiPopup emojiPopup;

    private int valAnterior;
    private View root_view_neta;

    private View fondo_estado_texto;
    private FloatingActionButton fab_publicar_estado;
    private EmojiEditText et_texto_estado;

    private RecyclerView color_fondo_estado;
    private AdaptadorColorTarjeta adaptadorColorTarjeta;
    private LinearLayoutManager linearLayoutManager;

    private ArrayList<Integer> degradados;
    private RecyclerView degradado_fondo_estado;
    private AdaptadorDatosFondoEstadoTexto adaptadorDatosFondoEstadoTexto;

    private ArrayList<String> seguidores;
//    private NavController navController;
    private int colorEstado, tipoLetra=0;

    ///fondo color
    private View input_color_fondo_estado, input_color_fondo_estado_salir, input_change_font;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        YouChatApplication.nuevoEstadoTextoActivity=null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return attachToSwipeBack(inflater.inflate(R.layout.activity_nuevo_estado_texto, container, false));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        YouChatApplication.nuevoEstadoTextoActivity = this;

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

//        navController = Navigation.findNavController(view);

        root_view_neta = view.findViewById(R.id.root_view_neta);
        fondo_estado_texto = view.findViewById(R.id.fondo_estado_texto);
        color_fondo_estado = view.findViewById(R.id.color_fondo_estado);
        et_texto_estado = view.findViewById(R.id.et_texto_estado);
        et_texto_estado.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Roboto Medium.ttf"));

        emojiButton = view.findViewById(R.id.input_emoji_estado);
        fab_publicar_estado = view.findViewById(R.id.fab_publicar_estado);

        input_color_fondo_estado = view.findViewById(R.id.input_color_fondo_estado);
        input_color_fondo_estado_salir = view.findViewById(R.id.input_color_fondo_estado_salir);
        input_change_font = view.findViewById(R.id.input_change_font);
        degradado_fondo_estado = view.findViewById(R.id.degradado_fondo_estado);

        fondo_estado_texto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //click de nada, para no tocar atras
            }});

        input_color_fondo_estado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim= AnimationUtils.loadAnimation(context,R.anim.fade_out_fast);
                fondo_estado_texto.startAnimation(anim);
                fondo_estado_texto.setVisibility(View.GONE);
                fab_publicar_estado.hide();

                InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(et_texto_estado.getWindowToken(), 0);
            }
        });

        input_color_fondo_estado_salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim= AnimationUtils.loadAnimation(context,R.anim.fade_in_fast);
                fondo_estado_texto.setVisibility(View.VISIBLE);
                fondo_estado_texto.startAnimation(anim);
                fab_publicar_estado.show();
            }
        });

        input_change_font.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Typeface font ;
                tipoLetra++;
                if(tipoLetra>=8) tipoLetra = 0;
                switch (tipoLetra){
                    case 0: font = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto Medium.ttf");
                        break;
                    case 1: font = Typeface.createFromAsset(context.getAssets(), "fonts/Burnstown Dam.otf");
                        break;
                    case 2: font = Typeface.createFromAsset(context.getAssets(), "fonts/comicz.ttf");
                        break;
                    case 3: font = Typeface.createFromAsset(context.getAssets(), "fonts/Inkfree.ttf");
                        break;
                    case 4: font = Typeface.createFromAsset(context.getAssets(), "fonts/mw_bold.ttf");
                        break;
                    case 5: font = Typeface.createFromAsset(context.getAssets(), "fonts/Norican Regular.ttf");
                        break;
                    case 6: font = Typeface.createFromAsset(context.getAssets(), "fonts/Oswald Heavy.ttf");
                        break;
                    case 7: font = Typeface.createFromAsset(context.getAssets(), "fonts/Thunder Pants.otf");
                        break;
                    default: font = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto Medium.ttf");
                }
                et_texto_estado.setTypeface(font);
            }
        });

        seguidores = dbWorker.obtenerTodosSeguidores();
        if(seguidores.size()==0)
            Utils.ShowToastAnimated(mainActivity,"Sin seguidores no podrás publicar ningún estado",R.raw.swipe_disabled);

        et_texto_estado.setTextSize(40);
        et_texto_estado.setEmojiSizeRes(R.dimen.estado_size_40,false);
        valAnterior = 0;

        setUpEmojiPopup();
        emojiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emojiPopup.toggle();
            }
        });

        colorEstado=4;
        fondo_estado_texto.setBackgroundColor(ContextCompat.getColor(context, R.color.card5));

        degradado_fondo_estado.setLayoutManager(new GridLayoutManager(context,2));
        degradados = new ArrayList<>();
        for(int i=0; i<10; i++)
            degradados.add(i);
        adaptadorDatosFondoEstadoTexto = new AdaptadorDatosFondoEstadoTexto(degradados,this);
        degradado_fondo_estado.setAdapter(adaptadorDatosFondoEstadoTexto);

        linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        color_fondo_estado.setLayoutManager(linearLayoutManager);
        adaptadorColorTarjeta= new AdaptadorColorTarjeta(context);
        adaptadorColorTarjeta.setOnColorPickerClickListener(new AdaptadorColorTarjeta.OnColorPickerClickListener() {
            @Override
            public void onColorPickerClickListener(int colorCode, int pos) {
                colorEstado=pos;
                fondo_estado_texto.setBackgroundResource(0);
                fondo_estado_texto.setBackgroundColor(colorCode);
                Animation anim= AnimationUtils.loadAnimation(context,R.anim.fade_in_fast);
                fondo_estado_texto.setVisibility(View.VISIBLE);
                fondo_estado_texto.startAnimation(anim);
                fab_publicar_estado.show();
            }
        });
        color_fondo_estado.setAdapter(adaptadorColorTarjeta);

        et_texto_estado.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                procesoVerificarTexto(s.toString());
            }
        });

        fab_publicar_estado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int longi = seguidores.size();
                String texto = et_texto_estado.getText().toString().trim();
                if(YouChatApplication.estaAndandoChatService() && !YouChatApplication.chatService.hayConex)
                    Utils.ShowToastAnimated(mainActivity,"Compruebe su conexión",R.raw.ic_ban);
                else if(longi==0)
                    Utils.ShowToastAnimated(mainActivity,"Sin seguidores aún, no podrás publicar ningún estado",R.raw.swipe_disabled);
                else if(texto.equals(""))
                    Utils.ShowToastAnimated(mainActivity,"El estado no puede estar vacío",R.raw.error);
                else {
                    if(onNowPublicarListener!=null)
                        onNowPublicarListener.onPublicar(texto,colorEstado,tipoLetra);
                    mainActivity.atrasFragment();
                }
            }
        });
    }
    private OnNowPublicarListener onNowPublicarListener;
    public void setOnNowPublicarListener(OnNowPublicarListener onNowPublicarListener) {
        this.onNowPublicarListener = onNowPublicarListener;
    }
    public interface OnNowPublicarListener{
        void onPublicar(String texto, int colorEstado, int tipoLetra);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private synchronized void procesoVerificarTexto(String cad){
        if(cad.replace(" ","").length()>0){
            int l = cad.length();
            int rango=l/20;
            if(l>500)
                Utils.ShowToastAnimated(mainActivity,"Límite de 500 caracteres superado",R.raw.chats_infotip);
            else if(rango!=valAnterior){
                valAnterior=rango;
                if(rango==0) {
                    if (et_texto_estado.getTextSize() != 40){
                        et_texto_estado.setTextSize(40);
                        et_texto_estado.setEmojiSizeRes(R.dimen.estado_size_40,false);
                        et_texto_estado.setText(cad);
                        et_texto_estado.setSelection(l);
                    }
                }
                else if(rango==1){
                    if(et_texto_estado.getTextSize()!=38){
                        et_texto_estado.setTextSize(38);
                        et_texto_estado.setEmojiSizeRes(R.dimen.estado_size_38,false);
                        et_texto_estado.setText(cad);
                        et_texto_estado.setSelection(l);
                    }
                }
                else if(rango==2){
                    if(et_texto_estado.getTextSize()!=36){
                        et_texto_estado.setTextSize(36);
                        et_texto_estado.setEmojiSizeRes(R.dimen.estado_size_36,false);
                        et_texto_estado.setText(cad);
                        et_texto_estado.setSelection(l);
                    }
                }
                else if(rango==3){
                    if(et_texto_estado.getTextSize()!=34){
                        et_texto_estado.setTextSize(34);
                        et_texto_estado.setEmojiSizeRes(R.dimen.estado_size_34,false);
                        et_texto_estado.setText(cad);
                        et_texto_estado.setSelection(l);
                    }
                }
                else if(rango==4){
                    if(et_texto_estado.getTextSize()!=32){
                        et_texto_estado.setTextSize(32);
                        et_texto_estado.setEmojiSizeRes(R.dimen.estado_size_32,false);
                        et_texto_estado.setText(cad);
                        et_texto_estado.setSelection(l);
                    }
                }
                else if(rango==5){
                    if(et_texto_estado.getTextSize()!=30){
                        et_texto_estado.setTextSize(30);
                        et_texto_estado.setEmojiSizeRes(R.dimen.estado_size_30,false);
                        et_texto_estado.setText(cad);
                        et_texto_estado.setSelection(l);
                    }
                }
                else if(rango==6){
                    if(et_texto_estado.getTextSize()!=28){
                        et_texto_estado.setTextSize(28);
                        et_texto_estado.setEmojiSizeRes(R.dimen.estado_size_28,false);
                        et_texto_estado.setText(cad);
                        et_texto_estado.setSelection(l);
                    }
                }
                else if(rango==7){
                    if(et_texto_estado.getTextSize()!=26){
                        et_texto_estado.setTextSize(26);
                        et_texto_estado.setEmojiSizeRes(R.dimen.estado_size_26,false);
                        et_texto_estado.setText(cad);
                        et_texto_estado.setSelection(l);
                    }
                }
                else if(rango==8){
                    if(et_texto_estado.getTextSize()!=24){
                        et_texto_estado.setTextSize(24);
                        et_texto_estado.setEmojiSizeRes(R.dimen.estado_size_24,false);
                        et_texto_estado.setText(cad);
                        et_texto_estado.setSelection(l);
                    }
                }
                else if(rango==9){
                    if(et_texto_estado.getTextSize()!=22){
                        et_texto_estado.setTextSize(22);
                        et_texto_estado.setEmojiSizeRes(R.dimen.estado_size_22,false);
                        et_texto_estado.setText(cad);
                        et_texto_estado.setSelection(l);
                    }
                }
                else if(rango==10){
                    if(et_texto_estado.getTextSize()!=20){
                        et_texto_estado.setTextSize(20);
                        et_texto_estado.setEmojiSizeRes(R.dimen.estado_size_20,false);
                        et_texto_estado.setText(cad);
                        et_texto_estado.setSelection(l);
                    }
                }
                else if(rango==11){
                    if(et_texto_estado.getTextSize()!=18){
                        et_texto_estado.setTextSize(18);
                        et_texto_estado.setEmojiSizeRes(R.dimen.estado_size_18,false);
                        et_texto_estado.setText(cad);
                        et_texto_estado.setSelection(l);
                    }
                }
                else if(rango==12){
                    if(et_texto_estado.getTextSize()!=16){
                        et_texto_estado.setTextSize(16);
                        et_texto_estado.setEmojiSizeRes(R.dimen.estado_size_16,false);
                        et_texto_estado.setText(cad);
                        et_texto_estado.setSelection(l);
                    }
                }
            }
        }
        else {
            if(cad.length()>0) et_texto_estado.setText("");
            et_texto_estado.setTextSize(40);
            et_texto_estado.setEmojiSizeRes(R.dimen.estado_size_40,false);
            valAnterior=0;
        }
    }

    private synchronized void setUpEmojiPopup() {
        emojiPopup = EmojiPopup.Builder.fromRootView(root_view_neta)
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
                .build(et_texto_estado,false);
    }

    public void cambiarFondoADegradado(int intFondo) {
        fondo_estado_texto.setBackgroundColor(0);
        colorEstado=intFondo+30;
        switch (intFondo){
            case 0:
                fondo_estado_texto
                        .setBackgroundResource(R.drawable.shape_fondo_estado_texto_1);
                break;
            case 1:
                fondo_estado_texto
                        .setBackgroundResource(R.drawable.shape_fondo_estado_texto_2);
                break;
            case 2:
                fondo_estado_texto
                        .setBackgroundResource(R.drawable.shape_fondo_estado_texto_3);
                break;
            case 3:
                fondo_estado_texto
                        .setBackgroundResource(R.drawable.shape_fondo_estado_texto_4);
                break;
            case 4:
                fondo_estado_texto
                        .setBackgroundResource(R.drawable.shape_fondo_estado_texto_5);
                break;
            case 5:
                fondo_estado_texto
                        .setBackgroundResource(R.drawable.shape_fondo_estado_texto_6);
                break;
            case 6:
                fondo_estado_texto
                        .setBackgroundResource(R.drawable.shape_fondo_estado_texto_7);
                break;
            case 7:
                fondo_estado_texto
                        .setBackgroundResource(R.drawable.shape_fondo_estado_texto_8);
                break;
            case 8:
                fondo_estado_texto
                        .setBackgroundResource(R.drawable.shape_fondo_estado_texto_9);
                break;
            case 9:
                fondo_estado_texto
                        .setBackgroundResource(R.drawable.shape_fondo_estado_texto_10);
                break;
            default:
                fondo_estado_texto
                        .setBackgroundResource(R.drawable.shape_fondo_estado_texto_1);
                colorEstado=30;
        }

        Animation anim= AnimationUtils.loadAnimation(context,R.anim.fade_in_fast);
        fondo_estado_texto.setVisibility(View.VISIBLE);
        fondo_estado_texto.startAnimation(anim);
        fab_publicar_estado.show();
    }


    public void atras() {
        if(fondo_estado_texto.getVisibility()==View.GONE){
            Animation anim= AnimationUtils.loadAnimation(context,R.anim.fade_in_fast);
            fondo_estado_texto.setVisibility(View.VISIBLE);
            fondo_estado_texto.startAnimation(anim);
            fab_publicar_estado.show();
        }
        else mainActivity.atrasFragment();
    }
}
