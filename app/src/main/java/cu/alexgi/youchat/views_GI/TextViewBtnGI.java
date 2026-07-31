package cu.alexgi.youchat.views_GI;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import com.vanniktech.emoji.EmojiTextView;

import cu.alexgi.youchat.YouChatApplication;

public class TextViewBtnGI extends EmojiTextView {

    public TextViewBtnGI(Context context) {
        super(context);
        super.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getColor_btn()));
    }

    public TextViewBtnGI(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getColor_btn()));
    }

    public TextViewBtnGI(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getColor_btn()));
    }
}
