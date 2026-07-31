package cu.alexgi.youchat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import cu.alexgi.youchat.R;
import cu.alexgi.youchat.Utils;
import cu.alexgi.youchat.ViewImageActivity;
import cu.alexgi.youchat.items.ItemImg;

public class AdaptadorDatosImgEditorCarrucel extends RecyclerView.Adapter<AdaptadorDatosImgEditorCarrucel.ViewHolderDatos> implements View.OnClickListener {

    ArrayList<ItemImg> listaDatos;
    private View.OnClickListener listener;
    private ViewImageActivity viewImageActivity;
    private Context context;

    private int size50, size100;

    public AdaptadorDatosImgEditorCarrucel(Context c, ArrayList<ItemImg> listaDatos, ViewImageActivity v) {
        this.listaDatos = listaDatos;
        viewImageActivity = v;
        context = c;
        size50 = Utils.dpToPx(context,50);
        size100 = Utils.dpToPx(context,100);
    }

    @Override
    public ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_img_carrucel_editor,null, false);
        view.setOnClickListener(this);
        return new ViewHolderDatos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderDatos holder, int position) {
        holder.AsignarDatos(listaDatos.get(position).getRuta());

        setAnimation(holder.itemView);
    }

    private int buscarID(String ruta){
        int l = listaDatos.size();
        for(int i=0; i<l; i++){
            if(listaDatos.get(i).getRuta().equals(ruta)){
                return i;
            }
        }
        return -1;
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

    private void setAnimation(View v){
        Animation anim= AnimationUtils.loadAnimation(v.getContext(),R.anim.fade_in_fast);
        v.startAnimation(anim);
    }

    public class ViewHolderDatos extends RecyclerView.ViewHolder {

        ImageView imgSend;

        public ViewHolderDatos(@NonNull View itemView) {
            super(itemView);
            imgSend = itemView.findViewById(R.id.imgSend);
        }

        public void AsignarDatos(String ruta){
//            int pos = buscarID(ruta);
//            if(pos!=-1){
//                if(pos == viewImageActivity.getPosActual()){
//                    Glide.with(context).load(ruta).override(size100)
//                            .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
//                            .error(R.drawable.image_placeholder).into(imgSend);
//                } else {
//                    Glide.with(context).load(ruta).override(size50)
//                            .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
//                            .error(R.drawable.image_placeholder).into(imgSend);
//                }
//            } else {
//                Glide.with(context).load(ruta).override(size50)
//                        .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
//                        .error(R.drawable.image_placeholder).into(imgSend);
//            }
            Glide.with(context).load(ruta)
                    .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
                    .error(R.drawable.image_placeholder).into(imgSend);
            imgSend.setOnClickListener(v -> viewImageActivity.elegirNuevaImagen(buscarID(ruta)));
        }
    }
}
