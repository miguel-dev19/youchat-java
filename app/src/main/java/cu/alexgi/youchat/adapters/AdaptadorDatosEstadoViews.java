package cu.alexgi.youchat.adapters;

import android.content.Context;
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
import java.util.Locale;

import cu.alexgi.youchat.CircleImageView;
import cu.alexgi.youchat.Convertidor;
import cu.alexgi.youchat.R;
import cu.alexgi.youchat.Utils;
import cu.alexgi.youchat.YouChatApplication;
import cu.alexgi.youchat.items.ItemVistaEstado;

import static cu.alexgi.youchat.MainActivity.dbWorker;

public class AdaptadorDatosEstadoViews extends RecyclerView.Adapter<AdaptadorDatosEstadoViews.ViewHolderDatos> {

    ArrayList<ItemVistaEstado> listaDatos;
    private Context context;
//    private PrincipalActivity principalActivity;
//    private EstadosFragment estadosFragment;
    private String fechaEntera;

    public AdaptadorDatosEstadoViews(Context c, ArrayList<ItemVistaEstado> listaDatos) {
        this.listaDatos = listaDatos;
        context = c;

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date = new Date();
        fechaEntera = sdf.format(date);
        fechaEntera = fechaEntera.replace(" ", "");
    }

    @Override
    public ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_estado_views,null, false);
        return new ViewHolderDatos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderDatos holder, int position) {
        //holder.AsignarDatos(listaDatos.get(position));
        holder.AsignarDatos(listaDatos.get(position));
    }

    @Override
    public int getItemCount() {
        return listaDatos.size();
    }


    public class ViewHolderDatos extends RecyclerView.ViewHolder {
        private CircleImageView preview_chat_item;
        private EmojiTextView tv_usuarios_view;
        private TextView hora_view;
        private View contenedor_layout_view;

        public ViewHolderDatos(@NonNull View itemView) {
            super(itemView);
            preview_chat_item = itemView.findViewById(R.id.preview_chat_item);
            tv_usuarios_view = itemView.findViewById(R.id.tv_usuarios_view);
            hora_view = itemView.findViewById(R.id.hora_view);
            contenedor_layout_view = itemView.findViewById(R.id.contenedor_layout_view);
        }
        public synchronized void AsignarDatos(ItemVistaEstado vistaEstado){
//            contenedor_layout_view.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_fondo()));

            String nombre = dbWorker.obtenerNombre(vistaEstado.getCorreo());
            String ruta = dbWorker.obtenerRutaImg(vistaEstado.getCorreo());
            cargarImg(ruta,vistaEstado.getCorreo());

            tv_usuarios_view.setText(nombre);
            hora_view.setText(Convertidor.convertirFechaAFechaLinda(vistaEstado.getFecha())+", "+vistaEstado.getHora());
        }

        private synchronized void cargarImg(String ruta,String cor){
            if(cor.equals(YouChatApplication.idOficial)){
                YouChatApplication.ponerIconOficial(preview_chat_item);
            }
            else {
                String cache = Utils.cargarImgCache(ruta);
                if(cache.equals("")) preview_chat_item.setImageResource(R.drawable.profile_white);
                else Glide.with(context).load(cache).into(preview_chat_item);
            }
        }
    }
    ///////////////////////////////////////////////////////////
}
