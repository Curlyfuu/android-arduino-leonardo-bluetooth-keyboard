package com.example.danyal.bluetoothhc05;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class TestView extends View {


    private Paint paint;

    private Path path;


    private TouchListener touchListener;

    public void setTouchListener(TouchListener touchListener) {
        this.touchListener = touchListener;
    }


    public TestView(Context context) {
        super(context);
    }

    public TestView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public TestView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private float cur_x, cur_y;
    private float downX, downY;
    private float detaX, detaY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {

                downX = x;
                downY = y;

                cur_x = x;
                cur_y = y;

//                path.moveTo(cur_x, cur_y);
                if (touchListener != null) {
                    touchListener.onTouchDown((int) cur_x, (int) cur_y);
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                cur_x = x;
                cur_y = y;

                detaX = x - downX;
                detaY = y - downY;

                if (touchListener != null) {
                    touchListener.onTouchMove((int) cur_x, (int) cur_y);
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                cur_x = x;
                cur_y = y;
                if (touchListener != null) {
                    touchListener.onTouchUp((int) cur_x, (int) cur_y);
                }
                break;
            }
        }
        invalidate();
        return true;
    }

}
