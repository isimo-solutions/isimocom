package com.smartwebproject.testframework.ui.perspective;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.dynamichelpers.IExtensionChangeHandler;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.PlatformUI;

public class RemoveUnwantedPerspectives {

	public static final String[] IGNORE_PERSPECTIVES = new String[] {
	        "org.eclipse.birt.report.designer.ui.ReportPerspective", "org.eclipse.debug.ui.DebugPerspective",
	        "org.eclipse.jdt.ui.JavaPerspective", "org.eclipse.jdt.ui.JavaHierarchyPerspective",
	        "org.eclipse.jdt.ui.JavaBrowsingPerspective", "org.eclipse.mylyn.tasks.ui.perspectives.planning",
	        "org.eclipse.pde.ui.PDEPerspective", "org.eclipse.team.cvs.ui.cvsPerspective",
	        "org.eclipse.ui.resourcePerspective", }; 
	
	
}
