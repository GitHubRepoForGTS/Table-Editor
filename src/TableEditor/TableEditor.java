package TableEditor;



import java.util.*;
import java.util.Date;

import org.openswing.swing.mdi.client.*;
import org.openswing.swing.util.client.ClientSettings;
import org.openswing.swing.util.client.ClientUtils;
import org.openswing.swing.permissions.client.*;

import javax.swing.*;

import org.openswing.swing.domains.java.Domain;
import org.openswing.swing.export.java.ExportOptions;
import org.openswing.swing.internationalization.java.EnglishOnlyResourceFactory;
import org.openswing.swing.internationalization.java.Language;

import javax.swing.text.Utilities;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.openswing.swing.mdi.java.ApplicationFunction;


import java.applet.Applet;
import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.sql.*;

import org.openswing.swing.table.profiles.client.FileGridProfileManager;
import org.openswing.swing.tree.java.OpenSwingTreeNode;
import org.openswing.swing.lookup.client.LookupController;
import org.openswing.swing.permissions.java.CryptUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;



public class TableEditor extends ClientUtils implements MDIController,LoginController {

  /**
	 * 
	 */
	private static final long serialVersionUID = 6102647719580742848L;	
	private String DB2Version = "";
	private TableEditorFacade clientFacade = null;
	private Connection conn = null;
	private static final String NEWLINE = System.getProperty("line.separator");	
	public static final String Version = "1.0.0" + NEWLINE + "Last Modified:" + "2011-12-06";
	private static final String TABLE_TAG = "table";
	private static final String COLUMN_TAG = "column";
	private static final String COLUMN_TITLE = "title";
	private static final String COLUMN_DB2 = "db2column";
	private static final String COLUMN_MAPVO = "mapvo";
	private static final String DATA_TYPE = "datatype";
	private static final String NAME = "name";
	private static final String NUMBER = "number";
	private static final String MAX_LENGTH = "maxLength";
	private static final String DATE_FORMAT = "dateFormat";
	private static final String TIME_FORMAT = "timeFormat"; 
	private static final String MIN_VALUE = "minValue";
	private static final String MAX_VALUE = "maxValue";
	//  XML attribute (dataType) values
	private static final String STRING_TYPE = "STRING";
	private static final String INTEGER_TYPE = "INTEGER";
	private static final String DECIMAL_TYPE = "DECIMAL";
	private static final String DATE_TYPE = "DATE";
	private static final String TIME_TYPE = "TIME";
	private static final String TIMESTAMP_TYPE = "TIMESTAMP";
	private static final int MAX_LENGTH_INDICATOR = 0;
	private static final float MIN_VALUE_INDICATOR = 0;
	private static final float MAX_VALUE_INDICATOR = 0;
	private static final String YES_VALUE = "Y";
	private static final String NO_VALUE = "N";
	private static final String IS_BOOL = "isBool";
	private static final String TIMESTAMP_FORMAT = "timestampFromat";
	private ArrayList tables = null;
	private Properties sysProperties = new Properties();
	private String connection_url;
	private JDialog login = null;
	
	public Properties getSysProperties() {
		if(sysProperties == null){
			sysProperties = Utility.loadSystemProperties();
		}
		return sysProperties;
	}

	public void setSysProperties(Properties sysProperties) {
		this.sysProperties = sysProperties;
	}

	public String getConnection_url() {
		return connection_url;
	}

	public void setConnection_url(String connectionUrl) {
		connection_url = connectionUrl;
	}

	public String getDB2Driver() {
		return DB2Driver;
	}

	public void setDB2Driver(String dB2Driver) {
		DB2Driver = dB2Driver;
	}

	private String DB2Driver;
  public TableEditor() {
	  sysProperties = Utility.loadSystemProperties();
	  //  The connection URL is built from the java properties file that is included in the jar.  It is called
	  //  LSRApplt.properties
	  connection_url = "jdbc:db2://" + sysProperties.getProperty("SERVER_HOST") + ":" + sysProperties.getProperty("DB2_PORT") + "/" + sysProperties.getProperty("DATABASE_NAME");
	  DB2Driver = sysProperties.getProperty("CONNECTION_DRIVER");
   }
  
