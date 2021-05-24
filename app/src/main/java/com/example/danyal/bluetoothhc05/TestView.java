package com.example.danyal.bluetoothhc05;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class TestView extends View {


    private Paint paint;
//    private Canvas cacheCanvas;
//    private Bitmap cachebBitmap;
    private Path path;


    private TouchListener touchListener;

    public void setTouchListener(TouchListener touchListener) {
        this.touchListener = touchListener;
    }




//    public Bitmap getCachebBitmap() {
//        return cachebBitmap;
//    }

    public TestView(Context context) {
        super(context);
//        init();
    }

    public TestView(Context context, AttributeSet attrs) {
        super(context, attrs);

//        init();
    }

    public TestView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        init();
    }

//    private void init() {
//        paint = new Paint();
//        paint.setAntiAlias(true);
//        paint.setStrokeWidth(3);
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setColor(Color.BLACK);
//        path = new Path();
//
//    }

//    public void clear() {
//        if (cacheCanvas != null) {
//            paint.setColor(Color.WHITE);
//            cacheCanvas.drawPaint(paint);
//            paint.setColor(Color.BLACK);
//            cacheCanvas.drawColor(Color.WHITE);
//            invalidate();
//        }
//    }

//    @Override
//    protected void onDraw(Canvas canvas) {
//
////        canvas.drawPath(path, paint);
//    }

//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//
//        int curW = cachebBitmap != null ? cachebBitmap.getWidth() : 0;
//        int curH = cachebBitmap != null ? cachebBitmap.getHeight() : 0;
//        if (curW >= w && curH >= h) {
//            return;
//        }
//
//        if (curW < w)
//            curW = w;
//        if (curH < h)
//            curH = h;
//
//        Bitmap newBitmap = Bitmap.createBitmap(curW, curH, Bitmap.Config.ARGB_8888);
//        Canvas newCanvas = new Canvas();
//        newCanvas.setBitmap(newBitmap);
//        if (cachebBitmap != null) {
//            newCanvas.drawBitmap(cachebBitmap, 0, 0, null);
//        }
//        cachebBitmap = newBitmap;
//        cacheCanvas = newCanvas;
//    }

    private float cur_x, cur_y;
    private float downX,downY;
    private float detaX,detaY;

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
                if(touchListener !=null){
                    touchListener.onTouchDown((int)cur_x,(int)cur_y);
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
//                path.quadTo(cur_x, cur_y, x, y);
                cur_x = x;
                cur_y = y;

                detaX = x - downX;
                detaY = y - downY;

                if(touchListener !=null){
                    touchListener.onTouchMove((int)cur_x,(int)cur_y);
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                cur_x = x;
                cur_y = y;
                if(touchListener !=null){
                    touchListener.onTouchUp((int)cur_x,(int)cur_y);
                }
                //reset();
                break;
            }
        }
        invalidate();
        return true;
    }

    /**
     * 清空手势轨迹
     */
//    public void reset() {
//
//        postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                path.reset();
//                invalidate();
//            }
//        }, 500);
//
//    }
}
