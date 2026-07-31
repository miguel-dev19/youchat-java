package cu.alexgi.youchat.views_GI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import com.vanniktech.emoji.EmojiEditText;

import cu.alexgi.youchat.YouChatApplication;

@SuppressLint("RestrictedApi")
public class EditTextFontGenGI extends EmojiEditText {

    public EditTextFontGenGI(Context context) {
        super(context);
        super.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getColor_texto()));
        super.setHintTextColor(Color.parseColor(YouChatApplication.itemTemas.getColor_texto_oscuro()));
    }

    public EditTextFontGenGI(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getColor_texto()));
        super.setHintTextColor(Color.parseColor(YouChatApplication.itemTemas.getColor_texto_oscuro()));
    }

    public EditTextFontGenGI(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getColor_texto()));
        super.setHintTextColor(Color.parseColor(YouChatApplication.itemTemas.getColor_texto_oscuro()));
    }
}
