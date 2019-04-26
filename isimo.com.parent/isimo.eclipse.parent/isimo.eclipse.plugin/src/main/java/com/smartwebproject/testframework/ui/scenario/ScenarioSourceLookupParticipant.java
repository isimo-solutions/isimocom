package com.smartwebproject.testframework.ui.scenario;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupParticipant;
import org.eclipse.debug.core.sourcelookup.ISourceLookupDirector;
import org.eclipse.debug.core.sourcelookup.ISourceLookupParticipant;

import com.smartwebproject.testframework.ui.scenario.debug.ScenarioStackFrame;

public class ScenarioSourceLookupParticipant extends AbstractSourceLookupParticipant implements ISourceLookupParticipant {

	@Override
	public String getSourceName(Object pObject) throws CoreException {
		if(pObject instanceof ScenarioStackFrame) {
			return ((ScenarioStackFrame) pObject).getStackTrace().scenarioPath;
		}
		return null;
	}

}
