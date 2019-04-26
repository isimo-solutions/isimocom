package com.smartwebproject.testframework.ui.editor.internal;

public class LocationCoordinate {
	private int lineNo = -1;
	private int columnNumber = -1;
	private String element;
	private String attribute;

	public LocationCoordinate(int lineNo, int columnNumber, String element) {
		this.lineNo = lineNo;
		this.columnNumber = columnNumber;
		this.element = element;
	}

	public int getLineNumber() {
		return this.lineNo;
	}

	public int getColumnNumber() {
		return this.columnNumber;
	}

	public String getElement() {
		return element;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String pAttribute) {
		attribute = pAttribute;
	}
	
}