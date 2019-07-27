package org.vagivagi.blog.api.entry;

import java.util.ArrayList;
import java.util.List;
import org.springframework.jdbc.core.ResultSetExtractor;
import am.ik.blog.entry.Tag;

class TagExtractors {

  static ResultSetExtractor<List<Tag>> forTags() {
    return rs -> {
      List<Tag> tags = new ArrayList<>();
      if (rs.next()) {
        do {
          tags.add(new Tag(rs.getString("tag_name")));
        } while (rs.next());
      }
      return tags;
    };
  }
}
