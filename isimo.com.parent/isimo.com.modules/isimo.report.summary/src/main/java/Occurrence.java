
import javax.xml.bind.annotation.XmlAttribute;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Occurrence implements Comparable<Occurrence> {

	
	@XmlAttribute
	String issue;
	
	@XmlAttribute
	String scenario;
	
	@XmlAttribute
	String linenumber;

	@XmlAttribute
	String status;
	
	public Occurrence() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public int hashCode() {
        return new HashCodeBuilder(17, 37).
        append(issue).
        append(scenario).
        append(linenumber).
        toHashCode();
	}

	public int compareTo(Occurrence o) {
		int i = issue.compareTo(o.issue);
		if( i != 0) return i;
		
		i = linenumber.compareTo(o.linenumber);
		if( i != 0) return i;
		
		return i = scenario.compareTo(o.scenario);
	}
	

}
