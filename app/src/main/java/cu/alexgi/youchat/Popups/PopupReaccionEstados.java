package cu.alexgi.youchat.Popups;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.airbnb.lottie.LottieAnimationView;
import com.vanniktech.emoji.Utils;

import cu.alexgi.youchat.EstadosViewPagerFragment;
import cu.alexgi.youchat.PrincipalActivity;
import cu.alexgi.youchat.R;
import cu.alexgi.youchat.YouChatApplication;

import static android.view.View.MeasureSpec.makeMeasureSpec;

public final class PopupReaccionEstados {
  private static final int MARGIN = 2;

  private PrincipalActivity pa;
  private EstadosViewPagerFragment estadosViewPagerFragment;

  @NonNull private final View rootView;
  @Nullable private PopupWindow popupWindow;

  @Nullable final OnClickListener listener;
  @Nullable View rootImageView;

  public PopupReaccionEstados(@NonNull final View rootView, EstadosViewPagerFragment va) {
    this.rootView = rootView;
    this.listener = null;
    estadosViewPagerFragment = va;
  }

  public void show(View view) {
    dismiss();
    rootImageView = view;
    final View content = initView(view.getContext(), (YouChatApplication.anchoPantalla/6));

    popupWindow = new PopupWindow(content, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
    popupWindow.setFocusable(true);
    popupWindow.setOutsideTouchable(true);
    popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
    popupWindow.setBackgroundDrawable(new BitmapDrawable(view.getContext().getResources(), (Bitmap) null));

    popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
      @Override
      public void onDismiss() {
        estadosViewPagerFragment.playStory();
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

  public void setOnDismiss(PopupReaccionEstados.onDismissListener onDismiss) {
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
    final View result = View.inflate(context, R.layout.popup_window_option_reaccion_estados, null);
    final LinearLayout imageContainer = result.findViewById(R.id.ll_popupWindow);

    final LayoutInflater inflater = LayoutInflater.from(context);

    for(int i=0; i<7; i++){
      final LottieAnimationView emojiImage = (LottieAnimationView) inflater.inflate(R.layout.popup_adapter_item_reaccion_lottie, imageContainer, false);
      if(i==0) {
        emojiImage.setAnimation(R.raw.like1);
        emojiImage.setOnClickListener(new OnClickListener() {
          @Override public void onClick(final View view) {
            estadosViewPagerFragment.enviarReaccion(1);
            dismiss();
          }
        });
      }
      else if(i==1) {
        emojiImage.setAnimation(R.raw.encanta);
        emojiImage.setOnClickListener(new OnClickListener() {
          @Override public void onClick(final View view) {
            estadosViewPagerFragment.enviarReaccion(2);
            dismiss();
          }
        });
      }
      else if(i==2) {
        emojiImage.setAnimation(R.raw.sonroja);
        emojiImage.setOnClickListener(new OnClickListener() {
          @Override public void onClick(final View view) {
            estadosViewPagerFragment.enviarReaccion(3);
            dismiss();
          }
        });
      }
      else if(i==3) {
        emojiImage.setAnimation(R.raw.divierte);
        emojiImage.setOnClickListener(new OnClickListener() {
          @Override public void onClick(final View view) {
            estadosViewPagerFragment.enviarReaccion(4);
            dismiss();
          }
        });
      }
      else if(i==4) {
        emojiImage.setAnimation(R.raw.asombra);
        emojiImage.setOnClickListener(new OnClickListener() {
          @Override public void onClick(final View view) {
            estadosViewPagerFragment.enviarReaccion(5);
            dismiss();
          }
        });
      }
      else if(i==5) {
        emojiImage.setAnimation(R.raw.entristece);
        emojiImage.setOnClickListener(new OnClickListener() {
          @Override public void onClick(final View view) {
            estadosViewPagerFragment.enviarReaccion(6);
            dismiss();
          }
        });
      }
      else if(i==6) {
        emojiImage.setAnimation(R.raw.enoja);
        emojiImage.setOnClickListener(new OnClickListener() {
          @Override public void onClick(final View view) {
            estadosViewPagerFragment.enviarReaccion(7);
            dismiss();
          }
        });
      }

      imageContainer.addView(emojiImage);
    }
    return result;
  }
}
