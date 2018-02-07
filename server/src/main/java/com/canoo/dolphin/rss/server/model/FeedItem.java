package com.canoo.dolphin.rss.server.model;

import com.canoo.platform.remoting.DolphinBean;
import com.canoo.platform.remoting.Property;

import java.util.Date;
import java.util.Objects;

@DolphinBean
public class FeedItem {

    private Property<String> text;

    private Property<Date> date;

    public String getText() {
        return text.get();
    }

    public Property<String> textProperty() {
        return text;
    }

    public void setText(final String text) {
        this.text.set(text);
    }

    public Property<Date> dateProperty() {
        return date;
    }

    public Date getDate() {
        return date.get();
    }

    public void setDate(final Date date) {
        this.date.set(date);
    }

    public FeedItem withText(final String text) {
        setText(text);
        return this;
    }

    public FeedItem withDate(final Date date) {
        setDate(date);
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(text.get(), date.get());
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
        return Objects.equals(this.text.get(), other.text.get())
                && Objects.equals(this.date.get(), other.date.get());
    }
}
