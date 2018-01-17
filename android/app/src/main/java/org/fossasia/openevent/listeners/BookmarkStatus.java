package org.fossasia.openevent.listeners;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookmarkStatus {

    private int storedColor;
    private int sessionId;
    private Status actionCode;

    public enum Status {
        CODE_UNDO_ADDED,
        CODE_BLANK,
        CODE_UNDO_REMOVED,
        CODE_ERROR
    }
}
