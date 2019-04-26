package com.smartwebproject.testframework.ui.scenario;

import java.io.File;

public class ScenarioLauncherConstants {
	public static final String PLUGINID = "com.smartwebproject.testframework";
	public static final String PREFERENCE_FILE = PLUGINID+".xml";
	public static final String PREFIX = "com.smartwebproject.testframework.ui.scenario.";
	public static final String SCENARIO_ATTR = PREFIX+"SCENARIO";
	public static final String ENVIRONMENT_ATTR = PREFIX+"ENVIRONMENT";
	public static final String BROWSER_ATTR = PREFIX+"BROWSER";
	public static final String USE_JAVA_DEBUGGER_ATTR = PREFIX+"USEJAVADEBUGGER";
	public static final String TESTCLASS = "com.isimo.core.TestCases";
	public static final String TESTMETHOD = "testScenario";
	public static final String SCENARIO_CONTENT_TYPE = "com.smartwebproject.testframework.ui.editor.contenttype";
	public static final String SCENARIO_DEFAULT_WORKING_SUBDIR = "target"+File.separator+"tests";
	public static final String SCENARIO_DEFAULT_TESTCLASSES_OUTPUT ="target"+File.separator+"test-classes";
	public static final String DEFAULT_BROWSER="firefox";
}
