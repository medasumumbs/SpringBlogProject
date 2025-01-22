package muravin.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.util.ArrayList;
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

    @OneToMany(mappedBy = "post", fetch = FetchType.EAGER)
    private List<Comment> comments = new ArrayList<>();

    @Transient
    private Integer likesCount;

    @Transient
    private String tagsString;

    private static Integer PREVIEW_LINES_COUNT = 3;

    private static Integer MAX_PREVIEW_SYMBOLS_COUNT = 400;
    /// Возвращает первые PREVIEW_LINES_COUNT строк содержимого поста (превью)
    public String getPreviewText() {
        var lines = content.split("\n");
        if (lines.length < PREVIEW_LINES_COUNT) {
            return content;
        }
        StringBuilder sb = new StringBuilder();
        Arrays.stream(lines)
                .limit(PREVIEW_LINES_COUNT)
                .forEach(line -> sb.append(line).append("\n"));
        if (sb.length() > MAX_PREVIEW_SYMBOLS_COUNT) {
            return content.substring(0, MAX_PREVIEW_SYMBOLS_COUNT)+"...";
        }
        sb.append("...");
        return sb.toString();
    }

    public Integer getCommentsCount() {
        return comments.size();
    }
}
