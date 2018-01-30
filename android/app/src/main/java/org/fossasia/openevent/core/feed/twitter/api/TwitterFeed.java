package org.fossasia.openevent.core.feed.twitter.api;

import java.util.ArrayList;

import lombok.Data;

@Data
public class TwitterFeed {

    private ArrayList<TwitterFeedItem> statuses;

}
