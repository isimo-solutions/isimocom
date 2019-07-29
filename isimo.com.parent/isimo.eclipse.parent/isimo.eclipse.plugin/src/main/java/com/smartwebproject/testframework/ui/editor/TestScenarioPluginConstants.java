package com.smartwebproject.testframework.ui.editor;

import com.isimo.core.IsimoProperties;
import com.isimo.core.SpringContext;
import com.isimo.core.TestCases;
import com.smartwebproject.testframework.ui.TestFrameworkUIPlugin;

public class TestScenarioPluginConstants {	
	public final static String TEST_SCENARIO_CONTENT_TYPE = "com.smartwebproject.testframework.ui.editor.contenttype";
	public final static String TF_MODEL ="tf.model";
	public final static String SCENARIO_ROOT ="scenario.root";
	public final static String ENV_DIR ="environments.dir";
	public final static String BROWSERS_DIR ="browsers.dir";
	public final static String MODEL_ATTRIBUTE_NAME = "model";
	public final static String CONSOLE_NAME = "com.smartwebproject.testframework.ui.launcher.TestCaptureListenerConsole";
	public final static String TEST_SCENARIO_MODEL_NAMESPACE = "http://isimo.com/scenario/1.0";
	public final static String PLUGIN_ID = TestFrameworkUIPlugin.getDefault().getId();
	public final static String SCENARIO_EDITOR_ID = "com.smartwebproject.testframework.ui.editor.TestScenarioEditor";
}
