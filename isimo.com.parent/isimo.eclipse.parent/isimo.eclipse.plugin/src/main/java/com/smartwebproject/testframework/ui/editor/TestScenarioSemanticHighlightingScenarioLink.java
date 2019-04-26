package com.smartwebproject.testframework.ui.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.text.Position;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.ui.ISemanticHighlightingExtension;
import org.eclipse.wst.sse.ui.ISemanticHighlightingExtension2;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;

public class TestScenarioSemanticHighlightingScenarioLink extends TestScenarioSemanticHighlighting implements ISemanticHighlightingExtension {
	boolean scenarioAttr = false;
	
	public Position[] consumes(IStructuredDocumentRegion region, IndexedRegion arg1) {
		ArrayList array = new ArrayList();
		array.addAll(createSemanticPositions(region, DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE, "scenario"));
		Position[] allPos = new Position[array.size()];
		if (!array.isEmpty()) {
			array.toArray(allPos);
		}
		return allPos;
	}
	
	protected List createSemanticPositions(IStructuredDocumentRegion region, String regionType, String attributeName) {
		if (region == null) {
			return Collections.EMPTY_LIST;
		}
		
		if (!region.getType().equals(DOMRegionContext.XML_TAG_NAME)) {
			return Collections.EMPTY_LIST;
		}
	
		ITextRegionList regionList = region.getRegions();
		
		ArrayList arrpos = new ArrayList();
		for (int i = 0; i < regionList.size(); i++) {
			ITextRegion textRegion = regionList.get(i);
			if (textRegion.getType().equals(regionType) && scenarioAttr) {
				Position pos = new Position(region
						.getStartOffset(textRegion), textRegion.getLength());
				arrpos.add(pos);
			} else if(textRegion.getType().equals(DOMRegionContext.XML_TAG_ATTRIBUTE_NAME)) {
				String name = region.getText(textRegion);
				scenarioAttr = (attributeName.equals(name));
			}
		}
		return arrpos;
	}

}
