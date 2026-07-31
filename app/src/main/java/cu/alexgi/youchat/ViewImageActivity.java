package cu.alexgi.youchat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.Slider;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.listeners.OnEmojiPopupDismissListener;
import com.vanniktech.emoji.listeners.OnEmojiPopupShownListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import cu.alexgi.youchat.adapters.AdaptadorDatosImgEditorCarrucel;
import cu.alexgi.youchat.cropper.CropActivity;
import cu.alexgi.youchat.items.ItemImg;
import cu.alexgi.youchat.photoeditorpro.EditImageActivity;
import cu.alexgi.youchat.photoutil.ImageLoader;
import cu.alexgi.youchat.swipebackfragment.BaseSwipeBackFragment;
import cu.alexgi.youchat.swipebackfragment.SwipeBackLayout;
import cu.alexgi.youchat.views_GI.SliderGI;

import static cu.alexgi.youchat.MainActivity.context;
import static cu.alexgi.youchat.MainActivity.dbWorker;
import static cu.alexgi.youchat.MainActivity.mainActivity;

public class ViewImageActivity extends BaseSwipeBackFragment {

    private static int tipo_de_procedencia;
//    private NavController navController;
    private PhotoView imageView;
    private CircleImageView mini_img_perfil;
    private static ArrayList<ItemImg> itemImgs;
    private LinearLayout contenedor_input_send;
    private FloatingActionButton fab_enviar_img;
    private View input_send, input_control_calidad, ll;
    private int posActual;
    private EmojiEditText input_text;
    private EmojiPopup emojiPopup;
    private ImageView emojiButton;
    private boolean modoEdit=false, modoCrop=false, borrar=true;
//    private ProgressDialog mProgressDialog;
    private Animation anim;
    private View ContentView, editImg_Back, item_delete, item_crop, item_edit;

    private SliderGI seekBar_calidad_img;
    private TextView text_seek_size;

    private RecyclerView lista_img_carrucel;
    private AdaptadorDatosImgEditorCarrucel adapter;

    private final Runnable OcultarBarraSistema = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            ContentView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    };

    public static ViewImageActivity newInstance(ArrayList<ItemImg> item, int tipo) {
        ViewImageActivity fragment = new ViewImageActivity();
        tipo_de_procedencia = tipo;
        itemImgs = item;
        return fragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        YouChatApplication.viewImageActivity = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return attachToSwipeBack(inflater.inflate(R.layout.activity_view_image, container, false));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ////TODO PONER
//        YouChatApplication.viewImageActivity = this;


        ContentView = view.findViewById(R.id.view);
        lista_img_carrucel = view.findViewById(R.id.lista_img_carrucel);
//        navController = Navigation.findNavController(view);

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

        imageView = view.findViewById(R.id.image_view);

        editImg_Back = view.findViewById(R.id.editImg_Back);
        item_delete = view.findViewById(R.id.item_delete);
        item_crop = view.findViewById(R.id.item_crop);
        item_edit = view.findViewById(R.id.item_edit);
        input_control_calidad = view.findViewById(R.id.input_control_calidad);
        ll = view.findViewById(R.id.ll);

        input_control_calidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ll.getVisibility()==View.VISIBLE){
                    if(itemImgs.size()>1) lista_img_carrucel.setVisibility(View.VISIBLE);
                    ll.setVisibility(View.GONE);
                }
                else{
                    lista_img_carrucel.setVisibility(View.GONE);
                    ll.setVisibility(View.VISIBLE);
                }
            }
        });

        editImg_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.onBackPressed();
//                Navigation.findNavController(v).navigateUp();
            }
        });

        seekBar_calidad_img = view.findViewById(R.id.seekBar_calidad_img);
        text_seek_size = view.findViewById(R.id.text_seek_size);

        item_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.borrarFile(new File(itemImgs.get(posActual).getRuta()));
                itemImgs.remove(posActual);
//                int posAnt = posActual;
                adapter.notifyItemRemoved(posActual);
                if(itemImgs.size()==0){
                    atras();
                    return;
                }
                else if(itemImgs.size()==1) lista_img_carrucel.setVisibility(View.GONE);
                posActual=0;
