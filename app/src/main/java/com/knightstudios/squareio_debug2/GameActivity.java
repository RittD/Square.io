package com.knightstudios.squareio_debug2;



import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.util.Queue;


/**
 * The main activity of the game. It calls GameActivity_Layout which renders the whole game.
 * This class prepares the rendering and takes the user interaction in the game.
 */
public class GameActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    GameActivity_Layout gameActivityLayoutView;
    static DisplayMetrics displayMetrics;

//    static final int RC_SIGN_IN = 9001;
//    GoogleApiClient signInClient;

//    private LinearLayout prof_section;
//    private SignInButton signIn;
//    private TextView name,email;
//    private ImageView prof_pic;
    private GoogleApiClient client;
    private static final int REQ_CODE = 9001;

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.btn_login:
//                signIn();
//                break;
//            case R.id.btn_logout:
//                signOut();
//                break;
//        }
//    }

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
        //TODO redirect to startscreen instead of pause
        new Handler().postDelayed(()->gameActivityLayoutView.setPaused(true),50);
        setContentView(gameActivityLayoutView);

//        setContentView(R.layout.activity_game_over);

//        prof_section = findViewById(R.id.prof_section);
//        Button signOut = findViewById(R.id.btn_logout);
//        signIn = findViewById(R.id.btn_login);
//        name = findViewById(R.id.name);
//        email = findViewById(R.id.email);
//        prof_pic = findViewById(R.id.prof_pic);
//        signIn.setOnClickListener(this);
//        signOut.setOnClickListener(this);
//        prof_section.setVisibility(View.GONE);
        GoogleSignInOptions signInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        client = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions).build();

        signIn();


//        new Handler().postDelayed(()->setContentView(gameActivityLayoutView),20000);

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
                //TODO replace with welcome dialog:
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
            float margin_pause = getResources().getDimension(R.dimen.margin_pause);
            float pause_size = getResources().getDimension(R.dimen.size_pause);
            if (gameActivityLayoutView.isGameOver()) {
                if (!gameActivityLayoutView.isWaiting()) {
                    gameActivityLayoutView.startNewGame();
                }
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
    }


    @Override
    protected void onResume() {
        super.onResume();

        gameActivityLayoutView.resume();
    }
}
