package TableEditor;

import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.Properties;

public  class Utility {
	
	/**
	 ***************************************************************************************************
	 * NAME:  loadSystemProperties 
	 *
	 * PURPOSE: This method will load the system properties file based on the file TableEditor.properties
	 * It assumes that the file in the the application working directory or in the top level of the 
	 * application jar file.but it should be out side of jar file
	 *
	 * INPUTS:  1.  N/A
	 *
	 * OUTPUTS: 1.  sysProperties - loaded with data
	 *
	 * @author Monaco
	 *
	 ***************************************************************************************************
	 */
	public static Properties loadSystemProperties() {
		Properties prop = new Properties();
		CodeSource src = Utility.class.getProtectionDomain().getCodeSource();
		if (src != null) {
			try {
				URL url = new URL(src.getLocation(), "TableEditor.properties");
				//java.net.URL sysPropertiesURL = TableEditor.class.getResource("TableEditor.properties");
				prop.load(url.openStream());
		    } catch (IOException e) {
		    	prop = null;
		    }
		}
	    return prop;
	}
	
	
	/**
	 ***************************************************************************************************
	 * NAME:  getSystemProperty(String prop) 
	 *
	 * PURPOSE: This a public method available to all screens that will return a system property from the
	 * LSRApplt.properties file.
	 *
	 * INPUTS:  1.  String prop - a property name
	 *
	 * OUTPUTS: 1.  String - the value associated with the property.  "" if property is not valid.
	 *
	 * @author Monaco
	 *
	 ***************************************************************************************************
	 */	
	public static String getSystemProperty(Properties prop,String key) {
		String result = null;
		if(prop != null){
			result = prop.getProperty(key, "");
		}
		return result;
	}
}
