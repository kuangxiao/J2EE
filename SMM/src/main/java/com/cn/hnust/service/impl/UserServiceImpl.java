package com.cn.hnust.service.impl;

import com.cn.hnust.service.IUserService;
import com.cn.hnust.service.dao.UserMapper;
import com.cn.hnust.service.domain.User;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("userService")
public class UserServiceImpl implements IUserService {
	
	@Resource
	private UserMapper userMapper;

	public User getUserById(int userId) {
		return userMapper.selectById(userId);
	}

}
