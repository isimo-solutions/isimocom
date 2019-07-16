package com.smartwebproject.testframework.ui.views.dependencies;

import org.eclipse.jface.viewers.LabelProvider;

public class DependencyLabelProvider extends LabelProvider{
	
	@Override
	public String getText(Object element) {
		if(element instanceof DependencyTreeNode) {
			DependencyTreeNode node = (DependencyTreeNode) element;
			String label = node.label;
			if(node instanceof IncludingScenariosNode) {
				IncludingScenariosNode including = (IncludingScenariosNode)node;
				if(including.linenumber != null)
				label = including.linenumber +": "+ label; 						
			}
			
			return label;
		}
		return super.getText(element);
	}
	
}
