package cu.alexgi.youchat.views_GI;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import com.vanniktech.emoji.EmojiTextView;

import cu.alexgi.youchat.YouChatApplication;

public class TextViewBarGI extends EmojiTextView {

    public TextViewBarGI(Context context) {
        super(context);
        super.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getFont_barra()));
    }

    public TextViewBarGI(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getFont_barra()));
    }

    public TextViewBarGI(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getFont_barra()));
    }
}
