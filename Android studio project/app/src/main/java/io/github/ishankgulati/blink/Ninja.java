package io.github.ishankgulati.blink;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;

import java.util.Arrays;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import java.util.Random;

/**
 * Created by Hackbook on 1/23/2016.
 */

/**
 * This is the game player class
 */
public class Ninja extends VisibleGameObject{

    // states of movement
    public enum NinjaState {Idle, Run, Jump, Slide, Dead, Blink}

    // direction of movement
    public enum NinjaDirection {Up, Down, Left, Right, Nowhere}

    // direction towards ninja is facing
    public enum NinjaView {Left, Right}

    // current movement state
    private NinjaState ninjaState;

    // current movement direction
    private NinjaDirection ninjaDirection;

    // current facing direction
    private NinjaView ninjaView;

    // screen dimensions
    private int screenX;
    private int screenY;

    // y co-ordinate of surface
    private float surfaceY;

    // velocity of character
    private final float ninjaVelocityX = 320.0f;

    private float jumpVelocityY = 0;

    // velocity in x direction while jumping
    private final float jumpVelocityX = 280.0f;

    // initial momentum given at the start of jump
    private final float jumpMomentum = -360.0f;

    // velocity when starts to slide
    private final float initialSlideVelocity = 400.0f;

    private float slideVelocity = 0;

    private boolean runAfterLanding = false;

    // gravitational constant
    private final float gravity = 900.0f;

    // to stop ninja after sliding
    private final float friction = 11.0f;

    // data structure for storing sprites
    ConcurrentHashMap<NinjaState, Bitmap[]> spriteHolder = new ConcurrentHashMap<NinjaState, Bitmap[]>();

    // number of sprites per movement state
    private final int spritesPerState = 10;

    // start at the first frame
    private int currentSprite = 0;

    // What time was it when we last changed frames
    private long lastFrameChangeTime = 0;

    // How long should each frame last
    private int spriteLengthInMilliseconds = 70;

    // to generate new position
    Random generator = new Random();

    // How long should character disappear
    private final long blinkThreshold = 500;

    // What time was it when character disappeared
    private long lastBlinkTime = 0;

    // maximum number of times a character can blink without recharge
    private final int maxBlinks = 5;

    // recharge time of a blink
    private final long blinkRechargeTime = 10000;

    // number of blinks left
    private int currentBlinks = maxBlinks;

    // blinks to be recharged
    private int tobeRecharged = 0;

    // time elapsed since start
    private long elapsedTimeSinceStart = 0;

    Paint textPaint = new Paint();

    // strength / life of player
    private int health = 100;

    // coordinates for gui
    private int healthX;
    private int healthY;
    private int blinkX;
    private int blinkY;

