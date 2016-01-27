package io.github.ishankgulati.blink;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by Hackbook on 1/23/2016.
 */

/**
 * This class implements collision detection algorithm
 */
public class CollisionUtil {
    public static boolean isCollisionDetected(Bitmap bitmap1, RectF boundsF1,
                                              Bitmap bitmap2, RectF boundsF2) {

        Rect bounds1 = new Rect();
        Rect bounds2 = new Rect();

        boundsF1.round(bounds1);
        boundsF2.round(bounds2);

        int x1 = bounds1.left;  int y1 = bounds1.top;
        int x2 = bounds2.left;  int y2 = bounds2.top;

        if (Rect.intersects(bounds1, bounds2)) {
            Rect collisionBounds = getCollisionBounds(bounds1, bounds2);
            for (int i = collisionBounds.left; i < collisionBounds.right; i++) {
                for (int j = collisionBounds.top; j < collisionBounds.bottom; j++) {
                    int bitmap1Pixel = bitmap1.getPixel(i-x1, j-y1);
                    int bitmap2Pixel = bitmap2.getPixel(i-x2, j-y2);
                    if (isFilled(bitmap1Pixel) && isFilled(bitmap2Pixel)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static Rect getCollisionBounds(Rect rect1, Rect rect2) {
        int left = (int) Math.max(rect1.left, rect2.left);
        int top = (int) Math.max(rect1.top, rect2.top);
        int right = (int) Math.min(rect1.right, rect2.right);
        int bottom = (int) Math.min(rect1.bottom, rect2.bottom);
        return new Rect(left, top, right, bottom);
    }

    private static boolean isFilled(int pixel) {
        return pixel != Color.TRANSPARENT;
    }
}
