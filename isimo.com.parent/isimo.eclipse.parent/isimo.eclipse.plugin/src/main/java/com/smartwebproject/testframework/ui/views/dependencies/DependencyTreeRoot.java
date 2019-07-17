package com.smartwebproject.testframework.ui.views.dependencies;


import org.dom4j.DocumentException;
import org.eclipse.jface.viewers.TreeNode;

import com.isimo.dependencies.DependencyHolder;


public class DependencyTreeRoot extends DependencyTreeNode {
	
	public DependencyTreeRoot(DependencyHolder holder, String name) throws DocumentException {
		super(null);
		
		RootScenariosFolderNode scenarioRoots = new RootScenariosFolderNode(holder.getScenario(name).getIncludingRootScenarios(), this, "Root Scenarios"); 
		ScenariosFolder includingFolder = new ScenariosFolder(holder.getScenario(name).getIncludingScenarios(), this, "Scenarios Including", true, true);
		ScenariosFolder includedFolder = new ScenariosFolder(holder.getScenario(name).getIncludedScenarios(), this, "Scenarios included", false, true);
		
		TreeNode[] containers = {scenarioRoots, includingFolder, includedFolder};
		
		this.setParent(null);
		this.setChildren(containers);
	}

}
