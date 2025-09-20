package com.spring.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "attachment")
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // ✅ avoid recursion

public class AttachmentBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String filename;

    @Column(name = "filetype")
    private String fileType;

    @Lob
    private byte[] file;

    // FIX: Use proper relationship mapping without duplicate fields
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cardsId")
    @ToString.Exclude               // ✅ prevent infinite loop in toString
    private CardBean card;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usersId")
    private UserBean user;

    @Column(name = "createdat")
    private Timestamp createdAt;
}