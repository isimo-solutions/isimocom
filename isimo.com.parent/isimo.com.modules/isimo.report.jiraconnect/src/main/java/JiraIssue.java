

import java.util.ArrayList;

import org.dom4j.Element;

public class JiraIssue implements Comparable<JiraIssue>{

	public String id;
	public String summary;
	public int priority;
	public String priorityString;
	public String status;
	public String sprint;
	public Element testListEl;
	
	public JiraIssue(String id, String summary,int priority, String priorityString, String status, String sprint, Element testListEl){
		this.id = id;
		this.priority = priority;
		this.priorityString = priorityString;
		this.summary = summary;
		this.status = status;
		this.sprint = sprint;
		this.testListEl = testListEl;
	}
	
	@Override
	public int compareTo(JiraIssue o) {
		return Integer.compare(o.priority, priority);
	}
	
	
}
