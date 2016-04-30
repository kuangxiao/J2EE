package com.cn.hnust.service.dao;

import org.springframework.stereotype.Repository;

import com.cn.hnust.service.domain.User;

@Repository
public interface UserMapper {
	int deleteByPrimaryKey(Integer id);

	int insert(User record);

	int insertSelective(User record);

	User selectById(Integer id);

	int updateByPrimaryKeySelective(User record);

	int updateByPrimaryKey(User record);
}