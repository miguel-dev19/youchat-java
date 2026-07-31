package cu.alexgi.youchat.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import cu.alexgi.youchat.BottomSheetDialogFragment.BottomSheetDialogFragment_fondo_post_tarjeta;
import cu.alexgi.youchat.R;

public class AdaptadorDatosFondoPostTarjeta extends RecyclerView.Adapter<AdaptadorDatosFondoPostTarjeta.ViewHolderDatos> {

    ArrayList<Integer> listaDatos;
    private BottomSheetDialogFragment_fondo_post_tarjeta bsdf_fondo_post_tarjeta;

    public AdaptadorDatosFondoPostTarjeta(ArrayList<Integer> listaDatos, BottomSheetDialogFragment_fondo_post_tarjeta net) {
        this.listaDatos = listaDatos;
        bsdf_fondo_post_tarjeta = net;
    }

    @Override
    public ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_fondo_estado_texto,null, false);
        return new ViewHolderDatos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderDatos holder, int position) {
        holder.AsignarDatos(listaDatos.get(position));
        setAnimation(holder.itemView);
    }

    @Override
    public int getItemCount() {
        return listaDatos.size();
    }

    private void setAnimation(View v){
        Animation anim= AnimationUtils.loadAnimation(v.getContext(),R.anim.fade_in);
        v.startAnimation(anim);
    }


    public class ViewHolderDatos extends RecyclerView.ViewHolder {

        private View lfondo_estado_background;

        public ViewHolderDatos(@NonNull View itemView) {
            super(itemView);
            lfondo_estado_background = itemView.findViewById(R.id.lfondo_estado_background);
        }

        public void AsignarDatos(int fondo){
            switch (fondo){
                case 0:
                    lfondo_estado_background
                            .setBackgroundResource(R.drawable.shape_fondo_estado_texto_1);
                    break;
                case 1:
                    lfondo_estado_background
                            .setBackgroundResource(R.drawable.shape_fondo_estado_texto_2);
                    break;
                case 2:
                    lfondo_estado_background
                            .setBackgroundResource(R.drawable.shape_fondo_estado_texto_3);
                    break;
                case 3:
                    lfondo_estado_background
                            .setBackgroundResource(R.drawable.shape_fondo_estado_texto_4);
                    break;
                case 4:
                    lfondo_estado_background
                            .setBackgroundResource(R.drawable.shape_fondo_estado_texto_5);
                    break;
                case 5:
                    lfondo_estado_background
                            .setBackgroundResource(R.drawable.shape_fondo_estado_texto_6);
                    break;
                case 6:
                    lfondo_estado_background
                            .setBackgroundResource(R.drawable.shape_fondo_estado_texto_7);
                    break;
                case 7:
                    lfondo_estado_background
                            .setBackgroundResource(R.drawable.shape_fondo_estado_texto_8);
                    break;
                case 8:
                    lfondo_estado_background
                            .setBackgroundResource(R.drawable.shape_fondo_estado_texto_9);
                    break;
                case 9:
                    lfondo_estado_background
                            .setBackgroundResource(R.drawable.shape_fondo_estado_texto_10);
                    break;
                    default:
                        lfondo_estado_background
                                .setBackgroundResource(R.drawable.shape_fondo_estado_texto_1);
            }

            lfondo_estado_background.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bsdf_fondo_post_tarjeta.ponerFondo(fondo+30);
                }
            });
        }
    }
}
