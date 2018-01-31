package org.fossasia.openevent.common.ui;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import org.fossasia.openevent.core.bookmark.BookmarkWidgetProvider;

public class WidgetUpdater {
    public  static  void updateWidget(Context context){
        int widgetIds[] = AppWidgetManager.getInstance(context.getApplicationContext())
                .getAppWidgetIds(new ComponentName(context.getApplicationContext(), BookmarkWidgetProvider.class));
        BookmarkWidgetProvider bookmarkWidgetProvider = new BookmarkWidgetProvider();
        bookmarkWidgetProvider.onUpdate(context.getApplicationContext(),
                AppWidgetManager.getInstance(context.getApplicationContext()),widgetIds);
        context.sendBroadcast(new Intent(BookmarkWidgetProvider.ACTION_UPDATE));
    }
}
