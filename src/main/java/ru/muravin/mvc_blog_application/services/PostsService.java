package ru.muravin.mvc_blog_application.services;

import org.springframework.data.domain.PageRequest;
import ru.muravin.mvc_blog_application.model.Post;
import ru.muravin.mvc_blog_application.model.Tag;
import ru.muravin.mvc_blog_application.repositories.LikesRepository;
import ru.muravin.mvc_blog_application.repositories.PostsRepository;
import ru.muravin.mvc_blog_application.repositories.TagsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static ru.muravin.mvc_blog_application.utils.PageableUtils.listToPage;

@Service
@Transactional
public class PostsService {
    private final LikesRepository likesRepository;
    private final TagsRepository tagsRepository;
    private final PostsRepository postsRepository;

    @Autowired
    public PostsService(PostsRepository postsRepository, LikesRepository likesRepository, TagsRepository tagsRepository) {
        this.postsRepository = postsRepository;
        this.likesRepository = likesRepository;
        this.tagsRepository = tagsRepository;
    }

    public Optional<Post> findOne(Long id) {
        var result = postsRepository.findById(id);
        result.ifPresent(this::enrichPost);
        return result;
    }
    public Page<Post> findAll(Pageable pageable) {
        var result = postsRepository.findAll(pageable);
        result.forEach(this::enrichPost);
        return result;
    }
    public String createTagsString(Post post) {
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
        // Получаем количество всех привязок тэгов к постам, чтобы корректно отобразить пагинацию
        var countAll = tagsRepository.countAllByTag(tagValue);
        int pageNumber = pageable.getPageNumber();
        if (countAll < (long) (pageable.getPageSize() * pageable.getPageNumber() + 1)) {
            // Если в базе меньше записей, чем хочет получить пагинатор - возвращаем последнюю возможную страницу
            pageNumber = (int) (countAll / pageable.getPageSize() - 1);
            if (pageNumber < 0) pageNumber = 0;
            pageable = PageRequest.of(pageNumber, pageable.getPageSize());
        }
        var tags = tagsRepository.findAllByTag(tagValue, pageable);
        var result = new ArrayList<Post>();
        // Все множество, кроме загруженной страницы выше, инициализируется как null
        for (int i = 0; i < pageable.getPageNumber()* pageable.getPageSize(); i++) {
            result.add(null);
        }
        tags.forEach(tag -> {
            result.add(postsRepository.findById(tag.getPost().getId()).orElse(null));
        });
        result.forEach(this::enrichPost);
        for (int i = result.size(); i < countAll; i++) {
            result.add(null);
        }
        return listToPage(pageable, result);
    }
    public void enrichPost(Post post) {
        if (post == null) return;
        post.setLikesCount(likesRepository.countLikesByPost(post));
        post.setTagsString(createTagsString(post));
    }

    public void save(Post post) {
        postsRepository.save(post);
        if (!StringUtils.isEmpty(post.getTagsString())) {
            saveTags(post);
        }
    }

    private void saveTags(Post post) {
        if (post.getTagsString() == null) {
            return;
        }
        Arrays.stream(post.getTagsString().split(",")).forEach(
                tagValue -> {
                    Tag tag = new Tag(tagValue, post);
                    tagsRepository.save(tag);
                }
        );
    }

    public void deletePost(Long postId) {
        postsRepository.deleteById(postId);
    }
    public void updatePost(Post post, Boolean deleteImage) {
        Post postToBeUpdated = postsRepository.findById(post.getId()).get();
        post.setId(postToBeUpdated.getId());
        if ((!deleteImage) && (post.getPictureBase64() == null)) {
            post.setPictureBase64(postToBeUpdated.getPictureBase64());
        }
        tagsRepository.deleteAllByPost(postToBeUpdated);
        postsRepository.save(post);
        saveTags(post);
    }
}
