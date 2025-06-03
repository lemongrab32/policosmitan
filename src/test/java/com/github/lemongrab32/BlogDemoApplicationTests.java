package com.github.lemongrab32;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lemongrab32.model.dto.ArticleRequest;
import com.github.lemongrab32.repository.ArticleRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Integration-level testing for BlogDemo application")
class BlogDemoApplicationTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ArticleRepository articleRepository;

    private final String APP_URL = "http://localhost:8082/api/v1/articles";

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.hikari.datasource.username", postgresContainer::getUsername);
        registry.add("spring.hikari.datasource.password", postgresContainer::getPassword);
        registry.add("spring.jpa.generate-ddl", () -> true);
    }

    @BeforeEach
    void init() throws Exception {
        ArticleRequest articleRequest = new ArticleRequest(
                "Test title", "Test short description", "Test author", "Test content",
                Set.of("tag1", "tag2", "tag3")
        );
        String json = mapper.writeValueAsString(articleRequest);

        mvc.perform(
                MockMvcRequestBuilders.post(APP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)

        ).andExpect(status().isCreated());
    }

    @AfterEach
    void cleanUp() {
        articleRepository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("Получение одной статьи по id")
    void testGetArticleById() throws Exception {
        var expectedArticle = articleRepository.findById(1L).orElse(null);

        mvc.perform(
                MockMvcRequestBuilders.get(APP_URL + "/1")
        )
                .andExpect(status().isOk())
                .andExpect(
                        content().json(mapper.writeValueAsString(expectedArticle))
                );
    }

    @Test
    @DisplayName("Получение всех статей")
    void testGetAllArticles() throws Exception {
        var expectedArticles = articleRepository.findAll();

        mvc.perform(
                MockMvcRequestBuilders.get(APP_URL)
        )
                .andExpect(status().isOk())
                .andExpect(
                        content().json(mapper.writeValueAsString(expectedArticles))
                );
    }

    @Test
    @DisplayName("Обновление информации статьи")
    void testUpdateArticle() throws Exception {
        var expectedArticle = articleRepository.findAll().getFirst();

        String newTitle = "Updated " + expectedArticle.getTitle().toLowerCase();
        expectedArticle.setTitle(newTitle);

        Map<String, String> updates = new HashMap<>();
        updates.put("title", newTitle);

        mvc.perform(
                MockMvcRequestBuilders.patch(APP_URL + "/" + expectedArticle.getId())
                        .content(mapper.writeValueAsString(updates))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
        var updatedArticle = articleRepository.findById(expectedArticle.getId()).orElse(null);

        assertNotNull(updatedArticle);
        assertEquals(expectedArticle, updatedArticle);
    }

    @Test
    @DisplayName("Удаление статьи")
    void testDeleteArticle() throws Exception {
        var article = articleRepository.findAll().getFirst();

        mvc.perform(
                MockMvcRequestBuilders.delete(APP_URL + "/" + article.getId())
        ).andExpect(status().isOk());

        assertTrue(articleRepository.findById(article.getId()).isEmpty());
    }

    @Test
    @DisplayName("Фильтрация по заголовку")
    void testCommonTitleFiltration() throws Exception {
        var article = articleRepository.findAll().getFirst();

        mvc.perform(
                MockMvcRequestBuilders.get(APP_URL)
                        .param("search", "title:title")
        )
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(Set.of(article))));
    }

    @Test
    @DisplayName("Фильтрация по заголовку")
    void testWrongTitleFiltration() throws Exception {
        mvc.perform(
                        MockMvcRequestBuilders.get(APP_URL)
                                .param("search", "title:509")
                )
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(Set.of())));
        System.out.println();
    }

}
