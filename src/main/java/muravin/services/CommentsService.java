package muravin.services;

import muravin.model.Comment;
import muravin.repositories.CommentsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CommentsService {
    private final CommentsRepository commentsRepository;

    public CommentsService(CommentsRepository commentsRepository) {
        this.commentsRepository = commentsRepository;
    }

    @Transactional(readOnly = false)
    public void save(Comment comment) {
        commentsRepository.save(comment);
    }
}
