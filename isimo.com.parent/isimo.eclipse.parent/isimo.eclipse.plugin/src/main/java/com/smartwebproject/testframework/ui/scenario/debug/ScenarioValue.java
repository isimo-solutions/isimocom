package com.smartwebproject.testframework.ui.scenario.debug;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

public class ScenarioValue extends ScenarioDebugElement implements IValue {
	Object val;
	ScenarioValue(Object val, IDebugTarget target) {
		super(target);
		this.val = val;
	}

	@Override
	public String getReferenceTypeName() throws DebugException {
		return val.getClass().getName();
	}

	@Override
	public String getValueString() throws DebugException {
		return val.toString();
	}

	@Override
	public boolean isAllocated() throws DebugException {
		return true;
	}

	@Override
	public IVariable[] getVariables() throws DebugException {
		return new IVariable[0];
	}

	@Override
	public boolean hasVariables() throws DebugException {
		return false;
	}

}
