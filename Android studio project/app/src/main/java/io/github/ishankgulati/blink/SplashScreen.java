package io.github.ishankgulati.blink;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by Hackbook on 1/23/2016.
 */
public class SplashScreen {

    // screen resolution
    private int screenX;
    private int screenY;

    private Bitmap screen;

    public SplashScreen(Context context, int screenX, int screenY){
        this.screenX = screenX;
        this.screenY = screenY;

        screen = BitmapFactory.decodeResource(context.getResources(), R.drawable.splashscreen);
        screen = Bitmap.createScaledBitmap(screen, screenX, screenY, false);
    }

    public void draw(Canvas canvas, Paint paint){
        canvas.drawBitmap(screen, 0, 0, paint);
    }

    public void destroySpashScreen(){
        screen.recycle();
    }
}
