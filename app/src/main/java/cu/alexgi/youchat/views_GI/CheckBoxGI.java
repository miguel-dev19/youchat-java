package cu.alexgi.youchat.views_GI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.AttributeSet;

import com.google.android.material.checkbox.MaterialCheckBox;

import cu.alexgi.youchat.R;
import cu.alexgi.youchat.YouChatApplication;

@SuppressLint("RestrictedApi")
public class CheckBoxGI extends MaterialCheckBox {

    public CheckBoxGI(Context context) {
        super(context);
        init();
    }

    public CheckBoxGI(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public CheckBoxGI(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);
        if(checked){
            super.setSupportButtonTintList(ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getColor_btn())));
        }
        else {
            super.setSupportButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.gris_perfecto)));
        }
    }

    private void init(){
        if(isChecked()){
            super.setSupportButtonTintList(ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getColor_btn())));
        }
        else {
            super.setSupportButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.gris_perfecto)));
        }
        super.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getColor_texto()));
    }

}
