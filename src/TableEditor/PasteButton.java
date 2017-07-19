package TableEditor;

import java.awt.Dimension;

import javax.swing.ImageIcon;

import org.openswing.swing.client.DataController;
import org.openswing.swing.client.GenericButton;
import org.openswing.swing.mdi.client.MDIFrame;

public class PasteButton extends GenericButton {

	  /**
	 * 
	 */
	private static final long serialVersionUID = -4351715072576543380L;
	public PasteButton(String imgName) {
	    super();
	    ImageIcon img = createImageIcon("images/paste.png","this is test icon");
	    super.setIcon(img);
	    setPreferredSize(new Dimension(16,16));
	  }
	  
	  /** Returns an ImageIcon, or null if the path was invalid. */
	  protected ImageIcon createImageIcon(String path,String description) {
	      java.net.URL imgURL = getClass().getResource(path);
	      if (imgURL != null) {
	          return new ImageIcon(imgURL, description);
	      } else {
	          System.err.println("Couldn't find file: " + path);
	          return null;
	      }
	  }


	  /**
	   * Execute the edit operation.
	   * @param controller: data controller that contains the edit logic.
	   */
	  protected final void executeOperation(DataController controller) throws Exception {
	    try {
	      //controller.copy();
	    }
	    catch (Exception ex) {
	      ex.printStackTrace();
	    }
	  }
	  public void executeOperation(){
		  
	  }

	}

