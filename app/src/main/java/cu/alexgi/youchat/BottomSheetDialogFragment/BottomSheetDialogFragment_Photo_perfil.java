package cu.alexgi.youchat.BottomSheetDialogFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import cu.alexgi.youchat.R;
import cu.alexgi.youchat.ViewYouPerfilActivity;
import cu.alexgi.youchat.WelcomePerfilActivity;

public class BottomSheetDialogFragment_Photo_perfil extends BottomSheetDialogFragment {

    LinearLayout option_camara, option_galeria, option_eliminar;
    static ViewYouPerfilActivity viewYouPerfilActivity;
    static WelcomePerfilActivity welcomePerfilActivity;
    static boolean desdeViewYouPerfil;

    public static BottomSheetDialogFragment_Photo_perfil newInstance(ViewYouPerfilActivity view) {
        viewYouPerfilActivity=view;
        desdeViewYouPerfil = true;
        return new BottomSheetDialogFragment_Photo_perfil();
    }

    public static BottomSheetDialogFragment_Photo_perfil newInstance(WelcomePerfilActivity view) {
        welcomePerfilActivity=view;
        desdeViewYouPerfil = false;
        return new BottomSheetDialogFragment_Photo_perfil();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_dialog_photo_perfil, container, false);

        option_camara = v.findViewById(R.id.option_camara);
        option_galeria = v.findViewById(R.id.option_galeria);
        option_eliminar = v.findViewById(R.id.option_eliminar);

        option_camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                option_camara.setEnabled(false);
                if(desdeViewYouPerfil) viewYouPerfilActivity.usarCamara();
                else welcomePerfilActivity.usarCamara();
                dismiss();
                option_camara.setEnabled(true);
            }
        });

        option_galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                option_galeria.setEnabled(false);
                if(desdeViewYouPerfil) viewYouPerfilActivity.abrirGaleria();
                else welcomePerfilActivity.abrirGaleria();
                dismiss();
                option_galeria.setEnabled(true);
            }
        });

        option_eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                option_eliminar.setEnabled(false);
                if(desdeViewYouPerfil) viewYouPerfilActivity.borrarImgPerfil();
                else welcomePerfilActivity.borrarImgPerfil();
                dismiss();
                option_eliminar.setEnabled(true);
            }
        });

        return v;
    }
}
