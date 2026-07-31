package cu.alexgi.youchat.Popups;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.vanniktech.emoji.Utils;

import cu.alexgi.youchat.AdminEstadosActivity;
import cu.alexgi.youchat.R;

import static android.view.View.MeasureSpec.makeMeasureSpec;

public final class PopupOpcionesEstados {
  private static final int MARGIN = 2;

  private AdminEstadosActivity adminEstadosActivity;

  @NonNull private final View rootView;
  @Nullable private PopupWindow popupWindow;

  @Nullable final OnClickListener listener;
  @Nullable View rootImageView;

  public PopupOpcionesEstados(@NonNull final View rootView, AdminEstadosActivity aea) {
    this.rootView = rootView;
    this.listener = null;
    adminEstadosActivity = aea;
  }

  public void show(View view, final String correo, final boolean esCorreo) {
    dismiss();

    rootImageView = view;

    final View content = initView(view.getContext(), correo, esCorreo);

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
    desiredLocation = new Point(
            location.x - content.getMeasuredWidth() / 2 + view.getWidth() / 2,
            location.y - content.getMeasuredHeight()
    );

    popupWindow.showAtLocation(rootView, Gravity.NO_GRAVITY, desiredLocation.x, desiredLocation.y);
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

  private View initView(@NonNull final Context context, final String correo, final boolean esCorreo) {
    final View result = View.inflate(context, R.layout.popup_window_option_message, null);
    final LinearLayout imageContainer = result.findViewById(R.id.ll_popupWindow);

    final LayoutInflater inflater = LayoutInflater.from(context);

    LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    result.setLayoutParams(ll);

    for(int i=0; i<2; i++) {
      final View root = inflater.inflate(R.layout.popup_adapter_item_option_message, imageContainer, false);
      ImageView icon_popup = root.findViewById(R.id.icon_popup);
      TextView text_popup = root.findViewById(R.id.text_popup);

      if(i==0) {
        icon_popup.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.info_circle));
        text_popup.setText("Información");
        root.setOnClickListener(new OnClickListener() {
          @Override public void onClick(final View view) {
            adminEstadosActivity.mostrarInfoEstado(correo);
            dismiss();
          }
        });
      }

      if(i==1) {
        icon_popup.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.delete));
        text_popup.setText("Eliminar");
        root.setOnClickListener(new OnClickListener() {
          @Override public void onClick(final View view) {
            adminEstadosActivity.eliminarEstado(correo);
            dismiss();
          }
        });
      }

      imageContainer.addView(root);
    }




    return result;

  }
}
