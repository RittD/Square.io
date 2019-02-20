package squareio.knightstudios.com.squareio;

//import android.graphics.drawable.Drawable;
//import android.widget.FrameLayout;

import android.graphics.Paint;
import android.graphics.Rect;

class Stripe{
    private Rect stripe;
    private Paint paint;

    public void setStripe(Rect stripe) {
        this.stripe = stripe;
    }

    Rect getStripe() {
        return stripe;
    }

    Paint getPaint() {
        return paint;
    }

    Stripe(Rect stripe, Paint paint){
        this.stripe = stripe;
        this.paint = paint;
    }


//    static final int STANDARD_STRIPE_WIDTH = 200;
//    static final int INITIAL_SPEED = 10;
//    static final int INITIAL_DURATION = 5000;
//    static int duration_variation = 3000;
//    static float growth_factor = 0.02f;   //for the e function => the level of difficulty
//    private int screenWidth;
//    int width, color, travelDuration, delay;
//    private GameActivity currentState;
//
//    Stripe(int width, int color, int travelDuration, int delay, GameActivity context) {
//        this.currentState = context;
//        this.screenWidth = GameActivity.screenWidth;
//        this.width = width;
//        this.color = color;
//        this.travelDuration = travelDuration;
//        this.delay = delay;
////        float speed = (float) screenWidth / (float) travelDuration;
//    }
//
//
//    /**
//     * Builds the View that represents the stripe on the screen.
//     * @return a FrameLayout showing the stripe
//     */
//    FrameLayout prepare(){
//        final FrameLayout newStripe = new FrameLayout(currentState);
//        newStripe.setX(screenWidth);
//        newStripe.setY(0);
//        newStripe.setLayoutParams(new FrameLayout.LayoutParams(width, FrameLayout.LayoutParams.MATCH_PARENT));
//        newStripe.setBackground(getDrawable(color));
//        return newStripe;
//    }
//
//
//    /**
//     * Get the drawable that is used to show a stripe of the given color
//     * @param color the color code
//     * @return the background of the stripe
//     */
//    private Drawable getDrawable(int color){
//
//        final int YELLOW = 0;
//        final int RED = 1;
//        final int BLUE = 2;
//        final int GREEN = 3;
//
//        switch (color){
//            case YELLOW:
//                return currentState.getResources().getDrawable(R.drawable.stripe_yellow);
//            case RED:
//                return currentState.getResources().getDrawable(R.drawable.stripe_red);
//            case BLUE:
//                return currentState.getResources().getDrawable(R.drawable.stripe_blue);
//            case GREEN:
//                return currentState.getResources().getDrawable(R.drawable.stripe_green);
//
//            //will never happen
//            default:
//                return null;
//        }
//    }

}
