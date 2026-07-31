package cu.alexgi.youchat.Popups;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.vanniktech.emoji.Utils;

import java.io.File;

import cu.alexgi.youchat.ChatsActivityCorreo;
import cu.alexgi.youchat.R;
import cu.alexgi.youchat.YouChatApplication;
import cu.alexgi.youchat.items.ItemChat;

import static android.view.View.MeasureSpec.makeMeasureSpec;

public final class PopupMsgChatCorreo {
  private static final int MARGIN = 2;

  private ChatsActivityCorreo chatsActivity;

  @NonNull private final View rootView;
  @Nullable private PopupWindow popupWindow;

  @Nullable final OnClickListener listener;
  @Nullable View rootImageView;

  public PopupMsgChatCorreo(@NonNull final View rootView, ChatsActivityCorreo ca) {
    this.rootView = rootView;
    this.listener = null;
    chatsActivity = ca;
  }
  public PopupMsgChatCorreo(@NonNull final View rootView, @Nullable final OnClickListener listener, ChatsActivityCorreo ca) {
    this.rootView = rootView;
    this.listener = listener;
    chatsActivity = ca;
  }

  public void show(View view, ItemChat chat) {
    dismiss();

    rootImageView = view;

//    int cant = 4;
//    if(tipo==1) cant=3;

    final View content = initView(view.getContext(), (YouChatApplication.anchoPantalla/7), chat);

    popupWindow = new PopupWindow(content, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
    popupWindow.setFocusable(true);
    popupWindow.setOutsideTouchable(true);
    popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
    popupWindow.setBackgroundDrawable(new BitmapDrawable(view.getContext().getResources(), (Bitmap) null));

    content.measure(makeMeasureSpec
            (0, View.MeasureSpec.UNSPECIFIED), makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

    content.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        dismiss();
      }
    });

    final Point location = Utils.locationOnScreen(view);
    Point desiredLocation;
    if(chat.esDer()){
      desiredLocation = new Point(
              0,
              location.y - (content.getMeasuredHeight()/2)
      );
    }else {
      desiredLocation = new Point(
              view.getWidth(),
              location.y - (content.getMeasuredHeight()/2)
      );
    }
//    desiredLocation = new Point(
//            location.x - content.getMeasuredWidth() / 2 + view.getWidth() / 2,
//            location.y - content.getMeasuredHeight()
//    );

    popupWindow.showAtLocation(rootView, Gravity.START, desiredLocation.x, desiredLocation.y);
    popupWindow.setAnimationStyle(R.style.MyPopupAnimation);
    rootImageView.getParent().requestDisallowInterceptTouchEvent(true);
    Utils.fixPopupLocation(popupWindow, desiredLocation);
  }

  public void dismiss() {
    rootImageView = null;

    if (popupWindow != null) {
      popupWindow.dismiss();
      popupWindow = null;
    }
  }

  private View initView(@NonNull final Context context, final int width, ItemChat chat) {
    final View result = View.inflate(context, R.layout.popup_window_option_message, null);
    final LinearLayout imageContainer = result.findViewById(R.id.ll_popupWindow);

    final LayoutInflater inflater = LayoutInflater.from(context);

    for(int i=0; i<6; i++){
      final View root = inflater.inflate(R.layout.popup_adapter_item_option_message, imageContainer, false);
      final ImageView icon_popup = root.findViewById(R.id.icon_popup);
      final TextView text_popup = root.findViewById(R.id.text_popup);

      switch (i){
        case 0:
          if(chat.hayQReintentarEnviar()) {
            icon_popup.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.option_chat_retry));
            text_popup.setText("Reintentar");
            root.setOnClickListener(new OnClickListener() {
              @Override public void onClick(final View view) {
                chatsActivity.reintentarEnviarMsg();
                dismiss();
              }
            });
            imageContainer.addView(root);
          }
          break;
        case 1:
          if(chat.esMsgTexto() || chat.esImagen() || chat.esTarjeta()) {
            if(!chat.getMensaje().isEmpty()) {
              icon_popup.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.copy_duplicate));
              text_popup.setText("Copiar");
              root.setOnClickListener(new OnClickListener() {
                @Override public void onClick(final View view) {
                  chatsActivity.copiarMsg();
                  dismiss();
                }
              });
              imageContainer.addView(root);
            }
          }
          break;
        case 2:
          if(chat.puedeSwipear()) {
            icon_popup.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.option_chat_answer));
            text_popup.setText("Responder");
            root.setOnClickListener(new OnClickListener() {
              @Override public void onClick(final View view) {
                chatsActivity.responderMsg();
                dismiss();
              }
            });
            imageContainer.addView(root);
          }
          break;
        case 3:
          if(chat.puedeSwipear()) {
            icon_popup.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.option_chat_reenviar));
            text_popup.setText("Reenviar");
            root.setOnClickListener(new OnClickListener() {
              @Override public void onClick(final View view) {
                chatsActivity.reenviarMsg();
                dismiss();
              }
            });
            imageContainer.addView(root);
          }
          break;
        case 4:
          if(chat.esImagen()) {
            boolean exist = false;
            if(chat.esIzq())
              exist = new File(YouChatApplication.RUTA_ADJUNTOS_CORREO+chat.getRuta_Dato()).exists();
            else exist = new File(chat.getRuta_Dato()).exists();
            if(exist) {
              icon_popup.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.image));
              text_popup.setText("Guardar en galería");
              root.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View view) {
                  chatsActivity.guardarImagen();
                  dismiss();
                }
              });
              imageContainer.addView(root);
            }
          }
          break;
        case 5:
          icon_popup.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.delete));
          text_popup.setText("Eliminar");
          root.setOnClickListener(new OnClickListener() {
            @Override public void onClick(final View view) {
              chatsActivity.eliminarMsg();
              dismiss();
            }
          });
          imageContainer.addView(root);
          break;
      }
    }
    return result;

  }
}
