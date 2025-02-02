package ru.muravin.mvc_blog_application.mvcTest;

import ru.muravin.mvc_blog_application.configurations.DataSourceConfiguration;
import ru.muravin.mvc_blog_application.configurations.WebConfiguration;
import ru.muravin.mvc_blog_application.model.Post;
import ru.muravin.mvc_blog_application.repositories.LikesRepository;
import ru.muravin.mvc_blog_application.repositories.PostsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitConfig(classes = {DataSourceConfiguration.class, WebConfiguration.class})
@WebAppConfiguration
@TestPropertySource(locations = "classpath:application.properties")
public class LikesControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    @Autowired
    private LikesRepository likesRepository;
    @Autowired
    private PostsRepository postsRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        likesRepository.deleteAll();
        //postsRepository.deleteAll();
        var post = new Post();
        post.setTitle("Test Post");
        post.setContent("Test Content");
        postsRepository.save(post);
    }
    @Test void postShouldAddLike() throws Exception {
        var id = postsRepository.findAll().get(0).getId();
        mockMvc.perform(post("/likes")
                .param("post", id.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + id.toString()));
        assertEquals(1, likesRepository.count());
        assertEquals(id, likesRepository.findAll().get(0).getPost().getId());
    }
}
