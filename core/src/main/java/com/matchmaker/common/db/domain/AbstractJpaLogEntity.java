package com.matchmaker.common.db.domain;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import com.matchmaker.common.constants.CommonColumnNames;

@MappedSuperclass
@EntityListeners({ AbstractJpaLogEntity.AbstractEntityListener.class })
@SuppressWarnings("serial")
public class AbstractJpaLogEntity extends AbstractJpaEntity
{
	
	@Column(name = CommonColumnNames.ArchivedId)
	private long archivedId;
	
	public long getArchivedId()
	{
		return archivedId;
	}
	
	public void setArchivedId(long archivedId)
	{
		this.archivedId = archivedId;
	}
	
}
