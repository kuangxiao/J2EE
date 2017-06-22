package test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Log4jTest {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Log4jTest.class);

	public static void main(String[] args) {		
		LOGGER.trace("trace!!!"); 
		LOGGER.debug("debug!!!"); 
		LOGGER.info("info!!!"); 
		LOGGER.warn("warn!!!"); 
		LOGGER.error("error!!!");
	}

}
