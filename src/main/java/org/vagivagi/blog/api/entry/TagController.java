package org.vagivagi.blog.api.entry;

import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import am.ik.blog.entry.Tag;

@RestController
@CrossOrigin
public class TagController {
  private final TagRepository tagRepository;

  public TagController(TagRepository tagRepository) {
    this.tagRepository = tagRepository;
  }

  @GetMapping("tags")
  public List<Tag> getCategories() {
    return tagRepository.findAll();
  }
}
