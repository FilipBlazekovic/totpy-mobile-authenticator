package com.filipblazekovic.totpy.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.room.Room;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.filipblazekovic.totpy.R;
import com.filipblazekovic.totpy.activity.AddEditTokenActivity;
import com.filipblazekovic.totpy.activity.ExportTokensActivity;
import com.filipblazekovic.totpy.activity.TokensActivity;
import com.filipblazekovic.totpy.crypto.CryptoHandler;
import com.filipblazekovic.totpy.fragment.dialog.SecurityDetailsDialog;
import com.filipblazekovic.totpy.storage.TokenDao;
import com.filipblazekovic.totpy.storage.TokenDatabase;
import com.filipblazekovic.totpy.fragment.dialog.PasswordInputDialog;
import com.filipblazekovic.totpy.fragment.dialog.PublicKeyDialog;
import com.filipblazekovic.totpy.model.inout.ExportLocked;
import com.filipblazekovic.totpy.model.inout.ExportLockingMethod;
import com.filipblazekovic.totpy.model.inout.ExportLockingPublicKey;
import com.filipblazekovic.totpy.model.internal.Token;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.val;

public final class DataHandler {

  private static final ExecutorService executor = Executors.newSingleThreadExecutor();
  private static final Handler handler = new Handler(Looper.getMainLooper());
  private static TokenDatabase DB = null;

  private DataHandler() {
  }

  public static void loadConfig(Context context) {
    executor.execute(() -> {
      val config = ConfigStore.get(context);
      handler.post(() -> ((TokensActivity) context).setTokenCategoryVisible(config.isTokenCategoryVisible()));
    });
  }

  public static void loadAndShowSecurityInfo(Context context) {
    executor.execute(() -> {
      val deviceSecurityInfo = CryptoHandler.getDeviceSecurityInfo(context);
      handler.post(() -> SecurityDetailsDialog
          .newInstance(deviceSecurityInfo)
          .show(((TokensActivity) context).getSupportFragmentManager(), Common.DIALOG_LABEL));
    });
  }

  private static TokenDao getDao(Context context) {
    if (DB == null) {
      DB = Room.databaseBuilder(context, TokenDatabase.class, "totpy").build();
    }
    return DB.tokenDao();
  }

  public static void loadToken(Context context, Integer id) {
    executor.execute(() -> {
      val token = CryptoHandler.unlockToken(
          getDao(context).getById(id)
      );
      handler.post(() -> {
        val activity = (AddEditTokenActivity) context;
        activity.loadToken(token);
      });
    });
  }

  public static void loadTokens(Context context, List<Integer> ids) {
    executor.execute(() -> {
      val tokens = CryptoHandler.unlockTokens(
          getDao(context).getByIds(ids)
      );
      handler.post(() -> {
        val activity = (ExportTokensActivity) context;
        activity.loadTokens(tokens);
      });
    });
  }

  public static void loadTokens(Context context) {
    executor.execute(() -> {
      val tokens = CryptoHandler.unlockTokens(getDao(context).getAll());
      handler.post(() -> {
        val tokensActivity = (TokensActivity) context;
        tokensActivity.loadTokens(tokens);
      });
    });
  }

  public static void deleteTokens(Context context) {
    executor.execute(() -> getDao(context).deleteAll());
  }

  public static void deleteTokens(Context context, List<Integer> ids) {
    executor.execute(() -> getDao(context).deleteByIds(ids));
  }

  public static void insertToken(Context context, Token token) {
    executor.execute(() -> getDao(context).insert(
            CryptoHandler.lockToken(token)
        )
    );
  }

  public static void insertTokens(Context context, List<Token> tokens) {
    executor.execute(() -> getDao(context).insertAll(
            CryptoHandler.lockTokens(tokens)
        )
    );
  }

  public static void updateToken(Context context, Token token) {
    executor.execute(() -> getDao(context).update(
            CryptoHandler.lockToken(token)
        )
    );
  }

  public static void shareLockedExport(Context context, List<Token> tokens, Uri exportLockingPublicKeyFile) {
    executor.execute(() -> {

      try (val input = context.getContentResolver().openInputStream(exportLockingPublicKeyFile)) {

        val output = new ByteArrayOutputStream();
        val buffer = new byte[1024];
        int len;
        while ((len = input.read(buffer)) != -1) {
          output.write(buffer, 0, len);
        }

        val exportLockingPublicKey = new ObjectMapper().readValue(
            output.toByteArray(),
            ExportLockingPublicKey.class
        );

        val exportLocked = CryptoHandler.generateLockedExport(tokens, exportLockingPublicKey);
        val exportLockedBytes = new ObjectMapper()
            .writeValueAsString(exportLocked)
            .getBytes(StandardCharsets.UTF_8);

        handler.post(() -> {
          shareFile(context, exportLockedBytes, "export_locked.json");
          ConfigStore.updateLastExportDateTime(context);
        });

      } catch (Exception e) {
        Log.e(Common.TAG, "Exception thrown while generating locked export", e);
        handler.post(() -> Toast
            .makeText(
                context,
                e.getMessage() == null
                    ? context.getResources().getString(R.string.error_message_internal_error)
                    : e.getMessage(),
                Toast.LENGTH_LONG
            )
            .show());
      }
    });
  }

