package com.isimo.debug;

import com.isimo.core.event.EventType;
import com.isimo.core.event.ExecutionListener;

public class DebugExecutionFeedbackThread extends DebugThreadBase<FeedbackEvent, FeedbackResponse> implements ExecutionListener<FeedbackEvent> {
	public DebugExecutionFeedbackThread(int pPort) {
		super(pPort);
	}

	@Override
	public void run(){
		try {
			while(true) {
				try {
					waitForClient();
					while(true) {
						FeedbackEvent event = eventQueue.take();
						publishToSocket(event);
						receiveFromSocket(FeedbackResponse.class);
						if(event.getEventType().equals(EventType.Terminated))
							return;
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
