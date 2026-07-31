package cu.alexgi.youchat.views_GI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.AttributeSet;

import com.google.android.material.slider.Slider;

import cu.alexgi.youchat.YouChatApplication;

@SuppressLint("RestrictedApi")
public class SliderGI extends Slider {

    public SliderGI(Context context) {
        super(context);
        super.setThumbTintList(ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getColor_btn())));
        super.setTrackActiveTintList(ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getColor_btn())));
//        super.setTickActiveTintList(ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getColor_btn())));

        super.setTrackInactiveTintList(ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getColor_btn_oscuro())));
    }

    public SliderGI(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setThumbTintList(ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getColor_btn())));
        super.setTrackActiveTintList(ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getColor_btn())));
//        super.setTickActiveTintList(ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getColor_btn())));

        super.setTrackInactiveTintList(ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getColor_btn_oscuro())));
    }


    public SliderGI(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setThumbTintList(ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getColor_btn())));
        super.setTrackActiveTintList(ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getColor_btn())));
//        super.setTickActiveTintList(ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getColor_btn())));

        super.setTrackInactiveTintList(ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getColor_btn_oscuro())));
    }

}
