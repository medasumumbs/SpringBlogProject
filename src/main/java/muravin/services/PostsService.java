package muravin.services;

import muravin.model.Post;
import muravin.model.Tag;
import muravin.repositories.LikesRepository;
import muravin.repositories.PostsRepository;
import muravin.repositories.TagsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static muravin.utils.PageableUtils.listToPage;

@Service
@Transactional
public class PostsService {
    private final LikesRepository likesRepository;
    private final TagsRepository tagsRepository;
    private final PostsRepository postsRepository;

    @Autowired
    public PostsService(PostsRepository postsRepository, LikesRepository likesRepository, TagsRepository tagsRepository) {
        this.postsRepository = postsRepository;
        this.likesRepository = likesRepository;
        this.tagsRepository = tagsRepository;
    }

    public Optional<Post> findOne(Long id) {
        var result = postsRepository.findById(id);
        result.ifPresent(this::enrichPost);
        return result;
    }
    public Page<Post> findAll(Pageable pageable) {
        var result = postsRepository.findAll(pageable);
        result.forEach(this::enrichPost);
        return result;
    }
    public String createTagsString(Post post) {
        var tagsList = tagsRepository.findAllByPost(post);
        StringBuilder tagsString = new StringBuilder();
        tagsList.forEach(tag -> {
            if (!tagsString.isEmpty()) {
                tagsString.append(", ");
            }
            tagsString.append(tag.getTag());
        });
        if (tagsString.isEmpty()) {
            return "Отсутствуют";
        }
        return tagsString.toString();
    }

    public Page<Post> findByTag(String tagValue, Pageable pageable) {
        var tags = tagsRepository.findAllByTag(tagValue, pageable);
        var result = new ArrayList<Post>();
        tags.forEach(tag -> {
            result.add(postsRepository.findById(tag.getPost().getId()).orElse(null));
        });
        result.forEach(this::enrichPost);
        return listToPage(pageable, result);
    }
    public void enrichPost(Post post) {
        if (post == null) return;
        post.setLikesCount(likesRepository.countLikesByPost(post));
        post.setTagsString(createTagsString(post));
    }

    public void save(Post post) {
        postsRepository.save(post);
        if (!StringUtils.isEmpty(post.getTagsString())) {
            saveTags(post);
        }
    }

    private void saveTags(Post post) {
        if (post.getTagsString() == null) {
            return;
        }
        Arrays.stream(post.getTagsString().split(",")).forEach(
                tagValue -> {
                    Tag tag = new Tag(tagValue, post);
                    tagsRepository.save(tag);
                }
        );
    }

    public void deletePost(Long postId) {
        postsRepository.deleteById(postId);
    }
    public void updatePost(Post post, Boolean deleteImage) {
        Post postToBeUpdated = postsRepository.findById(post.getId()).get();
        post.setId(postToBeUpdated.getId());
        if ((!deleteImage) && (post.getPictureBase64() == null)) {
            post.setPictureBase64(postToBeUpdated.getPictureBase64());
        }
        tagsRepository.deleteAllByPost(postToBeUpdated);
        postsRepository.save(post);
        saveTags(post);
    }
}
