package org.vagivagi.blog.api.entry;

import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import am.ik.blog.entry.Categories;

//TODO
@RestController
@CrossOrigin
public class CategoryController {
  private final CategoryRepository categoryRepository;

  public CategoryController(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  @GetMapping("categories")
  public List<Categories> getCategories() {
    return categoryRepository.findAll();
  }
}
