package TableEditor;
import org.openswing.swing.client.*;

import java.awt.*;

import org.openswing.swing.table.columns.client.*;
import org.openswing.swing.util.client.ClientUtils;
import org.openswing.swing.util.java.Consts;

import java.sql.*;
import java.util.ArrayList;

import java.awt.event.*;
import org.openswing.swing.mdi.client.InternalFrame;
import org.openswing.swing.message.receive.java.Response;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;


public class GenericGridFrame extends InternalFrame {
	  /**
	 * 
	 */
	private static final long serialVersionUID = -9007031419636511693L;
	GridControl grid = new GridControl();
	JPanel buttonsPanel = new JPanel();
	FlowLayout flowLayout1 = new FlowLayout();
	DBTable table = null;
	DeleteButton deleteButton = new DeleteButton();
	GenericGridFrameController controller = null;
  
	InsertButton insertButton = new InsertButton();
	EditButton editButton = new EditButton();
	SaveButton saveButton = new SaveButton();
	ReloadButton reloadButton = new ReloadButton();
	CopyButton copyButton = new CopyButton();
	PasteButton pasteButton = new PasteButton("table-edit.png");
	
	ExportButton exportButton1 = new ExportButton();
	NavigatorBar navigatorBar1 = new NavigatorBar();
	FilterButton filterButton1 = new FilterButton();
  
	ImportButton importButton1 = new ImportButton();
  	public double minValue = -1;
  	public int minValueInt = -1;
  	
	public void setGridEditable() {
		editButton.execute();
		grid.setMode(Consts.EDIT);
	}

