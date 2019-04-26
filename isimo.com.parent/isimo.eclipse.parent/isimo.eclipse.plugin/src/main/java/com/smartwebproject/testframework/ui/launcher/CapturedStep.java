package com.smartwebproject.testframework.ui.launcher;

public class CapturedStep {
	private String html, htmlId, action, description, tagName, value, label, name, text, url, src, forLabel, xpath, checked;

	public String getHtml() {
		return html;
	}

	public void setHtml(String pHtml) {
		html = pHtml;
	}

	public String getHtmlId() {
		return htmlId;
	}

	public void setHtmlId(String pHtmlId) {
		htmlId = pHtmlId;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String pAction) {
		action = pAction;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String pDescription) {
		description = pDescription;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String pTagName) {
		tagName = pTagName;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String pValue) {
		value = pValue;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String pLabel) {
		label = pLabel;
	}

	public String getName() {
		return name;
	}

	public void setName(String pName) {
		name = pName;
	}

	public String getText() {
		return text;
	}

	public void setText(String pText) {
		text = pText;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String pUrl) {
		url = pUrl;
	}
	
	

	public String getSrc() {
		return src;
	}

	public void setSrc(String pSrc) {
		src = pSrc;
	}

	public String getForLabel() {
		return forLabel;
	}

	public void setForLabel(String pForLabel) {
		forLabel = pForLabel;
	}

	public String getXpath() {
		return xpath;
	}

	public void setXpath(String pXpath) {
		xpath = pXpath;
	}

	public String getChecked() {
		return checked;
	}

	public void setChecked(String pChecked) {
		checked = pChecked;
	}

	public static String step2Action(CapturedStep step) { 
		String action = step.getAction();
		if("invoke".equals(action)) {
			return "<open url=\""+step.getUrl()+"/>";
		} else if("verifyTitle".equals(action)) {
			return "<assert title=\""+step.getText()+"/>";
		} else if("verifyText".equals(action)) {
			return "<assert xpath=\"//*[contains(.,'"+step.getText()+"')]\"/>";
		} else if("clickLink".equals(action)) {
			return clickLink(step);
		} else if("clickButton".equals(action)) {
			return clickButton(step);
		} else if("setInputField".equals(action)) {
			return setInputField(step);
		} else if("setFileField".equals(action)) {
			return "# setFileField not supported";
		} else if("setRadioButton".equals(action)) {
			return setRadioButton(step);
		} else if("setCheckbox".equals(action)) {
			return setCheckbox(step);
		} else if("setSelectField".equals(action)) {
			return setSelectField(step);
		} else if("span".equals(action)) {
			return clickSpan(step);
		}
		return "# can't handle "+action;
	}
	
	public static String clickLink(CapturedStep step) {
		return "<click "+identification("a",step)+"/>";
	}
	
	public static String clickButton(CapturedStep step) {
		return "<click "+identification("button",step)+"/>";
	}
	
	public static String clickSpan(CapturedStep step) {
		return "<click "+identification("span",step)+"/>";
	}
	
	public static String setInputField(CapturedStep step) {
		return "<input "+identification("*",step)+" text=\""+step.getValue()+"\"/>";
	}
	
	public static String setRadioButton(CapturedStep step) {
		return "<click "+identification("input",step, " and type=\"radio\"")+"/>";
	}
	
	public static String setCheckbox(CapturedStep step) {
		return "<click "+identification("input",step," and type=\"checkbox\"")+"/>";
	}
	
	public static String setSelectField(CapturedStep step) {
		return "<select "+identification("select",step)+" visibleText=\""+step.getText()+"\"/>";
	}
	
	
	
	
	public static String identification(String elementname, CapturedStep step) {
		return identification(elementname, step, null);
	}
	
	public static String identification(String elementname, CapturedStep step, String additionalQuery) {
		String retval = "";
		String additionalQ = "";
		if(additionalQuery!=null)
			additionalQ = additionalQuery;
		if(!empty(step.getHtmlId()))
			retval += "id=\""+step.getHtmlId()+"\"";
		else if(!empty(step.getForLabel()))
			retval += "xpath=\"//"+elementname+"[@label='"+step.getLabel()+"'"+additionalQ+"]\"";
		else if(!empty(step.getName()))
			retval += "xpath=\"//"+elementname+"[@name='"+step.getName()+"'"+additionalQ+"]\"";
		else if(!empty(step.getLabel()))
			retval += "xpath=\"//"+elementname+"[text()='"+step.getLabel()+"'"+additionalQ+"]\"";
		else if(!empty(step.getSrc()))
			retval += "xpath=\"//"+elementname+"[@src='"+step.getLabel()+"'"+additionalQ+"]\"";
		else if(!empty(step.getXpath()))
			retval += "xpath=\""+step.getXpath()+"\"";
		return retval;
	}
	
	static boolean empty(String str) {
		return str==null || "".equals(str);
	}
}
