package cu.alexgi.youchat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.slider.Slider;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import cu.alexgi.youchat.BottomSheetDialogFragment.BottomSheetDialogFragment_Ayuda_ajustes;
import cu.alexgi.youchat.BottomSheetDialogFragment.BottomSheetDialogFragment_tema_info;
import cu.alexgi.youchat.adapters.AdaptadorDatosEstilosTemas;
import cu.alexgi.youchat.items.ItemChat;
import cu.alexgi.youchat.items.ItemTemas;
import cu.alexgi.youchat.swipebackfragment.BaseSwipeBackFragment;
import cu.alexgi.youchat.views_GI.CardViewBarChatGI;
import cu.alexgi.youchat.views_GI.CardViewFechaGI;
import cu.alexgi.youchat.views_GI.SliderGI;
import cu.alexgi.youchat.views_GI.SwitchGI;
import cu.alexgi.youchat.views_GI.TextViewFontGenGI;
import cu.alexgi.youchat.views_GI.TextViewFontGenOscuroGI;

import static cu.alexgi.youchat.MainActivity.context;
import static cu.alexgi.youchat.MainActivity.dbWorker;
import static cu.alexgi.youchat.MainActivity.mainActivity;
import static cu.alexgi.youchat.YouChatApplication.principalActivity;

@SuppressLint("RestrictedApi")
public class PersonalizarChat extends BaseSwipeBackFragment {

    private boolean entroFondo;
//    private NavController navController;

    private ImageView fondo_preview;
    private AppCompatImageView corner, cornerDer;
    private TextView msg_izq_preview,msg_der_preview,text_seek, text_globo_curva, text_curva, fecha_global,text_blur_fondo;
    private CardViewBarChatGI input_chat;
    private SliderGI seekBar_globo_curva_chat, seekBar_tam_text, seekBar_curva_chat, seekBar_blur_fondo;
    private int tam_fuente, curvaChat, curvaGlobosChat;
    private RecyclerView lista_estilos_claros, lista_estilos_oscuros;
    private AdaptadorDatosEstilosTemas adaptadorDatosEstilosTemasClaros, adaptadorDatosEstilosTemasOscuros;
//    private ArrayList<ItemTema> datos_tema;
    private CardViewFechaGI fecha_card_color;
    private MaterialRadioButton selected_dos, selected_tres;
    private int selectTemas, colorMsgDer;
    private ExtendedFloatingActionButton eFab_abrirFondo;
    private SwitchGI switch_blur_fondo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return attachToSwipeBack(inflater.inflate(R.layout.activity_personalizar_chat, container, false));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        selectTemas = Color.parseColor(YouChatApplication.itemTemas.getColor_msg_izq());
        colorMsgDer = Color.parseColor(YouChatApplication.itemTemas.getColor_msg_der());

