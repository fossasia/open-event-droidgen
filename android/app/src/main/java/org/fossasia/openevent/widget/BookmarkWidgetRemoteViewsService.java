package org.fossasia.openevent.widget;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.MatrixCursor;
import android.os.Build;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import org.fossasia.openevent.R;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.utils.ConstantStrings;
import org.fossasia.openevent.utils.ISO8601Date;

import java.util.List;

import timber.log.Timber;

/**
 * User: Opticod(Anupam Das)
 * Date: 10/1/16
 */

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class BookmarkWidgetRemoteViewsService extends RemoteViewsService {

    private static final int INDEX_BOOKMARK_ID = 0;

    private static final int INDEX_BOOKMARK_TITLE = 1;

    private static final int INDEX_BOOKMARK_START_TIME = 2;

    private static final int INDEX_BOOKMARK_END_TIME = 3;

    private static final int INDEX_BOOKMARK_DATE= 4;

    private final String ID = "id";

    private final String TITLE = "title";

    private final String START_TIME = "startTime";

    private final String END_TIME = "endTime";

    private final String DATE = "sessionDate";

    private int LESS_DETAIL_SIZE = 300;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                BookmarkWidgetProvider.class));
        final int appWidgetId = appWidgetIds[0];

        return new RemoteViewsFactory() {
            private MatrixCursor data = null;

            @Override
            public void onCreate() {
                //Called when your factory is first constructed.
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                DbSingleton dbSingleton = DbSingleton.getInstance();
                try {
                    List<Integer> bookmarkedIds = dbSingleton.getBookmarkIds();

                    String[] columns = new String[]{ID, TITLE, START_TIME, END_TIME, DATE};
                    data = new MatrixCursor(columns);

                    for (Integer id : bookmarkedIds) {
                        Session session = dbSingleton.getSessionById(id);
                        String start = ISO8601Date.get12HourTime(ISO8601Date.getDateObject(session.getStartTime()));
                        String end = ISO8601Date.get12HourTime(ISO8601Date.getDateObject(session.getEndTime()));
                        String date = ISO8601Date.getDate(ISO8601Date.getDateObject(session.getStartTime()));
                        data.addRow(new Object[]{id, session.getTitle(), start, end, date});
                    }
                } catch (Exception e) {
                    Timber.e("Parsing Error Occurred at BookmarkWidgetRemoteViewsService::onDataSetChanged.");
                }
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                if(data != null)
                    return data.getCount();

                return 0;
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }

                int widgetWidth = getWidgetWidth(appWidgetManager, appWidgetId);

                String title = data.getString(INDEX_BOOKMARK_TITLE);
                String start = data.getString(INDEX_BOOKMARK_START_TIME);
                String end = data.getString(INDEX_BOOKMARK_END_TIME);
                String date = data.getString(INDEX_BOOKMARK_DATE);
                int id = data.getInt(INDEX_BOOKMARK_ID);
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_list_item);

                views.setTextViewText(R.id.title_widget_bookmarks, title);

                if (widgetWidth > LESS_DETAIL_SIZE) {
                    views.setTextViewText(R.id.date_widget_bookmarks, date);
                    views.setTextViewText(R.id.time_widget_bookmarks, start + " to " + end);
                } else {
                    views.setTextViewText(R.id.date_widget_bookmarks, "");
                    views.setTextViewText(R.id.time_widget_bookmarks, "");
                }

                final Intent fillInIntent = new Intent();
                fillInIntent.putExtra(ConstantStrings.SESSION, title);
                fillInIntent.putExtra(ConstantStrings.ID, id);
                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(INDEX_BOOKMARK_ID);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }

            private int getWidgetWidth(AppWidgetManager appWidgetManager, int appWidgetId) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    return getResources().getDimensionPixelSize(R.dimen.widget_min_resize_width);
                }
                return getWidgetWidthFromOptions(appWidgetManager, appWidgetId);
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            private int getWidgetWidthFromOptions(AppWidgetManager appWidgetManager, int appWidgetId) {
                Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
                if (options.containsKey(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)) {
                    return options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
                }
                return getResources().getDimensionPixelSize(R.dimen.widget_min_resize_width);
            }
        };
    }
}
