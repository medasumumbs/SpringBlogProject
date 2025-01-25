package serviceTest;

import muravin.repositories.LikesRepository;
import muravin.repositories.PostsRepository;
import muravin.repositories.TagsRepository;
import muravin.services.PostsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class TestConfig {
    @Bean
    public PostsService userService(PostsRepository postsRepository, LikesRepository likesRepository, TagsRepository tagsRepository) {
        return new PostsService(postsRepository, likesRepository, tagsRepository);
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
