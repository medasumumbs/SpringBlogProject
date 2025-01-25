package serviceTest;

import muravin.model.Like;
import muravin.repositories.LikesRepository;
import muravin.repositories.PostsRepository;
import muravin.services.LikesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class, LikesServiceTest.TestConfig.class})
public class LikesServiceTest {
    @Autowired
    private PostsRepository postsRepository;
    @Autowired
    private LikesRepository likesRepository;
    @Autowired
    private LikesService likesService;
    @BeforeEach
    void setUp() {
        Mockito.reset(postsRepository, likesRepository);
    }
    @Test
    void addLikeTest() {
        likesService.addLike(1L);
        Mockito.verify(likesRepository, Mockito.times(1)).save(any(Like.class));
    }
    @Configuration
    public static class TestConfig {
        @Bean
        public LikesService likesService(LikesRepository likesRepository, PostsRepository postsRepository) {
            return new LikesService(likesRepository, postsRepository);
        }
    }
}
