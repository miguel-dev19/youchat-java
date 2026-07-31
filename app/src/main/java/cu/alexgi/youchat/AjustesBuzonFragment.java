package cu.alexgi.youchat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.Slider;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.concurrent.TimeUnit;

import cu.alexgi.youchat.BottomSheetDialogFragment.BottomSheetDialogFragment_Atajos;
import cu.alexgi.youchat.BottomSheetDialogFragment.BottomSheetDialogFragment_Ayuda_ajustes;
import cu.alexgi.youchat.items.ItemAtajo;
import cu.alexgi.youchat.swipebackfragment.BaseSwipeBackFragment;
import cu.alexgi.youchat.views_GI.ExtendedFABGI;

import static cu.alexgi.youchat.MainActivity.context;
import static cu.alexgi.youchat.MainActivity.dbWorker;
import static cu.alexgi.youchat.MainActivity.mainActivity;

@SuppressLint("RestrictedApi")
public class AjustesBuzonFragment extends BaseSwipeBackFragment {

    private boolean modoPreguntar;
    private Activity activity;
    private MaterialCardView efab_reintentar_be;
    private View efab_guardar_atajo, option_atajos, option_add_pie_firma, option_conv_en_grupos, option_sync_mensajes,
            option_mensaje_tam_limite,
            option_slider_mensaje_tam_limite, option_bloqueados, ir_atras;
    private View option_rec_correos_DeltaLab, option_rec_correos_Dimelo, option_rec_correos_LetterSocial;

