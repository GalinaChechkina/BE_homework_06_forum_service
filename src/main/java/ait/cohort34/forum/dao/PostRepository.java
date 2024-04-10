package ait.cohort34.forum.dao;

import ait.cohort34.forum.model.Post;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Stream;

//добавляем extends CrudRepository ( или MongoRepository ), укажем <Post, String>
public interface PostRepository extends CrudRepository<Post, String> {
    Stream<Post>findByAuthorIgnoreCase(String author);
    Stream<Post>findByTagsInIgnoreCase(Set<String>tags);
    //@Query("{$and: [ { dateCreated: { $gte: ?0 } }, { dateCreated: { $lte: ?1 } } ] }")
    Stream<Post>findByDateCreatedBetween (LocalDate from, LocalDate to);
}
