package cu.alexgi.youchat.BottomSheetDialogFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import cu.alexgi.youchat.PostFragment;
import cu.alexgi.youchat.R;
import cu.alexgi.youchat.Utils;
import cu.alexgi.youchat.YouChatApplication;

import static cu.alexgi.youchat.MainActivity.dbWorker;

public class BottomSheetDialogFragment_opciones_post extends BottomSheetDialogFragment {

    private static PostFragment postFragment;
    private static int cantLimite;

    public static BottomSheetDialogFragment_opciones_post newInstance(PostFragment pf, int cl) {
        postFragment = pf;
        cantLimite = cl;
        return new BottomSheetDialogFragment_opciones_post();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_dialog_opciones_post, container, false);
        TextView tv_limite_post = v.findViewById(R.id.tv_limite_post);
        View efab_post_eliminar_todos = v.findViewById(R.id.efab_post_eliminar_todos);
//        View efab_post_publicar_1 = v.findViewById(R.id.efab_post_publicar_1);

        int cant_seguidores = dbWorker.obtenerCantSeguidores();
        String cad = "Usted es un usuario";
        if(YouChatApplication.comprobarOficialidad(YouChatApplication.correo))
            cad+=" oficial o VIP\n";
        else if(YouChatApplication.es_beta_tester)
            cad+=" BetaTester\n";
        else if(cant_seguidores>=YouChatApplication.usuMayor)
            cad+=" influencer\n";
        else if(cant_seguidores>=YouChatApplication.usuMedio)
            cad+=" micro influencer\n";
        else if(cant_seguidores>=YouChatApplication.usuMenor)
            cad+=" popular\n";
        else cad+=" normal\n";
        int limPost = Utils.calcularCantLimPostXDia(cant_seguidores);
        int cantPtoPost = limPost-YouChatApplication.cantPostSubidosHoy;
        cad+="Puntos para Post: "+cantPtoPost+"/"+limPost;
//        if(cantPtoPost==0) cad+="No tiene puntos para publicar Post de "+limPost;
//        else if(cantPtoPost==1) cad+="Queda 1 punto para publicar Post de "+limPost;
//        else cad+="Quedan "+cantPtoPost+" puntos para publicar Post de "+limPost;

        cad+="\n";

        int limComentarioPost = Utils.calcularCantLimComentarioPostXDia(cant_seguidores);
        int cantPtoComPost = limComentarioPost-YouChatApplication.cantComentarioPostSubidosHoy;
        cad+="Puntos de comentario: "+cantPtoComPost+"/"+limComentarioPost;
//        if(cantPtoComPost==0) cad+="No tiene puntos para comentar Post de "+limComentarioPost;
//        else if(cantPtoComPost==1) cad+="Queda 1 punto para comentar un Post de "+limComentarioPost;
//        else cad+="Quedan "+cantPtoComPost+" puntos para comentar Post de "+limComentarioPost;

        tv_limite_post.setText(cad);

        efab_post_eliminar_todos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                postFragment.eliminarTodosPost();
            }
        });
//        efab_post_publicar_1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dismiss();
////                if(YouChatApplication.cantPostSubidosHoy<cantLimite)
//                    postFragment.crearPostPrueba();
////                else Utils.ShowToastAnimated(mainActivity,"Límite de Post diario alcanzado",R.raw.error);
//            }
//        });
        return v;
    }
}
