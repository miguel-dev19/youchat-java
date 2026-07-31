package cu.alexgi.youchat.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.vanniktech.emoji.EmojiTextView;

import java.io.File;
import java.util.ArrayList;

import cu.alexgi.youchat.AdminEstadosActivity;
import cu.alexgi.youchat.CircleImageView;
import cu.alexgi.youchat.Convertidor;
import cu.alexgi.youchat.R;
import cu.alexgi.youchat.YouChatApplication;
import cu.alexgi.youchat.items.ItemEstado;

public class AdaptadorDatosMiEstado extends RecyclerView.Adapter<AdaptadorDatosMiEstado.ViewHolderDatos> implements View.OnClickListener {

    ArrayList<ItemEstado> listaDatos;
    private View.OnClickListener listener;
    private Context context;
    private AdminEstadosActivity adminEstadosActivity;

    public AdaptadorDatosMiEstado(Context c, ArrayList<ItemEstado> listaDatos, AdminEstadosActivity aea) {
        this.listaDatos = listaDatos;
        context = c;
        adminEstadosActivity = aea;
    }

    @Override
    public ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_mi_estado,null, false);
        view.setOnClickListener(this);
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

        private View contenedor_layout_mi_estado, info_reac_mis_estados, info_vistas_mis_estados;
        private CircleImageView preview_mi_estado_image;
        private EmojiTextView tv_contenido_mi_estado;
        private TextView tv_fecha_subida, tv_cant_total_reacciones;
        private LottieAnimationView may_reac_1, may_reac_2, may_reac_3;

        public ViewHolderDatos(@NonNull View itemView) {
            super(itemView);
            contenedor_layout_mi_estado = itemView.findViewById(R.id.contenedor_layout_mi_estado);
            preview_mi_estado_image = itemView.findViewById(R.id.preview_mi_estado_image);
            tv_contenido_mi_estado = itemView.findViewById(R.id.tv_contenido_mi_estado);

            tv_fecha_subida = itemView.findViewById(R.id.tv_fecha_subida);
            tv_cant_total_reacciones = itemView.findViewById(R.id.tv_cant_total_reacciones);
            may_reac_1 = itemView.findViewById(R.id.may_reac_1);
            may_reac_2 = itemView.findViewById(R.id.may_reac_2);
            may_reac_3 = itemView.findViewById(R.id.may_reac_3);
            info_reac_mis_estados = itemView.findViewById(R.id.info_reac_mis_estados);
            info_vistas_mis_estados = itemView.findViewById(R.id.info_vistas_mis_estados);
        }

        public void AsignarDatos(ItemEstado estado){

            contenedor_layout_mi_estado.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    adminEstadosActivity.abrirPopupXid(v,estado.getId());
                    return true;
                }
            });

            info_reac_mis_estados.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adminEstadosActivity.abrirInfoReacciones(estado);
                }
            });

            info_vistas_mis_estados.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adminEstadosActivity.abrirInfoVistas(estado);
                }
            });

            may_reac_1.setVisibility(View.GONE);
            may_reac_2.setVisibility(View.GONE);
            may_reac_3.setVisibility(View.GONE);
            int[] reacPopup = estado.obtenerTresReaccionesPopulares();
            if(reacPopup[0]!=0){
                may_reac_1.setVisibility(View.VISIBLE);
                switch (reacPopup[0]){
                    case 1:
                        may_reac_1.setAnimation(R.raw.like1);
                        break;
                    case 2:
                        may_reac_1.setAnimation(R.raw.encanta);
                        break;
                    case 3:
                        may_reac_1.setAnimation(R.raw.sonroja);
                        break;
                    case 4:
                        may_reac_1.setAnimation(R.raw.divierte);
                        break;
                    case 5:
                        may_reac_1.setAnimation(R.raw.asombra);
                        break;
                    case 6:
                        may_reac_1.setAnimation(R.raw.entristece);
                        break;
                    case 7:
                        may_reac_1.setAnimation(R.raw.enoja);
                        break;
                }
            }
            if(reacPopup[1]!=0){
                may_reac_2.setVisibility(View.VISIBLE);

                switch (reacPopup[1]){
                    case 1:
                        may_reac_2.setAnimation(R.raw.like1);
                        break;
                    case 2:
                        may_reac_2.setAnimation(R.raw.encanta);
                        break;
                    case 3:
                        may_reac_2.setAnimation(R.raw.sonroja);
                        break;
                    case 4:
                        may_reac_2.setAnimation(R.raw.divierte);
                        break;
                    case 5:
                        may_reac_2.setAnimation(R.raw.asombra);
                        break;
                    case 6:
                        may_reac_2.setAnimation(R.raw.entristece);
                        break;
                    case 7:
                        may_reac_2.setAnimation(R.raw.enoja);
                        break;
                }
            }
            if(reacPopup[2]!=0){
                may_reac_3.setVisibility(View.VISIBLE);

                switch (reacPopup[2]){
                    case 1:
                        may_reac_3.setAnimation(R.raw.like1);
                        break;
                    case 2:
                        may_reac_3.setAnimation(R.raw.encanta);
                        break;
                    case 3:
                        may_reac_3.setAnimation(R.raw.sonroja);
                        break;
                    case 4:
                        may_reac_3.setAnimation(R.raw.divierte);
                        break;
                    case 5:
                        may_reac_3.setAnimation(R.raw.asombra);
                        break;
                    case 6:
                        may_reac_3.setAnimation(R.raw.entristece);
                        break;
                    case 7:
                        may_reac_3.setAnimation(R.raw.enoja);
                        break;
                }
            }
            if(estado.totalReacciones()==0) tv_cant_total_reacciones.setText("0 reacciones");
            else tv_cant_total_reacciones.setText(""+estado.totalReacciones());

            tv_fecha_subida.setText("Subido el "
                    + Convertidor.convertirFechaAFechaLinda(estado.getFecha())
                    +", "+estado.getHora());

            preview_mi_estado_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setEnabled(false);
