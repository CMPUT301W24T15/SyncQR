package com.example.sync;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.Glide;

import java.util.Random;

/**
 * Custom ImageView class for displaying user avatars.
 *
 * <p>
 * The AvatarView class provides functionality to display user avatars with options for initials,
 * background color, and border. It also supports loading images from a URI using Glide library
 * for displaying user profile images. If no image is loaded, it displays initials generated from
 * the user's name. It also provides methods to reset to a default state or remove the currently
 * displayed initials and image.
 * </p>
 */
public class AvatarView extends AppCompatImageView {
    private int borderColor;
    private int borderWidth;
    private int backgroundColor;
    private String initials = "";
    private boolean isImageLoaded = false;

    /**
     * Constructor for creating an AvatarView instance.
     *
     * @param context The context in which the AvatarView is created.
     */
    public AvatarView(Context context) {
        super(context);
        init();
    }

    /**
     * Constructor for creating an AvatarView instance with attribute set.
     *
     * @param context The context in which the AvatarView is created.
     * @param attrs   The attribute set.
     */
    public AvatarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * Constructor for creating an AvatarView instance with attribute set and default style.
     *
     * @param context      The context in which the AvatarView is created.
     * @param attrs        The attribute set.
     * @param defStyleAttr The default style attribute.
     */
    public AvatarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * Loads a saved image if available.
     */
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

    /**
     * Checks if an image is loaded.
     *
     * @return True if an image is loaded, false otherwise.
     */
    public boolean isImageLoaded() {
        return this.isImageLoaded;
    }

    /**
     * Initializes the AvatarView with default values and loads saved image if available.
     */
    private void init() {
        borderColor = getResources().getColor(android.R.color.black);
        borderWidth = 4; // Default border width
        backgroundColor = generateRandomColor();
        loadImageIfAvailable();
    }

    /**
     * Draws the border around the AvatarView.
     *
     * @param canvas The canvas on which to draw the border.
     */
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

    /**
     * Draws the initials on the AvatarView.
     *
     * @param canvas The canvas on which to draw the initials.
     */
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

    /**
     * Draws the initials on the AvatarView.
     *
     * @param canvas The canvas on which to draw the initials.
     */
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

    /**
     * Sets the initials based on the user's name.
     *
     * @param name The user's name.
     */
    public void setInitialsFromName(String name) {
        // Check if an image has already been loaded; if so, do not overwrite with initials
        if (!isImageLoaded) {
            this.initials = getInitials(name);
            invalidate(); // Trigger a redraw
        }
    }

    /**
     * Generates initials from the provided name.
     *
     * @param name The name from which to generate the initials.
     * @return The generated initials.
     */
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

    /**
     * Generates a random color.
     *
     * @return The generated random color.
     */
    private int generateRandomColor() {
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

    /**
     * Removes the initials and image from the AvatarView.
     */
    public void removeInitialsAndImage() {
        this.initials = ""; // Clear initials
        isImageLoaded = false; // Reset the flag as no image is now loaded
        setImageDrawable(null); // Remove any set image
        invalidate(); // Trigger a redraw to ensure correct state is shown
    }

    /**
     * Saves the image URI to shared preferences.
     *
     * @param uri The URI of the image to save.
     */
    private void saveImageUri(Uri uri) {
        getContext().getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE)
                .edit()
                .putString("profileImageUri", uri.toString())
                .apply();
    }

    /**
     * Sets the image URI and loads the image using Glide library.
     *
     * @param uri The URI of the image to set.
     */
    public void setImageUri(Uri uri) {
        this.initials = ""; // Clear initials
        isImageLoaded = true; // Mark that an image has been loaded
        saveImageUri(uri); // Save the image URI

        Glide.with(getContext())
                .load(uri)
                .fitCenter() // Ensure the image fits within the ImageView bounds
                .into(this);
    }

    /**
     * Resets the AvatarView to its default state.
     */
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