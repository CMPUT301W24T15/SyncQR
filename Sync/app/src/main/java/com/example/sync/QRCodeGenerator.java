package com.example.sync;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QRCodeGenerator {

    /**
     * Generates a QR Code bitmap from the given text.
     *
     * @param text The text to encode in the QR Code.
     * @param width The desired width of the QR Code image.
     * @param height The desired height of the QR Code image.
     * @return A Bitmap containing the QR Code, or null if generation fails.
     */
    public static Bitmap generateQRCodeBitmap(String text, int width, int height) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, width, height);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            return barcodeEncoder.createBitmap(bitMatrix);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Generates a QR Code bitmap for check-in information and saves it as an image file.
     *
     * @param checkInInfo The check-in information to encode in the QR Code.
     * @param width       The desired width of the QR Code image.
     * @param height      The desired height of the QR Code image.
     * @return The URI of the saved image file, or null if saving fails.
     */
    public static Uri generateAndSaveCheckInQRCode(String checkInInfo, int width, int height, Context context) {
        // For check-in, you could structure the checkInInfo in a specific format
        // Example: "CHECKIN:eventId:userID"
        Bitmap qrCodeBitmap = generateQRCodeBitmap(checkInInfo, width, height);
        if (qrCodeBitmap != null) {
            return QRCodeFileSaver.saveBitmapAsImage(context, qrCodeBitmap, "SyncQR\\Sync\\qrcode");
        } else {
            return null;
        }
    }

    /**
     * Generates a QR Code bitmap that redirects to an event page and saves it as an image file.
     *
     * @param eventID The ID of the event page to encode in the QR Code.
     * @param width   The desired width of the QR Code image.
     * @param height  The desired height of the QR Code image.
     * @return The URI of the saved image file, or null if saving fails.
     */
    public static Uri generateAndSaveEventPageQRCode(String eventID, int width, int height, Context context) {
        // The eventPageURL should be a fully qualified URL
        // Example: "EVENTID:eventId"
        // This QR code, when scanned, will redirect users to the event page
        Bitmap qrCodeBitmap = generateQRCodeBitmap(eventID, width, height);
        if (qrCodeBitmap != null) {
            return QRCodeFileSaver.saveBitmapAsImage(context, qrCodeBitmap, "SyncQR\\Sync\\qrcode");
        } else {
            return null;
        }
    }

}


