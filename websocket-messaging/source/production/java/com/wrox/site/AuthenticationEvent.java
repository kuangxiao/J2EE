package com.wrox.site;

import java.io.Serializable;

public abstract class AuthenticationEvent extends ClusterEvent {

	private static final long serialVersionUID = 1L;

	public AuthenticationEvent(Serializable source) {
		super(source);
	}
}
