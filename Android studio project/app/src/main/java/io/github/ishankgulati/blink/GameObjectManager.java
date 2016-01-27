package io.github.ishankgulati.blink;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Hackbook on 1/23/2015.
 */

/**
 * This class is used to store and manage every game object
 */
public class GameObjectManager {

    private int screenX;
    private int screenY;

    private ConcurrentHashMap<String, VisibleGameObject> gameObjects = new ConcurrentHashMap
            <String, VisibleGameObject>();

    public void add(String name, VisibleGameObject gameObject){
        gameObjects.put(name, gameObject);
    }

    public void remove(String name){
        gameObjects.remove(name);
    }

    public int getObjectCount(){
        return gameObjects.size();
    }

    public VisibleGameObject get(String name){
        return gameObjects.get(name);
    }

    // draw all the game objects on canvas
    public void drawAll(SurfaceHolder ourHolder, Canvas canvas, Paint paint){
        if(ourHolder.getSurface().isValid()) {
            canvas = ourHolder.lockCanvas();
            BlinkView.getBackgroundScreen().draw(canvas, paint);
            for (ConcurrentHashMap.Entry<String, VisibleGameObject> entry : gameObjects.entrySet()) {
                VisibleGameObject value = entry.getValue();
                value.draw(canvas, paint);
            }
            BlinkView.getOnScreenControls().draw(canvas, paint);
            BlinkView.getScoreBoard().draw(canvas, paint);
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    // update all the game objects
    public void updateAll(long fps, long deltaTime){
        for(ConcurrentHashMap.Entry<String, VisibleGameObject> entry : gameObjects.entrySet()){
            VisibleGameObject value = entry.getValue();
            value.update(fps, deltaTime);
        }
    }

    // reset all the game objects
    public void resetAll(){
        for(ConcurrentHashMap.Entry<String, VisibleGameObject> entry : gameObjects.entrySet()){
            VisibleGameObject value = entry.getValue();
            value.reset();
        }
    }
}
