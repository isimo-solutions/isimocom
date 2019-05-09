package com.smartwebproject.testframework.ui.editor;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class TestScenarioHandler extends DefaultHandler {
	private SAXParserFactory fFactory;
	public static final String SCENARIO_URI="http://isimo.com/scenario/1.0";
	public static final String SCENARIO_ELEMENT = "scenario";
	private boolean isScenario = false;
	
	private final SAXParser createParser(SAXParserFactory parserFactory) throws Exception {
		// Initialize the parser.
		final SAXParser parser = parserFactory.newSAXParser();
		final XMLReader reader = parser.getXMLReader();
		// disable DTD validation (bug 63625)
		try {
			// be sure validation is "off" or the feature to ignore DTD's will not apply
			reader.setFeature("http://xml.org/sax/features/validation", false); //$NON-NLS-1$
			reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false); //$NON-NLS-1$
		}
		catch (SAXNotRecognizedException e) {
			// not a big deal if the parser does not recognize the features
		}
		catch (SAXNotSupportedException e) {
			// not a big deal if the parser does not support the features
		}
		return parser;
	}

	protected boolean parseContents(InputSource contents) {
		SAXParser parser = null;
		try {
			parser = createParser(getFactory());
			parser.parse(contents, this);
			return isScenario();
		} catch(StopParsingException e) {
			return isScenario();
		} catch(SAXParseException e) {
			return false;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private SAXParserFactory getFactory() {
		
		synchronized (this) {
			if (fFactory != null) {
				return fFactory;
			}
			fFactory = SAXParserFactory.newInstance();
			fFactory.setNamespaceAware(true);
		}
		return fFactory;
	}
	
	@Override
	public void startElement(String pUri, String pLocalName, String pQName, Attributes pAttributes) throws SAXException {
		// TODO Auto-generated method stub
		if(SCENARIO_URI.equals(pUri) && SCENARIO_ELEMENT.equals(pLocalName)) {
			isScenario = true;
		}
		// stop parsing as only first element is interesting for us
		throw new StopParsingException();
	}
	
	private class StopParsingException extends SAXException {
		/**
		 * All serializable objects should have a stable serialVersionUID
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Constructs an instance of <code>StopParsingException</code> with a <code>null</code> detail message.
		 */
		public StopParsingException() {
			super((String) null);
		}
	}

	public boolean isScenario() {
		return isScenario;
	}
}
