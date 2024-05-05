package com.filipblazekovic.totpy.crypto;

import com.filipblazekovic.totpy.model.shared.HashAlgorithm;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.SneakyThrows;
import lombok.val;

public final class TOTP {

  //                                         0  1   2    3     4      5       6        7         8
  private static final int[] DIGITS_POWER = {1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000};

  private TOTP() {
  }

  static boolean shouldRecalculate(int period) {
    return period == getRemainingSeconds(period);
  }

  static int getRemainingSeconds(int period) {
    return (int) (period - ((System.currentTimeMillis() / 1000) % period));
  }

  @SneakyThrows
  static String calculateOtp(HashAlgorithm algorithm, int period, int digits, byte[] secret) {
    long counter = (System.currentTimeMillis() / 1000) / period;
    return generateTOTP(
        calculateHmac(algorithm, secret, longToBytes(counter)),
        digits
    );
  }

  private static byte[] longToBytes(long x) {
    val buffer = ByteBuffer.allocate(Long.BYTES);
    buffer.order(ByteOrder.BIG_ENDIAN);
    buffer.putLong(x);
    return buffer.array();
  }

  private static String generateTOTP(byte[] hmac, int otpLength) {
    String result;

    // put selected bytes into result int
    int offset = hmac[hmac.length - 1] & 0xf;

    int binary = ((hmac[offset] & 0x7f) << 24) |
        ((hmac[offset + 1] & 0xff) << 16) |
        ((hmac[offset + 2] & 0xff) << 8) |
        (hmac[offset + 3] & 0xff);

    int otp = binary % DIGITS_POWER[otpLength];

    result = Integer.toString(otp);
    while (result.length() < otpLength) {
      result = "0" + result;
    }

    Arrays.fill(hmac, (byte) 0);
    return result;
  }

  @SneakyThrows
  private static byte[] calculateHmac(HashAlgorithm digestAlgorithm, byte[] keyBytes, byte[] data) {
    val key = new SecretKeySpec(keyBytes, digestAlgorithm.getHMACAlgorithm());
    val mac = Mac.getInstance(key.getAlgorithm());
    mac.init(key);
    return mac.doFinal(data);
  }

}