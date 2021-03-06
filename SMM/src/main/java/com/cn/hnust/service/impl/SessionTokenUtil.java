package com.cn.hnust.service.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 会话令牌工具类， 是一个单例类。
 */
public class SessionTokenUtil {
	
	static final String TOKEN_KEY = "com.mogoroom.session.token";

	private static SessionTokenUtil instance = new SessionTokenUtil();

	/**
	 * 最近一次生成Token的时间戳。
	 */
	private long previous;

	/**
	 * getInstance()方法得到单例类的实例。
	 */
	public static SessionTokenUtil getInstance() {
		return instance;
	}

	/**
	 * 判断请求参数中的Token是否有效。
	 */
	public synchronized boolean isTokenValid(HttpServletRequest request) {
		// 得到请求的当前Session对象。
		HttpSession session = request.getSession(false);
		if (session == null) {
			return false;
		}

		// 从Session中取出保存的Token。
		String saved = (String) session.getAttribute(TOKEN_KEY);
		if (saved == null) {
			return false;
		}

		// 清除Session中的Token。
		resetToken(request);

		// 得到请求参数中的Token。
		String token = request.getParameter(TOKEN_KEY);
		if (token == null) {
			return false;
		}

		return saved.equals(token);
	}

	/**
	 * 清除Session中的Token。
	 */
	public synchronized void resetToken(HttpServletRequest request) {

		HttpSession session = request.getSession(false);
		if (session == null) {
			return;
		}
		session.removeAttribute(TOKEN_KEY);
	}

	/**
	 * 产生一个新的Token，保存到Session中， 如果当前Session不存在，则创建一个新的Session。
	 */
	public synchronized void saveToken(HttpServletRequest request) {

		HttpSession session = request.getSession();
		String token = generateToken(request);
		if (token != null) {
			session.setAttribute(TOKEN_KEY, token);
		}

	}

	/**
	 * 根据用户会话ID和当前的系统时间生成一个唯一的Token。
	 */
	public synchronized String generateToken(HttpServletRequest request) {

		HttpSession session = request.getSession();
		try {
			byte id[] = session.getId().getBytes();
			long current = System.currentTimeMillis();
			if (current == previous) {
				current++;
			}
			previous = current;
			byte now[] = new Long(current).toString().getBytes();
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(id);
			md.update(now);
			return toHex(md.digest());
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}

	/**
	 * 将一个字节数组转换为一个十六进制数字的字符串。
	 */
	private String toHex(byte buffer[]) {
		StringBuffer sb = new StringBuffer(buffer.length * 2);
		for (int i = 0; i < buffer.length; i++) {
			sb.append(Character.forDigit((buffer[i] & 0xf0) >> 4, 16));
			sb.append(Character.forDigit(buffer[i] & 0x0f, 16));
		}
		return sb.toString();
	}

	/**
	 * 从Session中得到Token，如果Session中没有保存Token，则生成一个新的Token。
	 */
	public synchronized String getToken(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (null == session){
			return null;
		}			
		String token = (String) session.getAttribute(TOKEN_KEY);
		if (null == token) {
			token = generateToken(request);
			if (token != null) {
				session.setAttribute(TOKEN_KEY, token);
				return token;
			} else{
				return null;
			}				
		} else{
			return token;
		}			
	}

}