  public static void shareLockedExport(Context context, List<Token> tokens, String exportLockingPublicKeyString) {
    executor.execute(() -> {
      try {

        val exportLockingPublicKey = new ObjectMapper().readValue(
            exportLockingPublicKeyString.getBytes(StandardCharsets.UTF_8),
            ExportLockingPublicKey.class
        );

        val exportLocked = CryptoHandler.generateLockedExport(tokens, exportLockingPublicKey);
        val exportLockedBytes = new ObjectMapper()
            .writeValueAsString(exportLocked)
            .getBytes(StandardCharsets.UTF_8);

        handler.post(() -> {
          shareFile(context, exportLockedBytes, "export_locked.json");
          ConfigStore.updateLastExportDateTime(context);
        });

      } catch (Exception e) {
        Log.e(Common.TAG, "Exception thrown while generating locked export", e);
        handler.post(() -> Toast
            .makeText(
                context,
                e.getMessage() == null
                    ? context.getResources().getString(R.string.error_message_internal_error)
                    : e.getMessage(),
                Toast.LENGTH_LONG
            )
            .show());
      }
    });
  }

  public static void shareLockedExport(Context context, List<Token> tokens, char[] password, char[] passwordConfirmation) {
    executor.execute(() -> {
      try {

        val exportLocked = CryptoHandler.generateLockedExport(context, tokens, password, passwordConfirmation);
        val exportLockedBytes = new ObjectMapper()
            .writeValueAsString(exportLocked)
            .getBytes(StandardCharsets.UTF_8);

        handler.post(() -> {
          shareFile(context, exportLockedBytes, "export_locked.json");
          ConfigStore.updateLastExportDateTime(context);
        });

      } catch (Exception e) {
        Log.e(Common.TAG, "Exception thrown while generating locked export", e);
        handler.post(() -> Toast
            .makeText(
                context,
                e.getMessage() == null
                    ? context.getResources().getString(R.string.error_message_internal_error)
                    : e.getMessage(),
                Toast.LENGTH_LONG
            )
            .show());
      }
    });
  }

  public static void shareFile(Context context, byte[] bytes, String filename) {
    executor.execute(() -> {

      try (val output = new FileOutputStream(new File(context.getCacheDir(), filename))) {
        output.write(bytes);
      } catch (Exception e) {
        Log.e(Common.TAG, "Exception thrown while sharing file", e);
        handler.post(() -> Toast
            .makeText(
                context,
                e.getMessage() == null
                    ? context.getResources().getString(R.string.error_message_internal_error)
                    : e.getMessage(),
                Toast.LENGTH_LONG
            )
            .show());
        return;
      }

      handler.post(() -> {

        val intent = new Intent(Intent.ACTION_SEND);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        val fileUri = FileProvider.getUriForFile(
            context,
            context.getApplicationContext().getPackageName(),
            new File(context.getCacheDir(), filename)
        );

        intent.setDataAndType(fileUri, Common.JSON_FILE_TYPE);
        intent.putExtra(Intent.EXTRA_STREAM, fileUri);

        context.startActivity(
            Intent.createChooser(intent, "Select Application")
        );
      });

    });
  }

  public static void loadAndShowExportLockingPublicKey(Context context) {
    executor.execute(() -> {
      val publicKey = CryptoHandler.getExportLockingPublicKey();
      handler.post(() -> PublicKeyDialog
          .newInstance(publicKey)
          .show(((TokensActivity) context).getSupportFragmentManager(), Common.DIALOG_LABEL));
    });
  }

  public static void loadLockedExport(Context context, Uri uri) {
    executor.execute(() -> {

      try (val input = context.getContentResolver().openInputStream(uri)) {

        val output = new ByteArrayOutputStream();
        val buffer = new byte[1024];
        int len;
        while ((len = input.read(buffer)) != -1) {
          output.write(buffer, 0, len);
        }

        val exportLocked = new ObjectMapper().readValue(
            output.toByteArray(),
            ExportLocked.class
        );

        // PROCESS KEY PROTECTED EXPORT
        if (exportLocked.getExportLockingMethod() == ExportLockingMethod.PUBLIC_KEY) {
          val export = CryptoHandler.unlockExport(exportLocked);
          handler.post(() -> {
            DataHandler.insertTokens(context, Token.from(export.getTokens()));
            DataHandler.loadTokens(context);
          });
          return;
        }

        // PROCESS PASSWORD PROTECTED EXPORT
        handler.post(() -> {
          final DialogFragment fragment = PasswordInputDialog.newInstance(exportLocked);
          fragment.show(((TokensActivity) context).getSupportFragmentManager(), Common.DIALOG_LABEL);
        });

      } catch (Exception e) {
        Log.e(Common.TAG, "Exception thrown while loading locked export", e);
        handler.post(() -> Toast
            .makeText(
                context,
                e.getMessage() == null
                    ? context.getResources().getString(R.string.error_message_internal_error)
                    : e.getMessage(),
                Toast.LENGTH_LONG
            )
            .show());
      }
    });
  }

  public static void loadLockedExport(Context context, ExportLocked exportLocked, char[] password) {
    executor.execute(() -> {
      try {

        val export = CryptoHandler.unlockExport(exportLocked, password);

        handler.post(() -> {
          DataHandler.insertTokens(context, Token.from(export.getTokens()));
          DataHandler.loadTokens(context);
        });

      } catch (Exception e) {
        Log.e(Common.TAG, "Exception thrown while loading locked export", e);
        handler.post(() -> Toast
            .makeText(
                context,
                context.getResources().getString(R.string.error_message_invalid_password),
                Toast.LENGTH_LONG
            )
            .show());
      }
    });
  }

}
