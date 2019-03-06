package squareio.knightstudios.com.squareio;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

public class GameActivity_Layout extends SurfaceView implements Runnable {

    Thread thread = null;
    boolean canDraw = false;

    Bitmap background;
    Bitmap square;
    Bitmap pause_button;
    Bitmap play_button;
    Canvas canvas;
    SurfaceHolder surfaceHolder;
    double frames_per_second, frame_time_seconds, frame_time_ms, frame_time_ns;
    double tLastFrame, tEndOfRender, delta_t;



    DisplayMetrics screenMetrics = GameActivity.displayMetrics;


    int square_x, square_y, pause_x, pause_y, score_x, score_y;


    Paint redPaint, yellowPaint,greenPaint, bluePaint, blackPaint, whitePaint;
    SparseArray<Paint> paints = new SparseArray<>(4);

    Random random = new Random();

    // in px per second
    int speed = 20;

    // in milliseconds
    int pauseTime = 2500;

    int timeTillNextStripe = 0;

    List<Stripe> stripes = new LinkedList<>();

    //Square rotation
    Matrix matrix = new Matrix();
    float rotationAngle = 0;
    private Queue<Integer> futureRotations = new PriorityQueue<>();


    boolean paused = false;

    int score = 0;
    private List<Stripe> countableStripes = new LinkedList<>();
    boolean gameOver = false;

    int level = 0;

    public GameActivity_Layout(Context context) {
        super(context);
        surfaceHolder = getHolder();

        //Prepare UI Elements
        Bitmap immutableBackground = BitmapFactory.decodeResource(getResources(),R.drawable.background);
        background = immutableBackground.copy(Bitmap.Config.ARGB_8888,true);
        background = Bitmap.createScaledBitmap(
                background, screenMetrics.widthPixels, screenMetrics.heightPixels, false);
//        System.out.println("Density: "+screenMetrics.densityDpi);
//        System.out.println("Height: "+screenMetrics.heightPixels);
//        System.out.println("Width: "+screenMetrics.widthPixels);
//        System.out.println("Real height: "+background.getHeight()/screenMetrics.densityDpi*160);
        background.setHeight(background.getHeight());
//        System.out.println("New height: "+background.getHeight());




        square = BitmapFactory.decodeResource((getResources()),R.drawable.twister_100px);
        square_x = (int) getResources().getDimension(R.dimen.margin_square);
        square_y = (screenMetrics.heightPixels)/2 - square.getHeight()/2;


        int pause_size = (int)getResources().getDimension(R.dimen.size_pause);
        int pause_margin = (int) getResources().getDimension(R.dimen.margin_pause);

        Bitmap immutablePause = BitmapFactory.decodeResource(getResources(),R.drawable.pause);
        pause_button = immutablePause.copy(Bitmap.Config.ARGB_8888,true);
        pause_button = Bitmap.createScaledBitmap(pause_button, pause_size, pause_size, false);
        pause_x = screenMetrics.widthPixels - (pause_button.getWidth() + pause_margin);
        pause_y = pause_margin;

        Bitmap immutablePlay = BitmapFactory.decodeResource(getResources(),R.drawable.play);
        play_button = immutablePlay.copy(Bitmap.Config.ARGB_8888,true);
        play_button = Bitmap.createScaledBitmap(play_button, pause_size, pause_size, false);

        score_x = (int) getResources().getDimension(R.dimen.margin_score_x);
        score_y = (int) getResources().getDimension(R.dimen.margin_score_y);

        preparePainters();


        //Prepare game loop variables
        frames_per_second = 40; //35;
        frame_time_seconds = 1/frames_per_second;
        frame_time_ms = frame_time_seconds * 1000;
        frame_time_ns = frame_time_ms * 1_000_000;


        //Prepare stripe
//        stripes.add(generateNewRandomStripe());


    }


