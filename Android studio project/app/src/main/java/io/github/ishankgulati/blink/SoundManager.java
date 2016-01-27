package io.github.ishankgulati.blink;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Hackbook on 1/23/2015.
 */
public class SoundManager {

    public SoundPool soundPool;
    public MediaPlayer player;
    private int clickID = -1;
    private int arrowID = -1;
    private int hitID = -1;
    private int shurikenID = -1;
    private int teleportID = -1;
    private int appearID = -1;
    private int streamId = -1;
    private ConcurrentHashMap<String, Integer> soundIds;

    SoundManager(Context context){
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC,0);
        soundIds = new ConcurrentHashMap<String, Integer>();

        // load sounds
        try{
            // Create objects of the 2 required classes
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            // Load our fx in memory ready for use
            descriptor = assetManager.openFd("click.ogg");
            clickID = soundPool.load(descriptor, 0);
            soundIds.put("click", clickID);

            descriptor = assetManager.openFd("arrow.wav");
            arrowID = soundPool.load(descriptor, 0);
            soundIds.put("arrow", arrowID);

            descriptor = assetManager.openFd("hit.wav");
            hitID = soundPool.load(descriptor, 0);
            soundIds.put("hit", hitID);

            descriptor = assetManager.openFd("shuriken.wav");
            shurikenID = soundPool.load(descriptor, 0);
            soundIds.put("shuriken", shurikenID);

            descriptor = assetManager.openFd("teleport.wav");
            teleportID = soundPool.load(descriptor, 0);
            soundIds.put("blink", teleportID);

            descriptor = assetManager.openFd("appear.wav");
            appearID = soundPool.load(descriptor, 0);
            soundIds.put("appear", appearID);

            // Adding music
            player = MediaPlayer.create(context, R.raw.soundtrack);
            player.setLooping(true);

        }catch(IOException e){
            // Print an error message to the console
            Log.e("error", "failed to load sound files");
        }
    }

    public void playSound(String name){
        int id = soundIds.get(name);
        streamId = soundPool.play(id, 1, 1, 0, 0, 1);
    }


    public void playMusic(){
        if(!player.isPlaying()){
            player.start();
        }
    }

    public void stopAllSounds(){

        if(player.isPlaying()) {
            player.stop();
            try {
                player.prepare();
                player.seekTo(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        soundPool.stop(streamId);
    }
}
