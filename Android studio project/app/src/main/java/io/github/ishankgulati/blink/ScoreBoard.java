package io.github.ishankgulati.blink;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import java.util.Locale;

/**
 * Created by Hackbook on 1/23/2015.
 */
public class ScoreBoard {

    private int score;
    private int screenY, screenX;

    // possible status of game
    enum GameResult{End, Playing}

    // current status of game
    private GameResult result;

    Paint textPaint = new Paint();

    // timer used to update scores
    private long scoreUpdateTimer;

    private int previousHighScore = 0;

    Typeface font;

    // coordinates for gui
    private int scoreX;
    private int scoreY;

    ScoreBoard(Context context, int screenX, int screenY){
        score = 0;
        this.screenX = screenX;
        this.screenY = screenY;
        result = GameResult.Playing;

        AssetManager assetManager = context.getAssets();
        font = Typeface.createFromAsset(assetManager, String.format(Locale.US, "fonts/%s",
                "kenvector.ttf"));

        textPaint.setColor(Color.WHITE);
        textPaint.setAlpha(150);
        textPaint.setTextSize(screenY / 28);
        textPaint.setTypeface(BlinkView.font);

        scoreUpdateTimer = System.currentTimeMillis();

        String text = "Score: 00";
        Rect bounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), bounds);

        scoreX = (3 * screenX / 4) - (bounds.width() / 2);
        scoreY = screenY / 25;
    }

    public void draw(Canvas canvas, Paint paint) {

        long time = System.currentTimeMillis();

        String text = "Score: " + score;
        Rect bounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), bounds);

        canvas.drawText("Score: " + score, scoreX, scoreY, textPaint);

        incrementScore(time);
    }

    public void drawEndGame(Canvas canvas, Paint paint){
        String text;
        Rect bounds = new Rect();

        canvas.drawColor(Color.argb(80, 26, 128, 182));
        paint.setColor(Color.WHITE);
        paint.setTextSize(screenY / 6);

        text = "Game Over!";
        paint.getTextBounds(text, 0, text.length(), bounds);
        float height = paint.descent() - paint.ascent();
        float offset = (height / 2) - paint.descent();
        float xPos = (canvas.getWidth() / 2) - (bounds.width() / 2);
        float yPos = (canvas.getHeight() / 4) + offset;
        canvas.drawText(text, xPos, yPos, paint);

        paint.setTextSize(screenY / 11);
        height = paint.descent() - paint.ascent();
        offset = (height / 2) - paint.descent();

        text = "Score   " + score;
        paint.getTextBounds(text, 0, text.length(), bounds);
        xPos = (canvas.getWidth() / 2) - (bounds.width() / 2);
        yPos = (canvas.getHeight() / 2) + offset;
        canvas.drawText(text, xPos, yPos, paint);

        if(previousHighScore < score){
            previousHighScore = score;
        }
        text = "High Score   " + previousHighScore;
        paint.getTextBounds(text, 0, text.length(), bounds);
        xPos = (canvas.getWidth() / 2) - (bounds.width() / 2);
        yPos = yPos + 2 * bounds.height();
        canvas.drawText(text, xPos, yPos, paint);
    }

    public GameResult getGameResult(){
        return result;
    }

    public void setGameResult(GameResult result){
        this.result = result;

        if(result == GameResult.End){
            previousHighScore = BlinkView.getGamePreferences().getHighScore();
            if(score > previousHighScore) {
                BlinkView.getGamePreferences().setHighScore(score);
            }
        }
    }

    public void incrementScore(long time){
        if(time - scoreUpdateTimer >= 1000) {
            score += 1;
            scoreUpdateTimer = time;
        }
    }

    public void resetScore(){
        score = 0;
        result = GameResult.Playing;
        scoreUpdateTimer = System.currentTimeMillis();
    }

    public void resetScoreUpdateTimer(){
        scoreUpdateTimer = System.currentTimeMillis();
    }
}