//                adapter.notifyItemChanged(posActual,7);
                Glide.with(context).load(itemImgs.get(posActual).getRuta())
                        .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(imageView);
                lista_img_carrucel.scrollToPosition(posActual);
            }
        });
        item_crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CropActivity.class);
                intent.putExtra("rutaImg", itemImgs.get(posActual).getRuta());
                startActivity(intent);
                modoCrop=true;
            }
        });
        item_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditImageActivity.class);
                intent.putExtra("rutaImg", itemImgs.get(posActual).getRuta());
                startActivity(intent);
                modoEdit=true;
            }
        });

        contenedor_input_send = view.findViewById(R.id.contenedor_input_send);

        input_send = view.findViewById(R.id.input_send);
        input_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showLoading("Enviando imágenes");
                borrar = false;
                itemImgs.get(posActual).setTexto(input_text.getText().toString());
                if(tipo_de_procedencia==1 && YouChatApplication.chatsActivity!=null)
                    YouChatApplication.chatsActivity.enviarImagenes(itemImgs);
                else if(tipo_de_procedencia==2 && YouChatApplication.viewYouPerfilActivity!=null)
                    YouChatApplication.viewYouPerfilActivity.actualizarImagenPerfil(itemImgs);
                else if(tipo_de_procedencia==3 && YouChatApplication.principalActivity!=null && YouChatApplication.principalActivity.estadosFragment!=null)
                    YouChatApplication.principalActivity.estadosFragment.publicarEstado(itemImgs);
                else if(tipo_de_procedencia==4 && YouChatApplication.chatsActivityCorreo!=null)
                    YouChatApplication.chatsActivityCorreo.enviarImagenes(itemImgs);

                mainActivity.onBackPressed();
//                Esperar(1000);
            }
        });

        fab_enviar_img = view.findViewById(R.id.fab_enviar_img);
        fab_enviar_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showLoading("Enviando imagen");
                borrar = false;
                itemImgs.get(posActual).setTexto(input_text.getText().toString());
                if(tipo_de_procedencia==1 && YouChatApplication.chatsActivity!=null)
                    YouChatApplication.chatsActivity.enviarImagenes(itemImgs);
                else if(tipo_de_procedencia==2 && YouChatApplication.viewYouPerfilActivity!=null)
                    YouChatApplication.viewYouPerfilActivity.actualizarImagenPerfil(itemImgs);
                else if(tipo_de_procedencia==3 && YouChatApplication.principalActivity!=null && YouChatApplication.principalActivity.estadosFragment!=null)
                    YouChatApplication.principalActivity.estadosFragment.publicarEstado(itemImgs);

                mainActivity.onBackPressed();
//                Esperar(1000);
            }
        });

        input_text = view.findViewById(R.id.input_text);
        mini_img_perfil = view.findViewById(R.id.mini_img_perfil);

