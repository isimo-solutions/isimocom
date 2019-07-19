package com.smartwebproject.testframework.ui.views.dependencies;

import org.eclipse.core.internal.resources.File;
import org.eclipse.jface.action.Action;

public class DependenciesAction  extends Action{
	
	File file;
	
	public DependenciesAction(String s) {
		super();
		this.setText(s);
	}
	
	@Override
	public void run() {
		RunCommand.Search(file);
	}
	
	public void setFile(File file) {
		this.file = file;
	}

}
