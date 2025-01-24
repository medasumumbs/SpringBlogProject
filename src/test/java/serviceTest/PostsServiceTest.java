package serviceTest;

import muravin.model.Comment;
import muravin.model.Post;
import muravin.repositories.CommentsRepository;
import muravin.repositories.LikesRepository;
import muravin.repositories.PostsRepository;
import muravin.repositories.TagsRepository;
import muravin.services.PostsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.Mockito.mock;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = PostsServiceTest.TestConfig.class)
public class PostsServiceTest {
    @Autowired
    private PostsService postsService;
    @Autowired
    private PostsRepository postsRepository;
    @Autowired
    private LikesRepository likesRepository;
    @Autowired
    private TagsRepository tagsRepository;

    @BeforeEach
    void setUp() {
        Mockito.reset(postsRepository, likesRepository, tagsRepository);
    }
    @Test
    void testFindPostById() {
        Post mockPost = new Post(
                1L,
                "Новый пост",
                "Content",
                "base64",
                new ArrayList<Comment>(),
                0,
                ""
        );
        mockPost.getComments().add(new Comment(mockPost,"12345"));
        Mockito.when(postsRepository.findById(1L)).thenReturn(Optional.of(mockPost));
        Optional<Post> post = postsService.findOne(1L);
        assertNotNull(post.get());
        assertEquals(1, mockPost.getComments().size());
        assertEquals(mockPost.getTitle(),post.get().getTitle());
        assertEquals(mockPost.getContent(),post.get().getContent());
        assertEquals(mockPost.getPictureBase64(),post.get().getPictureBase64());
        assertEquals(0, post.get().getLikesCount());
        assertEquals("Отсутствуют", post.get().getTagsString());
    }


    @Configuration
    public static class TestConfig {

        @Bean
        public PostsService userService(PostsRepository postsRepository, LikesRepository likesRepository, TagsRepository tagsRepository) {
            return new PostsService(postsRepository,likesRepository,tagsRepository);
        }

        @Bean
        public PostsRepository postsRepository() {
            return mock(PostsRepository.class);
        }

        @Bean
        public LikesRepository likesRepository() {
            return mock(LikesRepository.class);
        }

        @Bean
        public TagsRepository tagsRepository() {
            return mock(TagsRepository.class);
        }
    }
}
