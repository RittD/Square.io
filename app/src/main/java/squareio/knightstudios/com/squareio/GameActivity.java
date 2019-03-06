package squareio.knightstudios.com.squareio;



import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

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


    //Square
//    static final int ROTATION_TIME = 80;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //get display metrics
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        //set layout view (that continuously draws the screen)
        gameActivityLayoutView = new GameActivity_Layout(this);
        setContentView(gameActivityLayoutView);
    }



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
            if (gameActivityLayoutView.gameOver) {
                Toast.makeText(getApplicationContext(), "Let's try again!", Toast.LENGTH_SHORT).show();
                gameActivityLayoutView.startNewGame();
            }
            else {
                boolean clickedOnPause = x >= displayMetrics.widthPixels - (pause_size + margin_pause) &&
                        y >= margin_pause && y <= margin_pause + pause_size;
                if (clickedOnPause) {
                    gameActivityLayoutView.setPaused(!gameActivityLayoutView.isPaused());

                } else {
                    boolean clickedInRightHalf = event.getX() >= displayMetrics.widthPixels / 2;

                    //futureRotations are the rotations that will be executed after the currently executed rotation
                    Queue<Integer> futureRotations = gameActivityLayoutView.getFutureRotations();

                    int MAX_FUTURE_ROTATIONS = 5;

                    //only allow a specific maximum amount of future rotations and do not accept clicks while paused
                    if (futureRotations.size() < MAX_FUTURE_ROTATIONS && !gameActivityLayoutView.isPaused()) {
                        if (clickedInRightHalf) {
                            //rotate square right by 90 degrees
                            futureRotations.add(90);
                        } else {
                            //rotate square left by 90 degrees
                            futureRotations.add(-90);
                        }
                    }
                    gameActivityLayoutView.setFutureRotations(futureRotations);
                }
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


    @Override
    protected void onResume() {
        super.onResume();
        gameActivityLayoutView.resume();
//        stripeMovement.onResume();
    }



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
