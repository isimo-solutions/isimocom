import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.StringReader;
import java.util.List;

import javax.xml.bind.JAXB;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class PostprocessCoverage extends Task {
	File in, out;
	boolean summaries = false;
	Stats stats = null;
	String name = null;
	public PostprocessCoverage() {
	}
	
	public void execute() throws BuildException {
		try  {
			// TODO Auto-generated method stub
			SAXReader reader = new SAXReader();
			Document indoc = reader.read(in);
			stats = new Stats();
			if(!summaries) {
				System.out.println("Postprocessing coverage scenarios: "+in);
				analyzeScenarios(indoc.getRootElement());
				stats.calcActions();
			} else {
				System.out.println("Postprocessing coverage summaries: "+in);
				analyzeStats(indoc.getRootElement());
				stats.calcSums();
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			JAXB.marshal(stats, baos);
			baos.flush();
			baos.close();
			String xmlstats = baos.toString();
			//System.out.println("Stats="+xmlstats);
			Document statsdoc = reader.read(new StringReader(xmlstats));
			FileWriter outwriter = new FileWriter(out);
			outwriter.write("<wrapper name=\""+name+"\">"+statsdoc.getRootElement().asXML()+"</wrapper>");
			outwriter.flush();
			outwriter.close();
		} catch(Exception e) {
			e.printStackTrace();
			throw new BuildException(e);
		}
	}

	public File getIn() {
		return in;
	}

	public void setIn(File in) {
		this.in = in;
	}

	public File getOut() {
		return out;
	}

	public void setOut(File out) {
		this.out = out;
	}

	public void setSummaries(boolean summaries) {
		this.summaries = summaries;
	}
	
	
	private void analyzeScenarios(Element scenarios) {
		List<Element> elements = scenarios.elements();
		for(Element element: elements) {
			if(element.attribute("log_title") != null) {
				stats.addAction(element);
			}
			else 
				analyzeScenarios(element);
		}
	}
	
	private void analyzeStats(Element scenarios) {
		List<Element> elements = scenarios.elements();
		for(Element element: elements) {
			if("stats".equals(element.getName()))
				stats.addStats(element);
			else 
				analyzeStats(element);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
