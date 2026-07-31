package cu.alexgi.youchat.views_GI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.AttributeSet;

import com.vanniktech.emoji.EmojiTextView;

import cu.alexgi.youchat.YouChatApplication;
@SuppressLint("RestrictedApi")
public class TextViewFontGenGI extends EmojiTextView {

    public TextViewFontGenGI(Context context) {
        super(context);
        init(context);
    }

    public TextViewFontGenGI(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TextViewFontGenGI(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        super.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getColor_texto()));
        super.setHintTextColor(ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getColor_texto_oscuro())));
        super.setSupportCompoundDrawablesTintList(ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getColor_ico_gen())));
        super.setLinkTextColor(ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getColor_btn())));
    }
}
