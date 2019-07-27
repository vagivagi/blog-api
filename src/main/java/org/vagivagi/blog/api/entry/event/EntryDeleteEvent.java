package org.vagivagi.blog.api.entry.event;

import am.ik.blog.entry.EntryId;

public class EntryDeleteEvent {
  private final EntryId entryId;

  public EntryDeleteEvent(EntryId entryId) {
    this.entryId = entryId;
  }

  public EntryId getEntryId() {
    return entryId;
  }
}
