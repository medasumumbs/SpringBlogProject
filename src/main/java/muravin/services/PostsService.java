package muravin.services;

import muravin.model.Post;
import muravin.repositories.LikesRepository;
import muravin.repositories.PostsRepository;
import muravin.repositories.TagsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        result.forEach(post -> post.setLikesCount(likesRepository.countLikesByPost(post)));
        result.forEach(post -> post.setTagsString(createTagsString(post)));
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
}
