package org.vagivagi.blog.api.entry.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.vagivagi.blog.api.entry.EntryRepository;
import am.ik.blog.entry.Entry;

@Component
public class EntryUpdateEventListener {
  private final Logger log = LoggerFactory.getLogger(EntryUpdateEventListener.class);
  private final EntryRepository entryRepository;

  public EntryUpdateEventListener(EntryRepository entryRepository) {
    this.entryRepository = entryRepository;
  }

  @EventListener
  public void onUpdate(EntryUpdateEvent event) {
    Entry entry = event.getEntry();
    log.info("Update {}", entry.getEntryId());
    this.entryRepository.update(entry);
  }
}
