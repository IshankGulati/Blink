package io.github.ishankgulati.blink;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Hackbook on 1/23/2016.
 */

/**
 * This class uses SharedPreferences to store high score
 */
public class SharedPreferencesHandler {

    private SharedPreferences gamePrefs;

    public SharedPreferencesHandler(Context context){
        gamePrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setHighScore(int score){
        SharedPreferences.Editor scoreEdit = gamePrefs.edit();
        scoreEdit.putInt("highScore", score);
        scoreEdit.commit();
    }

    public int getHighScore(){
        return gamePrefs.getInt("highScore", 0);
    }
}
