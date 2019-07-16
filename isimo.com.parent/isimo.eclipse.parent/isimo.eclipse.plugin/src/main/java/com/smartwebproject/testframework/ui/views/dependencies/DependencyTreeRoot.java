package com.smartwebproject.testframework.ui.views.dependencies;


import org.dom4j.DocumentException;
import org.eclipse.jface.viewers.TreeNode;

import com.isimo.dependencies.DependencyHolder;


public class DependencyTreeRoot extends DependencyTreeNode {
	
	public DependencyTreeRoot(DependencyHolder holder, String name) throws DocumentException {
		super(null);
		
		RootScenariosFolderNode scenarioRoots = new RootScenariosFolderNode(holder.getScenario(name).getIncludingRootScenarios(), this, "Root Scenarios"); 
		IncludingScenariosNode includingFolder = new IncludingScenariosNode(holder.getScenario(name).getIncludingScenarios(), this, "Scenarios Including " + name, null);
		
		
		TreeNode[] containers = {scenarioRoots, includingFolder};
		
		this.setParent(null);
		this.setChildren(containers);
	}

}
