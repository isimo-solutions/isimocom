package com.isimo.scenariolog;

import java.io.File;
import java.util.Stack;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.isimo.core.Action;
import com.isimo.core.CompoundAction;
import com.isimo.core.TestExecutionManager;
import com.isimo.core.event.Event;
import com.isimo.core.event.EventType;
import com.isimo.core.event.ExecutionListener;

@Component
public class ScenarioLogExecutionListener implements ExecutionListener<Event> {
	Document testcaseLog = null;
	Stack<Element> stack = new Stack<Element>();
	String scenarioName;
	
	@Autowired
	TestExecutionManager testExecutionManager;
	
	@Override
	public void handleEvent(Event event) {
		if(event.getEventType()==EventType.StartTestCase) {
			initTestcaseLog();
		} else if(event.getEventType()==EventType.StartAction) {
			addActionToLog(event.getCurrentAction());
			if(event.getCurrentAction() instanceof CompoundAction)
				stack.push(event.getCurrentAction().getLog());
		} else if(event.getEventType()==EventType.StopAction) {
			if(event.getCurrentAction() instanceof CompoundAction)
				stack.pop();
		}
		flushXmlLog();
	}
	
	public void flushXmlLog() {
		if(testcaseLog.getRootElement()!=null) {
			testcaseLog.getRootElement().addAttribute("status", testExecutionManager.testStatus.toString());
			testcaseLog.getRootElement().addAttribute("execution", testExecutionManager.testExecution.toString());
		}
		saveTestResultFile("scenariolog.xml", testcaseLog.asXML());
	}
	
	void initTestcaseLog() {
		testcaseLog = DocumentHelper.createDocument(stack.push(DocumentHelper.createElement("testcase")));
	}
	
	void addActionToLog(Action action) {
		if (action != null && action.getLog() != null) {
			action.getLog().setParent(null);
			getCurrentParent().add(action.getLog());
		}
	}
	
	public void saveTestResultFile(String filename, String content) {
		try {
			FileWriterWithEncoding xmllog = new FileWriterWithEncoding(testExecutionManager.getReportDir()+File.separator+filename, "UTF-8");
			xmllog.write(content);
			xmllog.flush();
			xmllog.close();
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public Element getCurrentParent() {
		return stack.peek();
	}



}
