package com.anwesome.ui.lineanddot;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.anwesome.ui.dimensionsutil.DimensionsUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by anweshmishra on 06/06/17.
 */

public class LineAndDotView extends View{
    private int time = 0,w,h,n=4;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private List<LineAndDot> lineAndDots = new ArrayList<>();
    private AnimationHandler animationHandler;
    private int color = Color.parseColor("#283593");
    private OnSelectedListener onSelectedListener;
    public void setOnSelectedListener(OnSelectedListener onSelectedListener) {
        this.onSelectedListener = onSelectedListener;
    }
    public LineAndDotView(Context context,int n) {
        super(context);
        this.n = Math.max(n,this.n);
    }
    public void onDraw(Canvas canvas) {
        if(time == 0) {
            w = canvas.getWidth();
            h = canvas.getHeight();
            float gapLine = (4*h/5)/(n+1),gapDot = (w)/(2*n+1),lineY = gapLine,dotX = 3*gapDot/2;
            for(int i=0;i<n;i++) {
                LineAndDot lineAndDot = new LineAndDot(dotX,lineY,gapDot/2);
                lineAndDot.setIndex(i+1);
                lineAndDots.add(lineAndDot);
                lineY += gapLine;
                dotX += 2*gapDot;
            }
            animationHandler = new AnimationHandler();
        }
        paint.setStrokeWidth(Math.max(w,h)/100);
        paint.setColor(color);
        for(LineAndDot lineAndDot:lineAndDots) {
            lineAndDot.draw(canvas);
        }
        time++;
        animationHandler.animate();
    }
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN && animationHandler != null) {
            animationHandler.handleTap(event.getX(),event.getY());
        }
        return true;
    }
    private class AnimationHandler {
        private ConcurrentLinkedQueue<LineAndDot> activeLineDots = new ConcurrentLinkedQueue<>();
        private boolean isAnimated = false;
        public void animate() {
            if(isAnimated) {
                for(LineAndDot lineAndDot:activeLineDots) {
                    lineAndDot.update();
                    if(lineAndDot.stopped()) {
                        int index = lineAndDot.getIndex();
                        if(onSelectedListener != null) {
                            if (lineAndDot.getPrevDir() == 1) {
                                onSelectedListener.onSelected(index);
                            } else {
                                onSelectedListener.onUnSelected(index);
                            }
                        }
                        activeLineDots.remove(lineAndDot);
                        if(activeLineDots.size() == 0) {
                            isAnimated = false;
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
        public void handleTap(float x,float y) {
            for(LineAndDot lineAndDot:lineAndDots) {
                if(lineAndDot.handleTap(x,y)) {
                    lineAndDot.startMoving();
                    activeLineDots.add(lineAndDot);
                    if(activeLineDots.size() == 1) {
                        isAnimated = true;
                        postInvalidate();
                    }
                    break;
                }
            }
        }
    }
    private class Line {
        private float y,x,lx = 0;
        private boolean stopped = true;
        public Line(float y) {
            x = w/10;
            this.y = y;
        }
        public void startMoving() {
            if(stopped) {
                stopped = false;
            }
        }
        public void update(float dir) {
            lx += (0.16f*w)*dir;
            if(lx>=0.8f*w || lx<0) {
                stopped = true;
                if(lx<0) {
                    lx = 0;
                }
            }
        }
        public boolean isStopped() {
            return stopped;
        }
        public void draw(Canvas canvas) {
            canvas.drawLine(x,y,x+lx,y,paint);
        }
        public int hashCode() {
            return (int)(y+lx);
        }
    }
    private class Dot {
        private float x,y,scale = 0,r;
        private boolean stopped = true;
        public Dot(float x,float r) {
            this.x = x;
            this.y = 9*h/10;
            this.r = r;
        }
        public void draw(Canvas canvas) {
            canvas.save();
            canvas.translate(x,y);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(0,0,r,paint);
            paint.setStyle(Paint.Style.FILL);
            canvas.save();
            canvas.scale(scale,scale);
            canvas.drawCircle(0,0,r,paint);
            canvas.restore();
            canvas.restore();
        }
        public void update(float dir) {
            scale += 0.2f*dir;
            if(scale>=1 || scale<0) {
                stopped = true;
                if(scale < 0) {
                    scale = 0;
                }
            }
        }
        public void startMoving() {
            if(stopped) {
                stopped = false;
            }
        }
        public boolean isStopped() {
            return stopped;
        }
        public int hashCode() {
            return (int)(x+scale);
        }
        public boolean handleTap(float x,float y) {
            return x>=this.x-1.5f*r && x<=this.x+1.5f*r && y>=this.y-1.5f*r && y<=this.y+1.5f*r;
        }
    }
    private class LineAndDot {
        private Line line;
        private int index = 0;
        private float dir = 0,prevDir = -1;
        private Dot dot;
        private float getPrevDir() {
            return prevDir;
        }
        public boolean stopped() {
            return dir == 0;
        }
        public LineAndDot(float x,float y,float r) {
            line = new Line(y);
            dot = new Dot(x,r);
        }
        public void setIndex(int index) {
            this.index = index;
        }
        public int getIndex() {
            return index;
        }
        public void draw(Canvas canvas) {
            line.draw(canvas);
            dot.draw(canvas);
        }
        public void startMoving() {
            dir = -1*prevDir;
            line.startMoving();
            dot.startMoving();
        }
        public void update() {
            line.update(dir);
            dot.update(dir);
            if(line.isStopped() && dot.isStopped()) {
                prevDir = dir;
                dir = 0;
            }
        }
        public int hashCode() {
            return dot.hashCode()+line.hashCode();
        }

        public boolean handleTap(float x,float y) {
            return dot!=null && dot.handleTap(x,y);
        }

    }
    public static void create(Activity activity,int n,OnSelectedListener onSelectedListener) {
        Point size = DimensionsUtil.getDeviceDimension(activity);
        int w = size.x;
        LineAndDotView lineAndDotView = new LineAndDotView(activity,n);
        lineAndDotView.setOnSelectedListener(onSelectedListener);
        lineAndDotView.setX(0);
        lineAndDotView.setY(0);
        activity.addContentView(lineAndDotView,new ViewGroup.LayoutParams(w,w));
    }
    public interface OnSelectedListener {
        void onSelected(int index);
        void onUnSelected(int index);
    }
}
