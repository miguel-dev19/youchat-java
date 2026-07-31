package cu.alexgi.youchat.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vanniktech.emoji.EmojiTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cu.alexgi.youchat.CircleImageView;
import cu.alexgi.youchat.Convertidor;
import cu.alexgi.youchat.R;
import cu.alexgi.youchat.Utils;
import cu.alexgi.youchat.YouChatApplication;
import cu.alexgi.youchat.items.ItemContacto;
import cu.alexgi.youchat.items.ItemUsuarioCorreo;

import static cu.alexgi.youchat.MainActivity.dbWorker;

public class AdaptadorDatosUsuarioCorreo extends RecyclerView.Adapter<AdaptadorDatosUsuarioCorreo.ViewHolderDatos>
        implements View.OnClickListener, View.OnLongClickListener {

    private ArrayList<ItemUsuarioCorreo> listaDatos;
    private View.OnClickListener listener;
    private View.OnLongClickListener onLongClickListener;
    private Context context;
    //private PrincipalFragment principalFragment;
    private ColorStateList stateList,stateListEstadoView;
    private ItemContacto contacto;

    public AdaptadorDatosUsuarioCorreo(Context c, ArrayList<ItemUsuarioCorreo> listaDatos) {
        context = c;
        this.listaDatos = listaDatos;
        //principalFragment= pa.principalFragment;

        stateList = ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getColor_btn()));
        stateListEstadoView = ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getColor_texto_oscuro()));
    }

    @Override
    public ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_correo_user,null, false);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);

        return new ViewHolderDatos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderDatos holder, int position) {
        ItemUsuarioCorreo usuario = listaDatos.get(position);
        holder.AsignarDatos(usuario);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderDatos holder, int position, @NonNull List<Object> payloads) {
        if(payloads.size()>0){
            Object o = payloads.get(0);
            if(o instanceof Integer){
                int num = (int) o;
                switch (num){
                    case 1://seleccionar el elmento en pos
                            holder.seleccionar(true);
                        break;
                    case 2://deseleccionar el elmento en pos
                            holder.seleccionar(false);
                        break;
                    default:
                        super.onBindViewHolder(holder, position, payloads);
                }
            }
            else super.onBindViewHolder(holder, position, payloads);
        }
        else super.onBindViewHolder(holder, position, payloads);
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
        private TextView usuarios_cont, usuarios_time, tv_primera_letra;
        private CircleImageView item_seleccionado, preview_chat_image;
        private View viewBackground;

        //ImageView fotos;


        public ViewHolderDatos(@NonNull View itemView) {
            super(itemView);
            usuarios_lu = itemView.findViewById(R.id.usuarios_lu);
            ult_mensaje = itemView.findViewById(R.id.ult_mensaje);
            usuarios_cont = itemView.findViewById(R.id.usuarios_cont);
            usuarios_time=itemView.findViewById(R.id.usuarios_time);
            preview_chat_image = itemView.findViewById(R.id.preview_chat_image);
            ll_usuario_seleccionar = itemView.findViewById(R.id.ll_usuario_seleccionar);
            item_seleccionado = itemView.findViewById(R.id.item_seleccionado);

            viewBackground = itemView.findViewById(R.id.viewBackground);
            tv_primera_letra = itemView.findViewById(R.id.tv_primera_letra);
        }

        @SuppressLint("RestrictedApi")
        public synchronized void AsignarDatos(final ItemUsuarioCorreo usuario) {

            String cor = usuario.getCorreo();

            item_seleccionado.setCircleBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_btn()));
            item_seleccionado.setBorderColor(Color.parseColor(YouChatApplication.itemTemas.getColor_btn()));

            String primeraLetra = (""+cor.charAt(0)).toUpperCase();
            tv_primera_letra.setText(primeraLetra);

            preview_chat_image.setCircleBackgroundColor(Utils.obtenerColorDadoUnCorreo(cor));

            String usu;
            if(!cor.contains(",")){
                contacto = dbWorker.obtenerContacto(cor);
                if(contacto==null) usu = usuario.getNombre();
                else usu = contacto.getNombreMostrar();
            }
            else usu = usuario.getNombre();

            usuarios_lu.setText(usu);

//            ArrayList<ItemMensajeCorreo> mensajeCorreos = dbWorker.obtenerMensajeCorreoDe(cor);
//            ult_mensaje.setText("Correos: "+mensajeCorreos.size());
            ult_mensaje.setText(cor);

            int cantNuevosMensajes = dbWorker.obtenerCantMensajeCorreoNoVistoDe(cor);
            if(cantNuevosMensajes==0) viewBackground.setVisibility(View.GONE);
            else {
                viewBackground.setVisibility(View.VISIBLE);
                usuarios_cont.setText(""+cantNuevosMensajes);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = new Date();
            String fecha = sdf.format(date);

            String fechaAct = usuario.getFecha();
            String horaAct = usuario.getHora();
            if(fechaAct.equals("")) fechaAct=fecha;
            if(fechaAct.equals(fecha)){
                if(horaAct.equals("")) usuarios_time.setText("Hoy");
                else usuarios_time.setText(horaAct);
            }
            else usuarios_time.setText(Convertidor.convertirFechaAFechaLinda(fechaAct));

            seleccionar(usuario.isSeleccionado());
        }

        public synchronized void seleccionar(boolean esSeleccionado){
            if(esSeleccionado){
//                Animation anim= AnimationUtils.loadAnimation(context,R.anim.zoom_foward_in);
//                item_seleccionado.startAnimation(anim);
//                item_seleccionado.setVisibility(View.VISIBLE);
                ll_usuario_seleccionar.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getSel_msj()));
            }
            else {
//                Animation anim= AnimationUtils.loadAnimation(context,R.anim.zoom_foward_out);
//                item_seleccionado.startAnimation(anim);
//                item_seleccionado.setVisibility(View.GONE);
                ll_usuario_seleccionar.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_fondo()));
            }
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
