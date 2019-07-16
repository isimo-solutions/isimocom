package com.smartwebproject.testframework.ui.views.dependencies;


import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeNodeContentProvider;

import com.isimo.dependencies.Dependency;

public class TreeContentProvider extends TreeNodeContentProvider {
	
	@Override
	public Object[] getChildren(Object parentElement) {
		return super.getChildren(parentElement);
	}
	
	@Override
	public Object[] getElements(final Object inputElement) {
		if (inputElement instanceof TreeNode) {
			return ((TreeNode)inputElement).getChildren();
		}
		return new Object[0];
	}

}
