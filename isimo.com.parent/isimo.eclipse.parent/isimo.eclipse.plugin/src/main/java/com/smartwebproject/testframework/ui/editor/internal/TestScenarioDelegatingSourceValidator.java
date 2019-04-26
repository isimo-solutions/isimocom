package com.smartwebproject.testframework.ui.editor.internal;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.validation.ValidationFramework;
import org.eclipse.wst.validation.Validator;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.eclipse.wst.xml.ui.internal.validation.DelegatingSourceValidator;

public class TestScenarioDelegatingSourceValidator extends DelegatingSourceValidator {
	private final static String Id = "com.smartwebproject.testframework.ui.testScenario";
	private Validator _validator;
	
	private Validator getValidator() {
		System.out.println("getValidator");
		if (_validator == null)
			_validator = ValidationFramework.getDefault().getValidator(Id);
		return _validator;
	}
	
	@Override
	protected IValidator getDelegateValidator() {
		Validator v = getValidator();
	    if (v == null)
	      return null;
	    return v.asIValidator();
	}
	
	@Override
	protected boolean isDelegateValidatorEnabled(IFile pFile) {
		// TODO Auto-generated method stub
		return super.isDelegateValidatorEnabled(pFile);
	}
}
