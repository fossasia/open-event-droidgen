package org.fossasia.openevent.config.strategies;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import org.fossasia.openevent.common.ConstantStrings;
import org.fossasia.openevent.common.api.Urls;
import org.fossasia.openevent.common.network.NetworkConnectivityChangeReceiver;
import org.fossasia.openevent.common.utils.SharedPreferencesUtil;
import org.fossasia.openevent.common.utils.Utils;
import org.fossasia.openevent.config.ConfigStrategy;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Parses application configuration and populates necessary data application wide
 */
public class AppConfigStrategy implements ConfigStrategy {

    private static final String API_LINK = "api-link";
    private static final String EMAIL = "email";
    private static final String APP_NAME = "app-name";
    private static final String AUTH_OPTION = "is-auth-enabled";

    @Override
    public boolean configure(Context context) {
        String config_json = null;
        String event_json = null;
        //getting config.json data
        try {
            InputStream inputStream = context.getAssets().open("config.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            config_json = new String(buffer, "UTF-8");
        } catch(IOException e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonObject = new JSONObject(config_json);
            String email = jsonObject.has(EMAIL) ? jsonObject.getString(EMAIL) : "";
            String appName = jsonObject.has(APP_NAME) ? jsonObject.getString(APP_NAME) : "";
            String apiLink = jsonObject.has(API_LINK) ? jsonObject.getString(API_LINK) : "";
            boolean isAuthEnabled = jsonObject.has(AUTH_OPTION) && jsonObject.getBoolean(AUTH_OPTION);

            Urls.setBaseUrl(apiLink);

            SharedPreferencesUtil.putString(ConstantStrings.EMAIL, email);
            SharedPreferencesUtil.putString(ConstantStrings.APP_NAME, appName);
            SharedPreferencesUtil.putString(ConstantStrings.BASE_API_URL, apiLink);
            SharedPreferencesUtil.putBoolean(ConstantStrings.IS_AUTH_ENABLED, isAuthEnabled);

            if (extractEventIdFromApiLink(apiLink) != 0)
                SharedPreferencesUtil.putInt(ConstantStrings.EVENT_ID, extractEventIdFromApiLink(apiLink));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //getting event data
        try {
            InputStream inputStream = context.getAssets().open("event");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            event_json = new String(buffer, "UTF-8");
        } catch(IOException e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonObject = new JSONObject(event_json);
            String org_description = jsonObject.has(ConstantStrings.ORG_DESCRIPTION) ?
                    jsonObject.getString(ConstantStrings.ORG_DESCRIPTION) : "";
            String eventTimeZone = jsonObject.has(ConstantStrings.TIMEZONE) ? jsonObject.getString(ConstantStrings.TIMEZONE) : "";
            SharedPreferencesUtil.putString(ConstantStrings.ORG_DESCRIPTION, org_description);
            SharedPreferencesUtil.putString(ConstantStrings.TIMEZONE, eventTimeZone);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!Utils.isBaseUrlEmpty()) {
            context.registerReceiver(new NetworkConnectivityChangeReceiver(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }

        return false;
    }

    private int extractEventIdFromApiLink(String apiLink){
        if(Utils.isEmpty(apiLink))
            return 0;

        return Integer.parseInt(apiLink.split("/v1/events/")[1].replace("/",""));
    }

}
