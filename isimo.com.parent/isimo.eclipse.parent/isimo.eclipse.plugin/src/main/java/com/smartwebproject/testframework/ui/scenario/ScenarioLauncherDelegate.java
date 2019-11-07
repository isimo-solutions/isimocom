package com.smartwebproject.testframework.ui.scenario;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.internal.ui.actions.DebugContextualLaunchAction;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.core.PackageFragmentRoot;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;
import org.eclipse.jdt.internal.junit.launcher.ITestFinder;
import org.eclipse.jdt.internal.junit.launcher.ITestKind;
import org.eclipse.jdt.internal.junit.launcher.JUnitLaunchConfigurationConstants;
import org.eclipse.jdt.internal.junit.launcher.JUnitRuntimeClasspathEntry;
import org.eclipse.jdt.junit.launcher.JUnitLaunchConfigurationDelegate;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jst.ws.internal.common.ResourceUtils;

import com.isimo.core.IsimoProperties;
import com.isimo.core.SpringContext;
import com.isimo.core.TestCases;
import com.isimo.core.properties.PropertiesGenerator;
import com.isimo.core.properties.XProperties;
import com.smartwebproject.testframework.ui.BaseLaunchHelper;
import com.smartwebproject.testframework.ui.TestFrameworkUIPlugin;
import com.smartwebproject.testframework.ui.editor.TestScenarioPluginConstants;
import com.smartwebproject.testframework.ui.preferences.TestFrameworkConfigurationBlock;
import com.smartwebproject.testframework.ui.scenario.debug.ScenarioDebugTarget;

public class ScenarioLauncherDelegate extends JUnitLaunchConfigurationDelegate {

	@Override
	public String[] getClasspath(ILaunchConfiguration pConfiguration) throws CoreException {
		List<String> cplist = new ArrayList<String>();
		cplist.addAll(BaseLaunchHelper.getClasspathAsList(pConfiguration));
		String[] list = super.getBootpath(pConfiguration);
		for(String s: list) {
			cplist.add(s);
		}
		return cplist.toArray(new String[cplist.size()]);
	}

	protected IMember[] evaluateTests(ILaunchConfiguration configuration, IProgressMonitor monitor) throws CoreException {
		IJavaProject javaProject = getJavaProject(configuration);
		IJavaElement testTarget = getTestTarget(configuration, javaProject);
		String testMethodName = "testScenario";
		return new IMember[] { ((IType) testTarget).getMethod(testMethodName, new String[0]) };
	}

	private final IJavaElement getTestTarget(ILaunchConfiguration configuration, IJavaProject javaProject) throws CoreException {
		String testTypeName = ScenarioLauncherConstants.TESTCLASS;
		IType type= javaProject.findType(testTypeName);
		if (type != null && type.exists()) {
			return type;
		}
		return null;
	}
	
