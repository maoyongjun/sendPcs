package org.foxconn.dao;

import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;

public interface PcsDao {
	public List<List<?>> findHPAll(Map<String,Object> map) throws DataAccessException;
	public List<List<?>> findOtherAll(Map<String,Object> map) throws DataAccessException;
	public void updateSSNStatus(Map<String,String> map) throws DataAccessException;
 }
