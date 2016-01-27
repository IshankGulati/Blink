package io.github.ishankgulati.blink;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.Vector;

/**
 * Created by Hackbook on 1/23/2016.
 */
public class PauseMenu {

    // screen dimensions
    private int screenX;
    private int screenY;

    // result on clicking a button
    public enum MenuResult { Resume, Restart, MainMenu, Sound }

    // class representing every menu button
    public class MenuItem{
        public RectF rect;
        public MenuResult action;
    }

    // to hold images of buttons
    private Bitmap resume;
    private Bitmap restart;
    private Bitmap mainMenu;

    // dimensions of buttons
    float length;
    float height;

    // position of buttons
    float resumeX;
    float resumeY;

    float restartX;
    float restartY;

    float mainMenuX;
    float mainMenuY;

    // to hold buttons
    private Vector<MenuItem> menuItems = new Vector<MenuItem>();

    public PauseMenu(Context context, int screenX, int screenY){

        this.screenX = screenX;
        this.screenY = screenY;

        // dimensions of buttons
        length = screenX / 8;
        height = screenX / 8;

        // position of buttons
        resumeX = screenX / 16 + screenX / 4 + screenX / 2;
        resumeY = screenY / 20;

        restartX = screenX / 16 + screenX / 8 + screenX / 4;
        restartY = screenY / 20;

        mainMenuX = screenX / 16;
        mainMenuY = screenY / 20;

        // loading and scaling images
        restart = BitmapFactory.decodeResource(context.getResources(), R.drawable.restart);
        restart = Bitmap.createScaledBitmap(restart, (int) length, (int) height, false);

        resume = BitmapFactory.decodeResource(context.getResources(), R.drawable.resume);
        resume = Bitmap.createScaledBitmap(resume, (int) length, (int) height, false);

        mainMenu = BitmapFactory.decodeResource(context.getResources(), R.drawable.mainmenu);
        mainMenu = Bitmap.createScaledBitmap(mainMenu, (int) length, (int) height, false);

        // create and add buttons to vector
        MenuItem resumeButton = new MenuItem();
        resumeButton.rect = new RectF(resumeX, resumeY, resumeX + length, resumeY + height);
        resumeButton.action = MenuResult.Resume;
        menuItems.addElement(resumeButton);

        MenuItem restartButton = new MenuItem();
        restartButton.rect = new RectF(restartX, restartY, restartX + length, restartY + height);
        restartButton.action = MenuResult.Restart;
        menuItems.addElement(restartButton);

        MenuItem mainMenuButton = new MenuItem();
        mainMenuButton.rect = new RectF(mainMenuX, mainMenuY, mainMenuX + length,
                mainMenuY + height);
        mainMenuButton.action = MenuResult.MainMenu;
        menuItems.addElement(mainMenuButton);
    }

    public void draw(Canvas canvas, Paint paint){
        canvas.drawColor(Color.argb(30, 50, 50, 50));
        canvas.drawBitmap(mainMenu, mainMenuX, mainMenuY, paint);
        canvas.drawBitmap(resume, resumeX, resumeY, paint);
        canvas.drawBitmap(restart, restartX, restartY, paint);
    }

    public Vector getMenuItems(){
        return menuItems;
    }
}