	@Override
	public synchronized void launch(ILaunchConfiguration pConfiguration, String pMode, ILaunch pLaunch, IProgressMonitor pMonitor) throws CoreException {
		String browser = getBrowser(pConfiguration);
		String environment = getEnvironmentName(pConfiguration);
		String scenario = getScenario(pConfiguration);
		boolean useJavaDebugger = getUseJavaDebugger(pConfiguration);
		XProperties props = new XProperties();
		props.setProperty("env", environment);
		props.setProperty("browser", browser);
		props.setProperty("isimo.browser", browser);
		props.setProperty("testdir", getWorkingDirectoryPath(pConfiguration).toString());
		props.setProperty("user.dir", getWorkingDirectoryPath(pConfiguration).toString());
		System.setProperty("testdir", getWorkingDirectoryPath(pConfiguration).toString());
		Integer requestPort = -1, eventPort = -1;
		if(ILaunchManager.DEBUG_MODE.equals(pMode)) {
			props.setProperty("isimo.debug.request.port", (requestPort=getAvailablePort()).toString());
			props.setProperty("isimo.debug.event.port", (eventPort=getAvailablePort()).toString());			
			props.setProperty("remote.debug", "true");
		}
		props.setProperty("isimo.closebrowseronerror", "false");
		props.setProperty("isimo.nocommandline", "suspend");
		props.setProperty("isimo.commandlineonerror", "false");
		props.setProperty("isimo.debug.mode", Boolean.toString(ILaunchManager.DEBUG_MODE.equals(pMode)));
		props.setProperty("isimo.suspendonerror", getProjectProperty(pConfiguration, TestFrameworkUIPlugin.getDefault().getSuspendOnErrorPropertyName()));
		props.setProperty("isimo.suspendonfailure", getProjectProperty(pConfiguration, TestFrameworkUIPlugin.getDefault().getSuspendOnErrorPropertyName()));
		File testDir = getWorkingDirectoryPath(pConfiguration).toFile();
		testDir.mkdirs();
		File inputProperties = new File(testDir.getAbsolutePath()+File.separator+"input.properties");
		File resultProperties = new File(testDir.getAbsolutePath()+File.separator+"test.properties");
		try {
			props.store(new FileWriter(inputProperties),"");
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
		PropertiesGenerator gen = new PropertiesGenerator();
		gen.setResultProperties(resultProperties);
		gen.setInputProperties(inputProperties);
		gen.setProperty("project.build.directory", getProject(pConfiguration).getLocation().toFile()+File.separator+"target");
		gen.setProperty("project.build.testSourceDirectory", getProject(pConfiguration).getLocation().toFile()+File.separator+"src"+File.separator+"test"+File.separator+"java");
		gen.generateProperties();
		//Properties props = config.getProperties(configdir.getAbsolutePath()+File.separator+"browser"+File.separator+"browser".properties", props);
		pLaunch.setAttribute(TestScenarioPluginConstants.SCENARIO_ROOT, getProject(pConfiguration).getFullPath()+"/"+getProjectProperty(pConfiguration, TestScenarioPluginConstants.SCENARIO_ROOT));
		//pLaunch.setAttribute(JUnitLaunchConfigurationConstants.ATTR_TEST_RUNNER_KIND, "com.smartwebproject.testframework.ui.scenario.kind");
		super.launch(pConfiguration, pMode, pLaunch, pMonitor);
		if(ILaunchManager.DEBUG_MODE.equals(pMode)) {
			IDebugTarget target = new ScenarioDebugTarget(pLaunch, pLaunch.getProcesses()[0], requestPort, eventPort);
			pLaunch.addDebugTarget(target);
			/*if(!useJavaDebugger) {
				JDIDebugTarget javatarget = null;
				for(IDebugTarget t: pLaunch.getDebugTargets()) { 
					if(t instanceof JDIDebugTarget)
						javatarget = (JDIDebugTarget)t;
				}
				pLaunch.removeProcess(javatarget.getProcess());
			}*/
		}
	}
	
	private Integer getAvailablePort() {
		try {
		    try (
		        ServerSocket socket = new ServerSocket(0);
		    ) {
		      return socket.getLocalPort();
		    }
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	static String getConfigProperty(ILaunchConfiguration config, String property, String defaultValue) {
		try {
			return config.getAttribute(property, defaultValue);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	};
	
	static boolean getBooleanConfigProperty(ILaunchConfiguration config, String property, boolean defaultValue) {
		try {
			return config.getAttribute(property, defaultValue);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	};
	
	static String getBrowser(ILaunchConfiguration config) {
		return getConfigProperty(config, ScenarioLauncherConstants.BROWSER_ATTR, ScenarioLauncherConstants.DEFAULT_BROWSER);
	};
	
	static String getEnvironmentName(ILaunchConfiguration config) {
		return getConfigProperty(config, ScenarioLauncherConstants.ENVIRONMENT_ATTR, "");
	};
	
	static String getScenario(ILaunchConfiguration config) {
		return getConfigProperty(config, ScenarioLauncherConstants.SCENARIO_ATTR, "");
	};
	
	static boolean getUseJavaDebugger(ILaunchConfiguration config) {
		return getBooleanConfigProperty(config, ScenarioLauncherConstants.USE_JAVA_DEBUGGER_ATTR, false);
	};
	
	static String getDebuggerType(ILaunchConfiguration config) {
//		return getConfigProperty(config, ScenarioLauncherConstants.DEBUGGER_ATTR, ScenarioLauncherConstants.DEFAULT_BROWSER);
		return null;
	};

	@Override
	public String verifyMainTypeName(ILaunchConfiguration configuration) throws CoreException {
		return "com.smartwebproject.testframework.ui.scenario.ScenarioTestRunner";
	}
	
	@Override
	public String getVMArguments(ILaunchConfiguration pConfiguration) throws CoreException {
		String vmarguments = super.getVMArguments(pConfiguration);
		vmarguments +="-Dscenario="+getScenario(pConfiguration)+" ";
		vmarguments +="-Dbrowser="+getBrowser(pConfiguration)+" ";
		vmarguments +="-Denv="+getEnvironmentName(pConfiguration)+" ";
		vmarguments +="-Dcom.isimo.scenarios="+calculateScenariosRoot(pConfiguration)+" ";
		vmarguments +="-Disimo.model.path="+getProjectProperty(pConfiguration, TestScenarioPluginConstants.TF_MODEL)+" ";
		vmarguments +="-Dtestdir="+getWorkingDirectoryPath(pConfiguration)+" ";
		vmarguments +="-Duser.dir="+getWorkingDirectoryPath(pConfiguration)+" ";
		return vmarguments;
	}
	
	public IProject getProject(ILaunchConfiguration pConfiguration) throws CoreException {
		return getJavaProject(pConfiguration).getProject();
	}
	
	public String getProjectProperty(ILaunchConfiguration pConfiguration, String key) throws CoreException {
		return TestFrameworkUIPlugin.getProjectProperty(getProject(pConfiguration), key);
	}
	
	public String calculateScenariosRoot(ILaunchConfiguration pConfiguration) throws CoreException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IJavaProject project = getJavaProject(pConfiguration);
		String scenariosDir = getProjectProperty(pConfiguration, TestScenarioPluginConstants.SCENARIO_ROOT);
		IPackageFragmentRoot[] paths = ResourceUtils.getJavaPackageFragmentRoots(getProject(pConfiguration));
		String sFolder = "";
		IPath targetLocation = null;
		for(IPackageFragmentRoot path: paths) {
			if(path instanceof PackageFragmentRoot) {
				String projectRelative = path.getResource().getProjectRelativePath().toString();
				if(scenariosDir.startsWith(projectRelative)) {
					if(!scenariosDir.equals(projectRelative)) {
						sFolder = scenariosDir.substring(projectRelative.length()+1);
					}
					targetLocation = path.getRawClasspathEntry().getOutputLocation();
					break;
				}
			}
		}
		targetLocation = targetLocation.removeFirstSegments(1);
		return project.getResource().getLocation().toFile().toString()+File.separator+targetLocation.toString()+File.separator+sFolder;
	}
	
	@Override
	public IPath getWorkingDirectoryPath(ILaunchConfiguration pConfiguration) throws CoreException {
		// TODO Auto-generated method stub
		return new Path(super.getDefaultWorkingDirectory(pConfiguration)+File.separator+ScenarioLauncherConstants.SCENARIO_DEFAULT_WORKING_SUBDIR);
	}
}
