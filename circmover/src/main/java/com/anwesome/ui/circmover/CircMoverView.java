package com.anwesome.ui.circmover;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.anwesome.ui.dimensionsutil.DimensionsUtil;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by anweshmishra on 14/06/17.
 */

public class CircMoverView extends View {
    private int n=3,time = 0,w,h;
    private GestureDetector gestureDetector;
    private ConcurrentLinkedQueue<CircMover> circMovers = new ConcurrentLinkedQueue<>();
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private AnimationHandler animationHandler;
    private OnMovementCompletionListener onMovementCompletionListener;
    public CircMoverView(Context context,int n) {
        super(context);
        this.n = Math.max(n,this.n);
        gestureDetector = new GestureDetector(context,new CustomGestureListener());
    }
    public void setOnMovementCompletionListener(OnMovementCompletionListener onMovementCompletionListener) {
        this.onMovementCompletionListener = onMovementCompletionListener;
    }
    public void onDraw(Canvas canvas) {
        if(time == 0) {
            w = canvas.getWidth();
            h = canvas.getHeight();
            int gap = h/(n+1),y = gap;
            for(int i=0;i<n;i++) {
                circMovers.add(new CircMover(y,gap));
                y+=gap;
            }
            animationHandler = new AnimationHandler();
        }
        for(CircMover circMover:circMovers) {
            circMover.draw(canvas);
        }
        time++;
        if(animationHandler != null) {
            animationHandler.animate();
        }
    }
    public boolean onTouchEvent(MotionEvent event) {
        if(gestureDetector != null) {
            return gestureDetector.onTouchEvent(event);
        }
        return true;
    }
    private class CircMover {
        private float x,y,h,dir=0;
        public CircMover(float y,float size) {
            this.y = y;
            this.x = 0;
            this.h = size;
        }
        public void draw(Canvas canvas) {
            paint.setStrokeWidth(h/15);
            canvas.save();
            canvas.translate(0,y);
            canvas.drawLine(0,0,w,0,paint);
            canvas.drawCircle(x+h/5,0,h/5,paint);
            canvas.restore();
        }
        public boolean handleTap(float y) {
            return y>=this.y-h/2 && y<=this.y+h/2 && dir == 0;
        }
        public boolean stopped() {
            return dir == 0;
        }
        public void setDir(float dir) {
            if(x <= 0 && dir == 1) {
                this.dir = 1;
            }
            if(x >= w-2*h/5 && dir == -1) {
                this.dir = -1;
            }
        }
        public void update() {
            x+=(w/8)*dir;
            if(x > w-2*h/5) {
                x = w-2*h/5;
                dir = 0;
            }
            if(x < 0) {
                x = 0;
                dir = 0;
            }
            if(onMovementCompletionListener != null && dir == 0) {
                int index = (int)(y/h);
                if(x <= 0) {
                    onMovementCompletionListener.onMoveToLeft(index);
                }
                if(x >= w-2*h/5) {
                    onMovementCompletionListener.onMoveToRight(index);
                }
            }
        }
        public int hashCode() {
            return (int)(y+x+dir);
        }
    }
    private class CustomGestureListener extends GestureDetector.SimpleOnGestureListener {
        public boolean onDown(MotionEvent event) {
            return true;
        }
        public boolean onSingleTapUp(MotionEvent event) {
            return true;
        }
        public boolean onFling(MotionEvent e1,MotionEvent e2,float velx,float vely) {
            float y = e1.getY();
            if(Math.abs(velx)>Math.abs(vely) && e1.getX() != e2.getX()) {
                float dir = (e2.getX()-e1.getX());
                dir /= Math.abs(dir);
                for(CircMover circMover:circMovers) {
                    if(circMover.handleTap(y)) {
                        circMover.setDir(dir);
                        if(animationHandler != null) {
                            animationHandler.addSwipedMover(circMover);
                        }
                    }
                }
            }
            return true;
        }
    }
    private class AnimationHandler {
        private boolean isRunning = false;
        private ConcurrentLinkedQueue<CircMover> swipedMovers = new ConcurrentLinkedQueue<>();
        public void animate() {
            if(isRunning) {
                for(CircMover swipedMover:swipedMovers) {
                    swipedMover.update();
                    if(swipedMover.stopped()) {
                        swipedMovers.remove(swipedMover);
                        if(swipedMovers.size() == 0) {
                            isRunning = false;
                        }
                    }
                }
                try {
                    Thread.sleep(50);
                    invalidate();
                }
                catch (Exception ex) {

                }
            }
        }
        public void addSwipedMover(CircMover circMover) {
            swipedMovers.add(circMover);
            if(swipedMovers.size() == 1 && !isRunning) {
                isRunning = true;
                postInvalidate();
            }
        }
    }
    public static void create(Activity activity,int n,OnMovementCompletionListener onMovementCompletionListener) {
        Point size = DimensionsUtil.getDeviceDimension(activity);
        int w = size.x,h = size.y;
        CircMoverView circMoverView = new CircMoverView(activity,n);
        circMoverView.setOnMovementCompletionListener(onMovementCompletionListener);
        activity.addContentView(circMoverView,new ViewGroup.LayoutParams(w,(h/10)*(Math.max(n,3)+1)));

    }
    public interface OnMovementCompletionListener {
        void onMoveToRight(int index);
        void onMoveToLeft(int index);
    }
}
