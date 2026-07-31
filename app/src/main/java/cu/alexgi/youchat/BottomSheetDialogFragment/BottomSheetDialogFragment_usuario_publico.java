package cu.alexgi.youchat.BottomSheetDialogFragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.vanniktech.emoji.EmojiTextView;

import cu.alexgi.youchat.CircleImageView;
import cu.alexgi.youchat.R;
import cu.alexgi.youchat.items.ItemContacto;
import cu.alexgi.youchat.items.ItemContactoPublico;

import static cu.alexgi.youchat.MainActivity.dbWorker;

public class BottomSheetDialogFragment_usuario_publico extends BottomSheetDialogFragment {

    private static Context context;

    private static ItemContactoPublico contactoPublico;

    private CircleImageView info_contacto_imagen;
    private EmojiTextView info_contacto_informacion, info_contacto_nombre;
    private TextView info_contacto_correo, info_contacto_telefono, info_contacto_genero;
    private TextView info_contacto_fecha_nacimiento, info_contacto_provincia;

    private View info_contacto_btn_ir_chat;

    public static BottomSheetDialogFragment_usuario_publico newInstance(Context c,
                                                                        ItemContactoPublico cp) {
        contactoPublico = cp;
        context = c;
        return new BottomSheetDialogFragment_usuario_publico();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_dialog_info_usuario_publico, container, false);

        info_contacto_imagen = v.findViewById(R.id.info_contacto_imagen);
        info_contacto_nombre = v.findViewById(R.id.info_contacto_nombre);
        info_contacto_correo = v.findViewById(R.id.info_contacto_correo);

//        info_contacto_alias = v.findViewById(R.id.info_contacto_alias);
        info_contacto_informacion = v.findViewById(R.id.info_contacto_informacion);
        info_contacto_telefono = v.findViewById(R.id.info_contacto_telefono);
        info_contacto_genero = v.findViewById(R.id.info_contacto_genero);
        info_contacto_fecha_nacimiento = v.findViewById(R.id.info_contacto_fecha_nacimiento);
        info_contacto_provincia = v.findViewById(R.id.info_contacto_provincia);

        info_contacto_btn_ir_chat = v.findViewById(R.id.info_contacto_btn_ir_chat);

        ItemContacto contacto = dbWorker.obtenerContacto(contactoPublico.getCorreo());
        if(contacto!=null){
            if(!contacto.getRuta_img().equals(""))
                Glide.with(context)
                        .load(contacto.getRuta_img())
                        .error(R.drawable.profile_white)
                        .into(info_contacto_imagen);
            else info_contacto_imagen.setImageResource(R.drawable.profile_white);
        }

        info_contacto_nombre.setText(contactoPublico.getAlias());
        info_contacto_correo.setText(contactoPublico.getCorreo());
        info_contacto_informacion.setText(contactoPublico.getInfo());
        info_contacto_telefono.setText(contactoPublico.getTelefono());
        info_contacto_genero.setText(contactoPublico.getGenero());
        info_contacto_provincia.setText(contactoPublico.getProvincia());
        info_contacto_fecha_nacimiento.setText(contactoPublico.getFecha_nac());

        info_contacto_btn_ir_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onClickListener!=null)
                    onClickListener.OnIrAChatSelected(contactoPublico.getAlias(),
                            contactoPublico.getCorreo());
                dismiss();
            }
        });

        return v;
    }

    private OnClickListener onClickListener;
    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
    public interface OnClickListener{
        void OnIrAChatSelected(String alias, String correo);
    }
}
