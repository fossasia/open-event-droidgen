package org.fossasia.openevent.core.bookmark

data class BookmarkStatus(
        var storedColor: Int? = null,
        var sessionId: Int? = null,
        var actionCode: Status? = null) {

    enum class Status {
        CODE_UNDO_ADDED,
        CODE_BLANK,
        CODE_UNDO_REMOVED,
        CODE_ERROR
    }
}
