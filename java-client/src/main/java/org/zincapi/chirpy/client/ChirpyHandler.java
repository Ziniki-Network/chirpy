package org.zincapi.chirpy.client;

import org.zincapi.ResponseHandler;

public abstract class ChirpyHandler implements ResponseHandler {
	private boolean isReady;
	
	// TODO: this needs to be more sophisticated than this to handle:
	// No records being returned just yet (wait less time)
	// Multiple records being returned (keep waiting for a while longer)
	// Being called multiple times
	public void waitForReady() {
		synchronized (this) {
			if (isReady)
				return;
			try {
				this.wait(1000);
			} catch (Throwable t) {
			}
		}
	}
	
	protected void makeReady() {
		synchronized (this) {
			isReady = true;
			this.notifyAll();
		}
	}
}
