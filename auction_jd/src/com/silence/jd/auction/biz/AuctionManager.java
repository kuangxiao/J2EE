package com.silence.jd.auction.biz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AuctionManager {

	protected static Log logger = LogFactory.getLog(AuctionManager.class);
	
	final static int THREAD_POOL_SIZE = 4;
	final static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(THREAD_POOL_SIZE);
	static JDAuction jda = null;

	public static void main(String[] args) {

		ApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "classpath*:auction_spring.xml" });

		jda = (JDAuction) context.getBean("auction");
		initAuction();
		if (args.length > 0 && "test".equalsIgnoreCase(args[0])) {
			jda.bidOnce();
		}
	}

	public static void initAuction() {
		logger.info("--- in initAuction(" + jda.getPaimaiId() + "[" + jda.getMode() + ","+ jda.getMaxPrice() + "]" + ") ---");

		jda.queryAuctionInfo();
		switch (jda.auctionStatus) {
		case 0:
			// 尚未开始
			scheduleInition();
			break;

		case 1:
			// 进行中
			if (jda.getMode() == 0) {// 询价投标模式
				scheduleQueryPrice();
			}
			scheduleFirstBid();
			break;

		case 2:
			// 已经结束
		default:
			logger.info("*** in initAuction(): bid over! shutdown scheduler ***");
			scheduler.shutdown();
			logger.warn("press enter to exit >>>");
			try {
				new BufferedReader(new InputStreamReader(System.in)).readLine();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
			System.exit(0);
			break;
		}
	}

	public static void scheduleInition() {
		scheduler.schedule(new Runnable() {
			@Override
			public void run() {
				initAuction();
			}
		}, jda.remainTime + 99000L, TimeUnit.MILLISECONDS);
		logger.info("*** in initAuction(): after " + JDAuction.timeBetweenText(jda.remainTime + 59000L)
				+ " init again ***");
	}

	public static void scheduleQueryPrice() {
		long startQueryTime = jda.remainTime - jda.getAheadTime();
		scheduler.schedule(new Runnable() {
			@Override
			public void run() {
				jda.queryCurrentPrice();
			}
		}, startQueryTime, TimeUnit.MILLISECONDS);
		logger.info("*** in initAuction(): after " + startQueryTime + "[" + JDAuction.timeBetweenText(startQueryTime)
				+ "]" + " queryCurrentPrice() ***");
	}

	public static void scheduleFirstBid() {
		long firtBidTime = jda.remainTime - AuctionConstant.BIDDING_RETENTION_TIME;
		scheduler.schedule(new Runnable() {
			@Override
			public void run() {
				jda.bid();
			}
		}, firtBidTime, TimeUnit.MILLISECONDS);
		logger.info("*** in initAuction(): after " + firtBidTime + "[" + JDAuction.timeBetweenText(firtBidTime) + "]"
				+ " do first bid() ***");
	}

	public static void scheduleSecondBid() {
		long secondBidTime = jda.remainTime - AuctionConstant.BIDDING_RETENTION_TIME + 511L;
		scheduler.schedule(new Runnable() {
			@Override
			public void run() {
				jda.bid();
			}
		}, secondBidTime, TimeUnit.MILLISECONDS);
		logger.info("*** in initAuction(): after " + secondBidTime + "[" + JDAuction.timeBetweenText(secondBidTime) + "]"
				+ " do second bid() ***");
	}
}
