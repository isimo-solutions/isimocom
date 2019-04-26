package com.smartwebproject.testframework.ui.launcher;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationTabGroup;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaMainTab;
import org.eclipse.pde.ui.launcher.MainTab;

public class TestCaptureListenerTabGroup extends AbstractLaunchConfigurationTabGroup implements ILaunchConfigurationTabGroup {

	@Override
	public void createTabs(ILaunchConfigurationDialog pArg0, String pArg1) {
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] { new TestCaptureListenerTab(), new CommonTab()};
		setTabs(tabs);
	}

}
