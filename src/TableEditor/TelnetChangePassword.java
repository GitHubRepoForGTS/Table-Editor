package TableEditor;

import java.net.*;
import java.io.*;

/**
 ***************************************************************************************************
 * NAME:  TelnetChangePassword 
 *
 * PURPOSE: This class is used to provide the user with a GUI to change thier unix password.  This class 
 * is implemented due to the inability of DB2 to provide a JDBC password change facility.  In upcoming
 * releases of DB2 they will be providing this functionality.  Without this class, the user would have to 
 * log into a dos command line telnet session to change thier password every 3 months when it expires.
 * 
 * Please note that this class is only called when DB2 returns a login error of "password expired".  
 * This will occur the first time a user enters the database and every 3 months after according to IBM
 * security standards.
 * 
 * Once the password expired message is returned from DB2 when the user tries to logon, this class is 
 * called to open a multi threaded java telnet session.  A users new password is verified by the LSR GUI
 * to conform to IBM standards and is passed to this class.
 * 
 * This telnet session works by listening to keyword text requests from the server.  Once a key  
 * request from the server is detected this class will send an appropriate response.  
 * eg.  listen for text response "Login:" from the server.  When this response is detected send username
 *      listen for text response "Password:" from the server.  When this response is detected send password
 *      etc...
 * 
 *
 * INPUTS:  1.  N/A
 *
 * OUTPUTS: 1.  N/A
 *
 * @author Monaco
 *
 *Change History
 *  07/09/2008 - JWF - C3463 - Change case test in password change script
 *
 *
 ***************************************************************************************************
 */
public class TelnetChangePassword {
	private Socket sock;
	public OutputPipe telnetThread;

	
	/**
	 ***************************************************************************************************
	 * NAME:  TelnetChangePassword 
	 *
	 * PURPOSE: This is the constructor for the class.  It is only called once a password expired error
	 * is received from DB2.
	 *
	 * INPUTS:  1.  String host -  this is the server host name
	 * 			2.  int port - this is the servers telnet port usually 23
	 * 			3.  String username - the user name
	 * 			4.  String psw - the password
	 * 			5.  String new_psw - the new password  (previously confirmed and verified)
	 *
	 * OUTPUTS: 1.  N/A
	 *
	 * @author Monaco
	 *
	 ***************************************************************************************************
	 */
	public TelnetChangePassword(String host, int port, String username, String psw, String new_psw){
		try	{
			sock = new Socket(host, port);
			telnetThread = new OutputPipe(sock, username, psw, new_psw);
		} catch(IOException e) {
			System.out.println(e);
			return;
		}	
	}
	
	
	/**
	 ***************************************************************************************************
	 * NAME:  close() 
	 *
	 * PURPOSE: Closes the socket if it is open
	 *
	 * INPUTS:  1.  N/A
	 *
	 * OUTPUTS: 1.  N/A
	 *
	 * @author Monaco
	 *
	 ***************************************************************************************************
	 */
    public void close(){
    	try	{
    		sock.close();
    		System.out.println("Application Closed Socket.");
    	} catch(IOException e) {

    	}
    }

    
    /**
	 ***************************************************************************************************
	 * NAME:  finalize() 
	 *
	 * PURPOSE: Automatically close when we're garbage collected
	 *
	 * INPUTS:  1.  N/A
	 *
	 * OUTPUTS: 1.  N/A
	 *
	 * @author Monaco
	 *
	 ***************************************************************************************************
	 */
    protected void finalize() {
    	close();
    }

    
    /**
     ***************************************************************************************************
     * NAME:  OutputPipe 
     *
     * PURPOSE: This is the main multi threaded telnet class that is used for receiving and sending
     * tenlet requests.
     *
     * INPUTS:  1.  N/A
     *
     * OUTPUTS: 1.  N/A
     *
     * @author Monaco
     *
     ***************************************************************************************************
     */
    public class OutputPipe extends Thread {
		private InputStream     in;
		private OutputStream    ot;
		private PrintStream     os;
		private BufferedOutputStream    buf_out;
		private String username;
		private String password;
		private String new_password;
		private int status;
		private String runningLine;

