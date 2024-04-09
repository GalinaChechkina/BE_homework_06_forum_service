package ait.cohort34.forum.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
public class PostAddDto {
    private String title;
    private String content;
    private Set<String> tags;

}
