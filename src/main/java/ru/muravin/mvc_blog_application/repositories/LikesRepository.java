package ru.muravin.mvc_blog_application.repositories;

import ru.muravin.mvc_blog_application.model.Like;
import ru.muravin.mvc_blog_application.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikesRepository extends JpaRepository<Like, Long> {
    public Integer countLikesByPost(Post post);
}
