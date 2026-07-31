package cu.alexgi.youchat.views_GI;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import cu.alexgi.youchat.YouChatApplication;

public class ExtendedFABMsgIzqGI extends ExtendedFloatingActionButton {

    public ExtendedFABMsgIzqGI(Context context) {
        super(context);
        super.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_msg_izq_audio()));
    }

    public ExtendedFABMsgIzqGI(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_msg_izq_audio()));
    }

    public ExtendedFABMsgIzqGI(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_msg_izq_audio()));
    }
}
