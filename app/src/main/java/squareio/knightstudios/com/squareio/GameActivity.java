package squareio.knightstudios.com.squareio;



import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.easyandroidanimations.library.RotationAnimation;

import java.util.LinkedList;
import java.util.Queue;
//import android.os.Handler;
//
//import android.view.MotionEvent;
//import android.view.View;
//
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.easyandroidanimations.library.*;
//
//
//import java.util.LinkedList;
//import java.util.Queue;


/**
 * Annotation 1:
 * The positions of the colors are: 0 = red, 1 = yellow, 2 = blue and 3 = green.
 *
 *
 * Annotation 2:
 * The basic units everything is calculated with are pixels and milliseconds.
 */
public class GameActivity extends AppCompatActivity {

    GameActivity_Layout gameActivityLayoutView;
    static DisplayMetrics displayMetrics;

//    //Game status
//    boolean gameOver = false;
//    TextView scoreboard;
//    int score;
//    StripeMovement stripeMovement;
//
//    //Menu
//    ImageView pause_button;
//    boolean paused = false;

    //Square
    static final int ROTATION_TIME = 80;
//    static int squareWidth;
//    public ImageView square;
    int currentPosition = 0;
    boolean rotating = false;
    Queue<Boolean> futureRotations = new LinkedList<>();
    int maxFutureRotations = 10;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        gameActivityLayoutView = new GameActivity_Layout(this);
        setContentView(gameActivityLayoutView);

//        setContentView(R.layout.activity_game);
//
//        //set fullscreen

//
//        prepareComponents();
//
//        //Start the game
//        stripeMovement = new StripeMovement(this);
//        stripeMovement.start();
//        new Handler().postDelayed(()-> stripeMovement.onPause(), 5000);
//        new Handler().postDelayed(()->stripeMovement.stripeAnimators.get(stripeMovement.stripeAnimators.size()-1).pause_button(),7000);

//        sleep(2000);
//            startNewStripe();
//
//            //TODO understand multithreading (fix pause_button/resume problem)
//            synchronized (pauseLock) {
//                while (paused) {
//                    try {
//                        pauseLock.wait();
//                    } catch (InterruptedException e) {
//                    }
//                }
//            }

    }

//    private void prepareComponents() {
//        //get screen size
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        screenWidth = displayMetrics.widthPixels;
//        squareWidth = screenWidth / 10;
//
//
//        //get square & layout
//        square = findViewById(R.id.square);
//        square.getLayoutParams().width = squareWidth;
//        all = findViewById(R.id.all);
//
//        //get score board
//        scoreboard = findViewById(R.id.scoreboard);
//        score = Integer.parseInt(scoreboard.getText().toString());
//
//        //get pause_button button and enable pausing/resuming the game
//        pause_button = findViewById(R.id.pause_button);
//        pause_button.setOnClickListener((view) -> {
//            if (paused){
//                paused = false;
////                resumeStripes();
//            }
//            else{
//                paused = true;
////                pauseStripes();
//            }
//        });
//    }
//
//
    /**
     * When clicking on the pause_button button, the game should pause_button.
     * Otherwise provides the 90°-rotation of the square clockwise (anti-clockwise)
     * when clicking on the right (left) half of the screen.
     * @param event the motion event that occurred, only interesting if it is a click
     * @return whether it was correctly executed??
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();
            float margin_pause = getResources().getDimension(R.dimen.margin_pause);
            float pause_size = getResources().getDimension(R.dimen.size_pause);
//            if (gameOver) {
//                startNewGame();
//            }
//            else {
            boolean clickedOnPause =    x >= displayMetrics.widthPixels - (pause_size + margin_pause) &&
                                        y >= margin_pause && y <= margin_pause + pause_size;
            if (clickedOnPause){
                gameActivityLayoutView.setPaused(!gameActivityLayoutView.isPaused());

            }


            else{
                boolean clickedInRightHalf = event.getX() >= displayMetrics.widthPixels / 2;

                //futureRotations are the rotations that will be executed after the currently executed rotation
                Queue<Integer> futureRotations = gameActivityLayoutView.getFutureRotations();
                if(futureRotations.size() < 5 && !gameActivityLayoutView.isPaused()){
                    if(clickedInRightHalf){
                        //rotate square right by 90 degrees
                        futureRotations.add(90);
                    }
                    else{
                        //rotate square left by 90 degrees
                        futureRotations.add(-90);
                    }
                }
                gameActivityLayoutView.setFutureRotations(futureRotations);
            }
        }
        return super.onTouchEvent(event);
    }


    @Override
    protected void onPause() {
        super.onPause();
        gameActivityLayoutView.pause();
//        stripeMovement.onPause();
    }


//    private void pauseStripes(){
//        for (Animator animator : stripeAnimators) {
//            animator.pause_button();
//        }
//    }
//    private void resumeStripes(){
//        for (Animator animator : stripeAnimators) {
//            animator.resume();
//        }
//    }
//    //
//    private void cancelStripes(){
//        for (Animator animator : stripeAnimators) {
//            runOnUiThread(animator::cancel);
//        }
////        int j = 0;
////        Toast.makeText(getApplicationContext(), " "+all.getChildCount(), Toast.LENGTH_SHORT).show();
//        for (int i=0; i < all.getChildCount(); i++){
//            View child = all.getChildAt(i);
//            if(child instanceof FrameLayout){
//                runOnUiThread(() -> all.removeView(child));
////                j++;
//            }
//        }
////        Toast.makeText(getApplicationContext(), "removed: "+j, Toast.LENGTH_SHORT).show();
//    }
    @Override
    protected void onResume() {
        super.onResume();
        gameActivityLayoutView.resume();
//        stripeMovement.onResume();
    }




    /**
     * Rotates the square right if the right half of the screen is clicked. Because otherwise the position of the square could
     * differ from the wanted states (0°, 90°, 180° and 270°), it is necessary to prohibit starting a new rotation,
     * while already doing one. Therefore a queue is used that is filled with rotations that were requested but cannot be
     * executed right away. This queue is dismantled right after (+20 ms to prevent mistakes) the first rotation is finished.
     * @param square the square to rotate
     * @param rightRound
     */
    private void rotateSquare(View square, boolean rightRound){
        if (!rotating) {
            rotating = true;
            new Handler().postDelayed(() -> currentPosition = (currentPosition + (rightRound ? 1 : 3)) % 4, ROTATION_TIME /2);
            new RotationAnimation(square).setDegrees(rightRound ? 90 : -90).setDuration(ROTATION_TIME).animate();

            //after rotating take the rotations from the list until it is empty
            new Handler().postDelayed(() -> {
                rotating = false;

                if (!futureRotations.isEmpty()){
                    boolean rotateRight = futureRotations.poll();

                    if (rotateRight){
                        rotateSquare(square, true);
                    }
                    else{
                       rotateSquare(square, false);
                    }
                }
            }, ROTATION_TIME + 20);


        }
        //build up a list of rotations that were requested but not yet executed
        else if (futureRotations.size() <= maxFutureRotations){
            futureRotations.add(rightRound);
        }
    }


