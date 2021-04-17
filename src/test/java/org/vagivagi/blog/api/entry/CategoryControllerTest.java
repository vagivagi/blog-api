package org.vagivagi.blog.api.entry;

import am.ik.blog.entry.Categories;
import am.ik.blog.entry.Category;
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
public class CategoryControllerTest {
    @LocalServerPort
    int port;
    WebTestClient webClient;
    @MockBean
    CategoryRepository categoryRepository;

    @Before
    public void setup() {
        this.webClient = WebTestClient.bindToServer() //
                .baseUrl("http://localhost:" + port + "/api") //
                .build();
    }

    @Test
    public void getCategories() throws Exception {
        given(categoryRepository.findAll())
                .willReturn(List.of(new Categories(new Category("categories"), new Category("demo"))));
        this.webClient
                .get() //
                .uri("/categories") //
                .accept(MediaType.APPLICATION_JSON) //
                .exchange() //
                .expectStatus() //
                .isOk() //
                .expectBody() //
                .jsonPath("$")
                .isEqualTo("{categories=[\"categories\",\"demo\"]}");
    }

    @Test
    public void getCategories_not_found() throws Exception {
        given(categoryRepository.findAll())
                .willReturn(List.of());
        this.webClient
                .get() //
                .uri("/categories") //
                .accept(MediaType.APPLICATION_JSON) //
                .exchange() //
                .expectStatus() //
                .isOk() //
                .expectBody() //
                .jsonPath("$")
                .isEmpty();
    }

}
