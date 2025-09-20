package com.spring.model;

import javax.persistence.*;

import lombok.Data;
@Data
@Entity
@Table(name = "board_members")
public class BoardMemberBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "board_id", nullable = true)
    private BoardBean board;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private UserBean user;

    @Column(name = "role", nullable = true)
    private String role = "member";

    
}
