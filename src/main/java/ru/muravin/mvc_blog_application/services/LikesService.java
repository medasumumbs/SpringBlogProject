package ru.muravin.mvc_blog_application.services;

import ru.muravin.mvc_blog_application.model.Like;
import ru.muravin.mvc_blog_application.repositories.LikesRepository;
import ru.muravin.mvc_blog_application.repositories.PostsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LikesService {
    private final LikesRepository likesRepository;
    private final PostsRepository postsRepository;

    public LikesService(LikesRepository likesRepository, PostsRepository postsRepository) {
        this.likesRepository = likesRepository;
        this.postsRepository = postsRepository;
    }
    public void addLike(Long postId) {
        likesRepository.save(new Like(postsRepository.findById(postId).orElse(null)));
    }
}
