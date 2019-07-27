package org.vagivagi.blog.api.entry;

import java.util.List;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import am.ik.blog.entry.Tag;

@Repository
public class TagRepository {
  private final NamedParameterJdbcTemplate jdbcTemplate;

  public TagRepository(NamedParameterJdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Transactional(readOnly = true)
  public List<Tag> findAll() {
    return this.jdbcTemplate.query(
        "SELECT tag_name FROM tag ORDER BY tag_name ASC",
        TagExtractors.forTags());
  }
}
