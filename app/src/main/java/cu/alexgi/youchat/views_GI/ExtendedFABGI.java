package cu.alexgi.youchat.views_GI;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import cu.alexgi.youchat.YouChatApplication;

public class ExtendedFABGI extends ExtendedFloatingActionButton {

    public ExtendedFABGI(Context context) {
        super(context);
        super.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_btn()));
    }

    public ExtendedFABGI(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_btn()));
    }

    public ExtendedFABGI(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_btn()));
    }
}
