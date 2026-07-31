package cu.alexgi.youchat.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vanniktech.emoji.EmojiTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import cu.alexgi.youchat.CircleImageView;
import cu.alexgi.youchat.Convertidor;
import cu.alexgi.youchat.PrincipalActivity;
import cu.alexgi.youchat.R;
import cu.alexgi.youchat.Utils;
import cu.alexgi.youchat.YouChatApplication;
import cu.alexgi.youchat.items.ItemChat;
import cu.alexgi.youchat.items.ItemContacto;
import cu.alexgi.youchat.items.ItemUsuario;

import static cu.alexgi.youchat.MainActivity.dbWorker;

public class AdaptadorDatosUsuario extends RecyclerView.Adapter<AdaptadorDatosUsuario.ViewHolderDatos> implements View.OnClickListener, View.OnLongClickListener {

    private ArrayList<ItemUsuario> listaDatos;
    private View.OnClickListener listener;
    private View.OnLongClickListener onLongClickListener;
    private Context context;
    private PrincipalActivity principalActivity;
    //private PrincipalFragment principalFragment;
    private ColorStateList stateList,stateListEstadoView;
    private ItemContacto contacto;

    public AdaptadorDatosUsuario(Context c, ArrayList<ItemUsuario> listaDatos, PrincipalActivity pa) {
        context = c;
        this.listaDatos = listaDatos;
        principalActivity = pa;
        //principalFragment= pa.principalFragment;

        stateList = ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getColor_btn()));
        stateListEstadoView = ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getColor_texto_oscuro()));
    }

    @Override
    public ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_usuario,null, false);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);

        return new ViewHolderDatos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderDatos holder, int position) {
        ItemUsuario usuario = listaDatos.get(position);
        holder.AsignarDatos(usuario);
    }

    @Override
    public int getItemCount() {
        return listaDatos.size();
    }

    public void setOnClickListener(View.OnClickListener l)
    {
        listener=l;
    }
    public void setOnLongClickListener(View.OnLongClickListener l)
    {
        onLongClickListener=l;
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

    public class ViewHolderDatos extends RecyclerView.ViewHolder {

        //lo q esta en el xml item_1
        private View ll_usuario_seleccionar;
        private EmojiTextView usuarios_lu, ult_mensaje;
        private TextView cantMsg, usuarios_time;
        private AppCompatImageView estado_view;
        private ImageView anclado_view,estado_type,user_verificado;
        private CircleImageView item_seleccionado, item_en_linea, preview_chat_image;
        private View preview_chat_item,viewBackground,user_silenciado;

        //ImageView fotos;


        public ViewHolderDatos(@NonNull View itemView) {
            super(itemView);
            usuarios_lu = itemView.findViewById(R.id.usuarios_lu);
            ult_mensaje = itemView.findViewById(R.id.ult_mensaje);
            cantMsg = itemView.findViewById(R.id.usuarios_cont);
            estado_view = itemView.findViewById(R.id.estado_view);
            usuarios_time=itemView.findViewById(R.id.usuarios_time);
            preview_chat_image = itemView.findViewById(R.id.preview_chat_image);
            anclado_view=itemView.findViewById(R.id.anclado_view);
            estado_type=itemView.findViewById(R.id.estado_type);
            ll_usuario_seleccionar = itemView.findViewById(R.id.ll_usuario_seleccionar);
            item_seleccionado = itemView.findViewById(R.id.item_seleccionado);
            item_en_linea = itemView.findViewById(R.id.item_en_linea);

            preview_chat_item = itemView.findViewById(R.id.preview_chat_item);
            viewBackground = itemView.findViewById(R.id.viewBackground);
            user_silenciado = itemView.findViewById(R.id.user_silenciado);
            user_verificado = itemView.findViewById(R.id.user_verificado);
        }

        @SuppressLint("RestrictedApi")
        public synchronized void AsignarDatos(final ItemUsuario usuario) {

            String cor = usuario.getCorreo();
            int cant = usuario.getCant_mensajes();

            if(YouChatApplication.maxLines==2){
                ult_mensaje.setMaxLines(1);
                ult_mensaje.setMinLines(1);
            }
            else {
                ult_mensaje.setMaxLines(2);
                ult_mensaje.setMinLines(2);
            }

            item_seleccionado.setCircleBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_btn()));
            item_seleccionado.setBorderColor(Color.parseColor(YouChatApplication.itemTemas.getColor_btn()));

            item_en_linea.setCircleBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_btn()));
            item_en_linea.setBorderColor(Color.parseColor(YouChatApplication.itemTemas.getColor_fondo()));

            if(usuario.isEstaSeleccionado()){
                item_seleccionado.setVisibility(View.VISIBLE);
                ll_usuario_seleccionar.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getSel_msj()));
            }
            else {
                item_seleccionado.setVisibility(View.GONE);
                ll_usuario_seleccionar.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_fondo()));
            }
            if(usuario.EsAnclado()) anclado_view.setVisibility(View.VISIBLE);
            else anclado_view.setVisibility(View.GONE);

            contacto = dbWorker.obtenerContacto(cor);
            if(contacto==null){
                contacto = new ItemContacto(cor,cor);
                contacto.setTipo_contacto(ItemContacto.TIPO_CONTACTO_INVISIBLE);
            }

            String usu = contacto.getNombreMostrar();
            usuarios_lu.setText(usu);

            int tipo = usuario.getUlt_msg_tipo();
            String text = usuario.getUlt_msg_texto();
            int estado = usuario.getUlt_msg_estado();

            ItemChat ultMsg = dbWorker.obtenerUltMsgChatDe(cor);
            String fechaAct="",horaAct="";
            if(ultMsg!=null){
                tipo = ultMsg.getTipo_mensaje();
                text = ultMsg.getMensaje();
                estado = ultMsg.getEstado();
                fechaAct = ultMsg.getFecha();
                horaAct = ultMsg.getHora();
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = new Date();
            String fecha = sdf.format(date);

            String ultHora=contacto.getUlt_hora_conex();
            String ultFecha=contacto.getUlt_fecha_conex();
            boolean estaEnLinea = verificarEnLinea(ultHora,ultFecha, fecha);
            if(estaEnLinea
                    && YouChatApplication.estaAndandoChatService()
                    && YouChatApplication.chatService.hayConex) item_en_linea.setVisibility(View.VISIBLE);
            else item_en_linea.setVisibility(View.GONE);

            if(fechaAct.equals("")) fechaAct=fecha;
            if(fechaAct.equals(fecha)){
                if(horaAct.equals("")) usuarios_time.setText("Hoy");
                else usuarios_time.setText(horaAct);
            }
            else usuarios_time.setText(Convertidor.convertirFechaAFechaLinda(fechaAct));

            String estadoPersonal = YouChatApplication.obtenerEstadoPersonalSiExisteDe(cor);
            String borrador = dbWorker.obtenerBorradorDe(cor);

            if((estadoPersonal.equals("1") || estadoPersonal.equals("2"))
                    && estaEnLinea && YouChatApplication.chatService.hayConex){
                if (estadoPersonal.equals("1")) { //texto
                    ult_mensaje.setText("escribiendo...");
                } else { //audio
                    ult_mensaje.setText("grabando audio");
                }

                ult_mensaje.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getColor_accento()));

                estado_view.setVisibility(View.GONE);
                estado_type.setVisibility(View.GONE);
            }
            else if(!borrador.equals("")){
                ult_mensaje.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getColor_texto_oscuro()));
                ult_mensaje.setText(borrador);
                estado_type.setImageResource(R.drawable.edit);
                estado_type.setVisibility(View.VISIBLE);
                estado_view.setVisibility(View.GONE);
            }
            else {

                if(cant==0){
                    usuarios_time.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getColor_texto_oscuro()));
                    ult_mensaje.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getColor_texto_oscuro()));
                }
                else{
                    usuarios_time.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getColor_texto()));
                    ult_mensaje.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getColor_texto()));
                }

                if(estadoPersonal.equals("1") || estadoPersonal.equals("2"))
                    YouChatApplication.eliminarEstadoPersonalSiExisteDe(cor);
                if(tipo!=0 && tipo%2!=1){
                    estado_view.setVisibility(View.VISIBLE);
                    if(estado==ItemChat.ESTADO_ESPERANDO) {
                        //Log.e("AdaptadorDatosUsuario",""+usuarios_lu.getText().toString()+" - ESTADO_ESPERANDO");
                        estado_view.setImageResource(R.drawable.time_circle);
                        estado_view.setSupportImageTintList(stateListEstadoView);
                    }
                    else if(estado==ItemChat.ESTADO_ERROR)
                    {
                        //Log.e("AdaptadorDatosUsuario",""+usuarios_lu.getText().toString()+" - ESTADO_ERROR");
                        estado_view.setImageResource(R.drawable.msg_est_error);
                        estado_view.setSupportImageTintList(stateList);
                    }
                    else if(estado==ItemChat.ESTADO_ENVIADO)
                    {
                        //Log.e("AdaptadorDatosUsuario",""+usuarios_lu.getText().toString()+" - ESTADO_ENVIADO");
                        estado_view.setImageResource(R.drawable.msg_est_enviado);
                        estado_view.setSupportImageTintList(stateListEstadoView);
                    }
                    else if(estado==ItemChat.ESTADO_RECIBIDO)
                    {
                        //Log.e("AdaptadorDatosUsuario",""+usuarios_lu.getText().toString()+" - ESTADO_RECIBIDO");
                        estado_view.setImageResource(R.drawable.msg_est_recibido);
                        estado_view.setSupportImageTintList(stateListEstadoView);
                    }
                    else {
                        //Log.e("AdaptadorDatosUsuario",""+usuarios_lu.getText().toString()+" - ESTADO_VISTO");
                        estado_view.setImageResource(R.drawable.msg_est_recibido);
                        estado_view.setSupportImageTintList(stateList);
                    }
                }
                else estado_view.setVisibility(View.GONE);

                if(tipo==97) {
                    ult_mensaje.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getColor_accento()));

                    estado_type.setVisibility(View.GONE);
                    ult_mensaje.setText("¡"+usu+" se ha unido a YouChat!");
                }
                else if(tipo==83) {
                    estado_type.setImageResource(R.drawable.user_3);
                    estado_type.setVisibility(View.VISIBLE);
                    ult_mensaje.setText("Solicitud de seguidor");
                }
                else if(tipo==81) {
                    estado_type.setImageResource(R.drawable.download);
                    estado_type.setVisibility(View.VISIBLE);
                    ult_mensaje.setText("Mensaje no descargado");
                }
                else if(tipo==15 || tipo==16) {
                    estado_type.setImageResource(R.drawable.document);
                    estado_type.setVisibility(View.VISIBLE);
                    ult_mensaje.setText("Tarjeta");
                }
                else if(tipo==17 || tipo==18) {
                    estado_type.setImageResource(R.drawable.activity);
                    estado_type.setVisibility(View.VISIBLE);
                    ult_mensaje.setText("Estado");
                }
                else if(tipo==13 || tipo==14) {
                    estado_type.setImageResource(R.drawable.paper);
                    estado_type.setVisibility(View.VISIBLE);
                    ult_mensaje.setText("Archivo");
                }
                else if(tipo==11 || tipo==12) {
                    estado_type.setImageResource(R.drawable.profile);
                    estado_type.setVisibility(View.VISIBLE);
                    ult_mensaje.setText(""+text);
                }
                else if(tipo==7 || tipo==8 || tipo==9 || tipo==10) {
                    estado_type.setImageResource(R.drawable.voice);
                    estado_type.setVisibility(View.VISIBLE);
                    ult_mensaje.setText("Audio "+text);
                }
                else if(tipo==3 || tipo==4) {
                    estado_type.setImageResource(R.drawable.image);
                    estado_type.setVisibility(View.VISIBLE);
                    if(text.equals("")) ult_mensaje.setText("Imagen");
                    else ult_mensaje.setText(text);
                }
                else if(tipo==19 || tipo==20) {
                    estado_type.setImageResource(R.drawable.ic_masks_sticker1);
                    estado_type.setVisibility(View.VISIBLE);
                    ult_mensaje.setText("Sticker");
                }
                else if(tipo==21 || tipo==22) {
                    estado_type.setImageResource(R.drawable.option_fondo);
                    estado_type.setVisibility(View.VISIBLE);
                    ult_mensaje.setText("Tema");
                }
                else {
                    estado_type.setVisibility(View.GONE);
                    ult_mensaje.setText(text);
                }
            }

            ///SILENCIADO///
            if(contacto.getSilenciado()==1) user_silenciado.setVisibility(View.VISIBLE);
            else user_silenciado.setVisibility(View.GONE);
            ///SILENCIADO///

            ///VERIFICADO///
            if(YouChatApplication.comprobarOficialidad(contacto.getCorreo())){
                user_verificado.setImageResource(R.drawable.verified_profile);
                user_verificado.setVisibility(View.VISIBLE);
            }
            else if(contacto.getCant_seguidores()>=YouChatApplication.usuMayor){
                user_verificado.setImageResource(R.drawable.vip_crown_line);
                user_verificado.setVisibility(View.VISIBLE);
            }
            else if(contacto.getCant_seguidores()>=YouChatApplication.usuMedio){
                user_verificado.setImageResource(R.drawable.vip_diamond_line);
                user_verificado.setVisibility(View.VISIBLE);
            }
            else if(contacto.getCant_seguidores()>=YouChatApplication.usuMenor){
                user_verificado.setImageResource(R.drawable.award_line);
                user_verificado.setVisibility(View.VISIBLE);
            }
            else user_verificado.setVisibility(View.GONE);
            ///VERIFICADO///

            cargarImg(contacto);

            preview_chat_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setEnabled(false);
                    if(!usuario.getCorreo().equals(YouChatApplication.idOficial)) principalActivity.MostrarPreviewChat(usuario);
                    v.setEnabled(true);
                }
            });

            if(cant==0) viewBackground.setVisibility(View.GONE);
            else{
                viewBackground.setVisibility(View.VISIBLE);
                cantMsg.setText(""+cant);
            }
        }

        public synchronized void seleccionar(boolean esSeleccionado){
            if(esSeleccionado){
                Animation anim= AnimationUtils.loadAnimation(context,R.anim.zoom_foward_in);
                item_seleccionado.startAnimation(anim);
                item_seleccionado.setVisibility(View.VISIBLE);
                ll_usuario_seleccionar.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getSel_msj()));
            }
            else {
                Animation anim= AnimationUtils.loadAnimation(context,R.anim.zoom_foward_out);
                item_seleccionado.startAnimation(anim);
                item_seleccionado.setVisibility(View.GONE);
                ll_usuario_seleccionar.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_fondo()));
            }
        }

        private synchronized boolean verificarEnLinea(String ultHora, String ultFecha, String fechaHoyAct){
            if(!ultHora.equals("") && !ultFecha.equals("")){
                if(ultFecha.equals(fechaHoyAct)){
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault());
                    Date date = new Date();
                    String fechaEntera = sdf.format(date);
                    String horaHoy = Convertidor.conversionHora(fechaEntera);

                    int intHoraHoy = Convertidor.createIntOfStringHora(horaHoy);
                    int intHoraUltMsg = Convertidor.createIntOfStringHora(ultHora);
                    int dif=Math.abs(intHoraHoy-intHoraUltMsg);
                    if(dif<=3) return true;
                    else return false;
                }
                else return false;
            }
            else return false;
        }
        private synchronized void cargarImg(ItemContacto contacto) {
            if(contacto.getCorreo().equals(YouChatApplication.idOficial)){
                preview_chat_image.setVisibility(View.VISIBLE);
                YouChatApplication.ponerIconOficial(preview_chat_image);
            }
            else {
                String cache = Utils.cargarImgCache(contacto.getRuta_img());
                if(cache.equals("")){
                    preview_chat_image.setVisibility(View.GONE);
                }
                else {
                    preview_chat_image.setVisibility(View.VISIBLE);
                    Glide.with(context).load(cache).into(preview_chat_image);
                }
            }

        }
    }
}
