package cu.alexgi.youchat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import cu.alexgi.youchat.CircleImageView;
import cu.alexgi.youchat.R;
import cu.alexgi.youchat.items.ItemContacto;

public class AdaptadorDatosContactoSeleccionable extends RecyclerView.Adapter<AdaptadorDatosContactoSeleccionable.ViewHolder> {
    private Context context;
    private ArrayList<ItemContacto> contactos;
    private ArrayList<String> seleccionados;
    private OnSeleccionChangeListener listener;

    public interface OnSeleccionChangeListener { void onSeleccionChanged(); }
    public void setOnSeleccionChangeListener(OnSeleccionChangeListener listener) { this.listener = listener; }

    public AdaptadorDatosContactoSeleccionable(Context context, ArrayList<ItemContacto> contactos, ArrayList<String> seleccionados) {
        this.context = context; this.contactos = contactos; this.seleccionados = seleccionados;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_contacto_seleccionable, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemContacto c = contactos.get(position);
        holder.tvNombre.setText(c.getNombreMostrar());
        holder.tvCorreo.setText(c.getCorreo());
        if (!c.getRuta_img().isEmpty()) Glide.with(context).load(c.getRuta_img()).error(R.drawable.profile_white).into(holder.fotoPerfil);
        else holder.fotoPerfil.setImageResource(R.drawable.profile_white);
        holder.cbSeleccionar.setOnCheckedChangeListener(null);
        holder.cbSeleccionar.setChecked(seleccionados.contains(c.getCorreo()));
        holder.cbSeleccionar.setOnCheckedChangeListener((b, chk) -> {
            if (chk) { if (!seleccionados.contains(c.getCorreo())) seleccionados.add(c.getCorreo()); }
            else seleccionados.remove(c.getCorreo());
            if (listener != null) listener.onSeleccionChanged();
        });
        holder.itemView.setOnClickListener(v -> holder.cbSeleccionar.setChecked(!holder.cbSeleccionar.isChecked()));
    }

    @Override public int getItemCount() { return contactos.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView fotoPerfil; TextView tvNombre, tvCorreo; CheckBox cbSeleccionar;
        public ViewHolder(@NonNull View v) {
            super(v);
            fotoPerfil = v.findViewById(R.id.foto_contacto);
            tvNombre = v.findViewById(R.id.nombre_contacto);
            tvCorreo = v.findViewById(R.id.correo_contacto);
            cbSeleccionar = v.findViewById(R.id.cb_seleccionar);
        }
    }
}
