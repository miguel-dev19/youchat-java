package cu.alexgi.youchat.chatUtils;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import cu.alexgi.youchat.R;
import cu.alexgi.youchat.chatUtils.concurrent.ListenableFuture;
import cu.alexgi.youchat.chatUtils.concurrent.SettableFuture;


public class InputPanel extends LinearLayout {

  private static final String TAG = InputPanel.class.getSimpleName();

  private static final int FADE_TIME = 150;

  //private ComposeText composeText;
  private View        quickCameraToggle;
  //private View        quickAudioToggle;
  private View        buttonToggle;
  private View        recordingContainer;
  private View emojiButton,attachButton,input_text;

  //private MicrophoneRecorderView microphoneRecorderView;
  //private SlideToCancel          slideToCancel;
  //private RecordTime             recordTime;

  private @Nullable Listener listener;

  public InputPanel(Context context) {
    super(context);
  }

  public InputPanel(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  public InputPanel(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  public void onFinishInflate() {
    super.onFinishInflate();

    //this.emojiToggle            = findViewById(R.id.emoji_toggle);
    //this.composeText            = findViewById(R.id.embedded_text_editor);
    this.quickCameraToggle      = findViewById(R.id.quick_camera_toggle);
    //this.quickAudioToggle       = findViewById(R.id.quick_audio_toggle);
    this.buttonToggle           = findViewById(R.id.button_toggle);
    this.recordingContainer     = findViewById(R.id.recording_container);
    /*this.recordTime             = new RecordTime(findViewById(R.id.record_time));
    this.slideToCancel          = new SlideToCancel(findViewById(R.id.slide_to_cancel));
    this.microphoneRecorderView = findViewById(R.id.recorder_view);*/
    //this.microphoneRecorderView.setListener(this);

    this.emojiButton=findViewById(R.id.input_emoji);
    this.attachButton=findViewById(R.id.attach_button);
    this.input_text=findViewById(R.id.input_texts);

    /*if (Prefs.isSystemEmojiPreferred(getContext())) {
      //emojiToggle.setVisibility(View.GONE);
      //emojiVisible = false;
    } else {
      //emojiToggle.setVisibility(View.VISIBLE);
      emojiVisible = true;
    }*/
  }

  public void setListener(final @NonNull Listener listener) {
    this.listener = listener;

    //emojiToggle.setOnClickListener(v -> listener.onEmojiToggle());
  }

  /*public void setMediaListener(@NonNull MediaListener listener) {
    composeText.setMediaListener(listener);
  }*/

  /*public void setEmojiDrawer(@NonNull EmojiDrawer emojiDrawer) {
    emojiToggle.attach(emojiDrawer);
  }
*/





  public void onPause() {
//    this.microphoneRecorderView.cancelAction();
  }

  public void setEnabled(boolean enabled) {
    //composeText.setEnabled(enabled);
    //quickAudioToggle.setEnabled(enabled);
    quickCameraToggle.setEnabled(enabled);
  }

  private long onRecordHideEvent(float x) {
//    ListenableFuture<Void> future      = slideToCancel.hide(x);
//    long elapsedTime = recordTime.hide();

    /*future.addListener(new AssertedSuccessListener<Void>() {
      @Override
      public void onSuccess(Void result) {
        //if (emojiVisible) ViewUtil.fadeIn(emojiToggle, FADE_TIME);
        //ViewUtil.fadeIn(composeText, FADE_TIME);
        ViewUtil.fadeIn(quickCameraToggle, FADE_TIME);
        ViewUtil.fadeIn(quickAudioToggle, FADE_TIME);
        ViewUtil.fadeIn(buttonToggle, FADE_TIME);

        ViewUtil.fadeIn(emojiButton,FADE_TIME);
        ViewUtil.fadeIn(attachButton,FADE_TIME);
        ViewUtil.fadeIn(input_text,FADE_TIME);
      }
    });*/

//    return elapsedTime;
      return 2000;
  }

 /* @Override
  public void onKeyboardShown() {
    emojiToggle.setToEmoji();
  }

  @Override
  public void onKeyEvent(KeyEvent keyEvent) {
    composeText.dispatchKeyEvent(keyEvent);
  }

  @Override
  public void onEmojiSelected(String emoji) {
    composeText.insertEmoji(emoji);
  }
*/

  public interface Listener {
    //void onRecorderStarted();
    //void onRecorderFinished();
    //void onRecorderCanceled();
    //void onRecorderPermissionRequired();
    //void onEmojiToggle();
  }

  private static class SlideToCancel {

    private final View slideToCancelView;

    private float startPositionX;

    public SlideToCancel(View slideToCancelView) {
      this.slideToCancelView = slideToCancelView;
    }

    public void display(float startPositionX) {
      this.startPositionX = startPositionX;
      ViewUtil.fadeIn(this.slideToCancelView, FADE_TIME);
    }

    public ListenableFuture<Void> hide(float x) {
      final SettableFuture<Void> future = new SettableFuture<>();
      float offset = getOffset(x);

      AnimationSet animation = new AnimationSet(true);
      animation.addAnimation(new TranslateAnimation(Animation.ABSOLUTE, offset,
                                                    Animation.ABSOLUTE, 0,
                                                    Animation.RELATIVE_TO_SELF, 0,
                                                    Animation.RELATIVE_TO_SELF, 0));
      animation.addAnimation(new AlphaAnimation(1, 0));

//      animation.setDuration(MicrophoneRecorderView.ANIMATION_DURATION);
      animation.setFillBefore(true);
      animation.setFillAfter(false);

      slideToCancelView.postDelayed(() -> future.set(null), 200);
      slideToCancelView.setVisibility(View.GONE);
      slideToCancelView.startAnimation(animation);

      return future;
    }

    public void moveTo(float x) {
      float     offset    = getOffset(x);
      Animation animation = new TranslateAnimation(Animation.ABSOLUTE, offset,
                                                   Animation.ABSOLUTE, offset,
                                                   Animation.RELATIVE_TO_SELF, 0,
                                                   Animation.RELATIVE_TO_SELF, 0);

      animation.setDuration(0);
      animation.setFillAfter(true);
      animation.setFillBefore(true);

      slideToCancelView.startAnimation(animation);
    }

    private float getOffset(float x) {
      return ViewCompat.getLayoutDirection(slideToCancelView) == ViewCompat.LAYOUT_DIRECTION_LTR ?
          -Math.max(0, this.startPositionX - x) : Math.max(0, x - this.startPositionX);
    }

  }

  private static class RecordTime implements Runnable {

    private final TextView recordTimeView;
    private final AtomicLong startTime = new AtomicLong(0);
    private final int UPDATE_EVERY_MS = 137;

    private RecordTime(TextView recordTimeView) {
      this.recordTimeView = recordTimeView;
    }

    public void display() {
      this.startTime.set(System.currentTimeMillis());
      this.recordTimeView.setText(formatElapsedTime(0));
      ViewUtil.fadeIn(this.recordTimeView, FADE_TIME);
      Util.runOnMainDelayed(this, UPDATE_EVERY_MS);
    }

    public long hide() {
      long elapsedtime = System.currentTimeMillis() - startTime.get();
      this.startTime.set(0);
      ViewUtil.fadeOut(this.recordTimeView, FADE_TIME, View.INVISIBLE);
      return elapsedtime;
    }

    @Override
    public void run() {
      long localStartTime = startTime.get();
      if (localStartTime > 0) {
        long elapsedTime = System.currentTimeMillis() - localStartTime;
        recordTimeView.setText(formatElapsedTime(elapsedTime));
        Util.runOnMainDelayed(this, UPDATE_EVERY_MS);
      }
    }

    private String formatElapsedTime(long ms)
    {
      return DateUtils.formatElapsedTime(TimeUnit.SECONDS.toSeconds(ms))
          + String.format(".%02d", ((ms/10)%100));

    }
  }

  public interface MediaListener {
    public void onMediaSelected(@NonNull Uri uri, String contentType);
  }
}
