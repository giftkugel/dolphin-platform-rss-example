package com.canoo.dolphin.rss.server.service;

import com.rometools.rome.feed.synd.SyndEntry;

import java.util.Date;
import java.util.Objects;

public class FeedItem {

    private String title;

    private Date date;

    private FeedItem(final String title, final Date date) {
        this.title = title;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "FeedItem{" +
                "title='" + title + '\'' +
                ", date=" + date +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, date);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final FeedItem other = (FeedItem) obj;
        return Objects.equals(this.title, other.title)
                && Objects.equals(this.date, other.date);
    }

    public static FeedItem fromSyndEntry(final SyndEntry syndEntry) {
        final FeedItem item = new FeedItem(syndEntry.getTitle(), syndEntry.getPublishedDate());
        return item;
    }
}
