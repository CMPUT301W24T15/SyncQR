package com.example.sync;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class AvatarView extends AppCompatImageView {
    private int borderColor;
    private int borderWidth;

    public AvatarView(Context context) {
        super(context);
        init();
    }

    public AvatarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AvatarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // Initialize any default properties or values
        borderColor = getResources().getColor(android.R.color.black);
        borderWidth = 4; // Default border width
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBorder(canvas);
    }

    private void drawBorder(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(borderColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(borderWidth);

        canvas.drawRect(
                borderWidth / 2f,
                borderWidth / 2f,
                getWidth() - borderWidth / 2f,
                getHeight() - borderWidth / 2f,
                paint
        );
    }
}