		private int LOGGEDIN = 1 ;
		private int	REUSEDPASSWORD = 2;
		private int INVALIDPASSWORD = 3;
		private int CANNOTCHANGNOW = 4;
		private int PASSWORDCHANGED = 5;
		/**
		 ***************************************************************************************************
		 * NAME:  OutputPipe 
		 *
		 * PURPOSE: This is the constructor for the class, basically used to setup input variables and create
		 * the input/output stream objects on the open socket.
		 *
		 * INPUTS:  1.  Socket sock - open socket to server
		 * 			2.  String user - the user name
		 * 			3.  String psw - the password
		 * 			4.  String new_psw - the new password  (previously confirmed and verified)
		 *
		 * OUTPUTS: 1.  N/A
		 *
		 * @author Monaco
		 *
		 ***************************************************************************************************
		 */
		public OutputPipe(Socket sock, String user, String psw, String new_psw) {
			try	{
				ot = sock.getOutputStream();
				in = sock.getInputStream();
				os = new PrintStream(System.out);
				buf_out = new BufferedOutputStream(sock.getOutputStream());
				username = user;
				password = psw;
				new_password = new_psw;
				status = 0;

			}catch(IOException e){
				System.out.println(e);
			}
		}
	
		
		/**
		 ***************************************************************************************************
		 * NAME:  run()  
		 *
		 * PURPOSE: This is the overrided thread run method that we need to use to capture telnet requests and 
		 * send our responses. Since requests and responses are received and sent as text streams, we need 
		 * some boolean flags to determine if our responses to a request was made.  ie once the "login:" 
		 * request is detected and we send the username in response, we will then set the flag entered_user
		 * to true to verify that this request and response has been made.  We capture each request/response 
		 * sequence in the same manner.
		 *
		 * INPUTS:  1.  N/A
		 *
		 * OUTPUTS: 1.  N/A
		 *
		 * @author Monaco
		 *
		 ***************************************************************************************************
		 */
		public void run() {
			// default all responses to false (not sent when we first start)
			boolean entered_user = false;
			boolean entered_psw = false;
			boolean entered_new_psw = false;
			boolean entered_repeat_psw = false;
			
			int    i;
			try {
				//  running line is used to capture all the the server request text since the last response
				runningLine = "";
				//  continue to receive server requests indefinetly
				while(true) {
					//  we read character by character
					i = in.read();
					//  these are special telnet challenges to establish standard communication
					if (i == 255) {
						int i1 = in.read();
						int i2 = in.read();
						tel_net(i1,i2);

					//  it is not a special telnet challenge just a text request or message, these are the ones that
					//  we are interested in
					} else {
						os.print((char)i);  //  print to screen
						//  add this character to our runningLine
						runningLine += (char)i;
					//  If we have the Login request
						if(runningLine.toUpperCase().lastIndexOf("LOGIN:") > 0 & !entered_user){   //C3463
							runningLine ="";
							send(username,buf_out);
							entered_user = true;
						}
						// If we have the Password request
						if(runningLine.toUpperCase().lastIndexOf("PASSWORD:") > 0 & entered_user & !entered_psw){   //C3463
							runningLine ="";
							send(password,buf_out);
							entered_psw = true;
						}
						if( (runningLine.lastIndexOf("> ") > 0) | (runningLine.toUpperCase().lastIndexOf("FAILED RUNNING LOGIN SHELL.") > 0) |(runningLine.lastIndexOf("$ ") > 0)) {
							if ((entered_user & entered_psw) & (!entered_new_psw | !entered_repeat_psw)){
								status = LOGGEDIN;								 							
								// issue change password command
								runningLine ="";
								send("passwd",buf_out);
							}
							if (entered_user & entered_psw & entered_new_psw & entered_repeat_psw){
								status = PASSWORDCHANGED;
								return;
							}
								
						}
						if (runningLine.lastIndexOf("Changing password for") > 0){
							runningLine ="";
							// enter old password
							send(password,buf_out);
						}
						// If we have the New Password request and we haven't entered it yet
						if(runningLine.toUpperCase().lastIndexOf("NEW PASSWORD:") > 0 & entered_psw & !entered_new_psw){   //C3463
							runningLine ="";
							send(new_password,buf_out);
							entered_new_psw = true;
						}
						if(runningLine.toUpperCase().lastIndexOf("NOT VALID FOR REUSE") > 0 & entered_psw & !entered_new_psw){   //C3463
							status = REUSEDPASSWORD;
							return;
						}
						if(runningLine.toUpperCase().lastIndexOf("NEW PASSWORD MUST HAVE") > 0 & entered_psw & !entered_new_psw){   //C3463
							status = INVALIDPASSWORD;
							return;
						}
						if(runningLine.toUpperCase().lastIndexOf("BETWEEN CHANGES") > 0 & entered_psw & entered_new_psw){   //C3463
							status = CANNOTCHANGNOW;
							return;
						}
						//  This is one type of unix request to enter the new password for the second time
						if(runningLine.toUpperCase().lastIndexOf("RE-ENTER " + username.toUpperCase() + "'S " + "NEW PASSWORD:") > 0 & entered_new_psw & !entered_repeat_psw){    //C3463
							runningLine ="";
							send(new_password,buf_out);
							entered_repeat_psw = true;
						}
						//  This is another type of unix request to enter the new password for the second time
						if(runningLine.toUpperCase().lastIndexOf("ENTER THE NEW PASSWORD AGAIN:") > 0 & entered_new_psw & !entered_repeat_psw){ //C3463
							runningLine ="";
							send(new_password,buf_out);
							entered_repeat_psw = true;
						} 
												
											
					}	
					os.flush();
				}
			}catch(IOException e) {
				throw new RuntimeException(e.getMessage());
			}
		}
		
