package com.smartwebproject.testframework.ui.scenario;

import org.eclipse.jdt.internal.junit.runner.ITestReference;
import org.eclipse.jdt.internal.junit.runner.RemoteTestRunner;
import org.eclipse.jdt.internal.junit4.runner.FailuresFirstSorter;
import org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader;
import org.eclipse.jdt.internal.junit4.runner.JUnit4TestReference;
import org.junit.runner.Description;
import org.junit.runner.Request;
import org.junit.runner.Runner;

public class ScenarioJUnit4TestLoader extends JUnit4TestLoader {
	@Override
	public ITestReference[] loadTests(Class[] testClasses, String testName, String[] failureNames, String[] pPackages, String[][] pIncludeExcludeTags, String pUniqueId,
			RemoteTestRunner listener) {
		ITestReference[] refs= new ITestReference[testClasses.length];
		for (int i= 0; i < testClasses.length; i++) {
			Class<?> clazz= testClasses[i];
			ITestReference ref= createTest(clazz, testName, failureNames, listener);
			refs[i]= ref;
		}
		return refs;
	}
	
	private ITestReference createTest(Class<?> clazz, String testName, String[] failureNames, RemoteTestRunner listener) {
		JUnit4TestLoader jUnit4TestLoader= new JUnit4TestLoader();
		Class[] clazzez = new Class[1];
		clazzez[0] = clazz; 
		return createUnfilteredTest(clazz, failureNames);
	}
	private ITestReference createUnfilteredTest(Class<?> clazz, String[] failureNames) {
		Request request= sortByFailures(Request.aClass(clazz), failureNames);
		Runner runner= request.getRunner();
		Description description= runner.getDescription();
		return new JUnit4TestReference(runner, description);
	}
	
	private Request sortByFailures(Request request, String[] failureNames) {
		if (failureNames != null) {
			return request.sortWith(new FailuresFirstSorter(failureNames));
		}
		return request;
	}
}
