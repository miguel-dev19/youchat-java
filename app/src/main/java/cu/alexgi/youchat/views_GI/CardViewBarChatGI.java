package cu.alexgi.youchat.views_GI;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import com.google.android.material.card.MaterialCardView;

import cu.alexgi.youchat.YouChatApplication;

public class CardViewBarChatGI extends MaterialCardView {

    public CardViewBarChatGI(Context context) {
        super(context);
        super.setCardBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_barchat()));
        super.setRadius(YouChatApplication.curvaChat);
    }

    public CardViewBarChatGI(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setCardBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_barchat()));
        super.setRadius(YouChatApplication.curvaChat);
    }

    public CardViewBarChatGI(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setCardBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_barchat()));
        super.setRadius(YouChatApplication.curvaChat);
    }
}
