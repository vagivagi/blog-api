package org.vagivagi.blog.api;

import am.ik.blog.entry.Author;
import am.ik.blog.entry.Categories;
import am.ik.blog.entry.Category;
import am.ik.blog.entry.Content;
import am.ik.blog.entry.Entry;
import am.ik.blog.entry.EntryId;
import am.ik.blog.entry.EventTime;
import am.ik.blog.entry.FrontMatter;
import am.ik.blog.entry.Name;
import am.ik.blog.entry.Tag;
import am.ik.blog.entry.Tags;
import am.ik.blog.entry.Title;

public class Fixtures {

  public static Entry entry(EntryId entryId) {
    EventTime now = EventTime.now();
    Categories categories =
        new Categories(new Category("foo"), new Category("bar"), new Category("hoge"));
    Tags tags = new Tags(new Tag("a"), new Tag("b"), new Tag("c"));
    return Entry.builder() //
        .entryId(entryId) //
        .content(new Content("Hello")) //
        .frontMatter(new FrontMatter(new Title("Hello"), categories, tags, now, now)) //
        .created(new Author(new Name("demo"), now)) //
        .updated(new Author(new Name("demo"), now)) //
        .build();
  }
}
