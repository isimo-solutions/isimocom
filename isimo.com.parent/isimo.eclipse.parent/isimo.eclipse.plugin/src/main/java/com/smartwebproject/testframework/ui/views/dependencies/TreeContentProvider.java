package com.smartwebproject.testframework.ui.views.dependencies;


import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeNodeContentProvider;

import com.isimo.dependencies.Dependency;

public class TreeContentProvider extends TreeNodeContentProvider {
	
	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof ScenariosNode) {
			ScenariosNode n = (ScenariosNode) parentElement;
			n.calculateChilderen();
			//return n.getChildren();
		}
		
		return super.getChildren(parentElement);
	}
	
	@Override
	public boolean hasChildren(Object element) {
		if(element instanceof ScenariosNode) {
			ScenariosNode n = (ScenariosNode) element;
			Set<Dependency> dependencies = (Set<Dependency>)n.getValue();
			return dependencies.size() > 0;
		}
		return super.hasChildren(element);
	}
	
	@Override
	public Object[] getElements(final Object inputElement) {
		if (inputElement instanceof TreeNode) {
			return ((TreeNode)inputElement).getChildren();
		}
		return new Object[0];
	}

	
	
}
