package com.example.sync;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.graphics.Typeface;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import com.bumptech.glide.Glide;
import java.util.Random;

public class AvatarView extends AppCompatImageView {
    private int borderColor;
    private int borderWidth;
    private int backgroundColor;
    private String initials = "";
    private boolean isImageLoaded = false;
    
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
    private void loadImageIfAvailable() {
        String uriString = getContext().getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE)
                .getString("profileImageUri", null);
        if (uriString != null) {
            Uri savedUri = Uri.parse(uriString);
            Glide.with(getContext())
                    .load(savedUri)
                    .fitCenter()
                    .into(this);
            isImageLoaded = true;
        }
    }

    public boolean isImageLoaded() {
        return this.isImageLoaded;
    }
    private void init() {
        borderColor = getResources().getColor(android.R.color.black);
        borderWidth = 4; // Default border width
        backgroundColor = generateRandomColor();
        loadImageIfAvailable();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isImageLoaded) {
            // Only draw the background and initials if no image has been loaded
            Paint backgroundPaint = new Paint();
            backgroundPaint.setColor(backgroundColor);
            canvas.drawRect(0, 0, getWidth(), getHeight(), backgroundPaint);

            if (!initials.isEmpty()) {
                // Draw initials if they are set
                drawInitials(canvas);
            }
        }

        if (!isImageLoaded || initials.isEmpty()) {
            // Draw the border unless we've loaded an image and initials are not set
            drawBorder(canvas);
        }

        if (isImageLoaded) {
            // If an image is loaded, skip the above and let super.onDraw handle it
            super.onDraw(canvas);
        }
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
            textPaint.setColor(Color.WHITE); // Set text color to white for contrast
            textPaint.setTextSize(120); // Increased text size for bigger and bolder letters
            textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD)); // Ensure the font is bold
            textPaint.setTextAlign(Paint.Align.CENTER);

            float centerX = getWidth() / 2f;
            float centerY = (getHeight() / 2f) - ((textPaint.descent() + textPaint.ascent()) / 2f);
            canvas.drawText(initials, centerX, centerY, textPaint);
        }
    }
    public void setInitialsFromName(String name) {
        // Check if an image has already been loaded; if so, do not overwrite with initials
        if (!isImageLoaded) {
            this.initials = getInitials(name);
            invalidate(); // Trigger a redraw
        }
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
    private int generateRandomColor() {
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }
    public void removeInitialsAndImage() {
        this.initials = ""; // Clear initials
        isImageLoaded = false; // Reset the flag as no image is now loaded
        setImageDrawable(null); // Remove any set image
        invalidate(); // Trigger a redraw to ensure correct state is shown
    }
    private void saveImageUri(Uri uri) {
        getContext().getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE)
                .edit()
                .putString("profileImageUri", uri.toString())
                .apply();
    }
    public void setImageUri(Uri uri) {
        this.initials = ""; // Clear initials
        isImageLoaded = true; // Mark that an image has been loaded
        saveImageUri(uri); // Save the image URI

        Glide.with(getContext())
                .load(uri)
                .fitCenter() // Ensure the image fits within the ImageView bounds
                .into(this);
    }

    public void resetToDefault() {
        this.initials = ""; // Clear initials
        isImageLoaded = false; // Reset the flag as no image is now loaded
        getContext().getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE).edit().remove("profileImageUri").apply(); // Clear saved URI
        setImageDrawable(null); // Remove any set image
        // Optionally, set a default image if you have one
        // Glide.with(getContext()).load(R.drawable.default_image).into(this);
        invalidate(); // Trigger a redraw to ensure correct state is shown
    }

}