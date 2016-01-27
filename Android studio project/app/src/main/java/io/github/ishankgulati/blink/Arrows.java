package io.github.ishankgulati.blink;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.DisplayMetrics;

/**
 * Created by Hackbook on 1/23/2016.
 */

/**
 * This class implements the arrow attack
 */

public class Arrows extends Attacks{

    // to hold the sprite
    private Bitmap bitmap;

    // damage
    private int damage = 20;

    // How many frames are there on the sprite sheet?
    private int frameCount = 1;

    private float shurikenVelocityX = 750.0f;

    public Arrows(Context context, int screenX, int screenY){
        // dimensions of object
        float length = screenY / 4;
        float height = screenY / 4;

        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.arrows);
        bitmap = Bitmap.createScaledBitmap(bitmap, (int) (frameCount * length), (int) height, false);

        setSize(length, height);
        setScreenSize(screenX, screenY);
        initializeWeapon(bitmap, frameCount, damage, shurikenVelocityX);
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        if(isAttackActive()){
            if(getDirection() == Direction.Right) {
                Bitmap temp = getBitmap();
                Bitmap flippedBitmap = flip(temp);
                setBitmap(flippedBitmap);
                super.draw(canvas, paint);
                setBitmap(temp);
            }
            else{
                super.draw(canvas, paint);
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
}
