package org.vagivagi.blog.api.entry;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.util.StringUtils;
import am.ik.blog.entry.Author;
import am.ik.blog.entry.Categories;
import am.ik.blog.entry.Category;
import am.ik.blog.entry.Content;
import am.ik.blog.entry.Entry;
import am.ik.blog.entry.Entry.EntryBuilder;
import am.ik.blog.entry.EntryId;
import am.ik.blog.entry.EventTime;
import am.ik.blog.entry.FrontMatter;
import am.ik.blog.entry.Name;
import am.ik.blog.entry.Tags;
import am.ik.blog.entry.Title;

class EntryExtractors {
  static ResultSetExtractor<Optional<Entry>> forEntry(boolean excludeContent) {
    return rs -> {
      if (!rs.next()) {
        return Optional.empty();
      }
      List<Category> categories = new ArrayList<>();
      EntryBuilder builder = builder(rs, categories, excludeContent);
      do {
        String categoryName = rs.getString("category_name");
        if (!StringUtils.isEmpty(categoryName)) {
          categories.add(new Category(categoryName));
        }
      } while (rs.next());
      return Optional.of(builder.build());
    };
  }

  static ResultSetExtractor<List<Entry>> forEntries(boolean excludeContent) {
    return rs -> {
      List<Entry> entries = new ArrayList<>();
      if (rs.next()) {
        withEntries(rs, entries::add, excludeContent);
      }
      return entries;
    };
  }

  static void withEntries(ResultSet rs, Consumer<Entry> consumer, boolean excludeContent)
      throws SQLException {
    long prevId = -1;
    EntryBuilder builder = null;
    List<Category> categories = new ArrayList<>();
    do {
      long entryId = rs.getLong("entry_id");
      if (entryId != prevId) {
        if (builder != null) {
          Entry entry = builder.build();
          consumer.accept(entry);
        }
        categories = new ArrayList<>();
        builder = builder(rs, categories, excludeContent);
      }
      Category category = new Category(rs.getString("category_name"));
      categories.add(category);
      prevId = entryId;
    } while (rs.next());
    if (builder != null) {
      // for last loop
      Entry entry = builder.build();
      consumer.accept(entry);
    }
  }

  private static EntryBuilder builder(ResultSet rs, List<Category> categories,
      boolean excludeContent) throws SQLException {
    EventTime createdDate = new EventTime(
        OffsetDateTime.of(rs.getTimestamp("created_date").toLocalDateTime(), ZoneOffset.UTC));
    EventTime lastModifiedDate = new EventTime(
        OffsetDateTime.of(rs.getTimestamp("last_modified_date").toLocalDateTime(), ZoneOffset.UTC));
    EntryBuilder entryBuilder = Entry.builder() //
        .entryId(new EntryId(rs.getLong("entry_id")));
    if (!excludeContent) {
      entryBuilder = entryBuilder.content(new Content(rs.getString("content")));
    } else {
      entryBuilder = entryBuilder.content(new Content(""));
    }
    return entryBuilder //
        .frontMatter(new FrontMatter(new Title(rs.getString("title")), new Categories(categories),
            new Tags(), createdDate, lastModifiedDate)) //
        .created(new Author(new Name(rs.getString("created_by")), createdDate)) //
        .updated(new Author(new Name(rs.getString("last_modified_by")), lastModifiedDate));
  }
}
