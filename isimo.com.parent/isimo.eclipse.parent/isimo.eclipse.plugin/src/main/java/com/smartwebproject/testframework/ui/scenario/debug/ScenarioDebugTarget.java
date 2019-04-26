package com.smartwebproject.testframework.ui.scenario.debug;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManagerListener;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.internal.core.sourcelookup.SourceLookupUtils;
import org.eclipse.debug.internal.ui.contexts.DebugContextManager;
import org.eclipse.debug.internal.ui.sourcelookup.SourceLookupManager;
import org.eclipse.debug.internal.ui.sourcelookup.SourceLookupService;
import org.eclipse.debug.internal.ui.sourcelookup.SourceLookupUIUtils;
import org.eclipse.debug.ui.contexts.DebugContextEvent;
import org.eclipse.debug.ui.contexts.IDebugContextListener;
import org.eclipse.jdt.debug.core.IJavaDebugTarget;
import org.eclipse.jdt.internal.debug.core.model.JDIThread;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

import com.google.gson.Gson;
import com.isimo.core.event.DebuggerEvent;
import com.isimo.core.event.DebuggerEvent.DebuggerEventType;
import com.isimo.core.event.StackTrace;
import com.smartwebproject.testframework.ui.TestFrameworkUIPlugin;
import com.smartwebproject.testframework.ui.editor.TestScenarioPluginConstants;
import com.smartwebproject.testframework.ui.scenario.ScenarioBreakpoint;

public class ScenarioDebugTarget extends ScenarioDebugElement implements IDebugTarget, ILaunchListener, IBreakpointManagerListener, IDebugEventSetListener, IDebugContextListener {
	public enum State { Running, Suspended, Terminated, Stepping};

	private IProcess process;
	private ScenarioDebugThread[] threads;
	private Socket requestSocket, eventSocket;
	private InputStream requestIS, eventIS;
	private OutputStream requestOS, eventOS;
	private StackTrace stackTrace = new StackTrace();
	private IStackFrame[] stackFrames = new IStackFrame[0];
	private ArrayList<IBreakpoint> breakpointsList = new ArrayList<IBreakpoint>();
	private PrintWriter requestPW = null;
	private Gson gson = new Gson();
    protected State currentState;
    protected State requestedState;
	
	public ScenarioDebugTarget(ILaunch launch, IProcess process, int requestPort, int eventPort) {
		super();
		this.launch = launch;
		this.process = process;
		threads = new ScenarioDebugThread[1];
		threads[0] = new ScenarioDebugThread(this);
		try {
			boolean portsOpen = false;
			int i = 0;
			while(!portsOpen) {
				try {
					requestSocket = new Socket("localhost", requestPort);
					eventSocket = new Socket("localhost", eventPort);
					requestIS = requestSocket.getInputStream();
					eventIS = eventSocket.getInputStream();
					eventOS = eventSocket.getOutputStream();
					requestIS = requestSocket.getInputStream();
					requestOS = requestSocket.getOutputStream();
					requestPW = new PrintWriter(new OutputStreamWriter(requestOS));
					portsOpen = true;
				} catch(IOException e) {
					Thread.sleep(100);
					// waiting for the ports to be open
					i++;
					if(i > 100) {
						throw new RuntimeException("Can't connect to scenario runner");
					}
				}
			} 
			ScenarioEventListener listener = new ScenarioEventListener(this.getName(), this);
			listener.schedule();
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		DebugPlugin.getDefault().getBreakpointManager().addBreakpointListener(this);
		DebugContextManager.getDefault().addDebugContextListener(this);
		currentState = State.Suspended;
		initializeInterpreter();
	}
	
	void initializeInterpreter() {
		installDeferredBreakpoints();
		requestResume();
	}
	
	void requestSuspend() {
		this.requestedState = State.Suspended;
		DebuggerEvent event = new DebuggerEvent();
		event.setEventType(DebuggerEventType.Suspend);
		sendEvent(event);
	}
	
	void requestResume() {
		this.requestedState = State.Running;
		DebuggerEvent event = new DebuggerEvent();
		event.setEventType(DebuggerEventType.Resume);
		sendEvent(event);
	}
	
	@Override
	public void resume() throws DebugException {
		// TODO Auto-generated method stub
		requestResume();
	}
	
	void installDeferredBreakpoints() {
		IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager()
	             .getBreakpoints(TestScenarioPluginConstants.PLUGIN_ID);
	       for (int i = 0; i < breakpoints.length; i++) {
	          breakpointAdded(breakpoints[i]);
	       }
	}
	
	public IBreakpoint[] getBreakpoints() {
		return breakpointsList.toArray(new IBreakpoint[breakpointsList.size()]);
	}

	@Override
	public IDebugTarget getDebugTarget() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		if (adapter == IDebugEventSetListener.class) {
		       return (T) this;
		}
		return super.getAdapter(adapter);
	}

