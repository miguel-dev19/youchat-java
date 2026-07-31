package cu.alexgi.youchat.adapters;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;

import java.io.File;
import java.util.ArrayList;

import cu.alexgi.youchat.R;
import cu.alexgi.youchat.YouChatApplication;
import cu.alexgi.youchat.items.ItemTemas;

import static cu.alexgi.youchat.MainActivity.context;

public class AdaptadorDatosEstilosTemasCompartir extends RecyclerView.Adapter<AdaptadorDatosEstilosTemasCompartir.ViewHolderDatos>
        implements View.OnClickListener {

    ArrayList<ItemTemas> listaDatos;
    private View.OnClickListener listener;

    public AdaptadorDatosEstilosTemasCompartir(ArrayList<ItemTemas> listaDatos) {
        this.listaDatos = listaDatos;
    }

    @Override
    public ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_pick_theme,null, false);
        view.setOnClickListener(this);
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

    public void eliminarTema(String id) {
        int pos = obtenerID(id);
        if(pos!=-1){
            listaDatos.remove(pos);
            notifyItemRemoved(pos);
        }
    }
    public int obtenerID(String id){
        int l = listaDatos.size();
        for(int i=0; i<l; i++){
            if(listaDatos.get(i).getId().equals(id))
                return i;
        }
        return -1;
    }

    public class ViewHolderDatos extends RecyclerView.ViewHolder {

        private MaterialCardView theme_card, theme_bar;
        private AppCompatImageView theme_msg_izq, theme_msg_der, theme_radio;
        private View theme_view, theme_view_add;
//        private CircleImageView temaCircle;
//        private View contenedor_layout_pick_theme,selected;
        private TextView theme_name;
        private ImageView img_fondo_style_theme;

        public ViewHolderDatos(@NonNull View itemView) {
            super(itemView);
//            temaCircle = itemView.findViewById(R.id.temaCircle);
//            selected = itemView.findViewById(R.id.selected);
//            contenedor_layout_pick_theme = itemView.findViewById(R.id.contenedor_layout_pick_theme);
            theme_card = itemView.findViewById(R.id.theme_card);
            theme_bar = itemView.findViewById(R.id.theme_bar);
            theme_msg_izq = itemView.findViewById(R.id.theme_msg_izq);
            theme_msg_der = itemView.findViewById(R.id.theme_msg_der);
            theme_radio = itemView.findViewById(R.id.theme_radio);

            theme_view = itemView.findViewById(R.id.theme_view);
            theme_view_add = itemView.findViewById(R.id.theme_view_add);

            theme_name = itemView.findViewById(R.id.theme_name);
            img_fondo_style_theme = itemView.findViewById(R.id.img_fondo_style_theme);
        }

        @SuppressLint("RestrictedApi")
        public void AsignarDatos(ItemTemas item){
            theme_view.setVisibility(View.VISIBLE);
            theme_view_add.setVisibility(View.GONE);

            theme_name.setText(item.getNombre());
            theme_card.setCardBackgroundColor(Color.parseColor(item.getColor_fondo()));
            theme_bar.setCardBackgroundColor(Color.parseColor(item.getColor_barra()));
            theme_msg_izq.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor(item.getColor_msg_izq())));
            theme_msg_der.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor(item.getColor_msg_der())));

            if(!item.getRutaImg().isEmpty()){
                if(new File(YouChatApplication.RUTA_FONDO_YOUCHAT+item.getRutaImg()).exists()){
                    img_fondo_style_theme.setVisibility(View.VISIBLE);
                    Glide.with(context)
                            .load(YouChatApplication.RUTA_FONDO_YOUCHAT+item.getRutaImg())
                            .error(0)
                            .into(img_fondo_style_theme);
                }
                else img_fondo_style_theme.setVisibility(View.GONE);
            }
            else img_fondo_style_theme.setVisibility(View.GONE);

            theme_radio.setImageResource(R.drawable.radio_button_unchecked);

            if(!item.getColor_accento().equals(""))
                theme_radio.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor(item.getColor_accento())));
        }
    }
}
