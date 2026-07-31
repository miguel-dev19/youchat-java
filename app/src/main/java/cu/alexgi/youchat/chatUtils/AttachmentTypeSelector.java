package cu.alexgi.youchat.chatUtils;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cu.alexgi.youchat.MainActivity;
import cu.alexgi.youchat.Permisos;
import cu.alexgi.youchat.R;
import cu.alexgi.youchat.Utils;
import cu.alexgi.youchat.YouChatApplication;
import cu.alexgi.youchat.items.ItemImg;
import cu.alexgi.youchat.photoutil.ImageLoader;

import static cu.alexgi.youchat.MainActivity.context;

public class AttachmentTypeSelector extends PopupWindow {

    public static final int ADD_GALLERY       = 1;
    public static final int ADD_DOCUMENT      = 2;
    public static final int ADD_SOUND         = 3;
    public static final int ADD_CONTACT_INFO  = 4;
    public static final int TAKE_PHOTO        = 5;
    public static final int ADD_LOCATION      = 6;
    public static final int RECORD_VIDEO      = 7;

    private static final int ANIMATION_DURATION = 500;

    @SuppressWarnings("unused")
    private static final String TAG = AttachmentTypeSelector.class.getSimpleName();

    private final @NonNull LoaderManager       loaderManager;
    private final @NonNull RecentPhotoViewRail recentRail;
    private final @NonNull View           imageButton;
    //private final @NonNull ImageView           audioButton;
    private final @NonNull View           documentButton;
    private final @NonNull View           contactButton;

    //private final @NonNull ImageView           cameraButton;
    //private final @NonNull ImageView           videoButton;
    //private final @NonNull ImageView           locationButton;
//  private final @NonNull View closeButton;

    private final @NonNull View tarjeta;
    private final @NonNull View           option_atajo;

    private @Nullable View                      currentAnchor;
    private @Nullable AttachmentClickedListener listener;
    private int chatId;

    public AttachmentTypeSelector(@NonNull Context context, @NonNull LoaderManager loaderManager, @Nullable AttachmentClickedListener listener, int chatId) {
        super(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout   layout   = (LinearLayout) inflater.inflate(R.layout.deltachat_attachment_type_selector, null, true);

        this.listener       = listener;
        this.loaderManager  = loaderManager;
        this.chatId         = chatId;
        this.recentRail     = ViewUtil.findById(layout, R.id.recent_photos);

        this.imageButton    = ViewUtil.findById(layout, R.id.gallery_button);
        //this.audioButton    = ViewUtil.findById(layout, R.id.audio_button);
        this.documentButton = ViewUtil.findById(layout, R.id.document_button);
        this.contactButton  = ViewUtil.findById(layout, R.id.contact_button);

        //this.cameraButton   = ViewUtil.findById(layout, R.id.camera_button);
        //this.videoButton    = ViewUtil.findById(layout, R.id.record_video_button);
        //this.locationButton = ViewUtil.findById(layout, R.id.location_button);
//    this.closeButton    = ViewUtil.findById(layout, R.id.close_button);

        this.tarjeta=ViewUtil.findById(layout,R.id.option_tarjeta);
        this.option_atajo  = ViewUtil.findById(layout, R.id.option_atajo);

        ////////////////////////////////////////////////////////////////////
        //this.imageButton.setOnClickListener(new PropagatingClickListener(ADD_GALLERY));
        //this.audioButton.setOnClickListener(new PropagatingClickListener(ADD_SOUND));
        //this.documentButton.setOnClickListener(new PropagatingClickListener(ADD_DOCUMENT));
        //this.contactButton.setOnClickListener(new PropagatingClickListener(ADD_CONTACT_INFO));
        //this.cameraButton.setOnClickListener(new PropagatingClickListener(TAKE_PHOTO));
        //this.videoButton.setOnClickListener(new PropagatingClickListener(RECORD_VIDEO));
        //this.locationButton.setOnClickListener(new PropagatingClickListener(ADD_LOCATION));
//    this.closeButton.setOnClickListener(new CloseClickListener());
        this.recentRail.setListener(new RecentPhotoSelectedListener());

        //this.tarjeta.setOnClickListener(new TarjetaClickListener());

        if(YouChatApplication.chatsActivity!=null){
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    YouChatApplication.chatsActivity.abrirGaleria();
                }
            });

            contactButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    YouChatApplication.chatsActivity.mostrarBSDContactos();
                }
            });

            documentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    YouChatApplication.chatsActivity.buscarArchivo();
                }
            });

            tarjeta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    YouChatApplication.chatsActivity.mostrarBSDTarjeta();
                }
            });
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                animateButtonIn(imageButton, ANIMATION_DURATION / 2);
                animateButtonIn(documentButton, ANIMATION_DURATION / 3);
                animateButtonIn(contactButton, ANIMATION_DURATION / 4);
                animateButtonIn(tarjeta, 0);
            }
        }
        else if(YouChatApplication.chatsActivityCorreo!=null){
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    YouChatApplication.chatsActivityCorreo.abrirGaleria();
                }
            });
            contactButton.setVisibility(View.INVISIBLE);
            documentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    YouChatApplication.chatsActivityCorreo.buscarArchivo();
                }
            });
            tarjeta.setVisibility(View.GONE);
            option_atajo.setVisibility(View.VISIBLE);
            option_atajo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if(YouChatApplication.chatsActivityCorreo!=null)
                        YouChatApplication.chatsActivityCorreo.mostrarBSDAtajo();
                }
            });
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                animateButtonIn(imageButton, ANIMATION_DURATION / 2);
                animateButtonIn(documentButton, ANIMATION_DURATION / 3);
                animateButtonIn(option_atajo, ANIMATION_DURATION / 4);
            }
        }



    /*if (!Prefs.isLocationStreamingEnabled(context)) {
      this.locationButton.setVisibility(View.GONE);
      ViewUtil.findById(layout, R.id.location_button_label).setVisibility(View.GONE);
    }*/

        //setLocationButtonImage(context);

        setContentView(layout);
        setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(new BitmapDrawable());
        setAnimationStyle(0);
        setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
        setFocusable(true);
        setTouchable(true);

        loaderManager.initLoader(1, null, recentRail);
    }

    public void show(@NonNull Activity activity, final @NonNull View anchor) {
        if (new Permisos(activity, MainActivity.context).requestPermissionAlmacenamiento()) {
            recentRail.setVisibility(View.VISIBLE);
            loaderManager.restartLoader(1, null, recentRail);
        } else {
            recentRail.setVisibility(View.GONE);
        }

        this.currentAnchor = anchor;
        //setLocationButtonImage(activity);

        showAtLocation(anchor, Gravity.BOTTOM, 0, 0);

        getContentView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getContentView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    animateWindowInCircular(anchor, getContentView());
                } else {
                    animateWindowInTranslate(getContentView());
                }
            }
        });

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            animateButtonIn(imageButton, ANIMATION_DURATION / 2);
//            animateButtonIn(documentButton, ANIMATION_DURATION / 3);
//            animateButtonIn(contactButton, ANIMATION_DURATION / 4);
//            animateButtonIn(tarjeta,0);
//
////      animateButtonIn(closeButton, 0);
//      /*animateButtonIn(cameraButton, ANIMATION_DURATION / 2);
//      animateButtonIn(videoButton, ANIMATION_DURATION / 2);
//      animateButtonIn(imageButton, ANIMATION_DURATION / 3);
//      animateButtonIn(audioButton, ANIMATION_DURATION / 3);
//      animateButtonIn(locationButton, ANIMATION_DURATION / 4);
//      animateButtonIn(documentButton, ANIMATION_DURATION / 4);
//      animateButtonIn(contactButton, 0);
//      animateButtonIn(closeButton, 0);*/
//        }
    }

    @Override
    public void dismiss() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            animateWindowOutCircular(currentAnchor, getContentView());