        fondo_preview = view.findViewById(R.id.fondo_preview);
//        navController = Navigation.findNavController(view);
        view.findViewById(R.id.ir_atras).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
//                navController.navigateUp();
            }
        });
        view.findViewById(R.id.root).setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_interior()));

        //difuminar fondo ini
        switch_blur_fondo = view.findViewById(R.id.switch_blur_fondo);
        seekBar_blur_fondo = view.findViewById(R.id.seekBar_blur_fondo);
        text_blur_fondo = view.findViewById(R.id.text_blur_fondo);

        text_blur_fondo.setText(String.format("%02d",YouChatApplication.nivelDifuminadoBlur));
        seekBar_blur_fondo.setValue(YouChatApplication.nivelDifuminadoBlur);
        if(YouChatApplication.hacerBlurFondo){
            switch_blur_fondo.setChecked(true);
            seekBar_blur_fondo.setEnabled(true);
        }
        else {
            switch_blur_fondo.setChecked(false);
            seekBar_blur_fondo.setEnabled(false);
        }
        switch_blur_fondo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                YouChatApplication.setHacerBlurFondo(isChecked);
                if(YouChatApplication.hacerBlurFondo){
                    switch_blur_fondo.setChecked(true);
                    seekBar_blur_fondo.setEnabled(true);
                    YouChatApplication.imageFondoBlur=null;
                    Utils.cargarFondo(context,fondo_preview);
                }
                else {
                    switch_blur_fondo.setChecked(false);
                    seekBar_blur_fondo.setEnabled(false);
                    YouChatApplication.imageFondoBlur=null;
                    Utils.cargarFondo(context,fondo_preview);
                }
            }
        });

        seekBar_blur_fondo.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {

            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                YouChatApplication.setNivelDifuminadoBlur((int)slider.getValue());
                text_blur_fondo.setText(String.format("%02d",YouChatApplication.nivelDifuminadoBlur));
                YouChatApplication.imageFondoBlur = null;
                Utils.cargarFondo(context,fondo_preview);
            }
        });
        //difuminar fondo fin

        selected_dos = view.findViewById(R.id.selected_dos);
        selected_tres = view.findViewById(R.id.selected_tres);

        if(YouChatApplication.maxLines==2) selected_dos.setChecked(true);
        else selected_tres.setChecked(true);

        view.findViewById(R.id.lineas_dos).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!selected_dos.isChecked())
                {
                    selected_dos.setChecked(true);
                    selected_tres.setChecked(false);
                    YouChatApplication.setMaxLines(2);
                }
            }
        });
        view.findViewById(R.id.lineas_tres).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!selected_tres.isChecked())
                {
                    selected_dos.setChecked(false);
                    selected_tres.setChecked(true);
                    YouChatApplication.setMaxLines(3);
                }
            }
        });

        fecha_card_color = view.findViewById(R.id.fecha_card_color);
        fecha_global = view.findViewById(R.id.fecha_global);
        fecha_global.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getFont_msg_fecha()));


        tam_fuente=YouChatApplication.tam_fuente;
        curvaChat = YouChatApplication.curvaChat;
        curvaGlobosChat = YouChatApplication.curvaGlobosChat;

        corner=view.findViewById(R.id.corner);
        cornerDer=view.findViewById(R.id.cornerDer);
        msg_izq_preview = view.findViewById(R.id.msg_izq_preview);
        msg_der_preview = view.findViewById(R.id.msg_der_preview);

        msg_der_preview.setTextSize(tam_fuente);
        msg_izq_preview.setTextSize(tam_fuente);

        msg_izq_preview.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getFont_msg_izq()));
        msg_der_preview.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getFont_msg_der()));

        eFab_abrirFondo=view.findViewById(R.id.efab_abrirFondos);
        eFab_abrirFondo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entroFondo=true;
                if(mAddFragmentListener!=null) mAddFragmentListener.onAddFragment(PersonalizarChat.this, new FondoActivity());
