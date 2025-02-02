package ru.muravin.mvc_blog_application.controllers;

import ru.muravin.mvc_blog_application.model.Comment;
import ru.muravin.mvc_blog_application.repositories.CommentsRepository;
import ru.muravin.mvc_blog_application.repositories.PostsRepository;
import ru.muravin.mvc_blog_application.services.CommentsService;
import ru.muravin.mvc_blog_application.services.PostsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    @PatchMapping("/{id}")
    public String editComment(@PathVariable("id") Long id, @RequestParam(name = "post") Long postId, @RequestParam(name = "text") String text) {
        Comment originalComment = commentsService.findCommentById(id).orElseThrow(RuntimeException::new);
        originalComment.setText(text);
        commentsService.save(originalComment);
        return "redirect:/posts/"+postId;
    }
    @DeleteMapping("/{id}")
    public String deletePost(@PathVariable("id") Long commentId, @RequestParam(name = "post") Long postId) {
        commentsService.deleteComment(commentId);
        return "redirect:/posts/"+postId;
    }
}
