package squareio.knightstudios.com.squareio;

import android.graphics.Paint;
import android.graphics.Rect;

class Stripe{
    private Rect stripe;
    private int colorCode;
    private Paint paint;

    void setStripe(Rect stripe) {
        this.stripe = stripe;
    }

    Rect getStripe() {
        return stripe;
    }

    Paint getPaint() {
        return paint;
    }

    int getColorCode() {
        return colorCode;
    }

    Stripe(Rect stripe, int colorCode, Paint paint){
        this.stripe = stripe;
        this.colorCode = colorCode;
        this.paint = paint;
    }
}
