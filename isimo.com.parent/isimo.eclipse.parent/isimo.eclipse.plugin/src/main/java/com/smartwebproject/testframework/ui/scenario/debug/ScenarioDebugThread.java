package com.smartwebproject.testframework.ui.scenario.debug;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.jdi.internal.StackFrameImpl;

import com.isimo.core.event.StackTrace;

public class ScenarioDebugThread extends ScenarioDebugElement implements IThread {
    

    private int charPos;

	public ScenarioDebugThread(IDebugTarget pTarget) {
		super(pTarget);
		DebugPlugin.getDefault().addDebugEventListener(this.getScenarioDebugTarget());
		// TODO Auto-generated constructor stub
	}


	@Override
	public IStackFrame[] getStackFrames() throws DebugException {
		return this.getScenarioDebugTarget().getStackFrames();
	}

	@Override
	public boolean hasStackFrames() throws DebugException {
		return this.getScenarioDebugTarget().getStackFrames() != null && this.getScenarioDebugTarget().getStackFrames().length > 0;
	}

	@Override
	public int getPriority() throws DebugException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IStackFrame getTopStackFrame() throws DebugException {
		// TODO Auto-generated method stub
		if(!hasStackFrames())
			return null;
		return getScenarioDebugTarget().getStackFrames()[0];
	}

	@Override
	public String getName() throws DebugException {
		return "Exec-Thread ("+ getScenarioDebugTarget().getCurrentState()+")";
	}

	@Override
	public IBreakpoint[] getBreakpoints() {
		return getScenarioDebugTarget().getBreakpoints();
	}
	
	public ScenarioDebugTarget getScenarioDebugTarget() {
		return (ScenarioDebugTarget) getDebugTarget();
	}


	@Override
	public void terminate() throws DebugException {
		getScenarioDebugTarget().terminate();
	}
	
	
	
	
	
	
}
