package com.example.sync;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.graphics.Typeface;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class AvatarView extends AppCompatImageView {
    private int borderColor;
    private int borderWidth;
    private String initials = "";
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
        drawInitials(canvas);
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
    private void drawInitials(Canvas canvas) {
        if (!initials.isEmpty()) {
            Paint textPaint = new Paint();
            textPaint.setColor(borderColor); // Use the border color for text for simplicity
            textPaint.setTextSize(60); // Example text size, adjust as needed
            textPaint.setTypeface(Typeface.DEFAULT_BOLD);
            textPaint.setTextAlign(Paint.Align.CENTER);

            float centerX = getWidth() / 2f;
            float centerY = (getHeight() / 2f) - ((textPaint.descent() + textPaint.ascent()) / 2f);
            canvas.drawText(initials, centerX, centerY, textPaint);
        }
    }
    public void setInitialsFromName(String name) {
        this.initials = getInitials(name);
        invalidate(); // Trigger redraw
    }
    private String getInitials(String name) {
        if (name == null || name.isEmpty()) return "";

        String[] parts = name.split("\\s+");
        if (parts.length == 0) return "";

        // Build initials: first character of the first word and first character of the last word if there's more than one
        String initials = parts[0].substring(0, 1).toUpperCase();
        if (parts.length > 1) {
            initials += parts[parts.length - 1].substring(0, 1).toUpperCase();
        }
        return initials;
    }
}

