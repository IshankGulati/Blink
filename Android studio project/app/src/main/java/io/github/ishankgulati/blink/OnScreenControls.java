package io.github.ishankgulati.blink;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.Vector;


/**
 * Created by Hackbook on 1/23/2016.
 */
public class OnScreenControls {

    // bitmaps to hold control buttons
    private Bitmap dPad;
    private Bitmap leftButton;
    private Bitmap rightButton;
    private Bitmap upButton;
    private Bitmap downButton;
    private Bitmap blinkButton;
    private Bitmap pauseButton;

    // screen dimensions
    private int screenX;
    private int screenY;

    private float blinkButtonX;
    private float blinkButtonY;

    private float pauseButtonX;
    private float pauseButtonY;

    private float leftButtonX;
    private float leftButtonY;

    private float rightButtonX;
    private float rightButtonY;

    private float upButtonX;
    private float upButtonY;

    private float downButtonX;
    private float downButtonY;

    // control button's dimensions

    private float blinkButtonLength;
    private float blinkButtonHeight;

    private float pauseButtonLength;
    private float pauseButtonHeight;

    private float dButtonLength;
    private float dButtonHeight;

    enum controlAction {Up, Down, Left, Right, Blink, Pause}

    public class Control{
        public RectF rect;
        public controlAction action;
    }

    // to store all the control buttons
    private Vector<Control> controls = new Vector<Control>();

    public OnScreenControls(Context context, int screenX, int screenY){

        this.screenX = screenX;
        this.screenY = screenY;

        // dimensions of object
        dButtonLength = screenX / 10f;
        dButtonHeight = screenX / 10f;

        blinkButtonLength = screenX / 10f;
        blinkButtonHeight = screenX / 10f;

        pauseButtonLength = screenX / 12f;
        pauseButtonHeight = screenX / 12f;

        // position of object
        leftButtonX = screenX / 20 ;
        leftButtonY = screenY - dButtonHeight - screenY / 12;

        rightButtonX = screenX / 20 + dButtonLength + (dButtonLength / 4.0f);
        rightButtonY = screenY - dButtonHeight - screenY / 12;

        upButtonX = screenX - screenX / 20 - 2 * dButtonLength;
        upButtonY = screenY - (2 * dButtonHeight) - screenY / 12;

        downButtonX = screenX - screenX / 20 - 2 * dButtonLength;
        downButtonY = screenY - dButtonHeight - screenY / 15;

        blinkButtonX = screenX - blinkButtonLength - screenX / 22;
        blinkButtonY = screenY - blinkButtonHeight - screenY / 6;

        pauseButtonX = screenX - pauseButtonLength - screenX / 40;
        pauseButtonY = screenY / 27;

        leftButton = BitmapFactory.decodeResource(context.getResources(), R.drawable.left);
        leftButton = Bitmap.createScaledBitmap(leftButton, (int) dButtonLength,
                (int) dButtonHeight, false);

        rightButton = BitmapFactory.decodeResource(context.getResources(), R.drawable.right);
        rightButton = Bitmap.createScaledBitmap(rightButton, (int) dButtonLength,
                (int) dButtonHeight, false);

        upButton = BitmapFactory.decodeResource(context.getResources(), R.drawable.jump);
        upButton = Bitmap.createScaledBitmap(upButton, (int) dButtonLength,
                (int) dButtonHeight, false);

        downButton = BitmapFactory.decodeResource(context.getResources(), R.drawable.down);
        downButton = Bitmap.createScaledBitmap(downButton, (int) dButtonLength,
                (int) dButtonHeight, false);

        blinkButton = BitmapFactory.decodeResource(context.getResources(), R.drawable.blink);
        blinkButton = Bitmap.createScaledBitmap(blinkButton, (int) blinkButtonLength,
                (int) blinkButtonHeight, false);

        pauseButton = BitmapFactory.decodeResource(context.getResources(), R.drawable.pause);
        pauseButton = Bitmap.createScaledBitmap(pauseButton, (int) pauseButtonLength,
                (int) pauseButtonHeight, false);

        Control up = new Control();
        up.rect = new RectF(upButtonX, upButtonY, upButtonX + dButtonLength,
                upButtonY + dButtonHeight);
        up.action = controlAction.Up;
        controls.add(up);

        Control down = new Control();
        down.rect = new RectF(downButtonX, downButtonY, downButtonX + dButtonLength,
                downButtonY + dButtonHeight);
        down.action = controlAction.Down;
        controls.add(down);

        Control left = new Control();
        left.rect = new RectF(leftButtonX, leftButtonY, leftButtonX + dButtonLength,
                leftButtonY + dButtonHeight);
        left.action = controlAction.Left;
        controls.add(left);

        Control right = new Control();
        right.rect = new RectF(rightButtonX, rightButtonY, rightButtonX + dButtonLength,
                rightButtonY + dButtonHeight);
        right.action = controlAction.Right;
        controls.add(right);

        Control blink = new Control();
        blink.rect = new RectF(blinkButtonX, blinkButtonY, blinkButtonX + blinkButtonLength,
                blinkButtonY + blinkButtonHeight);
        blink.action = controlAction.Blink;
        controls.add(blink);

        Control pause = new Control();
        pause.rect = new RectF(pauseButtonX, pauseButtonY, pauseButtonX + pauseButtonLength,
                pauseButtonY + pauseButtonHeight);
        pause.action = controlAction.Pause;
        controls.add(pause);
    }

    public void draw(Canvas canvas, Paint paint){
        paint.setAlpha(180);
        canvas.drawBitmap(upButton, upButtonX, upButtonY, paint);
        canvas.drawBitmap(downButton, downButtonX, downButtonY, paint);
        canvas.drawBitmap(leftButton, leftButtonX, leftButtonY, paint);
        canvas.drawBitmap(rightButton, rightButtonX, rightButtonY, paint);
        canvas.drawBitmap(blinkButton, blinkButtonX, blinkButtonY, paint);
        canvas.drawBitmap(pauseButton, pauseButtonX, pauseButtonY, paint);
        paint.setAlpha(255);
    }

    public Vector<Control> getControlButtons(){
        return controls;
    }
}
