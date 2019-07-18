package com.smartwebproject.testframework.ui.views.dependencies;

import java.util.Arrays;
import java.util.Set;

import org.eclipse.jface.viewers.TreeNode;

import com.isimo.dependencies.Dependency;
import com.isimo.dependencies.Scenario;

public class ScenariosNode extends ScenariosFolder {

	int linenumber;
	
	public ScenariosNode(Scenario s , Set<Dependency> dependencies, TreeNode root, String label, int linenumber, boolean including) {
		super(s, dependencies, root, label, including, false);
		this.linenumber = linenumber;
	}
	
	public Set<Dependency> getDependencyies(){
		return this.dependencies;
	}
	
}
