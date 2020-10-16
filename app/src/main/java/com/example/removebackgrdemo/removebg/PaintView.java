package com.example.removebackgrdemo.removebg;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import androidx.annotation.Nullable;

import static android.graphics.PorterDuff.Mode.DST_OUT;

public class PaintView extends View {

    public static int BRUSH_SIZE = 26;
    public static final int DEFAULT_COLOR = Color.RED;
    public static final int DEFAULT_BG_COLOR = Color.WHITE;
    private static final float TOUCH_TOLERANCE = 4;
    private float mX, mY;
    private Path mPath;
    private Paint mPaint;
    private ArrayList<FingerPath> paths = new ArrayList<>();
    private ArrayList<FingerPath> undoPaths = new ArrayList<>();
    private ArrayList<Bitmap> bitmaps = new ArrayList<>();
    private ArrayList<Bitmap> undobitmap = new ArrayList<>();
    private int currentColor;
    private int backgroundColor = DEFAULT_BG_COLOR;
    private int strokeWidth;
    private boolean emboss;
    private boolean blur;
    private MaskFilter mEmboss;
    private MaskFilter mBlur;
    private Bitmap mBitmap;
    private Bitmap mBitmap1,oldbm;
    private Canvas mCanvas;
    private Paint mBitmapPaint = new Paint(Paint.DITHER_FLAG);
    private DisplayMetrics displayMetrics;
    private int isFlood;

    private Paint mPaint1;
    private Paint mPaint2;
    private float radius;
    private float mX1,mY1;
    private float oldW,oldH;
    private float newW,newH;
    private boolean ismove;


    public PaintView(Context context) {
        super(context);
    }

    public PaintView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PaintView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public PaintView(Context context, AttributeSet attrs) {

        super(context, attrs);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(DEFAULT_COLOR);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setXfermode(null);
        mPaint.setAlpha(0xff);

        mEmboss = new EmbossMaskFilter(new float[]{1, 1, 1}, 0.4f, 6, 3.5f);
        mBlur = new BlurMaskFilter(5, BlurMaskFilter.Blur.NORMAL);

    }

    public void init(DisplayMetrics metrics) {

        displayMetrics = metrics;
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        mCanvas = new Canvas(mBitmap);

        currentColor = DEFAULT_COLOR;
        strokeWidth = BRUSH_SIZE;
        isFlood = 0;
        this.mPaint1=new Paint();
        this.mPaint1.setColor(Color.RED);
        this.mPaint1.setStrokeWidth(10);
        this.mPaint1.setStyle(Paint.Style.STROKE);
        this.mPaint2=new Paint();
        this.mPaint2.setColor(Color.RED);
        this.mPaint2.setStrokeWidth(20);

    }


    public void setbm(Bitmap bm, float newW, float newH) {
        this.oldW=bm.getWidth();
        this.oldH=bm.getHeight();
        Log.d("BM",bm.toString());

        ///resize

        mBitmap1 = bm;
        oldbm=bm.copy(Bitmap.Config.ARGB_8888, true);

        if (newH > 0 && newH > 0) {
            int width = bm.getWidth();
            int height = bm.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) newW / (float) newH;

            int finalWidth = (int)newW;
            int finalHeight = (int)newH;
            if (ratioMax > ratioBitmap) {
                finalWidth = (int) ((float)newH * ratioBitmap);
            } else {
                finalHeight = (int) ((float)newW / ratioBitmap);
            }
            this.newW=(float)finalWidth;
            this.newH=(float)finalHeight;
            mBitmap1 = Bitmap.createScaledBitmap(mBitmap1, finalWidth, finalHeight, true);
        }

//        float bmwidth = bm.getWidth();
//        float bmheigh = bm.getHeight();
//        float newwidth = 0;
//        float newhight = 0;
//        //  mBitmap1= BitmapFactory.decodeResource(getResources(),R.drawable.im);
//        if(bmwidth<displayMetrics.widthPixels&&bmheigh<displayMetrics.heightPixels){
//            float tl=bmheigh/bmwidth;
//            bmwidth=displayMetrics.widthPixels;
//            bmheigh=bmwidth*tl;
//        }
//
//
//        if (bmwidth > displayMetrics.widthPixels) {
//            newwidth = (float) displayMetrics.widthPixels;
//            newhight = bmheigh / bmwidth * newwidth;
//        }
//        if (bmheigh > displayMetrics.heightPixels) {
//            newhight = (float) displayMetrics.heightPixels;
//            newwidth = bmwidth / bmheigh * newhight;
//        }
//        if (newhight != 0 && newwidth != 0)
          //  mBitmap1 = Bitmap.createScaledBitmap(bm,(int)newW  ,(int)newH , true);


        //ghep
//        mBitmap = Bitmap.createBitmap(displayMetrics.widthPixels, displayMetrics.heightPixels, Bitmap.Config.ARGB_8888);
//        mBitmap.eraseColor(Color.WHITE);
//        mBitmap1 = mergeToPin(mBitmap, mBitmap1);


