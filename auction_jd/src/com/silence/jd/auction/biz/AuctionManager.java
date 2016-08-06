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

	protected static Log log = LogFactory.getLog(AuctionManager.class);

	final static int THREAD_POOL_SIZE = 5;
	final static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(THREAD_POOL_SIZE);	
	static JDAuction jda = null;

	public static void main(String[] args) {

		ApplicationContext context = new ClassPathXmlApplicationContext(new String[] { "classpath*:auction_spring.xml" });

		jda = (JDAuction) context.getBean("auction");
		initAuction();
		if (args.length > 0 && "test".equalsIgnoreCase(args[0])) {
			jda.bidOnce();
		}
	}

	public static void initAuction() {
		log.info("--- in initAuction(" + jda.getPaimaiId() + "[" + jda.getMaxPrice() + "]" + ") ---");

		jda.queryAuctionInfo();
		switch (jda.auctionStatus) {
		case 0:
			// 尚未开始
			scheduler.schedule(new Runnable() {
				@Override
				public void run() {
					initAuction();
				}
			}, jda.remainTime + 99000L, TimeUnit.MILLISECONDS);
			log.info("*** in initAuction(): after " + JDAuction.timeBetweenText(jda.remainTime + 59000L) + " init again ***");
			break;
			
		case 1:
			// 进行中
			AuctionConstant.BIDDING_DEADLINE = System.currentTimeMillis() + jda.remainTime - AuctionConstant.BIDDING_RETENTION_TIME;
			scheduler.schedule(new Runnable() {
				@Override
				public void run() {
					jda.bid();
				}
			}, jda.remainTime - jda.getAheadTime(), TimeUnit.MILLISECONDS);
			log.info("*** in initAuction(): after " + JDAuction.timeBetweenText(jda.remainTime - jda.getAheadTime())
					+ " bid() ***");
			break;
		case 2:
			// 已经结束
		default:
			log.info("*** in initAuction(): bid over! shutdown scheduler ***");
			scheduler.shutdown();
			System.out.println("press enter to exit >>>");
			try {
				new BufferedReader(new InputStreamReader(System.in)).readLine();
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
			System.exit(0);
			break;
		}
	}

}
