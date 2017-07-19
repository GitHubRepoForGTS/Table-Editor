package TableEditor;

import java.lang.reflect.*;
import java.math.*;
import java.sql.*;
import java.util.*;

import javax.servlet.*;

import org.openswing.swing.logger.server.*;
import org.openswing.swing.message.receive.java.*;
import org.openswing.swing.message.send.java.*;
import org.openswing.swing.server.UserSessionParameters;
import org.openswing.swing.util.java.Consts;
import org.openswing.swing.server.*;
import java.io.BufferedInputStream;


/**
 * <p>Title: OpenSwing Framework</p>
 * <p>Description: Utility (singleton) class used with queries.</p>
 * <p>Copyright: Copyright (C) 2006 Mauro Carniel</p>
 *
 * <p> This file is part of OpenSwing Framework.
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the (LGPL) Lesser General Public
 * License as published by the Free Software Foundation;
 *
 *                GNU LESSER GENERAL PUBLIC LICENSE
 *                 Version 2.1, February 1999
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free
 * Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 *       The author may be contacted at:
 *           maurocarniel@tin.it</p>
 *
 * @author Mauro Carniel
 * @version 1.0
 */
public class DB2QueryUtil {

	  /* @param baseSQL SQL to change by adding filter and order clauses
	   * @param values binding values related to baseSQL
	   * @param filteredColumns columns to add in the WHERE clause
	   * @param currentSortedColumns columns to add in the ORDER clause
	   * @param currentSortedVersusColumns ordering versus
	   * @param attributesMapping collection of pairs attributeName, corresponding database column (table.column)
	   * @return baseSQL + filtering and ordering conditions
	   */
	  public static String getSql(
	      UserSessionParameters userSessionPars,
	      String baseSQL,
	      ArrayList values,
	      Map filteredColumns,
	      ArrayList currentSortedColumns,
	      ArrayList currentSortedVersusColumns,
	      Map attributesMapping
	  ) {
	    return QueryUtil.getSql(
	      userSessionPars,
	      baseSQL,
	      new ArrayList(),
	      values,
	      filteredColumns,
	      currentSortedColumns,
	      currentSortedVersusColumns,
	      attributesMapping
	    );
	  }
	  
	 
	/**
	   * This method read a block of record from the result set.
	   * @param baseSQL SQL to change by adding filter and order clauses
	   * @param values binding values related to baseSQL
	   * @param attribute2dbField collection of pairs attributeName, corresponding database column (table.column) - for ALL fields is the select clause
	   * @param valueObjectClass value object class to use to generate the result
	   * @param booleanTrueValue read value to interpret as true
	   * @param booleanFalseValue read value to interpret as false
	   * @param gridParams grid parameters (filtering/ordering settings, starting row to read, read versus)
	   * @param blockSize number of rows to read
	   * @param logQuery <code>true</code> to log the query, <code>false</code> to no log the query
	   * @return a list of value objects  (in VOListResponse object) or an error response
	   */
	  public static Response getQuery(
	      Connection conn,
	      String baseSQL,
	      ArrayList values,
	      Map attribute2dbField,
	      Class valueObjectClass,
	      String booleanTrueValue,
	      String booleanFalseValue,
	      GridParams gridParams,
	      int blockSize,
	      boolean logQuery
	  		) throws Exception {
		    return getQuery(
		      conn,
		      new UserSessionParameters(),
		      baseSQL,
		      values,
		      attribute2dbField,
		      valueObjectClass,
		      booleanTrueValue,
		      booleanFalseValue,
		      null,
		      gridParams,
		      blockSize,
		      1,
		      logQuery
		    );
		  }
	  /**
	   * @param baseSQL SQL to change by adding filter and order clauses
	   * @param values binding values related to baseSQL
	   * @param attribute2dbField collection of pairs attributeName, corresponding database column (table.column) - for ALL fields is the select clause
	   * @param valueObjectClass value object class to use to generate the result
	   * @param booleanTrueValue read value to interpret as true
	   * @param booleanFalseValue read value to interpret as false
	   * @param context servlet context; this may be null
	   * @param gridParams grid parameters (filtering/ordering settings, starting row to read, read versus)
	   * @param blockSize number of rows to read
	   * @param rowsToRead 0 = all rows, 1 = a block of rows, 2 = only one row
	   * @param logQuery <code>true</code> to log the query, <code>false</code> to no log the query
	   * @return a list of value objects or an error response
	   */
	  private static Response getQuery(
	      Connection conn,
	      UserSessionParameters userSessionPars,
	      String baseSQL,
	      ArrayList values,
	      Map attribute2dbField,
	      Class valueObjectClass,
	      String booleanTrueValue,
	      String booleanFalseValue,
	      ServletContext context,
	      GridParams gridParams,
	      int blockSize,
	      int rowsToRead,
	      boolean logQuery
	  ) throws Exception {
	    baseSQL = getSql(
	      userSessionPars,
	      baseSQL,
	      values,
	      gridParams.getFilteredColumns(),
	      gridParams.getCurrentSortedColumns(),
	      gridParams.getCurrentSortedVersusColumns(),
	      attribute2dbField
	    );
	    return getQuery(
	      conn,
	      userSessionPars,
	      baseSQL,
	      null,
	      null,
	      null,
	      null,
	      null,
	      null,
	      values,
	      attribute2dbField,
	      valueObjectClass,
	      booleanTrueValue,
	      booleanFalseValue,
	      context,
	      blockSize,
	      rowsToRead,
	      logQuery,
	      gridParams.getAction(),
	      gridParams.getStartPos(),
	      false
	    );
	  }


