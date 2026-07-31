package cu.alexgi.youchat.Popups;

import android.content.Context;
import android.graphics.Point;
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

import cu.alexgi.youchat.ChatsActivity;
import cu.alexgi.youchat.R;
import cu.alexgi.youchat.YouChatApplication;
import cu.alexgi.youchat.items.ItemChat;

import static android.view.View.MeasureSpec.makeMeasureSpec;

public final class PopupMsgChat {
  private static final int MARGIN = 2;

  private ChatsActivity chatsActivity;

  @NonNull private final View rootView;
  @Nullable private PopupWindow popupWindow;

  @Nullable final OnClickListener listener;
  @Nullable View rootImageView;

  public PopupMsgChat(@NonNull final View rootView, ChatsActivity ca) {
    this.rootView = rootView;
    this.listener = null;
    chatsActivity = ca;
  }
  public PopupMsgChat(@NonNull final View rootView, @Nullable final OnClickListener listener, ChatsActivity ca) {
    this.rootView = rootView;
    this.listener = listener;
    chatsActivity = ca;
  }

  public void show(View view, ItemChat chat) {
    dismiss();

    rootImageView = view;

//    int cant = 4;
//    if(tipo==1) cant=3;

    final View content = initView(view.getContext(), (int)(YouChatApplication.anchoPantalla/2.2f), chat);

//    content.setBackgroundColor(Color.parseColor("#4D000000"));
    popupWindow = new PopupWindow(content, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
    popupWindow.setFocusable(true);
    popupWindow.setOutsideTouchable(true);
    popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);

//    popupWindow.setBackgroundDrawable(new BitmapDrawable(view.getContext().getResources(), BitmapFactory.decodeResource(view.getContext().getResources(), R.drawable.shadow_left)));

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

//  public void show(@NonNull final ImageView clickedImage) {
//    dismiss();
//
//    rootImageView = clickedImage;
//
//    final View content = initView(clickedImage.getContext(), clickedImage.getWidth());
//
//    popupWindow = new PopupWindow(content, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
//    popupWindow.setFocusable(true);
//    popupWindow.setOutsideTouchable(true);
//    popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
//    popupWindow.setBackgroundDrawable(new BitmapDrawable(clickedImage.getContext().getResources(), (Bitmap) null));
//
//    content.measure(makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//
//    final Point location = Utils.locationOnScreen(clickedImage);
//    final Point desiredLocation = new Point(
//            location.x - content.getMeasuredWidth() / 2 + clickedImage.getWidth() / 2,
//            location.y - content.getMeasuredHeight()
//    );
//
//    popupWindow.showAtLocation(rootView, Gravity.NO_GRAVITY, desiredLocation.x, desiredLocation.y);
//    rootImageView.getParent().requestDisallowInterceptTouchEvent(true);
//    Utils.fixPopupLocation(popupWindow, desiredLocation);
//  }

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

    for(int i=0; i<7; i++){
      final View root = inflater.inflate(R.layout.popup_adapter_item_option_message, imageContainer, false);

      final ImageView icon_popup = root.findViewById(R.id.icon_popup);
      final TextView text_popup = root.findViewById(R.id.text_popup);

//      final ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) root.getLayoutParams();
//      final int margin = Utils.dpToPx(context, MARGIN);
//      layoutParams.width = width;

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
              if(!chat.getMensaje().isEmpty())
              {
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
          if(chat.esMsgTexto() || chat.esImagen()) {
            if(!chat.hayQReintentarEnviar() && chat.esDer()){
              icon_popup.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.edit));
              text_popup.setText("Editar");
              root.setOnClickListener(new OnClickListener() {
                @Override public void onClick(final View view) {
                  chatsActivity.editarMsg();
                  dismiss();
                }
              });
              imageContainer.addView(root);
            }
          }
          break;
        case 5:
          if(chat.esImagen() && new File(chat.getRuta_Dato()).exists()) {
            icon_popup.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.image));
            text_popup.setText("Guardar en galería");
            root.setOnClickListener(new OnClickListener() {
              @Override public void onClick(final View view) {
                chatsActivity.guardarImagen();
                dismiss();
              }
            });
            imageContainer.addView(root);
          }
          break;
        case 6:
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

