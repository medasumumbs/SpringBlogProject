package muravin.controllers;

import muravin.model.Post;
import muravin.services.PostsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/posts")
public class PostsController {
    private PostsService postsService;

    @Autowired
    public PostsController(PostsService postsService) {
        this.postsService = postsService;
    }

    @GetMapping
    public String getFeed(
            Model model,
            @RequestParam(name = "tag", required = false) String tag,
            @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(name = "pageSize", required = false, defaultValue = "30") Integer pageSize) {
        model.addAttribute("pageSize", pageSize);
        if (tag == null || tag.isEmpty()) {
            Pageable pageable = PageRequest.of(page-1, pageSize);
            Page<Post> pageObject = postsService.findAll(pageable);
            model.addAttribute("postsPage", pageObject);
            model.addAttribute("posts", pageObject.stream().toList());
            int pagesCount = pageObject.getTotalPages();
            if (pagesCount > 0) {
                model.addAttribute(
                        "pageNumbers",
                        IntStream.rangeClosed(1, pagesCount)
                        .boxed()
                        .collect(Collectors.toList())
                );
            }
        } else {
            model.addAttribute("posts", postsService.findByTag(tag));
            model.addAttribute("tag", tag);
        }
        return "posts";
    }
}
