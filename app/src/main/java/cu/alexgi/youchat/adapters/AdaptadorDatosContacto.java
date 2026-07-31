package cu.alexgi.youchat.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vanniktech.emoji.EmojiTextView;

import java.security.SecureRandom;
import java.util.ArrayList;

import cu.alexgi.youchat.CircleImageView;
import cu.alexgi.youchat.ContactActivity;
import cu.alexgi.youchat.R;
import cu.alexgi.youchat.Utils;
import cu.alexgi.youchat.YouChatApplication;
import cu.alexgi.youchat.items.ItemContacto;
import cu.alexgi.youchat.sticky_headers_data.StickyRecyclerHeadersAdapter;

public class AdaptadorDatosContacto
        extends RecyclerView.Adapter<AdaptadorDatosContacto.ViewHolderDatos>
        implements View.OnClickListener, StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {

    ArrayList<ItemContacto> listaDatos;
    private View.OnClickListener listener;
    private ContactActivity contactActivity;

    public AdaptadorDatosContacto(ArrayList<ItemContacto> listaDatos, ContactActivity ca) {
        this.listaDatos = listaDatos;
        contactActivity = ca;
    }

    @Override
    public ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType==66) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_contacto_divider,null, false);
            return new ViewHolderDatosDivider(view);
        }
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_contacto_espacio,null, false);
            view.setOnClickListener(this);
            return new ViewHolderDatosContactos(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderDatos holder, int position) {

        ItemContacto contacto = listaDatos.get(position);
        switch (holder.getItemViewType()) {
            case 66: //divider
                ViewHolderDatosDivider vhdd = (ViewHolderDatosDivider) holder;
                vhdd.AsignarDatos(contacto.getInfo());
                break;
            case 1: //contacto normal
                ViewHolderDatosContactos vhdc = (ViewHolderDatosContactos) holder;
                vhdc.AsignarDatos(contacto);
                break;
        }
    }

    //sticky header
    @Override
    public long getHeaderId(int position) {
        if(listaDatos.size()>0 && listaDatos.get(position).getTipo_contacto()!=66){
            int idSticky;
            if(YouChatApplication.orden_contacto_nombre)
                idSticky = listaDatos.get(position).getNombreMostrar().toLowerCase().charAt(0);
            else idSticky = listaDatos.get(position).getCorreo().toLowerCase().charAt(0);
            boolean usaYC = listaDatos.get(position).isUsaYouchat();
            if(usaYC) idSticky+=1423;
//            idSticky = 6690+listaDatos.get(position).getInfo().charAt(0);
            return idSticky;
        }else return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_header, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        EmojiTextView textView = (EmojiTextView) holder.itemView;
        if(listaDatos.get(position).getTipo_contacto()==66)
            textView.setVisibility(View.INVISIBLE);
        else{
            textView.setVisibility(View.VISIBLE);
            if(YouChatApplication.orden_contacto_nombre)
                textView.setText(String.valueOf(listaDatos.get(position).getNombreMostrar().toUpperCase().charAt(0)));
            else textView.setText(String.valueOf(listaDatos.get(position).getCorreo().toUpperCase().charAt(0)));
        }
//        holder.itemView.setBackgroundColor(getRandomColor());
    }

    private int getRandomColor() {
        SecureRandom rgen = new SecureRandom();
        return Color.HSVToColor(150, new float[]{
                rgen.nextInt(359), 1, 1
        });
    }
    //sticky header fin

    @Override
    public int getItemCount() {
        return listaDatos.size();
    }

    @Override
    public int getItemViewType(int position) {
        int tipo = listaDatos.get(position).getTipo_contacto();
        if(tipo==66)
            return 66;
        else return 1;
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
        public ViewHolderDatos(@NonNull View itemView) {
            super(itemView);
        }
        public void AsignarDatos() {}
    }

    ///////////////////////////////////////////////////////////
    public class ViewHolderDatosDivider extends ViewHolderDatos {

        //lo q esta en el xml item_1
        TextView tipo_contacto_divider;

        public ViewHolderDatosDivider(@NonNull View itemView) {
            super(itemView);
            tipo_contacto_divider = itemView.findViewById(R.id.tipo_contacto_divider);
        }

        public void AsignarDatos(String infoText) {
            tipo_contacto_divider.setText(infoText);
        }
    }

    ///////////////////////////////////////////////////////////
    public class ViewHolderDatosContactos extends ViewHolderDatos {

        //lo q esta en el xml item_1
        TextView contacto_correo;
        //ImageView fotos;
        EmojiTextView contacto_nombre;
        CircleImageView preview_contacto_image;

        View preview_chat_item;
        ImageView user_verificado;


        public ViewHolderDatosContactos(@NonNull View itemView) {
            super(itemView);
            contacto_nombre = itemView.findViewById(R.id.contacto_nombre);
            contacto_correo = itemView.findViewById(R.id.contacto_correo);
            preview_contacto_image = itemView.findViewById(R.id.preview_contacto_image);
            preview_chat_item = itemView.findViewById(R.id.preview_chat_item);
            user_verificado = itemView.findViewById(R.id.user_verificado);
        }

        public synchronized void AsignarDatos(ItemContacto contacto) {
            String usu = contacto.getNombreMostrar();
            String cor = contacto.getCorreo();
            boolean esMascota = contacto.esMascota();
            int tipoMascota = contacto.getVersion();

            contacto_nombre.setText(usu);
            contacto_correo.setText(cor);

            ///VERIFICADO///
            if(YouChatApplication.comprobarOficialidad(contacto.getCorreo())){
                user_verificado.setImageResource(R.drawable.verified_profile);
                user_verificado.setVisibility(View.VISIBLE);
            }
            else if(contacto.getCant_seguidores()>=YouChatApplication.usuMayor){
                user_verificado.setImageResource(R.drawable.vip_crown_line);
                user_verificado.setVisibility(View.VISIBLE);
            }
            else if(contacto.getCant_seguidores()>=YouChatApplication.usuMedio){
                user_verificado.setImageResource(R.drawable.vip_diamond_line);
                user_verificado.setVisibility(View.VISIBLE);
            }
            else if(contacto.getCant_seguidores()>=YouChatApplication.usuMenor){
                user_verificado.setImageResource(R.drawable.award_line);
                user_verificado.setVisibility(View.VISIBLE);
            }
            else user_verificado.setVisibility(View.GONE);
            ///VERIFICADO///

            if(!esMascota){
                cargarImg(contacto);
                preview_chat_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.setEnabled(false);
                        if(!cor.equals(YouChatApplication.idOficial)) contactActivity.MostrarPreviewContacto(cor);
                        v.setEnabled(true);
                    }
                });
            }
            else {
                preview_chat_item.setOnClickListener(null);
                switch (tipoMascota){
                    case 1:
                        preview_contacto_image.setImageResource(R.drawable.placeholder);
                        break;
                    case 2:
                        preview_contacto_image.setImageResource(R.drawable.iconycvector9);
                        break;
                }
            }
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
}
