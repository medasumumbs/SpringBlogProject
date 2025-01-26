package mvcTest;

import muravin.configurations.DataSourceConfiguration;
import muravin.configurations.WebConfiguration;
import muravin.model.Post;
import muravin.repositories.PostsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;


@SpringJUnitConfig(classes = {DataSourceConfiguration.class, WebConfiguration.class})
@WebAppConfiguration
@TestPropertySource(locations = "classpath:application.properties")
class PostsControllerTest {

    @Autowired
    private PostsRepository postsRepository;
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    @Transactional(readOnly = false)
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        // Очистка и заполнение тестовых данных в базе
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
    void getUsers_shouldReturnHtmlWithPosts() throws Exception {
        mockMvc.perform(get("/posts?pageSize=10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("posts"))
                .andExpect(model().attributeExists("posts"))
                .andExpect(xpath("//body/div/h2").nodeCount(2))
                .andExpect(xpath("//div[@class='shadow post'][2]/h2[1]/a[1]/text()[1]").string("post5"));
    }
}
