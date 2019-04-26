package com.smartwebproject.testframework.ui.launcher;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.commands.actions.TerminateCommandAction;
import org.eclipse.debug.internal.ui.views.console.ConsoleTerminateAction;
import org.eclipse.debug.internal.ui.views.console.ProcessConsolePageParticipant;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.xml.ui.internal.tabletree.XMLMultiPageEditorPart;

public class TestCaptureListenerConsolePageParticipant extends ProcessConsolePageParticipant {
	
	@Override
	public void handleDebugEvents(DebugEvent[] events) {
        for (int i = 0; i < events.length; i++) {
            DebugEvent event = events[i];
            if (event.getSource().equals(getProcess())) {
				Runnable r = () -> {
					if (getTerminate() != null) {
						getTerminate().update();
					}
				};

                DebugUIPlugin.getStandardDisplay().asyncExec(r);
            }
        }
    }
	
	public ConsoleTerminateAction getTerminate() {
		try {
			Field field = ProcessConsolePageParticipant.class.getDeclaredField("fTerminate");
			field.setAccessible(true);
			return (ConsoleTerminateAction) field.get(this);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
}
