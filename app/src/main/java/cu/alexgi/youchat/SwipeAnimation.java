package cu.alexgi.youchat;

import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.view.View;
import android.view.animation.Interpolator;

public final class SwipeAnimation{
    public static final float TRIGGER_DX = ((float) dpToPx(64));
    private static final float MAX_DX = ((float) dpToPx(96));
    private static final Interpolator AVATAR_INTERPOLATOR = new ClampingLinearInterpolator(0.0f, (float) dpToPx(8));
    private static final Interpolator BUBBLE_INTERPOLATOR = new BubblePositionInterpolator(0.0f, TRIGGER_DX, MAX_DX);
    private static final float REPLY_SCALE_MAX = 1.2f;
    private static final float REPLY_SCALE_MIN = 0.8f;
    private static final float REPLY_SCALE_OVERSHOOT = 1.8f;
    private static final long REPLY_SCALE_OVERSHOOT_DURATION = 180;
    private static final Interpolator REPLY_ALPHA_INTERPOLATOR = new ClampingLinearInterpolator(0.0f, REPLY_SCALE_MIN, REPLY_SCALE_MIN);
    private static final Interpolator REPLY_SCALE_INTERPOLATOR = new ClampingLinearInterpolator(REPLY_SCALE_MIN, REPLY_SCALE_MAX);
    private static final Interpolator REPLY_TRANSITION_INTERPOLATOR = new ClampingLinearInterpolator(0.0f, (float) dpToPx(10));


    private SwipeAnimation() {
    }

    public static void update(View layout, View imgReply, float dx, float sign) {
        float progress = dx / TRIGGER_DX;
        if(layout!=null) updateBodyBubbleTransition(layout, dx, sign);
        if(imgReply!=null) updateReplyIconTransition(imgReply, dx, progress, sign);
    }

    public static void trigger(View view) {
        triggerReplyIcon(view);
    }

    private static void updateBodyBubbleTransition(View bodyBubble, float dx, float sign) {
        bodyBubble.animate().translationX(BUBBLE_INTERPOLATOR.getInterpolation(dx) * sign).setDuration(0);
//        bodyBubble.setTranslationX(BUBBLE_INTERPOLATOR.getInterpolation(dx) * sign);
    }

    private static void updateReactionsTransition(View reactionsContainer, float dx, float sign) {
        reactionsContainer.setTranslationX(BUBBLE_INTERPOLATOR.getInterpolation(dx) * sign);
    }

    private static void updateReplyIconTransition(View replyIcon, float dx, float progress, float sign) {
        if (progress > 0.05f) {
            replyIcon.animate().alpha(REPLY_ALPHA_INTERPOLATOR.getInterpolation(progress)).setDuration(0);
//            replyIcon.setAlpha(REPLY_ALPHA_INTERPOLATOR.getInterpolation(progress));
        } else {
            replyIcon.animate().alpha(0.0f);
//            replyIcon.setAlpha(0.0f);
        }
        replyIcon.animate().translationX(REPLY_TRANSITION_INTERPOLATOR.getInterpolation(progress) * sign).setDuration(0);
//        replyIcon.setTranslationX(REPLY_TRANSITION_INTERPOLATOR.getInterpolation(progress) * sign);
        if (dx < TRIGGER_DX) {
            float scale = REPLY_SCALE_INTERPOLATOR.getInterpolation(progress);
            replyIcon.setScaleX(scale);
            replyIcon.setScaleY(scale);
        }
    }

    private static void updateContactPhotoHolderTransition(View contactPhotoHolder, float progress, float sign) {
        if (contactPhotoHolder != null) {
            contactPhotoHolder.setTranslationX(AVATAR_INTERPOLATOR.getInterpolation(progress) * sign);
        }
    }

    private static void triggerReplyIcon(View replyIcon) {
        ValueAnimator animator = ValueAnimator.ofFloat(new float[]{REPLY_SCALE_MAX, REPLY_SCALE_OVERSHOOT, REPLY_SCALE_MAX});
        animator.setDuration(REPLY_SCALE_OVERSHOOT_DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                replyIcon.setScaleX(((Float) animation.getAnimatedValue()).floatValue());
                replyIcon.setScaleY(((Float) animation.getAnimatedValue()).floatValue());
            }
        });
        animator.start();
    }


    private static int dpToPx(int dp) {
        return (int) (((float) dp) * Resources.getSystem().getDisplayMetrics().density);
    }

    private static final class BubblePositionInterpolator implements Interpolator {
        private final float end;
        private final float middle;
        private final float start;

        private BubblePositionInterpolator(float start2, float middle2, float end2) {
            this.start = start2;
            this.middle = middle2;
            this.end = end2;
        }

        public float getInterpolation(float input) {
            float f = this.start;
            if (input < f) {
                return f;
            }
            float f2 = this.middle;
            if (input < f2) {
                return input;
            }
            float f3 = this.end;
            float segmentLength = f3 - f2;
            return Math.min(f2 + (segmentLength * ((input - f2) / segmentLength) * (f2 / (2.0f * input))), f3);
        }
    }

    private static final class ClampingLinearInterpolator implements Interpolator {
        private final float max;
        private final float min;
        private final float slope;
        private final float yIntercept;

        ClampingLinearInterpolator(float start, float end) {
            this(start, end, REPLY_SCALE_MIN);
        }

        ClampingLinearInterpolator(float start, float end, float scale) {
            this.slope = (end - start) * scale;
            this.yIntercept = start;
            this.max = Math.max(start, end);
            this.min = Math.min(start, end);
        }

        public float getInterpolation(float input) {
            return Math.min(Math.max((this.slope * input) + this.yIntercept, this.min), this.max);
        }
    }
}
