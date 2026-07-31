package cu.alexgi.youchat.BottomSheetDialogFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

import cu.alexgi.youchat.PublicarPostFragment;
import cu.alexgi.youchat.R;
import cu.alexgi.youchat.adapters.AdaptadorDatosEstilosTemasCompartir;
import cu.alexgi.youchat.items.ItemTemas;

import static cu.alexgi.youchat.MainActivity.context;
import static cu.alexgi.youchat.MainActivity.dbWorker;

public class BottomSheetDialogFragment_elegir_tema extends BottomSheetDialogFragment {

    private static PublicarPostFragment publicarPostFragment;

    public static BottomSheetDialogFragment_elegir_tema newInstance(PublicarPostFragment ppf) {
        publicarPostFragment = ppf;
        return new BottomSheetDialogFragment_elegir_tema();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_dialog_elegir_tema, container, false);

        View list_empty = v.findViewById(R.id.list_empty);
        View contenedor_lista_estilos_claros = v.findViewById(R.id.contenedor_lista_estilos_claros);
        RecyclerView lista_estilos_claros = v.findViewById(R.id.lista_estilos_claros);
        lista_estilos_claros.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        View contenedor_lista_estilos_oscuros = v.findViewById(R.id.contenedor_lista_estilos_oscuros);
        RecyclerView lista_estilos_oscuros = v.findViewById(R.id.lista_estilos_oscuros);
        lista_estilos_oscuros.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));

        ArrayList<ItemTemas> datosClaros = dbWorker.obtenerTemas(false);
        ArrayList<ItemTemas> datosOscuros = dbWorker.obtenerTemas(true);

        for(int i=0; i<datosClaros.size(); i++){
            if(datosClaros.get(i).getTipo()==1){
                datosClaros.remove(i);
                i--;
            }
        }
        for(int i=0; i<datosOscuros.size(); i++){
            if(datosOscuros.get(i).getTipo()==1){
                datosOscuros.remove(i);
                i--;
            }
        }
        if(datosClaros.size()==0 && datosOscuros.size()==0){
            contenedor_lista_estilos_claros.setVisibility(View.GONE);
            contenedor_lista_estilos_oscuros.setVisibility(View.GONE);
            list_empty.setVisibility(View.VISIBLE);
        }
        else {
            if(datosClaros.size()>0){
                AdaptadorDatosEstilosTemasCompartir adapterClaros = new AdaptadorDatosEstilosTemasCompartir(datosClaros);
                lista_estilos_claros.setAdapter(adapterClaros);

                adapterClaros.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = lista_estilos_claros.getChildAdapterPosition(v);
                        if(pos!=-1){
                            dismiss();
                            publicarPostFragment.compartirTema(datosClaros.get(pos));
                        }
                    }
                });
            } else contenedor_lista_estilos_claros.setVisibility(View.GONE);
            if(datosOscuros.size()>0){
                AdaptadorDatosEstilosTemasCompartir adapterOscuros = new AdaptadorDatosEstilosTemasCompartir(datosOscuros);
                lista_estilos_oscuros.setAdapter(adapterOscuros);

                adapterOscuros.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = lista_estilos_oscuros.getChildAdapterPosition(v);
                        if(pos!=-1){
                            dismiss();
                            publicarPostFragment.compartirTema(datosOscuros.get(pos));
                        }
                    }
                });
            } else contenedor_lista_estilos_oscuros.setVisibility(View.GONE);
        }
        return v;
    }
}
