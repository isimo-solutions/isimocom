package com.smartwebproject.testframework.ui.scenario;

import java.lang.reflect.Field;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.junit.launcher.JUnitLaunchConfigurationTab;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;

public class ScenarioLauncherMainTab extends JUnitLaunchConfigurationTab {
	
	Object getNamedPrivateField(String name) {
		try {
			Field f = JUnitLaunchConfigurationTab.class.getDeclaredField(name);
			f.setAccessible(true);
			return f.get(this);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	Text getTestText() {
		return (Text) getNamedPrivateField("fTestText");
	}
	
	
	Text getTestMethodText() {
		return (Text) getNamedPrivateField("fTestMethodText");
	}
	
	ComboViewer getTestLoaderViewer() {
		return (ComboViewer) getNamedPrivateField("fTestLoaderViewer");
	} ;
	
	
	Button getTestContainerRadioButton() {
		return (Button) getNamedPrivateField("fTestContainerRadioButton");
	};
	
	Button getContainerSearchButton() {
		return (Button) getNamedPrivateField("fContainerSearchButton");
	};
	
	Text getContainerText() {
		return (Text) getNamedPrivateField("fContainerText");
	}
	
	
	
	
	
	
	
	@Override
	public void initializeFrom(ILaunchConfiguration pConfig) {
		// TODO Auto-generated method stub
		super.initializeFrom(pConfig);
		getTestText().setEditable(false);
		getTestMethodText().setEditable(false);
		getTestText().setText(ScenarioLauncherConstants.TESTCLASS);
		getTestMethodText().setText(ScenarioLauncherConstants.TESTMETHOD);
		getTestLoaderViewer().getCombo().setEnabled(false);
		getTestContainerRadioButton().setVisible(false);
		getContainerSearchButton().setVisible(false);
		getContainerText().setVisible(false);
	}
}
