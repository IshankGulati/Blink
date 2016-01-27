package io.github.ishankgulati.blink;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Iterator;
import java.util.Locale;

/**
 * Created by Hackbook on 1/23/2016.
 */
public class BlinkView extends SurfaceView implements Runnable{

    Context context;

    public enum GameState{Playing, Paused, ShowingSplash, ShowingMenu, ShowingHelp, Completed,
                        Exiting}

    private Thread gameThread = null;

    private SurfaceHolder ourHolder;
    private Canvas canvas;
    private Paint paint;

    private long fps;
    private long elapsedTime;
    private static GameState gameState;

    // screen resolution
    private int screenX;
    private int screenY;

    // id for pointer which has left screen
    private static final int INVALID_POINTER_ID = -1;

    // The ‘active pointer’ is the one currently moving our object.
    int mActivePointerId = INVALID_POINTER_ID;

    // Last direction towards ninja was moving
    Ninja.NinjaDirection mLastDirection = Ninja.NinjaDirection.Nowhere;

    // time period of splash screen
    private static long splashScreenDuration = 2000;
    private long startSplashScreenTime;

    private static long endGameTime;

    long lastFrameTime = 0;

    // chosen ideal delta time
    long dt = 1000/60;

    // calculated delta time
    long deltaTime;

    // custom font
    public static Typeface font;

    private static GameObjectManager gameObjectManager;
    private static OnScreenControls controls;
    private static PauseMenu pauseMenu;
    private static GameBackground background;
    private static SplashScreen splashScreen;
    private static MainMenu mainMenu;
    private static HelpMenu helpMenu;
    private static ScoreBoard scoreBoard;
    private static SoundManager soundManager;
    private static SharedPreferencesHandler gamePrefrences;

    private static Ninja ninja;
    private static AttackHandler attacks;

    public BlinkView(Context context, Point size){
        super(context);

        this.context = context;

        // getting context from activity
        this.context = context;

        ourHolder = getHolder();
        paint = new Paint();

        // loading custom font
        AssetManager assetManager = context.getAssets();
        font = Typeface.createFromAsset(assetManager, String.format(Locale.US, "fonts/%s",
                "kenvector.ttf"));
        paint.setTypeface(font);

        screenX = size.x;
        screenY = size.y;

        fps = 1;
        elapsedTime = 1;

        gameState = GameState.Playing;

        gameObjectManager = new GameObjectManager();

        ninja = new Ninja(context, screenX, screenY);
        gameObjectManager.add("Ninja", ninja);

        controls = new OnScreenControls(context, screenX, screenY);
        pauseMenu = new PauseMenu(context, screenX, screenY);
        splashScreen = new SplashScreen(context, screenX, screenY);
        background = new GameBackground(context, screenX, screenY);
        mainMenu = new MainMenu(context, screenX, screenY);
        helpMenu = new HelpMenu(context, screenX, screenY);
        scoreBoard = new ScoreBoard(context, screenX, screenY);
        soundManager = new SoundManager(context);
        gamePrefrences = new SharedPreferencesHandler(context);
        attacks = new AttackHandler(context, screenX, screenY);
    }

    @Override
    public void run(){
        soundManager.playMusic();
        while(!isExiting()){
            GameLoop();
        }
    }

    private void GameLoop(){
        switch(gameState) {

            case ShowingSplash:
                showSplashScreen();
                break;

            case ShowingMenu:
                showMainMenu();
                break;

            case ShowingHelp:
                showHelpMenu();
                break;

            case Completed:
                showEndGame();
                break;

            case Paused:
                showPauseMenu();
                break;

            case Playing:

                long startFrameTime = System.currentTimeMillis();
                long frameTime = startFrameTime - lastFrameTime;
                lastFrameTime = startFrameTime;

                deltaTime = Math.min(frameTime, dt);

                if (startFrameTime - endGameTime > 1000 && ninja.getState() == Ninja.NinjaState.Dead) {
                    gameState = GameState.Completed;
                    scoreBoard.setGameResult(ScoreBoard.GameResult.End);
                }

                gameObjectManager.updateAll(fps, deltaTime);
                gameObjectManager.drawAll(ourHolder, canvas, paint);
                if (ninja.getState() != Ninja.NinjaState.Dead) {
                    attackNinja();
                }

                elapsedTime = System.currentTimeMillis() - startFrameTime;

                if(elapsedTime > 1){
                    fps = 1000 / elapsedTime;
                }

                break;
            default:
                break;
        }
    }

    // onTouchListener to detect screen touches
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        // get masked (not specific to a pointer) action
        int maskedAction = motionEvent.getActionMasked();
        int action = motionEvent.getAction();

