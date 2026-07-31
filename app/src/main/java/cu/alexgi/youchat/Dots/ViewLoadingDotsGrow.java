package cu.alexgi.youchat.Dots;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import cu.alexgi.youchat.YouChatApplication;

public class ViewLoadingDotsGrow extends LinearLayout {

    /* renamed from: a  reason: collision with root package name */
    boolean f9339a = false;

    /* renamed from: b  reason: collision with root package name */
    private Context f9340b;

    /* renamed from: c  reason: collision with root package name */
    private ImageView[] f9341c;
    private GradientDrawable d = new GradientDrawable();
    private ObjectAnimator[] e;

    public ViewLoadingDotsGrow(Context context) {
        super(context);
        super.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_texto()));
    }

    public ViewLoadingDotsGrow(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        super.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_texto()));
        this.f9340b = context;
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(17);
        setLayoutParams(new LayoutParams(-1, -1));
        b();
    }

    public ViewLoadingDotsGrow(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        super.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_texto()));
    }

    /* access modifiers changed from: private */
    public void a() {
        this.e = new ObjectAnimator[3];
        for (int i = 0; i < 3; i++) {
            PropertyValuesHolder ofFloat = PropertyValuesHolder.ofFloat(View.SCALE_X, new float[]{1.5f});
            PropertyValuesHolder ofFloat2 = PropertyValuesHolder.ofFloat(View.SCALE_Y, new float[]{1.5f});
            this.e[i] = ObjectAnimator.ofPropertyValuesHolder(this.f9341c[i], new PropertyValuesHolder[]{ofFloat, ofFloat2});
            this.e[i].setRepeatCount(-1);
            this.e[i].setRepeatMode(ValueAnimator.REVERSE);
            this.e[i].setDuration(300);
            this.e[i].setStartDelay((long) (i * 150));
            this.e[i].start();
        }
        //this.e[2].addListener(new ObjectAnimator());
    }

    private void b() {
        Drawable background = getBackground();
        int color = background instanceof ColorDrawable ? ((ColorDrawable) background).getColor() : -7829368;
        setBackgroundColor(0);
        removeAllViews();
        this.f9341c = new ImageView[3];
        this.d.setShape(GradientDrawable.OVAL);
        this.d.setColor(color);
        this.d.setSize(200, 200);
        LayoutParams layoutParams = new LayoutParams(0, -1);
        layoutParams.weight = 1.0f;
        LinearLayout[] linearLayoutArr = new LinearLayout[3];
        for (int i = 0; i < 3; i++) {
            linearLayoutArr[i] = new LinearLayout(this.f9340b);
            linearLayoutArr[i].setGravity(17);
            linearLayoutArr[i].setLayoutParams(layoutParams);
            this.f9341c[i] = new ImageView(this.f9340b);
            this.f9341c[i].setBackgroundDrawable(this.d);
            linearLayoutArr[i].addView(this.f9341c[i]);
            addView(linearLayoutArr[i]);
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(e==null || e.length==0) return;
        for (int i = 0; i < 3; i++) {
            if (this.e[i].isRunning()) {
                this.e[i].removeAllListeners();
                this.e[i].end();
                this.e[i].cancel();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (!this.f9339a) {
            this.f9339a = true;
            LayoutParams layoutParams = new LayoutParams(getWidth() / 6, getWidth() / 6);
            for (int i5 = 0; i5 < 3; i5++) {
                this.f9341c[i5].setLayoutParams(layoutParams);
            }
            a();
        }
    }
}
