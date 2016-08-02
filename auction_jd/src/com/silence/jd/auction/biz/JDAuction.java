package com.silence.jd.auction.biz;

import java.io.IOException;
import java.util.Random;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.HttpUnitOptions;
import com.meterware.httpunit.PostMethodWebRequest;
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

	private static Log log = LogFactory.getLog(JDAuction.class);
	/**
	 * 使用同一个conversation可以自动保持session
	 */
	private static WebConversation conversation = new WebConversation();
	public static WebResponse webResponse = null;
	public static Long lastLoginTime = null;
	public static boolean isMyPrice = false;
	public static boolean isExceededMaxPrice = false;

	public transient int currentPrice = 0;
	public transient int auctionStatus = 0; // 拍卖状态：0-未开始；1-正在进行；2-结束或一口价；
	public transient int myPrice = 0;
	public transient int stockNum = 0;// 库存
	public transient long remainTime = 0;// 剩余时间（毫秒）：-1-已结束 ；

	private String uuid = "a7d68c97-06fe-41c2-a922-a166adfb4960"; // UUID.randomUUID().toString();

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
		conversation.setHeaderField("User-Agent",
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
		log.info("--- in queryAuctionInfo(" + getPaimaiId() + "[" + getMaxPrice() + "]" + ") XHR get ---");

		// http://dbditem.jd.com/json/current/englishquery?paimaiId=12863687&skuId=0&t=265688&start=0&end=9
		String urlParams = "?paimaiId=" + getPaimaiId() + "&skuId=0&t=" + getRamdomNumber() + "&start=0&end=4";

		WebRequest bidRecordReq = new GetMethodWebRequest(AuctionConstant.URL_BID_RECORDS + urlParams);
		bidRecordReq.setHeaderField("Host", "dbditem.jd.com");
		bidRecordReq.setHeaderField("Referer", "http://dbditem.jd.com/" + paimaiId);
		bidRecordReq.setHeaderField("X-Requested-With", "XMLHttpRequest");
		setCookie(bidRecordReq);

		try {
			webResponse = conversation.getResponse(bidRecordReq);
			String recordJsonStr = webResponse.getText();
			log.info("recordJsonStr==>" + recordJsonStr);
			JSONObject jsonObj = JSONObject.fromObject(recordJsonStr);
			currentPrice = jsonObj.optInt("currentPrice", 0);
			auctionStatus = jsonObj.optInt("auctionStatus", 0);
			remainTime = jsonObj.optInt("remainTime", 0);
			stockNum = jsonObj.optInt("stockNum", 0);
			if (currentPrice > this.getMaxPrice()) {
				isExceededMaxPrice = true;
			}
			log.info("*** in queryAuctionInfo(): currentPrice = " + currentPrice + ", auctionStatus=" + auctionStatus
					+ ", remainTime=" + remainTime + "(" + timeBetweenText(remainTime) + "), stockNum=" + stockNum);

			return currentPrice;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return 0;
	}

	/**
	 * 加价
	 * 
	 * {"message":"出价成功","result":200} {"message":"不能低于当前价","result":561}
	 * 
	 * @return
	 */
	public boolean increPrice() {
		log.info("--- in increPrice(" + getPaimaiId() + "[" + getMaxPrice() + "]" + ") XHR get ---");

		boolean result = false;
		int responseCode = 0;
		int newPrice = getNewPrice();
		// http://dbditem.jd.com/services/bid.action?t=411891&paimaiId=12866823&price=1002&proxyFlag=0&bidSource=0
		String urlParams = "?t=" + getRamdomNumber() + "&paimaiId=" + getPaimaiId() + "&price=" + newPrice
				+ "&proxyFlag=0&bidSource=0";
		WebRequest addPriceReq = new GetMethodWebRequest(AuctionConstant.URL_INCRE_PRICE + urlParams);
		addPriceReq.setHeaderField("Host", "dbditem.jd.com");
		addPriceReq.setHeaderField("Referer", "http://dbditem.jd.com/" + paimaiId);
		addPriceReq.setHeaderField("X-Requested-With", "XMLHttpRequest");
		setCookie(addPriceReq);
		try {
			webResponse = conversation.getResponse(addPriceReq);
			String responseText = webResponse.getText();
			log.debug("responseText==>" + responseText);
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
			log.error(e.getMessage(), e);
		}

		log.info("*** in increPrice(): result=" + result + ", myPrice == newPrice(" + newPrice + ")" + ", responseCode==>"
				+ responseCode);

		return result;
	}	

	/**
	 * 不断地出价 有没结束可以通过 1- 时间比较判断；2-状态（auctionStatus==2）判断。
	 * 注意：auctionStatus==0可能已经开始了！
	 */
	public void bid() {
		log.info("--- in bid(" + getPaimaiId() + "[" + getMaxPrice() + "]" + ") ---");

		while (auctionStatus == 1 && !isExceededMaxPrice) {
			increPriceAsync();
			try {
				Thread.sleep(AuctionConstant.BIDDING_SLEEP_TIME);
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
			}
		}

		// 拍卖结束
		log.info("*** in bid(): bid over! auctionStatus=" + auctionStatus + ", isExceededMaxPrice(" + currentPrice + ")="
				+ isExceededMaxPrice + ", isMyPrice(" + myPrice + ")==>" + isMyPrice());
	}

	/**
	 * 新建一个线程出价一次
	 */
	public void increPriceAsync() {
		new Thread(new Runnable() {
			public void run() {
				// 禁止连续出价及频繁出价，必须确保每次投标的价格有效
				queryAuctionInfo();
				if (auctionStatus == 1 && !isMyPrice() && !isExceededMaxPrice) {
					increPrice();
				} else {
					log.info("*** in increPriceAsync(): give up! auctionStatus=" + auctionStatus + ", isExceededMaxPrice("
							+ currentPrice + ")=" + isExceededMaxPrice + ", isMyPrice(" + myPrice + ")==>" + isMyPrice());
				}
			}

		}).start();
	}
	
	public int getNewPrice() {
		int inc = new Random().nextInt(getIncrementPerTime());
		if (inc < 1) {
			inc = 1;
		}

		return currentPrice + inc;
	}

	public boolean isMyPrice() {
		return currentPrice <= myPrice;
	}

	/**
	 * TODO:需要设置此Cookie及 请求头部"Referer"才能正确登录。
	 * 
	 * @Title login
	 * @param _t
	 * @return
	 * @author 王涛(Silence)
	 * @date 2016年7月18日 下午1:55:19
	 */
	public Long login(String _t) {
		log.info("---------- in login() post ----------");

		Long loginTime = null;
		// https://passport.jd.com/uc/loginService?uuid=fc001e14-0cdf-42eb-823a-c5301ce78fb8&&r=0.9680759462401334&version=2015
		String loginUrlParams = "?uuid=" + uuid + "&r=" + getRamdomNumber() + "&version=2015";
		WebRequest loginReq = new PostMethodWebRequest(AuctionConstant.URL_LOGIN_SUBMIT + loginUrlParams);
		loginReq.setHeaderField("Referer", "https://passport.jd.com/uc/login");
		loginReq.setHeaderField("Cookie", cookie);

		try {
			loginReq.setParameter("machineNet", "");
			loginReq.setParameter("machineCpu", "");
			loginReq.setParameter("machineDisk", "");
			loginReq.setParameter("eid",
					"21DE77D31C7BBEF5BA1EB1C3E7E072EEDC3D58637113E31ED4C3AFA9CBBFBE8746867DC2B474EB5D8F1714D9A8B7F022");
			loginReq.setParameter("fp", "aa07ef3d4f2548726b66170b7adfffde");
			loginReq.setParameter("_t", "_ntJnQeh");
			loginReq.setParameter("hPFFVwKsze", "FXaLM");

			loginReq.setParameter("chkRememberUsername", "on");
			loginReq.setParameter("loginname", AuctionConstant.LOGIN_NAME);
			loginReq.setParameter("nloginpwd", AuctionConstant.LOGIN_PWD);
			loginReq.setParameter("loginpwd", AuctionConstant.LOGIN_PWD);
			loginReq.setParameter("chkRememberMe", "on");
			loginReq.setParameter("authcode", "");

			webResponse = conversation.getResponse(loginReq);
			String loginCookie = webResponse.getHeaderField("Set-Cookie");
			cookie += loginCookie;

			String responseText = webResponse.getText();
			log.debug("responseText==>" + responseText);
			if (responseText != null && !"".equals(responseText)) {
				// 登录失败
				JSONObject jsonObj = JSONObject.fromObject(responseText.replace("(", "").replace(")", ""));
				_t = jsonObj.optString("_t", "");
				login(_t);
			} else {
				loginTime = System.currentTimeMillis();
				lastLoginTime = loginTime;
				log.info("lastLoginTime==>" + lastLoginTime);
			}

		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} catch (SAXException e) {
			log.error(e.getMessage(), e);
		}

		return loginTime;
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

		if (hours > 0) {
			return rightZeroStr(hours) + "时" + rightZeroStr(minutes) + "分" + rightZeroStr(seconds) + "秒";
		} else if (minutes > 0) {
			return rightZeroStr(minutes) + "分" + rightZeroStr(seconds) + "秒";
		} else if (seconds > 0) {
			return rightZeroStr(seconds) + "秒";
		} else {
			return "0秒";
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
		log.info("---------- in Shutdown() ----------");

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
			log.error(e);
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
	 * 每次最少加价 单位 元
	 */
	private int incrementPerTime = 1;

	/**
	 * 提前时间（毫秒）
	 */
	private long aheadTime = 1500;

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
