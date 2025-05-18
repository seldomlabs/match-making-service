package com.matchmaker.model;

import com.matchmaker.common.db.domain.AbstractJpaEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_match_mapping")
public class UserMatchMapping extends AbstractJpaEntity {

    @Column(name = "user_id")
    private String userId;

    @Column(name = "match_info_id")
    private Long matchInfoId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getMatchInfoId() {
        return matchInfoId;
    }

    public void setMatchInfoId(Long matchInfoId) {
        this.matchInfoId = matchInfoId;
    }
}
