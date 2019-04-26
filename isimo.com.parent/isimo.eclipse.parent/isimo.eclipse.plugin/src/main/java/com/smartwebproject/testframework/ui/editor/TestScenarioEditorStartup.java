package com.smartwebproject.testframework.ui.editor;

import org.eclipse.ui.IStartup;

public class TestScenarioEditorStartup implements IStartup {

	@Override
	public void earlyStartup() {
		/* int j = 0;
		Validator[] newValidators = new Validator[ValidationFramework.getDefault().getValidators().length];
		for(Validator v: ValidationFramework.getDefault().getValidators()) {
			Validator current = v;
			if("org.eclipse.wst.xml.core.xml".equals(v.getId())) {
				int i = 0;
				V2 v2 =  v.asV2Validator();
				FilterGroup[] fgs = v2.getGroups();
				FilterGroup newFilterGroup = null;
				ArrayList<FilterGroup> al = new ArrayList<FilterGroup>();
				for(FilterGroup fg: fgs) {
					if(fg.isExclude()) {
						FilterRule fr = FilterRule.createContentType(TestScenarioPluginConstants.TEST_SCENARIO_CONTENT_TYPE, true);
						fgs[i] = FilterGroup.addRule(fg, fr);
					}
					al.add(fgs[i]);
					i++;
				}
				v2.setGroups(al);
				current = v2;
			}
			newValidators[j++] = current;
			
		}
		try {
			ValidationFramework.getDefault().saveValidators(newValidators);
		} catch(InvocationTargetException e) {
			throw new RuntimeException(e);
		}*/
	}
}
