package TableEditor;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JPasswordField;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Toolkit;

import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;

import org.openswing.swing.client.LinkButton;
import org.openswing.swing.client.OptionPane;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.util.HashMap;

import org.openswing.swing.client.TextControl;
import org.openswing.swing.permissions.client.LoginController;

public class EditorLoginDialog extends JDialog implements ItemListener,ActionListener {
	private static final long serialVersionUID = -2397818294447994823L;
	private static final String NEWLINE = System.getProperty("line.separator");
	
	private JPasswordField passwordFieldNew;
	private JPasswordField passwordFieldVerify;
	private JPanel panelTitle,panelTitle2;
	private JLabel labelTitle,labelTitle2;
	private JPanel panelPassword;
	private TextControl textControlUserId ;
	private JLabel labelCurrent;
	private JPasswordField passwordFieldCurrent;
	private JPanel panelChange;
	private JPanel panelButton;
	private JButton buttonLogin;
	private JPanel panelMessage;
	
	private int LOGGEDIN = 1 ;
	private int	REUSEDPASSWORD = 2;
	private int INVALIDPASSWORD = 3;
	private int CANNOTCHANGNOW = 4;
	private int PASSWORDCHANGED = 5;
	
	private int LOGINWIDTH = 665;
	private int LOGINHEIGHT = 357;
	
	private String LOGINTITLE = "M T S  T a b l e  E d i t o r";
	private String LOGINTITLE2 = "P l e a s e   L o g i n";
	private String LOGINCHANGEPASSWORD = "C h a n g e  P a s s w o r d";
	
	private String LOGIN = "login";
	
	/** login controller */
	private LoginController loginController = null;
	
	/** number of faild login attempts */
	private int attempts = 1;
	/** parent frame; may be null */
	private JFrame parentFrame = null;

	/** flag used in windowClosed method */
	private boolean fromOtherMethod = false;

	/** flag used to indicate that the login dialog is for login or change password */
	private boolean changePassword;
	
	public boolean isChangePassword() {
		return changePassword;
	}

	public void setChangePassword(boolean changePassword) {
		this.changePassword = changePassword;
	}

