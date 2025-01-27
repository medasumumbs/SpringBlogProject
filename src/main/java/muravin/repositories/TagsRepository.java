package muravin.repositories;

import muravin.model.Post;
import muravin.model.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TagsRepository extends JpaRepository<Tag, Long> {
    public List<Tag> findAllByPost(Post post);
    public List<Tag> findAllByTag(String tag, Pageable pageable);

    @Transactional
    @Modifying
    @Query("DELETE FROM Tag tag WHERE tag.post = :post")
    void deleteAllByPost(@Param("post") Post post);
}
