package org.vagivagi.blog.api.entry;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.jdbc.core.ResultSetExtractor;
import am.ik.blog.entry.Categories;
import am.ik.blog.entry.Category;

class CategoryExtractors {

  static ResultSetExtractor<List<Categories>> forCategories() {
    return rs -> {
      List<Categories> categories = new ArrayList<>();
      if (rs.next()) {
        do {
          categories.add(new Categories(Stream.of(rs.getString("category").split(","))
              .map(Category::new).collect(Collectors.toList())));
        } while (rs.next());
      }
      return categories;
    };
  }
}
