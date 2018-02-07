package com.canoo.dolphin.rss.server.service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class FeedEntry {

    private List<FeedItem> items = new CopyOnWriteArrayList<>();

    private String url;

    private String title;

    FeedEntry(final String url, final String title) {
        this.url = url;
        this.title = title;
    }

    public List<FeedItem> getItems() {
        return items;
    }

    public void setItems(final List<FeedItem> items) {
        this.items = items;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }
}
