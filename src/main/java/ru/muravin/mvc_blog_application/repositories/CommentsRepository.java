package ru.muravin.mvc_blog_application.repositories;

import ru.muravin.mvc_blog_application.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentsRepository extends JpaRepository<Comment, Long> {
}
