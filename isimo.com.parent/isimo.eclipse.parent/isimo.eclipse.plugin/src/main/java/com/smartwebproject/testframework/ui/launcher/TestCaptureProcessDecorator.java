package com.smartwebproject.testframework.ui.launcher;

import java.net.ConnectException;
import java.net.Socket;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamsProxy;

public class TestCaptureProcessDecorator implements IProcess {
	private IProcess decorated;
	private IProgressMonitor monitor;
	public TestCaptureProcessDecorator(IProcess decorated) {
		this.decorated = decorated;
		fireCreationEvent();
	}

	@Override
	public <T> T getAdapter(Class<T> arg0) {
		return decorated.getAdapter(arg0);
	}

	@Override
	public boolean canTerminate() {
		return decorated.canTerminate();
	}

	@Override
	public boolean isTerminated() {
		return decorated.isTerminated();
	}

	@Override
	public void terminate() throws DebugException {
		try {
			
			try {
				Socket s = new Socket("localhost", TestCaptureListenerDelegate.INTERNAL_LISTENER_PORT);
				s.getOutputStream().write(TestCaptureListenerDelegate.EXITMARKER.getBytes());
				s.getOutputStream().flush();
			} catch(ConnectException e) {
				// socket probably closed already
			}
			decorated.terminate();
			do { Thread.sleep(100); } 
			while (!decorated.isTerminated());
			fireTerminateEvent();
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getAttribute(String name) {
		return decorated.getAttribute(name);
	}

	@Override
	public int getExitValue() throws DebugException {
		return decorated.getExitValue();
	}

	@Override
	public String getLabel() {
		return decorated.getLabel();
	}

	@Override
	public ILaunch getLaunch() {
		return decorated.getLaunch();
	}

	@Override
	public IStreamsProxy getStreamsProxy() {
		return decorated.getStreamsProxy();
	}

	@Override
	public void setAttribute(String arg0, String arg1) {
		decorated.setAttribute(arg0, arg1);
	}
	
	protected void fireEvent(DebugEvent event) {
		DebugPlugin manager= DebugPlugin.getDefault();
		if (manager != null) {
			manager.fireDebugEventSet(new DebugEvent[]{event});
		}
	}
	
	/**
	 * Fires a creation event.
	 */
	protected void fireCreationEvent() {
		fireEvent(new DebugEvent(this, DebugEvent.CREATE));
	}
	
	/**
	 * Fires a creation event.
	 */
	protected void fireTerminateEvent() {
		fireEvent(new DebugEvent(decorated, DebugEvent.TERMINATE));
		fireEvent(new DebugEvent(this, DebugEvent.TERMINATE));
	}
}
