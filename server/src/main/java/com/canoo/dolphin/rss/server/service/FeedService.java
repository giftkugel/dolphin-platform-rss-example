package com.canoo.dolphin.rss.server.service;

import com.canoo.platform.remoting.server.event.RemotingEventBus;
import com.canoo.platform.remoting.server.event.Topic;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Service
public class FeedService {

    private static final Logger LOG = LoggerFactory.getLogger(FeedService.class);

    private final RemotingEventBus eventBus;

    private final ReentrantLock lock = new ReentrantLock();

    private final List<String> feeds = Arrays.asList(
            "http://www.spiegel.de/schlagzeilen/index.rss",
            "http://www.faz.net/rss/aktuell/");

    private final Map<String, FeedEntry> collectedFeeds = new ConcurrentHashMap<>();

    public FeedService(final RemotingEventBus eventBus) {
        this.eventBus = eventBus;


        final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        executor.scheduleAtFixedRate(() -> {
            feeds.forEach(url -> {
                try {
                    final URL feedUrl = new URL(url);

                    final SyndFeedInput input = new SyndFeedInput();
                    final SyndFeed feed = input.build(new XmlReader(feedUrl));
                    LOG.info("Feed fetched from {} with {} entries", url, feed.getEntries().size());
                    lock.lock();
                    try {
                        addEntries(url, feed.getTitle(), feed.getEntries());
                    } finally {
                        lock.unlock();
                    }
                } catch (IOException | FeedException exception) {
                    LOG.error("Could not fetch or parse RSS feed for: {}", url);
                }
            });

        }, 0, 30, TimeUnit.SECONDS);
    }

    public List<FeedEntry> getFeeds() {
        lock.lock();
        try {
            return new CopyOnWriteArrayList<>(collectedFeeds.values());
        } finally {
            lock.unlock();
        }
    }

    private void addEntries(final String url, final String title, final List<SyndEntry> items) {
        final FeedEntry entry = collectedFeeds.get(url);
        if (entry == null) {
            final FeedEntry newEntry = new FeedEntry(url, title);
            newEntry.setItems(items.stream()
                    .sorted(this::compareSyndEntries)
                    .map(FeedItem::fromSyndEntry)
                    .collect(Collectors.toList())
            );
            collectedFeeds.put(url, newEntry);
        } else {
            final List<FeedItem> newItems = items.stream()
                    .filter(syndEntry -> !entry.getItems().contains(FeedItem.fromSyndEntry(syndEntry)))
                    .sorted(this::compareSyndEntries)
                    .map(FeedItem::fromSyndEntry)
                    .collect(Collectors.toList());

            if (newItems.size() > 0) {
                entry.getItems().addAll(newItems);
                LOG.debug("{} new items found: {}", newItems.size(), newItems.stream().map(FeedItem::toString).collect(Collectors.toList()));

                final Topic<NewItemsEvent> topic = Topic.create("update");
                eventBus.publish(topic, new NewItemsEvent(url, newItems));
            }
        }
    }

    private int compareSyndEntries(final SyndEntry first, final SyndEntry second) {
        if (first.getPublishedDate().after(second.getPublishedDate())) {
            return -1;
        } else if (first.getPublishedDate().before(second.getPublishedDate())) {
            return 1;
        } else {
            return 0;
        }
    }

}
