package cu.alexgi.youchat;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.vanniktech.emoji.EmojiEditText;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cu.alexgi.youchat.BottomSheetDialogFragment.BottomSheetDialogFragment_elegir_fondo;
import cu.alexgi.youchat.colorpicker.ColorPickerDialog;
import cu.alexgi.youchat.colorpicker.ColorPickerDialogListener;
import cu.alexgi.youchat.colorpicker.ColorShape;
import cu.alexgi.youchat.items.ItemTemas;
import cu.alexgi.youchat.swipebackfragment.BaseSwipeBackFragment;
import cu.alexgi.youchat.views_GI.ExtendedFABGI;
import cu.alexgi.youchat.views_GI.RadioButtonGI;
import cu.alexgi.youchat.views_GI.TextViewBarGI;

import static cu.alexgi.youchat.MainActivity.context;
import static cu.alexgi.youchat.MainActivity.dbWorker;
import static cu.alexgi.youchat.MainActivity.mainActivity;

public class AddThemeFragment extends BaseSwipeBackFragment{

//    private NavController navController;

    private int[] COLORSe = {
            0xFF200E32, // negro
            0xFF6B6B6B, // gris oscuro
            0xFF9E9E9E, // GREY 500
            0xFFE6E6E6, // msg der
            0xFFFFFFFF, // BLANCO
    };
    private int[] COLORS_ACCENT={
            0xFFB71C1C,
            0xFFD81B60,
            0xFF039BE5,
            0xFF43A047,
            0xFFFB8C00,
            0xFF886558,
            0xFF9c27b0,
            0xFF673ab7,
            0xFF00bcd4,
            0xFF009688,
            0xFF4caf50,
            0xFF8bc34a,
            0xFFffeb3b,
            0xFF607d8b
    };

    private boolean cambios;
    private String[] colores;

    private ImageView imgView_add_theme;
    private ExtendedFABGI efab_elegirFondo;
    private AddThemeFragment addThemeFragment;
    private String rutaFondo;
    boolean esOscuro;

    //COLOR
    private CircleImageView theme_color_accento, theme_color_barra, theme_color_barra_estado, theme_color_btn, theme_color_fondo, theme_color_texto, theme_color_interior, theme_color_msg_izq, theme_color_msg_der, theme_color_msg_fecha, theme_color_ico_gen, theme_color_dialogo, theme_color_toast, theme_color_burbuja, theme_color_barchat;

    //FONT
    private CircleImageView theme_color_sel_msj,theme_font_barra, theme_font_msg_izq, theme_font_msg_der, theme_font_msg_fecha, theme_font_ico, theme_font_burbuja, theme_font_barchat;

    private static ItemTemas itemTemas;
    private static boolean esParaEditar;

    public AddThemeFragment() { }

    public static AddThemeFragment newInstance(ItemTemas item, boolean esEditar) {
        AddThemeFragment fragment = new AddThemeFragment();
        itemTemas = item;
        esParaEditar = esEditar;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        YouChatApplication.addThemeFragment=this;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        YouChatApplication.addThemeFragment=null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return attachToSwipeBack(inflater.inflate(R.layout.fragment_add_theme, container, false));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        navController = Navigation.findNavController(view);
        view.findViewById(R.id.root).setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_fondo()));

        rutaFondo = "";
        if(esParaEditar){
            ((TextViewBarGI) view.findViewById(R.id.titulo)).setText("Editar tema");
            if(!itemTemas.getRutaImg().isEmpty())
                rutaFondo = YouChatApplication.RUTA_FONDO_YOUCHAT+itemTemas.getRutaImg();
        }
        view.findViewById(R.id.atras).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                YouChatApplication.setTheme(colores);
                atras();

