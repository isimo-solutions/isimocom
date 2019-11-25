
import java.util.ArrayList;

public class Issue {
	
	String id;
	ArrayList<IssueOccurence> tests;
	
	public Issue(String id) {
		this.id = id;
		tests = new ArrayList<IssueOccurence>();
	}
	
	public void addTest(String module, String test, String path) {
		for(IssueOccurence io : tests) {
			if(io.module.equals(module)) {
				io.addTest(test);
				return;
			}
		}
		tests.add(new IssueOccurence(module, test, path));
	}
	
	public int issueCount() {
		int count = 0;
		
		for(IssueOccurence io : tests){
			count += io.tests.size();
		}
		
		return count;
	}
	

}
