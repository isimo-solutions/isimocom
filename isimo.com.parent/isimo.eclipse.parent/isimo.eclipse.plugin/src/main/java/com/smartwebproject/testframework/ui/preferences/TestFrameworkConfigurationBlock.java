package com.smartwebproject.testframework.ui.preferences;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.annotation.PostConstruct;

import org.eclipse.core.commands.operations.ICompositeOperation;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.ui.preferences.OptionsConfigurationBlock;
import org.eclipse.jdt.internal.ui.wizards.IStatusChangeListener;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.eclipse.ui.preferences.IWorkingCopyManager;
import org.eclipse.wst.xml.ui.internal.tabletree.XMLMultiPageEditorPart;

import com.smartwebproject.testframework.ui.TestFrameworkUIPlugin;
import com.smartwebproject.testframework.ui.editor.TestScenarioPluginConstants;

public class TestFrameworkConfigurationBlock extends OptionsConfigurationBlock  {
	public static QualifiedName SCENARIO_ROOT_NAME = new QualifiedName(TestScenarioPluginConstants.PLUGIN_ID, TestScenarioPluginConstants.SCENARIO_ROOT);
	static Key MODEL_PATH_KEY = getKey(TestScenarioPluginConstants.PLUGIN_ID, TestScenarioPluginConstants.TF_MODEL);
	static Key SCENARIO_ROOT_KEY = getKey(SCENARIO_ROOT_NAME.getQualifier(), SCENARIO_ROOT_NAME.getLocalName());
	static Key BROWSER_DIR_KEY = getKey(TestScenarioPluginConstants.PLUGIN_ID, TestScenarioPluginConstants.BROWSERS_DIR);
	static Key ENV_DIR_KEY = getKey(TestScenarioPluginConstants.PLUGIN_ID, TestScenarioPluginConstants.ENV_DIR);
	static Key SUSPEND_ON_FAILURE = getKey(TestScenarioPluginConstants.PLUGIN_ID, TestFrameworkUIPlugin.getDefault().getSuspendOnFailurePropertyName());
	static Key SUSPEND_ON_ERROR = getKey(TestScenarioPluginConstants.PLUGIN_ID, TestFrameworkUIPlugin.getDefault().getSuspendOnErrorPropertyName());
	
	FileFieldEditor editor = null;
	Composite inputFileComposite = null, dirComposite = null, parent = null;
	PixelConverter fPixelConverter = null;
	Text text;
	Layout layout;
	
	public TestFrameworkConfigurationBlock(IStatusChangeListener context, IProject project, IWorkbenchPreferenceContainer container) {
		super(context, project, getKeys(), container);
	}
	
	private static Key[] getKeys() {
		Key[] keys= new Key[] {
				MODEL_PATH_KEY, SCENARIO_ROOT_KEY, BROWSER_DIR_KEY, ENV_DIR_KEY, SUSPEND_ON_FAILURE, SUSPEND_ON_ERROR, 
			};
		return keys;
	}

	@Override
	@PostConstruct
	protected Control createContents(Composite parent) {
		try {
			this.parent = parent;
			fPixelConverter= new PixelConverter(parent);
			setShell(parent.getShell());
			
			Composite composite= new Composite(parent, SWT.NONE);
			composite.setFont(parent.getFont());
			composite.setLayout(new GridLayout(1, false));
			composite.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));
			
			
			inputFileComposite = new Composite(composite, SWT.NONE);
			
			inputFileComposite.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));
			editor = new FileFieldEditor(MODEL_PATH_KEY.toString(), "Test Framework Modell: ", inputFileComposite);
			text = editor.getTextControl(inputFileComposite);
			text.setData(MODEL_PATH_KEY);
			text.addModifyListener(getTextModifyListener());
			Composite c= new Composite(parent, SWT.NONE);
			c.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));

			layout= new GridLayout(4, true);
			c.setLayout(layout);
			
			if(fProject!=null) {
				addDirectoryChooser(c, "Project directory containing scenarios:", SCENARIO_ROOT_KEY);
				addDirectoryChooser(c, "Project directory containing environment property files: ", ENV_DIR_KEY);
				addDirectoryChooser(c, "Project directory containing browser property files: ", BROWSER_DIR_KEY);
			}
			fTextBoxes.add(text);
			addCheckBox(c, "Suspend on errors", SUSPEND_ON_ERROR, new String[] {"true", "false"}, 0);
			addCheckBox(c, "Suspend on failures", SUSPEND_ON_FAILURE, new String[] {"true", "false"}, 0);
			return parent;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	Text addDirectoryChooser(Composite composite, String title, Key key) {
		final ContainerSelectionDialog dirDialog = new ContainerSelectionDialog(composite.getShell(), fProject, true, title);
		final Text dirText = addTextField(composite, title, key, 0, 0);
		dirText.computeSize(fPixelConverter.convertWidthInCharsToPixels(10),fPixelConverter.convertHeightInCharsToPixels(1), true);
		Button dirTextButton = new Button(composite, SWT.BUTTON1);
		dirTextButton.setText("Browse...");
		dirTextButton.addMouseListener(new MouseListener() {
			@Override
			public void mouseUp(MouseEvent pArg0) {
				
			}
			@Override
			public void mouseDown(MouseEvent pArg0) {
				if (dirDialog.open() == ContainerSelectionDialog.OK) {
					Object[] result = dirDialog.getResult();
					if (result.length == 1) {
						IPath path = (IPath) result[0];
						if(fProject != null)
							path = path.makeRelativeTo(fProject.getFullPath());
						dirText.setText(path.toString());
					}
				}
			}
			@Override
			public void mouseDoubleClick(MouseEvent pArg0) {	
			}
		});
		return dirText;
	}

	@Override
	protected void validateSettings(Key pChangedKey, String pOldValue, String pNewValue) {
		System.out.println("validateSettings");
	}
	


	@Override
	protected String[] getFullBuildDialogStrings(boolean pWorkspaceSettings) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected void updateControls() {
		// TODO Auto-generated method stub
		super.updateControls();
	}
	
}
