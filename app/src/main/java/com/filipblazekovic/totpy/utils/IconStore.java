package com.filipblazekovic.totpy.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.core.content.res.ResourcesCompat;
import com.filipblazekovic.totpy.R;
import com.filipblazekovic.totpy.model.shared.IssuerIcon;
import java.util.HashMap;
import java.util.Map;
import lombok.val;

public final class IconStore {

  private static final Map<IssuerIcon, Integer> icons = new HashMap<>();

  static {
    icons.put(IssuerIcon.ABOUTME, R.drawable.issuer_icon_aboutme);
    icons.put(IssuerIcon.ACADEMIA, R.drawable.issuer_icon_academia);
    icons.put(IssuerIcon.AIRBNB, R.drawable.issuer_icon_airbnb);
    icons.put(IssuerIcon.ALCHEMY, R.drawable.issuer_icon_alchemy);
    icons.put(IssuerIcon.AMAZONAWS, R.drawable.issuer_icon_amazonaws);
    icons.put(IssuerIcon.AMAZON, R.drawable.issuer_icon_amazon);
    icons.put(IssuerIcon.APPLE, R.drawable.issuer_icon_apple);
    icons.put(IssuerIcon.ATLASSIAN, R.drawable.issuer_icon_atlassian);
    icons.put(IssuerIcon.BABEL, R.drawable.issuer_icon_babel);
    icons.put(IssuerIcon.BADOO, R.drawable.issuer_icon_badoo);
    icons.put(IssuerIcon.BITBUCKET, R.drawable.issuer_icon_bitbucket);
    icons.put(IssuerIcon.BITCOIN, R.drawable.issuer_icon_bitcoin);
    icons.put(IssuerIcon.CLOUDFLARE, R.drawable.issuer_icon_cloudflare);
    icons.put(IssuerIcon.CODECADEMY, R.drawable.issuer_icon_codecademy);
    icons.put(IssuerIcon.COINBASE, R.drawable.issuer_icon_coinbase);
    icons.put(IssuerIcon.CONFLUENCE, R.drawable.issuer_icon_confluence);
    icons.put(IssuerIcon.COUCHBASE, R.drawable.issuer_icon_couchbase);
    icons.put(IssuerIcon.CPANEL, R.drawable.issuer_icon_cpanel);
    icons.put(IssuerIcon.CRUNCHBASE, R.drawable.issuer_icon_crunchbase);
    icons.put(IssuerIcon.DEVIANTART, R.drawable.issuer_icon_deviantart);
    icons.put(IssuerIcon.DOCKER, R.drawable.issuer_icon_docker);
    icons.put(IssuerIcon.DPD, R.drawable.issuer_icon_dpd);
    icons.put(IssuerIcon.DUCKDUCKGO, R.drawable.issuer_icon_duckduckgo);
    icons.put(IssuerIcon.EBAY, R.drawable.issuer_icon_ebay);
    icons.put(IssuerIcon.EDX, R.drawable.issuer_icon_edx);
    icons.put(IssuerIcon.EPICGAMES, R.drawable.issuer_icon_epicgames);
    icons.put(IssuerIcon.ETHERIUM, R.drawable.issuer_icon_ethereum);
    icons.put(IssuerIcon.ETSY, R.drawable.issuer_icon_etsy);
    icons.put(IssuerIcon.EVERNOTE, R.drawable.issuer_icon_evernote);
    icons.put(IssuerIcon.FACEBOOK, R.drawable.issuer_icon_facebook);
    icons.put(IssuerIcon.FREELANCER, R.drawable.issuer_icon_freelancer);
    icons.put(IssuerIcon.GITHUB, R.drawable.issuer_icon_github);
    icons.put(IssuerIcon.GITLAB, R.drawable.issuer_icon_gitlab);
    icons.put(IssuerIcon.GLASSDOOR, R.drawable.issuer_icon_glassdoor);
    icons.put(IssuerIcon.GLOVO, R.drawable.issuer_icon_glovo);
    icons.put(IssuerIcon.GMAIL, R.drawable.issuer_icon_gmail);
    icons.put(IssuerIcon.GOFUNDME, R.drawable.issuer_icon_gofundme);
    icons.put(IssuerIcon.GOLDMANSACHS, R.drawable.issuer_icon_goldmansachs);
    icons.put(IssuerIcon.GOOGLE, R.drawable.issuer_icon_gofundme);
    icons.put(IssuerIcon.GRAFANA, R.drawable.issuer_icon_grafana);
    icons.put(IssuerIcon.GRAMMARLY, R.drawable.issuer_icon_grammarly);
    icons.put(IssuerIcon.GRAYLOG, R.drawable.issuer_icon_grafana);
    icons.put(IssuerIcon.INDEED, R.drawable.issuer_icon_indeed);
    icons.put(IssuerIcon.INSTAGRAM, R.drawable.issuer_icon_instagram);
    icons.put(IssuerIcon.INTELLIJ, R.drawable.issuer_icon_intellij);
    icons.put(IssuerIcon.ITUNES, R.drawable.issuer_icon_itunes);
    icons.put(IssuerIcon.JENKINS, R.drawable.issuer_icon_jenkins);
    icons.put(IssuerIcon.JIRA, R.drawable.issuer_icon_jira);
    icons.put(IssuerIcon.KICKSTARTER, R.drawable.issuer_icon_kickstarter);
    icons.put(IssuerIcon.KUBERNETES, R.drawable.issuer_icon_kubernetes);
    icons.put(IssuerIcon.LINKEDIN, R.drawable.issuer_icon_linkedin);
    icons.put(IssuerIcon.LINUX, R.drawable.issuer_icon_linux);
    icons.put(IssuerIcon.MEDIUM, R.drawable.issuer_icon_medium);
    icons.put(IssuerIcon.META, R.drawable.issuer_icon_meta);
    icons.put(IssuerIcon.MICROSOFT, R.drawable.issuer_icon_microsoft);
    icons.put(IssuerIcon.MONEYGRAM, R.drawable.issuer_icon_moneygram);
    icons.put(IssuerIcon.MOZILLA, R.drawable.issuer_icon_mozilla);
    icons.put(IssuerIcon.MYSPACE, R.drawable.issuer_icon_myspace);
    icons.put(IssuerIcon.NETFLIX, R.drawable.issuer_icon_netflix);
    icons.put(IssuerIcon.NGROK, R.drawable.issuer_icon_ngrok);
    icons.put(IssuerIcon.NODEJS, R.drawable.issuer_icon_nodejs);
    icons.put(IssuerIcon.OKCUPID, R.drawable.issuer_icon_okcupid);
    icons.put(IssuerIcon.ONLYFANS, R.drawable.issuer_icon_onlyfans);
    icons.put(IssuerIcon.OPENAI, R.drawable.issuer_icon_openai);
    icons.put(IssuerIcon.OPENVPN, R.drawable.issuer_icon_openvpn);
    icons.put(IssuerIcon.PAYONEER, R.drawable.issuer_icon_payoneer);
    icons.put(IssuerIcon.PAYPAL, R.drawable.issuer_icon_paypal);
    icons.put(IssuerIcon.PINTEREST, R.drawable.issuer_icon_pinterest);
    icons.put(IssuerIcon.POSTMAN, R.drawable.issuer_icon_postman);
    icons.put(IssuerIcon.PROMETHEUS, R.drawable.issuer_icon_prometheus);
    icons.put(IssuerIcon.PROTONMAIL, R.drawable.issuer_icon_protonmail);
    icons.put(IssuerIcon.PROTON, R.drawable.issuer_icon_proton);
    icons.put(IssuerIcon.RASPBERRYPI, R.drawable.issuer_icon_raspberrypi);
    icons.put(IssuerIcon.REDDIT, R.drawable.issuer_icon_reddit);
    icons.put(IssuerIcon.RESEARCHGATE, R.drawable.issuer_icon_researchgate);
    icons.put(IssuerIcon.REVOLUT, R.drawable.issuer_icon_reddit);
    icons.put(IssuerIcon.RIOTGAMES, R.drawable.issuer_icon_riotgames);
    icons.put(IssuerIcon.ROBINHOOD, R.drawable.issuer_icon_robinhood);
    icons.put(IssuerIcon.SAMSUNG, R.drawable.issuer_icon_samsung);
    icons.put(IssuerIcon.SHOPIFY, R.drawable.issuer_icon_shopify);
    icons.put(IssuerIcon.SIGNAL, R.drawable.issuer_icon_signal);
    icons.put(IssuerIcon.SKILLSHARE, R.drawable.issuer_icon_skillshare);
    icons.put(IssuerIcon.SKYPE, R.drawable.issuer_icon_skype);
    icons.put(IssuerIcon.SLACK, R.drawable.issuer_icon_slack);
    icons.put(IssuerIcon.SNAPCHAT, R.drawable.issuer_icon_snapchat);
    icons.put(IssuerIcon.SOURCEFORGE, R.drawable.issuer_icon_sourceforge);
    icons.put(IssuerIcon.SPOTIFY, R.drawable.issuer_icon_spotify);
    icons.put(IssuerIcon.SQUARESPACE, R.drawable.issuer_icon_squarespace);
    icons.put(IssuerIcon.STACKOVERFLOW, R.drawable.issuer_icon_stackoverflow);
    icons.put(IssuerIcon.STRIPE, R.drawable.issuer_icon_stripe);
    icons.put(IssuerIcon.TELEGRAM, R.drawable.issuer_icon_telegram);
    icons.put(IssuerIcon.TESLA, R.drawable.issuer_icon_tesla);
    icons.put(IssuerIcon.TINDER, R.drawable.issuer_icon_tinder);
    icons.put(IssuerIcon.TOPTAL, R.drawable.issuer_icon_toptal);
    icons.put(IssuerIcon.TORPROJECT, R.drawable.issuer_icon_torproject);
    icons.put(IssuerIcon.TRIPADVISOR, R.drawable.issuer_icon_tripadvisor);
    icons.put(IssuerIcon.TRUSTPILOT, R.drawable.issuer_icon_trustpilot);
    icons.put(IssuerIcon.TWITCH, R.drawable.issuer_icon_twitch);
    icons.put(IssuerIcon.TWITTER, R.drawable.issuer_icon_twitter);
    icons.put(IssuerIcon.UBER, R.drawable.issuer_icon_uber);
    icons.put(IssuerIcon.UPWORK, R.drawable.issuer_icon_upwork);
    icons.put(IssuerIcon.VK, R.drawable.issuer_icon_vk);
    icons.put(IssuerIcon.WELLSFARGO, R.drawable.issuer_icon_wellsfargo);
    icons.put(IssuerIcon.WHATSAPP, R.drawable.issuer_icon_whatsapp);
    icons.put(IssuerIcon.WIKIPEDIA, R.drawable.issuer_icon_wikipedia);
    icons.put(IssuerIcon.WIRE, R.drawable.issuer_icon_wire);
    icons.put(IssuerIcon.WIREGUARD, R.drawable.issuer_icon_wireguard);
    icons.put(IssuerIcon.WISE, R.drawable.issuer_icon_wise);
    icons.put(IssuerIcon.WOLFRAM, R.drawable.issuer_icon_wolfram);
    icons.put(IssuerIcon.X, R.drawable.issuer_icon_x);
    icons.put(IssuerIcon.XERO, R.drawable.issuer_icon_xero);
    icons.put(IssuerIcon.XIAOMI, R.drawable.issuer_icon_xiaomi);
    icons.put(IssuerIcon.ZILLOW, R.drawable.issuer_icon_zillow);
    icons.put(IssuerIcon.ZOOM, R.drawable.issuer_icon_zoom);
    icons.put(IssuerIcon.UNKNOWN, R.drawable.issuer_icon_unknown);
  }

  private IconStore() {
  }

  public static Drawable get(Context context, IssuerIcon icon) {
    val resourceId = icons.get(icon);
    return ResourcesCompat.getDrawable(
        context.getResources(),
        (resourceId == null) ? R.drawable.issuer_icon_unknown : resourceId,
        context.getTheme()
    );
  }

}
