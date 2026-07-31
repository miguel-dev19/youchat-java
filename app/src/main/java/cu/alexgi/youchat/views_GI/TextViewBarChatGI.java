package cu.alexgi.youchat.views_GI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import com.vanniktech.emoji.EmojiTextView;

import cu.alexgi.youchat.YouChatApplication;

@SuppressLint("RestrictedApi")
public class TextViewBarChatGI extends EmojiTextView {


    public TextViewBarChatGI(Context context) {
        super(context);
        super.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getFont_barchat()));
    }

    public TextViewBarChatGI(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getFont_barchat()));
    }

    public TextViewBarChatGI(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getFont_barchat()));
    }
}
