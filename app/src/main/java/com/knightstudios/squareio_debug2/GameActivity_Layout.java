package com.knightstudios.squareio_debug2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

//TODO build different sizes of icons+square
/**
 * This Class is the frontend of the GameActivity. It shows the game by executing the run() method
 * which is called automatically when setting this class as layout(setContentView) in an activity.
 * It is a dynamical layout that uses a canvas consisting of the whole screen where the current state
 * of the UI is drawn onto 70 times per second.
 */
public class GameActivity_Layout extends SurfaceView implements Runnable {

    private static final DisplayMetrics screenMetrics = GameActivity.displayMetrics;
    private Thread thread = null;
    private GameState gameState = GameState.IN_MENU;


    private static Bitmap background, square, pause_button, home_button, leaderboard_button, settings_button;
    static int square_x, square_y, pause_x, pause_y, score_x, score_y, leaderboard_x, leaderboard_y,
            gameOver_x, gameOver_y, tapRestart_y, home_x, home_y;
    private boolean canDraw = false;
    private static Canvas canvas;
    private static SurfaceHolder surfaceHolder;
    private double frame_time_seconds, frame_time_ms, frame_time_ns;

    private double delta_t;


    private Paint scoreStandardPaint, whiteTextPaintBig, pausePaint, scoreHighlightPaint, alphaPaint;
    private SparseArray<Paint> stripePaints = new SparseArray<>(4);

    private Random random = new Random();

    //the stripes that are visible on the display
    private List<Stripe> stripes = new LinkedList<>();


    //Square rotation
    private Matrix matrix = new Matrix();
    private float rotationAngle = 0;
    private Queue<Integer> futureRotations = new PriorityQueue<>();


//    private boolean paused = false;
//    private boolean gameOver = false;
    Rect backgroundVeil;

    //level raises every 10 points
    private int score = 0, level = 0;

    //used for level-up effects etc.
    private boolean level_up = false;

    //in px per second
    private final int MAX_SPEED = dpToPx(550);
    private final int MIN_SPEED = dpToPx(200);


    // in milliseconds
    private static final int MAX_PAUSE = 2300;
    private static final int MIN_PAUSE = 800;

    private int timeTillNextStripe = 0;

    //difficulty (the bigger the absolute value is, the faster the speed rises)
    private static final double EXP_FACTOR = -0.08;

    //needed to wait a moment after a game ends
    private boolean waitAfterNext = false;
    private boolean waiting = false;
    private boolean alreadyWaited = false;
    private int x = 0;


    public GameActivity_Layout(Context context) {
        super(context);
        surfaceHolder = getHolder();

        prepareUIElements();

        preparePainters();

        //Prepare game loop variables
        frame_time_seconds = 1/70.0;
        frame_time_ms = frame_time_seconds * 1000;
        frame_time_ns = frame_time_ms * 1_000_000;
    }


    private void prepareUIElements() {

        //background
        Bitmap immutableBackground = BitmapFactory.decodeResource(getResources(),R.drawable.background);
        background = immutableBackground.copy(Bitmap.Config.ARGB_8888,true);
        background = Bitmap.createScaledBitmap(background, screenMetrics.widthPixels, screenMetrics.heightPixels, false);

        //square
        square = BitmapFactory.decodeResource((getResources()),R.drawable.twister_light_100px);

        //icons
        int icon_size = (int)getResources().getDimension(R.dimen.size_icon);

        Bitmap immutablePause = BitmapFactory.decodeResource(getResources(),R.drawable.pause);
        pause_button = immutablePause.copy(Bitmap.Config.ARGB_8888,true);
        pause_button = Bitmap.createScaledBitmap(pause_button, icon_size, icon_size, false);


//        Bitmap immutablePlay = BitmapFactory.decodeResource(getResources(),R.drawable.play);
//        play_button = immutablePlay.copy(Bitmap.Config.ARGB_8888,true);
//        play_button = Bitmap.createScaledBitmap(play_button, icon_size, icon_size, false);

        Bitmap immutableHome = BitmapFactory.decodeResource(getResources(),R.drawable.home_80px);
        home_button = immutableHome.copy(Bitmap.Config.ARGB_8888,true);
        home_button = Bitmap.createScaledBitmap(home_button, icon_size, icon_size, false);


        Bitmap immutableHighscore = BitmapFactory.decodeResource(getResources(),R.drawable.medal_80px);
        leaderboard_button = immutableHighscore.copy(Bitmap.Config.ARGB_8888,true);
        leaderboard_button = Bitmap.createScaledBitmap(leaderboard_button, icon_size, icon_size, false);


        Bitmap immutableSettings = BitmapFactory.decodeResource(getResources(),R.drawable.settings_80px);
        settings_button = immutableSettings.copy(Bitmap.Config.ARGB_8888,true);
        settings_button = Bitmap.createScaledBitmap(settings_button, icon_size, icon_size, false);



        backgroundVeil = new Rect();
        backgroundVeil.set(0, 0, screenMetrics.widthPixels, screenMetrics.heightPixels);


        prepareUIPositions();
    }


