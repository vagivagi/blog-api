package org.vagivagi.blog.api.entry;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.vagivagi.blog.api.entry.criteria.CategoryOrders;
import org.vagivagi.blog.api.entry.criteria.SearchCriteria;
import am.ik.blog.entry.Category;
import am.ik.blog.entry.Entry;
import am.ik.blog.entry.EntryId;
import am.ik.blog.entry.Tag;

@RestController
@CrossOrigin
public class EntryController {
  private final EntryRepository entryRepository;
  private static final String DEFAULT_EXCLUDE_CONTENT = "false";

  public EntryController(EntryRepository entryRepository) {
    this.entryRepository = entryRepository;
  }

  @GetMapping("entries")
  public List<Entry> getEntries(
      @RequestParam(defaultValue = DEFAULT_EXCLUDE_CONTENT) boolean excludeContent) {
    SearchCriteria criteria = SearchCriteria.builder().excludeContent(excludeContent).build();
    return entryRepository.findAll(criteria);
  }

  @GetMapping(path = "entries", params = "q")
  public List<Entry> searchEntries(@RequestParam String q,
      @RequestParam(defaultValue = DEFAULT_EXCLUDE_CONTENT) boolean excludeContent) {
    SearchCriteria criteria =
        SearchCriteria.builder().excludeContent(excludeContent).keyword(q).build();
    return entryRepository.findAll(criteria);
  }

  @GetMapping("entries/{entryId}")
  public Entry getEntry(@PathVariable EntryId entryId,
      @RequestParam(defaultValue = DEFAULT_EXCLUDE_CONTENT) boolean excludeContent) {
    return entryRepository.findById(entryId, excludeContent)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
            "EntryId " + entryId + " is not found."));
  }

  @GetMapping("categories/{categories}/entries")
  public List<Entry> getEntriesByCategories(@PathVariable List<Category> categories,
      @RequestParam(defaultValue = DEFAULT_EXCLUDE_CONTENT) boolean excludeContent) {
    int order = categories.size() - 1;
    Category category = categories.get(order);
    SearchCriteria criteria =
        SearchCriteria.builder().categoryOrders(new CategoryOrders().add(category, order))
            .excludeContent(excludeContent).build();
    return entryRepository.findAll(criteria);
  }

  @GetMapping("tags/{tag}/entries")
  public List<Entry> getEntriesByTag(@PathVariable Tag tag,
      @RequestParam(defaultValue = DEFAULT_EXCLUDE_CONTENT) boolean excludeContent) {
    SearchCriteria criteria =
        SearchCriteria.builder().tag(tag).excludeContent(excludeContent).build();
    return entryRepository.findAll(criteria);
  }
}
