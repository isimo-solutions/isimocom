package com.smartwebproject.testframework.ui.editor;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.wst.xml.ui.internal.contentoutline.JFaceNodeContentProvider;
import org.eclipse.wst.xml.ui.internal.contentoutline.XMLNodeActionManager;
import org.eclipse.wst.xml.ui.views.contentoutline.XMLContentOutlineConfiguration;

public class TestScenarioContentOutlineConfiguration extends XMLContentOutlineConfiguration {
	TestScenarioContentProvider scenarioContentProvider = null;
	public TestScenarioContentOutlineConfiguration() {
		super();
	}
	
	@Override
	public IContentProvider getContentProvider(TreeViewer viewer) {
		if (scenarioContentProvider == null) {
			scenarioContentProvider = new TestScenarioContentProvider(viewer);
		}
		return scenarioContentProvider;
	}
}
