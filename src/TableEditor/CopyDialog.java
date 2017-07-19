package TableEditor;

import java.lang.reflect.Method;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

import org.openswing.swing.client.*;
import org.openswing.swing.table.client.*;
import org.openswing.swing.table.model.client.*;
import org.openswing.swing.util.client.*;




public class CopyDialog extends JDialog {
	private static final long serialVersionUID = 6293382543773079313L;
	private static final String EXCLUSIVECOLUMNS = "CREATED_DATE,CREATED_TIME,MODIFIED_DATE,MODIFIED_TIME,MODIFIED_BY"; 
	JPanel buttonsPanel = new JPanel();
	JPanel mainPanel = new JPanel();
	JButton copyButton = new JButton();
	FlowLayout flowLayout1 = new FlowLayout();
	JButton cancelButton = new JButton();
	GridBagLayout gridBagLayout1 = new GridBagLayout();
	JLabel colsLabel = new JLabel();
	JScrollPane scrollPane = new JScrollPane();
	JTable cols = new JTable();
  
	  /** parent frame */
	 private Window frame = null;
	
	  /** grid */
	 private Grids grids = null;
	  
	 private GenericGridVO vo = null;
	 private HashMap selectedColumns = null;
	 private final JCheckBox chckbxNewCheckBox = new JCheckBox("Select All");
	 private ArrayList keyColumns = null; 
	 public HashMap getSelectedColumns() {
		return selectedColumns;
	 }
	
	
	 public void setSelectedColumns(HashMap selectedColumns) {
		 this.selectedColumns = selectedColumns;
	 }
	
	
	/**
	   * Constructor called by Grid.
	   * @param frame parent frame
	   * @param grid grid to import
	   * @param colsVisible collection of grid columns currently visible
	   */
	 public CopyDialog(JFrame frame,Grids grids,GenericGridVO vo,ArrayList keys) {
	    super(frame, "Select Columns to Copy", true);
	    this.vo = vo;
	    this.keyColumns = keys;
	    init(frame,grids);
	 }
	
	
	   /**
	   * Constructor called by Grid.
	   * @param frame parent frame
	   * @param grid grid to import
	   * @param colsVisible collection of grid columns currently visible
	   */
	 private void init(Window frame,Grids grids) {
		 this.frame = frame;
		 this.grids = grids;
		 try {
			 jbInit();
			 setSize(560,300);
			 init(grids);
			 ClientUtils.centerDialog(frame,this);
			 setVisible(true);
		 }
		 catch(Exception ex) {
			 ex.printStackTrace();
		 }
	 }
	 boolean IsKey(String columnName){
		boolean retValue = false;
		DBColumn col = null;
		for(int i=0;i<this.keyColumns.size();i++){
			col=(DBColumn)keyColumns.get(i);
			if(col.getDbColName().equalsIgnoreCase(columnName)){
				retValue = true;
				break;
			}
		}
		return retValue;
	}
	
