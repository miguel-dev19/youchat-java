package cu.alexgi.youchat.views_GI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.AttributeSet;

import com.google.android.material.radiobutton.MaterialRadioButton;

import cu.alexgi.youchat.R;
import cu.alexgi.youchat.YouChatApplication;

@SuppressLint("RestrictedApi")
public class RadioButtonGI extends MaterialRadioButton {

    public RadioButtonGI(Context context) {
        super(context);
        super.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getColor_texto()));
        super.setSupportButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.gris_perfecto)));
    }

    public RadioButtonGI(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getColor_texto()));
        super.setSupportButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.gris_perfecto)));
    }


    public RadioButtonGI(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getColor_texto()));
        super.setSupportButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.gris_perfecto)));
    }

    @Override
    public void setChecked(boolean checked) {
        if(checked){
            super.setSupportButtonTintList(ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getColor_btn())));
        }
        else {
            super.setSupportButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.gris_perfecto)));
        }
        super.setChecked(checked);
    }

}
