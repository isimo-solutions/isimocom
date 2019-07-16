package com.smartwebproject.testframework.ui.views.dependencies;

import java.io.File;
import java.util.Set;

import org.dom4j.DocumentException;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.isimo.dependencies.Dependency;
import com.isimo.dependencies.DependencyHolder;
import com.isimo.dependencies.Scenario;

public class DependencyView extends ViewPart {
	
	TreeViewer viewer;
	String path="";
	String name="";
	
	public void setProperties(String name, String path) {
		this.name = name;
		this.path = path;
		setContents();
	}
	
	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		 viewer = new TreeViewer(parent);
		 viewer.setContentProvider(new TreeContentProvider());
		 viewer.setLabelProvider(new DependencyLabelProvider());
		 setContents();
	}
	
	public void setContents() {
		if(path.equals("") || name.equals(""))
		 {
			 //viewer.setInput(new String[] {"There is nothing to show"});
			 return;
		 }
		 DependencyHolder holder = new DependencyHolder(path);
		 holder.setTestDirectory(new File(path));
   	 try {
			holder.analyzeDependencies();
			DependencyTreeRoot root = new DependencyTreeRoot(holder, name);

			viewer.setInput(root);
			
   	 
   	 } catch (DocumentException e) {
   		 //viewer.setInput(new String[] {"There is nothing to show"});
   		 //e.printStackTrace();
		}
	 	 
       //viewer.getTree().setHeaderVisible(true);
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}
	
	

}
