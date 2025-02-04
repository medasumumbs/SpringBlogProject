package ru.muravin.mvc_blog_application.serviceTest;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockReset;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.muravin.mvc_blog_application.model.Comment;
import ru.muravin.mvc_blog_application.repositories.CommentsRepository;
import ru.muravin.mvc_blog_application.repositories.PostsRepository;
import ru.muravin.mvc_blog_application.services.CommentsService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static ru.muravin.mvc_blog_application.serviceTest.PostsServiceTest.getMockPost;

@SpringBootTest()
@ActiveProfiles("test")
public class CommentsServiceTest {

    @Autowired
    private CommentsService commentsService;

    @MockitoBean(reset = MockReset.BEFORE)
    private CommentsRepository commentsRepository;

    @MockitoBean(reset = MockReset.BEFORE)
    private PostsRepository postsRepository;

    @Captor
    private ArgumentCaptor<Comment> commentCaptor;

    @Test() void saveTest() {
        var testComment = getTestComment();
        commentsService.save(testComment);
        Mockito.verify(commentsRepository, Mockito.times(1)).save(testComment);
        Mockito.verify(commentsRepository).save(commentCaptor.capture());
        assertEquals(testComment, commentCaptor.getValue());
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
}
