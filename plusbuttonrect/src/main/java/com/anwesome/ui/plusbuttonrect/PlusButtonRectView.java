package com.anwesome.ui.plusbuttonrect;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.anwesome.ui.dimensionsutil.DimensionsUtil;

/**
 * Created by anweshmishra on 11/06/17.
 */

public class PlusButtonRectView extends View {
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int time = 0,w,h;
    private PlusButtonGroup plusButtonGroup;
    private RectShape rectShape;
    private AnimationHandler animationHandler;
    public PlusButtonRectView(Context context) {
        super(context);
    }
    public void onDraw(Canvas canvas) {
        if(time == 0) {
            w = canvas.getWidth();
            h = canvas.getHeight();
            plusButtonGroup = new PlusButtonGroup();
            rectShape = new RectShape();
            animationHandler = new AnimationHandler();
        }
        rectShape.draw(canvas);
        plusButtonGroup.draw(canvas);
        time++;
    }
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN && animationHandler != null) {
            animationHandler.start();
        }
        return true;
    }
    public void update(float factor) {
        plusButtonGroup.update(factor);
        rectShape.update(factor);
        postInvalidate();
    }
    private class PlusButton {
        private float rot = 0;
        public void draw(Canvas canvas,float x,float y) {
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeCap(Paint.Cap.ROUND);
            canvas.save();
            canvas.translate(x,y);
            canvas.rotate(rot);
            canvas.drawCircle(0,0,w/20,paint);
            paint.setColor(Color.WHITE);
            paint.setStrokeWidth(w/60);
            for(int i=0;i<2;i++) {
                canvas.save();
                canvas.rotate(i*90);
                canvas.drawLine(-w/30,0,w/30,0,paint);
                canvas.restore();
            }
            canvas.restore();
        }
        public void update(float factor) {
            rot = 45*factor;
        }
    }
    private class RectShape {
        private float scale = 0;
        public void draw(Canvas canvas) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.BLACK);
            canvas.save();
            canvas.translate(w/2,h/2);
            float r = Math.max(w,h)/10;
            canvas.scale(scale,scale);
            canvas.drawRoundRect(new RectF(-w/3,-h/3,w/3,h/3),r,r,paint);
            canvas.restore();
        }
        public void update(float factor) {
            scale = factor;
        }
    }
    private class PlusButtonGroup {
        private float x=0,y=0;
        private PlusButton plusButton;
        public PlusButtonGroup() {
            plusButton = new PlusButton();
        }
        public void draw(Canvas canvas) {
            for(int i=0;i<4;i++) {
                canvas.save();
                canvas.translate(w/2,h/2);
                canvas.rotate(i*90);
                plusButton.draw(canvas,x,y);
                canvas.restore();
            }
        }
        public void update(float factor) {
            x = (-w/3+w/40)*factor;
            y = (-h/3+w/40)*factor;
            plusButton.update(factor);
        }
    }
    private class AnimationHandler extends AnimatorListenerAdapter implements ValueAnimator.AnimatorUpdateListener {
        private ValueAnimator startAnim = ValueAnimator.ofFloat(0,1),endAnim = ValueAnimator.ofFloat(1,0);
        private int dir = 0;
        private boolean isAnimated = false;
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            update((float)valueAnimator.getAnimatedValue());
        }
        public void onAnimationEnd(Animator animator) {
            if(isAnimated) {
                dir = dir == 0?1:0;
                if(dir == 1 && onExpandListener != null) {
                    onExpandListener.onExpand();
                }
                isAnimated = false;
            }
        }
        public AnimationHandler() {
            startAnim.setDuration(500);
            endAnim.setDuration(500);
            startAnim.addUpdateListener(this);
            endAnim.addUpdateListener(this);
            startAnim.addListener(this);
            endAnim.addListener(this);
        }
        public void start() {
            if(!isAnimated) {
                if(dir == 0) {
                    startAnim.start();
                }
                else {
                    endAnim.start();
                }
                isAnimated = true;
            }
        }
    }
    public static void create(Activity activity,OnExpandListener onExpandListener) {
        PlusButtonRectView plusButtonRectView = new PlusButtonRectView(activity);
        plusButtonRectView.setOnExpandListener(onExpandListener);
        Point size = DimensionsUtil.getDeviceDimension(activity);
        int  w = size.x;
        activity.addContentView(plusButtonRectView,new ViewGroup.LayoutParams(w,w));
    }
    private OnExpandListener onExpandListener;
    public void setOnExpandListener(OnExpandListener onExpandListener) {
        this.onExpandListener = onExpandListener;
    }
    public interface OnExpandListener {
        void onExpand();
    }
}
