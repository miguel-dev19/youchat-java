package cu.alexgi.youchat.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vanniktech.emoji.EmojiTextView;

import java.util.ArrayList;

import cu.alexgi.youchat.BloqueadosActivity;
import cu.alexgi.youchat.CircleImageView;
import cu.alexgi.youchat.R;
import cu.alexgi.youchat.YouChatApplication;
import cu.alexgi.youchat.items.ItemContacto;

public class AdaptadorDatosBloqueado extends RecyclerView.Adapter<AdaptadorDatosBloqueado.ViewHolderDatos>{

    private ArrayList<ItemContacto> listaDatos;
    private BloqueadosActivity bloqueadosActivity;

    public AdaptadorDatosBloqueado(ArrayList<ItemContacto> listaDatos, BloqueadosActivity ba) {
        this.listaDatos = listaDatos;
        bloqueadosActivity = ba;
    }

    @Override
    public ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_bloqueado,null, false);
        return new ViewHolderDatos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderDatos holder, int position) {
        holder.AsignarDatos(listaDatos.get(position));
    }

    @Override
    public int getItemCount() {
        return listaDatos.size();
    }

    public class ViewHolderDatos extends RecyclerView.ViewHolder {

        private CircleImageView preview_bloqueado_image;
        private EmojiTextView bloqueado_nombre;
        private TextView bloqueado_correo;
        private View btn_desbloquear;
        private RelativeLayout Layout;

        public ViewHolderDatos(@NonNull View itemView) {
            super(itemView);
            preview_bloqueado_image=itemView.findViewById(R.id.preview_bloqueado_image);
            bloqueado_nombre=itemView.findViewById(R.id.bloqueado_nombre);
            bloqueado_correo=itemView.findViewById(R.id.bloqueado_correo);
            btn_desbloquear=itemView.findViewById(R.id.btn_desbloquear);
            Layout=itemView.findViewById(R.id.Layout);
        }

        public void AsignarDatos(final ItemContacto contacto){
            Layout.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_fondo()));

            if(contacto.getCorreo().equals(YouChatApplication.idOficial)){
                YouChatApplication.ponerIconOficial(preview_bloqueado_image);
            }
            else {
                Glide.with(preview_bloqueado_image.getContext())
                        .load(contacto.getRuta_img())
                        .error(R.drawable.profile_white)
                        .into(preview_bloqueado_image);
            }
            bloqueado_nombre.setText(contacto.getNombreMostrar());
            bloqueado_correo.setText(contacto.getCorreo());
            btn_desbloquear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setEnabled(false);
                    bloqueadosActivity.mostrarDialogoConfirmacion(contacto.getNombreMostrar(), contacto.getCorreo());
                    v.setEnabled(true);
                }
            });
        }
    }
}
