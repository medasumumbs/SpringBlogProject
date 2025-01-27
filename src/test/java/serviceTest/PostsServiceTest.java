package serviceTest;

import muravin.model.Comment;
import muravin.model.Post;
import muravin.model.Tag;
import muravin.repositories.LikesRepository;
import muravin.repositories.PostsRepository;
import muravin.repositories.TagsRepository;
import muravin.services.PostsService;
import muravin.utils.PageableUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
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
        Pageable pageable = PageRequest.of(1, 3);
        Mockito.when(tagsRepository.findAllByTag(any(), any())).thenReturn(
                List.of(firstTag, secondTag)
        );
        Mockito.when(postsRepository.findById(1L)).thenReturn(Optional.of(firstPost));
        Mockito.when(postsRepository.findById(2L)).thenReturn(Optional.of(secondPost));
        pageable = PageRequest.of(0, 1);

        var result = postsService.findByTag("tag", pageable);
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getTotalPages());
        assertEquals(1, result.getContent().getFirst().getId());

        Pageable pageable2 = PageRequest.of(1, 1);
        result = postsService.findByTag("tag", pageable2);
        assertNotNull(result);
        assertEquals(2, result.getContent().getFirst().getId());

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
        Post mockPost = new Post(
                i,
                "Новый пост",
                "Content",
                "base64",
                new ArrayList<Comment>(),
                0,
                ""
        );
        return mockPost;
    }


}
