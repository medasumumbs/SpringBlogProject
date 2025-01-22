package muravin.controllers;

import muravin.model.Post;
import muravin.services.PostsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Base64;
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

    @GetMapping("/{id}")
    public String show(@PathVariable("id") Long id, Model model, @ModelAttribute("post") Post post) {
        model.addAttribute("post",postsService.findOne(id).orElse(null));
        return "posts/show";
    }

    @GetMapping("/add")
    public String addPost(@ModelAttribute("post") Post post) {
        return "posts/add";
    }

    @DeleteMapping("/{id}")
    public String deletePost(@PathVariable("id") Long postId) {
        postsService.deletePost(postId);
        return "redirect:/posts";
    }

    @PostMapping
    public String addPost(@ModelAttribute("post") Post post, @RequestParam("image") MultipartFile file, Model model) {
        try {
            post.setPictureBase64(
                    new String(Base64.getEncoder().encode(file.getBytes()))
            );
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        postsService.save(post);
        return "redirect:/posts";
    }

    @GetMapping
    public String getFeed(
            Model model,
            @RequestParam(name = "tag", required = false) String tag,
            @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(name = "pageSize", required = false, defaultValue = "1") Integer pageSize) {
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("page", page);
        if (model.getAttribute("tag") == null) model.addAttribute("tag", "");
        Pageable pageable = PageRequest.of(page-1, pageSize);
        Page<Post> pageObject;
        if (tag == null || tag.isEmpty()) {
            pageObject = postsService.findAll(pageable);
        } else {
            /// TODO исправить возможность indexOutOfBoundsException!
            if (!tag.equals(model.getAttribute("tag"))) {
                model.addAttribute("page", 1);
            }
            pageObject = postsService.findByTag(tag,pageable);
            model.addAttribute("tag", tag);
        }
        int pagesCount = pageObject.getTotalPages();
        if (pagesCount > 0) {
            model.addAttribute(
                    "pageNumbers",
                    IntStream.rangeClosed(1, pagesCount)
                            .boxed()
                            .collect(Collectors.toList())
            );
        }
        model.addAttribute("posts", pageObject.stream().toList());
        model.addAttribute("postsPage", pageObject);
        return "posts";
    }
}
