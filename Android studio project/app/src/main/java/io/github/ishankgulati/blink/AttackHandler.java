package io.github.ishankgulati.blink;

import android.content.Context;
import android.graphics.PointF;

import java.util.Random;

/**
 * Created by Hackbook on 1/23/2016.
 */

/**
 * This class handles all the attacks on player
 */
public class AttackHandler {

    // Pixel Perfect collision detection
    CollisionUtil collisionHadler = new CollisionUtil();

    // Declare Weapons
    private Shuriken shuriken;
    private Arrows arrows;

    // Random generator to initiate attack sequences
    private Random generator = new Random();

    public AttackHandler(Context context, int screenX, int screenY){
        shuriken = new Shuriken(context, screenX, screenY);
        BlinkView.getGameObjectManager().add("Shuriken", shuriken);

        arrows = new Arrows(context, screenX, screenY);
        BlinkView.getGameObjectManager().add("Arrows", arrows);
    }

    public void fire(){

        Ninja ninja = (Ninja) BlinkView.getGameObjectManager().get("Ninja");

        PointF locNinja = ninja.getPosition();

        // a 1/150 probability to fire shuriken
        int takeAimShuriken = generator.nextInt(150);

        // a 1/500 probability to fire shuriken
        int takeAimArrows = generator.nextInt(500);

        if(takeAimArrows == 0) {
            if(!arrows.isAttackActive()) {
                arrows.activate(ninja.getHeight(), locNinja.y);
                BlinkView.getSoundManager().playSound("arrow");
            }
        }
        else if(takeAimShuriken == 0) {
            if(!shuriken.isAttackActive()) {
                shuriken.activate(ninja.getHeight(), locNinja.y);
                BlinkView.getSoundManager().playSound("shuriken");
            }
        }

        if(shuriken.isAttackActive() && ninja.getState() != Ninja.NinjaState.Blink){

            if(collisionHadler.isCollisionDetected(ninja.getBitmap(), ninja.getBoundingRect(),
                    shuriken.getBitmap(), shuriken.getBoundingRect())){
                shuriken.deActivate();
                ninja.loseHealth(shuriken.getDamagePoints());
                BlinkView.getSoundManager().playSound("hit");
            }
        }

        if(arrows.isAttackActive() && ninja.getState() != Ninja.NinjaState.Blink) {

            if (collisionHadler.isCollisionDetected(ninja.getBitmap(), ninja.getBoundingRect(),
                    arrows.getBitmap(), arrows.getBoundingRect())){
                arrows.deActivate();
                ninja.loseHealth(arrows.getDamagePoints());
                BlinkView.getSoundManager().playSound("hit");
            }
        }

        if(ninja.checkEndOfGame()){
            BlinkView.endTheGame();
        }
    }
}
