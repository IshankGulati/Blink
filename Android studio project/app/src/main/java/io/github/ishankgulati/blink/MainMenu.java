package io.github.ishankgulati.blink;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.Vector;

/**
 * Created by Hackbook on 1/23/2015.
 */
public class MainMenu {

    // screen dimensions
    private int screenX;
    private int screenY;

    private Bitmap menu;

    // possible result of press of button
    public enum MenuResult { Play, Help }

    public class MenuItem{
        public RectF rect;
        public MenuResult action;
    }

    // buttons
    MenuItem playButton;

    MenuItem helpButton;

    // data structure to hold menu buttons
    private Vector<MenuItem> menuItems = new Vector<MenuItem>();

    // dimensions of buttons
    float length;
    float height;

    // factors to scale buttons by current screen resolution
    float scalingFactorX;
    float scalingFactorY;


    MainMenu(Context context, int screenX, int screenY){

        this.screenX = screenX;
        this.screenY = screenY;

        length = screenX;
        height = screenY;

        scalingFactorX = screenX / 1920.0f;
        scalingFactorY = screenY / 1074.0f;

        // loading and scaling images
        menu = BitmapFactory.decodeResource(context.getResources(), R.drawable.menu);
        menu = Bitmap.createScaledBitmap(menu, (int) length, (int) height, false);

        playButton = new MenuItem();
        playButton.rect = new RectF(740 * scalingFactorX, 482 * scalingFactorY, 1182 *scalingFactorX,
                634 * scalingFactorY);
        playButton.action = MenuResult.Play;
        menuItems.addElement(playButton);

        helpButton = new MenuItem();
        helpButton.rect = new RectF(740 * scalingFactorX, 718 * scalingFactorY, 1182 *scalingFactorX,
                872 * scalingFactorY);
        helpButton.action = MenuResult.Help;
        menuItems.addElement(helpButton);
    }

    public Vector<MenuItem> getMenuItems(){
        return menuItems;
    }

    public void draw(Canvas canvas, Paint paint){
        canvas.drawBitmap(menu, 0, 0, paint);
    }
}
