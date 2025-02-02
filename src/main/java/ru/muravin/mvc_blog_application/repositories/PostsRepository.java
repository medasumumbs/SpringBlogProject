package ru.muravin.mvc_blog_application.repositories;

import ru.muravin.mvc_blog_application.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostsRepository extends JpaRepository<Post, Long> {
    @Override
    public List<Post> findAll();
}
