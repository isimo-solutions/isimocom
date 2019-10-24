package com.isimo.debug;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.isimo.core.Action;

@Component
public class ExecutionState {
	public enum Mode { RUNNING, STEPPINGRETURN, STEPPINGRETURN_STOPONNEXT, STEPPINGIN, STEPPINGOVER, SUSPENDED};
	private Mode mode;
	
	@Autowired
	DebugExecutionController executionController;
	
	public ExecutionState() {
		mode = Mode.RUNNING;
	}
		
	public Action getCurrentAction() {
		return executionController.getCurrentAction();
	}

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode pMode) {
		mode = pMode;
	}

	public void setCurrentAction(Action pCurrentAction) {
		executionController.setCurrentAction(pCurrentAction);
	}
}
