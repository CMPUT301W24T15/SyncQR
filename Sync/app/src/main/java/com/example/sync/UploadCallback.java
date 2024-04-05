package com.example.sync;

/**
 * Callback interface for upload operations.
 */
public interface UploadCallback {
    /**
     * Called when the upload operation is successful.
     *
     * @param downloadUrl The download URL of the uploaded file.
     */
    void onSuccess(String downloadUrl);

    /**
     * Called when the upload operation fails.
     *
     * @param e The exception representing the failure.
     */
    void onFailure(Exception e);
}
