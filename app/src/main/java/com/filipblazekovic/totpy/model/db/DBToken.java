package com.filipblazekovic.totpy.model.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.filipblazekovic.totpy.model.internal.Token;
import com.filipblazekovic.totpy.model.shared.Category;
import com.filipblazekovic.totpy.model.shared.HashAlgorithm;
import com.filipblazekovic.totpy.model.shared.IssuerIcon;
import com.filipblazekovic.totpy.model.shared.Type;

// Room can't find lombok getters & setters for some reason even after dependency reordering
// https://stackoverflow.com/questions/44203814/lombok-not-working-with-android-room-gives-error-cannot-find-getter-for-fie
// It's likely a bug in Room itself, given that it can't find setters after writing them manually, so fields need to be public

@Entity(tableName = "tokens")
public class DBToken {

  @PrimaryKey(autoGenerate = true)
  public int id;

  @ColumnInfo(name = "issuer_name")
  public String issuerName;

  @ColumnInfo(name = "issuer_icon")
  public IssuerIcon issuerIcon;

  @ColumnInfo(name = "account")
  public String account;

  @ColumnInfo(name = "category")
  public Category category;

  @ColumnInfo(name = "type")
  public Type type;

  @ColumnInfo(name = "algorithm")
  public HashAlgorithm algorithm;

  @ColumnInfo(name = "digits")
  public int digits;

  @ColumnInfo(name = "period")
  public int period;

  @ColumnInfo(name = "secret_locked")
  public String secretLocked;

  public int getId() {
    return id;
  }

  public String getIssuerName() {
    return issuerName;
  }

  public IssuerIcon getIssuerIcon() {
    return issuerIcon;
  }

  public String getAccount() {
    return account;
  }

  public Category getCategory() {
    return category;
  }

  public Type getType() {
    return type;
  }

  public HashAlgorithm getAlgorithm() {
    return algorithm;
  }

  public int getDigits() {
    return digits;
  }

  public int getPeriod() {
    return period;
  }

  public String getSecretLocked() {
    return secretLocked;
  }

  public DBToken setId(int id) {
    this.id = id;
    return this;
  }

  public DBToken setIssuerName(String issuerName) {
    this.issuerName = issuerName;
    return this;
  }

  public DBToken setIssuerIcon(IssuerIcon issuerIcon) {
    this.issuerIcon = issuerIcon;
    return this;
  }

  public DBToken setAccount(String account) {
    this.account = account;
    return this;
  }

  public DBToken setCategory(Category category) {
    this.category = category;
    return this;
  }

  public DBToken setType(Type type) {
    this.type = type;
    return this;
  }

  public DBToken setAlgorithm(HashAlgorithm algorithm) {
    this.algorithm = algorithm;
    return this;
  }

  public DBToken setDigits(int digits) {
    this.digits = digits;
    return this;
  }

  public DBToken setPeriod(int period) {
    this.period = period;
    return this;
  }

  public DBToken setSecretLocked(String secretLocked) {
    this.secretLocked = secretLocked;
    return this;
  }

  public DBToken() {
  }

  public static DBToken from(Token token, String secretLocked) {
    final DBToken dbToken = new DBToken()
        .setIssuerName(token.getIssuer().getName())
        .setIssuerIcon(token.getIssuer().getIcon())
        .setAccount(token.getAccount())
        .setCategory(token.getCategory())
        .setType(token.getType())
        .setAlgorithm(token.getAlgorithm())
        .setDigits(token.getDigits())
        .setPeriod(token.getPeriod())
        .setSecretLocked(secretLocked);

    if (token.getId() != null) {
      dbToken.setId(token.getId());
    }

    return dbToken;
  }

}
