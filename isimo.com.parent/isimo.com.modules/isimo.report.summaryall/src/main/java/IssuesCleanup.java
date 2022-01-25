

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

@XmlRootElement
public class IssuesCleanup {
	
	@XmlElement
	int issuesnumber = 0;
	
	@XmlElement
	List<Occurrence> occs;
	
	String rootDir;
	
	public IssuesCleanup() {
	}
	
	public IssuesCleanup(String dir) {
		rootDir = dir;
	}

	public static void main(String[] args) throws Exception {
		IssuesCleanup ia = new IssuesCleanup(args[0]);
		File [] files = new File(ia.rootDir).listFiles();
		List<File> logsList = FindLogs(files);
		SAXReader reader = new SAXReader();
		TreeMap<Occurrence, Occurrence> ht = new TreeMap<Occurrence, Occurrence>();
		
		for (File file : logsList)
		{
			Document doc = reader.read(file);
			String status = doc.selectSingleNode("//testcase").valueOf("@status");
			List<Node> issues = doc.selectNodes("//*[@issue]");
			
			for(Node n : issues)
			{
				Occurrence occ = new Occurrence();
				occ.issue = n.valueOf("@issue");
				occ.linenumber = n.valueOf("@linenumber");
				occ.scenario = n.getParent().valueOf("@scenario").replace("sb/gen/", "");
				if(occ.scenario.lastIndexOf("_") > 0) occ.scenario = occ.scenario.substring(0, occ.scenario.lastIndexOf("_"));
				
				
				if("SUCCESS".equals(status)) {
					occ.status = "Resolved";
					occ.statusTranslateId = "resolvedSpan";
				}
				else if("FAILED".equals(status) && n.selectNodes(".//*[name()='failure']").size()==0) {
					occ.status = "Need verification";
					occ.statusTranslateId = "verificationSpan";
				}
				else {
					occ.status = "Unresolved";
					occ.statusTranslateId = "unresolvedSpan";
				}
				
				if(ht.containsKey(occ))
				{
					Occurrence previous = ht.remove(occ);
					if("Resolved".equals(previous.status)) previous.status = occ.status;
					else if ("Need verification".equals(previous.status) && "Unresolved".equals(occ.status)) previous.status = occ.status; 
					ht.put(previous, previous);
				}else ht.put(occ, occ);
			}
		}
		ia.occs = new ArrayList<Occurrence>(ht.values());
		ia.issuesnumber = ht.size();
		Collections.sort(ia.occs);
		ia.output();
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
	
	
	public void output() throws Exception {
		OutputStream os = new FileOutputStream(this.rootDir + "/summaryall/IssuesCleanup.xml" );
		JAXB.marshal( this, os );
		JAXB.marshal( this, System.out );
	}
	

	
}
