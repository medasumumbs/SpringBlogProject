package ru.muravin.mvc_blog_application.serviceTest;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockReset;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.muravin.mvc_blog_application.model.Like;
import ru.muravin.mvc_blog_application.repositories.LikesRepository;
import ru.muravin.mvc_blog_application.repositories.PostsRepository;
import ru.muravin.mvc_blog_application.services.LikesService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
public class LikesServiceTest {
    @MockitoBean(reset = MockReset.BEFORE)
    private PostsRepository postsRepository;
    @MockitoBean(reset = MockReset.BEFORE)
    private LikesRepository likesRepository;
    @Autowired
    private LikesService likesService;
    @Test
    void addLikeTest() {
        likesService.addLike(1L);
        Mockito.verify(likesRepository, Mockito.times(1)).save(any(Like.class));
    }
}
