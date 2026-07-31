package cu.alexgi.youchat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.vanniktech.emoji.EmojiTextView;

import java.util.ArrayList;

import cu.alexgi.youchat.Convertidor;
import cu.alexgi.youchat.R;
import cu.alexgi.youchat.items.ItemReaccionEstado;

import static cu.alexgi.youchat.MainActivity.dbWorker;

public class AdaptadorDatosReaccionEstado extends RecyclerView.Adapter<AdaptadorDatosReaccionEstado.ViewHolderDatos>{

    ArrayList<ItemReaccionEstado> listaDatos;

    public AdaptadorDatosReaccionEstado(Context context, ArrayList<ItemReaccionEstado> listaDatos) {
        this.listaDatos = listaDatos;
    }

    @Override
    public ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_info_reaccion_estado,null, false);
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

        private LottieAnimationView tipo_reac;
        private EmojiTextView nombre_usuario_reacciono;

        public ViewHolderDatos(@NonNull View itemView) {
            super(itemView);
            tipo_reac = itemView.findViewById(R.id.tipo_reac);
            nombre_usuario_reacciono = itemView.findViewById(R.id.nombre_usuario_reacciono);
        }

        public void AsignarDatos(ItemReaccionEstado reaccionEstado){

            switch (reaccionEstado.getTipoReaccion()){
                case 1:
                    tipo_reac.setAnimation(R.raw.like1);
                    break;
                case 2:
                    tipo_reac.setAnimation(R.raw.encanta);
                    break;
                case 3:
                    tipo_reac.setAnimation(R.raw.sonroja);
                    break;
                case 4:
                    tipo_reac.setAnimation(R.raw.divierte);
                    break;
                case 5:
                    tipo_reac.setAnimation(R.raw.asombra);
                    break;
                case 6:
                    tipo_reac.setAnimation(R.raw.entristece);
                    break;
                case 7:
                    tipo_reac.setAnimation(R.raw.enoja);
                    break;
                    default:
                        tipo_reac.setAnimation(R.raw.like1);
            }

            String nombre = dbWorker.obtenerNombre(reaccionEstado.getCorreo());
            nombre_usuario_reacciono.setText(nombre+" reaccionó a tu estado el " + Convertidor.convertirFechaAFechaLinda(reaccionEstado.getFecha())+", "+reaccionEstado.getHora());
        }
    }
}
