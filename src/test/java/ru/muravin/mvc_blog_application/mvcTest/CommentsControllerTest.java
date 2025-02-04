package ru.muravin.mvc_blog_application.mvcTest;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.muravin.mvc_blog_application.controllers.CommentsController;
import ru.muravin.mvc_blog_application.model.Comment;
import ru.muravin.mvc_blog_application.model.Post;
import ru.muravin.mvc_blog_application.repositories.CommentsRepository;
import ru.muravin.mvc_blog_application.repositories.LikesRepository;
import ru.muravin.mvc_blog_application.repositories.PostsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import ru.muravin.mvc_blog_application.repositories.TagsRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class CommentsControllerTest {
    @Autowired
    private CommentsRepository commentsRepository;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PostsRepository postsRepository;
    @Autowired
    private LikesRepository likesRepository;
    @Autowired
    private TagsRepository tagsRepository;

    @BeforeEach
    void setUp() {
        likesRepository.deleteAll();
        tagsRepository.deleteAll();
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
