package com.smartwebproject.testframework.ui.launcher;

import java.io.BufferedInputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jdt.launching.JavaLaunchDelegate;
import org.eclipse.ui.IEditorPart;
import org.xml.sax.InputSource;

import com.google.gson.Gson;
import com.isimo.testcapture.TestCaptureListenerProcess;
import com.smartwebproject.testframework.ui.BaseLaunchHelper;
import com.smartwebproject.testframework.ui.TestFrameworkUIPlugin;
import com.smartwebproject.testframework.ui.editor.TestScenarioEditorPart;

public class TestCaptureListenerDelegate extends JavaLaunchDelegate implements ILaunchConfigurationDelegate {
	static String STEPMARKER = "%%STEP%%";
	static String HTMLMARKER = "%%HTML%%";
	static String EXITMARKER = "%%EXIT%%";
	public static int INTERNAL_LISTENER_PORT = 9998;
	public static TestCaptureEclipseListenerThread LISTENER_THREAD = null;
	
	@Override
	public String[] getClasspath(ILaunchConfiguration pConfiguration) throws CoreException {
		return BaseLaunchHelper.getClasspath(pConfiguration);
	}
	
	@Override
	public String verifyMainTypeName(ILaunchConfiguration pConfiguration) throws CoreException {
		return TestCaptureListenerProcess.class.getCanonicalName();
	}
	
	@Override
	public void launch(ILaunchConfiguration pConfiguration, String pMode, ILaunch pLaunch, IProgressMonitor pMonitor) throws CoreException {
		LISTENER_THREAD=new TestCaptureEclipseListenerThread();
		LISTENER_THREAD.start();
		super.launch(pConfiguration, pMode, pLaunch, pMonitor);
		IProcess process = pLaunch.getProcesses()[0];
		pLaunch.addProcess(new TestCaptureProcessDecorator(process));
		pLaunch.removeProcess(process);
	}
	
	
	public static class TestCaptureEclipseListenerThread extends Thread {
		PrintStream out = null;
		IEditorPart editor = null;
		
		public void run() {
			try {
				out = System.out;
				out.println("TestCaptureEclipseListenerThread starting");
				ServerSocket ss = new ServerSocket(INTERNAL_LISTENER_PORT);
				Socket s = ss.accept();
				BufferedInputStream br = new BufferedInputStream(s.getInputStream());
				String line = null;
				out.println("TestCaptureListenerProcess connected");
				int bytesRead = -1;
				byte[] buffer = new byte[100000];
				StringBuffer lineBuffer = new StringBuffer();
				String step;
				Gson gson = new Gson();
				DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				while((bytesRead=br.read(buffer)) != -1) {
					try {
						String chunk = new String(buffer, 0, bytesRead);
						lineBuffer.append(chunk);
						if(chunk.endsWith(STEPMARKER)) {
							step = lineBuffer.substring(0, lineBuffer.length()-STEPMARKER.length());
							CapturedStep cstep = gson.fromJson(step, CapturedStep.class);
							cstep.setHtml(URLDecoder.decode(cstep.getHtml(), "UTF-8"));
							if(cstep.getHtml().startsWith("<!DOCTYPE"))
								cstep.setHtml(cstep.getHtml().substring(cstep.getHtml().indexOf('>')+1));
							InputSource is = new InputSource(new StringReader(cstep.getHtml()));
							lineBuffer = new StringBuffer();
							String action = CapturedStep.step2Action(cstep);
							output("ACTION="+action);
							//IEditorPart editor = getActiveEditor();
							//if(editor != null && editor instanceof TestScenarioEditorPart) {
								//editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
								type(action);
							//}
							//out.println("HTML="+cstep.getHtml());
						}
						if(EXITMARKER.equals(chunk)) {
							ss.close();
							break;
						}
					} catch(Exception e) {
						e.printStackTrace(out);
						out.flush();
					}
				}
				out.println("TestCaptureEclipseListenerThread finished");
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		void output(String str) {
			out.println(str);
			out.flush();
		}
		
		public void type(String s) {
			TestScenarioEditorPart part = TestFrameworkUIPlugin.getDefault().getLastVisited();
			if(part!=null) {
				part.typeAtCursor(s);
			}
		}
		
		@Override
		public void interrupt() {
			// TODO Auto-generated method stub
			super.interrupt();
		}
	}
}
