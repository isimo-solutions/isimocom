package com.smartwebproject.testframework.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.ui.internal.contentoutline.IJFaceNodeAdapter;
import org.eclipse.wst.sse.ui.internal.editor.EditorModelUtil;
import org.eclipse.wst.xml.core.internal.document.DOMModelImpl;
import org.eclipse.wst.xml.core.internal.document.XMLModelParser;
import org.eclipse.wst.xml.core.internal.modelhandler.XMLModelLoader;
import org.eclipse.wst.xml.core.internal.parser.XMLSourceParser;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.ui.internal.contentoutline.JFaceNodeAdapterFactory;
import org.eclipse.wst.xml.ui.internal.contentoutline.JFaceNodeContentProvider;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.smartwebproject.testframework.ui.TestFrameworkUIPlugin;

public class TestScenarioContentProvider extends JFaceNodeContentProvider {
	TreeViewer viewer;
	public TestScenarioContentProvider(TreeViewer viewer) {
		super();
		this.viewer = viewer; 
	}
	
	@Override
	public Object[] getChildren(Object object) {
		// TODO Auto-generated method stub
		if(!(object instanceof IDOMElement))
			return super.getChildren(object);
		else {
			IDOMElement elem = (IDOMElement) object;
			if(isInclude(elem)) {
				String baseLocation = ((DOMModelImpl)viewer.getInput()).getBaseLocation();
				IPath path = new Path(baseLocation);
				IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
				IFile file = root.getFile(path);
				

				IFile includedFile = TestFrameworkUIPlugin.getIncludedScenarioFile(file, elem.getAttribute("scenario"));
				IStructuredModel model = null;
				try {
					model = StructuredModelManager.getModelManager().getModelForEdit(includedFile);
					EditorModelUtil.addFactoriesTo(model);
				} catch(Exception e) {
					throw new RuntimeException(e);
				}
				if(model instanceof IDOMModel) {
					IDOMModel idommodel = (IDOMModel) model;
					return getChildren(idommodel.getDocument().getDocumentElement());
				} else {
					throw new RuntimeException("Should be IDOMModel");
				}
			 
			}  else if(isScenario(elem)) {
				NodeList nl = elem.getChildNodes();
				for (int i = 0; i < nl.getLength(); i++) {
					if(isActions(nl.item(i))) {
						return getChildren(nl.item(i));
/*						NodeList children = nl.item(i).getChildNodes();
						List<Object> retlist = new ArrayList<Object>();
						Object[] retval = new Object[children.getLength()];
						for (int j = 0; j < children.getLength(); j++) {
							if(children.item(j).getNodeType() == Node.ELEMENT_NODE)
								retlist.add(children.item(j));
						}
						return retlist.toArray(new Object[retlist.size()]);*/
					}
				}
				return new Object[0];
			} else {
				return super.getChildren(object);
			}
		}
	}
	
	@Override
	public boolean hasChildren(Object object) {
		if(!(object instanceof IDOMElement))
			return super.hasChildren(object);
		else {
			IDOMElement elem = (IDOMElement) object;
			if(isInclude(elem)) {
				return true;
			} else {
				return super.hasChildren(object);
			}
		}
	}
	
	boolean isInclude(Node elem) {
		return isNamedElem(elem, "include");
	}
	
	boolean isScenario(Node elem) {
		return isNamedElem(elem, "scenario");
	}
	
	boolean isActions(Node elem) {
		return isNamedElem(elem, "actions");
	}
	
	boolean isNamedElem(Node elem, String name) {
		if(elem==null || elem.getNodeType() != Node.ELEMENT_NODE)
			return false;
		if(elem.getNamespaceURI().equals(TestScenarioPluginConstants.TEST_SCENARIO_MODEL_NAMESPACE) && name.equals(elem.getLocalName())) {
			return true;
		} else {
			return false;
		}
	}
	
	protected IJFaceNodeAdapter getAdapter(Object adaptable) {
		INodeAdapter adapter = super.getAdapter(adaptable);
		if(adapter !=null)
			return (IJFaceNodeAdapter) adapter;
		JFaceNodeAdapterFactory factory = new JFaceNodeAdapterFactory();
		adapter = factory.adapt((INodeNotifier)adaptable);
		return (IJFaceNodeAdapter)adapter;
	}
	
	
	

}
