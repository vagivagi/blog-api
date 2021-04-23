package org.vagivagi.blog.api.entry;

import am.ik.blog.entry.Categories;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class CategoryRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public CategoryRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly = true)
    public List<Categories> findAll() {
        return this.jdbcTemplate.query(
                "SELECT DISTINCT ARRAY_TO_STRING(ARRAY(SELECT category_name FROM category WHERE category.entry_id = e.entry_id ORDER BY category_order ASC), ',') AS category FROM entry AS e ORDER BY "
                        + "category",
                CategoryExtractors.forCategories());
    }
}
