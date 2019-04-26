package com.smartwebproject.testframework.ui.editor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.xml.ui.internal.tabletree.XMLMultiPageEditorPart;

import com.smartwebproject.testframework.ui.TestFrameworkUIPlugin;

public class TestScenarioEditorPart extends XMLMultiPageEditorPart implements KeyListener, MouseListener {
	int lastOffset = 0;
	
	@Override
	public void init(IEditorSite pSite, IEditorInput pInput) throws PartInitException {
		super.init(pSite, pInput);
		setEditor(new TestScenarioEditor());
		getEditor().setEditorPart(this);
		getEditor().setAction(ITextEditorActionConstants.RULER_DOUBLE_CLICK, null);
	}
		
	
	
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		TestFrameworkUIPlugin.getDefault().setLastVisited(this);
		((StyledText)this.getAdapter(Control.class)).addKeyListener(this);
		((StyledText)this.getAdapter(Control.class)).addMouseListener(this);
	}
	
	public IDocument getDocumentPublic() {
		try {
			Method method = XMLMultiPageEditorPart.class.getDeclaredMethod("getDocument");
			method.setAccessible(true);
			return (IDocument) method.invoke(this);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public StructuredTextEditor getEditor() {
		try {
			Field field = XMLMultiPageEditorPart.class.getDeclaredField("fTextEditor");
			field.setAccessible(true);
			return (StructuredTextEditor) field.get(this);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void setEditor(StructuredTextEditor editor) {
		try {
			Field field = XMLMultiPageEditorPart.class.getDeclaredField("fTextEditor");
			field.setAccessible(true);
			field.set(this, editor);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	
	
	public void typeAtCursor(String text) {
		StyledText st = (StyledText) this.getAdapter(Control.class);
		IDocument doc = getDocumentPublic();
		StringBuffer contentAfter = new StringBuffer();
		contentAfter.append(doc.get().substring(0, lastOffset));
		String toType = text+"\n";
		contentAfter.append(toType);
		contentAfter.append(doc.get().substring(lastOffset));
		lastOffset += toType.length();
		doc.set(contentAfter.toString());
	}

	@Override
	public void keyPressed(KeyEvent pE) {
		updateCursorPosition();
	}

	@Override
	public void keyReleased(KeyEvent pE) {
		updateCursorPosition();
	}
	
	private void updateCursorPosition()  {
		ITextSelection sel = (ITextSelection) this.getSite().getWorkbenchWindow().getSelectionService().getSelection();
		if(sel==null)
			return;
		lastOffset = sel.getOffset();
		System.out.println("lastOffset="+lastOffset);
	}

	@Override
	public void mouseDoubleClick(MouseEvent pE) {
		updateCursorPosition();
	}

	@Override
	public void mouseDown(MouseEvent pE) {
		updateCursorPosition();
	}

	@Override
	public void mouseUp(MouseEvent pE) {
		updateCursorPosition();
	}
	
	
}
