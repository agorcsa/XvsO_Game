package com.example.bitmap;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.annotation.RequiresApi;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import static com.example.bitmap.BitmapUtils.getBitmap;

public class MyCanvas extends View {

    Paint paint;
    Bitmap bitmap;

    private float X_PARTITION_RATIO = 1 / 3f;
    private float Y_PARTITION_RATIO = 1 / 3f;

    public MyCanvas(Context context) {
        super(context);
        paint = new Paint();
    }


    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setAntiAlias(true);

        getBitmapFromVectorDrawable(getContext(), R.drawable.ic_zero);
        canvas.drawBitmap(bitmap, getWidth()/2, getHeight()/2, paint);
        drawVerticalLines(canvas);
        drawHorizontalLines(canvas);
    }


    public void  drawVerticalLines(Canvas canvas) {
        canvas.drawLine(getWidth() * X_PARTITION_RATIO, 0f, getWidth() * X_PARTITION_RATIO, getHeight(), paint);
        canvas.drawLine(getWidth() * (2 * X_PARTITION_RATIO), 0f, getWidth() * (2 * X_PARTITION_RATIO), getHeight(), paint);
    }

    public void drawHorizontalLines(Canvas canvas) {
        canvas.drawLine(0f, getHeight() * Y_PARTITION_RATIO, getWidth(), getHeight() * Y_PARTITION_RATIO, paint);
        canvas.drawLine(0f, getHeight() * (2 * Y_PARTITION_RATIO), getWidth(), getHeight() * (2 * Y_PARTITION_RATIO), paint);
    }
}