	  /**
	   * Initialize the grid.
	   */
	 private void init(Grids grids) {
		 DefaultTableModel model = new DefaultTableModel() {
			 public String getColumnName(int col) {
				 if (col==0)
					 return "select";
				 else if (col==1)
					 return "key";
				 else if (col==2)
					 return "Column";
				 else if (col==3)
					 return "Value";
				 else
					 return "";
			 }
	
			 public int getColumnCount() {
				 return 4;
			 }
	
			 public Class getColumnClass(int col) {
				 if (col==0)
					 return Boolean.class;					 
				 else if (col==1)
					 return String.class;
				 else if (col==2)
					 return String.class;
				 else if (col==3)
					 return String.class;
				 else
					 return Integer.class;
			 }
	
			 public boolean isCellEditable(int row,int col) {
				 return col==0;
			 }
	
		 };
	
		 VOListTableModel gridModel = grids.getVOListTableModel();
		 Object value;
		 String keyIndicator = "";
		 for(int i=0;i<gridModel.getColumnCount();i++){			 
			 if(EXCLUSIVECOLUMNS.indexOf(grids.getHeaderColumnName(gridModel.getColumnName(i)).toUpperCase())<0){ 
				 try{
					 keyIndicator = "";
					 value = findGetter(gridModel.getColumnName(i)).invoke(vo, null);
					 if(IsKey(grids.getHeaderColumnName(gridModel.getColumnName(i))))
						 keyIndicator = "*";
					 model.addRow(new Object[]{        		
							 new Boolean(false),
							 keyIndicator,
							 grids.getHeaderColumnName(gridModel.getColumnName(i)),
							 //gridModel.getValueAt(grids.getSelectedRow(), i)
							 value
					 });
				 }catch(Exception ex){
					 System.out.println(ex.getMessage());
				 }
			 }
		 }
		 cols.setModel(model);
		 cols.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		 cols.setRowHeight(ClientSettings.CELL_HEIGHT);
		 cols.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		 cols.setSurrendersFocusOnKeystroke(true);
		 cols.setBackground(ClientSettings.GRID_CELL_BACKGROUND);
		 cols.setForeground(ClientSettings.GRID_CELL_FOREGROUND);
		 cols.setSelectionBackground(ClientSettings.GRID_SELECTION_BACKGROUND);
		 cols.setSelectionForeground(ClientSettings.GRID_SELECTION_FOREGROUND);
		 //set  column width
		 cols.getColumnModel().getColumn(0).setPreferredWidth(40);
		 cols.getColumnModel().getColumn(1).setPreferredWidth(1);
		 DefaultTableCellRenderer dtcr1 = new DefaultTableCellRenderer();
		 dtcr1.setHorizontalAlignment(SwingConstants.CENTER);
		 dtcr1.setForeground(Color.red);
		 cols.getColumnModel().getColumn(1).setCellRenderer(dtcr1);
		 cols.getColumnModel().getColumn(2).setPreferredWidth(120);
		 cols.getColumnModel().getColumn(3).setMinWidth(300);
		 DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
		 dtcr.setHorizontalAlignment(SwingConstants.LEFT);
		 cols.getColumnModel().getColumn(2).setCellRenderer(dtcr);			 
		 cols.revalidate();
	  	
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
	
	 private void jbInit() throws Exception {
		 buttonsPanel.setLayout(flowLayout1);
		 mainPanel.setBorder(BorderFactory.createEtchedBorder());
		 mainPanel.setLayout(gridBagLayout1);
		 buttonsPanel.setBorder(BorderFactory.createEtchedBorder());
		 copyButton.setMnemonic(ClientSettings.getInstance().getResources().getResource("importmnemonic").charAt(0));
		 copyButton.setText("Copy");
		 copyButton.addActionListener(new CopyDialog_copyButton_actionAdapter(this));
		 cancelButton.setMnemonic(ClientSettings.getInstance().getResources().getResource("cancelmnemonic").charAt(0));
		 cancelButton.setText("Cancel");
		 cancelButton.addActionListener(new CopyDialog_cancelButton_actionAdapter(this));
		 colsLabel.setText("columns to copy");
		 this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);   
		 getContentPane().add(buttonsPanel,  BorderLayout.SOUTH);
		 this.getContentPane().add(mainPanel,  BorderLayout.CENTER);
		 chckbxNewCheckBox.setSelected(false);
		 chckbxNewCheckBox.addActionListener(new ActionListener() {
			 public void actionPerformed(ActionEvent e) {
				 System.out.println("Action performated:" + chckbxNewCheckBox.isSelected());
				 for(int i=0;i<cols.getRowCount();i++){
					 cols.setValueAt(new Boolean(chckbxNewCheckBox.isSelected()), i, 0);
				 }
			 }
		 });
	    
	    buttonsPanel.add(chckbxNewCheckBox);
	    buttonsPanel.add(copyButton, null);
	    buttonsPanel.add(cancelButton, null);
	    mainPanel.add(colsLabel,  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
	            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
	    mainPanel.add(scrollPane,  new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0
	            ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 5, 5, 5), 0, 0));
	    scrollPane.setViewportView(cols);
	 }
	 void copyButton_actionPerformed(ActionEvent e) {
		 this.selectedColumns = new HashMap();
		 for(int i=0;i<cols.getRowCount();i++){
			 if (((Boolean)cols.getValueAt(i,0)).booleanValue()) {
				 this.selectedColumns.put(cols.getValueAt(i,2),cols.getValueAt(i,3));	    	  
			 }
		 }
		 if (selectedColumns.size()==0) {
			 OptionPane.showMessageDialog(
					 frame,
					 "you must select at least one column",
					 "Error",
					 JOptionPane.WARNING_MESSAGE
			 );
		 }else
			 setVisible(false);
	 }
	
	 void cancelButton_actionPerformed(ActionEvent e) {
		 setVisible(false);
		 this.selectedColumns = null;
	 }
}

class CopyDialog_copyButton_actionAdapter implements java.awt.event.ActionListener {
	CopyDialog adaptee;
	CopyDialog_copyButton_actionAdapter(CopyDialog adaptee) {
		this.adaptee = adaptee;
	}
	public void actionPerformed(ActionEvent e) {
		adaptee.copyButton_actionPerformed(e);
	}
}

class CopyDialog_cancelButton_actionAdapter implements java.awt.event.ActionListener {
	CopyDialog adaptee;

	CopyDialog_cancelButton_actionAdapter(CopyDialog adaptee) {
		this.adaptee = adaptee;
	}
	public void actionPerformed(ActionEvent e) {
		adaptee.cancelButton_actionPerformed(e);
	}
 
}


