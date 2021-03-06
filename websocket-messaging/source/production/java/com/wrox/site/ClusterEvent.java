package com.wrox.site;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import org.springframework.context.ApplicationEvent;

public class ClusterEvent extends ApplicationEvent implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Serializable serializableSource;
	private boolean rebroadcasted;

	public ClusterEvent(Serializable source) {
		super(source);
		this.serializableSource = source;
	}

	final boolean isRebroadcasted() {
		return this.rebroadcasted;
	}

	final void setRebroadcasted() {
		this.rebroadcasted = true;
	}

	@Override
	public Serializable getSource() {
		return this.serializableSource;
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		this.source = this.serializableSource;
	}

}
