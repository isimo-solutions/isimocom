package com.smartwebproject.testframework.ui.views.dependencies;


import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.internal.resources.File;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.ViewPart;

import com.smartwebproject.testframework.ui.TestFrameworkUIPlugin;
import com.smartwebproject.testframework.ui.editor.TestScenarioPluginConstants;

public class RunCommand extends AbstractHandler {

	static String getScenariosRoot(IProject project){
		try {
			return TestFrameworkUIPlugin.getProjectProperty(project, TestScenarioPluginConstants.SCENARIO_ROOT);
		} catch (CoreException e) {
			throw new RuntimeException();
		}
	}
	
	    @Override
	    public Object execute(ExecutionEvent event) throws ExecutionException {
	    	ISelection selection = HandlerUtil.getCurrentSelection(event);
	    	IEditorInput editor = HandlerUtil.getActiveEditorInput(event);
    		if(selection instanceof TreeSelection) {
    			TreeSelection treeSelection = (TreeSelection) selection; 
    			if(treeSelection.size() != 1) return null; 
    			File file = (File)treeSelection.getFirstElement();
    			Search(file);
    		}else if(editor instanceof IFileEditorInput){
    			File file = (File)((IFileEditorInput)editor).getFile();
    			Search(file);
	    	}

	        return null;
	    }
	    
	    public static void Search(File file) {
	    	IPath filePath = file.getFullPath();
    		
    		String projectName = file.getFullPath().segment(0);
    		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
    		String scenariosRoot = getScenariosRoot(project);
    		String scenariosRootAbsolute = project.getLocation()+ "/" + scenariosRoot;
    		IFile fileRoot = project.getFile(scenariosRoot);
    		String name = filePath.makeRelativeTo(fileRoot.getFullPath()).toString();
    		try {
				IViewPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("com.smartwebproject.testframework.ui.views.dependencies");
				((DependencyView)part).setProperties(name, scenariosRootAbsolute, projectName+ "/" +scenariosRoot);
				
    		} catch (PartInitException e) {
				throw new RuntimeException();
			}
	    }
	    
	}