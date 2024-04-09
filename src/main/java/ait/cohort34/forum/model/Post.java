package ait.cohort34.forum.model;

import ait.cohort34.forum.dto.CommentDto;
import ait.cohort34.forum.dto.PostDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@Document(collection = "posts")//в бд наша коллекция б. называться posts
public class Post {
    private String id;
    @Setter
    private String title;
    @Setter
    private String content;
    @Setter
    private String author;
    private LocalDateTime dateCreated =LocalDateTime.now();;
    private Set<String> tags = new HashSet<>();
    private int likes;
    private List<Comment> comments = new ArrayList<>();

//хочу конструктор только на 4 поля
//дата будет текущей, тэги, лайки и комменты б. добавлять методами

    public Post(String title, String content, String author, Set<String> tags) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.tags = tags;
    }

    public void likePost() { likes++; }

    public boolean addTag(String tag){
        return tags.add(tag);
    }

    public boolean removeTag(String tag){
        return tags.remove(tag);
    }

    public boolean addComment(Comment comment){
        return comments.add(comment);
    }

    public boolean removeComment(Comment comment){
        return comments.remove(comment);
    }
}