//        } else {
            animateWindowOutTranslate(getContentView());
//        }
    }

    public void setListener(@Nullable AttachmentClickedListener listener) {
        this.listener = listener;
    }

  /*private void setLocationButtonImage(Context context) {
    int resId;
    //if (ApplicationContext.getInstance(context).dcContext.isSendingLocationsToChat(chatId)) {
     // resId = R.drawable.ic_location_off_white_24;
    //} else {
      resId = R.drawable.edit_draw;
    //}

    this.locationButton.setImageDrawable(ContextCompat.getDrawable(context, resId));
  }*/

    private synchronized void animateButtonIn(View button, int delay) {
        AnimationSet animation = new AnimationSet(true);
        Animation scale = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.0f);

        animation.addAnimation(scale);
        animation.setInterpolator(new OvershootInterpolator(1));
        animation.setDuration(ANIMATION_DURATION);
        animation.setStartOffset(delay);
        button.startAnimation(animation);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private synchronized void animateWindowInCircular(@Nullable View anchor, @NonNull View contentView) {
        Pair<Integer, Integer> coordinates = getClickOrigin(anchor, contentView);
        Animator animator = ViewAnimationUtils.createCircularReveal(contentView,
                coordinates.first,
                coordinates.second,
                0,
                Math.max(contentView.getWidth(), contentView.getHeight()));
        animator.setDuration(ANIMATION_DURATION);
        animator.start();
    }

    private synchronized void animateWindowInTranslate(@NonNull View contentView) {
        Animation animation = new TranslateAnimation(0, 0, contentView.getHeight(), 0);
        animation.setDuration(ANIMATION_DURATION);

        getContentView().startAnimation(animation);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private synchronized void animateWindowOutCircular(@Nullable View anchor, @NonNull View contentView) {
        Pair<Integer, Integer> coordinates = getClickOrigin(anchor, contentView);
        Animator               animator    = ViewAnimationUtils.createCircularReveal(getContentView(),
                coordinates.first,
                coordinates.second,
                Math.max(getContentView().getWidth(), getContentView().getHeight()),
                0);

        animator.setDuration(ANIMATION_DURATION);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                AttachmentTypeSelector.super.dismiss();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        animator.start();
    }

    private synchronized void animateWindowOutTranslate(@NonNull View contentView) {
        Utils.runOnUIThread(()->{
            Animation animation = new TranslateAnimation(0, 0, 0, contentView.getTop() + contentView.getHeight());
            animation.setDuration(ANIMATION_DURATION);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    AttachmentTypeSelector.super.dismiss();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            getContentView().startAnimation(animation);
        });
    }

    private Pair<Integer, Integer> getClickOrigin(@Nullable View anchor, @NonNull View contentView) {
        if (anchor == null) return new Pair<>(0, 0);

        final int[] anchorCoordinates = new int[2];
        anchor.getLocationOnScreen(anchorCoordinates);
        anchorCoordinates[0] += anchor.getWidth() / 2;
        anchorCoordinates[1] += anchor.getHeight() / 2;

        final int[] contentCoordinates = new int[2];
        contentView.getLocationOnScreen(contentCoordinates);

        int x = anchorCoordinates[0] - contentCoordinates[0];
        int y = anchorCoordinates[1] - contentCoordinates[1];

        return new Pair<>(x, y);
    }

    private class RecentPhotoSelectedListener implements RecentPhotoViewRail.OnItemClickedListener {
        @Override
        public synchronized void onItemClicked(Uri uri) {
            Utils.runOnUIThread(()->{
                ArrayList<ItemImg> itemImgs = new ArrayList<>();

                String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                String imageFileName = "img" + timeStamp +".jpg";
                String destino=YouChatApplication.RUTA_IMAGENES_ENVIADAS+imageFileName;
                if(Utils.SavePhotoUri(context,uri,destino)){
                    String origen = destino;
                    imageFileName = "img" + timeStamp +"2.jpg";
                    destino=YouChatApplication.RUTA_IMAGENES_ENVIADAS+imageFileName;
                    if(new File(origen).exists()){
                        try {
                            if(ImageLoader.init().comprimirImagen(origen, destino, YouChatApplication.calidad)){
                                itemImgs.add(new ItemImg(destino, origen, YouChatApplication.calidad));
                                animateWindowOutTranslate(getContentView());
                                if(YouChatApplication.chatsActivity!=null)
                                    YouChatApplication.chatsActivity.irAlEditor(itemImgs);
                                else if(YouChatApplication.chatsActivityCorreo!=null)
                                    YouChatApplication.chatsActivityCorreo.irAlEditor(itemImgs);
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            Utils.ShowToastAnimated(MainActivity.mainActivity,"Error al cargar la imagen",R.raw.error);
                        }
                    } else Utils.ShowToastAnimated(MainActivity.mainActivity,"Error al intentar obtener imagen",R.raw.error);
                } else if(Utils.SavePhotoUri2(context,uri,destino)){
                    String origen = destino;
                    imageFileName = "img" + timeStamp +"2.jpg";
                    destino=YouChatApplication.RUTA_IMAGENES_ENVIADAS+imageFileName;
                    if(new File(origen).exists()){
                        try {
                            if(ImageLoader.init().comprimirImagen(origen, destino, YouChatApplication.calidad)){
                                itemImgs.add(new ItemImg(destino, origen, YouChatApplication.calidad));
                                animateWindowOutTranslate(getContentView());
                                if(YouChatApplication.chatsActivity!=null)
                                    YouChatApplication.chatsActivity.irAlEditor(itemImgs);
                                else if(YouChatApplication.chatsActivityCorreo!=null)
                                    YouChatApplication.chatsActivityCorreo.irAlEditor(itemImgs);
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            Utils.ShowToastAnimated(MainActivity.mainActivity,"Error al cargar la imagen",R.raw.error);
                        }
                    } else Utils.ShowToastAnimated(MainActivity.mainActivity,"Error al intentar obtener imagen",R.raw.error);
                } else {
                    String origen = Utils.getImageFromUri(context,uri);
                    if(Utils.esImagen(origen)){
                        try {
                            if(ImageLoader.init().comprimirImagen(origen, destino, YouChatApplication.calidad)){
                                itemImgs.add(new ItemImg(destino, origen, YouChatApplication.calidad));
                                animateWindowOutTranslate(getContentView());
                                if(YouChatApplication.chatsActivity!=null)
                                    YouChatApplication.chatsActivity.irAlEditor(itemImgs);
                                else if(YouChatApplication.chatsActivityCorreo!=null)
                                    YouChatApplication.chatsActivityCorreo.irAlEditor(itemImgs);
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            Utils.ShowToastAnimated(MainActivity.mainActivity,"Error al cargar la imagen",R.raw.error);
                        }
                    } else Utils.ShowToastAnimated(MainActivity.mainActivity,"Error al intentar obtener imagen",R.raw.error);
                }
            });
            /*Utils.runOnUIThread(()->{
                ArrayList<ItemImg> itemImgs = new ArrayList<>();
                Bitmap bitmap;
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(MainActivity.context.getContentResolver(), uri));
                    }
                    else bitmap = MediaStore.Images.Media.getBitmap(MainActivity.context.getContentResolver(), uri);

                    if(bitmap!=null){
                        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                        String imageFileName = "img" + timeStamp +".jpg";
                        String destino=YouChatApplication.RUTA_IMAGENES_ENVIADAS+imageFileName;

                        File file = new File(destino);
                        FileOutputStream fileOutputStream = new FileOutputStream(file, false);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                        fileOutputStream.flush();
                        fileOutputStream.close();

                        if(ImageLoader.init().comprimirImagen(destino, destino, YouChatApplication.calidad)){
                            itemImgs.add(new ItemImg(destino));
                            animateWindowOutTranslate(getContentView());
                            YouChatApplication.chatsActivity.irAlEditor(itemImgs);
                        }
                    }
                    else Utils.ShowToastAnimated(MainActivity.mainActivity,"Error al cargar la imagen",R.raw.error);

                } catch (IOException e) {
                    e.printStackTrace();
                    Utils.ShowToastAnimated(MainActivity.mainActivity,"Ha ocurrido un error",R.raw.error);
                }
            });*/

//            finally {
//                animateWindowOutTranslate(getContentView());
//                if(bitmap!=null) YouChatApplication.chatsActivity.EsperarParaElEditor(100, itemImgs);
//            }


      /*new Thread(new Runnable() {
        @Override
        public void run()
        {
          String path="";
          if (Build.VERSION.SDK_INT < 19){
            path = RealPathUtil.getRealPathFromURI_API11to18(YouChatApplication.context, uri);
          }
          else path = RealPathUtil.getRealPathFromURI_API19(YouChatApplication.context, uri);

          String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
          String imageFileName = "img" + timeStamp +".jpg";
          String destino=YouChatApplication.RUTA_IMAGENES_ENVIADAS+imageFileName;



          try {
            if(ImageLoader.init().comprimirImagen(path,destino,YouChatApplication.calidad)){
              itemImgs.add(new ItemImg(destino));
            }
          } catch (FileNotFoundException e) {
            Utils.ShowToastAnimated(MainActivity.mainActivity,"Error al cargar la imagen",R.raw.error);
          }

          animateWindowOutTranslate(getContentView());
          YouChatApplication.chatsActivity.EsperarParaElEditor(100, itemImgs);
        }
      }).run();*/


      /*String foto=uri.toString();
      Log.e("ATTACH---INFO",foto);



      Log.e("ATTACH---INFO",path);

      Bitmap bitmap = BitmapFactory.decodeFile(foto);
      Log.e("ATTACH---INFO",bitmap.toString());*/


     /* try {
        //bitmap.compress(Bitmap.CompressFormat.JPEG, YouChatApplication.calidad, new FileOutputStream(path));
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }*/
      /*

      File file= new File(uri);
      try {
        FileOutputStream outputStream = new FileOutputStream(new File(YouChatApplication.RUTA_IMAGENES_ENVIADAS+"fotomia.png"));
        outputStream.flush();
        outputStream.close();
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }*/

        //if (listener != null) listener.onQuickAttachment(uri);
    }
}

  /*private File createImageFile() throws IOException {
    // Create an image file name
    String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    String imageFileName = "img" + timeStamp + "_";
    File asd= new File(YouChatApplication.RUTA_IMAGENES_ENVIADAS);
    File storageDir = Environment.getExternalStorageDirectory(asd);
    File image = File.createTempFile(
            imageFileName,  *//* prefix *//*
            ".jpg",         *//* suffix *//*
            storageDir      *//* directory *//*
    );

    // Save a file: path for use with ACTION_VIEW intents
    miPathCamera = image.getAbsolutePath();
    return image;
  }*/

private class PropagatingClickListener implements View.OnClickListener {

    private final int type;

    private PropagatingClickListener(int type) {
        this.type = type;
    }

    @Override
    public void onClick(View v) {
        animateWindowOutTranslate(getContentView());

        if (listener != null) listener.onClick(type);
    }

}

private class CloseClickListener implements View.OnClickListener {
    @Override
    public void onClick(View v) {
        dismiss();
    }
}

public interface AttachmentClickedListener {
    public void onClick(int type);
    public void onQuickAttachment(Uri uri);
}

}
