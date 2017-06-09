package org.fossasia.openevent.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.EdgeEffectCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EdgeEffect;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static android.os.Build.VERSION_CODES.JELLY_BEAN;
import static android.os.Build.VERSION_CODES.LOLLIPOP;

public final class Views {

    private Views() {
        throw new UnsupportedOperationException();
    }

    @TargetApi(JELLY_BEAN)
    public static void removeOnGlobalLayoutListener(ViewTreeObserver viewTreeObserver, ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (isCompatible(JELLY_BEAN)) {
            viewTreeObserver.removeOnGlobalLayoutListener(listener);
        } else {
            viewTreeObserver.removeGlobalOnLayoutListener(listener);
        }
    }

    @TargetApi(LOLLIPOP)
    @SuppressWarnings("deprecation")
    public static Drawable getDrawable(Context context, @DrawableRes int id) {
        if (isCompatible(LOLLIPOP)) {
            return context.getDrawable(id);
        } else {
            return context.getResources().getDrawable(id);
        }
    }

    public static int getActionBarSize(Context context) {
        if (context == null) {
            return 0;
        }

        Resources.Theme curTheme = context.getTheme();
        if (curTheme == null) {
            return 0;
        }

        int[] attrs = {android.R.attr.actionBarSize};
        TypedArray att = curTheme.obtainStyledAttributes(attrs);
        if (att == null) {
            return 0;
        }

        float size = att.getDimension(0, 0);
        att.recycle();
        return (int) size;
    }

    public static boolean isCompatible(int apiLevel) {
        return android.os.Build.VERSION.SDK_INT >= apiLevel;
    }

    public static void setTint(ImageView imageView, int tintColor) {
        Drawable wrapped = DrawableCompat.wrap(imageView.getDrawable());
        DrawableCompat.setTint(wrapped, tintColor);
    }

    public static int getDarkColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f;
        return Color.HSVToColor(hsv);
    }

    public static CharSequence fromHtml(String html) {
        if (TextUtils.isEmpty(html)) {
            return "";
        }

        if (isCompatible(Build.VERSION_CODES.N)) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(html);
        }
    }

    public static void setHtml(TextView textView, String html, boolean hide) {
        CharSequence converted = fromHtml(html);

        if (TextUtils.isEmpty(converted)) {
            if(hide) textView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.VISIBLE);
            textView.setText(converted);
        }

        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /*Check if the version is above Lollipop to change the edge glow color of Nested ScrollView
    according to the session detail activity color*/
    public static void setEdgeGlowColorScrollView(int color, NestedScrollView scrollView) {

        try {
            Field edgeGlowTop = NestedScrollView.class.getDeclaredField("mEdgeGlowTop");
            edgeGlowTop.setAccessible(true);
            Field edgeGlowBottom = NestedScrollView.class.getDeclaredField("mEdgeGlowBottom");
            edgeGlowBottom.setAccessible(true);

            EdgeEffectCompat edgeEffect = (EdgeEffectCompat) edgeGlowTop.get(scrollView);
            if (edgeEffect == null) {
                edgeEffect = new EdgeEffectCompat(scrollView.getContext());
                edgeGlowTop.set(scrollView, edgeEffect);
            }

            Views.setEdgeGlowColor(edgeEffect, color);
            edgeEffect = (EdgeEffectCompat) edgeGlowBottom.get(scrollView);
            if (edgeEffect == null) {
                edgeEffect = new EdgeEffectCompat(scrollView.getContext());
                edgeGlowBottom.set(scrollView, edgeEffect);
            }
            Views.setEdgeGlowColor(edgeEffect, color);

        } catch (Exception ex) {
            ex.printStackTrace();
            //Catching the error that can be caused by EdgeEffectCompat
        }

    }

    /*Check if the version is above Lollipop to change the edge glow color of RecyclerView
    according to the session detail activity color*/
    @TargetApi(LOLLIPOP)
    public static void setEdgeGlowColorRecyclerView(final RecyclerView recyclerView, final int color) {

        try {
            final Class<?> clazz = RecyclerView.class;
            for (final String name : new String[]{"ensureTopGlow", "ensureBottomGlow", "ensureLeftGlow", "ensureRightGlow"}) {
                Method method = clazz.getDeclaredMethod(name);
                method.setAccessible(true);
                method.invoke(recyclerView);
            }
            for (final String name : new String[]{"mTopGlow", "mBottomGlow", "mLeftGlow", "mRightGlow"}) {
                final Field field = clazz.getDeclaredField(name);
                field.setAccessible(true);
                final Object edge = field.get(recyclerView);
                final Field fEdgeEffect = edge.getClass().getDeclaredField("mEdgeEffect");
                fEdgeEffect.setAccessible(true);
                ((EdgeEffect) fEdgeEffect.get(edge)).setColor(color);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            //Catching the error that can be caused by setColor() for versions < LOLLIPOP
        }

    }

    @TargetApi(LOLLIPOP)
    public static void setEdgeGlowColor(@NonNull EdgeEffectCompat edgeEffect, @ColorInt int color) throws Exception {
        Field field = EdgeEffectCompat.class.getDeclaredField("mEdgeEffect");
        field.setAccessible(true);
        EdgeEffect effect = (EdgeEffect) field.get(edgeEffect);
        if (effect != null)
            effect.setColor(color);
    }
}