//                navController.navigate(R.id.fondoActivity);
            }
        });

        GradientDrawable drawableIzq = new GradientDrawable();
        drawableIzq.setColor(Color.parseColor(YouChatApplication.itemTemas.getColor_msg_izq()));
        drawableIzq.setCornerRadii(new float[]{
                0, 0, curvaGlobosChat, curvaGlobosChat, curvaGlobosChat, curvaGlobosChat, curvaGlobosChat, curvaGlobosChat
        });
        GradientDrawable drawableDer = new GradientDrawable();
        drawableDer.setColor(Color.parseColor(YouChatApplication.itemTemas.getColor_msg_der()));
        drawableDer.setCornerRadii(new float[]{
                curvaGlobosChat, curvaGlobosChat, 0, 0, curvaGlobosChat, curvaGlobosChat, curvaGlobosChat, curvaGlobosChat
        });
        msg_der_preview.setBackground(drawableDer);
        msg_izq_preview.setBackground(drawableIzq);

        //COLOR PUNTA
        ColorStateList stateList = ColorStateList.valueOf(selectTemas);
        corner.setSupportImageTintList(stateList);
        stateList = ColorStateList.valueOf(colorMsgDer);
        cornerDer.setSupportImageTintList(stateList);

        text_seek = view.findViewById(R.id.text_seek);
        text_curva = view.findViewById(R.id.text_curva);
        text_globo_curva = view.findViewById(R.id.text_globo_curva);

        seekBar_tam_text = view.findViewById(R.id.seekBar_tam_text);
        seekBar_curva_chat = view.findViewById(R.id.seekBar_curva_chat);
        seekBar_globo_curva_chat = view.findViewById(R.id.seekBar_globo_curva_chat);

        Utils.cargarFondo(context,fondo_preview);

        seekBar_tam_text.setValue(tam_fuente-12);
        text_seek.setText(""+tam_fuente);
        seekBar_tam_text.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                tam_fuente=(int)value+12;
                msg_der_preview.setTextSize(tam_fuente);
                msg_izq_preview.setTextSize(tam_fuente);
                text_seek.setText(""+tam_fuente);
                YouChatApplication.setTam_Fuente(tam_fuente);
            }
        });

        input_chat = view.findViewById(R.id.input_chat);
        seekBar_curva_chat.setValue(curvaChat);
        text_curva.setText(""+String.format("%2d", curvaChat));
        seekBar_curva_chat.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                curvaChat=(int)value;
                input_chat.setRadius(curvaChat);
                if(curvaChat>35) fecha_card_color.setRadius(35);
                else if(curvaChat<15) fecha_card_color.setRadius(0);
                else fecha_card_color.setRadius(curvaChat);
                text_curva.setText(""+String.format("%2d", curvaChat));
                YouChatApplication.setCurvaChat(curvaChat);
            }
        });

        seekBar_globo_curva_chat.setValue(curvaGlobosChat);
        text_globo_curva.setText(""+String.format("%2d", curvaGlobosChat));
        seekBar_globo_curva_chat.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                curvaGlobosChat=(int)value;

                GradientDrawable drawableIzq = new GradientDrawable();
                drawableIzq.setColor(Color.parseColor(YouChatApplication.itemTemas.getColor_msg_izq()));
                drawableIzq.setCornerRadii(new float[]{
                        0, 0, curvaGlobosChat, curvaGlobosChat, curvaGlobosChat, curvaGlobosChat, curvaGlobosChat, curvaGlobosChat
                });
                GradientDrawable drawableDer = new GradientDrawable();
                drawableDer.setColor(Color.parseColor(YouChatApplication.itemTemas.getColor_msg_der()));
                drawableDer.setCornerRadii(new float[]{
                        curvaGlobosChat, curvaGlobosChat, 0, 0, curvaGlobosChat, curvaGlobosChat, curvaGlobosChat, curvaGlobosChat
                });
                msg_der_preview.setBackground(drawableDer);
                msg_izq_preview.setBackground(drawableIzq);

                text_globo_curva.setText(""+String.format("%2d", curvaGlobosChat));
                YouChatApplication.setCurvaGlobosChat(curvaGlobosChat);
            }
        });

        lista_estilos_claros = view.findViewById(R.id.lista_estilos_claros);
        lista_estilos_oscuros = view.findViewById(R.id.lista_estilos_oscuros);
        lista_estilos_claros.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        lista_estilos_oscuros.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

