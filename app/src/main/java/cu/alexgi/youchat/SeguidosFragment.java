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

import java.util.ArrayList;

import cu.alexgi.youchat.adapters.AdaptadorDatosSeguido;
import cu.alexgi.youchat.items.ItemContacto;

import static cu.alexgi.youchat.MainActivity.context;

public class SeguidosFragment extends Fragment {

    private RecyclerView listaDatos;
    private View tv_empty;
    private static SeguidoresActivity seguidoresActivity;
    private AdaptadorDatosSeguido adaptadorDatosSeguidor;
    private static ArrayList<ItemContacto> datos;


    public SeguidosFragment() {
        // Required empty public constructor
    }

    public static SeguidosFragment newInstance(ArrayList<ItemContacto> x, SeguidoresActivity s) {
        SeguidosFragment fragment = new SeguidosFragment();
        datos = x;
        seguidoresActivity = s;
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
        tv_empty = view.findViewById(R.id.tv_empty);

        if(datos.size()>0){
            adaptadorDatosSeguidor = new AdaptadorDatosSeguido(datos, this);
            listaDatos.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
            listaDatos.setAdapter(adaptadorDatosSeguidor);
            listaDatos.setHasFixedSize(true);
        }
        else{
            listaDatos.setVisibility(View.GONE);
            tv_empty.setVisibility(View.VISIBLE);
        }
    }

    public void irPerfil(String nombreMostrar, String correo) {
        Bundle mibundle=new Bundle();
        mibundle.putString("usuario",nombreMostrar);
        mibundle.putString("correo",correo);
        mibundle.putBoolean("vieneDeChat", false);
        seguidoresActivity.abrirPerfil(mibundle);
    }

    public void actualizarBadge() {
        seguidoresActivity.actualizarTab();
    }

    public void eliminarSiguiendoA(String correo) {
        int pos = buscarId(correo);
        if(pos!=-1){
            datos.remove(pos);
            if(adaptadorDatosSeguidor!=null)
                adaptadorDatosSeguidor.notifyItemRemoved(pos);
            seguidoresActivity.actualizarTab();
        }
    }

    private int buscarId(String correo){
        int l = datos.size();
        for(int i=0; i<l; i++){
            if(datos.get(i).getCorreo().equals(correo)){
                return i;
            }
        }
        return -1;
    }
}