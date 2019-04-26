package com.smartwebproject.testframework.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;
import org.eclipse.wst.xml.core.internal.text.XMLStructuredDocumentRegion;
import org.eclipse.wst.xml.ui.internal.contentassist.DefaultXMLCompletionProposalComputer;
import org.eclipse.wst.xml.ui.internal.taginfo.XMLTagInfoHoverProcessor;

import com.isimo.core.model.ModelValidator;
import com.smartwebproject.testframework.ui.TestFrameworkUIPlugin;

public class TestScenarioEditorCompletionProcessor extends DefaultXMLCompletionProposalComputer {
	
	@Override
	public List<ICompletionProposal> computeCompletionProposals(CompletionProposalInvocationContext pContext, IProgressMonitor pMonitor) {
		// TODO Auto-generated method stub
		List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
		if(!pContext.getViewer().getDocument().get().contains("\""+TestScenarioPluginConstants.TEST_SCENARIO_MODEL_NAMESPACE+"\""))
			return 	proposals;	
		int pOffset = pContext.getInvocationOffset();
		StructuredTextViewer viewer = (StructuredTextViewer) pContext.getViewer();
		XMLTagInfoHoverProcessor hp = new XMLTagInfoHoverProcessor();
		IRegion region = hp.getHoverRegion(viewer, pOffset);
		String hoverinfo = hp.getHoverInfo(viewer, hp.getHoverRegion(viewer, pOffset));
		XMLStructuredDocumentRegion flatNode = (XMLStructuredDocumentRegion) ((IStructuredDocument) viewer.getDocument()).getRegionAtCharacterOffset(pOffset);
		String attributeName = null;
		String precedingAttribute = "";
		ITextRegion attributeRegion = null;
		
		if (flatNode != null) {
			ITextRegionList textRegionList = flatNode.getRegions();
			ITextRegion textregion = flatNode.getRegionAtCharacterOffset(pOffset);
			for(int i = textRegionList.size()-1; i >= 0; i--) {
			   if(regionsEqual(textRegionList.get(i),textregion) && "XML_TAG_ATTRIBUTE_VALUE".equals(textregion.getType())) {
				   attributeName = flatNode.getFullText(textRegionList.get(i-2));
				   attributeRegion = textRegionList.get(i);
				   break;
			   }
			}
		}
		if(TestScenarioPluginConstants.MODEL_ATTRIBUTE_NAME.equals(attributeName)) {
			int attrStart = flatNode.getStartOffset(attributeRegion);
			int attrEnd = attrStart+attributeRegion.getTextLength();
			String attributeValue = flatNode.getText(attributeRegion);
			String attributeValuePrefix = attributeValue.substring(1, pOffset-flatNode.getStartOffset(attributeRegion));
			System.out.println("prefix="+attributeValuePrefix);
			IDocument doc = pContext.getDocument();
			IWorkspace ws = ResourcesPlugin.getWorkspace();
			IResource resource = getFileForDocument(doc);
			List<String> proposalsStr = ModelValidator.getCompletionsForPrefix(TestFrameworkUIPlugin.getDefault().getModelForURI(resource.getLocationURI().toString()), attributeValuePrefix);
			for(String pstr: proposalsStr) {
				String first = attributeValue.substring(0,1);
				int replacementOffset =  attrStart;
				int replacementLength =  attrEnd-attrStart-1;
				
				int cursorPosition = pOffset+pstr.length();
				CompletionProposal cp = new CompletionProposal(first+pstr, replacementOffset, replacementLength, cursorPosition, null, pstr, null, null);
				System.out.println("("+(first+pstr)+","+replacementOffset+","+replacementLength+","+cursorPosition+")"+pOffset);
				proposals.add(cp);
			}
		}
		return proposals;		
	}
	
	boolean regionsEqual(ITextRegion region1, ITextRegion region2) {
		return stringsEqual(region1.getType(), region2.getType()) && region1.getStart() == region2.getStart() && region1.getEnd() == region2.getEnd();
	}
	
	boolean stringsEqual(String str1, String str2) {
		return (str1==null && str2==null) || ((str1!=null) && str1.equals(str2));
	}
	
	@Override
	public List computeContextInformation(CompletionProposalInvocationContext pContext, IProgressMonitor pMonitor) {
		// TODO Auto-generated method stub
		List ret = super.computeContextInformation(pContext, pMonitor);
		
		return ret;
	}
	
	public IResource getFileForDocument(IDocument doc) {
		ITextFileBufferManager bufferManager = FileBuffers.getTextFileBufferManager();
		ITextFileBuffer buffer = bufferManager.getTextFileBuffer(doc);
		IPath path = buffer.getLocation();
		IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
		return resource;
	}
}