		/**
		 ***************************************************************************************************
		 * NAME:  getLoginStatus 
		 *
		 * PURPOSE: This public method is used to return the status of the attempted password change
		 * 
		 * INPUTS:  1.  N/A
		 *
		 * OUTPUTS: int - 	status = 1 Login Successful but Password not changed
		 * 					status = 2 Password Successfully Changed
		 * 					status = -1 There was an error changing your password
		 *
		 * @author Monaco
		 *
		 ***************************************************************************************************
		 */
		public int getLoginStatus() {
			return status;
		}
		
		
		/**
		 ***************************************************************************************************
		 * NAME:  getLastResponse() 
		 *
		 * PURPOSE: If there was a login error this method will return the last response from the server and 
		 * will contain the error that occurred.
		 *
		 * INPUTS:  1.  N/A
		 *
		 * OUTPUTS: 1.  N/A
		 *
		 * @author Monaco
		 *
		 ***************************************************************************************************
		 */
		public String getLastResponse() {
			return runningLine;
		}
		
		
		/**
		 ***************************************************************************************************
		 * NAME:  send(String str,BufferedOutputStream bos) 
		 *
		 * PURPOSE: This method is used to send responses to the server throught the socket
		 *
		 * INPUTS:  1.  N/A
		 *
		 * OUTPUTS: 1.  N/A
		 *
		 * @author Monaco
		 *
		 ***************************************************************************************************
		 */
		private void send(String str,BufferedOutputStream bos) {
			str += "\r\n";
			byte[] buf = new byte[str.length()];
			buf = str.getBytes();
			
			try { 
				bos.write(buf);bos.flush();
	    	} catch(IOException e) {}
		}

		
		/**
		 ***************************************************************************************************
		 * NAME:  tel_net 
		 *
		 * PURPOSE: This is used to handle standard telnet challenges to establish communication within the 
		 * tenlet protocol.  This is not to be modified
		 *
		 * INPUTS:  1.  N/A
		 *
		 * OUTPUTS: 1.  N/A
		 *
		 * @author Monaco
		 *
		 ***************************************************************************************************
		 */
		private void tel_net(int i1, int i2) {
			int i = i1;
			if (i == 253)
			{
				i = i2;
				if (i == 1)  wont(i);
				else if (i == 24) wont(i);
				else if (i == 31) wont(i);
				else if (i == 35) wont(i);
				else if (i == 36) wont(i);
				else if (i == 39) wont(i);
			}
		}

		
		/**
		 ***************************************************************************************************
		 * NAME:  wont 
		 *
		 * PURPOSE: This is a helper method for sending telnet responses.  This is not to be modified
		 *
		 * INPUTS:  1.  N/A
		 *
		 * OUTPUTS: 1.  N/A
		 *
		 * @author Monaco
		 *
		 ***************************************************************************************************
		 */
		private void wont(int i) {
			try {
				ot.write(255);
				ot.write(252);
				ot.write(i);
			} catch (IOException e)	{
				System.out.println(e);
			}
		}

	} 

}