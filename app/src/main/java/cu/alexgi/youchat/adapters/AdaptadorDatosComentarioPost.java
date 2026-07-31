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
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.vanniktech.emoji.EmojiTextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cu.alexgi.youchat.CircleImageView;
import cu.alexgi.youchat.Convertidor;
import cu.alexgi.youchat.PostFragment;
import cu.alexgi.youchat.R;
import cu.alexgi.youchat.Utils;
import cu.alexgi.youchat.YouChatApplication;
import cu.alexgi.youchat.items.ItemComentarioPost;
import cu.alexgi.youchat.items.ItemContacto;
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

import static cu.alexgi.youchat.MainActivity.context;
import static cu.alexgi.youchat.MainActivity.dbWorker;
import static cu.alexgi.youchat.MainActivity.mainActivity;

public class AdaptadorDatosComentarioPost extends RecyclerView.Adapter<AdaptadorDatosComentarioPost.ViewHolderDatos>{

    private ArrayList<ItemComentarioPost> listaDatos;
    private AdaptadorDatosPost.OnItemLongClick onItemLongClick;
    private ItemPost postGral;
    float curva =(float) YouChatApplication.curvaGlobosChat;

    public AdaptadorDatosComentarioPost(ArrayList<ItemComentarioPost> listaDatos,
                                        AdaptadorDatosPost.OnItemLongClick oilc, ItemPost p) {
        this.listaDatos = listaDatos;
        onItemLongClick = oilc;
        postGral = p;
    }

    public AdaptadorDatosComentarioPost(ArrayList<ItemComentarioPost> listaDatos) {
        this.listaDatos = listaDatos;
        onItemLongClick = null;
        postGral = null;
    }

    @Override
    public ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
//        if(viewType==1){
//            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_post_imagen,null, false);
//            return new ViewHolderDatosPostImagen(view);
//        }
//        else if(viewType==3){
//            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_post_tema,null, false);
//            return new ViewHolderDatosPostTema(view);
//        }
//        else if(viewType==4){
//            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_post_tarjeta,null, false);
//            return new ViewHolderDatosPostTarjeta(view);
//        }
//        else
            {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_post_comentario,null, false);
            return new ViewHolderDatosPostComentarioTexto(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderDatos holder, int position) {
        int l = listaDatos.size()-1;
        switch (holder.getItemViewType()) {
            case 1: //post normal
                ViewHolderDatosPostComentarioTexto vhdp = (ViewHolderDatosPostComentarioTexto) holder;
                vhdp.AsignarDatos(listaDatos.get(position));
                break;
//            case 2: //post imagen
//                ViewHolderDatosPostImagen vhdpi = (ViewHolderDatosPostImagen) holder;
//                vhdpi.AsignarDatos(listaDatos.get(position),l==position);
//                break;
//            case 3: //post tema
//                ViewHolderDatosPostTema vhdpt = (ViewHolderDatosPostTema) holder;
//                vhdpt.AsignarDatos(listaDatos.get(position),l==position);
//                break;
//            case 4: //post tarjeta
//                ViewHolderDatosPostTarjeta vhdpta = (ViewHolderDatosPostTarjeta) holder;
//                vhdpta.AsignarDatos(listaDatos.get(position),l==position);
//                break;
        }
    }

    @Override
    public int getItemCount() {
        return listaDatos.size();
    }

    @Override
    public int getItemViewType(int position) {
        return listaDatos.get(position).getTipo_comentario_post();
    }

    public class ViewHolderDatos extends RecyclerView.ViewHolder {
        public ViewHolderDatos(@NonNull View itemView) {
            super(itemView);
        }
        public void AsignarDatos() {}
    }

    public class ViewHolderDatosPostComentarioTexto extends ViewHolderDatos {

        private ImageView user_type;
        private TextViewPostGI mensaje_noti;
        private TextViewFontGenOscuroGI hora_noti;
        private TextViewFontResGI nombre_noti;
        private CircleImageView preview_contacto_image;
        private View root, fondo_msg;

        public ViewHolderDatosPostComentarioTexto(@NonNull View itemView) {
            super(itemView);
//            foto_noti = itemView.findViewById(R.id.foto_noti);
            root = itemView;
            nombre_noti = itemView.findViewById(R.id.nombre_noti);
            mensaje_noti = itemView.findViewById(R.id.mensaje_noti);
            hora_noti = itemView.findViewById(R.id.hora_noti);
            user_type = itemView.findViewById(R.id.user_type);
            preview_contacto_image = itemView.findViewById(R.id.preview_contacto_image);
            fondo_msg = itemView.findViewById(R.id.fondo_msg);

//            GradientDrawable drawable2 = new GradientDrawable();
//            drawable2.setColor(Color.parseColor(YouChatApplication.itemTemas.getColor_interior()));
//            drawable2.setCornerRadii(new float[]{
//                    curva, curva, curva, curva, curva, curva, curva, curva
//            });
////            root.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_interior()));
//            root.setBackground(drawable2);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(Color.parseColor(YouChatApplication.itemTemas.getColor_fondo()));
            drawable.setCornerRadii(new float[]{
                    curva, curva, curva, curva, curva, curva, curva, curva
            });
            fondo_msg.setBackground(drawable);
        }

        public void AsignarDatos(ItemComentarioPost post){

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

            nombre_noti.setText(post.getNombre());
            mensaje_noti.setText(post.getTexto());
            hora_noti.setText(Convertidor.convertirFechaAFechaLinda(post.getFecha())+", "+post.getHora());

            ItemContacto contacto = dbWorker.obtenerContacto(post.getCorreo());
            if(contacto!=null)
                cargarImg(contacto);
            else preview_contacto_image.setImageResource(R.drawable.profile_white);

            preview_contacto_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemLongClick!=null)
                        onItemLongClick.abrirPerfilDe(post.getNombre(),post.getCorreo());
                    if(onItemEventListener!=null)
                        onItemEventListener.OnClickPhoto(post.getNombre(),post.getCorreo());
                }
            });
            preview_contacto_image.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onItemLongClick != null)
                        onItemLongClick.onLongClick(postGral);
                    return true;
                }
            });
            root.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onItemLongClick != null)
                        onItemLongClick.onLongClick(postGral);
                    return true;
                }
            });
        }

        private synchronized void cargarImg(ItemContacto contacto) {
            if(contacto.getCorreo().equals(YouChatApplication.idOficial)){
                YouChatApplication.ponerIconOficial(preview_contacto_image);
            }
            else {
                String cache = Utils.cargarImgCache(contacto.getRuta_img());
                if(cache.equals("")) preview_contacto_image.setImageResource(R.drawable.profile_white);
                else Glide.with(preview_contacto_image.getContext()).load(cache).into(preview_contacto_image);
            }
        }
    }

    private OnItemEventListener onItemEventListener;

    public void setOnItemEventListener(OnItemEventListener onItemEventListener) {
        this.onItemEventListener = onItemEventListener;
    }

    public interface OnItemEventListener{
        void OnClickPhoto(String nombre, String correo);
    }
}
