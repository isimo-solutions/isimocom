package com.smartwebproject.testframework.ui.perspective;


import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class TestPerspective implements IPerspectiveFactory {
	
	@Override
	public void createInitialLayout(IPageLayout layout) {
        String editorArea = layout.getEditorArea();

        IFolderLayout leftContener = layout.createFolder("left", IPageLayout.LEFT, (float) 0.26, editorArea);
        leftContener.addView(IPageLayout.ID_PROJECT_EXPLORER);
        leftContener.addView(IPageLayout.ID_OUTLINE);
        leftContener.addPlaceholder("org.eclipse.jdt.junit.ResultView");
        
        IFolderLayout bottomContener =layout.createFolder("bottom", IPageLayout.BOTTOM,(float) 0.75, editorArea);
        bottomContener.addView(IPageLayout.ID_PROBLEM_VIEW);
        bottomContener.addView("org.eclipse.ui.console.ConsoleView");
        bottomContener.addView(IPageLayout.ID_PROGRESS_VIEW);    
        
	}

}
