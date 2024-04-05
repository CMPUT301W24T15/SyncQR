package com.example.sync;

import android.graphics.Bitmap;

/**
 * Callback interface for download operations.
 */
public interface DownloadCallback {
    /**
     * Called when the download operation is successful.
     *
     * @param bitmap The downloaded bitmap image.
     */
    void onDownloaded(Bitmap bitmap);

    /**
     * Called when the download operation fails.
     *
     * @param e The exception representing the failure.
     */
    void onError(Exception e);
}
