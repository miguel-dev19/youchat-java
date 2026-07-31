package cu.alexgi.youchat.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

import cu.alexgi.youchat.CircleImageView;
import cu.alexgi.youchat.R;
import cu.alexgi.youchat.Utils;
import cu.alexgi.youchat.VistaMensajeCorreoFragment;
import cu.alexgi.youchat.YouChatApplication;
import cu.alexgi.youchat.items.ItemAdjuntoCorreo;
import cu.alexgi.youchat.items.ItemContacto;
import cu.alexgi.youchat.progressbar.DownloadProgressView;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

import static cu.alexgi.youchat.MainActivity.mainActivity;

public class AdaptadorDatosAdjuntoCorreo extends RecyclerView.Adapter<AdaptadorDatosAdjuntoCorreo.ViewHolderDatos>
        implements View.OnClickListener, View.OnLongClickListener {

    private ArrayList<ItemAdjuntoCorreo> listaDatos;
    private View.OnClickListener listener;
    private View.OnLongClickListener onLongClickListener;
    private Context context;
    //private PrincipalFragment principalFragment;
    private ColorStateList stateList,stateListEstadoView;
    private ItemContacto contacto;
    private boolean esMio;
    private VistaMensajeCorreoFragment vistaMensajeCorreoFragment;

    public AdaptadorDatosAdjuntoCorreo(Context c, ArrayList<ItemAdjuntoCorreo> listaDatos,
                                       boolean esMio, VistaMensajeCorreoFragment v) {
        context = c;
        this.esMio = esMio;
        this.listaDatos = listaDatos;
        vistaMensajeCorreoFragment = v;
        //principalFragment= pa.principalFragment;

        stateList = ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getColor_btn()));
        stateListEstadoView = ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getColor_texto_oscuro()));
    }

    @Override
    public ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_adjunto_mensaje_correo,null, false);
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
        private View cl_background;
        private DownloadProgressView progress_descarga;


        public ViewHolderDatos(@NonNull View itemView) {
            super(itemView);
            tv_file_absolute_path = itemView.findViewById(R.id.tv_file_absolute_path);
            fileSize = itemView.findViewById(R.id.fileSize);
            iv_file = itemView.findViewById(R.id.iv_file);
            cl_background=itemView.findViewById(R.id.cl_background);
            progress_descarga=itemView.findViewById(R.id.progress_descarga);
        }

        @SuppressLint("RestrictedApi")
        public synchronized void AsignarDatos(final ItemAdjuntoCorreo adjunto) {
            tv_file_absolute_path.setText(adjunto.getNombre());

            String rutaDato;
            if(esMio){
                rutaDato = adjunto.getNombre();
            }else {
                rutaDato = YouChatApplication.RUTA_ADJUNTOS_CORREO+adjunto.getNombre();
            }

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

            File file = new File(rutaDato);
            if(file.exists()){
                progress_descarga.setVisibility(View.GONE);
                fileSize.setText(Utils.convertirBytes(file.length()));
                cl_background.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vistaMensajeCorreoFragment.abrirArchivoEn(rutaDato);
                    }
                });
            }
            else {
                if(esMio || adjunto.getPosicion()==-1){
                    progress_descarga.setVisibility(View.GONE);
                    cl_background.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Utils.ShowToastAnimated(mainActivity, "No existe el archivo", R.raw.error);
                        }
                    });
                }
                else {
                    progress_descarga.setVisibility(View.VISIBLE);
                    cl_background.setOnClickListener(null);
                    progress_descarga.setProgressListener(new Function1<Boolean, Unit>() {
                        @Override
                        public Unit invoke(Boolean descargar) {
                            if(YouChatApplication.estaAndandoChatService()
                                    && YouChatApplication.chatService.hayConex){
                                progress_descarga.quitarClick();
                                progress_descarga.setDownloading(true);
                                progress_descarga.setProgress(0f);
                                vistaMensajeCorreoFragment.descargarCorreo(adjunto,progress_descarga);
                            }
                            else{
                                Utils.mostrarToastDeConexion(mainActivity);
                                progress_descarga.setDownloading(false);
                            }
                            return null;
                        }
                    });
                }

                if(adjunto.getPeso()>0){
                    fileSize.setText(Utils.convertirBytes(adjunto.getPeso()));
                }else {
                    fileSize.setText("Tamaño desconocido");
                }
            }
        }



//        public void abrirArchivoEn(String rutaArchivo){
//            if(!permisos.requestPermissionAlmacenamiento()) return;
//            File file = new File(rutaArchivo);
//            if(file.exists()){
//                String ext= Utils.obtenerExtension(file.getName());
//                String tipo;
//                if(SimpleFileExplorerFragment.AUDIO.contains(ext)) tipo = "audio/*";
//                else if(SimpleFileExplorerFragment.APK.contains(ext)) tipo = "application/vnd.android.package-archive";
//                else if(SimpleFileExplorerFragment.VIDEO.contains(ext)) tipo = "video/*";
//                else if(SimpleFileExplorerFragment.IMAGEN.contains(ext)) tipo = "image/*";
//                else if(SimpleFileExplorerFragment.TXT.contains(ext)) tipo = "text/*";
//                else if(SimpleFileExplorerFragment.GIF.contains(ext)) tipo = "image/gif";
//                else if(SimpleFileExplorerFragment.COMPRESS.contains(ext)) tipo = "*/*";
//                else if(SimpleFileExplorerFragment.XML.contains(ext)) tipo = "text/html";
//                else if(SimpleFileExplorerFragment.PDF.contains(ext)) tipo = "application/pdf";
//                else tipo = "*/*";
//
//                Uri uri = Uri.fromFile(file);
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setDataAndType(uri, tipo);
//                startActivity(intent);
//
//                ext = rutaArchivo.substring(rutaArchivo.lastIndexOf(".")+1);
//                if(ext!=null){
//                    tipo = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
//                    if(tipo!=null) tipo = "*/*";
//                }
//                else tipo = "*/*";
//                Log.e("FILE", "EXT: "+ext+"/ TIPO: "+tipo);
//            }
//        }

    }
}
