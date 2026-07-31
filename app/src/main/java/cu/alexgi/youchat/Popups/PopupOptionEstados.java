package cu.alexgi.youchat.Popups;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.vanniktech.emoji.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import cu.alexgi.youchat.Convertidor;
import cu.alexgi.youchat.R;
import cu.alexgi.youchat.SendMsg;
import cu.alexgi.youchat.YouChatApplication;
import cu.alexgi.youchat.items.ItemChat;
import cu.alexgi.youchat.items.ItemEstado;
import cu.alexgi.youchat.photoutil.ImageLoader;

import static android.view.View.MeasureSpec.makeMeasureSpec;
import static cu.alexgi.youchat.MainActivity.context;
import static cu.alexgi.youchat.MainActivity.dbWorker;
import static cu.alexgi.youchat.MainActivity.mainActivity;

public final class PopupOptionEstados {

  @NonNull private final View rootView;
  @Nullable private PopupWindow popupWindow;

  @Nullable final OnClickListener listener;
  @Nullable View rootImageView;

  private ItemEstado estado;

  public PopupOptionEstados(@NonNull final View rootView, ItemEstado s) {
    this.rootView = rootView;
    this.listener = null;
    estado = s;
  }

  public void show(View view) {
    dismiss();

    rootImageView = view;

    final View content = initView(view.getContext(), Utils.dpToPx(view.getContext(), 120));

    popupWindow = new PopupWindow(content, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
    popupWindow.setFocusable(true);
    popupWindow.setOutsideTouchable(true);
    popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
    popupWindow.setBackgroundDrawable(new BitmapDrawable(view.getContext().getResources(), (Bitmap) null));

    popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
      @Override
      public void onDismiss() {
//        visorEstadosActivity.playStories();
      }
    });

    content.measure(makeMeasureSpec
            (0, View.MeasureSpec.UNSPECIFIED), makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

    final Point location = Utils.locationOnScreen(view);
    Point desiredLocation;
    desiredLocation = new Point(
            location.x - content.getMeasuredWidth() / 2 + view.getWidth() / 2,
            location.y - content.getMeasuredHeight()
    );

    popupWindow.showAtLocation(rootView, Gravity.NO_GRAVITY, desiredLocation.x, desiredLocation.y);
    popupWindow.setAnimationStyle(R.style.MyPopupAnimation);

    popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
      @Override
      public void onDismiss() {
          if(onDismiss!=null) onDismiss.onDismiss();
      }
    });

    rootImageView.getParent().requestDisallowInterceptTouchEvent(true);
    Utils.fixPopupLocation(popupWindow, desiredLocation);
    if(onDismiss!=null) onDismiss.onShow();
  }

  public interface onDismissListener{
    void onDismiss();
    void onShow();
  }
  private onDismissListener onDismiss;

  public void setOnDismiss(PopupOptionEstados.onDismissListener onDismiss) {
    this.onDismiss = onDismiss;
  }

  public void dismiss() {
//    if(onDismiss!=null) onDismiss.onDismiss();
    rootImageView = null;

    if (popupWindow != null) {
      popupWindow.dismiss();
      popupWindow = null;
    }
  }


  private View initView(@NonNull final Context context, final int width) {
    final View result = View.inflate(context, R.layout.popup_window_option_estados, null);
    final LinearLayout copy = result.findViewById(R.id.copy);
    final LinearLayout repost = result.findViewById(R.id.repost);
    final LinearLayout share = result.findViewById(R.id.share);
    final LinearLayout save = result.findViewById(R.id.save);

    if(estado.getTexto().isEmpty()) copy.setVisibility(View.GONE);
    if(!estado.esEstadoImagen()) save.setVisibility(View.GONE);

    copy.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        dismiss();
        String clip = estado.getTexto();
        ClipData c = ClipData.newPlainText("YouChatCopy", clip);
        YouChatApplication.clipboard.setPrimaryClip(c);
        cu.alexgi.youchat.Utils.ShowToastAnimated(mainActivity,"Texto copiado al portapapeles",R.raw.voip_invite);
      }
    });
    repost.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        dismiss();
        resubirEstado();
      }
    });
    share.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        dismiss();
        compartirEstado();
      }
    });
    save.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        dismiss();
        guardarEstado();
      }
    });

