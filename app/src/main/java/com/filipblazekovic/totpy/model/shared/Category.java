package com.filipblazekovic.totpy.model.shared;

import android.content.Context;
import androidx.core.content.ContextCompat;
import com.filipblazekovic.totpy.R;
import com.google.common.base.CaseFormat;

public enum Category {

  DEFAULT,
  EDUCATION,
  EMAIL,
  FINANCE,
  PERSONAL,
  SOCIAL_NETWORK,
  STEM,
  TRAVEL,
  VOIP,
  VPN,
  WORK;


  public String getDisplayName() {
    return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, this.name());
  }

  public int getColor(Context context) {
    switch (this) {
      case EDUCATION:
        return ContextCompat.getColor(context, R.color.category_education);
      case EMAIL:
        return ContextCompat.getColor(context, R.color.category_email);
      case FINANCE:
        return ContextCompat.getColor(context, R.color.category_finance);
      case PERSONAL:
        return ContextCompat.getColor(context, R.color.category_personal);
      case SOCIAL_NETWORK:
        return ContextCompat.getColor(context, R.color.category_social_network);
      case STEM:
        return ContextCompat.getColor(context, R.color.category_stem);
      case TRAVEL:
        return ContextCompat.getColor(context, R.color.category_travel);
      case VOIP:
        return ContextCompat.getColor(context, R.color.category_voip);
      case VPN:
        return ContextCompat.getColor(context, R.color.category_vpn);
      case WORK:
        return ContextCompat.getColor(context, R.color.category_work);
      default:
        return ContextCompat.getColor(context, R.color.category_default);
    }
  }

  public static Category from(String category) {
    try {
      return Category.valueOf(category);
    } catch (Exception e) {
      return Category.DEFAULT;
    }
  }

}
