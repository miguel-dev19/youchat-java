package cu.alexgi.youchat.BottomSheetDialogFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

import cu.alexgi.youchat.PublicarPostFragment;
import cu.alexgi.youchat.R;
import cu.alexgi.youchat.adapters.AdaptadorColorTarjeta;
import cu.alexgi.youchat.adapters.AdaptadorDatosFondoPostTarjeta;

import static cu.alexgi.youchat.MainActivity.context;

public class BottomSheetDialogFragment_fondo_post_tarjeta extends BottomSheetDialogFragment {

    private RecyclerView color_fondo_estado;
    private AdaptadorColorTarjeta adaptadorColorTarjeta;

    private ArrayList<Integer> degradados;
    private RecyclerView degradado_fondo_estado;
    private AdaptadorDatosFondoPostTarjeta adaptadorDatosFondoPostTarjeta;

    private static PublicarPostFragment publicarPostFragment;

    public static BottomSheetDialogFragment_fondo_post_tarjeta newInstance(PublicarPostFragment ppf) {
        publicarPostFragment = ppf;
        return new BottomSheetDialogFragment_fondo_post_tarjeta();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_dialog_fondo_post_tarjeta, container, false);
        color_fondo_estado = view.findViewById(R.id.color_fondo_estado);
        degradado_fondo_estado = view.findViewById(R.id.degradado_fondo_estado);
        degradado_fondo_estado.setLayoutManager(new GridLayoutManager(context,2));
        degradados = new ArrayList<>();
        for(int i=0; i<10; i++)
            degradados.add(i);
        adaptadorDatosFondoPostTarjeta = new AdaptadorDatosFondoPostTarjeta(degradados,this);
        degradado_fondo_estado.setAdapter(adaptadorDatosFondoPostTarjeta);

        color_fondo_estado.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        adaptadorColorTarjeta= new AdaptadorColorTarjeta(context);
        adaptadorColorTarjeta.setOnColorPickerClickListener(new AdaptadorColorTarjeta.OnColorPickerClickListener() {
            @Override
            public void onColorPickerClickListener(int colorCode, int pos) {
                dismiss();
                publicarPostFragment.ponerColorFondo(pos);
            }
        });
        color_fondo_estado.setAdapter(adaptadorColorTarjeta);

        return view;
    }

    public void ponerFondo(int pos) {
        dismiss();
        publicarPostFragment.ponerColorFondo(pos);
    }
}
