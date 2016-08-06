package test.schedule;

import java.text.ParseException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Test {  
	
	final static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10); 
    
    public static void main(String[] args) throws ParseException { 
    	
    	scheduler.scheduleWithFixedDelay(new Runnable() {			
    		public void run() {  
         	   try {  
             	   System.out.println("execute task!"+ System.currentTimeMillis());  
                    Thread.sleep(6000);  
                } catch (InterruptedException e) {  
                    e.printStackTrace();  
                }                 
            } 
             
        },0L, 3L * 1000,TimeUnit.MILLISECONDS);  
    }  
      
}  