    private void prepareUIPositions() {
        square_x = (int) getResources().getDimension(R.dimen.margin_square);
        square_y = (screenMetrics.heightPixels)/2 - square.getHeight()/2;

        int icon_size = (int)getResources().getDimension(R.dimen.size_icon);
        int icon_margin = (int) getResources().getDimension(R.dimen.margin_icon);

        pause_x = screenMetrics.widthPixels - (icon_size + icon_margin);
        pause_y = icon_margin;

        home_x = screenMetrics.widthPixels - (icon_size + icon_margin);
        home_y = screenMetrics.heightPixels - (icon_size + icon_margin);

        leaderboard_x = icon_margin;
        leaderboard_y = icon_margin;

        score_x = (int) getResources().getDimension(R.dimen.margin_score_x);
        score_y = (int) getResources().getDimension(R.dimen.text_size)+(int) getResources().getDimension(R.dimen.margin_score_y);

        gameOver_x = screenMetrics.widthPixels/2;
        gameOver_y = screenMetrics.heightPixels/2;

        tapRestart_y = gameOver_y
                + (int) getResources().getDimension(R.dimen.margin_tap_to_start)
                + (int) getResources().getDimension(R.dimen.text_size_medium);
    }


    private void preparePainters() {
        //Paints for stripes
        Paint redPaint = new Paint();
        redPaint.setColor(Color.rgb(255, 0, 0));
        redPaint.setStyle(Paint.Style.FILL);


        Paint yellowPaint = new Paint();
        yellowPaint.setColor(Color.rgb(255,235,0));
        yellowPaint.setStyle(Paint.Style.FILL);


        Paint greenPaint = new Paint();
        greenPaint.setColor(Color.rgb(0,160,14));
        greenPaint.setStyle(Paint.Style.FILL);


        Paint bluePaint = new Paint();
        bluePaint.setColor(Color.rgb(0, 25, 225));
        bluePaint.setStyle(Paint.Style.FILL);

        //Paint for stripe borders
        Paint blackPaint = new Paint();
        blackPaint.setColor(Color.BLACK);
        blackPaint.setStyle(Paint.Style.STROKE);

        //Paints for text
        scoreStandardPaint = new Paint();
        scoreStandardPaint.setColor(Color.WHITE);
        scoreStandardPaint.setTextSize(getResources().getDimension(R.dimen.text_size));
        scoreStandardPaint.setTextAlign(Paint.Align.CENTER);
        scoreStandardPaint.setStyle(Paint.Style.FILL);
        scoreStandardPaint.setShadowLayer(50,0,0,Color.BLACK);


        scoreHighlightPaint = new Paint();
        scoreHighlightPaint.setColor(Color.RED);
        scoreHighlightPaint.setTextSize(getResources().getDimension(R.dimen.text_size_big));
        scoreHighlightPaint.setTextAlign(Paint.Align.CENTER);
        scoreHighlightPaint.setStyle(Paint.Style.FILL);

        whiteTextPaintBig = new Paint();
        whiteTextPaintBig.setColor(Color.WHITE);
        whiteTextPaintBig.setTextSize(getResources().getDimension(R.dimen.text_size_big));
        whiteTextPaintBig.setTextAlign(Paint.Align.CENTER);
        whiteTextPaintBig.setStyle(Paint.Style.FILL);
        whiteTextPaintBig.setShadowLayer(30, 0, 0, Color.WHITE);

        pausePaint = new Paint();
        pausePaint.setColor(Color.WHITE);
        pausePaint.setTextSize(getResources().getDimension(R.dimen.text_size_medium));
        pausePaint.setTextAlign(Paint.Align.CENTER);
        pausePaint.setStyle(Paint.Style.FILL);
        pausePaint.setShadowLayer(25, 0, 0, Color.WHITE);



        //alpha paint
        alphaPaint = new Paint();
        scoreHighlightPaint.setColor(Color.BLACK);
        alphaPaint.setAlpha(160);
        scoreHighlightPaint.setStyle(Paint.Style.FILL);

        //collect paints for stripes
        stripePaints.put(0,redPaint);
        stripePaints.put(1,yellowPaint);
        stripePaints.put(2,greenPaint);
        stripePaints.put(3,bluePaint);
    }



