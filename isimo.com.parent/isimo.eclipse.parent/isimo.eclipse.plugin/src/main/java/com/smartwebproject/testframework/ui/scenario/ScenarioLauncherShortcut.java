package com.smartwebproject.testframework.ui.scenario;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.internal.core.LaunchMode;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchConfigurationEditDialog;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchConfigurationsDialog;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchGroupExtension;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.debug.ui.ILaunchShortcut2;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;
import org.eclipse.jdt.internal.junit.launcher.JUnitLaunchConfigurationConstants;
import org.eclipse.jdt.internal.junit.launcher.TestKindRegistry;
import org.eclipse.jdt.junit.launcher.JUnitLaunchShortcut;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.ExceptionHandler;

import com.smartwebproject.testframework.ui.TestFrameworkUIPlugin;
import com.smartwebproject.testframework.ui.editor.TestScenarioPluginConstants;

public class ScenarioLauncherShortcut extends JUnitLaunchShortcut implements ILaunchShortcut2  {
	private static final String EMPTY_STRING= "";
	protected String getLaunchConfigurationTypeId() {
		return "com.smartwebproject.testframework.ui.scenario.ScenarioLauncher";
	}
	
	protected void launch(IFile file, String pMode) {
		try {
				ILaunchConfigurationWorkingCopy lctemp = createLaunchConfiguration(file);
				ILaunchConfiguration lc = findExistingLaunchConfiguration(lctemp, pMode);
				if (lc==null) {
					// no existing found: create a new one
					lc= lctemp.doSave();
					Shell shell = 					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
					IStatus status = Status.OK_STATUS;
					LaunchGroupExtension group = DebugUIPlugin.getDefault().getLaunchConfigurationManager().getDefaultLaunchGroup(pMode);
			    	if (group != null) {
			    		LaunchConfigurationEditDialog dialog = new LaunchConfigurationEditDialog(shell, lc, group, true);
			    		dialog.setInitialStatus(status);
			    		dialog.create();
			    		for(ILaunchConfigurationTab tab: dialog.getTabs()) {
			    			if(tab instanceof ScenarioLauncherTab)
			    				dialog.setActiveTab(tab);
			    		}
			    		dialog.open();
			    	}
				}
				DebugUITools.launch(lc, pMode);
		} catch(Exception e) {
			ExceptionHandler.getInstance().handleException(e);
		}
	}

	@Override
	public void launch(ISelection pSelection, String pMode) {
		try {
			if(pSelection instanceof TreeSelection) {
				TreeSelection ts = (TreeSelection) pSelection;
				if(ts.getPaths().length != 1)
					MessageDialog.openError(TestFrameworkUIPlugin.getDefault().getShell(), "Launch Problem", "Must select exactly one scenario!");
				IFile first = (IFile) ts.getFirstElement();
				launch(first, pMode);
			}
		} catch(Exception e) {
			ExceptionHandler.getInstance().handleException(e);
		}
	}
	
	
	
