package com.smartwebproject.testframework.ui.editor;

import java.io.InputStream;
import java.io.StringReader;
import java.net.URI;

import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.xerces.parsers.SAXParser;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XNIException;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.xml.core.internal.validation.XMLValidationConfiguration;
import org.eclipse.wst.xml.core.internal.validation.XMLValidationReport;
import org.eclipse.wst.xml.core.internal.validation.core.NestedValidatorContext;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationMessage;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationReport;
import org.eclipse.wst.xml.core.internal.validation.eclipse.Validator;
import org.w3c.css.sac.CSSParseException;
import org.w3c.css.sac.Parser;
import org.w3c.css.sac.SelectorList;
import org.w3c.css.sac.helpers.ParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

import com.isimo.core.model.ModelValidationException;
import com.isimo.core.model.ModelValidator;
import com.smartwebproject.testframework.ui.TestFrameworkUIPlugin;
import com.smartwebproject.testframework.ui.editor.internal.CSSErrorHandler;
import com.smartwebproject.testframework.ui.editor.internal.LocationCoordinate;
import com.smartwebproject.testframework.ui.editor.internal.XMLValidationInfo;
import org.apache.xerces.parsers.XIncludeAwareParserConfiguration;

public class TestScenarioValidator extends Validator {
	String uri = null;
	public TestScenarioValidator() {
		System.out.println("Init Validator");
	}

	public ValidationReport validate(String uri, InputStream inputstream, NestedValidatorContext context, ValidationResult result) {

		XMLValidationReport valreport = validate(uri, inputstream, null, result, context);

		return valreport;
	}

	XMLValidationReport validate(String uri, InputStream is, XMLValidationConfiguration config, ValidationResult result, NestedValidatorContext ctx) {
		this.uri = uri;
		MySAXParser parser = new MySAXParser(uri);
		try {
			InputStream istoread = null;
			if(is != null)
				istoread = is;
			else
				is = new URI(uri).toURL().openStream();
			parser.parse(new InputSource(is));
			return parser.getXmlValidationInfo();
		} catch (SAXParseException e) {
			return parser.getXmlValidationInfo();
			// ignore, xml parser will handle this
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	

	/*
	 * public ValidationResult validate(IResource pResource, int pKind, ValidationState pState, IProgressMonitor pMonitor) { try { ValidationResult vr = new ValidationResult(); XMLValidationReport
	 * report = validate(pResource.getLocationURI().toString(), pResource.getLocationURI().toURL().openStream(), null, null, null); for(ValidationMessage vm: report.getValidationMessages()) {
	 * vr.add(vm); } return vr; } catch (Exception e) { throw new RuntimeException(e); } }
	 */

	static class MyXMLParserConfiguration extends XIncludeAwareParserConfiguration {

	}

	static class MySAXParser extends SAXParser {
		XMLLocator locator = null;
		String uri;
		XMLValidationInfo xmlValidationInfo = null;

		MySAXParser(String uri) {
			super();
			this.uri = uri;
			/*
			 * try { setFeature("http://apache.org/xml/features/continue-after-fatal-error", false); setFeature("http://xml.org/sax/features/namespace-prefixes", true);
			 * setFeature("http://xml.org/sax/features/namespaces", true); } catch(SAXException e) { throw new RuntimeException(e); }
			 */
		}

		public void startDocument(XMLLocator theLocator, String encoding, NamespaceContext nscontext, Augmentations augs) {
			this.locator = theLocator;
			this.xmlValidationInfo = new XMLValidationInfo(uri);
			this.xmlValidationInfo.setXMLLocator(theLocator);
			super.startDocument(theLocator, encoding, nscontext, augs);
		}

		public void startElement(QName qname, XMLAttributes xmlAttributes, Augmentations arg2) {

			LocationCoordinate lc = new LocationCoordinate(this.locator.getLineNumber(), this.locator.getColumnNumber(), qname.rawname);
			this.xmlValidationInfo.getStartElementLocations().push(lc);
			if (shouldValidateAttr(xmlAttributes,"css")) {
				ParserFactory pf = new ParserFactory();
				try {
					System.setProperty("org.w3c.css.sac.parser", com.steadystate.css.parser.SACParserCSS3.class.getCanonicalName());
					Parser p = pf.makeParser();
					CSSErrorHandler errorHandler = new CSSErrorHandler();
					p.setErrorHandler(errorHandler);
					SelectorList sl = p.parseSelectors(new org.w3c.css.sac.InputSource(new StringReader(xmlAttributes.getValue("css"))));
					if(errorHandler.getExceptions().size() != 0) {
						throw errorHandler.getExceptions().get(0);
					}
				} catch(CSSParseException e) {
					lc.setAttribute("css");
					addValidatorMessage("CSS-Selector-Syntax Problem ["+e.getLineNumber()+","+e.getColumnNumber()+"]: "+e.getMessage(), IMarker.SEVERITY_ERROR, lc);
				} catch(Exception e) {
					throw new RuntimeException(e);
				}
			}
			if(shouldValidateAttr(xmlAttributes,"xpath")) {
				XPathFactory factory = XPathFactory.newInstance();
				try {
					lc.setAttribute("xpath");
					factory.newXPath().compile(xmlAttributes.getValue("xpath"));
				} catch(XPathExpressionException e) {
					addValidatorMessage("XPath-Syntax Problem: "+getCauseMessage(e), IMarker.SEVERITY_ERROR, lc);
				}
			}
			if(shouldValidateAttr(xmlAttributes,"model")) {
				try {
					lc.setAttribute("model");
					ModelValidator.validateModelPath(TestFrameworkUIPlugin.getDefault().getModelForURI(uri),xmlAttributes.getValue("model"));
				} catch (ModelValidationException e) {
					addValidatorMessage("Model-Path Problem: "+getCauseMessage(e), IMarker.SEVERITY_ERROR, lc);
				}
			}	
			//super.startElement(qname, xmlAttributes, arg2);
		}
		
		
		
		boolean shouldValidateAttr(XMLAttributes attrs, String attr) {
			String value = attrs.getValue(attr);
			return value != null && !value.matches(".*\\{.*\\}.*"); // attributes exists and contains no variable expressions in which case it's not possible if the value is correct or not
		}
		
		public String getCauseMessage(Throwable e) {
			if(e.getCause() == null)
				return e.getMessage();
			else return getCauseMessage(e.getCause());
		}

		public void endElement(QName arg0, Augmentations arg1) throws XNIException {
			super.endElement(arg0, arg1);
			this.xmlValidationInfo.getStartElementLocations().pop();

		}

		void addValidatorMessage(String message, int severity, LocationCoordinate lc) {
			Object[] args = new Object[1];
			args[0] = lc.getAttribute();
			xmlValidationInfo.addError(message, lc.getLineNumber(), lc.getColumnNumber(), uri, "attr-error", args);
		}

		public XMLValidationInfo getXmlValidationInfo() {
			return xmlValidationInfo;
		}

	}

	@Override
	protected void addInfoToMessage(ValidationMessage validationMessage, IMessage message) {

		String key = validationMessage.getKey();
		if (key == null)
			return;
		message.setAttribute("columnNumber", new Integer(validationMessage.getColumnNumber()));
		message.setAttribute("squiggleSelectionStrategy", "ATTRIBUTE_VALUE");
		message.setAttribute("squiggleNameOrValue", validationMessage.getMessageArguments()[0]);
	}
	

}
