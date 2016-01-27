package io.github.ishankgulati.blink;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Display;

public class BlinkActivity extends Activity{

    BlinkView blinkView;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // get the screen resolution
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        // set the gameview to blinkView
        blinkView = new BlinkView(this, size);
        setContentView(blinkView);
    }

    // when our app goes out of focus
    @Override
    protected void onPause(){
        super.onPause();
        blinkView.pause();
    }

    // when our app gains focus
    @Override
    protected void onResume(){
        super.onResume();
        blinkView.resume();
    }

    // when activity is stopped
    @Override
    protected void onStop(){
        super.onStop();
        blinkView.stop();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }
}