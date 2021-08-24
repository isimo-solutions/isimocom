package com.smartwebproject.testframework.ui.scenario;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.ILaunchesListener2;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.RefreshTab;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.m2e.actions.MavenLaunchConstants;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;
import org.eclipse.m2e.core.project.ResolverConfiguration;

@SuppressWarnings("restriction")
public class PomExecutor {

	static {
		DebugPlugin.getDefault().getLaunchManager().addLaunchListener(new ILaunchesListener2() {
			
			
			public void launchesRemoved(ILaunch[] launches) {
				for(ILaunch l: launches) {
					ILaunchConfiguration conf = l.getLaunchConfiguration();
					synchronized(conf) {
						conf.notifyAll();
					}
				}
			}
			
			public void launchesChanged(ILaunch[] launches) {
			}
			
			public void launchesAdded(ILaunch[] launches) {
			}
			
			public void launchesTerminated(ILaunch[] launches) {
				for(ILaunch l: launches) {
					ILaunchConfiguration conf = l.getLaunchConfiguration();
					synchronized(conf) {
						conf.notifyAll();
					}
				}
			}
		});
	}
	
	
	private IProject project;

	public PomExecutor(IProject project) {
		this.project = project;
	}

	public void exec(String goals, IProgressMonitor monitor)
			throws CoreException {
		if (project == null) {
			return;
		}

		ILaunchConfiguration launchConfiguration = createLaunchConfiguration(project, goals);
		if (launchConfiguration == null) {
			return;
		}
		
		ILaunch launch = launchConfiguration.launch("run", monitor, false, true);
		synchronized(launchConfiguration) {
			while(!launch.isTerminated()) {
				try{ launchConfiguration.wait(5000L); } catch(InterruptedException e){}
			}
		}
	}

	private ILaunchConfiguration createLaunchConfiguration(IContainer basedir, String goals) {
		try {
			
			ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
			ILaunchConfigurationType launchConfigurationType = launchManager.getLaunchConfigurationType(MavenLaunchConstants.LAUNCH_CONFIGURATION_TYPE_ID);

			ILaunchConfigurationWorkingCopy workingCopy = launchConfigurationType.newInstance(null, "Executing POM");
			workingCopy.setAttribute(MavenLaunchConstants.ATTR_POM_DIR, basedir.getLocation().toOSString());
			workingCopy.setAttribute(MavenLaunchConstants.ATTR_GOALS, goals);
			workingCopy.setAttribute(MavenLaunchConstants.ATTR_UPDATE_SNAPSHOTS, true);
			workingCopy.setAttribute(IDebugUIConstants.ATTR_PRIVATE, true);
			workingCopy.setAttribute(RefreshTab.ATTR_REFRESH_SCOPE, "${project}");
			workingCopy.setAttribute(RefreshTab.ATTR_REFRESH_RECURSIVE, true);

			setProjectConfiguration(workingCopy, basedir);

			IPath path = getJREContainerPath(basedir);
			if (path != null) {
				workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_JRE_CONTAINER_PATH, path.toPortableString());
			}

			return workingCopy;
		} catch (CoreException ex) {
			// TODO: report error somewhere
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	private void setProjectConfiguration(ILaunchConfigurationWorkingCopy workingCopy, IContainer basedir) {
		IMavenProjectRegistry projectManager = MavenPlugin.getMavenProjectRegistry();
		IFile pomFile = basedir.getFile(new Path(IMavenConstants.POM_FILE_NAME));
		IMavenProjectFacade projectFacade = projectManager.create(pomFile, false, new NullProgressMonitor());
		if (projectFacade != null) {
			ResolverConfiguration configuration = projectFacade.getResolverConfiguration();
			
			String activeProfiles = configuration.getActiveProfiles();
			if (activeProfiles != null && activeProfiles.length() > 0) {
				workingCopy.setAttribute(MavenLaunchConstants.ATTR_PROFILES,
						activeProfiles);
			}
		}
	}

	// TODO ideally it should use MavenProject, but it is faster to scan
	// IJavaProjects
	private IPath getJREContainerPath(IContainer basedir) throws CoreException {
		IProject project = basedir.getProject();
		if (project != null && project.hasNature(JavaCore.NATURE_ID)) {
			IJavaProject javaProject = JavaCore.create(project);
			IClasspathEntry[] entries = javaProject.getRawClasspath();
			for (int i = 0; i < entries.length; i++) {
				IClasspathEntry entry = entries[i];
				if (JavaRuntime.JRE_CONTAINER.equals(entry.getPath().segment(0))) {
					return entry.getPath();
				}
			}
		}
		return null;
	}

}