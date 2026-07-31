package cu.alexgi.youchat;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.vanniktech.emoji.EmojiTextView;

import java.io.File;
import java.util.ArrayList;

import cu.alexgi.youchat.BottomSheetDialogFragment.BottomSheetDialogFragment_reacciones_estado;
import cu.alexgi.youchat.Popups.PopupOpcionesEstados;
import cu.alexgi.youchat.adapters.AdaptadorDatosMiEstado;
import cu.alexgi.youchat.items.ItemChat;
import cu.alexgi.youchat.items.ItemContacto;
import cu.alexgi.youchat.items.ItemEstado;
import cu.alexgi.youchat.swipebackfragment.BaseSwipeBackFragment;
import cu.alexgi.youchat.views_GI.CheckBoxGI;

import static cu.alexgi.youchat.MainActivity.context;
import static cu.alexgi.youchat.MainActivity.dbWorker;
import static cu.alexgi.youchat.MainActivity.mainActivity;

public class AdminEstadosActivity extends BaseSwipeBackFragment {


    private AdminEstadosActivity adminEstadosActivity;

    //recycler mis estados
    private RecyclerView lista_mis_estados;
    private ArrayList<ItemEstado> datos_mis_estados;
    private AdaptadorDatosMiEstado adaptadorMisEstados;
    private View text_vacio, atras_admin_estados;

    /////////////////////////////////BSD ESTADOS//////////////////////////////////////////////
    private int posActual;
    private ArrayList<ItemEstado> estados;
    private ImageView estado_fondo;
    private EmojiTextView tv_texto_estado, preview_infotext_estados;
    private View contenedor_preview_infotext_estados, root_estado;
    private View toolbar_visor_estado, answer;
    private TextView fecha_subida_estado;
    private CircleImageView mini_img_perfil_estados;
    private EmojiTextView nombre_usuario_estado;
    ///////////////////////////////////BSD ESTADOS////////////////////////////////////////////

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /*

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout., container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    Navigation.findNavController(view).navigateUp();

 NavOptions.Builder optionsBuilder = new NavOptions.Builder();
                optionsBuilder
                        .setEnterAnim(R.anim.fade_in_system)
                        .setExitAnim(R.anim.fade_out_system)
                        .setPopEnterAnim(R.anim.fade_in_system)
                        .setPopExitAnim(R.anim.fade_out_system);
                Bundle bundle = new Bundle();
                bundle.putString("id", materialActual.getId_material());
                navController.navigate(R.id.viewImageFragment, bundle, optionsBuilder.build());
    */

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return attachToSwipeBack(inflater.inflate(R.layout.activity_admin_estados, container, false));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adminEstadosActivity = this;
        view.findViewById(R.id.root).setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_interior()));

        if(YouChatApplication.estaAndandoChatService())
            YouChatApplication.chatService.eliminarNotiNowReaccion();

        lista_mis_estados = view.findViewById(R.id.lista_mis_estados);
        text_vacio = view.findViewById(R.id.text_vacio);
        atras_admin_estados = view.findViewById(R.id.atras_admin_estados);
        atras_admin_estados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
