package org.fossasia.openevent.core.feed;

import org.fossasia.openevent.core.feed.facebook.api.CommentItem;

import java.util.List;

public interface OpenCommentsDialogListener {
    void openCommentsDialog(List<CommentItem> commentItems);
}