package com.smartwebproject.testframework.ui.views.dependencies;

import java.util.Arrays;
import java.util.Set;

import org.eclipse.jface.viewers.TreeNode;

import com.isimo.dependencies.Scenario;

public class RootScenariosFolderNode extends DependencyTreeNode {

	public RootScenariosFolderNode(Set<Scenario> rootScenarios, TreeNode root, String label) {
		super(rootScenarios);
		this.label = label;
		this.setParent(root);
		TreeNode[] childeren = new TreeNode[rootScenarios.size()];
		int i = 0;
		for(Scenario scenario : rootScenarios) {
			childeren[i] = new ScenarioRootNode(scenario, this);
			i++;
		}
		Arrays.sort(childeren);
		
		this.setChildren(childeren);
		
	}
	
}