	public EditorLoginDialog(JFrame parentFrame,
			String title,
		    boolean chPassword,
		    LoginController loginController
		    ) 
	{
		
		//super(parentFrame==null?new JFrame():parentFrame,title,true);
	    //this.parentFrame = parentFrame;
	    this.loginController = loginController;
	    //labelTitle.setText(title);
	    this.changePassword = chPassword;
		//setTitle(title);
	    int halfWidth;
	    int halfHeight;
	    int lineHeight;
	    
        halfWidth = LOGINWIDTH/2;
        halfHeight = LOGINHEIGHT/2;
        lineHeight = 25;
	   
		Dimension dim = new Dimension(
		        (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2-halfWidth,
		        (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2-halfHeight
		    );

	    setSize(LOGINWIDTH,LOGINHEIGHT);
	    setLocation(dim.width,dim.height);
	      
		setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		panelTitle = new JPanel();
		add(panelTitle);
		GridBagLayout gbl_panelTitle = new GridBagLayout();
		gbl_panelTitle.columnWidths = new int[]{50,320,50};
		gbl_panelTitle.rowHeights = new int[]{50, 0};
		gbl_panelTitle.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_panelTitle.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panelTitle.setLayout(gbl_panelTitle);
		
		labelTitle = new JLabel(LOGINTITLE);
		labelTitle.setForeground(new java.awt.Color(68,119,187));
		labelTitle.setFont(new java.awt.Font("Times New Roman", java.awt.Font.BOLD | java.awt.Font.ITALIC, 32));		
		labelTitle.setHorizontalAlignment(SwingConstants.CENTER);
		labelTitle.setToolTipText("MTS Table Editor");
		GridBagConstraints gbc_labelTitle = new GridBagConstraints();
		gbc_labelTitle.gridx = 1;
		gbc_labelTitle.gridy = 0;
		panelTitle.add(labelTitle, gbc_labelTitle);
		
		panelTitle2 = new JPanel();
		add(panelTitle2);
		GridBagLayout gbl_panelTitle2 = new GridBagLayout();
		gbl_panelTitle2.columnWidths = new int[]{50,260,50};
		gbl_panelTitle2.rowHeights = new int[]{22, 0};
		gbl_panelTitle2.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_panelTitle2.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panelTitle2.setLayout(gbl_panelTitle2);
		
		labelTitle2 = new JLabel();
		labelTitle2.setText(LOGINTITLE2);
		labelTitle2.setForeground(new java.awt.Color(68,119,187));
		labelTitle2.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 18));
		labelTitle2.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_labelTitle2 = new GridBagConstraints();
		gbc_labelTitle2.anchor = GridBagConstraints.NORTH;
		gbc_labelTitle2.gridx = 1;
		gbc_labelTitle2.gridy = 0;
		panelTitle2.add(labelTitle2, gbc_labelTitle2);
		
		
			
		panelPassword = new JPanel();
		add(panelPassword);
		GridBagLayout gbl_panelPassword = new GridBagLayout();
		gbl_panelPassword.columnWidths = new int[]{40,130,230,40};
		gbl_panelPassword.rowHeights = new int[]{30,30,30};
		gbl_panelPassword.rowWeights = new double[]{1.0, 1.0,1.0};
		gbl_panelPassword.columnWeights = new double[]{1.0, 1.0, 1.0, 1.0};
		
		panelPassword.setLayout(gbl_panelPassword);
		
		JLabel labelUserId = new JLabel("User Id:");
		labelUserId.setToolTipText("user id");
		GridBagConstraints gbc_labelUserId = new GridBagConstraints();
		gbc_labelUserId.anchor = GridBagConstraints.EAST;
		gbc_labelUserId.insets = new Insets(0, 0, 5, 5);
		gbc_labelUserId.gridx = 1;
		gbc_labelUserId.gridy = 1;
		panelPassword.add(labelUserId, gbc_labelUserId);
		
		textControlUserId = new TextControl();
		textControlUserId.setColumns(15);
		GridBagConstraints gbc_textControlUserId = new GridBagConstraints();
		gbc_textControlUserId.gridwidth = 1;
		gbc_textControlUserId.insets = new Insets(0, 0, 5, 5);
		gbc_textControlUserId.fill = GridBagConstraints.BOTH;
		gbc_textControlUserId.gridx = 2;
		gbc_textControlUserId.gridy = 1;
		panelPassword.add(textControlUserId, gbc_textControlUserId);
		
		labelCurrent = new JLabel("Password:");
		labelCurrent.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_labelCurrent = new GridBagConstraints();
		gbc_labelCurrent.anchor = GridBagConstraints.EAST;
		gbc_labelCurrent.insets = new Insets(0, 0, 5, 5);
		gbc_labelCurrent.gridx = 1;
		gbc_labelCurrent.gridy = 2;
		panelPassword.add(labelCurrent, gbc_labelCurrent);
		
		passwordFieldCurrent = new JPasswordField();
		passwordFieldCurrent.setToolTipText("current password");
		passwordFieldCurrent.setColumns(15);
		passwordFieldCurrent.setActionCommand(LOGIN);
		passwordFieldCurrent.addActionListener(this);
		GridBagConstraints gbc_passwordFieldCurrent = new GridBagConstraints();
		gbc_passwordFieldCurrent.fill = GridBagConstraints.HORIZONTAL;
		gbc_passwordFieldCurrent.gridwidth = 1;
		gbc_passwordFieldCurrent.insets = new Insets(0, 0, 5, 5);
		gbc_passwordFieldCurrent.gridx = 2;
		gbc_passwordFieldCurrent.gridy = 2;
		
		panelPassword.add(passwordFieldCurrent, gbc_passwordFieldCurrent);
		
		panelChange = new JPanel();
		add(panelChange);
		GridBagLayout gbl_panelChange = new GridBagLayout();
		gbl_panelChange.columnWidths = new int[]{40,130,230,40};
		gbl_panelChange.rowHeights = new int[]{30,30};
		gbl_panelChange.rowWeights = new double[]{1.0, 1.0};
		gbl_panelChange.columnWeights = new double[]{1.0, 1.0, 1.0};
		
		panelChange.setLayout(gbl_panelChange);
		panelChange.setVisible(false);
		
		JLabel labelNew = new JLabel("New Password:");
		labelNew.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_labelNew = new GridBagConstraints();
		gbc_labelNew.anchor = GridBagConstraints.EAST;
		gbc_labelNew.insets = new Insets(0, 0, 5, 5);
		gbc_labelNew.gridx = 1;
		gbc_labelNew.gridy = 0;
		panelChange.add(labelNew, gbc_labelNew);
		
		passwordFieldNew = new JPasswordField();
		passwordFieldNew.setColumns(15);
		passwordFieldNew.setToolTipText("new password");
		GridBagConstraints gbc_passwordFieldNew = new GridBagConstraints();
		gbc_passwordFieldNew.fill = GridBagConstraints.HORIZONTAL;
		gbc_passwordFieldNew.gridwidth = 1;
		gbc_passwordFieldNew.insets = new Insets(0, 0, 5, 5);
		gbc_passwordFieldNew.gridx = 2;
		gbc_passwordFieldNew.gridy = 0;
		panelChange.add(passwordFieldNew, gbc_passwordFieldNew);
		
		JLabel labelVerify = new JLabel("Verify Password:");
		labelVerify.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_labelVerify = new GridBagConstraints();
		gbc_labelVerify.anchor = GridBagConstraints.EAST;
		gbc_labelVerify.insets = new Insets(0, 0, 5, 5);
		gbc_labelVerify.gridx = 1;
		gbc_labelVerify.gridy = 1;
		panelChange.add(labelVerify, gbc_labelVerify);
		
		passwordFieldVerify = new JPasswordField();
		passwordFieldVerify.setToolTipText("verify password");
		passwordFieldVerify.setColumns(15);
		GridBagConstraints gbc_passwordFieldVerify = new GridBagConstraints();
		gbc_passwordFieldVerify.fill = GridBagConstraints.HORIZONTAL;
		gbc_passwordFieldVerify.gridwidth = 1;
		gbc_passwordFieldVerify.insets = new Insets(0, 0, 5, 5);
		gbc_passwordFieldVerify.gridx = 2;
		gbc_passwordFieldVerify.gridy = 1;
		panelChange.add(passwordFieldVerify, gbc_passwordFieldVerify);
		
		panelButton = new JPanel();
		add(panelButton);
		GridBagLayout gbl_panelButton = new GridBagLayout();
		gbl_panelButton.columnWidths = new int[]{150,50,50,150};
		gbl_panelButton.rowHeights = new int[]{30};
		gbl_panelButton.rowWeights = new double[]{1.0};
		gbl_panelButton.columnWeights = new double[]{1.0, 1.0, 1.0};
		panelButton.setLayout(gbl_panelButton);
		
		buttonLogin = new JButton(LOGIN);
		buttonLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loginButton_actionPerformed(e);
			}
		});
		buttonLogin.setToolTipText("Change password");
		GridBagConstraints gbc_buttonChange = new GridBagConstraints();
		gbc_buttonChange.insets = new Insets(0, 0, 0, 5);
		gbc_buttonChange.gridx = 1;
		gbc_buttonChange.gridy = 0;
		panelButton.add(buttonLogin, gbc_buttonChange);
		
		JButton buttonCancel = new JButton("Cancel");
		buttonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelButton_actionPerformed(e);
			}
		});
		buttonCancel.setToolTipText("Cancel new password");
		GridBagConstraints gbc_buttonCancel = new GridBagConstraints();
		gbc_buttonCancel.gridx = 2;
		gbc_buttonCancel.gridy = 0;
		panelButton.add(buttonCancel, gbc_buttonCancel);
		
		panelMessage = new JPanel();
		panelMessage.setToolTipText("Message");
		add(panelMessage);
		GridBagLayout gbl_panelMessage = new GridBagLayout();
		gbl_panelMessage.columnWidths = new int[]{50,180, 50};
		gbl_panelMessage.rowHeights = new int[]{16, 0};
		gbl_panelMessage.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_panelMessage.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panelMessage.setLayout(gbl_panelMessage);
		
		LinkButton linkButton = new LinkButton();
		linkButton.getLinkButton().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				//setTitle("Change Password");
				setChangePassword(true);
				ShowPanels();
			}
		});
		linkButton.setText("Change Password");
		GridBagConstraints gbc_linkButton = new GridBagConstraints();
		gbc_linkButton.gridx = 1;
		gbc_linkButton.gridy = 0;
		panelMessage.add(linkButton, gbc_linkButton);
		
		ShowPanels();
		//setVisible(true);
		
	}
	 public void actionPerformed(ActionEvent e) {
	        String cmd = e.getActionCommand();
	 
	        if (LOGIN.equals(cmd)) { //Process the password.
	        	loginButton_actionPerformed(e);
	            passwordFieldCurrent.selectAll();
	       
	        } else { //The user has asked for help.
	            JOptionPane.showMessageDialog(this,
	                "You can get the password by searching this example's\n"
	              + "source code for the string \"correctPassword\".\n"
	              + "Or look at the section How to Use Password Fields in\n"
	              + "the components section of The Java Tutorial.");
	        }
	    }
	void loginButton_actionPerformed(ActionEvent e) {
		String userId = textControlUserId.getText();
		char[] currentPassword = passwordFieldCurrent.getPassword();
		char[] newPassword = passwordFieldNew.getPassword();
		char[] repeatPassword = passwordFieldVerify.getPassword();
		
		if(!isChangePassword()){
			boolean ok = false;
		    HashMap map = new HashMap();
		    try {
		      map.put("username",userId);
		      map.put("password",String.valueOf(currentPassword));
	
		      if (! (ok = loginController.authenticateUser(map)) &&
		          attempts < loginController.getMaxAttempts()) {
		    	  attempts++;
		    	  passwordFieldCurrent.requestFocus();
		    	  return;
		      }
		    }
		    catch (Throwable ex) {
		      if (attempts < loginController.getMaxAttempts()) {
		        OptionPane.showMessageDialog(
		          parentFrame,
		          ex.getMessage(),
		          "Error",
		          JOptionPane.ERROR_MESSAGE
		        );
		        attempts++;
		        passwordFieldCurrent.requestFocus();
		        return;
		      }
		      else
		        ok = false;
		    }
		    if (!ok) {
		      // max number of failed attempts reached: the application will be closed
		    	cancelButton_actionPerformed(null);
		    	return;
		    }
//		    else if (parentFrame==null) {
//		        this.getParent().setVisible(false);
//		        ((JFrame)this.getParent()).dispose();
//		    }
//		    else
//		      parentFrame.setVisible(false);
	
		    // logon ok...
		    fromOtherMethod = true;
		    setVisible(false);
		    loginController.loginSuccessful(map);
		}
		else{ // change password
			String errMsg = "";
			//  Make sure the users new password conforms to the IBM password standards for AIX servers
			errMsg = validateNewPassword(userId,currentPassword, newPassword, repeatPassword);

			//  If the new password conforms to the IBM standard.
			if (errMsg.equalsIgnoreCase("")){
				//  Change the users password using a custom multi threaded java telnet session
				if (changePasswordViaTelnet(userId, currentPassword, newPassword) == PASSWORDCHANGED){
					//  If the password change was successful then connect to the database using the new
					//  password.
					changePassword = false;
					connectDb(userId, newPassword);							
				} else {
					//  The password change was not successful.  Clear login screen and try again.
					//changePassword = false;
					clearLoginScreen();
				}

			//  The new password does not conform to the IBM standards so tell the user why, and let them
			//  fix it
			} else {
				JOptionPane.showMessageDialog(null, errMsg, "New Password Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	  }
	private void clearLoginScreen() {
		// TODO Auto-generated method stub
		passwordFieldNew.setText("");
		passwordFieldVerify.setText("");
		
	}

	/**
     ***************************************************************************************************
     * NAME:  validateNewPassword(String username, char[] psw, char[] new_psw, char[] repeat_psw) {
     *
     * PURPOSE:  This method is used to validate a users new password against the IBM standard
     * password rules for AIX servers.
     *
     * INPUTS:  
     * 			1.  String username - userid
     * 			2.  char[] psw  -  users current password
     * 			3.  char[] new_psw  -  users new password
     * 			4.  char[] repeat_psw  -  repeated new password
     *
     * OUTPUTS: 1.  String - ""  if no error and password is ok, else error message.
     *
     * @author Monaco
     * 
     *  * Modification History:
     * 2010-01-15 - MM  - C4072 Added new password rules
     *
     ***************************************************************************************************
     */
    private String validateNewPassword(String username, char[] psw, char[] new_psw, char[] repeat_psw) {
		String errMsg = "";
		boolean passed, found;

		//  New password must be the same as the repeat password
		if (new_psw.length == repeat_psw.length){
			for (int i = 0; i < new_psw.length; i++) {
				if(new_psw[i] != repeat_psw[i]){
					return "New password and Repeat password are not the same.";
				}
			}
		} else {
			return  "New password and Repeat password are not the same.";

		}


		// New Pasword must be minimum 8 characters
		if (new_psw.length < 8) {
			errMsg = errMsg + "New password length must must be at least 8 characters." + NEWLINE;
		}

		//  Must contain at least one alphabetic character
		passed = false;
		for (int i = 0; i < new_psw.length; i++) {
			if(((int)new_psw[i] >= 65 & (int)new_psw[i] <= 90) | ((int)new_psw[i] >= 97 & (int)new_psw[i] <= 122)){
				passed = true;
			}
		}
		if (!passed) {
			errMsg = errMsg + "New password must contain a minimum of 1 alphabetic character." + NEWLINE;
		}

		//  Must contain at least one numeric character
		passed = false;
		for (int i = 0; i < new_psw.length; i++) {
			if((int)new_psw[i] >= 48 & (int)new_psw[i] <= 57){
				passed = true;
			}
		}
		if (!passed) {
			errMsg = errMsg + "New password must contain a minimum of 1 numeric character." + NEWLINE;
		}

		//  Can not contain more than 2 repeated characters
		passed = true;
		for (int i = 0; i < new_psw.length-2; i++) {
			if(new_psw[i] == new_psw[i+1] & new_psw[i] == new_psw[i+2]){
				passed = false;
			}
		}
		if (!passed) {
			errMsg = errMsg + "New password can not contain more than 2 repeated characters." + NEWLINE;
		}

		// must have at least one character not found in old password
		passed = false;
		for (int i = 0; i < new_psw.length; i++) {
			found = false;
			for (int j = 0; j < psw.length; j++) {
				if(new_psw[i] == psw[j]){
					found = true;
				}
			}
			if (!found) {
				passed = true;
			}
		}
		if (!passed) {
			errMsg = errMsg + "New password must contain at least one character not found in Old password." + NEWLINE;
		}
		//C4072 start
		// Must not contain userid
		passed = false;
		String pswString = new String(new_psw);		
		if(pswString.indexOf(username)!=-1){
			errMsg = errMsg + "New password must not contain userid." + NEWLINE;
		}
		
		//C4072 end
		return errMsg;
	}


    /**
     ***************************************************************************************************
     * NAME:  changePasswordViaTelnet(String username, char[] psw, char[] new_psw) {
     *
     * PURPOSE:  This method is used to change a users password once it has expired.  Before entering this
     * method the new password has already been validated against the IBM password rules for AIX servers.
     * This method connects to the AIX server where db2 resides via a multi threaded telnet session.  The
     * only purpose of the telnet session is to login and change the users password.
     *
     * INPUTS:  1.  String username
     * 			2.  char[] psw
     * 			3.  char[] new_psw
     *
     * OUTPUTS: 1.  int -  1 = Login Successful but Password not changed
     * 					   2 = Password Successfully Changed
     * 					  -1 = There was an error changing your password
     *
     * @author Monaco
     *
     ***************************************************************************************************
     */
    private int changePasswordViaTelnet(String username, char[] psw, char[] new_psw) {
		String resultMsg = "";
		String password = "";
		String new_password = "";
		int result = 0;
		long delayMillis = 8000;

		//  Get the password and new password as strings
		for (int i = 0; i < psw.length; i++) {
			password = password + psw[i];
		}
		for (int i = 0; i < new_psw.length; i++) {
			new_password = new_password + new_psw[i];
		}

		//  We get the telnet connection properties from the application properties file called
		//  LSRApplt.properties.  The mainPanel holds the properties file in memory.
		String server = ((TableEditor)loginController).getSysProperties().getProperty("SERVER_HOST");
		int port = Integer.parseInt(((TableEditor)loginController).getSysProperties().getProperty("TELNET_PORT"));

		//  Create a new telnet object
		TelnetChangePassword tCh = new TelnetChangePassword(server, port, username, password, new_password);
		//  Start the telnet thread
		tCh.telnetThread.start();
		try {
			//  Delay 8 seconds and if it's not done then there must be a problem.
			tCh.telnetThread.join(delayMillis);
	        if (tCh.telnetThread.isAlive()) {
	            //  Timeout occurred, thread has not finished and there must have been a problem
	        	//  report the error to the user
     	   		resultMsg = "There was an error changing your password and a Timeout occurred.  The following was the last response: " + NEWLINE + NEWLINE +
				tCh.telnetThread.getLastResponse();
        		JOptionPane.showMessageDialog(null, resultMsg, "Password Change Error", JOptionPane.ERROR_MESSAGE);

	        //  The change password session has finished normally, get the status and report to the user.
	        } else {
	        	result = tCh.telnetThread.getLoginStatus();
	        	if (result == LOGGEDIN) {
	        		resultMsg = "Login Successful but Password not changed." + NEWLINE +
	        					"Please contact System Administrator.";
	        		JOptionPane.showMessageDialog(null, resultMsg, "Application Error", JOptionPane.ERROR_MESSAGE);
	        	} else if (result == PASSWORDCHANGED) {
	        		resultMsg = "Password Successfully Changed.";
	        		JOptionPane.showMessageDialog(null, resultMsg, "Password Change", JOptionPane.INFORMATION_MESSAGE);
	        	}else if (result == INVALIDPASSWORD) {
	        		resultMsg = "There was an error changing your password.  The following was the last response: " + NEWLINE + NEWLINE +
					tCh.telnetThread.getLastResponse();
	        		JOptionPane.showMessageDialog(null, resultMsg, "Password Change Error", JOptionPane.ERROR_MESSAGE);
	        	} else if (result == REUSEDPASSWORD) {
	        		resultMsg = "There was an error changing your password.  The following was the last response: " + NEWLINE + NEWLINE +
					"Password was recently used and is not valid for reuse.";
	        		JOptionPane.showMessageDialog(null, resultMsg, "Password Change Error", JOptionPane.ERROR_MESSAGE);
	        	} else if (result == CANNOTCHANGNOW) {
	        		resultMsg = "There was an error changing your password.  The following was the last response: " + NEWLINE + NEWLINE +
					"New password requires a minimum of 1 elapsed week between changes.";
	        		JOptionPane.showMessageDialog(null, resultMsg, "Password Change Error", JOptionPane.ERROR_MESSAGE);
	        	} 
	        }
	    } catch (InterruptedException iE) {
	        // Thread was interrupted for some reason (should not happen),  display to console
	    	System.out.println("Thread Interupted.  Contact Administrator.");
	    } finally {
	    	//  Make sure we always close the thread.
	    	tCh.close();
	    }

		return result;
	}

	void cancelButton_actionPerformed(ActionEvent e) {
		if(isChangePassword()){
			setChangePassword(false);
			ShowPanels();
		}else{
			setVisible(false);				    
			if (parentFrame==null) {
				this.getParent().setVisible(false);
				//((JFrame)this.getParent()).dispose();
			}
		    loginController.stopApplication();
		}
	  }

	private void ShowPanels(){
		if(this.isChangePassword()){
			panelChange.setVisible(true);
			panelMessage.setVisible(false);
			this.labelTitle2.setText(LOGINCHANGEPASSWORD);
			this.labelCurrent.setText("Current Password:");
			this.buttonLogin.setText("Change");
		}else{
			panelChange.setVisible(false);
			panelMessage.setVisible(true);
			this.labelTitle2.setText(LOGINTITLE2);
			this.labelCurrent.setText("Password:");
			this.buttonLogin.setText(LOGIN);
		}
	}
	/**
	 * 
	 */
	
	
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 ***************************************************************************************************
	 * NAME:  Connection connectDb(String user, char[] password)
	 *
	 * PURPOSE: This routine is used to establish a database connection.  If the connection is not
	 * successful then null is returned as the connection object.  Class not found exceptions are caught and
	 * and printed to console where as SQL exceptions to deal with user name and password are brought to
	 * the users attention in a dialog box.  A password expired exception will trigger the change password
	 * fields to appear on screen.
	 *
	 * N.B.  AutoCommit is set to false.
	 *
	 * INPUTS:  1.  String user - User ID
	 *			2.  String psw - User ID Password   {not encrypted}
	 *			3.  Constants: CONNECTION_DRIVER
	 *
	 * OUTPUTS: 1.  Valid Java connection object or Null if the connection was not successful.
	 *
	 * @author Monaco
	 *
	 ***************************************************************************************************
	 */

	public Connection connectDb(String user, char[] password) {
		DBConnection dbConnection = null;
		Connection resultConn;
		String errMsgSQL;
		String psw;
		String connection_url;
		boolean exitFlag = false;
		resultConn = null;
		errMsgSQL = "";
		psw = "";
		//  The connection URL is built from the java properties file that is included in the jar.  It is called
		//  LSRApplt.properties
		connection_url = ((TableEditor)loginController).getConnection_url();
		String DB2Driver = ((TableEditor)loginController).getDB2Driver();
		//  build a password string from the character array
		for (int i = 0; i < password.length; i++) {
			psw = psw + password[i];
		}
		dbConnection = new DBConnection(user, psw, connection_url, DB2Driver);
		do{
			exitFlag = false;
			if(dbConnection.ConnectDB()){
				resultConn = dbConnection.getConnection();
			}else{
				//  The following block of code will catch the password expired exception.  Since DB2 JDBC
				//  can not return a specific error code for this then we have to parse the error message.
				errMsgSQL = dbConnection.getErrorMessage();					
				if ((errMsgSQL.toLowerCase().indexOf("password")>-1) & (errMsgSQL.toLowerCase().indexOf("expired")>-1)) {
					JOptionPane.showMessageDialog(null,
							"Your password has expired and must be changed." + NEWLINE + NEWLINE +
							DBConnection.PASSWORD_RULES, "Password Expired", JOptionPane.INFORMATION_MESSAGE);
					//  Display the change password fields on the screen.
					//setTitle("Change Password");
					setChangePassword(true);
					ShowPanels();					
	
				//  Password is invalid
				} else if ((errMsgSQL.toLowerCase().indexOf("password")>-1) & (errMsgSQL.toLowerCase().indexOf("invalid")>-1)) {
					JOptionPane.showMessageDialog(null,
							"Your database logon was not recognized." + NEWLINE +
							"(Logon information is case sensitive - be sure to use correct upper and lower case.)" + NEWLINE + NEWLINE +
							"If you have forgotten your password or need to have it reset please see the " + NEWLINE +
							"'Request Access' link on the main site."
							, "Unauthorized Logon", JOptionPane.ERROR_MESSAGE);
					break;
				}				
			}
	
			//  If the connection was not established then print msg to console
			if ((resultConn == null) & !changePassword) {
				System.out.println("Application Error:  Connection to database can not be established");				
				int result = JOptionPane.showConfirmDialog(this,"Connection to database can not be established\r\nWould you like to try it again ?","Application Error",JOptionPane.YES_NO_OPTION);
				if(result == JOptionPane.YES_OPTION){
					exitFlag = true;
				}else{
					System.exit(-1);
				}
			}
			
		}while(exitFlag);

		return(resultConn);
	}
	
}

