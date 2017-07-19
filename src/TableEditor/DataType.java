package TableEditor;

public class DataType {

	private static final String STRING_TYPE = "String";
	private static final String DATE_TYPE = "Date";
	private static final String TIME_TYPE = "Time";
	private static final String INTEGER_TYPE = "Integer";
	private static final String DECIMAL_TYPE = "Decimal";
	
	private static final float MIN_VALUE_INDICATOR = 0;
	private static final float MAX_VALUE_INDICATOR = 0;
	private static final float NOT_EQUAL_VALUE_INDICATOR = 0;
	private static final int MAX_LENGTH_INDICATOR = 0;
	
	private static final String TIMESTAMP_TYPE = "Timestamp";
	
	private String type;		//  Store the data type: STRING_TYPE, DATE_TYPE, INTEGER_TYPE, DECIMAL_TYPE
	private String dateFormat;	//  Store date format  (eg.  yyyy/mm/dd)
	private String timeFormat;	//  Store time format  (eg.  HH:MM:ss:mm)
	private String timestampFormat;// Store timestamp format  (eg.  YYYY-MM-DD-hh.mm.ss.zzzzzz)
	private int maxLength;		//  Do we want to validate for string length
	private boolean isBool = false;
	private float minValue;		//  Do we want to validate for number minimun value
	private float maxValue;		//  Do we want to validate for number maximum value
	// C2246
	private float notEqValue;   //  Do we want to validate inquality of a number

	// Constructor for string
	public DataType (String in_dataType,int in_maxLength) {
		this.type = in_dataType;
		this.dateFormat = "";
		this.timeFormat = "";
		this.timestampFormat ="";
		this.maxLength = in_maxLength;
		this.minValue = MIN_VALUE_INDICATOR;
		this.maxValue = MAX_VALUE_INDICATOR;
		// C2246
		this.notEqValue = NOT_EQUAL_VALUE_INDICATOR;
	}
	
	// Constructor for string
	public DataType (String in_dataType,boolean isbool) {
		this.type = in_dataType;
		this.dateFormat = "";
		this.timeFormat = "";
		this.timestampFormat ="";
		this.isBool = isbool;
		this.maxLength =1;
		this.minValue = MIN_VALUE_INDICATOR;
		this.maxValue = MAX_VALUE_INDICATOR;
		// C2246
		this.notEqValue = NOT_EQUAL_VALUE_INDICATOR;
	}

	public boolean isBool() {
		return isBool;
	}

	public void setBool(boolean isBool) {
		this.isBool = isBool;
	}

	// Constructor for date, time, timestamp
	public DataType (String in_dataType,String in_dateFormat) {
		this.type = in_dataType;
		this.dateFormat = in_dateFormat;
		this.timeFormat = in_dateFormat;
		this.timestampFormat  = in_dateFormat;
		this.maxLength = MAX_LENGTH_INDICATOR;
		this.minValue = MIN_VALUE_INDICATOR;
		this.maxValue = MAX_VALUE_INDICATOR;

	}
	
	// Constructor for integer, decimal
	public DataType (String in_dataType,float in_minValue,float in_maxValue) {
		this.type = in_dataType;
		this.dateFormat = "";
		this.timeFormat = "";
		this.timestampFormat = "";
		this.maxLength = MAX_LENGTH_INDICATOR;
		this.minValue = in_minValue;
		this.maxValue = in_maxValue;
		
	}

	//  Return the data type
	public String getDataType () {
		return (this.type);
	}
	//  Return the date format
	public String getDateFormat () {
		return (this.dateFormat);
	}
	 //  Return the time format
	public String getTimeFormat () {
		return (this.timeFormat);
	}
//  Return the time format
	public String getTimestampFormat () {
		return (this.timestampFormat);
	}
	//  Return the string maximum length
	public int getMaxLength () {
		return (this.maxLength);
	}
	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}
	//  Return the number minimum value
	public float getMinValue () {
		return (this.minValue);
	}
	//  Return the number maximum value
	public float getMaxValue () {
		return (this.maxValue);
	}
	// C2246 Return the not equal number value
	public float getNotEqValue () {
		return (this.notEqValue);
	}
	//  Test for string data type
	public boolean isString() {
		return (this.type.equalsIgnoreCase(STRING_TYPE));
	}
	//  Test for date data type
	public boolean isDate() {
		return (this.type.equalsIgnoreCase(DATE_TYPE));
	}
//  Test for time data type
	public boolean isTime() {
		return (this.type.equalsIgnoreCase(TIME_TYPE));
	}	
	//Test for time data type
	public boolean isTimestamp() {
		return (this.type.equalsIgnoreCase(TIMESTAMP_TYPE));
	}	
	//  Test for integer data type
	public boolean isInteger() {
		return (this.type.equalsIgnoreCase(INTEGER_TYPE));
	}
	//  Test for decimal data type
	public boolean isDecimal() {
		return (this.type.equalsIgnoreCase(DECIMAL_TYPE));
	}
}