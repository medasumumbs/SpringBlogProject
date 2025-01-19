package muravin.services;

import muravin.model.Post;
import muravin.repositories.LikesRepository;
import muravin.repositories.PostsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PostsService {
    private final LikesRepository likesRepository;
    private PostsRepository postsRepository;

    @Autowired
    public PostsService(PostsRepository postsRepository, LikesRepository likesRepository) {
        this.postsRepository = postsRepository;
        this.likesRepository = likesRepository;
    }

    public List<Post> findAll() {
        var result = postsRepository.findAll();
        result.forEach(post -> post.setLikesCount(likesRepository.countLikesByPost(post)));
        return result;
    }
}
