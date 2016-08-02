package com.silence.jd.auction.biz;

public class AuctionConstant {

	// get
	public static final String URL_LOGIN_PAGE = "https://passport.jd.com/uc/login";

	// post
	public static final String URL_LOGIN_SUBMIT = "https://passport.jd.com/uc/loginService";

	// get XHR
	// http://dbditem.jd.com/json/current/englishquery?paimaiId=12863687&skuId=0&t=265688&start=0&end=9
	public static final String URL_BID_RECORDS = "http://dbditem.jd.com/json/current/englishquery";

	// get XHR
	// http://dbditem.jd.com/services/bid.action?t=331673&paimaiId=12863864&price=104&proxyFlag=0&bidSource=0
	public static final String URL_INCRE_PRICE = "http://dbditem.jd.com/services/bid.action";

	// 账户信息
	public static final String LOGIN_NAME = "test";
	public static final String LOGIN_PWD = "test";

	// 投标主线程休眠间隔
	public static long BIDDING_SLEEP_TIME = 8L;

	// Session有效时长（毫秒）
	public static long SESSION_TIMEOUT = 20L * 60 * 1000;

}
