package cu.alexgi.youchat.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vanniktech.emoji.EmojiTextView;

import java.util.ArrayList;

import cu.alexgi.youchat.CircleImageView;
import cu.alexgi.youchat.EstadisticasPersonalesFragment;
import cu.alexgi.youchat.R;
import cu.alexgi.youchat.Utils;
import cu.alexgi.youchat.YouChatApplication;
import cu.alexgi.youchat.items.ItemEstadisticaPersonal;

import static cu.alexgi.youchat.MainActivity.dbWorker;

public class AdaptadorDatosUsuarioEstadisticas extends RecyclerView.Adapter<AdaptadorDatosUsuarioEstadisticas.ViewHolderDatos> {

    private ArrayList<ItemEstadisticaPersonal> listaDatos;
    private Context context;
    private ColorStateList stateList,stateListEstadoView,stateListEstadoViewRojo;
    private EstadisticasPersonalesFragment estadisticasPersonalesFragment;

    public AdaptadorDatosUsuarioEstadisticas(Context c, ArrayList<ItemEstadisticaPersonal> listaDatos, EstadisticasPersonalesFragment epf) {
        context = c;
        this.listaDatos = listaDatos;
        stateListEstadoView = ColorStateList.valueOf(context.getResources().getColor(R.color.texto_grisoscuro_to_grisclaro));
        stateListEstadoViewRojo = ColorStateList.valueOf(context.getResources().getColor(R.color.temaRojoAccent));

        estadisticasPersonalesFragment = epf;
    }

    @Override
    public ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user_estadisticas,null, false);

        return new ViewHolderDatos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderDatos holder, int position) {
        ItemEstadisticaPersonal itemEstadisticaPersonal = listaDatos.get(position);
        holder.AsignarDatos(itemEstadisticaPersonal);
    }

    @Override
    public int getItemCount() {
        return listaDatos.size();
    }

    public class ViewHolderDatos extends RecyclerView.ViewHolder {
        EmojiTextView tv_nombre;
        TextView tv_subida, tv_bajada, tv_total;
        CircleImageView preview_image;
        View card;

        public ViewHolderDatos(@NonNull View itemView) {
            super(itemView);
            tv_nombre = itemView.findViewById(R.id.tv_nombre);
            tv_subida = itemView.findViewById(R.id.tv_subida);
            tv_bajada = itemView.findViewById(R.id.tv_bajada);
            tv_total = itemView.findViewById(R.id.tv_total);
            preview_image = itemView.findViewById(R.id.preview_image);
            card = itemView.findViewById(R.id.card);

            preview_image.setImageResource(R.drawable.profile_white);
        }

        @SuppressLint("RestrictedApi")
        public synchronized void AsignarDatos(ItemEstadisticaPersonal itemEstadisticaPersonal) {

            String ruta = dbWorker.obtenerRutaImg(itemEstadisticaPersonal.getId());
            String nombre = dbWorker.obtenerNombre(itemEstadisticaPersonal.getId());

            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    estadisticasPersonalesFragment.showBSD(nombre, ruta, itemEstadisticaPersonal);
                }
            });

            tv_subida.setText(Utils.convertirBytes(itemEstadisticaPersonal.obtenerTotalEnviado()));
            tv_bajada.setText(Utils.convertirBytes(itemEstadisticaPersonal.obtenerTotalRecibido()));
            tv_total.setText("Total: "+Utils.convertirBytes(itemEstadisticaPersonal.obtenerTotal()));
            tv_nombre.setText(nombre);
            cargarImg(ruta, itemEstadisticaPersonal.getId());
        }
        private synchronized void cargarImg(String ruta, String cor) {
            if(cor.equals(YouChatApplication.idOficial)){
                YouChatApplication.ponerIconOficial(preview_image);
            }
            else {
                String cache = Utils.cargarImgCache(ruta);
                if(cache.equals("")) preview_image.setImageResource(R.drawable.profile_white);
                else Glide.with(context).load(cache).into(preview_image);
            }

        }
    }
}
