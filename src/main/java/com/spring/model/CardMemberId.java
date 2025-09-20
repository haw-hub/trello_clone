package com.spring.model;

import java.io.Serializable;
import java.util.Objects;

public class CardMemberId implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer cardId;
    private Integer userId;

    // Constructors
    public CardMemberId() {}
    
    public CardMemberId(Integer cardId, Integer userId) {
        this.cardId = cardId;
        this.userId = userId;
    }

    // Getters and Setters
    public Integer getCardId() { return cardId; }
    public void setCardId(Integer cardId) { this.cardId = cardId; }
    
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardMemberId that = (CardMemberId) o;
        return Objects.equals(cardId, that.cardId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardId, userId);
    }
}