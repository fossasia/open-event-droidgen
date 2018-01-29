package org.fossasia.openevent.data.twitter;

import java.util.ArrayList;

import lombok.Data;

@Data
public class TwitterFeed {

    private ArrayList<TwitterFeedItem> statuses;
}