 //@@@@@@@@@@@@@@@@@@@
 /**
  * @param baseSQL SQL that already contains filtering and sorting conditions
  * @param values binding values related to baseSQL
  * @param attribute2dbField collection of pairs attributeName, corresponding database column (table.column) - for ALL fields is the select clause
  * @param valueObjectClass value object class to use to generate the result
  * @param booleanTrueValue read value to interpret as true
  * @param booleanFalseValue read value to interpret as false
  * @param context servlet context; this may be null
  * @param blockSize number of rows to read
  * @param rowsToRead 0 = all rows, 1 = a block of rows, 2 = only one row
  * @param logQuery <code>true</code> to log the query, <code>false</code> to no log the query
  * @param fetchTotalResultSetLength fetch all result set length; be careful: this task could slow down the data retrieval
  * @return a list of value objects or an error response
  */
 private static Response getQuery(
     Connection conn,
     UserSessionParameters userSessionPars,
     String baseSQL,
     String select,
     String from,
     String where,
     String group,
     String having,
     String order,
     ArrayList values,
     Map attribute2dbField,
     Class valueObjectClass,
     String booleanTrueValue,
     String booleanFalseValue,
     ServletContext context,
     int blockSize,
     int rowsToRead,
     boolean logQuery,
     int action,
     int startPos,
     boolean fetchTotalResultSetLength
 ) throws Exception {
   PreparedStatement pstmt = null;
   String params = "";
   if (baseSQL==null)
     baseSQL =
       "SELECT "+select+" "+
       "FROM "+from+" "+
       (where==null  || where.equals("") ?"":("WHERE "+where+" "))+
       (group==null  || group.equals("") ?"":("GROUP BY "+group+" "))+
       (having==null || having.equals("")?"":("HAVING "+having+" "))+
       (order==null  || order.equals("") ?"":("ORDER BY "+order));
   else
     select = baseSQL.substring(baseSQL.toLowerCase().indexOf("select ")+7,baseSQL.toLowerCase().indexOf(" from ")).replace('\n',' ').replace('\r',' ').trim();
   try {

     // prepare the collection of pairs database column (table.column), attributeName - for ALL fields is the select clause
     Iterator it = attribute2dbField.keySet().iterator();
     String attributeName = null;
     HashMap field2Attribute = new HashMap();
     while(it.hasNext()) {
       attributeName = it.next().toString();
       field2Attribute.put(attribute2dbField.get(attributeName),attributeName);
     }
     // prepare the SQL statement...
     long t1 = System.currentTimeMillis();
     pstmt = conn.prepareStatement(baseSQL);
     //pstmt = conn.prepareStatement(baseSQL,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT);
     for(int i=0;i<values.size();i++) {
       if (values.get(i)!=null && values.get(i).getClass().getName().equals("java.util.Date"))
         values.set(i,new java.sql.Date(((java.util.Date)values.get(i)).getTime()));
       pstmt.setObject(i+1,values.get(i));
     }
     ResultSet rset = pstmt.executeQuery();
     long t2 = System.currentTimeMillis();

     // prepare setter methods of the v.o...
     ArrayList cols = getColumns(select);
     Method[] setterMethods = new Method[cols.size()];
     Method getter = null;
     Method setter = null;
     String aName = null;
     ArrayList[] getters = new ArrayList[cols.size()];
     ArrayList[] setters = new ArrayList[cols.size()];
     Class clazz = null;
     for(int i=0;i<cols.size();i++) {
       attributeName = (String)field2Attribute.get(cols.get(i));
       if (attributeName==null) {
         return new ErrorResponse("Attribute not found in 'attribute2dbField' argument for database field '"+cols.get(i)+"'");
       }

       aName = attributeName;
       getters[i] = new ArrayList(); // list of Methods objects (getters for accessing inner vos)
       setters[i] = new ArrayList(); // list of Methods objects (setters for instantiating inner vos)
       clazz = valueObjectClass;

       // check if the specified attribute is a composed attribute and there exist inner v.o. to instantiate...
       while(aName.indexOf(".")!=-1) {
         try {
           getter = clazz.getMethod(
             "get" +
             aName.substring(0, 1).
             toUpperCase() +
             aName.substring(1,aName.indexOf(".")),
             new Class[0]
           );
         }
         catch (NoSuchMethodException ex2) {
           getter = clazz.getMethod("is"+aName.substring(0,1).toUpperCase()+aName.substring(1,aName.indexOf(".")),new Class[0]);
         }
         setter = clazz.getMethod("set"+aName.substring(0,1).toUpperCase()+aName.substring(1,aName.indexOf(".")),new Class[]{getter.getReturnType()});
         aName = aName.substring(aName.indexOf(".")+1);
         clazz = getter.getReturnType();
         getters[i].add(getter);
         setters[i].add(setter);
       }

       try {
         getter = clazz.getMethod(
           "get" +
           aName.substring(0, 1).
           toUpperCase() +
           aName.substring(1),
           new Class[0]
         );
       }
       catch (NoSuchMethodException ex2) {
         getter = clazz.getMethod("is"+aName.substring(0,1).toUpperCase()+aName.substring(1),new Class[0]);
       }

       setterMethods[i] = clazz.getMethod("set"+aName.substring(0,1).toUpperCase()+aName.substring(1),new Class[]{getter.getReturnType()});
     }

     int rowCount = 0;
     int resultSetLength = -1;
     int totalResultSetLength = -1;
     if (rowsToRead==1) {
       // read a block of rows...

       if (action==GridParams.LAST_BLOCK_ACTION) {
         try {
           rset.last();
           resultSetLength = rset.getRow();
           totalResultSetLength = resultSetLength;
           rset.afterLast();
         }
         catch (SQLException ex4) {
           // last & getRow methods not supported!
           while(rset.next())
             rowCount++;
           resultSetLength = rowCount;
           totalResultSetLength = resultSetLength;
           action = GridParams.NEXT_BLOCK_ACTION;
           startPos = Math.max(rowCount-blockSize,0);
           rowCount = 0;
           rset.close();
           rset = pstmt.executeQuery();
           int i=0;
           while(i<startPos) {
             rset.next();
             i++;
           }
         }
       } else {
         try {
           rset.absolute(startPos);
         }
         catch (SQLException ex3) {
           if (action==GridParams.PREVIOUS_BLOCK_ACTION) {
             action = GridParams.NEXT_BLOCK_ACTION;
             startPos = Math.max(startPos-blockSize,0);
           }
           // absolute method not supported!
           int i=0;
           while(i<startPos) {
             rset.next();
             i++;
           }
         }
       }
     }
     else if (rowsToRead==0) {
       // load all rows...
       action = GridParams.NEXT_BLOCK_ACTION;
     }

     ArrayList list = new ArrayList();
     Object value = null;
     Object vo = null;
     while (
         action==GridParams.LAST_BLOCK_ACTION && rset.previous() ||
         action==GridParams.NEXT_BLOCK_ACTION && rset.next() ||
         action==GridParams.PREVIOUS_BLOCK_ACTION && rset.previous()) {
       rowCount++;

       vo = valueObjectClass.newInstance();
       Object currentVO = null;
       Object innerVO = null;
       for(int i=0;i<cols.size();i++) {
         currentVO = vo;
         for(int j=0;j<getters[i].size();j++) {
           if (((Method)getters[i].get(j)).invoke(currentVO,new Object[0])==null) {
             innerVO = ((Method)getters[i].get(j)).getReturnType().newInstance();  // instantiate the inner v.o.
             ((Method)setters[i].get(j)).invoke(currentVO,new Object[]{ innerVO });
             currentVO = innerVO;
           }
           else
             currentVO = ((Method)getters[i].get(j)).invoke(currentVO,new Object[0]);
         }

         Class parType = setterMethods[i].getParameterTypes()[0];
         if (parType.equals(String.class))
           value = rset.getString(i+1);
         else if (parType.equals(Boolean.class) ||
                  parType.equals(boolean.class)) {
           value = rset.getString(i + 1);
           if (value!=null && value.equals(booleanTrueValue))
             value = Boolean.TRUE;
           else if (value!=null && value.equals(booleanFalseValue))
             value = Boolean.FALSE;
         }
         else if (parType.equals(BigDecimal.class))
           value = rset.getBigDecimal(i+1);
         else if (parType.equals(Double.class) || parType==Double.TYPE) {
           value = rset.getBigDecimal(i+1);
           if (value!=null)
             value = new Double(((BigDecimal)value).doubleValue());
         }
         else if (parType.equals(Float.class) || parType==Float.TYPE) {
           value = rset.getBigDecimal(i+1);
           if (value!=null)
             value = new Float(((BigDecimal)value).floatValue());
         }
         else if (parType.equals(Integer.class) || parType==Integer.TYPE) {
           value = rset.getBigDecimal(i+1);
           if (value!=null)
             value = new Integer(((BigDecimal)value).intValue());
         }
         else if (parType.equals(Long.class) || parType==Long.TYPE) {
           value = rset.getBigDecimal(i+1);
           if (value!=null)
             value = new Long(((BigDecimal)value).longValue());
         }
         else if (parType.equals(Short.class) || parType==Short.TYPE) {
           value = rset.getBigDecimal(i+1);
           if (value!=null)
             value = new Long(((BigDecimal)value).longValue());
         }
         else if (parType.equals(java.util.Date.class) ||
                  parType.equals(java.sql.Date.class))
           value = rset.getDate(i+1);
         else if (parType.equals(java.sql.Timestamp.class))
           value = rset.getTimestamp(i+1);
         else
           value = rset.getObject(i+1);

         try {
           if (setterMethods[i].getParameterTypes()[0].equals(byte[].class) &&
               value!=null &&
               value instanceof Blob){
             Blob b = (Blob)value;
             BufferedInputStream in = null;
             try {
               in = new BufferedInputStream(b.getBinaryStream());
               byte[] bb = new byte[10000];
               byte[] bytes = new byte[0];
               byte[] aux = null;
               int len = 0;
               while((len=in.read(bb))>0) {
                 aux = new byte[bytes.length+len];
                 System.arraycopy(bytes,0,aux,0,bytes.length);
                 System.arraycopy(bb,0,aux,bytes.length,len);
                 bytes = aux;
               }
               value = bytes;
             }
             catch (Exception ex7) {
               ex7.printStackTrace();
               value = null;
             }
             finally {
               try {
                 if (in != null) {
                   in.close();
                 }
               }
               catch (Exception ex8) {
               }
             }

           }
           setterMethods[i].invoke(currentVO, new Object[] {value});
         }
         catch (IllegalArgumentException ex5) {
           try {
             if (value!=null && !value.getClass().getName().equals(parType.getName()))
               Logger.error(
                   userSessionPars!=null?userSessionPars.getUsername():null,
                   "org.openswing.swing.server.QueryUtil",
                   "getQuery",
                   "Error while executing the SQL:\n"+
                   baseSQL+"\n"+
                   params+"\n"+
                   "Incompatible type found between value read ("+value.getClass().getName()+") and value expected ("+parType.getName()+") in setter '"+setterMethods[i].getName()+"'.",
                   null
               );
           }
           catch (Throwable ex6) {

           }
           throw ex5;
         }
       }

       list.add(vo);

       if (rowCount==blockSize && rowsToRead==1)
         break;
     }
     boolean moreRows = false;
     if (rowsToRead==1 && !rset.isClosed()) {
       if (action == GridParams.NEXT_BLOCK_ACTION && rset.next())
         moreRows = true;
       else if (action==GridParams.PREVIOUS_BLOCK_ACTION && rset.previous())
         moreRows = true;
     }


     if (fetchTotalResultSetLength) {
       try {
         rset.last();
         totalResultSetLength = rset.getRow();
       }
       catch (SQLException ex4) {
         // last & getRow methods not supported!
         while(rset.next())
           rowCount++;
         totalResultSetLength = startPos+rowCount+(!moreRows?0:1);
       }
     }


     if (resultSetLength==-1)
       resultSetLength = list.size();

     long t3 = System.currentTimeMillis();
     for(int i=0;i<values.size();i++) {
       params += "Param. n."+(i+1)+" - Value = ";
       if (values.get(i)==null)
         params += "null\n";
       else if (values.get(i) instanceof Number || values.get(i) instanceof java.util.Date)
         params += values.get(i)+"\n";
       else
         params += "'"+values.get(i)+"'\n";
     }
     if (logQuery)
       Logger.debug(
           userSessionPars!=null?userSessionPars.getUsername():null,
           "org.openswing.swing.server.QueryUtil",
           "getQuery",
           "Execute SQL:\n"+
           baseSQL+"\n"+
           params+"\n"+
           list.size()+" rows read\n"+
           "Parsing Query Time: "+(t2-t1)+"ms\n"+
           "Reading Query Time: "+(t3-t2)+"ms\n"
       );

     if (rowsToRead==2) {
       if (list.size()==0)
         return new ErrorResponse("Record not found.");
       else
         return new VOResponse(list.get(0));
     }
     else {
       VOListResponse res = new VOListResponse(list,moreRows,resultSetLength);
       res.setTotalAmountOfRows(totalResultSetLength);
       return res;
     }
   } catch (Throwable ex) {
     try {
      Logger.error(
          userSessionPars!=null?userSessionPars.getUsername():null,
          "org.openswing.swing.server.QueryUtil",
          "getQuery",
          "Error while executing the SQL:\n"+
          baseSQL+"\n"+
          params+"\n"+
          ex.getMessage(),
          ex
      );
     } catch (Exception exx) {
       Logger.error(
           userSessionPars!=null?userSessionPars.getUsername():null,
           "org.openswing.swing.server.QueryUtil",
           "getQuery",
           "Error while executing the SQL:\n"+ex.getMessage(),
           ex
       );
     }
    return new ErrorResponse(ex.getMessage());
   }
   finally {
     try {
       if (pstmt!=null)
         pstmt.close();
     }
     catch (SQLException ex1) {
     }
   }
}
 /**
  * Method called by getQuery method to retrieve all db fields in select clause.
  * @param sql query to execute
  * @return list of db fields
  */
 private static ArrayList getColumns(String sql) {
   ArrayList list = new ArrayList();
   String token = null;
   int comma = 0,lastIndex = 0;
   int parenthesis = 0;
   while((comma=sql.indexOf(",",lastIndex))>0) {
     token = sql.substring(lastIndex,comma).trim();
     parenthesis = parenthesis + new StringTokenizer(" "+token+" ","(").countTokens();
     parenthesis = parenthesis - new StringTokenizer(" "+token+" ",")").countTokens();
     if (parenthesis>0) {
       lastIndex = comma+1;
       continue;
     }
     parenthesis = 0;

     if (token.indexOf(" ")!=-1)
       token = token.substring(token.lastIndexOf(" ")+1);
     list.add( token );

     lastIndex = comma+1;
   }

   token = sql.substring(lastIndex).trim();
   if (token.indexOf(" ")!=-1)
     token = token.substring(token.lastIndexOf(" ")+1);
   list.add( token );

   return list;
 }


}
