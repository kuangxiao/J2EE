package com.wrox.site;

public class LoginEvent extends AuthenticationEvent {
	
	private static final long serialVersionUID = 1L;

	public LoginEvent(String username) {
		super(username);
	}
	
}
