package cu.alexgi.youchat.BottomSheetDialogFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

import cu.alexgi.youchat.R;
import cu.alexgi.youchat.adapters.AdaptadorDatosAtajos;
import cu.alexgi.youchat.items.ItemAtajo;

import static cu.alexgi.youchat.MainActivity.context;
import static cu.alexgi.youchat.MainActivity.dbWorker;

public class BottomSheetDialogFragment_Atajos extends BottomSheetDialogFragment {

    private RecyclerView lista_atajos;
    private AdaptadorDatosAtajos mAdapter;
    private ArrayList<ItemAtajo> datos_atajo;
    private TextView list_empty;

    private static boolean puedeBorrar, puedeAdd;
    public static BottomSheetDialogFragment_Atajos newInstance(boolean pe, boolean pa) {
        puedeBorrar = pe;
        puedeAdd = pa;
        return new BottomSheetDialogFragment_Atajos();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_dialog_atajos, container, false);

        list_empty=v.findViewById(R.id.list_empty);
        if(puedeAdd){
            View add_new_atajo = v.findViewById(R.id.add_new_atajo);
            add_new_atajo.setVisibility(View.VISIBLE);
            add_new_atajo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onAtajoClickListener!=null){
                        dismiss();
                        onAtajoClickListener.OnAddAtajo();
                    }
                }
            });
        }
        lista_atajos=v.findViewById(R.id.lista_atajos);
        datos_atajo = new ArrayList<>();
        lista_atajos.setLayoutManager(new LinearLayoutManager(context,RecyclerView.VERTICAL,false));
        datos_atajo = dbWorker.obtenerAtajos();
        mAdapter = new AdaptadorDatosAtajos(datos_atajo,puedeBorrar);
        lista_atajos.setAdapter(mAdapter);

        mAdapter.setOnItemAtajoClickListener(new AdaptadorDatosAtajos.OnItemAtajoClickListener() {
            @Override
            public void OnItemClick(ItemAtajo atajo) {
                if(onAtajoClickListener!=null){
                    dismiss();
                    onAtajoClickListener.OnClick(atajo);
                }
            }

            @Override
            public void OnItemDelete(ItemAtajo atajo, int pos) {
                if(pos!=-1 && pos<datos_atajo.size()){
                    dbWorker.eliminarAtajo(atajo.getComando());
                    datos_atajo.remove(pos);
                    mAdapter.notifyItemRemoved(pos);
                    if(datos_atajo.size()==0) list_empty.setVisibility(View.VISIBLE);
                    else list_empty.setVisibility(View.GONE);
                }
            }
        });

        if(datos_atajo.size()==0) list_empty.setVisibility(View.VISIBLE);
        else list_empty.setVisibility(View.GONE);



        return v;
    }

    private OnAtajoClickListener onAtajoClickListener;
    public void setOnAtajoClickListener(OnAtajoClickListener onAtajoClickListener) {
        this.onAtajoClickListener = onAtajoClickListener;
    }
    public interface OnAtajoClickListener{
        void OnClick(ItemAtajo atajo);
        void OnAddAtajo();
    }
}
