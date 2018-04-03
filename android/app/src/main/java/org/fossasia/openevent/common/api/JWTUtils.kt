package org.fossasia.openevent.common.api

import android.support.v4.util.SparseArrayCompat

import org.json.JSONException
import org.json.JSONObject

object JWTUtils {

    private fun decode(token: String): SparseArrayCompat<String> {
        val decoded = SparseArrayCompat<String>(2)

        val split = token.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        decoded.append(0, getJson(split[0]))
        decoded.append(1, getJson(split[1]))

        return decoded
    }

    @Throws(JSONException::class)
    private fun getExpiry(token: String): Long {
        val decoded = decode(token)

        // We are using JSONObject instead of GSON as it takes about 5 ms instead of 150 ms taken by GSON
        return JSONObject(decoded.get(1)).get("exp").toString().toLong()
    }

    @Throws(JSONException::class)
    @JvmStatic
    fun getIdentity(token: String): Int {
        val decoded = decode(token)

        return JSONObject(decoded.get(1)).get("identity").toString().toInt()
    }

    @JvmStatic
    fun isExpired(token: String): Boolean {
        val expiry: Long

        try {
            expiry = getExpiry(token)
        } catch (jse: JSONException) {
            return true
        }

        return System.currentTimeMillis() / 1000 >= expiry
    }

    private fun getJson(strEncoded: String): String {
        val decodedBytes = Base64Utils.decode(strEncoded)
        return String(decodedBytes)
    }

    /**
     * Base64 class because we can't test Android class and this is faster
     */
    private object Base64Utils {

        private val ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray()

        private val toInt = IntArray(128)

        init {
            for (i in ALPHABET.indices) {
                toInt[ALPHABET[i].toInt()] = i
            }
        }

        /**
         * Translates the specified Base64 string into a byte array.
         *
         * @param s the Base64 string (not null)
         * @return the byte array (not null)
         */
        fun decode(s: String): ByteArray {
            val delta = if (s.endsWith("==")) 2 else if (s.endsWith("=")) 1 else 0
            val buffer = ByteArray(s.length * 3 / 4 - delta)
            val mask = 0xFF
            var index = 0
            var i = 0
            while (i < s.length) {
                val c0 = toInt[s[i].toInt()]
                val c1 = toInt[s[i + 1].toInt()]
                buffer[index++] = (c0 shl 2 or (c1 shr 4) and mask).toByte()
                if (index >= buffer.size) {
                    return buffer
                }
                val c2 = toInt[s[i + 2].toInt()]
                buffer[index++] = (c1 shl 4 or (c2 shr 2) and mask).toByte()
                if (index >= buffer.size) {
                    return buffer
                }
                val c3 = toInt[s[i + 3].toInt()]
                buffer[index++] = (c2 shl 6 or c3 and mask).toByte()
                i += 4
            }
            return buffer
        }

    }
}
