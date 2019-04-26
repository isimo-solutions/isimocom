package com.smartwebproject.testframework.ui.launcher;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.debug.core.ILaunchesListener2;
import org.eclipse.debug.internal.core.LaunchConfiguration;
import org.eclipse.debug.internal.core.LaunchManager;

public class TestCaptureListener extends LaunchConfiguration implements ILaunchConfiguration {
	protected TestCaptureListener(IFile pFile) {
		super(pFile);
	}
	
	@Override
	public String getAttribute(String attributeName, String defaultValue) throws CoreException {
		if(DebugPlugin.ATTR_PROCESS_FACTORY_ID.endsWith(attributeName))
			return "com.smartwebproject.testframework.ui.launcher.TestCaptureListenerProcessFactory";
		return super.getAttribute(attributeName, defaultValue);
	}
}
