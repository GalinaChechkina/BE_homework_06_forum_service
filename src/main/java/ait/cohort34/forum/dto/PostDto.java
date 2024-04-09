package ait.cohort34.forum.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@AllArgsConstructor
@NoArgsConstructor//если не ставить аннотации, то дефолтный конструктор по умолчанию,
@Builder
public class PostDto {
    private String id;
    private String title;
    private String content;
    private String author;
    private LocalDateTime dateCreated;
    @Singular//при аннотации @Builder позволяет заполнять по одиночке множества
    private Set<String> tags;
    private Integer likes;
    @Singular
    private List<CommentDto> comments;
}
