package com.yihaomen.annotation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogAspect {

	private static final Logger log = LoggerFactory.getLogger(LogAspect.class);

	@Around(value = "@annotation(com.yihaomen.annotation.Log) and args(ia,sa,ib,sb)")
	public Object doLogging(ProceedingJoinPoint pjp, int ib,String sb,int ia,String sa) throws Throwable {

		log.info("class name: " + pjp.getSignature().getDeclaringType().getName());
		log.info("method name: " + pjp.getSignature().getName());
		log.info("ia = " + ia + ", sb = " + sb + ", ib = " + ib + ", sa = " + sa);

		for (Object obj : pjp.getArgs()) {
			log.info("parameters: " + obj);
		}

		Object result = pjp.proceed();
		log.info("end of exec function");

		return result;
	}
}
