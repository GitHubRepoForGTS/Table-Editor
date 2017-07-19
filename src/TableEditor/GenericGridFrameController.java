package TableEditor;


import org.openswing.swing.table.client.GridController;
import java.util.*;

import org.openswing.swing.export.java.ExportOptions;
import org.openswing.swing.message.receive.java.*;

import java.lang.reflect.Method;
import java.sql.*;

import org.openswing.swing.table.java.GridDataLocator;

import org.openswing.swing.mdi.client.MDIFrame;
import java.awt.Color;
import org.openswing.swing.server.QueryUtil;

import org.openswing.swing.message.send.java.GridParams;


public class GenericGridFrameController extends GridController implements GridDataLocator {
	  /**
	 * 
	 */
	private final  int MAXROWS = 500;
	private static final long serialVersionUID = 1288228662195032856L;
	private GenericGridFrame grid = null;
	private Connection conn = null;
	private DBTable table = null;
	private Map attribute2dbField = null;
	private String selectSql = "";
	private HashMap copyBuffer = null;
	
	public HashMap getCopyBuffer() {
		return copyBuffer;
	}


	public void setCopyBuffer(HashMap copyBuffer) {
		this.copyBuffer = copyBuffer;
	}


	public DBTable getTable() {
		return table;
	}


	public void setTable(DBTable table) {
		this.table = table;
	}


	public GenericGridFrameController(Connection conn,DBTable table) {
	    this.conn = conn;	    
	    this.table = table;
	    DBColumn col = null;
	    
	    
	    attribute2dbField = new HashMap();
	    StringBuffer sb = new StringBuffer();
    	sb.append("SELECT ");
	    for(int i=0; i < this.table.getColumns().size()-1;i++){
	    	col = (DBColumn)this.table.getColumns().get(i);
	    	sb.append(col.getDbColName() + ",");
	    	attribute2dbField.put(col.getMapvo(), col.getDbColName());
	    }
	    col = (DBColumn)this.table.getColumns().get(this.table.getColumns().size()-1);
	    sb.append(col.getDbColName() + " FROM " + this.table.getTbName());
	    attribute2dbField.put(col.getMapvo().toLowerCase(), col.getDbColName());
	    selectSql = sb.toString();
	    grid = new GenericGridFrame(conn,this);
	    MDIFrame.add(grid);
	    
	  }


	  /**
	   * Callback method invoked when the user has double clicked on the selected row of the grid.
	   * @param rowNumber selected row index
	   * @param persistentObject v.o. related to the selected row
	   */
	  public void doubleClick(int rowNumber,ValueObject persistentObject) {
		  GenericGridVO vo = (GenericGridVO)persistentObject;
		  grid.setGridEditable();
	  }