	@Override
	public void breakpointAdded(IBreakpoint pBreakpoint) {
		breakpointsList.add(pBreakpoint);
		sendBreakpoint(pBreakpoint);
	}

	@Override
	public void breakpointRemoved(IBreakpoint pBreakpoint, IMarkerDelta pDelta) {
		breakpointsList.remove(pBreakpoint);
		sendBreakpoint(pBreakpoint);
	}

	@Override
	public void breakpointChanged(IBreakpoint pBreakpoint, IMarkerDelta pDelta) {
		IBreakpoint e = null;
		for(IBreakpoint existing: breakpointsList) {
			if(existing.equals(pBreakpoint)) {
				e = existing;
				break;
			}
		}
		if(e!=null)
			breakpointsList.remove(e);
		breakpointsList.add(pBreakpoint);
		sendBreakpoint(pBreakpoint);
	}
	
	private void sendBreakpoint(IBreakpoint bp) {
		if(!(bp instanceof ScenarioBreakpoint))
			return;
		ScenarioBreakpoint breakpoint = (ScenarioBreakpoint) bp;
		DebuggerEvent de = new DebuggerEvent();
		de.setEventType(DebuggerEventType.SetBreakpoint);
		de.setBreakpoint(breakpoint.toTestFrameworkBreakpoint(getDebugTarget().getLaunch().getAttribute(TestScenarioPluginConstants.SCENARIO_ROOT)));
		sendEvent(de);
	}
	
