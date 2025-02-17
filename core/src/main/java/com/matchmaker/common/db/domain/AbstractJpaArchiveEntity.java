package com.matchmaker.common.db.domain;

import javax.persistence.*;

@MappedSuperclass
public  class AbstractJpaArchiveEntity extends AbstractJpaEntity {

    @Transient
    public Boolean archived=false;

    public Boolean getArchived() {
        return archived;
    }
}
