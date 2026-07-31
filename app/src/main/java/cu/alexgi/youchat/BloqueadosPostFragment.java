package cu.alexgi.youchat;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import cu.alexgi.youchat.adapters.AdaptadorDatosBloqueadoPost;
import cu.alexgi.youchat.swipebackfragment.BaseSwipeBackFragment;

import static cu.alexgi.youchat.MainActivity.context;
import static cu.alexgi.youchat.MainActivity.dbWorker;

public class BloqueadosPostFragment extends BaseSwipeBackFragment {

    //Para llenar el recycler
    private ArrayList<String> datos_bloqueados;
    private RecyclerView lista_bloqueados;
    private AdaptadorDatosBloqueadoPost adaptadorBloqueados;

    private View list_empty, atras_bloqueados;
    private TextView cant_bloqueados;
    int colorTema;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return attachToSwipeBack(inflater.inflate(R.layout.fragment_bloqueados_post, container, false));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.root).setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_interior()));

        list_empty = view.findViewById(R.id.list_empty);
        atras_bloqueados = view.findViewById(R.id.atras_bloqueados);
        atras_bloqueados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
//                Navigation.findNavController(v).navigateUp();
            }
        });
        cant_bloqueados = view.findViewById(R.id.cant_bloqueados);
        lista_bloqueados = view.findViewById(R.id.lista_bloqueados);
        lista_bloqueados.setLayoutManager(new LinearLayoutManager(context,RecyclerView.VERTICAL, false));
        datos_bloqueados = dbWorker.obtenerBloqueadosPost();
        adaptadorBloqueados = new AdaptadorDatosBloqueadoPost(datos_bloqueados, this);
        lista_bloqueados.setAdapter(adaptadorBloqueados);
        if(datos_bloqueados.size()==0){
            list_empty.setVisibility(View.VISIBLE);
            lista_bloqueados.setVisibility(View.GONE);
        }
        else {
            list_empty.setVisibility(View.GONE);
            lista_bloqueados.setVisibility(View.VISIBLE);
        }
        cant_bloqueados.setText(datos_bloqueados.size()+" en total");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void mostrarDialogoConfirmacion(String nombre, String correo) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(1);
        View mview=getLayoutInflater().inflate(R.layout.dialog_confirm,null);
        dialog.setContentView(mview);

        LinearLayout header=mview.findViewById(R.id.header);
        ImageView icono_eliminar = mview.findViewById(R.id.icono_eliminar);
        TextView text_icono = mview.findViewById(R.id.text_icono);
        TextView text_eliminar = mview.findViewById(R.id.text_eliminar);
        TextView btn_ok=mview.findViewById(R.id.btn_ok);
        View btn_cancel=mview.findViewById(R.id.btn_cancel);

        header.setBackgroundResource(R.color.primary);
        icono_eliminar.setImageResource(R.drawable.option_bloquear);

        text_icono.setText("Desbloquear usuario");
        text_eliminar.setText("¿Quieres desbloquear a "+nombre+"?");

        btn_ok.setText("Aceptar");

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                buscarYborrarA(correo);
                dbWorker.actualizarBloqueadoDe(correo,false);
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

    private synchronized void buscarYborrarA(String correo){
        int l=datos_bloqueados.size();
        for(int i=0; i<l; i++){
            if(datos_bloqueados.get(i).equals(correo)){
                datos_bloqueados.remove(i);
                adaptadorBloqueados.notifyItemRemoved(i);
                break;
            }
        }
        cant_bloqueados.setText(datos_bloqueados.size()+" en total");
        if(datos_bloqueados.size()==0){
            list_empty.setVisibility(View.VISIBLE);
            lista_bloqueados.setVisibility(View.GONE);
        }
    }
}
