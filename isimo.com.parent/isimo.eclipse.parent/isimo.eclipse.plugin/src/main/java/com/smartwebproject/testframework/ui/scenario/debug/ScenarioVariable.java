package com.smartwebproject.testframework.ui.scenario.debug;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

import com.smartwebproject.testframework.ui.NotImplementedException;

public class ScenarioVariable extends ScenarioDebugElement implements IVariable, Comparable<ScenarioVariable> {
	private String scope = "";
	private String name;
	private IValue value;

	ScenarioVariable(IDebugTarget target, String scope, String name, Object valueObject) {
		super(target);
		this.scope = scope;
		this.name = name;
		this.value = new ScenarioValue(valueObject, target);
	}

	@Override
	public void setValue(String expression) throws DebugException {
		throw new NotImplementedException();		
	}

	@Override
	public void setValue(IValue value) throws DebugException {
		throw new NotImplementedException();		
	}

	@Override
	public boolean supportsValueModification() {
		return false;
	}

	@Override
	public boolean verifyValue(String expression) throws DebugException {
		throw new NotImplementedException();		
	}

	@Override
	public boolean verifyValue(IValue value) throws DebugException {
		throw new NotImplementedException();		
	}

	@Override
	public IValue getValue() throws DebugException {
		return this.value;
	}

	@Override
	public String getName() throws DebugException {
		// TODO Auto-generated method stub
		return ((StringUtils.isNotEmpty(scope))?(scope+":"):"")+name;
	}

	@Override
	public String getReferenceTypeName() throws DebugException {
		return this.value.getReferenceTypeName();
	}

	@Override
	public boolean hasValueChanged() throws DebugException {
		return false;
	}

	@Override
	public int compareTo(ScenarioVariable o) {
		ScenarioVariable var = (ScenarioVariable) o;
		try {
			return getName().compareTo(var.getName());
		} catch(DebugException e) {
			throw new RuntimeException(e);
		}
	}
}
