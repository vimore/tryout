package com.securityx.ise;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cisco.pxgrid.GridConnection.Listener;

public class ISEConnectionListener implements Listener {
	
	public static final Logger logger = LoggerFactory.getLogger(ISEConnectionListener.class);
	private boolean connected;
	
	@Override
	public void beforeConnect() {
		logger.debug("connecting...");
	}

	@Override
	public void onConnected() {
		logger.debug("connected.");
		this.connected = true;
	}

	@Override
	public void onDisconnected() {
		if (this.connected) {
			logger.debug("connection closed");
			this.connected = false;
		}
	}

	@Override
	public void onDeleted() {
		logger.debug("account deleted");
	}

	@Override
	public void onDisabled() {
		logger.debug("account disabled");
	}

	@Override
	public void onEnabled() {
		logger.debug("account enabled");
	}

	@Override
	public void onAuthorizationChanged() {
		logger.debug("authorization changed");
	}
}
