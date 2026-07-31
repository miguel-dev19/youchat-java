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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

import cu.alexgi.youchat.R;
import cu.alexgi.youchat.Utils;
import cu.alexgi.youchat.adapters.AdaptadorDatosEstadoViews;
import cu.alexgi.youchat.adapters.AdaptadorDatosReaccionEstado;
import cu.alexgi.youchat.base_datos.DBWorker;
import cu.alexgi.youchat.items.ItemEstado;
import cu.alexgi.youchat.items.ItemReaccionEstado;
import cu.alexgi.youchat.items.ItemVistaEstado;

public class BottomSheetDialogFragment_reacciones_estado extends BottomSheetDialogFragment {

    private static ItemEstado estado;
    private static Context context;
    private static DBWorker dbWorker;

    private RecyclerView lista_info_reacciones;
    private AdaptadorDatosReaccionEstado mAdapter;
    private AdaptadorDatosEstadoViews adaptadorDatosUsuarioView;
    private ArrayList<ItemReaccionEstado> datos_reacciones_estados;
    private ArrayList<ItemVistaEstado> datos_vistas_estados;
    private static int tipo;

    public static BottomSheetDialogFragment_reacciones_estado newInstance(Context c, ItemEstado e, int x) {
        dbWorker =  new DBWorker(c);
        context = c;
        estado = e;
        tipo=x;
        return new BottomSheetDialogFragment_reacciones_estado();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_dialog_reacciones_estado, container, false);
        if(tipo==0)
        {
            TextView tv_cant_me_gusta_bs = v.findViewById(R.id.tv_cant_me_gusta_bs);
            TextView tv_cant_me_encanta_bs = v.findViewById(R.id.tv_cant_me_encanta_bs);
            TextView tv_cant_me_sonroja_bs = v.findViewById(R.id.tv_cant_me_sonroja_bs);
            TextView tv_cant_me_divierte_bs = v.findViewById(R.id.tv_cant_me_divierte_bs);
            TextView tv_cant_me_asombra_bs = v.findViewById(R.id.tv_cant_me_asombra_bs);
            TextView tv_cant_me_entristese_bs = v.findViewById(R.id.tv_cant_me_entristese_bs);
            TextView tv_cant_me_enoja_bs = v.findViewById(R.id.tv_cant_me_enoja_bs);

            tv_cant_me_gusta_bs.setText(""+estado.getCant_me_gusta());
            tv_cant_me_encanta_bs.setText(""+estado.getCant_me_encanta());
            tv_cant_me_sonroja_bs.setText(""+estado.getCant_me_sonroja());
            tv_cant_me_divierte_bs.setText(""+estado.getCant_me_divierte());
            tv_cant_me_asombra_bs.setText(""+estado.getCant_me_asombra());
            tv_cant_me_entristese_bs.setText(""+estado.getCant_me_entristese());
            tv_cant_me_enoja_bs.setText(""+estado.getCant_me_enoja());

            lista_info_reacciones=v.findViewById(R.id.lista_info_reacciones);

            lista_info_reacciones.setLayoutManager(new LinearLayoutManager(context,RecyclerView.VERTICAL,false));
            datos_reacciones_estados = dbWorker.obtenerReaccionesEstadosDelEstado(estado.getId());
            mAdapter = new AdaptadorDatosReaccionEstado(context,datos_reacciones_estados);
            lista_info_reacciones.setAdapter(mAdapter);

            //animaciones
            LottieAnimationView anim_emoji_me_gusta, anim_emoji_me_encanta, anim_emoji_me_sonroja,
                    anim_emoji_me_divierte, anim_emoji_me_asombra, anim_emoji_me_entristese,
                    anim_emoji_me_enoja;

            anim_emoji_me_gusta = v.findViewById(R.id.anim_emoji_me_gusta);
            anim_emoji_me_encanta = v.findViewById(R.id.anim_emoji_me_encanta);
            anim_emoji_me_sonroja = v.findViewById(R.id.anim_emoji_me_sonroja);
            anim_emoji_me_divierte = v.findViewById(R.id.anim_emoji_me_divierte);
            anim_emoji_me_asombra = v.findViewById(R.id.anim_emoji_me_asombra);
            anim_emoji_me_entristese = v.findViewById(R.id.anim_emoji_me_entristese);
            anim_emoji_me_enoja = v.findViewById(R.id.anim_emoji_me_enoja);

            anim_emoji_me_gusta.setAnimation(R.raw.like1);
            anim_emoji_me_encanta.setAnimation(R.raw.encanta);
            anim_emoji_me_sonroja.setAnimation(R.raw.sonroja);
            anim_emoji_me_divierte.setAnimation(R.raw.divierte);
            anim_emoji_me_asombra.setAnimation(R.raw.asombra);
            anim_emoji_me_entristese.setAnimation(R.raw.entristece);
            anim_emoji_me_enoja.setAnimation(R.raw.enoja);

            ///sonidos
            View reproducirSonidoMeGusta, reproducirSonidoMeEncanta, reproducirSonidoMeSonroja,
                    reproducirSonidoMeDivierte, reproducirSonidoMeAsombra, reproducirSonidoMeEntristese,
                    reproducirSonidoMeEnoja;

            reproducirSonidoMeGusta = v.findViewById(R.id.reproducirSonidoMeGusta);
            reproducirSonidoMeEncanta = v.findViewById(R.id.reproducirSonidoMeEncanta);
            reproducirSonidoMeSonroja = v.findViewById(R.id.reproducirSonidoMeSonroja);
            reproducirSonidoMeDivierte = v.findViewById(R.id.reproducirSonidoMeDivierte);
            reproducirSonidoMeAsombra = v.findViewById(R.id.reproducirSonidoMeAsombra);
            reproducirSonidoMeEntristese = v.findViewById(R.id.reproducirSonidoMeEntristese);
            reproducirSonidoMeEnoja = v.findViewById(R.id.reproducirSonidoMeEnoja);

            reproducirSonidoMeGusta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.reproducirSonido(R.raw.like, context);
                }
            });

            reproducirSonidoMeEncanta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.reproducirSonido(R.raw.like, context);
                }
            });

            reproducirSonidoMeSonroja.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.reproducirSonido(R.raw.like, context);
                }
            });

            reproducirSonidoMeDivierte.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.reproducirSonido(R.raw.risita, context);
                }
            });

            reproducirSonidoMeAsombra.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.reproducirSonido(R.raw.wow, context);
                }
            });

            reproducirSonidoMeEntristese.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.reproducirSonido(R.raw.triston, context);
                }
            });

            reproducirSonidoMeEnoja.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.reproducirSonido(R.raw.like, context);
                }
            });
        }
        else
        {
            v.findViewById(R.id.ll_reacc).setVisibility(View.GONE);
            v.findViewById(R.id.view).setVisibility(View.GONE);
            v.findViewById(R.id.tv_visto_por).setVisibility(View.VISIBLE);

            lista_info_reacciones=v.findViewById(R.id.lista_info_reacciones);
            lista_info_reacciones.setLayoutManager(new LinearLayoutManager(context,RecyclerView.VERTICAL,true));

            datos_vistas_estados = dbWorker.obtenerVistasEstadosDe(estado.getId());//.obtenerReaccionesEstadosDelEstado(estado.getId());
            Log.e("vistas",datos_vistas_estados.size()+"");
            adaptadorDatosUsuarioView = new AdaptadorDatosEstadoViews(context,datos_vistas_estados);
            lista_info_reacciones.setAdapter(adaptadorDatosUsuarioView);
        }
        return v;
    }
}
