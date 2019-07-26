package com.smartwebproject.testframework.ui.actionslauncher;


import java.util.ArrayList;
import java.util.Collection;

import javax.print.attribute.standard.JobHoldUntil;

import org.apache.commons.lang3.ThreadUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.internal.jobs.Worker;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.internal.progress.ProgressManager.JobMonitor;

import com.smartwebproject.testframework.ui.scenario.debug.ScenarioDebugTarget;
import com.smartwebproject.testframework.ui.scenario.debug.ScenarioDebugThread;
import com.smartwebproject.testframework.ui.scenario.debug.ScenarioEventListener;

public class RunCommand extends AbstractHandler {
	
	    @Override
	    public Object execute(ExecutionEvent event) throws ExecutionException {
	    	Shell shell = HandlerUtil.getActiveWorkbenchWindow(event).getShell();
	    	IEditorPart editor = HandlerUtil.getActiveEditor(event);
	    	ISelection sel = editor.getSite().getSelectionProvider().getSelection();
	    	if(sel instanceof TextSelection) {
	    		TextSelection s = (TextSelection)sel;
	    		String text = s.getText();
	    		if(text.length() <= 0)  {
	    			showError(shell, "Selected text does not contains proper actions!");
	    			return null;
	    		}
	    		Collection<Thread> threads = ThreadUtils.getAllThreads();
	    		ArrayList<ScenarioDebugTarget> targetList = new ArrayList<ScenarioDebugTarget>();
	    		for(Thread t : threads) {
    				if(t instanceof Worker) {
    					Worker worker = (Worker)t;
    					Job job = worker.currentJob();
	    				if(job instanceof ScenarioEventListener) {
	    					ScenarioEventListener scenarioListener = (ScenarioEventListener)job;
	    					ScenarioDebugTarget target = scenarioListener.getTarget();
	    					if(target.isSuspended()) {
	    						targetList.add(target);
	    					}
		    			}
    				}
	    		}
	    		if(targetList.size() == 1) {
	    			System.out.println("Executing commands:\n"+text);
	    			ScenarioDebugTarget target = targetList.get(0);
	    			//Validation and execute actions here
	    			
	    		}else if(targetList.size() > 1){
	    			showError(shell, "There is more than one suspended debug thread!");
	    			return null;
	    		}else {
	    			showError(shell, "There is no suspended debug thread!");
	    			return null;
	    		}
	    	}
	    	
	        return null;
	    }
	    
	    
	    void showError(Shell s, String message) {
	    	MessageDialog.openInformation(s, "Error", message);
			
	    }
	    
	}