package com.spring.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "card")
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // ✅ avoid recursion
public class CardBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    private String title;
    private String description;
    private Integer position;
    private Date due_date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lists_id", insertable = false, updatable = false)
    @ToString.Exclude
    private ListBean list;

    private Integer lists_id;

    @Column(name = "completed")
    private Boolean completed = false;

    @Column(name = "completed_at")
    private Timestamp completedAt;

    @Transient
    private List<UserBean> members = new ArrayList<>();

    public List<UserBean> getMembers() {
        return this.members;
    }

    public void setMembers(List<UserBean> members) {
        this.members = members;
    }

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<CardMemberBean> cardMembers = new ArrayList<>();

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<AttachmentBean> attachments;

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<CommentBean> comments;

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<ChecklistBean> checklists;
}
