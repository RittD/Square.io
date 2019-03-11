package squareio.knightstudios.com.squareio;

import android.graphics.Paint;
import android.graphics.Rect;

/**
 * The class that represents the stripes that move in the game activity (layout).
 * Stripe stands for the shape of the stripe.
 * ColorCode stands for its color(0 = red, 1 = yellow, 2 = blue, 3 = green)
 * Paint is the paint the stripe is drawn with.
 * StillCountable tells whether the stripe was already counted because it passed the square or not.
 */
class Stripe{
    private Rect stripe;
    private int colorCode;
    private Paint paint;
    private boolean stillCountable = true;

    Stripe(Rect stripe, int colorCode, Paint paint){
        this.stripe = stripe;
        this.colorCode = colorCode;
        this.paint = paint;
    }

    void setStripe(Rect stripe) {
        this.stripe = stripe;
    }

    Rect getStripe() {
        return stripe;
    }

    int getColorCode() {
        return colorCode;
    }

    Paint getPaint() {
        return paint;
    }

    boolean isStillCountable() {
        return stillCountable;
    }

    void setStillCountableFalse() {
        this.stillCountable = false;
    }
}
