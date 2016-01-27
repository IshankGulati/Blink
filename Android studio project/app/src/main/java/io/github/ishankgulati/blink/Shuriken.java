package io.github.ishankgulati.blink;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import java.util.Random;

/**
 * Created by Hackbook on 1/23/2016.
 */

/**
 * This class implements the shuriken attack
 */
public class Shuriken extends Attacks{

    // to hold the sprite
    private Bitmap bitmap;

    // damage
    private int damage = 10;

    // How many frames are there on the sprite sheet?
    private int frameCount = 10;

    private float shurikenVelocityX = 700.0f;

    public Shuriken(Context context, int screenX, int screenY){

        // dimensions of object
        float length = screenY / 18;
        float height = screenY / 18;

        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.shuriken);
        bitmap = Bitmap.createScaledBitmap(bitmap, (int) (frameCount * length), (int) height, false);

        setSize(length, height);
        setScreenSize(screenX, screenY);
        initializeWeapon(bitmap, frameCount, damage, shurikenVelocityX);
    }

    @Override
    public void activate(float targetHeight, float targetY){
        super.activate(targetHeight, targetY);
    }
}