    private void preparePainters() {
        //Paints for stripes
        redPaint = new Paint();
        redPaint.setColor(Color.RED);
        redPaint.setStyle(Paint.Style.FILL);


        yellowPaint = new Paint();
        yellowPaint.setColor(Color.YELLOW);
        yellowPaint.setStyle(Paint.Style.FILL);


        greenPaint = new Paint();
        greenPaint.setColor(Color.GREEN);
        greenPaint.setStyle(Paint.Style.FILL);


        bluePaint = new Paint();
        bluePaint.setColor(Color.BLUE);
        bluePaint.setStyle(Paint.Style.FILL);

        //Paint for stripe borders
        blackPaint = new Paint();
        blackPaint.setColor(Color.BLACK);
        blackPaint.setStyle(Paint.Style.STROKE);

        //Paint for score
        whitePaint = new Paint();
        whitePaint.setColor(Color.WHITE);
        whitePaint.setTextSize(getResources().getDimension(R.dimen.text_size));
        whitePaint.setTextAlign(Paint.Align.CENTER);
        whitePaint.setStyle(Paint.Style.FILL);


        //Collect paints
        paints.put(0,redPaint);
        paints.put(1,yellowPaint);
        paints.put(2,greenPaint);
        paints.put(3,bluePaint);
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
        paused = true;
    }


