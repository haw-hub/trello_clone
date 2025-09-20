package com.spring.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.sql.Timestamp;
import javax.persistence.*;

@Data
@Entity
@Table(name = "comment")
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // ✅ prevent recursion
public class CommentBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include // ✅ only use ID in equals/hashCode
    private Integer id;

    private String content;

    @Column(name = "created_at")
    private Timestamp createdAt;

    private String username;

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cards_id", insertable = false, updatable = false)
    private CardBean card;

    private Integer cards_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id", insertable = false, updatable = false)
    private UserBean user;

    private Integer users_id;
}
