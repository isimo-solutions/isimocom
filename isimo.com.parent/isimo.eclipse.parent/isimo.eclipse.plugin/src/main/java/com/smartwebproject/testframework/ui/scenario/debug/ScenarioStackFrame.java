package com.smartwebproject.testframework.ui.scenario.debug;

import java.util.HashSet;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;

import com.isimo.core.event.DebuggerEvent;
import com.isimo.core.event.StackTrace;
import com.isimo.core.event.Variable;
import com.isimo.core.event.DebuggerEvent.DebuggerEventType;

public class ScenarioStackFrame extends ScenarioDebugElement implements IStackFrame {
	private IThread thread;
	private StackTrace stackTrace;
	
	public ScenarioStackFrame(ScenarioDebugTarget target, StackTrace stackTrace) {
        super(target);
        try {
	        this.stackTrace = stackTrace;
	        this.thread = target.getThreads()[0];
        } catch(Exception e) {
        	throw new RuntimeException(e);
        }
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;    
        return false;
    }


	@Override
	public IThread getThread() {
		return thread;
	}


	@Override
	public IVariable[] getVariables() throws DebugException {
		SortedSet<Variable> sortedVars = new TreeSet<Variable>();
		sortedVars.addAll(stackTrace.variables);
		IVariable[] vars = new IVariable[sortedVars.size()];
		int i = 0;
		for(Variable var: sortedVars) {
			vars[i++] = new ScenarioVariable(this.getDebugTarget(), "", var.getName(), var.getValue());
		}
		return vars;
	}


	@Override
	public boolean hasVariables() throws DebugException {
		return true;
	}


	@Override
	public int getLineNumber() throws DebugException {
		return stackTrace.lineNumber;
	}


	@Override
	public int getCharStart() throws DebugException {
		return -1;
	}


	@Override
	public int getCharEnd() throws DebugException {
		return -1;
	}


	@Override
	public String getName() throws DebugException {
		return "Scenario: "+stackTrace.scenarioPath+" Action: "+stackTrace.actionName+ " Line: "+getLineNumber();
	}


	@Override
	public IRegisterGroup[] getRegisterGroups() throws DebugException {
		return new IRegisterGroup[0];
	}


	@Override
	public boolean hasRegisterGroups() throws DebugException {
		return false;
	}


	@Override
	public void terminate() throws DebugException {
		getScenarioDebugTarget().terminate();
	}


	public StackTrace getStackTrace() {
		return stackTrace;
	}
	
	@Override
	public boolean canStepInto() {
		return stackTrace.canStepInto;
	}
	
	@Override
	public boolean canStepReturn() {
		return stackTrace.canStepReturn;
	}
	
	@Override
	public boolean canStepOver() {
		return stackTrace.canStepOver;
	}
	
	@Override
	public void stepInto() throws DebugException {
		DebuggerEvent event = new DebuggerEvent();
		event.setEventType(DebuggerEventType.StepInto);
		sendEvent(event);
	}
	
	@Override
	public void stepOver() throws DebugException {
		DebuggerEvent event = new DebuggerEvent();
		event.setEventType(DebuggerEventType.StepOver);
		sendEvent(event);
	}
	
	@Override
	public void stepReturn() throws DebugException {
		DebuggerEvent event = new DebuggerEvent();
		event.setEventType(DebuggerEventType.StepReturn);
		sendEvent(event);
	}
}
