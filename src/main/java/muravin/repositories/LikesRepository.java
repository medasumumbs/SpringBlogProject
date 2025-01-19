package muravin.repositories;

import muravin.model.Like;
import muravin.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikesRepository extends JpaRepository<Like, Long> {
    public Integer countLikesByPost(Post post);
}