//                    adminEstadosActivity.abrirVisorOneEstado(YouChatApplication.correo, estado.getId());
                    adminEstadosActivity.abrirVisorOneEstado(estado);
                    v.setEnabled(true);
                }
            });

            switch (estado.getEstilo_texto()){
                case 0: tv_contenido_mi_estado.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Roboto Medium.ttf"));
                    break;
                case 1: tv_contenido_mi_estado.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Burnstown Dam.otf"));
                    break;
                case 2: tv_contenido_mi_estado.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/comicz.ttf"));
                    break;
                case 3: tv_contenido_mi_estado.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Inkfree.ttf"));
                    break;
                case 4: tv_contenido_mi_estado.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/mw_bold.ttf"));
                    break;
                case 5: tv_contenido_mi_estado.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Norican Regular.ttf"));
                    break;
                case 6: tv_contenido_mi_estado.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Oswald Heavy.ttf"));
                    break;
                case 7: tv_contenido_mi_estado.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Thunder Pants.otf"));
                    break;
                default: tv_contenido_mi_estado.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Roboto Medium.ttf"));
            }

            int tipo_estado = estado.getTipo_estado();
            if(tipo_estado==99){
                tv_contenido_mi_estado.setVisibility(View.GONE);
                tv_contenido_mi_estado.setText("");
                String ruta_img = estado.getRuta_imagen();
                File file = new File(ruta_img);
                if(file.exists()){
                    Glide.with(context)
                            .load(ruta_img)
                            .error(R.drawable.image_placeholder)
                            .into(preview_mi_estado_image);
                }
                else {
                    preview_mi_estado_image.setImageResource(0);
                    preview_mi_estado_image.setImageResource(R.drawable.image_placeholder);
                }
            }
            else {
                tv_contenido_mi_estado.setVisibility(View.VISIBLE);
                tv_contenido_mi_estado.setText(estado.getTexto());
                preview_mi_estado_image.setImageResource(0);
                if(tipo_estado<30){
                    preview_mi_estado_image.setBackgroundResource(0);
                    int colorTarjeta= ContextCompat.getColor(context, R.color.card5);
                    switch (tipo_estado)
                    {
                        case 0: colorTarjeta=ContextCompat.getColor(context, R.color.card1);
                            break;
                        case 1: colorTarjeta=ContextCompat.getColor(context, R.color.card2);
                            break;
                        case 2: colorTarjeta=ContextCompat.getColor(context, R.color.card3);
                            break;
                        case 3: colorTarjeta=ContextCompat.getColor(context, R.color.card4);
                            break;
                        case 4: colorTarjeta=ContextCompat.getColor(context, R.color.card5);
                            break;
                        case 5: colorTarjeta=ContextCompat.getColor(context, R.color.card6);
                            break;
                        case 6: colorTarjeta=ContextCompat.getColor(context, R.color.card7);
                            break;
                        case 7: colorTarjeta=ContextCompat.getColor(context, R.color.card8);
                            break;
                        case 8: colorTarjeta=ContextCompat.getColor(context, R.color.card9);
                            break;
                        case 9: colorTarjeta=ContextCompat.getColor(context, R.color.card10);
                            break;
                        case 10: colorTarjeta=ContextCompat.getColor(context, R.color.card11);
                            break;
                        case 11: colorTarjeta=ContextCompat.getColor(context, R.color.card12);
                            break;
                        case 12: colorTarjeta=ContextCompat.getColor(context, R.color.card13);
                            break;
                        case 13: colorTarjeta=ContextCompat.getColor(context, R.color.card14);
                            break;
                        case 14: colorTarjeta=ContextCompat.getColor(context, R.color.card15);
                            break;
                        case 15: colorTarjeta=ContextCompat.getColor(context, R.color.card16);
                            break;
                    }
                    preview_mi_estado_image.setCircleBackgroundColor(colorTarjeta);
                }
                else {
                    preview_mi_estado_image.setCircleBackgroundColor(0);
                    switch (tipo_estado){
                        case 30:
                            preview_mi_estado_image
                                    .setBackgroundResource(R.drawable.shape_fondo_estado_texto_circular_1);
                            break;
                        case 31:
                            preview_mi_estado_image
                                    .setBackgroundResource(R.drawable.shape_fondo_estado_texto_circular_2);
                            break;
                        case 32:
                            preview_mi_estado_image
                                    .setBackgroundResource(R.drawable.shape_fondo_estado_texto_circular_3);
                            break;
                        case 33:
                            preview_mi_estado_image
                                    .setBackgroundResource(R.drawable.shape_fondo_estado_texto_circular_4);
                            break;
                        case 34:
                            preview_mi_estado_image
                                    .setBackgroundResource(R.drawable.shape_fondo_estado_texto_circular_5);
                            break;
                        case 35:
                            preview_mi_estado_image
                                    .setBackgroundResource(R.drawable.shape_fondo_estado_texto_circular_6);
                            break;
                        case 36:
                            preview_mi_estado_image
                                    .setBackgroundResource(R.drawable.shape_fondo_estado_texto_circular_7);
                            break;
                        case 37:
                            preview_mi_estado_image
                                    .setBackgroundResource(R.drawable.shape_fondo_estado_texto_circular_8);
                            break;
                        case 38:
                            preview_mi_estado_image
                                    .setBackgroundResource(R.drawable.shape_fondo_estado_texto_circular_9);
                            break;
                        case 39:
                            preview_mi_estado_image
                                    .setBackgroundResource(R.drawable.shape_fondo_estado_texto_circular_10);
                            break;
                        default:
                            preview_mi_estado_image
                                    .setBackgroundResource(R.drawable.shape_fondo_estado_texto_circular_1);
                    }
                }

            }
        }
    }
}