//        ArrayList<ItemTemas> datos = dbWorker.obtenerTemas(YouChatApplication.temaApp==1);
//        datos.add(new ItemTemas("new"));
//        adaptadorDatosEstilosTemas = new AdaptadorDatosEstilosTemas(datos,this);
//        lista_estilos.setAdapter(adaptadorDatosEstilosTemas);

        //lista_estilos.scrollToPosition(YouChatApplication.colorApp);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        if(entroFondo){
            entroFondo=false;
            Utils.cargarFondo(context,fondo_preview);
        }
        actualizarTemas();
    }

    private void actualizarTemas() {
        ArrayList<ItemTemas> datos = dbWorker.obtenerTemas(false);
        datos.add(new ItemTemas("new"));
        adaptadorDatosEstilosTemasClaros = new AdaptadorDatosEstilosTemas(datos,this);
        lista_estilos_claros.setAdapter(adaptadorDatosEstilosTemasClaros);

        datos = dbWorker.obtenerTemas(true);
        datos.add(new ItemTemas("new"));
        adaptadorDatosEstilosTemasOscuros = new AdaptadorDatosEstilosTemas(datos,this);
        lista_estilos_oscuros.setAdapter(adaptadorDatosEstilosTemasOscuros);
    }

    public void cambiarTema(String id, String rutaImg, boolean isOscuro) {
        if(!rutaImg.isEmpty()){
            if(new File(YouChatApplication.RUTA_FONDO_YOUCHAT+rutaImg).exists()){
                YouChatApplication.imageFondoBlur = null;
                YouChatApplication.setRuta_fondo(YouChatApplication.RUTA_FONDO_YOUCHAT+rutaImg);
            }
        }

        YouChatApplication.setTemaApp(isOscuro?1:0);
        if(isOscuro) YouChatApplication.setIDTemaOscuro(id);
        else YouChatApplication.setIDTemaClaro(id);

        mainActivity.vaciarPila();



//        if(!YouChatApplication.IDTemaClaro.equals(id))
//        {
//            datos_tema.get(YouChatApplication.colorApp).setEstaSeleccionado(false);
//            datos_tema.get(idTema).setEstaSeleccionado(true);
//            adaptadorDatosEstilosTemas.notifyDataSetChanged();

//            super.onDestroy();
//            if(mAddFragmentListener!=null) mAddFragmentListener.onAddFragment(PersonalizarChat.this, new PersonalizarChat());
//            navController.navigate(R.id.personalizarChat);
//        }
    }

    @Override
    public void onDestroy() {
        if(principalActivity!=null)
            principalActivity.cargarFondo();
        super.onDestroy();
    }

    public void addTheme() {
        if(mAddFragmentListener!=null)
            mAddFragmentListener.onAddFragment(PersonalizarChat.this, AddThemeFragment.newInstance(YouChatApplication.itemTemas,false));
    }

    public void showDialog(ItemTemas item){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(1);
        View mview=getLayoutInflater().inflate(R.layout.dialog_theme_options,null);
        dialog.setContentView(mview);

        TextViewFontGenGI tv_name = mview.findViewById(R.id.tv_name);
        tv_name.setText(item.getNombre());

        TextViewFontGenOscuroGI tv_creador = mview.findViewById(R.id.tv_creador);
        tv_creador.setText("Creado por "+dbWorker.obtenerNombre(item.getCreador()));

        View tv_compartir, tv_editar, tv_eliminar, tv_info;
        tv_compartir = mview.findViewById(R.id.tv_compartir);
        tv_editar = mview.findViewById(R.id.tv_editar);
        tv_eliminar = mview.findViewById(R.id.tv_eliminar);
        tv_info = mview.findViewById(R.id.tv_info);

        if(item.getTipo()==3){
            tv_editar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if(mAddFragmentListener!=null)
                        mAddFragmentListener.onAddFragment(PersonalizarChat.this, AddThemeFragment.newInstance(item,true));
                }
            });
        }
        else{
            tv_editar.setVisibility(View.GONE);
        }

        if(item.getTipo()==2 || item.getTipo()==3){
            tv_compartir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if(mAddFragmentListener!=null){
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
                        Date date = new Date();
                        String fechaEntera = sdf.format(date);

                        String id="YCchatema"+fechaEntera;
                        String hora = Convertidor.conversionHora(fechaEntera);
                        String fecha = Convertidor.conversionFecha(fechaEntera);
                        ItemChat chatTema=new ItemChat( id,
                                22, 3, YouChatApplication.correo,
                                item.temaToMensaje(),
                                "",
                                hora, fecha, "", YouChatApplication.correo, false, fechaEntera,false,"",0,true);
                        mAddFragmentListener.onAddFragment(PersonalizarChat.this, ReenviarActivity.newInstance(chatTema,false));
                    }
                }
            });

            tv_eliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dbWorker.eliminarTema(item.getId());
                    adaptadorDatosEstilosTemasClaros.eliminarTema(item.getId());
                    adaptadorDatosEstilosTemasOscuros.eliminarTema(item.getId());
                    dialog.dismiss();
                }
            });
        }
        else{
            tv_compartir.setVisibility(View.GONE);
            tv_eliminar.setVisibility(View.GONE);
        }

        if(YouChatApplication.correo.equals("alexgi@nauta.cu")
                || YouChatApplication.correo.equals("octaviog97@nauta.cu")
                || YouChatApplication.correo.equals("niuvis2019@nauta.cu")
                || YouChatApplication.correo.equals("anthonyruiz@nauta.cu")
                || YouChatApplication.correo.equals("maryanis.noval@nauta.cu")
                || YouChatApplication.correo.equals("ordiel2005@nauta.cu")
                || YouChatApplication.correo.equals("raidelmis@nauta.cu")){

            tv_info.setVisibility(View.VISIBLE);
            tv_info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    mostrarInfo(item.getInfo(),item.temaToMensaje());
                }
            });
        }
        else tv_info.setVisibility(View.GONE);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCancelable(true);
        dialog.show();

    }

    public void mostrarInfo(String texto, String copy) {
        BottomSheetDialogFragment_tema_info bsdFragment = BottomSheetDialogFragment_tema_info
                .newInstance("Info tema", texto, copy);
        bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
    }
}
