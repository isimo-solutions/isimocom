	package com.isimo.debug;

import com.isimo.core.SpringContext;
import com.isimo.core.TestCases;
import com.isimo.core.TestExecutionManager;
import com.isimo.core.event.Event;

public class DebugExecutionDebuggerThread extends DebugThreadBase<Event, DebuggerResponse> {
	ExecutionState state = null;
	public DebugExecutionDebuggerThread(int pPort) {
		super(pPort);
	}
	
	@Override
	public void run(){
		try {
			while(true) {
				try {
					waitForClient();
					while(true) {
						DebuggerEvent event = (DebuggerEvent) receiveFromSocket(DebuggerEvent.class);
						SpringContext.getBean(DebugExecutionController.class, "debugExecutionController").handleDebuggerEvent(event);
						publishToSocket(new DebuggerResponse());
					}
				} catch(Exception e) {
					throw new RuntimeException(e);
				}
			}		
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
}
