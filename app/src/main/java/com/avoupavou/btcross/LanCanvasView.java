package com.avoupavou.btcross;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Pantazis on 12/23/2015.
 */
public class LanCanvasView extends View{


    private Bitmap mBitmap;
    private Canvas mCanvas;
    Context context;
    private Paint mPaint;
    private Paint paintP1;
    private Paint paintP2;
    private LanBoard testB;

    public LanCanvasView(Context c, AttributeSet attrs) {
        super(c, attrs);
        context = c;
        //config paint
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(7f);
        mPaint.setTextSize(100);
        paintP1 = new Paint();
        paintP1.setAntiAlias(true);
        paintP1.setColor(Color.RED);
        paintP1.setStyle(Paint.Style.STROKE);
        paintP1.setStrokeJoin(Paint.Join.ROUND);
        paintP1.setStrokeWidth(7f);
        paintP1.setTextSize(100);
        paintP2 = new Paint();
        paintP2.setAntiAlias(true);
        paintP2.setColor(Color.BLACK);
        paintP2.setStyle(Paint.Style.STROKE);
        paintP2.setStrokeJoin(Paint.Join.ROUND);
        paintP2.setStrokeWidth(7f);
        paintP2.setTextSize(100);
    }

    // override onSizeChanged
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // your Canvas will draw onto the defined Bitmap
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        testB.setSize(w, h);
    }

    // override onDraw
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(testB.getGridPath(), mPaint);
        canvas.drawPath(testB.getMovePathP1(), paintP1);
        canvas.drawPath(testB.getMovePathP2(), paintP2);
        if(testB.isGameOver()){
            testB.gameOver();
            canvas.drawPath(testB.getStrikeThrough(),mPaint);
        }
        invalidate();
    }


    public void clearCanvas() {
        invalidate();
    }

    //override the onTouchEvent
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                testB.clickOnCanvas((int)x, (int)y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                invalidate();
                break;
        }
        return true;
    }

    public void setBoard(LanBoard a){
        testB=a;
        testB.setSize(this.getWidth(), this.getHeight());
    }
}
