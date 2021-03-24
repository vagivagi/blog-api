package org.vagivagi.blog.api.entry;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.*;
import java.util.Collections;
import java.util.Optional;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.vagivagi.blog.api.Fixtures;
import org.vagivagi.blog.api.entry.criteria.SearchCriteria;
import am.ik.blog.entry.EntryId;

@RunWith(SpringRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
      "spring.flyway.enabled=false",
      "blog.github.access-token=foo",
      "blog.github.webhook-secret=bar"
    })
public class EntryControllerTest {
  @LocalServerPort int port;
  WebTestClient webClient;
  @MockBean EntryRepository entryRepository;

  @Before
  public void setup() {
    this.webClient = WebTestClient.bindToServer() //
        .baseUrl("http://localhost:" + port+ "/api") //
        .build();
  }

  @Test
  public void getEntry_NotFound() throws Exception {
    this.webClient
        .get() //
        .uri("/entries/999") //
        .accept(MediaType.APPLICATION_JSON) //
        .exchange() //
        .expectStatus() //
        .isNotFound() //
        .expectBody() //
        .jsonPath("message")
        .isEqualTo("EntryId 999 is not found.");
  }

  @Test
  public void getEntry() throws Exception {
    EntryId entryId = new EntryId("100");
    given(entryRepository.findById(entryId, false))
        .willReturn(Optional.of(Fixtures.entry(entryId)));

    this.webClient
        .get() //
        .uri("/entries/100") //
        .accept(MediaType.APPLICATION_JSON) //
        .exchange() //
        .expectStatus() //
        .isOk() //
        .expectBody() //
        .jsonPath("entryId")
        .isEqualTo(100) //
        .jsonPath("content")
        .isEqualTo("Hello") //
        .jsonPath("entryId")
        .isEqualTo(100) //
        .jsonPath("content")
        .isEqualTo("Hello") //
        .jsonPath("created")
        .isMap() //
        .jsonPath("created.name")
        .isEqualTo("demo") //
        .jsonPath("created.date")
        .isNotEmpty() //
        .jsonPath("updated")
        .isMap() //
        .jsonPath("updated.name")
        .isEqualTo("demo") //
        .jsonPath("updated.date")
        .isNotEmpty() //
        .jsonPath("frontMatter")
        .isMap() //
        .jsonPath("frontMatter.title")
        .isEqualTo("Hello") //
        .jsonPath("frontMatter.categories")
        .isArray() //
        .jsonPath("frontMatter.categories[0]")
        .isEqualTo("foo") //
        .jsonPath("frontMatter.categories[1]")
        .isEqualTo("bar") //
        .jsonPath("frontMatter.categories[2]")
        .isEqualTo("hoge") //
        .jsonPath("frontMatter.tags")
        .isArray() //
        .jsonPath("frontMatter.tags[0]")
        .isEqualTo("a") //
        .jsonPath("frontMatter.tags[1]")
        .isEqualTo("b") //
        .jsonPath("frontMatter.tags[2]")
        .isEqualTo("c");
  }

  @Test
  public void getEntries() throws Exception {
    given(entryRepository.findAll(any(SearchCriteria.class)))
        .willReturn(Collections.singletonList(Fixtures.entry(new EntryId("100"))));

    this.webClient
        .get() //
        .uri("/entries") //
        .accept(MediaType.APPLICATION_JSON) //
        .exchange() //
        .expectStatus() //
        .isOk() //
        .expectBody() //
        .jsonPath("$")
        .isArray() //
        .jsonPath("$[0].entryId")
        .isEqualTo(100) //
        .jsonPath("$[0].content")
        .isEqualTo("Hello") //
        .jsonPath("$[0].created")
        .isMap() //
        .jsonPath("$[0].created.name")
        .isEqualTo("demo") //
        .jsonPath("$[0].created.date")
        .isNotEmpty() //
        .jsonPath("$[0].updated")
        .isMap() //
        .jsonPath("$[0].updated.name")
        .isEqualTo("demo") //
        .jsonPath("$[0].updated.date")
        .isNotEmpty() //
        .jsonPath("$[0].frontMatter")
        .isMap() //
        .jsonPath("$[0].frontMatter.title")
        .isEqualTo("Hello") //
        .jsonPath("$[0].frontMatter.categories")
        .isArray() //
        .jsonPath("$[0].frontMatter.categories[0]")
        .isEqualTo("foo") //
        .jsonPath("$[0].frontMatter.categories[1]")
        .isEqualTo("bar") //
        .jsonPath("$[0].frontMatter.categories[2]")
        .isEqualTo("hoge") //
        .jsonPath("$[0].frontMatter.tags")
        .isArray() //
        .jsonPath("$[0].frontMatter.tags[0]")
        .isEqualTo("a") //
        .jsonPath("$[0].frontMatter.tags[1]")
        .isEqualTo("b") //
        .jsonPath("$[0].frontMatter.tags[2]")
        .isEqualTo("c");
  }

  @Test
  public void searchEntries() throws Exception {
    ArgumentCaptor<SearchCriteria> searchCriteriaArgumentCaptor =
        ArgumentCaptor.forClass(SearchCriteria.class);
    given(entryRepository.findAll(any(SearchCriteria.class)))
        .willReturn(Collections.singletonList(Fixtures.entry(new EntryId("100"))));

    this.webClient
        .get() //
        .uri(uriBuilder -> uriBuilder.path("entries").queryParam("q", "Hello").build()) //
        .accept(MediaType.APPLICATION_JSON) //
        .exchange() //
        .expectStatus() //
        .isOk() //
        .expectBody() //
        .jsonPath("$")
        .isArray() //
        .jsonPath("$[0].entryId")
        .isEqualTo(100) //
        .jsonPath("$[0].content")
        .isEqualTo("Hello") //
        .jsonPath("$[0].created")
        .isMap() //
        .jsonPath("$[0].created.name")
        .isEqualTo("demo") //
        .jsonPath("$[0].created.date")
        .isNotEmpty() //
        .jsonPath("$[0].updated")
        .isMap() //
        .jsonPath("$[0].updated.name")
        .isEqualTo("demo") //
        .jsonPath("$[0].updated.date")
        .isNotEmpty() //
        .jsonPath("$[0].frontMatter")
        .isMap() //
        .jsonPath("$[0].frontMatter.title")
        .isEqualTo("Hello") //
        .jsonPath("$[0].frontMatter.categories")
        .isArray() //
        .jsonPath("$[0].frontMatter.categories[0]")
        .isEqualTo("foo") //
        .jsonPath("$[0].frontMatter.categories[1]")
        .isEqualTo("bar") //
        .jsonPath("$[0].frontMatter.categories[2]")
        .isEqualTo("hoge") //
        .jsonPath("$[0].frontMatter.tags")
        .isArray() //
        .jsonPath("$[0].frontMatter.tags[0]")
        .isEqualTo("a") //
        .jsonPath("$[0].frontMatter.tags[1]")
        .isEqualTo("b") //
        .jsonPath("$[0].frontMatter.tags[2]")
        .isEqualTo("c");

    verify(entryRepository, times(1)).findAll(searchCriteriaArgumentCaptor.capture());
    SearchCriteria searchCriteria = searchCriteriaArgumentCaptor.getValue();
    assertThat(searchCriteria.getKeyword(), is("Hello"));
  }
}
