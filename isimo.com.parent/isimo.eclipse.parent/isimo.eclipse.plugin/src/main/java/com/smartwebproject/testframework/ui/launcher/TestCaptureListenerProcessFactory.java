package com.smartwebproject.testframework.ui.launcher;

import java.util.Map;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.IProcessFactory;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.RuntimeProcess;

public class TestCaptureListenerProcessFactory implements IProcessFactory {

	@Override
	public IProcess newProcess(ILaunch launch, Process process, String label, Map<String, String> attributes) {
		IProcess iprocess = new TestCaptureProcessDecorator(new RuntimeProcess(launch, process, label, attributes));
		return iprocess;
	}

}
