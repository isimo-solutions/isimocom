package com.smartwebproject.testframework.ui.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.Position;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.ui.ISemanticHighlightingExtension2;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.eclipse.wst.xsl.ui.internal.style.AbstractXSLSemanticHighlighting;

import com.smartwebproject.testframework.ui.TestFrameworkUIPlugin;;

public class TestScenarioSemanticHighlighting implements org.eclipse.wst.sse.ui.ISemanticHighlighting, ISemanticHighlightingExtension2 {
	public static String ENABLED_PREFERENCE_KEY = "testscenario.highlighting.enabled";
	
	@Override
	public Position[] consumes(IStructuredDocumentRegion arg0) {
		return null;
	}

	@Override
	public String getBoldPreferenceKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getColorPreferenceKey() {
		return null;
	}

	@Override
	public String getDisplayName() {
		return null;
	}

	@Override
	public String getEnabledPreferenceKey() {
		return ENABLED_PREFERENCE_KEY;
	}

	@Override
	public String getItalicPreferenceKey() {
		return null;
	}

	@Override
	public IPreferenceStore getPreferenceStore() {
		return TestFrameworkUIPlugin.getDefault().getPreferenceStore();
	}

	@Override
	public String getStrikethroughPreferenceKey() {
		return null;
	}

	@Override
	public String getUnderlinePreferenceKey() {
		return null;
	}

	@Override
	public String getBackgroundColorPreferenceKey() {
		return null;
	}

}
