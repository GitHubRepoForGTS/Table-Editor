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
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;



public class PasteDialog extends JDialog {

  JPanel buttonsPanel = new JPanel();
  JPanel mainPanel = new JPanel();
  JButton pasteButton = new JButton();
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
  public PasteDialog(JFrame frame,Grids grids,GenericGridVO vo) {
    super(frame, "Select paste Columns", true);
    init(frame,grids,vo);
  }


   /**
   * Constructor called by Grid.
   * @param frame parent frame
   * @param grid grid to import
   * @param colsVisible collection of grid columns currently visible
   */
  private void init(Window frame,Grids grids,GenericGridVO vo) {
    this.frame = frame;
    this.grids = grids;
    this.vo = vo;
    try {
      jbInit();
      setSize(460,300);
      init(grids);
      ClientUtils.centerDialog(frame,this);
      setVisible(true);
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
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
          return "Select";
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
          return String.class;
        else if (col==1)
          return Boolean.class;
        else if (col==2)
          return String.class;
        else
          return Integer.class;
      }

      public boolean isCellEditable(int row,int col) {
        return col==1;
      }

    };

    VOListTableModel gridModel = grids.getVOListTableModel();
    Object value;
	for(int i=0;i<gridModel.getColumnCount();i++)
      //if (colsVisible.contains(gridModel.getColumnName(i)))    	
    	try{
    		value = findGetter(gridModel.getColumnName(i)).invoke(vo, null);
    		model.addRow(new Object[]{        		
            		grids.getHeaderColumnName(gridModel.getColumnName(i)),        		
            		new Boolean(true),
            		grids.getHeaderColumnName(gridModel.getColumnName(i)),
            		//gridModel.getValueAt(grids.getSelectedRow(), i)
            		value
            });
    	}catch(Exception ex){
    		System.out.println(ex.getMessage());
    	}
        

    cols.setModel(model);
    cols.setRowHeight(ClientSettings.CELL_HEIGHT);
    cols.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    cols.setSurrendersFocusOnKeystroke(true);
    cols.setBackground(ClientSettings.GRID_CELL_BACKGROUND);
    cols.setForeground(ClientSettings.GRID_CELL_FOREGROUND);
    cols.setSelectionBackground(ClientSettings.GRID_SELECTION_BACKGROUND);
    cols.setSelectionForeground(ClientSettings.GRID_SELECTION_FOREGROUND);
    cols.getColumnModel().removeColumn(cols.getColumnModel().getColumn(0));
    //cols.getColumnModel().removeColumn(cols.getColumnModel().getColumn(2));
//    cols.getColumnModel().getColumn(1).setPreferredWidth(10);
//    cols.getColumnModel().getColumn(2).setPreferredWidth(50);
//    cols.getColumnModel().getColumn(3).setPreferredWidth(100);
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
    pasteButton.setMnemonic(ClientSettings.getInstance().getResources().getResource("importmnemonic").charAt(0));
    pasteButton.setText("paste");
    pasteButton.addActionListener(new PasteDialog_pasteButton_actionAdapter(this));
    cancelButton.setMnemonic(ClientSettings.getInstance().getResources().getResource("cancelmnemonic").charAt(0));
    cancelButton.setText("Cancel");
    cancelButton.addActionListener(new PasteDialog_cancelButton_actionAdapter(this));
    colsLabel.setText("columns to paste");
    this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);   
    getContentPane().add(buttonsPanel,  BorderLayout.SOUTH);
    this.getContentPane().add(mainPanel,  BorderLayout.CENTER);
    chckbxNewCheckBox.setSelected(true);
    chckbxNewCheckBox.addActionListener(new ActionListener() {
    	public void actionPerformed(ActionEvent e) {
    		System.out.println("Action performated:" + chckbxNewCheckBox.isSelected());
    		for(int i=0;i<cols.getRowCount();i++){
    			cols.setValueAt(new Boolean(chckbxNewCheckBox.isSelected()), i, 0);
    		}
    	}
    });
    
    buttonsPanel.add(chckbxNewCheckBox);
    buttonsPanel.add(pasteButton, null);
    buttonsPanel.add(cancelButton, null);
    mainPanel.add(colsLabel,  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    mainPanel.add(scrollPane,  new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 5, 5, 5), 0, 0));
    scrollPane.setViewportView(cols);
  }


  void pasteButton_actionPerformed(ActionEvent e) {
	  	this.selectedColumns = new HashMap();
	    for(int i=0;i<cols.getRowCount();i++){
	    	if (((Boolean)cols.getValueAt(i,0)).booleanValue()) {
	    		this.selectedColumns.put(cols.getValueAt(i,1),cols.getValueAt(i,2));	    	  
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

class PasteDialog_pasteButton_actionAdapter implements java.awt.event.ActionListener {
  PasteDialog adaptee;

  PasteDialog_pasteButton_actionAdapter(PasteDialog adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.pasteButton_actionPerformed(e);
  }
}

class PasteDialog_cancelButton_actionAdapter implements java.awt.event.ActionListener {
  PasteDialog adaptee;

  PasteDialog_cancelButton_actionAdapter(PasteDialog adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.cancelButton_actionPerformed(e);
  }
}


