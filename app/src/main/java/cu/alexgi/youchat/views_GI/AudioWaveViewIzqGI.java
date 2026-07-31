package cu.alexgi.youchat.views_GI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import com.vanniktech.emoji.Utils;

import cu.alexgi.youchat.MainActivity;
import cu.alexgi.youchat.YouChatApplication;
import cu.alexgi.youchat.audiowave.AudioWaveView;

@SuppressLint("RestrictedApi")
public class AudioWaveViewIzqGI extends AudioWaveView {

    public AudioWaveViewIzqGI(Context context) {
        super(context);
        super.setWaveColor(Color.parseColor(YouChatApplication.itemTemas.getColor_msg_izq_audio()));
        super.setChunkSpacing(Utils.dpToPx(MainActivity.context, 1));
        super.setChunkWidth(Utils.dpToPx(MainActivity.context, 2));
        super.setMinChunkHeight(Utils.dpToPx(MainActivity.context, 1));
        super.setChunkHeight(Utils.dpToPx(MainActivity.context, 24));
        super.setChunkRadius(Utils.dpToPx(MainActivity.context, 2));
    }

    public AudioWaveViewIzqGI(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setWaveColor(Color.parseColor(YouChatApplication.itemTemas.getColor_msg_izq_audio()));
        super.setChunkSpacing(Utils.dpToPx(MainActivity.context, 1));
        super.setChunkWidth(Utils.dpToPx(MainActivity.context, 2));
        super.setMinChunkHeight(Utils.dpToPx(MainActivity.context, 1));
        super.setChunkHeight(Utils.dpToPx(MainActivity.context, 24));
        super.setChunkRadius(Utils.dpToPx(MainActivity.context, 2));
    }


    public AudioWaveViewIzqGI(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setWaveColor(Color.parseColor(YouChatApplication.itemTemas.getColor_msg_izq_audio()));
        super.setChunkSpacing(Utils.dpToPx(MainActivity.context, 1));
        super.setChunkWidth(Utils.dpToPx(MainActivity.context, 2));
        super.setMinChunkHeight(Utils.dpToPx(MainActivity.context, 1));
        super.setChunkHeight(Utils.dpToPx(MainActivity.context, 24));
        super.setChunkRadius(Utils.dpToPx(MainActivity.context, 2));
    }
}
