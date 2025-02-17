package com.matchmaker.common.db;

import java.sql.ResultSet;
import java.util.List;

import com.matchmaker.common.exception.ApplicationException;

public interface ResultSetMapper<T>
{
	
	/**
	 * This method provides the logic to convert resultSet into a JavaBean
	 * 
	 * @param resultSet
	 * @return
	 */
	public List<T> mapResultSet(ResultSet resultSet) throws ApplicationException;
}
