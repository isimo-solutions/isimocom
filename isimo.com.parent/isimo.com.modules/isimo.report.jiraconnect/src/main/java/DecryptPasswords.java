import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class DecryptPasswords extends Task {
	String encryptedSuffix = "encrypted.password";
	String decryptedSuffix = "decrypted.password";
	TrippleDes trippleDes;
	
	public DecryptPasswords() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void execute() throws BuildException {
		try {
			// TODO Auto-generated method stub
			super.execute();
			trippleDes = new TrippleDes();
			this.log("keyset size="+getProject().getProperties().entrySet().size());
			for(Object me: getProject().getProperties().entrySet()) {
				encryptProperty(me);
			}
		} catch(Exception e) {
			throw new BuildException(e);
		}
	}
	
	void encryptProperty(Object obj) {
		Map.Entry<String, String> me = (Map.Entry<String, String>) obj;
		String propertyName = me.getKey();
		String value = me.getValue();
		//log("Property "+propertyName+" value="+value);
		if(propertyName.toString().endsWith(encryptedSuffix)) {
			String decryptedPropertyName = propertyName.toString().substring(0, propertyName.toString().length()-encryptedSuffix.length())+decryptedSuffix;
			this.log("Decrypting property "+propertyName+"; encrypted="+value+";decr="+decryptedPropertyName);
			getProject().setProperty(decryptedPropertyName,trippleDes.decrypt(value.toString()));
		}
	}
	
	
}
