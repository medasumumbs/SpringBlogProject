package muravin.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "comment")
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Column(name = "content")
    private String text;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "post_id",referencedColumnName = "post_id")
    private Post post;

    @PreRemove
    public void removeChild() {
        this.post.getComments().removeIf(c -> c.getId() == this.id);
    }
    public Comment(Post post, String text) {
        this.post = post;
        this.text = text;
    }
}
