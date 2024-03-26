package com.example.sync;

import android.graphics.Bitmap;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class CodeGenerator {

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
     * Generates a QR Code bitmap for check-in information.
     *
     * @param checkInInfo The check-in information to encode in the QR Code.
     * @param width The desired width of the QR Code image.
     * @param height The desired height of the QR Code image.
     * @return A Bitmap containing the QR Code.
     */
    public static Bitmap generateCheckInQRCode(String checkInInfo, int width, int height) {
        // For check-in, you could structure the checkInInfo in a specific format
        // Example: "CHECKIN:EVENTID"
        return generateQRCodeBitmap(checkInInfo, width, height);
    }

    /**
     * Generates a QR Code bitmap that redirects to an event page.
     *
     * @param eventID The ID of the event page to encode in the QR Code.
     * @param width The desired width of the QR Code image.
     * @param height The desired height of the QR Code image.
     * @return A Bitmap containing the QR Code.
     */
    public static Bitmap generateEventPageQRCode(String eventID, int width, int height) {
        // The eventPageURL should be a fully qualified URL
        // Example: "EVENTID:"
        // This QR code, when scanned, will redirect users to the event page
        return generateQRCodeBitmap(eventID, width, height);
    }
}


