
import java.util.ArrayList;

public class IssueOccurence {
	
	String module;
	String path;
	ArrayList<String> tests;
	
	public IssueOccurence(String module, String test, String path) {
		this.module = module;
		this.path = path;
		tests = new ArrayList<String>();
		tests.add(test);
	}
	
	public void addTest(String test) {
		if(tests.contains(test)) return;
		tests.add(test);
	}

}