	  /**
	   * Callback method invoked to load data on the grid.
	   * @param action fetching versus: PREVIOUS_BLOCK_ACTION, NEXT_BLOCK_ACTION or LAST_BLOCK_ACTION
	   * @param startPos start position of data fetching in result set
	   * @param filteredColumns filtered columns
	   * @param currentSortedColumns sorted columns
	   * @param currentSortedVersusColumns ordering versus of sorted columns
	   * @param valueObjectType v.o. type
	   * @param otherGridParams other grid parameters
	   * @return response from the server: an object of type VOListResponse if data loading was successfully completed, or an ErrorResponse onject if some error occours
	   */
	  public Response loadData(
	      int action,
	      int startIndex,
	      Map filteredColumns,
	      ArrayList currentSortedColumns,
	      ArrayList currentSortedVersusColumns,
	      Class valueObjectType,
	      Map otherGridParams) {		  
		  try {
			  ArrayList vars = new ArrayList();	      
			  GridParams tmpGridParams = new GridParams(
	              action,
	              startIndex,
	              filteredColumns,
	              currentSortedColumns,
	              currentSortedVersusColumns,
	              otherGridParams
	            );
			  return DB2QueryUtil.getQuery(
					  conn,
					  selectSql,
					  vars,
					  attribute2dbField,
					  GenericGridVO.class, 
					  "Y",
					  "N",
					  tmpGridParams,
					  this.MAXROWS,
					  true
			  );
			  

	    }
	    catch (Exception ex) {
	      ex.printStackTrace();
	      return new ErrorResponse(ex.getMessage());
	    }

	/*
	    // an alternative way: you can define your own business logic to retrieve data and adding filtering/sorting conditions at hand...
	    PreparedStatement stmt = null;
	    try {
	      String sql = "select EMP.EMP_CODE,EMP.FIRST_NAME, EMP.LAST_NAME,EMP.DEPT_CODE,DEPT.DESCRIPTION from EMP,DEPT where EMP.DEPT_CODE=DEPT.DEPT_CODE";
	      Vector vals = new Vector();
	      if (filteredColumns.size()>0) {
	        FilterWhereClause[] filter = (FilterWhereClause[])filteredColumns.get("deptCode");
	        sql += " and EMP.DEPT_CODE "+ filter[0].getOperator()+"?";
	        vals.add(filter[0].getValue());
	        if (filter[1]!=null) {
	          sql += " and EMP.DEPT_CODE "+ filter[1].getOperator()+"?";
	          vals.add(filter[1].getValue());
	        }
	      }
	      if (currentSortedColumns.size()>0) {
	        sql += " ORDER BY EMP.FIRST_NAME "+currentSortedVersusColumns.get(0);
	      }

	      stmt = conn.prepareStatement(sql);
	      for(int i=0;i<vals.size();i++)
	        stmt.setObject(i+1,vals.get(i));

	      ResultSet rset = stmt.executeQuery();


	      ArrayList list = new ArrayList();
	      GridEmpVO vo = null;
	      while (rset.next()) {
	        vo = new GridEmpVO();
	        vo.setEmpCode(rset.getString(1));
	        vo.setFirstName(rset.getString(2));
	        vo.setLastName(rset.getString(3));
	        vo.setDeptCode(rset.getString(4));
	        vo.setDeptDescription(rset.getString(5));
	        list.add(vo);
	      }
	      return new VOListResponse(list,false,list.size());
	    }
	    catch (SQLException ex) {
	      ex.printStackTrace();
	      return new ErrorResponse(ex.getMessage());
	    }
	    finally {
	      try {
	        stmt.close();
	      }
	      catch (SQLException ex1) {
	      }
	    }
	*/

	  }


	  /**
	   * Method invoked when the user has clicked on delete button and the grid is in READONLY mode.
	   * @param persistentObjects value objects to delete (related to the currently selected rows)
	   * @return an ErrorResponse value object in case of errors, VOResponse if the operation is successfully completed
	   */
	  public Response deleteRecords(ArrayList persistentObjects) throws Exception {
		  PreparedStatement stmt = null;
		  String sqlString = null;
		  StringBuffer sb = null;
		  for(int i = 0; i< persistentObjects.size();i++){
			  sb = new StringBuffer();
		      sb.append("DELETE FROM ");
		      sb.append(table.getTbName());	
			  sb.append(buildWhereSQL((GenericGridVO)persistentObjects.get(i)));
			  sqlString = sb.toString();
			  try {
			      stmt = conn.prepareStatement(sqlString);			      
			      stmt.execute();			     			      
			   }catch (SQLException ex) {
			      ex.printStackTrace();
			      return new ErrorResponse(ex.getMessage());
			    }
			    finally {
			      try {
			        stmt.close();
			        conn.commit();
			      }
			      catch (SQLException ex1) {
			      }
			    }
			    
		  }
		  return new VOResponse(new Boolean(true));
	    }
	  private Method findGetter(String mapvo){
		  String attributeName = mapvo;
	      Method getter = null;
	      Class clazz = GenericGridVO.class;
	      int loc = 0;
	      Method[] methods = clazz.getMethods();
	      for(int i = 0; i < methods.length; i++){	    	  
	    	  loc = methods[i].getName().toUpperCase().indexOf(("get"+ attributeName).toUpperCase());
	    	  //System.out.println(methods[i].getName().toUpperCase() + " ?= " + ("get"+ attributeName).toUpperCase() + " ==> " + loc );
	    	  if(loc > -1){
	    		  getter = methods[i];
	    		  break;
	    	  }
		  }
	      return getter;
	  }
	  /**
	   * Method used to define the background color for each cell of the grid.
	   * @param rowNumber selected row index
	   * @param attributedName attribute name related to the column currently selected
	   * @param value object contained in the selected cell
	   * @return background color of the selected cell
	   */
	  public Color getBackgroundColor(int row,String attributedName,Object value) {
         
		  /*  This is how we can use colors on specific columns
		  if (attributedName.equals("deptCode")) {
	      if (value.equals("SF"))
	        return new Color(255,100,100);
	      else if (value.equals("S"))
	        return new Color(210,100,100);
	      else if (value.equals("P"))
	        return new Color(170,100,100);
	    }
	    */
	    return super.getBackgroundColor(row,attributedName,value);
	  }


