package ait.cohort34.forum.services;

import ait.cohort34.forum.dto.DatePeriodDto;
import ait.cohort34.forum.dto.NewCommentDto;
import ait.cohort34.forum.dto.PostAddDto;
import ait.cohort34.forum.dto.PostDto;

import java.util.Set;


public interface PostService {
    PostDto addPost (String author, PostAddDto postAddDto);
    PostDto findPostById  (String id);
    PostDto removePost (String id);
    PostDto updatePostById  (String id, PostAddDto postAddDto);
    void addLike(String id);
    PostDto addComment (String id, String author, NewCommentDto newCommentDto);
    Iterable<PostDto> findPostsByAuthor  (String author);
    Iterable<PostDto> findPostsByTags  (Set<String> tags);
    Iterable<PostDto> findPostsByPeriod  (DatePeriodDto periodDto);


}
