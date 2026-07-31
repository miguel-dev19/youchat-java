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
import cu.alexgi.youchat.items.ItemMensajeCorreo;
import cu.alexgi.youchat.views_GI.CardViewButtonGI;
import cu.alexgi.youchat.views_GI.ImageViewIconTextGI;

import static cu.alexgi.youchat.MainActivity.dbWorker;

public class AdaptadorDatosMensajeCorreo extends RecyclerView.Adapter<AdaptadorDatosMensajeCorreo.ViewHolderDatos>
        implements View.OnClickListener, View.OnLongClickListener {

    private ArrayList<ItemMensajeCorreo> listaDatos;
    private View.OnClickListener listener;
    private View.OnLongClickListener onLongClickListener;
    private Context context;
    //private PrincipalFragment principalFragment;
    private ColorStateList stateList,stateListEstadoView;
    private ItemContacto contacto;

    public AdaptadorDatosMensajeCorreo(Context c, ArrayList<ItemMensajeCorreo> listaDatos) {
        context = c;
        this.listaDatos = listaDatos;
        //principalFragment= pa.principalFragment;

        stateList = ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getColor_btn()));
        stateListEstadoView = ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getColor_texto_oscuro()));
    }

    @Override
    public ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_correo_mensaje,null, false);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);

        return new ViewHolderDatos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderDatos holder, int position) {
        ItemMensajeCorreo usuario = listaDatos.get(position);
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
        private View favorito, ll_usuario_seleccionar, adjunto;
        private EmojiTextView usuarios_lu, ult_mensaje;
        private TextView cantMsg, usuarios_time;
        private CardViewButtonGI viewBackground;
        private ImageViewIconTextGI view_star;
        private CircleImageView icon_email;

        //ImageView fotos;


        public ViewHolderDatos(@NonNull View itemView) {
            super(itemView);
            usuarios_lu = itemView.findViewById(R.id.usuarios_lu);
            ult_mensaje = itemView.findViewById(R.id.ult_mensaje);
            cantMsg = itemView.findViewById(R.id.usuarios_cont);
            usuarios_time=itemView.findViewById(R.id.usuarios_time);
            ll_usuario_seleccionar = itemView.findViewById(R.id.ll_usuario_seleccionar);

            viewBackground = itemView.findViewById(R.id.viewBackground);
            favorito = itemView.findViewById(R.id.favorito);
            adjunto = itemView.findViewById(R.id.adjunto);
            icon_email = itemView.findViewById(R.id.icon_email);
            view_star = itemView.findViewById(R.id.view_star);
        }

        @SuppressLint("RestrictedApi")
        public synchronized void AsignarDatos(final ItemMensajeCorreo mensaje) {
            usuarios_lu.setText("De: "+mensaje.getRemitente());
            ult_mensaje.setText("Asunto: "+mensaje.getAsunto());
            if(mensaje.isEsNuevo()){
                viewBackground.setVisibility(View.VISIBLE);
                viewBackground.setCardBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_btn()));
            }
            else viewBackground.setVisibility(View.INVISIBLE);

            int cantAdjuntoTiene = dbWorker.obtenerAdjuntosCorreoDe(mensaje.getId()).size();
            if(cantAdjuntoTiene==0) adjunto.setVisibility(View.GONE);
            else adjunto.setVisibility(View.VISIBLE);

            if(mensaje.isEsFavorito()) view_star.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getColor_btn())));
            else view_star.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getColor_texto())));
            favorito.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean nuevoValor = !mensaje.isEsFavorito();
                    dbWorker.modificarFavoritoMensajeCorreo(mensaje.getId(), nuevoValor);

                    for(int i=0; i<listaDatos.size(); i++){
                        if(listaDatos.get(i).getId().equals(mensaje.getId())){
                            listaDatos.get(i).setEsFavorito(nuevoValor);
                            notifyItemChanged(i,7);
                            break;
                        }
                    }
                }
            });

            if(mensaje.isEsMio()) icon_email.setImageResource(R.drawable.mai_send);
            int color = Utils.obtenerColorDadoUnCorreo(mensaje.getCorreo());
            icon_email.setCircleBackgroundColor(color);
            icon_email.setBorderColor(color);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = new Date();
            String fecha = sdf.format(date);

            String fechaAct = mensaje.getFecha();
            String horaAct = mensaje.getHora();
            if(fechaAct.equals("")) fechaAct=fecha;
            if(fechaAct.equals(fecha)){
                if(horaAct.equals("")) usuarios_time.setText("Hoy");
                else usuarios_time.setText(horaAct);
            }
            else usuarios_time.setText(Convertidor.convertirFechaAFechaLinda(fechaAct));

            seleccionar(mensaje.isSeleccionado());
        }

        public synchronized void seleccionar(boolean esSeleccionado){
            if(esSeleccionado){
                ll_usuario_seleccionar.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getSel_msj()));
            }
            else {
                ll_usuario_seleccionar.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_fondo()));
            }
        }
    }
}
