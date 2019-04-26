package com.smartwebproject.testframework.ui.scenario;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jdt.internal.corext.util.Strings;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData; 
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wst.xml.ui.internal.tabletree.XMLMultiPageEditorPart;

import com.smartwebproject.testframework.ui.SWTFactory;
import com.smartwebproject.testframework.ui.editor.TestScenarioPluginConstants;


public class ScenarioLauncherTab extends AbstractLaunchConfigurationTab implements ModifyListener, SelectionListener {
	Combo env, browser;
	Button useJavaDebugger;
	private Composite mcomp;
	private Combo scenarioCombo;
	private ILaunchConfiguration configCopy = null;
	private IProject project;
	
	/*List<String> envlist = null;
	List<String> browserlist = null;
	ThreadLocal<Integer> actiontimeout, shorttimeout;
	ThreadLocal<Boolean> commandlineonerror;
	ThreadLocal<Boolean> nocommandline;*/
	
	
	public void createPropertyEditors(Composite comp) {
		
		Label l = new Label(comp, SWT.NONE);
		l.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		l.setText("Scenario");
		
		scenarioCombo = new Combo(mcomp, SWT.NONE);
		GridData gd_scenarioCombo = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_scenarioCombo.widthHint = 122;
		scenarioCombo.setLayoutData(gd_scenarioCombo);

		l = new Label(comp, SWT.NONE);
		l.setText("Environment");
		env = new Combo(comp, SWT.DROP_DOWN | SWT.BORDER);
		l = new Label(comp, SWT.NONE);
		l.setText("Browser");
		browser = new Combo(comp, SWT.DROP_DOWN | SWT.BORDER);
		//useJavaDebugger = new Button(comp, SWT.CHECK);
		//useJavaDebugger.setText("use Java debugger");
	}
	
	@PostConstruct
	public void createControl(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		mcomp = SWTFactory.createComposite(parent, 2, 1, GridData.FILL_HORIZONTAL);
		setControl(mcomp);
		createPropertyEditors(mcomp);
	}

	@Override
	public String getName() {
		return "Scenario";
	}

