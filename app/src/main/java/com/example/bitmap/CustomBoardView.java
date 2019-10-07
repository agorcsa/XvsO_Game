package com.example.bitmap;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

// renders the Board on the screen
// draws the Board using Canvas
// contains the logic of the game
public class CustomBoardView extends View {

    Paint paint;

    InterfaceGame interfaceGame;

    private float X_PARTITION_RATIO = 1 / 3f;
    private float Y_PARTITION_RATIO = 1 / 3f;

    // to save the value for the drawable reference
    int x;
    int zero;

    // to save the value for the color
    int mXColor;
    int mZeroColor;

    // ---------------------------------

    private Board board;

    private MainActivity activity;

    // width and height of the board
    // eltW = cell weight, eltH = cell height
    private int width, height, eltW, eltH;

    // ----------------------------------

    public CustomBoardView(Context context) {
        super(context);
        init(null);
    }

    public CustomBoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CustomBoardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CustomBoardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attributeSet) {
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setAntiAlias(true);

        if (attributeSet == null) {
            return;
        }

        TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.CustomImageView);

        mXColor = typedArray.getResourceId(R.styleable.CustomBoardView_color_red, getResources().getColor(R.color.color_red));
        mZeroColor = typedArray.getResourceId(R.styleable.CustomBoardView_color_blue, getResources().getColor(R.color.color_blue));

        x = typedArray.getResourceId(R.styleable.CustomImageView_x, R.drawable.ic_cross);
        zero = typedArray.getResourceId(R.styleable.CustomImageView_zero, R.drawable.ic_zero);

        typedArray.recycle();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawVerticalLines(canvas);
        drawHorizontalLines(canvas);
    }

    // draws the vertical lines of the grid
    public void drawVerticalLines(Canvas canvas) {
        canvas.drawLine(getWidth() * X_PARTITION_RATIO, 0f, getWidth() * X_PARTITION_RATIO, getHeight(), paint);
        canvas.drawLine(getWidth() * (2 * X_PARTITION_RATIO), 0f, getWidth() * (2 * X_PARTITION_RATIO), getHeight(), paint);
    }

    // draws the horizontal lines of the grid
    public void drawHorizontalLines(Canvas canvas) {
        canvas.drawLine(0f, getHeight() * Y_PARTITION_RATIO, getWidth(), getHeight() * Y_PARTITION_RATIO, paint);
        canvas.drawLine(0f, getHeight() * (2 * Y_PARTITION_RATIO), getWidth(), getHeight() * (2 * Y_PARTITION_RATIO), paint);
    }

    // ---------------------------

    public void setup(InterfaceGame interfaceGame) {
        this.interfaceGame = interfaceGame;
    }

    public void setMainActivity(MainActivity a) {
        activity = a;
    }

    public void setBoard(Board b) {
        board = b;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        height = View.MeasureSpec.getSize(heightMeasureSpec);
        width = View.MeasureSpec.getSize(widthMeasureSpec);
        eltW = width  / 3;
        eltH = height / 3;

        setMeasuredDimension(width, height);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!board.isEnded()  &&  event.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int) (event.getX() / eltW);
            int y = (int) (event.getY() / eltH);
            char win = board.play(x, y);
            invalidate();

            if (win != ' ') {
                activity.gameEnded(win);
            } else {
                // computer plays ...


                if (win != ' ') {
                    activity.gameEnded(win);
                }
            }
        }
        return super.onTouchEvent(event);
    }
}