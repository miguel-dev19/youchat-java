package cu.alexgi.youchat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.api.widget.Widget;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import cu.alexgi.youchat.adapters.AdaptadorDatosFondo;
import cu.alexgi.youchat.items.ItemFondo;
import cu.alexgi.youchat.photoutil.GalleryPhoto;
import cu.alexgi.youchat.swipebackfragment.BaseSwipeBackFragment;

import static cu.alexgi.youchat.MainActivity.context;
import static cu.alexgi.youchat.MainActivity.mainActivity;
import static cu.alexgi.youchat.MainActivity.permisos;

public class FondoActivity extends BaseSwipeBackFragment {

    private GalleryPhoto galleryPhoto;
    private View efab_abrirGaleria;
    private static ArrayList<ItemFondo> datos_fondo_imagen;
    private static RecyclerView lista_fondo;
    private static AdaptadorDatosFondo adaptadorFondo;
    private String ruta_fondo_chat;
    private int ruta_drawable,cant;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return attachToSwipeBack(inflater.inflate(R.layout.activity_fondo, container, false));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cant=8;

        galleryPhoto = new GalleryPhoto(context);
        lista_fondo = view.findViewById(R.id.recycler_fondo);
        efab_abrirGaleria = view.findViewById(R.id.efab_abrirGaleria);

        view.findViewById(R.id.ir_atras).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
//                Navigation.findNavController(v).navigateUp();
            }
        });

        lista_fondo.setLayoutManager(new GridLayoutManager(context, 3));
        datos_fondo_imagen = new ArrayList<>();

        ruta_fondo_chat = YouChatApplication.ruta_fondo_chat;
        if(YouChatApplication.ruta_drawable>8) YouChatApplication.setRuta_fondo(0);
        ruta_drawable = YouChatApplication.ruta_drawable;

        llenarListaImagenes();
        if(ruta_drawable!=-1)
            datos_fondo_imagen.get(ruta_drawable).setEstaSeleccionado(true);

        adaptadorFondo = new AdaptadorDatosFondo(datos_fondo_imagen);
        lista_fondo.setAdapter(adaptadorFondo);
        adaptadorFondo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int posk = lista_fondo.getChildAdapterPosition(v);
                if(posk!=-1){
                    if(posk<cant){
                        datos_fondo_imagen.get(posk).setEstaSeleccionado(true);
                        ruta_drawable=posk;
                        actualizarFondo(posk);
                        YouChatApplication.setRuta_fondo(ruta_drawable);
                    }
                    else {
                        ruta_drawable=-1;
                        YouChatApplication.setRuta_fondo(datos_fondo_imagen.get(posk).getRuta());
                        actualizarFondo(posk);
                    }
                }
            }
        });

        efab_abrirGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirGaleria();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void llenarListaImagenes() {
        for(int i=0; i<cant; i++){
            datos_fondo_imagen.add(new ItemFondo( false, true,
                    i, ""));
        }
        boolean encontrado = false;
        if(permisos.requestPermissionAlmacenamiento()){
            File root = new File(YouChatApplication.RUTA_FONDO_YOUCHAT);
            if(root.exists()){
                File[] listaFondo = root.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return(name.endsWith(".jpg"));
                    }
                });
                int l = listaFondo.length;
                if(l>0){
                    for(int i=0; i<l; i++){
                        datos_fondo_imagen.add(new ItemFondo(false,
                                false, -1, listaFondo[i].getPath()));
                        if(ruta_drawable==-1 && !encontrado){
                            if(ruta_fondo_chat.equals(listaFondo[i].getPath())){
                                encontrado = true;
                                datos_fondo_imagen.get(cant+i).setEstaSeleccionado(true);
                            }
                        }
                    }
                }
            }
            if(ruta_drawable==-1 && !encontrado){
                if(new File(ruta_fondo_chat).exists()){
                    datos_fondo_imagen.add(new ItemFondo(true, false, -1, ruta_fondo_chat));
                }
            }
        }
    }

    /////////////////////////////ABRIR IMAGEN///////////////////////////////////////////////

    public void abrirGaleria() {
        if(!permisos.requestPermissionAlmacenamiento()) return;
        selectImage();
    }

    private void selectImage() {
        Album.image(this)
                .singleChoice()
                .camera(false)
                .columnCount(3)
                .widget(
                        Widget.newDarkBuilder(context)
                                .title("Seleccione un fondo")
                                .build()
                )
                .onResult(new Action<ArrayList<AlbumFile>>() {
                    @Override
                    public void onAction(@NonNull ArrayList<AlbumFile> result) {
                        if(result!=null && result.size()>0){
                            guardarImgAGaleria(result.get(0).getPath());
                        }
                        else Utils.ShowToastAnimated(mainActivity,"Error al cargar la imagen",R.raw.error);

                    }
                })
                .start();
    }

    private void guardarImgAGaleria(String path) {

        ruta_fondo_chat = path;

        int pos = datos_fondo_imagen.size();
        datos_fondo_imagen.add(new ItemFondo(false, false, -1, ruta_fondo_chat));
        adaptadorFondo.notifyItemInserted(pos);
        ruta_drawable=-1;
        YouChatApplication.setRuta_fondo(ruta_fondo_chat);
        actualizarFondo(pos);
    }

    private synchronized void actualizarFondo(int pos) {
        int l = datos_fondo_imagen.size();
        for (int i=0; i<l; i++){
            if(i!=pos && datos_fondo_imagen.get(i).isEstaSeleccionado()){
                datos_fondo_imagen.get(i).setEstaSeleccionado(false);
                adaptadorFondo.notifyItemChanged(i);
            }
        }
        datos_fondo_imagen.get(pos).setEstaSeleccionado(true);
        adaptadorFondo.notifyItemChanged(pos);

        Utils.resetearImageFondoBitmap(context);
    }

    /////////////////////////////CERRAR IMAGEN///////////////////////////////////////////////
}
