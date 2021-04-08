package org.vagivagi.blog.api.entry;

import am.ik.blog.entry.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class TagRepositoryTest {
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @Sql("/testfiles/test_data_entry.sql")
    public void success_findAll() {
        List<Tag> tags = tagRepository.findAll();
        assertAll(
                () -> assertThat(tags.size()).isEqualTo(4),
                () -> assertThat(tags.get(0)).isEqualTo(new Tag("blog")),
                () -> assertThat(tags.get(1)).isEqualTo(new Tag("demo")),
                () -> assertThat(tags.get(2)).isEqualTo(new Tag("food")),
                () -> assertThat(tags.get(3)).isEqualTo(new Tag("protein"))
        );
    }

    @Test
    @Sql("/testfiles/test_data_no_entry.sql")
    public void success_findAll_no_record() {
        assertThat(tagRepository.findAll().isEmpty()).isEqualTo(true);
    }
}
