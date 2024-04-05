package com.example.sync;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

/**
 * Utility class for generating QR code bitmaps from content.
 */
public class QRCodeGenerator {

    /**
     * Generates a QR code bitmap for the given content with the specified width and height.
     *
     * @param content The content to encode in the QR code.
     * @param width   The width of the QR code bitmap.
     * @param height  The height of the QR code bitmap.
     * @return The generated QR code bitmap, or null if an error occurs during generation.
     */
    public static Bitmap generateQRCodeBitmap(String content, int width, int height) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
}

