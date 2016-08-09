package com.silence.jd.auction.biz;

public class AuctionConstant {

	// get XHR
	// http://dbditem.jd.com/json/current/englishquery?paimaiId=12863687&skuId=0&t=265688&start=0&end=9
	public static final String URL_BID_RECORDS = "http://dbditem.jd.com/json/current/englishquery";

	// get XHR
	// http://dbauction.jd.com/paimai/json/current/englishquery?paimaiId=13042813&skuId=0&start=0&end=4&_t=1470551762363&_=1470551762364&callback=jsonp34
	public static final String URL_BID_RECORDS_M = "http://dbauction.jd.com/paimai/json/current/englishquery";

	// get XHR
	// http://dbditem.jd.com/services/bid.action?t=331673&paimaiId=12863864&price=104&proxyFlag=0&bidSource=0
	public static final String URL_BID = "http://dbditem.jd.com/services/bid.action";

	// 获取价格主线程休眠间隔
	public static long QUERRING_SLEEP_TIME = 58L;

	// 投标保留时间
	public static long BIDDING_RETENTION_TIME = 1230;

}
