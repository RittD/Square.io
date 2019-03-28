package com.knightstudios.squareio_debug2;



import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Queue;

import static com.knightstudios.squareio_debug2.GameActivity_Layout.leaderboard_x;
import static com.knightstudios.squareio_debug2.GameActivity_Layout.leaderboard_y;
import static com.knightstudios.squareio_debug2.GameActivity_Layout.home_x;
import static com.knightstudios.squareio_debug2.GameActivity_Layout.home_y;
import static com.knightstudios.squareio_debug2.GameActivity_Layout.pause_x;
import static com.knightstudios.squareio_debug2.GameActivity_Layout.pause_y;


/**
 * The main activity of the game. It calls GameActivity_Layout which renders the whole game.
 * This class prepares the rendering and takes the user interaction in the game.
 */
public class GameActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    GameActivity_Layout gameActivityLayoutView;
    static DisplayMetrics displayMetrics;

//    private ImageView prof_pic;
    private GoogleApiClient client;
    private static final int REQ_CODE = 9001;


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

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

        GoogleSignInOptions signInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        client = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions).build();

        signIn();

    }

    private void signIn(){
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(client);
        startActivityForResult(intent,REQ_CODE);
    }

//    private void signOut(){
//        Auth.GoogleSignInApi.signOut(client).setResultCallback(new ResultCallback<Status>() {
//            @Override
//            public void onResult(@NonNull Status status) {
//                updateUI(false);
//            }
//        });
//    }

    private void handleResult(GoogleSignInResult result){
        if(result.isSuccess()){
            GoogleSignInAccount account = result.getSignInAccount();
            if (account != null){
                String name2 = account.getDisplayName();
                //TODO remove or replace this?
                Toast.makeText(getApplicationContext(),"Welcome "+ (name2 == null ? "Guest" : name2.split("\\s")[0]+"!"), Toast.LENGTH_SHORT).show();
//                String email2 = account.getEmail();
//                String img_url = account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : "";
//                name.setText(name2);
//                email.setText(email2);
//                Glide.with(this).load(img_url).into(prof_pic);
            }
            updateUI(true);
        }
        else{
            updateUI(false);
        }
    }

    private void updateUI(boolean isLogIn) {
        if (isLogIn){
            //TODO show daily/all time highscore?
//            prof_section.setVisibility(View.VISIBLE);
//            signIn.setVisibility(View.GONE);
        }
        else{
//            prof_section.setVisibility(View.GONE);
//            signIn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(result);
        }
    }

    /**
     * Handles the whole user interaction in the game activity:
     *
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
            int icon_size = (int) getResources().getDimension(R.dimen.size_icon);
            boolean clickedOnHome = x >= home_x && x <= home_x + icon_size &&
                                    y >= home_y && y <= home_y + icon_size;
            boolean clickedOnPauseOrSettings =  x >= pause_x && x <= pause_x + icon_size &&
                                      y >= pause_y && y <= pause_y + icon_size;
            boolean clickedOnHighscore = x >= leaderboard_x && x <= leaderboard_x + icon_size &&
                                         y >= leaderboard_y && y <= leaderboard_y + icon_size;
//            boolean clickedOnAchievements = x >= achievements_x && x <= achievements_x + icon_size &&
//                    y >= achievements_y && y <= achievements_y + icon_size;


            //Depending on the current view the reaction to touch event should vary.
            switch(gameActivityLayoutView.getGameState()){

                case IN_GAME:
                    //pause
                    if (clickedOnPauseOrSettings) {
                        gameActivityLayoutView.setGameState(GameState.PAUSED);
                        break;
                    }

                    boolean clickedInRightHalf = event.getX() >= displayMetrics.widthPixels / 2;

                    //futureRotations are the rotations that will be executed after the currently executed rotation
                    Queue<Integer> futureRotations = gameActivityLayoutView.getFutureRotations();

                    int MAX_FUTURE_ROTATIONS = 5;

                    //only allow a specific maximum amount of future rotations
                    if (futureRotations.size() < MAX_FUTURE_ROTATIONS) {
                        if (clickedInRightHalf) {
                            //rotate square right by 90 degrees
                            futureRotations.add(90);
                        } else {
                            //rotate square left by 90 degrees
                            futureRotations.add(-90);
                        }
                    }
                    gameActivityLayoutView.setFutureRotations(futureRotations);

                    break;


                case IN_MENU:
                    if (clickedOnHighscore){
                        //TODO implement and open leaderboard
                        break;
                    }
                    //TODO replace with clickedOnAchievements
                    if(false){
                        //TODO implement and open achievements
                        break;
                    }
                    //settings
                    if (clickedOnPauseOrSettings){
                        //TODO implement and open settings
                        break;
                    }

                    gameActivityLayoutView.startNewGame();


                    break;


                case PAUSED:
                    if (clickedOnHome) {
                        gameActivityLayoutView.setGameState(GameState.IN_MENU);
                        break;
                    }
                    //settings
                    if (clickedOnPauseOrSettings) {
                        //TODO implement and open settings
                        break;
                    }
                    gameActivityLayoutView.setGameState(GameState.IN_GAME);
                    break;


                case GAME_OVER:
                    if (clickedOnHome) {
                        gameActivityLayoutView.setGameState(GameState.IN_MENU);
                        break;
                    }
                    //settings
                    if (clickedOnPauseOrSettings) {
                        //TODO implement and open settings
                        break;
                    }

                    gameActivityLayoutView.startNewGame();
                    gameActivityLayoutView.setGameState(GameState.IN_GAME);
                    break;

            }
        }
        return super.onTouchEvent(event);
    }


    @Override
    protected void onPause() {
        super.onPause();
        gameActivityLayoutView.pause();
    }


    @Override
    protected void onResume() {
        super.onResume();

        gameActivityLayoutView.resume();
    }
}
