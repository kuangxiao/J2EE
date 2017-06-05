package com.wrox.site;

public class LogoutEvent extends AuthenticationEvent {
	
	private static final long serialVersionUID = 1L;

	public LogoutEvent(String username) {
		super(username);
	}
	
}
