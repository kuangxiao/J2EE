package com.silence.jd.auction.biz;

import java.util.Random;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.HttpUnitOptions;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

/**
 * auction main class
 * 
 * @author Silence
 * 
 */
public class JDAuction {

	private static Log logger = LogFactory.getLog(JDAuction.class);
	/**
	 * 使用同一个conversation可以自动保持session
	 */
	public static WebConversation conversation = new WebConversation();
	public static WebResponse webResponse = null;
	public static Long lastLoginTime = null;
	public static boolean isMyPrice = false;
	public static boolean isExceededMaxPrice = false;

	public transient int initialPrice = 1;
	public transient int currentPrice = 1;
	public transient int auctionStatus = 0; // 拍卖状态：0-未开始；1-正在进行；2-结束或一口价；
	public transient int myPrice = 1;
	public transient int stockNum = 0;// 库存
	public transient long remainTime = 0;// 剩余时间（毫秒）：-1-已结束 ；

	public String jsonp = "jsonp";
	public int jsonpCount = 1;

	/**
	 * 禁用js，防止自动跳转，全部使用手工提交
	 */
	static {
		HttpUnitOptions.setScriptingEnabled(false);
		conversation.setHeaderField("Accept", "text/plain, */*; q=0.01");
		conversation.setHeaderField("Accept-Encoding", "gzip, deflate");
		conversation.setHeaderField("Accept-Charset", "x-gbk,utf-8;q=0.7,*;q=0.7");
		conversation.setHeaderField("Accept-Language", "zh-cn,zh;q=0.8");
		conversation.setHeaderField("Connection", "Keep-Alive");
		conversation.setHeaderField("Pragma", "	no-cache");
		conversation.setHeaderField("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		conversation
				.setHeaderField("User-Agent",
						"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.87 Safari/537.36");
	}

	/**
	 * 设置 Cookie
	 * 
	 * @param request
	 */
	public void setCookie(WebRequest request) {
		request.setHeaderField("Cookie", getCookie());
	}

	/**
	 * 查询拍卖详情。
	 * 
	 * @return
	 */
	public int queryAuctionInfo() {
		logger.info("--- in queryAuctionInfo(" + getPaimaiId() + "[" + getMode() + "," + getMaxPrice() + "]" + ") XHR get ---");		

		// http://dbditem.jd.com/json/current/englishquery?paimaiId=12863687&skuId=0&t=265688&start=0&end=9
		String urlParams = "?paimaiId=" + getPaimaiId() + "&skuId=0&t=" + getRamdomNumber() + "&start=0&end=4";

		WebRequest bidRecordReq = new GetMethodWebRequest(AuctionConstant.URL_BID_RECORDS + urlParams);
		bidRecordReq.setHeaderField("Host", "dbditem.jd.com");
		bidRecordReq.setHeaderField("Connection", "keep-alive");
		bidRecordReq.setHeaderField("Referer", "http://dbditem.jd.com/" + paimaiId);
		bidRecordReq.setHeaderField("X-Requested-With", "XMLHttpRequest");
		setCookie(bidRecordReq);

		try {
			webResponse = conversation.getResponse(bidRecordReq);
			String recordJsonStr = webResponse.getText();
			logger.info("recordJsonStr==>" + recordJsonStr);
			JSONObject jsonObj = JSONObject.fromObject(recordJsonStr);
			int tempCurrentPrice = jsonObj.optInt("currentPrice", 1);
			if (tempCurrentPrice > currentPrice) {
				currentPrice = tempCurrentPrice;
				if (currentPrice > getMaxPrice()) {
					isExceededMaxPrice = true;
				}
			}
			auctionStatus = jsonObj.optInt("auctionStatus", 1);
			remainTime = jsonObj.optInt("remainTime", 1);
			stockNum = jsonObj.optInt("stockNum", 1);

			logger.info("*** in queryAuctionInfo(): currentPrice=" + currentPrice + ", auctionStatus=" + auctionStatus
					+ ", remainTime=" + remainTime + "(" + timeBetweenText(remainTime) + "), stockNum=" + stockNum);

			return currentPrice;

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return 1;
	}

	public int queryAuctionInfoM() {			
		logger.info("--- in queryAuctionInfoM(" + getPaimaiId() + "[" + getMode() + "," + getMaxPrice() + "]" + ") XHR get ---");
		
		// http://dbauction.jd.com/paimai/json/current/englishquery?paimaiId=13042813&skuId=0&start=0&end=4&_t=1470551762363&_=1470551762364&callback=jsonp34
		String urlParams = "?paimaiId=" + getPaimaiId() + "&skuId=0&start=0&end=4&_t=" + System.currentTimeMillis()
				/ 1000 + "&_=" + System.currentTimeMillis() / 1000 + "&callback=jsonp" + (jsonpCount++);

		WebRequest bidRecordReq = new GetMethodWebRequest(AuctionConstant.URL_BID_RECORDS_M + urlParams);
		bidRecordReq.setHeaderField("Accept", "*/*");
		bidRecordReq.setHeaderField("Accept-Encoding", "gzip, deflate, sdch");
		bidRecordReq.setHeaderField("Accept-Language", "zh-CN,zh;q=0.8");
		bidRecordReq.setHeaderField("Connection", "keep-alive");
		bidRecordReq.setHeaderField("Host", "dbauction.jd.com");
		bidRecordReq.setHeaderField("Referer","http://duobao.m.jd.com/duobao/item/detail.html?paimaiId="
								+ getPaimaiId()
								+ "&auctionType=5&from=jingdou&resourceType=jdapp_share&resourceValue=ShareMore&utm_source=androidapp&utm_medium=appshare&utm_campaign=t_335139774&utm_term=ShareMore");
		bidRecordReq.setHeaderField("User-Agent",
						"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36");
		setCookie(bidRecordReq);

		try {
			webResponse = conversation.getResponse(bidRecordReq);
			String recordJsonStr = webResponse.getText();
			logger.info("recordJsonStr==>" + recordJsonStr);
			JSONObject jsonObj = JSONObject.fromObject(recordJsonStr.substring(recordJsonStr.indexOf('(') + 1,
					recordJsonStr.indexOf(')')));
			JSONObject jsonData = jsonObj.optJSONObject("data");
			int tempCurrentPrice = jsonData.optInt("currentPrice", 1);
			if (tempCurrentPrice > currentPrice) {
				currentPrice = tempCurrentPrice;
				if (currentPrice > getMaxPrice()) {
					isExceededMaxPrice = true;
				}
			}
			auctionStatus = jsonData.optInt("auctionStatus", 1);
			remainTime = jsonData.optInt("remainTime", 1);
			stockNum = jsonData.optInt("stockNum", 1);
			logger.info("*** in queryAuctionInfoM(): currentPrice=" + currentPrice + ", auctionStatus=" + auctionStatus
					+ ", remainTime=" + remainTime + "(" + timeBetweenText(remainTime) + "), stockNum=" + stockNum);

			return currentPrice;

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return 1;
	}

	/**
	 * 投标。
	 * 
	 * {"message":"出价成功","result":200}
	 * {"message":"不能低于当前价","result":561}
	 * 
	 * @return
	 */
	public boolean bid() {
		boolean result = false;
		int responseCode = 0;
		int newPrice = getNewPrice();
		// http://dbditem.jd.com/services/bid.action?t=411891&paimaiId=12866823&price=1002&proxyFlag=0&bidSource=0
		String urlParams = "?t=" + getRamdomNumber() + "&paimaiId=" + getPaimaiId() + "&price=" + newPrice
				+ "&proxyFlag=0&bidSource=0";
		logger.info("$$$ in bid(" + getPaimaiId() +"[mode=" + getMode() + ",maxPrice=" + getMaxPrice() + ",bidPrice=" + newPrice + "]" + ") $$$");		
		
		WebRequest addPriceReq = new GetMethodWebRequest(AuctionConstant.URL_BID + urlParams);
		addPriceReq.setHeaderField("Host", "dbditem.jd.com");
		addPriceReq.setHeaderField("Connection", "keep-alive");
		addPriceReq.setHeaderField("Referer", "http://dbditem.jd.com/" + paimaiId);
		addPriceReq.setHeaderField("X-Requested-With", "XMLHttpRequest");
		setCookie(addPriceReq);

		try {
			webResponse = conversation.getResponse(addPriceReq);
			String responseText = webResponse.getText();
			logger.debug("responseText==>" + responseText);
			JSONObject jsonObj = JSONObject.fromObject(responseText);
			responseCode = jsonObj.optInt("result", 0);
			switch (responseCode) {
			case 200:// 出价成功
				myPrice = newPrice;
				result = true;
				break;
			default:// 出价失败
				result = false;
				break;
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		logger.info("$$$ in bid(): result=" + result + ", myPrice == newPrice(" + newPrice + ")"
				+ ", responseCode==>" + responseCode);

		if (getMode() != 0) {
			queryCurrentPrice();
		}

		return result;
	}

	/**
	 * 不断地出价 有没结束可以通过 1- 时间比较判断；2-状态（auctionStatus==2）判断。
	 * 注意：auctionStatus==0可能已经开始了！
	 */
	public void queryCurrentPrice() {		
		logger.info("--- in queryCurrentPrice(" + getPaimaiId() + "[" + getMode() + "," + getMaxPrice() + "]" + ") ---");

		while (!isOver()) {

			queryAuctionInfoMAsync();

			try {
				Thread.sleep(AuctionConstant.QUERRING_SLEEP_TIME);
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}
		}

		// 拍卖结束
		logger.info("*** in queryCurrentPrice(): bid over! auctionStatus=" + auctionStatus + ", isExceededMaxPrice("
				+ currentPrice + ")=" + isExceededMaxPrice + ", isMyPrice(" + myPrice + ")==>" + isMyPrice());

	}

	/**
	 * 异步获取拍卖的最新信息。
	 */
	public void queryAuctionInfoMAsync() {		
		logger.info("--- in queryAuctionInfoMAsync(" + getPaimaiId() + "[" + getMode() + "," + getMaxPrice() + "]" + ") ---");

		new Thread(new Runnable() {
			public void run() {
				queryAuctionInfoM();// 此方法会阻塞
			}

		}).start();
	}

	/**
	 * 异步获取拍卖的最新信息。
	 */
	public void queryAuctionInfoAsync() {
		logger.info("--- in queryAuctionInfoAsync(" + getPaimaiId() + "[" + getMode() + "," + getMaxPrice() + "]" + ") ---");

		new Thread(new Runnable() {
			public void run() {
				queryAuctionInfo();// 此方法会阻塞
			}

		}).start();
	}

	/**
	 * 同步出一次价。
	 */
	public void bidOnce() {
		logger.info("--- in bidOnce(" + getPaimaiId() + "[" + getMode() + "," + getMaxPrice() + "]" + ") ---");
		
		int oldMode = getMode();
		setMode(0);
		queryAuctionInfo();// 此方法会阻塞
		bid();
		setMode(oldMode);
	}

	public int getNewPrice() {
		logger.info("--- in getNewPrice(" + getPaimaiId() + "[" + getMode() + "," + getMaxPrice() + "]" + ") ---");
		
		int inc = new Random().nextInt(getIncrementPerTime());
		if (inc < 1) {
			inc = 1;
		}
		if (getMode() == 0) {
			if (currentPrice > getMaxPrice()) {
				return getMaxPrice();
			}
			return currentPrice + inc;
		} else {
			return getMaxPrice();
		}
	}

	public boolean isMyPrice() {
		return currentPrice <= myPrice;
	}

	public boolean isOver() {
		return auctionStatus == 2;
	}

	/**
	 * 生成6位数字随机数。
	 * 
	 * @return
	 */
	public static String getRamdomNumber() {
		String num = "";
		Random random = new Random();
		for (int i = 0; i < 6; i++) {
			num += random.nextInt(10);
		}
		return num;
	}

	public static String timeBetweenText(long remainTime) {
		long dayOfMil = (24L * 60 * 60 * 1000);
		long hourOfMil = 60L * 60 * 1000;
		long minOfMil = 60L * 1000;
		long secOfMil = 1000L;

		long hourOffset = remainTime % dayOfMil;
		long minuteOffset = hourOffset % hourOfMil;
		long seccondOffset = minuteOffset % minOfMil;

		long hours = (long) Math.floor(remainTime / hourOfMil);
		long minutes = (long) Math.floor(minuteOffset / minOfMil);
		long seconds = (long) Math.floor(seccondOffset / secOfMil);
		long mills = (long) seccondOffset % secOfMil;

		if (hours > 0) {
			return rightZeroStr(hours) + "时" + rightZeroStr(minutes) + "分" + rightZeroStr(seconds) + "秒" + mills + "毫秒";
		} else if (minutes > 0) {
			return rightZeroStr(minutes) + "分" + rightZeroStr(seconds) + "秒" + mills + "毫秒";
		} else if (seconds > 0) {
			return rightZeroStr(seconds) + "秒" + mills + "毫秒";
		} else {
			return "0秒" + mills + "毫秒";
		}
	}

	public static String rightZeroStr(long v) {
		if (v < 10) {
			return "0" + v;
		}
		return v + "";
	}

	/**
	 * 关机。
	 */
	public static void shutdown() {
		logger.info("---------- in Shutdown() ----------");

		String osName = System.getProperty("os.name");
		// 60秒后关机
		String windowsShutdownStr = "shutdown -s -t 60";
		String linuxShutdownStr = "shutdown -h -t 60";
		String cmd = "";

		try {
			if (osName.matches("^(?i)Windows.*$")) {
				cmd = "  cmd /c " + windowsShutdownStr;
				Runtime.getRuntime().exec(cmd);
			} else {
				Runtime.getRuntime().exec(cmd);
				cmd = "  " + linuxShutdownStr;
				Runtime.getRuntime().exec(cmd);
			}
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
	}

	/**
	 * 拍卖 ID
	 */
	private int paimaiId = 0;

	/**
	 * 能接受的最大价格
	 */
	private int maxPrice = 0;

	/**
	 * 秒杀模式，0-询价模式（默认）；1-心里价格秒杀模式
	 */
	private int mode = 0;

	/**
	 * 每次最少加价 单位 元
	 */
	private int incrementPerTime = 1;

	/**
	 * 询价提前时间（毫秒）
	 */
	private long aheadTime = 1350;

	private String cookie = "";

	public int getPaimaiId() {
		return paimaiId;
	}

	public void setPaimaiId(int paimaiId) {
		this.paimaiId = paimaiId;
	}

	public int getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(int maxPrice) {
		this.maxPrice = maxPrice;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public int getIncrementPerTime() {
		return incrementPerTime;
	}

	public void setIncrementPerTime(int incrementPerTime) {
		this.incrementPerTime = incrementPerTime;
	}

	public long getAheadTime() {
		return aheadTime;
	}

	public void setAheadTime(long aheadTime) {
		this.aheadTime = aheadTime;
	}

	public String getCookie() {
		return cookie;
	}

	public void setCookie(String cookie) {
		this.cookie = cookie;
	}

}
