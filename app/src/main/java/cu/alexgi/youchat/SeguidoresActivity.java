package cu.alexgi.youchat;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cu.alexgi.youchat.items.ItemChat;
import cu.alexgi.youchat.items.ItemContacto;
import cu.alexgi.youchat.swipebackfragment.BaseSwipeBackFragment;

import static cu.alexgi.youchat.MainActivity.context;
import static cu.alexgi.youchat.MainActivity.dbWorker;
import static cu.alexgi.youchat.MainActivity.mainActivity;

public class SeguidoresActivity extends BaseSwipeBackFragment {

    private ArrayList<ItemContacto> datos_seguidores, datos_siguiendo;
    private View icon_letterSocial, icon_compartir;
    private SeguidoresActivity seguidoresActivity;

    private SeguidosFragment seguidosFragment;

    public static final int NUM_PAGES = 2;
    private static ViewPager2 pager;
    private FragmentStateAdapter pagerAdapter;
    private static TabLayout tab;

    public void abrirPerfil(Bundle mibundle) {
        if(mAddFragmentListener!=null)
            mAddFragmentListener.onAddFragment(SeguidoresActivity.this, ViewPerfilActivity.newInstance(mibundle));
    }

    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter() {
            super(SeguidoresActivity.this);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) return SeguidoresFragment.newInstance(datos_seguidores, SeguidoresActivity.this);
            else {
                return seguidosFragment;
            }
        }

        @Override
        public int getItemCount() {
            if(!YouChatApplication.activePost) return 2;
            return NUM_PAGES;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return attachToSwipeBack(inflater.inflate(R.layout.activity_seguidores, container, false));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        seguidoresActivity = this;
//        navController = Navigation.findNavController(view);
        view.findViewById(R.id.root).setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_fondo()));
        view.findViewById(R.id.atras_seguidores).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.atrasFragment();
            }
        });

        icon_letterSocial = view.findViewById(R.id.icon_letterSocial);
        if(YouChatApplication.correo.equals("djbola@nauta.cu") || YouChatApplication.correo.equals("alexgi@nauta.cu") ||
                YouChatApplication.correo.equals("octaviog97@nauta.cu") ||
                YouChatApplication.correo.equals("niuvis2019@nauta.cu")){
            icon_letterSocial.setVisibility(View.VISIBLE);
            icon_letterSocial.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<String> listaSeguidores = dbWorker.obtenerTodosSeguidores();
                    ArrayList<String> listaSeguidos = dbWorker.obtenerTodosSiguiendoA();
                    String cad = "";
                    int l1 = listaSeguidores.size();
                    int l2 = listaSeguidos.size();
                    for(int i=0; i<l1; i++){
                        String c1 = listaSeguidores.get(i);
                        for(int j=0; j<l2; j++){
                            if(c1.equals(listaSeguidos.get(j))){
                                if(!cad.equals("")) cad+=",";
                                cad+=c1;
                                break;
                            }
                        }
                    }
                    if(!cad.equals("")){
                        String packApk = "com.tecnonew365.lettersocial";
                        String action = "https://lettersocial.youchat.cu/enlace";
                        if(comprobarQueEstaInstalado(packApk,action)){
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(action));
                            Bundle bundle = new Bundle();
                            bundle.putString("lista_usuarios",cad);
                            intent.putExtras(bundle);
                            intent.setPackage(packApk);
                            startActivity(intent);
                        }
                        else
                            Toast.makeText(context, "No tiene esa apk instalada o no es compatible", Toast.LENGTH_SHORT).show();
                    }
                    else Toast.makeText(context, "Debe de poseer al menos un amigo", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else icon_letterSocial.setVisibility(View.GONE);
        icon_compartir = view.findViewById(R.id.icon_compartir);

        ArrayList<String> seguidores = dbWorker.obtenerTodosSeguidoresOrdenadosCorreo();
        ArrayList<String> siguiendo = dbWorker.obtenerTodosSiguiendoA();
        int l = seguidores.size();
        int l1 = siguiendo.size();
        datos_seguidores = new ArrayList<>();
        datos_siguiendo = new ArrayList<>();

        for(int i=0; i<l; i++){
            String correo = seguidores.get(i);
            ItemContacto contacto = dbWorker.obtenerContacto(correo);
            if(contacto==null){
                contacto = new ItemContacto(correo, correo);
            }
            datos_seguidores.add(contacto);
        }
        for(int i=0; i<l1; i++){
            String correo = siguiendo.get(i);
            ItemContacto contacto = dbWorker.obtenerContacto(correo);
            if(contacto==null){
                contacto = new ItemContacto(correo, correo);
            }
            datos_siguiendo.add(contacto);
        }


        icon_compartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pager.getCurrentItem()==0){
                    int longi = datos_seguidores.size();
                    if(longi>0){
                        if(mAddFragmentListener!=null){
                            String texto_enviar="Mis seguidores:";
                            for(int i=0; i<longi; i++){
                                texto_enviar+="\n"+datos_seguidores.get(i).getCorreo();
                            }
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
                            Date date = new Date();
                            String fechaEntera = sdf.format(date);

                            String id="YCchaLista"+fechaEntera;
                            String hora = Convertidor.conversionHora(fechaEntera);
                            String fecha = Convertidor.conversionFecha(fechaEntera);
                            ItemChat chatLista=new ItemChat( id,
                                    2, 3, YouChatApplication.correo,
                                    texto_enviar,
                                    "",
                                    hora, fecha, "",
                                    YouChatApplication.correo, false, fechaEntera,false
                                    ,"",0,true);
                            mAddFragmentListener.onAddFragment(SeguidoresActivity.this, ReenviarActivity.newInstance(chatLista,false));
                        }
                    } else Utils.ShowToastAnimated(mainActivity,"Debe de tener al menos un seguidor",R.raw.error);
                }
                else {
                    int longi = datos_siguiendo.size();
                    if(longi>0){
                        if(mAddFragmentListener!=null){
                            String texto_enviar="Sigo a:";
                            for(int i=0; i<longi; i++){
                                texto_enviar+="\n"+datos_siguiendo.get(i).getCorreo();
                            }
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
                            Date date = new Date();
                            String fechaEntera = sdf.format(date);

                            String id="YCchaLista"+fechaEntera;
                            String hora = Convertidor.conversionHora(fechaEntera);
                            String fecha = Convertidor.conversionFecha(fechaEntera);
                            ItemChat chatLista=new ItemChat( id,
                                    2, 3, YouChatApplication.correo,
                                    texto_enviar,
                                    "",
                                    hora, fecha, "",
                                    YouChatApplication.correo, false, fechaEntera,false
                                    ,"",0,true);
                            mAddFragmentListener.onAddFragment(SeguidoresActivity.this, ReenviarActivity.newInstance(chatLista,false));
                        }
                    } else Utils.ShowToastAnimated(mainActivity,"Debe de seguir al menos a un usuario",R.raw.error);
                }

            }
        });

        seguidosFragment = SeguidosFragment.newInstance(datos_siguiendo, SeguidoresActivity.this);
        pager = view.findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter();
        pager.setAdapter(pagerAdapter);
        tab = view.findViewById(R.id.tab);
        actualizarTab();
    }

    public void actualizarTab(){
        new TabLayoutMediator(tab, pager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if (position == 0) {
                    tab.setText("Seguidores "+datos_seguidores.size());
                }
                else{
                    tab.setText("Seguidos "+datos_siguiendo.size());
                }
            }
        }).attach();
    }

    public boolean comprobarQueEstaInstalado(String packApk, String action){

        Intent youchat = new Intent(Intent.ACTION_VIEW, Uri.parse(action));
        boolean tieneYC = false;
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(youchat, 0);
        if (!resInfo.isEmpty()) {
            for (ResolveInfo info : resInfo) {
                if (info.activityInfo.packageName != null){
                    String pack = info.activityInfo.packageName;
                    pack=pack.trim();
                    if(pack.equals(packApk)){
                        tieneYC = true;
                        break;
                    }
                }
            }
        }
        return tieneYC;
    }

    public void eliminarSiguiendoA(String correo){
        if(seguidosFragment!=null)
            seguidosFragment.eliminarSiguiendoA(correo);
    }
}
