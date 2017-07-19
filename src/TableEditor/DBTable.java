package TableEditor;

import java.util.ArrayList;
import java.util.HashSet;


public class DBTable implements Comparable{
	private ArrayList Columns = null;
	private String tbName;
	public DBTable(ArrayList columns, String tbName) {
		super();
		Columns = columns;
		this.tbName = tbName;
	}
	
	public DBTable() {
		super();
		Columns = null;
		this.tbName = null;
	}

	public ArrayList getColumns() {
		return Columns;
	}

	public void setColumns(ArrayList columns) {
		Columns = columns;
	}

	public String getTbName() {
		return tbName;
	}

	public void setTbName(String tbName) {
		this.tbName = tbName;
	}
	public ArrayList getPkeyColumns(){
		ArrayList keyColumns = new ArrayList();
		DBColumn col = null;
		for(int i = 0; i < this.Columns.size(); i++){
			col = (DBColumn)this.Columns.get(i);
			if(col.IsPkey()){
				keyColumns.add(col);				
			}
		}		
		return keyColumns;
	}

	public int compareTo(Object dbTable) {
		if (!(dbTable instanceof DBTable))
		      throw new ClassCastException("A DBTable object expected.");  
		    return this.tbName.compareToIgnoreCase(((DBTable)dbTable).tbName);  

	}
	
}
