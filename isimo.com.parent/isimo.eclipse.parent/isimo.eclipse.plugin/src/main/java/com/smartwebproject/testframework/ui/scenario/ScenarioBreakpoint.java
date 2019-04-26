package com.smartwebproject.testframework.ui.scenario;

import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.LineBreakpoint;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.model.LineBreakpoint;
import org.eclipse.wst.xsl.internal.launching.Messages;

import com.isimo.core.event.Breakpoint;
import com.smartwebproject.testframework.ui.TestFrameworkUIPlugin;
import com.smartwebproject.testframework.ui.editor.TestScenarioPluginConstants;

public class ScenarioBreakpoint extends LineBreakpoint {
	   private int lineNumber;
	   private IResource resource;
	 
	   public ScenarioBreakpoint()
	   {
	   }
	 
	   public ScenarioBreakpoint(final IResource resource, final int lineNumber, final int charStart, final int charEnd)
	     throws CoreException
	   {
	     this.lineNumber = lineNumber;
	     this.resource = resource;
	     IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
	       public void run(IProgressMonitor monitor) throws CoreException
	       {
	         IMarker marker = resource.createMarker("com.smartwebproject.testframework.ui.scenarioBreakpointMarker");
	         ScenarioBreakpoint.this.setMarker(marker);
	         marker.setAttribute(IBreakpoint.ID, ScenarioBreakpoint.this.getModelIdentifier());
	         marker.setAttribute(IBreakpoint.ENABLED, Boolean.TRUE);
	         
	         marker.setAttribute(IMarker.MESSAGE, "ScenarioBreakpoint " + resource.getName() + " [line: " + lineNumber + "]");
	         marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
	         marker.setAttribute(IMarker.CHAR_START, Integer.valueOf(charStart));
	         marker.setAttribute(IMarker.CHAR_END, Integer.valueOf(charEnd));
	         register(true);
	       }
	     };
	     run(getMarkerRule(resource), runnable);
	   }
	 
	   public int getLineNumber()
	     throws CoreException
	   {
	     int line = super.getLineNumber();
	     return ((line == -1) ? this.lineNumber : line);
	   }
	 
	   protected void register(boolean register) throws CoreException
	   {
	     DebugPlugin plugin = DebugPlugin.getDefault();
	     if ((plugin != null) && (register))
	     {
	       plugin.getBreakpointManager().addBreakpoint(this);
	     }
	     else
	     {
	       setRegistered(false);
	     }
	   }
	 
	
	@Override
	public String getModelIdentifier() {
		// TODO Auto-generated method stub
		return TestScenarioPluginConstants.PLUGIN_ID;
	}
	
	public Breakpoint toTestFrameworkBreakpoint(String scenariosRoot) {
		try {
			Breakpoint bp = new Breakpoint();
			bp.lineNumber = getLineNumber();
			bp.scenarioPath = getMarker().getResource().getFullPath().toString();
			if(bp.scenarioPath.startsWith(scenariosRoot))
				bp.scenarioPath = bp.scenarioPath.substring(scenariosRoot.length()+1);
			return bp;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}


}
