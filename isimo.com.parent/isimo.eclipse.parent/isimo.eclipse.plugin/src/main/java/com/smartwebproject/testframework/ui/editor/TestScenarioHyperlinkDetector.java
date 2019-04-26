package com.smartwebproject.testframework.ui.editor;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.text.rules.IStructuredTypedRegion;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xsl.core.XSLCore;
import org.eclipse.wst.xsl.ui.internal.editor.SourceFileHyperlink;
import org.w3c.dom.Node;

import com.smartwebproject.testframework.ui.TestFrameworkPreferenceStore;
import com.smartwebproject.testframework.ui.TestFrameworkUIPlugin;

public class TestScenarioHyperlinkDetector extends AbstractHyperlinkDetector {

	public TestScenarioHyperlinkDetector() {
		super();
	}

	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer viewer, IRegion region, boolean canShowMultipleHyperlinks) {
		System.out.println("DetectHyperLinks!!!!");
		if(viewer==null || viewer.getDocument()==null|| region==null)
			return emptyHyperlink();
		IHyperlink hl = detectHyperlinks(viewer.getDocument(), region);
		if(hl!=null)
			return new IHyperlink[] {hl};
		else
			return emptyHyperlink();
	}
	
	static IHyperlink[] emptyHyperlink() {
		return null;
	}
	
	IHyperlink detectHyperlinks(IDocument document, IRegion region) {
		ITextFileBufferManager bufferManager = FileBuffers.getTextFileBufferManager();
		ITextFileBuffer buffer = bufferManager.getTextFileBuffer(document);
		IPath path = buffer.getLocation().makeAbsolute();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

		IFile file = root.getFile(path);
		Node node = null;
		try  {
			node = XSLCore.getCurrentNode(document, region.getOffset());
		} catch(NullPointerException e) {
			return null;
		}
		if(node.getNodeType()==Node.ELEMENT_NODE && "include".equals(node.getLocalName()) && TestScenarioPluginConstants.TEST_SCENARIO_MODEL_NAMESPACE.equals(node.getNamespaceURI())) {
			Node scenario = node.getAttributes().getNamedItem("scenario");
			String link = scenario.getNodeValue();
			IRegion hyperlinkRegion = getHyperlinkRegion(node.getAttributes().getNamedItem("scenario"));
			IFile targetfile = TestFrameworkUIPlugin.getIncludedScenarioFile(file, link);
			return new SourceFileHyperlink(hyperlinkRegion, targetfile);
		}
		return null;
	}
	
	private IRegion getHyperlinkRegion(Node node)
	{
		IRegion hyperRegion = null;

		if (node != null)
		{
			short nodeType = node.getNodeType();
			if (nodeType == Node.DOCUMENT_TYPE_NODE)
			{
				// handle doc type node
				IDOMNode docNode = (IDOMNode) node;
				hyperRegion = new Region(docNode.getStartOffset(), docNode.getEndOffset() - docNode.getStartOffset());
			}
			else if (nodeType == Node.ATTRIBUTE_NODE)
			{
				// handle attribute nodes
				IDOMAttr att = (IDOMAttr) node;
				// do not include quotes in attribute value region
				int regOffset = att.getValueRegionStartOffset();
				ITextRegion valueRegion = att.getValueRegion();
				if (valueRegion != null)
				{
					int regLength = valueRegion.getTextLength();
					String attValue = att.getValueRegionText();
					if (StringUtils.isQuoted(attValue))
					{
						++regOffset;
						regLength = regLength - 2;
					}
					hyperRegion = new Region(regOffset, regLength);
				}
			}
		}
		return hyperRegion;
	}


}
