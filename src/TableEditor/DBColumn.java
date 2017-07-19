package TableEditor;

public class DBColumn {
	private String colTitle;
	private String dbColName;
	private DataType dbDataType;
	private String mapvo;
	private boolean pkey = false;
	
	
	private boolean required = false;
	
	private boolean editable = true;
	private boolean filterable = true;
	private boolean sortable = true;
	
	
	public boolean IsPkey() {
		return pkey;
	}
	public void setPkey(boolean pkey) {
		this.pkey = pkey;
	}
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	public String getMapvo() {
		return mapvo;
	}
	public void setMapvo(String mapvo) {
		this.mapvo = mapvo;
	}
	public DBColumn(String colTitle, String dbColName, DataType dbDataType,String mapvo) {
		this.colTitle = colTitle;
		this.dbColName = dbColName;
		this.dbDataType = dbDataType;
		this.mapvo = mapvo;
	}
	public DBColumn() {
		this.colTitle = null;
		this.dbColName = null;
		this.dbDataType = null;
		this.mapvo = null;
	}
	
	public String getColTitle() {
		return colTitle;
	}
	public void setColTitle(String colTitle) {
		this.colTitle = colTitle;
	}
	public String getDbColName() {
		return dbColName;
	}
	public void setDbColName(String dbColName) {
		this.dbColName = dbColName;
	}
	public DataType getDbDataType() {
		return dbDataType;
	}
	public void setDbDataType(DataType dbDataType) {
		this.dbDataType = dbDataType;
	}
	
	public int getMaxlength() {
		return dbDataType.getMaxLength();
	}
	public void setMaxlength(int maxlength) {
		this.dbDataType.setMaxLength(maxlength);
	}
	public boolean isEditable() {
		return editable;
	}
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	public boolean isFilterable() {
		return filterable;
	}
	public void setFilterable(boolean filterable) {
		this.filterable = filterable;
	}
	public boolean isSortable() {
		return sortable;
	}
	public void setSortable(boolean sortable) {
		this.sortable = sortable;
	}
}
