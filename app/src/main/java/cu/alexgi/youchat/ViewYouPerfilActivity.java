package cu.alexgi.youchat;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.vanniktech.emoji.EmojiTextView;
import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.api.widget.Widget;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import cu.alexgi.youchat.BottomSheetDialogFragment.BottomSheetDialogFragment_Photo_perfil;
import cu.alexgi.youchat.items.ItemContacto;
import cu.alexgi.youchat.items.ItemImg;
import cu.alexgi.youchat.photoView.photoViewLibrary.Info;
import cu.alexgi.youchat.photoView.photoViewLibrary.PhotoView;
import cu.alexgi.youchat.photoutil.CameraPhoto;
import cu.alexgi.youchat.photoutil.ImageLoader;
import cu.alexgi.youchat.swipebackfragment.BaseSwipeBackFragment;

import static android.app.Activity.RESULT_OK;
import static cu.alexgi.youchat.MainActivity.context;
import static cu.alexgi.youchat.MainActivity.dbWorker;
import static cu.alexgi.youchat.MainActivity.mainActivity;
import static cu.alexgi.youchat.MainActivity.permisos;

public class ViewYouPerfilActivity extends BaseSwipeBackFragment {

    private CameraPhoto cameraPhoto;
    private String miPathCamera;
//    private NavController navController;

    private View fab_camera, ll_alias, ll_info, ll_phone, ll_genero, ll_calendario, ll_provincia;
    private ImageView back;
    private static ImageView imagen_perfil;
    private static EmojiTextView text_alias, text_info;
    private static TextView text_correo, text_telefono, text_genero, text_provincia, text_fecha_nacimiento, text_edad;
    private static String alias, info, correo, telefono, genero, provincia, fecha_nacimiento;
    private static String ruta_img_perfil ;
    private int calidad;

    //img linda
    private View frameLayout_visorImg_perfil;
    private PhotoView photoView_visorImg_perfil;
    private Info info_photoView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return attachToSwipeBack(inflater.inflate(R.layout.activity_view_you_perfil, container, false));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.root).setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_fondo()));
        miPathCamera = "";
        back=view.findViewById(R.id.btn_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                atras();
            }
        });
        cameraPhoto = new CameraPhoto(context);
        calidad=YouChatApplication.calidad;
//        navController = Navigation.findNavController(view);

        text_alias = view.findViewById(R.id.text_alias);
        text_info = view.findViewById(R.id.text_info);
        text_correo = view.findViewById(R.id.text_correo);
        text_telefono = view.findViewById(R.id.text_telefono);
        text_genero = view.findViewById(R.id.text_genero);
        text_provincia = view.findViewById(R.id.text_provincia);
        imagen_perfil = view.findViewById(R.id.imagen_perfil);
        text_fecha_nacimiento = view.findViewById(R.id.text_calendario);
        frameLayout_visorImg_perfil = view.findViewById(R.id.frameLayout_visorImg_perfil);
        photoView_visorImg_perfil = view.findViewById(R.id.photoView_visorImg_perfil);

        photoView_visorImg_perfil.enable();
        photoView_visorImg_perfil.enableRotate();
        photoView_visorImg_perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocultarImagen();
            }
        });
        ll_alias = view.findViewById(R.id.ll_alias);
        ll_alias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("key","alias");
                if(mAddFragmentListener!=null) mAddFragmentListener.onAddFragment(ViewYouPerfilActivity.this, EditPerfilActivity.newInstance(bundle));
//                navController.navigate(R.id.editPerfilActivity, bundle);
//                startActivityForResult(trans,1);
            }
        });
        ll_info = view.findViewById(R.id.ll_info);
        ll_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("key","info");
                if(mAddFragmentListener!=null) mAddFragmentListener.onAddFragment(ViewYouPerfilActivity.this, EditPerfilActivity.newInstance(bundle));
