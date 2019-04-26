package com.smartwebproject.testframework.ui.editor.internal;

import java.util.ArrayList;

import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CSSParseException;
import org.w3c.css.sac.ErrorHandler;

import com.steadystate.css.parser.HandlerBase;

public class CSSErrorHandler extends HandlerBase implements ErrorHandler {
	ArrayList<CSSParseException> exceptions = new ArrayList<CSSParseException>(); 

	@Override
	public void error(CSSParseException cssParseException) throws CSSException {
		exceptions.add(cssParseException);
	}

	@Override
	public void fatalError(CSSParseException cssParseException) throws CSSException {
		exceptions.add(cssParseException);
	}

	@Override
	public void warning(CSSParseException cssWarning) throws CSSException {
		super.warning(cssWarning);
	}

	public ArrayList<CSSParseException> getExceptions() {
		return exceptions;
	}
}
