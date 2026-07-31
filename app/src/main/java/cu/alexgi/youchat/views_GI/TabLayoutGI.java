package cu.alexgi.youchat.views_GI;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.AttributeSet;

import com.google.android.material.tabs.TabLayout;

import cu.alexgi.youchat.YouChatApplication;

public class TabLayoutGI extends TabLayout {

    public TabLayoutGI(Context context) {
        super(context);
        super.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_barra()));
        super.setSelectedTabIndicatorColor(Color.parseColor(YouChatApplication.itemTemas.getFont_barra()));
        super.setTabTextColors(Color.parseColor(YouChatApplication.itemTemas.getFont_barra_oscuro()), Color.parseColor(YouChatApplication.itemTemas.getFont_barra()));

        super.setTabIconTint(ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getFont_barra())));
    }

    public TabLayoutGI(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_barra()));
        super.setSelectedTabIndicatorColor(Color.parseColor(YouChatApplication.itemTemas.getFont_barra()));
        super.setTabTextColors(Color.parseColor(YouChatApplication.itemTemas.getFont_barra_oscuro()), Color.parseColor(YouChatApplication.itemTemas.getFont_barra()));

        super.setTabIconTint(ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getFont_barra())));
    }

    public TabLayoutGI(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_barra()));
        super.setSelectedTabIndicatorColor(Color.parseColor(YouChatApplication.itemTemas.getFont_barra()));
        super.setTabTextColors(Color.parseColor(YouChatApplication.itemTemas.getFont_barra_oscuro()), Color.parseColor(YouChatApplication.itemTemas.getFont_barra()));

        super.setTabIconTint(ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getFont_barra())));
    }


}
