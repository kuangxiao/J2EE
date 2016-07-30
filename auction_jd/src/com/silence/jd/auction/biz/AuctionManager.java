package com.silence.jd.auction.biz;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AuctionManager {

	protected static Log log = LogFactory.getLog(AuctionManager.class);
	
	final static int THREAD_POOL_SIZE = 2;
	final static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(THREAD_POOL_SIZE);		
	static JDAuction jda = null;	

	public static void main(String[] args) {

		ApplicationContext context = new ClassPathXmlApplicationContext(new String[] { "classpath*:auction_spring.xml" });

		jda = (JDAuction) context.getBean("auction");
		initAuction();
		if (args.length > 0 && "test".equalsIgnoreCase(args[0])) {
			jda.increPriceAsync();
		}
	}

	public static void initAuction() {
		log.info("--- in initAuction("+jda.getPaimaiId()+"["+jda.getMaxPrice()+"]"+") ---");		

		jda.queryAuctionInfo();
		switch (jda.auctionStatus) {
		case 0:
			// 尚未开始		
			scheduler.schedule(new Runnable(){
				@Override
				public void run() {
					initAuction();
				}				
			}, jda.remainTime + 59000L, TimeUnit.MILLISECONDS);
			break;
		case 1:
			// 进行中
			scheduler.schedule(new Runnable(){
				@Override
				public void run() {
					jda.bid();
				}				
			}, jda.remainTime - jda.getAheadTime(), TimeUnit.MILLISECONDS);
			break;
		case 2:
			// 已经结束				
		default:
			log.info("*** in initAuction(): bid over! ***");
			break;			
		}		
	}
	
}
