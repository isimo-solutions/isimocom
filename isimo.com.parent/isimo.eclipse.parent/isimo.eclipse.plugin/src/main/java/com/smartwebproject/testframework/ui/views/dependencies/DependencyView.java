package com.smartwebproject.testframework.ui.views.dependencies;

import java.io.File;

import org.dom4j.DocumentException;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.isimo.dependencies.DependencyHolder;

public class DependencyView extends ViewPart {
	
	TreeViewer viewer;
	StyledText title;
	
	
	String pathAbsolute="";
	String name="";
	String pathProjectRelative;
	
	public void setProperties(String name, String path, String pathRelative) {
		this.name = name;
		this.pathAbsolute = path;
		this.pathProjectRelative = pathRelative;
		setContents();
	}
	
	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		FillLayout fillLayout = new FillLayout();
		parent.setLayout(fillLayout);
		Composite outer = new Composite(parent, SWT.NONE);
		
		FormLayout formLayout = new FormLayout();
		formLayout.spacing = 5;
		outer.setLayout(formLayout);
				
		Composite innerTitle = new Composite(outer, SWT.NONE);
		innerTitle.setLayout(fillLayout);
	
		FormData fData = new FormData();
		fData.left = new FormAttachment( 0 );
		fData.right = new FormAttachment( 100 );
		innerTitle.setLayoutData( fData );
		
		Composite innerTree = new Composite( outer, SWT.NONE );
		innerTree.setLayout(fillLayout);

		fData = new FormData();
		fData.top = new FormAttachment( innerTitle );
		fData.left = new FormAttachment( 0 );
		fData.right = new FormAttachment( 100 );
		fData.bottom = new FormAttachment( 100 );
		innerTree.setLayoutData( fData );
		
		
		title = new StyledText(innerTitle, SWT.NONE);
		title.setEditable(false);
		title.setEnabled(false);
		
		viewer = new TreeViewer(innerTree);
		viewer.setContentProvider(new TreeContentProvider());
		viewer.setLabelProvider(new DependencyLabelProvider());
		
		viewer.addDoubleClickListener(new IDoubleClickListener() {
		    @Override
		    public void doubleClick(DoubleClickEvent event) {
		        IStructuredSelection thisSelection = (IStructuredSelection) event.getSelection();
		        Object selectedNode = thisSelection.getFirstElement();
		        if(selectedNode instanceof ScenariosNode || selectedNode instanceof ScenarioRootNode) {
		        	DependencyTreeNode node = (DependencyTreeNode) selectedNode;
		        	node.openFile(pathProjectRelative);
		        }
		        
		     }
		});
		
		
		setContents();
	}
	
	public void setContents() {
		if(pathAbsolute.equals("") || name.equals(""))
		 {
			 title.setText("There is nothing to show");
			 return;
		 }
		 title.setText("Showing depedencies for: "+ name);
		 DependencyHolder holder = new DependencyHolder(pathAbsolute);
		 holder.setTestDirectory(new File(pathAbsolute));
   	 try {
			holder.analyzeDependencies();
			DependencyTreeRoot root = new DependencyTreeRoot(holder, name);
			
			viewer.setInput(root);
			
   	 
   	 } catch (DocumentException e) {
   		title.setText("There is nothing to show");
   			//e.printStackTrace();
		}
	 }

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}
	
	

}
