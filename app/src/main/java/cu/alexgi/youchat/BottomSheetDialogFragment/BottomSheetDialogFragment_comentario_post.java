package cu.alexgi.youchat.BottomSheetDialogFragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

import cu.alexgi.youchat.R;
import cu.alexgi.youchat.Utils;
import cu.alexgi.youchat.adapters.AdaptadorDatosComentarioPost;
import cu.alexgi.youchat.adapters.AdaptadorDatosEstadoViews;
import cu.alexgi.youchat.adapters.AdaptadorDatosReaccionEstado;
import cu.alexgi.youchat.base_datos.DBWorker;
import cu.alexgi.youchat.items.ItemComentarioPost;
import cu.alexgi.youchat.items.ItemEstado;
import cu.alexgi.youchat.items.ItemReaccionEstado;
import cu.alexgi.youchat.items.ItemVistaEstado;

import static cu.alexgi.youchat.MainActivity.context;

public class BottomSheetDialogFragment_comentario_post extends BottomSheetDialogFragment {

    private static Context context;

    private RecyclerView lista_comentario_post;
    private AdaptadorDatosComentarioPost adaptadorDatosComentarioPost;
    private static ArrayList<ItemComentarioPost> datos_comentario_post;

    public static BottomSheetDialogFragment_comentario_post newInstance(Context c, ArrayList<ItemComentarioPost> list) {
        context = c;
        datos_comentario_post = list;
        return new BottomSheetDialogFragment_comentario_post();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_dialog_comentario_post, container, false);

        lista_comentario_post=v.findViewById(R.id.lista_comentario_post);
        lista_comentario_post.setLayoutManager(new LinearLayoutManager(context));

        adaptadorDatosComentarioPost = new AdaptadorDatosComentarioPost(datos_comentario_post);
        lista_comentario_post.setAdapter(adaptadorDatosComentarioPost);

        adaptadorDatosComentarioPost.setOnItemEventListener(new AdaptadorDatosComentarioPost.OnItemEventListener() {
            @Override
            public void OnClickPhoto(String nombre, String correo) {
                if(onItemClickListener!=null){
                    dismiss();
                    onItemClickListener.abrirPerfil(nombre,correo);
                }
            }
        });

//        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(v);
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        return v;
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void abrirPerfil(String nombre, String correo);
    }
}
