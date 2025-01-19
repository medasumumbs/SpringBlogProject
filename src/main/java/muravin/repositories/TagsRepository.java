package muravin.repositories;

import muravin.model.Post;
import muravin.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagsRepository extends JpaRepository<Tag, Integer> {
    public List<Tag> findAllByPost(Post post);
}
