package org.vagivagi.blog.api.entry;

import am.ik.blog.entry.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.vagivagi.blog.api.entry.criteria.SearchCriteria;

import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class EntryRepositoryTest {
  @Autowired private EntryRepository entryRepository;

  @Test
  @Sql("/testfiles/test_data_entry.sql")
  public void findById() {
    Optional<Entry> entryOptional = entryRepository.findById(new EntryId("1"), true);
    entryOptional.ifPresentOrElse(
        entry -> {
          assertAll(
              "entry",
              () -> assertThat(entry.getEntryId()).isEqualTo(new EntryId("1")),
              () -> assertThat(entry.getFrontMatter().title()).isEqualTo(new Title("Title")),
              () ->
                  assertThat(entry.getFrontMatter().categories())
                      .isEqualTo(new Categories(new Category("demo"), new Category("Hello"))));
        },
        () -> {
          fail("entry is not found.");
        });
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
