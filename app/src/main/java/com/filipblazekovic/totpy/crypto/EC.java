package com.filipblazekovic.totpy.crypto;

import static com.filipblazekovic.totpy.crypto.CryptoHandler.SECURE_HARDWARE_KEYSTORE_PROVIDER;
import static com.filipblazekovic.totpy.crypto.Keystore.ECIES_KEYS_ALIAS;
import static com.filipblazekovic.totpy.crypto.Keystore.MASTER_KEYS_ALIAS;

import android.os.Build.VERSION_CODES;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;
import androidx.annotation.RequiresApi;
import com.filipblazekovic.totpy.model.internal.KeyPairGenerationResponse;
import com.filipblazekovic.totpy.model.internal.KeyPairGenerationStatus;
import com.filipblazekovic.totpy.utils.Common;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.spec.ECGenParameterSpec;
import lombok.SneakyThrows;
import lombok.val;

public final class EC {

  private static final String KEY_ALGORITHM = "EC";
  private static final String CURVE_NAME = "secp256r1";

  private EC() {
  }

  @SneakyThrows
  static KeyPair generateEphemoralKeyPair() {
    val generator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
    generator.initialize(new ECGenParameterSpec(CURVE_NAME));
    return generator.generateKeyPair();
  }

  @RequiresApi(api = VERSION_CODES.S)
  static KeyPairGenerationResponse generateKeyPairs() {
    val response = generateKeyPair(ECIES_KEYS_ALIAS);
    if (response.getStatus() == KeyPairGenerationStatus.FAILURE) {
      return response;
    }
    return generateKeyPair(MASTER_KEYS_ALIAS);
  }

  @RequiresApi(api = VERSION_CODES.S)
  private static KeyPairGenerationResponse generateKeyPair(String alias) {
    try {

      Keystore.deleteKeyStoreEntry(alias);

      val keyPairGenerator = KeyPairGenerator.getInstance(
          KeyProperties.KEY_ALGORITHM_EC,
          SECURE_HARDWARE_KEYSTORE_PROVIDER
      );

      val builder = generateBuilder(alias);
      Keystore.setNoAuthorizationParams(builder);
      return Keystore.generateKeyPairForParams(keyPairGenerator, builder);

    } catch (Exception e) {
      Log.e(Common.TAG, "Exception thrown while generating EC key pair", e);
      Keystore.deleteKeyStoreEntry(alias);
      return KeyPairGenerationResponse.error();
    }
  }

  @RequiresApi(api = VERSION_CODES.S)
  private static KeyGenParameterSpec.Builder generateBuilder(String alias) {
    return new KeyGenParameterSpec
        .Builder(alias, KeyProperties.PURPOSE_AGREE_KEY)
        .setAlgorithmParameterSpec(new ECGenParameterSpec(CURVE_NAME))
        .setDigests(KeyProperties.DIGEST_SHA256);
  }
}
