package com.smartwebproject.testframework.ui.scenario.debug;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.DebugElement;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;

import com.isimo.debug.DebuggerEvent;
import com.smartwebproject.testframework.ui.TestFrameworkUIPlugin;

public class ScenarioDebugElement extends DebugElement implements IDebugElement {
	ILaunch launch;

	
	public ScenarioDebugElement() {
		super(null);
	}

	public ScenarioDebugElement(IDebugTarget pTarget) {
		super(pTarget);
	}
	
	
	public ILaunch getLaunch() {
		return getScenarioDebugTarget().getLaunch();
	}

	
	public String getModelIdentifier() {
		// TODO Auto-generated method stub
		return TestFrameworkUIPlugin.getDefault().getId();
	}
	
	
    
    public boolean canResume() {
        return isSuspended();
    }

    
    public boolean canSuspend() {
        return !isTerminated() && !isSuspended();
    }

    
    public boolean isSuspended() {
        return getScenarioDebugTarget().isSuspended();
    }

    
    public void resume() throws DebugException {
    	getScenarioDebugTarget().resume();
    }

    
    public void suspend() throws DebugException {
        getScenarioDebugTarget().suspend();
    }

    
    public boolean canStepOver() {
        return isSuspended();
    }

    
    public void stepOver() throws DebugException {
        
    }

    
    public boolean canStepInto() {
        return false;
    }

    
    public boolean canStepReturn() {
        return false;
    }

    
    public void stepInto() throws DebugException {
    }

    
    public void stepReturn() throws DebugException {
    }

    
    public boolean canTerminate() {
        return !isTerminated();
    }

    
    public boolean isTerminated() {
        return getScenarioDebugTarget().isTerminated();
    }
    
    public boolean isStepping() {
    	return getScenarioDebugTarget().isStepping();
    }
    
    public ScenarioDebugTarget getScenarioDebugTarget() {
		return (ScenarioDebugTarget) this.getDebugTarget();
	}
    
	public void sendEvent(DebuggerEvent de) {
		getScenarioDebugTarget().sendEvent(de);
	}

}
