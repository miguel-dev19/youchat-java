package cu.alexgi.youchat.views_GI;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.AttributeSet;

import com.google.android.material.switchmaterial.SwitchMaterial;

import cu.alexgi.youchat.R;
import cu.alexgi.youchat.Utils;
import cu.alexgi.youchat.YouChatApplication;

public class SwitchGI extends SwitchMaterial {

    public SwitchGI(Context context) {
        super(context);
    }

    public SwitchGI(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwitchGI(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);
        if(checked){
            super.setThumbTintList(ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getColor_btn())));
            super.setTrackTintList(ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getColor_btn_oscuro())));
        }
        else{
            super.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.fondo_blanco)));
            super.setTrackTintList(ColorStateList.valueOf(Color.parseColor(Utils.obtenerOscuroDe(Integer.toHexString(getResources().getColor(R.color.fondo_blanco))))));
        }
    }
}
