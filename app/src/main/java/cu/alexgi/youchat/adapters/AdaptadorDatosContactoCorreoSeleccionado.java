package cu.alexgi.youchat.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vanniktech.emoji.EmojiTextView;

import java.util.ArrayList;

import cu.alexgi.youchat.CircleImageView;
import cu.alexgi.youchat.R;
import cu.alexgi.youchat.Utils;
import cu.alexgi.youchat.items.ItemContacto;

public class AdaptadorDatosContactoCorreoSeleccionado extends RecyclerView.Adapter<AdaptadorDatosContactoCorreoSeleccionado.ViewHolderDatos>
        implements View.OnClickListener, View.OnLongClickListener {

    ArrayList<ItemContacto> listaDatos;
    private View.OnClickListener listener;
    private View.OnLongClickListener longClickListener;

    private boolean hacerAnim;

    public AdaptadorDatosContactoCorreoSeleccionado(ArrayList<ItemContacto> listaDatos) {
        this.listaDatos = listaDatos;
        hacerAnim = false;
    }

    @Override
    public ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_contacto_horizontal,null, false);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return new ViewHolderDatos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderDatos holder, int position) {
        holder.AsignarDatos(listaDatos.get(position));
        setAnimation(holder.itemView);
    }

    public void hacerAnim(){
        hacerAnim=true;
    }

    private void setAnimation(View v){
        if(hacerAnim){
            hacerAnim=false;
            v.startAnimation(AnimationUtils.loadAnimation(v.getContext(),R.anim.zoom_add_correo));
        }
    }

    @Override
    public int getItemCount() {
        return listaDatos.size();
    }

    public void setOnClickListener(View.OnClickListener l)
    {
        listener=l;
    }

    public void setLongClickListener(View.OnLongClickListener l){
        longClickListener = l;
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
        if(longClickListener!=null){
            longClickListener.onLongClick(v);
            return true;
        }
        return false;
    }

    public class ViewHolderDatos extends RecyclerView.ViewHolder {

        private CircleImageView image;
        private EmojiTextView nombre;

        public ViewHolderDatos(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            nombre = itemView.findViewById(R.id.nombre);
        }

        public synchronized void AsignarDatos(ItemContacto contacto){
            cargarImg(contacto);
            nombre.setText(contacto.getNombreMostrar());
//            tv_seguidor_correo.setText(contacto.getCorreo());
        }

        private synchronized void cargarImg(ItemContacto contacto) {
            String cache = Utils.cargarImgCache(contacto.getRuta_img());
            if(cache.equals("")) image.setImageResource(R.drawable.profile_white);
            else Glide.with(image.getContext()).load(cache).into(image);
        }
    }
}