	  public GenericGridFrame(Connection conn, GenericGridFrameController controller) {
	    //Dynamic add columns to grid based on DBTable object
	    table = controller.getTable();
	    DBColumn col = null;
	    ArrayList cols = table.getColumns();
	    this.controller = controller;
	    grid.setFunctionId(table.getTbName());
//	    Font f = this.getContentPane().getFont();
//	    FontMetrics metrics =this.getGraphics().getFontMetrics(f);
//	    int adv = metrics.stringWidth("A");

	    for(int i = 0; i < cols.size();i++){
	    	col = (DBColumn)cols.get(i);
	    	if(col.getDbDataType().isString()){
	    		if(col.getDbDataType().isBool()){
	    			ComboColumn combCol = new ComboColumn();
	    			combCol.setHeaderColumnName(col.getColTitle());
	    			combCol.setColumnName(col.getMapvo());
	    			combCol.setDomainId("BOOLCOMBO");
	    			combCol.setColumnRequired(col.isRequired());
	    			combCol.setEditableOnInsert(col.isEditable());
	    			combCol.setEditableOnEdit(col.isEditable());
	    			combCol.setColumnSortable(col.isSortable());	    		
	    			combCol.setColumnFilterable(col.isFilterable());
	    			combCol.setColumnDuplicable(true);
		    		grid.getColumnContainer().add(combCol);
	    		}else{
		    		TextColumn txtCol = new TextColumn();
		    		txtCol.setHeaderColumnName(col.getColTitle());
		    		txtCol.setColumnName(col.getMapvo());
		    		if(col.getDbDataType().getMaxLength() != 0){
		    			txtCol.setMaxCharacters(col.getDbDataType().getMaxLength());
		    		}
		    		txtCol.setColumnRequired(col.isRequired());
		    		txtCol.setEditableOnInsert(col.isEditable());
		    		txtCol.setEditableOnEdit(col.isEditable());
		    		txtCol.setColumnSortable(col.isSortable());	    		
		    		txtCol.setColumnFilterable(col.isFilterable());
		    		txtCol.setColumnDuplicable(true);
		    		txtCol.autoFitColumn = true;
		    		txtCol.setAutoFitColumn(true);
		    		//txtCol.setPreferredWidth(col.getDbDataType().getMaxLength());
		    		txtCol.setPreferredWidth(100);
		    		txtCol.setMinWidth(20);
		    		txtCol.setUpperCase(true);
		    		grid.getColumnContainer().add(txtCol);
	    		}
	    	}else if(col.getDbDataType().isDecimal()){
	    		DecimalColumn decCol = new DecimalColumn();
	    		decCol.setColumnRequired(col.isRequired());
	    		decCol.setHeaderColumnName(col.getColTitle());
	    		decCol.setEditableOnInsert(col.isEditable());
	    		decCol.setEditableOnEdit(col.isEditable());
	    		decCol.setColumnSortable(col.isSortable());
	    		decCol.setColumnName(col.getMapvo());
	    		decCol.setColumnFilterable(col.isFilterable());
	    		decCol.setColumnDuplicable(true);
	    		//decCol.setMaxValue(maxValue);
	    		decCol.setMinValue(minValue);
	    		grid.getColumnContainer().add(decCol);
	    		
	    	}else if(col.getDbDataType().isInteger()){
	    		IntegerColumn intCol = new IntegerColumn();
	    		intCol.setHeaderColumnName(col.getColTitle());
	    		intCol.setColumnRequired(col.isRequired());
	    		intCol.setEditableOnInsert(col.isEditable());
	    		intCol.setEditableOnEdit(col.isEditable());
	    		intCol.setColumnSortable(col.isSortable());
	    		intCol.setColumnName(col.getMapvo());
	    		intCol.setColumnFilterable(col.isFilterable());
	    		intCol.setColumnDuplicable(true);
	    		//intCol.setMaxValue(maxValue);
	    		intCol.setMinValue(minValueInt);

	    		grid.getColumnContainer().add(intCol);
	    	}else if(col.getDbDataType().isDate()){
	    		DateColumn dateCol = new DateColumn();
	    		dateCol.setHeaderColumnName(col.getColTitle());
	    		dateCol.setColumnRequired(col.isRequired());
	    		dateCol.setEditableOnInsert(col.isEditable());
	    		dateCol.setEditableOnEdit(col.isEditable());
	    		dateCol.setColumnSortable(col.isSortable());
	    		dateCol.setColumnName(col.getMapvo());
	    		dateCol.setColumnFilterable(col.isFilterable());
	    		dateCol.setColumnDuplicable(true);
	    		grid.getColumnContainer().add(dateCol);	
	    	}else if(col.getDbDataType().isTime()){
	    		TimeColumn timeCol = new TimeColumn();
	    		timeCol.setHeaderColumnName(col.getColTitle());
	    		timeCol.setColumnRequired(col.isRequired());
	    		timeCol.setEditableOnInsert(col.isEditable());
	    		timeCol.setEditableOnEdit(col.isEditable());
	    		timeCol.setColumnSortable(col.isSortable());
	    		timeCol.setColumnName(col.getMapvo());
	    		timeCol.setColumnFilterable(col.isFilterable());
	    		timeCol.setColumnDuplicable(true);
	    		grid.getColumnContainer().add(timeCol);
	    	}else if(col.getDbDataType().isTimestamp()){
	    		TimeColumn timeCol = new TimeColumn();
	    		timeCol.setHeaderColumnName(col.getColTitle());
	    		timeCol.setColumnRequired(col.isRequired());
	    		timeCol.setEditableOnInsert(col.isEditable());
	    		timeCol.setEditableOnEdit(col.isEditable());
	    		timeCol.setColumnSortable(col.isSortable());
	    		timeCol.setColumnName(col.getMapvo());
	    		timeCol.setColumnFilterable(col.isFilterable());
	    		timeCol.setColumnDuplicable(true);
	    		grid.getColumnContainer().add(timeCol);
	    	}else{
	    		System.out.println("Unsupported data type from GenericGridFrame.java.");
	    		System.exit(1);
	    	}
	    	
	    	
	    }
	    try {
	      jbInit();
	      setSize(770,300);
	      grid.setController(controller);
	      grid.setGridDataLocator(controller);
	      setTitle(table.getTbName());

	    }
	    catch(Exception e) {
	      e.printStackTrace();
	    }
	  }


	  public void reloadData() {
	    grid.reloadData();
	  }


