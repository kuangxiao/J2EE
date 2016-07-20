package com.silence.jd.auction.biz;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AuctionManager implements Runnable {

	protected static Log log = LogFactory.getLog(AuctionManager.class);
		
	final static int THREAD_POOL_SIZE = 2;
	final static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(THREAD_POOL_SIZE);
	final static AuctionManager am = new AuctionManager();	
	static JDAuction jda = null;	

	public AuctionManager() {

	}
	
	public static void main(String[] args) {

		ApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "classpath*:auction_spring.xml" });
		
		jda = (JDAuction) context.getBean("auction");
		am.initAuction();
		if( args.length > 0 && "test".equalsIgnoreCase(args[0])){
			jda.increPriceAsync();
		} 	    
	}
	
	public void initAuction(){
		log.info(jda.getPaimaiId()+"["+jda.getMaxPrice()+"]"+"--- in initAuction()  ---");
		
		jda.queryAuctionInfo();	
		scheduler.schedule( am,jda.remainTime-jda.getAheadTime(), TimeUnit.MILLISECONDS);				
	}
	

	public void run() {	
		log.info(jda.getPaimaiId()+"["+jda.getMaxPrice()+"]"+"--- in run()  ---");
		
		jda.bid();
//		System.exit(0);
	}

}