//    if(cant==3){
//      final LayoutInflater inflater = LayoutInflater.from(context);
//
////    for (final ImageView variant : variants) {
//      for(int i=0; i<cant; i++){
//        final ImageView emojiImage = (ImageView) inflater
//                .inflate(R.layout.popup_adapter_item_option_message, imageContainer, false);
//        final ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) emojiImage.getLayoutParams();
//        final int margin = Utils.dpToPx(context, MARGIN);
//
//        // Use the same size for Emojis as in the picker.
//        layoutParams.width = width;
////      layoutParams.setMargins(margin, margin, margin, margin);
//        if(i==0) {
//          emojiImage.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.option_chat_retry));
//          emojiImage.setOnClickListener(new OnClickListener() {
//            @Override public void onClick(final View view) {
//              chatsActivity.reintentarEnviarMsg();
//              dismiss();
//            }
//          });
//        }
//        else if(i==1) {
//          emojiImage.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.option_chat_copy));
//          emojiImage.setOnClickListener(new OnClickListener() {
//            @Override public void onClick(final View view) {
//              chatsActivity.copiarMsg();
//              dismiss();
//            }
//          });
//        }
//        else {
//          emojiImage.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.delete));
//          emojiImage.setOnClickListener(new OnClickListener() {
//            @Override public void onClick(final View view) {
//                chatsActivity.eliminarMsg();
//              dismiss();
//            }
//          });
//        }
//
//        imageContainer.addView(emojiImage);
//      }
//
//      return result;
//    }
//    else if(cant==4){
//      final LayoutInflater inflater = LayoutInflater.from(context);
//
////    for (final ImageView variant : variants) {
//      for(int i=0; i<cant; i++){
//        final ImageView emojiImage = (ImageView) inflater
//                .inflate(R.layout.popup_adapter_item_option_message, imageContainer, false);
//        final ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) emojiImage.getLayoutParams();
//        final int margin = Utils.dpToPx(context, MARGIN);
//
//        // Use the same size for Emojis as in the picker.
//        layoutParams.width = width;
////      layoutParams.setMargins(margin, margin, margin, margin);
//        if(i==0) {
//          emojiImage.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.option_chat_answer1));
//          emojiImage.setOnClickListener(new OnClickListener() {
//            @Override public void onClick(final View view) {
//              chatsActivity.responderMsg();
//              dismiss();
//            }
//          });
//        }
//        else if(i==1) {
//          emojiImage.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.option_chat_reenviar));
//          emojiImage.setOnClickListener(new OnClickListener() {
//            @Override public void onClick(final View view) {
//              chatsActivity.reenviarMsg();
//              dismiss();
//            }
//          });
//        }
//        else if(i==2) {
//          emojiImage.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.option_chat_copy));
//          emojiImage.setOnClickListener(new OnClickListener() {
//            @Override public void onClick(final View view) {
//              chatsActivity.copiarMsg();
//              dismiss();
//            }
//          });
//        }
//        else {
//          emojiImage.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.delete));
//          emojiImage.setOnClickListener(new OnClickListener() {
//            @Override public void onClick(final View view) {
//              chatsActivity.eliminarMsg();
//              dismiss();
//            }
//          });
//        }
//
//        imageContainer.addView(emojiImage);
//      }
//
//      return result;
//    }
//    return null;

  }
//  private View initView(@NonNull final Context context, final int width) {
//    final View result = View.inflate(context, R.layout.popup_window_option_message, null);
//    final LinearLayout imageContainer = result.findViewById(R.id.ll_popupWindow);
//
////    final List<ImageView> variants = new ArrayList<>();
////    variants.add();
//
//    final LayoutInflater inflater = LayoutInflater.from(context);
//
////    for (final ImageView variant : variants) {
//    for(int i=0; i<3; i++){
//      final ImageView emojiImage = (ImageView) inflater
//              .inflate(R.layout.popup_adapter_item_option_message, imageContainer, false);
//      final ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) emojiImage.getLayoutParams();
//      final int margin = Utils.dpToPx(context, MARGIN);
//
//      // Use the same size for Emojis as in the picker.
//      layoutParams.width = width;
//      layoutParams.setMargins(margin, margin, margin, margin);
//      emojiImage.setImageDrawable(YouChatApplication.icon_responder);
//
////      emojiImage.setOnClickListener(new OnClickListener() {
////        @Override public void onClick(final View view) {
////          if (listener != null && rootImageView != null) {
////            listener.onClick(rootImageView, variant);
////          }
////        }
////      });
//
//      imageContainer.addView(emojiImage);
//    }
//
//    return result;
//  }
}
