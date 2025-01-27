package mvcTest;

import muravin.configurations.DataSourceConfiguration;
import muravin.configurations.WebConfiguration;
import muravin.model.Post;
import muravin.repositories.CommentsRepository;
import muravin.repositories.PostsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(classes = {DataSourceConfiguration.class, WebConfiguration.class})
@WebAppConfiguration
@TestPropertySource(locations = "classpath:application.properties")
class PostsControllerTest {

    @Autowired
    private PostsRepository postsRepository;
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    @Autowired
    private CommentsRepository commentsRepository;

    @BeforeEach
    @Transactional()
    void setUp() {
        System.out.println("setUp");
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        // Очистка и заполнение тестовых данных в базе
        commentsRepository.deleteAll();
        postsRepository.deleteAll();
        var post4 = getPost(4L);
        post4.setId(null);
        var post5 = getPost(5L);
        post5.setId(null);
        postsRepository.save(post4);
        postsRepository.save(post5);
    }

    private static Post getPost(Long id) {
        return new Post(id, "post" + id, "123", "123", new ArrayList<>(), 0, "");
    }

    @Test
    void getPosts_shouldReturnHtmlWithPosts() throws Exception {
        mockMvc.perform(get("/posts?pageSize=10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("posts"))
                .andExpect(model().attributeExists("posts"))
                .andExpect(xpath("//body/div/h2").nodeCount(2))
                .andExpect(xpath("//div[@class='shadow post'][2]/h2[1]/a[1]/text()[1]").string("post5"));
    }
    @Test
    void save_shouldAddPostToDbAndRedirectToIndex() throws Exception {
        mockMvc.perform(multipart("/posts")
                .file("image",null)
                .param("title", "title")
                .param("content", "content")
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts"));
    }
    @Test void getAddPostShouldReturnView() throws Exception {
        mockMvc.perform(get("/posts/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("posts/add"));
    }

    @Test void getWithIdShouldReturnView() throws Exception {
        mockMvc.perform(get("/posts/{id}", 14L))
                .andExpect(status().isOk())
                .andExpect(view().name("posts/show"))
                .andExpect(model().attributeExists("post"));
    }
    @Test void deletePostShouldRedirectToIndex() throws Exception {
        mockMvc.perform(delete("/posts/{id}", 5))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts"));
        assertFalse(postsRepository.existsById(5L));
    }
    @Test void addPostShouldSavePostAndRedirectToIndex() throws Exception {
        var id = postsRepository.findAll().getLast().getId()+1;
        var post = getPost(id);
        post.setId(null);
        mockMvc.perform(multipart("/posts")
                        .file("image",null)
                        .param("title", post.getTitle()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts"));
        assertTrue(postsRepository.existsById(id));
    }

    @Test void updatePostShouldSavePostAndRedirectToIndex() throws Exception {
        MockMultipartFile file = new MockMultipartFile("data", "dummy.csv",
                "text/plain", "123".getBytes());
        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/posts/19");
        builder.with(new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setMethod("PATCH");
                return request;
            }
        });
        var post = postsRepository.findById(19L).get();
        post.setId(null);
        var oldTitle = post.getTitle();
        mockMvc.perform(builder.file("image", file.getBytes())
                //.file("image", "MTIz".getBytes())
                .param("title", post.getTitle()+"abcde")
                        //.param("_method","PATCH")
                .param("content","123")
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts"));
        post = postsRepository.findById(19L).get();
        assertTrue(postsRepository.existsById(19L));
        assertEquals("MTIz", post.getPictureBase64());
        assertEquals(oldTitle+"abcde", post.getTitle());
    }
}
