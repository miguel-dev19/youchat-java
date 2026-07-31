package cu.alexgi.youchat.views_GI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.AttributeSet;

import cu.alexgi.youchat.YouChatApplication;

@SuppressLint("RestrictedApi")
public class TextViewMsgIzqGI extends TextGI {

    public TextViewMsgIzqGI(Context context) {
        super(context);
        super.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getFont_msg_izq()));
        super.setSupportCompoundDrawablesTintList(ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getFont_msg_izq())));
        super.setLinkTextColor(Color.parseColor(YouChatApplication.itemTemas.getColor_msg_izq_audio()));
    }

    public TextViewMsgIzqGI(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getFont_msg_izq()));
        super.setSupportCompoundDrawablesTintList(ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getFont_msg_izq())));
        super.setLinkTextColor(Color.parseColor(YouChatApplication.itemTemas.getColor_msg_izq_audio()));
    }

    public TextViewMsgIzqGI(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getFont_msg_izq()));
        super.setSupportCompoundDrawablesTintList(ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getFont_msg_izq())));
        super.setLinkTextColor(Color.parseColor(YouChatApplication.itemTemas.getColor_msg_izq_audio()));
    }
}
