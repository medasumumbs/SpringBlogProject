package ru.muravin.mvc_blog_application.services;

import ru.muravin.mvc_blog_application.model.Comment;
import ru.muravin.mvc_blog_application.repositories.CommentsRepository;
import ru.muravin.mvc_blog_application.repositories.PostsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class CommentsService {
    private final CommentsRepository commentsRepository;
    private final PostsRepository postsRepository;

    public CommentsService(CommentsRepository commentsRepository, PostsRepository postsRepository) {
        this.commentsRepository = commentsRepository;
        this.postsRepository = postsRepository;
    }

    public void save(Comment comment) {
        commentsRepository.save(comment);
    }

    public Optional<Comment> findCommentById(Long id) {
        return commentsRepository.findById(id);
    }

    public void deleteComment(Long commentId) {
        var comment = commentsRepository.findById(commentId).get();
        //var post = comment.getPost();
        //post.getComments().remove(comment);
        //postsRepository.save(post);
        commentsRepository.delete(comment);
    }
}