	  public Response updateRecords(int[] rowNumbers,ArrayList oldPersistentObjects,ArrayList persistentObjects) throws Exception {		  
	    HashSet pk = new HashSet();    
	    Response res = null;
	    GenericGridVO oldVO = null;
	    GenericGridVO newVO = null;
	    for(int i=0;i<persistentObjects.size();i++) {
	      oldVO = (GenericGridVO)oldPersistentObjects.get(i);
	      newVO = (GenericGridVO)persistentObjects.get(i);
	      res = QueryUtil.updateTable(conn,pk,oldVO,newVO,this.table.getTbName(),attribute2dbField,"Y","N",true);
	      if (res.isError()) {
	        conn.rollback();
	        return res;
	      }
	    }
	    conn.commit();
	    return new VOListResponse(persistentObjects,false,persistentObjects.size());
	  }

	  
	  /**
	   * Method invoked when the user has clicked on save button and the grid is in INSERT mode.
	   * @param rowNumbers row indexes related to the new rows to save
	   * @param newValueObjects list of new value objects to save
	   * @return an ErrorResponse value object in case of errors, VOListResponse if the operation is successfully completed
	   */
	  public Response insertRecords(int[] rowNumbers, ArrayList newValueObjects) throws Exception {	  	    
		Response res = QueryUtil.insertTable(conn,newValueObjects,this.table.getTbName(),attribute2dbField,"Y","N",true);
	    if (res.isError())
	      conn.rollback();
	    else
	      conn.commit();
	    return res;

	/*
	    // an alternative way: you can define your own business logic to store data at hand...
	    PreparedStatement stmt = null;
	    try {
	      stmt = conn.prepareStatement("insert into TASKS(TASK_CODE,DESCRIPTION,STATUS) values(?,?,?)");
	      TaskVO vo = (TaskVO)newValueObjects.get(0);
	      stmt.setString(1,vo.getTaskCode());
	      stmt.setString(2,vo.getDescription());
	      stmt.setString(3,"E");
	      stmt.execute();
	      return new VOListResponse(newValueObjects,false,newValueObjects.size());
	    }
	    catch (SQLException ex) {
	      ex.printStackTrace();
	      return new ErrorResponse(ex.getMessage());
	    }
	    finally {
	      try {
	        stmt.close();
	        conn.commit();
	      }
	      catch (SQLException ex1) {
	      }
	    }
	*/
	  }

	  /**
	   * Callback method invoked when the user has clicked on the insert button
	   * @param valueObject empty value object just created: the user can manage it to fill some attribute values
	   */
	  public void createValueObject(ValueObject valueObject) throws Exception {
		  GenericGridVO vo = (GenericGridVO)valueObject;
		//  This function may be useful for something as you work through the code.
	  }
	  
