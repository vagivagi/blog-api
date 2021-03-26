package org.vagivagi.blog.api.entry;

import am.ik.blog.entry.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.vagivagi.blog.api.entry.criteria.SearchCriteria;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class EntryRepositoryTest {
  @Autowired private EntryRepository entryRepository;

  @Nested
  @ExtendWith(SpringExtension.class)
  @SpringBootTest
  @Sql("/testfiles/test_data_entry.sql")
  class findById {
    @Test
    public void success_include_content() {
      entryRepository
          .findById(new EntryId("1"), false)
          .ifPresentOrElse(
              entry -> {
                this.assertAllForEntry(entry);
                assertThat(entry.getContent()).isEqualTo(new Content("Content"));
              },
              () -> {
                fail("entry is not found.");
              });
    }

    @Test
    public void success_exclude_content() {
      entryRepository
          .findById(new EntryId("1"), true)
          .ifPresentOrElse(
              entry -> {
                this.assertAllForEntry(entry);
                assertThat(entry.getContent()).isEqualTo(new Content(""));
              },
              () -> {
                fail("entry is not found.");
              });
    }

    @Test
    public void not_found_include() {
      entryRepository
          .findById(new EntryId("99"), false)
          .ifPresent(
              entry -> {
                fail("entry is found.");
              });
    }

    @Test
    public void not_found_exclude() {
      entryRepository
          .findById(new EntryId("99"), true)
          .ifPresent(
              entry -> {
                fail("entry is found.");
              });
    }

    private void assertAllForEntry(Entry entry) {
      assertAll(
          "entry",
          () -> assertThat(entry.getEntryId()).isEqualTo(new EntryId("1")),
          () -> assertThat(entry.getFrontMatter().title()).isEqualTo(new Title("Title")),
          () ->
              assertThat(entry.getFrontMatter().categories())
                  .isEqualTo(new Categories(new Category("demo"), new Category("Hello"))),
          () ->
              assertThat(entry.getFrontMatter().tags())
                  .isEqualTo(new Tags(new Tag("blog"), new Tag("demo"))),
          () ->
              assertThat(entry.getFrontMatter().date().getValue())
                  .isEqualTo(
                      OffsetDateTime.of(LocalDateTime.of(2021, 3, 1, 21, 0, 0), ZoneOffset.UTC)),
          () ->
              assertThat(entry.getFrontMatter().updated().getValue())
                  .isEqualTo(
                      OffsetDateTime.of(LocalDateTime.of(2021, 3, 2, 22, 0, 0), ZoneOffset.UTC)),
          () ->
              assertThat(entry.getCreated())
                  .isEqualTo(
                      new Author(
                          new Name("author"),
                          new EventTime(
                              OffsetDateTime.of(
                                  LocalDateTime.of(2021, 3, 1, 21, 0, 0), ZoneOffset.UTC)))),
          () ->
              assertThat(entry.getUpdated())
                  .isEqualTo(
                      new Author(
                          new Name("updater"),
                          new EventTime(
                              OffsetDateTime.of(
                                  LocalDateTime.of(2021, 3, 2, 22, 0, 0), ZoneOffset.UTC)))));
    }
  }

  public void entryIds() {
    SearchCriteria searchCriteria = SearchCriteria.builder().build();
    entryRepository.entryIds(searchCriteria, searchCriteria.toWhereClause(), null);
  }

  public void sqlForEntries() {
    entryRepository.sqlForEntries(true);
  }

  public void findAll() {
    SearchCriteria searchCriteria = SearchCriteria.builder().build();
    entryRepository.findAll(searchCriteria);
  }

  public void create() {
    Entry entry = Entry.builder().build();
    entryRepository.create(entry);
  }

  public void update() {
    Entry entry = Entry.builder().build();
    entryRepository.update(entry);
  }

  public void delete() {
    entryRepository.delete(new EntryId("1"));
  }

  public void tagsMap() {
    entryRepository.tagsMap(List.of(1l));
  }
}
