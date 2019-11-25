import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlTransient;

import org.dom4j.Element;

import javafx.collections.transformation.SortedList;


public class UIMask {
	@XmlAttribute
	int totalActions;
	@XmlAttribute
	String title;
	@XmlAttribute
	int errors;
	@XmlElement
	public Map<String, Integer> actionsPerTyp = new HashMap<String, Integer>();

	@XmlElement
	public Map<String, UIMask> submasks = new HashMap<String, UIMask>();

	@XmlElement
	public SortedSet<String> testcases = new TreeSet<String>();

	@XmlTransient
	public Map<String, List<Element>> actions = new HashMap<String, List<Element>>(); 
	
	public UIMask() {};
	public UIMask(String title) {
		this.title = title;
	}
	
	void addTestcase(String testcase) {
		testcases.add(testcase);
	}
	
	void addAction(Element elem) {
		String type = actionType(elem);
		if("screenshot".equals(type))
			return;
		if(actions.get(type)==null)
			actions.put(type, new ArrayList<Element>());
		actions.get(type).add(elem);
	}
	
	
	String actionType(Element elem) {
		if(elem.getName().equals("action")) {
			String qualified = elem.attributeValue("classname");
			return qualified.substring(qualified.lastIndexOf('.')+1);
		}
		return elem.getName();
	}
	
	void calc() {
		totalActions = 0;
		actionsPerTyp = new HashMap<String, Integer>();
		for(String actiontype: actions.keySet()) {
			int actionscount = actions.get(actiontype).size();
			actionsPerTyp.put(actiontype, actionscount);
			totalActions += actionscount;
		}
		for(UIMask submask: submasks.values()) {
			submask.calc();
			mergeStats(submask);
		}
	}
	
	
	void calcSums() {
		totalActions = 0;
		for(String actiontype: actionsPerTyp.keySet()) {
			totalActions += actionsPerTyp.get(actiontype);
		}
	}
	
	void mergeTestcases(UIMask submask) {
		testcases.addAll(submask.testcases);
	}
	
	void mergeTestcases() {
		for(UIMask submask: submasks.values()) {
			mergeTestcases(submask);
		}
	}
	
	
	void mergeStats(UIMask tomerge) {
		errors += tomerge.errors;
		for(Map.Entry<String, Integer> stat: tomerge.actionsPerTyp.entrySet()) {
			int initialValue = 0;
			if(actionsPerTyp.containsKey(stat.getKey()))
				initialValue = actionsPerTyp.get(stat.getKey());
			System.out.println("stat="+stat.getKey()+",initial="+initialValue+",add="+stat.getValue());
			actionsPerTyp.put(stat.getKey(), initialValue+stat.getValue());
		}
		
		for(UIMask submask: tomerge.submasks.values()) {
			UIMask localsubmask  = subMask(submask.title);
			localsubmask.mergeStats(submask);
			localsubmask.calcSums();
			localsubmask.mergeTestcases(submask);
		}
		mergeTestcases(tomerge);
	}
	
	UIMask subMask(String title) {
		UIMask mask = null;
		if(submasks.containsKey(title)) {
			mask = submasks.get(title);
		} else {
			mask = new UIMask(title);
			submasks.put(title, mask);
		}
		return mask;
	}
}
