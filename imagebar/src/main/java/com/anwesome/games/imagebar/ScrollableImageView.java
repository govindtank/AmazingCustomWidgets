package com.anwesome.games.imagebar;

import android.graphics.*;

/**
 * Created by anweshmishra on 14/03/17.
 */
public class ScrollableImageView  {
    private Bitmap bitmap;
    private float h = 0,dir = 0,w,maxH,y=0;
    private OnToggleListener onToggleListener;
    private ScrollableImageView(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
    public static ScrollableImageView newInstance(Bitmap bitmap) {
        return new ScrollableImageView(bitmap);
    }
    public void setOnToggleListener(OnToggleListener onToggleListener) {
        this.onToggleListener = onToggleListener;
    }
    public void setHeight(float w,float h) {
        this.maxH = 4*h/5;
        this.y = h/5;
        this.w = w;
        bitmap = Bitmap.createScaledBitmap(bitmap,(int)w,(int)(maxH),true);
    }
    public void draw(Canvas canvas, Paint paint) {
        Path path = new Path();
        path.addRect(new RectF(0,y,w,y+h), Path.Direction.CCW);
        canvas.clipPath(path);
        canvas.drawBitmap(bitmap,0,y,paint);
    }
    public void update() {
        h+=dir*(maxH)/10;
        if((h>=maxH) || h<0) {
            dir = 0;
            if(h>maxH) {
                h = maxH;
                if(onToggleListener!=null) {
                    onToggleListener.show();
                }
            }
            if(h<0) {
                h = 0;
                if(onToggleListener!=null){
                    onToggleListener.hide();
                }
            }
        }

    }
    public boolean stopped() {
        return dir == 0;
    }
    public void startMoving() {
        dir = h == 0?1:-1;
    }
    public int hashCode() {
        return bitmap.hashCode()+(int)(h+dir);
    }
}