    public void resume(){
        canDraw = true;

        if(thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }


    @Override
    public void run() {

        tLastFrame = System.nanoTime();
        delta_t = 0;


        //TODO fix problem with delta t
        while(canDraw){

            update();


            if (!surfaceHolder.getSurface().isValid()){
                continue;
            }
            draw();


            tEndOfRender = System.nanoTime();
            delta_t = frame_time_ns - (tEndOfRender - tLastFrame);

            try {
                if (delta_t > 0) {
                    thread.sleep((long) delta_t / 1_000_000);
                }
//                else{
//                    System.out.println("skipped sth.");
//                }
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
            tLastFrame = System.nanoTime();
        }
    }

    private void update() {

        //Do not change anything if paused!
        if (paused) {
            return;
        }


        if (!futureRotations.isEmpty()) {
            int remainingRotation = futureRotations.poll();
            int rotationStep = calculateDegreesPerFrame();
            if (remainingRotation > 0 && remainingRotation >= rotationStep) {
                rotationAngle = (rotationAngle + rotationStep) % 360;
                futureRotations.add(remainingRotation - rotationStep);
            } else if (remainingRotation < 0 && remainingRotation <= -rotationStep) {
                rotationAngle = (rotationAngle - rotationStep) % 360;
                futureRotations.add(remainingRotation + rotationStep);
            }
            else{
                rotationAngle = (rotationAngle + remainingRotation) % 360;
            }
        }


        matrix.setTranslate(-square_x - square.getWidth() / 2f, -square_y - square.getWidth() / 2f);
        matrix.setRotate(rotationAngle, square.getWidth() / 2f, square.getHeight() / 2f);
        matrix.postTranslate(square_x, square_y);


        //update stripe positions
        level = score / 10;
        List<Stripe> visibleStripes = new LinkedList<>();
        List<Stripe> stillCountableStripes = new LinkedList<>();
        for (Stripe s : stripes) {
            if(updateStripe(s, delta_t)){
                visibleStripes.add(s);
                if (countableStripes.contains(s)){
                    stillCountableStripes.add(s);
                }
            }
        }
        stripes = visibleStripes;
        countableStripes = stillCountableStripes;

        //update time
        timeTillNextStripe -= frame_time_ms;

        int firstContact = square_x + square.getWidth();
        for (Stripe s : stripes) {
            Rect currentStripe = s.getStripe();
            int square_color = getColorOfSquare();
            int stripe_color = s.getColorCode();

            int lastContact = firstContact - (currentStripe.right-currentStripe.left);

            if (currentStripe.left <= firstContact && currentStripe.left >= lastContact
                    && square_color != stripe_color){
                System.out.println(square_color+" "+stripe_color);
                paused = true;
                gameOver = true;
            }
            if (currentStripe.left <= lastContact && countableStripes.contains(s)
                    && square_color == stripe_color){
                score++;
                countableStripes.remove(s);
            }
        }
    }

    public void startNewGame() {
        rotationAngle = 0;
        futureRotations = new PriorityQueue<>();
        paused = false;
        gameOver = false;
        score = 0;
        stripes = new LinkedList<>();
        countableStripes = new LinkedList<>();
        timeTillNextStripe = 500;
    }


    /**
     * Updates the position of the given stripe
     * @param stripe the stripe that shall be moved
     * @param delta_t the time that is needed to fill the remains of the time period set
     * @return whether the given stripe is still visible and was therefore moved
     */
    private boolean updateStripe(Stripe stripe, double delta_t) {
//        System.out.println("deltat: "+delta_t);
//        System.out.println("moved: "+speed * delta_t/1_000_000_000);
//
        Rect movedStripe = stripe.getStripe();

        //Stripe no longer visible
        if(movedStripe.right < 0){
            return false;
        }
//TODO ruckeln in den Griff kriegen + Geschwindigkeit und Zeit anpassen
        double movement = speed + level*3; //* delta_t/1_000_000_000;
        movedStripe.left -= movement;
        movedStripe.right -= movement;
        stripe.setStripe(movedStripe);
        return true;
    }

    private void draw() {
        canvas = surfaceHolder.lockCanvas();

        //background
        canvas.drawBitmap(background,0,0,null);

        //stripes
        drawStripes();

        //square
        canvas.drawBitmap(square, matrix, null);

        //pause/play
        if(paused){
            canvas.drawBitmap(play_button, pause_x, pause_y,null);
        }
        else{
            canvas.drawBitmap(pause_button, pause_x, pause_y,null);
        }

        //score
        canvas.drawText(String.valueOf(score),
                score_x,
                score_y + getResources().getDimension(R.dimen.text_size), whitePaint);

        surfaceHolder.unlockCanvasAndPost(canvas);
    }

    private void drawStripes() {
        if (timeTillNextStripe <= 0){
//            System.out.println(timeTillNextStripe);
            stripes.add(generateNewRandomStripe());
            timeTillNextStripe = pauseTime - level * 300;
        }
        for (Stripe s : stripes) {
            canvas.drawRect(s.getStripe(), s.getPaint());
            //Todo give stripes a black edge?!
            //canvas.drawRect(s.getStripe(), blackPaint);
        }
    }


    /**
     * Draw a new random colored stripe with black borders on the right edge of the display.
     * @return the painted stripe
     */
    public Stripe generateNewRandomStripe() {

        int colorCode = random.nextInt(4);
        int stripeWidth = 200 + random.nextInt(200);
        Paint painter = paints.get(colorCode);
        Rect stripe = new Rect();
        stripe.set(screenMetrics.widthPixels, 0, screenMetrics.widthPixels + stripeWidth, screenMetrics.heightPixels);
//        canvas.drawRect(stripe, painter);
//        canvas.drawRect(stripe,blackPaint);
        Stripe newStripe = new Stripe(stripe, colorCode, painter);
        countableStripes.add(newStripe);
        return newStripe;
    }


    /*-------------------------------------------------------------------------------*/


    //Auxiliary methods:

    private int calculateDegreesPerFrame() {
        int ROTATION_TIME = 200;
        int DEGREES_PER_ROTATION = 90;
        return (int) (frame_time_ms * DEGREES_PER_ROTATION)/ ROTATION_TIME;
    }

    public static Bitmap loadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap( v.getLayoutParams().width, v.getLayoutParams().height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return b;
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

    private int pxToDp(int px){
        return px * 160 / screenMetrics.densityDpi;
    }


    /*-------------------------------------------------------------------------------*/


    //Getter:

    public Queue<Integer> getFutureRotations() {
        return futureRotations;
    }

    public boolean isPaused() {
        return paused;
    }


    /*-------------------------------------------------------------------------------*/


    //Setter:

    public void setFutureRotations(Queue<Integer> futureRotations) {
        this.futureRotations = futureRotations;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }
}
