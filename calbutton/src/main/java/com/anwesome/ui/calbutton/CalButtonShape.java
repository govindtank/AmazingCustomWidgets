package com.anwesome.ui.calbutton;

import android.graphics.*;
import java.util.*;

/**
 * Created by anweshmishra on 05/04/17.
 */
public class CalButtonShape {
    private int mode = -1;
    private String mon,day;
    private float x,y,deg = 0,time = 0,a;
    private CalButtonShape(float x,float y,float a) {
        this.x = x;
        this.y = y;
        this.a = a;
        init();
    }
    private void init() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH);
        day = ""+ calendar.get(Calendar.DAY_OF_MONTH);
        mon = CalButtonUtil.months[month-1];
    }
    public static CalButtonShape getInstance(float x,float y,float a) {
        return new CalButtonShape(x,y,a);
    }
    public void draw(Canvas canvas, Paint paint) {
        canvas.save();
        canvas.translate(x,y+a);
        canvas.rotate(deg);
        paint.setColor(Color.parseColor(CalButtonUtil.MONTH_BAR_COLOR));
        canvas.drawRect(new RectF(0,-a,a,-a+a/5),paint);
        paint.setColor(Color.parseColor(CalButtonUtil.DAY_BAR_COLOR));
        paint.setTextSize(a/8);
        canvas.drawText(mon,a/2-paint.measureText(mon)/2,-a+a/10,paint);
        canvas.drawRect(new RectF(0,-a+a/5,a,0),paint);
        paint.setColor(Color.parseColor(CalButtonUtil.DAY_TEXT_COLOR));
        paint.setTextSize(3*a/5);
        canvas.drawText(day,a/2-paint.measureText(day),-3*a/5,paint);
        canvas.restore();
    }
    public void update() {
        animate();
    }
    public void animate() {
        switch(mode) {
            case 0:
                deg-=5;
                if(deg<=-45) {
                    mode = 1;
                }
                break;
            case 1:
                time++;
                if(time%10 == 0) {
                    mode = 2;
                }
                break;
            case 2:
                deg+=5;
                if(deg>=0) {
                    mode = -1;
                }
                break;
            default:
                break;
        }
    }
    public void handleTap() {
        mode = mode == -1?0:mode;
    }
    public boolean stop() {
        return mode == -1;
    }
}