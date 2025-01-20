package muravin.services;

import muravin.model.Post;
import muravin.repositories.LikesRepository;
import muravin.repositories.PostsRepository;
import muravin.repositories.TagsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class PostsService {
    private final LikesRepository likesRepository;
    private final TagsRepository tagsRepository;
    private PostsRepository postsRepository;

    @Autowired
    public PostsService(PostsRepository postsRepository, LikesRepository likesRepository, TagsRepository tagsRepository) {
        this.postsRepository = postsRepository;
        this.likesRepository = likesRepository;
        this.tagsRepository = tagsRepository;
    }

    public Page<Post> findAll(Pageable pageable) {
        var result = postsRepository.findAll(pageable);
        result.forEach(this::enrichPost);
        return result;
    }
    private String createTagsString(Post post) {
        var tagsList = tagsRepository.findAllByPost(post);
        StringBuilder tagsString = new StringBuilder();
        tagsList.forEach(tag -> {
            if (!tagsString.isEmpty()) {
                tagsString.append(", ");
            }
            tagsString.append(tag.getTag());
        });
        if (tagsString.isEmpty()) {
            return "Отсутствуют";
        }
        return tagsString.toString();
    }

    public Page<Post> findByTag(String tagValue, Pageable pageable) {
        var tags = tagsRepository.findAllByTag(tagValue);
        var result = new ArrayList<Post>();
        tags.forEach(tag -> {
            result.add(postsRepository.findById(tag.getPost().getId()).orElse(null));
        });
        result.forEach(this::enrichPost);
        return listToPage(pageable, result);
    }
    private void enrichPost(Post post) {
        if (post == null) return;
        post.setLikesCount(likesRepository.countLikesByPost(post));
        post.setTagsString(createTagsString(post));
    }
    private <T> Page<T> listToPage(Pageable pageable, List<T> entities) {
        int lowerBound = pageable.getPageNumber() * pageable.getPageSize();
        int upperBound = Math.min(lowerBound + pageable.getPageSize(), entities.size());
        //if (upperBound - lowerBound < pageable.getPageSize()) upperBound = upperBound + pageable.getPageSize();
        if (lowerBound == upperBound) upperBound = lowerBound + pageable.getPageSize();
        List<T> subList = entities.subList(lowerBound, upperBound);

        return new PageImpl<T>(subList, pageable, entities.size());
    };
}
