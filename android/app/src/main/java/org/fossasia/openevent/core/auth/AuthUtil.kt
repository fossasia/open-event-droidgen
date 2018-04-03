package org.fossasia.openevent.core.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.annotation.StringRes
import android.support.v4.app.ActivityCompat
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast

import org.fossasia.openevent.OpenEventApp
import org.fossasia.openevent.R
import org.fossasia.openevent.core.main.MainActivity
import org.fossasia.openevent.data.repository.RealmDataRepository
import org.fossasia.openevent.common.ConstantStrings
import org.fossasia.openevent.common.api.JWTUtils
import org.fossasia.openevent.common.utils.SharedPreferencesUtil
import org.fossasia.openevent.common.utils.Utils

import java.io.IOException

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import timber.log.Timber

object AuthUtil {

    const val VALID = 0
    const val EMPTY = 1
    const val INVALID = 2

    private var authenticator: Authenticator? = null

    @JvmStatic
    val authorization: String?
        get() = formatToken(token)

    private val token: String?
        get() = SharedPreferencesUtil.getString(ConstantStrings.TOKEN, null)

    @JvmStatic
    val isUserLoggedIn: Boolean
        get() {
            val token = token
            return token != null && !JWTUtils.isExpired(token)
        }

    @JvmStatic
    fun logout(context: Context) {
        SharedPreferencesUtil.remove(ConstantStrings.TOKEN)
        SharedPreferencesUtil.remove(ConstantStrings.USER_EMAIL)
        SharedPreferencesUtil.remove(ConstantStrings.USER_FIRST_NAME)
        SharedPreferencesUtil.remove(ConstantStrings.USER_LAST_NAME)

        //Delete User data from realm
        RealmDataRepository.defaultInstance.clearUserData()

        goToMainActivity(context)
        showMessage(R.string.logged_out_successfully)
        Timber.d("Removed token & email and logged out successfully")
    }

    @JvmStatic
    fun getAuthenticator(): Authenticator {
        if (authenticator == null) {
            authenticator = Authenticator { _, response ->
                if (response.request().header("Authorization") != null) {
                    return@Authenticator null // Give up, we've already failed to authenticate.
                }

                val token = token

                if (token == null) {
                    Timber.wtf("Someone tried to access authenticated resource without auth token")
                    return@Authenticator null
                }

                response.request().newBuilder()
                        .header("Authorization", formatToken(token))
                        .build()
            }
        }
        return authenticator as Authenticator
    }

    private fun formatToken(token: String?): String {
        return String.format("JWT $token")
    }

    @JvmStatic
    private fun goToMainActivity(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
        ActivityCompat.finishAffinity(context as Activity)
    }

    private fun showProgressBar(progressBar: ProgressBar, show: Boolean) {
        if (show) progressBar.visibility = View.VISIBLE else progressBar.visibility = View.GONE
    }

    private fun showMessage(@StringRes id: Int) {
        Toast.makeText(OpenEventApp.getAppContext(), id, Toast.LENGTH_SHORT).show()
    }

    @JvmStatic
    fun validateEmail(email: String): Int {
        if (Utils.isEmpty(email)) {
            return EMPTY
        } else if (!Utils.isEmailValid(email)) {
            return INVALID
        }
        return VALID
    }

    @JvmStatic
    fun validatePassword(password: String): Int {
        if (Utils.isEmpty(password)) {
            return EMPTY
        } else if (!Utils.isPasswordValid(password)) {
            return INVALID
        }
        return VALID
    }
}