//    /**
//     * Get one new stripe.
//     */
//    private void startNewStripe() {
////        if(gameOver){
////            return;
////        }
//        removeInvisibleStripes();
//        System.out.println("Visible stripes: " + stripes.size());
//
//
//        //prepare variables stripe
//        int stripe_width = STANDARD_STRIPE_WIDTH + r.nextInt(STANDARD_STRIPE_WIDTH * 2);
//        int stripe_color = r.nextInt(4);
//        int travel_duration = (int) (INITIAL_DURATION - (duration_variation - (duration_variation - INITIAL_SPEED) * Math.exp(-growth_factor * score)));
//        int delay = travel_duration - (INITIAL_DURATION - duration_variation) + 5 * ROTATION_TIME;
//
//
//        //prepare new stripe
//        Stripe stripe = new Stripe(stripe_width, stripe_color, travel_duration, delay);
//        FrameLayout newStripe = stripe.prepare();
////        if(!gameOver) {
//        runOnUiThread(() -> all.addView(newStripe));
//        stripes.add(newStripe);
////        }
//        runOnUiThread(() -> {
//            square.bringToFront();
//            scoreboard.bringToFront();
//            pause_button.bringToFront();
//        });
//
//
//        //start sliding animation and check for collision
////        if (!gameOver){
//        // what to animate?, how to animate?, from where to start (+ some extra space for 18.5:9 phones)?, where to stop?
//        ObjectAnimator objectAnimator =
//                ObjectAnimator.ofFloat(newStripe, View.TRANSLATION_X, screenWidth + screenWidth / 10, -stripe.width);
//        objectAnimator.setDuration(stripe.travelDuration);
//        objectAnimator.setInterpolator(new LinearInterpolator());
//        runOnUiThread(objectAnimator::start);
//        stripeAnimators.add(objectAnimator);
//
//        //check for collision in another thread
//        new Thread(() -> checkForCollision(stripe, newStripe)).start();
//
//
//        //TODO remove invisible stripes and/or change cancel method to allow starting a new game
////        }
//
//        if (!gameOver) {
//            sleep(stripe.delay);
//            startNewStripe();
//        }
//
//
//    }



//    /**
//     * Only called when a collision happened and the game shall stop. A new game can only be started
//     * by calling startNewGame.
//     */
//    private void gameEnds(){
//        stripeMovement.interrupt();
////        gameOver = true;
////        cancelStripes();
////        Toast.makeText(getApplicationContext(), "Game over!", Toast.LENGTH_SHORT).show();
//    }
//
//    //TODO integrate at a nice place
//    private void startNewGame(){
//        gameOver = false;
//        score = 0;
//        scoreboard.setText(R.string.initial_score);
//        cancelStripes();
//        new Handler().postDelayed(this::startNewStripe, 2000);
//    }



//TODO tidy up and comment EVERYTHING!
}
