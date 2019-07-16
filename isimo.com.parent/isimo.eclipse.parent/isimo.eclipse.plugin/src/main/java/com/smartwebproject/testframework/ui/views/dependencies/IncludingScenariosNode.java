package com.smartwebproject.testframework.ui.views.dependencies;

import java.util.Arrays;
import java.util.Set;

import org.eclipse.jface.viewers.TreeNode;

import com.isimo.dependencies.Dependency;
import com.isimo.dependencies.Scenario;

public class IncludingScenariosNode extends DependencyTreeNode {

	Integer linenumber;
	
	public IncludingScenariosNode(Set<Dependency> dependencies, TreeNode root, String label, Integer linenumber) {
		super(dependencies);
		this.label = label;
		if(linenumber != null) this.linenumber = linenumber;
		this.setParent(root);
		TreeNode[] childeren = new TreeNode[dependencies.size()];
		int i = 0;
		for(Dependency dep : dependencies) {
			Scenario s = dep.getSource();
			childeren[i] = new IncludingScenariosNode(s.getIncludingScenarios(), this, s.getRelativePath(), dep.getLineNumber());
			i++;
		}
		Arrays.sort(childeren);
		
		this.setChildren(childeren);
		
	}
}
