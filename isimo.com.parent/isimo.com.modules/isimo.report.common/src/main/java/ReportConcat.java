import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class ReportConcat extends Task{
	
	ArrayList<FileSet> filesets = new ArrayList<>();
	File out;
	String rootElement = "root";
	String parentElement = "";

	public void execute() throws BuildException {
		try  {
			Document outDoc = DocumentHelper.createDocument();
			Element root = outDoc.addElement( rootElement );
			
			
			for (FileSet fs : filesets) {
				DirectoryScanner sc = fs.getDirectoryScanner();
				File basedir = sc.getBasedir();
				for (String name : sc.getIncludedFiles()){
					File file = new File(basedir.getAbsolutePath() + File.separator + name);
					SAXReader reader = new SAXReader();
					Document doc = reader.read(file);
					
					Element parent = root;
					if(!"".equals(parentElement))
					{
						parent = root.addElement(parentElement);
					}
					String concatDir = out.getParentFile().toPath().relativize(file.getParentFile().toPath()).toString();
					parent.add(doc.getRootElement().createCopy().addAttribute("concatDir", concatDir).addAttribute("concatName", file.getName()));
					
					OutputFormat format = OutputFormat.createPrettyPrint();
					OutputStream os = new FileOutputStream(out);
					XMLWriter writer = new XMLWriter( os, format );
			        writer.write(outDoc);
				}
			}
		
		}catch(Exception e) {
			e.printStackTrace();
			throw new BuildException(e);
		}
			
		}
	
	
	public String getRootElement() {
		return rootElement;
	}
	public void setRootElement(String rootElement) {
		this.rootElement = rootElement;
	}
	
	public void addFileSet(FileSet fileset) {
		filesets.add(fileset);
	}
	
	public File getOut() {
		return out;
	}
	public void setOut(File out) {
		this.out = out;
	}
	

}
