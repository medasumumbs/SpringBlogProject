package muravin.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "post")
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "picture_base_64")
    private String pictureBase64;

    @OneToMany(mappedBy = "post")
    private List<Comment> comments;

    private static Integer PREVIEW_LINES_COUNT = 3;

    /// Возвращает первые PREVIEW_LINES_COUNT строк содержимого поста (превью)
    public String getPreviewText() {
        StringBuilder sb = new StringBuilder();
        Arrays.stream(content.split("\n"))
                .limit(PREVIEW_LINES_COUNT)
                .forEach(line -> sb.append(line).append("\n"));
        sb.append("...");
        return sb.toString();
    }
}
