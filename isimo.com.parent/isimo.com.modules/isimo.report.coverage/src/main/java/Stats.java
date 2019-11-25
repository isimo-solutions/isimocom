import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.dom4j.Element;

public class Stats {
	@XmlAttribute
	int totalActions;
	@XmlAttribute
	int totalErrors;
	@XmlElement
	Map<String, UIMask> uimasks = new HashMap<String, UIMask>();
	
	static Pattern pknummer;
	
	{
		pknummer = Pattern.compile("PK\\-\\d+");
	}
	
	public Stats() {
		
	}
	
	UIMask uiMask(String title) {
		UIMask mask = null;
		if(uimasks.containsKey(title)) {
			mask = uimasks.get(title);
		} else {
			mask = new UIMask(title);
			uimasks.put(title, mask);
		}
		return mask;
	}
	
	String shortname(String testcase) {
		Matcher m = pknummer.matcher(testcase);
		if(m.find()) {
			return m.group(0);
		} else {
			return testcase;
		}
	}
	
	void addAction(Element action) {
		String title = action.attributeValue("log_title");
		String tab = action.attributeValue("log_tab");
		
		int errors = 0;
		List<Element> elements = action.elements();
		for (Element element : elements) {
			if(element.getName().equals("error") || element.getName().equals("failure")) {
				errors++;
			}
		} 
		
		String testcase = shortname(action.attributeValue("log_testcase"));
		UIMask mask = uiMask(title);
		if(tab!=null) {
			UIMask tabmask = mask.subMask(tab);
			tabmask.addAction(action);
			tabmask.addTestcase(testcase);
			tabmask.errors += errors;
		} else {
			mask.addAction(action);
			mask.addTestcase(testcase);
			mask.errors += errors;
		}

	}
	
	void addStats(Element statselem) {
		String statsbefore = statselem.asXML();
		Reader reader = new StringReader(statselem.asXML());
		Stats stats = (Stats) JAXB.unmarshal(reader, Stats.class);

		for(UIMask uimask: stats.uimasks.values()) {
			System.out.println("ActionTypes="+uimask.actionsPerTyp.entrySet().size());
			if(uimasks.containsKey(uimask.title))
				uimasks.get(uimask.title).mergeStats(uimask);
			else
				uimasks.put(uimask.title, uimask);
		}
	}
	void calcActions() {
		totalActions = 0;
		totalErrors = 0;
		for(UIMask mask: uimasks.values()) {
			mask.calc();
			totalActions += mask.totalActions;
			totalErrors += mask.errors;
		}
	}
	
	void calcSums() {
		totalActions = 0;
		totalErrors = 0;
		for(UIMask mask: uimasks.values()) {
			mask.calcSums();
			mask.mergeTestcases();
			totalActions += mask.totalActions;
			totalErrors += mask.errors;
		}
	}

}
