package com.smartwebproject.testframework.ui.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.internal.ui.preferences.PropertyAndPreferencePage;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

public class TestFrameworkPropertyPage extends PropertyAndPreferencePage {
	public static final String PREF_ID= "com.smartwebproject.testframework.ui.preferences.TestFrameworkPreferencePage"; //$NON-NLS-1$
	public static final String PROP_ID= "com.smartwebproject.testframework.ui.propertyPages.TestFrameworkPreferencePage"; //$NON-NLS-1$
	private TestFrameworkConfigurationBlock fConfigurationBlock;
	
	
	public TestFrameworkPropertyPage() {
		PreferenceStore ps = new PreferenceStore();
		this.setPreferenceStore(ps);
	}
	
	@Override
	public void createControl(Composite pParent) {
		IWorkbenchPreferenceContainer container= (IWorkbenchPreferenceContainer) getContainer();
		fConfigurationBlock= new TestFrameworkConfigurationBlock(getNewStatusChangedListener(), getProject(), container);
		super.createControl(pParent);
	}
	
	
	
	@Override
	protected void performDefaults() {
		super.performDefaults();
		if (fConfigurationBlock != null) {
			fConfigurationBlock.performDefaults();
		}
	}

	/*
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	@Override
	public boolean performOk() {
		if (fConfigurationBlock != null && !fConfigurationBlock.performOk()) {
			return false;
		}
		return super.performOk();
	}

	/*
	 * @see org.eclipse.jface.preference.IPreferencePage#performApply()
	 */
	@Override
	public void performApply() {
		if (fConfigurationBlock != null) {
			fConfigurationBlock.performApply();
		}
	}

	@Override
	protected Control createPreferenceContent(Composite composite) {
		return fConfigurationBlock.createContents(composite);
	}

	@Override
	protected boolean hasProjectSpecificOptions(IProject project) {
		// TODO Auto-generated method stub
		return fConfigurationBlock.hasProjectSpecificOptions(project);
	}

	@Override
	protected String getPreferencePageID() {
		return PREF_ID;
	}

	@Override
	protected String getPropertyPageID() {
		// TODO Auto-generated method stub
		return PROP_ID;
	}
	
	@Override
	protected void enableProjectSpecificSettings(boolean useProjectSpecificSettings) {
		super.enableProjectSpecificSettings(useProjectSpecificSettings);
		if (fConfigurationBlock != null) {
			fConfigurationBlock.useProjectSpecificSettings(useProjectSpecificSettings);
		}
	}
	
	@Override
	public void setVisible(boolean visible) {
		if (visible && fConfigurationBlock != null) {
			fConfigurationBlock.updateControls();
		}
	}
}
