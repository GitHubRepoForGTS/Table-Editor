package TableEditor;

import org.openswing.swing.mdi.client.*;
import java.sql.Connection;
import java.util.ArrayList;

/**
 * <p>Title: OpenSwing Framework</p>
 * <p>Description: Client Facade, called by the MDI Tree.</p>
 * <p>Copyright: Copyright (C) 2006 Mauro Carniel</p>
 * <p> </p>
 * @author Mauro Carniel
 * @version 1.0
 */
public class TableEditorFacade implements ClientFacade {

  private Connection conn = null;
  private ArrayList tables = null;
  public TableEditorFacade(Connection conn) {
	    this.conn = conn;
	    this.tables = null;
	  }
  public TableEditorFacade(Connection conn,ArrayList tbs) {
    this.conn = conn;
    this.tables = tbs;
  }
  
  public void getGenericTable(String functionId){
	  System.out.println("FunctionId = " +functionId);
	  DBTable tb = null;
	  for(int i = 0;i<this.tables.size();i++){
		  tb = (DBTable)tables.get(i);
		  if(tb.getTbName().equalsIgnoreCase(functionId)){
			  new GenericGridFrameController(conn,tb);
			  break;
		  }
	  }
	  
  }
  public void getGenericTable01() {
	new GenericGridFrameController(conn,null);
  }
public ArrayList getTableName() {
	return tables;
}
public void setTableName(ArrayList tbs) {
	this.tables = tbs;
}
  

//  You will have to create many of these functions.  Just create a about 50 of them even if we will never use them all.  This 
//  is a required class with the open swing MDI framework. 
  
}