//                navController.navigate(R.id.editPerfilActivity, bundle);
//                startActivityForResult(trans,1);
            }
        });
        ll_phone = view.findViewById(R.id.ll_phone);
        ll_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("key","telefono");
                if(mAddFragmentListener!=null) mAddFragmentListener.onAddFragment(ViewYouPerfilActivity.this, EditPerfilActivity.newInstance(bundle));
//                navController.navigate(R.id.editPerfilActivity, bundle);
//                startActivityForResult(trans,1);
            }
        });
        ll_genero = view.findViewById(R.id.ll_genero);
        ll_genero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("key","genero");
                if(mAddFragmentListener!=null) mAddFragmentListener.onAddFragment(ViewYouPerfilActivity.this, EditPerfilActivity.newInstance(bundle));
//                navController.navigate(R.id.editPerfilActivity, bundle);
//                startActivityForResult(trans,1);
            }
        });
        ll_calendario = view.findViewById(R.id.ll_calendario);
        ll_calendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("key","fecha_nacimiento");
                if(mAddFragmentListener!=null) mAddFragmentListener.onAddFragment(ViewYouPerfilActivity.this, EditPerfilActivity.newInstance(bundle));
//                navController.navigate(R.id.editPerfilActivity, bundle);
//                startActivityForResult(trans,1);
            }
        });
        ll_provincia = view.findViewById(R.id.ll_provincia);
        ll_provincia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("key","provincia");
                if(mAddFragmentListener!=null) mAddFragmentListener.onAddFragment(ViewYouPerfilActivity.this, EditPerfilActivity.newInstance(bundle));
