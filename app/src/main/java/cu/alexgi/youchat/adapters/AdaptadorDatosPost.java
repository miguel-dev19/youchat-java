package cu.alexgi.youchat.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.EmojiTextView;
import com.vanniktech.emoji.listeners.OnEmojiPopupDismissListener;
import com.vanniktech.emoji.listeners.OnEmojiPopupShownListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cu.alexgi.youchat.Convertidor;
import cu.alexgi.youchat.PageTransformer;
import cu.alexgi.youchat.R;
import cu.alexgi.youchat.Utils;
import cu.alexgi.youchat.YouChatApplication;
import cu.alexgi.youchat.items.ItemComentarioPost;
import cu.alexgi.youchat.items.ItemDesImgPost;
import cu.alexgi.youchat.items.ItemDetallesTarjeta;
import cu.alexgi.youchat.items.ItemPost;
import cu.alexgi.youchat.items.ItemTemas;
import cu.alexgi.youchat.progressbar.DownloadProgressView;
import cu.alexgi.youchat.views_GI.TextViewFontGenGI;
import cu.alexgi.youchat.views_GI.TextViewFontGenOscuroGI;
import cu.alexgi.youchat.views_GI.TextViewFontResGI;
import cu.alexgi.youchat.views_GI.TextViewPostGI;
import cu.alexgi.youchat.zoominimageview.ZoomInImageViewAttacher;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

import static cu.alexgi.youchat.MainActivity.dbWorker;
import static cu.alexgi.youchat.MainActivity.mainActivity;

public class AdaptadorDatosPost extends RecyclerView.Adapter<AdaptadorDatosPost.ViewHolderDatos>{

    private ArrayList<ItemPost> listaDatos;
    private Context context;

    private OnItemLongClick onItemLongClick;
    public void setOnItemLongClick(OnItemLongClick onItemLongClick){
        this.onItemLongClick = onItemLongClick;
    }
    public interface OnItemLongClick{
        void onLongClick(ItemPost post);
        void abrirVisorImagen(String ruta);
        void descargarImgPost(ItemDesImgPost itemDesImgPost);
        void scrollUp();
        void abrirPerfilDe(String nombre, String correo);
        void abrirBottomSheetComentarios(ArrayList<ItemComentarioPost> list_comentario);
        void responderPost(ItemPost post);
        void compartirPost(ItemPost post);
        void comentarPost(ItemPost post, String cad);
    }

    public AdaptadorDatosPost(Context c, ArrayList<ItemPost> listaDatos) {
        this.listaDatos = listaDatos;
        context = c;
    }

    private void ponerIconoPost(int icono, ImageView icon) {
        switch (icono){
            case 1:
                Glide.with(context).load(R.drawable.noti1).dontAnimate().into(icon);
                break;
            case 2:
                Glide.with(context).load(R.drawable.noti2).dontAnimate().into(icon);
                break;
            case 3:
                Glide.with(context).load(R.drawable.noti3).dontAnimate().into(icon);
                break;
            case 4:
                Glide.with(context).load(R.drawable.noti4).dontAnimate().into(icon);
                break;
            case 5:
                Glide.with(context).load(R.drawable.noti5).dontAnimate().into(icon);
                break;
            case 6:
                Glide.with(context).load(R.drawable.noti6).dontAnimate().into(icon);
                break;
            case 7:
                Glide.with(context).load(R.drawable.noti7).dontAnimate().into(icon);
                break;
            case 8:
                Glide.with(context).load(R.drawable.noti8).dontAnimate().into(icon);
                break;
            case 9:
                Glide.with(context).load(R.drawable.noti9).dontAnimate().into(icon);
                break;
            case 10:
                Glide.with(context).load(R.drawable.noti10).dontAnimate().into(icon);
                break;
            case 11:
                Glide.with(context).load(R.drawable.noti11).dontAnimate().into(icon);
                break;
            case 12:
                Glide.with(context).load(R.drawable.noti12).dontAnimate().into(icon);
                break;
            case 13:
                Glide.with(context).load(R.drawable.noti13).dontAnimate().into(icon);
                break;
            case 14:
                Glide.with(context).load(R.drawable.noti14).dontAnimate().into(icon);
                break;
            case 15:
                Glide.with(context).load(R.drawable.noti15).dontAnimate().into(icon);
                break;
            case 99:
                Glide.with(context).load(R.drawable.noti16).dontAnimate().into(icon);
                break;
            default: Glide.with(context).load(R.drawable.noti1).dontAnimate().into(icon);
        }
    }

