package cu.alexgi.youchat;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import cu.alexgi.youchat.Popups.PopupOpcionesSeguidor;
import cu.alexgi.youchat.adapters.AdaptadorDatosSeguidor;
import cu.alexgi.youchat.items.ItemContacto;

import static cu.alexgi.youchat.MainActivity.context;
import static cu.alexgi.youchat.MainActivity.dbWorker;

public class SeguidoresFragment extends Fragment {

    private RecyclerView listaDatos;
    private View tv_empty;
    private static SeguidoresActivity seguidoresActivity;
    private AdaptadorDatosSeguidor adaptadorDatosSeguidor;
    private static ArrayList<ItemContacto> datos;


    public SeguidoresFragment() {
        // Required empty public constructor
    }

    public static SeguidoresFragment newInstance(ArrayList<ItemContacto> x, SeguidoresActivity s) {
        SeguidoresFragment fragment = new SeguidoresFragment();
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
            adaptadorDatosSeguidor = new AdaptadorDatosSeguidor(datos, this);
            listaDatos.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
            listaDatos.setAdapter(adaptadorDatosSeguidor);
            listaDatos.setHasFixedSize(true);
        }
        else{
            listaDatos.setVisibility(View.GONE);
            tv_empty.setVisibility(View.VISIBLE);
        }
    }

    public void sacarPopupSeguidosFragment(View v, String correo) {
        PopupOpcionesSeguidor popupOpcionesSeguidor = new PopupOpcionesSeguidor(v, this);
        popupOpcionesSeguidor.show(v, correo);
    }

    public synchronized void eliminarSeguidor(String correo){
        int pos = buscarSeguidorPos(correo);
        if(pos!=-1){
            Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(1);
            View mview=getLayoutInflater().inflate(R.layout.dialog_confirm,null);
            dialog.setContentView(mview);

            TextView text_icono = mview.findViewById(R.id.text_icono);
            TextView text_eliminar = mview.findViewById(R.id.text_eliminar);
            View btn_cancel=mview.findViewById(R.id.btn_cancel);
            View btn_ok=mview.findViewById(R.id.btn_ok);

            text_icono.setText("Eliminar seguidor");
            text_eliminar.setText("¿Deseas eliminar a este seguidor? Si lo haces, no será notificado " +
                    "ni tampoco recibirá más estados tuyos.");

            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    dbWorker.eliminarSeguidor(correo);
                    datos.remove(pos);
                    adaptadorDatosSeguidor.notifyItemRemoved(pos);
                    if(datos.size()==0){
                        listaDatos.setVisibility(View.GONE);
                        tv_empty.setVisibility(View.VISIBLE);
                    }
                    seguidoresActivity.actualizarTab();
                }
            });
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.setCancelable(true);
            dialog.show();
        }
    }

    private int buscarSeguidorPos(String correo){
        int l=datos.size();
        for(int i=0; i<l; i++){
            if(datos.get(i).getCorreo().equals(correo))
                return i;
        }
        return -1;
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

    public void eliminarSiguiendoA(String correo){
        seguidoresActivity.eliminarSiguiendoA(correo);
    }
}