  public void start(){
	  sysProperties = Utility.loadSystemProperties();
	  //  The connection URL is built from the java properties file that is included in the jar.  It is called
	  //  LSRApplt.properties
	  connection_url = "jdbc:db2://" + sysProperties.getProperty("SERVER_HOST") + ":" + sysProperties.getProperty("DB2_PORT") + "/" + sysProperties.getProperty("DATABASE_NAME");
	  DB2Driver = sysProperties.getProperty("CONNECTION_DRIVER");
	  Container top=getContentPane();
		if(top!=null){
			while( top.getParent() != null ){
				System.out.println("top=" + top.toString());
			  top = top.getParent();
			}
			top.setVisible(false);
		}
	  //Window win = ClientUtils.getParentWindow((JComponent)this.getParent());
	  //System.out.println("top window = " + win.toString() );
	  //win.setVisible(false);
	  login = new EditorLoginDialog(null,
				"Table Editor Login",
			    false,
			    this
			    );
	  login.setVisible(true);
  }
 
/**
   * Method called after MDI creation.
   */
  public void afterMDIcreation(MDIFrame frame) {
    MDIFrame.addStatusComponent(new Clock());
    }

  
  /**
   * @see JFrame getExtendedState method
   */
  public int getExtendedState() {
    return JFrame.NORMAL;
  }


  /**
   * @return client facade, invoked by the MDI Frame tree/menu
   */
  public ClientFacade getClientFacade() {
    return clientFacade;
  }


  /**
   * Method used to destroy application.
   */
  public void stopApplication() {
    System.exit(0);
  }


  /**
   * Defines if application functions must be viewed inside a tree panel of MDI Frame.
   * @return <code>true</code> if application functions must be viewed inside a tree panel of MDI Frame, <code>false</code> no tree is viewed
   */
  public boolean viewFunctionsInTreePanel() {
    return true;
  }


  /**
   * Defines if application functions must be viewed in the menubar of MDI Frame.
   * @return <code>true</code> if application functions must be viewed in the menubar of MDI Frame, <code>false</code> otherwise
   */
  public boolean viewFunctionsInMenuBar() {
    return true;
  }


  /**
   * @return <code>true</code> if the MDI frame must show a login menu in the menubar, <code>false</code> no login menu item will be added
   */
  public boolean viewLoginInMenuBar() {
    return true;
  }


  /**
   * @return application title
   */
  public String getMDIFrameTitle() {
    return "Table Editor";
  }


  /**
   * @return text to view in the about dialog window
   */
  public String getAboutText() {
    return
    "Table Editor" + NEWLINE + NEWLINE + "Version: " +
	TableEditor.Version+ NEWLINE + NEWLINE + "Currrently connected to: " + sysProperties.getProperty("DATABASE_NAME") + NEWLINE	+
	"Database: " + this.DB2Version;
  }


  /**
   * @return image name to view in the about dialog window
   */
  public String getAboutImage() {
    return "about.jpg";
  }


  /**
   * @param parentFrame parent frame
   * @return a dialog window to logon the application; the method can return null if viewLoginInMenuBar returns false
   */
  public JDialog viewLoginDialog(JFrame parentFrame) {
	//Container c = getContentPane();		
	this.setVisible(false);	
  	login = new EditorLoginDialog(null,
			"Table Editor Login",
		    false,
		    this
		    );
	//c.add (login); 
	login.setVisible(true);
	
	return null;
  }


