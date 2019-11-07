package com.isimo.debug;

import java.util.ArrayList;
import java.util.List;

import com.isimo.core.Action;
import com.isimo.core.event.Event;
import com.isimo.core.event.EventType;
import com.isimo.core.event.StackTrace;

public class FeedbackEvent extends Event {
	StackTrace stackTrace;
	List<Breakpoint> breakpointsHit = new ArrayList<Breakpoint>();
	
	

	
	public static FeedbackEvent problem(Action action, String message) {
		FeedbackEvent fe = new FeedbackEvent(FeedbackEventType.Problem, action, "actionName", action.getDefinitionOrig().getName());
		fe.getMetadata().put("message", message);
		return fe;
	}
	
	
	public static FeedbackEvent breakpointHit(Action action, List<Breakpoint> bps) {
		FeedbackEvent fe = new FeedbackEvent(FeedbackEventType.BreakpointHit, action);
		fe.breakpointsHit = bps;
		return fe;
	}
	
	
	
	private FeedbackEvent() {
		super();
	}

	public FeedbackEvent(EventType et, Action action, String... metadataValues) {
		super(metadataValues);
		setEventType(et);
		setCurrentAction(action);
		stackTrace = StackTrace.getStacktraceFromAction(action);
	}


	public StackTrace getStackTrace() {
		return stackTrace;
	}

	public void setStackTrace(StackTrace pStackTrace) {
		stackTrace = pStackTrace;
	}
}
