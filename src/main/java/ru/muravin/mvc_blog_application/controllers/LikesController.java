package ru.muravin.mvc_blog_application.controllers;

import ru.muravin.mvc_blog_application.services.LikesService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/likes")
public class LikesController {
    private final LikesService likesService;

    public LikesController(LikesService likesService) {
        this.likesService = likesService;
    }

    @PostMapping
    public String addLike(@RequestParam(name = "post") Long postId) {
        likesService.addLike(postId);
        return "redirect:/posts/" + postId;
    }
}
