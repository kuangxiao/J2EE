package com.cn.hnust.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.cn.hnust.service.IUserService;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/acct")
@Api(value = "用户", description = "关于用户的CURD操作")
public class UserController {

	@Resource
	private IUserService userService;

	/**
	 * 根据用户名获取用户对象
	 * 
	 * @param name
	 * @return
	 */
	@RequestMapping(value = "/users/{name}", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ApiOperation(value = "根据用户名获取用户对象", httpMethod = "GET", response = String.class, notes = "详细描述：1.name;2.password")
	public String getUserByName(@ApiParam(required = true, name = "name", value = "用户名") @PathVariable String name)
			throws Exception {

		return "Your name is:" + name;
	}

	@RequestMapping(value = "/users/{id}", method = RequestMethod.DELETE, produces = "application/json; charset=utf-8")
	@ApiOperation(value = "删除指定ID的用户", httpMethod = "DELETE", notes = "删除指定ID用户")
	public String delUserById(@ApiParam(required = true, name = "id", value = "用户ID") @PathVariable String id)
			throws Exception {

		return "Your input id:" + id;
	}

	@RequestMapping(value = "/users", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ApiOperation(value = "新增用户", notes = "管理员登录后方可操作。")
	public String addUser(
			@ApiParam(required = true, name = "postData", value = "用户信息json数据") @RequestParam(value = "postData") String postData,
			HttpServletRequest request) throws JSONException {
		JSONObject json = new JSONObject();
		json.put("postData", postData);
		return json.toString();
	}

}
