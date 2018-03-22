package org.fossasia.openevent.core.feed.facebook.api

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import kotlinx.android.parcel.Parcelize

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
@Parcelize
data class CommentItem(
        val createdTime: String? = null,
        val from: Commenter? = null,
        val message: String? = null,
        val id: String? = null
): Parcelable