package com.smartwebproject.testframework.ui.launcher;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.widgets.Composite;

public class TestCaptureListenerTab extends AbstractLaunchConfigurationTab {

	@Override
	public void createControl(Composite parent) {
		Composite mainComposite = new Composite(parent, 0);
		setControl(mainComposite);
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Test Capture Listener";
	}

	@Override
	public void initializeFrom(ILaunchConfiguration pParamILaunchConfiguration) {
		// TODO Auto-generated method stub
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy pParamILaunchConfigurationWorkingCopy) {
		pParamILaunchConfigurationWorkingCopy.setAttribute(DebugPlugin.ATTR_PROCESS_FACTORY_ID, "com.smartwebproject.testframework.ui.launcher.TestCaptureListenerProcessFactory");

	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy pParamILaunchConfigurationWorkingCopy) {
		pParamILaunchConfigurationWorkingCopy.setAttribute(DebugPlugin.ATTR_PROCESS_FACTORY_ID, "com.smartwebproject.testframework.ui.launcher.TestCaptureListenerProcessFactory");
	}

}
