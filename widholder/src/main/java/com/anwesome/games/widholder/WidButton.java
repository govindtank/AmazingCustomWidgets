package com.anwesome.games.widholder;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

/**
 * Created by anweshmishra on 04/03/17.
 */
public class WidButton {
    private Bitmap bitmap;
    private WidOnClickListener widOnClickListener;
    private float x,y,size,initY;
    private WidButton(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
    public static WidButton newInstance(Bitmap bitmap) {
        return new WidButton(bitmap);
    }
    public void setDimension(float x,float y,float size) {
        this.x = x;
        this.y = y;
        this.initY = y;
        this.size = size;
        bitmap = Bitmap.createScaledBitmap(bitmap,(int)size,(int)size,true);
    }
    public void setWidOnClickListener(WidOnClickListener widOnClickListener) {
        this.widOnClickListener = widOnClickListener;
    }
    public WidOnClickListener getWidOnClickListener() {
        return widOnClickListener;
    }
    public void draw(Canvas canvas, Paint paint) {
        canvas.save();
        canvas.translate(x,y);
        Path path = new Path();
        path.addRoundRect(new RectF(-size/2,-size/2,size/2,size/2),size/8,size/8, Path.Direction.CCW);
        canvas.drawBitmap(bitmap,-size/2,-size/2,paint);
        canvas.restore();
    }
    public void reset() {
        this.y = initY;
    }
    public void update() {
        y-=(initY)/5;
    }
    public boolean isStop() {
        return y<0;
    }
    public boolean handleTap(float x,float y) {
        return x>=this.x-size && x<=this.x+size && y>=this.y-size && y<=this.y+size;
    }
    public int hashCode() {
        return bitmap.hashCode()+(int)x+(int)y;
    }
}