//        Bundle mibundle=getArguments();
        if(itemImgs!=null && itemImgs.size()>0){
            /**
             * tipos de procedencias
             * 1 chatActivity
             * 2 ViewYourPerfilActivity
             * 3 principalActivity
             */
            if(tipo_de_procedencia==0) getActivity().onBackPressed();
            else {
                if(tipo_de_procedencia==1 && YouChatApplication.chatsActivity!=null){//chat
                    String ruta = dbWorker.obtenerRutaImg(YouChatApplication.chatsActivity.getCorreo());
                    Glide.with(context).load(ruta).error(R.drawable.profile_white).into(mini_img_perfil);
                }
                else if(tipo_de_procedencia==2){//perfil
                    Glide.with(context).load(YouChatApplication.ruta_img_perfil).error(R.drawable.profile_white).into(mini_img_perfil);
                    contenedor_input_send.setVisibility(View.GONE);
                    fab_enviar_img.setVisibility(View.VISIBLE);
                }
                else if(tipo_de_procedencia==3){//estado
                    Glide.with(context).load(YouChatApplication.ruta_img_perfil).error(R.drawable.profile_white).into(mini_img_perfil);
                }
                else if(tipo_de_procedencia==4 && YouChatApplication.chatsActivityCorreo!=null){//chat
                    String ruta = dbWorker.obtenerRutaImg(YouChatApplication.chatsActivityCorreo.getCorreo());
                    Glide.with(context).load(ruta).error(R.drawable.profile_white).into(mini_img_perfil);
                }

                posActual=0;
                if(itemImgs.size()==0) atras();
                else if(itemImgs.size()==1) lista_img_carrucel.setVisibility(View.GONE);

                lista_img_carrucel.setLayoutManager(new LinearLayoutManager(context,RecyclerView.HORIZONTAL,false));
                adapter = new AdaptadorDatosImgEditorCarrucel(context,itemImgs,this);
                lista_img_carrucel.setAdapter(adapter);

                Uri uri = Uri.fromFile(new File(itemImgs.get(posActual).getRuta()));
                Glide.with(context).load(uri)
                        .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(imageView);
                actualizarSliderCalidad();
            }
        }
        else getActivity().onBackPressed();
            //Navigation.findNavController(view).navigateUp();

        emojiButton=view.findViewById(R.id.input_emoji_IE);
        setUpEmojiPopup();
        emojiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emojiPopup.toggle();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if(modoEdit || modoCrop){
            modoEdit=modoCrop=false;
//            Uri uri = Uri.fromFile(new File(itemImgs.get(posActual).getRuta()));
//            imageView.setImageURI(null);
//            imageView.setImageURI(uri);

            File origen = new File(itemImgs.get(posActual).getRuta());
            File destino = new File(itemImgs.get(posActual).getRuta()+"o");
            try {
                Utils.copyFile(origen,destino);
                itemImgs.get(posActual).setRutaOriginal(itemImgs.get(posActual).getRuta()+"o");
            } catch (IOException e) {
                e.printStackTrace();
                itemImgs.get(posActual).setRutaOriginal(itemImgs.get(posActual).getRuta());
            }
            itemImgs.get(posActual).setCalidad(100);

            Glide.with(context).load(itemImgs.get(posActual).getRuta())
                    .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(imageView);
//            imgSends[posActual].setImageURI(null);
//            imgSends[posActual].setImageURI(uri);
            adapter.notifyItemChanged(posActual,7);
            actualizarSliderCalidad();
//            Glide.with(context).load(uri)
//                    .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(imgSends[posActual]);
        }
    }

    public void hideFragment(){
        if(itemImgs.size()>0 && borrar){
            for (int i=0; i<itemImgs.size(); i++){
                Utils.borrarFile(new File(itemImgs.get(i).getRuta()));
            }
        }
    }

    public void atras(){
//        showLoading("Cancelando edición...");
        if(itemImgs.size()>0 && borrar) {
            for (int i=0; i<itemImgs.size(); i++){
                Utils.borrarFile(new File(itemImgs.get(i).getRuta()));
            }
        }

//        hideLoading();

        mainActivity.atrasFragment();
    }

    private void setUpEmojiPopup() {
        emojiPopup = EmojiPopup.Builder.fromRootView(ContentView)
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

    private void actualizarSliderCalidad(){
        seekBar_calidad_img.setValue(itemImgs.get(posActual).getCalidad());
        text_seek_size.setText(Utils.convertirBytes(new File(itemImgs.get(posActual).getRuta()).length()));

        if(itemImgs.get(posActual).getRuta().equals(itemImgs.get(posActual).getRutaOriginal())){
            File origen = new File(itemImgs.get(posActual).getRuta());
            File destino = new File(itemImgs.get(posActual).getRuta()+"o");
            try {
                Utils.copyFile(origen,destino);
                itemImgs.get(posActual).setRutaOriginal(itemImgs.get(posActual).getRuta()+"o");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        seekBar_calidad_img.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {}
            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                int progress=(int)seekBar_calidad_img.getValue();
                try {
                    ImageLoader.init().comprimirImagen(itemImgs.get(posActual).getRutaOriginal(),
                            itemImgs.get(posActual).getRuta(),progress);
                    Glide.with(context).load(itemImgs.get(posActual).getRuta())
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true).into(imageView);
                    itemImgs.get(posActual).setCalidad(progress);
                    text_seek_size.setText(Utils.convertirBytes(
                            new File(itemImgs.get(posActual).getRuta()).length()));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Utils.ShowToastAnimated(mainActivity,"Error al transformar",R.raw.error);
                }
            }
        });
    }

    public int getPosActual() {
        return posActual;
    }

    public void elegirNuevaImagen(int pos) {
        if(pos==-1) pos=0;
        if(pos!=posActual){
            if(posActual<pos) anim=AnimationUtils.loadAnimation(context,R.anim.left_in_fast);
            else anim=AnimationUtils.loadAnimation(context,R.anim.right_in_fast);
            itemImgs.get(posActual).setTexto(input_text.getText().toString());
//            int posAnt = posActual;
            posActual = pos;
//            adapter.notifyItemChanged(posAnt,7);
//            adapter.notifyItemChanged(posActual, 7);
            imageView.startAnimation(anim);
            actualizarSliderCalidad();
            Glide.with(context).load(itemImgs.get(posActual).getRuta())
                    .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(imageView);
            input_text.setText(itemImgs.get(posActual).getTexto());
            input_text.setSelection(input_text.length());
        }
    }

   /* protected void showLoading(@NonNull String message) {
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage(message);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    protected void hideLoading() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }*/
}
