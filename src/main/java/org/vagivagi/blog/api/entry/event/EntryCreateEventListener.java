package org.vagivagi.blog.api.entry.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.vagivagi.blog.api.entry.EntryRepository;
import am.ik.blog.entry.Entry;

@Component
public class EntryCreateEventListener {
  private final Logger log = LoggerFactory.getLogger(EntryCreateEventListener.class);
  private final EntryRepository entryRepository;

  public EntryCreateEventListener(EntryRepository entryRepository) {
    this.entryRepository = entryRepository;
  }

  @EventListener
  public void onUpdate(EntryCreateEvent event) {
    Entry entry = event.getEntry();
    log.info("Create {}", entry.getEntryId());
    this.entryRepository.create(entry);
  }
}