  /**
   * @return maximum number of failed login
   */
  public int getMaxAttempts() {
    return 3;
  }
  
  
  /**
   * Method called by MDI Frame to authenticate the user.
   * @param loginInfo login information, like username, password, ...
   * @return <code>true</code> if user is correcly authenticated, <code>false</code> otherwise
   */
  public boolean authenticateUser(Map loginInfo) throws Exception {
	    String username = loginInfo.get("username").toString().toUpperCase();
	    String password = "";
//		try {
//			password = CryptUtils.getInstance().decodeText((byte[]) loginInfo.get("password"));
//		} catch (Throwable e) {
//			// TODO Auto-generated catch block
//			//e.printStackTrace();
//		}
	    password = loginInfo.get("password").toString();
		DBConnection DBConnection = null;
		String errMsgSQL;
		errMsgSQL = "";
		boolean authenticated = false;
		
		

		// use variable id and psw
		DBConnection = new DBConnection(username, password, connection_url, DB2Driver);
		
		// Hard coded id and psw
        //DBConnection = new DBConnection("lsrdev", "sep06sep", connection_url, DB2Driver);	
		
			if(DBConnection.ConnectDB()){
				conn = DBConnection.getConnection();
				conn.setAutoCommit(false);
			    //clientFacade = new TableEditorFacade(conn);
				authenticated = true;
				this.DB2Version = DBConnection.getDB2Version();
			}else{
				//  The following block of code will catch the password expired exception.  Since DB2 JDBC
				//  can not return a specific error code for this then we have to parse the error message.
				errMsgSQL = DBConnection.getErrorMessage();					
				if ((errMsgSQL.toLowerCase().indexOf("password")>-1) & (errMsgSQL.toLowerCase().indexOf("expired")>-1)) {
					JOptionPane.showMessageDialog(null,
							"Your password has expired and must be changed." + NEWLINE + NEWLINE +
							DBConnection.PASSWORD_RULES, "Password Expired", JOptionPane.INFORMATION_MESSAGE);

				//  Password is invalid
				} else if ((errMsgSQL.toLowerCase().indexOf("password")>-1) & (errMsgSQL.toLowerCase().indexOf("invalid")>-1)) {
					JOptionPane.showMessageDialog(null,
							"Your database logon was not recognized." + NEWLINE +
							"(Logon information is case sensitive - be sure to use correct upper and lower case.)" + NEWLINE + NEWLINE +
							"If you have forgotten your password or need to have it reset please see the " + NEWLINE +
							"'Request Access' link on the main site."
							, "Unauthorized Logon", JOptionPane.ERROR_MESSAGE);
				}else{
					JOptionPane.showMessageDialog(null,
							"Your database logon was not recognized." + NEWLINE + errMsgSQL + NEWLINE +
							"(Logon information is case sensitive - be sure to use correct upper and lower case.)" + NEWLINE + NEWLINE +
							"If you have forgotten your password or need to have it reset please see the " + NEWLINE +
							"'Request Access' link on the main site."
							, "Unauthorized Logon", JOptionPane.ERROR_MESSAGE);
					
				}
			}
	  
	return(authenticated);
  }




//  public static void main(String[] argv) {
//    SwingUtilities.invokeLater(new Runnable() {
//      public void run() {
//        new TableEditor();
//      }
//    });
//  }


  /**
   * Method called by LoginDialog to notify the sucessful login.
   * @param loginInfo login information, like username, password, ...
   */
  public void loginSuccessful(Map loginInfo) { 

	  Properties props = new Properties();
	  props.setProperty("text to translate","text to translate");
	  props.setProperty("Change Password","Change Password");
	  Hashtable domains = new Hashtable();
	  Domain boolCombo = new Domain("BOOLCOMBO");
	  boolCombo.addDomainPair(null,"");
	  boolCombo.addDomainPair("Y","Yes");
	  boolCombo.addDomainPair("N","No");
	  domains.put(boolCombo.getDomainId(),boolCombo);
	  login.setVisible(false);
	  
			
	  ClientSettings clientSettings = new ClientSettings(new EnglishOnlyResourceFactory("$",props,false,'-'), domains);

	  ClientSettings.BACKGROUND = "background4.jpg";
	  ClientSettings.TREE_BACK = "treeback1.jpg";
	  ClientSettings.VIEW_BACKGROUND_SEL_COLOR = true;
	  ClientSettings.TREE_BACK = null;
	  ClientSettings.VIEW_MANDATORY_SYMBOL = true;
	  ClientSettings.FILTER_PANEL_ON_GRID = true;
	  ClientSettings.SHOW_FILTER_SYMBOL = true;
	  ClientSettings.ASK_BEFORE_CLOSE = false;
	  ClientSettings.SHOW_TREE_MENU_ROOT = false;
	  ClientSettings.MAX_EXPORTABLE_ROWS = 70000;
      ClientSettings.GRID_PROFILE_MANAGER = new FileGridProfileManager();
      ClientSettings.LOOKUP_FRAME_CONTENT = LookupController.GRID_AND_FILTER_FRAME;
      ClientSettings.STORE_INTERNAL_FRAME_PROFILE = true;
      ClientSettings.AUTO_EXPAND_TREE_MENU = true;
      ClientSettings.AUTO_FIT_COLUMNS = false;
      ClientSettings.VIEW_MANDATORY_SYMBOL = true;
      ClientSettings.FILTER_PANEL_ON_GRID = false;
      //ClientSettings.SHOW_PAGINATION_BUTTONS_ON_NAVBAR = true;
      //ClientSettings.SHOW_PAGE_NUMBER_IN_GRID = false;
	  MDIFrame mdi = new MDIFrame(this);
	  this.getContentPane().add(mdi);
	  
  }



