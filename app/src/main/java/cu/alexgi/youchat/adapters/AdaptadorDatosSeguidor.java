package cu.alexgi.youchat.adapters;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.vanniktech.emoji.EmojiTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import cu.alexgi.youchat.CircleImageView;
import cu.alexgi.youchat.Convertidor;
import cu.alexgi.youchat.MainActivity;
import cu.alexgi.youchat.R;
import cu.alexgi.youchat.SeguidoresFragment;
import cu.alexgi.youchat.SendMsg;
import cu.alexgi.youchat.Utils;
import cu.alexgi.youchat.YouChatApplication;
import cu.alexgi.youchat.items.ItemChat;
import cu.alexgi.youchat.items.ItemContacto;

import static cu.alexgi.youchat.MainActivity.context;
import static cu.alexgi.youchat.MainActivity.dbWorker;
import static cu.alexgi.youchat.MainActivity.mainActivity;

public class AdaptadorDatosSeguidor extends RecyclerView.Adapter<AdaptadorDatosSeguidor.ViewHolderDatos>
        implements View.OnClickListener, View.OnLongClickListener {

    ArrayList<ItemContacto> listaDatos;

    public ArrayList<ItemContacto> getListaDatos() {
        return listaDatos;
    }

    SeguidoresFragment seguidosFragment;
    private View.OnClickListener listener;
    private View.OnLongClickListener longClickListener;

    public AdaptadorDatosSeguidor(ArrayList<ItemContacto> listaDatos, SeguidoresFragment s) {
        this.listaDatos = listaDatos;
        seguidosFragment = s;
    }

    private void mostrarDialogoSeguir(String nombre, String correo) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(1);
        View mview=mainActivity.getLayoutInflater().inflate(R.layout.dialog_confirm,null);
        dialog.setContentView(mview);

        LinearLayout header=mview.findViewById(R.id.header);
        ImageView icono_eliminar = mview.findViewById(R.id.icono_eliminar);
        TextView text_icono = mview.findViewById(R.id.text_icono);
        TextView text_eliminar = mview.findViewById(R.id.text_eliminar);
        TextView btn_ok=mview.findViewById(R.id.btn_ok);
        View btn_cancel=mview.findViewById(R.id.btn_cancel);

        header.setBackgroundResource(YouChatApplication.colorTemaActual);
        icono_eliminar.setImageResource(R.drawable.add_user);
        text_icono.setText("Seguir");
        text_eliminar.setText("¿Quieres seguir a "+nombre+"? Se enviará una solicitud pidiendo ser " +
                "parte de su lista de seguidores, estará en sus manos aceptarte o no.");
        btn_ok.setText("ACEPTAR");

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(YouChatApplication.estaAndandoChatService()
                        && YouChatApplication.chatService.hayConex){
                    dialog.dismiss();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
                    Date date = new Date();
                    String fechaEntera = sdf.format(date);
                    String hora = Convertidor.conversionHora(fechaEntera);
                    String fecha = Convertidor.conversionFecha(fechaEntera);
                    ItemChat solicitud = new ItemChat(correo,"1");
                    solicitud.setId("-ss-");
                    solicitud.setHora(hora);
                    solicitud.setFecha(fecha);
                    YouChatApplication.chatService.enviarMensaje(solicitud, SendMsg.CATEGORY_SOL_SEGUIR);
                    Utils.ShowToastAnimated(mainActivity,"Solicitud enviada",R.raw.contact_check);
                }
                else Utils.mostrarToastDeConexion(mainActivity);
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCancelable(true);
        dialog.show();
    }

    private void mostrarDialogoDejarDeSeguir(String nombre, String correo) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(1);
        View mview=mainActivity.getLayoutInflater().inflate(R.layout.dialog_confirm,null);
        dialog.setContentView(mview);

        LinearLayout header=mview.findViewById(R.id.header);
        ImageView icono_eliminar = mview.findViewById(R.id.icono_eliminar);
        TextView text_icono = mview.findViewById(R.id.text_icono);
        TextView text_eliminar = mview.findViewById(R.id.text_eliminar);
        TextView btn_ok=mview.findViewById(R.id.btn_ok);
        View btn_cancel=mview.findViewById(R.id.btn_cancel);

        header.setBackgroundResource(YouChatApplication.colorTemaActual);

        icono_eliminar.setImageResource(R.drawable.remove_seguir);
        text_icono.setText("Dejar de seguir");
        text_eliminar.setText("¿Quieres dejar de seguir a "+nombre+"? Se eliminarán todos " +
                "sus estados y se eliminará de su lista de seguidores.");
        btn_ok.setText("ACEPTAR");

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(YouChatApplication.estaAndandoChatService()
                        && YouChatApplication.chatService.hayConex){
                    dialog.dismiss();
                    dbWorker.eliminarTodosLosEstadosDe(correo);
                    dbWorker.eliminarSiguiendoA(correo);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
                    Date date = new Date();
                    String fechaEntera = sdf.format(date);
                    String hora = Convertidor.conversionHora(fechaEntera);
                    String fecha = Convertidor.conversionFecha(fechaEntera);
                    ItemChat solicitud = new ItemChat(correo,"0");
                    solicitud.setId("-ss-");
                    solicitud.setHora(hora);
                    solicitud.setFecha(fecha);
                    YouChatApplication.chatService.enviarMensaje(solicitud,SendMsg.CATEGORY_SOL_SEGUIR);
                    Utils.ShowToastAnimated(mainActivity,"Correo enviado",R.raw.contact_check);

                    int p = buscarId(correo);
                    if(p!=-1){
                        notifyItemChanged(p,7);
                    }
                    seguidosFragment.eliminarSiguiendoA(correo);
                }
                else Utils.mostrarToastDeConexion(mainActivity);
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCancelable(true);
        dialog.show();
    }

    private int buscarId(String correo){
        int l = listaDatos.size();
        for(int i=0; i<l; i++){
            if(listaDatos.get(i).getCorreo().equals(correo)){
                return i;
            }
        }
        return -1;
    }

    @Override
    public ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_seguidores,null, false);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
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

    public void setLongClickListener(View.OnLongClickListener l){
        longClickListener = l;
    }

    @Override
    public void onClick(View v) {
        if(listener!=null)
            listener.onClick(v);
    }

    @Override
    public boolean onLongClick(View v) {
        if(longClickListener!=null){
            longClickListener.onLongClick(v);
            return true;
        }
        return false;
    }

    public class ViewHolderDatos extends RecyclerView.ViewHolder {

        private final MaterialCardView fondo_btn;
        private final CircleImageView preview_seguidor_image;
        private final EmojiTextView tv_seguidor_nombre;
        private final TextView tv_seguidor_correo;
        private final TextView btn_seguir;
        private final View root;

        public ViewHolderDatos(@NonNull View itemView) {
            super(itemView);
            root = itemView;
            preview_seguidor_image = itemView.findViewById(R.id.preview_seguidor_image);
            tv_seguidor_nombre = itemView.findViewById(R.id.tv_seguidor_nombre);
            tv_seguidor_correo = itemView.findViewById(R.id.tv_seguidor_correo);
            btn_seguir = itemView.findViewById(R.id.btn_seguir);
            fondo_btn = itemView.findViewById(R.id.fondo_btn);
        }

        public synchronized void AsignarDatos(ItemContacto contacto){
            cargarImg(contacto);
            tv_seguidor_nombre.setText(contacto.getNombreMostrar());
            tv_seguidor_correo.setText(contacto.getCorreo());

            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    seguidosFragment.irPerfil(contacto.getNombreMostrar(), contacto.getCorreo());
                }
            });

            fondo_btn.setStrokeColor(Color.parseColor(YouChatApplication.itemTemas.getColor_btn()));
            fondo_btn.setRadius(YouChatApplication.curvaChat<=32?YouChatApplication.curvaChat:32);

            root.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    seguidosFragment.sacarPopupSeguidosFragment(root,contacto.getCorreo());
                    return false;
                }
            });

            if(MainActivity.dbWorker.existeSiguiendoA(contacto.getCorreo())){
                btn_seguir.setText("siguiendo");
                btn_seguir.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getColor_btn()));
                fondo_btn.setCardBackgroundColor(Color.TRANSPARENT);
                fondo_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mostrarDialogoDejarDeSeguir(contacto.getNombreMostrar(), contacto.getCorreo());
                    }
                });
            }
            else{
                btn_seguir.setText("seguir");
                btn_seguir.setTextColor(Color.WHITE);
                fondo_btn.setCardBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_btn()));
                fondo_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mostrarDialogoSeguir(contacto.getNombreMostrar(), contacto.getCorreo());
                    }
                });
            }
        }

        private synchronized void cargarImg(ItemContacto contacto) {
            String cache = Utils.cargarImgCache(contacto.getRuta_img());
            if(cache.equals("")) preview_seguidor_image.setImageResource(R.drawable.profile_white);
            else Glide.with(preview_seguidor_image.getContext()).load(cache).into(preview_seguidor_image);
        }
    }
}
