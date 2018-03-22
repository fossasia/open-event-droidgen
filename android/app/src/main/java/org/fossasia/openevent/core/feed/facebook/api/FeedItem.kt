package org.fossasia.openevent.core.feed.facebook.api

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
data class FeedItem (
    var id: String? = null,
    var message: String? = null,
    var createdTime: String? = null,
    var comments: Comments? = null,
    var fullPicture: String? = null,
    var link: String? = null
)