package ait.cohort34.forum.services;

import ait.cohort34.forum.dao.PostRepository;
import ait.cohort34.forum.dto.DatePeriodDto;
import ait.cohort34.forum.dto.NewCommentDto;
import ait.cohort34.forum.dto.PostAddDto;
import ait.cohort34.forum.dto.PostDto;
import ait.cohort34.forum.dto.exceptions.PostNotFoundException;
import ait.cohort34.forum.model.Comment;
import ait.cohort34.forum.model.Post;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final ModelMapper modelMapper;

    @Override
    public PostDto addPost(String author, PostAddDto postAddDto) {
        Post post = modelMapper.map(postAddDto, Post.class);
        post.setAuthor(author);//автора не даст мэппер, поэтому добавляем его отдельно сэттером
        post = postRepository.save(post);//метод, кот. понимает Spring DB
        return modelMapper.map(post, PostDto.class);
    }

    @Override
    public PostDto findPostById(String id) {
        Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
        return modelMapper.map(post, PostDto.class);
    }

    @Override
    public PostDto removePost(String id) {
        Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
        postRepository.delete(post);
        return modelMapper.map(post, PostDto.class);
    }

    @Override
    public PostDto updatePostById(String id, PostAddDto postAddDto) {
        Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);//нашли пост
        String content = postAddDto.getContent(); //если контент не null, сохраним его в обновленный пост
        if(content != null){
            post.setContent(content);
        }
        String title = postAddDto.getTitle();
        if(title != null){
            post.setTitle(title);
        }
        Set<String> tags = postAddDto.getTags();
        if(tags != null){
            tags.forEach(post::addTag);
        }
        post = postRepository.save(post);//сохранили обновленный пост в репо
        return modelMapper.map(post, PostDto.class);//вернули обновленный пост
    }

    @Override
    public void addLike(String id) {
        Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
        post.likePost();
        post = postRepository.save(post);
    }

    @Override
    public PostDto addComment(String id, String author, NewCommentDto newCommentDto) {
        Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
        Comment comment = new Comment(author, newCommentDto.getMessage());
        post.addComment(comment);
        post = postRepository.save(post);
        return modelMapper.map(post, PostDto.class);
    }

    @Override
    public Iterable<PostDto> findPostsByAuthor(String author) {
        return postRepository.findByAuthorIgnoreCase(author)
                .map(e->modelMapper.map(e, PostDto.class))
                .toList();
    }

    @Override
    public Iterable<PostDto> findPostsByTags(Set<String> tags) {
        return postRepository.findByTagsInIgnoreCase(tags)
                .map(e->modelMapper.map(e, PostDto.class))
                .toList();
    }

    @Override
    public Iterable<PostDto> findPostsByPeriod(DatePeriodDto periodDto) {
        return postRepository.findByDateCreatedBetween(periodDto.getDateFrom(), periodDto.getDateTo())
                .map(e->modelMapper.map(e, PostDto.class))
                .toList();
    }
//    @Override
//    public Iterable<PostDto> findPostsByPeriod(DatePeriodDto periodDto) {
//        return postRepository.findByDateCreatedBetween(periodDto.getDateFrom(), periodDto.getDateTo())
//                .map(e->modelMapper.map(e, PostDto.class))
//                .toList();
//    }

}
