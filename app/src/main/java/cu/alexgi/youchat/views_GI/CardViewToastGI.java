package cu.alexgi.youchat.views_GI;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import com.google.android.material.card.MaterialCardView;

import cu.alexgi.youchat.YouChatApplication;

public class CardViewToastGI extends MaterialCardView {

    public CardViewToastGI(Context context) {
        super(context);
        super.setCardBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_toast()));
        super.setRadius(YouChatApplication.curvaChat);
    }

    public CardViewToastGI(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setCardBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_toast()));
        super.setRadius(YouChatApplication.curvaChat);
    }

    public CardViewToastGI(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setCardBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_toast()));
        super.setRadius(YouChatApplication.curvaChat);
    }
}
