package cu.alexgi.youchat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

import cu.alexgi.youchat.BottomSheetDialogFragment.BottomSheetDialogFragment_User_Estadisticas;
import cu.alexgi.youchat.adapters.AdaptadorDatosUsuarioEstadisticas;
import cu.alexgi.youchat.items.ItemEstadisticaPersonal;

public class EstadisticasPersonalesFragment extends Fragment {

    private RecyclerView listaDatos;
    private AdaptadorDatosUsuarioEstadisticas adaptadorDatosUsuarioEstadisticas;
    private static ArrayList<ItemEstadisticaPersonal> datos_personales;


    public EstadisticasPersonalesFragment() {
        // Required empty public constructor
    }

    public static EstadisticasPersonalesFragment newInstance(ArrayList<ItemEstadisticaPersonal> x) {
        EstadisticasPersonalesFragment fragment = new EstadisticasPersonalesFragment();
        datos_personales = x;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_estadisticas_personales, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listaDatos = view.findViewById(R.id.listaDatos);

        if(datos_personales.size()>0){
            adaptadorDatosUsuarioEstadisticas = new AdaptadorDatosUsuarioEstadisticas(MainActivity.context, datos_personales, EstadisticasPersonalesFragment.this);
            listaDatos.setLayoutManager(new LinearLayoutManager(MainActivity.context, RecyclerView.VERTICAL, false));
            listaDatos.setAdapter(adaptadorDatosUsuarioEstadisticas);
            listaDatos.setHasFixedSize(true);
        }
        else{
            listaDatos.setVisibility(View.GONE);
            view.findViewById(R.id.tv_empty).setVisibility(View.VISIBLE);
        }
    }

    public void showBSD(String nombre, String ruta, ItemEstadisticaPersonal itemEstadisticaPersonal){
        BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_User_Estadisticas
                .newInstance(MainActivity.context, nombre, ruta, itemEstadisticaPersonal);
        bsdFragment.show(getChildFragmentManager(), "BSDialog");
    }
}