//                navController.navigateUp();
            }
        });
        view.findViewById(R.id.fab_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(esParaEditar){
                    dbWorker.modificarTema(new ItemTemas(itemTemas.getId(), itemTemas.getNombre(), 3, itemTemas.isOscuro(),
                            rutaFondo.isEmpty()?"":new File(rutaFondo).getName(),itemTemas.getCreador(),
                            colores[0], colores[1], colores[2], colores[3], colores[4], colores[5], colores[6], colores[7]
                            , colores[8], colores[9], colores[10], colores[11], colores[12], colores[13], colores[14],
                            colores[15], colores[16], colores[17], colores[18], colores[19], colores[20], colores[21], colores[22]));

                    mainActivity.atrasFragment();
                    Utils.ShowToastAnimated(mainActivity, "Tema modificado con éxito", R.raw.contact_check);
                }
                else showDialogSave("", YouChatApplication.temaApp==1);
            }
        });

        addThemeFragment = this;
        esOscuro = false;
        imgView_add_theme = view.findViewById(R.id.imgView_add_theme);
        efab_elegirFondo = view.findViewById(R.id.efab_elegirFondo);
        if(!rutaFondo.isEmpty()){
            Glide.with(context)
                    .load(rutaFondo)
                    .error(R.drawable.image_placeholder)
                    .into(imgView_add_theme);
        }
        efab_elegirFondo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialogFragment_elegir_fondo bottomSheetDialog =
                        BottomSheetDialogFragment_elegir_fondo.newInstance(addThemeFragment,rutaFondo);
                bottomSheetDialog.show(getParentFragmentManager(),"BottomSheetElegirFondo");
            }
        });

        theme_color_barra = view.findViewById(R.id.theme_color_barra);
        theme_color_barra_estado = view.findViewById(R.id.theme_color_barra_estado);
        theme_color_btn = view.findViewById(R.id.theme_color_btn);
        theme_color_fondo = view.findViewById(R.id.theme_color_fondo);
        theme_color_texto = view.findViewById(R.id.theme_color_texto);
        theme_color_interior = view.findViewById(R.id.theme_color_interior);
        theme_color_msg_izq = view.findViewById(R.id.theme_color_msg_izq);
        theme_color_msg_der = view.findViewById(R.id.theme_color_msg_der);
        theme_color_msg_fecha = view.findViewById(R.id.theme_color_msg_fecha);
        theme_color_ico_gen = view.findViewById(R.id.theme_color_ico_gen);
        theme_color_accento = view.findViewById(R.id.theme_color_accento);


        theme_font_barra = view.findViewById(R.id.theme_font_barra);
        theme_font_msg_izq = view.findViewById(R.id.theme_font_msg_izq);
        theme_font_msg_der = view.findViewById(R.id.theme_font_msg_der);
        theme_font_msg_fecha = view.findViewById(R.id.theme_font_msg_fecha);
        theme_font_ico = view.findViewById(R.id.theme_font_ico);

        theme_color_dialogo = view.findViewById(R.id.theme_color_dialogo);
        theme_color_toast = view.findViewById(R.id.theme_color_toast);
        theme_color_burbuja = view.findViewById(R.id.theme_color_burbuja);
        theme_font_burbuja = view.findViewById(R.id.theme_font_burbuja);

        theme_color_barchat = view.findViewById(R.id.theme_color_barchat);
        theme_font_barchat = view.findViewById(R.id.theme_font_barchat);
        theme_color_sel_msj = view.findViewById(R.id.theme_color_sel_msj);

        if(itemTemas==null) itemTemas=YouChatApplication.itemTemas;

        colores = new String[25];
        colores[0] = itemTemas.getColor_barra();
        colores[1] = itemTemas.getColor_btn();
        colores[2] = itemTemas.getColor_fondo();
        colores[3] = itemTemas.getColor_texto();
        colores[4] = itemTemas.getColor_interior();
        colores[5] = itemTemas.getColor_msg_izq();
        colores[6] = itemTemas.getColor_msg_der();
        colores[7] = itemTemas.getColor_msg_fecha();
        colores[8] = itemTemas.getColor_accento();
        colores[9] = itemTemas.getColor_ico_gen();
        colores[10] = itemTemas.getFont_barra();
        colores[11] = itemTemas.getFont_msg_izq();
        colores[12] = itemTemas.getFont_msg_der();
        colores[13] = itemTemas.getFont_msg_fecha();
        colores[14] = itemTemas.getFont_texto_resaltado();

        colores[15] = itemTemas.getColor_dialogo();
        colores[16] = itemTemas.getColor_toast();
        colores[17] = itemTemas.getColor_burbuja();
        colores[18] = itemTemas.getFont_burbuja();
        colores[19] = itemTemas.getColor_barchat();
        colores[20] = itemTemas.getFont_barchat();
        colores[21] = itemTemas.getSel_msj();
        colores[22] = itemTemas.getStatus_bar();


        theme_color_barra.setCircleBackgroundColor(Color.parseColor(colores[0]));
        theme_color_btn.setCircleBackgroundColor(Color.parseColor(colores[1]));
        theme_color_fondo.setCircleBackgroundColor(Color.parseColor(colores[2]));
        theme_color_texto.setCircleBackgroundColor(Color.parseColor(colores[3]));
        theme_color_interior.setCircleBackgroundColor(Color.parseColor(colores[4]));
        theme_color_msg_izq.setCircleBackgroundColor(Color.parseColor(colores[5]));
        theme_color_msg_der.setCircleBackgroundColor(Color.parseColor(colores[6]));
        theme_color_msg_fecha.setCircleBackgroundColor(Color.parseColor(colores[7]));
        theme_color_accento.setCircleBackgroundColor(Color.parseColor(colores[8]));

        theme_color_ico_gen.setCircleBackgroundColor(Color.parseColor(colores[9]));

        theme_font_barra.setCircleBackgroundColor(Color.parseColor(colores[10]));
        theme_font_msg_izq.setCircleBackgroundColor(Color.parseColor(colores[11]));
        theme_font_msg_der.setCircleBackgroundColor(Color.parseColor(colores[12]));
        theme_font_msg_fecha.setCircleBackgroundColor(Color.parseColor(colores[13]));
        theme_font_ico.setCircleBackgroundColor(Color.parseColor(colores[14]));

        theme_color_dialogo.setCircleBackgroundColor(Color.parseColor(colores[15]));
        theme_color_toast.setCircleBackgroundColor(Color.parseColor(colores[16]));
        theme_color_burbuja.setCircleBackgroundColor(Color.parseColor(colores[17]));
        theme_font_burbuja.setCircleBackgroundColor(Color.parseColor(colores[18]));
        theme_color_barchat.setCircleBackgroundColor(Color.parseColor(colores[19]));
        theme_font_barchat.setCircleBackgroundColor(Color.parseColor(colores[20]));
        theme_color_sel_msj.setCircleBackgroundColor(Color.parseColor(colores[21]));

        theme_color_barra_estado.setCircleBackgroundColor(Color.parseColor(colores[22]));

        /////////////////////////////////////////////COLORS//////////////////////////////////////////
        theme_color_barra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog dialog = ColorPickerDialog.newBuilder()
                        .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                        .setDialogId(0)
                        .setColorShape(ColorShape.CIRCLE)
                        .setAllowPresets(true)
                        .setAllowCustom(true)
                        .setShowAlphaSlider(false)
                        .setShowColorShades(true)
                        .setColor(Color.BLACK)
                        .create();
                dialog.setColorPickerDialogListener(new ColorPickerDialogListener() {
                    @Override
                    public void onColorSelected(int dialogId, int color) {
//                        String c = (""+color).replace("0x","#");
                        String c = "#"+Integer.toHexString(color);
                        if(!colores[0].equals(c)) cambios=true;
                        colores[0] = c;
                        theme_color_barra.setCircleBackgroundColor(color);
                        Log.e("MAIN",dialogId+" -- "+c);
                    }
                    @Override
                    public void onDialogDismissed(int dialogId) {}
                });
                dialog.show(mainActivity.getSupportFragmentManager(), "ColorPicker");

            }
        });
        theme_color_barra_estado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog dialog = ColorPickerDialog.newBuilder()
                        .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                        .setDialogId(0)
                        .setColorShape(ColorShape.CIRCLE)
                        .setAllowPresets(true)
                        .setAllowCustom(true)
                        .setShowAlphaSlider(false)
                        .setShowColorShades(true)
                        .setColor(Color.BLACK)
                        .create();
                dialog.setColorPickerDialogListener(new ColorPickerDialogListener() {
                    @Override
                    public void onColorSelected(int dialogId, int color) {
//                        String c = (""+color).replace("0x","#");
                        String c = "#"+Integer.toHexString(color);
                        if(!colores[22].equals(c)) cambios=true;
                        colores[22] = c;
                        theme_color_barra.setCircleBackgroundColor(color);
                        Log.e("MAIN",dialogId+" -- "+c);
                    }
                    @Override
                    public void onDialogDismissed(int dialogId) {}
                });
                dialog.show(mainActivity.getSupportFragmentManager(), "ColorPicker");

            }
        });
        theme_color_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog dialog = ColorPickerDialog.newBuilder()
                        .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                        .setDialogId(1)
                        .setColorShape(ColorShape.CIRCLE)
                        .setAllowPresets(true)
                        .setAllowCustom(true)
                        .setShowAlphaSlider(false)
                        .setShowColorShades(true)
                        .setColor(Color.BLACK)
                        .create();
                dialog.setColorPickerDialogListener(new ColorPickerDialogListener() {
                    @Override
                    public void onColorSelected(int dialogId, int color) {
                        String c = "#"+Integer.toHexString(color);
                        if(!colores[1].equals(c)) cambios=true;
                        colores[1] = c;
                        theme_color_btn.setCircleBackgroundColor(color);
                        Log.e("MAIN",dialogId+" -- "+c);
                    }
                    @Override
                    public void onDialogDismissed(int dialogId) {}
                });
                dialog.show(mainActivity.getSupportFragmentManager(), "ColorPicker");

            }
        });
        theme_color_fondo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog dialog = ColorPickerDialog.newBuilder()
                        .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                        .setDialogId(2)
                        .setColorShape(ColorShape.CIRCLE)
                        .setAllowPresets(true)
                        .setAllowCustom(true)
                        .setShowAlphaSlider(false)
                        .setShowColorShades(true)
                        .setColor(Color.BLACK)
                        .create();
                dialog.setColorPickerDialogListener(new ColorPickerDialogListener() {
                    @Override
                    public void onColorSelected(int dialogId, int color) {
                        String c = "#"+Integer.toHexString(color);
                        if(!colores[2].equals(c)) cambios=true;
                        colores[2] = c;
                        theme_color_fondo.setCircleBackgroundColor(color);
                        Log.e("MAIN",dialogId+" -- "+c);
                    }
                    @Override
                    public void onDialogDismissed(int dialogId) {}
                });
                dialog.show(mainActivity.getSupportFragmentManager(), "ColorPicker");

            }
        });
        theme_color_texto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog dialog = ColorPickerDialog.newBuilder()
                        .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                        .setDialogTitle(R.string.theme_font)
                        .setDialogId(3)
                        .setColorShape(ColorShape.CIRCLE)
                        .setAllowPresets(true)
                        .setAllowCustom(true)
                        .setShowAlphaSlider(false)
                        .setShowColorShades(true)
                        .setColor(Color.BLACK)
                        .create();
                dialog.setColorPickerDialogListener(new ColorPickerDialogListener() {
                    @Override
                    public void onColorSelected(int dialogId, int color) {
                        String c = "#"+Integer.toHexString(color);
                        if(!colores[3].equals(c)) cambios=true;
                        colores[3] = c;
                        theme_color_texto.setCircleBackgroundColor(color);
                        Log.e("MAIN",dialogId+" -- "+c);
                    }
                    @Override
                    public void onDialogDismissed(int dialogId) {}
                });
                dialog.show(mainActivity.getSupportFragmentManager(), "ColorPicker");

            }
        });
        theme_color_interior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog dialog = ColorPickerDialog.newBuilder()
                        .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                        .setDialogId(4)
                        .setColorShape(ColorShape.CIRCLE)
                        .setAllowPresets(true)
                        .setAllowCustom(true)
                        .setShowAlphaSlider(false)
                        .setShowColorShades(true)
                        .setColor(Color.BLACK)
                        .create();
                dialog.setColorPickerDialogListener(new ColorPickerDialogListener() {
                    @Override
                    public void onColorSelected(int dialogId, int color) {
                        String c = "#"+Integer.toHexString(color);
                        if(!colores[4].equals(c)) cambios=true;
                        colores[4] = c;
                        theme_color_interior.setCircleBackgroundColor(color);
                        Log.e("MAIN",dialogId+" -- "+c);
                    }
                    @Override
                    public void onDialogDismissed(int dialogId) {}
                });
                dialog.show(mainActivity.getSupportFragmentManager(), "ColorPicker");

            }
        });
        theme_color_msg_izq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog dialog = ColorPickerDialog.newBuilder()
                        .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                        .setDialogId(5)
                        .setColorShape(ColorShape.CIRCLE)
                        .setAllowPresets(true)
                        .setAllowCustom(true)
                        .setShowAlphaSlider(true)
                        .setShowColorShades(true)
                        .setColor(Color.BLACK)
                        .create();
                dialog.setColorPickerDialogListener(new ColorPickerDialogListener() {
                    @Override
                    public void onColorSelected(int dialogId, int color) {
                        String c = "#"+Integer.toHexString(color);
                        if(!colores[5].equals(c)) cambios=true;
                        colores[5] = c;
                        theme_color_msg_izq.setCircleBackgroundColor(color);
                        Log.e("MAIN",dialogId+" -- "+c);
                    }
                    @Override
                    public void onDialogDismissed(int dialogId) {}
                });
                dialog.show(mainActivity.getSupportFragmentManager(), "ColorPicker");

            }
        });
        theme_color_msg_der.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog dialog = ColorPickerDialog.newBuilder()
                        .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                        .setDialogId(6)
                        .setColorShape(ColorShape.CIRCLE)
                        .setAllowPresets(true)
                        .setAllowCustom(true)
                        .setShowAlphaSlider(true)
                        .setShowColorShades(true)
                        .setColor(Color.BLACK)
                        .create();
                dialog.setColorPickerDialogListener(new ColorPickerDialogListener() {
                    @Override
                    public void onColorSelected(int dialogId, int color) {
                        String c = "#"+Integer.toHexString(color);
                        if(!colores[6].equals(c)) cambios=true;
                        colores[6] = c;
                        theme_color_msg_der.setCircleBackgroundColor(color);
                        Log.e("MAIN",dialogId+" -- "+c);
                    }
                    @Override
                    public void onDialogDismissed(int dialogId) {}
                });
                dialog.show(mainActivity.getSupportFragmentManager(), "ColorPicker");

            }
        });
        theme_color_msg_fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog dialog = ColorPickerDialog.newBuilder()
                        .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                        .setDialogId(7)
                        .setColorShape(ColorShape.CIRCLE)
                        .setAllowPresets(true)
                        .setAllowCustom(true)
                        .setShowAlphaSlider(true)
                        .setShowColorShades(true)
                        .setColor(Color.BLACK)
                        .create();
                dialog.setColorPickerDialogListener(new ColorPickerDialogListener() {
                    @Override
                    public void onColorSelected(int dialogId, int color) {
                        String c = "#"+Integer.toHexString(color);
                        if(!colores[7].equals(c)) cambios=true;
                        colores[7] = c;
                        theme_color_msg_fecha.setCircleBackgroundColor(color);
                        Log.e("MAIN",dialogId+" -- "+c);
                    }
                    @Override
                    public void onDialogDismissed(int dialogId) {}
                });
                dialog.show(mainActivity.getSupportFragmentManager(), "ColorPicker");

            }
        });
        theme_color_accento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog dialog = ColorPickerDialog.newBuilder()
                        .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                        .setDialogId(8)
                        .setColorShape(ColorShape.CIRCLE)
                        .setPresets(COLORS_ACCENT)
                        .setAllowPresets(false)
                        .setAllowCustom(false)
                        .setShowAlphaSlider(false)
                        .setShowColorShades(false)
                        .setColor(0xFF3F51B5)
                        .create();
                dialog.setColorPickerDialogListener(new ColorPickerDialogListener() {
                    @Override
                    public void onColorSelected(int dialogId, int color) {
                        String c = "#"+Integer.toHexString(color);
                        if(!colores[8].equals(c)) cambios=true;
                        colores[8] = c;
                        theme_color_accento.setCircleBackgroundColor(color);
                        Log.e("MAIN",dialogId+" -- "+c);
                    }
                    @Override
                    public void onDialogDismissed(int dialogId) {}
                });
                dialog.show(mainActivity.getSupportFragmentManager(), "ColorPicker");
            }
        });

        theme_color_ico_gen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog dialog = ColorPickerDialog.newBuilder()
                        .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                        .setDialogId(9)
                        .setColorShape(ColorShape.CIRCLE)
                        .setAllowPresets(true)
                        .setAllowCustom(true)
                        .setShowAlphaSlider(false)
                        .setShowColorShades(true)
                        .setColor(Color.BLACK)
                        .create();
                dialog.setColorPickerDialogListener(new ColorPickerDialogListener() {
                    @Override
                    public void onColorSelected(int dialogId, int color) {
                        String c = "#"+Integer.toHexString(color);
                        if(!colores[9].equals(c)) cambios=true;
                        colores[9] = c;
                        theme_color_ico_gen.setCircleBackgroundColor(color);
                        Log.e("MAIN",dialogId+" -- "+c);
                    }
                    @Override
                    public void onDialogDismissed(int dialogId) {}
                });
                dialog.show(mainActivity.getSupportFragmentManager(), "ColorPicker");

            }
        });
        theme_color_dialogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog dialog = ColorPickerDialog.newBuilder()
                        .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                        .setDialogId(15)
                        .setColorShape(ColorShape.CIRCLE)
                        .setAllowPresets(true)
                        .setAllowCustom(true)
                        .setShowAlphaSlider(false)
                        .setShowColorShades(true)
                        .setColor(Color.BLACK)
                        .create();
                dialog.setColorPickerDialogListener(new ColorPickerDialogListener() {
                    @Override
                    public void onColorSelected(int dialogId, int color) {
                        String c = "#"+Integer.toHexString(color);
                        if(!colores[15].equals(c)) cambios=true;
                        colores[15] = c;
                        theme_color_dialogo.setCircleBackgroundColor(color);
                        Log.e("MAIN",dialogId+" -- "+c);
                    }
                    @Override
                    public void onDialogDismissed(int dialogId) {}
                });
                dialog.show(mainActivity.getSupportFragmentManager(), "ColorPicker");

            }
        });
        theme_color_toast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog dialog = ColorPickerDialog.newBuilder()
                        .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                        .setDialogId(16)
                        .setColorShape(ColorShape.CIRCLE)
                        .setAllowPresets(true)
                        .setAllowCustom(true)
                        .setShowAlphaSlider(true)
                        .setShowColorShades(true)
                        .setColor(Color.BLACK)
                        .create();
                dialog.setColorPickerDialogListener(new ColorPickerDialogListener() {
                    @Override
                    public void onColorSelected(int dialogId, int color) {
                        String c = "#"+Integer.toHexString(color);
                        if(!colores[16].equals(c)) cambios=true;
                        colores[16] = c;
                        theme_color_toast.setCircleBackgroundColor(color);
                        Log.e("MAIN",dialogId+" -- "+c);
                    }
                    @Override
                    public void onDialogDismissed(int dialogId) {}
                });
                dialog.show(mainActivity.getSupportFragmentManager(), "ColorPicker");

            }
        });
        theme_color_burbuja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog dialog = ColorPickerDialog.newBuilder()
                        .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                        .setDialogId(17)
                        .setColorShape(ColorShape.CIRCLE)
                        .setAllowPresets(true)
                        .setAllowCustom(true)
                        .setShowAlphaSlider(true)
                        .setShowColorShades(true)
                        .setColor(Color.BLACK)
                        .create();
                dialog.setColorPickerDialogListener(new ColorPickerDialogListener() {
                    @Override
                    public void onColorSelected(int dialogId, int color) {
                        String c = "#"+Integer.toHexString(color);
                        if(!colores[17].equals(c)) cambios=true;
                        colores[17] = c;
                        theme_color_burbuja.setCircleBackgroundColor(color);
                        Log.e("MAIN",dialogId+" -- "+c);
                    }
                    @Override
                    public void onDialogDismissed(int dialogId) {}
                });
                dialog.show(mainActivity.getSupportFragmentManager(), "ColorPicker");

            }
        });
        theme_color_barchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog dialog = ColorPickerDialog.newBuilder()
                        .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                        .setDialogId(19)
                        .setColorShape(ColorShape.CIRCLE)
                        .setAllowPresets(true)
                        .setAllowCustom(true)
                        .setShowAlphaSlider(false)
                        .setShowColorShades(true)
                        .setColor(Color.BLACK)
                        .create();
                dialog.setColorPickerDialogListener(new ColorPickerDialogListener() {
                    @Override
                    public void onColorSelected(int dialogId, int color) {
                        String c = "#"+Integer.toHexString(color);
                        if(!colores[19].equals(c)) cambios=true;
                        colores[19] = c;
                        theme_color_barchat.setCircleBackgroundColor(color);
                        Log.e("MAIN",dialogId+" -- "+c);
                    }
                    @Override
                    public void onDialogDismissed(int dialogId) {}
                });
                dialog.show(mainActivity.getSupportFragmentManager(), "ColorPicker");

            }
        });

        theme_color_sel_msj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog dialog = ColorPickerDialog.newBuilder()
                        .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                        .setDialogId(21)
                        .setColorShape(ColorShape.CIRCLE)
                        .setAllowPresets(true)
                        .setAllowCustom(true)
                        .setShowAlphaSlider(true)
                        .setShowColorShades(true)
                        .setColor(Color.BLACK)
                        .create();
                dialog.setColorPickerDialogListener(new ColorPickerDialogListener() {
                    @Override
                    public void onColorSelected(int dialogId, int color) {
                        String c = "#"+Integer.toHexString(color);
                        if(!colores[21].equals(c)) cambios=true;
                        colores[21] = c;
                        theme_color_sel_msj.setCircleBackgroundColor(color);
                        Log.e("MAIN",dialogId+" -- "+c);
                    }
                    @Override
                    public void onDialogDismissed(int dialogId) {}
                });
                dialog.show(mainActivity.getSupportFragmentManager(), "ColorPicker");

            }
        });
        /////////////////////////////////////////////COLORS//////////////////////////////////////////
        /////////////////////////////////////////////FONT//////////////////////////////////////////
        theme_font_barra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog dialog = ColorPickerDialog.newBuilder()
                        .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                        .setDialogTitle(R.string.theme_font)
                        .setDialogId(10)
                        .setColorShape(ColorShape.CIRCLE)
                        .setAllowPresets(true)
                        .setAllowCustom(true)
                        .setShowAlphaSlider(false)
                        .setShowColorShades(true)
                        .create();
                dialog.setColorPickerDialogListener(new ColorPickerDialogListener() {
                    @Override
                    public void onColorSelected(int dialogId, int color) {
                        String c = "#"+Integer.toHexString(color);
                        if(!colores[10].equals(c)) cambios=true;
                        colores[10] = c;
                        theme_font_barra.setCircleBackgroundColor(color);
                        Log.e("MAIN",dialogId+" -- "+c);
                    }
                    @Override
                    public void onDialogDismissed(int dialogId) {}
                });
                dialog.show(mainActivity.getSupportFragmentManager(), "ColorPicker");

            }
        });
        theme_font_msg_izq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog dialog = ColorPickerDialog.newBuilder()
                        .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                        .setDialogTitle(R.string.theme_font)
                        .setDialogId(11)
                        .setColorShape(ColorShape.CIRCLE)
                        .setAllowPresets(true)
                        .setAllowCustom(true)
                        .setShowAlphaSlider(false)
                        .setShowColorShades(true)
                        .setColor(Color.BLACK)
                        .create();
                dialog.setColorPickerDialogListener(new ColorPickerDialogListener() {
                    @Override
                    public void onColorSelected(int dialogId, int color) {
                        String c = "#"+Integer.toHexString(color);
                        if(!colores[11].equals(c)) cambios=true;
                        colores[11] = c;
                        theme_font_msg_izq.setCircleBackgroundColor(color);
                        Log.e("MAIN",dialogId+" -- "+c);
                    }
                    @Override
                    public void onDialogDismissed(int dialogId) {}
                });
                dialog.show(mainActivity.getSupportFragmentManager(), "ColorPicker");

            }
        });
        theme_font_msg_der.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog dialog = ColorPickerDialog.newBuilder()
                        .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                        .setDialogTitle(R.string.theme_font)
                        .setDialogId(12)
                        .setColorShape(ColorShape.CIRCLE)
                        .setAllowPresets(true)
                        .setAllowCustom(true)
                        .setShowAlphaSlider(false)
                        .setShowColorShades(true)
                        .setColor(Color.BLACK)
                        .create();
                dialog.setColorPickerDialogListener(new ColorPickerDialogListener() {
                    @Override
                    public void onColorSelected(int dialogId, int color) {
                        String c = "#"+Integer.toHexString(color);
                        if(!colores[12].equals(c)) cambios=true;
                        colores[12] = c;
                        theme_font_msg_der.setCircleBackgroundColor(color);
                        Log.e("MAIN",dialogId+" -- "+c);
                    }
                    @Override
                    public void onDialogDismissed(int dialogId) {}
                });
                dialog.show(mainActivity.getSupportFragmentManager(), "ColorPicker");

            }
        });
        theme_font_msg_fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog dialog = ColorPickerDialog.newBuilder()
                        .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                        .setDialogTitle(R.string.theme_font)
                        .setDialogId(13)
                        .setColorShape(ColorShape.CIRCLE)
                        .setAllowPresets(true)
                        .setAllowCustom(true)
                        .setShowAlphaSlider(false)
                        .setShowColorShades(true)
                        .setColor(Color.BLACK)
                        .create();
                dialog.setColorPickerDialogListener(new ColorPickerDialogListener() {
                    @Override
                    public void onColorSelected(int dialogId, int color) {
                        String c = "#"+Integer.toHexString(color);
                        if(!colores[13].equals(c)) cambios=true;
                        colores[13] = c;
                        theme_font_msg_fecha.setCircleBackgroundColor(color);
                        Log.e("MAIN",dialogId+" -- "+c);
                    }
                    @Override
                    public void onDialogDismissed(int dialogId) {}
                });
                dialog.show(mainActivity.getSupportFragmentManager(), "ColorPicker");

            }
        });
        theme_font_ico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog dialog = ColorPickerDialog.newBuilder()
                        .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                        .setDialogTitle(R.string.theme_font)
                        .setDialogId(14)
                        .setColorShape(ColorShape.CIRCLE)
                        .setAllowPresets(true)
                        .setAllowCustom(true)
                        .setShowAlphaSlider(false)
                        .setShowColorShades(true)
                        .setColor(Color.BLACK)
                        .create();
                dialog.setColorPickerDialogListener(new ColorPickerDialogListener() {
                    @Override
                    public void onColorSelected(int dialogId, int color) {
                        String c = "#"+Integer.toHexString(color);
                        if(!colores[14].equals(c)) cambios=true;
                        colores[14] = c;
                        theme_font_ico.setCircleBackgroundColor(color);
                        Log.e("MAIN",dialogId+" -- "+c);
                    }
                    @Override
                    public void onDialogDismissed(int dialogId) {}
                });
                dialog.show(mainActivity.getSupportFragmentManager(), "ColorPicker");

            }
        });
        theme_font_burbuja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog dialog = ColorPickerDialog.newBuilder()
                        .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                        .setDialogTitle(R.string.theme_font)
                        .setDialogId(18)
                        .setColorShape(ColorShape.CIRCLE)
                        .setAllowPresets(true)
                        .setAllowCustom(true)
                        .setShowAlphaSlider(false)
                        .setShowColorShades(true)
                        .create();
                dialog.setColorPickerDialogListener(new ColorPickerDialogListener() {
                    @Override
                    public void onColorSelected(int dialogId, int color) {
                        String c = "#"+Integer.toHexString(color);
                        if(!colores[18].equals(c)) cambios=true;
                        colores[18] = c;
                        theme_font_burbuja.setCircleBackgroundColor(color);
                        Log.e("MAIN",dialogId+" -- "+c);
                    }
                    @Override
                    public void onDialogDismissed(int dialogId) {}
                });
                dialog.show(mainActivity.getSupportFragmentManager(), "ColorPicker");

            }
        });
        theme_font_barchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog dialog = ColorPickerDialog.newBuilder()
                        .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                        .setDialogTitle(R.string.theme_font)
                        .setDialogId(20)
                        .setColorShape(ColorShape.CIRCLE)
                        .setAllowPresets(true)
                        .setAllowCustom(true)
                        .setShowAlphaSlider(false)
                        .setShowColorShades(true)
                        .create();
                dialog.setColorPickerDialogListener(new ColorPickerDialogListener() {
                    @Override
                    public void onColorSelected(int dialogId, int color) {
                        String c = "#"+Integer.toHexString(color);
                        if(!colores[20].equals(c)) cambios=true;
                        colores[20] = c;
                        theme_font_barchat.setCircleBackgroundColor(color);
                        Log.e("MAIN",dialogId+" -- "+c);
                    }
                    @Override
                    public void onDialogDismissed(int dialogId) {}
                });
                dialog.show(mainActivity.getSupportFragmentManager(), "ColorPicker");

            }
        });
        /////////////////////////////////////////////FONT//////////////////////////////////////////

    }

    private void showDialogSave(String s, boolean esO) {
        esOscuro = esO;
        Dialog dialogo = new Dialog(context);
        dialogo.requestWindowFeature(1);
        View mviewe=getLayoutInflater().inflate(R.layout.dialog_new_theme,null);
        dialogo.setContentView(mviewe);

        RadioGroup radioGroup_add = mviewe.findViewById(R.id.radioGroup_add);
        if(esOscuro) ((RadioButtonGI)mviewe.findViewById(R.id.radio_es_oscuro)).setChecked(true);
        else ((RadioButtonGI)mviewe.findViewById(R.id.radio_es_claro)).setChecked(true);
        radioGroup_add.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==R.id.radio_es_oscuro) esOscuro = true;
                else esOscuro = false;
            }
        });

        EmojiEditText text_name = mviewe.findViewById(R.id.text_name);
        text_name.setText(s);

        View btn_ok=mviewe.findViewById(R.id.btn_ok);
        View btn_cancel=mviewe.findViewById(R.id.btn_cancel);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = text_name.getText().toString().trim();
                if(name.isEmpty()) Utils.ShowToastAnimated(mainActivity, "Debe establecer un nombre", R.raw.chats_infotip);
                else {
//                    if(name.contains(" ")) Utils.ShowToastAnimated(mainActivity, "El nombre no puede tener espacios", R.raw.error);
//                    else
                    if(checkName(name)){
                        dialogo.dismiss();
                        if(dbWorker.existeTemaNombre(name)){
                            Dialog dialogo = new Dialog(context);
                            dialogo.requestWindowFeature(1);
                            View mview=getLayoutInflater().inflate(R.layout.dialog_confirm,null);
                            dialogo.setContentView(mview);

                            ImageView icono_eliminar = mview.findViewById(R.id.icono_eliminar);
                            TextView text_icono = mview.findViewById(R.id.text_icono);
                            TextView text_eliminar = mview.findViewById(R.id.text_eliminar);
                            TextView btn_ok=mview.findViewById(R.id.btn_ok);
                            TextView btn_cancel=mview.findViewById(R.id.btn_cancel);

                            icono_eliminar.setImageResource(R.drawable.danger);
                            text_icono.setText("¡ADVERTENCIA!");
                            text_eliminar.setText("Ya existe un tema con este nombre, ¿desea continuar?.");
                            btn_ok.setText("GUARDAR");
                            btn_cancel.setText("VOLVER");

                            btn_ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialogo.dismiss();

                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
                                    Date date = new Date();
                                    String id = "theme"+sdf.format(date);

                                    dbWorker.insertarNuevoTema(new ItemTemas(id, name, 3, esOscuro,
                                            rutaFondo.isEmpty()?"":new File(rutaFondo).getName(),YouChatApplication.correo,
                                            colores[0], colores[1], colores[2], colores[3], colores[4], colores[5], colores[6], colores[7]
                                            , colores[8], colores[9], colores[10], colores[11], colores[12], colores[13], colores[14],
                                            colores[15], colores[16], colores[17], colores[18], colores[19], colores[20], colores[21], colores[22]));

                                    mainActivity.atrasFragment();
                                    Utils.ShowToastAnimated(mainActivity, "Tema añadido con éxito", R.raw.contact_check);
                                }
                            });
                            btn_cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialogo.dismiss();
                                    showDialogSave(name,esOscuro);
                                }
                            });

                            dialogo.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                            dialogo.setCancelable(true);
                            dialogo.show();
                        }
                        else{
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
                            Date date = new Date();
                            String id = "theme"+sdf.format(date);

                            dbWorker.insertarNuevoTema(new ItemTemas(id, name, 3, esOscuro,
                                    rutaFondo.isEmpty()?"":new File(rutaFondo).getName(),YouChatApplication.correo,
                                    colores[0], colores[1], colores[2], colores[3], colores[4], colores[5], colores[6], colores[7]
                                    , colores[8], colores[9], colores[10], colores[11], colores[12], colores[13], colores[14],
                                    colores[15], colores[16], colores[17], colores[18], colores[19], colores[20], colores[21], colores[22]));
                            mainActivity.atrasFragment();
                            Utils.ShowToastAnimated(mainActivity, "Tema añadido con éxito", R.raw.contact_check);
                        }
                    }
                }
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogo.dismiss();
            }
        });

        dialogo.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialogo.setCancelable(true);
        dialogo.show();
    }

    private boolean checkName(String name) {
        for(int i=0 ; i<name.length() ; i++){
            if(!Character.isLetterOrDigit(name.charAt(i)) && !(name.charAt(i)=='_') && !(name.charAt(i)==' ')){
                Utils.ShowToastAnimated(mainActivity, "El nombre contiene caracteres inválidos", R.raw.error);
                return false;
            }
        }
        return true;
    }

    public void atras() {
        if(cambios){
            showDialogConfirm();
        }
        else mainActivity.atrasFragment();
    }

    private void showDialogConfirm() {
        Dialog dialogo = new Dialog(context);
        dialogo.requestWindowFeature(1);
        View mviewe=getLayoutInflater().inflate(R.layout.dialog_confirm,null);
        dialogo.setContentView(mviewe);

        ImageView icono_eliminar = mviewe.findViewById(R.id.icono_eliminar);
        TextView text_icono = mviewe.findViewById(R.id.text_icono);
        TextView text_eliminar = mviewe.findViewById(R.id.text_eliminar);
        TextView btn_ok=mviewe.findViewById(R.id.btn_ok);
        View btn_cancel=mviewe.findViewById(R.id.btn_cancel);

        icono_eliminar.setImageResource(R.drawable.danger);
        text_icono.setText("¡ADVERTENCIA!");
        text_eliminar.setText("Se han realizado cambios en el tema actual, ¿desea continuar?.");
        btn_ok.setText("ACEPTAR");

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogo.dismiss();
                mainActivity.atrasFragment();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogo.dismiss();
            }
        });

        dialogo.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialogo.setCancelable(true);
        dialogo.show();
    }

    public void ponerFondo(String ruta) {
        rutaFondo = ruta;
        Glide.with(context)
                .load(ruta)
                .error(R.drawable.image_placeholder)
                .into(imgView_add_theme);
    }

    //                ColorPickerDialog.newBuilder()
//                        .setDialogType(ColorPickerDialog.TYPE_PRESETS)
//                        .setAllowPresets(true)
//                        .setDialogId(777)
//                        .setColor(Color.BLACK)
//                        .setShowAlphaSlider(true)
//                        .setDialogTitle(R.string.album_title)
//                        .setCustomButtonText(R.string.cpv_custom)
//                        .setPresetsButtonText(R.string.cpv_presets)
//                        .setSelectedButtonText(R.string.cpv_select)
//                        .show(mainActivity);
}