  /**
   * @return application functions (ApplicationFunction objects), organized as a tree
   */
  public DefaultTreeModel getApplicationFunctions() {
    DefaultMutableTreeNode root = new OpenSwingTreeNode("");
    DefaultTreeModel model = new DefaultTreeModel(root);
    ApplicationFunction n1 = new ApplicationFunction("My Tables",null);
    
	//tables = loadTablesFromXML(getXmlFromDB2());
    tables = loadTablesFromXML(getXmlFromDB2());
    Collections.sort(tables);
	clientFacade = new TableEditorFacade(conn,tables);
	String tbName = null;
	for(int i = 0; i < tables.size(); i++){
		//ApplicationFunction t = new ApplicationFunction(((DBTable)tables.get(i)).getTbName(),"nada","men.gif","getGenericTable01");
		tbName = ((DBTable)tables.get(i)).getTbName();
		ApplicationFunction t = new ApplicationFunction(tbName,tbName,"men.gif","getGenericTable");
		n1.add(t);
	}
    root.add(n1);
    return model;
  }
  private String getXmlFromDB2(){
	//  statement to get the period end dates
		String sqlStatement = "SELECT CONFIG FROM QBIS.CONFIGXML";
		Statement stmt = null;
		ResultSet rSet = null;
		String xmlRecord = null;
		try {
			stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
			//  execute the statement
			rSet = stmt.executeQuery(sqlStatement);
			//  while we have results add them to the list
			while( rSet.next() )
			{
				xmlRecord = rSet.getString(1);				
			}
			return xmlRecord;
		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
			return null;
		} finally {
			closeDBResources(stmt, rSet);			
		}
  }
  public ArrayList loadTablesFromXML(String xmlRecords){
	  ArrayList tbs = null,cols = null;
	  DBTable tb =null;
	  DBColumn col = null;
	  Document document=null;
	  String result="";
	  String dataType=null;
	  String dateFormat = null,timeFormat = null,timestampFormat = null;
	  Element element_root;
	  NodeList tbNodes, colNodes,detailNodes;
	  Element tbNode, colNode,detailNode;
	  DataType dataAttributes = null;
	  int maxLength =0;
	  float minValue = 0, maxValue = 0;
	  //  Create DOM document for xml file
	  DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	  //  Turn on XML DTD validation
	  //factory.setValidating(true);

	  //  Create errorHandler to handle any XML parsing errors.  This is an external error handler object/class
	  //  which is part of the lenovo library
	  XMLErrorHandler errorHandler = new XMLErrorHandler();
	  
		//  Load the XML file into an XML document.  The XML doc will be parsed for syntax
		//  and validated against the DTD at this point.
		//  Register errorHandler to builder.  Any parse warning / error will invoke errorHandler
	  try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setErrorHandler(errorHandler);
			InputSource is = new InputSource();
	        is.setCharacterStream(new StringReader(xmlRecords));

			document = builder.parse(is);
	
		//  General XML parse error
	  } catch (SAXParseException spe) {
			result = result + "Application Error: XML parse failed! ";
	
		//  Error generated by this application (or a parser-initialization error)
	  } catch (SAXException sxe) {
			result = result + "Application Error: Incorrect XML format! ";
	
		//  Parser with specified options can't be built
	  } catch (ParserConfigurationException pce) {
			result = result + "Application Error: Parser with specified options cannot be built! ";
	
		//  I/O error
	  } catch (IOException ioe) {
			result = result + "Application Error: XML File not found []";
			System.out.println(System.getProperty("user.dir"));
	  }
	
	  //  If builder.parse above was successful, errorCount and warningCount should be 0
	  
