package com.smartwebproject.testframework.ui.views.dependencies;


import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.swt.graphics.Image;

public class DependencyLabelProvider extends LabelProvider{
	
	@Override
	public String getText(Object element) {
		if(element instanceof DependencyTreeNode) {
			DependencyTreeNode node = (DependencyTreeNode) element;
			String label = node.label;
			
			if(node instanceof ScenariosNode) {
				ScenariosNode including = (ScenariosNode)node;
				label = including.linenumber +": "+ label; 						
			}
			else if(node instanceof RootScenariosFolderNode || 
			   node instanceof ScenariosFolder )
			{
				TreeNode[] childeren = node.getChildren();
				if(childeren != null) label = label + " (" +childeren.length + " matches)";
				else label = label + " (not found)";
			}
			
			return label;
		}
		return super.getText(element);
	}
	
	@Override
	public Image getImage(Object element) {
		
		return super.getImage(element);
	}
	

	
}
