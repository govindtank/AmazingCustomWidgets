package com.anwesome.games.bicirccolorview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by anweshmishra on 26/06/17.
 */

public class BiCircColorView extends View {
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int time = 0,w,h,gapDeg = 0;
    private int[] colors;
    public BiCircColorView(Context context,int[] colors) {
        super(context);
        this.colors = colors;
    }
    public void onDraw(Canvas canvas) {
        if(time == 0) {
            w = canvas.getWidth();
            h = canvas.getHeight();
            if(colors.length > 0) {
                gapDeg = 360/colors.length;
            }
        }
        time++;
    }
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {

        }
        return true;
    }
    private class BiCircColor {
        private int index,sweepDeg = 0,startDeg = 0,dir=0;
        public BiCircColor(int index) {
            this.index = index;
            startDeg = gapDeg*index;
        }
        public void draw(Canvas canvas) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(colors[index]);
            paint.setStrokeWidth(w/60);
            int r = Math.min(w,h)/2;
            canvas.drawArc(new RectF(-r,-r,r,r),startDeg,sweepDeg,false,paint);
            canvas.drawArc(new RectF(-r,-r,r,r),startDeg+gapDeg-sweepDeg,sweepDeg,false,paint);
        }
        public void update() {
            this.sweepDeg += (this.dir)*(gapDeg/10);
            if(this.sweepDeg> gapDeg/2) {
                this.dir = 0;
                this.sweepDeg = gapDeg/2;
            }
            if(this.sweepDeg < 0) {
                this.dir = 0;
                this.sweepDeg = 0;
            }
        }
        public boolean stopped() {
            return dir == 0;
        }
        public int hashCode() {
            return index;
        }
        public void startUpdating(int dir) {
            this.dir = dir;
        }
    }
    private class AnimationHandler {
        private BiCircColor curr,prev;
        private int i = 0;
        private boolean isAnimated = false;
        public void animate(Canvas canvas) {
            if(isAnimated) {
                if(prev!=null) {
                    prev.draw(canvas);
                    prev.update();
                }
                if(curr != null) {
                    curr.draw(canvas);
                    curr.update();
                    if(curr.stopped()) {
                        i++;
                        i%=colors.length;
                        prev = curr;
                        curr = null;
                        isAnimated = false;
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
        public void startAnimation() {
            if(!isAnimated && curr == null) {
                if(prev != null) {
                    prev.startUpdating(-1);
                }
                if(curr!=null) {
                    curr.startUpdating(1);
                }
                isAnimated = true;
                postInvalidate();
            }
        }
    }
}
