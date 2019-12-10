

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.codehaus.jettison.json.JSONArray;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.atlassian.greenhopper.service.sprint.Sprint;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;

public class JiraGetIssuesInfo extends Task {

	public String failFromPriority = "";
	public int failFromPriorityId = -1;
	public String[] prioritiesTab;
	public String[] colorsTab;

	public Boolean addToSummary = true;
	public String summaryNodeXpath = "//summaryall/summary";
	public Boolean mockup = false;


	private URI JIRA_URL = URI.create("");
	 private String JIRA_USERNAME = "";
	 private String JIRA_PASSWORD = "";
	 public String rootDir="";
	 	 
	 private int getPrioityId(String priority) {
		 for (int i = 0; i < prioritiesTab.length; i++) {
			if(priority.equals(prioritiesTab[i])) return i;
		}
		 return -1;
	 }
	 
	 private JiraRestClient JiraConnect() {
		 return new AsynchronousJiraRestClientFactory().createWithBasicHttpAuthentication(JIRA_URL, JIRA_USERNAME, JIRA_PASSWORD);
	 }
	 
	 public void addToSummaryAll(Element el) throws Exception {
		 File summaryFile = new File(rootDir + "/summaryall/summaryall.xml");
		 SAXReader reader = new SAXReader();
		 Document summaryDoc = reader.read(summaryFile);
		 Node n = summaryDoc.selectSingleNode(summaryNodeXpath);
		 ((Element)n).add(el);
		 
		OutputFormat format = OutputFormat.createPrettyPrint();
		OutputStream os = new FileOutputStream(rootDir + "/summaryall/summaryall.xml");
		XMLWriter writer = new XMLWriter( os, format );
        writer.write(summaryDoc);
	 }
	 
	 public void execute() throws BuildException{
		 try { 
			File issuesFile = new File(rootDir + "/summaryall/UniqueIssues.xml");
			SAXReader reader = new SAXReader();
			Document issuesDoc = reader.read(issuesFile);
			Document outDoc = DocumentHelper.createDocument();
			Element root = outDoc.addElement( "issuesInfo" );
			Element stats = root.addElement("stats");
			Element issuesListWrapper = root.addElement("issuesList");
			int prioritiesNumbers[] = new int [prioritiesTab.length];
			List<Node> nodes = issuesDoc.selectNodes("//issue");
			
			IssueWrapper.setMockup(mockup);
			if (!mockup) {
				IssueWrapper.setRestClient(JiraConnect());
			}
			
			ArrayList<JiraIssue> issuesList = new ArrayList<>();
			for (Node node : nodes) {
				String issueKey = ((Element)node).attributeValue("id");
				IssueWrapper issue = new IssueWrapper(issueKey);
				Element testlistEl = (Element)node.selectSingleNode("./testlist").clone();
				String priority = issue.getPriority();
				String summary = issue.getSummary();
				String sprint = issue.getFieldByName("Sprint");
				String status = issue.getStatus();
				
				
				System.out.println("Key: "+ issueKey + " priority: " + priority + " id: " + getPrioityId(priority) +" sprint: " + sprint);
				issuesList.add( new JiraIssue(issueKey, summary,getPrioityId(priority), priority , status, sprint, testlistEl));				
				prioritiesNumbers[getPrioityId(priority)] += 1;
			}
			
			Collections.sort(issuesList);
			for (JiraIssue issue : issuesList) {
				issuesListWrapper.addElement("issue").addAttribute("key", issue.id).addAttribute("summary", issue.summary)
											.addAttribute("priority", issue.priorityString).addAttribute("id", issue.priority+"")
											.addAttribute("status", issue.status).addAttribute("sprint", issue.sprint).add(issue.testListEl);
			}
			
			stats.addAttribute("uniqueIssues", nodes.size()+"");
			Element priorities = stats.addElement("priorities");
			for (int i = 0; i < prioritiesNumbers.length; i++) {
				priorities.addElement(prioritiesTab[i]).addAttribute("id", i+"").addAttribute("color", colorsTab[i]).setText(prioritiesNumbers[i]+"");
			}
			
			if(!failFromPriority.equals(""))
				failFromPriorityId = getPrioityId(failFromPriority);
			
			if(failFromPriorityId >= 0) {
				
				int sum = 0;
				for(int i = failFromPriorityId; i < 6; i++) {
					sum += prioritiesNumbers[i];
				}
				if(sum > 0) root.addAttribute("issuesFailed", "true").addAttribute("failPriority", failFromPriority);
			}
			addToSummaryAll(root);
			
			OutputFormat format = OutputFormat.createPrettyPrint();
			OutputStream os = new FileOutputStream(rootDir + "/summaryall/UniqueIssues.xml");
			XMLWriter writer = new XMLWriter( os, format );
	        writer.write(outDoc);
		}catch (Exception e) {
			e.printStackTrace();
			throw new BuildException(e);
		}
	}
	 
	 public static void main(String[] args) throws Exception {
		 JiraGetIssuesInfo a = new JiraGetIssuesInfo();
		 a.execute();
	 }
	 
	 public URI getJIRA_URL() {
		return JIRA_URL;
	}

	public void setJIRA_URL(URI jIRA_URL) {
		this.JIRA_URL = jIRA_URL;
	}

	public String getRootDir() {
		return rootDir;
	}

	public void setRootDir(String rootDir) {
		this.rootDir = rootDir;
	}
	public String getJIRA_USERNAME() {
		return JIRA_USERNAME;
	}

	public void setJIRA_USERNAME(String jIRA_USERNAME) {
		JIRA_USERNAME = jIRA_USERNAME;
	}

	public String getJIRA_PASSWORD() {
		return JIRA_PASSWORD;
	}

	public void setJIRA_PASSWORD(String jIRA_PASSWORD) {
		JIRA_PASSWORD = jIRA_PASSWORD;
	}

	public String getFailFromPriority() {
		return failFromPriority;
	}

	public void setFailFromPriority(String failFromPriority) {
		this.failFromPriority = failFromPriority;
	}
	
	public Boolean getAddToSummary() {
		return addToSummary;
	}

	public void setAddToSummary(Boolean addToSummary) {
		this.addToSummary = addToSummary;
	}

	public String getSummaryNodeXpath() {
		return summaryNodeXpath;
	}

	public void setSummaryNodeXpath(String summaryNodeXpath) {
		this.summaryNodeXpath = summaryNodeXpath;
	}
	public String[] getPrioritiesTab() {
		return prioritiesTab;
	}

	public void setPrioritiesTab(String prioritiesTab) {
		this.prioritiesTab = prioritiesTab.split(";");
	}
	
	public String[] getColorsTab() {
		return colorsTab;
	}

	public void setColorsTab(String colorsTab) {
		this.colorsTab = colorsTab.split(";");
	}

	public Boolean getMockup() {
		return mockup;
	}

	public void setMockup(Boolean mockup) {
		this.mockup = mockup;
	}

}