	@Override
	public void initializeFrom(ILaunchConfiguration ilaunchConfiguration) {
		
		if(ilaunchConfiguration==null)
			return;
		configCopy = ilaunchConfiguration;
		env.removeModifyListener(this);
		browser.removeModifyListener(this);
		scenarioCombo.removeModifyListener(this);
		env.removeAll();
		browser.removeAll();
		scenarioCombo.removeAll();
		getItems();
		addList(env, getEnvironments());
		addList(browser, getBrowsers(), "firefox");
		try {
			env.setText(ilaunchConfiguration.getAttribute(ScenarioLauncherConstants.ENVIRONMENT_ATTR, ""));
			browser.setText(ilaunchConfiguration.getAttribute(ScenarioLauncherConstants.BROWSER_ATTR, ""));
			scenarioCombo.setText(ilaunchConfiguration.getAttribute(ScenarioLauncherConstants.SCENARIO_ATTR, ""));
			//useJavaDebugger.setSelection(ilaunchConfiguration.getAttribute(ScenarioLauncherConstants.USE_JAVA_DEBUGGER_ATTR, false));
		} catch(CoreException e) {
			throw new RuntimeException(e);
		}
		env.addModifyListener(this);
		browser.addModifyListener(this);
		scenarioCombo.addModifyListener(this);
		//useJavaDebugger.addSelectionListener(this);
	}
	
	

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy iConfigCopy) {
		iConfigCopy.setAttribute(ScenarioLauncherConstants.ENVIRONMENT_ATTR, env.getText());
		iConfigCopy.setAttribute(ScenarioLauncherConstants.BROWSER_ATTR, browser.getText());
		iConfigCopy.setAttribute(ScenarioLauncherConstants.SCENARIO_ATTR, scenarioCombo.getText());
		//iConfigCopy.setAttribute(ScenarioLauncherConstants.USE_JAVA_DEBUGGER_ATTR, useJavaDebugger.getSelection());
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy iConfigCopy) {
		configCopy = iConfigCopy;
		//env.setText("rhtzptng06");
		//browser.setText("internetExplorer");
		//	scenario.setText("");
	}





	@Override
	public void modifyText(ModifyEvent pE) {
		setDirty(true);
		if(pE.getSource()!=null)
			updateLaunchConfigurationDialog();
	}

	
	IEclipsePreferences getEclipsePreferences() {
		try {
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			String projectName = configCopy.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME,"");
			project = null;
			if(!StringUtils.isEmpty(projectName)) {
				project = root.getProject(projectName);
				ProjectScope ps = new ProjectScope(project);
				return ps.getNode(TestScenarioPluginConstants.PLUGIN_ID);
			}
			return null;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public List<IResource> getItems() {
		try {
			ArrayList<IResource> items = new ArrayList<IResource>();
			
			if(configCopy==null)
				return items;
			IEclipsePreferences prefs = getEclipsePreferences();
			String scName = "";
			if(prefs != null)
				scName = prefs.get(TestScenarioPluginConstants.SCENARIO_ROOT, "");
			if(project==null)
				return items;
			IContainer scenariosContainer = project.getFolder(scName);
			
			
			IContentTypeManager contentTypeManager = Platform.getContentTypeManager();
			IContentType scenarioType = contentTypeManager.getContentType(ScenarioLauncherConstants.SCENARIO_CONTENT_TYPE);
			int scNameLength = scName.length()+1;
			for(IResource res: getMembers(scenariosContainer)) {
				IContentType[] ctlist = contentTypeManager.findContentTypesFor(res.getLocation().toString());
				for(IContentType ct: ctlist) {
					if(ct.equals(scenarioType)) {
						items.add(res);
						String scenarioName = res.getProjectRelativePath().toString().substring(scNameLength);
						if(scenarioName.endsWith(".xml"))
							scenarioName = scenarioName.substring(0, scenarioName.length()-4);
						scenarioCombo.add(scenarioName);
						break;
					}
				}
			}
			return items;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static List<IResource> getMembers(IContainer c) throws CoreException {
		List<IResource> resources = new ArrayList<IResource>();
		if(c ==null)
			return resources;
		for(IResource m: c.members()) {
			if(m instanceof IFile)
				resources.add(m);
			else if(m instanceof IContainer)
				resources.addAll(getMembers((IContainer)m));
		}
		return resources;
	}
	
	public void addList(Combo c, List<String> list, String defval) {
		String def = "";
		if(list.contains(defval))
			def = defval;
		c.setText(def);
		for(String item: list) {
			c.add(item);
		}
	}
	
	public void addList(Combo c, List<String> list) {
		addList(c, list, "");
	}
	
	
	
	public IProject getProject() {
		try {
			if(configCopy==null)
				return null;
			String projectName = configCopy.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, "");
			IProject project = null;
			if(!StringUtils.isEmpty(projectName))
				project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			return project;
		} catch(CoreException e) {
			throw new RuntimeException(e);
		}
	}
	
	public List<String> getEnvironments() {
		return getPropertyFilesFromSubdir(TestScenarioPluginConstants.ENV_DIR);
	}
	
	public List<String> getBrowsers() {
		return getPropertyFilesFromSubdir(TestScenarioPluginConstants.BROWSERS_DIR);
	}
	
	public List<String> getPropertyFilesFromSubdir(String projectPropertyName) {
		try {
			List<String> retval = new ArrayList<String>();
			IProject p = getProject();
			if(p==null)
				return retval;
			IEclipsePreferences prefs = getEclipsePreferences();
			if(prefs==null)
				return retval;
			String subdir = prefs.get(projectPropertyName, "");
			if(subdir==null || "".equals(subdir))
				return retval;
			IFolder folder = p.getFolder(subdir);
			IResource[] resarray = folder.members();
			for(IResource res: resarray) {
				if((res instanceof IFile) && (res.getName().endsWith(".properties")))
					retval.add(res.getName().substring(0, res.getName().lastIndexOf('.')));
			}
			return retval;
		} catch(CoreException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void setConfigCopy(ILaunchConfiguration pConfigCopy) {
		configCopy = pConfigCopy;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		setDirty(true);
		if(e.getSource()!=null)
			updateLaunchConfigurationDialog();
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		setDirty(true);
		if(e.getSource()!=null)
			updateLaunchConfigurationDialog();
	}
	
	
}