    public void pause(){
        canDraw = false;

        while(true) {
            try {
                thread.join();
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        thread = null;
        if (gameState == GameState.IN_GAME){
                gameState = GameState.PAUSED;
        }

    }


    public void resume(){
        canDraw = true;

        if(thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }


    /**
     * Called instantly when the class is used as layout of an activity:
     *
     * To build a frame of the layout this method first calculates the current position of backgroundVeil
     * UI elements (especially the stripes and the square) in the update()-method.
     *
     * After updating the positions, the rendering of the UI and drawing on the canvas
     * is done in the draw()-method.
     *
     * At last the thread goes to sleep until the appointed frame time is achieved (for delta_t ns).
     * Then the calculation for the next frame starts.
     *
     * Note: If the render time takes longer than the appointed frame time, delta_t is negative
     * which leads to a frame being longer visible. The more often this happens the worse the animations
     * appear.
     *
     * This procedure is endlessly iterated 70 times per second so each frame is visible for 1/70 seconds.
     *
     * This way of rendering makes the animations pretty smooth and continuous. It would look even better,
     * if the rendering time would be less but the background already takes ~6 ms to display.
     **/
    @Override
    public void run() {

        double tLastFrame = System.nanoTime();
        delta_t = 0;


        //TODO fix problem with animation/ find smoother animation method...
        while(canDraw){

            update();


            if (!surfaceHolder.getSurface().isValid()){
                continue;
            }
            draw();


            delta_t = frame_time_ns - (System.nanoTime() - tLastFrame);

            try {
                if (delta_t > 0) {
                    thread.sleep((long) delta_t / 1_000_000);
                }
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
            System.out.println("Render time: "+(System.nanoTime() - tLastFrame)/1_000_000);
            tLastFrame = System.nanoTime();
        }
    }

    private void update() {

        //Do not change anything if not in game!
        if (gameState != GameState.IN_GAME) {
            return;
        }

        //Update the queue of rotations that the square shall fulfill at the next possible moment
        //by one rotation step, which means the part of a rotation that shall be done within one frame.
        if (!futureRotations.isEmpty()) {
            int remainingRotation = futureRotations.poll();
            int rotationStep = calculateDegreesPerFrame();
            //step clockwise
            if (remainingRotation > 0 && remainingRotation >= rotationStep) {
                rotationAngle = (rotationAngle + rotationStep) % 360;
                futureRotations.add(remainingRotation - rotationStep);
            }
            //step anti-clockwise
            else if (remainingRotation < 0 && remainingRotation <= -rotationStep) {
                rotationAngle = (rotationAngle - rotationStep) % 360;
                futureRotations.add(remainingRotation + rotationStep);
            }
            //last step
            else{
                rotationAngle = (rotationAngle + remainingRotation) % 360;
            }
        }


        //prepare rotation matrix for square
        matrix.setTranslate(-square_x - square.getWidth() / 2f, -square_y - square.getWidth() / 2f);
        matrix.setRotate(rotationAngle, square.getWidth() / 2f, square.getHeight() / 2f);
        matrix.postTranslate(square_x, square_y);


        //update stripe positions
        List<Stripe> visibleStripes = new LinkedList<>();
        for (Stripe s : stripes) {
            if(updateStripe(s, delta_t)){
                visibleStripes.add(s);
            }
        }
        stripes = visibleStripes;

        //update time till next stripe shall appear
        timeTillNextStripe -= frame_time_ms;


        //update the level that the player has achieved
        level = score / 10;


        //check for collisions
        int firstContact = square_x + square.getWidth();
        for (Stripe s : stripes) {
            Rect currentStripe = s.getStripe();
            int square_color = getColorOfSquare();
            int stripe_color = s.getColorCode();

            int lastContact = firstContact - (currentStripe.right - currentStripe.left);

            //collision detected:
            if (currentStripe.left <= firstContact && currentStripe.left >= lastContact
                    && square_color != stripe_color){
                gameState = GameState.GAME_OVER;
            }

            //no collision found:
            if (currentStripe.left <= lastContact && s.isStillCountable()
                    && square_color == stripe_color){
                score++;
                level_up = score%10 == 0;
                s.setStillCountableFalse();
            }
        }
    }

    public void startNewGame() {
        gameState = GameState.IN_GAME;
        rotationAngle = 0;
        futureRotations = new PriorityQueue<>();
        alreadyWaited = false;
        x = 0;
        score = 0;
        stripes = new LinkedList<>();
        timeTillNextStripe = 500;
    }


    /**
     * Updates the position of the given stripe
     * @param stripe the stripe that shall be moved
     * @param delta_t the time that is needed to fill the remains of the time period set
     * @return whether the given stripe is still visible and was therefore moved
     */
    private boolean updateStripe(Stripe stripe, double delta_t) {
        Rect movedStripe = stripe.getStripe();

        //Stripe no longer visible
        if(movedStripe.right < 0){
            return false;
        }

        double px_per_sec = MAX_SPEED - (MAX_SPEED - MIN_SPEED) * Math.exp(EXP_FACTOR * level);
        double movement = px_per_sec * (frame_time_seconds - (delta_t < 0 ? (delta_t / 1_000_000_000) : 0));


        movedStripe.left -= movement;
        movedStripe.right -= movement;
        stripe.setStripe(movedStripe);
        return true;
    }

    private void draw() {
        canvas = surfaceHolder.lockCanvas();

        //choose the layout of the app depending on the state the app is currently in
        switch(gameState){
            case IN_GAME:
                drawGameView();
                break;
            case IN_MENU:
                drawMenuView();
                break;

            case PAUSED:
                drawPauseMenuView();
                break;
            case GAME_OVER:
                drawGameOverView();
                break;

        }

        surfaceHolder.unlockCanvasAndPost(canvas);
    }


    private void drawGameView() {

        //background
        canvas.drawBitmap(background,0,0, null);


        //stripes
        drawStripes();

        //TODO find out why it spawns at (0|0) in the beginning
        //square (rotated with the given matrix)
        canvas.drawBitmap(square, matrix, null);


        //score (highlighted if level up is achieved)
        if (level_up){
            level_up = false;
            canvas.drawText(String.valueOf(score), score_x, score_y, scoreHighlightPaint);
        }
        else{
            canvas.drawText(String.valueOf(score), score_x, score_y, scoreStandardPaint);
        }


        //pause button
        canvas.drawBitmap(pause_button, pause_x, pause_y,null);
    }


    private void drawMenuView() {
        //background
        canvas.drawBitmap(background,0,0, null);


        //Square.io text
        canvas.drawText("SQUARE.IO", gameOver_x, gameOver_y, whiteTextPaintBig);

        int PULSATING_PERIOD = 1300;
        canvas.drawText("(Tap to start)", gameOver_x, tapRestart_y, getPulsatingPaint(PULSATING_PERIOD));


        //leaderboard
        canvas.drawBitmap(leaderboard_button, leaderboard_x, leaderboard_y, null);

        //settings
        canvas.drawBitmap(settings_button, pause_x, pause_y, null);
    }


    private void drawPauseMenuView() {

        //background
        canvas.drawBitmap(background,0,0, null);


        //stripes
        drawStripes();


        //square (rotated with the given matrix)
        canvas.drawBitmap(square, matrix, null);


        //veil to make the game view less visible
        canvas.drawRect(backgroundVeil, alphaPaint);
        canvas.drawText("Pause", gameOver_x, gameOver_y, pausePaint);
        int PULSATING_PERIOD = 1300;
        canvas.drawText("(Tap to resume)", gameOver_x, tapRestart_y, getPulsatingPaint(PULSATING_PERIOD));


        //score
        canvas.drawText(String.valueOf(score), score_x, score_y, scoreStandardPaint);


        //home button (return to menu)
        canvas.drawBitmap(home_button, home_x, home_y, null);

        //settings
        canvas.drawBitmap(settings_button, pause_x, pause_y, null);
    }


    private void drawGameOverView() {
        //background
        canvas.drawBitmap(background,0,0, null);

        //stripes
        drawStripes();

        //square (rotated with the given matrix)
        canvas.drawBitmap(square, matrix, null);


        //veils the game to make the text more visible
        canvas.drawRect(backgroundVeil, alphaPaint);


        //score (highlighted if level up is achieved)
        //TODO replace with: if(newHighsore)
        if (false){
            canvas.drawText(String.valueOf(score), score_x, score_y, scoreHighlightPaint);
        }
        else{
            canvas.drawText(String.valueOf(score), score_x, score_y, scoreStandardPaint);
        }


        //game over text
        canvas.drawText("Game over!", gameOver_x, gameOver_y, whiteTextPaintBig);
        int PULSATING_PERIOD = 1300;
        canvas.drawText("(Tap to restart)", gameOver_x, tapRestart_y, getPulsatingPaint(PULSATING_PERIOD));

        //home button (return to menu)
        canvas.drawBitmap(home_button, home_x, home_y, null);

        //settings
        canvas.drawBitmap(settings_button, pause_x, pause_y, null);
    }



    private Paint getPulsatingPaint(int pulsatingPeriod) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        int MIN_ALPHA = 30;
        int MAX_ALPHA = 210;
        int midAlpha = (MAX_ALPHA-MIN_ALPHA)/2;
        paint.setAlpha((int) (midAlpha * Math.cos(x*Math.PI/pulsatingPeriod)+midAlpha+MIN_ALPHA));
        paint.setTextSize(getResources().getDimension(R.dimen.text_size_small));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setStyle(Paint.Style.FILL);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));

        x+=frame_time_ms;
        if (x >= 2*pulsatingPeriod){
            x-=2*1500;
        }

        return paint;
    }

    private void drawStripes() {

        if (timeTillNextStripe <= 0){
            generateNewRandomStripe();
            timeTillNextStripe = (int) (MIN_PAUSE+(MAX_PAUSE-MIN_PAUSE)*Math.exp(3*EXP_FACTOR*level));
        }
        for (Stripe s : stripes) {
            canvas.drawRect(s.getStripe(), s.getPaint());
            //Todo give stripes a black edge?!
            //canvas.drawRect(s.getStripe(), blackPaint);
        }

        //Wait for 1 second after losing a game. Do not accept any input in this time.
        if (waitAfterNext){
            try {
                waiting = true;
                //TODO Find a better solution for waiting after game ends
                thread.sleep(1000);
                waiting = false;
                alreadyWaited = true;
                waitAfterNext = false;
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
        }

        //Wait after next step. This is the first one where the stripe and the square collide.
        if(gameState == GameState.GAME_OVER && !alreadyWaited){
           waitAfterNext = true;
        }


    }


    /**
     * Draw a new random colored stripe with black borders on the right edge of the display.
     */
    public void generateNewRandomStripe() {

        //color code: 0 = red, 1 = yellow, 2 = blue, 3 = green
        int colorCode = random.nextInt(4);
        final int STANDARD_STRIPE_WIDTH = 300;
        final int VARIATION_STRIPE_WIDTH = 250;
        int stripeWidth = STANDARD_STRIPE_WIDTH + random.nextInt(VARIATION_STRIPE_WIDTH);
        Paint painter = stripePaints.get(colorCode);
        Rect stripe = new Rect();
        stripe.set(screenMetrics.widthPixels, 0, screenMetrics.widthPixels + stripeWidth, screenMetrics.heightPixels);
        Stripe newStripe = new Stripe(stripe, colorCode, painter);
        stripes.add(newStripe);
    }


    /*-------------------------------------------------------------------------------*/


    //Auxiliary methods:

    private int calculateDegreesPerFrame() {
        final int ROTATION_TIME_MS = (int) (MIN_PAUSE/8 + (MAX_PAUSE/12-MIN_PAUSE/8)* Math.exp(2*EXP_FACTOR*level));
        final int DEGREES_PER_ROTATION = 90;
        return (int) ((frame_time_ms - (delta_t < 0 ? delta_t/1_000_000 : 0)) * DEGREES_PER_ROTATION)/ ROTATION_TIME_MS;
    }


    public int getColorOfSquare(){

        /* Because of the java version of % the value of x%360 is of [-359,359]
         while -10%360 and 355%360 should be equal. */
        if (rotationAngle < 0){
            rotationAngle += 360;
        }


        if (rotationAngle >= 315 && rotationAngle < 360 ||
                rotationAngle >= 0 && rotationAngle < 45){
            return 1;
        }
        else if (rotationAngle >= 45 && rotationAngle < 135){
            return 0;
        }
        else if (rotationAngle >= 135 && rotationAngle < 225){
            return 3;
        }
        else if (rotationAngle >= 225 && rotationAngle < 315){
            return 2;
        }
        else{
            System.err.println("Error");
            return -1;
        }
    }

    private int dpToPx(int dp){
        return screenMetrics.densityDpi/160 * dp;
    }
//
//    private int pxToDp(int px){
//        return px * 160 / screenMetrics.densityDpi;
//    }


    /*-------------------------------------------------------------------------------*/


    //Getter:

    public Queue<Integer> getFutureRotations() {
        return futureRotations;
    }

    public boolean isWaiting() {
        return waiting;
    }

    public GameState getGameState() {
        return gameState;
    }

    /*-------------------------------------------------------------------------------*/


    //Setter:

    public void setFutureRotations(Queue<Integer> futureRotations) {
        this.futureRotations = futureRotations;
    }


    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }
}