	  private void jbInit() throws Exception {
     	grid.setEditOnSingleRow(true);
	  	grid.setMaxSortedColumns(10);
	  	grid.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

	    buttonsPanel.setLayout(flowLayout1);
	    flowLayout1.setAlignment(FlowLayout.LEFT);
	  	grid.setInsertButton(insertButton);
	    grid.setDeleteButton(deleteButton);
	    grid.setExportButton(exportButton1);
	    grid.setFilterButton(filterButton1);
	    grid.setImportButton(importButton1);
	    
	    
	    grid.setNavBar(navigatorBar1);
	    grid.setReloadButton(reloadButton);
	    grid.setValueObjectClassName("TableEditor.GenericGridVO");
	    insertButton.setText("insertButton1");
	    insertButton.addActionListener(new GenericGridFrame_insertButton_actionAdapter(this));
	    exportButton1.setText("exportButton1");
	    filterButton1.setText("filterButton1");
	    this.getContentPane().add(grid, BorderLayout.CENTER);
	    this.getContentPane().add(buttonsPanel, BorderLayout.NORTH);
	    buttonsPanel.add(insertButton, null);
	    grid.setEditButton(editButton);
	    editButton.setText("editButton1");
	    grid.setSaveButton(saveButton);
	    grid.setBorder(new LineBorder(new java.awt.Color(0,0,0), 1, false));
	    saveButton.setText("saveButton1");
	    buttonsPanel.add(editButton);
	    
	    buttonsPanel.add(saveButton);
	    buttonsPanel.add(reloadButton, null);
	    buttonsPanel.add(deleteButton, null);
	    buttonsPanel.add(exportButton1, null);
	    
	    buttonsPanel.add(importButton1);
	    buttonsPanel.add(filterButton1, null);
	    buttonsPanel.add(navigatorBar1, null);
	    copyButton.setToolTipText("Copy selected row ");
	    
	    //  This is the start of the copy and paste function.  I just wanted to test to see how we can get
	    //  the currently selected cells.
	    
	    copyButton.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent arg0) {
	    		int tmp [] = grid.getSelectedRows();

	    		Copy(tmp.length,(GenericGridVO)grid.getVOListTableModel().getObjectForRow(tmp[0]));	    			    	
		    	if(!pasteButton.isEnabled())
		    		pasteButton.setEnabled(true);
	    		}
	    	});
	    copyButton.setText("Copy");
	    buttonsPanel.add(copyButton);
	    
	    pasteButton.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent arg0) {
	    		int tmp [] = grid.getSelectedRows();
	    		GenericGridVO vo = null;
	    		for(int i=0;i<tmp.length;i++){
	    			vo = (GenericGridVO)grid.getVOListTableModel().getObjectForRow(tmp[i]);
	    			PasteData(vo);
	    		}
	    	}
	    });
	    pasteButton.setText("Paste");
	    pasteButton.setToolTipText("Paste copied data into select row(s)");
	    pasteButton.setEnabled(false);
	    buttonsPanel.add(pasteButton);	    
	}
	  
	  void Copy(int selectedRowNum,GenericGridVO currentVO){
		  CopyDialog d = null;
		  Window parentComp = ClientUtils.getParentWindow(this);
		  if(selectedRowNum > 1){
			  OptionPane.showMessageDialog(
		              this,
		              "You have selected multiple rows, Current design only the first selected row will be copied.",
		              "Copy Warning",
		              JOptionPane.WARNING_MESSAGE
		          );
		  }
		  d = new CopyDialog((JFrame)parentComp,grid.getTable(),currentVO,table.getPkeyColumns());
		  this.controller.setCopyBuffer(d.getSelectedColumns());
	  }
	  
	  void PasteData(GenericGridVO vo){
		  Response res = this.controller.PasteRecord(vo);
		  if(res.isError()){
			  OptionPane.showMessageDialog(
		              this,
		              res.getErrorMessage(),
		              "Paste Error",
		              JOptionPane.ERROR_MESSAGE
		          );
		  }
	  }
	  
	  public void setGrid(GridControl grid) {
		this.grid = grid;
	  }

	void insertButton_actionPerformed(ActionEvent e) {
		  //  This function may be useful for something.
	  }
	  
	  public GridControl getGrid() {
	    return grid;
	  }


	}

	class GenericGridFrame_insertButton_actionAdapter implements java.awt.event.ActionListener {
	  GenericGridFrame adaptee;

	  GenericGridFrame_insertButton_actionAdapter(GenericGridFrame adaptee) {
	    this.adaptee = adaptee;
	  }
	  public void actionPerformed(ActionEvent e) {
	    adaptee.insertButton_actionPerformed(e);
	  }
	  
	}
	
	
