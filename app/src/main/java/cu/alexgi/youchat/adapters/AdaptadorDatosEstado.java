package cu.alexgi.youchat.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.vanniktech.emoji.EmojiTextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cu.alexgi.youchat.CircleImageView;
import cu.alexgi.youchat.CircularStatusView;
import cu.alexgi.youchat.EstadosFragment;
import cu.alexgi.youchat.PrincipalActivity;
import cu.alexgi.youchat.R;
import cu.alexgi.youchat.Utils;
import cu.alexgi.youchat.YouChatApplication;
import cu.alexgi.youchat.items.ItemEstado;

import static cu.alexgi.youchat.MainActivity.dbWorker;

public class AdaptadorDatosEstado extends RecyclerView.Adapter<AdaptadorDatosEstado.ViewHolderDatos> {

    ArrayList<ItemEstado> listaDatos;
    private Context context;
    private PrincipalActivity principalActivity;
    private EstadosFragment estadosFragment;
    private String fechaEntera;
    private int seenColor = Color.parseColor("#ff868c90");
    private int notSeenColor = Color.parseColor(YouChatApplication.itemTemas.getColor_btn());

    public AdaptadorDatosEstado(Context c, ArrayList<ItemEstado> listaDatos, PrincipalActivity pa) {
        this.listaDatos = listaDatos;
        context = c;
        principalActivity = pa;
        estadosFragment = pa.estadosFragment;

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date = new Date();
        fechaEntera = sdf.format(date);
        fechaEntera = fechaEntera.replace(" ", "");
    }

