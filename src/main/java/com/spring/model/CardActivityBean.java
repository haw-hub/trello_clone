package com.spring.model;

import lombok.Data;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "card_activity")
public class CardActivityBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "card_id", nullable = true)
    private CardBean card;
    
    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false)
    private BoardBean board;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserBean user;

    @Column(nullable = false)
    private String action; // e.g. "Added attachment", "Assigned member"

    @Column(nullable = false)
    private Timestamp createdAt;


    
}
