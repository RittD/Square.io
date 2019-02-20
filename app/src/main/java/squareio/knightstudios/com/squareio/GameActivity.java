package squareio.knightstudios.com.squareio;



import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.WindowManager;
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
//    ImageView pause;
//    boolean paused = false;
//
//
//    //Screen
//    public RelativeLayout all;
//    static int screenWidth;
//
//
//    //Square
//    static final int ROTATION_TIME = 80;
//    static int squareWidth;
//    public ImageView square;
//    int currentPosition = 0;
//    boolean rotating = false;
//    Queue<Boolean> futureRotations = new LinkedList<>();
//    int maxFutureRotations = 10;


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
//        new Handler().postDelayed(()->stripeMovement.stripeAnimators.get(stripeMovement.stripeAnimators.size()-1).pause(),7000);

//        sleep(2000);
//            startNewStripe();
//
//            //TODO understand multithreading (fix pause/resume problem)
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
//        //get pause button and enable pausing/resuming the game
//        pause = findViewById(R.id.pause);
//        pause.setOnClickListener((view) -> {
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
//    /**
//     * Provides the rotation of the square clockwise or anti-clickwise when clicking on the left
//     * or the right half of the screen.
//     * @param event the motion event that occured, only interesting if it is a click
//     * @return whatever
//     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(event.getAction() == MotionEvent.ACTION_DOWN) {
//            if (gameOver) {
//                startNewGame();
//            }
//            else {
                //TODO not in the middle on 18.5:9 screens, is it?
//                boolean clickedInRightHalf = event.getX() >= screenWidth / 2;
//                if(clickedInRightHalf){
                    //TODO fix rotation
//                }
//                rotateSquare(square, clickedInRightHalf);
//            }
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
//            animator.pause();
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



//
//    /**
//     * Rotates the square right if the right half of the screen is clicked. Because otherwise the position of the square could
//     * differ from the wanted states (0°, 90°, 180° and 270°), it is necessary to prohibit starting a new rotation,
//     * while already doing one. Therefore a queue is used that is filled with rotations that were requested but cannot be
//     * executed right away. This queue is dismantled right after (+20 ms to prevent mistakes) the first rotation is finished.
//     * @param square the square to rotate
//     * @param rightRound
//     */
//    private void rotateSquare(View square, boolean rightRound){
//        if (!rotating) {
//            rotating = true;
//            new Handler().postDelayed(() -> currentPosition = (currentPosition + (rightRound ? 1 : 3)) % 4, ROTATION_TIME /2);
//            new RotationAnimation(square).setDegrees(rightRound ? 90 : -90).setDuration(ROTATION_TIME).animate();
//
//            //after rotating take the rotations from the list until it is empty
//            new Handler().postDelayed(() -> {
//                rotating = false;
//
//                if (!futureRotations.isEmpty()){
//                    boolean rotateRight = futureRotations.poll();
//
//                    if (rotateRight){
//                        rotateSquare(square, true);
//                    }
//                    else{
//                       rotateSquare(square, false);
//                    }
//                }
//            }, ROTATION_TIME + 20);
//
//
//        }
//        //build up a list of rotations that were requested but not yet executed
//        else if (futureRotations.size() <= maxFutureRotations){
//            futureRotations.add(rightRound);
//        }
//    }


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
//            pause.bringToFront();
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
