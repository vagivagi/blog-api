package org.vagivagi.blog.api.entry.event;

import am.ik.blog.entry.Entry;

public class EntryUpdateEvent {
  private final Entry entry;

  public EntryUpdateEvent(Entry entry) {
    this.entry = entry;
  }

  public Entry getEntry() {
    return entry;
  }
}
