import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.tools.ant.BuildException;
import org.codehaus.jettison.json.JSONArray;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.Issue;

public class IssueWrapper {
	static JiraRestClient restClient;
	static Boolean mockup;

	static HashMap<String, ArrayList<String>> mockupValues;
	{
		mockupValues = new HashMap<String, ArrayList<String>>();
		mockupValues.put("RR-01", new ArrayList<String>(Arrays.asList("Normal", "Page not found after pressing delete faktura button", "Open", "2020")));
		
	}
	
	Issue issue;
	String issueKey;
	public IssueWrapper(String issueKey) {
		this.issueKey = issueKey;
		if (!mockup) {
			 try { 
				 issue = restClient.getIssueClient().getIssue(issueKey).claim();
			 }catch (RestClientException e) {
				 System.out.println("Issue '"+ issueKey +"' does not exist!");
				 issue = null;
			}
		}
	}
	
	public String getPriority() {
		if(!mockup)
		{
			if(issue == null)
				return "---";
			return issue.getPriority().getName();
		}else{
			return mockupValues.get(issueKey).get(0);
		}
	}
	
	public String getSummary() {
		if (!mockup) {
			if(issue == null)
				return "Issue was not found!";
			return issue.getSummary();
		}else{
			return mockupValues.get(issueKey).get(1);
		}
	}
	
	public String getStatus() {
		if(!mockup) {
			if(issue == null)
				return "---";
			return issue.getStatus().getName();
		}else{
			return mockupValues.get(issueKey).get(2);
		}
	}

	public String getFieldByName(String name) {
		if (!mockup) {
			if(issue == null)
				return "---";
			String value = "";
			try {
			if(issue.getFieldByName(name).getValue() != null) {
				JSONArray jsonArray = (JSONArray)issue.getFieldByName(name).getValue();
				for (int i = 0; i < jsonArray.length(); i++) {
					String fieldJson = jsonArray.get(i).toString();
					fieldJson = fieldJson.substring(fieldJson.indexOf("["));
					String[] sprintFields = fieldJson.split(",");
					for (String field : sprintFields) {
						if(field.startsWith("name=")) {
							if(!value.equals("")) value+=", ";
							value += field.replace("name=", "");
						}
					}
				}				
			}
				return value;
			}catch (Exception e) {
					e.printStackTrace();
					throw new BuildException(e);	
			}
		}else{
			return mockupValues.get(issueKey).get(3);
		}
	}
	

	public static Boolean getMockup() {
		return mockup;
	}

	public static void setMockup(Boolean mockup) {
		IssueWrapper.mockup = mockup;
	}
	
	public static JiraRestClient getRestClient() {
		return restClient;
	}

	public static void setRestClient(JiraRestClient restClient) {
		IssueWrapper.restClient = restClient;
	}
}
