package cu.alexgi.youchat.BottomSheetDialogFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import cu.alexgi.youchat.AddThemeFragment;
import cu.alexgi.youchat.R;
import cu.alexgi.youchat.YouChatApplication;
import cu.alexgi.youchat.adapters.AdaptadorDatosFondo;
import cu.alexgi.youchat.items.ItemFondo;

import static cu.alexgi.youchat.MainActivity.context;
import static cu.alexgi.youchat.MainActivity.permisos;

public class BottomSheetDialogFragment_elegir_fondo extends BottomSheetDialogFragment {

    private static AddThemeFragment addThemeFragment;
    private static String rutaFondo;

    public static BottomSheetDialogFragment_elegir_fondo newInstance(AddThemeFragment atf, String rf) {
        addThemeFragment = atf;
        rutaFondo = rf;
        return new BottomSheetDialogFragment_elegir_fondo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_dialog_elegir_fondo, container, false);

        TextView list_empty=v.findViewById(R.id.list_empty);
        RecyclerView lista_fondos=v.findViewById(R.id.lista_fondos);
        ArrayList<ItemFondo> datos_fondos = new ArrayList<>();

        v.findViewById(R.id.efab_elegirFondo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                addThemeFragment.ponerFondo("");
            }
        });

        if(permisos.requestPermissionAlmacenamiento()){
            File root = new File(YouChatApplication.RUTA_FONDO_YOUCHAT);
            if(root.exists()){
                File[] listaFondo = root.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return(name.toLowerCase().endsWith(".jpg")
                                || name.toLowerCase().endsWith(".jpeg")
                                || name.toLowerCase().endsWith(".png")
                                || name.toLowerCase().endsWith(".gif"));
                    }
                });
                int l = listaFondo.length;
                if(l>0){
                    for(int i=0; i<l; i++){
                        datos_fondos.add(new ItemFondo(false,
                                false, -1, listaFondo[i].getPath()));
                        if(rutaFondo.equals(listaFondo[i].getPath())){
                            datos_fondos.get(i).setEstaSeleccionado(true);
                        }
                    }
                }
            }
        }

        if(datos_fondos.size()==0){
            list_empty.setVisibility(View.VISIBLE);
        }
        else {
            lista_fondos.setLayoutManager(new GridLayoutManager(context, 3));
            AdaptadorDatosFondo mAdapter = new AdaptadorDatosFondo(datos_fondos);
            lista_fondos.setAdapter(mAdapter);
            mAdapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    int pos = lista_fondos.getChildAdapterPosition(v);
                    if(pos!=-1){
                        addThemeFragment.ponerFondo(datos_fondos.get(pos).getRuta());
                    }
                }
            });
        }
        return v;
    }

}
