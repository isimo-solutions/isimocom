package com.smartwebproject.testframework.ui.scenario.debug;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.SocketException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;

import com.google.gson.Gson;
import com.isimo.core.event.EventType;
import com.isimo.debug.FeedbackEvent;
import com.isimo.debug.FeedbackEventType;
import com.isimo.debug.FeedbackResponse;
import com.smartwebproject.testframework.ui.editor.TestScenarioPluginConstants;
import com.smartwebproject.testframework.ui.scenario.debug.ScenarioDebugTarget.State;


public class ScenarioEventListener extends Job {
	private ScenarioDebugTarget target;
	private Gson gson = new Gson();
	
	public ScenarioEventListener(String name, ScenarioDebugTarget target) {
		super(name);
		this.target = target;
	}
	
	public ScenarioDebugTarget getTarget() {
		return target;
	}
	
	
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(target.getEventIS()));
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(target.getEventOS()));
			while(true) {
				String line = reader.readLine();
				FeedbackEvent event = gson.fromJson(line, FeedbackEvent.class);
				handleEvent(event);
				writer.println(gson.toJson(new FeedbackResponse()));
				writer.flush();
				target.setStackTrace(event.getStackTrace());
			}
		} catch(SocketException e) {
			try {
				target.terminate();
				return new Status(IStatus.INFO, TestScenarioPluginConstants.PLUGIN_ID, "Remote scenarioRunner has closed the socket");
			} catch(DebugException de) {
				throw new RuntimeException(de);
			}
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	void handleEvent(FeedbackEvent fe) {
		if(fe.getEventType().equals(EventType.Terminated) || fe.getEventType().equals(EventType.StopTestCase)) {
			target.currentState = State.Terminated;
			target.fireTerminateEvent();
		} else if(fe.getEventType().equals(FeedbackEventType.BreakpointHit)) {
			target.currentState = State.Suspended;
			target.fireChangeEvent(DebugEvent.BREAKPOINT);
		} else if(fe.getEventType().equals(FeedbackEventType.Suspended)) {
			target.currentState = State.Suspended;
			target.fireChangeEvent(DebugEvent.SUSPEND);
		} else if(fe.getEventType().equals(FeedbackEventType.Resumed)) {
			target.currentState = State.Running;
			target.fireChangeEvent(DebugEvent.RESUME);
		} else if(fe.getEventType().equals(FeedbackEventType.StepInto)) {
			target.currentState = State.Stepping;
			target.fireChangeEvent(DebugEvent.STEP_INTO);
		} else if(fe.getEventType().equals(FeedbackEventType.StepReturn)) {
			target.currentState = State.Stepping;
			target.fireChangeEvent(DebugEvent.STEP_RETURN);
		} else if(fe.getEventType().equals(FeedbackEventType.StepOver)) {
			target.currentState = State.Stepping;
			target.fireChangeEvent(DebugEvent.STEP_OVER);
		}
	}
}
