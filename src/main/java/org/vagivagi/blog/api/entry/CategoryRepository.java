package org.vagivagi.blog.api.entry;

import java.util.List;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import am.ik.blog.entry.Categories;

@Repository
public class CategoryRepository {
  private final NamedParameterJdbcTemplate jdbcTemplate;

  public CategoryRepository(NamedParameterJdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Transactional(readOnly = true)
  public List<Categories> findAll() {
    return this.jdbcTemplate.query(
        "SELECT DISTINCT GROUP_CONCAT(DISTINCT category_name ORDER BY category_order ASC SEPARATOR ',') category"
            + " FROM category" //
            + " GROUP BY entry_id" //
            + " ORDER BY category",
        CategoryExtractors.forCategories());
  }
}
