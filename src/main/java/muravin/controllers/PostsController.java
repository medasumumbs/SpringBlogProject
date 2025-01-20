package muravin.controllers;

import muravin.services.PostsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/posts")
public class PostsController {
    private PostsService postsService;

    @Autowired
    public PostsController(PostsService postsService) {
        this.postsService = postsService;
    }

    @GetMapping
    public String getFeed(Model model, @RequestParam(name = "tag", required = false) String tag) {
        if (tag == null || tag.isEmpty()) {
            model.addAttribute("posts", postsService.findAll());
        } else {
            model.addAttribute("posts", postsService.findByTag(tag));
            model.addAttribute("tag", tag);
        }
        return "posts";
    }
}
