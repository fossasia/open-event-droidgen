package org.fossasia.openevent.core.feed.twitter.api

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
data class TwitterFeedItem(
        var link: String? = null,
        var hashTags: List<String>? = null,
        var links: List<String>? = null,
        var images: List<String>? = null,
        var text: String? = null,
        var createdAt: String? = null
)
