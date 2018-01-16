package com.canoo.dolphin.rss.server.model;

import com.canoo.platform.remoting.DolphinBean;
import com.canoo.platform.remoting.ObservableList;

@DolphinBean
public class FeedList {

    private ObservableList<FeedEntry> feedEntries;

    public ObservableList<FeedEntry> getFeedEntries() {
        return feedEntries;
    }
}
