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
import cu.alexgi.youchat.Utils;
import cu.alexgi.youchat.items.ItemEstadisticaPersonal;

public class BottomSheetDialogFragment_User_Estadisticas extends BottomSheetDialogFragment {

    private static ItemEstadisticaPersonal item;
    private static String ruta, nombre;
    private static Context context;

    public static BottomSheetDialogFragment_User_Estadisticas newInstance(Context c, String n, String r, ItemEstadisticaPersonal itemEstadisticaPersonal) {
        nombre = n;
        ruta = r;
        item = itemEstadisticaPersonal;
        context = c;
        return new BottomSheetDialogFragment_User_Estadisticas();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_dialog_user_estadisticas, container, false);

        CircleImageView info_contacto_imagen = v.findViewById(R.id.info_contacto_imagen);
        Glide.with(context).load(ruta).error(R.drawable.profile_white).into(info_contacto_imagen);

        EmojiTextView info_contacto_nombre = v.findViewById(R.id.info_contacto_nombre);
        info_contacto_nombre.setText(nombre);

        TextView info_contacto_correo = v.findViewById(R.id.info_contacto_correo);
        info_contacto_correo.setText(item.getId());

        TextView tv_env_cant_msj,tv_env_cant_img,tv_env_cant_aud,tv_env_cant_arc,tv_env_cant_sti;
        TextView tv_env_bytes_msj,tv_env_bytes_img,tv_env_bytes_aud,tv_env_bytes_arc,tv_env_bytes_sti;
        TextView tv_rec_cant_msj,tv_rec_cant_img,tv_rec_cant_aud,tv_rec_cant_arc,tv_rec_cant_sti,tv_rec_cant_est,tv_rec_cant_act;
        TextView tv_rec_bytes_msj,tv_rec_bytes_img,tv_rec_bytes_aud,tv_rec_bytes_arc,tv_rec_bytes_sti,tv_rec_bytes_est,tv_rec_bytes_act;

        tv_env_cant_msj = v.findViewById(R.id.tv_env_cant_msj);
        tv_env_cant_img = v.findViewById(R.id.tv_env_cant_img);
        tv_env_cant_aud = v.findViewById(R.id.tv_env_cant_aud);
        tv_env_cant_arc = v.findViewById(R.id.tv_env_cant_arc);
        tv_env_cant_sti = v.findViewById(R.id.tv_env_cant_sti);

        tv_env_bytes_msj = v.findViewById(R.id.tv_env_bytes_msj);
        tv_env_bytes_img = v.findViewById(R.id.tv_env_bytes_img);
        tv_env_bytes_aud = v.findViewById(R.id.tv_env_bytes_aud);
        tv_env_bytes_arc = v.findViewById(R.id.tv_env_bytes_arc);
        tv_env_bytes_sti = v.findViewById(R.id.tv_env_bytes_sti);

        tv_rec_cant_msj = v.findViewById(R.id.tv_rec_cant_msj);
        tv_rec_cant_img = v.findViewById(R.id.tv_rec_cant_img);
        tv_rec_cant_aud = v.findViewById(R.id.tv_rec_cant_aud);
        tv_rec_cant_arc = v.findViewById(R.id.tv_rec_cant_arc);
        tv_rec_cant_sti = v.findViewById(R.id.tv_rec_cant_sti);
        tv_rec_cant_est = v.findViewById(R.id.tv_rec_cant_est);
        tv_rec_cant_act = v.findViewById(R.id.tv_rec_cant_act);

        tv_rec_bytes_msj = v.findViewById(R.id.tv_rec_bytes_msj);
        tv_rec_bytes_img = v.findViewById(R.id.tv_rec_bytes_img);
        tv_rec_bytes_aud = v.findViewById(R.id.tv_rec_bytes_aud);
        tv_rec_bytes_arc = v.findViewById(R.id.tv_rec_bytes_arc);
        tv_rec_bytes_sti = v.findViewById(R.id.tv_rec_bytes_sti);
        tv_rec_bytes_est = v.findViewById(R.id.tv_rec_bytes_est);
        tv_rec_bytes_act = v.findViewById(R.id.tv_rec_bytes_act);


        tv_env_cant_msj.setText(""+item.getCant_msg_env());
        tv_env_cant_img.setText(""+item.getCant_img_env());
        tv_env_cant_aud.setText(""+item.getCant_aud_env());
        tv_env_cant_arc.setText(""+item.getCant_arc_env());
        tv_env_cant_sti.setText(""+item.getCant_sti_env());

        tv_env_bytes_msj.setText(Utils.convertirBytes(item.getCant_msg_env_mg()));
        tv_env_bytes_img.setText(Utils.convertirBytes(item.getCant_img_env_mg()));
        tv_env_bytes_aud.setText(Utils.convertirBytes(item.getCant_aud_env_mg()));
        tv_env_bytes_arc.setText(Utils.convertirBytes(item.getCant_arc_env_mg()));
        tv_env_bytes_sti.setText(Utils.convertirBytes(item.getCant_sti_env_mg()));

        tv_rec_cant_msj.setText(""+item.getCant_msg_rec());
        tv_rec_cant_img.setText(""+item.getCant_img_rec());
        tv_rec_cant_aud.setText(""+item.getCant_aud_rec());
        tv_rec_cant_arc.setText(""+item.getCant_arc_rec());
        tv_rec_cant_sti.setText(""+item.getCant_sti_rec());
        tv_rec_cant_est.setText(""+item.getCant_est_rec());
        tv_rec_cant_act.setText(""+item.getCant_act_per_rec());

        tv_rec_bytes_msj.setText(Utils.convertirBytes(item.getCant_msg_rec_mg()));
        tv_rec_bytes_img.setText(Utils.convertirBytes(item.getCant_img_rec_mg()));
        tv_rec_bytes_aud.setText(Utils.convertirBytes(item.getCant_aud_rec_mg()));
        tv_rec_bytes_arc.setText(Utils.convertirBytes(item.getCant_arc_rec_mg()));
        tv_rec_bytes_sti.setText(Utils.convertirBytes(item.getCant_sti_rec_mg()));
        tv_rec_bytes_est.setText(Utils.convertirBytes(item.getCant_est_rec_mg()));
        tv_rec_bytes_act.setText(Utils.convertirBytes(item.getCant_act_per_rec_mg()));

        return v;
    }
}
