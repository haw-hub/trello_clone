package com.spring.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "list")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ListBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;
    
    private String title;
    private Integer position;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "boards_id", insertable = false, updatable = false)
    private BoardBean board;
    
    private Integer boards_id;
    
    @OneToMany(mappedBy = "list", cascade = CascadeType.ALL,orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CardBean> cards;
}