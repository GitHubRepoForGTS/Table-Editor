package TableEditor;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DBConnection {
	
	private static final String NEWLINE = System.getProperty("line.separator");
	public static final String PASSWORD_RULES = "1. A minimum of 8 characters in length" + NEWLINE +
	"2. A minimum of 1 numeric character" + NEWLINE +
	"3. A minimum of 1 alphabetic character" + NEWLINE +
	"4. A minimum of 1 character not found in old password" + NEWLINE +
	"5. Not contain the userid as part of the password " + NEWLINE +
	"6. New password requires a minimum of 1 elapsed week between changes." + NEWLINE +
	"7. Not be reused until after at least eight iterations.";
	
	private Connection conn;
	private String userId;
	private String password;
	private String connectionURL;
	private String connectionDriver;
	private String errorMessage;
	private String DBVersion;
	
	public DBConnection(String userId, String password, String connectionUrl,String DB2Driver){
		this.userId = userId;
		this.password = password;
		this.connectionURL = connectionUrl;
		this.connectionDriver = DB2Driver;
		this.conn = null;
		this.errorMessage = "";
		this.DBVersion = "";
	}
	
	public DBConnection(){
		this.userId = "";
		this.password = "";
		this.connectionURL = "";
		this.connectionDriver = "";
		this.conn = null;
		this.errorMessage = "";
		this.DBVersion = "";
	}
	
	/**
	 ***************************************************************************************************
	 * NAME:  Connection connectDb(String user, char[] password)
	 *
	 * PURPOSE: This routine is used to establish a database connection.  If the connection is not
	 * successful then null is returned as the connection object.  Class not found exceptions are caught and
	 * and printed to console where as SQL exceptions to deal with user name and password are brought to
	 * the users attention in a dialog box.  A password expired exception will trigger the change password
	 * fields to appear on screen.
	 *
	 * N.B.  AutoCommit is set to false.
	 *
	 * INPUTS:  1.  String user - User ID
	 *			2.  String psw - User ID Password   {not encrypted}
	 *			3.  Constants: CONNECTION_DRIVER
	 *
	 * OUTPUTS: 1.  Valid Java connection object or Null if the connection was not successful.
	 *
	 * @author Monaco
	 *
	 ***************************************************************************************************
	 */
	public boolean ConnectDB() {
		boolean returnValue = false;
		this.errorMessage = "";
		//  Try to establish a connection using the input parameters
	    try {
        	Class.forName(this.connectionDriver);
        	//  Put the user name and password into a properties object
        	Properties p = new Properties();
            p.put ("user", this.userId);
            p.put ("password", this.password);            
            
            //  Make connection
        	this.conn = DriverManager.getConnection(this.connectionURL, p);
        	
        	//  Set auto commit to false
        	this.conn.setAutoCommit(false);	  
        	returnValue = true;
        	//throw new SQLException("[IBM][CLI Driver] SQL30082N Attempt to establish connection failed with security reason \"1\" (\"PASSWORD EXPIRED\"). SQLSTATE=08001");
		}

		//  This will catch missing database drivers and display to console
		catch (ClassNotFoundException cnfe) {
			this.errorMessage += "Exception Message:  " + cnfe.getMessage() + NEWLINE;
			this.errorMessage += "    Connection Driver -> " + this.connectionDriver + NEWLINE;
			this.errorMessage += "    Connection URL    -> " + this.connectionURL + NEWLINE;
			this.conn = null;
		}

		//  This will catch SQL exceptions related to connection
		catch (SQLException sqle) {			
			this.errorMessage +=  "Exception Message:  " + sqle.getMessage() + NEWLINE;
			this.errorMessage +=  "    Connection Driver -> " + this.connectionDriver + NEWLINE;
			this.errorMessage +=  "    Connection URL    -> " + this.connectionURL + NEWLINE;
			this.conn = null;
		}
		return(returnValue);
	}
	
	public String getDB2Version(){
		try{
			if(this.conn != null){
				DatabaseMetaData metaData = this.conn.getMetaData();
	        	this.DBVersion = metaData.getDatabaseProductName() + " " + metaData.getDatabaseProductVersion() + "\r\n" + metaData.getDriverName() + ": " + metaData.getDriverVersion();
			}
		}catch(Exception ex){
			this.DBVersion = ex.getMessage();
		}
		return this.DBVersion;
	}
	
	public void setPassword(String pwd){
		this.password = pwd;
	}
	
	public String getErrorMessage(){
		return this.errorMessage;
	}
	
	public Connection getConnection(){
		try{
			//if((this.conn == null) || this.conn.isClosed() ){
			if(!IsValid(this.conn)){
				this.errorMessage = "";
				this.ConnectDB();
			}
		}catch(Exception ex){
			this.conn = null;
			this.errorMessage += ex.getMessage();
		}
		return this.conn;
	}
	private boolean IsValid(Connection conn)
	{
		boolean ret = true;
		Statement stmt;	
		if(conn != null){
			try{
				stmt = conn.createStatement();
				stmt.execute("VALUES(1)");	
				stmt.close();
			}catch(Exception ex){
				ret = false;
			}
		}else{
			ret = false;
		}
		return ret;
	}
	
	public String getUserId(){
		return this.userId;
	}
	
	
}
