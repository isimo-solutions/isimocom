package com.smartwebproject.testframework.ui.views.dependencies;

import java.util.Arrays;
import java.util.Set;

import org.eclipse.jface.viewers.TreeNode;

import com.isimo.dependencies.Dependency;
import com.isimo.dependencies.Scenario;

public class ScenariosFolder extends DependencyTreeNode {
	
	boolean including;
	Set<Dependency> dependencies;
	
	public ScenariosFolder(Scenario s, Set<Dependency> dependencies, TreeNode root, String label, boolean including, boolean getNext) {
		super(s);
		this.dependencies = dependencies;
		this.label = label;
		this.setParent(root);
		this.including = including;
		
		if(getNext) calculateChilderen();
		
	}
	
	public void calculateChilderen() {
		TreeNode[] childeren = new TreeNode[dependencies.size()];
		
		int i = 0;
		for(Dependency dep : dependencies) {
			Scenario s;
			if(including) {
				s = dep.getSource();
				childeren[i] = new ScenariosNode(s, s.getIncludingScenarios(), this, s.getRelativePath(), dep.getLineNumber(), including);
			}
			else {
				s = dep.getTarget();
				childeren[i] = new ScenariosNode(s, s.getIncludedScenarios(), this, s.getRelativePath(), dep.getLineNumber(), including);
			}
			
			i++;
		}
		Arrays.sort(childeren);
		
		this.setChildren(childeren);
	}
	
}