        // if ninja is dead dont take any more inputs
        if(gameState == GameState.Playing && ninja.getState() == Ninja.NinjaState.Dead){
            return true;
        }

        if (gameState == GameState.Playing) {

            switch (maskedAction) {

                // player has touched the screen
                case MotionEvent.ACTION_DOWN: {

                    float xPos = motionEvent.getX();
                    float yPos = motionEvent.getY();

                    // if screen touched while ninja is invisible, move it to the touched position
                    if (ninja.getState() == Ninja.NinjaState.Blink) {
                        ninja.blink(xPos, yPos);
                    }

                    // handle all the on screen buttons
                    Iterator itr = controls.getControlButtons().iterator();
                    while (itr.hasNext()) {

                        OnScreenControls.Control button = (OnScreenControls.Control) itr.next();
                        RectF buttonRect = new RectF(button.rect);

                        if (xPos > buttonRect.left && xPos < buttonRect.right &&
                                yPos > buttonRect.top && yPos < buttonRect.bottom) {

                            switch (button.action) {
                                case Up:
                                    ninja.jump(Ninja.NinjaDirection.Up);
                                    mLastDirection = Ninja.NinjaDirection.Up;
                                    break;

                                case Down:
                                    ninja.slide(Ninja.NinjaDirection.Down);
                                    mLastDirection = Ninja.NinjaDirection.Down;
                                    break;

                                case Right:
                                    if(ninja.getState() == Ninja.NinjaState.Jump
                                            && !ninja.isNinjaOnSurface()){
                                        //ninja.setDirection(Ninja.NinjaDirection.Right);
                                        ninja.runAfterLanding(Ninja.NinjaDirection.Right);
                                    }
                                    else {
                                        ninja.run(Ninja.NinjaDirection.Right);
                                    }
                                    mLastDirection = Ninja.NinjaDirection.Right;
                                    break;

                                case Left:
                                    if(ninja.getState() == Ninja.NinjaState.Jump
                                            && !ninja.isNinjaOnSurface()){
                                        //ninja.setDirection(Ninja.NinjaDirection.Left);
                                        ninja.runAfterLanding(Ninja.NinjaDirection.Left);
                                    }
                                    else {
                                        ninja.run(Ninja.NinjaDirection.Left);
                                    }
                                    mLastDirection = Ninja.NinjaDirection.Left;
                                    break;

                                case Blink:
                                    ninja.blink(Ninja.NinjaDirection.Up);
                                    break;

                                case Pause:
                                    gameState = GameState.Paused;
                                    break;

                                default:
                                    break;
                            }
                        }
                    }
                    mActivePointerId = motionEvent.getPointerId(0);
                    break;
                }

                case MotionEvent.ACTION_POINTER_DOWN: {

                    int pointerIndex = motionEvent.getActionIndex();

                    float xPos = motionEvent.getX(pointerIndex);
                    float yPos = motionEvent.getY(pointerIndex);

                    // handle all the on screen buttons
                    Iterator itr = controls.getControlButtons().iterator();

                    while (itr.hasNext()) {

                        OnScreenControls.Control button = (OnScreenControls.Control) itr.next();
                        RectF buttonRect = new RectF(button.rect);

                        if (xPos > buttonRect.left && xPos < buttonRect.right &&
                                yPos > buttonRect.top && yPos < buttonRect.bottom) {

                            switch (button.action) {

                                case Left:

                                    if(mLastDirection == Ninja.NinjaDirection.Up){
                                        ninja.setDirection(Ninja.NinjaDirection.Left);
                                    }
                                    break;

                                case Right:

                                    if(mLastDirection == Ninja.NinjaDirection.Up){
                                        ninja.setDirection(Ninja.NinjaDirection.Right);
                                    }
                                    break;

                                case Up:

                                    if(mLastDirection == Ninja.NinjaDirection.Left){
                                        ninja.jump(Ninja.NinjaDirection.Left);
                                    }
                                    else if(mLastDirection == Ninja.NinjaDirection.Right){
                                        ninja.jump(Ninja.NinjaDirection.Right);
                                    }
                                    else{
                                        ninja.jump(Ninja.NinjaDirection.Up);
                                    }

                                    break;

                                case Down:

                                    if(mLastDirection == Ninja.NinjaDirection.Left){
                                        ninja.slide(Ninja.NinjaDirection.Left);
                                    }
                                    else if(mLastDirection == Ninja.NinjaDirection.Right){
                                        ninja.slide(Ninja.NinjaDirection.Right);
                                    }
                                    else{
                                        ninja.slide(Ninja.NinjaDirection.Down);
                                    }

                                    break;

                                case Blink:
                                    if(mLastDirection == Ninja.NinjaDirection.Left){
                                        ninja.blink(Ninja.NinjaDirection.Left);
                                    }
                                    else if(mLastDirection == Ninja.NinjaDirection.Right){
                                        ninja.blink(Ninja.NinjaDirection.Right);
                                    }
                                    else{
                                        ninja.blink(Ninja.NinjaDirection.Up);
                                    }

                                    break;

                                default:
                                    break;
                            }
                        }

                    }

                    break;
                }

                // player has removed finger from screen
                case MotionEvent.ACTION_UP: {

                    if (ninja.getState() == Ninja.NinjaState.Run ||
                            ninja.getState() == Ninja.NinjaState.Idle) {
                        // bring player in idle mode
                        ninja.setState(Ninja.NinjaState.Idle);
                        mLastDirection = Ninja.NinjaDirection.Nowhere;
                        mActivePointerId = INVALID_POINTER_ID;
                    }
                    if(ninja.getState() == Ninja.NinjaState.Slide){
                        ninja.setState(Ninja.NinjaState.Idle);
                        mLastDirection = Ninja.NinjaDirection.Nowhere;
                        mActivePointerId = INVALID_POINTER_ID;
                    }
                    // if no key pressed cancel all future direction assignments
                    ninja.cancelFutureStateAllocations();
                    break;
                }

                case MotionEvent.ACTION_POINTER_UP: {

                    // Extract the index of the pointer that left the touch sensor
                    int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK)
                            >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                    int pointerId = motionEvent.getPointerId(pointerIndex);

                    if (pointerId == mActivePointerId) {
                        // This was our active pointer going up. Choose a new
                        // active pointer and adjust accordingly.
                        int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                        mActivePointerId = motionEvent.getPointerId(newPointerIndex);
                    }

                    float xPos = motionEvent.getX(mActivePointerId);
                    float yPos = motionEvent.getY(mActivePointerId);

                    // handle all the on screen buttons
                    Iterator itr = controls.getControlButtons().iterator();
                    while (itr.hasNext()) {

                        OnScreenControls.Control button = (OnScreenControls.Control) itr.next();
                        RectF buttonRect = new RectF(button.rect);

                        if (xPos > buttonRect.left && xPos < buttonRect.right &&
                                yPos > buttonRect.top && yPos < buttonRect.bottom) {

                            switch (button.action) {
                                case Up:
                                    ninja.jump(Ninja.NinjaDirection.Up);
                                    break;

                                case Down:
                                    ninja.slide(Ninja.NinjaDirection.Down);
                                    break;

                                case Right:
                                    if(ninja.getState() == Ninja.NinjaState.Jump
                                            && !ninja.isNinjaOnSurface()){
                                        ninja.runAfterLanding(Ninja.NinjaDirection.Right);
                                    }
                                    else {
                                        ninja.run(Ninja.NinjaDirection.Right);
                                    }
                                    break;

                                case Left:
                                    if(ninja.getState() == Ninja.NinjaState.Jump
                                            && !ninja.isNinjaOnSurface()){
                                        ninja.runAfterLanding(Ninja.NinjaDirection.Left);
                                    }
                                    else {
                                        ninja.run(Ninja.NinjaDirection.Left);
                                    }
                                    break;

                                case Blink:
                                    ninja.blink(Ninja.NinjaDirection.Up);
                                    break;

                                default:
                                    break;
                            }
                        }
                    }
                    break;
                }

                case MotionEvent.ACTION_CANCEL: {
                    mLastDirection = Ninja.NinjaDirection.Nowhere;
                    mActivePointerId = INVALID_POINTER_ID;
                    ninja.cancelFutureStateAllocations();
                    break;
                }
            }
        }

        else if (gameState == GameState.Paused) {

            switch (maskedAction) {

                // player has touched the screen
                case MotionEvent.ACTION_DOWN: {
                    float xPos = motionEvent.getX();
                    float yPos = motionEvent.getY();

                    Iterator itr = pauseMenu.getMenuItems().iterator();
                    while (itr.hasNext()) {

                        PauseMenu.MenuItem button = (PauseMenu.MenuItem) itr.next();
                        RectF buttonRect = new RectF(button.rect);

                        if (xPos > buttonRect.left && xPos < buttonRect.right &&
                                yPos > buttonRect.top && yPos < buttonRect.bottom) {

                            switch (button.action) {
                                case MainMenu:
                                    soundManager.playSound("click");
                                    resetGame();
                                    gameState = GameState.ShowingMenu;
                                    soundManager.playMusic();
                                    break;

                                case Sound:
                                    break;

                                case Restart:
                                    soundManager.playSound("click");
                                    resetGame();
                                    gameState = GameState.Playing;
                                    break;

                                case Resume:
                                    soundManager.playSound("click");
                                    gameState = GameState.Playing;
                                    break;

                                default:
                                    break;
                            }
                        }
                    }

                    break;
                }
            }
        }

        else if(gameState == GameState.ShowingMenu){

            switch (maskedAction) {

                // player has touched the screen
                case MotionEvent.ACTION_DOWN: {

                    float xPos = motionEvent.getX();
                    float yPos = motionEvent.getY();

                    Iterator itr = mainMenu.getMenuItems().iterator();
                    while (itr.hasNext()) {
                        MainMenu.MenuItem button = (MainMenu.MenuItem) itr.next();
                        RectF buttonRect = new RectF(button.rect);

                        if (xPos > buttonRect.left && xPos < buttonRect.right &&
                                yPos > buttonRect.top && yPos < buttonRect.bottom) {
                            switch (button.action) {
                                case Play:
                                    soundManager.stopAllSounds();
                                    gameState = GameState.Playing;
                                    scoreBoard.resetScoreUpdateTimer();
                                    soundManager.playSound("click");
                                    break;
                                case Help:
                                    soundManager.playSound("click");
                                    gameState = GameState.ShowingHelp;
                                    break;
                            }
                        }
                    }
                    break;
                }
            }
        }

        else if(gameState == GameState.ShowingHelp){
            if (maskedAction == MotionEvent.ACTION_DOWN) {
                // player has touched the screen
                gameState = GameState.ShowingMenu;
            }
        }

        else if(gameState == GameState.Completed){
            if(maskedAction == MotionEvent.ACTION_DOWN){
                gameState = GameState.ShowingMenu;
                resetGame();
                soundManager.playMusic();
            }
        }
        return true;
    }

    // helper methods
    private boolean isExiting(){
        if(gameState == GameState.Exiting)
            return true;
        else
            return false;
    }

    private void resetGame(){
        scoreBoard.resetScore();
        gameObjectManager.resetAll();
    }

    private void attackNinja(){
        attacks.fire();
    }

    public static void endTheGame(){
        endGameTime = System.currentTimeMillis();
    }

    // show methods
    private  void showSplashScreen(){

        long time = System.currentTimeMillis();
        if(time - startSplashScreenTime < splashScreenDuration) {
            if (ourHolder.getSurface().isValid()) {
                canvas = ourHolder.lockCanvas();
                splashScreen.draw(canvas, paint);
                ourHolder.unlockCanvasAndPost(canvas);
            }
        }
        else{
            splashScreen.destroySpashScreen();
            gameState = GameState.ShowingMenu;
        }
    }

    private void showMainMenu() {
        if(ourHolder.getSurface().isValid()) {
            canvas = ourHolder.lockCanvas();
            mainMenu.draw(canvas, paint);
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void showPauseMenu(){
        if(ourHolder.getSurface().isValid()) {
            canvas = ourHolder.lockCanvas();
            pauseMenu.draw(canvas, paint);
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void showHelpMenu(){
        if(ourHolder.getSurface().isValid()) {
            canvas = ourHolder.lockCanvas();
            helpMenu.draw(canvas, paint);
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void showEndGame(){
        if(ourHolder.getSurface().isValid()) {
            canvas = ourHolder.lockCanvas();
            scoreBoard.drawEndGame(canvas, paint);
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    // setters

    // getters
    public static OnScreenControls getOnScreenControls(){
        return controls;
    }

    public static GameBackground getBackgroundScreen() { return background; }

    public static GameObjectManager getGameObjectManager() {return gameObjectManager;}

    public static ScoreBoard getScoreBoard() { return scoreBoard; }

    public static SharedPreferencesHandler getGamePreferences(){
        return gamePrefrences;
    }

    public static SoundManager getSoundManager() { return soundManager; }


    // activity methods
    public void pause(){
        gameState = GameState.Exiting;
        try{
            gameThread.join();
        }
        catch (InterruptedException e){
            Log.e("Error:", "joining thread");
        }
    }

    public void resume(){

        if(gameState == GameState.Exiting) {
            gameState = GameState.Playing;
        }
        else {
            startSplashScreenTime = System.currentTimeMillis();
            gameState = GameState.ShowingSplash;
        }

        gameThread = new Thread(this);
        gameThread.start();
    }

    public void stop(){
        soundManager.player.stop();
        soundManager.player.release();
        soundManager.soundPool.release();
    }
}
