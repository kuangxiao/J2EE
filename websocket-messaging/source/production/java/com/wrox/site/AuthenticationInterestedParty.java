package com.wrox.site;

import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationInterestedParty implements ApplicationListener<AuthenticationEvent> {
	
	private static final Logger log = LogManager.getLogger();

	@Inject
	ServletContext servletContext;

	@Override
	public void onApplicationEvent(AuthenticationEvent event) {
		log.info("Authentication event from context {} received in context {}.", event.getSource(),
				this.servletContext.getContextPath());
	}
}
