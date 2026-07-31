package cu.alexgi.youchat.views_GI;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.AttributeSet;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import cu.alexgi.youchat.YouChatApplication;

public class FABGI extends FloatingActionButton {

    public FABGI(Context context) {
        super(context);
        super.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getColor_btn())));
    }

    public FABGI(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getColor_btn())));
    }

    public FABGI(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getColor_btn())));
    }
}
