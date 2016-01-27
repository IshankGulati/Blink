package io.github.ishankgulati.blink;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by Hackbook on 1/24/2016.
 */
public class HelpMenu {

    // screen dimensions
    private int screenX;
    private int screenY;

    private Bitmap menu;

    public HelpMenu(Context context, int screenX, int screenY){
        this.screenX = screenX;
        this.screenY = screenY;

        // loading and scaling images
        menu = BitmapFactory.decodeResource(context.getResources(), R.drawable.help);
        menu = Bitmap.createScaledBitmap(menu, screenX, screenY, false);
    }

    public void draw(Canvas canvas, Paint paint){
        canvas.drawBitmap(menu, 0, 0, paint);
    }
}
