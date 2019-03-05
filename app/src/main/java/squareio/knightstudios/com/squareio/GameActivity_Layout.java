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

    int square_x, square_y;
    int pause_x, pause_y;

    Paint redPaint, yellowPaint,greenPaint, bluePaint, blackPaint;
    SparseArray<Paint> paints = new SparseArray<>(4);

    Random random = new Random();

    // in px per second
    int speed = 500;

    // in milliseconds
    int pauseTime = 6000;

    int timeTillNextStripe = 0;

    //TODO multiple stripes at the same time
    List<Stripe> stripes = new LinkedList<>();


    Matrix matrix = new Matrix();
    float rotationAngle = 0;

    boolean paused = false;

    private Queue<Integer> futureRotations = new PriorityQueue<>();

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
        int pause_margin = (int) getResources().getDimension(R.dimen.margin_points);

        Bitmap immutablePause = BitmapFactory.decodeResource(getResources(),R.drawable.pause);
        pause_button = immutablePause.copy(Bitmap.Config.ARGB_8888,true);
        pause_button = Bitmap.createScaledBitmap(pause_button, pause_size, pause_size, false);
        pause_x = screenMetrics.widthPixels - (pause_button.getWidth() + pause_margin);
        pause_y = pause_margin;

        Bitmap immutablePlay = BitmapFactory.decodeResource(getResources(),R.drawable.play);
        play_button = immutablePlay.copy(Bitmap.Config.ARGB_8888,true);
        play_button = Bitmap.createScaledBitmap(play_button, pause_size, pause_size, false);

        preparePainters();


        //Prepare game loop variables
        frames_per_second = 35;
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
            int currentRotation = futureRotations.poll();
            if (currentRotation > 0) {
                rotationAngle += 5;
                futureRotations.add(currentRotation - 5);
            } else if (currentRotation < 0) {
                rotationAngle -= 5;
                futureRotations.add(currentRotation + 5);
            }
        }


        matrix.setTranslate(-square_x - square.getWidth() / 2f, -square_y - square.getWidth() / 2f);
        matrix.setRotate(rotationAngle, square.getWidth() / 2f, square.getHeight() / 2f);
        matrix.postTranslate(square_x, square_y);
//        if (rotationAngle <= 90f){
//            rotationAngle += 1f;
//        }
//        System.out.println(rotationAngle);



//        //update stripe positions
//        List<Stripe> visibleStripes = new LinkedList<>();
//        for (Stripe s : stripes) {
//            if(updateStripe(s, delta_t)){
//                visibleStripes.add(s);
//            }
//        }
//        stripes = visibleStripes;
//
//        //update time
//        timeTillNextStripe -= frame_time_ms;

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

        double movement = speed * delta_t/1_000_000_000;
        movedStripe.left -= movement;
        movedStripe.right -= movement;
        stripe.setStripe(movedStripe);
        return true;
    }

    private void draw() {
        canvas = surfaceHolder.lockCanvas();
        canvas.drawBitmap(background,0,0,null);

//        drawStripes();


//        //Build new stripe after current one left the screen
//        if(counter <= 0) {
//            a = generateNewRandomStripe(canvas);
//            int stripeWidth = a.getStripe().right - a.getStripe().left;
//            counter = (screenMetrics.widthPixels + stripeWidth)/speed;
//        }
//        //Move the current stripe left with the given speed
//        else {
//            moveStripe(a, canvas);
//            counter--;
//        }
        canvas.drawBitmap(square, matrix, null);

        if(paused){
            canvas.drawBitmap(play_button, pause_x, pause_y,null);
        }
        else{
            canvas.drawBitmap(pause_button, pause_x, pause_y,null);
        }

        surfaceHolder.unlockCanvasAndPost(canvas);
    }

    private void drawStripes() {
        if (timeTillNextStripe <= 0){
//            System.out.println(timeTillNextStripe);
            stripes.add(generateNewRandomStripe());
            timeTillNextStripe = pauseTime;
        }
        for (Stripe s : stripes) {
            canvas.drawRect(s.getStripe(), s.getPaint());
        }
    }


    /**
     * Draw a new random colored stripe with black borders on the right edge of the display.
     * @return the painted stripe
     */
    public Stripe generateNewRandomStripe(){

        int color = random.nextInt(4);
        int stripeWidth = 200 + random.nextInt(200);
        Paint painter = paints.get(color);
        Rect stripe = new Rect();
        stripe.set(screenMetrics.widthPixels,0,screenMetrics.widthPixels + stripeWidth,screenMetrics.heightPixels);
//        canvas.drawRect(stripe, painter);
//        canvas.drawRect(stripe,blackPaint);
        return new Stripe(stripe, painter);
    }

//    /**
//     * Move the given stripe and draw it and its borders at the new location. The globally defined speed tells how far it is moved
//     * to the left. Called in GAME LOOP.
//     * @param stripe the stripe that shall be moved left
//     * @param canvas the place where the stripe is painted
//     */
//    public void moveStripe(Stripe stripe, Canvas canvas){
//        Rect rect = stripe.getStripe();
//        Paint painter = stripe.getPaint();
//        rect.left -= speed;
//        rect.right -= speed;
//        canvas.drawRect(rect, painter);
//        canvas.drawRect(rect,blackPaint);
//    }


    //Auxiliary methods:
    private int dpToPx(int dp){
        return screenMetrics.densityDpi/160 * dp;
    }

    private int pxToDp(int px){
        return px * 160 / screenMetrics.densityDpi;
    }



    //Getter:

    public Queue<Integer> getFutureRotations() {
        return futureRotations;
    }

    public boolean isPaused() {
        return paused;
    }


    //Setter:

    public void setFutureRotations(Queue<Integer> futureRotations) {
        this.futureRotations = futureRotations;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }
}
