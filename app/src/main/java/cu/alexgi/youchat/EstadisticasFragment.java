package cu.alexgi.youchat;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

import cu.alexgi.youchat.items.ItemEstadisticaPersonal;
import cu.alexgi.youchat.swipebackfragment.BaseSwipeBackFragment;

import static cu.alexgi.youchat.MainActivity.context;
import static cu.alexgi.youchat.MainActivity.dbWorker;
import static cu.alexgi.youchat.MainActivity.permisos;

public class EstadisticasFragment extends BaseSwipeBackFragment {

    public static final int NUM_PAGES = 2;
    private ViewPager2 viewpager;
    private FragmentStateAdapter pagerAdapter;
    private static TabLayout tab;
    private ArrayList<ItemEstadisticaPersonal> datos_personales;
    private View iv_resetear_estadisticas, iv_compartir_estadisticas;

    private EstadisticasGeneralesFragment estadisticasGeneralesFragment;

    public EstadisticasFragment() {
        // Required empty public constructor
    }

    public static EstadisticasFragment newInstance(String param1, String param2) {
        EstadisticasFragment fragment = new EstadisticasFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return attachToSwipeBack(inflater.inflate(R.layout.fragment_estadisticas, container, false));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.root).setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_interior()));

        viewpager = view.findViewById(R.id.viewpager);
        iv_resetear_estadisticas = view.findViewById(R.id.iv_resetear_estadisticas);
        iv_compartir_estadisticas = view.findViewById(R.id.iv_compartir_estadisticas);
        tab = view.findViewById(R.id.tab);
        View atras = view.findViewById(R.id.atras);
        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
//                Navigation.findNavController(v).navigateUp();
            }
        });

        iv_compartir_estadisticas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(permisos.requestPermissionAlmacenamiento()){
                    estadisticasGeneralesFragment.tomarCapturaEstadisticasGral();
                }
            }
        });

        actualizarVista();

        iv_resetear_estadisticas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);

                Dialog dialogo = new Dialog(context);
                dialogo.requestWindowFeature(1);
                View mview = getLayoutInflater().inflate(R.layout.dialog_confirm, null);
                dialogo.setContentView(mview);

                TextView text_icono = mview.findViewById(R.id.text_icono);
                text_icono.setText("Restablecer valores");

                TextView text_eliminar = mview.findViewById(R.id.text_eliminar);
                text_eliminar.setText("¿Deseas restablecer todos los datos desde cero?");

                View btn_cancel =mview.findViewById(R.id.btn_cancel);
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogo.dismiss();
                    }
                });
                View btn_ok =mview.findViewById(R.id.btn_ok);
                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        YouChatApplication.resetearEstadisticasGral();
                        dbWorker.resetearEstadisticasPersonales();
                        actualizarVista();
                        dialogo.dismiss();
                    }
                });

                dialogo.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                dialogo.setCancelable(true);
                dialogo.show();
                v.setEnabled(true);
            }
        });
    }

    private void actualizarVista() {
        datos_personales = new ArrayList<>();
        datos_personales = dbWorker.obtenerTodasEstadisticasPersonales();

        estadisticasGeneralesFragment = EstadisticasGeneralesFragment.newInstance(datos_personales);

        pagerAdapter = new ScreenSlidePagerAdapter();
        viewpager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tab, viewpager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                int tema = YouChatApplication.temaApp;
                if (position == 0) {
//                    if (tema == 1) tab.setIcon(R.drawable.tab_chats_white);
//                    else tab.setIcon(R.drawable.tab_chats);
                    tab.setIcon(R.drawable.chart);
                    tab.setText("General");
                }
                else if (position == 1){
//                    if (tema == 1) tab.setIcon(R.drawable.activity_white);
//                    else tab.setIcon(R.drawable.activity);
                    tab.setIcon(R.drawable.profile);
                    tab.setText("Personal");
                }
            }
        }).attach();
    }

    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter() {
            super(EstadisticasFragment.this);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) return estadisticasGeneralesFragment;
            return EstadisticasPersonalesFragment.newInstance(datos_personales);
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }
}