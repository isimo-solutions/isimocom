package com.smartwebproject.testframework.ui.scenario;

import org.eclipse.jdt.internal.junit.runner.RemoteTestRunner;

public class ScenarioTestRunner extends RemoteTestRunner {
	public ScenarioTestRunner() {
		super();
	}
	
	public static void main(String[] args)  {
		try {
			ScenarioTestRunner testRunServer= new ScenarioTestRunner();
			testRunServer.init(args);
			testRunServer.setLoader(new ScenarioJUnit4TestLoader());
			testRunServer.run();
		} catch (Throwable e) {
			e.printStackTrace(); // don't allow System.exit(0) to swallow exceptions
		} finally {
			// fix for 14434
			System.exit(0);
		}
	}
	
	@Override
	protected Class loadTestLoaderClass(String pClassName) throws ClassNotFoundException {
		// TODO Auto-generated method stub
		return super.loadTestLoaderClass(pClassName);
	}
}