    public Ninja(Context context, int screenX, int screenY){

        this.screenX = screenX;
        this.screenY = screenY;

        // dimensions of object
        float length = screenX / 14;
        float height = screenY / 7;

        surfaceY = 774.0f / 1080.0f * screenY;

        // position of object
        float x = screenX / 2 - length / 2;
        float y = surfaceY - height;


        Bitmap bitmap;

        // movement state of character
        String state[];
        state = new String[] {"idle", "run", "jump", "slide", "dead"};

        int motions = state.length;

        for(int i = 0; i < motions; i++) {

            // temporary array for storing Bitmaps of a particular motion
            Bitmap bitmapVector[] = new Bitmap[spritesPerState];

            for(int j = 0; j < spritesPerState; j++) {
                String spriteName = state[i] + "__00" + j;

                // get the id of drawable
                int id = context.getResources().getIdentifier(spriteName, "drawable",
                        context.getPackageName());

                // load and scale bitmap
                bitmap = BitmapFactory.decodeResource(context.getResources(), id);
                bitmap = Bitmap.createScaledBitmap(bitmap, (int)length, (int)height, false);

                // add bitmap to temporary vector
                bitmapVector[j] = bitmap;
            }

            NinjaState name = NinjaState.Idle;
            if(state[i].equals("idle")){
                name = NinjaState.Idle;
            }
            if(state[i].equals("run")){
                name = NinjaState.Run;
            }
            if(state[i].equals("jump")){
                name = NinjaState.Jump;
            }
            if(state[i].equals("slide")){
                name = NinjaState.Slide;
            }
            if(state[i].equals("dead")){
                name = NinjaState.Dead;
            }
            spriteHolder.put(name, bitmapVector);
        }

        setSize(length, height);
        setInitialPosition(x, y);

        ninjaState = NinjaState.Idle;
        ninjaDirection = NinjaDirection.Right;
        ninjaView = NinjaView.Right;

        textPaint.setColor(Color.WHITE);
        textPaint.setAlpha(150);
        textPaint.setTextSize(screenY / 28);
        textPaint.setTypeface(BlinkView.font);


        String text = "Blinks: 00";
        Rect bounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), bounds);

        blinkX = screenX / 4 - (bounds.width() / 2);
        blinkY = screenY / 25;

        text = "Health: 00";
        textPaint.getTextBounds(text, 0, text.length(), bounds);
        healthX = screenX / 2 - (bounds.width() / 2);
        healthY = screenY / 25;
    }

    // overrided methods to control game object
    @Override
    public void update(long fps, long deltaTimeInMS){

        // recharging blinks
        elapsedTimeSinceStart += deltaTimeInMS;
        if(elapsedTimeSinceStart > blinkRechargeTime){
            if(currentBlinks < maxBlinks && tobeRecharged > 0){
                currentBlinks ++;
                tobeRecharged --;

                if(tobeRecharged > 0){
                    elapsedTimeSinceStart = 0;
                }
            }
        }

        // ninja is jumping but direction key is pressed
        //so run after landing on ground
        if(runAfterLanding && isNinjaOnSurface()){
            ninjaState = NinjaState.Run;
            runAfterLanding = false;
        }

        PointF loc = getPosition();

        // check collision with left wall
        if (loc.x < 0.0f) {
            if(ninjaState == NinjaState.Run) {
                if (ninjaDirection == NinjaDirection.Left) {
                    ninjaState = NinjaState.Idle;
                }
                else if (ninjaDirection == NinjaDirection.Right) {
                    ninjaState = NinjaState.Run;
                }
            }

            if(ninjaState == NinjaState.Jump || ninjaState == NinjaState.Blink){
                ninjaState = NinjaState.Jump;
                jumpVelocityY = 0f;
                ninjaDirection = NinjaDirection.Up;
            }

            loc.x = 2.0f;
        }

        // check collision with right wall
        if (loc.x + getLength() > screenX) {
            if(ninjaState == NinjaState.Run) {
                if (ninjaDirection == NinjaDirection.Right) {
                    ninjaState = NinjaState.Idle;
                }
                else if (ninjaDirection == NinjaDirection.Left) {
                    ninjaState = NinjaState.Run;
                }
            }

            if(ninjaState == NinjaState.Jump || ninjaState == NinjaState.Blink){
                ninjaState = NinjaState.Jump;
                jumpVelocityY = 0f;
                ninjaDirection = NinjaDirection.Up;
            }

            loc.x = screenX - getLength() - 2;
        }

        // set new location if ninja is running
        if (ninjaState == NinjaState.Run && ninjaDirection == NinjaDirection.Right) {
            loc.x = loc.x + ninjaVelocityX / fps;
        }

        if(ninjaState == NinjaState.Run && ninjaDirection == NinjaDirection.Left){
            loc.x = loc.x - ninjaVelocityX / fps;
        }

        // make ninja jump
        if(ninjaState == NinjaState.Jump){
            float deltaTime = deltaTimeInMS / 1000.0f;

            jumpVelocityY += (gravity * deltaTime / 2);
            loc.y += jumpVelocityY * deltaTime;
            jumpVelocityY += (gravity * deltaTime/ 2);

            if(ninjaDirection == NinjaDirection.Right){
                loc.x = loc.x + jumpVelocityX / fps;
            }

            if(ninjaDirection == NinjaDirection.Left){
                loc.x = loc.x - jumpVelocityX / fps;
            }

            if(loc.y > surfaceY - getHeight()){
                loc.y = surfaceY - getHeight();
                jumpVelocityY = 0;
                ninjaState = NinjaState.Idle;
                loc.y = surfaceY - getHeight();
            }
        }

        // make ninja slide
        if(ninjaState == NinjaState.Slide){
            if(ninjaDirection == NinjaDirection.Right){
                loc.x = loc.x + slideVelocity / fps;
                slideVelocity -= friction;
            }

            if(ninjaDirection == NinjaDirection.Left){
                loc.x = loc.x - slideVelocity / fps;
                slideVelocity -= friction;
            }

            if(slideVelocity < 0){
                slideVelocity = 0;
            }
        }

        // if blinked for a time period above threshold, reappear at a random position
        if(ninjaState == NinjaState.Blink){
            if(System.currentTimeMillis() - lastBlinkTime > blinkThreshold){
                int lowx = (int)getLength() + 5;
                int highx = (int)(screenX - getLength() - 5);
                loc.x = lowx + generator.nextInt(highx - lowx);

                int lowy = (int)getHeight();
                int highy = (int)(surfaceY - getHeight());
                loc.y = lowy + generator.nextInt(highy - lowy);
                ninjaState = NinjaState.Jump;
                BlinkView.getSoundManager().playSound("appear");
            }
        }

        if(ninjaState == NinjaState.Dead){
            if(loc.y < surfaceY - getHeight()){
                loc.y += jumpVelocityY / fps;
                jumpVelocityY += gravity;
            }
        }

        // update location of ninja
        setPosition(loc.x, loc.y);
    }

    @Override
    public void draw(Canvas canvas, Paint paint){
        if(ninjaState != NinjaState.Blink) {

            long time = System.currentTimeMillis();
            PointF loc = getPosition();

            if (ninjaState == NinjaState.Idle) {
                spriteLengthInMilliseconds = 150;
            }

            if (ninjaState == NinjaState.Run) {
                spriteLengthInMilliseconds = 70;
            }

            if (ninjaState == NinjaState.Jump) {
                spriteLengthInMilliseconds = 80;
            }

            if (ninjaState == NinjaState.Slide) {
                spriteLengthInMilliseconds = 120;
            }

            if (ninjaState == NinjaState.Dead) {
                spriteLengthInMilliseconds = 200;
            }

            Bitmap bitmap = spriteHolder.get(ninjaState)[currentSprite];

            if (ninjaView == NinjaView.Left) {
                bitmap = flip(bitmap);
            }
            canvas.drawBitmap(bitmap, loc.x, loc.y, paint);
            updateCurrentSprite(time);
        }

        String text = "Blinks: " + currentBlinks;
        canvas.drawText(text, blinkX, blinkY, textPaint);

        text = "Health: " + health;
        canvas.drawText(text, healthX, healthY, textPaint);
    }

    @Override
    public void reset(){
        super.reset();
        ninjaState = NinjaState.Idle;
        ninjaDirection = NinjaDirection.Right;
        ninjaView = NinjaView.Right;
        health = 100;
        currentBlinks = maxBlinks;
        tobeRecharged = 0;
    }

    // helper functions
    private void updateCurrentSprite(long time){
        if(time > lastFrameChangeTime + spriteLengthInMilliseconds){

            currentSprite++;
            lastFrameChangeTime = time;

            if(ninjaState == NinjaState.Dead && currentSprite >= spritesPerState){
                currentSprite = spritesPerState - 1;
            }
            else if(currentSprite >= spritesPerState){
                currentSprite = 0;
            }
        }
    }

    private Bitmap flip(Bitmap src) {
        Matrix flipper = new Matrix();
        flipper.preScale(-1, 1);
        Bitmap dst = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), flipper, false);
        dst.setDensity(DisplayMetrics.DENSITY_DEFAULT);
        return dst;
    }

    // disappear on press of button
    public void blink(NinjaDirection direction){
        if(ninjaState != NinjaState.Blink && currentBlinks > 0){
            ninjaState = NinjaState.Blink;
            lastBlinkTime = System.currentTimeMillis();
            ninjaDirection = direction;
            BlinkView.getSoundManager().playSound("blink");
            if(direction == NinjaDirection.Right){
                ninjaView = NinjaView.Right;
            }
            else if(direction == NinjaDirection.Left){
                ninjaView = NinjaView.Left;
            }

            currentBlinks --;
            tobeRecharged ++;

            if(!isNinjaRecharging() && tobeRecharged > 0){
                elapsedTimeSinceStart = 0;
            }
        }
    }

    // reappear at given position
    public void blink(float x, float y){
        if(System.currentTimeMillis() - lastBlinkTime < blinkThreshold
                && y < surfaceY - getHeight()){
            setPosition(x, y);
            currentSprite = 0;
            ninjaState = NinjaState.Jump;
            BlinkView.getSoundManager().playSound("appear");
        }
    }

    public void jump(NinjaDirection direction){
        if(ninjaState != NinjaState.Jump && ninjaState != NinjaState.Blink) {
            ninjaState = NinjaState.Jump;
            jumpVelocityY = jumpMomentum;
            currentSprite = 0;
            ninjaDirection = direction;

            if(direction == NinjaDirection.Right){
                ninjaView = NinjaView.Right;
            }
            else if(direction == NinjaDirection.Left){
                ninjaView = NinjaView.Left;
            }
        }
    }

    public void slide(NinjaDirection direction){
        if(ninjaState != NinjaState.Slide && ninjaState != NinjaState.Jump &&
                ninjaState != NinjaState.Blink) {
            ninjaState = NinjaState.Slide;
            currentSprite = 0;
            slideVelocity = initialSlideVelocity;
            ninjaDirection = direction;

            if(direction == NinjaDirection.Right){
                ninjaView = NinjaView.Right;
            }
            else if(direction == NinjaDirection.Left){
                ninjaView = NinjaView.Left;
            }
        }
    }

    public void run(NinjaDirection direction){
        if(ninjaState == NinjaState.Idle) {
            ninjaState = NinjaState.Run;
            ninjaDirection = direction;

            if(direction == NinjaDirection.Right){
                ninjaView = NinjaView.Right;
            }
            else if(direction == NinjaDirection.Left){
                ninjaView = NinjaView.Left;
            }
        }
    }

    public boolean isNinjaOnSurface(){
        PointF loc = getPosition();
        if(loc.y + getHeight() > surfaceY - 0.0001 && loc.y + getHeight() < surfaceY + 0.0001){
            return true;
        }
        return false;
    }


    private boolean isNinjaRecharging(){
        if(currentBlinks < maxBlinks && elapsedTimeSinceStart < blinkRechargeTime){
            return true;
        }
        return false;
    }

    public boolean isNinjaVisible(){
        return ninjaState != NinjaState.Blink;
    }

    public void runAfterLanding(NinjaDirection direction){
        setDirection(direction);
        runAfterLanding = true;
    }

    public void cancelFutureStateAllocations(){
        runAfterLanding = false;
    }

    public void loseHealth(int damage){
        health -= damage;
        if(health < 0){
            health = 0;
        }
        if(health == 0){
            ninjaState = NinjaState.Dead;
        }
    }

    public boolean checkEndOfGame(){
        //return true if health drops to zero
        return ninjaState == NinjaState.Dead;
    }

    //getters
    public NinjaState getState(){ return ninjaState; }

    public Bitmap getBitmap(){
        return spriteHolder.get(ninjaState)[currentSprite];
    }

    // setters
    public void setState( NinjaState state ){ ninjaState = state; }

    public void setDirection( NinjaDirection direction ){
        ninjaDirection = direction;

        if(direction == NinjaDirection.Right){
            ninjaView = NinjaView.Right;
        }
        else if(direction == NinjaDirection.Left){
            ninjaView = NinjaView.Left;
        }
    }
}
