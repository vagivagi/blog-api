package org.vagivagi.blog.api.entry;

import am.ik.blog.entry.Tag;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.flyway.enabled=false",
                "blog.github.access-token=foo",
                "blog.github.webhook-secret=bar"
        })
public class TagControllerTest {
    @LocalServerPort
    int port;
    WebTestClient webClient;
    @MockBean
    TagRepository tagRepository;

    @Before
    public void setup() {
        this.webClient = WebTestClient.bindToServer() //
                .baseUrl("http://localhost:" + port + "/api") //
                .build();
    }

    @Test
    public void getTags() throws Exception {
        given(tagRepository.findAll())
                .willReturn(List.of(new Tag("demo"), new Tag("blog")));
        this.webClient
                .get() //
                .uri("/tags") //
                .accept(MediaType.APPLICATION_JSON) //
                .exchange() //
                .expectStatus() //
                .isOk() //
                .expectBody() //
                .jsonPath("$")
                .isArray() //
                .jsonPath("$[0].value")
                .isEqualTo("demo")
                .jsonPath("$[1].value")
                .isEqualTo("blog");
    }

    @Test
    public void getTags_not_found() throws Exception {
        given(tagRepository.findAll())
                .willReturn(List.of());
        this.webClient
                .get() //
                .uri("/tags") //
                .accept(MediaType.APPLICATION_JSON) //
                .exchange() //
                .expectStatus() //
                .isOk() //
                .expectBody() //
                .jsonPath("$")
                .isEmpty();
    }
}
