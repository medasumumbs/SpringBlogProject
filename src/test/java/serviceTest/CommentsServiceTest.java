package serviceTest;

import muravin.model.Comment;
import muravin.repositories.CommentsRepository;
import muravin.repositories.PostsRepository;
import muravin.services.CommentsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static serviceTest.PostsServiceTest.getMockPost;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CommentsServiceTest.TestConfig.class})
public class CommentsServiceTest {

    @Autowired
    private CommentsService commentsService;

    @Autowired
    private CommentsRepository commentsRepository;

    @Autowired
    private PostsRepository postsRepository;

    @BeforeEach
    void setUp() {
        Mockito.reset(commentsRepository);
    }

    @Test() void saveTest() {
        var testComment = getTestComment();
        commentsService.save(testComment);
        Mockito.verify(commentsRepository, Mockito.times(1)).save(testComment);
    }

    @Test() void findCommentByIdTest() {
        var testComment = getTestComment();
        Mockito.when(commentsRepository.findById(getTestComment().getId())).thenReturn(
                Optional.of(testComment));
        var comment = commentsService.findCommentById(testComment.getId());
        Mockito.verify(commentsRepository, Mockito.times(1)).findById(testComment.getId());
        assertEquals(testComment, comment.get());
    }

    private Comment getTestComment() {
        return new Comment(getMockPost(1L),"Good");
    }

    @Configuration
    public static class TestConfig {
        @Bean public CommentsService commentsService(CommentsRepository commentsRepository, PostsRepository postsRepository) {
            return new CommentsService(commentsRepository, postsRepository);
        }
        @Bean public PostsRepository postsRepository() {
            return Mockito.mock(PostsRepository.class);
        }
        @Bean
        public CommentsRepository commentsRepository() {
            return mock(CommentsRepository.class);
        }
    }
}
