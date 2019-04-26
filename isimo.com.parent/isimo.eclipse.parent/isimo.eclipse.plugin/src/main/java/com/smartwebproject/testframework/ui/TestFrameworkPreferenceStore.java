package com.smartwebproject.testframework.ui;

import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import com.smartwebproject.testframework.ui.editor.TestScenarioSemanticHighlighting;

public class TestFrameworkPreferenceStore extends ScopedPreferenceStore {

	public TestFrameworkPreferenceStore(IScopeContext context, String qualifier) {
		super(context, qualifier);
	}

	public TestFrameworkPreferenceStore(IScopeContext context, String qualifier, String defaultQualifierPath) {
		super(context, qualifier, defaultQualifierPath);
	}
	
	@Override
	public boolean getBoolean(String name) {
		if(name.equals(TestScenarioSemanticHighlighting.ENABLED_PREFERENCE_KEY))
			return true;
		return super.getBoolean(name);
	}
}
