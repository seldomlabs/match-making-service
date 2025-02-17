package com.matchmaker.model;

import com.matchmaker.common.db.domain.AbstractJpaEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "user_match_mapping")
public class UserMatchMapping extends AbstractJpaEntity {

    @Column(name = "user_id")
    private String userId;

    @Column(name = "match_id")
    private Long matchId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }
}