//                navController.navigate(R.id.editPerfilActivity, bundle);
//                startActivityForResult(trans,1);
            }
        });
        fab_camera = view.findViewById(R.id.fab_camera);
        fab_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Photo_perfil.newInstance(ViewYouPerfilActivity.this);
                bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogPhotoPerfil");
            }
        });

        cargarPreferenciaImgPerfil();
        ActualizarInfo();



        imagen_perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ruta_img_perfil.equals("")) previewImage();
            }
        });
        YouChatApplication.viewYouPerfilActivity = this;
    }

    private void ocultarImagen() {
        photoView_visorImg_perfil.animaTo(info_photoView, new Runnable() {
            @Override
            public void run() {
                imagen_perfil.setVisibility(View.VISIBLE);
                frameLayout_visorImg_perfil.setVisibility(View.GONE);
            }
        });
    }

    public void actualizarImagenPerfil(ArrayList<ItemImg> itemImgs){
        if(itemImgs!=null){
            guardarPreferenciaImgPerfil(itemImgs.get(0).getRuta());
            Glide.with(this).load(itemImgs.get(0).getRuta())
                    .error(R.drawable.image_placeholder).into(imagen_perfil);
        }
    }

    private static void cargarPreferencias() {

        alias = YouChatApplication.alias;
        correo = YouChatApplication.correo;

        info = YouChatApplication.info;
        telefono = YouChatApplication.telefono;
        genero = YouChatApplication.genero;
        provincia = YouChatApplication.provincia;
        fecha_nacimiento = YouChatApplication.fecha_nacimiento;
    }

    private void cargarPreferenciaImgPerfil(){
        ruta_img_perfil = YouChatApplication.ruta_img_perfil;
        Glide.with(this).load(ruta_img_perfil).error(R.drawable.profile_white).into(imagen_perfil);
    }

    private void guardarPreferenciaImgPerfil(String ruta){
        YouChatApplication.setRuta_img_perfil(ruta);
        Utils.borrarFile(new File(ruta_img_perfil));
        ruta_img_perfil = ruta;
    }

    public void hideFragment(){
        ItemContacto yo = dbWorker.obtenerContacto(YouChatApplication.correo);
        if(yo==null) {
            yo = new ItemContacto("", YouChatApplication.correo);
            yo.setTipo_contacto(ItemContacto.TIPO_CONTACTO_INVISIBLE);
        }
        yo.setAlias(YouChatApplication.alias);
        yo.setTelefono(YouChatApplication.telefono);
        yo.setInfo(YouChatApplication.info);
        yo.setProvincia(YouChatApplication.provincia);
        yo.setFecha_nac(YouChatApplication.fecha_nacimiento);
        yo.setGenero(YouChatApplication.genero);
        yo.setRuta_img(YouChatApplication.ruta_img_perfil);
        yo.setVersion(YouChatApplication.version_info);
        yo.setUsaYouchat(true);
        dbWorker.actualizarContacto(yo);
    }

    public void atras(){
//        ItemContacto yo = dbWorker.obtenerContacto(YouChatApplication.correo);
//        if(yo==null) {
//            yo = new ItemContacto("", YouChatApplication.correo);
//            yo.setTipo_contacto(ItemContacto.TIPO_CONTACTO_INVISIBLE);
//        }
//        yo.setAlias(YouChatApplication.alias);
//        yo.setTelefono(YouChatApplication.telefono);
//        yo.setInfo(YouChatApplication.info);
//        yo.setProvincia(YouChatApplication.provincia);
//        yo.setFecha_nac(YouChatApplication.fecha_nacimiento);
//        yo.setGenero(YouChatApplication.genero);
//        yo.setRuta_img(YouChatApplication.ruta_img_perfil);
//        yo.setVersion(YouChatApplication.version_info);
//        yo.setUsaYouchat(true);
//        dbWorker.actualizarContacto(yo);

        if(frameLayout_visorImg_perfil.getVisibility()==View.VISIBLE)
            ocultarImagen();
        else{
            YouChatApplication.viewYouPerfilActivity = null;
            mainActivity.atrasFragment();
        }
//        getActivity().onBackPressed();
//        navController.navigateUp();
    }
    public static void ActualizarInfo(){
        cargarPreferencias();
        text_alias.setText(alias);
        text_info.setText(info);
        text_correo.setText(correo);
        text_telefono.setText(telefono);
        text_genero.setText(genero);
        text_provincia.setText(provincia);
        text_fecha_nacimiento.setText(fecha_nacimiento);
    }

    /////////////////////////////ABRIR IMAGEN///////////////////////////////////////////////
    public void borrarImgPerfil(){
        guardarPreferenciaImgPerfil("");
        Glide.with(this).load(R.drawable.profile_white).into(imagen_perfil);
    }

    public void usarCamara(){
        if(!permisos.requestPermissionCamera()) return;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                Date date = new Date();
                String fechaEntera = sdf.format(date);
                String nombre_img="img"+fechaEntera+".jpg";
                startActivityForResult(cameraPhoto
                        .takePhotoIntent(YouChatApplication.RUTA_IMAGENES_ENVIADAS, nombre_img), 1);
                cameraPhoto.addToGallery();
            } catch (IOException e) {
                Utils.ShowToastAnimated(getActivity(),"Ocurrió un error al acceder a la cámara",R.raw.error);
            }
        }
        else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Uri photoURI;
                    photoURI = FileProvider.getUriForFile(context,
                            "cu.alexgi.youchat.fileprovider",
                            photoFile);

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, 31);
                }
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String imageFileName = "img" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(YouChatApplication.RUTA_IMAGENES_ENVIADAS);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        miPathCamera = image.getAbsolutePath();
        return image;
    }

    public void abrirGaleria() {
        if(!permisos.requestPermissionAlmacenamiento()) return;
            selectImage();
    }

    public static String getCad() {
        return "ÓÅßÉÂ";
    }

    private void selectImage() {
        Album.image(this)
                .singleChoice()
                .camera(false)
                .columnCount(3)
                .widget(
                        Widget.newDarkBuilder(context)
                                .title("Seleccione una foto de perfil")
                                .build()
                )
                .onResult(new Action<ArrayList<AlbumFile>>() {
                    @Override
                    public void onAction(@NonNull ArrayList<AlbumFile> result) {
                        if(result!=null && result.size()>0){
                            guardarImgAGaleria(result.get(0).getPath());
                        }
                        else Utils.ShowToastAnimated(getActivity(),"Error al cargar la imagen",R.raw.error);
                    }
                })
                .start();
    }

    public void guardarImgAGaleria(String photoPath) {
        File directorioImagenes = new File(YouChatApplication.RUTA_IMAGENES_PERFIL);
        if (!directorioImagenes.exists())
            directorioImagenes.mkdirs();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        Date date = new Date();
        String fechaEntera = sdf.format(date);
        String nombre_img=correo.replace(".","").replace("@","")+fechaEntera+".jpg";
        String miPath=YouChatApplication.RUTA_IMAGENES_PERFIL+nombre_img;
        ArrayList<ItemImg> itemImgs = new ArrayList<>();

        try {
            if(ImageLoader.init().comprimirImagen(photoPath,miPath,50)){
                itemImgs.add(new ItemImg(miPath, photoPath, 50));
                irAlEditor(itemImgs);
//                EsperarParaElEditor(100, itemImgs);
            }
        } catch (FileNotFoundException e) {
            Utils.ShowToastAnimated(mainActivity,"Error al cargar la imagen",R.raw.error);
        }
    }

    private void irAlEditor(ArrayList<ItemImg> itemImgs) {
        if(itemImgs.size()==0) return;
        if(mAddFragmentListener!=null) mAddFragmentListener.onAddFragment(ViewYouPerfilActivity.this, ViewImageActivity.newInstance(itemImgs, 2));
//        navController.navigate(R.id.viewImageActivity, Convertidor.createBundleOfItemImg(itemImgs,2));
    }

    private void previewImage() {
        File file =  new File(ruta_img_perfil);
        if(!file.exists()) return;
        info_photoView = PhotoView.getImageViewInfo(imagen_perfil);
        imagen_perfil.setVisibility(View.INVISIBLE);
        frameLayout_visorImg_perfil.setVisibility(View.VISIBLE);
        Glide.with(context).load(ruta_img_perfil).error(R.drawable.placeholder).into(photoView_visorImg_perfil);
        photoView_visorImg_perfil.animaFrom(info_photoView);

    }

    private void previewImage2() {
        File file =  new File(ruta_img_perfil);
        if(!file.exists()) return;

        ArrayList<AlbumFile> imagenesChatAlbumFile=new ArrayList<>();
        AlbumFile albumFile = new AlbumFile();
        albumFile.setPath(ruta_img_perfil);
        imagenesChatAlbumFile.add(albumFile);

        Album.galleryAlbum(this)
                .checkable(false)
                .checkedList(imagenesChatAlbumFile)
                .currentPosition(0)
                .widget(
                        Widget.newDarkBuilder(context)
                                .title("Imagen de perfil")
                                .build()
                )
                .start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==1) {//camara
                ArrayList<ItemImg> itemImgs = new ArrayList<>();
                String miPath=cameraPhoto.getPhotoPath();
                try {
                    if(ImageLoader.init().comprimirImagen(miPath,miPath,50)){
                        itemImgs.add(new ItemImg(miPath,miPath,50));
//                        EsperarParaElEditor(100, itemImgs);
                        irAlEditor(itemImgs);
                    }
                } catch (FileNotFoundException e) {
                    Utils.ShowToastAnimated(getActivity(),"Error al cargar la imagen",R.raw.error);
                }
            }
            else if(requestCode == 31){ //camara para android 7 en adelante

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                File f = new File(miPathCamera);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                getActivity().sendBroadcast(mediaScanIntent);

                ArrayList<ItemImg> itemImgs = new ArrayList<>();

                try {
                    if(ImageLoader.init().comprimirImagen(miPathCamera,miPathCamera,50)){
                        itemImgs.add(new ItemImg(miPathCamera,miPathCamera,50));
//                        EsperarParaElEditor(100, itemImgs);
                        irAlEditor(itemImgs);
                    }
                } catch (FileNotFoundException e) {
                    Utils.ShowToastAnimated(getActivity(),"Error al cargar la imagen",R.raw.error);
                }
            }
        }
    }
    /////////////////////////////CERRAR IMAGEN///////////////////////////////////////////////
}
