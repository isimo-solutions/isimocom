package com.smartwebproject.testframework.ui.editor;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.XMLContentDescriber;
import org.xml.sax.InputSource;

public class TestScenarioContentDescriber extends XMLContentDescriber {
	@Override
	public int describe(InputStream contents, IContentDescription description) throws IOException {
		if (super.describe(contents, description) == INVALID) {
			return INVALID;
		}
		contents.reset();
		boolean isscenario = (new TestScenarioHandler()).parseContents(new InputSource(contents));
		if(isscenario)
			return VALID;
		else
			return INDETERMINATE;
	}
	
	@Override
	public int describe(Reader contents, IContentDescription description) throws IOException {
		if (super.describe(contents, description) == INVALID) {
			return INVALID;
		}
		contents.reset();
		boolean isscenario = (new TestScenarioHandler()).parseContents(new InputSource(contents));
		if(isscenario)
			return VALID;
		else
			return INDETERMINATE;
	}
	
}
