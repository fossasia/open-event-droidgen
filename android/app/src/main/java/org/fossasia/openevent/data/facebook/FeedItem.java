package org.fossasia.openevent.data.facebook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;

/**
 * Created by rohanagarwal94 on 6/09/2017.
 */

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FeedItem {

    private String id;
    private String message;
    private String createdTime;
    private Comments comments;
    private String fullPicture;
    private String link;
}