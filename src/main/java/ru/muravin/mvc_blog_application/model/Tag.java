package ru.muravin.mvc_blog_application.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tag")
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long id;

    @Column(name = "tag_value")
    private String tag;

    @ManyToOne
    @JoinColumn(name = "post_id",referencedColumnName = "post_id")
    private Post post;

    public Tag(String tag, Post post) {
        this.tag = tag;
        this.post = post;
    }
}
