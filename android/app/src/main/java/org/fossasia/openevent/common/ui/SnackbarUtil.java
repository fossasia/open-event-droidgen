package org.fossasia.openevent.common.ui;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.ColorUtils;

import org.fossasia.openevent.R;
import org.fossasia.openevent.data.repository.RealmDataRepository;
import org.fossasia.openevent.core.bookmark.BookmarkStatus;

import static org.fossasia.openevent.core.bookmark.BookmarkStatus.Status.CODE_ERROR;
import static org.fossasia.openevent.core.bookmark.BookmarkStatus.Status.CODE_UNDO_ADDED;
import static org.fossasia.openevent.core.bookmark.BookmarkStatus.Status.CODE_UNDO_REMOVED;

public class SnackbarUtil {
    public static Snackbar setSnackbarAction(Context context, Snackbar snackbar, BookmarkStatus bookmarkStatus) {

        if (bookmarkStatus.getActionCode() == CODE_UNDO_ADDED) {
            snackbar.setAction(R.string.undo, view1 -> {
                RealmDataRepository.getDefaultInstance().setBookmark(bookmarkStatus.getSessionId(), false).subscribe();
                WidgetUpdater.updateWidget(context);
            })
                    .setActionTextColor(ColorUtils.setAlphaComponent(Views.getAccentColor(context), 220));
        } else if (bookmarkStatus.getActionCode() == CODE_UNDO_REMOVED) {
            snackbar.setAction(R.string.undo, view1 -> {
                RealmDataRepository.getDefaultInstance().setBookmark(bookmarkStatus.getSessionId(), true).subscribe();
                WidgetUpdater.updateWidget(context);
            })
                    .setActionTextColor(ColorUtils.setAlphaComponent(Views.getAccentColor(context), 220));
        }

        return snackbar;
    }

    public static int getMessageResource(BookmarkStatus bookmarkStatus) {
        if (bookmarkStatus.getActionCode() == CODE_UNDO_ADDED)
            return R.string.added_bookmark;
        else if (bookmarkStatus.getActionCode() == CODE_UNDO_REMOVED)
            return R.string.removed_bookmark;
        else if (bookmarkStatus.getActionCode() == CODE_ERROR)
            return R.string.error_create_notification;
        else
            return R.string.added_bookmark;
    }
}
