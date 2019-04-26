package com.smartwebproject.testframework.ui;

import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.osgi.internal.loader.EquinoxClassLoader;
import org.eclipse.osgi.internal.loader.classpath.ClasspathEntry;
import org.eclipse.osgi.storage.bundlefile.BundleEntry;
import org.osgi.framework.wiring.BundleWiring;

public class BaseLaunchHelper {
	public static String[] getClasspath(ILaunchConfiguration pConfiguration) throws CoreException {
		ArrayList<String> list = getClasspathAsList(pConfiguration);
		return list.toArray(new String[list.size()]);
	}
	
	public static ArrayList<String> getClasspathAsList(ILaunchConfiguration pConfiguration) throws CoreException {
		// TODO Auto-generated method stub
	    ArrayList<String> list = new ArrayList<String>();
		//Enumeration<BundleEntry> entries = ((EquinoxClassLoader)TestFrameworkUIPlugin.getDefault().getClass().getClassLoader()).getClasspathManager().findLocalEntries("");
		ClasspathEntry[] entries = ((EquinoxClassLoader)TestFrameworkUIPlugin.getDefault().getClass().getClassLoader()).getClasspathManager().getHostClasspathEntries();
		for(ClasspathEntry entry: entries) {
			list.add(entry.getBundleFile().getBaseFile().getAbsolutePath());
		}
		return list;
	}
}
