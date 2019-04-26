package com.smartwebproject.testframework.ui.scenario;

import org.eclipse.core.resources.IFile;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.internal.core.LaunchConfiguration;

public class ScenarioLauncher extends LaunchConfiguration implements ILaunchConfiguration {
	protected ScenarioLauncher(IFile pFile) {
		super(pFile);
		// TODO Auto-generated constructor stub
	}
}
