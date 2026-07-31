package cu.alexgi.youchat.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import cu.alexgi.youchat.R;
import cu.alexgi.youchat.items.ItemAtajo;

public class AdaptadorDatosAtajos
        extends RecyclerView.Adapter<AdaptadorDatosAtajos.ViewHolderDatos> {

    ArrayList<ItemAtajo> listaDatos;
    private boolean puedeBorrar;

    public AdaptadorDatosAtajos(ArrayList<ItemAtajo> listaDatos, boolean pb) {
        this.listaDatos = listaDatos;
        puedeBorrar = pb;
    }

    @Override
    public ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_atajo,null, false);
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

        private View root, iv_eliminar_atajo;
        private TextView tv_atajo_com, tv_atajo_des;

        public ViewHolderDatos(@NonNull View itemView) {
            super(itemView);
            root = itemView;
            tv_atajo_com = itemView.findViewById(R.id.tv_atajo_com);
            tv_atajo_des = itemView.findViewById(R.id.tv_atajo_des);
            iv_eliminar_atajo = itemView.findViewById(R.id.iv_eliminar_atajo);
        }

        public void AsignarDatos(ItemAtajo atajo){
            tv_atajo_com.setText(atajo.getComando());
            tv_atajo_des.setText(atajo.getDescripcion());

            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemAtajoClickListener!=null)
                        onItemAtajoClickListener.OnItemClick(atajo);
                }
            });
            if(puedeBorrar){
                iv_eliminar_atajo.setVisibility(View.VISIBLE);
                iv_eliminar_atajo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(onItemAtajoClickListener!=null)
                            onItemAtajoClickListener.OnItemDelete(atajo,getAbsoluteAdapterPosition());
                    }
                });
            }
        }
    }

    private OnItemAtajoClickListener onItemAtajoClickListener;

    public void setOnItemAtajoClickListener(OnItemAtajoClickListener onItemAtajoClickListener) {
        this.onItemAtajoClickListener = onItemAtajoClickListener;
    }

    public interface OnItemAtajoClickListener{
        void OnItemClick(ItemAtajo atajo);
        void OnItemDelete(ItemAtajo atajo, int pos);
    }
}
