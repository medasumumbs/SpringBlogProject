package muravin.controllers;

import muravin.model.Comment;
import muravin.model.Post;
import muravin.repositories.CommentsRepository;
import muravin.repositories.PostsRepository;
import muravin.services.CommentsService;
import muravin.services.PostsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;

@Controller
@RequestMapping("/comments")
public class CommentsController {
    private final PostsService postsService;
    private final CommentsService commentsService;

    public CommentsController(PostsRepository postsRepository, CommentsRepository commentsRepository, PostsService postsService, CommentsService commentsService) {
        this.postsService = postsService;
        this.commentsService = commentsService;
    }

    @PostMapping
    public String addComment(@RequestParam(name = "post") Long postId, @RequestParam(name = "text") String text, Model model) {
        commentsService.save(
                new Comment(postsService.findOne(postId).orElse(null),text)
        );
        return "redirect:/posts/"+postId;
    }
}
