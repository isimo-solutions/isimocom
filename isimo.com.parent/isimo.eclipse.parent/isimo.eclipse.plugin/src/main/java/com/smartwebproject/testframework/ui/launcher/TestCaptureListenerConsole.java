package com.smartwebproject.testframework.ui.launcher;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.internal.ui.views.console.ProcessConsole;
import org.eclipse.debug.ui.console.IConsoleColorProvider;
import org.eclipse.ui.console.IConsole;

public class TestCaptureListenerConsole extends ProcessConsole implements IConsole {
	public TestCaptureListenerConsole(IProcess process, IConsoleColorProvider colorprovider) {
		super(process, colorprovider);
	}
	
	@Override
	public void handleDebugEvents(DebugEvent[] events) {
		super.handleDebugEvents(events);
	}
	
	
}
