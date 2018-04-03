package org.fossasia.openevent.common.utils

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.CalendarContract
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Patterns
import android.view.View

import org.fossasia.openevent.R
import org.fossasia.openevent.common.ConstantStrings
import org.fossasia.openevent.common.api.Urls
import org.fossasia.openevent.common.date.DateConverter
import org.fossasia.openevent.config.StrategyRegistry
import org.fossasia.openevent.data.Event

import java.util.regex.Matcher
import java.util.regex.Pattern

object Utils {

        @JvmStatic
        var twoPane = false

        @JvmStatic
        val isBaseUrlEmpty: Boolean
            get() = Urls.baseUrl == Urls.EMPTY_LINK

        @JvmStatic
        fun isEmpty(string: String?): Boolean {
            return string == null || string.trim { it <= ' ' }.isEmpty()
        }

        @JvmStatic
        fun displayNoResults(resultView: View, recyclerView: View, noView: View, count: Int) {
            if (count != 0) {
                resultView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            } else if (noView.visibility != View.VISIBLE) {
                resultView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }
        }

        @JvmStatic
        fun checkStringEmpty(string: String): String {
            var finalString = ""
            if (!isEmpty(string)) {
                finalString = string
            }
            return finalString
        }

        @JvmStatic
        fun pxToDp(px: Float): Float {
            return px / Resources.getSystem().displayMetrics.density
        }

        @JvmStatic
        fun dpToPx(dp: Float): Float {
            return dp * Resources.getSystem().displayMetrics.density
        }

        @JvmStatic
        fun isEmailValid(email: String): Boolean {
            val pattern = Patterns.EMAIL_ADDRESS
            val matcher = pattern.matcher(email)
            return matcher.matches()
        }

        //Check password with minimum requirement
        @JvmStatic
        fun isPasswordValid(password: String): Boolean {
            return password.length >= 6
        }

        @JvmStatic
        fun registerIfUrlValid(swipeRefreshLayout: SwipeRefreshLayout,
                               `object`: Any, onRefreshListener: SwipeRefreshLayout.OnRefreshListener) {
            if (isBaseUrlEmpty) {
                swipeRefreshLayout.isEnabled = false
            } else {
                StrategyRegistry.instance.eventBusStrategy?.eventBus?.register(`object`)
                swipeRefreshLayout.setOnRefreshListener(onRefreshListener)
            }
        }

        @JvmStatic
        fun unregisterIfUrlValid(`object`: Any) {
            if (!isBaseUrlEmpty) {
                StrategyRegistry.instance.eventBusStrategy?.eventBus?.unregister(`object`)
            }
        }

        @JvmStatic
        fun getNameLetters(name: String): String {
            if (isEmpty(name))
                return "#"

            val strings = name.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val nameLetters = StringBuilder()
            for (s in strings) {
                if (nameLetters.length >= 2)
                    return nameLetters.toString().toUpperCase()
                if (!isEmpty(s)) {
                    nameLetters.append(s.trim { it <= ' ' }[0])
                }
            }
            return nameLetters.toString().toUpperCase()
        }

        @JvmStatic
        fun parseImageUri(uri: String?): String? {
            if (isEmpty(uri))
                return null

            if (uri!!.startsWith("http") || uri.startsWith("https"))
                return uri

            return if (uri.startsWith("/")) "file:///android_asset$uri" else null

        }

        @JvmStatic
        fun setUpCustomTab(context: Context, url: String) {

            var URL = url
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                URL = "http://$url"
            }

            val customTabsBuilder = CustomTabsIntent.Builder()
            customTabsBuilder.setToolbarColor(ContextCompat.getColor(context, R.color.color_primary))
            customTabsBuilder.setCloseButtonIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_arrow_back_white_cct_24dp))
            customTabsBuilder.setStartAnimations(context, R.anim.slide_in_right, R.anim.slide_out_left)
            customTabsBuilder.setExitAnimations(context, R.anim.slide_in_left, R.anim.slide_out_right)
            val customTabsIntent = customTabsBuilder.build()
            customTabsIntent.launchUrl(context, Uri.parse(URL))
        }

        /**
         * @return Drawable id for given SocialLink if found else returns 1
         */
        @JvmStatic
        fun getSocialLinkDrawableId(link: String): Int {
            var id = 1
            val name = getSocialLinkName(link.toLowerCase())

            when (name) {
                ConstantStrings.SOCIAL_LINK_GITHUB -> id = R.drawable.ic_github_24dp
                ConstantStrings.SOCIAL_LINK_TWITTER -> id = R.drawable.ic_twitter_24dp
                ConstantStrings.SOCIAL_LINK_FACEBOOK -> id = R.drawable.ic_facebook_24dp
                ConstantStrings.SOCIAL_LINK_LINKEDIN -> id = R.drawable.ic_linkedin_24dp
                ConstantStrings.SOCIAL_LINK_YOUTUBE -> id = R.drawable.ic_youtube_24dp
                ConstantStrings.SOCIAL_LINK_GOOGLE -> id = R.drawable.ic_google_plus_24dp
                else -> {
                }
            }
            return id
        }

        @JvmStatic
        private fun getSocialLinkName(link: String): String {

            return when {
                link.contains(getSocialLinkHostName(ConstantStrings.SOCIAL_LINK_GITHUB)) -> ConstantStrings.SOCIAL_LINK_GITHUB
                link.contains(getSocialLinkHostName(ConstantStrings.SOCIAL_LINK_TWITTER)) -> ConstantStrings.SOCIAL_LINK_TWITTER
                link.contains(getSocialLinkHostName(ConstantStrings.SOCIAL_LINK_FACEBOOK)) -> ConstantStrings.SOCIAL_LINK_FACEBOOK
                link.contains(getSocialLinkHostName(ConstantStrings.SOCIAL_LINK_LINKEDIN)) -> ConstantStrings.SOCIAL_LINK_LINKEDIN
                link.contains(getSocialLinkHostName(ConstantStrings.SOCIAL_LINK_YOUTUBE)) -> ConstantStrings.SOCIAL_LINK_YOUTUBE
                link.contains(getSocialLinkHostName(ConstantStrings.SOCIAL_LINK_GOOGLE)) -> ConstantStrings.SOCIAL_LINK_GOOGLE
                else -> ""
            }

        }

        @JvmStatic
        fun eventCalendar(event: Event): Intent {
            val intent = Intent(Intent.ACTION_INSERT)
            intent.type = "vnd.android.cursor.item/event"
            intent.putExtra(CalendarContract.Events.TITLE, event.name)
            intent.putExtra(CalendarContract.Events.DESCRIPTION, event.description)
            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, DateConverter.formatDateWithDefault(DateConverter.FORMAT_24H, event.startsAt))
            intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, DateConverter.formatDateWithDefault(DateConverter.FORMAT_24H, event.endsAt))
            return intent
        }

        @JvmStatic
        private fun getSocialLinkHostName(name: String): String {
            return "$name.com".toLowerCase()
        }
    }
