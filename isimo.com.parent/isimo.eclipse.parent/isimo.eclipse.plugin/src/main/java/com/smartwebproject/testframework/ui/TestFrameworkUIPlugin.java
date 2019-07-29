package com.smartwebproject.testframework.ui;

import java.io.File;
import java.net.URI;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.BundleActivator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.w3c.dom.Document;

import com.isimo.core.SpringContext;
import com.isimo.core.model.Model;
import com.smartwebproject.testframework.ui.editor.TestScenarioEditorPart;
import com.smartwebproject.testframework.ui.editor.TestScenarioPluginConstants;

@SpringBootApplication
public class TestFrameworkUIPlugin extends AbstractUIPlugin implements BundleActivator {
	private IPreferenceStore preferenceStore;
	private static TestFrameworkUIPlugin plugin;
	private Document model = null;
	private TestScenarioEditorPart lastVisited;

	public TestFrameworkUIPlugin() {
		super();
		plugin = this;
	}

	public IPreferenceStore getPreferenceStore() {
		if (this.preferenceStore == null) {
			this.preferenceStore = new TestFrameworkPreferenceStore(InstanceScope.INSTANCE, this.getBundle().getSymbolicName());
		}
		return this.preferenceStore;
	}

	public IWorkbench getWorkbench() {
		return PlatformUI.getWorkbench();
	}

	public static TestFrameworkUIPlugin getDefault() {
		return plugin;
	}
	
	public Document getModelForURI(String uri) {
		System.setProperty(Model.MODEL_PATH_PROPERTY_NAME, getModelPathForUri(uri));
		return Model.getInstance().getModel();
	}

	public void setModel(Document pModel) {
		model = pModel;
	}

	public TestScenarioEditorPart getLastVisited() {
		return lastVisited;
	}

	public void setLastVisited(TestScenarioEditorPart pLastVisited) {
		lastVisited = pLastVisited;
	}

	public String getId() {
		return "isimo.eclipse.plugin";
	}

	public Shell getShell() {
		return getWorkbench().getActiveWorkbenchWindow().getShell();
	}

	public IScopeContext[] createPreferenceScopes(IProject project) {
		if (project != null && project.isAccessible()) {
			final ProjectScope projectScope = new ProjectScope(project);
			return new IScopeContext[] { projectScope, InstanceScope.INSTANCE, DefaultScope.INSTANCE };
		}
		return new IScopeContext[] { InstanceScope.INSTANCE, DefaultScope.INSTANCE };
	}
	
	public IScopeContext[] createPreferenceScopes(IResource resource) {
		return createPreferenceScopes(resource.getProject());
	}
	
	public String getModelPathForResource(IResource resource) {
		IPreferencesService service = Platform.getPreferencesService();
		return service.getString(getBundle().getSymbolicName(), TestScenarioPluginConstants.TF_MODEL, "", createPreferenceScopes(resource));
	}
	
	public String getModelPathForUri(String uristr) {
		try {
			URI uri = new URI(uristr);
			IWorkspaceRoot myWorkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
			IResource modelRes = myWorkspaceRoot.findFilesForLocationURI(uri)[0];
			return TestFrameworkUIPlugin.getDefault().getModelPathForResource(modelRes);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static IFile getIncludedScenarioFile(IFile sourceFile, String includePath) {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = sourceFile.getProject();
		try {
			return project.getFile(getProjectProperty(project, TestScenarioPluginConstants.SCENARIO_ROOT)+File.separator+includePath+".xml");
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String getProjectProperty(IProject project, String key) throws CoreException {
		ProjectScope scope = new ProjectScope(project);
		return scope.getNode(TestScenarioPluginConstants.PLUGIN_ID).get(key,"");
	}

	
	public String getSuspendOnErrorPropertyName() {
		return "isimo.suspendonerror";
	}
	
	public String getSuspendOnFailurePropertyName() {
		return "isimo.suspendonfailure";
	}
	
	
}
