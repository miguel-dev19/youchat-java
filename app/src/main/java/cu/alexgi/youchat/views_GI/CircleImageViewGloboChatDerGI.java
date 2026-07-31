package cu.alexgi.youchat.views_GI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import cu.alexgi.youchat.CircleImageView;
import cu.alexgi.youchat.YouChatApplication;

@SuppressLint("RestrictedApi")
public class CircleImageViewGloboChatDerGI extends CircleImageView {

    public CircleImageViewGloboChatDerGI(Context context) {
        super(context);
        super.setCircleBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_msg_der_audio()));
        super.setBorderColor(Color.parseColor(YouChatApplication.itemTemas.getColor_msg_der_audio()));
    }

    public CircleImageViewGloboChatDerGI(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setCircleBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_msg_der_audio()));
        super.setBorderColor(Color.parseColor(YouChatApplication.itemTemas.getColor_msg_der_audio()));
    }


    public CircleImageViewGloboChatDerGI(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setCircleBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_msg_der_audio()));
        super.setBorderColor(Color.parseColor(YouChatApplication.itemTemas.getColor_msg_der_audio()));
    }

}
