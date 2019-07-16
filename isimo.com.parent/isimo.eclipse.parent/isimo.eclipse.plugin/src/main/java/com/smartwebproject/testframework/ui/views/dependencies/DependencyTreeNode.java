package com.smartwebproject.testframework.ui.views.dependencies;


import org.eclipse.jface.viewers.TreeNode;

public class DependencyTreeNode extends TreeNode implements Comparable<DependencyTreeNode>{

	String label;
	
	public DependencyTreeNode(Object value) {
		super(value);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int compareTo(DependencyTreeNode n) {
	  return label.compareTo(n.label);
	}
	
	

}
