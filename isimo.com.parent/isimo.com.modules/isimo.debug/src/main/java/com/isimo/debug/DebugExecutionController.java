package com.isimo.debug;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.isimo.core.Action;
import com.isimo.core.DefaultExecutionController;
import com.isimo.core.IExecutionController;
import com.isimo.core.IsimoProperties;
import com.isimo.core.TestExecutionManager;
import com.isimo.core.event.Event;
import com.isimo.core.event.EventType;
import com.isimo.debug.ExecutionState.Mode;

import org.apache.commons.lang3.StringUtils;

@Component
public class DebugExecutionController extends DefaultExecutionController {
	DebugExecutionDebuggerThread debugger = null;
	DebugExecutionFeedbackThread feedback = null;
	Thread scenarioThread = null;
	Action steppingAction = null;
	
	
	
	@Autowired
	IsimoDebugProperties isimoDebugProperties;
	
	@Autowired
	ExecutionState state;
	
	List<Breakpoint> breakpoints = new ArrayList<Breakpoint>();

	public ExecutionState getState() {
		return state;
	}

	public void setState(ExecutionState pState) {
		state = pState;
	}

	public List<Breakpoint> getBreakpoints() {
		return breakpoints;
	}

	public void setBreakpoints(List<Breakpoint> pBreakpoints) {
		breakpoints = pBreakpoints;
	}
	
	public void sendFeedback(FeedbackEvent fe) {
		if(feedback!=null)
			feedback.handleEvent(fe);
	}
	
	public void terminate() {
		scenarioThread.interrupt();
		if(debugger != null)
			debugger.interrupt();
		FeedbackEvent fe = new FeedbackEvent(EventType.Terminated, getState().getCurrentAction());
		sendFeedback(fe);
	}
	
	public void handleDebuggerEvent(DebuggerEvent de) { 
		synchronized(this) {
			if(DebuggerEventType.Resume.equals(de.getEventType())) {
				getState().setMode(Mode.RUNNING);
				notify();
				FeedbackEvent fe = new FeedbackEvent(FeedbackEventType.Resumed, getState().getCurrentAction());
				sendFeedback(fe);
			} else if(DebuggerEventType.SetBreakpoint.equals(de.getEventType())) {
				getBreakpoints().add(de.getBreakpoint());
			} else if(DebuggerEventType.Terminate.equals(de.getEventType())) {
				terminate();
			} else if(DebuggerEventType.StepInto.equals(de.getEventType()) || DebuggerEventType.StepOver.equals(de.getEventType()) || DebuggerEventType.StepReturn.equals(de.getEventType())) {
				getState().setMode(steppingModeFromEventType(de.getEventType()));
				steppingAction = getState().getCurrentAction();
				notify();
				sendFeedbackStepping(de.getEventType());
			}
		}
	}
	
	public Mode steppingModeFromEventType(EventType type) {
		if(DebuggerEventType.StepInto.equals(type))
			return Mode.STEPPINGIN;
		else if(DebuggerEventType.StepOver.equals(type))
			return Mode.STEPPINGOVER;
		else if(DebuggerEventType.StepReturn.equals(type))
			return Mode.STEPPINGRETURN;
		return null;
	}
	
	public void sendFeedbackStepping(EventType det) {
		EventType et = null;
		if(DebuggerEventType.StepInto.equals(det))
			et = FeedbackEventType.StepInto;
		else if(DebuggerEventType.StepReturn.equals(det))
			et = FeedbackEventType.StepReturn;
		else if(DebuggerEventType.StepOver.equals(det))
			et = FeedbackEventType.StepOver;
		FeedbackEvent fe = new FeedbackEvent(et, getState().getCurrentAction());
		sendFeedback(fe);
	}
	
	
	