    @Override
    public ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType==-1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_post_footer,null, false);
            return new ViewHolderDatosPostDivisor(view);
        }
        else if(viewType==-2) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_post_footer,null, false);
            return new ViewHolderDatosPostDivisorNuevos(view);
        }
        else if(viewType==1){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_post,null, false);
            return new ViewHolderDatosPost(view);
        }
        else if(viewType==3){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_post_tema,null, false);
            return new ViewHolderDatosPostTema(view);
        }
        else if(viewType==4){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_post_tarjeta,null, false);
            return new ViewHolderDatosPostTarjeta(view);
        }
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_post_imagen,null, false);
            return new ViewHolderDatosPostImagen(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderDatos holder, int position) {
        int l = listaDatos.size()-1;
        switch (holder.getItemViewType()) {
            case -1: //divider
                ViewHolderDatosPostDivisor vhdpd = (ViewHolderDatosPostDivisor) holder;
                vhdpd.AsignarDatos();
                break;
            case -2: //divider nuevos
                ViewHolderDatosPostDivisorNuevos vhdpdn = (ViewHolderDatosPostDivisorNuevos) holder;
                vhdpdn.AsignarDatos(listaDatos.get(position).getIcono());
                break;
            case 1: //post normal
                ViewHolderDatosPost vhdp = (ViewHolderDatosPost) holder;
                vhdp.AsignarDatos(listaDatos.get(position),l==position);
                break;
            case 2: //post imagen
                ViewHolderDatosPostImagen vhdpi = (ViewHolderDatosPostImagen) holder;
                vhdpi.AsignarDatos(listaDatos.get(position),l==position);
                break;
            case 3: //post tema
                ViewHolderDatosPostTema vhdpt = (ViewHolderDatosPostTema) holder;
                vhdpt.AsignarDatos(listaDatos.get(position),l==position);
                break;
            case 4: //post tarjeta
                ViewHolderDatosPostTarjeta vhdpta = (ViewHolderDatosPostTarjeta) holder;
                vhdpta.AsignarDatos(listaDatos.get(position),l==position);
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
                    case 1://cancelarDescarga
                        if(holder instanceof ViewHolderDatosPostImagen){
                            ((ViewHolderDatosPostImagen) holder).cancelarDescarga();
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
//        int tipo = listaDatos.get(position).getTipo_usuario();
//        if(tipo==-1)
//            return 69;
//        else if(tipo==-2)
//            return 70;
        return listaDatos.get(position).getTipo_post();
    }

    private float maxAnchoImagen = (float) YouChatApplication.anchoPantalla*0.6f;
    private float maxLargoImagen = (float) YouChatApplication.largoPantalla*0.4f;

    public class ViewHolderDatos extends RecyclerView.ViewHolder {
        public ViewHolderDatos(@NonNull View itemView) {
            super(itemView);
        }
        public void AsignarDatos() {}
    }

    public class ViewHolderDatosPostDivisor extends ViewHolderDatos {
        private RelativeLayout fondo_post_footer;
        private View contenedor_post_footer;
        private AppCompatImageView img_post_footer;

        public ViewHolderDatosPostDivisor(@NonNull View itemView) {
            super(itemView);
            fondo_post_footer = itemView.findViewById(R.id.fondo_post_footer);
            contenedor_post_footer = itemView.findViewById(R.id.contenedor_post_footer);
            img_post_footer = itemView.findViewById(R.id.img_post_footer);
        }
        @SuppressLint("RestrictedApi")
        public void AsignarDatos() {
            img_post_footer.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getColor_texto())));
            contenedor_post_footer.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_interior()));
            if(onItemLongClick!=null){
                fondo_post_footer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(onItemLongClick!=null)
                            onItemLongClick.scrollUp();
                    }
                });
            }
        }
    }

    public class ViewHolderDatosPostDivisorNuevos extends ViewHolderDatos {
        private RelativeLayout fondo_post_footer;
        private View contenedor_post_footer;
        private AppCompatImageView img_post_footer;
        private TextViewFontGenGI tv_footer_post;

        public ViewHolderDatosPostDivisorNuevos(@NonNull View itemView) {
            super(itemView);
            fondo_post_footer = itemView.findViewById(R.id.fondo_post_footer);
            contenedor_post_footer = itemView.findViewById(R.id.contenedor_post_footer);
            img_post_footer = itemView.findViewById(R.id.img_post_footer);
            tv_footer_post = itemView.findViewById(R.id.tv_footer_post);
        }
        @SuppressLint("RestrictedApi")
        public void AsignarDatos(int cant) {
            if(cant==1) tv_footer_post.setText("1 nuevo Post arriba");
            else tv_footer_post.setText(cant+" nuevos Post arriba");
            img_post_footer.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getColor_texto())));
            contenedor_post_footer.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_interior()));
        }
    }

    public class ViewHolderDatosPost extends ViewHolderDatosLayoutPost {

        private ImageView foto_noti, user_type;
        private TextViewFontGenGI correo_noti;
        private TextViewPostGI mensaje_noti;
        private TextViewFontGenOscuroGI hora_noti;
        private TextViewFontResGI nombre_noti;
        private MaterialCardView fondo_mensaje_izq;
        private RelativeLayout fondo_msg_post;

        public ViewHolderDatosPost(@NonNull View itemView) {
            super(itemView);
            foto_noti = itemView.findViewById(R.id.foto_noti);
            nombre_noti = itemView.findViewById(R.id.nombre_noti);
            correo_noti = itemView.findViewById(R.id.correo_noti);
            mensaje_noti = itemView.findViewById(R.id.mensaje_noti);
            hora_noti = itemView.findViewById(R.id.hora_noti);
            fondo_mensaje_izq = itemView.findViewById(R.id.fondo_mensaje_izq);
            user_type = itemView.findViewById(R.id.user_type);
            fondo_msg_post = itemView.findViewById(R.id.fondo_msg_post);
        }

        public void AsignarDatos(ItemPost post,boolean esElUltimo){
            super.AsignarDatos(post);
            RelativeLayout.MarginLayoutParams a =
                    new RelativeLayout
                            .MarginLayoutParams
                            (RelativeLayout.MarginLayoutParams.MATCH_PARENT,
                                    RelativeLayout.MarginLayoutParams.WRAP_CONTENT);

            if(esElUltimo){
                a.setMargins(0,0,0,Utils.dpToPx(context,75));
                fondo_msg_post.setLayoutParams(a);
            }
            else {
                a.setMargins(0,0,0,0);
                fondo_msg_post.setLayoutParams(a);
            }

            fondo_mensaje_izq.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Utils.vibrate(context, 50);
                    if (onItemLongClick != null)
                        onItemLongClick.onLongClick(post);
                    return true;
                }
            });

            mensaje_noti.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Utils.vibrate(context, 50);
                    if (onItemLongClick != null)
                        onItemLongClick.onLongClick(post);
                    return true;
                }
            });

            if(post.getTipo_usuario()==5 || post.getTipo_usuario()==4){
                user_type.setImageResource(R.drawable.verified_profile);
                user_type.setVisibility(View.VISIBLE);
            }
            else if(post.getTipo_usuario()==3){
                user_type.setImageResource(R.drawable.vip_crown_line);
                user_type.setVisibility(View.VISIBLE);
            }
            else if(post.getTipo_usuario()==2){
                user_type.setImageResource(R.drawable.vip_diamond_line);
                user_type.setVisibility(View.VISIBLE);
            }
            else if(post.getTipo_usuario()==1){
                user_type.setImageResource(R.drawable.award_line);
                user_type.setVisibility(View.VISIBLE);
            }
            else user_type.setVisibility(View.GONE);

            ponerIconoPost(post.getIcono(),foto_noti);

            nombre_noti.setText(post.getNombre());
            correo_noti.setText(post.getCorreo());
            mensaje_noti.setText(post.getTexto());
            hora_noti.setText(Convertidor.convertirFechaAFechaLinda(post.getFecha())+", "+post.getHora());
        }

    }

    public class ViewHolderDatosPostImagen extends ViewHolderDatosLayoutPost {

        private View view_descarga;
        private DownloadProgressView progress_img;
        private TextView tv_tam_img, tv_tam_img_file;
        private ImageView icon, user_type;
        private TextViewFontGenGI correo;
        private TextViewPostGI mensaje;
        private TextViewFontGenOscuroGI hora;
        private TextViewFontResGI nombre;
        private MaterialCardView fondo;
        private RelativeLayout fondo_msg_post;
        private ShapeableImageView image;

        public ViewHolderDatosPostImagen(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            nombre = itemView.findViewById(R.id.nombre);
            correo = itemView.findViewById(R.id.correo);
            mensaje = itemView.findViewById(R.id.mensaje);
            hora = itemView.findViewById(R.id.hora);
            fondo = itemView.findViewById(R.id.fondo);
            user_type = itemView.findViewById(R.id.user_type);
            fondo_msg_post = itemView.findViewById(R.id.fondo_msg_post);
            image = itemView.findViewById(R.id.image);

            view_descarga = itemView.findViewById(R.id.view_descarga);
            progress_img = itemView.findViewById(R.id.progress_img);
            tv_tam_img = itemView.findViewById(R.id.tv_tam_img);
            tv_tam_img_file = itemView.findViewById(R.id.tv_tam_img_file);
        }

        public synchronized void AsignarDatos(ItemPost post,boolean esElUltimo){
            super.AsignarDatos(post);
            RelativeLayout.MarginLayoutParams a =
                    new RelativeLayout
                            .MarginLayoutParams
                            (RelativeLayout.MarginLayoutParams.MATCH_PARENT,
                                    RelativeLayout.MarginLayoutParams.WRAP_CONTENT);

            if(esElUltimo){
                a.setMargins(0,0,0,Utils.dpToPx(context,75));
                fondo_msg_post.setLayoutParams(a);
            }
            else {
                a.setMargins(0,0,0,0);
                fondo_msg_post.setLayoutParams(a);
            }

            fondo.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Utils.vibrate(context, 50);
                    if (onItemLongClick != null)
                        onItemLongClick.onLongClick(post);
                    return true;
                }
            });

            mensaje.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Utils.vibrate(context, 50);
                    if (onItemLongClick != null)
                        onItemLongClick.onLongClick(post);
                    return true;
                }
            });

            image.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Utils.vibrate(context, 50);
                    if (onItemLongClick != null)
                        onItemLongClick.onLongClick(post);
                    return true;
                }
            });

            if(post.getTipo_usuario()==5 || post.getTipo_usuario()==4){
                user_type.setImageResource(R.drawable.verified_profile);
                user_type.setVisibility(View.VISIBLE);
            }
            else if(post.getTipo_usuario()==3){
                user_type.setImageResource(R.drawable.vip_crown_line);
                user_type.setVisibility(View.VISIBLE);
            }
            else if(post.getTipo_usuario()==2){
                user_type.setImageResource(R.drawable.vip_diamond_line);
                user_type.setVisibility(View.VISIBLE);
            }
            else if(post.getTipo_usuario()==1){
                user_type.setImageResource(R.drawable.award_line);
                user_type.setVisibility(View.VISIBLE);
            }
            else user_type.setVisibility(View.GONE);

            ponerIconoPost(post.getIcono(),icon);

            nombre.setText(post.getNombre());
            correo.setText(post.getCorreo());
            mensaje.setText(post.getTexto());
            hora.setText(Convertidor.convertirFechaAFechaLinda(post.getFecha())+", "+post.getHora());

            //CARGAR IMAGEN
            File imgFile = new File(post.getRuta_dato());
            if(imgFile.exists()){
                view_descarga.setVisibility(View.GONE);
                tv_tam_img_file.setVisibility(View.VISIBLE);
                tv_tam_img_file.setText(Utils.convertirBytes(imgFile.length()));
//                Glide.with(context).load(post.getRuta_dato()).error(R.drawable.placeholder).into(image);
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemLongClick != null)
                            onItemLongClick.abrirVisorImagen(post.getRuta_dato());
                    }
                });

                int[] al = Utils.obtenerAnchoLargo(post.getRuta_dato());
                Glide.with(context)
                        .load(post.getRuta_dato())
                        .override(al[0],al[1])
                        .error(R.drawable.placeholder).into(image);
                new ZoomInImageViewAttacher(image,true);
            }
            else {
                view_descarga.setVisibility(View.VISIBLE);
                tv_tam_img_file.setVisibility(View.GONE);
                tv_tam_img.setText(Utils.convertirBytes(post.getPeso_dato()));
                image.setOnClickListener(null);
//                image.setImageResource(R.drawable.placeholder);
                int[] al = new int[]{(int) maxAnchoImagen,(int) maxAnchoImagen};
                Glide.with(context)
                        .load(R.drawable.image_placeholder).dontAnimate().override(al[0],al[1])
                        .error(R.drawable.image_placeholder).into(image);
                new ZoomInImageViewAttacher(image,false,true);
            }

            //descarga
            progress_img.setDownloading(false);
            progress_img.ponerClick();
            progress_img.setProgressListener(new Function1<Boolean, Unit>() {
                @Override
                public Unit invoke(Boolean descargar) {
                    if(YouChatApplication.estaAndandoChatService()
                            && YouChatApplication.chatService.hayConex){
                        progress_img.quitarClick();
                        progress_img.setDownloading(true);
                        progress_img.setProgress(0f);
                        if (onItemLongClick != null)
                            onItemLongClick.descargarImgPost(new ItemDesImgPost(post, progress_img));
                    }
                    else {
                        progress_img.setDownloading(false);
                        progress_img.setProgress(0f);
                        Utils.mostrarToastDeConexion(mainActivity);
                    }
                    return null;
                }
            });
        }

        public void cancelarDescarga() {
            if(progress_img!=null){
                progress_img.setDownloading(false);
                progress_img.ponerClick();
            }
        }
    }

    public class ViewHolderDatosPostTema extends ViewHolderDatosLayoutPost {

        private ImageView icon, user_type;
        private TextViewFontGenGI correo;
        private TextViewPostGI mensaje;
        private TextViewFontGenOscuroGI hora;
        private TextViewFontResGI nombre;
        private MaterialCardView fondo;
        private RelativeLayout fondo_msg_post;

        //disenno
        private TextViewFontGenGI tema_nombre, tema_creador;
        private MaterialCardView theme_card, theme_bar, fondo_btn;
        private AppCompatImageView theme_msg_izq, theme_msg_der, theme_radio;
        private TextView theme_btn;
        private ImageView img_fondo_style_theme;

        public ViewHolderDatosPostTema(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            nombre = itemView.findViewById(R.id.nombre);
            correo = itemView.findViewById(R.id.correo);
            mensaje = itemView.findViewById(R.id.mensaje);
            hora = itemView.findViewById(R.id.hora);
            fondo = itemView.findViewById(R.id.fondo);
            user_type = itemView.findViewById(R.id.user_type);
            fondo_msg_post = itemView.findViewById(R.id.fondo_msg_post);

            tema_nombre=itemView.findViewById(R.id.tema_nombre);
            tema_creador=itemView.findViewById(R.id.tema_creador);
            theme_card=itemView.findViewById(R.id.theme_card);
            theme_bar=itemView.findViewById(R.id.theme_bar);
            theme_msg_izq=itemView.findViewById(R.id.theme_msg_izq);
            theme_msg_der=itemView.findViewById(R.id.theme_msg_der);
            theme_radio=itemView.findViewById(R.id.theme_radio);
            theme_btn=itemView.findViewById(R.id.theme_btn);
            fondo_btn=itemView.findViewById(R.id.fondo_btn);
            img_fondo_style_theme = itemView.findViewById(R.id.img_fondo_style_theme);
        }

        @SuppressLint("RestrictedApi")
        public synchronized void AsignarDatos(ItemPost post, boolean esElUltimo){
            super.AsignarDatos(post);
            RelativeLayout.MarginLayoutParams a =
                    new RelativeLayout
                            .MarginLayoutParams
                            (RelativeLayout.MarginLayoutParams.MATCH_PARENT,
                                    RelativeLayout.MarginLayoutParams.WRAP_CONTENT);

            if(esElUltimo){
                a.setMargins(0,0,0,Utils.dpToPx(context,75));
                fondo_msg_post.setLayoutParams(a);
            }
            else {
                a.setMargins(0,0,0,0);
                fondo_msg_post.setLayoutParams(a);
            }

            fondo.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Utils.vibrate(context, 50);
                    if (onItemLongClick != null)
                        onItemLongClick.onLongClick(post);
                    return true;
                }
            });

            mensaje.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Utils.vibrate(context, 50);
                    if (onItemLongClick != null)
                        onItemLongClick.onLongClick(post);
                    return true;
                }
            });

            if(post.getTipo_usuario()==5 || post.getTipo_usuario()==4){
                user_type.setImageResource(R.drawable.verified_profile);
                user_type.setVisibility(View.VISIBLE);
            }
            else if(post.getTipo_usuario()==3){
                user_type.setImageResource(R.drawable.vip_crown_line);
                user_type.setVisibility(View.VISIBLE);
            }
            else if(post.getTipo_usuario()==2){
                user_type.setImageResource(R.drawable.vip_diamond_line);
                user_type.setVisibility(View.VISIBLE);
            }
            else if(post.getTipo_usuario()==1){
                user_type.setImageResource(R.drawable.award_line);
                user_type.setVisibility(View.VISIBLE);
            }
            else user_type.setVisibility(View.GONE);

            ponerIconoPost(post.getIcono(),icon);

            nombre.setText(post.getNombre());
            correo.setText(post.getCorreo());
            mensaje.setText(post.getTexto());
            hora.setText(Convertidor.convertirFechaAFechaLinda(post.getFecha())+", "+post.getHora());

            //disenno
            ItemTemas tema = Convertidor.createItemTemasOfMensaje(post.getRuta_dato());
            if(tema!=null){
                SpannableString s;
            s = new SpannableString( "Nombre:\n"+tema.getNombre());
            s.setSpan(new RelativeSizeSpan(0.7f), 0, 7, 0);
            tema_nombre.setText(s);

            String nombre = dbWorker.obtenerNombre(tema.getCreador());

            s = new SpannableString("Creado por:\n"+nombre);
            s.setSpan(new RelativeSizeSpan(0.7f), 0, 11, 0);
            s.setSpan(new RelativeSizeSpan(0.9f), 11, s.length(), 0);
            tema_creador.setText(s);

            img_fondo_style_theme.setVisibility(View.GONE);

            theme_btn.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getColor_msg_izq_audio()));
            fondo_btn.setStrokeColor(Color.parseColor(YouChatApplication.itemTemas.getColor_msg_izq_audio()));
            fondo_btn.setRadius(YouChatApplication.curvaChat<=32?YouChatApplication.curvaChat:32);

            if(dbWorker.existeTemaId(tema.getId())){
                fondo_btn.setVisibility(View.INVISIBLE);
                fondo_btn.setOnClickListener(null);
            }
            else {
                fondo_btn.setVisibility(View.VISIBLE);
                fondo_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dbWorker.insertarNuevoTema(tema);
                        fondo_btn.setVisibility(View.INVISIBLE);
                        fondo_btn.setOnClickListener(null);
                    }
                });
            }
            theme_card.setCardBackgroundColor(Color.parseColor(tema.getColor_fondo()));
            theme_bar.setCardBackgroundColor(Color.parseColor(tema.getColor_barra()));
            theme_msg_izq.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor(tema.getColor_msg_izq())));
            theme_msg_der.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor(tema.getColor_msg_der())));
            theme_radio.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor(tema.getColor_accento())));
            }
             else {
                tema_creador.setText("Error, tema no compatible para esta versión");
                fondo_btn.setVisibility(View.INVISIBLE);
            }
