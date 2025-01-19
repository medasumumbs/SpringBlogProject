package muravin.services;

import muravin.model.Post;
import muravin.repositories.LikesRepository;
import muravin.repositories.PostsRepository;
import muravin.repositories.TagsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class PostsService {
    private final LikesRepository likesRepository;
    private final TagsRepository tagsRepository;
    private PostsRepository postsRepository;

    @Autowired
    public PostsService(PostsRepository postsRepository, LikesRepository likesRepository, TagsRepository tagsRepository) {
        this.postsRepository = postsRepository;
        this.likesRepository = likesRepository;
        this.tagsRepository = tagsRepository;
    }

    public List<Post> findAll() {
        var result = postsRepository.findAll();
        result.forEach(this::enrichPost);
        return result;
    }
    private String createTagsString(Post post) {
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

    public List<Post> findByTag(String tagValue) {
        var tags = tagsRepository.findAllByTag(tagValue);
        var result = new ArrayList<Post>();
        tags.forEach(tag -> {
            result.add(postsRepository.findById(tag.getPost().getId()).orElse(null));
        });
        result.forEach(this::enrichPost);
        return result;
    }
    private void enrichPost(Post post) {
        if (post == null) return;
        post.setLikesCount(likesRepository.countLikesByPost(post));
        post.setTagsString(createTagsString(post));
    }
}