	  if (result.equals("")) {
		  if (errorHandler.getErrorCount() > 0 || errorHandler.getWarningCount() > 0) {
			  SAXException se = errorHandler.getFirstException();
			  result = result + "Application Error: CSV is NOT processed because " + errorHandler.getErrorCount() + " error(s), " + errorHandler.getWarningCount() + " warning(s) are found in XML" + NEWLINE;
			  result = result + "First error is: " + se.getMessage();
		  }
	  }
	  if (result.equals("")) {
			//  Load xml elements from DOM document
			//  If the root node contains the optional CSV File Name Verification String
		  element_root = document.getDocumentElement();
		  tbNodes = element_root.getElementsByTagName(TABLE_TAG);
		  tbs = new ArrayList();
			//  Go through all nodes in the tables.xml
		  for (int i = 0; i < tbNodes.getLength(); i++) {
			  tbNode = (Element)tbNodes.item(i);			  
			  tb = new DBTable();
			  tb.setTbName(tbNode.getAttribute(NAME));
			  cols = new ArrayList();
			  colNodes = tbNode.getElementsByTagName(COLUMN_TAG);
			  for(int j = 0; j < colNodes.getLength(); j++){
				  colNode = (Element)colNodes.item(j);
				  col= new DBColumn(); 
				  if(!colNode.getAttribute("required").equalsIgnoreCase("")){
					  if(colNode.getAttribute("required").equalsIgnoreCase(YES_VALUE)){
						  col.setRequired(true);
					  }else{
						  col.setRequired(false);
					  }
				  }
				  if(!colNode.getAttribute("editable").equalsIgnoreCase("")){
					  if(colNode.getAttribute("editable").equalsIgnoreCase(YES_VALUE)){
						  col.setEditable(true);
					  }else{
						  col.setEditable(false);
					  }
				  }
				  if(!colNode.getAttribute("filterable").equalsIgnoreCase("")){
					  if(colNode.getAttribute("filterable").equalsIgnoreCase(YES_VALUE)){
						  col.setFilterable(true);
					  }else{
						  col.setFilterable(false);
					  }
				  }
				  if(!colNode.getAttribute("sortable").equalsIgnoreCase("")){
					  if(colNode.getAttribute("sortable").equalsIgnoreCase(YES_VALUE)){
						  col.setSortable(true);
					  }else{
						  col.setSortable(false);
					  }
				  }
				  if(!colNode.getAttribute("pkey").equalsIgnoreCase("")){
					  if(colNode.getAttribute("pkey").equalsIgnoreCase(YES_VALUE)){
						  col.setPkey(true);
					  }else{
						  col.setPkey(false);
					  }
				  }
				  
				  
				  detailNodes= colNode.getChildNodes();
				  for(int k = 0 ; k < detailNodes.getLength(); k++){
					  if(detailNodes.item(k).getNodeType()== Node.ELEMENT_NODE){
						  detailNode = (Element)detailNodes.item(k);
						  if(detailNode.getTagName().equalsIgnoreCase(COLUMN_TITLE)){
							  col.setColTitle(detailNode.getAttribute(NAME));  
						  }else if(detailNode.getTagName().equalsIgnoreCase(COLUMN_DB2)){
							  col.setDbColName(detailNode.getAttribute(NAME));
						  }else if(detailNode.getTagName().equalsIgnoreCase(COLUMN_MAPVO)){
							  col.setMapvo(detailNode.getAttribute(NAME).toLowerCase());
						  }else if (detailNode.getTagName().equalsIgnoreCase(DATA_TYPE)) {
							  dataType = detailNode.getAttribute(NAME);
							  //  String
							  if (dataType.equalsIgnoreCase(STRING_TYPE)) {
								  if(!detailNode.getAttribute(IS_BOOL).equalsIgnoreCase("")){										  
									  if(detailNode.getAttribute(IS_BOOL).equalsIgnoreCase(YES_VALUE)){
										  dataAttributes = new DataType(dataType, true);
									  }else{
										  //should never happen
									  }
								  }else if(!detailNode.getAttribute(MAX_LENGTH).equalsIgnoreCase("")){	
									  try {
										  maxLength = Integer.parseInt(detailNode.getAttribute(MAX_LENGTH));
									  }catch (NumberFormatException nfe){
											maxLength = MAX_LENGTH_INDICATOR;
									  }
									  dataAttributes = new DataType(dataType, maxLength);
								  }	
								  
								}
								//  Date
								if (dataType.equalsIgnoreCase(DATE_TYPE)) {
									dateFormat = detailNode.getAttribute(DATE_FORMAT);
									dataAttributes = new DataType(dataType, dateFormat);
								}
								//  Time
								if (dataType.equalsIgnoreCase(TIME_TYPE)) {
									timeFormat = detailNode.getAttribute(TIME_FORMAT);
									dataAttributes = new DataType(dataType, timeFormat);
								}
								
							//  Timestamp
								if (dataType.equalsIgnoreCase(TIMESTAMP_TYPE)) {
									timestampFormat = detailNode.getAttribute(TIMESTAMP_FORMAT);
									dataAttributes = new DataType(dataType, timestampFormat);
								}
								
								//  Integer or Decimal
								if (dataType.equalsIgnoreCase(INTEGER_TYPE) || dataType.equalsIgnoreCase(DECIMAL_TYPE)) {
									try {
										minValue = Float.parseFloat(detailNode.getAttribute(MIN_VALUE));
									} catch (NumberFormatException nfe) {
										minValue = MIN_VALUE_INDICATOR;
									}
									try {
										maxValue = Float.parseFloat(detailNode.getAttribute(MAX_VALUE));
									} catch (NumberFormatException nfe) {
										maxValue = MAX_VALUE_INDICATOR;
									}
									
									dataAttributes = new DataType(dataType, minValue, maxValue);
								}
								if(dataAttributes != null){
									col.setDbDataType(dataAttributes);
								}
							}else{
								System.out.println("Non Supported Data type.");
							}
					  }					 
				  }
				  cols.add(col);
			  }
			  tb.setColumns(cols);
			  tbs.add(tb);
		  }		 
	  }
	  return tbs;
  }
  
  /**
   * @return <code>true</code> if the MDI frame must show a panel in the bottom, containing last opened window icons, <code>false</code> no panel is showed
   */
  public boolean viewOpenedWindowIcons() {
    return true;
  }


  /**
   * @return <code>true</code> if the MDI frame must show the "File" menu in the menubar of the frame, <code>false</code> to hide it
   */
  public boolean viewFileMenu() {
    return true;
  }


  /**
   * @return <code>true</code> if the MDI frame must show a change language menu in the menubar, <code>false</code> no change language menu item will be added
   */
  public boolean viewChangeLanguageInMenuBar() {
    return false;
  }


  /**
   * @return list of languages supported by the application
   */
  public ArrayList getLanguages() {
    ArrayList list = new ArrayList();
    list.add(new Language("EN","English"));
    return list;
  }


	/**
	***************************************************************************************************
	* NAME:  closeDBResources(Statement s, ResultSet r)
	*
	* PURPOSE:  This method is used to close database resources after we are done with them.  This
	* method will close a result set and a statement object together.  Please use this method as soon as
	* we are finished with the objects so that we do not leave any open resources active.  If you do not
	* close all resources then the logout operation will not succeed.  You can not close a database 
	* connection gracefully with out first closing off the open resources.
	*
	* INPUTS:  1.  Statement s - statement object to be closed
	* 		   2.  ResultSet r - result set object to be closed
	*
	* OUTPUTS: 1.  boolean - true if objects closed without error, false otherwise
	*
	* @author Monaco
	*
	***************************************************************************************************
	*/
	public boolean closeDBResources(Statement s, ResultSet r){
		boolean result = true;
	
		//  Close the statement object if it is not null.  A null statement object is not open
		if (s != null) {
			try {
				s.close();
			} catch (SQLException sqlEx) {
				s = null;
				result = false;
			}
		}
		//  Close the result set object if it is not null.  A null statement object is not open
		if (r != null) {
			try {
				r.close();
				} catch (SQLException sqlEx) {
				r = null;
				result = false;
			}
		}

		//  N.B.
		//  Since Auto Commit is turned off in this application we need to commit every operation.
		//  Even a SQL select statement with a read only cursor requires a commit.  If you do not
		//  then the connection object will assume a transaction in progress.

		//  When performing transaction on the database please remember that Auto Commit is turned 
		//  off.  Therefore you must make sure that after every transaction you either 
		//  commit or rollback! 
		try {
			//conn = mainPanel.getConnection();
			if(conn != null){
				conn.commit();
			}else{
				result = false;
				System.out.println("Connection is lost,  please try it again.");
			}
		} catch (SQLException sqlEx) {
			result = false;
		}catch (Exception ex){
			result = false;
		}
		
  	return result;
  }
	
	private static String readFileAsString(String filePath){
	    try{
	    	String curDir = System.getProperty("user.dir");
	    	filePath = curDir + filePath;
	    	byte[] buffer = new byte[(int) new File(filePath).length()];
		    BufferedInputStream f = new BufferedInputStream(new FileInputStream(filePath));
		    f.read(buffer);
		    return new String(buffer);
	    }catch(Exception e){
	    	return new String("");
	    }finally{
	    	
	    }
	}
	
	
	

  
}
