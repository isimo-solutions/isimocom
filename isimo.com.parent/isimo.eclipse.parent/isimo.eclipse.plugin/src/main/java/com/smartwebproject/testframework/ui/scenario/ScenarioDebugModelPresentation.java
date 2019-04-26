package com.smartwebproject.testframework.ui.scenario;

import org.eclipse.core.resources.IFile;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.internal.ui.DelegatingModelPresentation;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.IValueDetailListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.xml.ui.internal.util.SharedXMLEditorPluginImageHelper;
import org.eclipse.wst.xsl.launching.model.NodeListVariable;
import org.w3c.dom.Node;

import com.smartwebproject.testframework.ui.TestFrameworkUIPlugin;
import com.smartwebproject.testframework.ui.editor.TestScenarioPluginConstants;
import com.smartwebproject.testframework.ui.scenario.debug.ScenarioDebugElement;

public class ScenarioDebugModelPresentation extends LabelProvider implements IDebugModelPresentation {
	private Image localImg;
	public void setAttribute(String attribute, Object value) {
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof NodeListVariable) {
			NodeListVariable nodeVar = (NodeListVariable) element;
			if (nodeVar.getNode() != null) {
				Node node = nodeVar.getNode();
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					localImg = SharedXMLEditorPluginImageHelper
							.getImage(SharedXMLEditorPluginImageHelper.IMG_OBJ_ELEMENT);
				}
				if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
					localImg = SharedXMLEditorPluginImageHelper
							.getImage(SharedXMLEditorPluginImageHelper.IMG_OBJ_ATTRIBUTE);
				}
				if (node.getNodeType() == Node.COMMENT_NODE) {
					localImg = SharedXMLEditorPluginImageHelper
							.getImage(SharedXMLEditorPluginImageHelper.IMG_OBJ_COMMENT);
				}
				if (node.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE) {
					localImg = SharedXMLEditorPluginImageHelper
							.getImage(SharedXMLEditorPluginImageHelper.IMG_OBJ_PROCESSINGINSTRUCTION);
				}
				if (node.getNodeType() == Node.CDATA_SECTION_NODE) {
					localImg = SharedXMLEditorPluginImageHelper
							.getImage(SharedXMLEditorPluginImageHelper.IMG_OBJ_CDATASECTION);
				}
				if (node.getNodeType() == Node.ENTITY_NODE) {
					localImg = SharedXMLEditorPluginImageHelper
							.getImage(SharedXMLEditorPluginImageHelper.IMG_OBJ_ENTITY);
				}
				if (node.getNodeType() == Node.ENTITY_REFERENCE_NODE) {
					localImg = SharedXMLEditorPluginImageHelper
							.getImage(SharedXMLEditorPluginImageHelper.IMG_OBJ_ENTITY_REFERENCE);
				}
				if (node.getNodeType() == Node.TEXT_NODE) {
					localImg = SharedXMLEditorPluginImageHelper
							.getImage(SharedXMLEditorPluginImageHelper.IMG_OBJ_TXTEXT);
				}
				return localImg;
			}
		}
		return null;
	}
	
	@Override
	public String getText(Object element) {
		return null; 
	}

	@Override
	public void dispose() {
		if (localImg != null)
			localImg.dispose();
		super.dispose();
	}

	public void computeDetail(IValue value, IValueDetailListener listener) {
		String detail = ""; //$NON-NLS-1$
		try {
			detail = value.getValueString();
		} catch (DebugException e) {
		}
		listener.detailComputed(value, detail);
	}

	public IEditorInput getEditorInput(Object element) {
		if (element instanceof IFile) {
			return new FileEditorInput((IFile) element);
		}
		if (element instanceof ILineBreakpoint) {
			return new FileEditorInput((IFile) ((ILineBreakpoint) element)
					.getMarker().getResource());
		}
		return null;
	}

	public String getEditorId(IEditorInput input, Object element) {
		if (element instanceof IFile || element instanceof ILineBreakpoint) {
			return TestScenarioPluginConstants.SCENARIO_EDITOR_ID;
		}
		return null;
	}
}
