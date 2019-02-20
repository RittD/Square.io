package squareio.knightstudios.com.squareio;
//import android.animation.Animator;
//import android.widget.FrameLayout;
//
//import java.util.LinkedList;
//import java.util.List;

//
//import android.animation.Animator;
//import android.animation.ObjectAnimator;
//import android.view.View;
//import android.view.animation.LinearInterpolator;
//import android.widget.FrameLayout;
//import android.widget.Toast;
//
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Random;
//
//import static squareio.knightstudios.com.squareio.Stripe.*;
//import static squareio.knightstudios.com.squareio.GameActivity.*;
//
class StripeMovement extends Thread {
//    private GameActivity context;
//
//    private final Object pauseLock;
//    private boolean paused;
//    private boolean finished;
//
//    private List<Animator> stripeAnimators;
//    private List<FrameLayout> stripes;
//
//    StripeMovement(GameActivity context){
//        this.context = context;
//        pauseLock = new Object();
//        paused = false;
//        finished = false;
//
//        stripeAnimators = new LinkedList<>();
//        stripes = new LinkedList<>();
//    }
//
//    @Override
//    public void run() {
//        while(!finished){
//            Stripe newStripe = startNewStripe();
//            sleep(newStripe.delay);
////            synchronized (pauseLock){
////                while (paused){
////                    try {
////                        pauseLock.wait();
////                    }
////                    catch (InterruptedException e){
////                        e.printStackTrace();
////                    }
////                }
////            }
//        }
//    }
//
//
////    void onPause(){
////        synchronized (pauseLock) {
////            paused = true;
////        }
////    }
////
////    void onResume(){
////        synchronized (pauseLock){
////            paused = false;
////            pauseLock.notifyAll();
////        }
////    }
//
//
//    /**
//     * Get one new stripe.
//     */
//    private Stripe startNewStripe() {
//        removeInvisibleStripes();
//
//        //prepare variables stripe
//        Random r = new Random();
//        int stripe_width = Stripe.STANDARD_STRIPE_WIDTH + r.nextInt(Stripe.STANDARD_STRIPE_WIDTH * 2);
//        int stripe_color = r.nextInt(4);
//        int travel_duration = (int) (INITIAL_DURATION - (duration_variation - (duration_variation - INITIAL_SPEED)
//                                    * Math.exp(-growth_factor * context.score)));
//        int delay = travel_duration - (INITIAL_DURATION - duration_variation) + 5 * ROTATION_TIME;
//
//
//
//        //prepare new stripe
//        Stripe stripe = new Stripe(stripe_width, stripe_color, travel_duration, delay, context);
//        FrameLayout newStripe = stripe.prepare();
//
//        context.runOnUiThread(() -> context.all.addView(newStripe));
//        stripes.add(newStripe);
//
//        context.runOnUiThread(() -> {
//            context.square.bringToFront();
//            context.scoreboard.bringToFront();
//            context.pause.bringToFront();
//        });
//
//
//        //start sliding animation and check for collision
////        if (!gameOver){
//        // what to animate?, how to animate?, from where to start (+ some extra space for 18.5:9 phones)?, where to stop?
//        ObjectAnimator objectAnimator =
//                ObjectAnimator.ofFloat(newStripe, View.TRANSLATION_X, screenWidth + screenWidth / 10f, -stripe.width);
//        objectAnimator.setDuration(stripe.travelDuration);
//        objectAnimator.setInterpolator(new LinearInterpolator());
//        context.runOnUiThread(objectAnimator::start);
//        stripeAnimators.add(objectAnimator);
//
//        //check for collision in another thread
////        new Thread(() -> checkForCollision(stripe, newStripe)).start();
//        return stripe;
//
//        //TODO change cancel method to allow starting a new game
////        }
//    }


//    /**
//     * Checks whether a collision happens when the square touches the stripe for the first time.
//     * The exact time for this is calculated while the animation is in another thread. That could lead
//     * to slightly wrong position where collision is checked.
//     * Also checks whether a collision happens when the right side of the square is within the stripe.
//     * Called every time a new stripe is started and calls either gameEnds if collision is detected
//     * or incrementScore otherwise.
//     * @param stripe the stripe that is checked for collision
//     * @param newStripe the View object of stripe
//     */
//    private void checkForCollision(Stripe stripe, FrameLayout newStripe){
//        for (int i = 0; i < stripe.travelDuration; i+=10) {
//            //TODO Why no sout/Toast? check condition! Other problems with collision checking?
//            float currentPosition = newStripe.getX();
//            float firstContactWithSquare = context.getResources().getDimension(R.dimen.margin_square) + squareWidth + 500;
//            float lastContactWithSquare = context.getResources().getDimension(R.dimen.margin_square) + squareWidth - stripe.width;
//            if (currentPosition <= firstContactWithSquare
//                    && currentPosition >= lastContactWithSquare
//                    && stripe.color != context.currentPosition){
//                context.runOnUiThread(() -> newStripe.setX(0));
//
//            }
//            sleep(10);
////          Toast.makeText(context,"Time: "+ (System.currentTimeMillis()-before), Toast.LENGTH_SHORT).show();
////          System.out.println("Time: "+ (System.currentTimeMillis()-before));
//        }
//
////        //complete duration * the percentage of the complete travel that it takes to get to the square
////        int timeToReachSquare = (int) (stripe.travelDuration *
////                (1 - (stripe.width + context.getResources().getDimension(R.dimen.margin_square) + squareWidth)
////                        / (stripe.width + screenWidth + screenWidth/10)));
//
////        //wait for the stripe to reach the square
////        sleep(timeToReachSquare);
////        System.out.println("Checked");
//////      if (!gameOver) {
////        //check for collision:
////
////        //initial collision
////        if (context.currentPosition != stripe.color) {
////            System.out.println("1");
////            //gameEnds();
////        }
////
////        //collision inside stripe
////        else {
////            checkWithinStripe(stripe, newStripe);
////            System.out.println("checked2");
////            //wait until the stripe left the square and then increment the score if necessary
////
////
//////                int timeToPass = (int) (stripe.travelDuration * ((float) (stripe.width) / (float) (stripe.width + screenWidth + screenWidth / 10)));
//////                new Handler().postDelayed(() -> {
//////                    if (!gameOver) {
//////                        incrementScore();
//////                    }
//////                }, timeToPass);
////
////        }
//////      }
////
////
//    }
//TODO debug!!

//    /**
//     * Checks in intervals of 10ms whether a rotation was done. It is called when the stripe touches
//     * the right side of the square and is finished when the right side of the square does not touch
//     * the square anymore.
//     * @param stripe the Stripe object of the current stripe
//     * @param newStripe the View object of the current stripe
//     */
//    private void checkWithinStripe(Stripe stripe, FrameLayout newStripe){
//        if (newStripe.getX() + stripe.width >= context.getResources().getDimension(R.dimen.margin_square) + squareWidth){
//            if (context.currentPosition != stripe.color){
//                System.out.println("2");
////                gameEnds();
//            }
//            else{
//                sleep(20);
//                checkWithinStripe(stripe,newStripe);
//            }
//        }
//        //finished without colliding
//        else{
//            incrementScore();
//        }
//    }

//
//    private void removeInvisibleStripes(){
//        List<FrameLayout> visibleStripes = new LinkedList<>();
//        List<Animator> neededAnimators = new LinkedList<>();
//        for (int i = 0; i < stripes.size(); i++){
//            FrameLayout stripe = stripes.get(i);
//            boolean stripeOutOfScreen = stripe.getX() + stripe.getWidth() <= 1;
//            if (stripeOutOfScreen){
//                context.runOnUiThread(() -> context.all.removeView(stripe));
//            }
//            else{
//                visibleStripes.add(stripe);
//                neededAnimators.add(stripeAnimators.get(i));
//            }
//        }
//        stripes = visibleStripes;
//        stripeAnimators = neededAnimators;
//    }
//
//
////    /**
////     * Increments the score and trigger everything that is connected to this (scoreboard, effect, ...).
////     * Called when a possible collision was prevented.
////     */
////    private void incrementScore(){
////        context.score++;
////        callScoreEffect();
////        context.scoreboard.setText(String.valueOf(context.score));
////    }
//
//
//    /**
//     * Starts the effect that should be activated when a certain score is achieved.
//     * Could be used for achievements too.
//     */
////    private void callScoreEffect(){
//////        if (newHighscore){
//////            new HighlightAnimation(all).animate();
//////        }
//////        else{
////        context.runOnUiThread(new BlinkAnimation(context.square)::animate);
//////        }
////    }
//
//
//    private static void sleep(int millis){
//        try{
//            Thread.sleep(millis);
//        }
//        catch (InterruptedException e){
////            e.printStackTrace();
//        }
//
//    }
}
