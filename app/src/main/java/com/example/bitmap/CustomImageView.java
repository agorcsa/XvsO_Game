package com.example.bitmap;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatImageView;

public class CustomImageView extends AppCompatImageView {

    Paint paint;
    ImageView imageView;

    int mXColor;
    int mZeroColor;

    int x;
    int zero;

    public CustomImageView(Context context) {
        super(context);
        init(null);
    }

    public CustomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CustomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attributeSet) {
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setAntiAlias(true);

        imageView = findViewById(R.id.custom_imageview);

        if (attributeSet == null) {
            return;
        }

        TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.CustomImageView);

        x = typedArray.getResourceId(R.styleable.CustomImageView_x, R.drawable.ic_cross);
        zero = typedArray.getResourceId(R.styleable.CustomImageView_zero, R.drawable.ic_zero);

        mXColor = typedArray.getResourceId(R.styleable.BoardView_color_red, getResources().getColor(R.color.color_red));
        mZeroColor = typedArray.getResourceId(R.styleable.BoardView_color_blue, getResources().getColor(R.color.color_blue));

        imageView.setImageResource(x);
        imageView.setColorFilter(mXColor);

        typedArray.recycle();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Drawable drawable = getResources().getDrawable(R.drawable.ic_cross, null);
        drawable.setBounds(0, 0, 0, 0);
        drawable.draw(canvas);
    }
}
