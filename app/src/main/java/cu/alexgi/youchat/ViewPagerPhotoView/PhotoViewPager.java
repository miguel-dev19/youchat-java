package cu.alexgi.youchat.ViewPagerPhotoView;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

/**
 * Created by GUO on 2016/1/9.
 */
public class PhotoViewPager extends ViewPager {


    public PhotoViewPager(Context context) {
        super(context);
        super.setBackgroundColor(Color.parseColor("#CC000000"));
    }

    public PhotoViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setBackgroundColor(Color.parseColor("#CC000000"));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            return super.dispatchTouchEvent(ev);
        } catch (IllegalArgumentException ignored) {
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        return false;

    }


}
