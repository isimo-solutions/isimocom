
package com.smartwebproject.testframework.ui.editor.internal;

import java.util.Stack;

import org.apache.xerces.xni.XMLLocator;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xml.core.internal.validation.XMLValidationReport;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationInfo;
import org.eclipse.wst.xml.core.internal.validation.errorcustomization.ErrorCustomizationManager;

public class XMLValidationInfo extends ValidationInfo implements XMLValidationReport {
	protected boolean grammarEncountered = false;
	protected boolean dtdEncountered = false;
	protected boolean namespaceEncountered = false;
	protected int elementDeclCount = 0;

	protected Object[] messageArguments = null;
	protected XMLLocator locator = null;
	protected ErrorCustomizationManager errorCustomizationManager = null;

	protected Stack<LocationCoordinate> startElementLocations = new Stack<LocationCoordinate>();

	public XMLValidationInfo(String uri) {
		super(uri);
	}

	public boolean isGrammarEncountered() {
		return this.grammarEncountered;
	}

	public void setGrammarEncountered(boolean grammarEncountered) {
		this.grammarEncountered = grammarEncountered;
	}

	public boolean isDTDWithoutElementDeclarationEncountered() {
		return ((this.dtdEncountered) && (this.elementDeclCount == 0));
	}

	public void setDTDEncountered(boolean dtdEncountered) {
		this.dtdEncountered = dtdEncountered;
	}

	public boolean isNamespaceEncountered() {
		return this.namespaceEncountered;
	}

	public void setNamespaceEncountered(boolean namespaceEncountered) {
		this.namespaceEncountered = namespaceEncountered;
	}

	public void increaseElementDeclarationCount() {
		this.elementDeclCount += 1;
	}

	public void setElementDeclarationCount(int count) {
		this.elementDeclCount = count;
	}

	public XMLLocator getXMLLocator() {
		return this.locator;
	}

	public void setXMLLocator(XMLLocator locator) {
		this.locator = locator;
	}

	public Object[] getMessageArguments() {
		return this.messageArguments;
	}

	public void setMessageArguments(Object[] messageArguments) {
		this.messageArguments = messageArguments;
	}

	public Stack getStartElementLocations() {
		return this.startElementLocations;
	}

	protected ErrorCustomizationManager getErrorCustomizationManager() {
		if (this.errorCustomizationManager == null) {
			this.errorCustomizationManager = new ErrorCustomizationManager(getFileURI());
		}
		return this.errorCustomizationManager;
	}

	public boolean isUseXInclude() {
		return XMLCorePlugin.getDefault().getPluginPreferences().getBoolean("xinclude");
	}
}