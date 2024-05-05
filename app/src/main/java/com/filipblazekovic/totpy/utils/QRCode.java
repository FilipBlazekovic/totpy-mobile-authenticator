package com.filipblazekovic.totpy.utils;

import android.graphics.Bitmap;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public final class QRCode {

  private static final int QR_CODE_WIDTH = 250;
  private static final int QR_CODE_HEIGHT = 250;

  private QRCode() {
  }

  public static Bitmap generate(String data) throws WriterException {
    return new BarcodeEncoder().encodeBitmap(
        data,
        BarcodeFormat.QR_CODE,
        QR_CODE_WIDTH,
        QR_CODE_HEIGHT
    );
  }

}
