package cu.alexgi.youchat.adapters;

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
import cu.alexgi.youchat.R;
import cu.alexgi.youchat.Utils;
import cu.alexgi.youchat.YouChatApplication;
import cu.alexgi.youchat.items.ItemContacto;

public class AdaptadorDatosContactoEnviar extends RecyclerView.Adapter<AdaptadorDatosContactoEnviar.ViewHolderDatos> implements View.OnClickListener {

    ArrayList<ItemContacto> listaDatos;
    private View.OnClickListener listener;

    public AdaptadorDatosContactoEnviar(ArrayList<ItemContacto> listaDatos) {
        this.listaDatos = listaDatos;
    }

    @Override
    public ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_contacto_enviar,null, false);
        view.setOnClickListener(this);
        return new ViewHolderDatos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderDatos holder, int position) {
        ItemContacto contacto = listaDatos.get(position);
        holder.AsignarDatos(contacto.getNombreMostrar(), contacto.getCorreo(), contacto.getRuta_img());
    }

    @Override
    public int getItemCount() {
        return listaDatos.size();
    }

    public void setOnClickListener(View.OnClickListener l)
    {
        listener=l;
    }

    @Override
    public void onClick(View v) {
        if(listener!=null)
        {
            listener.onClick(v);
        }
    }

    public class ViewHolderDatos extends RecyclerView.ViewHolder {

        //lo q esta en el xml item_1
        TextView contacto_correo;
        EmojiTextView contacto_nombre;
        CircleImageView preview_contacto_image;

        View preview_chat_item;

        public ViewHolderDatos(@NonNull View itemView) {
            super(itemView);
            contacto_nombre = itemView.findViewById(R.id.contacto_nombre);
            contacto_correo = itemView.findViewById(R.id.contacto_correo);
            preview_contacto_image = itemView.findViewById(R.id.preview_contacto_image);
            preview_chat_item = itemView.findViewById(R.id.preview_chat_item);
        }

        public void AsignarDatos(String usu,String cor, String ruta_img) {
            contacto_nombre.setText(usu);
            contacto_correo.setText(cor);
//            preview_contacto_image.setImageResource(R.drawable.profile_white);
            cargarImg(ruta_img,cor);
        }
        private synchronized void cargarImg(String ruta_img,String cor) {
            if(cor.equals(YouChatApplication.idOficial)){
                YouChatApplication.ponerIconOficial(preview_contacto_image);
            }
            else {
                String cache = Utils.cargarImgCache(ruta_img);
                if(cache.equals("")) preview_contacto_image.setImageResource(R.drawable.profile_white);
                else Glide.with(preview_contacto_image.getContext()).load(cache).into(preview_contacto_image);
            }

        }
    }
}