    @Override
    public ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType==66) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_estado_divider,null, false);
            return new ViewHolderDatosDivisor(view);
        }
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_estado,null, false);
            return new ViewHolderDatosEstados(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderDatos holder, int position) {
        //holder.AsignarDatos(listaDatos.get(position));

        ItemEstado estado = listaDatos.get(position);
        switch (holder.getItemViewType()) {
            case 66: //divider
                ViewHolderDatosDivisor vhdd = (ViewHolderDatosDivisor) holder;
                String cad="";
                if(estado.getTipo_estado()==111) cad="Recientes";
                else if(estado.getTipo_estado()==222) cad="Vistos";
                vhdd.AsignarDatos(cad);
                break;
            case 1: //estado normal
                ViewHolderDatosEstados vhdc = (ViewHolderDatosEstados) holder;
                vhdc.AsignarDatos(estado);
                break;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderDatos holder, int position, @NonNull List<Object> payloads) {
        if(payloads.size()>0){
            Object o = payloads.get(0);
            if(o instanceof Integer){
                int num = (int) o;
                switch (num){
                    case 300:
                        if(listaDatos.size()>0){
                            if(holder instanceof ViewHolderDatosEstados){
                                ((ViewHolderDatosEstados)holder)
                                        .cargaEnvNow(estadosFragment.getProgressSubida(), estadosFragment.getProgressTotal());
                            }
                        }
                        break;
                    default:
                        super.onBindViewHolder(holder, position, payloads);
                }
            }
            else super.onBindViewHolder(holder, position, payloads);
        }
        else super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public int getItemCount() {
        return listaDatos.size();
    }

    @Override
    public int getItemViewType(int position) {
        int tipo = listaDatos.get(position).getTipo_estado();
        if(tipo==111 || tipo==222)
            return 66;
        else return 1;
    }

    public class ViewHolderDatos extends RecyclerView.ViewHolder {
        public ViewHolderDatos(@NonNull View itemView) {
            super(itemView);
        }
        public void AsignarDatos() {}
    }

    public class ViewHolderDatosDivisor extends ViewHolderDatos {
        TextView tipo_estado_divider;
        View root;
        public ViewHolderDatosDivisor(@NonNull View itemView) {
            super(itemView);
            tipo_estado_divider = itemView.findViewById(R.id.tipo_estado_divider);
            root = itemView.findViewById(R.id.root);
        }
        public void AsignarDatos(String infoText) {
            root.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_interior()));
            tipo_estado_divider.setText(infoText);
        }
    }
    ///////////////////////////////////////////////////////////

    public class ViewHolderDatosEstados extends ViewHolderDatos {

        private View contenedor_layout_estado;
        private CircleImageView preview_estado_image;
        private ImageView img_download;
        private EmojiTextView tv_usuarios_estado;
        //private TextView estados_cont;
        private EmojiTextView tv_contenido_estado;
        private TextView hora_estado;
        private CircularStatusView circular_status_view;
        private View img_admin_estado,div;
        private CircularProgressIndicator progressNowSubida;

        public ViewHolderDatosEstados(@NonNull View itemView) {
            super(itemView);
            contenedor_layout_estado = itemView.findViewById(R.id.contenedor_layout_estado);
            preview_estado_image = itemView.findViewById(R.id.preview_estado_image);
            tv_usuarios_estado = itemView.findViewById(R.id.tv_usuarios_estado);
            //estados_cont = itemView.findViewById(R.id.estados_cont);
            tv_contenido_estado = itemView.findViewById(R.id.tv_contenido_estado);
            hora_estado = itemView.findViewById(R.id.hora_estado);
            circular_status_view = itemView.findViewById(R.id.circular_status_view);
            img_admin_estado = itemView.findViewById(R.id.img_admin_estado);
            div = itemView.findViewById(R.id.div);
            img_download = itemView.findViewById(R.id.img_download);
            progressNowSubida = itemView.findViewById(R.id.progressNowSubida);
        }


        public void AsignarDatos(ItemEstado estado){

            progressNowSubida.setVisibility(View.GONE);
            boolean soyYo = estado.getCorreo().equals(YouChatApplication.correo);

            div.setVisibility(View.VISIBLE);
            circular_status_view.setPortionsCount(0);
            circular_status_view.setPortionsColor(seenColor);
            img_download.setVisibility(View.GONE);

            contenedor_layout_estado.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_fondo()));
            contenedor_layout_estado.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setEnabled(false);
                    /*if(!estaVisto) estadosFragment.abrirVisorEstadosNuevosDe(estado.getCorreo());
                    else estadosFragment.abrirVisorEstadosDe(estado.getCorreo());*/
                    estadosFragment.abrirVisorEstadosDe(estado.getCorreo(), !estado.isEsta_visto());
                    v.setEnabled(true);
                }
            });

            contenedor_layout_estado.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(!soyYo){
                        Utils.vibrate(context, 50);
                        estadosFragment.showDialog(estado);
                    }
                    return true;
                }
            });

            String fechaEstado = estado.getFecha();
            if(fechaEntera.equals(fechaEstado)) hora_estado.setText(estado.getHora());
            else hora_estado.setText(estado.getHora());

            int tipo_estado = estado.getTipo_estado();
            img_admin_estado.setVisibility(View.GONE);

            if(tipo_estado==-1){
                int cantGeneral = estado.getCant_me_encanta();
//                int seenColor = itemView.getContext().getResources().getColor(R.color.gris_perfecto);
                circular_status_view.setPortionsCount(cantGeneral);
                circular_status_view.setPortionsColor(seenColor);

                tv_usuarios_estado.setText("Mis Now");
                hora_estado.setText("Añada un nuevo Now");
                div.setVisibility(View.GONE);
                img_admin_estado.setVisibility(View.VISIBLE);
                img_admin_estado.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.setEnabled(false);
                        estadosFragment.abrirAdminEstados();
                        v.setEnabled(true);
                    }
                });
            }
            else if(soyYo){
                int cantGeneral = estado.getCant_me_encanta();
//                int seenColor = itemView.getContext().getResources().getColor(R.color.gris_perfecto);
                circular_status_view.setPortionsCount(cantGeneral);
                circular_status_view.setPortionsColor(seenColor);

                tv_usuarios_estado.setText("Mis Now");
                hora_estado.setText("Añada un nuevo Now");
                img_admin_estado.setVisibility(View.VISIBLE);
                img_admin_estado.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.setEnabled(false);
                        estadosFragment.abrirAdminEstados();
                        v.setEnabled(true);
                    }
                });
            }
            else {
                int cantNuevos = estado.getCant_me_gusta();
                int cantGeneral = estado.getCant_me_encanta();

//                int seenColor = itemView.getContext().getResources().getColor(R.color.gris_perfecto);

                circular_status_view.setPortionsCount(cantGeneral);
                circular_status_view.setPortionsColor(seenColor);

//                Log.e("CANTIDADES ADAPTADOR","GENERAL: "+cantGeneral+" *** NUEVOS: "+cantNuevos);
                if (cantNuevos==0) circular_status_view.setPortionsColor(seenColor);
                else
                {
                    //int cantVistos = Math.abs(cantGeneral-cantNuevos);
                    for (int i=0 ; i<cantNuevos ; i++) circular_status_view.setPortionColorForIndex(i, notSeenColor);
                }

//                String nom;
//                nom = dbWorker.obtenerNombre(estado.getCorreo());
                tv_usuarios_estado.setText(dbWorker.obtenerNombre(estado.getCorreo()));
            }

            switch (estado.getEstilo_texto()){
                case 0: tv_contenido_estado.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Roboto Medium.ttf"));
                    break;
                case 1: tv_contenido_estado.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Burnstown Dam.otf"));
                    break;
                case 2: tv_contenido_estado.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/comicz.ttf"));
                    break;
                case 3: tv_contenido_estado.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Inkfree.ttf"));
                    break;
                case 4: tv_contenido_estado.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/mw_bold.ttf"));
                    break;
                case 5: tv_contenido_estado.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Norican Regular.ttf"));
                    break;
                case 6: tv_contenido_estado.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Oswald Heavy.ttf"));
                    break;
                case 7: tv_contenido_estado.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Thunder Pants.otf"));
                    break;
                default: tv_contenido_estado.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Roboto Medium.ttf"));
            }

            if(tipo_estado==-1){
                tv_contenido_estado.setVisibility(View.GONE);
                tv_contenido_estado.setText("");
                preview_estado_image.setCircleBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_btn()));
                preview_estado_image.setImageResource(R.drawable.menu_add);
                contenedor_layout_estado.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.setEnabled(false);
                        estadosFragment.nuevoEstadoImg();
                        v.setEnabled(true);
                    }
                });
            }
            else if(tipo_estado==99){
                if(!estado.isDescargado()){
                    tv_contenido_estado.setVisibility(View.GONE);
                    tv_contenido_estado.setText("");
                    preview_estado_image.setImageDrawable(null);
                    preview_estado_image.setCircleBackgroundColor(context.getResources().getColor(R.color.gris_perfecto));
                    img_download.setVisibility(View.VISIBLE);

                }
                else {
                    tv_contenido_estado.setVisibility(View.GONE);
                    tv_contenido_estado.setText("");

                    preview_estado_image.setImageResource(R.drawable.image_placeholder);
                    cargarImg(estado);
                }
            }
            else {
                tv_contenido_estado.setVisibility(View.VISIBLE);
                tv_contenido_estado.setText(estado.getTexto());
                preview_estado_image.setImageResource(0);

                if(tipo_estado<30){
                    preview_estado_image.setBackgroundResource(0);
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
                    preview_estado_image.setCircleBackgroundColor(colorTarjeta);
                }
                else {
                    preview_estado_image.setCircleBackgroundColor(0);
                    switch (tipo_estado){
                        case 30:
                            preview_estado_image
                                    .setBackgroundResource(R.drawable.shape_fondo_estado_texto_circular_1);
                            break;
                        case 31:
                            preview_estado_image
                                    .setBackgroundResource(R.drawable.shape_fondo_estado_texto_circular_2);
                            break;
                        case 32:
                            preview_estado_image
                                    .setBackgroundResource(R.drawable.shape_fondo_estado_texto_circular_3);
                            break;
                        case 33:
                            preview_estado_image
                                    .setBackgroundResource(R.drawable.shape_fondo_estado_texto_circular_4);
                            break;
                        case 34:
                            preview_estado_image
                                    .setBackgroundResource(R.drawable.shape_fondo_estado_texto_circular_5);
                            break;
                        case 35:
                            preview_estado_image
                                    .setBackgroundResource(R.drawable.shape_fondo_estado_texto_circular_6);
                            break;
                        case 36:
                            preview_estado_image
                                    .setBackgroundResource(R.drawable.shape_fondo_estado_texto_circular_7);
                            break;
                        case 37:
                            preview_estado_image
                                    .setBackgroundResource(R.drawable.shape_fondo_estado_texto_circular_8);
                            break;
                        case 38:
                            preview_estado_image
                                    .setBackgroundResource(R.drawable.shape_fondo_estado_texto_circular_9);
                            break;
                        case 39:
                            preview_estado_image
                                    .setBackgroundResource(R.drawable.shape_fondo_estado_texto_circular_10);
                            break;
                        default:
                            preview_estado_image
                                    .setBackgroundResource(R.drawable.shape_fondo_estado_texto_circular_1);
                    }
                }
            }
        }
        private synchronized void cargarImg(ItemEstado estado){
            String ruta_img = estado.getRuta_imagen();
            File file = new File(ruta_img);
            if(file.exists()){
                Glide.with(context)
                        .load(ruta_img)
                        .error(R.drawable.image_placeholder)
                        .into(preview_estado_image);
            }
            else Glide.with(context).load(R.drawable.image_placeholder).into(preview_estado_image);
        }

        public synchronized void cargaEnvNow(int progress, int total) {
            if(total==0 || total<=progress){
                estadosFragment.resetProgressSubida();
                progressNowSubida.setVisibility(View.GONE);
            }
            else{
                progressNowSubida.setVisibility(View.VISIBLE);
                progressNowSubida.setIndicatorColor(Color.parseColor(YouChatApplication.itemTemas.getColor_btn()));
                progressNowSubida.setTrackColor(Color.parseColor(YouChatApplication.itemTemas.getColor_btn_oscuro()));
                progressNowSubida.setMax(total);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    progressNowSubida.setProgress(progress+1,true);
                }
                else progressNowSubida.setProgress(progress+1);
            }
        }
    }
}