	private ILaunchConfiguration findExistingLaunchConfiguration(ILaunchConfigurationWorkingCopy temporary, String mode) throws InterruptedException, CoreException {
		List<ILaunchConfiguration> candidateConfigs= findExistingLaunchConfigurations(temporary);

		// If there are no existing configs associated with the IType, create
		// one.
		// If there is exactly one config associated with the IType, return it.
		// Otherwise, if there is more than one config associated with the
		// IType, prompt the
		// user to choose one.
		int candidateCount= candidateConfigs.size();
		if (candidateCount == 0) {
			return null;
		} else if (candidateCount == 1) {
			return candidateConfigs.get(0);
		} else {
			// Prompt the user to choose a config. A null result means the user
			// cancelled the dialog, in which case this method returns null,
			// since cancelling the dialog should also cancel launching
			// anything.
			Method m = null;
			try {
				m = chooseConfiguration(this.getClass());
				m.setAccessible(true);
				ILaunchConfiguration config= (ILaunchConfiguration) m.invoke(this,candidateConfigs, mode);			
				return config;
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	Method chooseConfiguration(Class clazz) {
		try {
			Method m = clazz.getDeclaredMethod("chooseConfiguration", List.class, String.class);
			return m;
		} catch(NoSuchMethodException e) {
			return chooseConfiguration(clazz.getSuperclass());
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Returns the attribute names of the attributes that are compared when looking for an existing
	 * similar launch configuration. Clients can override and replace to customize.
	 *
	 * @return the attribute names of the attributes that are compared
	 */
	protected String[] getAttributeNamesToCompare() {
		return new String[] {
				ScenarioLauncherConstants.SCENARIO_ATTR
		};
	}
	
	private List<ILaunchConfiguration> findExistingLaunchConfigurations(ILaunchConfigurationWorkingCopy temporary) throws CoreException {
		ILaunchConfigurationType configType= temporary.getType();

		ILaunchConfiguration[] configs= getLaunchManager().getLaunchConfigurations(configType);
		String[] attributeToCompare= getAttributeNamesToCompare();

		ArrayList<ILaunchConfiguration> candidateConfigs= new ArrayList<>(configs.length);
		for (ILaunchConfiguration config : configs) {
			if (hasSameAttributes(config, temporary, attributeToCompare)) {
				candidateConfigs.add(config);
			}
		}
		return candidateConfigs;
	}
	
	private static boolean hasSameAttributes(ILaunchConfiguration config1, ILaunchConfiguration config2, String[] attributeToCompare) {
		try {
			for (String element : attributeToCompare) {
				String val1= config1.getAttribute(element, EMPTY_STRING);
				String val2= config2.getAttribute(element, EMPTY_STRING);
				if (!val1.equals(val2)) {
					return false;
				}
			}
			return true;
		} catch (CoreException e) {
			// ignore access problems here, return false
		}
		return false;
	}

	@Override
	public void launch(IEditorPart pEditor, String pMode) {
		IEditorInput input = pEditor.getEditorInput();
		if(input instanceof IFileEditorInput) {
			IFileEditorInput fin = (IFileEditorInput) input;
			launch(fin.getFile(), pMode);
		}
	}

	@Override
	public ILaunchConfiguration[] getLaunchConfigurations(ISelection pSelection) {
		if(pSelection instanceof ITreeSelection) {
			if(((ITreeSelection) pSelection).getFirstElement()!=null)
				return new ILaunchConfiguration[0];			
		} else {
			return null;
		}
		return null;

		//throw new RuntimeException("Not implemented");
	}

	@Override
	public ILaunchConfiguration[] getLaunchConfigurations(IEditorPart pEditorpart) {
		return super.getLaunchConfigurations(pEditorpart);
	}
	
	protected ILaunchConfigurationWorkingCopy createLaunchConfiguration(IFile element) throws CoreException {
		final String mainTypeQualifiedName;
		final String containerHandleId;
		
		IContentTypeManager ctm = Platform.getContentTypeManager();
		IContentType[] cts = null;
		try {
			cts = ctm.findContentTypesFor(element.getContents(), element.getName());	
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		boolean found = false;
		for(IContentType ct: cts) {
			if(TestScenarioPluginConstants.TEST_SCENARIO_CONTENT_TYPE.equals(ct.getId())) {
				found = true;
				break;
			}
		}
		if(!found)
			throw new IllegalArgumentException("Invalid file type to create a launch configuration: " + element.getName()); //$NON-NLS-1$

		ILaunchConfigurationType configType= getLaunchManager().getLaunchConfigurationType(getLaunchConfigurationTypeId());
		String scenariopath = element.getFullPath().toString();
		ScenarioLauncherDelegate delegate = new ScenarioLauncherDelegate();
		
		ILaunchConfigurationWorkingCopy wc= configType.newInstance(null, element.getName());
		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, element.getProject().getName());
		String prefix = "/"+element.getProject().getName()+"/"+delegate.getProjectProperty(wc, TestScenarioPluginConstants.SCENARIO_ROOT);
		if(!scenariopath.startsWith(prefix))
			throw new RuntimeException("Chosen scenario "+scenariopath+" is not in the structure of scenario root "+prefix);
		scenariopath = scenariopath.substring(prefix.length()+1);
		if(scenariopath.endsWith(".xml"))
			scenariopath = scenariopath.substring(0, scenariopath.length()-".xml".length());
		String testName = scenariopath.replace("/", "_");
		String configName= getLaunchManager().generateLaunchConfigurationName(suggestLaunchConfigurationName(element, testName));
		wc= configType.newInstance(null, configName);
		

		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, ScenarioLauncherConstants.TESTCLASS);
		wc.setAttribute(JUnitLaunchConfigurationConstants.ATTR_TEST_NAME, ScenarioLauncherConstants.TESTMETHOD);
		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, element.getProject().getName());
		wc.setAttribute(JUnitLaunchConfigurationConstants.ATTR_TEST_RUNNER_KIND, "com.smartwebproject.testframework.ui.scenario.kind");
		wc.setAttribute(ScenarioLauncherConstants.BROWSER_ATTR, ScenarioLauncherConstants.DEFAULT_BROWSER);
		wc.setAttribute(ScenarioLauncherConstants.ENVIRONMENT_ATTR, "rhtzptng06");
		
		wc.setAttribute(ScenarioLauncherConstants.SCENARIO_ATTR, scenariopath);	
		return wc;
	}
	
	protected String suggestLaunchConfigurationName(IFile element, String fullTestName) {
		return fullTestName;
	}
	
	private ILaunchManager getLaunchManager() {
		return DebugPlugin.getDefault().getLaunchManager();
	}

	@Override
	public IResource getLaunchableResource(ISelection pSelection) {
		return null;
		//throw new RuntimeException("Not implemented");
	}

	@Override
	public IResource getLaunchableResource(IEditorPart pEditorpart) {
		return null;
		//throw new RuntimeException("Not implemented");
	}
	
	
}
