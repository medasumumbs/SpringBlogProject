package ru.muravin.mvc_blog_application.mvcTest;

import ru.muravin.mvc_blog_application.configurations.DataSourceConfiguration;
import ru.muravin.mvc_blog_application.configurations.WebConfiguration;
import ru.muravin.mvc_blog_application.model.Comment;
import ru.muravin.mvc_blog_application.model.Post;
import ru.muravin.mvc_blog_application.repositories.CommentsRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringJUnitConfig(classes = {DataSourceConfiguration.class, WebConfiguration.class})
@WebAppConfiguration
@TestPropertySource(locations = "classpath:application.properties")
public class CommentsControllerTest {
    @Autowired
    private CommentsRepository commentsRepository;
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    @Autowired
    private PostsRepository postsRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        commentsRepository.deleteAll();
        postsRepository.deleteAll();
        var post = new Post();
        post.setTitle("Test Post");
        post.setContent("Test Content");
        postsRepository.save(post);
    }
    @Test void postShouldAddComment() throws Exception {
        var id = postsRepository.findAll().get(0).getId();
        mockMvc.perform(post("/comments")
                .param("post", id.toString())
                .param("text", "Test Content"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + id.toString()));
        assertEquals(1, commentsRepository.count());
        var post = postsRepository.findAll().get(0);
        assertEquals(1, post.getCommentsCount());
        assertEquals("Test Content", post.getComments().get(0).getText());
    }
    @Test void patchShouldEditComment() throws Exception {
        var post = postsRepository.findAll().get(0);
        var postId = postsRepository.findAll().get(0).getId();
        var comment = new Comment(post, "asd");
        commentsRepository.save(comment);
        var commentId = comment.getId();
        mockMvc.perform(patch("/comments/" + commentId)
                .param("post", post.getId().toString())
                .param("text", "Test Content New"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + postId.toString()));
        assertEquals(1, commentsRepository.count());
        assertEquals("Test Content New", commentsRepository.findById(commentId).get().getText());
    }
}
