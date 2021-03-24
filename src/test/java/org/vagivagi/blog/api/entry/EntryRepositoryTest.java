package org.vagivagi.blog.api.entry;

import am.ik.blog.entry.Entry;
import am.ik.blog.entry.EntryId;
import org.springframework.boot.test.context.SpringBootTest;
import org.vagivagi.blog.api.entry.criteria.SearchCriteria;

import java.util.List;

@SpringBootTest
public class EntryRepositoryTest {
  private EntryRepository entryRepository;

  public void findById() {
    entryRepository.findById(new EntryId("1"), true);
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
