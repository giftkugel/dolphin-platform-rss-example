package com.canoo.dolphin.rss.server.controller;

import com.canoo.dolphin.rss.server.model.FeedEntry;
import com.canoo.dolphin.rss.server.model.FeedItem;
import com.canoo.dolphin.rss.server.model.FeedList;
import com.canoo.dolphin.rss.server.service.FeedService;
import com.canoo.platform.remoting.BeanManager;
import com.canoo.platform.remoting.server.DolphinController;
import com.canoo.platform.remoting.server.DolphinModel;
import com.canoo.platform.remoting.server.event.RemotingEventBus;
import com.canoo.platform.remoting.server.event.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@DolphinController("FeedController")
public class FeedController {

    private final static Logger LOG = LoggerFactory.getLogger(FeedController.class);

    private final BeanManager beanManager;

    private final RemotingEventBus eventBus;

    private final FeedService feedService;

    @DolphinModel
    private FeedList feedList;

    @Inject
    public FeedController(final BeanManager beanManager, final RemotingEventBus eventBus, final FeedService feedService) {
        this.beanManager = beanManager;
        this.feedService = feedService;
        this.eventBus = eventBus;
        LOG.debug("constructor done");
    }

    @PostConstruct
    public void onInit() {
        feedService.getFeedsAsStream().forEach(feed -> {
            final FeedEntry feedEntry = beanManager.create(FeedEntry.class).withName(feed.getTitle()).withUrl(feed.getUri());
            feed.getEntries().stream().forEach(entry -> {
                final FeedItem feedItem = beanManager.create(FeedItem.class).withText(entry.getTitle()).withTime(entry.getPublishedDate().toString());
                feedEntry.getItems().add(feedItem);
            });
            feedList.getFeedEntries().add(feedEntry);
        });
        final Topic<String> topic = Topic.create("update");
        eventBus.subscribe(topic, message -> update(message.getData()));
        LOG.debug("init method");
    }

    private void update(final String url) {
        feedService.getFeedsAsStream().filter(syndFeed -> syndFeed.getUri().equals(url)).forEach(feed -> {
            final FeedEntry feedEntry = beanManager.create(FeedEntry.class).withName(feed.getTitle()).withUrl(feed.getUri());
            feed.getEntries().stream().forEach(entry -> {
                final FeedItem feedItem = beanManager.create(FeedItem.class).withText(entry.getTitle()).withTime(entry.getPublishedDate().toString());
                feedEntry.getItems().add(feedItem);
            });
            feedList.getFeedEntries().add(feedEntry);
            LOG.debug("updated {}", url);
        });
    }

}