    private Slider seekBar_slider_mensaje_tam_limite;
    private TextView tv_mensaje_tam_limite;
    private SwitchMaterial switch_add_pie_firma, switch_conv_en_grupos, switch_mensaje_tam_limite, switch_rec_correos_DeltaLab,
            switch_rec_correos_Dimelo, switch_rec_correos_LetterSocial;
    private AppCompatImageView icon_preguntar;
    private ColorStateList stateListAccent, stateListNone;
    private RadioGroup radioGroup_vaciado_bandeja;
    private EditText et_pie_firma, et_atajo_com, et_atajo_des;
    private ExtendedFABGI efab_guardar_pie;
    private LinearProgressIndicator progres_bar;
    private TextView tv_bandeja_ent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return attachToSwipeBack(inflater.inflate(R.layout.fragment_ajustes_buzon, container, false));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity=getActivity();
        init(view);
    }

    private void init(View view) {
        stateListAccent = ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getColor_accento()));
        stateListNone = ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getFont_barra()));

        modoPreguntar = false;

        view.findViewById(R.id.root).setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_interior()));

        tv_bandeja_ent = view.findViewById(R.id.tv_bandeja_ent);
        efab_reintentar_be = view.findViewById(R.id.efab_reintentar_be);
        progres_bar = view.findViewById(R.id.progres_bar);
        tv_bandeja_ent.setText(YouChatApplication.descripEscanerBandeja);
        progres_bar.setProgress(YouChatApplication.progressEscanerBandeja);
        ((TextView)view.findViewById(R.id.tv_escanear)).setTextColor(Color.parseColor(YouChatApplication.itemTemas.getColor_btn()));
        efab_reintentar_be.setStrokeColor(Color.parseColor(YouChatApplication.itemTemas.getColor_btn()));
        efab_reintentar_be.setCardBackgroundColor(Color.TRANSPARENT);
        efab_reintentar_be.setRadius(YouChatApplication.curvaChat<=32?YouChatApplication.curvaChat:32);
        efab_reintentar_be.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modoPreguntar) {
                    modoPreguntar = false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Escanear bandeja", getResources().getString(R.string.explicarEscanearBandeja));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                } else {
                    if(YouChatApplication.estaAndandoChatService()
                            && YouChatApplication.chatService.hayConex){
                        YouChatApplication.chatService.escanearBandejaEntrada(tv_bandeja_ent,progres_bar);
                    } else Utils.mostrarToastDeConexion(mainActivity);
                }
            }
        });
        switch_add_pie_firma = view.findViewById(R.id.switch_add_pie_firma);
        switch_conv_en_grupos = view.findViewById(R.id.switch_conv_en_grupos);
        option_add_pie_firma = view.findViewById(R.id.option_add_pie_firma);
        option_conv_en_grupos = view.findViewById(R.id.option_conv_en_grupos);
        option_sync_mensajes = view.findViewById(R.id.option_sync_mensajes);
        option_mensaje_tam_limite = view.findViewById(R.id.option_mensaje_tam_limite);
        option_slider_mensaje_tam_limite = view.findViewById(R.id.option_slider_mensaje_tam_limite);
        seekBar_slider_mensaje_tam_limite = view.findViewById(R.id.seekBar_slider_mensaje_tam_limite);
        tv_mensaje_tam_limite = view.findViewById(R.id.tv_mensaje_tam_limite);
        switch_mensaje_tam_limite = view.findViewById(R.id.switch_mensaje_tam_limite);
        ir_atras = view.findViewById(R.id.ir_atras);
        option_rec_correos_DeltaLab = view.findViewById(R.id.option_rec_correos_DeltaLab);
        switch_rec_correos_DeltaLab = view.findViewById(R.id.switch_rec_correos_DeltaLab);
        option_rec_correos_Dimelo = view.findViewById(R.id.option_rec_correos_Dimelo);
        switch_rec_correos_Dimelo = view.findViewById(R.id.switch_rec_correos_Dimelo);
        option_rec_correos_LetterSocial = view.findViewById(R.id.option_rec_correos_LetterSocial);
        switch_rec_correos_LetterSocial = view.findViewById(R.id.switch_rec_correos_LetterSocial);
        radioGroup_vaciado_bandeja = view.findViewById(R.id.radioGroup_vaciado_bandeja);
        option_bloqueados = view.findViewById(R.id.option_bloqueados);
        option_atajos = view.findViewById(R.id.option_atajos);
        efab_guardar_atajo = view.findViewById(R.id.efab_guardar_atajo);
        et_pie_firma = view.findViewById(R.id.et_pie_firma);
        et_atajo_com = view.findViewById(R.id.et_atajo_com);
        et_atajo_des = view.findViewById(R.id.et_atajo_des);
        efab_guardar_pie = view.findViewById(R.id.efab_guardar_pie);

        efab_guardar_pie.setVisibility(View.GONE);
        et_pie_firma.setText(YouChatApplication.pieDeFirma);
        et_pie_firma.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                YouChatApplication.setPieDeFirma(et_pie_firma.getText().toString().trim());
            }
        });
        /*efab_guardar_pie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modoPreguntar) {
                    modoPreguntar = false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Pie de firma", getResources().getString(R.string.explicarPieDeFirma));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                } else {
                    String cad = et_pie_firma.getText().toString().trim();
                    YouChatApplication.setPieDeFirma(cad);
                }
            }
        });*/

        switch (YouChatApplication.opcionVaciadoAutomaticoMensajes){
            case 1:
                radioGroup_vaciado_bandeja.check(R.id.radio1);
                break;
            case 2:
                radioGroup_vaciado_bandeja.check(R.id.radio2);
                break;
            case 3:
                radioGroup_vaciado_bandeja.check(R.id.radio3);
                break;
            case 4:
                radioGroup_vaciado_bandeja.check(R.id.radio4);
                break;
            default:
                YouChatApplication.setOpcionVaciadoAutomaticoMensajes(1);
                radioGroup_vaciado_bandeja.check(R.id.radio1);
        }

        radioGroup_vaciado_bandeja.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radio1:
                        if(YouChatApplication.opcionVaciadoAutomaticoMensajes!=1){
                            YouChatApplication.setOpcionVaciadoAutomaticoMensajes(1);
                            WorkManager.getInstance(context).cancelAllWorkByTag("WORKER-VACIAR-BANDEJA");
                        }
                        break;
                    case R.id.radio2:
                        if(YouChatApplication.opcionVaciadoAutomaticoMensajes!=2){
                            YouChatApplication.setOpcionVaciadoAutomaticoMensajes(2);
                            WorkManager.getInstance(context).cancelAllWorkByTag("WORKER-VACIAR-BANDEJA");
                            PeriodicWorkRequest periodicWorkRequest =
                                    new PeriodicWorkRequest.Builder(WorkerServiceVaciarBandeja.class,
                                            1, TimeUnit.DAYS)
                                    .addTag("WORKER-VACIAR-BANDEJA")
                                    .build();
                            WorkManager.getInstance(context).enqueue(periodicWorkRequest);
                        }
                        break;
                    case R.id.radio3:
                        if(YouChatApplication.opcionVaciadoAutomaticoMensajes!=3){
                            YouChatApplication.setOpcionVaciadoAutomaticoMensajes(3);
                            WorkManager.getInstance(context).cancelAllWorkByTag("WORKER-VACIAR-BANDEJA");
                            PeriodicWorkRequest periodicWorkRequest =
                                    new PeriodicWorkRequest.Builder(WorkerServiceVaciarBandeja.class,
                                            3, TimeUnit.DAYS)
                                            .addTag("WORKER-VACIAR-BANDEJA")
                                            .build();
                            WorkManager.getInstance(context).enqueue(periodicWorkRequest);
                        }
                        break;
                    case R.id.radio4:
                        if(YouChatApplication.opcionVaciadoAutomaticoMensajes!=4){
                            YouChatApplication.setOpcionVaciadoAutomaticoMensajes(4);
                            WorkManager.getInstance(context).cancelAllWorkByTag("WORKER-VACIAR-BANDEJA");
                            PeriodicWorkRequest periodicWorkRequest =
                                    new PeriodicWorkRequest.Builder(WorkerServiceVaciarBandeja.class,
                                            7, TimeUnit.DAYS)
                                            .addTag("WORKER-VACIAR-BANDEJA")
                                            .build();
                            WorkManager.getInstance(context).enqueue(periodicWorkRequest);
                        }
                        break;
                }
            }
        });

        option_mensaje_tam_limite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modoPreguntar) {
                    modoPreguntar = false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Descargar adjuntos automáticamente", getResources().getString(R.string.explicarDescargarAdjuntosAutomatico));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                } else {
                    YouChatApplication.setDescargaAutMensajesCorreo(!YouChatApplication.descargaAutMensajesCorreo);
                    if (YouChatApplication.descargaAutMensajesCorreo) {
                        switch_mensaje_tam_limite.setChecked(true);
                        option_slider_mensaje_tam_limite.setVisibility(View.VISIBLE);
                    } else {
                        switch_mensaje_tam_limite.setChecked(false);
                        option_slider_mensaje_tam_limite.setVisibility(View.GONE);
                    }
                }
            }
        });
        option_rec_correos_DeltaLab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modoPreguntar) {
                    modoPreguntar = false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Recibir correos de DeltaLab", getResources().getString(R.string.explicarRecibirCorreoDeltaLab));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                } else {
                    YouChatApplication.setDescargarMsjDeltaLab(!YouChatApplication.descargarMsjDeltaLab);
                    switch_rec_correos_DeltaLab.setChecked(YouChatApplication.descargarMsjDeltaLab);
                }
            }
        });
        option_rec_correos_Dimelo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modoPreguntar) {
                    modoPreguntar = false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Recibir correos Dímelo", getResources().getString(R.string.explicarRecibirCorreoDimelo));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                } else {
                    YouChatApplication.setDescargarMsjDimelo(!YouChatApplication.descargarMsjDimelo);
                    switch_rec_correos_Dimelo.setChecked(YouChatApplication.descargarMsjDimelo);
                }
            }
        });
        option_rec_correos_LetterSocial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modoPreguntar) {
                    modoPreguntar = false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Recibir correos LetterSocial", getResources().getString(R.string.explicarRecibirCorreoLetterSocial));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                } else {
                    YouChatApplication.setDescargarMsjLetterSocial(!YouChatApplication.descargarMsjLetterSocial);
                    switch_rec_correos_LetterSocial.setChecked(YouChatApplication.descargarMsjLetterSocial);
                }
            }
        });
        option_conv_en_grupos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modoPreguntar) {
                    modoPreguntar = false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Habilitar grupos de correo", getResources().getString(R.string.explicarConvertirGruposMuchosDest));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                } else {
                    YouChatApplication.setConvertirCorreosMDenGrupos(!YouChatApplication.convertirCorreosMDenGrupos);
                    switch_conv_en_grupos.setChecked(YouChatApplication.convertirCorreosMDenGrupos);
                }
            }
        });
        option_add_pie_firma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modoPreguntar) {
                    modoPreguntar = false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Añadir pie de firma a correos del chat", getResources().getString(R.string.explicarAddPieFirmaChat));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                } else {
                    YouChatApplication.setAddPieFirmaAChat(!YouChatApplication.addPieFirmaAChat);
                    switch_add_pie_firma.setChecked(YouChatApplication.addPieFirmaAChat);
                }
            }
        });

        ir_atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modoPreguntar) {
                    modoPreguntar = false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Botón de ir atrás", "Con este botón se regresa a la pantalla de conversaciones");
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                } else mainActivity.atrasFragment();
                //Navigation.findNavController(v).navigateUp();
            }
        });
        option_sync_mensajes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modoPreguntar) {
                    modoPreguntar = false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Sincronizar mensajes", getResources().getString(R.string.explicarSincronizarMensajes));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                } else {
                    if(YouChatApplication.activarBuzon){
                        if(YouChatApplication.estaAndandoChatService()){
                            if(YouChatApplication.chatService.hayConex){
                                YouChatApplication.chatService.sincronizarTodosMensajes();
                                Utils.ShowToasty(mainActivity,"Sincronizando, por favor espere", R.drawable.refresh_icon);
                                if(YouChatApplication.bandejaFragment!=null)
                                    mainActivity.atrasFragment();
                            } else Utils.mostrarToastDeConexion(mainActivity);
                        }
                    }
                    else Utils.ShowToastAnimated(mainActivity, "Debe de activar el buzón", R.raw.ic_ban);
                }
            }
        });
        option_bloqueados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modoPreguntar) {
                    modoPreguntar = false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Bloqueados", getResources().getString(R.string.explicarBloqueados));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                } else {
                    if (mAddFragmentListener != null)
                        mAddFragmentListener.onAddFragment(AjustesBuzonFragment.this, new BloqueadosActivity());
                }
            }
        });
        option_atajos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modoPreguntar) {
                    modoPreguntar = false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Ver atajos de escritura", getResources().getString(R.string.explicarVerAtajos));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                } else {
                    BottomSheetDialogFragment_Atajos aaa = BottomSheetDialogFragment_Atajos.newInstance(true,false);
                    aaa.show(getParentFragmentManager(),"BottomSheetDialogFragment_Atajos");
                }
            }
        });
        efab_guardar_atajo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modoPreguntar) {
                    modoPreguntar = false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Atajos", getResources().getString(R.string.explicarAtajos));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                } else {
                    String comando = et_atajo_com.getText().toString();
                    String descrip = et_atajo_des.getText().toString();
                    if(!comando.trim().isEmpty()){
                        dbWorker.insertarNuevoAtajo(new ItemAtajo(comando,descrip));
                        et_atajo_com.setText("");
                        et_atajo_des.setText("");
                        Utils.ShowToastAnimated(mainActivity,"Atajo guardado con éxito",R.raw.contact_check);
                    }
                    else Utils.ShowToastAnimated(mainActivity,"El atajo no puede estar vacío",R.raw.error);
                }
            }
        });

        tv_mensaje_tam_limite.setText(Utils.convertirBytes(YouChatApplication.tam_max_descarga_correo*1024));
        seekBar_slider_mensaje_tam_limite.setValue(YouChatApplication.tam_max_descarga_correo);

        seekBar_slider_mensaje_tam_limite.setLabelFormatter(new LabelFormatter() {
            @NonNull
            @Override
            public String getFormattedValue(float value) {
                int x = (int) value;
                return Utils.convertirBytes(x*1024);
            }
        });
        seekBar_slider_mensaje_tam_limite.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {
            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                int x = (int) slider.getValue();
                YouChatApplication.setTam_max_descarga_correo(x);
                tv_mensaje_tam_limite.setText(Utils.convertirBytes(x*1024));
            }
        });

        icon_preguntar = view.findViewById(R.id.icon_pregunta);
        icon_preguntar.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                modoPreguntar = !modoPreguntar;
                if (modoPreguntar) {

                    icon_preguntar.setSupportImageTintList(stateListAccent);
                    Utils.ShowToastAnimated(activity, "Elija una opción para obtener su explicación", R.raw.chats_infotip);
                } else icon_preguntar.setSupportImageTintList(stateListNone);
                v.setEnabled(true);
            }
        });

        if (YouChatApplication.descargaAutMensajesCorreo) {
            switch_mensaje_tam_limite.setChecked(true);
            option_slider_mensaje_tam_limite.setVisibility(View.VISIBLE);
        } else {
            switch_mensaje_tam_limite.setChecked(false);
            option_slider_mensaje_tam_limite.setVisibility(View.GONE);
        }

        switch_add_pie_firma.setChecked(YouChatApplication.addPieFirmaAChat);
        switch_conv_en_grupos.setChecked(YouChatApplication.convertirCorreosMDenGrupos);
        switch_rec_correos_DeltaLab.setChecked(YouChatApplication.descargarMsjDeltaLab);
        switch_rec_correos_Dimelo.setChecked(YouChatApplication.descargarMsjDimelo);
        switch_rec_correos_LetterSocial.setChecked(YouChatApplication.descargarMsjLetterSocial);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
