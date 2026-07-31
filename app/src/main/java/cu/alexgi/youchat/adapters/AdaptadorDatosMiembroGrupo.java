package cu.alexgi.youchat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import cu.alexgi.youchat.CircleImageView;
import cu.alexgi.youchat.R;
import cu.alexgi.youchat.YouChatApplication;
import cu.alexgi.youchat.items.ItemContacto;

import static cu.alexgi.youchat.MainActivity.dbWorker;

public class AdaptadorDatosMiembroGrupo extends RecyclerView.Adapter<AdaptadorDatosMiembroGrupo.ViewHolder> {

    private Context context;
    private ArrayList<String> miembros;
    private ArrayList<String> admins;
    private String creador;
    private boolean esAdmin;

    public AdaptadorDatosMiembroGrupo(Context context, ArrayList<String> miembros,
                                       ArrayList<String> admins, String creador, boolean esAdmin) {
        this.context = context;
        this.miembros = miembros;
        this.admins = admins;
        this.creador = creador;
        this.esAdmin = esAdmin;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_miembro_grupo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String correo = miembros.get(position);
        
        String nombre = dbWorker.obtenerNombre(correo);
        holder.tvNombre.setText(nombre);
        holder.tvCorreo.setText(correo);

        // Cargar foto de perfil
        ItemContacto contacto = dbWorker.obtenerContacto(correo);
        if (contacto != null && !contacto.getRuta_img().isEmpty()) {
            Glide.with(context).load(contacto.getRuta_img())
                    .error(R.drawable.profile_white).into(holder.fotoPerfil);
        } else {
            holder.fotoPerfil.setImageResource(R.drawable.profile_white);
        }

        // Mostrar rol
        if (correo.equals(creador)) {
            holder.tvRol.setText("Creador");
            holder.tvRol.setVisibility(View.VISIBLE);
        } else if (admins.contains(correo)) {
            holder.tvRol.setText("Admin");
            holder.tvRol.setVisibility(View.VISIBLE);
        } else {
            holder.tvRol.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return miembros.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView fotoPerfil;
        TextView tvNombre, tvCorreo, tvRol;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fotoPerfil = itemView.findViewById(R.id.foto_miembro);
            tvNombre = itemView.findViewById(R.id.nombre_miembro);
            tvCorreo = itemView.findViewById(R.id.correo_miembro);
            tvRol = itemView.findViewById(R.id.rol_miembro);
        }
    }
}
