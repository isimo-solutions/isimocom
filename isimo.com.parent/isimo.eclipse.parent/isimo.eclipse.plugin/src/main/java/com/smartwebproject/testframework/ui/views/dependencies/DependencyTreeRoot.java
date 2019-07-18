package com.smartwebproject.testframework.ui.views.dependencies;


import org.dom4j.DocumentException;
import org.eclipse.jface.viewers.TreeNode;

import com.isimo.dependencies.DependencyHolder;
import com.isimo.dependencies.Scenario;


public class DependencyTreeRoot extends DependencyTreeNode {
	
	public DependencyTreeRoot(DependencyHolder holder, String name) throws DocumentException {
		super(null);
		Scenario s = holder.getScenario(name);
		RootScenariosFolderNode scenarioRoots = new RootScenariosFolderNode(s.getIncludingRootScenarios(), this, "Root Scenarios"); 
		ScenariosFolder includingFolder = new ScenariosFolder( s, s.getIncludingScenarios(), this, "Scenarios Including", true, true);
		ScenariosFolder includedFolder = new ScenariosFolder( s, s.getIncludedScenarios(), this, "Scenarios included", false, true);
		
		TreeNode[] containers = {scenarioRoots, includingFolder, includedFolder};
		
		this.setParent(null);
		this.setChildren(containers);
	}

}