	public void suspend(Element elem, Action action) throws InterruptedException {
		String failureString = testExecutionManager.isFailure(elem, action);
		boolean failure = StringUtils.isEmpty(failureString);
		if((isimoDebugProperties.isimo.suspendonerror && !failure) || (isimoDebugProperties.isimo.suspendonfailure && failure) && isimoDebugProperties.isimo.debug.mode) {
			try {				
				//log("Suspend cause: " + message + issuetext, action);
				testExecutionManager.log("Action: " + action.toString(),action);
				FeedbackEvent fe = new FeedbackEvent(FeedbackEventType.Suspended, getState().getCurrentAction());
				sendFeedback(fe);
				synchronized(this) {
					wait();
				}
			} catch(InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

	}
	
	public void problem(String message) throws InterruptedException {
		FeedbackEvent fe = FeedbackEvent.problem(getState().getCurrentAction(), message);
		sendFeedback(fe);
	}
	
	public void controlSteppingFinish(Event event) {
		try {
			synchronized(this) {
				if(Mode.STEPPINGRETURN.equals(getState().getMode())) {
					if(descendantNotEqual(steppingAction, event.getCurrentAction())) {
						getState().setMode(Mode.STEPPINGRETURN_STOPONNEXT);
					}
				}
			}
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	void suspend() {
		try {
			Action action = getCurrentAction();
			suspend(action.getDefinition(), action);
		} catch(InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void controlSteppingStart(Event event) {
		try {
			synchronized(this) {
				if(Mode.STEPPINGRETURN_STOPONNEXT.equals(getState().getMode())) {
					steppingAction = event.getCurrentAction();
					suspend();
				} else if(Mode.STEPPINGIN.equals(getState().getMode()))
					suspend();
				else if(Mode.STEPPINGOVER.equals(getState().getMode())) {
					if(notDescendant(event.getCurrentAction(), steppingAction)) {
						suspend();
					}
				}
			}
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static boolean notDescendant(Action current, Action stored) {
		if(stored == null)
			return true;
		if(current == null)
			return true;
		if(stored.equals(current.getParent()))
			return false;
		return notDescendant(current.getParent(), stored);
	}
	
	public static boolean descendantNotEqual(Action stored, Action current) {
		return stored!=current && !notDescendant(stored, current);
	}
	
	public void startDebugThreads() {
		debugger = new DebugExecutionDebuggerThread(isimoDebugProperties.isimo.debug.request.port);
		feedback = new DebugExecutionFeedbackThread(isimoDebugProperties.isimo.debug.event.port);
		debugger.start();
		feedback.start();
		scenarioThread = Thread.currentThread();
	}
	
	public List<Breakpoint> breakpointsMatched(Action action) {
		ArrayList<Breakpoint> bps = new ArrayList<Breakpoint>();
		Path path = action.getDefinitionOrig().getSystemid();
		for(Breakpoint bp: breakpoints) {
			Path p = Paths.get(bp.scenarioPath);
			if(p.equals(path) && (bp.lineNumber == action.getDefinitionOrig().getLineNumber()))
				bps.add(bp);
		}
		return bps;
	}
	
	public void finishDebugThreads() {
		FeedbackEvent fe = new FeedbackEvent(EventType.Terminated, getState().getCurrentAction());
		sendFeedback(fe);
		try {
			if(feedback!=null)
				feedback.join();
		} catch(InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
	
	public synchronized void waitOnAction(Action action) throws InterruptedException {
		getState().setCurrentAction(action);
		wait();
	}

	@Override
	public void startTestCase(String testcaseName) {
		super.startTestCase(testcaseName);
		if(isimoDebugProperties.isimo.debug.mode) {
			startDebugThreads();
			getState().setMode(ExecutionState.Mode.SUSPENDED);
			try {
				waitOnAction(null);
			} catch(InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void finalize() {
		finishDebugThreads();		
	}

	@Override
	public void startAction() {
		super.startAction();
		synchronized(this) {
			controlSteppingStart(Event.startAction(getCurrentAction()));
			List<Breakpoint> bps = breakpointsMatched(getState().getCurrentAction());
			if(!bps.isEmpty()) {
				FeedbackEvent fe = FeedbackEvent.breakpointHit(getState().getCurrentAction(), bps);
				publishEvent(fe);
				try {
					feedback.handleEvent(fe);
					waitOnAction(getCurrentAction());
				} catch(InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
			controlSteppingFinish(Event.stopAction(getCurrentAction()));
		}
	}	
}
