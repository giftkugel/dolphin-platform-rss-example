package com.canoo.dolphin.rss.server.controller;

import com.canoo.dolphin.rss.server.model.FeedEntry;
import com.canoo.dolphin.rss.server.model.FeedItem;
import com.canoo.dolphin.rss.server.model.FeedList;
import com.canoo.dolphin.rss.server.service.FeedService;
import com.canoo.dolphin.rss.server.service.NewItemsEvent;
import com.canoo.platform.remoting.BeanManager;
import com.canoo.platform.remoting.server.DolphinController;
import com.canoo.platform.remoting.server.DolphinModel;
import com.canoo.platform.remoting.server.event.RemotingEventBus;
import com.canoo.platform.remoting.server.event.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.stream.Collectors;

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
    }

    @PostConstruct
    public void onInit() {
        feedService.getFeeds().forEach(feed -> {
            final FeedEntry feedEntry = beanManager.create(FeedEntry.class)
                    .withName(feed.getTitle())
                    .withUrl(feed.getUrl());
            feed.getItems().forEach(entry -> {
                final FeedItem feedItem = beanManager.create(FeedItem.class)
                        .withText(entry.getTitle())
                        .withDate(entry.getDate());
                feedEntry.getItems().add(feedItem);
            });
            feedList.getFeedEntries().add(feedEntry);
        });
        final Topic<NewItemsEvent> topic = Topic.create("update");
        eventBus.subscribe(topic, message -> update(message.getData()));
    }

    private void update(final NewItemsEvent event) {
        LOG.debug("Updating {}", event.getUrl());
        feedList.getFeedEntries().stream()
                .filter(feedEntry -> event.getUrl().equals(feedEntry.getUrl()))
                .forEach(feedEntry -> feedEntry.getItems().addAll(0, event.getItems().stream()
                        .map(feedItem -> beanManager.create(FeedItem.class)
                                .withText(feedItem.getTitle())
                                .withDate(feedItem.getDate()))
                        .collect(Collectors.toList())));
    }

}
