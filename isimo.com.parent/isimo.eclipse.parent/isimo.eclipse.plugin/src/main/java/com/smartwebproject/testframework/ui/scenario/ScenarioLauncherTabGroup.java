package com.smartwebproject.testframework.ui.scenario;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.EnvironmentTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.sourcelookup.SourceLookupTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaArgumentsTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaClasspathTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaJRETab;

public class ScenarioLauncherTabGroup extends AbstractLaunchConfigurationTabGroup {
	private ScenarioLauncherTab scenarioLauncherTab;
	public void createTabs(ILaunchConfigurationDialog pDialog, String pMode) {
		try {
			ILaunchConfigurationTab[] tabs= new ILaunchConfigurationTab[] {
					new ScenarioLauncherMainTab(),
					scenarioLauncherTab = new ScenarioLauncherTab(),
							new JavaArgumentsTab(),
							new JavaClasspathTab(),
							new JavaJRETab(),
							new SourceLookupTab(),
							new EnvironmentTab(),
							new CommonTab()
			};
			setTabs(tabs);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void initializeFrom(ILaunchConfiguration pConfiguration) {
		// TODO Auto-generated method stub
		super.initializeFrom(pConfiguration);
		scenarioLauncherTab.setConfigCopy(pConfiguration);
	}
}
