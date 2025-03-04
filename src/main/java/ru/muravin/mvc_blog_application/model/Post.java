package ru.muravin.mvc_blog_application.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(name = "title",columnDefinition = "VARCHAR(1024)")
    private String title;

    @Column(name = "content",columnDefinition = "VARCHAR(1024)")
    private String content;

    @Column(name = "picture_base_64", columnDefinition = "BLOB")
    private String pictureBase64;

    @OneToMany(mappedBy = "post", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @Transient
    private Integer likesCount;

    @Transient
    private String tagsString;

    private static Integer PREVIEW_LINES_COUNT = 1;

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
                .forEach(line -> sb.append(line));
        if (sb.length() > MAX_PREVIEW_SYMBOLS_COUNT) {
            return content.substring(0, MAX_PREVIEW_SYMBOLS_COUNT)+"...";
        }
        if (lines.length > PREVIEW_LINES_COUNT) {
            sb.append("...");
        }
        return sb.toString();
    }

    public Integer getCommentsCount() {
        return comments.size();
    }
}
