package com.smartwebproject.testframework.ui.views.dependencies;

import java.util.Set;

import org.eclipse.jface.viewers.TreeNode;

import com.isimo.dependencies.Scenario;

public class ScenarioRootNode extends DependencyTreeNode {

	public ScenarioRootNode(Scenario rootScenario, TreeNode root) {
		super(rootScenario);
		this.setParent(root);
		label = rootScenario.getRelativePath();
		this.setChildren(null);
	}
	
}
