package com.canoo.dolphin.rss.server.service;

import java.io.Serializable;
import java.util.List;

public class NewItemsEvent implements Serializable {

    private String url;

    private List<FeedItem> items;

    public NewItemsEvent(final String url, final List<FeedItem> items) {
        this.url = url;
        this.items = items;
    }

    public String getUrl() {
        return url;
    }

    public List<FeedItem> getItems() {
        return items;
    }
}
