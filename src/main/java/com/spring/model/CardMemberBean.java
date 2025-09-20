package com.spring.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.persistence.*;

@Data
@Entity
@Table(name = "card_member")
@IdClass(CardMemberId.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CardMemberBean {
    
    @Id
    @Column(name = "card_id")
    @EqualsAndHashCode.Include
    private Integer cardId;
    
    @Id
    @Column(name = "user_id")
    @EqualsAndHashCode.Include
    private Integer userId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", insertable = false, updatable = false)
    private CardBean card;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserBean user;
    
    private java.sql.Timestamp assignedAt;
}