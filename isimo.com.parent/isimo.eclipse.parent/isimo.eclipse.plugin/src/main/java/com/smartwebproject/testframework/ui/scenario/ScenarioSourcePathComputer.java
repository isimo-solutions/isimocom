package com.smartwebproject.testframework.ui.scenario;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.ISourceContainerType;
import org.eclipse.debug.core.sourcelookup.ISourceLookupDirector;
import org.eclipse.debug.core.sourcelookup.ISourcePathComputerDelegate;
import org.eclipse.debug.core.sourcelookup.containers.FolderSourceContainer;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;

import com.smartwebproject.testframework.ui.TestFrameworkUIPlugin;
import com.smartwebproject.testframework.ui.editor.TestScenarioPluginConstants;

public class ScenarioSourcePathComputer implements ISourcePathComputerDelegate {

	@Override
	public ISourceContainer[] computeSourceContainers(ILaunchConfiguration pConfiguration, IProgressMonitor pMonitor) throws CoreException {
		// TODO Auto-generated method stub
		String projectName = getJavaProjectName(pConfiguration);
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(projectName);
		String scenarioRoot = TestFrameworkUIPlugin.getProjectProperty(project, TestScenarioPluginConstants.SCENARIO_ROOT);
		IFolder folder = project.getFolder(new Path(scenarioRoot));
		return new ISourceContainer[] { new FolderSourceContainer(folder, true) };
	}
	
	public String getJavaProjectName(ILaunchConfiguration configuration)
			throws CoreException {
		return configuration.getAttribute(
				IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME,
				(String) null);
	}


}
