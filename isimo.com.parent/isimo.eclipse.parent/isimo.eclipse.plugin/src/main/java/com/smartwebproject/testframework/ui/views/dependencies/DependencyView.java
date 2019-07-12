package com.smartwebproject.testframework.ui.views.dependencies;

import java.io.File;
import java.util.Set;

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
	String path;
	String name;
	
	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		 viewer = new TreeViewer(parent);
		 viewer.setContentProvider(new TreeContentProvider());
		
		 DependencyHolder holder = new DependencyHolder(path);
		 holder.setTestDirectory(new File(path));
    	 try {
			holder.analyzeDependencies();
			Set<Scenario> rootScenarios = holder.getScenario(name+".xml").getIncludingRootScenarios();
			//Set<Dependency> including = holder.getScenario(name+".xml").getIncludingScenarios();
			viewer.setInput(rootScenarios);
    	 
    	 } catch (DocumentException e) {
    		 viewer.setInput("There is nothing to show");
			//e.printStackTrace();
		}
	 	 
        //viewer.getTree().setHeaderVisible(true);
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}
	
	

}
