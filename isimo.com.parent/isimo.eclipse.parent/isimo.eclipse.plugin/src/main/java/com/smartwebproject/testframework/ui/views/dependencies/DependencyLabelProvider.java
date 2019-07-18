package com.smartwebproject.testframework.ui.views.dependencies;


import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.swt.graphics.Image;

public class DependencyLabelProvider extends LabelProvider{
	
	 private Image scenarioIcon;
	 private Image dirIcon;

	 public DependencyLabelProvider() {
		 super();
		 /*try {
			 scenarioIcon = new Image(null, new FileInputStream("icons/isimo16x16_24.bmp"));
		      dirIcon = new Image(null, new FileInputStream("icons/sample.gif"));
		    } catch (FileNotFoundException e) {
		    	throw new RuntimeException(e);
		    }*/
	 }
	
	
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
		
		/*
		if(element instanceof ScenariosNode ||
				element instanceof ScenarioRootNode)
			return scenarioIcon;
		
		else if(element instanceof RootScenariosFolderNode || 
				element instanceof ScenariosFolder) {
			return dirIcon;
		}*/
		
		
		return super.getImage(element);
	}
	

	
}
