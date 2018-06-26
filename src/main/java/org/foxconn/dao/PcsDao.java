package org.foxconn.dao;

import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;

public interface PcsDao {
	
	public List<List<?>> findAll(Map<String, Object> map) throws DataAccessException;
	
	public void updateSSNStatus(Map<String, String> map) throws DataAccessException;
}
