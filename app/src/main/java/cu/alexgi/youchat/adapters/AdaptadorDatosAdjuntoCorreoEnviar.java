package cu.alexgi.youchat.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

import cu.alexgi.youchat.CircleImageView;
import cu.alexgi.youchat.NuevoMensajeCorreoFragment;
import cu.alexgi.youchat.R;
import cu.alexgi.youchat.Utils;
import cu.alexgi.youchat.items.ItemAdjuntoCorreo;

public class AdaptadorDatosAdjuntoCorreoEnviar extends RecyclerView.Adapter<AdaptadorDatosAdjuntoCorreoEnviar.ViewHolderDatos>
        implements View.OnClickListener, View.OnLongClickListener {

    private ArrayList<ItemAdjuntoCorreo> listaDatos;
    private View.OnClickListener listener;
    private View.OnLongClickListener onLongClickListener;
    private Context context;
    private NuevoMensajeCorreoFragment nuevoMensajeCorreoFragment;

    public AdaptadorDatosAdjuntoCorreoEnviar(Context c, ArrayList<ItemAdjuntoCorreo> listaDatos,
                                             NuevoMensajeCorreoFragment v) {
        context = c;
        this.listaDatos = listaDatos;
        nuevoMensajeCorreoFragment = v;
    }

    @Override
    public ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_adjunto_mensaje_correo_enviar,null, false);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);

        return new ViewHolderDatos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderDatos holder, int position) {
        ItemAdjuntoCorreo adjuntoCorreo = listaDatos.get(position);
        holder.AsignarDatos(adjuntoCorreo);
    }

    @Override
    public int getItemCount() {
        return listaDatos.size();
    }

    public void setOnClickListener(View.OnClickListener l)
    {
        listener=l;
    }
    public void setOnLongClickListener(View.OnLongClickListener l)
    {
        onLongClickListener=l;
    }

    @Override
    public void onClick(View v) {
        if(listener!=null)
        {
            listener.onClick(v);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if(onLongClickListener!=null){
            onLongClickListener.onLongClick(v);
            return true;
        }
        return false;
    }

    public class ViewHolderDatos extends RecyclerView.ViewHolder {

        private TextView tv_file_absolute_path, fileSize;
        private CircleImageView iv_file;
        private View iv_quitarAdjunto, root;


        public ViewHolderDatos(@NonNull View itemView) {
            super(itemView);
            root = itemView;
            tv_file_absolute_path = itemView.findViewById(R.id.tv_file_absolute_path);
            fileSize = itemView.findViewById(R.id.fileSize);
            iv_file = itemView.findViewById(R.id.iv_file);
            iv_quitarAdjunto = itemView.findViewById(R.id.iv_quitarAdjunto);
        }

        @SuppressLint("RestrictedApi")
        public synchronized void AsignarDatos(final ItemAdjuntoCorreo adjunto) {
            tv_file_absolute_path.setText(new File(adjunto.getNombre()).getName());

            switch (adjunto.getTipo()) {
                case 0:
                    iv_file.setImageResource(R.drawable.file_icon_read);
                    iv_file.setCircleBackgroundColor(context.getResources().getColor(R.color.card6));
                    iv_file.setBorderColor(context.getResources().getColor(R.color.card6));
                    break;
                case 1:
                    iv_file.setImageResource(R.drawable.file_icon_drawer_audio);
                    iv_file.setCircleBackgroundColor(context.getResources().getColor(R.color.card14));
                    iv_file.setBorderColor(context.getResources().getColor(R.color.card14));
                    break;
                case 2:
                    iv_file.setImageResource(R.drawable.file_icon_drawer_apk);
                    iv_file.setCircleBackgroundColor(context.getResources().getColor(R.color.card9));
                    iv_file.setBorderColor(context.getResources().getColor(R.color.card9));
                    break;
                case 3:
                    iv_file.setImageResource(R.drawable.file_icon_drawer_video);
                    iv_file.setCircleBackgroundColor(context.getResources().getColor(R.color.card1));
                    iv_file.setBorderColor(context.getResources().getColor(R.color.card1));
                    break;
                case 4:
                    iv_file.setImageResource(R.drawable.file_icon_image);
                    iv_file.setCircleBackgroundColor(context.getResources().getColor(R.color.card11));
                    iv_file.setBorderColor(context.getResources().getColor(R.color.card11));
                    break;
                case 5:
                    iv_file.setImageResource(R.drawable.file_icon_font);
                    iv_file.setCircleBackgroundColor(context.getResources().getColor(R.color.card17));
                    iv_file.setBorderColor(context.getResources().getColor(R.color.card17));
                    break;
                case 6:
                    iv_file.setImageResource(R.drawable.file_icon_gif);
                    iv_file.setCircleBackgroundColor(context.getResources().getColor(R.color.card12));
                    iv_file.setBorderColor(context.getResources().getColor(R.color.card12));
                    break;
                case 7:
                    iv_file.setImageResource(R.drawable.file_icon_rar);
                    iv_file.setCircleBackgroundColor(context.getResources().getColor(R.color.card5));
                    iv_file.setBorderColor(context.getResources().getColor(R.color.card5));
                    break;
                case 8:
                    iv_file.setImageResource(R.drawable.file_icon_markup);
                    iv_file.setCircleBackgroundColor(context.getResources().getColor(R.color.card10));
                    iv_file.setBorderColor(context.getResources().getColor(R.color.card10));
                    break;
                case 9:
                    iv_file.setImageResource(R.drawable.file_icon_pdf);
                    iv_file.setCircleBackgroundColor(context.getResources().getColor(R.color.card16));
                    iv_file.setBorderColor(context.getResources().getColor(R.color.card16));
                    break;
                default:
                    iv_file.setImageResource(R.drawable.file_icon_read);
                    iv_file.setCircleBackgroundColor(context.getResources().getColor(R.color.card6));
                    iv_file.setBorderColor(context.getResources().getColor(R.color.card6));
            }

            iv_quitarAdjunto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nuevoMensajeCorreoFragment.quitarAdjunto(adjunto.getId());
                }
            });
            fileSize.setText(Utils.convertirBytes(adjunto.getPeso()));

            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nuevoMensajeCorreoFragment.abrirArchivoEn(adjunto.getNombre());
                }
            });
        }
    }
}
