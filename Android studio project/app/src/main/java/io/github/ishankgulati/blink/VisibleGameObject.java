package io.github.ishankgulati.blink;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

/**
 * Created by Hackbook on 1/23/2015.
 */

/**
 * This is the parent class inherited by all the game objects
 */
public class VisibleGameObject {

    // bounding rect of object
    private RectF rect = new RectF();

    // size of object
    private float length;
    private float height;

    // coordinates of object
    private float x;
    private float y;

    // starting coordinates of object
    private float initialX = 0;
    private float initialY = 0;

    // helper methods
    public void draw(Canvas canvas, Paint paint){
        canvas.drawRect(rect, paint);
    }

    public void reset(){
        setSize(length, height);
        setPosition(initialX, initialY);
    }
    public void update(long fps, long startFrameTime){}

    // setters
    public void setPosition(float x, float y){
        this.x = x;
        this.y = y;

        RectF temp = new RectF(x, y, x+length, y+height);
        setBoundingRect(temp);
    }

    public void setInitialPosition(float x, float y){
        this.x = x; initialX = x;
        this.y = y; initialY = y;
        RectF temp = new RectF(x, y, x+length, y+height);
        setBoundingRect(temp);
    }

    public void setSize(float length, float height){
        this.length = length;
        this.height = height;
    }

    public void setBoundingRect(RectF r){
        rect.left = r.left;
        rect.right = r.right;
        rect.top = r.top;
        rect.bottom = r.bottom;
    }


    // getters
    public PointF getPosition(){
        PointF loc = new PointF();
        loc.x = x;
        loc.y = y;
        return loc;
    }

    public float getHeight(){
        return height;
    }

    public float getLength(){
        return length;
    }

    public RectF getBoundingRect(){
        return rect;
    }

}
