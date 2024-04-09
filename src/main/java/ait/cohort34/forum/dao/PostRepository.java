package ait.cohort34.forum.dao;

import ait.cohort34.forum.dto.DatePeriodDto;
import ait.cohort34.forum.model.Post;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;
import java.util.stream.Stream;

//добавляем extends CrudRepository ( или MongoRepository ), укажем <Post, String>
public interface PostRepository extends CrudRepository<Post, String> {
    Stream<Post>findPostsByAuthor(String author);
    Stream<Post>findPostsByTags(Set<String>tags);
    Stream<Post>findPostsByPeriod(DatePeriodDto periodDto);
}
