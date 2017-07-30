package org.fossasia.openevent.utils;

import android.support.v4.util.SparseArrayCompat;

import org.json.JSONException;
import org.json.JSONObject;

public class JWTUtils {

    public static SparseArrayCompat<String> decode(String token) {
        SparseArrayCompat<String> decoded = new SparseArrayCompat<>(2);

        String[] split = token.split("\\.");
        decoded.append(0, getJson(split[0]));
        decoded.append(1, getJson(split[1]));

        return decoded;
    }

    public static long getExpiry(String token) throws JSONException {
        SparseArrayCompat<String> decoded = decode(token);

        // We are using JSONObject instead of GSON as it takes about 5 ms instead of 150 ms taken by GSON
        return Long.parseLong(new JSONObject(decoded.get(1)).get("exp").toString());
    }

    public static boolean isExpired(String token) {
        long expiry;

        try {
            expiry = getExpiry(token);
        } catch (JSONException jse) {
            return true;
        }

        return System.currentTimeMillis() / 1000 >= expiry;
    }

    private static String getJson(String strEncoded) {
        byte[] decodedBytes = Base64Utils.decode(strEncoded);
        return new String(decodedBytes);
    }

    /**
     * Base64 class because we can't test Android class and this is faster
     */
    private static class Base64Utils {

        private final static char[] ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();

        private static int[] toInt = new int[128];

        static {
            for (int i = 0; i < ALPHABET.length; i++) {
                toInt[ALPHABET[i]] = i;
            }
        }

        /**
         * Translates the specified Base64 string into a byte array.
         *
         * @param s the Base64 string (not null)
         * @return the byte array (not null)
         */
        private static byte[] decode(String s) {
            int delta = s.endsWith("==") ? 2 : s.endsWith("=") ? 1 : 0;
            byte[] buffer = new byte[s.length() * 3 / 4 - delta];
            int mask = 0xFF;
            int index = 0;
            for (int i = 0; i < s.length(); i += 4) {
                int c0 = toInt[s.charAt(i)];
                int c1 = toInt[s.charAt(i + 1)];
                buffer[index++] = (byte) (((c0 << 2) | (c1 >> 4)) & mask);
                if (index >= buffer.length) {
                    return buffer;
                }
                int c2 = toInt[s.charAt(i + 2)];
                buffer[index++] = (byte) (((c1 << 4) | (c2 >> 2)) & mask);
                if (index >= buffer.length) {
                    return buffer;
                }
                int c3 = toInt[s.charAt(i + 3)];
                buffer[index++] = (byte) (((c2 << 6) | c3) & mask);
            }
            return buffer;
        }

    }
}
