package io.github.ishankgulati.blink;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import java.util.Random;

/**
 * Created by Hackbook on 1/23/2016.
 */

/**
 * This is the super class for all the attacks
 */
public class Attacks extends VisibleGameObject{

    // to hold the sprite
    private Bitmap bitmap;

    // screen dimensions
    private int screenX;
    private int screenY;

    protected enum Direction {Left, Right}

    // direction of weapon
    private Direction direction;

    // damage
    private int damage = 10;

    // wether weapon is active
    private boolean isActive = false;

    private Random generator = new Random();

    // How many frames are there on the sprite sheet?
    private int spriteCount = 1;

    // Start at the first frame
    private int currentSprite = 0;

    // What time was it when we last changed frames
    private long lastFrameChangeTime = 0;

    // How long should each frame last
    private int spriteLengthInMilliseconds = 20;

    private float velocityX = 700.0f;

    // A rectangle to define an area of the
    // sprite sheet that represents 1 frame
    private Rect frameToDraw;

    // A rect that defines an area of the screen
    // on which to draw
    private Rect whereToDraw;


    @Override
    public void update(long fps, long deltaTimeInMS){
        if(isActive) {
            PointF loc = getPosition();
            if(direction == Direction.Right) {
                loc.x = loc.x + velocityX / fps;
                if(loc.x > screenX - getLength()){
                    isActive = false;
                }
            }
            else if(direction == Direction.Left){
                loc.x = loc.x - velocityX / fps;
                if(loc.x <= 0){
                    isActive = false;
                }
            }
            setPosition(loc.x, loc.y);
        }
    }

    @Override
    public void draw(Canvas canvas, Paint paint){
        if(isActive) {
            PointF loc = getPosition();
            long time = System.currentTimeMillis();

            whereToDraw.set((int) loc.x, (int) loc.y, (int) (loc.x + getLength()),
                    (int)(loc.y + getHeight()));

            getCurrentFrame(time);
            canvas.drawBitmap(bitmap, frameToDraw, whereToDraw, paint);
        }
    }

    private void getCurrentFrame(long time){

        if ( time > lastFrameChangeTime + spriteLengthInMilliseconds) {
            lastFrameChangeTime = time;
            currentSprite++;
            if (currentSprite >= spriteCount) {
                currentSprite = 0;
            }
        }

        //update the left and right values of the source of
        //the next frame on the spritesheet
        frameToDraw.left = currentSprite * (int)getLength();
        frameToDraw.right = (int)(frameToDraw.left + getLength());
    }

    public void initializeWeapon(Bitmap bitmap, int frameCount, int damage, float velocity){
        this.bitmap = bitmap;
        spriteCount = frameCount;
        this.damage = damage;
        velocityX = velocity;

        frameToDraw = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        whereToDraw =  new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
    }

    // start the attack
    public void activate(float targetHeight, float targetY){
        if(!isActive) {
            isActive = true;

            int low = (int) (targetY - getHeight());
            int high = (int) (targetY + targetHeight - getHeight());

            int y = generator.nextInt(high - low) + low;

            // if true launch weapon to right side
            // else opposite
            boolean launchDirection = generator.nextBoolean();

            if (launchDirection) {
                setPosition(0, y);
                direction = Direction.Right;
            } else {
                setPosition(screenX - getLength(), y);
                direction = Direction.Left;
            }
        }
    }

    //stop the attack
    public void deActivate(){
        if(isActive){
            isActive = false;
        }
    }

    // return true if attack is going on
    public boolean isAttackActive(){
        return isActive;
    }

    // getters
    // get the amount of damage done by weapon
    public int getDamagePoints(){
        return damage;
    }

    public Bitmap getBitmap(){
        return bitmap;
    }

    public Direction getDirection(){
        return direction;
    }

    // setters
    public void setScreenSize(int screenX, int screenY){
        this.screenX = screenX;
        this.screenY = screenY;
    }

    public void setBitmap(Bitmap b){
        bitmap = b;
    }
}
