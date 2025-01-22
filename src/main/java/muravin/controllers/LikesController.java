package muravin.controllers;

import muravin.model.Comment;
import muravin.model.Like;
import muravin.repositories.CommentsRepository;
import muravin.repositories.PostsRepository;
import muravin.services.CommentsService;
import muravin.services.LikesService;
import muravin.services.PostsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
        return "redirect:/posts/"+postId;
    }
}
