package ait.cohort34.forum.controller;

import ait.cohort34.forum.dto.DatePeriodDto;
import ait.cohort34.forum.dto.NewCommentDto;
import ait.cohort34.forum.dto.PostAddDto;
import ait.cohort34.forum.dto.PostDto;
import ait.cohort34.forum.services.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/forum")//чтобы не писать долгий путь
public class PostController {

    private final PostService postService;

    @PostMapping("/post/{author}")
    public PostDto addPost(@PathVariable String author, @RequestBody PostAddDto postAddDto) {
        return postService.addPost(author, postAddDto);
    }

    @GetMapping("/post/{id}")
    public PostDto findPostById(@PathVariable String id) {
        return postService.findPostById(id);
    }

    @DeleteMapping("/post/{id}")
    public PostDto removePost(@PathVariable String id) { return postService.removePost(id); }

    @PutMapping("/post/{id}")
    public PostDto updatePostById(@PathVariable String id, @RequestBody PostAddDto postAddDto) {
        return postService.updatePostById(id, postAddDto);
    }

    @PutMapping("/post/{id}/like")
    @ResponseStatus(HttpStatus.NO_CONTENT)//оградились от статуса 204, теперь о нем будет сообщение
    public void addLike(@PathVariable String id) {
        postService.addLike(id);
    }

    @PutMapping("/post/{id}/comment/{author}")
    public PostDto addComment(@PathVariable String id, @PathVariable String author, @RequestBody NewCommentDto newCommentDto) {
        return postService.addComment(id, author, newCommentDto);
    }

    @GetMapping("/posts/author/{author}")
    public Iterable<PostDto> findPostsByAuthor(@PathVariable String author) {
        return postService.findPostsByAuthor(author);
    }

    @PostMapping("/posts/tags")
    public Iterable<PostDto> findPostsByTags(@RequestBody Set<String> tags) {
        return postService.findPostsByTags(tags);
    }

    @PostMapping("/posts/period")
    public Iterable<PostDto> findPostsByPeriod(@RequestBody DatePeriodDto periodDto) {
        return postService.findPostsByPeriod(periodDto);
    }
}
