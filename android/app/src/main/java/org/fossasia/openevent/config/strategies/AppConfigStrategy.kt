package org.fossasia.openevent.config.strategies

import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import org.fossasia.openevent.common.ConstantStrings
import org.fossasia.openevent.common.api.Urls
import org.fossasia.openevent.common.network.NetworkConnectivityChangeReceiver
import org.fossasia.openevent.common.utils.SharedPreferencesUtil
import org.fossasia.openevent.common.utils.Utils
import org.fossasia.openevent.config.ConfigStrategy
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.Charset

/**
 * Parses application configuration and populates necessary data application wide
 */
class AppConfigStrategy : ConfigStrategy {

    override fun configure(context: Context): Boolean {
        var config_json: String? = null
        var event_json: String? = null
        //getting config.json data
        try {
            val inputStream = context.assets.open("config.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            config_json = String(buffer, Charset.forName("UTF-8"))
        } catch (e: IOException) {
            e.printStackTrace()
        }

        try {
            val jsonObject = JSONObject(config_json)
            val email = if (jsonObject.has(EMAIL)) jsonObject.getString(EMAIL) else ""
            val appName = if (jsonObject.has(APP_NAME)) jsonObject.getString(APP_NAME) else ""
            val apiLink = if (jsonObject.has(API_LINK)) jsonObject.getString(API_LINK) else ""
            val isAuthEnabled = jsonObject.has(AUTH_OPTION) && jsonObject.getBoolean(AUTH_OPTION)

            Urls.baseUrl = apiLink

            SharedPreferencesUtil.putString(ConstantStrings.EMAIL, email)
            SharedPreferencesUtil.putString(ConstantStrings.APP_NAME, appName)
            SharedPreferencesUtil.putString(ConstantStrings.BASE_API_URL, apiLink)
            SharedPreferencesUtil.putBoolean(ConstantStrings.IS_AUTH_ENABLED, isAuthEnabled)

            if (extractEventIdFromApiLink(apiLink) != 0)
                SharedPreferencesUtil.putInt(ConstantStrings.EVENT_ID, extractEventIdFromApiLink(apiLink))
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        //getting event data
        try {
            val inputStream = context.assets.open("event")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            event_json = String(buffer, Charset.forName("UTF-8"))
        } catch (e: IOException) {
            e.printStackTrace()
        }

        try {
            val jsonObject = JSONObject(event_json)
            val orgDescription = if (jsonObject.has(ConstantStrings.ORG_DESCRIPTION))
                jsonObject.getString(ConstantStrings.ORG_DESCRIPTION)
            else
                ""
            val eventTimeZone = if (jsonObject.has(ConstantStrings.TIMEZONE)) jsonObject.getString(ConstantStrings.TIMEZONE) else ""
            SharedPreferencesUtil.putString(ConstantStrings.ORG_DESCRIPTION, orgDescription)
            SharedPreferencesUtil.putString(ConstantStrings.TIMEZONE, eventTimeZone)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        if (!Utils.isBaseUrlEmpty) {
            context.registerReceiver(NetworkConnectivityChangeReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        }

        return false
    }

    private fun extractEventIdFromApiLink(apiLink: String): Int {
        return if (Utils.isEmpty(apiLink)) 0 else Integer.parseInt(apiLink.split("/v1/events/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1].replace("/", ""))

    }

    companion object {

        private const val API_LINK = "api-link"
        private const val EMAIL = "email"
        private const val APP_NAME = "app-name"
        private const val AUTH_OPTION = "is-auth-enabled"
    }

}