//            tema_nombre.setText(tema.getNombre());
//            tema_creador.setText(tema.getCreador());
        }
    }

    public class ViewHolderDatosPostTarjeta extends ViewHolderDatosLayoutPost {

        private ImageView icon, user_type;
        private TextViewFontGenGI correo;
        private TextViewFontGenOscuroGI hora;
        private TextViewFontResGI nombre;
        private MaterialCardView fondo;
        private RelativeLayout fondo_msg_post;

        private View background_tarjeta;
        private EmojiTextView tv_tarjeta;
        private LottieAnimationView lottie_arriba, lottie_abajo, lottie_izquierda, lottie_derecha;

        public ViewHolderDatosPostTarjeta(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            nombre = itemView.findViewById(R.id.nombre);
            correo = itemView.findViewById(R.id.correo);
            hora = itemView.findViewById(R.id.hora);
            fondo = itemView.findViewById(R.id.fondo);
            user_type = itemView.findViewById(R.id.user_type);
            fondo_msg_post = itemView.findViewById(R.id.fondo_msg_post);

            background_tarjeta = itemView.findViewById(R.id.background_tarjeta);
            tv_tarjeta = itemView.findViewById(R.id.tv_tarjeta);
            lottie_arriba = itemView.findViewById(R.id.lottie_arriba);
            lottie_abajo = itemView.findViewById(R.id.lottie_abajo);
            lottie_izquierda = itemView.findViewById(R.id.lottie_izquierda);
            lottie_derecha = itemView.findViewById(R.id.lottie_derecha);
        }

        public synchronized void AsignarDatos(ItemPost post,boolean esElUltimo){
            super.AsignarDatos(post);
            RelativeLayout.MarginLayoutParams a =
                    new RelativeLayout
                            .MarginLayoutParams
                            (RelativeLayout.MarginLayoutParams.MATCH_PARENT,
                                    RelativeLayout.MarginLayoutParams.WRAP_CONTENT);

            if(esElUltimo){
                a.setMargins(0,0,0,Utils.dpToPx(context,75));
                fondo_msg_post.setLayoutParams(a);
            }
            else {
                a.setMargins(0,0,0,0);
                fondo_msg_post.setLayoutParams(a);
            }

            fondo.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Utils.vibrate(context, 50);
                    if (onItemLongClick != null)
                        onItemLongClick.onLongClick(post);
                    return true;
                }
            });

            if(post.getTipo_usuario()==5 || post.getTipo_usuario()==4){
                user_type.setImageResource(R.drawable.verified_profile);
                user_type.setVisibility(View.VISIBLE);
            }
            else if(post.getTipo_usuario()==3){
                user_type.setImageResource(R.drawable.vip_crown_line);
                user_type.setVisibility(View.VISIBLE);
            }
            else if(post.getTipo_usuario()==2){
                user_type.setImageResource(R.drawable.vip_diamond_line);
                user_type.setVisibility(View.VISIBLE);
            }
            else if(post.getTipo_usuario()==1){
                user_type.setImageResource(R.drawable.award_line);
                user_type.setVisibility(View.VISIBLE);
            }
            else user_type.setVisibility(View.GONE);

            ponerIconoPost(post.getIcono(),icon);

            nombre.setText(post.getNombre());
            correo.setText(post.getCorreo());
            hora.setText(Convertidor.convertirFechaAFechaLinda(post.getFecha())+", "+post.getHora());

            //sticker
            ItemDetallesTarjeta detallesTarjeta = Convertidor.createItemDetallesTarjetaOfMensaje(post.getRuta_dato());
            lottie_arriba.setVisibility(View.GONE);
            lottie_abajo.setVisibility(View.GONE);
            lottie_izquierda.setVisibility(View.GONE);
            lottie_derecha.setVisibility(View.GONE);

            tv_tarjeta.setText(post.getTexto());

            lottie_arriba.setOnClickListener(null);
            lottie_abajo.setOnClickListener(null);
            lottie_izquierda.setOnClickListener(null);
            lottie_derecha.setOnClickListener(null);

            ponerColorFondo(detallesTarjeta.getColorFondo());

            switch (detallesTarjeta.getPosLottie()){
                case 0:
                    lottie_arriba.setAnimation(Utils.obtenerRawActualDePos(detallesTarjeta.getTipoLottie()));
                    lottie_arriba.setVisibility(View.VISIBLE);
                    lottie_arriba.playAnimation();
                    lottie_arriba.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            lottie_arriba.playAnimation();
                        }
                    });
                    break;
                case 1:
                    lottie_abajo.setAnimation(Utils.obtenerRawActualDePos(detallesTarjeta.getTipoLottie()));
                    lottie_abajo.setVisibility(View.VISIBLE);
                    lottie_abajo.playAnimation();
                    lottie_abajo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            lottie_abajo.playAnimation();
                        }
                    });
                    break;
                case 2:
                    lottie_izquierda.setAnimation(Utils.obtenerRawActualDePos(detallesTarjeta.getTipoLottie()));
                    lottie_izquierda.setVisibility(View.VISIBLE);
                    lottie_izquierda.playAnimation();
                    lottie_izquierda.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            lottie_izquierda.playAnimation();
                        }
                    });
                    break;
                case 3:
                    lottie_derecha.setAnimation(Utils.obtenerRawActualDePos(detallesTarjeta.getTipoLottie()));
                    lottie_derecha.setVisibility(View.VISIBLE);
                    lottie_derecha.playAnimation();
                    lottie_derecha.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            lottie_derecha.playAnimation();
                        }
                    });
                    break;
            }
        }

        public synchronized void ponerColorFondo(int tipo_estado) {
            if (tipo_estado < 30) {
                background_tarjeta.setBackgroundResource(0);
                int colorTarjeta = ContextCompat.getColor(context, R.color.card5);
                switch (tipo_estado) {
                    case 0:
                        colorTarjeta = ContextCompat.getColor(context, R.color.card1);
                        break;
                    case 1:
                        colorTarjeta = ContextCompat.getColor(context, R.color.card2);
                        break;
                    case 2:
                        colorTarjeta = ContextCompat.getColor(context, R.color.card3);
                        break;
                    case 3:
                        colorTarjeta = ContextCompat.getColor(context, R.color.card4);
                        break;
                    case 4:
                        colorTarjeta = ContextCompat.getColor(context, R.color.card5);
                        break;
                    case 5:
                        colorTarjeta = ContextCompat.getColor(context, R.color.card6);
                        break;
                    case 6:
                        colorTarjeta = ContextCompat.getColor(context, R.color.card7);
                        break;
                    case 7:
                        colorTarjeta = ContextCompat.getColor(context, R.color.card8);
                        break;
                    case 8:
                        colorTarjeta = ContextCompat.getColor(context, R.color.card9);
                        break;
                    case 9:
                        colorTarjeta = ContextCompat.getColor(context, R.color.card10);
                        break;
                    case 10:
                        colorTarjeta = ContextCompat.getColor(context, R.color.card11);
                        break;
                    case 11:
                        colorTarjeta = ContextCompat.getColor(context, R.color.card12);
                        break;
                    case 12:
                        colorTarjeta = ContextCompat.getColor(context, R.color.card13);
                        break;
                    case 13:
                        colorTarjeta = ContextCompat.getColor(context, R.color.card14);
                        break;
                    case 14:
                        colorTarjeta = ContextCompat.getColor(context, R.color.card15);
                        break;
                    case 15:
                        colorTarjeta = ContextCompat.getColor(context, R.color.card16);
                        break;
                }
                background_tarjeta.setBackgroundColor(colorTarjeta);
            } else {
                background_tarjeta.setBackgroundColor(0);
                switch (tipo_estado) {
                    case 30:
                        background_tarjeta
                                .setBackgroundResource(R.drawable.shape_fondo_estado_texto_1);
                        break;
                    case 31:
                        background_tarjeta
                                .setBackgroundResource(R.drawable.shape_fondo_estado_texto_2);
                        break;
                    case 32:
                        background_tarjeta
                                .setBackgroundResource(R.drawable.shape_fondo_estado_texto_3);
                        break;
                    case 33:
                        background_tarjeta
                                .setBackgroundResource(R.drawable.shape_fondo_estado_texto_4);
                        break;
                    case 34:
                        background_tarjeta
                                .setBackgroundResource(R.drawable.shape_fondo_estado_texto_5);
                        break;
                    case 35:
                        background_tarjeta
                                .setBackgroundResource(R.drawable.shape_fondo_estado_texto_6);
                        break;
                    case 36:
                        background_tarjeta
                                .setBackgroundResource(R.drawable.shape_fondo_estado_texto_7);
                        break;
                    case 37:
                        background_tarjeta
                                .setBackgroundResource(R.drawable.shape_fondo_estado_texto_8);
                        break;
                    case 38:
                        background_tarjeta
                                .setBackgroundResource(R.drawable.shape_fondo_estado_texto_9);
                        break;
                    case 39:
                        background_tarjeta
                                .setBackgroundResource(R.drawable.shape_fondo_estado_texto_10);
                        break;
                    default:
                        background_tarjeta
                                .setBackgroundResource(R.drawable.shape_fondo_estado_texto_1);
                }
            }

        }
    }

    public class ViewHolderDatosLayoutPost extends ViewHolderDatos {

        //pack de comentario
        private View contenedor_comentario_post, btn_cp_responder, btn_cp_comentar,
                btn_cp_compartir, post_typping, contenedor_lista_comentario;
        private TextView tv_cant_comentario_post, tv_more;
        private RecyclerView lista_comentarios;
        private ImageView preview_contacto_mi_image, input_emoji;
        private AdaptadorDatosComentarioPost adaptadorDatosComentarioPost;
        private EmojiPopup emojiPopup;
        private View root, input_estado_send;
        private EmojiEditText input_text;

        public ViewHolderDatosLayoutPost(@NonNull View itemView) {
            super(itemView);
            root = itemView;
            contenedor_comentario_post = itemView.findViewById(R.id.contenedor_comentario_post);
            tv_cant_comentario_post = itemView.findViewById(R.id.tv_cant_comentario_post);
            lista_comentarios = itemView.findViewById(R.id.lista_comentarios);
            tv_more = itemView.findViewById(R.id.tv_more);
            btn_cp_responder = itemView.findViewById(R.id.btn_cp_responder);
            btn_cp_comentar = itemView.findViewById(R.id.btn_cp_comentar);
            btn_cp_compartir = itemView.findViewById(R.id.btn_cp_compartir);
            post_typping = itemView.findViewById(R.id.post_typping);
            preview_contacto_mi_image = itemView.findViewById(R.id.preview_contacto_mi_image);
            contenedor_lista_comentario = itemView.findViewById(R.id.contenedor_lista_comentario);
            input_emoji = itemView.findViewById(R.id.input_emoji);
            input_text = itemView.findViewById(R.id.input_text);
            input_estado_send = itemView.findViewById(R.id.input_estado_send);

            emojiPopup = null;

            GradientDrawable drawable2 = new GradientDrawable();
            drawable2.setColor(Color.parseColor(YouChatApplication.itemTemas.getColor_interior()));
            drawable2.setCornerRadii(new float[]{
                    0, 0, 0, 0, 20, 20, 20, 20
            });
//            drawable2.setCornerRadii(new float[]{
//                    20, 20, 20, 20, 20, 20, 20, 20
//            });
//            root.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_interior()));
            contenedor_lista_comentario.setBackground(drawable2);
        }

        public synchronized void AsignarDatos(ItemPost post){
            //pack de comentario
            Glide.with(context).load(YouChatApplication.ruta_img_perfil)
                    .error(R.drawable.profile_white).into(preview_contacto_mi_image);
            if(post.isInputShow()) post_typping.setVisibility(View.VISIBLE);
            else post_typping.setVisibility(View.GONE);
            btn_cp_responder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemLongClick!=null)
                        onItemLongClick.responderPost(post);
                }
            });
            input_estado_send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(YouChatApplication.puedeSubirPost){
                        YouChatApplication.resetearCantPostSubidosHoy();
                        int x = Utils.calcularCantLimComentarioPostXDia(dbWorker.obtenerCantSeguidores())-YouChatApplication.cantComentarioPostSubidosHoy;
                        if(x>0){
                            String cad = input_text.getText().toString().trim();
                            if(!cad.isEmpty()){
                                if(YouChatApplication.estaAndandoChatService()
                                        && YouChatApplication.chatService.hayConex){
                                    input_text.setText("");
                                    if(onItemLongClick!=null)
                                        onItemLongClick.comentarPost(post,cad);
                                }
                                else Utils.ShowToastAnimated(mainActivity,"Compruebe su conexión",R.raw.ic_ban);
                            }
                            else Utils.ShowToastAnimated(mainActivity,"Debe escribir un comentario",R.raw.ic_ban);
                        }
                        else Utils.ShowToastAnimated(mainActivity,"Límite de comentario a Post diario alcanzado",R.raw.error);
                    }
                    else Utils.ShowToastAnimated(mainActivity,"No puede publicar Post",R.raw.swipe_disabled);
                }
            });
            btn_cp_comentar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(post_typping.getVisibility()==View.VISIBLE){
                        post_typping.setVisibility(View.GONE);
                        post.setInputShow(false);
                        emojiPopup = null;
                        Utils.ocultarKeyBoard(mainActivity);
                    }
                    else{
                        if(YouChatApplication.puedeSubirPost){
                            YouChatApplication.resetearCantPostSubidosHoy();
                            int x = Utils.calcularCantLimComentarioPostXDia(dbWorker.obtenerCantSeguidores())-YouChatApplication.cantComentarioPostSubidosHoy;
                            if(x>0){
                                post_typping.setVisibility(View.VISIBLE);
                                post.setInputShow(true);
                                input_emoji.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        accionEmojiInput();
                                    }
                                });
                            }
                            else Utils.ShowToastAnimated(mainActivity,"Límite de comentario a Post diario alcanzado",R.raw.error);
                        }
                        else Utils.ShowToastAnimated(mainActivity,"No puede publicar Post",R.raw.swipe_disabled);
                    }
                }
            });
            btn_cp_compartir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemLongClick!=null)
                        onItemLongClick.compartirPost(post);
                }
            });
            int cantComentario = post.getComentarioPosts().size();
            if(cantComentario==1) tv_cant_comentario_post.setText("1 comentario");
            else tv_cant_comentario_post.setText(cantComentario + " comentarios");
            if(cantComentario>0){
                contenedor_comentario_post.setVisibility(View.VISIBLE);
                if(post.puedeMostrarMas()){
                    tv_more.setVisibility(View.VISIBLE);
                    if(cantComentario-3==1) tv_more.setText("Ver 1 comentario más");
                    else tv_more.setText("Ver "+(cantComentario-3)+" comentarios más");

                    tv_more.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(onItemLongClick!=null)
                                onItemLongClick.abrirBottomSheetComentarios(post.getComentarioPosts());
                        }
                    });
                } else tv_more.setVisibility(View.GONE);
                adaptadorDatosComentarioPost = new AdaptadorDatosComentarioPost(post.getUlt3ComentarioPosts(),onItemLongClick,post);
                lista_comentarios.setLayoutManager(new LinearLayoutManager(context));
                lista_comentarios.setAdapter(adaptadorDatosComentarioPost);
            }
            else contenedor_comentario_post.setVisibility(View.GONE);
        }

        private void accionEmojiInput() {
            if(emojiPopup==null) setUpEmojiPopup();
            emojiPopup.toggle();
        }
        private synchronized void setUpEmojiPopup() {
            emojiPopup = EmojiPopup.Builder.fromRootView(root)
                    .setOnEmojiPopupShownListener(new OnEmojiPopupShownListener() {
                        @Override
                        public void onEmojiPopupShown() {
                            input_emoji.setImageResource(R.drawable.input_keyboard);
                        }
                    })
                    .setOnEmojiPopupDismissListener(new OnEmojiPopupDismissListener() {
                        @Override
                        public void onEmojiPopupDismiss() {
                            input_emoji.setImageResource(R.drawable.emoji);
                        }
                    })
                    .setKeyboardAnimationStyle(R.style.emoji_fade_animation_style)
                    .setPageTransformer(new PageTransformer())
                    .build(input_text,false);
        }
    }
}
