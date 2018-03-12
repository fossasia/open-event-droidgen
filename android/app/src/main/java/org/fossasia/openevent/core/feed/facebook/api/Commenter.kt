package org.fossasia.openevent.core.feed.facebook.api

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Commenter (
    var name: String? = null,
    var id: String? = null
): Parcelable