import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;


public class CreateUniqueIssuesDoc extends Task {

	
	public String rootDir="";
	
	public void execute() throws BuildException{
		try {
			File [] files = new File(rootDir).listFiles();
			List<File> logsList = FindLogs(files);
			SAXReader reader = new SAXReader();
			HashMap<String , Issue> uniqueIssues = new HashMap<String, Issue>();
			File summaryAll = new File(rootDir + "/summaryall/summaryall.xml");
			Document summaryAllDoc = reader.read(summaryAll);
			for (File file : logsList)
			{
				Document doc = reader.read(file);
				List<Node> issues = doc.selectNodes("//*[@issue]");
				for(Node n : issues)
				{
					if(n.selectNodes(".//*[name()='failure']").size()>0) {
						Element el = (Element)n;
						String testcase = doc.selectSingleNode("//testcase").valueOf("@scenario");
						String testmodule = doc.selectSingleNode("//testcase").valueOf("@module");
						Element summaryEl = (Element)summaryAllDoc.selectSingleNode("//summaryall/summary[./testcase[contains(@name, '"+testcase+"')]]");
						String path = summaryEl.attributeValue("concatDir");
						Issue issue;
						if(!uniqueIssues.containsKey(el.attributeValue("issue"))) {
							issue = new Issue(el.attributeValue("issue"));
							issue.addTest(testmodule, testcase, path);
							uniqueIssues.put(issue.id, issue);
						}else {
							issue = uniqueIssues.get(el.attributeValue("issue"));
							issue.addTest(testmodule, testcase, path);
						}
					}
				}
			}
			uniqueIssuesOutput(new ArrayList<Issue>(uniqueIssues.values()), rootDir);
		}catch(Exception e) {
			e.printStackTrace();
			throw new BuildException(e);
		}
		
	}
	
	public static void uniqueIssuesOutput(List<Issue> uniqueIssues, String rootDir) throws Exception {
		Document uniqueIssuesDoc = DocumentHelper.createDocument();
		Element root = uniqueIssuesDoc.addElement( "issuesList" );
		root.addAttribute("issuesnumber", uniqueIssues.size()+"");
		for (Issue issue : uniqueIssues) {
			Element issueEl = root.addElement("issue").addAttribute("id",issue.id);
			Element testListEl = issueEl.addElement("testlist").addAttribute("testsnumber", issue.issueCount()+"");
			for (IssueOccurence io : issue.tests) {
				Element moduleEl = testListEl.addElement("module").addAttribute("name",io.module).addAttribute("testnumber", io.tests.size()+"").addAttribute("path", io.path);
				for (String test : io.tests) {
					moduleEl.addElement("test").setText(test);
				}
			}
		}
		
		OutputFormat format = OutputFormat.createPrettyPrint();
		OutputStream os = new FileOutputStream(rootDir + "/summaryall/UniqueIssues.xml");
		XMLWriter writer = new XMLWriter( os, format );
        writer.write(uniqueIssuesDoc);
		
	}
	
	public static List<File> FindLogs(File[] files) {
		   List<File> logs = new ArrayList<File>();
			for (File file : files) {
		        if (file.isDirectory()) {
		            logs.addAll(FindLogs(file.listFiles()));
		        } else {
		        	if("scenariolog.xml".equals(file.getName()))
		        	{
		        		logs.add(file);
		        	}
		        }
		    }
			return logs;
		}
	
	public String getRootDir() {
		return rootDir;
	}

	public void setRootDir(String rootDir) {
		this.rootDir = rootDir;
	}
}