	  public Response PasteRecord(GenericGridVO currentVO){
		  DBColumn col = null;
		  PreparedStatement stmt = null; 
		  String DBColName = "";
		  String DBColValue = "";
		  StringBuffer sb = new StringBuffer();
		  sb.append("UPDATE ").append(table.getTbName()).append(" SET ");
		  int i = 0;
		  if(this.copyBuffer!= null){
			  Iterator it = this.copyBuffer.keySet().iterator();
			  while(it.hasNext()){
				  DBColName = it.next().toString();
				  col = getDBColumn(DBColName);
				  if(copyBuffer.get(DBColName)!= null)
					  DBColValue = copyBuffer.get(DBColName).toString();
				  else
					  DBColValue = null;
				  sb.append(DBColName).append(" = ");
				  if(col.getDbDataType().isDecimal() || col.getDbDataType().isInteger()||DBColValue == null){
					  sb.append(DBColValue);
				  }else{
					  sb.append("'").append(DBColValue).append("' ");
				  }
				  if(i < copyBuffer.size()-1)
					  sb.append(", ");
				  else
					  sb.append(" ");
				  i++;
			  }
			  sb.append(buildWhereSQL(currentVO));
			  System.out.println(sb.toString());
			  try{
				  stmt = conn.prepareStatement(sb.toString());
				  stmt.execute();
				  
			  }catch(Exception ex){
				  if(ex.getMessage().indexOf("SQLCODE=-803")>0){
					  return new ErrorResponse("You can not paste a key column.\n" +ex.getMessage());
				  }
				  return new ErrorResponse(ex.getMessage());
			  }finally {
			      try{
				      	stmt.close();
				        conn.commit();
				        grid.getGrid().getTable().reload();
				      }
				      catch (SQLException ex1) {
				      }
			  }
		  }else{
			  System.out.println("No Column selected.");
		  }
		  return new VOResponse(this.copyBuffer);		  		 
	  }
	  private DBColumn getDBColumn(String colName){
		  DBColumn col = null;
		  Iterator it = table.getColumns().iterator();
		  while(it.hasNext()) {
			  col = (DBColumn)it.next();
			  if(col.getDbColName().equalsIgnoreCase(colName)){
				  break;
			  }
		  }
		  return col;
	  }
	  private String buildWhereSQL(GenericGridVO vo){
		  ArrayList keyCols = null;
		  DBColumn keyCol = null;
	      Method getter = null;
	      String  value = null;
	      StringBuffer sb = new StringBuffer();
	      sb.append(" WHERE ");
	      keyCols = table.getPkeyColumns();
	      if(keyCols.size()==0){
	    	  keyCols = table.getColumns();
	      }
	      try{
	    	  for(int j = 0; j < keyCols.size();j++){
	    		  keyCol = (DBColumn)keyCols.get(j);
	    		  getter = findGetter(keyCol.getMapvo());
	    		  if(getter != null){
	    			  value = getter.invoke(vo, null).toString();
					  DataType dt = keyCol.getDbDataType();
					  if(dt.isString() || dt.isDate()){
						  sb.append(keyCol.getDbColName()).append(" = '").append(value).append("' ");						  					
					  }else if(dt.isDecimal()||dt.isInteger()){
						  sb.append(keyCol.getDbColName()).append(" = ").append(value);
					  }else{
						  System.out.println("Un-Supported data Type. from delete deleteRecords");
					  }
					  if(j < keyCols.size() - 1){
						  sb.append(" AND ");
					  }
				  }
			  }
	      }catch(Exception ex){
	    	  System.out.println(ex.getMessage());
	      }
	      return sb.toString();
	  }
	  /**
	   * Callback method invoked by grid before showing exporting dialog;
	   * this method can be overrided to redefine document formats allowed for the grid
	   * @return list of available formats; possible values: ExportOptions.XLS_FORMAT, ExportOptions.CSV_FORMAT1, ExportOptions.CSV_FORMAT2, ExportOptions.XML_FORMAT, ExportOptions.XML_FORMAT_FAT, ExportOptions.HTML_FORMAT, ExportOptions.PDF_FORMAT, ExportOptions.RTF_FORMAT; default value: ClientSettings.EXPORTING_FORMATS
	   */
	  public String[] getExportingFormats() {
		  String[] formates = {
			    	ExportOptions.XLS_FORMAT,
			        ExportOptions.CSV_FORMAT1,
			        ExportOptions.CSV_FORMAT2};
	    return formates;
	  }


	}
