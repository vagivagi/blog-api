package org.vagivagi.blog.api.entry;

import am.ik.blog.entry.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.vagivagi.blog.api.entry.criteria.SearchCriteria;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class EntryRepositoryTest {
    @Autowired
    private EntryRepository entryRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Nested
    @ExtendWith(SpringExtension.class)
    @SpringBootTest
    @Sql("/testfiles/test_data_entry.sql")
    class findById {
        @Test
        public void success_include_content() {
            entryRepository
                    .findById(new EntryId("1"), false)
                    .ifPresentOrElse(
                            entry -> {
                                assertAllForEntry(entry);
                                assertThat(entry.getContent()).isEqualTo(new Content("Content"));
                            },
                            () -> {
                                fail("entry is not found.");
                            });
        }

        @Test
        public void success_exclude_content() {
            entryRepository
                    .findById(new EntryId("1"), true)
                    .ifPresentOrElse(
                            entry -> {
                                assertAllForEntry(entry);
                                assertThat(entry.getContent()).isEqualTo(new Content(""));
                            },
                            () -> {
                                fail("entry is not found.");
                            });
        }

        @Test
        public void not_found_include() {
            entryRepository
                    .findById(new EntryId("99"), false)
                    .ifPresent(
                            entry -> {
                                fail("entry is found.");
                            });
        }

        @Test
        public void not_found_exclude() {
            entryRepository
                    .findById(new EntryId("99"), true)
                    .ifPresent(
                            entry -> {
                                fail("entry is found.");
                            });
        }
    }

    @Test
    @Sql("/testfiles/test_data_entry.sql")
    public void entryIds() {
        SearchCriteria searchCriteria = SearchCriteria.builder().keyword("Content").build();
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValues(searchCriteria.toWhereClause().params());
        assertThat(entryRepository.entryIds(searchCriteria, searchCriteria.toWhereClause(), source)).isEqualTo(List.of(2L, 1L));
    }

    @Test
    public void sqlForEntries_exclude() {
        assertThat(entryRepository.sqlForEntries(true)).isEqualTo("SELECT e.entry_id, e.title"
                + ", e.created_by, e.created_date, e.last_modified_by, e.last_modified_date, c.category_name"
                + " FROM entry AS e LEFT JOIN category AS c ON e.entry_id = c.entry_id "
                + " WHERE e.entry_id IN (:entry_ids)"
                + " ORDER BY e.last_modified_date DESC, e.entry_id DESC, c.category_order ASC");
    }

    @Test
    public void sqlForEntries_include() {
        assertThat(entryRepository.sqlForEntries(false)).isEqualTo("SELECT e.entry_id, e.title, e.content"
                + ", e.created_by, e.created_date, e.last_modified_by, e.last_modified_date, c.category_name"
                + " FROM entry AS e LEFT JOIN category AS c ON e.entry_id = c.entry_id "
                + " WHERE e.entry_id IN (:entry_ids)"
                + " ORDER BY e.last_modified_date DESC, e.entry_id DESC, c.category_order ASC");
    }

    @Nested
    @ExtendWith(SpringExtension.class)
    @SpringBootTest
    @Sql("/testfiles/test_data_entry.sql")
    class findByAll {
        @Test
        public void success_exclude_content() {
            SearchCriteria searchCriteria = SearchCriteria.builder().excludeContent(true).build();
            List<Entry> entries = entryRepository.findAll(searchCriteria);
            assertAll(
                    () -> assertThat(entries).hasSize(2),
                    () -> assertAllForEntry(entries.get(1)),
                    () -> assertThat(entries.get(1).getContent()).isEqualTo(new Content(""))
            );
        }

        @Test
        public void success_include_content() {
            SearchCriteria searchCriteria = SearchCriteria.builder().excludeContent(false).build();
            List<Entry> entries = entryRepository.findAll(searchCriteria);
            assertAll(
                    () -> assertThat(entries).hasSize(2),
                    () -> assertAllForEntry(entries.get(1)),
                    () -> assertThat(entries.get(1).getContent()).isEqualTo(new Content("Content"))
            );
        }

        @Test
        public void not_found() {
            SearchCriteria searchCriteria = SearchCriteria.builder().keyword("not found").build();
            List<Entry> entries = entryRepository.findAll(searchCriteria);
            assertThat(entries).hasSize(0);
        }
    }

    @Nested
    @ExtendWith(SpringExtension.class)
    @SpringBootTest
    @Sql("/testfiles/test_data_entry.sql")
    class create {
        @Test
        public void success() {
            EventTime now = EventTime.now();
            EventTime expectedNow = new EventTime(OffsetDateTime.from(now.getValue()).withOffsetSameLocal(ZoneOffset.UTC));
            Entry entry = Entry.builder()
                    .entryId(new EntryId("3"))
                    .content(new Content("content3"))
                    .frontMatter(new FrontMatter(new Title("title3"), new Categories(new Category("newCategory")), new Tags(new Tag("newTag")), now, now))
                    .created(new Author(new Name("author3"), now))
                    .updated(new Author(new Name("updater3"), now))
                    .build();
            entryRepository.create(entry);
            jdbcTemplate.query(
                    "SELECT e.entry_id, e.title, e.content, e.created_by, e.created_date, e.last_modified_by, e.last_modified_date, c.category_name"
                            + " FROM entry AS e LEFT OUTER JOIN category AS c ON e.entry_id = c.entry_id"
                            + " WHERE e.entry_id = 3" + " ORDER BY c.category_order ASC",
                    EntryExtractors.forEntry(false)) //
                    .map(e -> {
                        List<Tag> tags =
                                jdbcTemplate.query("SELECT tag_name FROM entry_tag WHERE entry_id = 3",
                                        (rs, i) -> new Tag(rs.getString("tag_name")));
                        FrontMatter fm = e.getFrontMatter();
                        return e.copy().frontMatter(
                                new FrontMatter(fm.title(), fm.categories(), new Tags(tags), fm.date(), fm.updated()))
                                .build();
                    }).ifPresentOrElse(actualEntry -> {
                assertAll(
                        () -> assertThat(actualEntry.getEntryId()).isEqualTo(new EntryId("3")),
                        () -> assertThat(actualEntry.getContent()).isEqualTo(new Content("content3")),
                        () -> assertThat(actualEntry.getFrontMatter()).isEqualTo(new FrontMatter(new Title("title3"), new Categories(new Category("category3")), new Tags(new Tag("tag3")), expectedNow, expectedNow)),
                        () -> assertThat(actualEntry.getCreated()).isEqualTo(new Author(new Name("author3"), expectedNow)),
                        () -> assertThat(actualEntry.getUpdated()).isEqualTo(new Author(new Name("updater3"), expectedNow))
                );
            }, () -> {
                fail("entry is not found");
            });
        }

        @Test
        public void success_duplicate_tag() {
            EventTime now = EventTime.now();
            EventTime expectedNow = new EventTime(OffsetDateTime.from(now.getValue()).withOffsetSameLocal(ZoneOffset.UTC));
            Entry entry = Entry.builder()
                    .entryId(new EntryId("3"))
                    .content(new Content("content3"))
                    .frontMatter(new FrontMatter(new Title("title3"), new Categories(new Category("category3")), new Tags(new Tag("demo")), now, now))
                    .created(new Author(new Name("author3"), now))
                    .updated(new Author(new Name("updater3"), now))
                    .build();
            entryRepository.create(entry);
            jdbcTemplate.query(
                    "SELECT e.entry_id, e.title, e.content, e.created_by, e.created_date, e.last_modified_by, e.last_modified_date, c.category_name"
                            + " FROM entry AS e LEFT OUTER JOIN category AS c ON e.entry_id = c.entry_id"
                            + " WHERE e.entry_id = 3" + " ORDER BY c.category_order ASC",
                    EntryExtractors.forEntry(false)) //
                    .map(e -> {
                        List<Tag> tags =
                                jdbcTemplate.query("SELECT tag_name FROM entry_tag WHERE entry_id = 3",
                                        (rs, i) -> new Tag(rs.getString("tag_name")));
                        FrontMatter fm = e.getFrontMatter();
                        return e.copy().frontMatter(
                                new FrontMatter(fm.title(), fm.categories(), new Tags(tags), fm.date(), fm.updated()))
                                .build();
                    }).ifPresentOrElse(actualEntry -> {
                assertAll(
                        () -> assertThat(actualEntry.getEntryId()).isEqualTo(new EntryId("3")),
                        () -> assertThat(actualEntry.getContent()).isEqualTo(new Content("content3")),
                        () -> assertThat(actualEntry.getFrontMatter()).isEqualTo(new FrontMatter(new Title("title3"), new Categories(new Category("category3")), new Tags(new Tag("tag3")), expectedNow, expectedNow)),
                        () -> assertThat(actualEntry.getCreated()).isEqualTo(new Author(new Name("author3"), expectedNow)),
                        () -> assertThat(actualEntry.getUpdated()).isEqualTo(new Author(new Name("updater3"), expectedNow))
                );
            }, () -> {
                fail("entry is not found");
            });
        }

        @Test
        public void success_no_category_no_tag() {
            EventTime now = EventTime.now();
            EventTime expectedNow = new EventTime(OffsetDateTime.from(now.getValue()).withOffsetSameLocal(ZoneOffset.UTC));
            Entry entry = Entry.builder()
                    .entryId(new EntryId("3"))
                    .content(new Content("content3"))
                    .frontMatter(new FrontMatter(new Title("title3"), new Categories(), new Tags(), now, now))
                    .created(new Author(new Name("author3"), now))
                    .updated(new Author(new Name("updater3"), now))
                    .build();
            entryRepository.create(entry);
            jdbcTemplate.query(
                    "SELECT e.entry_id, e.title, e.content, e.created_by, e.created_date, e.last_modified_by, e.last_modified_date, c.category_name"
                            + " FROM entry AS e LEFT OUTER JOIN category AS c ON e.entry_id = c.entry_id"
                            + " WHERE e.entry_id = 3" + " ORDER BY c.category_order ASC",
                    EntryExtractors.forEntry(false)) //
                    .map(e -> {
                        List<Tag> tags =
                                jdbcTemplate.query("SELECT tag_name FROM entry_tag WHERE entry_id = 3",
                                        (rs, i) -> new Tag(rs.getString("tag_name")));
                        FrontMatter fm = e.getFrontMatter();
                        return e.copy().frontMatter(
                                new FrontMatter(fm.title(), fm.categories(), new Tags(tags), fm.date(), fm.updated()))
                                .build();
                    }).ifPresentOrElse(actualEntry -> {
                assertAll(
                        () -> assertThat(actualEntry.getEntryId()).isEqualTo(new EntryId("3")),
                        () -> assertThat(actualEntry.getContent()).isEqualTo(new Content("content3")),
                        () -> assertThat(actualEntry.getFrontMatter()).isEqualTo(new FrontMatter(new Title("title3"), new Categories(), new Tags(), expectedNow, expectedNow)),
                        () -> assertThat(actualEntry.getCreated()).isEqualTo(new Author(new Name("author3"), expectedNow)),
                        () -> assertThat(actualEntry.getUpdated()).isEqualTo(new Author(new Name("updater3"), expectedNow))
                );
            }, () -> {
                fail("entry is not found");
            });
        }
    }

    @Nested
    @ExtendWith(SpringExtension.class)
    @SpringBootTest
    @Sql("/testfiles/test_data_entry.sql")
    class update {
        @Test
        public void update() {
            EventTime now = EventTime.now();
            EventTime expectedNow = new EventTime(OffsetDateTime.from(now.getValue()).withOffsetSameLocal(ZoneOffset.UTC));
            Entry entry = Entry.builder()
                    .entryId(new EntryId("1"))
                    .content(new Content("updatedContent"))
                    .frontMatter(new FrontMatter(new Title("updatedTitle"), new Categories(), new Tags(), now, now))
                    .created(new Author(new Name("updatedAuthor"), now))
                    .updated(new Author(new Name("updatedUpdater"), now))
                    .build();
            entryRepository.update(entry);
            jdbcTemplate.query(
                    "SELECT e.entry_id, e.title, e.content, e.created_by, e.created_date, e.last_modified_by, e.last_modified_date, c.category_name"
                            + " FROM entry AS e LEFT OUTER JOIN category AS c ON e.entry_id = c.entry_id"
                            + " WHERE e.entry_id = 1" + " ORDER BY c.category_order ASC",
                    EntryExtractors.forEntry(false)) //
                    .map(e -> {
                        List<Tag> tags =
                                jdbcTemplate.query("SELECT tag_name FROM entry_tag WHERE entry_id = 1",
                                        (rs, i) -> new Tag(rs.getString("tag_name")));
                        FrontMatter fm = e.getFrontMatter();
                        return e.copy().frontMatter(
                                new FrontMatter(fm.title(), fm.categories(), new Tags(tags), fm.date(), fm.updated()))
                                .build();
                    }).ifPresentOrElse(actualEntry -> {
                assertAll(
                        () -> assertThat(actualEntry.getEntryId()).isEqualTo(new EntryId("1")),
                        () -> assertThat(actualEntry.getContent()).isEqualTo(new Content("updatedContent")),
                        () -> assertThat(actualEntry.getFrontMatter()).isEqualTo(new FrontMatter(new Title("updatedTitle"), new Categories(), new Tags(), expectedNow, expectedNow)),
                        () -> assertThat(actualEntry.getCreated()).isEqualTo(new Author(new Name("updatedAuthor"), expectedNow)),
                        () -> assertThat(actualEntry.getUpdated()).isEqualTo(new Author(new Name("updatedUpdater"), expectedNow))
                );
            }, () -> {
                fail("entry is not found");
            });
        }
    }


    @Test
    public void success_delete() {
        entryRepository.delete(new EntryId("1"));
        jdbcTemplate.query(
                "SELECT e.entry_id, e.title, e.content, e.created_by, e.created_date, e.last_modified_by, e.last_modified_date, c.category_name"
                        + " FROM entry AS e LEFT OUTER JOIN category AS c ON e.entry_id = c.entry_id"
                        + " WHERE e.entry_id = 1" + " ORDER BY c.category_order ASC",
                EntryExtractors.forEntry(false)) //
                .ifPresent(actualEntry -> {
                    fail("entry is not deleted");
                });
    }

    @Test
    public void success_delete_no_record() {
        entryRepository.delete(new EntryId("99"));
        jdbcTemplate.query(
                "SELECT e.entry_id, e.title, e.content, e.created_by, e.created_date, e.last_modified_by, e.last_modified_date, c.category_name"
                        + " FROM entry AS e LEFT OUTER JOIN category AS c ON e.entry_id = c.entry_id"
                        + " WHERE e.entry_id = 99" + " ORDER BY c.category_order ASC",
                EntryExtractors.forEntry(false)) //
                .ifPresent(actualEntry -> {
                    fail("entry is not deleted");
                });
    }

    @Test
    public void success_tagsMap_multiple() {
        entryRepository.tagsMap(List.of(1l)).forEach(
                (entryId, tags) -> {
                    assertAll(
                            () -> assertThat(entryId).isEqualTo(new EntryId(1l)),
                            () -> assertThat(tags).isEqualTo(new Tags(new Tag("blog"), new Tag("demo")))
                    );
                }
        );
    }

    @Test
    public void success_tagsMap_none() {
        assertThat(entryRepository.tagsMap(List.of(99l)).isEmpty()).isTrue();
    }

    private void assertAllForEntry(Entry entry) {
        assertAll(
                "entry",
                () -> assertThat(entry.getEntryId()).isEqualTo(new EntryId("1")),
                () -> assertThat(entry.getFrontMatter().title()).isEqualTo(new Title("Title")),
                () ->
                        assertThat(entry.getFrontMatter().categories())
                                .isEqualTo(new Categories(new Category("demo"), new Category("Hello"))),
                () ->
                        assertThat(entry.getFrontMatter().tags())
                                .isEqualTo(new Tags(new Tag("blog"), new Tag("demo"))),
                () ->
                        assertThat(entry.getFrontMatter().date().getValue())
                                .isEqualTo(
                                        OffsetDateTime.of(LocalDateTime.of(2021, 3, 1, 21, 0, 0), ZoneOffset.UTC)),
                () ->
                        assertThat(entry.getFrontMatter().updated().getValue())
                                .isEqualTo(
                                        OffsetDateTime.of(LocalDateTime.of(2021, 3, 2, 22, 0, 0), ZoneOffset.UTC)),
                () ->
                        assertThat(entry.getCreated())
                                .isEqualTo(
                                        new Author(
                                                new Name("author"),
                                                new EventTime(
                                                        OffsetDateTime.of(
                                                                LocalDateTime.of(2021, 3, 1, 21, 0, 0), ZoneOffset.UTC)))),
                () ->
                        assertThat(entry.getUpdated())
                                .isEqualTo(
                                        new Author(
                                                new Name("updater"),
                                                new EventTime(
                                                        OffsetDateTime.of(
                                                                LocalDateTime.of(2021, 3, 2, 22, 0, 0), ZoneOffset.UTC)))));
    }
}
