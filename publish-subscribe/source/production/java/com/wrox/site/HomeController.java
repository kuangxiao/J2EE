package com.wrox.site;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.RequestMapping;

import com.wrox.config.annotation.WebController;

@WebController
public class HomeController {
	@Inject
	ApplicationEventPublisher publisher;

	@RequestMapping("")
	public String login(HttpServletRequest request) {
		this.publisher.publishEvent(new LoginEvent(request.getRemoteAddr()));
		return "login";
	}

	@RequestMapping("/logout")
	public String logout(HttpServletRequest request) {
		this.publisher.publishEvent(new LogoutEvent(request.getRemoteAddr()));
		return "logout";
	}
}