//    final LayoutInflater inflater = LayoutInflater.from(context);

    return result;
  }

  private void compartirEstado() {
    if(estado.esEstadoImagen()){
      File file = new File(estado.getRuta_imagen());
      if(file.exists()){
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
          uri = FileProvider.getUriForFile(context,
                  "cu.alexgi.youchat.fileprovider",file);
        else uri = Uri.fromFile(file);

        Intent mShareIntent = new Intent();
        mShareIntent.setAction(Intent.ACTION_SEND);
        mShareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mShareIntent.setType("image/*");
        if(!estado.getTexto().isEmpty())
          mShareIntent.putExtra(Intent.EXTRA_TEXT, estado.getTexto());
        mShareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivity(mShareIntent);
      }else {
        Intent mShareIntent = new Intent();
        mShareIntent.setAction(Intent.ACTION_SEND);
        mShareIntent.setType("text/plain");
        mShareIntent.putExtra(Intent.EXTRA_TEXT, estado.getTexto());
        context.startActivity(Intent.createChooser(mShareIntent,"Compartir por:"));
      }
    }
    else {
      Intent mShareIntent = new Intent();
      mShareIntent.setAction(Intent.ACTION_SEND);
      mShareIntent.setType("text/plain");
      mShareIntent.putExtra(Intent.EXTRA_TEXT, estado.getTexto());
      context.startActivity(Intent.createChooser(mShareIntent,"Compartir por:"));
    }
  }

  private void guardarEstado() {
      boolean seCopio = cu.alexgi.youchat.Utils.guardarEnGaleria(estado.getRuta_imagen());
      if(seCopio) cu.alexgi.youchat.Utils.ShowToastAnimated(mainActivity,"Imagen guardada con éxito", R.raw.contact_check);
      else cu.alexgi.youchat.Utils.ShowToastAnimated(mainActivity,"Error al guardar la imagen", R.raw.error);
  }

  public synchronized void resubirEstado(){
    ArrayList<String> seguidores = dbWorker.obtenerTodosSeguidores();
    int longi = seguidores.size();
    if(YouChatApplication.estaAndandoChatService() && !YouChatApplication.chatService.hayConex)
      cu.alexgi.youchat.Utils.ShowToastAnimated(mainActivity,"Compruebe su conexión",R.raw.ic_ban);
    else if(longi==0)
      cu.alexgi.youchat.Utils.ShowToastAnimated(mainActivity,"Sin seguidores aún, no podrás publicar ningún Now",R.raw.swipe_disabled);
    else {
      int vueltas=longi/20+1;
      int vueltas_dadas=0;
      int ultPos=0;
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
      Date date = new Date();
      String fechaEntera = sdf.format(date);
      String hora = Convertidor.conversionHora(fechaEntera);
      String fecha = Convertidor.conversionFecha(fechaEntera);
      String idEstado="YouChat/estado/"+YouChatApplication.correo+"/"+fechaEntera;
      String newRuta =YouChatApplication.RUTA_ESTADOS_GUARDADOS+ "est"+fechaEntera+".jpg";
      if(estado.esEstadoImagen()){
        try {
          File origen = new File(estado.getRuta_imagen());
          File destino = new File(newRuta);
          boolean seCopio = cu.alexgi.youchat.Utils.copyFile(origen,destino);
          if(!seCopio){
            ImageLoader.init().comprimirImagen(estado.getRuta_imagen(),newRuta,100);
          }
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      while (vueltas_dadas<vueltas){
        String correosNotif="";
        int cant=0;
        for(int i=ultPos; i<longi; i++){
          if(cant==20){
            ultPos=i;
            break;
          }
          if(i!=ultPos)
            correosNotif=correosNotif+",";
          correosNotif=correosNotif+seguidores.get(i);
          cant++;
        }

        ItemChat nuevoEstado = new ItemChat(correosNotif,""+estado.getTipo_estado());
        nuevoEstado.setId(idEstado);//ESTADO_CAMPO_ID
        nuevoEstado.setMensaje(estado.getTexto());//ESTADO_CAMPO_TEXTO
        nuevoEstado.setRuta_Dato(newRuta);
        nuevoEstado.setHora(hora);//ESTADO_CAMPO_HORA
        nuevoEstado.setFecha(fecha);//ESTADO_CAMPO_FECHA
        nuevoEstado.setEmisor(""+estado.getEstilo_texto());
        if(YouChatApplication.estaAndandoChatService())
          YouChatApplication.chatService.enviarMensaje(nuevoEstado, SendMsg.CATEGORY_ESTADO_PUBLICAR);
        vueltas_dadas++;
      }
      ItemEstado estadoNuevo = new ItemEstado(idEstado, YouChatApplication.correo, estado.getTipo_estado(),
              true, newRuta, estado.getTexto(),
              0, 0, 0, 0,
              0, 0, 0,
              hora, fecha, fechaEntera,estado.getEstilo_texto(),true,0,"",0);
      dbWorker.insertarNuevoEstado(estadoNuevo);
      cu.alexgi.youchat.Utils.ShowToastAnimated(mainActivity,"Compartiendo Now", R.raw.chats_infotip);
    }
  }
}
