package ait.cohort34.forum.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@EqualsAndHashCode(of = {"user","dateCreated"})
@NoArgsConstructor
public class Comment {
    @Setter
    private String user;
    @Setter
    private String message;
    private LocalDateTime dateCreated = LocalDateTime.now();
    private int likes;

//конструктор на два поля, лайки добавим методом, дата текущая- задали в полях
//не забудем аннотацию для пустого конструктора
    public Comment(String user, String message) {
        this.user = user;
        this.message = message;
    }

    public void addLike() {
        likes++;
    }
}
