package cu.alexgi.youchat.adapters;

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
import cu.alexgi.youchat.items.ItemFondo;

public class AdaptadorDatosFondo extends RecyclerView.Adapter<AdaptadorDatosFondo.ViewHolderDatos> implements View.OnClickListener {

    ArrayList<ItemFondo> listaDatos;
    private View.OnClickListener listener;

    public AdaptadorDatosFondo(ArrayList<ItemFondo> listaDatos) {
        this.listaDatos = listaDatos;
    }

    @Override
    public ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_grid_fondo,null, false);
        view.setOnClickListener(this);
        return new ViewHolderDatos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderDatos holder, int position) {
        holder.AsignarDatos(listaDatos.get(position).estaSeleccionado, listaDatos.get(position).esInterno,
                listaDatos.get(position).getDrawable(), listaDatos.get(position).getRuta());

        setAnimation(holder.itemView);
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

        ImageView lfondo_background, lfondo_seleccionado;

        public ViewHolderDatos(@NonNull View itemView) {
            super(itemView);
            lfondo_background = itemView.findViewById(R.id.lfondo_background);
            lfondo_seleccionado = itemView.findViewById(R.id.lfondo_seleccionado);
        }

        public void AsignarDatos(boolean isSelect, boolean isIntern, int draw, String ruta){
            if(isIntern){
                switch (draw){
                    case 0:
                        Glide.with(lfondo_background.getContext())
                                .load(R.drawable.background_1).diskCacheStrategy(DiskCacheStrategy.NONE).into(lfondo_background);
                        break;
                    case 1:
                        Glide.with(lfondo_background.getContext())
                                .load(R.drawable.background_2).diskCacheStrategy(DiskCacheStrategy.NONE).into(lfondo_background);
                        break;
                    case 2:
                        Glide.with(lfondo_background.getContext())
                                .load(R.drawable.background_3).diskCacheStrategy(DiskCacheStrategy.NONE).into(lfondo_background);
                        break;
                    case 3:
                        Glide.with(lfondo_background.getContext())
                                .load(R.drawable.background_4).diskCacheStrategy(DiskCacheStrategy.NONE).into(lfondo_background);
                        break;
                    case 4:
                        Glide.with(lfondo_background.getContext())
                                .load(R.drawable.background_5).diskCacheStrategy(DiskCacheStrategy.NONE).into(lfondo_background);
                        break;
                    case 5:
                        Glide.with(lfondo_background.getContext())
                                .load(R.drawable.background_6).diskCacheStrategy(DiskCacheStrategy.NONE).into(lfondo_background);
                        break;
                    case 6:
                        Glide.with(lfondo_background.getContext())
                                .load(R.drawable.background_7).diskCacheStrategy(DiskCacheStrategy.NONE).into(lfondo_background);
                        break;
                    case 7:
                        Glide.with(lfondo_background.getContext())
                                .load(R.drawable.background_8).diskCacheStrategy(DiskCacheStrategy.NONE).into(lfondo_background);
                        break;
                    default:
                        Glide.with(lfondo_background.getContext())
                                .load(R.drawable.background_1).into(lfondo_background);
                }
            }
            else{
                Glide.with(lfondo_background.getContext()).load(ruta)
                        .error(R.drawable.background_1).into(lfondo_background);
            }
//            new ZoomInImageViewAttacher(lfondo_background,true);

            if(isSelect) lfondo_seleccionado.setVisibility(View.VISIBLE);
            else lfondo_seleccionado.setVisibility(View.GONE);
        }
    }
}