	public void sendEvent(DebuggerEvent de) {
		try {
			requestPW.println(gson.toJson(de));
			requestPW.flush();
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	

	@Override
	public boolean canDisconnect() {
		return true;
	}

	@Override
	public void disconnect() throws DebugException {
		terminate();
	}

	@Override
	public boolean isDisconnected() {
		return isTerminated();
	}

	@Override
	public boolean supportsStorageRetrieval() {
		return false;
	}

	@Override
	public IMemoryBlock getMemoryBlock(long pStartAddress, long pLength) throws DebugException {
		return null;
	}

	@Override
	public void handleDebugEvents(DebugEvent[] pEvents) {
		for(DebugEvent event: pEvents) {
			handleDebugEvent(event);
		}
	}
	
	void handleDebugEvent(DebugEvent event) {
		Object source = event.getSource();
	}

	@Override
	public void breakpointManagerEnablementChanged(boolean pEnabled) {
		// TODO Auto-generated method stub
	}

	@Override
	public void launchRemoved(ILaunch pLaunch) {
		
	}

	@Override
	public void launchAdded(ILaunch pLaunch) {
		
	}

	@Override
	public void launchChanged(ILaunch pLaunch) {
		
	}

	@Override
	public IProcess getProcess() {
		return process;
	}

	@Override
	public IThread[] getThreads() throws DebugException {
		return threads;
	}

	@Override
	public boolean hasThreads() throws DebugException {
		return true;
	}

	@Override
	public String getName() throws DebugException {
		return "Scenario";
	}

	@Override
	public boolean supportsBreakpoint(IBreakpoint pBreakpoint) {
		try {
			return(pBreakpoint.getMarker().isSubtypeOf(ScenarioBreakpoint.BREAKPOINT_MARKER));
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	public InputStream getRequestIS() {
		return requestIS;
	}

	public void setRequestIS(InputStream pRequestIS) {
		requestIS = pRequestIS;
	}

	public InputStream getEventIS() {
		return eventIS;
	}

	public void setEventIS(InputStream pEventIS) {
		eventIS = pEventIS;
	}

	public OutputStream getRequestOS() {
		return requestOS;
	}

	public void setRequestOS(OutputStream pRequestOS) {
		requestOS = pRequestOS;
	}

	public OutputStream getEventOS() {
		return eventOS;
	}

	public void setEventOS(OutputStream pEventOS) {
		eventOS = pEventOS;
	}
	
	public void setStackTrace(StackTrace stackTrace) {
		this.stackTrace = stackTrace;
		setStackFrames(fromStackTrace(stackTrace));
		fireChangeEvent(DebugEvent.CONTENT);
		threads[0].fireChangeEvent(DebugEvent.CONTENT);
	}
	
	public IStackFrame[] fromStackTrace(StackTrace st) {
		ArrayList<IStackFrame> retval = new ArrayList<IStackFrame>();
		fromStackTrace(retval, st);
		return retval.toArray(new IStackFrame[retval.size()]);
	}
	
	public void fromStackTrace(ArrayList<IStackFrame> stackFrames, StackTrace st) {
		if(st==null)
			return;
		stackFrames.add(new ScenarioStackFrame(this, st));
		fromStackTrace(stackFrames, st.parentStackTrace);
	}

	public IStackFrame[] getStackFrames() {
		return stackFrames;
	}

	public void setStackFrames(IStackFrame[] pStackFrames) {
		stackFrames = pStackFrames;
	}

	@Override
	public void terminate() throws DebugException {
		currentState = State.Terminated;
		DebuggerEvent de = new DebuggerEvent();
		de.setEventType(DebuggerEventType.Terminate);
		sendEvent(de);
	}
	
	@Override
	public boolean isTerminated() {
		return State.Terminated.equals(currentState);
	}
	
	@Override
	public boolean isSuspended() {
		return State.Suspended.equals(currentState);
	}
	
	@Override
	public boolean isStepping() {
		return State.Stepping.equals(currentState);
	}
	
	@Override
	public ILaunch getLaunch() {
		return launch;
	}

	public State getCurrentState() {
		return currentState;
	}

	@Override
	public void debugContextChanged(DebugContextEvent event) {
		System.out.println("event="+event);
		displaySource(event, true);
	}
	
	public ScenarioStackFrame findFrame(ScenarioDebugTarget target) {
		System.out.println("Frames "+target.getStackFrames());
		if(target.getStackFrames()==null || target.getStackFrames().length == 0)
			return null;
		else
			return (ScenarioStackFrame) target.getStackFrames()[0];
	}
	
	public IStackFrame findFrame(Object context) {
		IStackFrame frame = null;
		if(context instanceof IStackFrame)
			frame = (IStackFrame) context;
		else if(context instanceof IThread) {
			try {
				frame = ((IThread)context).getTopStackFrame();
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		} else if(context instanceof IDebugTarget) {
			IDebugTarget target = (IDebugTarget) context;
			try {
				frame = findFrame(target.getThreads()[0]);
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
		/*else if(context instanceof IDebugElement) {
			IDebugTarget target = ((IDebugElement)context).getDebugTarget();
			for(IDebugTarget t: target.getLaunch().getDebugTargets()) {
				if(t instanceof ScenarioDebugTarget) {
					System.out.println("finding frame in ScenarioDebugTarget");
					frame = findFrame((ScenarioDebugTarget)t);
				}
			}
		}*/
		return frame;
	}
	
	protected synchronized void displaySource(DebugContextEvent event, boolean force) {
		IWorkbenchPart part = event.getDebugContextProvider().getPart();
		IWorkbenchWindow window = part.getSite().getPage().getWorkbenchWindow();
		ISelection selection = event.getContext();
	    if (window == null) return; // disposed

		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection)selection;
			if (structuredSelection.size() == 1) {
				Object context = structuredSelection.getFirstElement();
				IStackFrame frame = findFrame(context);
				if(frame==null) {
					System.out.println("frame is null");
					return;
				}
				IWorkbenchPage page = null;
				page = window.getActivePage();
				if (part == null) {
					page = window.getActivePage();
				} else {
					page = part.getSite().getPage();
				}
				System.out.println("displaySource");
				SourceLookupManager.getDefault().displaySource(frame, page, force);
			}
		}
	}
}
