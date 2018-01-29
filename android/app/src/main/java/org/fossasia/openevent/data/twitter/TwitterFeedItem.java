package org.fossasia.openevent.data.twitter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.ArrayList;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TwitterFeedItem {
    private String link;
    private ArrayList<String> hashTags;
    private ArrayList<String> links;
    private ArrayList<String> images;
    private String text;
    private String createdAt;
}
