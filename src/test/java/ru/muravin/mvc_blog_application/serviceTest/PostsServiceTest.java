package ru.muravin.mvc_blog_application.serviceTest;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockReset;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.muravin.mvc_blog_application.model.Comment;
import ru.muravin.mvc_blog_application.model.Post;
import ru.muravin.mvc_blog_application.model.Tag;
import ru.muravin.mvc_blog_application.repositories.LikesRepository;
import ru.muravin.mvc_blog_application.repositories.PostsRepository;
import ru.muravin.mvc_blog_application.repositories.TagsRepository;
import ru.muravin.mvc_blog_application.services.PostsService;
import ru.muravin.mvc_blog_application.utils.PageableUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class PostsServiceTest {
    @Autowired
    private PostsService postsService;
    @MockitoBean(reset = MockReset.BEFORE)
    private PostsRepository postsRepository;
    @MockitoBean(reset = MockReset.BEFORE)
    private LikesRepository likesRepository;
    @MockitoBean(reset = MockReset.BEFORE)
    private TagsRepository tagsRepository;

    @Captor
    private ArgumentCaptor<Post> postCaptor;

    @Test
    void testFindPostById() {
        Post mockPost = getMockPost(1L);
        mockPost.getComments().add(new Comment(mockPost, "12345"));
        Mockito.when(postsRepository.findById(1L)).thenReturn(Optional.of(mockPost));
        Optional<Post> post = postsService.findOne(1L);
        assertNotNull(post.get());
        assertEquals(1, mockPost.getComments().size());
        assertEquals(mockPost.getTitle(), post.get().getTitle());
        assertEquals(mockPost.getContent(), post.get().getContent());
        assertEquals(mockPost.getPictureBase64(), post.get().getPictureBase64());
        assertEquals(0, post.get().getLikesCount());
        assertEquals("Отсутствуют", post.get().getTagsString());
    }

    @Test
    void testFindAll() {
        List<Post> posts = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Post mockPost = getMockPost(1L);
            posts.add(mockPost);
        }
        Pageable pageable = PageRequest.of(0, 10);

        Mockito.when(postsRepository.findAll(pageable)).thenReturn(PageableUtils.listToPage(pageable, posts));
        var result = postsService.findAll(pageable);
        assertNotNull(result);
        assertEquals(10, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
    }

    @Test
    void testFindAllPaged() {
        List<Post> posts = new ArrayList<>();
        for (Long i = 0L; i < 10L; i++) {
            Post mockPost = getMockPost(i);
            posts.add(mockPost);
        }
        Pageable pageable = PageRequest.of(1, 3);

        Mockito.when(postsRepository.findAll(pageable)).thenReturn(PageableUtils.listToPage(pageable, posts));
        var result = postsService.findAll(pageable);
        assertNotNull(result);
        assertEquals(10, result.getTotalElements());
        assertEquals(3, result.getContent().get(0).getId());
        assertEquals(4, result.getContent().get(1).getId());
        assertEquals(5, result.getContent().get(2).getId());

    }

    @Test
    void testFindByTag() {
        var firstTag = getTag(1L);
        var secondTag = getTag(2L);
        var firstPost = firstTag.getPost();
        var secondPost = secondTag.getPost();
        Mockito.when(tagsRepository.findAllByTag(any(), any())).thenReturn(
                List.of(firstTag)
        );
        Mockito.when(postsRepository.findById(1L)).thenReturn(Optional.of(firstPost));
        Mockito.when(postsRepository.findById(2L)).thenReturn(Optional.of(secondPost));
        Mockito.when(tagsRepository.countAllByTag(any())).thenReturn(2L);
        Pageable pageable = PageRequest.of(0, 1);

        var result = postsService.findByTag("tag", pageable);
        assertNotNull(result);
        assertEquals(2, result.getTotalElements(), "wrong number of elements");
        assertEquals(2, result.getTotalPages(), "wrong number of pages");
        assertEquals(1, result.getContent().getFirst().getId(), "wrong first tag");

        Pageable pageable2 = PageRequest.of(1, 1);
        Mockito.when(tagsRepository.findAllByTag(any(), any())).thenReturn(
                List.of(secondTag)
        );
        result = postsService.findByTag("tag", pageable2);
        assertNotNull(result);
        assertEquals(2, result.getContent().getLast().getId(), "wrong first tag id");
    }

    @Test
    void testCreateTagsString() {
        var firstTag = getTag(1L);
        var secondTag = getTag(2L);
        var post = getMockPost(1L);
        firstTag.setPost(post);
        secondTag.setPost(post);
        Mockito.when(tagsRepository.findAllByPost(post)).thenReturn(List.of(firstTag, secondTag));
        var tagsString = postsService.createTagsString(post);
        assertEquals("tag1, tag2", tagsString);
        Mockito.when(tagsRepository.findAllByPost(post)).thenReturn(List.of());
        tagsString = postsService.createTagsString(post);
        assertEquals("Отсутствуют", tagsString);
    }

    @Test
    void testEnrichPost() {
        var post = getMockPost(1L);
        Mockito.when(likesRepository.countLikesByPost(post)).thenReturn(4);
        Mockito.when(tagsRepository.findAllByPost(post)).thenReturn(List.of(new Tag("tag1", post)));
        postsService.enrichPost(post);
        Mockito.verify(likesRepository).countLikesByPost(post);
        assertEquals("tag1", post.getTagsString());
        assertEquals(4, post.getLikesCount());
    }

    @Test
    void saveTest() {
        var post = getMockPost(1L);
        post.setTagsString("tag1,tag2");
        postsService.save(post);
        Mockito.verify(postsRepository).save(post);
        Mockito.verify(postsRepository).save(postCaptor.capture());
        assertEquals(post, postCaptor.getValue());
        verify(tagsRepository, times(2)).save(any(Tag.class));
    }

    @Test
    void deleteTest() {
        var post = getMockPost(1L);
        postsService.deletePost(post.getId());
        Mockito.verify(postsRepository).deleteById(post.getId());
    }

    @Test
    void updateTest() {
        var post = getMockPost(1L);
        var base64 = post.getPictureBase64();
        Mockito.when(postsRepository.findById(post.getId())).thenReturn(Optional.of(post));
        postsService.updatePost(post, false);
        Mockito.verify(tagsRepository).deleteAllByPost(post);
        Mockito.verify(postsRepository).save(post);
        assertEquals(post.getPictureBase64(), base64);
    }

    @Test
    void updateAndDeleteImageTest() {
        var post = getMockPost(1L);
        post.setPictureBase64(null);
        var postFromDb = getMockPost(1L);
        postFromDb.setPictureBase64("oldBase64");
        Mockito.when(postsRepository.findById(post.getId())).thenReturn(Optional.of(postFromDb));
        postsService.updatePost(post, true);
        assertNull(post.getPictureBase64());
    }

    @Test
    void updateAndChangeImageTest() {
        var post = getMockPost(1L);
        post.setPictureBase64("newBase64");
        var postFromDb = getMockPost(1L);
        postFromDb.setPictureBase64("oldBase64");
        Mockito.when(postsRepository.findById(post.getId())).thenReturn(Optional.of(postFromDb));
        postsService.updatePost(post, true);
        assertNotEquals(post.getPictureBase64(), postFromDb.getPictureBase64());
    }

    private static Tag getTag(long i) {
        return new Tag(i, "tag" + i, getMockPost(i));
    }

    public static Post getMockPost(Long i) {
        return new Post(
                i,
                "Новый пост",
                "Content",
                "base64",
                new ArrayList<Comment>(),
                0,
                ""
        );
    }


}
