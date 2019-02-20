package squareio.knightstudios.com.squareio;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class GameActivity_Layout extends SurfaceView implements Runnable {

    Thread thread = null;
    boolean canDraw = false;

    Bitmap background;
    Bitmap square;
    Canvas canvas;
    SurfaceHolder surfaceHolder;
    double frames_per_second, frame_time_seconds, frame_time_ms, frame_time_ns;
    double tLastFrame, tEndOfRender, delta_t;



    DisplayMetrics screenMetrics = GameActivity.displayMetrics;

    int square_x,square_y;

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


    public GameActivity_Layout(Context context) {
        super(context);
        surfaceHolder = getHolder();
        background = BitmapFactory.decodeResource(getResources(),R.drawable.background_1000_px);

        //Place Square
        square = BitmapFactory.decodeResource((getResources()),R.drawable.twister_100px);
        square_x = (int) getResources().getDimension(R.dimen.margin_square);
        square_y = (screenMetrics.heightPixels)/2 - square.getHeight()/2;


        preparePainters();

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
        delta_t =0;


        //TODO fix problem with delta t  + nothing important
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
                else{
                    System.out.println("skipped sth.");
                }
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
            tLastFrame =System.nanoTime();
        }
    }

    private void update() {

        //update stripe positions
        List<Stripe> visibleStripes = new LinkedList<>();
        for (Stripe s : stripes) {
            if(updateStripe(s, delta_t)){
                visibleStripes.add(s);
            }
        }
        stripes = visibleStripes;

        //update time
        timeTillNextStripe -= frame_time_ms;

    }

    /**
     * Updates the position of the given stripe
     * @param stripe the stripe that shall be moved
     * @param delta_t
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

        drawStripes();
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
        canvas.drawBitmap(square,square_x,square_y,null);


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

    /**
     * Move the given stripe and draw it and its borders at the new location. The globally defined speed tells how far it is moved
     * to the left. Called in GAME LOOP.
     * @param stripe the stripe that shall be moved left
     * @param canvas the place where the stripe is painted
     */
    public void moveStripe(Stripe stripe, Canvas canvas){
        Rect rect = stripe.getStripe();
        Paint painter = stripe.getPaint();
        rect.left -= speed;
        rect.right -= speed;
        canvas.drawRect(rect, painter);
        canvas.drawRect(rect,blackPaint);
    }
}