        mBitmap = Bitmap.createBitmap(mBitmap1);
        mCanvas.setBitmap(mBitmap);
        invalidate();
    }

    public void normal() {
        emboss = false;
        blur = false;
    }



    public void clear() {
        // backgroundColor = DEFAULT_BG_COLOR;
        // mBitmap.eraseColor(Color.TRANSPARENT);


        mBitmap = Bitmap.createBitmap(displayMetrics.widthPixels, displayMetrics.heightPixels, Bitmap.Config.ARGB_8888);

        if (mBitmap1 != null) {
            mBitmap = Bitmap.createBitmap(mBitmap1);
        }

        mCanvas.setBitmap(mBitmap);
        paths.clear();
        undoPaths.clear();
        bitmaps.clear();
        undobitmap.clear();
        normal();
        invalidate();
    }

    public void undo() {
        if (bitmaps.size() > 0) {
            //  clearDraw();

            undobitmap.add(bitmaps.remove(bitmaps.size() - 1));
            if (bitmaps.size() > 0) {
                Bitmap temp = Bitmap.createBitmap(bitmaps.get(bitmaps.size() - 1));
                mBitmap = Bitmap.createBitmap(temp);
                paths.clear();
            } else
                if(mBitmap1!=null) {
                    mBitmap = Bitmap.createBitmap(mBitmap1);
                }
                else {
                    mBitmap = Bitmap.createBitmap(displayMetrics.widthPixels, displayMetrics.heightPixels, Bitmap.Config.ARGB_8888);
                }
            mCanvas.setBitmap(mBitmap);
            paths.clear();
            invalidate();
         //   Toast.makeText(getContext(), String.valueOf(bitmaps.size()), Toast.LENGTH_SHORT).show();
        }
    }

    public void redo() {
        if (undobitmap.size() > 0) {
            Bitmap temp = Bitmap.createBitmap(undobitmap.remove(undobitmap.size() - 1));
            bitmaps.add(temp);
            mBitmap = Bitmap.createBitmap(temp);
            mCanvas.setBitmap(mBitmap);
            invalidate();
        }
    }
//    private void clearDraw() {
//        mBitmap = Bitmap.createBitmap(displayMetrics.widthPixels, displayMetrics.heightPixels, Bitmap.Config.ARGB_8888);
//        if(mBitmap1!=null)
//        mBitmap=Bitmap.createBitmap(mBitmap1);
//        mCanvas.setBitmap(mBitmap);
//      //  mCanvas.drawBitmap(mBitmap1, 0, 0, mBitmapPaint);;
//        invalidate();
//    }

    @Override
    protected void onDraw(Canvas canvas) {
        // canvas.save();
        // ps.drawColor(backgroundColor);
        for (FingerPath fp : paths) {
            mPaint.setColor(fp.color);
            mPaint.setStrokeWidth(fp.strokeWidth);
            mPaint.setMaskFilter(null);
//            if (fp.emboss)
//                mPaint.setMaskFilter(mEmboss);
//            else if (fp.blur)
//                mPaint.setMaskFilter(mBlur);
            mPaint.setXfermode(new PorterDuffXfermode(DST_OUT));
            mCanvas.drawPath(fp.path, mPaint);

        }

        canvas.drawBitmap(mBitmap, 0, 0, null);
        canvas.drawCircle(mX,mY,strokeWidth/2,mPaint1); ////size path vua voi Vong tron
        Log.d("Ondraw", String.valueOf(mPaint1.getColor()));
        canvas.drawCircle(mX,mY+150,20,mPaint2);
        Log.d("Ondraw","OnDraw");



        // canvas.restore();
    }
    public Bitmap getBMRS(){
        Canvas ccc=new Canvas();
        ccc.setBitmap(oldbm);

        for (FingerPath fp : undoPaths) {
            mPaint.setColor(fp.color);
            mPaint.setStrokeWidth(fp.strokeWidth*(oldH/newH));
            mPaint.setMaskFilter(null);
//            if (fp.emboss)
//                mPaint.setMaskFilter(mEmboss);
//            else if (fp.blur) oldbm
//                mPaint.setMaskFilter(mBlur);
            Matrix scaleMatrix = new Matrix();
            RectF rectF = new RectF();
          //  fp.path.computeBounds(rectF, true);
            scaleMatrix.setScale(oldW/newW, oldH/newH);
            fp.path.transform(scaleMatrix);
            mPaint.setXfermode(new PorterDuffXfermode(DST_OUT));
            ccc.drawPath(fp.path, mPaint);

        }
        return oldbm;
    }

    private void touchStart(float x, float y) {
        mPath = new Path();
        ismove=false;
        FingerPath fp = new FingerPath(currentColor, emboss, blur, strokeWidth, mPath);
        paths.add(fp);
        undoPaths.add(fp);
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }


    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        ismove=true;
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touchUp() {
        mPath.lineTo(mX, mY);
        if(!ismove) {
            paths.remove(paths.size() - 1);
            undoPaths.remove(undoPaths.size()-1);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        Log.d("Location",x+"| "+y);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                    touchStart(x, y-150);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                    touchMove(x, y-150);
                    invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touchUp();
                invalidate();
               // Bitmap temp = Bitmap.createBitmap(mBitmap);
              //  bitmaps.add(temp);
              //  mCanvas.setBitmap(mBitmap);
               // Toast.makeText(getContext(), String.valueOf(paths.size()), Toast.LENGTH_SHORT).show();
                paths.clear();
                break;
        }

        return true;
    }

    public void setColorPaint(int color) {
        currentColor = color;
    }

    public void resize(int size) {
        strokeWidth = BRUSH_SIZE + size + 1;
        invalidate();
    }



    public static Bitmap mergeToPin(Bitmap back, Bitmap front) {
        Bitmap result = Bitmap.createBitmap(back.getWidth(), back.getHeight(), back.getConfig());
        Canvas mecanvas = new Canvas(result);
        int widthBack = back.getWidth();
        int widthFront = front.getWidth();
        float move = (widthBack - widthFront) / 2;
        int hBack = back.getHeight();
        int hFront = front.getHeight();
        float move1 = (hBack -hFront) / 2;
        front.setDensity(back.getDensity());
        mecanvas.drawBitmap(back, 0f, 0f, null);
        mecanvas.drawBitmap(front, move, move1, null);
        return result;
    }
}