//                Navigation.findNavController(view).navigateUp();
            }
        });

        datos_mis_estados = new ArrayList<>();
        adaptadorMisEstados = new AdaptadorDatosMiEstado(context, datos_mis_estados, adminEstadosActivity);
        lista_mis_estados.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL,false));
        lista_mis_estados.setAdapter(adaptadorMisEstados);

        llenarEstados();
    }

    private synchronized void llenarEstados(){
        ArrayList<ItemEstado> estadosAct = dbWorker.obtenerTodosLosEstados();
        int l=estadosAct.size();
        if(l>0){
            text_vacio.setVisibility(View.GONE);
            datos_mis_estados.clear();
            for(int i=0; i<l; i++){
                String cor = estadosAct.get(i).getCorreo();
                if(cor.equals(YouChatApplication.correo)){
                    datos_mis_estados.add(estadosAct.get(i));
                }
            }
            if(datos_mis_estados.size()>0){
                adaptadorMisEstados.notifyDataSetChanged();
            }
            else text_vacio.setVisibility(View.VISIBLE);
        }
        else text_vacio.setVisibility(View.VISIBLE);
    }


    private void verificarSiExisteTextoEnImagen(String texto) {
        tv_texto_estado.setVisibility(View.GONE);
        if (texto.length() > 0){
            //contenedor_preview_infotext_estados.setVisibility(View.VISIBLE);
            preview_infotext_estados.setText(texto);
            contenedor_preview_infotext_estados.setVisibility(View.VISIBLE);
        }
        else contenedor_preview_infotext_estados.setVisibility(View.GONE);
    }
    private synchronized void ponerColorFondo(int tipo_estado) {
        if (tipo_estado < 30) {
            root_estado.setBackgroundResource(0);
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
            root_estado.setBackgroundColor(colorTarjeta);
        } else {
            root_estado.setBackgroundColor(0);
            switch (tipo_estado) {
                case 30:
                    root_estado
                            .setBackgroundResource(R.drawable.shape_fondo_estado_texto_1);
                    break;
                case 31:
                    root_estado
                            .setBackgroundResource(R.drawable.shape_fondo_estado_texto_2);
                    break;
                case 32:
                    root_estado
                            .setBackgroundResource(R.drawable.shape_fondo_estado_texto_3);
                    break;
                case 33:
                    root_estado
                            .setBackgroundResource(R.drawable.shape_fondo_estado_texto_4);
                    break;
                case 34:
                    root_estado
                            .setBackgroundResource(R.drawable.shape_fondo_estado_texto_5);
                    break;
                case 35:
                    root_estado
                            .setBackgroundResource(R.drawable.shape_fondo_estado_texto_6);
                    break;
                case 36:
                    root_estado
                            .setBackgroundResource(R.drawable.shape_fondo_estado_texto_7);
                    break;
                case 37:
                    root_estado
                            .setBackgroundResource(R.drawable.shape_fondo_estado_texto_8);
                    break;
                case 38:
                    root_estado
                            .setBackgroundResource(R.drawable.shape_fondo_estado_texto_9);
                    break;
                case 39:
                    root_estado
                            .setBackgroundResource(R.drawable.shape_fondo_estado_texto_10);
                    break;
                default:
                    root_estado
                            .setBackgroundResource(R.drawable.shape_fondo_estado_texto_1);
            }
        }

    }
    private synchronized void procesoVerificarTamTexto(String cad, EmojiTextView texto_estado) {
        int l = cad.length();
        int rango = l / 20;
        if (rango == 0) {
            texto_estado.setTextSize(40);
            texto_estado.setEmojiSizeRes(R.dimen.estado_size_40, false);
            texto_estado.setText(cad);
        } else if (rango == 1) {
            texto_estado.setTextSize(38);
            texto_estado.setEmojiSizeRes(R.dimen.estado_size_38, false);
            texto_estado.setText(cad);
        } else if (rango == 2) {
            texto_estado.setTextSize(36);
            texto_estado.setEmojiSizeRes(R.dimen.estado_size_36, false);
            texto_estado.setText(cad);
        } else if (rango == 3) {
            texto_estado.setTextSize(34);
            texto_estado.setEmojiSizeRes(R.dimen.estado_size_34, false);
            texto_estado.setText(cad);
        } else if (rango == 4) {
            texto_estado.setTextSize(32);
            texto_estado.setEmojiSizeRes(R.dimen.estado_size_32, false);
            texto_estado.setText(cad);
        } else if (rango == 5) {
            texto_estado.setTextSize(30);
            texto_estado.setEmojiSizeRes(R.dimen.estado_size_30, false);
            texto_estado.setText(cad);
        } else if (rango == 6) {
            texto_estado.setTextSize(28);
            texto_estado.setEmojiSizeRes(R.dimen.estado_size_28, false);
            texto_estado.setText(cad);
        } else if (rango == 7) {
            texto_estado.setTextSize(26);
            texto_estado.setEmojiSizeRes(R.dimen.estado_size_26, false);
            texto_estado.setText(cad);
        } else if (rango == 8) {
            texto_estado.setTextSize(24);
            texto_estado.setEmojiSizeRes(R.dimen.estado_size_24, false);
            texto_estado.setText(cad);
        } else if (rango == 9) {
            texto_estado.setTextSize(22);
            texto_estado.setEmojiSizeRes(R.dimen.estado_size_22, false);
            texto_estado.setText(cad);
        } else if (rango == 10) {
            texto_estado.setTextSize(20);
            texto_estado.setEmojiSizeRes(R.dimen.estado_size_20, false);
            texto_estado.setText(cad);
        } else if (rango == 11) {
            texto_estado.setTextSize(18);
            texto_estado.setEmojiSizeRes(R.dimen.estado_size_18, false);
            texto_estado.setText(cad);
        } else {
            texto_estado.setTextSize(16);
            texto_estado.setEmojiSizeRes(R.dimen.estado_size_16, false);
            texto_estado.setText(cad);
        }
    }

    public void abrirVisorOneEstado(ItemEstado estado) {
        if (mAddFragmentListener != null)
            mAddFragmentListener.onAddFragment(AdminEstadosActivity.this,
                    EstadosViewPagerFragment.newInstance(estado),
                    R.anim.show_layout_answer, R.anim.hide_layout_answer);
    }


    public void abrirVisorOneEstado(String correo, String id) {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(R.layout.fragment_view_status);
        bottomSheetDialog.setDismissWithAnimation(true);

        root_estado = bottomSheetDialog.findViewById(R.id.root_estado);
        estado_fondo = bottomSheetDialog.findViewById(R.id.estados_fondo);
        tv_texto_estado = bottomSheetDialog.findViewById(R.id.tv_texto_estado);
        preview_infotext_estados = bottomSheetDialog.findViewById(R.id.preview_infotext_estados);
        contenedor_preview_infotext_estados = bottomSheetDialog.findViewById(R.id.contenedor_preview_infotext_estados);
        mini_img_perfil_estados = bottomSheetDialog.findViewById(R.id.mini_img_perfil_estados);
        nombre_usuario_estado = bottomSheetDialog.findViewById(R.id.nombre_usuario_estado);
        fecha_subida_estado = bottomSheetDialog.findViewById(R.id.fecha_subida_estado);

        View input_estado_reaccionar = bottomSheetDialog.findViewById(R.id.input_estado_reaccionar);
        input_estado_reaccionar.setVisibility(View.GONE);

        toolbar_visor_estado=bottomSheetDialog.findViewById(R.id.toolbar_visor_estado);
        answer=bottomSheetDialog.findViewById(R.id.answer);
        View story=bottomSheetDialog.findViewById(R.id.story);
        View show_text=bottomSheetDialog.findViewById(R.id.show_text);
        show_text.setVisibility(View.GONE);

        story.setVisibility(View.GONE);
        answer.setVisibility(View.GONE);

        ItemEstado estado = dbWorker.obtenerEstado(id);
        ItemContacto contacto = dbWorker.obtenerContacto(correo);
        if (contacto == null) contacto = new ItemContacto(correo, correo);

        Glide.with(context)
                .load(contacto.getRuta_img())
                .error(R.drawable.profile_white)
                .into(mini_img_perfil_estados);
        nombre_usuario_estado.setText(contacto.getNombreMostrar());
        fecha_subida_estado.setText("" + Convertidor.convertirFechaAFechaLinda(estado.getFecha()) + ", " + estado.getHora());

        if (!estado.isEsta_visto()) {
            estados.get(posActual).setEsta_visto(true);
            dbWorker.marcarEstadoComoVisto(estado.getId());
        }

        if (estado.getTipo_estado() == 99) {
            verificarSiExisteTextoEnImagen(estado.getTexto());
            String rutaImg = estado.getRuta_imagen();
            File file = new File(rutaImg);
            if (file.exists()) {
                Glide.with(context).load(rutaImg)
                        .error(R.drawable.image_placeholder)
                        .into(estado_fondo);
            } else estado_fondo.setImageResource(R.drawable.image_placeholder);
            estado_fondo.setVisibility(View.VISIBLE);

        } else {
            String texto = estado.getTexto();
            procesoVerificarTamTexto(texto, tv_texto_estado);
            ponerColorFondo(estado.getTipo_estado());
        }

        bottomSheetDialog.show();

        View bottomSheetInternal = bottomSheetDialog.findViewById(R.id.design_bottom_sheet);
        BottomSheetBehavior bsb = BottomSheetBehavior.from(bottomSheetInternal);
        bsb.setState(BottomSheetBehavior.STATE_EXPANDED);

        bsb.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        bsb.setState(BottomSheetBehavior.STATE_HIDDEN);
                        break;
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                        bsb.setState(BottomSheetBehavior.STATE_HIDDEN);
                        break;

                    case BottomSheetBehavior.STATE_HIDDEN:
                        bottomSheetDialog.dismiss();
                        break;
                }
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {}
        });
    }


    //////////////////popup
    public void abrirPopupXid(View v, String idEstado){
        PopupOpcionesEstados popupOpcionesEstados = new PopupOpcionesEstados(v,this);
        popupOpcionesEstados.show(v, idEstado, false);
    }

    public synchronized void mostrarInfoEstado(String idEstado){
        int pos = buscarEstadoXid(idEstado);
        if(pos!=-1){
            ItemEstado est = datos_mis_estados.get(pos);
            BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_reacciones_estado
                    .newInstance(context,est,0);
            bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogReaccionesEstados");
        }
    }

    public synchronized void eliminarEstado(String idEstado){
        int pos = buscarEstadoXid(idEstado);
        if(pos!=-1){
            Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(1);
            View mview=getLayoutInflater().inflate(R.layout.dialog_confirm,null);
            dialog.setContentView(mview);

            TextView text_icono = mview.findViewById(R.id.text_icono);
            TextView text_eliminar = mview.findViewById(R.id.text_eliminar);
            View btn_cancel=mview.findViewById(R.id.btn_cancel);
            View btn_ok=mview.findViewById(R.id.btn_ok);

            text_icono.setText("Eliminar estado");
            text_eliminar.setText("¿Deseas eliminar este estado? Sólo se eliminará de tu teléfono.");

            View deleteForAll = mview.findViewById(R.id.deleteForAll);
            CheckBoxGI selectedForAll = mview.findViewById(R.id.selectedForAll);
            deleteForAll.setVisibility(View.VISIBLE);

            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(selectedForAll.isChecked())
                    {
                        if(YouChatApplication.chatService.hayConex){
                            dialog.dismiss();
                            datos_mis_estados.remove(pos);
                            adaptadorMisEstados.notifyItemRemoved(pos);
                            dbWorker.eliminarElEstadosDe(idEstado);

                            ArrayList<String> seguidores = dbWorker.obtenerTodosSeguidores();
                            int longi = seguidores.size();
                            int vueltas=longi/20+1;
                            int vueltas_dadas=0;
                            int ultPos=0;
                            while (vueltas_dadas<vueltas){
                                String correosNotif="";
                                int cant=0;
                                for(int i=ultPos; i<longi; i++){
                                    if(cant==20){
                                        ultPos=i;
                                        break;
                                    }
                                    if(i!=ultPos)
                                        correosNotif=correosNotif+",";
                                    correosNotif=correosNotif+seguidores.get(i);
                                    cant++;
                                }

                                ItemChat borrarEst = new ItemChat(correosNotif,"");
                                borrarEst.setId("-ce-");
                                borrarEst.setId_msg_resp(idEstado);
                                if(YouChatApplication.estaAndandoChatService())
                                YouChatApplication.chatService.enviarMensaje(borrarEst,SendMsg.CATEGORY_CHAT_ACT_VERY_MUCH);
                                vueltas_dadas++;
                            }
                        }
                        else Utils.ShowToastAnimated(mainActivity,"Compruebe su conexión",R.raw.ic_ban);
                    }
                    else{
                        dialog.dismiss();
                        datos_mis_estados.remove(pos);
                        adaptadorMisEstados.notifyItemRemoved(pos);
                        dbWorker.eliminarElEstadosDe(idEstado);

                        if(datos_mis_estados.size()==0)
                            text_vacio.setVisibility(View.VISIBLE);
                    }
                }
            });
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.setCancelable(true);
            dialog.show();
        }
    }

    private int buscarEstadoXid(String idEstado){
        int l=datos_mis_estados.size();
        for(int i=0; i<l; i++){
            if(datos_mis_estados.get(i).getId().equals(idEstado))
                return i;
        }
        return -1;
    }

    public void abrirInfoReacciones(ItemEstado estado) {
        BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_reacciones_estado
                .newInstance(context,estado,0);
        bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogReaccionesEstados");
    }

    public void abrirInfoVistas(ItemEstado estado) {
        BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_reacciones_estado
                .newInstance(context,estado,1);
        bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogReaccionesEstados");
    }
}
