package ru.muravin.mvc_blog_application.serviceTest;

import ru.muravin.mvc_blog_application.repositories.LikesRepository;
import ru.muravin.mvc_blog_application.repositories.PostsRepository;
import ru.muravin.mvc_blog_application.repositories.TagsRepository;
import ru.muravin.mvc_blog_application.services.PostsService;
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
