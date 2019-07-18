package com.smartwebproject.testframework.ui.views.dependencies;


import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import com.isimo.dependencies.Scenario;
import com.smartwebproject.testframework.ui.TestFrameworkUIPlugin;
import com.smartwebproject.testframework.ui.editor.TestScenarioPluginConstants;

public class DependencyTreeNode extends TreeNode implements Comparable<DependencyTreeNode>{

	
	String label;
	
	public DependencyTreeNode(Object value) {
		super(value);
		// TODO Auto-generated constructor stub
	}
	
	String getScenariosRoot(IProject project){
		try {
			return TestFrameworkUIPlugin.getProjectProperty(project, TestScenarioPluginConstants.SCENARIO_ROOT);
		} catch (CoreException e) {
			throw new RuntimeException();
		}
	}
	
	public void openFile(String testsLocation) {
		Scenario s = (Scenario)value;
		if(s.getNotFound()) return;
		
		int lineNumber = 0;
		if(this instanceof ScenariosNode) {
			ScenariosNode node = (ScenariosNode)this;
			if(node.including) lineNumber = node.linenumber; 
		}
		
		try {
			IPath location = Path.fromOSString(testsLocation+ Path.SEPARATOR +s.getRelativePath());
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(location);
			IMarker marker = file.createMarker(IMarker.TEXT);
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IDE.openEditor(page, marker);
			marker.delete();
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public int compareTo(DependencyTreeNode n) {
	  return label.compareTo(n.label);
	}
	
	

}
