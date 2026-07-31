package cu.alexgi.youchat.views_GI;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import cu.alexgi.youchat.YouChatApplication;

public class LinearLayoutBarGI extends LinearLayout {

    public LinearLayoutBarGI(Context context) {
        super(context);
        super.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_barra()));
    }

    public LinearLayoutBarGI(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_barra()));
    }

    public LinearLayoutBarGI(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_barra()));
    }
}
