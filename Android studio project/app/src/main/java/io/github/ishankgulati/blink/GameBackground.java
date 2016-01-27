package io.github.ishankgulati.blink;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by Hackbook on 1/23/2016.
 */

/**
 * This is the game background class
 */
public class GameBackground {

    // screen resolution
    private int screenX;
    private int screenY;

    private Bitmap background;

    public GameBackground(Context context, int screenX, int screenY){

        this.screenX = screenX;
        this.screenY = screenY;

        background = BitmapFactory.decodeResource(context.getResources(), R.drawable.background);
        background = Bitmap.createScaledBitmap(background, screenX, screenY, false);
    }

    public void draw(Canvas canvas, Paint paint){
        canvas.drawBitmap(background, 0, 0, paint);
    }
}
